package com.santosh.blog.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.santosh.blog.user.dto.UserDto;
import com.santosh.blog.user.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {
	
	@Autowired
    private UserService userService;

    @PostMapping
    public UserDto registration(@RequestBody @Valid UserDto.Registration registration) {
        return userService.registration(registration);
    }

    @PostMapping("/login")
    public UserDto login(@RequestBody @Valid UserDto.Login login) {
        return userService.login(login);
    }
}
