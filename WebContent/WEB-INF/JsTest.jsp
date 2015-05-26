<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Libre-Panopticlick</title>
	<script type="text/javascript" src="jquery-1.11.2.min.js"></script>
	<script type="text/javascript" src="PluginDetect.js"></script>
	<script type="text/javascript" src="canvas.js"></script>
	<script type="text/javascript" src="three.min.js"></script>
	<script type="text/javascript" src="webGL.js"></script>
	<script type="text/javascript" src="fingerprint.js"></script>
	<script type="text/javascript">
	window.onload = function(){
		var flash = document.getElementById("OSData");
		
		//Platform
		$('<input>').attr({
    		type: 'hidden',
    		id: 'Platform',
    		name: 'Platform',
    		value: getPlatform()
		}).appendTo('#formdiv')
		
		//PlatformFlash
		try{
			$('<input>').attr({
	    		type: 'hidden',
	    		id: 'PlatformFlash',
	    		name: 'PlatformFlash',
	    		value: getPlatformFlash(flash)
			}).appendTo('#formdiv');
		}catch(e){
		}
		
		//PluginDetails
		$('<input>').attr({
    		type: 'hidden',
    		id: 'PluginDetails',
    		name: 'PluginDetails',
    		value: getPluginDetails()
		}).appendTo('#formdiv');
		
		//TimeZone
		$('<input>').attr({
    		type: 'hidden',
    		id: 'TimeZone',
    		name: 'TimeZone',
    		value: getTimeZone()
		}).appendTo('#formdiv');

		//ScreenDetails
		$('<input>').attr({
    		type: 'hidden',
    		id: 'ScreenDetails',
    		name: 'ScreenDetails',
    		value: getScreenDetails()
		}).appendTo('#formdiv');
		
		//ScreenDetailsFlash
		try{
			$('<input>').attr({
	    		type: 'hidden',
	    		id: 'ScreenDetailsFlash',
	    		name: 'ScreenDetailsFlash',
	    		value: getScreenDetailsFlash(flash)
			}).appendTo('#formdiv');
		}catch(e){
		}
		
		//LanguageFlash
		try{
			$('<input>').attr({
	    		type: 'hidden',
	    		id: 'LanguageFlash',
	    		name: 'LanguageFlash',
	    		value: getLanguageFlash(flash)
			}).appendTo('#formdiv');
		}catch(e){
		}
		
		//Fonts
		try{
			$('<input>').attr({
	    		type: 'hidden',
	    		id: 'Fonts',
	    		name: 'Fonts',
	    		value: getFonts(flash)
			}).appendTo('#formdiv');
		}catch(e){
		}
		
		//SuperCookie
		$('<input>').attr({
    		type: 'hidden',
    		id: 'SuperCookie',
    		name: 'SuperCookie',
    		value: getSuperCookie()
		}).appendTo('#formdiv');
		
		//DateTime
		$('<input>').attr({
    		type: 'hidden',
    		id: 'DateTime',
    		name: 'DateTime',
    		value: getDateTime()
		}).appendTo('#formdiv');
		
		//MathTan
		$('<input>').attr({
    		type: 'hidden',
    		id: 'MathTan',
    		name: 'MathTan',
    		value: getMathTan()
		}).appendTo('#formdiv');
		
		//AdsBlocked
		$('<input>').attr({
    		type: 'hidden',
    		id: 'AdsBlocked',
    		name: 'AdsBlocked',
    		value: getAdsBlocked()
		}).appendTo('#formdiv');
		
		//Canvas
		$('<input>').attr({
    		type: 'hidden',
    		id: 'Canvas',
    		name: 'Canvas',
    		value: getCanvas()
		}).appendTo('#formdiv');
		
		//WebGL
		$('<input>').attr({
    		type: 'hidden',
    		id: 'WebGL',
    		name: 'WebGL',
    		value: getWebGL()
		}).appendTo('#formdiv');
		
		//WebGLVendor
		$('<input>').attr({
    		type: 'hidden',
    		id: 'WebGLVendor',
    		name: 'WebGLVendor',
    		value: getWebGLVendor()
		}).appendTo('#formdiv');
		
		//WebGLVendor
		$('<input>').attr({
    		type: 'hidden',
    		id: 'WebGLRenderer',
    		name: 'WebGLRenderer',
    		value: getWebGLRenderer()
		}).appendTo('#formdiv');
		
		//Submit the page.
		submitDetailsForm();
	}
	
	function submitDetailsForm(){
		//Time, for clock skew test.
		//We put this here to avoid the timer altering the time by however many seconds.
		$('<input>').attr({
    		type: 'hidden',
    		id: 'Time',
    		name: 'Time',
    		value: getTime()
		}).appendTo('#formdiv');
		
		$('#detailsForm').submit();
	}
	</script>
</head>
<body>
	<p>
		Please wait...
	</p>
	<form id="detailsForm" action="Test?js_enabled=true" method="POST">
		<div id="formdiv">
		</div>
	</form>
	<!-- Flash for detecting fonts. -->
	<div>
		<embed id="OSData" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" src="OSData.swf">
	</div>
	<!-- Part of the ad blocking test. -->
	<script type="text/javascript">
        google_ad_height = 90;
    </script> 
	<div id="ad" style="display:none;">
		<script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js"></script>
	</div>
</body>
</html>