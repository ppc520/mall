package com.hdkj.mall.upload.service;

import com.hdkj.mall.upload.vo.CloudVo;

/**
 * OssService 接口
 */
public interface OssService {
    void upload(CloudVo cloudVo, String webPth, String localFile, Integer id);
}