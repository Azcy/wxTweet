package com.zcy.uploadmessage.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zcy.uploadmessage.util.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
@Component
public class WxService {
    private static final Logger logger=Logger.getLogger("");

    //调起微信接口

    public String getWxMessage(String data,String url){
        String result=null;
        try {
             result =  HttpClient.doPost(url,data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 上传素材
     *
     * @param uploadurl
     *            apiurl
     * @param access_token
     *            访问token
     * @param data
     *            提交数据
     * @return
     */
  /*  public  String uploadFodder(String uploadurl, String access_token,
                                      String data)
    {
        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        String posturl = String.format("%s?access_token=%s", uploadurl,
                access_token);
        PostMethod post = new PostMethod(posturl);
        post
                .setRequestHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:30.0) Gecko/20100101 Firefox/30.0");
        post.setRequestHeader("Host", "file.api.weixin.qq.com");
        post.setRequestHeader("Connection", "Keep-Alive");
        post.setRequestHeader("Cache-Control", "no-cache");
        String result = null;
        try
        {
            post.setRequestBody(data);
            int status = client.executeMethod(post);
            if (status == HttpStatus.SC_OK)
            {
                String responseContent = post.getResponseBodyAsString();
                System.out.println(responseContent);
                // 初始化解析json格式的对象
                JSONObject json = JSON.parseObject(responseContent);
                if (json.get("errcode") == null)
                {// 正确 { "type":"news", "media_id":"CsEf3ldqkAYJAU6EJeIkStVDSvffUJ54vqbThMgplD-VJXXof6ctX5fI6-aYyUiQ","created_at":1391857799}
                    result = json.getString("media_id");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return result;
        }
    }*/


    /**
     * 上传多媒体文件
     *
     * @param url
     *            访问url

     * @param file
     *            文件对象
     * @return
     */
    public  String uploadImage(String url, File file)
    {
        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        String uploadurl = url;
        PostMethod post = new PostMethod(uploadurl);
        post
                .setRequestHeader(
                        "User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64…) Gecko/20100101 Firefox/58.0");
        post.setRequestHeader("Host", "file.api.weixin.qq.com");
        post.setRequestHeader("Connection", "Keep-Alive");
        post.setRequestHeader("Cache-Control", "no-cache");
        String result = null;
        try
        {
            if (file != null && file.exists())
            {
                FilePart filepart = new FilePart("media", file, "image/jpeg",
                        "UTF-8");
                Part[] parts = new Part[] { filepart };
                MultipartRequestEntity entity = new MultipartRequestEntity(
                        parts,

                        post.getParams());
                post.setRequestEntity(entity);
                int status = client.executeMethod(post);
                if (status == HttpStatus.SC_OK)
                {
                    String responseContent = post.getResponseBodyAsString();
                   // 初始化解析json格式的对象
                    JSONObject json = JSON.parseObject(responseContent);
                    if (json.get("errcode") == null)// {"errcode":40004,"errmsg":"invalid media type"}
                    { // 上传成功  {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
                        result = json.toJSONString();
                        logger.info("上传成功");
                    }
                }

                logger.info("status:"+status);
            }
            logger.info("not file");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return result;
        }
    }


    /**
     * 文件上传到微信服务器
     * @param filePath 文件路径
     * @return JSONObject
     * @throws Exception
     */
    public  JSONObject send(String url, String filePath) throws Exception {
        String result = null;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }
        /**
         * 第一部分
         */
        URL urlObj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
        con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false); // post方式不能使用缓存
        // 设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        // 设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ BOUNDARY);
        // 请求正文信息
        // 第一部分：
        StringBuilder sb = new StringBuilder();
        sb.append("--"); // 必须多两道线
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\""+ file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");
        byte[] head = sb.toString().getBytes("utf-8");
        // 获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        // 输出表头
        out.write(head);
        // 文件正文部分
        // 把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();
        // 结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
        out.write(foot);
        out.flush();
        out.close();
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                buffer.append(line);
            }
            if(result==null){
                result = buffer.toString();
            }
        } catch (IOException e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
            throw new IOException("数据读取异常");
        } finally {
            if(reader!=null){
                reader.close();
            }
        }
        JSONObject jsonObj =JSON.parseObject(result);
        return jsonObj;
    }

}
