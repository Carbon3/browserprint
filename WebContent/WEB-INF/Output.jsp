<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="chrBean" class="beans.CharacteristicsBean" scope="request" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test</title>
</head>
<body>
	<a href="index.html">Back</a>
	<table>
		<tr>
			<th>Browser Characteristic</th>
			<th>bits of identifying information</th>
			<th>one in <i>x</i> browsers have this value</th>
			<th>value</th>
		</tr>
		<c:forEach var="chr" items="${ chrBean.characteristics }"><tr>
		<% java.text.DecimalFormat df = new java.text.DecimalFormat("#.##"); %>
			<td>${ chr.name }</td>
			<td><fmt:formatNumber value="${ chr.bits }" maxFractionDigits="2"/></td>
			<td><fmt:formatNumber value="${ chr.inX }" maxFractionDigits="2"/></td>
			<td>${ chr.value }</td>
		</tr>
		</c:forEach>
	</table>
</body>
</html>