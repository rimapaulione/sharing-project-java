package com.sharingAPI.sharing.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class VerificationTokenResponse {
    private UUID id;
    private String email;
    private LocalDateTime verified;
}