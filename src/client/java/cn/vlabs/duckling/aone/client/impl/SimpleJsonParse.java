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
package cn.vlabs.duckling.aone.client.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class SimpleJsonParse {
    private int myIndex;
    private String mySource;
    public SimpleJsonParse(String s){
        mySource = s;
    }

    public Map<String,Object> parse() throws ParseException{
        Map<String,Object> result = new HashMap<String,Object>();
        if (nextClean() != '{') {
            throw syntaxError("A JsonObject must begin with '{'");
        }
        String key;
        char c;
        while(true){
            c = nextClean();
            switch (c) {
                case 0:
                    throw syntaxError("A JsonObject must end with '}'");
                case '}':
                    return result;
                default :
                    back();
                    key = nextValue().toString().trim();
            }
            if (nextClean() != ':') {
                throw syntaxError("Expected a ':' after a key");
            }
            result.put(key, nextValue());
            switch (nextClean()) {
                case ',':
                    if (nextClean() == '}') {
                        return result;
                    }
                    back();
                    break;
                case '}':
                    return result;
                default:
                    throw syntaxError("Expected a ',' or '}'");
            }
        }
    }

    public char nextClean() throws ParseException {
        while (true) {
            char c = next();
            if (c == '/') {
                switch (next()) {
                    case '/':
                        do {
                            c = next();
                        } while (c != '\n' && c != '\r' && c != 0);
                        break;
                    case '*':
                        while (true) {
                            c = next();
                            if (c == 0) {
                                throw syntaxError("Unclosed comment.");
                            }
                            if (c == '*') {
                                if (next() == '/') {
                                    break;
                                }
                                back();
                            }
                        }
                        break;
                    default:
                        back();
                        return '/';
                }
            } else if (c == 0 || c > ' ') {
                return c;
            }
        }
    }

    public void back() {
        if (myIndex > 0) {
            myIndex -= 1;
        }
    }

    private  Object nextValue() throws ParseException {
        char c = nextClean();
        String s;

        if (c == '"' || c == '\'') {
            return nextString(c);
        }
        if (c == '{') {
            back();
            throw syntaxError("Unterminated string {");
        }
        if (c == '[') {
            back();
            throw syntaxError("Unterminated string [");
        }
        StringBuffer sb = new StringBuffer();
        char b = c;
        while (c >= ' ' && c != ':' && c != ',' && c != ']' && c != '}' &&
               c != '/') {
            sb.append(c);
            c = next();
        }
        back();
        s = sb.toString().trim();
        if (s.equals("true")) {
            return Boolean.TRUE;
        }
        if (s.equals("false")) {
            return Boolean.FALSE;
        }
        if (s.equals("null")) {
            return "";
        }
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            try {
                return new Integer(s);
            } catch (Exception e) {
            }
            try {
                return new Double(s);
            } catch (Exception e) {
            }
        }
        if (s.equals("")) {
            throw syntaxError("Missing value.");
        }
        return s;
    }

    public String nextString(char quote) throws ParseException {
        char c;
        StringBuffer sb = new StringBuffer();
        while (true) {
            c = next();
            switch (c) {
                case 0:
                case 0x0A:
                case 0x0D:
                    throw syntaxError("Unterminated string");
                case '\\':
                    c = next();
                    switch (c) {
                        case 'b':
                            sb.append('\b');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 'u':
                            sb.append((char)Integer.parseInt(next(4), 16));
                            break;
                        case 'x' :
                            sb.append((char) Integer.parseInt(next(2), 16));
                            break;
                        default:
                            sb.append(c);
                    }
                    break;
                default:
                    if (c == quote) {
                        return sb.toString();
                    }
                    sb.append(c);
            }
        }
    }

    public String next(int n) throws ParseException {
        int i = myIndex;
        int j = i + n;
        if (j >= mySource.length()) {
            throw syntaxError("Substring bounds error");
        }
        myIndex += n;
        return mySource.substring(i, j);
    }


    private char next() {
        char c = more() ? mySource.charAt(myIndex) : 0;
        myIndex += 1;
        return c;
    }

    public boolean more() {
        return myIndex < mySource.length();
    }

    public ParseException syntaxError(String message) {
        return new ParseException(message + toString(), myIndex);
    }
}
