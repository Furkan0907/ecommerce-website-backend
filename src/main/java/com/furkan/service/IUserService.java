package com.furkan.service;

import com.furkan.dto.request.DtoUserIU;
import com.furkan.dto.response.DtoUser;

import java.util.List;

public interface IUserService {

    List<DtoUser> findAllUsers();

    DtoUser findUserByUsername(String username);

    DtoUser findUserByEmail(String email);

    DtoUser findUserById(Long id);

    DtoUser updateUserById(Long id, DtoUserIU dtoUserIU);

    void deleteUserById(Long id);
}
