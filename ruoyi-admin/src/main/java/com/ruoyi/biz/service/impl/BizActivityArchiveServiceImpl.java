package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.entity.BizActivityArchive;
import com.ruoyi.biz.mapper.BizActivityArchiveMapper;
import com.ruoyi.biz.service.IBizActivityArchiveService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 活动归档Service业务层处理
 */
@Slf4j
@Service
public class BizActivityArchiveServiceImpl extends ServiceImpl<BizActivityArchiveMapper, BizActivityArchive> implements IBizActivityArchiveService {

    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateArchive(BizActivityArchive archive) {
        // 检查是否已存在
        BizActivityArchive existing = this.getOne(new LambdaQueryWrapper<BizActivityArchive>()
                .eq(BizActivityArchive::getActivityId, archive.getActivityId()));

        if (existing != null) {
            archive.setArchiveId(existing.getArchiveId());
            archive.setDownloadCount(existing.getDownloadCount());
            archive.setViewCount(existing.getViewCount());
            archive.setUpdateTime(DateUtils.getNowDate());
            return this.updateById(archive);
        } else {
            archive.setDownloadCount(0);
            archive.setViewCount(0);
            archive.setCreateTime(DateUtils.getNowDate());
            return this.save(archive);
        }
    }

    @Override
    public String uploadPPT(Long activityId, MultipartFile file) {
        try {
            // 上传文件
            String fileName = FileUploadUtils.upload(ruoYiConfig.getProfile() + "/archive/ppt", file);
            String fileUrl = ruoYiConfig.getProfile() + "/archive/ppt/" + fileName;

            // 更新归档记录
            BizActivityArchive archive = getOrCreateArchive(activityId);
            archive.setPptUrl(fileUrl);
            this.saveOrUpdateArchive(archive);

            return fileUrl;
        } catch (Exception e) {
            log.error("上传PPT失败", e);
            throw new RuntimeException("上传PPT失败: " + e.getMessage());
        }
    }

    @Override
    public boolean setVideoUrl(Long activityId, String videoUrl) {
        BizActivityArchive archive = getOrCreateArchive(activityId);
        archive.setVideoUrl(videoUrl);
        return this.saveOrUpdateArchive(archive);
    }

    @Override
    public List<String> uploadPhotos(Long activityId, List<MultipartFile> files) {
        List<String> photoUrls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String fileName = FileUploadUtils.upload(ruoYiConfig.getProfile() + "/archive/photos", file);
                String fileUrl = ruoYiConfig.getProfile() + "/archive/photos/" + fileName;
                photoUrls.add(fileUrl);
            }

            // 更新归档记录
            BizActivityArchive archive = getOrCreateArchive(activityId);
            // 将照片URL列表转为JSON字符串存储
            String existingPhotos = archive.getPhotoUrls();
            List<String> allPhotos = new ArrayList<>();
            if (StringUtils.isNotBlank(existingPhotos)) {
                // 简单处理：假设用逗号分隔（实际可以用JSON）
                allPhotos.addAll(Arrays.asList(existingPhotos.split(",")));
            }
            allPhotos.addAll(photoUrls);
            archive.setPhotoUrls(String.join(",", allPhotos));
            this.saveOrUpdateArchive(archive);

            return photoUrls;
        } catch (Exception e) {
            log.error("上传照片失败", e);
            throw new RuntimeException("上传照片失败: " + e.getMessage());
        }
    }

    @Override
    public void recordDownload(Long archiveId) {
        this.update(new LambdaUpdateWrapper<BizActivityArchive>()
                .eq(BizActivityArchive::getArchiveId, archiveId)
                .setSql("download_count = download_count + 1"));
    }

    @Override
    public void recordView(Long archiveId) {
        this.update(new LambdaUpdateWrapper<BizActivityArchive>()
                .eq(BizActivityArchive::getArchiveId, archiveId)
                .setSql("view_count = view_count + 1"));
    }

    @Override
    public BizActivityArchive getByActivityId(Long activityId) {
        return this.getOne(new LambdaQueryWrapper<BizActivityArchive>()
                .eq(BizActivityArchive::getActivityId, activityId));
    }

    /**
     * 获取或创建归档记录
     */
    private BizActivityArchive getOrCreateArchive(Long activityId) {
        BizActivityArchive archive = this.getByActivityId(activityId);
        if (archive == null) {
            archive = new BizActivityArchive();
            archive.setActivityId(activityId);
            archive.setDownloadCount(0);
            archive.setViewCount(0);
        }
        return archive;
    }
}

