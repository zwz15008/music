package com.example.demo.mapper;

import com.example.demo.model.Music;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MusicMapper {
    /**
     * 插入音乐信息
     * @return
     */
    int insert(@Param("title") String title, @Param("singer") String singer, @Param("time") String time, @Param("url") String url, @Param("userid") int userid);

    /**
     * 根据ID删除音乐
     * @param musicId
     * @return
     */
    int deleteMusicById(int musicId);

    /**
     * 根据ID查询音乐
     * @param id
     * @return
     */
    Music findMusicById(int id);
    /**
     * 查询所有音乐
     */
    List<Music> findAll();
    /**
     * 歌名 模糊查询音乐
     */
    List<Music> findMusicByName(String name);

}
