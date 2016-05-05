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
package net.duckling.ddl.service.export.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.bundle.BundleItem;
import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.export.ExportService;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.render.RenderingService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TagItem;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.vlabs.clb.api.AccessForbidden;
import cn.vlabs.clb.api.CLBException;
import cn.vlabs.clb.api.ResourceNotFound;

/**
 * 重要变量释义： id2Title, 类型：Map；内容：已下载页面的路径和标题； key: 已下载页面的存储路径；value：页面标题 allPages,
 * 类型：List；内容：存放所有页面的存储路径，在写Epub时使用 downResPath, 类型：Map；内容：已下载资源ID及其存储路径；key:
 * rid_tid_itemType；value：存储路径
 * 
 * @author Yangxp
 * 
 */

@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IBundleService bundleService;
    @Autowired
    private RenderingService renderingService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    

    protected static final Logger LOG = Logger.getLogger(ExportServiceImpl.class);
    private static final String EPUB_XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private static final String COVER = "/cover.jpg";
    private static final String CATALOG_TEM = "/catalog.template";
    private static final String HTML_TEM = "/html.template";
    private static final String AONE_CSS = "/aone.css";
    private static final String MIMETYPE = "/mimetype";
    private static final String META_INF = "/container.xml";
    private static final String TOC_NCX = "/toc.ncx";
    private static final String AONE_OPF = "/aone.opf";
    private static final String DEFAULT_TAG = "tag";
    private static final String FAKE_PAGE_TITLE = "404：页面未找到";
    private Map<String, String> downResPath = new HashMap<String, String>();
    @Value("${ddl.root}WEB-INF/templates/epub")
    private String epubPath;

    public String getEpubPath() {
        return epubPath;
    }

    public void setEpubPath(String epubPath) {
        this.epubPath = epubPath;
    }


    /*
     * ---------------------------- 服务入口
     * ------------------------------------------------
     */
    @Override
    public void download(VWBContext context, String tname, int[] rids, HttpServletResponse resp, String format) {
        downResPath.clear();
        if (format.equalsIgnoreCase("zip")) {
            writeZip2Client(context, tname, rids, resp);
        } else {
            writeEpub2Client(context, tname, rids, resp);
        }
    }

    @Override
    public void download(VWBContext context, String tname, Map<String, List<Tag>> tagMap, HttpServletResponse resp,
            String format) {
        downResPath.clear();
        if (format.equalsIgnoreCase("zip")) {
            writeZip2Client(context, tname, tagMap, resp);
        } else {
            writeEpub2Client(context, tname, tagMap, resp);
        }
    }

    /*
     * -------------------------------- Zip下载相关方法
     * -----------------------------------------
     */

    private void writeZip2Client(VWBContext context, String tname, int[] rids, HttpServletResponse resp) {
        Team team = teamService.getTeamByName(tname);
        setResponseHeader(tname + ".zip", resp);
        ArchiveOutputStream out = getArchiveOutputStream(resp);
        writeCss(tname, out);
        Map<String, String> id2Title = new HashMap<String, String>();
        // 下载资源内容
        writeForResource(tname, team.getId(), rids, context, out, id2Title, null, false);
        // 写目录
        writeCatalog(tname, id2Title, out);
        writeOverview(DEFAULT_TAG, tname, rids, out, context, false);
        try {
            out.close();
        } catch (IOException e) {
            LOG.error("关闭连接错误", e);
        }
    }

    private void writeZip2Client(VWBContext context, String tname, Map<String, List<Tag>> tagMap,
            HttpServletResponse resp) {
        Team team = teamService.getTeamByName(tname);
        setResponseHeader(tname + ".zip", resp);
        ArchiveOutputStream out = getArchiveOutputStream(resp);
        writeCss(tname, out);
        Map<String, String> id2Title = new HashMap<String, String>();
        writeForTag(tname, tagMap, team.getId(), context, out, id2Title, null, false);
        writeCatalog(tname, id2Title, out);
        writeOverview(tname, tagMap, out, context, false);
        try {
            out.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeCatalog(String tname, Map<String, String> id2Title, ArchiveOutputStream out) {
        writeAllPagesFile(tname, id2Title, out);
        writeOverviewSummary(tname, out);
        writeIndexFile(tname, out);
    }

    private void writeAllPagesFile(String tname, Map<String, String> id2Title, ArchiveOutputStream out) {
        String htmlHeader = getTemplate(epubPath + HTML_TEM);
        htmlHeader = htmlHeader.replace("TITLE", "所有页面");
        htmlHeader = htmlHeader.replace("../aone.css", "./aone.css");
        StringBuilder sb = new StringBuilder();
        sb.append(htmlHeader);
        sb.append("<body><h1>所有页面</h1><ul>");
        for (Entry<String, String> entry:id2Title.entrySet()){
        	sb.append("<li><a target=\"pageFrame\" href=\"").append(entry.getKey()).append("\">").append(entry.getValue())
            .append("</a></li>");
        }
        sb.append("</ul></body>");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        try {
            out.putArchiveEntry(new ZipArchiveEntry(tname + "/allPages.html"));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeOverviewSummary(String tname, ArchiveOutputStream out) {
        String htmlHeader = getTemplate(epubPath + HTML_TEM);
        htmlHeader = htmlHeader.replace("TITLE", tname + " overview");
        StringBuilder sb = new StringBuilder();
        sb.append(htmlHeader);
        sb.append("<body><h1>" + tname + "</h1></body>");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        try {
            out.putArchiveEntry(new ZipArchiveEntry(tname + "/overviewSummary.html"));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeIndexFile(String tname, ArchiveOutputStream out) {
        String html = getTemplate(epubPath + CATALOG_TEM);
        html = html.replaceAll("TEAMNAME", tname);
        InputStream in = new ByteArrayInputStream(html.getBytes());
        try {
            out.putArchiveEntry(new ZipArchiveEntry(tname + "/index.html"));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /*
     * -------------------------------- Epub下载相关方法
     * -----------------------------------------
     */

    private void writeEpub2Client(VWBContext context, String tname, int[] rids, HttpServletResponse resp) {
        Team team = teamService.getTeamByName(tname);
        setResponseHeader(tname + ".epub", resp);
        ArchiveOutputStream out = getArchiveOutputStream(resp);
        writeCss(tname, out);
        Map<String, String> id2Title = new HashMap<String, String>();
        List<String> allPages = new ArrayList<String>();
        writeForResource(tname, team.getId(), rids, context, out, id2Title, allPages, true);
        writeOverview(DEFAULT_TAG, tname, rids, out, context, true);
        writeEpubFiles(tname, allPages, out, id2Title);
        try {
            out.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeEpub2Client(VWBContext context, String tname, Map<String, List<Tag>> tagMap,
            HttpServletResponse resp) {
        Team team = teamService.getTeamByName(tname);
        setResponseHeader(tname + ".epub", resp);
        ArchiveOutputStream out = getArchiveOutputStream(resp);
        writeCss(tname, out);
        Map<String, String> id2Title = new HashMap<String, String>();
        List<String> allPages = new ArrayList<String>();
        writeForTag(tname, tagMap, team.getId(), context, out, id2Title, allPages, true);
        writeOverview(tname, tagMap, out, context, true);
        writeEpubFiles(tname, allPages, out, id2Title);
        try {
            out.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeEpubFiles(String tname, List<String> allPages, ArchiveOutputStream out,
            Map<String, String> id2Title) {
        writeMimetype(out);
        writeMetaInfo(tname, out);
        writeCover(tname, out);
        writeOpf(tname, allPages, out);
        writeNcx(tname, allPages, id2Title, out);
    }

    private void writeMimetype(ArchiveOutputStream out) {
        String mimeType = getTemplate(epubPath + MIMETYPE);
        InputStream in = new ByteArrayInputStream(mimeType.getBytes());
        try {
            out.putArchiveEntry(new ZipArchiveEntry("mimetype"));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeMetaInfo(String tname, ArchiveOutputStream out) {
        String metaTemplate = getTemplate(epubPath + META_INF);
        if (null != tname) {
            metaTemplate = metaTemplate.replace("TEAMNAME", "" + tname);
        } else {
            metaTemplate = metaTemplate.replace("TEAMNAME/", "");
        }
        InputStream in = new ByteArrayInputStream(metaTemplate.getBytes());
        try {
            out.putArchiveEntry(new ZipArchiveEntry("META-INF/container.xml"));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeCover(String path, ArchiveOutputStream out) {
        try {
            FileInputStream fis = new FileInputStream(epubPath + COVER);
            String entry = null != path ? path + "/cover.jpg" : "cover.jpg";
            out.putArchiveEntry(new ZipArchiveEntry(entry));
            IOUtils.copy(fis, out);
            fis.close();
            out.closeArchiveEntry();
        } catch (FileNotFoundException e) {
            LOG.error(COVER + "文件没找到");
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeOpf(String tname, List<String> allPages, ArchiveOutputStream out) {
        String pofTemplate = getTemplate(epubPath + AONE_OPF);
        StringBuilder sbItems = new StringBuilder();
        StringBuilder sbItemrefs = new StringBuilder();
        sbItems.append("<item id=\"css\" href=\"aone.css\" media-type=\"text/css\" />\n");
        sbItems.append("<item id=\"cover\" href=\"cover.jpg\" media-type=\"image/jpeg\" />\n");
        if (null != tname) {
            sbItems.append("<item id=\"index\" href=\"overview.html\" media-type=\"application/xhtml+xml\" />\n");
            sbItemrefs.append("<itemref idref=\"index\" />\n");
        }
        Iterator<String> it = allPages.iterator();
        while (it.hasNext()) {
            String str = it.next();
            if (str.contains("/index.html")) {
                String tagname = getIDFromResPath(str);
                String path = str.substring(str.indexOf("/") + 1, str.length());
                sbItems.append("<item id=\"" + tagname + "\" href=\"" + path
                        + "\" media-type=\"application/xhtml+xml\" />\n");
                sbItemrefs.append("<itemref idref=\"" + tagname + "\" />\n");
            } else {
                String pid = getIDFromResPath(str);
                sbItems.append("<item id=\"" + pid + "\" href=\"" + str
                        + "\" media-type=\"application/xhtml+xml\" />\n");
                sbItemrefs.append("<itemref idref=\"" + pid + "\" />\n");
            }
        }
        sbItems.append("<item id=\"ncx\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\" />");
        pofTemplate = pofTemplate.replace("ITEMS", sbItems.toString());
        pofTemplate = pofTemplate.replace("ITEMREFS", sbItemrefs.toString());
        InputStream in = new ByteArrayInputStream(pofTemplate.getBytes());
        try {
            String entry = null != tname ? tname + "/aone.opf" : "aone.opf";
            out.putArchiveEntry(new ZipArchiveEntry(entry));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeNcx(String tname, List<String> allPages, Map<String, String> id2Title, ArchiveOutputStream out) {
        String ncxTemplate = getTemplate(epubPath + TOC_NCX);
        if (null != tname) {
            ncxTemplate = ncxTemplate.replace("DOCTITLE", "" + tname);
            ncxTemplate = ncxTemplate.replace("DOCAUTHOR", "" + tname);
        }
        StringBuilder sb = new StringBuilder();
        int playOrder = 0;
        if (null != tname) {
            sb.append("<navPoint id=\"nv-catalog\" playOrder=\"" + (++playOrder) + "\">\n"
                    + "<navLabel><text>目录</text></navLabel>\n<content src=\"overview.html\"/>\n</navPoint>\n");
        }
        int len = allPages.size();
        for (int par = 0; par < len; par++) {
            String str = allPages.get(par);
            if (str.contains("/index.html")) {
                String tagName = getTagNameFromResPath(str);
                String path = str.substring(str.indexOf("/") + 1, str.length());
                playOrder++;
                sb.append("<navPoint id=\"nv-" + playOrder + "\" playOrder=\"" + playOrder + "\">\n<navLabel><text>"
                        + tagName + "</text></navLabel>\n<content src=\"" + path + "\"/>\n");
                for (int child = par + 1; child < len; child++) {
                    String item = allPages.get(child);
                    if (item.contains("index")) {
                        break;
                    }
                    String pageTitle = id2Title.get(item);
                    playOrder++;
                    sb.append("<navPoint id=\"nv-" + playOrder + "\" playOrder=\"" + playOrder
                            + "\">\n<navLabel><text>" + pageTitle + "</text></navLabel>\n<content src=\"" + item
                            + "\"/>\n</navPoint>\n");
                }
                sb.append("</navPoint>\n");
            }
        }
        ncxTemplate = ncxTemplate.replace("NAV_POINTS", sb.toString());
        InputStream in = new ByteArrayInputStream(ncxTemplate.getBytes());
        try {
            String entry = null != tname ? tname + "/toc.ncx" : "toc.ncx";
            out.putArchiveEntry(new ZipArchiveEntry(entry));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /*
     * -------------------------------- 公共方法
     * -----------------------------------------
     */

    private void setResponseHeader(String filename, HttpServletResponse resp) {
        try {
            filename = new String(filename.getBytes("utf8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
        }
        resp.setContentType("application/zip");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    }

    private ArchiveOutputStream getArchiveOutputStream(HttpServletResponse resp) {
        ArchiveOutputStream aos = null;
        try {
            aos = new ArchiveStreamFactory()
                    .createArchiveOutputStream(ArchiveStreamFactory.ZIP, resp.getOutputStream());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (ArchiveException e) {
            LOG.error(e.getMessage(), e);
        }
        return aos;
    }

    private void writeOverview(String tag, String tname, int[] rids, ArchiveOutputStream out, VWBContext context,
            boolean isEpub) {
        String htmlHeader = getTemplate(epubPath + HTML_TEM);
        htmlHeader = htmlHeader.replace("TITLE", "" + tname);
        htmlHeader = htmlHeader.replaceAll("[\\.]{2}", "\\.");

        StringBuilder sb = new StringBuilder();
        if (isEpub) {
            sb.append(EPUB_XML_HEADER);
            sb.append(htmlHeader);
            sb.append("<body><h1>目录</h1><ul>");
            for (int rid : rids) {
                writeOverviewItemByResource(context, rid, sb, tag, tname, true);
            }
        } else {
            sb.append(htmlHeader);
            sb.append("<body><h1>目录</h1><ul>");
            for (int rid : rids) {
                writeOverviewItemByResource(context, rid, sb, tag, tname, false);
            }
        }
        sb.append("</ul></body></html>");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        try {
            out.putArchiveEntry(new ZipArchiveEntry(tname + "/overview.html"));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeOverview(String tname, Map<String, List<Tag>> tagMap, ArchiveOutputStream out,
            VWBContext context, boolean isEpub) {
        String htmlHeader = getTemplate(epubPath + HTML_TEM);
        htmlHeader = htmlHeader.replace("TITLE", "" + tname);
        htmlHeader = htmlHeader.replaceAll("[\\.]{2}", "\\.");

        StringBuilder sb = new StringBuilder();
        if (isEpub) {
            sb.append(EPUB_XML_HEADER);
            sb.append(htmlHeader);
            sb.append("<body><h1>目录</h1><ul>");
            writeOverviewItemByTag(context, tagMap, sb, true);
        } else {
            sb.append(htmlHeader);
            sb.append("<body><h1>目录</h1><ul>");
            writeOverviewItemByTag(context, tagMap, sb, false);
        }
        sb.append("</ul></body></html>");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        try {
            out.putArchiveEntry(new ZipArchiveEntry(tname + "/overview.html"));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void writeOverviewItemByResource(VWBContext context, int rid, StringBuilder sb, String tag, String tname,
            boolean isEpub) {
        IResourceService rs = resourceService;
        Resource res = rs.getResource(rid);
        if (null == res) {
            return;
        }
        if (LynxConstants.TYPE_PAGE.equals(res.getItemType())) {
            String element = "<li><a";
            element = isEpub ? element : (element + " target=\"pageFrame\"");
            element = element + " href=\"" + tag + "/" + rid + ".html\"" + ">" + res.getTitle() + "</a></li>";
            sb.append(element);
        } else if (LynxConstants.TYPE_FILE.equals(res.getItemType())) {
            String resKey = res.getRid() + "_" + res.getTid() + "_" + res.getItemType();
            String element = "<li><a";
            element = isEpub ? element : (element + " target=\"pageFrame\"");
            element = element + " href=\"" + getResTagPath(resKey) + "\">" + res.getTitle() + "</a></li>";
            sb.append(element);
        } else {
            sb.append("<li><ul><p class=\"listTitle\">" + res.getTitle() + "</p>");
            List<BundleItem> items = bundleService.getBundleItems(rid);
            List<Long> itemsIds = new ArrayList<Long>();
            if (null != items && items.size() > 0) {
                for (BundleItem item : items) {
                    itemsIds.add((long) item.getRid());
                }
            }
            List<Resource> resList = rs.getResourcesBySphinxID(itemsIds);
            if (null != resList && resList.size() > 0) {
                for (Resource resource : resList) {
                    writeOverviewItemByResource(context, resource.getRid(), sb, tag, tname, isEpub);
                }
            }
            sb.append("</ul></li>");
        }
    }

    private void writeOverviewItemByTag(VWBContext context, Map<String, List<Tag>> tagMap, StringBuilder sb,
            boolean isEpub) {
        if (null != tagMap && !tagMap.isEmpty()) {
            for (Map.Entry<String, List<Tag>> entry : tagMap.entrySet()) {
                sb.append("<li><ul><p  class=\"listTitle\">" + entry.getKey() + "</p>");
                List<Tag> tags = entry.getValue();
                if (null != tags && !tags.isEmpty()) {
                    for (Tag tag : tags) {
                        String target = isEpub ? " " : " target=\"pageListFrame\" ";
                        sb.append("<li><a" + target + "href=\"" + tag.getTitle() + "_" + tag.getId() + "/index.html\">"
                                + tag.getTitle() + "</a></li>");
                    }
                }
                sb.append("</ul></li>");
            }
        }
    }

    private String getTemplate(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String str = br.readLine();
            while (str != null) {
                sb.append(str + "\n");
                str = br.readLine();
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            LOG.error(filename + "文件没找到");
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return "";
    }

    private void writeCss(String tname, ArchiveOutputStream out) {
        String css = getTemplate(epubPath + AONE_CSS);
        InputStream in = new ByteArrayInputStream(css.getBytes());
        String entry = tname != null ? tname + "/aone.css" : "aone.css";
        try {
            out.putArchiveEntry(new ZipArchiveEntry(entry));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private String getHtmlHeader(Resource page, boolean isEpub) {
        StringBuilder sb = new StringBuilder();
        if (isEpub) {
            sb.append(EPUB_XML_HEADER);
        }
        String htmlHeader = getTemplate(epubPath + HTML_TEM);
        htmlHeader = htmlHeader.replace("TITLE", page.getTitle());
        sb.append(htmlHeader);
        sb.append("<body>");
        return sb.toString();
    }

    private String getHtmlFooter() {
        return "</body></html>";
    }

    private String getProcessedHtml(String html, VWBContext context, String path, ArchiveOutputStream out,
            Map<String, String> id2Title, List<String> allPages, boolean isEpub) {
        String s = processFileOrPageLink(html, LynxConstants.TYPE_FILE, context, path, out, id2Title, allPages, isEpub);
        String ss = processImageLink(s, context, path, out, id2Title, allPages, isEpub);
        String sss = processFileOrPageLink(ss, LynxConstants.TYPE_PAGE, context, path, out, id2Title, allPages, isEpub);
        return sss;
    }

    private String processImageLink(String html, VWBContext context, String path, ArchiveOutputStream out,
            Map<String, String> id2Title, List<String> allPages, boolean isEpub) {
        String regex = "[0-9a-zA-Z\\-\\/]+/download/([0-9]+)(\\?func=cache){0,1}";
        Pattern p = Pattern.compile(regex);
        String[] cells = p.split(html);
        Matcher m = p.matcher(html);
        StringBuilder sb = new StringBuilder();
        sb.append(cells[0]);
        int index = 1;
        while (m.find()) {
            int imageId = Integer.parseInt(m.group(1));
            String resKey = imageId + "_" + VWBContext.getCurrentTid() + "_" + LynxConstants.TYPE_FILE;
            String tagname = path.substring(0, path.lastIndexOf("/"));
            String resPath = this.getRelativeResPath(resKey, tagname);
            if (null == resPath) {// 页面内的图片尚未下载
                writeAttFile(path, VWBContext.getCurrentTid(), imageId, context, out);
                resPath = getResNoTagPath(resKey);
                resPath = (null == resPath) ? "#" : resPath;
            }
            sb.append(resPath);
            sb.append(cells[index++]);
        }
        if (index < cells.length) {
            sb.append(cells[index]);
        }
        return sb.toString();
    }

    private String processFileOrPageLink(String html, String type, VWBContext context, String path,
            ArchiveOutputStream out, Map<String, String> id2Title, List<String> allPages, boolean isEpub) {
        String regex;
        if (LynxConstants.TYPE_FILE.equals(type)) {
            regex = "/file/([0-9]+)";
        } else {
            regex = "/page/([0-9]+)";
        }
        Pattern p = Pattern.compile(regex);
        String[] cells = p.split(html);
        if (cells.length == 1) {
            return html;
        }
        for (int i = 0; i < cells.length; i++) {
            int indexHref = cells[i].lastIndexOf("href=\"");
            if (i < cells.length - 1 && indexHref > 0) {
                cells[i] = cells[i].substring(0, indexHref);
            }
            int indexQuote = cells[i].indexOf('"');
            if (i > 0) {
                cells[i] = cells[i].substring(indexQuote + 1);
            }
        }
        Matcher m = p.matcher(html);
        StringBuilder sb = new StringBuilder();
        sb.append(cells[0]);
        int index = 1;
        while (m.find()) {
            int attId = Integer.parseInt(m.group(1));
            String resKey = attId + "_" + VWBContext.getCurrentTid() + "_" + type;
            String tagname = path.substring(0, path.lastIndexOf("/"));
            String resPath = getRelativeResPath(resKey, tagname);
            if (null == resPath) {// 页面内的文件后页面尚未下载
                if (regex.contains("file")) {
                    writeAttFile(path, VWBContext.getCurrentTid(), attId, context, out);
                } else {
                    Resource res = resourceService.getResource(attId, context.getTid());
                    if (null != res) {
                        List<FileVersion> attFiles = fileVersionService.getFilesOfPage(res.getRid(),
                                VWBContext.getCurrentTid());
                        for (FileVersion file : attFiles) {
                            writeAttFile(path, VWBContext.getCurrentTid(), file.getRid(), context, out);
                        }
                        writePage(path, res.getRid(), context, out, id2Title, allPages, isEpub);
                    }
                }
                resPath = getResNoTagPath(resKey);
                resPath = (null == resPath) ? "#" : resPath;
            }
            sb.append("href=\"" + resPath + "\"");
            sb.append(cells[index++]);
        }
        return sb.toString();
    }

    // 获取downResPath中resKey所对应的存放地址，并解析成为tag目录以后的路径名
    private String getResNoTagPath(String resKey) {
        if (downResPath.containsKey(resKey)) {
            String fullPath = downResPath.get(resKey);
            String tagPath = fullPath.substring(fullPath.indexOf("/") + 1, fullPath.length());
            String noTagPath = tagPath.substring(tagPath.indexOf("/") + 1, tagPath.length());
            return noTagPath;
        } else
            return null;
    }

    // 获取downResPath中resKey所对应的存放地址，并解析成为第一级目录为tag的目录
    private String getResTagPath(String resKey) {
        if (downResPath.containsKey(resKey)) {
            String fullPath = downResPath.get(resKey);
            String tagPath = fullPath.substring(fullPath.indexOf("/") + 1, fullPath.length());
            return tagPath;
        } else {
            return null;
        }
    }

    // 获取downResPath中resKey所对应的存放地址，并将该地址解析成为可以被tagName目录引用的地址类型
    private String getRelativeResPath(String resKey, String tagName) {
        if (downResPath.containsKey(resKey)) {
            String fullPath = downResPath.get(resKey);
            String tagPath = fullPath.substring(fullPath.indexOf("/") + 1, fullPath.length());
            String tag = tagPath.substring(0, tagPath.indexOf("/"));
            if (tagName.equals(tag)) {
                return tagPath.substring(tagPath.indexOf("/") + 1, tagPath.length());
            } else {
                return "../" + tagPath;
            }
        } else {
            return null;
        }
    }

    // 将resPath转换成ID，此ID在生成Epub的opf文件时使用
    private String getIDFromResPath(String resPath) {
        String temp = resPath.substring(0, resPath.lastIndexOf("."));
        temp = temp.replace("/", "_");
        return temp;
    }

    // 从resPath中获取tag名称
    private String getTagNameFromResPath(String resPath) {
        String subStr = resPath.substring(0, resPath.lastIndexOf("/"));
        return subStr.substring(subStr.lastIndexOf("/") + 1, subStr.length());
    }

    // 根据文件名title和resKey获取文件的实际存储文件名
    private String getRestoreFileName(String title, String resKey) {
        int dotIndex = title.lastIndexOf(".");
        dotIndex = (dotIndex <= 0) ? title.length() : dotIndex;
        String newFilename = title.substring(0, dotIndex) + "_" + resKey + title.substring(dotIndex, title.length());
        return newFilename;
    }

    /* --------------------------- 资源读写的相关方法 --------------------------------- */

    /*--------------------------------   写资源的相关方法   ----------------------------------------*/
    // 文档页导出时调用的主方法
    private void writeForResource(String parent, int tid, int[] rids, VWBContext context, ArchiveOutputStream out,
            Map<String, String> id2Title, List<String> allPages, boolean isEpub) {
        String path = parent + "/" + DEFAULT_TAG;
        int size = rids.length;
        for (int i = 0; i < size; i++) {
            writeResource(path, tid, rids[i], context, out, id2Title, allPages, isEpub);
        }
    }

    // 团队管理员导出文档时调用的主方法
    private void writeForTag(String parent, Map<String, List<Tag>> tagMap, int tid, VWBContext context,
            ArchiveOutputStream out, Map<String, String> id2Title, List<String> allPages, boolean isEpub) {
        ITagService ts = tagService;
        Team team = teamService.getTeamByID(tid);
        if (null == tagMap || tagMap.isEmpty()) {
            return;
        }
        Map<String, List<Integer>> resMap = new HashMap<String, List<Integer>>();
        for (Map.Entry<String, List<Tag>> entry : tagMap.entrySet()) {
            List<Tag> tagList = entry.getValue();
            if (null != tagList && tagList.size() > 0) {
                for (Tag tag : tagList) {
                    String path = parent + "/" + tag.getTitle() + "_" + tag.getId();
                    List<TagItem> items = ts.getTagItems(tag.getId());
                    if (null != items && items.size() > 0) {
                        List<Integer> rids = new ArrayList<Integer>();
                        for (TagItem item : items) {
                            rids.add(item.getRid());
                        }
                        resMap.put(path, rids);
                    }
                }
            }
        }
        for (Map.Entry<String, List<Integer>> entry : resMap.entrySet()) {
            String path = entry.getKey();
            if (allPages != null) {
                allPages.add(path + "/index.html");
            }
            List<Integer> rids = entry.getValue();
            for (Integer rid : rids) {
                writeResource(path, tid, rid, context, out, id2Title, allPages, isEpub);
            }
            String title = path.substring(path.lastIndexOf("/") + 1, path.length());
            writeTagIndex(path, title, rids, team.getName(), isEpub, out, context);
        }
    }

    // 写单个资源
    private void writeResource(String parent, int tid, int rid, VWBContext context, ArchiveOutputStream out,
            Map<String, String> id2Title, List<String> allPages, boolean isEpub) {
        Resource res = resourceService.getResource(rid);
        if (null == res) {
            LOG.error("Resource doesn't exist! rid=" + rid + " and tid=" + tid);
            return;
        }
        String attPath = parent + "/attachfiles";

        if (LynxConstants.TYPE_PAGE.equals(res.getItemType())) {
            List<FileVersion> attFiles = fileVersionService.getFilesOfPage(res.getRid(), tid);
            for (FileVersion file : attFiles) {
                writeAttFile(attPath, tid, file.getRid(), context, out);
            }
            writePage(parent, rid, context, out, id2Title, allPages, isEpub);
        } else if (LynxConstants.TYPE_FILE.equals(res.getItemType())) {
            writeAttFile(attPath, tid, res.getRid(), context, out);
        } else {
            writeBundle(parent, tid, res, context, out, id2Title, allPages, isEpub);
        }
    }

    private void writePage(String path, int resourceId, VWBContext context, ArchiveOutputStream out,
            Map<String, String> id2Title, List<String> allPages, boolean isEpub) {
        Resource page =  resourceService.getResource(resourceId);
        StringBuilder sbuf = new StringBuilder();
        String pagePath = path + "/" + resourceId + ".html";
        if (null != page) {
            String resKey = "" + page.getRid() + "_" + page.getTid() + "_" + LynxConstants.TYPE_PAGE;
            if (null != getResTagPath(resKey)) { // 检查页面是否已下载
                return;
            }
            downResPath.put(resKey, pagePath);
            String html = renderingService.getHTML(context, page);
            // site.getHTML(context, page);
            sbuf.append(getHtmlHeader(page, isEpub));
            sbuf.append("<h1 class=\"title\">" + page.getTitle() + "</h1>");
            sbuf.append(getProcessedHtml(html, context, path, out, id2Title, allPages, isEpub));// 处理页面中的文件、图片、页面链接
            sbuf.append(getHtmlFooter());
        } else {
            sbuf.append(getPageNotFoundHtml(resourceId, isEpub));
        }
        InputStream in = new ByteArrayInputStream(sbuf.toString().getBytes());
        try {
            out.putArchiveEntry(new ZipArchiveEntry(pagePath));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        String tagname = path.substring(path.indexOf('/') + 1);
        String title = (null == page) ? FAKE_PAGE_TITLE : page.getTitle();
        id2Title.put(tagname + "/" + resourceId + ".html", title);
        if (allPages != null) {
            allPages.add(tagname + "/" + resourceId + ".html");
        }
    }

    private String getPageNotFoundHtml(int resourceId, boolean isEpub) {
        StringBuilder result = new StringBuilder();
        Resource fakePage = new Resource();// 此对象仅为了此方法内使用。
        fakePage.setTitle(FAKE_PAGE_TITLE);
        result.append(getHtmlHeader(fakePage, isEpub));
        result.append("<h1 class=\"title\">ID为" + resourceId + "的页面未找到，可能已经被删除！</h1>");
        result.append(getHtmlFooter());
        return result.toString();
    }

    private void writeAttFile(String path, int tid, int rid, VWBContext context, ArchiveOutputStream out) {
        Resource res = resourceService.getResource(rid, tid);
        if (null == res) {
            LOG.error("File doesn't exist! rid=" + rid + " and tid=" + tid);
            return;
        }
        String resKey = res.getRid() + "_" + res.getTid() + "_" + LynxConstants.TYPE_FILE;
        if (null != getResTagPath(resKey)) {
            return;
        }
        String newFilename = getRestoreFileName(res.getTitle(), resKey);
        downResPath.put(resKey, path + "/" + newFilename);
        FileVersion file = fileVersionService.getLatestFileVersion(rid, tid);
        try {
//            file = fileService.getFile(rid, tid);
        } catch (net.duckling.ddl.service.export.FileNotFoundException e) {
            LOG.error("没有找到ID为" + rid + "的文档！");
            return;
        }
        ExportAttachSaver eas = new ExportAttachSaver(path, tid, rid, out);
        try {
        	resourceOperateService.getContent(file.getClbId(), "" + file.getClbVersion(), eas);
        } catch (AccessForbidden e) {
            LOG.warn("对ID为" + file.getClbId() + "文档的访问被拒绝！");
        } catch (ResourceNotFound e) {
            LOG.warn("没有找到ID为" + file.getClbId() + "的文档！");
        } catch (CLBException e) {
            LOG.warn("获取ID为" + file.getClbId() + "的文档时，返回CLBException错误！");
        }
    }

    private void writeBundle(String parent, int tid, Resource res, VWBContext context, ArchiveOutputStream out,
            Map<String, String> id2Title, List<String> allPages, boolean isEpub) {
        List<BundleItem> items = bundleService.getBundleItems(res.getRid());
        if (null != items && items.size() > 0) {
            for (BundleItem item : items) {
                writeResource(parent, tid, item.getRid(), context, out, id2Title, allPages, isEpub);
            }
        }
    }

    // 生成单个标签的目录页
    private void writeTagIndex(String path, String tagname, List<Integer> rids, String tname, boolean isEpub,
            ArchiveOutputStream out, VWBContext context) {
        String html = getIndexHtml(tagname, rids, tname, isEpub, context);
        InputStream in = new ByteArrayInputStream(html.getBytes());
        try {
            out.putArchiveEntry(new ZipArchiveEntry(path + "/index.html"));
            IOUtils.copy(in, out);
            in.close();
            out.closeArchiveEntry();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private String getIndexHtml(String tagname, List<Integer> rids, String tname, boolean isEpub, VWBContext context) {
        String htmlHeader = getTemplate(epubPath + HTML_TEM);
        htmlHeader = htmlHeader.replace("TITLE", tagname);
        StringBuilder sb = new StringBuilder();
        if (isEpub) {
            sb.append(EPUB_XML_HEADER);
            sb.append(htmlHeader);
            sb.append("<body><h1>" + tagname.substring(0, tagname.lastIndexOf("_")) + "</h1><ul>");
            if (rids != null) {
                for (Integer rid : rids) {
                    writeTagIndexItemByResource(context, rid, sb, tagname, tname, true);
                }

            }
        } else {
            sb.append(htmlHeader);
            sb.append("<body><h1>" + tagname + "</h1><ul>");
            if (rids != null) {
                for (Integer rid : rids) {
                    writeTagIndexItemByResource(context, rid, sb, tagname, tname, false);
                }
            }
        }
        sb.append("</ul></body></html>");
        return sb.toString();
    }

    private void writeTagIndexItemByResource(VWBContext context, int rid, StringBuilder sb, String tag, String tname,
            boolean isEpub) {
        IResourceService rs = resourceService;
        Resource res = rs.getResource(rid);
        if (null == res) {
            LOG.error("Resource doesn't exist! rid=" + rid);
            return;
        }
        if (LynxConstants.TYPE_PAGE.equals(res.getItemType())) {
            String resKey = res.getRid() + "_" + res.getTid() + "_" + res.getItemType();
            String element = "<li><a";
            element = isEpub ? element : (element + " target=\"pageFrame\"");
            element = element + " href=\"" + getRelativeResPath(resKey, tag) + "\">" + res.getTitle() + "</a></li>";
            sb.append(element);
        } else if (LynxConstants.TYPE_FILE.equals(res.getItemType())) {
            String resKey = res.getRid() + "_" + res.getTid() + "_" + res.getItemType();
            String element = "<li><a";
            element = isEpub ? element : (element + " target=\"pageFrame\"");
            element = element + " href=\"" + getRelativeResPath(resKey, tag) + "\">" + res.getTitle() + "</a></li>";
            sb.append(element);
        } else {
            sb.append("<li><ul><p class=\"listTitle\">" + res.getTitle() + "</p>");
            List<BundleItem> items = bundleService.getBundleItems(rid);
            List<Long> itemsIds = new ArrayList<Long>();
            if (null != items && items.size() > 0) {
                for (BundleItem item : items) {
                    itemsIds.add((long) item.getRid());
                }
            }
            List<Resource> resList = rs.getResourcesBySphinxID(itemsIds);
            if (null != resList && resList.size() > 0) {
                for (Resource resource : resList) {
                    writeTagIndexItemByResource(context, resource.getRid(), sb, tag, tname, isEpub);
                }
            }
            sb.append("</ul></li>");
        }
    }
}
