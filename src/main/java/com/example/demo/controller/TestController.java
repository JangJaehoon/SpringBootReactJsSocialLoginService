package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private")
public class TestController {

    @PostMapping("/hello")
    public String hello() {
        return "인증된 사용자만 접근 가능한 Api입니다.!";
    }

//    @GetMapping("/api/public/hello")
//    public String publicHello(){
//
//        return "Public Hello World";
//    }
//
//    @GetMapping("/api/private/hello")
//    public String privateHello(){
//
//        return "Private Hello World";
//    }

}
