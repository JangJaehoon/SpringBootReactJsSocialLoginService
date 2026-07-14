package com.example.demo.controller;

import com.example.demo.dto.UserRequestDTO;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // http://localhost:8080/api/auth/ --다양한 요청받음.
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody UserRequestDTO userRequestDTO ){
        userService.signUp(userRequestDTO);
        return "SignUp Success! (회원가입 성공!)";
    }

}
