package com.mingdutech.voiceassistant.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String hello() {
        return "这是我的第一个SpringBoot项目";
    }

}

