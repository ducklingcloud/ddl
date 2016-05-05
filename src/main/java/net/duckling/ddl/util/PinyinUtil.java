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

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.log4j.Logger;

/**
 * @date 2011-11-15
 * @author JohnX
 */

public final class PinyinUtil {
    private PinyinUtil() {
    }

    public static final char CH_START = '\u4e00';
    public static final char CH_END = '\u9fa5';
    public static final char SPLIT_CHAR = ';';

    private static final Logger LOG = Logger.getLogger(PinyinUtil.class);

    public static String getPinyin(String str) {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch < PinyinUtil.CH_START || ch > PinyinUtil.CH_END) {
                sb.append(ch);
            } else {
                String[] pinyinArray = null;
                try {
                    pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, outputFormat);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    LOG.error(e.getMessage(), e);
                }
                if (pinyinArray != null && pinyinArray.length > 0) {
                    sb.append(pinyinArray[0]);
                }
            }
        }
        boolean firstChineseChar = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch >= PinyinUtil.CH_START && ch <= PinyinUtil.CH_END) {
                if (firstChineseChar) {
                    sb.append(SPLIT_CHAR);
                    firstChineseChar = false;
                }
                String[] pinyinArray = null;
                try {
                    pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, outputFormat);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    LOG.error(e.getMessage(), e);
                }
                if (pinyinArray != null && pinyinArray.length > 0) {
                    sb.append(pinyinArray[0].charAt(0));
                }
            }
        }
        return sb.toString();
    }

}
