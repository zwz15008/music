package com.example.demo.mapper;

import com.example.demo.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User login(User userInfo);

    List<User> selectAll();

    User selectByName(String username);
}
