package com.furkan.service.impl;

import com.furkan.dto.request.DtoUserIU;
import com.furkan.dto.response.DtoUser;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.User;
import com.furkan.repository.UserRepository;
import com.furkan.service.IUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private DtoUser dtoTransformation(User user) {
        DtoUser dtoUser = new DtoUser();
        BeanUtils.copyProperties(user, dtoUser);
        return dtoUser;
    }

    @Override
    public List<DtoUser> findAllUsers() {
        List<DtoUser> dtoList = new ArrayList<>();
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            dtoList.add(dtoTransformation(user));
        }
        return dtoList;
    }

    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    @Override
    public DtoUser findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, username)));
        return dtoTransformation(user);
    }

    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.email")
    @Override
    public DtoUser findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMAIL_NOT_FOUND, email)));
        return dtoTransformation(user);
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Override
    public DtoUser findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, id.toString())));
        return dtoTransformation(user);
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Override
    public DtoUser updateUserById(Long id, DtoUserIU dtoUserIU) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, id.toString())));
        user.setUpdatedAt(new Date());
        user.setUsername(dtoUserIU.getUsername());
        user.setPassword(passwordEncoder.encode(dtoUserIU.getPassword()));
        user.setRole(dtoUserIU.getRole());
        user.setEmail(dtoUserIU.getEmail());
        User savedUser = userRepository.save(user);
        return dtoTransformation(savedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, id.toString())));
        userRepository.delete(user);
    }
}
