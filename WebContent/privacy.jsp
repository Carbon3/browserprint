<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Libre-Panopticlick</title>
<script type="text/javascript" src="jquery-1.11.2.min.js"></script>
<script type="text/javascript">
	/*
	 * Script that adds js_enabled=true to the end of the test_link.
	 * This is how we know that Javascript is enabled.
	 */
	window.onload = function() {
		var test_link = $("#test_link");
		test_link.attr("href", test_link.attr("href") + "?js_enabled=true");
	}
</script>
<link type="text/css" href="style.css" rel="stylesheet">
</head>
<body>
<div>
	<h1>Libre-Panopticlick</h1>
</div>
<div id="content">
	<p>
		Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit.
	</p>
	<p>
		Information we collect: ...
	</p>
	<p>
		One cookie is set to expire when the browser closes, this is only to check whether cookies are enabled.
		Another cookie set to expire after 30 days, for the main purpose of preventing double counting of fingerprints.
	</p>
</div>
<%@include file="WEB-INF/footer.html" %>
</body>
</html>