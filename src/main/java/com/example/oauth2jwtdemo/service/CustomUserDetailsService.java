package com.example.oauth2jwtdemo.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * User details service to fetch user details. Ideally meant to connect to database to fetch user details.
 * Created by Pelumi on 11-Jul-2020
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User mockUser = new User("pelumi","pelumi", new ArrayList<>());
        return mockUser;
    }
}
