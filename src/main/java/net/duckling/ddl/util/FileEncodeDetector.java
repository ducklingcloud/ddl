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
package net.duckling.ddl.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * 文件流字符编码探测器
 * @author Yangxp
 * @since 2012-12-03
 */
public class FileEncodeDetector {

    private static final Logger LOG = Logger.getLogger(FileEncodeDetector.class);
    private static final String GBK = "GBK";

    /**
     * 探测文件编码类型，默认返回GBK。<br/>
     * @param file
     * @return
     * @throws IOException
     */
    public static String guessEncoding(File file) throws IOException{
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            return guessEncoding(in, (int)file.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            if(null != in){
                in.close();
            }
        }
        return GBK;
    }

    /**
     * 根据输入流探测文件编码类型，默认返回GBK.<br/>
     * 注意：此方法并不会关闭文件流。
     * @param in
     * @return
     */
    public static String guessEncoding(BufferedInputStream bis, int fileLength){
        String charset = GBK;
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if ( read == -1 ){
                return charset;
            }
            StringBuilder headerCharSet = new StringBuilder();
            checked = guessFromFirst3Bytes(first3Bytes, headerCharSet);
            charset = headerCharSet.toString();
            if ( !checked ) {
                bis.reset();
                bis.mark(fileLength+1);
                charset = guessFromWholeStream(bis,charset, fileLength-3);
            }
            bis.reset();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return charset;
    }

    private static boolean guessFromFirst3Bytes(byte[] first3Bytes, StringBuilder charSet){
        boolean checked = false;
        if ( first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE ) {
            charSet.append("UTF-16LE");
            checked = true;
        }
        else if ( first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF ) {
            charSet.append("UTF-16BE");
            checked = true;
        }
        else if ( first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF ) {
            charSet.append("UTF-8");
            checked = true;
        }else{
            charSet.append(GBK);
            checked = false;
        }
        return checked;
    }

    private static String guessFromWholeStream(BufferedInputStream bis, String charset, int byteSize) throws IOException{
        String result = charset;
        byte[] temp = new byte[byteSize];
        while((bis.read(temp))!=-1){
            if(isUTF8Bytes(temp)){
                result = "UTF-8";
                break;
            }
        }
        return result;
    }

    private static boolean isUTF8Bytes(byte[] data){
        int charByteCounter = 1; //计算当前正分析的字符应有的字节数
        byte curByte; //当前分析的字节
        for(int i=0; i<data.length; i++){
            curByte = data[i];
            if(charByteCounter == 1){
                if(curByte >= 0x80){
                    //判断当前
                    while(((curByte <<= 1) & 0x80)!=0){
                        charByteCounter++;
                    }
                    if(charByteCounter == -1 || charByteCounter > 6){
                        return false;
                    }
                }
            }else{
                if((curByte & 0x80) == 0){
                    return false;
                }
                charByteCounter-- ;
            }
        }
        if(charByteCounter > 1){
            LOG.error("非预期的byte格式");
            return false;
        }
        return true;
    }
}
