package com.qupp.user.controller.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 응답")
public class ResponseRegister {

    @Schema(description = "사용자")
    private ResponseUser responseUser;

    @Schema(description = "JWT AccessToken")
    private String jwtToken;

    @Builder
    public ResponseRegister(ResponseUser responseUser, String jwtToken) {
        this.responseUser = responseUser;
        this.jwtToken = jwtToken;
    }
}
