package com.hdkj.mall.upload.service;

import com.hdkj.mall.system.model.SystemAttachment;

import java.util.List;

/**
 * AsyncService 接口
 */
public interface AsyncService {
    void async(List<SystemAttachment> systemAttachmentList);

    String getCurrentBaseUrl();
}