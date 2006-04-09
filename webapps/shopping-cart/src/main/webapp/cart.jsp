<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%@ taglib prefix="saf" uri="/struts-action" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<p class="boxTitle">Shopping Cart</p>

<div id="cartTable">
    <div><span class="cartLabel">Number of Items:</span><span class="cartValue"><saf:property value="numCartItems"/></span></div>
    <div><span class="cartLabel">Total:</span><span class="cartValue"> $<saf:property value="cartTotal" /></span></div>
</div>
