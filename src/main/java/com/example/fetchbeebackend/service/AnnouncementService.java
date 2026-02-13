package com.example.fetchbeebackend.service;

import com.example.fetchbeebackend.common.ResultCode;
import com.example.fetchbeebackend.dto.CreateAnnouncementRequest;
import com.example.fetchbeebackend.dto.UpdateAnnouncementRequest;
import com.example.fetchbeebackend.entity.Announcement;
import com.example.fetchbeebackend.exception.BusinessException;
import com.example.fetchbeebackend.mapper.AnnouncementMapper;
import com.example.fetchbeebackend.vo.AnnouncementVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公告服务类
 */
@Slf4j
@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    /**
     * 创建公告（管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAnnouncement(CreateAnnouncementRequest request, Long publisherId) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setPublisherId(publisherId);
        announcement.setStatus(1); // 默认已发布

        int result = announcementMapper.insert(announcement);
        if (result <= 0) {
            log.error("创建公告失败：publisherId={}", publisherId);
            throw new BusinessException("创建公告失败");
        }

        log.info("创建公告成功：id={}, publisherId={}", announcement.getId(), publisherId);
    }

    /**
     * 更新公告（管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAnnouncement(Long id, UpdateAnnouncementRequest request) {
        // 1. 查询公告是否存在
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "公告不存在");
        }

        // 2. 更新公告
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        int result = announcementMapper.update(announcement);
        if (result <= 0) {
            log.error("更新公告失败：id={}", id);
            throw new BusinessException("更新公告失败");
        }

        log.info("更新公告成功：id={}", id);
    }

    /**
     * 删除公告（管理员，逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnouncement(Long id) {
        // 1. 查询公告是否存在
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "公告不存在");
        }

        // 2. 逻辑删除（修改status为0）
        int result = announcementMapper.deleteById(id);
        if (result <= 0) {
            log.error("删除公告失败：id={}", id);
            throw new BusinessException("删除公告失败");
        }

        log.info("删除公告成功：id={}", id);
    }

    /**
     * 查询所有公告（管理员用，包含已下架）
     */
    public List<AnnouncementVO> getAllAnnouncements() {
        return announcementMapper.findAll();
    }

    /**
     * 查询最新的已发布公告（普通用户用）
     */
    public AnnouncementVO getLatestPublishedAnnouncement() {
        return announcementMapper.findLatestPublished();
    }
}
