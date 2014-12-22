<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Libre-Panopticlick</title>
	<script type="text/javascript" src="jquery-2.1.3.min.js"></script>
	<script type="text/javascript" src="PluginDetect.js"></script>
	<script type="text/javascript" src="fingerprint.js"></script>
	<script type="text/javascript">
	window.onload = function(){
		$("#PluginDetails").attr("value", getPluginDetails());
		$("#TimeZone").attr("value", getTimeZone());
		$("#ScreenDetails").attr("value", getScreenDetails());
		document.forms["detailsForm"].submit();
	}
	</script>
</head>
<body>
	<p>
		Please wait...
	</p>
	<form id="detailsForm" action="Test?js_enabled=true" method="POST">
		<p>
			<input type="hidden" id="PluginDetails" name="PluginDetails" value="-">
			<input type="hidden" id="TimeZone" name="TimeZone" value="-">
			<input type="hidden" id="ScreenDetails" name="ScreenDetails" value="-">
			<input type="hidden" id="Fonts" name="Fonts" value="-">
			<input type="hidden" id="SuperCookie" name="SuperCookie" value="-">
		</p>
	</form>
</body>
</html>