package com.furkan.service;

import com.furkan.dto.request.AuthRequest;
import com.furkan.dto.request.LogoutRequest;
import com.furkan.dto.request.RefreshTokenRequest;
import com.furkan.dto.request.RegisterRequest;
import com.furkan.dto.response.AuthResponse;
import com.furkan.dto.response.DtoUser;

import java.util.Date;

public interface IAuthenticationService {

    DtoUser register(RegisterRequest input);

    AuthResponse authenticate(AuthRequest input);

    AuthResponse refreshToken(RefreshTokenRequest input);

    void logout(LogoutRequest input);
}
