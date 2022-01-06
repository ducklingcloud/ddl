/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 *
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.duckling.ddl.web.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.util.IdentifyingCode;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PictureCheckCodeUtil;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 *
 * @author zhonghui
 *
 */
@Controller
@RequestMapping("/system/pictrueCheckCode")
public class PictureCheckCodeController {

    /**
     * 获取指定类型的一个验证图片， 并把验证号加入session
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(params="func=getImage")
    public void getImage(HttpServletRequest request,HttpServletResponse response) throws IOException{
        String type = request.getParameter("type");
        //设置不缓存图片
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "No-cache");
        response.setDateHeader("Expires", 0) ;
        //指定生成的相应图片
        response.setContentType("image/jpeg") ;

        String code = IdentifyingCode.getRandomCode(4);
        IdentifyingCode idCode = new IdentifyingCode(code);

        BufferedImage image = idCode.getImage();

        ImageIO.write(image, "JPEG", response.getOutputStream()) ;
        request.getSession().setAttribute(type, code.toLowerCase());
    }

    /**
     *
     * @param request
     * @param response
     */
    @RequestMapping(params="func=checkCode")
    public void checkCode(HttpServletRequest request,HttpServletResponse response){
        String checkCode = request.getParameter("checkCode");
        String type = request.getParameter("type");
        boolean result = PictureCheckCodeUtil.checkCode(request, checkCode, type,false);
        JSONObject re = new JSONObject();
        if(result){
            re.put("status", "success");
        }else{
            re.put("status", "false");
        }
        JsonUtil.writeJSONObject(response, re);
    }

}
