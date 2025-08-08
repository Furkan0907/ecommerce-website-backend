package com.furkan.service.impl;

import com.furkan.dto.request.*;
import com.furkan.dto.response.AuthResponse;
import com.furkan.dto.response.DtoUser;
import com.furkan.enums.Role;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.jwt.JwtService;
import com.furkan.model.RefreshToken;
import com.furkan.model.User;
import com.furkan.repository.RefreshTokenRepository;
import com.furkan.repository.UserRepository;
import com.furkan.service.IAuthenticationService;
import com.furkan.service.ICartService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ICartService cartService;


    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private User createUser(RegisterRequest input) {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(input.getRole() != null ? input.getRole() : Role.USER);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return user;
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + 1000*60*60*12));
        refreshToken.setCreatedAt(new Date());
        return refreshToken;
    }

    @Override
    public DtoUser register(RegisterRequest input) {
        if (userRepository.existsByUsername(input.getUsername())) {
            throw new BaseException(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS, input.getUsername()));
        }
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, input.getEmail()));
        }
        User user = createUser(input);
        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.CUSTOMER) {
            DtoCartIU dtoCart  = new DtoCartIU();
            dtoCart.setUserId(savedUser.getId());
            cartService.createCart(dtoCart);
        }

        DtoUser dtoUser = new DtoUser();
        BeanUtils.copyProperties(savedUser, dtoUser);
        return dtoUser;
    }

    @Override
    public AuthResponse authenticate(AuthRequest input) {
        authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword())
        );

        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, input.getUsername())));

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenRepository.save(createRefreshToken(user));

        return new AuthResponse(accessToken, refreshToken.getRefreshToken());
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest input) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(input.getRefreshToken())
                .orElseThrow(() -> new BaseException(new ErrorMessage(
                        MessageType.REFRESH_TOKEN_NOT_FOUND, input.getRefreshToken()
                )));

        if (refreshToken.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_IS_EXPIRED, input.getRefreshToken()));
        }

        User user = refreshToken.getUser();

        refreshTokenRepository.delete(refreshToken);

        String newAccessToken = jwtService.generateToken(user);
        RefreshToken newRefreshToken = refreshTokenRepository.save(createRefreshToken(user));


        return new AuthResponse(newAccessToken, newRefreshToken.getRefreshToken());
    }

    @Override
    public void logout(LogoutRequest input) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(input.getRefreshToken())
                .orElseThrow(() -> new BaseException(new ErrorMessage(
                        MessageType.REFRESH_TOKEN_NOT_FOUND, input.getRefreshToken()
                )));

        refreshTokenRepository.delete(refreshToken);
    }
}
