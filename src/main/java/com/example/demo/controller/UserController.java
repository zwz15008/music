package com.example.demo.controller;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.ResponseBodyMessage;
import com.example.demo.model.User;
import com.example.demo.tools.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping("/login")
    public ResponseBodyMessage<User> login(@RequestParam String username, @RequestParam String password, HttpServletRequest request){
        User loginUser = new User();
        loginUser.setUsername(username);
        loginUser.setPassword(password);

        User userInfo = userMapper.selectByName(loginUser.getUsername());

        if(userInfo == null){
            System.out.println("null");
            return new ResponseBodyMessage<>(-1,"用户名或者密码错误",loginUser);
        }else {
            if(!bCryptPasswordEncoder.matches(password, userInfo.getPassword())){
                return new ResponseBodyMessage<>(-1,"用户名或者密码错误",loginUser);
            }
            request.getSession().setAttribute(Constant.USERINFO_SESSION_KEY,userInfo);
            return new ResponseBodyMessage<>(0, "登录成功", userInfo);
        }
    }
}
