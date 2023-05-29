package com.hdkj.mall.upload.service;

import com.hdkj.mall.upload.vo.CloudVo;
import com.qiniu.storage.UploadManager;

/**
 * QiNiuService 接口
 */
public interface QiNiuService {
    void upload(UploadManager uploadManager, CloudVo cloudVo, String upToken, String webPth, String localFile, Integer id);
}