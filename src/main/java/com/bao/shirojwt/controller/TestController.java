package com.bao.shirojwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("getString")
    public String getString(){
        return "完成了这个功能啦！！！";
    }
}
