package com.example.demo.mapper;

import com.example.demo.model.Music;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavouriteMapper {
    /**
     * 根据用户id查找收藏歌曲
     */
    List<Music> findFavouriteByUserId(int userId);
    /**
     * 根据用户id和歌名查找
     */
    List<Music> findFavouriteByKeyAndUID(String musicName, int userId);
    Music findFavouriteByMIDAndUID(@Param("userId") int userId,@Param("musicId") int musicId);
    Boolean insertFavourite(@Param("userId") int userId,@Param("musicId") int musicId);
    int deleteFavourite(@Param("userId") int userId,@Param("musicId") int musicId);
    int deleteLoveMusicById(int musicId);
}
