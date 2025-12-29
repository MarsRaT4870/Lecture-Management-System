package com.ruoyi.biz.controller;

import com.ruoyi.biz.domain.entity.BizActivityArchive;
import com.ruoyi.biz.service.IBizActivityArchiveService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 活动归档Controller
 */
@RestController
@RequestMapping("/biz/archive")
@RequiredArgsConstructor
public class BizActivityArchiveController extends BaseController {

    private final IBizActivityArchiveService archiveService;

    /**
     * 获取活动归档信息
     */
    @GetMapping("/{activityId}")
    public AjaxResult getArchive(@PathVariable Long activityId) {
        BizActivityArchive archive = archiveService.getByActivityId(activityId);
        if (archive == null) {
            return success(new BizActivityArchive());
        }
        // 记录观看次数
        archiveService.recordView(archive.getArchiveId());
        return success(archive);
    }

    /**
     * 创建或更新归档信息
     */
    @PreAuthorize("@ss.hasPermi('biz:archive:edit')")
    @Log(title = "活动归档", businessType = BusinessType.UPDATE)
    @PostMapping
    public AjaxResult saveArchive(@RequestBody BizActivityArchive archive) {
        return toAjax(archiveService.saveOrUpdateArchive(archive));
    }

    /**
     * 上传PPT
     */
    @PreAuthorize("@ss.hasPermi('biz:archive:edit')")
    @Log(title = "上传PPT", businessType = BusinessType.UPDATE)
    @PostMapping("/upload/ppt")
    public AjaxResult uploadPPT(@RequestParam("activityId") Long activityId,
                                @RequestParam("file") MultipartFile file) {
        String url = archiveService.uploadPPT(activityId, file);
        return success(url);
    }

    /**
     * 设置视频链接
     */
    @PreAuthorize("@ss.hasPermi('biz:archive:edit')")
    @Log(title = "设置视频链接", businessType = BusinessType.UPDATE)
    @PostMapping("/video")
    public AjaxResult setVideoUrl(@RequestParam("activityId") Long activityId,
                                  @RequestParam("videoUrl") String videoUrl) {
        return toAjax(archiveService.setVideoUrl(activityId, videoUrl));
    }

    /**
     * 上传精彩瞬间照片
     */
    @PreAuthorize("@ss.hasPermi('biz:archive:edit')")
    @Log(title = "上传照片", businessType = BusinessType.UPDATE)
    @PostMapping("/upload/photos")
    public AjaxResult uploadPhotos(@RequestParam("activityId") Long activityId,
                                   @RequestParam("files") List<MultipartFile> files) {
        List<String> urls = archiveService.uploadPhotos(activityId, files);
        return success(urls);
    }

    /**
     * 下载PPT（记录下载次数）
     */
    @GetMapping("/download/ppt/{archiveId}")
    public AjaxResult downloadPPT(@PathVariable Long archiveId) {
        BizActivityArchive archive = archiveService.getById(archiveId);
        if (archive == null || archive.getPptUrl() == null) {
            return error("文件不存在");
        }
        archiveService.recordDownload(archiveId);
        return success(archive.getPptUrl());
    }
}

