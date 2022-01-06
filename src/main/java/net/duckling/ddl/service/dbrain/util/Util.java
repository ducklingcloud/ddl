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
package net.duckling.ddl.service.dbrain.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class Util
{

    /**
     * 文档向量化
     * @param text
     * @param word2vec
     * @param documentVector
     * @return
     */
    public static float[] doc2vec(String text, Word2VEC word2vec )
    {
        List<String> usertext = wordcut(text);
        float [] vector1 = new float[200];
        for(String word: usertext)
        {
            float [] vector2 = word2vec.getWordVector(word);
            if(vector2==null)
            {
                continue;
            }
            for(int i=0; i < 200;i++)       //所有词向量相加合并
            {
                vector1[i] = vector1[i]+vector2[i];
            }
        }
        return vector1;
    }




    /**
     * 计算两个向量相似度
     * @param wordVector1
     * @param wordVector2
     * @return
     */
    public static float vectorSimilarity(float[] wordVector1, float[] wordVector2)
    {
        if (wordVector1 == null||wordVector2==null)
        {
            return -1;
        }
        float sim= 0;
        for(int i = 0; i < wordVector1.length; i++)
        {
            sim += wordVector1[i] * wordVector2[i];
        }
        return sim;
    }


    /**
     * 分词
     * @param text
     * @return
     */
    public static List<String> wordcut(String text)
    {
        List<String> result = new ArrayList<String>();
        try
        {
            IKSegmenter seg = new IKSegmenter(new StringReader(text), true);
            Lexeme l = null;

            while ((l = seg.next()) != null)
            {

                if (l.getLength() == 1 || l.getLexemeText().contains("."))
                {
                    continue;
                }
                else
                {
                    result.add(l.getLexemeText());
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;

    }


    /**
     * 字符串数组转浮点数组
     * @param str
     * @return
     */
    public static float[] string2float(String [] str)
    {

        float array [] = new float[str.length];
        for(int i=0;i<str.length;i++)
        {
            array[i]=Float.parseFloat(str[i]);
        }
        return array;
    }


    public static double freq2distance(int freq1,int freq2)
    {
        double temp1 = Math.sqrt((freq1+1)*(freq2+1));
        double temp2 = 1/(1+Math.log10(temp1));
        return temp2;

    }





    public static void main(String[] args)
    {
        System.out.println(Util.freq2distance(56,56 ));
    }


}
