<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%@ taglib prefix="ww" uri="/webwork" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<ww:if test="category != null">
    <h3><ww:if test="category.parent != null"><ww:property value="category.parent.name"/> &gt; </ww:if><ww:property value="category.name"/></h3>

    <ww:set name="categoryProducts" value="%{catalog.findProductsByCategory(category)}"/>
    <ww:if test="(#categoryProducts != null) && (#categoryProducts.size > 0)">
        <div>
            <ww:iterator value="#categoryProducts">
                <ww:form id="qtyForm_%{id}" name="qtyForm_%{id}" namespace="/catalog/remote" action="updateQuantity" method="POST" theme="ajax" validate="true">
                    <ww:hidden name="productId" value="%{id}"/>
                    <div class="product">
                        <div class="productDetails">
                            <div class="productHeader">
                                <tr>
                                    <td><div class="productName"><ww:property value="name"/></div></td>
                                    <td><div class="productPrice">$<ww:property value="price" /></div></td>
                                </tr>
                            </div>
                            <tr><td colspan="2">
                            <div class="productDescription"><ww:property value="description"/></div>
                            </td></tr>
                        </div>
                        <tr><td colspan="2">
                        <p class="productQuantity">
                            Quantity:&nbsp;<ww:textfield id="quantity" name="quantity" theme="simple" size="2" value="0"/>
                            <ww:submit id="qtySubmit" name="qtySubmit" value="Update" theme="ajax" notifyTopics="cartUpdated" onLoadJS="document.qtyForm_%{id}.reset();" />
                        </p>
                        </td></tr>
                    </div>
                </ww:form>
            </ww:iterator>
        </div>
    </ww:if>
    <ww:else>
        <b>There are no products in this category</b>
    </ww:else>
</ww:if>
<ww:else>
    <h2>WebWork Ajax Catalog</h2>

    <p>Please choose a category to start shopping.</p>
</ww:else>
