package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.entity.BizActivityArchive;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 活动归档Service接口
 */
public interface IBizActivityArchiveService extends IService<BizActivityArchive> {

    /**
     * 创建或更新归档记录
     *
     * @param archive 归档信息
     * @return 是否成功
     */
    boolean saveOrUpdateArchive(BizActivityArchive archive);

    /**
     * 上传PPT文件
     *
     * @param activityId 活动ID
     * @param file PPT文件
     * @return 文件访问URL
     */
    String uploadPPT(Long activityId, MultipartFile file);

    /**
     * 上传视频文件或设置视频链接
     *
     * @param activityId 活动ID
     * @param videoUrl 视频URL（可以是上传的文件URL或外部链接）
     * @return 是否成功
     */
    boolean setVideoUrl(Long activityId, String videoUrl);

    /**
     * 上传精彩瞬间照片
     *
     * @param activityId 活动ID
     * @param files 照片文件列表
     * @return 照片URL列表
     */
    List<String> uploadPhotos(Long activityId, List<MultipartFile> files);

    /**
     * 记录下载次数
     *
     * @param archiveId 归档ID
     */
    void recordDownload(Long archiveId);

    /**
     * 记录观看次数
     *
     * @param archiveId 归档ID
     */
    void recordView(Long archiveId);

    /**
     * 根据活动ID获取归档信息
     *
     * @param activityId 活动ID
     * @return 归档信息
     */
    BizActivityArchive getByActivityId(Long activityId);
}

