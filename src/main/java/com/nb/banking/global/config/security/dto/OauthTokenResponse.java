package com.nb.banking.global.config.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class OauthTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    private String scope;

    @JsonProperty("token_type")
    private String tokenType;

    public OauthTokenResponse(String accessToken, String scope, String tokenType) {
        this.accessToken = accessToken;
        this.scope = scope;
        this.tokenType = tokenType;
    }
}
