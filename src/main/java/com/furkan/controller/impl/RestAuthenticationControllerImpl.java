package com.furkan.controller.impl;

import com.furkan.controller.IRestAuthenticationController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.AuthRequest;
import com.furkan.dto.request.LogoutRequest;
import com.furkan.dto.request.RefreshTokenRequest;
import com.furkan.dto.request.RegisterRequest;
import com.furkan.dto.response.AuthResponse;
import com.furkan.dto.response.DtoUser;
import com.furkan.service.IAuthenticationService;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class RestAuthenticationControllerImpl extends RestBaseController implements IRestAuthenticationController {

    @Autowired
    private IAuthenticationService authenticationService;


    @PostMapping("/register")
    @Override
    public RootEntity<DtoUser> register(@Valid @RequestBody RegisterRequest input) {
        return ok(authenticationService.register(input));
    }

    @PostMapping("/authenticate")
    @Override
    public RootEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest input) {
        return ok(authenticationService.authenticate(input));
    }

    @PostMapping("refresh-token")
    @Override
    public RootEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest input) {
        return ok(authenticationService.refreshToken(input));
    }

    @PostMapping("/logout")
    @Override
    public RootEntity<Void> logout(@Valid @RequestBody LogoutRequest input) {
        authenticationService.logout(input);
        return ok();
    }
}
