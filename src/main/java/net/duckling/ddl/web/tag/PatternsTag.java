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

package net.duckling.ddl.web.tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;

/**
 * @date Jun 13, 2011
 * @author xiejj@cnic.cn
 */
public class PatternsTag extends VWBBaseTag {
    private static final long serialVersionUID = 1L;

    // currpage, params, patterns, reversePattern, absoluteTeambase,
    // relativeTeambase
    @Override
    public int doVWBStart() throws Exception {
        StringBuilder buff = new StringBuilder();
        buff.append("{");
        buff.append("params" + ":" + JsonUtil.getJSONString(getParams()) + ",\n");
        buff.append("patterns" + ":"
                    + JsonUtil.getJSONString(UrlPatterns.getInstance().getPatterns())
                    + ",\n");
        buff.append("reversePattern"
                    + ":"
                    + convertReversPattern(UrlPatterns.getInstance()
                                           .getReversPatterns()) + ",\n");
        if (vwbcontext.getResource() != null) {
            buff.append("currpage" + ":"
                        + Integer.toString(vwbcontext.getResource().getRid())
                        + ",\n");
        }
        if (vwbcontext.getSite() != null) {
            buff.append("absoluteTeambase:\""
                        + vwbcontext.getSite().getAbsoluteTeamBase() + "\",\n");
            buff.append("relativeTeambase:\""
                        + vwbcontext.getSite().getRelativeTeamBase() + "\"\n");
        } else {
            buff.append("absoluteTeambase:\"\",\n");
            buff.append("relativeTeambase:\"\"\n");
        }
        buff.append("}");
        pageContext.getOut().println(buff.toString());
        return EVAL_PAGE;
    }

    private String convertReversPattern(Map<String, String> reversPatterns) {
        StringBuilder buff = new StringBuilder("[");
        boolean first = true;
        for (Entry<String, String> entry : reversPatterns.entrySet()) {
            String type = entry.getKey();
            String javaPattern = entry.getValue();
            if (!first) {
                buff.append(",");
            } else {
                first = false;
            }
            buff.append("{type:'" + type + "', pattern:" + java2js(javaPattern)
                        + "}\n");
        }
        buff.append("]");
        return buff.toString();
    }

    private String java2js(String javaPattern) {
        String result = javaPattern.replaceAll("/", "\\\\/");
        result = result.replaceAll("\\{key\\}", "(\\\\w+)");
        return "/" + result + "/";
    }

    private HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        Site site = vwbcontext.getSite();
        if (site != null) {
            params.put("%t", site.getTeamContext());
            params.put("%U", site.getBaseURL());
            params.put("%p", site.getBasePath());
            if (useRelative(vwbcontext.getContainer())) {
                params.put("%u", site.getBasePath());
            } else {
                params.put("%u", site.getBaseURL());
            }
        } else {
            VWBContainer container = vwbcontext.getContainer();
            params.put("%t", "");
            params.put("%U", container.getBaseURL());
            params.put("%p", container.getBasePath());
            if (useRelative(container)) {
                params.put("%u", container.getBasePath());
            } else {
                params.put("%u", container.getBaseURL());
            }
        }
        return params;
    }

    private boolean useRelative(VWBContainer container) {
        if ("relative".equals(container
                              .getProperty(KeyConstants.PREF_REFER_STYLE))) {
            return true;
        } else {
            return false;
        }
    }
}
