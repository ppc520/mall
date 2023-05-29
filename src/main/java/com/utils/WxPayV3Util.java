package com.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.exception.MallException;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.util.Arrays;

/**
 * 微信V3支付工具类
 */
public class WxPayV3Util {

    /**
     * 发起批量转账API 批量转账到零钱
     *
     * @param requestUrl
     * @param requestJson 组合参数
     * @param wechatPayserialNo 商户证书序列号
     * @param mchID4M  商户号
     * @param privatekeypath  商户私钥证书路径
     * @return
     */
    public static String postTransBatRequest(
            String requestUrl,
            String requestJson,
            String wechatPayserialNo,
            String mchID4M,
            String privatekeypath) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            //商户私钥证书
            HttpPost httpPost = new HttpPost(requestUrl);
            // NOTE: 建议指定charset=utf-8。低于4.4.6版本的HttpCore，不能正确的设置字符集，可能导致签名错误
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", "application/json");
            //"55E551E614BAA5A3EA38AE03849A76D8C7DA735A");
            httpPost.addHeader("Wechatpay-Serial", wechatPayserialNo);
            //-------------------------核心认证 start-----------------------------------------------------------------
            String strToken = VechatPayV3Util.getToken("POST",
                    "/v3/transfer/batches",
                    requestJson,mchID4M,wechatPayserialNo, privatekeypath);

            System.out.println("微信转账token "+strToken);
            // 添加认证信息
            httpPost.addHeader("Authorization",
                    "WECHATPAY2-SHA256-RSA2048" + " "
                            + strToken);
            //---------------------------核心认证 end---------------------------------------------------------------
            httpPost.setEntity(new StringEntity(requestJson, "UTF-8"));
            //发起转账请求
            response = httpclient.execute(httpPost);
            entity = response.getEntity();//获取返回的数据
            System.out.println("-----getHeaders.Request-ID:"+response.getHeaders("Request-ID"));
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if(httpclient !=null){
                    httpclient.close();
                }
                if(response !=null){
                    response.close();
                }
            }catch (Exception e){}

        }
        return null;
    }

    /**
     * 微信通讯client
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getClient(String wechatPayserialNo,
                                                 String mchID4M,
                                                 String privatekeypath,
                                                 String wechatCertificatePath) {
        /**商户私钥文件*/
        File privateKeyFile = new File(privatekeypath);
        InputStream privateKeyInputStream = FileUtil.getInputStream(privateKeyFile);

        /**微信平台证书文件*/
        File platFormKeyFile= new File(wechatCertificatePath);
        InputStream platformCertInputStream = FileUtil.getInputStream(platFormKeyFile);
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(privateKeyInputStream);
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(mchID4M, wechatPayserialNo, merchantPrivateKey)
                .withWechatPay(Arrays.asList(PemUtil.loadCertificate(platformCertInputStream)));
        CloseableHttpClient httpClient = builder.build();
        return httpClient;
    }

    public static final Integer SUCCESS_CODE = 200;

    /**
     * 商家明细单号查询明细单API
     * @throws URISyntaxException
     * @throws IOException
     */
    public static JSONObject queryBatch(
            String outBatchNo,
            String requestUrl,
            String wechatPayserialNo,
            String mchID4M,
            String privatekeypath,
            String wechatCertificatePath
    )  {
        CloseableHttpClient httpClient =null;
        CloseableHttpResponse response =null;
        try {
            httpClient = getClient(wechatPayserialNo,mchID4M,privatekeypath,wechatCertificatePath);
            //批次号
    //        String outBatchNo = "5f48ac004fdad8fa972872a31769fe20";
            StringBuilder url = new StringBuilder(requestUrl);
            url.append(outBatchNo).append("?need_query_detail=true").append("&detail_status=ALL");
            URIBuilder uriBuilder = new URIBuilder(url.toString());
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("Content-Type", "application/json");
            httpGet.addHeader("Accept", "application/json");
            response = httpClient.execute(httpGet);
            if (SUCCESS_CODE.equals(response.getStatusLine().getStatusCode())) {
                String bodyAsString = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSONUtil.parseObj(bodyAsString);
                System.out.println("微信支付查询返回:" + jsonObject);
                return jsonObject;
            } else {
                //失败
                return null;
            }
        }catch (Exception e){
            throw new MallException(e.getCause().getMessage());
        }finally {
            try {
                if(response!=null){
                    response.close();
                }
                if(httpClient!=null){
                    httpClient.close();
                }
            }catch (Exception e){
                e.getMessage();
            }
        }
    }
}
