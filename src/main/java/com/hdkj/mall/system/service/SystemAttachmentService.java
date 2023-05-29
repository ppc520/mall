package com.hdkj.mall.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.system.model.SystemAttachment;
import com.hdkj.mall.upload.vo.FileResultVo;

import java.util.List;

/**
 * SystemAttachmentService 接口
 */
public interface SystemAttachmentService extends IService<SystemAttachment> {
    void create(FileResultVo file, Integer pid);

    void async();

    void updateCloudType(Integer attId, int type);

    List<SystemAttachment> getList(Integer pid,PageParamRequest pageParamRequest);

    String prefixImage(String path);

    String prefixFile(String str);

    String clearPrefix(String attribute);

    /**
     * 附件基本查询
     * @param systemAttachment 附件参数
     * @return 附件
     */
    List<SystemAttachment> getByEntity(SystemAttachment systemAttachment);
}
