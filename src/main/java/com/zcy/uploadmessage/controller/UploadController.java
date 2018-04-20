package com.zcy.uploadmessage.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zcy.uploadmessage.service.WxService;
import com.zcy.uploadmessage.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


@Controller

public class UploadController {
    private static final Logger logger = Logger.getLogger("");

    @Autowired
    WxService wxService;


    //跳转到上传页面
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload() {
        return "upload";
    }

    //微信推送
    @RequestMapping(value = "/pushMessage", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String pushMessage(@RequestParam("data") String data, @RequestParam("url") String url, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String result = wxService.getWxMessage(data, url);

        return result;
    }

    //获取mediaid
    @RequestMapping(value = "/getMediaId", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String uploadMediaImg(@RequestParam("file") MultipartFile file, @RequestParam("url") String url,
                                 HttpServletResponse response) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "*");
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        /*System.out.println("fileName-->" + fileName);
        System.out.println("getContentType-->" + contentType);*/
        String filePath = "/tmp/upload/";
        //String filePath = request.getSession().getServletContext().getRealPath("imgupload/");
        try {
            FileUtil.uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            // TODO: handle exception
        }
        //返回json
        System.out.println("uploadimg success");

        File Sendfile = new File(filePath + fileName);

        logger.info("wxurl:"+url);

        JSONObject result = wxService.send(url,filePath+fileName);

        logger.info("uploadMediaImg result"+result);
        return JSON.toJSONString(result);

    }
}
