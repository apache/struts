<%@taglib prefix="s" uri="/struts-tags" %>
<div>
    <p>This example illustrates the Struts/Portlet/Tiles Plugin.</p>

    <h4>Features</h4>
    <ul>
        <li>
            <s:url id="freemarker" namespace="/tiles" action="processTilesFreemarkerExample" method="input" />
    		<s:a href="%{freemarker}">View FreeMarker Example</s:a>
        </li>
    </ul>

</div>