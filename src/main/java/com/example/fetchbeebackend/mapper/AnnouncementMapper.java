package com.example.fetchbeebackend.mapper;

import com.example.fetchbeebackend.entity.Announcement;
import com.example.fetchbeebackend.vo.AnnouncementVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告Mapper接口
 */
@Mapper
public interface AnnouncementMapper {

    /**
     * 插入公告
     */
    int insert(Announcement announcement);

    /**
     * 更新公告
     */
    int update(Announcement announcement);

    /**
     * 逻辑删除公告（修改status为0）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询公告
     */
    Announcement findById(@Param("id") Long id);

    /**
     * 查询所有公告（管理员用，包含已下架）
     */
    List<AnnouncementVO> findAll();

    /**
     * 查询最新的已发布公告（普通用户用）
     */
    AnnouncementVO findLatestPublished();

    /**
     * 查询所有已发布公告
     */
    List<AnnouncementVO> findAllPublished();
}
