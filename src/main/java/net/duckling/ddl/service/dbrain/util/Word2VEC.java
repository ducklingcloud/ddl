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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;


public class Word2VEC {

    private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();

    private int words;
    private int size;
    private int topNSize = 40;

    /**
     * 加载模型
     *
     * @param path 模型的路径
     * @throws IOException
     */
    public void loadModel(String path) throws IOException
    {
        double len = 0;
        float vector = 0;
        try (BufferedInputStream bis =
             new BufferedInputStream(new FileInputStream(path));
             DataInputStream dis = new DataInputStream(bis)) {
            //读取词数
            words = Integer.parseInt(readString(dis));
            size = Integer.parseInt(readString(dis));

            String word;
            float[] vectors = null;
            for (int i = 0; i < words; i++) {
                word = readString(dis);
                vectors = new float[size];
                len = 0;
                for (int j = 0; j < size; j++) {
                    vector = readFloat(dis);
                    len += vector * vector;
                    vectors[j] = (float) vector;
                }
                len = Math.sqrt(len);

                for (int j = 0; j < vectors.length; j++) {
                    vectors[j] = (float) (vectors[j] / len);
                }
                wordMap.put(word, vectors);
                dis.read();
            }
        }
    }

    public void unloadModel(){
        wordMap = null;
        wordMap = new HashMap<String, float[]>();
    }

    private static final int MAX_SIZE = 50;

    /**
     * 得到近义词
     *
     * @param word
     * @return
     */
    public Set<WordEntry> distance(String word)
    {
        float[] wordVector = getWordVector(word);
        if (wordVector == null)
        {
            return null;
        }
        Set<Entry<String, float[]>> entrySet = wordMap.entrySet();
        float[] tempVector = null;
        List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
        String name = null;
        for (Entry<String, float[]> entry : entrySet) {
            name = entry.getKey();
            if (name.equals(word)) {
                continue;
            }
            float dist = 0;
            tempVector = entry.getValue();
            for (int i = 0; i < wordVector.length; i++) {
                dist += wordVector[i] * tempVector[i];
            }
            insertTopN(name, dist, wordEntrys);
        }
        return new TreeSet<WordEntry>(wordEntrys);
    }

    /**
     * 计算两个词向量的相似度（内积）
     * @param word1
     * @param word2
     * @return  Similarity
     */
    public float similarity(String word1, String word2){
        float[] wordVector1 = getWordVector(word1);
        float[] wordVector2 = getWordVector(word2);

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
     * 词与词进行类比  (A+B-a=b)
     *
     * @return
     */
    public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
        float[] wv0 = getWordVector(word0);
        float[] wv1 = getWordVector(word1);
        float[] wv2 = getWordVector(word2);

        if (wv1 == null || wv2 == null || wv0 == null) {
            return null;
        }
        float[] wordVector = new float[size];
        for (int i = 0; i < size; i++) {
            wordVector[i] = wv1[i] - wv0[i] + wv2[i];
        }
        float[] tempVector;
        String name;
        List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
        for (Entry<String, float[]> entry : wordMap.entrySet()) {
            name = entry.getKey();
            if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
                continue;
            }
            float dist = 0;
            tempVector = entry.getValue();
            for (int i = 0; i < wordVector.length; i++) {
                dist += wordVector[i] * tempVector[i];
            }
            insertTopN(name, dist, wordEntrys);
        }
        return new TreeSet<WordEntry>(wordEntrys);
    }

    private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
        if (wordsEntrys.size() < topNSize) {
            wordsEntrys.add(new WordEntry(name, score));
            return;
        }
        float min = Float.MAX_VALUE;
        int minOffe = 0;
        for (int i = 0; i < topNSize; i++) {
            WordEntry wordEntry = wordsEntrys.get(i);
            if (min > wordEntry.score) {
                min = wordEntry.score;
                minOffe = i;
            }
        }

        if (score > min) {
            wordsEntrys.set(minOffe, new WordEntry(name, score));
        }
    }


    public class WordEntry implements Comparable<WordEntry> {
        public String name;
        public float score;

        public WordEntry(String name, float score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public String toString() {
            return this.name + "\t" + score;
        }

        @Override
        public int compareTo(WordEntry o) {
            if (this.score > o.score) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    /**
     * 得到词向量
     *
     * @param word
     * @return
     */
    public float[] getWordVector(String word) {
        return wordMap.get(word);
    }

    public static float readFloat(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return getFloat(bytes);
    }

    /**
     * 读取一个float
     *
     * @param b
     * @return
     */
    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    /**
     * 读取一个字符串
     *
     * @param dis
     * @return
     * @throws IOException
     */
    private static String readString(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[MAX_SIZE];
        byte b = dis.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != 32 && b != 10) {
            i++;
            bytes[i] = b;
            b = dis.readByte();
            if (i == 49) {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1,"UTF-8"));
        return sb.toString();
    }

    public int getTopNSize() {
        return topNSize;
    }

    public void setTopNSize(int topNSize) {
        this.topNSize = topNSize;
    }

    public HashMap<String, float[]> getWordMap() {
        return wordMap;
    }

    public int getWords() {
        return words;
    }

    public int getSize() {
        return size;
    }


    //  public static void main(String[] args) throws IOException {
    //      Word2VEC vec = new Word2VEC();
    //      vec.loadModel("D://newresult.bin");
    //
    //      //最近邻测试
    //      System.out.println("One word analysis：");
    //      Set<WordEntry> result = new TreeSet<WordEntry>();
    //      result = vec.distance("金庸");
    //      Iterator iter = result.iterator();
    //      while (iter.hasNext()) {
    //          WordEntry word = (WordEntry) iter.next();
    //          System.out.println(word.name + " " + word.score);
    //      }
    //
    //      //三词类比测试
    //      System.out.println("*******************************");
    //      System.out.println("Three word analysis：");
    //      result = vec.analogy( "牛顿", "万有引力定律","爱因斯坦");
    //      iter = result.iterator();
    //      while (iter.hasNext())
    //      {
    //          WordEntry word = (WordEntry) iter.next();
    //          System.out.println(word.name + " " + word.score);
    //      }
    //  }
}
