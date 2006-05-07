<%@ taglib prefix="saf" uri="/struts-action" %>
<%@include file="partialChunkHeader.jsp"%>
<ul>
<saf:iterator value="category.children">
    <li>
        <saf:if test="children.size() > 0">
            <saf:a theme="ajax" href="toggle.action?catId=%{id}">+</saf:a>
        </saf:if>
        <saf:property value="name"/>
    </li>
    <saf:if test="toggle">
        <saf:set name="display" value="'none'"/>
    </saf:if>
    <saf:else>
        <saf:set name="display" value="''"/>
    </saf:else>

    <saf:div theme="ajax"
            id="children_%{id}"
            cssStyle="display: %{display}"
            href="getCategory.action?catId=%{id}"
            listenTopics="children_%{id}"/>
</saf:iterator>
</ul>