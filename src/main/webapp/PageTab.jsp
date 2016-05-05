<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="net.duckling.ddl.common.*" %>
<%@ page import="net.duckling.ddl.service.resource.Resource" %>
<fmt:setBundle basename="templates.default"/>

<vwb:PageExists>
<%
	VWBContext c = VWBContext.getContext( request );
	Resource p = c.getResource();
	int resourceid = p.getRid();
%>

<%-- If the page is an older version, then offer a note and a possibility
     to restore this version as the latest one. --%>
<vwb:CheckVersion mode="notlatest">
  <form action="<vwb:LinkTo format='url'/>" 
        method="get"  accept-charset='UTF-8'>

    <div class="warning">
      <fmt:message key="view.oldversion">
        <fmt:param>
          <%--<wiki:PageVersion/>--%>
          <select id="version" name="version" onchange="this.form.submit();" >
<%
   int latestVersion = VWBContext.getResource( resourceid, "DPage" ).getLastVersion();
   int thisVersion = p.getLastVersion();

   if( thisVersion == VWBContext.LATEST_VERSION ) thisVersion = latestVersion; //should not happen
     for( int i = 1; i <= latestVersion; i++) 
     {
%> 
          <option value="<%= i %>" <%= ((i==thisVersion) ? "selected='selected'" : "") %> ><%= i %></option>
<%
     }    
%>
          </select>
        </fmt:param>
      </fmt:message>  
      <br />
      <vwb:LinkTo><fmt:message key="view.backtocurrent"/></vwb:LinkTo>&nbsp;&nbsp;
      <vwb:EditLink version="this"><fmt:message key="view.restore"/></vwb:EditLink>
    </div>

  </form>
</vwb:CheckVersion>

<%-- Inserts no text if there is no page. --%>
<vwb:render content="${RenderContext.content}"/>
</vwb:PageExists>
<vwb:NoSuchPage>
  <%-- FIXME: Should also note when a wrong version has been fetched. --%>
  <div class="information" >
  <fmt:message key="common.nopage">
    <fmt:param><vwb:EditLink><fmt:message key="common.createit"/></vwb:EditLink></fmt:param>
  </fmt:message>
  </div>
</vwb:NoSuchPage>
