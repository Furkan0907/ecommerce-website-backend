package com.furkan.controller;

import com.furkan.dto.request.DtoUserIU;
import com.furkan.dto.response.DtoUser;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestUserController {

    RootEntity<List<DtoUser>> findAllUsers();

    RootEntity<DtoUser> findUserByUsername(String username);

    RootEntity<DtoUser> findUserByEmail(String email);

    RootEntity<DtoUser> findUserById(Long id);

    RootEntity<DtoUser> updateUserById(Long id, DtoUserIU dtoUserIU);

    RootEntity<Void> deleteUserById(Long id);
}
