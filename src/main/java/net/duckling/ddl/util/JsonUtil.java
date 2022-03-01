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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import java.lang.reflect.Type;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.user.AoneUserService;

import org.apache.log4j.Logger;

// import org.json.simple.parser.JSONParser;
// import org.json.simple.parser.ParseException;
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author dylan 2010-5-13
 * @author kai.nan@icloud.com <2022-02-26 Sat>
 */
public final class JsonUtil {
    // private static ObjectMapper commonMapper = new ObjectMapper();
    // private static SimpleDateFormat sdf = new SimpleDateFormat(
    //     "yyyy-MM-dd HH:mm:ss");
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    private static final String ENCODING = "UTF-8";

    protected static final Logger LOG = Logger.getLogger(JsonUtil.class);

    // static {
    //     commonMapper.setDateFormat(sdf);
    // }

    public static String getJSONString(Object bean) {
        return gson.toJson(bean);
    }

    /**
     * 对象以json格式输出
     * @param response
     * @param obj
     */
    public static void write(HttpServletResponse response, Object obj){
        write(response, obj, null);
    }

    /**
     * 对象以json格式输出
     * @param response
     * @param obj
     * @param target mixin目标对象
     * @param mixinSource mixin过滤器
     */
    // public static void write(HttpServletResponse response, Object obj,
    //                          Class<?> target, Class<?> mixinSource) {
    //     Map<Class<?>, Class<?>> mixinMap = new HashMap<Class<?>, Class<?>>();
    //     mixinMap.put(target, mixinSource);
    //     write(response, obj, mixinMap);
    // }

    /**
     * 对象以json格式输出
     * @param response
     * @param obj
     * @param sourceMixins jackson mixin过滤器
     */
    public static void write(HttpServletResponse response, Object obj,
                             Map<Class<?>, Class<?>> sourceMixins) {
        response.setContentType("application/json");
        response.setCharacterEncoding(ENCODING);

        try (PrintWriter writer = response.getWriter()) {
            if(sourceMixins!=null){
                // commonMapper.setMixInAnnotations(sourceMixins);
                LOG.error(">>>>> Unimplemented!!! jackson Mixin");
            }
            gson.toJson(obj, writer);
        } catch (IOException e) {
            LOG.error("JsonUtil write json object IOException:"+
                      e.getMessage());
        }
    }

    /**
     * 输出jsonp
     * @param req
     * @param response
     * @param obj
     * @param target
     * @param mixinSource
     */
    public static void writeJSONP(HttpServletRequest req,
                                  HttpServletResponse response,
                                  Object obj,
                                  Class<?> target,
                                  Class<?> mixinSource) {
        Map<Class<?>, Class<?>> mixinMap = new HashMap<Class<?>, Class<?>>();
        mixinMap.put(target, mixinSource);
        writeJSONP(req, response, obj, mixinMap);
    }

    /**
     * 输出jsonp
     * @param response
     * @param obj
     * @param sourceMixins
     */
    public static void writeJSONP(HttpServletRequest req,
                                  HttpServletResponse response,
                                  Object obj,
                                  Map<Class<?>, Class<?>> sourceMixins) {
        String func = req.getParameter("jsoncallback");
        //是否为合法函数名
        if (func==null || !func.matches("^[A-Za-z0-9_]+$")) {
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding(ENCODING);
        try (PrintWriter writer = response.getWriter();
             StringWriter sw = new StringWriter()) {
            if (sourceMixins != null) {
                // commonMapper.setMixInAnnotations(sourceMixins);
                LOG.error(">>>>> Unimplemented!!! jackson Mixin");
            }
            sw.append(func + "(");
            gson.toJson(obj, sw);
            sw.append(")");
            sw.flush();
            writer.write(sw.getBuffer().toString());
        } catch (IOException e) {
            LOG.error("JsonUtil write json object IOException:"+
                      e.getMessage());
        }
    }

    /**
     * json反序列化成对象
     * @deprecated
     */
    public static <T> T readValue(String src, Type typeOfT) {
        return gson.fromJson(src, typeOfT);
    }

    /**
     * @deprecated
     * use {@link JsonUtil#writeJSONP(HttpServletResponse response,
     * Object obj, Map<Class<?>, Class<?>> sourceMixins)} instead.
     */
    public static void writeJSONP(HttpServletRequest req,
                                  HttpServletResponse resp,
                                  Object object) {
        String func = req.getParameter("jsoncallback");
        //是否为合法函数名
        if(func!=null && func.matches("^[A-Za-z0-9_]+$")){
            write(resp, func + "(" + object.toString() + ")");
        }
    }

    /**
     * @deprecated
     * use {@link JsonUtil#write(HttpServletResponse, Object)} instead.
     */
    // public static void writeJSONObject(HttpServletResponse resp,
    //                                    Object object) {
    //     responseWrite(resp, object.toString());
    // }

    /**
     * @deprecated
     * use {@link JsonUtil#write(HttpServletResponse, Object)} instead.
     */
    public static void writeResult(HttpServletResponse response,
                                   Object... params) {
        JsonObject result = new JsonObject();
        if (params != null && (params.length % 2 == 0)) {
            for (int i = 0; i < params.length / 2; i++) {
                result.add(params[2*i].toString(),
                           gson.toJsonTree(params[2*i + 1]));
            }
        }
        write(response, result);
    }

    public static JsonArray getJSONArrayFromListResource(List<Resource> list) {
        return gson.toJsonTree(list).getAsJsonArray();
    }

    /**
     * @deprecated
     */
    public static JsonArray getJSONArrayLite(List<Resource> list){
        JsonArray array = new JsonArray();
        if (list == null || list.size() < 1){
            return array;
        }
        for (Resource obj : list) {
            array.add(getJSONObjectLiteFromResource(obj));
        }
        return array;
    }

    /**
     * @deprecated
     */
    public static JsonObject getJSONObjectLiteFromResource(Resource r) {
        JsonObject o = new JsonObject();
        o.addProperty("rid", r.getRid());
        return o;
    }

    /**
     * @deprecated
     */
    // @SuppressWarnings("unchecked")
    // public static JsonObject getJSONObjectFromResource(Resource r){
    //     AoneUserService userServeice = DDLFacade.getBean(AoneUserService.class);
    //     JsonObject o = getJSONObject(r);
    //     o.put("creatorName", userServeice.getUserNameByID(r.getCreator()));
    //     o.put("lastEditorName", userServeice.getUserNameByID(r.getLastEditor()));
    //     o.put("bundle", r.isFolder());
    //     o.put("version", r.getLastVersion());
    //     if(LynxConstants.TYPE_PAGE.equals(r.getItemType())){
    //         o.put("title", r.getTitle()+".ddoc");
    //     }
    //     if(r.isFile()){
    //         FileVersionService fs = DDLFacade.getBean(FileVersionService.class);
    //         FileVersion fv = fs.getFileVersion(r.getRid(), r.getTid(), r.getLastVersion());
    //         if(fv!=null){
    //             o.put("length", fv.getSize());
    //         }
    //     }
    //     return o;
    // }

    public static JsonObject getJSONObject(Object bean) {
        return gson.toJsonTree(bean).getAsJsonObject();
    }

    /**
     * 对象转成json字符串
     * @deprecated
     * use {@link JsonUtil#getJSONString} instead.
     */
    // public static String toJson(Object obj){
    //     Gson gson = new Gson();
    //     return gson.toJson(obj);
    // }

    /**
     * @deprecated
     */
    public static JsonArray getJSONArrayFromList(List<?> list) {
        return gson.toJsonTree(list).getAsJsonArray();
    }

    /**
     * 用于4.0升级和历史移动版本兼容
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
    public static JsonArray getJSONArrayFromResourceList(List<Resource> list) {
        JsonArray array = new JsonArray();
        if (list == null || list.size() < 1){
            return array;
        }
        AoneUserService userServeice = DDLFacade.getBean(AoneUserService.class);
        for (Resource obj : list) {
            JsonObject o = (JsonObject)gson.toJsonTree(obj);
            if(obj.isPage()){
                o.addProperty("pid",obj.getRid());
            }else if(obj.isFile()){
                o.addProperty("fid", obj.getRid());
            }else if(obj.isFolder()){
                o.addProperty("itemType", "Bundle");
            }
            o.addProperty("itemId",obj.getRid());
            o.addProperty("creatorName", userServeice.getUserNameByID(obj.getCreator()));
            o.addProperty("lastEditorName", userServeice.getUserNameByID(obj.getLastEditor()));
            o.addProperty("bundle", obj.isFolder());
            array.add(o);
        }
        return array;
    }

    /**
     * @deprecated
     * use {@link JsonUtil#getJSONString(Object)} instead.
     */
    // public static String object2json(Object obj) {
    //     StringBuilder json = new StringBuilder();
    //     if (obj == null) {
    //         json.append("\"\"");
    //     } else if (obj instanceof String || obj instanceof Integer
    //                || obj instanceof Float || obj instanceof Boolean
    //                || obj instanceof Short || obj instanceof Double
    //                || obj instanceof Long || obj instanceof BigDecimal
    //                || obj instanceof BigInteger || obj instanceof Byte) {
    //         json.append("\"").append(string2json(obj.toString())).append("\"");
    //     } else if (obj instanceof Object[]) {
    //         json.append(array2json((Object[]) obj));
    //     } else if (obj instanceof List) {
    //         json.append(list2json((List<?>) obj));
    //     } else if (obj instanceof Map) {
    //         json.append(map2json((Map<?, ?>) obj));
    //     } else if (obj instanceof Set) {
    //         json.append(set2json((Set<?>) obj));
    //     } else {
    //         json.append(bean2json(obj));
    //     }
    //     return json.toString();
    // }

    /**
     * @deprecated
     * use {@link JsonUtil#getJSONString(Object)} instead.
     */
    // public static String bean2json(Object bean) {
    //     StringBuilder json = new StringBuilder();
    //     json.append("{");
    //     PropertyDescriptor[] props = null;
    //     try {
    //         props = Introspector.getBeanInfo(bean.getClass(), Object.class)
    //                 .getPropertyDescriptors();
    //     } catch (IntrospectionException e) {
    //     }
    //     if (props != null) {
    //         for (int i = 0; i < props.length; i++) {
    //             try {
    //                 String name = object2json(props[i].getName());
    //                 String value = object2json(props[i].getReadMethod().invoke(
    //                     bean));
    //                 json.append(name);
    //                 json.append(":");
    //                 json.append(value);
    //                 json.append(",");
    //             } catch (Exception e) {
    //             }
    //         }
    //         json.setCharAt(json.length() - 1, '}');
    //     } else {
    //         json.append("}");
    //     }
    //     return json.toString();
    // }

    /**
     * @deprecated
     * use {@link JsonUtil#getJSONString(Object)} instead.
     */
    // public static String list2json(List<?> list) {
    //     StringBuilder json = new StringBuilder();
    //     json.append("[");
    //     if (list != null && list.size() > 0) {
    //         for (Object obj : list) {
    //             json.append(object2json(obj));
    //             json.append(",");
    //         }
    //         json.setCharAt(json.length() - 1, ']');
    //     } else {
    //         json.append("]");
    //     }
    //     return json.toString();
    // }

    /**
     * @deprecated
     * use {@link JsonUtil#getJSONString(Object)} instead.
     */
    // public static String array2json(Object[] array) {
    //     StringBuilder json = new StringBuilder();
    //     json.append("[");
    //     if (array != null && array.length > 0) {
    //         for (Object obj : array) {
    //             json.append(object2json(obj));
    //             json.append(",");
    //         }
    //         json.setCharAt(json.length() - 1, ']');
    //     } else {
    //         json.append("]");
    //     }
    //     return json.toString();
    // }

    /**
     * @deprecated
     * use {@link JsonUtil#getJSONString(Object)} instead.
     */
    // public static String map2JSON(final Map<String, String> map) {
    //     StringBuilder buff = new StringBuilder();
    //     boolean first = true;
    //     buff.append("{");
    //     for (Map.Entry<String, String> entry : map.entrySet()) {
    //         if (!first) {
    //             buff.append("\n,\"" + entry.getKey() + "\":");
    //             buff.append("\"" + entry.getValue() + "\"");
    //         } else {
    //             buff.append("\n\"" + entry.getKey() + "\":");
    //             buff.append("\"" + entry.getValue() + "\"");
    //             first = false;
    //         }
    //     }
    //     buff.append("}");
    //     return buff.toString();
    // }

    /**
     * @deprecated
     * use {@link JsonUtil#getJSONString(Object)} instead.
     */
    // public static String map2json(Map<?, ?> map) {
    //     StringBuilder json = new StringBuilder();
    //     json.append("{");
    //     if (map != null && map.size() > 0) {
    //         for (Entry<?, ?> entry : map.entrySet()) {
    //             json.append(object2json(entry.getKey()));
    //             json.append(":");
    //             json.append(object2json(entry.getValue()));
    //             json.append(",");
    //         }
    //         json.setCharAt(json.length() - 1, '}');
    //     } else {
    //         json.append("}");
    //     }
    //     return json.toString();
    // }

    /**
     * @deprecated
     * use {@link JsonUtil#getJSONString(Object)} instead.
     */
    // public static String set2json(Set<?> set) {
    //     StringBuilder json = new StringBuilder();
    //     json.append("[");
    //     if (set != null && set.size() > 0) {
    //         for (Object obj : set) {
    //             json.append(object2json(obj));
    //             json.append(",");
    //         }
    //         json.setCharAt(json.length() - 1, ']');
    //     } else {
    //         json.append("]");
    //     }
    //     return json.toString();
    // }

    /**
     * @deprecated
     * use {@link JsonUtil#getJSONString(Object)} instead.
     */
    // public static String string2json(String s) {
    //     if (s == null) {
    //         return "";
    //     }
    //     StringBuilder sb = new StringBuilder();
    //     for (int i = 0; i < s.length(); i++) {
    //         char ch = s.charAt(i);
    //         switch (ch) {
    //             case '"':
    //                 sb.append("\\\"");
    //                 break;
    //             case '\\':
    //                 sb.append("\\\\");
    //                 break;
    //             case '\b':
    //                 sb.append("\\b");
    //                 break;
    //             case '\f':
    //                 sb.append("\\f");
    //                 break;
    //             case '\n':
    //                 sb.append("\\n");
    //                 break;
    //             case '\r':
    //                 sb.append("\\r");
    //                 break;
    //             case '\t':
    //                 sb.append("\\t");
    //                 break;
    //             case '/':
    //                 sb.append("\\/");
    //                 break;
    //             default:
    //                 if (ch >= '\u0000' && ch <= '\u001F') {
    //                     String ss = Integer.toHexString(ch);
    //                     sb.append("\\u");
    //                     for (int k = 0; k < 4 - ss.length(); k++) {
    //                         sb.append('0');
    //                     }
    //                     sb.append(ss.toUpperCase());
    //                 } else {
    //                     sb.append(ch);
    //                 }
    //         }
    //     }
    //     return sb.toString();
    // }

    /**
     * @deprecated
     */
    // private static void responseWrite(HttpServletResponse response, String str){
    //     PrintWriter writer = null;
    //     try {
    //         // 为了兼容IE系浏览器，特意设置成text/html格式
    //         response.setContentType("text/html");
    //         response.setCharacterEncoding(ENCODING);
    //         writer = response.getWriter();
    //         writer.write(str);
    //     } catch (IOException e) {
    //         LOG.error("JsonUtil write json object IOException:" + e.getMessage());
    //     } finally {
    //         if (writer != null) {
    //             writer.flush();
    //             writer.close();
    //         }
    //     }
    // }

    // private JsonUtil() {
    // }
}
