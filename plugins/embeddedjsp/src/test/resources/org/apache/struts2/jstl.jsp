<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="number" value="10"/>
<c:forEach begin="0" end="${number}">
    X
</c:forEach>
<c:if test="${number < 15}">
    Y
</c:if>