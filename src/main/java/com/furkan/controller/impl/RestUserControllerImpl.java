package com.furkan.controller.impl;

import com.furkan.controller.IRestUserController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoUserIU;
import com.furkan.dto.response.DtoUser;
import com.furkan.service.IUserService;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class RestUserControllerImpl extends RestBaseController implements IRestUserController {

    @Autowired
    private IUserService userService;

    @GetMapping
    @Override
    public RootEntity<List<DtoUser>> findAllUsers() {
        return ok(userService.findAllUsers());
    }

    @GetMapping("/username/{username}")
    @Override
    public RootEntity<DtoUser> findUserByUsername(@PathVariable(name = "username") String username) {
        return ok(userService.findUserByUsername(username));
    }

    @GetMapping("/email/{email}")
    @Override
    public RootEntity<DtoUser> findUserByEmail(@PathVariable(name = "email") String email) {
        return ok(userService.findUserByEmail(email));
    }

    @GetMapping("/{id}")
    @Override
    public RootEntity<DtoUser> findUserById(@PathVariable(name = "id") Long id) {
        return ok(userService.findUserById(id));
    }

    @PutMapping("/{id}")
    @Override
    public RootEntity<DtoUser> updateUserById(@PathVariable(name = "id") Long id, @Valid @RequestBody DtoUserIU dtoUserIU) {
        return ok(userService.updateUserById(id, dtoUserIU));
    }

    @DeleteMapping("/{id}")
    @Override
    public RootEntity<Void> deleteUserById(@PathVariable(name = "id") Long id) {
        userService.deleteUserById(id);
        return ok();
    }
}
