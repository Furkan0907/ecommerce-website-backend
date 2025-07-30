package com.furkan.dto.response;

import com.furkan.dto.DtoBase;
import com.furkan.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoUser extends DtoBase {

    private String username;

    private String email;

    private Role role;

    private boolean isBanned;
}
