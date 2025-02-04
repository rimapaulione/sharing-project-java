package com.sharingAPI.sharing.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserByIdResponse {
    private String token;
    private UUID id;
    private String firstname;
    private String city;
    private String email;
    private Role role;
}
