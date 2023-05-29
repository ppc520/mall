package com.hdkj.mall.upload.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.exception.MallException;
import com.hdkj.mall.system.service.SystemAttachmentService;
import com.hdkj.mall.upload.service.OssService;
import com.hdkj.mall.upload.vo.CloudVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;


/**
 * AsyncServiceImpl 同步到云服务
 */
@Service
public class OssServiceImpl implements OssService {

    private static final Logger logger = LoggerFactory.getLogger(OssServiceImpl.class);

    @Lazy
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 同步到阿里云oss
     * @param cloudVo CloudVo
     * @param webPth String web可访问目录
     * @param localFile String 服务器文件绝对地址
     * @param id Integer 文件id
     * @author Mr.Zhou
     * @since 2022-05-06
     */
    @Async  //多线程不可传递对象模式，后面的赋值会覆盖前面的数据
    @Override
    public void upload(CloudVo cloudVo, String webPth, String localFile, Integer id){
        logger.info("上传文件" + id + "开始：" + localFile);
        OSS ossClient = new OSSClientBuilder().build(cloudVo.getRegion(), cloudVo.getAccessKey(), cloudVo.getSecretKey());
        try {
            //判断bucket是否存在
            if (!ossClient.doesBucketExist(cloudVo.getBucketName())){
                ossClient.createBucket(cloudVo.getBucketName());
            }

            File file = new File(localFile);
            if(!file.exists()){
                logger.info("上传文件"+ id + localFile + "不存在：");
                return;
            }
            PutObjectRequest putObjectRequest = new PutObjectRequest(cloudVo.getBucketName(), webPth, file);
            // 上传文件。
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            logger.info("上传文件" + id + " -- 结束：" + putObjectResult.getETag());

            //更新数据库
            systemAttachmentService.updateCloudType(id, 3);
        } catch (Exception e){
            throw new MallException(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }


}

