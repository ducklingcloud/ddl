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
package net.duckling.ddl.service.diff.daisy;

/***************
 * by liuboyun LCS算法-求两个字符串的最大公共子串 功能一：求两个int数组的lcs 功能二：求两个char数组的lcs
 ****************/

public class LCS {

    private int deep;

    int[][] lcsLength(int[] x, int[] y) {
        int m = x.length;
        int n = y.length;
        int i, j;
        int[][] c = new int[m][n];
        int[][] b = new int[m][n];
        for (i = 1; i < m; i++) {
            c[i][0] = 0;
        }
        for (j = 0; j < n; j++) {
            c[0][j] = 0;
        }
        for (i = 1; i < m; i++) {
            for (j = 1; j < n; j++) {
                if (x[i] == y[j] && x[i] != -1) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                    b[i][j] = 1;
                } else if (c[i - 1][j] >= c[i][j - 1]) {
                    c[i][j] = c[i - 1][j];
                    b[i][j] = 2;
                } else {
                    c[i][j] = c[i][j - 1];
                    b[i][j] = 3;
                }
            }
        }
        return b;
    }

    void printLCS(int[][] b, int[] x, int i, int j, int LCSNodeId[]) {
        if (i == 0 || j == 0)
            return;
        if (b[i][j] == 1) {
            printLCS(b, x, i - 1, j - 1, LCSNodeId);
            LCSNodeId[deep] = x[i];
            deep++;

        } else if (b[i][j] == 2) {
            printLCS(b, x, i - 1, j, LCSNodeId);
        } else {
            printLCS(b, x, i, j - 1, LCSNodeId);
        }
    }

    public int GetLCSNode(int[] a, int[] b, int LCSNodeId[]) {
        deep = 0;
        int[] x = new int[a.length + 1];
        for (int i = 1; i < x.length; i++) {
            x[i] = a[i - 1];
        }
        int[] y = new int[b.length + 1];
        for (int i = 1; i < y.length; i++) {
            y[i] = b[i - 1];
        }
        printLCS(lcsLength(x, y), x, x.length - 1, y.length - 1, LCSNodeId);
        return deep;
    }

    // /////////////////////////////////

    int[][] lcsTextLength(char[] x, char[] y) {
        int m = x.length;
        int n = y.length;
        int i, j;
        int[][] c = new int[m][n];
        int[][] b = new int[m][n];
        for (i = 1; i < m; i++) {
            c[i][0] = 0;
        }
        for (j = 0; j < n; j++) {
            c[0][j] = 0;
        }
        for (i = 1; i < m; i++) {
            for (j = 1; j < n; j++) {
                if (x[i] == y[j]) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                    b[i][j] = 1;
                } else if (c[i - 1][j] >= c[i][j - 1]) {
                    c[i][j] = c[i - 1][j];
                    b[i][j] = 2;
                } else {
                    c[i][j] = c[i][j - 1];
                    b[i][j] = 3;
                }
            }
        }
        return b;
    }

    void printTextLCS(int[][] b, char[] x, int i, int j, char LCSNodeId[]) {
        if (i == 0 || j == 0) {
            return;
        }
        if (b[i][j] == 1) {
            printTextLCS(b, x, i - 1, j - 1, LCSNodeId);
            LCSNodeId[deep] = x[i];
            deep++;

        } else if (b[i][j] == 2) {
            printTextLCS(b, x, i - 1, j, LCSNodeId);
        } else {
            printTextLCS(b, x, i, j - 1, LCSNodeId);
        }
    }

    public int GetLCSText(char[] a, char[] b, char LCSNodeId[]) {
        deep = 0;
        char[] x = new char[a.length + 1];
        for (int i = 1; i < x.length; i++) {
            x[i] = a[i - 1];
        }
        char[] y = new char[b.length + 1];
        for (int i = 1; i < y.length; i++) {
            y[i] = b[i - 1];
        }
        printTextLCS(lcsTextLength(x, y), x, x.length - 1, y.length - 1, LCSNodeId);
        return deep;
    }

    public static String GetLCSString(String oldS, String newS) {
        LCS tmp = new LCS();
        tmp.deep = 0;
        char[] out = new char[newS.length()];
        tmp.GetLCSText(oldS.toCharArray(), newS.toCharArray(), out);
        char[] out2 = new char[tmp.deep];
        for (int i = 0; i < tmp.deep; i++) {
            out2[i] = out[i];
        }
        return new String(out2);
    }
}