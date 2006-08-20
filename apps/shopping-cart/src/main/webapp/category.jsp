<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<s:if test="category != null">
    <h3><s:if test="category.parent != null"><s:property value="category.parent.name"/> &gt; </s:if><s:property value="category.name"/></h3>

    <s:set name="categoryProducts" value="%{catalog.findProductsByCategory(category)}"/>
    <s:if test="(#categoryProducts != null) && (#categoryProducts.size > 0)">
        <div>
            <s:iterator value="#categoryProducts">
                <s:form id="qtyForm_%{id}" name="qtyForm_%{id}" namespace="/catalog/remote" action="updateQuantity" method="POST" theme="ajax" validate="true">
                    <s:hidden name="productId" value="%{id}"/>
                    <div class="product">
                        <div class="productDetails">
                            <div class="productHeader">
                                <tr>
                                    <td><div class="productName"><s:property value="name"/></div></td>
                                    <td><div class="productPrice">$<s:property value="price" /></div></td>
                                </tr>
                            </div>
                            <tr><td colspan="2">
                            <div class="productDescription"><s:property value="description"/></div>
                            </td></tr>
                        </div>
                        <tr><td colspan="2">
                        <p class="productQuantity">
                            Quantity:&nbsp;<s:textfield id="quantity" name="quantity" theme="simple" size="2" value="0"/>
                            <s:submit id="qtySubmit" name="qtySubmit" value="Update" theme="ajax" notifyTopics="cartUpdated" onLoadJS="document.qtyForm_%{id}.reset();" />
                        </p>
                        </td></tr>
                    </div>
                </s:form>
            </s:iterator>
        </div>
    </s:if>
    <s:else>
        <b>There are no products in this category</b>
    </s:else>
</s:if>
<s:else>
    <h2>Struts Ajax Catalog</h2>

    <p>Please choose a category to start shopping.</p>
</s:else>
