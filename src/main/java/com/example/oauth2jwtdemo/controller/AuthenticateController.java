package com.example.oauth2jwtdemo.controller;

import com.example.oauth2jwtdemo.config.JwtTokenUtil;
import com.example.oauth2jwtdemo.model.AuthenticationRequest;
import com.example.oauth2jwtdemo.model.AuthenticationResponse;
import com.example.oauth2jwtdemo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Pelumi on 11-Jul-2020
 */
@RestController
@CrossOrigin
@RequestMapping("api")
public class AuthenticateController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()));
        }
        catch(BadCredentialsException badCredentialsException){
            throw new Exception("Incorrect username or password", badCredentialsException);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        //final String token = jwtTokenUtil.generateVerifiedToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

}
