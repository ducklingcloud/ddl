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

package net.duckling.ddl.service.export.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.file.DFileSaver;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

/**
 * In this class, the saver method will save the file received from CLB under
 * the @param path path.
 *
 * @date 2011-9-24
 * @author JohnX
 */
public class ExportAttachSaver implements DFileSaver {
    protected static final Logger LOGGER = Logger.getLogger(ExportAttachSaver.class);
    private String path = null;
    private int tid;
    private ArchiveOutputStream out = null;
    private int rid;
    public ExportAttachSaver(String path, int tid, int rid, ArchiveOutputStream out) {
        this.path = path;
        this.out = out;
        this.tid = tid;
        this.rid = rid;
    }

    public void save(String filename, InputStream in) {
        String newFilename = getNormalFilename(filename);
        int dotIndex = newFilename.lastIndexOf(".");
        dotIndex = (dotIndex <= 0) ? newFilename.length() : dotIndex;
        newFilename = newFilename.substring(0, dotIndex) + "_" + rid + "_" + tid + "_" + LynxConstants.TYPE_FILE
                + newFilename.substring(dotIndex, newFilename.length());
        try {
            newFilename = java.net.URLDecoder.decode(newFilename, "utf8");
            out.putArchiveEntry(new ZipArchiveEntry(path + "/" + newFilename));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("文件名解码失败！", e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    // process Chinese characters
    private String getNormalFilename(String filename) {
        // transform the "+" into " "
        filename = filename.replaceAll("\\+", " ");
        Pattern p = Pattern.compile("(\\%[0-9A-F]{2}){3}");
        String[] ss = p.split(filename);
        StringBuffer sb = new StringBuffer();
        Matcher m = p.matcher(filename);
        for (int i = 0; i < ss.length; i++) {
            sb.append(ss[i]);
            if (m.find()) {
                sb.append(code2Utf8(m.group()));
            }
        }
        return sb.toString();
    }

    /**
     * @param group
     * @return string coded by utf8
     */
    private String code2Utf8(String group) {
        byte bss[] = new byte[group.length() / 3];
        byte bs[];
        String ss = "";
        try {
            bs = group.getBytes("utf8");
            int j = 0;
            byte b1 = 0, b2 = 0;
            for (int i = 1; i < bs.length; i += 2) {
                b1 = b2 = 0;
                if (bs[i] < 60) {
                    b1 = (byte) (bs[i] - 48);
                } else {
                    b1 = (byte) (bs[i] - 55);
                }
                i++;
                if (bs[i] < 60) {
                    b2 = (byte) (bs[i] - 48);
                } else {
                    b2 = (byte) (bs[i] - 55);
                }
                bss[j++] = (byte) (b1 << 4 | (b2 & 0x0f));
            }
            ss = new String(bss, "utf8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage() + ";group=" + group, e);
        }
        return ss;
    }

    @Override
    public void setLength(long length) {

    }

}
