<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="jquery-2.1.3.min.js" type="text/javascript"></script>
<script type="text/javascript" src="fingerprint.js"></script>
<script type="text/javascript">
	window.onload = function(){
		$("#ScreenDetails").attr("value", getScreenDetails());
		document.forms["detailsForm"].submit();
	}
</script>
</head>
<body>
	Please wait...
	<form id="detailsForm" action="Test?js_enabled=true" method="POST">
		<input type="hidden" id="PluginDetails" name="PluginDetails" value="1" />
		<input type="hidden" id="TimeZone" name="TimeZone" value="2" />
		<input type="hidden" id="ScreenDetails" name="ScreenDetails" value="3" />
		<input type="hidden" id="Fonts" name="Fonts" value="4" />
		<input type="hidden" id="SuperCookie" name="SuperCookie" value="5" />
	</form>
</body>
</html>