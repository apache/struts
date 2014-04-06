<%@ taglib prefix="s" uri="/struts-tags" %>
<%@include file="partialChunkHeader.jsp"%>
<ul>
<s:iterator value="category.children">
    <li>
        <s:if test="children.size() > 0">
            <sx:a href="toggle.action?catId=%{id}">+</sx:a>
        </s:if>
        <s:property value="name"/>
    </li>
    <s:if test="toggle">
        <s:set name="display" value="'none'"/>
    </s:if>
    <s:else>
        <s:set name="display" value="''"/>
    </s:else>                                                                                     ›

    <sx:div id="children_%{id}"
            cssStyle="display: %{display}"
            href="getCategory.action?catId=%{id}"
            refreshListenTopic="children_%{id}"/>
</s:iterator>
</ul>