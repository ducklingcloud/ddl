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
package net.duckling.ddl.service.lottery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import net.duckling.ddl.service.lottery.model.Lottery;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

public class LotteryConfigParser {

    private String configPath;

    private static Logger LOG = Logger.getLogger(LotteryConfigParser.class);

    public LotteryConfigParser(String path) {
        this.configPath = path;
    }

    public LotteryConfigParser() {
        this.configPath = System.getProperty("ddl.root") + "WEB-INF" + File.separator + "conf" + File.separator
                + "lottery.yml";
    }

    public Lottery loadConfig() {
        Yaml yaml = new Yaml();
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(new File(this.configPath)), "UTF-8");
            return yaml.loadAs(reader, Lottery.class);
        } catch (final FileNotFoundException | UnsupportedEncodingException fnfe) {
            LOG.error("We had a problem reading the YAML from the file because we couldn't find the file." + fnfe);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (final IOException ioe) {
                    LOG.error("We got the following exception trying to clean up the reader: " + ioe);
                }
            }
        }
        return null;
    }

}
