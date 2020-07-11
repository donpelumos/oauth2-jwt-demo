package com.example.oauth2jwtdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Pelumi on 10-Jul-2020
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/{id}")
    public String getUser(@PathVariable("id") Integer id){
        return "User with id - "+id;
    }
}
