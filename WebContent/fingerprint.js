function getPlatform(){
	return window.navigator.platform;
}

function getPlatformFlash(flash){
	return flash.getOS();
}

function getPluginDetails() {
	var plugins = "";

	/*
	 * This method said to not be supported by IE.
	 */
	for (var i = 0; i < navigator.plugins.length; ++i) {
		var plugin = navigator.plugins[i];
		plugins += "Plugin " + i + ": " + plugin.name + "; "
				+ plugin.description + "; " + plugin.filename + ";";

		for (var j = 0; j < plugin.length; ++j) {
			plugins += " (" + plugin[j].description + "; " + plugin[j].type
					+ "; " + plugin[j].suffixes + ")";
		}
		plugins += ". ";
	}

	if (plugins == "") {
		/*
		 * Try the method that works with IE. Uses an MIT licensed script,
		 * PluginDetect.
		 */
		var plugin_names = [ "Java", "QuickTime", "DevalVR", "Shockwave",
				"Flash", "WindowsMediaplayer", "Silverlight", "VLC" ];
		for (var i = 0; i < plugin_names.length; ++i) {
			var version = PluginDetect.getVersion(plugin_names[i]);
			if (version) {
				plugins += plugin_names[i] + " " + version + "; ";
			}
		}
	}
	
	if (plugins == ""){
		plugins = "No plugins detected";
	}

	return plugins;
}

function getScreenDetails() {
	return screen.width + "x" + screen.height + "x" + screen.colorDepth;
}

function getScreenDetailsFlash(flash){
	return flash.getResolution().join("x");
}

function getFonts(flash){
	return flash.getFonts();//.join().replace(/[&\/\\#,+()$~%.'":*?<>{}]/g, '_');
}

function getTimeZone() {
	return new Date().getTimezoneOffset();
}

function getSuperCookie() {
	var test = "";

	test += "DOM localStorage: ";
	try {
		if ('localStorage' in window && window['localStorage'] !== null) {
			test += "Yes";
		} else {
			test += "No";
		}
	} catch (ex) {
		test += "No";
	}
	test += ", ";

	test += "DOM sessionStorage: ";
	try{
		if ('sessionStorage' in window && window['sessionStorage'] !== null) {
			test += "Yes";
		} else {
			test += "No";
		}
	} catch (ex) {
		test += "No";
	}
	test += ", ";
	

	test += "IE userData: ";
	var persistDiv = $('<div id="tmpDiv" style="behavior:url(#default#userdata)"></div>');
	persistDiv.appendTo(document.body);
	try {
		tmpDiv.setAttribute("remember", "original value");
		tmpDiv.save("oXMLStore");
		tmpDiv.setAttribute("remember", "overwritten");
		tmpDiv.load("oXMLStore");
		if ((tmpDiv.getAttribute("remember")) == "original value") {
			test += "Yes";
		} else {
			test += "No";
		}
	} catch (ex) {
		test += "No";
	}

	return test;
}

function getTime(){
	var time = new Date().getTime();
	return time;
}

function getDateTime(){
	try{
		var d = new Date(0);
		return d.toLocaleString();	
	}catch(ex){
		return "Error";
	}
}

function getMathTan(){
	return Math.tan(-1e300);
}

function getAdsBlocked(){
	if($('#ad').height() == 0){
		//Ads are blocked.
		return 1;
	} else {
		//Ads are not blocked.
		return 0;
	}
}

function getCanvas(){
	return canvasData;
}

function getWebGL(){
	return webGLData;
}

function getWebGLVendor(){
	return webGLVendor;
}

function getWebGLRenderer(){
	return webGLRenderer;
}

function getLanguageFlash(flash){
	return flash.getLanguage();
}