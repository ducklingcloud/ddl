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

package net.duckling.ddl.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2013-09-22
 *
 * @author xiejj@cnic.cn
 *
 */
public class DucklingProperties extends Properties {
    private static final long serialVersionUID = 1L;
    private static final Pattern PATTERN = Pattern
            .compile("([^\\}]*)\\$\\{([^}]*)\\}(.*)");

    public DucklingProperties(boolean isXml, String filename)
            throws IOException {
        FileInputStream in = new FileInputStream(filename);
        try {
            if (isXml) {
                loadFromXML(in);
            } else {
                load(in);
            }
        } finally {
            in.close();
        }
    }

    public DucklingProperties(boolean isXml, InputStream in)
            throws InvalidPropertiesFormatException, IOException {
        if (isXml) {
            loadFromXML(in);
        } else {
            load(in);
        }
    }

    public DucklingProperties() {
    }

    public String getProperty(String key) {
        String value = super.getProperty(key);
        if (value != null) {
            return replace(value);
        } else {
            return null;
        }
    }

    public boolean getBool(String key, boolean b) {
        String value = getProperty(key);
        if (value != null) {
            return Boolean.valueOf(value);
        } else {
            return b;
        }
    }

    public int getInt(String key, int i) {
        String value = getProperty(key);
        try {
            if (value != null) {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {

        }
        return i;
    }

    private String replace(String input) {
        input = input.trim();
        int dollerPos = input.indexOf('$');
        if (dollerPos != -1) {
            Matcher matcher = PATTERN.matcher(input);
            if (matcher.matches()) {
                String left = matcher.group(1);

                String value = getProperty(matcher.group(2));
                if (value == null) {
                    value = matcher.group(2);
                }

                String right = replace(matcher.group(3));
                return left + value + right;
            }
        }
        return input;
    }
}
