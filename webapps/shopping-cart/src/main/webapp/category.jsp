<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%@ taglib prefix="saf" uri="/struts-action" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<saf:if test="category != null">
    <h3><saf:if test="category.parent != null"><saf:property value="category.parent.name"/> &gt; </saf:if><saf:property value="category.name"/></h3>

    <saf:set name="categoryProducts" value="%{catalog.findProductsByCategory(category)}"/>
    <saf:if test="(#categoryProducts != null) && (#categoryProducts.size > 0)">
        <div>
            <saf:iterator value="#categoryProducts">
                <saf:form id="qtyForm_%{id}" name="qtyForm_%{id}" namespace="/catalog/remote" action="updateQuantity" method="POST" theme="ajax" validate="true">
                    <saf:hidden name="productId" value="%{id}"/>
                    <div class="product">
                        <div class="productDetails">
                            <div class="productHeader">
                                <tr>
                                    <td><div class="productName"><saf:property value="name"/></div></td>
                                    <td><div class="productPrice">$<saf:property value="price" /></div></td>
                                </tr>
                            </div>
                            <tr><td colspan="2">
                            <div class="productDescription"><saf:property value="description"/></div>
                            </td></tr>
                        </div>
                        <tr><td colspan="2">
                        <p class="productQuantity">
                            Quantity:&nbsp;<saf:textfield id="quantity" name="quantity" theme="simple" size="2" value="0"/>
                            <saf:submit id="qtySubmit" name="qtySubmit" value="Update" theme="ajax" notifyTopics="cartUpdated" onLoadJS="document.qtyForm_%{id}.reset();" />
                        </p>
                        </td></tr>
                    </div>
                </saf:form>
            </saf:iterator>
        </div>
    </saf:if>
    <saf:else>
        <b>There are no products in this category</b>
    </saf:else>
</saf:if>
<saf:else>
    <h2>WebWork Ajax Catalog</h2>

    <p>Please choose a category to start shopping.</p>
</saf:else>
