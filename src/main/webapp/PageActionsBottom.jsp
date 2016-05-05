<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="templates.default"/>
<vwb:CheckRequestContext context='view|diff|edit|upload|info'>
<div id='actionsBottom' class="DCT_right_Amendment"> 
  <vwb:PageExists>  

    <vwb:CheckVersion mode="latest">
       <fmt:message key="info.lastmodified">
          <fmt:param><vwb:PageVersion/></fmt:param>
          <fmt:param><vwb:DiffLink version="latest" newVersion="previous"><vwb:PageDate/></vwb:DiffLink></fmt:param>
          <fmt:param><vwb:Author/></fmt:param>
       </fmt:message>
    </vwb:CheckVersion>

    <vwb:CheckVersion mode="notlatest">
      <fmt:message key="actions.publishedon">
         <fmt:param><vwb:PageDate/></fmt:param>
         <fmt:param><vwb:Author /></fmt:param>
      </fmt:message>
    </vwb:CheckVersion>
  </vwb:PageExists>

  <vwb:NoSuchPage><fmt:message key="actions.notcreated"/></vwb:NoSuchPage> 
</div>
</vwb:CheckRequestContext>