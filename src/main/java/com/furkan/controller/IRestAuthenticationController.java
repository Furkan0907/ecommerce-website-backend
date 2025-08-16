package com.furkan.controller;

import com.furkan.dto.request.AuthRequest;
import com.furkan.dto.request.LogoutRequest;
import com.furkan.dto.request.RefreshTokenRequest;
import com.furkan.dto.request.RegisterRequest;
import com.furkan.dto.response.AuthResponse;
import com.furkan.dto.response.DtoUser;
import com.furkan.utils.RootEntity;

public interface IRestAuthenticationController {

    RootEntity<DtoUser> register(RegisterRequest input);

    RootEntity<AuthResponse> authenticate(AuthRequest input);

    RootEntity<AuthResponse> refreshToken(RefreshTokenRequest input);

    RootEntity<Void> logout(LogoutRequest input);

    RootEntity<Boolean> checkEmailExists(String email);

    RootEntity<Void> resetPassword(String email, String newPassword);
}
