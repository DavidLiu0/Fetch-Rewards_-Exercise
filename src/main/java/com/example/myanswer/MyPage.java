package com.example.myanswer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class MyPage {

    @RequestMapping("mypage")
    public String mypage(){
        return "MyAnswer";  // 返回html模板

    }
}
