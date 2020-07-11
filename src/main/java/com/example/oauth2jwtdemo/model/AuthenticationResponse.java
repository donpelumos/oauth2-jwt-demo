package com.example.oauth2jwtdemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Pelumi on 11-Jul-2020
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
}
