package com.example.demo.controller;


import com.example.demo.mapper.FavouriteMapper;
import com.example.demo.mapper.MusicMapper;
import com.example.demo.model.Music;
import com.example.demo.model.ResponseBodyMessage;
import com.example.demo.model.User;
import com.example.demo.tools.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zwz
 */
@RestController
@RequestMapping("/music")
public class MusicController {
    @Resource
    private MusicMapper musicMapper;
    @Value("${music.local.path}")
    private String SAVE_PATH;

    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer, @RequestParam("filename") MultipartFile file, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession httpSession = req.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            return new ResponseBodyMessage<>(-1, "请先登录!", false);
        }

        String wholeFileName = file.getOriginalFilename();
        System.out.println("wholeFileName=>" + wholeFileName);

        String path = SAVE_PATH + "" + wholeFileName;

        File dest = new File(path);
        System.out.println("dest=>" + dest.getPath());

        if (!dest.exists()) {
            boolean res = dest.mkdirs();
            if(!res){
                System.out.println("路径创建失败");
            }
        }

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBodyMessage<>(-1, "上传失败!", false);
        }

        String title = wholeFileName.substring(0, wholeFileName.lastIndexOf("."));

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String time = date.format(new Date());
        String url = "/music/get?path=" + title;

        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();
        int ret = musicMapper.insert(title, singer, time, url, userId);

        byte[] buf = new byte[128];
        RandomAccessFile raf = new RandomAccessFile(dest, "r");
        raf.seek(raf.length() - 128);
        raf.read(buf);
        raf.close();
        String tag = "TAG";
        if (!tag.equalsIgnoreCase(new String(buf, 0, 3))) {
            boolean res = dest.delete();
            if(!res){
                System.out.println("数据库上传失败,但音乐未被删除!");
            }
            return new ResponseBodyMessage<>(-1, "MP3标签信息数据格式不正确!", false);
        }
        if(ret == 1){
            resp.sendRedirect("/list.html");
        }else {
            boolean res = dest.delete();
            if(!res){
                System.out.println("数据库上传失败,但音乐未被删除!");
            }
            return new ResponseBodyMessage<>(-1, "数据库上传失败,已删除上传的音乐!", false);
        }
        return new ResponseBodyMessage<>(0, "上传成功!", true);
    }

    //音乐播放
    @RequestMapping("/get")
    public ResponseEntity<byte[]> get(String path){
        try {
            byte[] bytes = Files.readAllBytes(new File(SAVE_PATH + File.separator + path).toPath());
            return ResponseEntity.ok(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Autowired
    public FavouriteMapper favouriteMapper;
    @RequestMapping("/delete")
    public ResponseBodyMessage<Boolean> deleteMusicById(@RequestParam String id){
        int idd = Integer.parseInt(id);
        Music music = musicMapper.findMusicById(idd);
        if(music == null) {
            return new ResponseBodyMessage<>(-1, "没有找到该音乐", false);
        }
        int ret = musicMapper.deleteMusicById(idd);
        if(ret == 1){
            int index = music.getUrl().lastIndexOf("=");
            String filename = music.getUrl().substring(index+1);
            File file = new File(SAVE_PATH+"/"+filename+".mp3");
            System.out.println("此时的路程:"+file.getPath());

            if(file.delete()){
                favouriteMapper.deleteLoveMusicById(idd);
                return new ResponseBodyMessage<>(0, "删除服务器音乐成功", true);
            }else {
                return new ResponseBodyMessage<>(-1, "删除服务器音乐失败", false);
            }
        }else {
            return new ResponseBodyMessage<>(-1, "删除数据库中的音乐失败", false);
        }
    }

    @RequestMapping("/findmusic")
    public ResponseBodyMessage<List<Music>> findMusic(@RequestParam(required = false) String musicName){
        List<Music> musicList = null;
        if(musicName != null){
            musicList = musicMapper.findMusicByName(musicName);
        }else {
            musicList = musicMapper.findAll();
        }
        return new ResponseBodyMessage<>(0, "查询到了歌曲的信息", musicList);
    }
}
