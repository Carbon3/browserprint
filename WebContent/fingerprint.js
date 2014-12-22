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
		 * Try the method that works with IE.
		 * Uses an MIT licensed script, PluginDetect.
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

	return plugins;
}

function getScreenDetails() {
	return screen.width + "x" + screen.height + "x" + screen.colorDepth;
}

function getTimeZone() {
	return new Date().getTimezoneOffset();
}