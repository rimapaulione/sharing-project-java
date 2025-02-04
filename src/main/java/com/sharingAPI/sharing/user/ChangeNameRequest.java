package com.sharingAPI.sharing.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeNameRequest {
    private String newFirstName;
    private String newCity;
    private Role role;
    private Optional<String> newPassword;
    private Optional<String> oldPassword;
    private String email;


}
