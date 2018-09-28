package com.happok.xiyan.uploadtools.service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happok.xiyan.uploadtools.common.UserInfo;
import com.happok.xiyan.uploadtools.config.CommonConfig;
import com.happok.xiyan.uploadtools.config.DatatimeConfig;
import com.happok.xiyan.uploadtools.config.PathConfig;
import com.happok.xiyan.uploadtools.entity.BasicInfoEntity;
import com.happok.xiyan.uploadtools.entity.SharedEntity;
import com.happok.xiyan.uploadtools.entity.SourceEntity;
import com.happok.xiyan.uploadtools.feignclient.ResourceCenter;
import com.happok.xiyan.uploadtools.mapper.now.SourceMapper;
import com.happok.xiyan.uploadtools.mapper.old.PathMapper;
import com.happok.xiyan.uploadtools.util.Base64Util;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VideoFile {

    @Resource
    private PathMapper pathMapper = null;

    @Resource
    private SourceMapper sourceMapper = null;

    @Resource
    private ResourceCenter resourceCenter = null;

    @Autowired
    private DatatimeConfig datatimeConfig;

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private PathConfig pathConfig;

    @Scheduled(cron = "0 0/2 * * * *")
    public void scheduled1() {
        if (commonConfig.getSql()) {
            log.info("查询数据库");
            if (!StringUtils.isEmpty(datatimeConfig.getBegintime())) {
                String end_time;
                if (StringUtils.isEmpty(datatimeConfig.getEndtime())) {
                    log.info("结束时间为空");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    end_time = df.format(new Date());
                } else {
                    end_time = datatimeConfig.getEndtime();
                }
                List<BasicInfoEntity> pathEntities = pathMapper.getResourcePath(datatimeConfig.getBegintime(), end_time);
                log.info("数据总条数 = " + pathEntities.size());

                for (BasicInfoEntity item : pathEntities) {
                    try {
                        if (StringUtils.isEmpty(item.getAuthor())) {
                            item.setAuthor("北京汉博");
                        }
                        if (sourceMapper.addSourceInfoOne(item)) {
                            log.info("添加成功 " + item.toString());
                        }
                    } catch (DuplicateKeyException exc) {
                        log.info("重复添加，跳过");
                        continue;
                    }
                }
            }
        }

        // 单个取出来
        log.info("开始");
        SourceEntity sourceEntity = sourceMapper.getSourceInfo();
        if (ObjectUtils.isEmpty(sourceEntity)) {
            log.info("---> null");
            return;
        }

        try {
            String filePath = pathConfig.getRoot() + sourceEntity.getPath_name();
            File file = new File(filePath);
            if (!file.exists()) {
                log.info("文件不存在:" + filePath);
                sourceMapper.updateSourceUpload(sourceEntity.getId());
                return;
            }
            if (file.length() == 0) {
                log.info("文件大小为0KB:" + filePath);
                sourceMapper.updateSourceUpload(sourceEntity.getId());
                return;
            }
            log.info(filePath);
            String str = formUpload(commonConfig.getUrl(), filePath, "video/mp4");
            log.info(str);
            if (!StringUtils.isEmpty(str)) {
                log.info("上传资源成功,路径 = " + sourceEntity.getPath_name());
                JSONObject jsonObject = JSON.parseObject(str);
                sourceEntity.setPath_name(jsonObject.getString("result"));
                JSONObject addResult = resourceCenter.addResource(commonConfig.getBase64(), sourceEntity);

                if (addResult.getInteger("code").equals(200)) {
                    log.info("添加成功");
                    Integer sourceId = addResult.getJSONObject("result").getInteger("id");
                    // 设置免审核用户
                    String userInfo = Base64Util.decodeData(commonConfig.getBase64());
                    UserInfo user = null;
                    if (null != userInfo && !"".equals(userInfo)) {
                        ObjectMapper mapper = new ObjectMapper();
                        user = mapper.readValue(userInfo, UserInfo.class);
                    }
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("userId", user.getId());
                    JSONObject json = resourceCenter.addAssessorUser(commonConfig.getBase64(), requestBody);

                    if (json.getInteger("code").equals(200)) {
                        log.info("添加免审核用户成功");
                    }
                    SharedEntity sharedEntity = new SharedEntity();
                    sharedEntity.setShared("public");
                    sharedEntity.setSource_id(sourceId + "");
                    // 添加公开资源
                    JSONObject shareObj = resourceCenter.adSharedSource(commonConfig.getBase64(), sharedEntity);
                    if (shareObj.getInteger("code").equals(200)) {
                        log.info("添加公开资源成功");
                    }
                    sourceMapper.updateSourceUpload(sourceEntity.getId());
                    log.info("完成");
                }
            } else {
                log.info("路径" + str.toString());
            }
        } catch (FeignException exc) {
            log.error("接口调用异常 ：" + exc.toString());
        } catch (Exception exc) {
            log.error("异常 " + exc.toString());
        }
    }

    public static String formUpload(String urlStr, String path, String contentType) {
        String res = "";
        HttpURLConnection conn = null;
        // boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "------WebKitFormBoundaryRLUF3aohDarb2puA";
        log.info("1");
        try {
            URL url = new URL(urlStr);
            log.info("2");
            conn = (HttpURLConnection) url.openConnection();
            log.info("3");
            //设置为true后，之后就可以使用conn.getOutputStream()
            conn.setDoOutput(true);
            //设置为true后，之后就可以使用conn.getInputStream()
            conn.setDoInput(true);
            conn.setUseCaches(true);
            conn.setChunkedStreamingMode(2048);// 为了解决大文件上传添加
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            File file = new File(path);
            log.info("4");
            conn.setRequestProperty("Content-Length", String.valueOf(file.length()));
            log.info("5");
            conn.connect();
            log.info("6");
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            log.info("7");

            String filename = file.getName();

            StringBuffer strBuf = new StringBuffer();
            log.info("8");
            strBuf.append("\r\n").append("--").append(BOUNDARY)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\""
                    + "filename" + "\"; filename=\"" + filename
                    + "\"\r\n");
            strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
            log.info("9");
            out.write(strBuf.toString().getBytes());
            log.info("10");
            DataInputStream in = new DataInputStream(
                    new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[2048];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
                out.flush();// 解决大文件上传添加
            }
            log.info("11");
            in.close();

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            StringBuffer str2Buf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                str2Buf.append(line).append("\n");
            }
            res = str2Buf.toString();
            reader.close();
            reader = null;
            //获取返回码
            int respCode = conn.getResponseCode();
            System.out.println(respCode);
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
//            if (conn != null) {
//                conn.disconnect();
//                conn = null;
//            }
        }
        return res;
    }
}
