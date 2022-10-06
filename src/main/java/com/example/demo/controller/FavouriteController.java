package com.example.demo.controller;

import com.example.demo.mapper.FavouriteMapper;
import com.example.demo.model.Music;
import com.example.demo.model.ResponseBodyMessage;
import com.example.demo.model.User;
import com.example.demo.tools.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/favourite")
@RequiredArgsConstructor
public class FavouriteController {
    private final FavouriteMapper favouriteMapper;

    @RequestMapping("/insertfavourite")
    public ResponseBodyMessage<Boolean> insertFavourite(@RequestParam String id, HttpServletRequest req){
        int musicId = Integer.parseInt(id);
        HttpSession httpSession = req.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            return new ResponseBodyMessage<>(-1, "请先登录!", null);
        }
        //获取id
        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();
        //判断是否已经收藏
        Music music = favouriteMapper.findFavouriteByMIDAndUID(musicId, userId);

        if (music != null){
            return new ResponseBodyMessage<>(-1, "已经收藏该歌曲", false);
        }else{
            boolean success = favouriteMapper.insertFavourite(userId, musicId);
            if(success){
                return new ResponseBodyMessage<>(0, "收藏成功" ,true);
            }else{
                return new ResponseBodyMessage<>(-1, "收藏失败",false);
            }
        }
    }
    @RequestMapping("/deletefavourite")
    public ResponseBodyMessage<Boolean> deleteCollecttion(@RequestParam String id, HttpServletRequest req){
        int musicId = Integer.parseInt(id);
        HttpSession httpSession = req.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            return new ResponseBodyMessage<>(-1, "请先登录!", null);
        }
        //获取id
        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();
        int success = favouriteMapper.deleteFavourite(userId, musicId);
        if(success != 0){
            return new ResponseBodyMessage<>(0, "取消收藏成功!", true);
        }else{
            return new ResponseBodyMessage<>(-1, "取消收藏失败!", false);
        }
    }

    @RequestMapping("/findfavourite")
    public ResponseBodyMessage<List<Music>> findFavourite(@RequestParam(required = false) String musicName, HttpServletRequest req){
        HttpSession httpSession = req.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            return new ResponseBodyMessage<>(-1, "请先登录!", null);
        }
        //获取id
        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();

        List<Music> musicList = new ArrayList<>();

        if(musicName != null){
            musicList = favouriteMapper.findFavouriteByKeyAndUID(musicName, userid);
        }else{
            musicList = favouriteMapper.findFavouriteByUserId(userid);
        }

        return new ResponseBodyMessage<>(0, "查询结束", musicList);
    }
}
