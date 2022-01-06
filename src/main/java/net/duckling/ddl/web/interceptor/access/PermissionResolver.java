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
package net.duckling.ddl.web.interceptor.access;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;

import org.springframework.core.annotation.AnnotationUtils;

class PermissionResolver {
    private static class ControllerPermissions {
        private String controller;

        private HashMap<String, Method> denyProcessor;

        private Method defaultDenyProcessor;

        private HashMap<String, RequirePermission> methodCache;

        private RequirePermission permission;

        public ControllerPermissions(Class clazz) {
            controller = clazz.getName();
            permission = AnnotationUtils.findAnnotation(clazz,
                                                        RequirePermission.class);
            methodCache = new HashMap<String, RequirePermission>();
            denyProcessor = new HashMap<String, Method>();
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                RequirePermission methodPermission = AnnotationUtils
                        .findAnnotation(m, RequirePermission.class);
                if (methodPermission != null) {
                    methodCache.put(m.getName(), methodPermission);
                }

                OnDeny denyAnnotation = AnnotationUtils.findAnnotation(m,
                                                                       OnDeny.class);
                if (denyAnnotation != null) {
                    for (String methodName : denyAnnotation.value()) {
                        if ("*".equals(methodName)){
                            this.defaultDenyProcessor=m;
                        }else{
                            denyProcessor.put(methodName, m);
                        }
                    }
                }
            }
        }

        public Method findDenyProcessor(String methodName) {
            Method m = denyProcessor.get(methodName);
            if (m==null && defaultDenyProcessor!=null){
                m=defaultDenyProcessor;
            }
            return m;
        }

        public RequirePermission findPermission(String methodName) {
            if (methodName != null) {
                RequirePermission methodPermission = methodCache
                        .get(methodName);
                if (methodPermission != null) {
                    return methodPermission;
                }
            }
            return permission;
        }

        public String getController() {
            return controller;
        }
    }

    private Hashtable<String, ControllerPermissions> controllers = new Hashtable<String, ControllerPermissions>();

    public Method findDenyProcessor(Object handler, String methodName) {
        ControllerPermissions helper = getHelper(handler);

        return helper.findDenyProcessor(methodName);
    }

    public RequirePermission findPermission(Object handler, String methodName) {
        ControllerPermissions helper =getHelper(handler);

        return helper.findPermission(methodName);
    }

    private ControllerPermissions getHelper(Object handler) {
        ControllerPermissions helper = controllers.get(handler.getClass()
                                                       .getName());
        if (helper == null) {
            helper = new ControllerPermissions(handler.getClass());
            controllers.put(helper.getController(), helper);
        }
        return helper;
    }
}
