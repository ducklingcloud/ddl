/*
 * Copyright (c) 2022 kai.nan@icloud.com
 * Licensed under the Apache License, Version 2.0
 */

package net.duckling.ddl.service.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* import javax.servlet.ServletContext; */
/* import javax.servlet.http.HttpServletRequest; */
/* import javax.servlet.http.HttpServletResponse; */

public class TransferSaver implements DFileSaver {
    private static final Logger log =
            LoggerFactory.getLogger(TransferSaver.class);
    private OutputStream out = null;
    private long length = -1;

    public TransferSaver(OutputStream out) {
        this.out = out;
    }

    @Override
    public void save(String filename, InputStream _in) {
        // Two streams (in/out) will be closed by try-with-resource
        try (InputStream in = _in;
             OutputStream out = this.out) {
            byte buf[] = new byte[4096];
            int read;
            while ((read = in.read(buf, 0, 4096)) != -1) {
                out.write(buf, 0, read);
            }
        } catch (IOException e) {
            log.error("Error when DDL transfers data from CLB to user.\n{}",
                      e.toString());
            log.debug("Failed to transfer between streams.", e);
        }
    }

    /* private static String getMimeType(HttpServletRequest req, String fileName) { */
    /*     String mimetype = null; */

    /*     if (req != null) { */
    /*         ServletContext s = req.getSession().getServletContext(); */

    /*         if (s != null) { */
    /*             mimetype = s.getMimeType(fileName.toLowerCase()); */
    /*         } */
    /*     } */

    /*     if (mimetype == null) { */
    /*         mimetype = "application/binary"; */
    /*     } */

    /*     return mimetype; */
    /* } */

    @Override
    public void setLength(long length) {
        this.length = length;
    }

}
