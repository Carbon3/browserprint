function getPlatform() {
	return window.navigator.platform;
}

function getPlatformFlash(flash) {
	return flash.getOS();
}

function getPluginDetails() {
	try {
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
			var plugin_names = [ "QuickTime", "Java", "DevalVR", "Flash",
					"Shockwave", "WindowsMediaPlayer", "Silverlight", "VLC",
					"AdobeReader", "PDFReader", "RealPlayer", "IEcomponent",
					"ActiveX", "PDFjs" ]
			for (var i = 0; i < plugin_names.length; ++i) {
				var version = PluginDetect.getVersion(plugin_names[i]);
				if (version) {
					plugins += plugin_names[i] + " " + version + "; ";
				}
			}
		}

		if (plugins == "") {
			plugins = "No plugins detected";
		}
	} catch (e) {
		return "Error";
	}

	return plugins;
}

function getScreenDetails() {
	try {
		return screen.width + "x" + screen.height + "x" + screen.colorDepth;
	} catch (e) {
		return "Error";
	}
}

function getScreenDetailsFlash(flash) {
	return flash.getResolution().join("x");
}

function getFonts(flash) {
	return flash.getFonts();// .join().replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,
	// '_');
}

function getTimeZone() {
	try {
		return new Date().getTimezoneOffset();
	} catch (e) {
		return "Error";
	}
}

function getSuperCookie() {
	try {
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
		try {
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
	} catch (e) {
		return "Error";
	}

	return test;
}

function getTime() {
	try {
		var time = new Date().getTime();
		return time;
	} catch (e) {
		return "Error";
	}
}

function getDateTime() {
	try {
		var d = new Date(0);
		return d.toLocaleString();
	} catch (ex) {
		return "Error";
	}
}

function getMathTan() {
	try {
		return Math.tan(-1e300);
	} catch (e) {
		return "Error";
	}
}

function getAdsBlocked() {
	try {
		if ($('#ad').height() == 0) {
			// Ads are blocked.
			return 1;
		} else {
			// Ads are not blocked.
			return 0;
		}
	} catch (e) {
		return "Error";
	}
}

function getCanvas() {
	try {
		/*
		 * Function's code taken from from Am I Unique?
		 * https://amiunique.org
		 * 
		 * The MIT License (MIT)
		 *
		 * Copyright (c) 2014 Pierre Laperdrix
		 *
		 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
		 *  to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
		 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
		 *
		 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
		 *
		 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
		 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
		 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
		 */
		canvas = document.createElement("canvas");
		canvas.height = 60;
		canvas.width = 400;
		canvasContext = canvas.getContext("2d");
		canvas.style.display = "inline";
		canvasContext.textBaseline = "alphabetic";
		canvasContext.fillStyle = "#f60";
		canvasContext.fillRect(125, 1, 62, 20);
		canvasContext.fillStyle = "#069";
		canvasContext.font = "11pt no-real-font-123";
		canvasContext.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03",
				2, 15);
		canvasContext.fillStyle = "rgba(102, 204, 0, 0.7)";
		canvasContext.font = "18pt Arial";
		canvasContext.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03",
				4, 45);
		return canvas.toDataURL();
	} catch (e) {
		return "Not supported";
	}
}

function getWebGLVendor() {
	try {
		var canvas = document.createElement('canvas');
		var ctx = canvas.getContext("webgl")
				|| canvas.getContext("experimental-webgl");
		if (ctx.getSupportedExtensions().indexOf("WEBGL_debug_renderer_info") >= 0) {
			return ctx
					.getParameter(ctx.getExtension('WEBGL_debug_renderer_info').UNMASKED_VENDOR_WEBGL);
		} else {
			return "Not supported";
		}
	} catch (e) {
		return "Not supported";
	}
}

function getWebGLRenderer() {
	try {
		var canvas = document.createElement('canvas');
		var ctx = canvas.getContext("webgl")
				|| canvas.getContext("experimental-webgl");
		if (ctx.getSupportedExtensions().indexOf("WEBGL_debug_renderer_info") >= 0) {
			return ctx
					.getParameter(ctx.getExtension('WEBGL_debug_renderer_info').UNMASKED_RENDERER_WEBGL);
		} else {
			return "Not supported";
		}
	} catch (e) {
		return "Not supported";
	}
}

function getLanguageFlash(flash) {
	return flash.getLanguage();
}