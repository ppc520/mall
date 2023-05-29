package com.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 通用工具类
 */
public class CommonUtil {

    /**
     * 随机生成密码
     *
     * @param phone 手机号
     * @return 密码
     * 使用des方式加密
     */
    public static String createPwd(String phone) {
        String password = "Abc" + MallUtil.randomCount(10000, 99999);
        return MallUtil.encryptPassword(password, phone);
    }

    /**
     * 随机生成用户昵称
     *
     * @param phone 手机号
     * @return 昵称
     */
    public static String createNickName(String phone) {
        return DigestUtils.md5Hex(phone + DateUtil.getNowTime()).
                subSequence(0, 12).
                toString();
    }

}
