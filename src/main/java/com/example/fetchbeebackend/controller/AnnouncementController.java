package com.example.fetchbeebackend.controller;

import com.example.fetchbeebackend.common.Result;
import com.example.fetchbeebackend.service.AnnouncementService;
import com.example.fetchbeebackend.vo.AnnouncementVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公告控制器（普通用户）
 */
@Slf4j
@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 查看最新公告
     */
    @GetMapping("/latest")
    public Result<AnnouncementVO> getLatestAnnouncement() {
        log.info("查询最新公告");
        AnnouncementVO announcement = announcementService.getLatestPublishedAnnouncement();
        return Result.success(announcement);
    }
}
