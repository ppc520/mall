package com.hdkj.mall.upload.service;

import com.qcloud.cos.COSClient;
import com.hdkj.mall.upload.vo.CloudVo;

/**
 * CosService 接口
 */
public interface CosService {
    void upload(CloudVo cloudVo, String webPth, String localFile, Integer id, COSClient cosClient);
}