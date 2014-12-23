package datastructures;

import java.util.ArrayList;

public class Fingerprint {
	private ArrayList<Integer> sampleIDs;
	private String user_agent;
	private String accept_headers;

	private String pluginDetails;
	private String timeZone;
	private String screenDetails;
	private String fonts;

	private boolean cookiesEnabled;

	private String superCookie;
	private String doNotTrack;

	public Fingerprint() {
		sampleIDs = null;
		user_agent = null;
		accept_headers = null;
		pluginDetails = null;
		timeZone = null;
		screenDetails = null;
		fonts = null;
		cookiesEnabled = false;
		superCookie = null;
		doNotTrack = null;
	}

	public ArrayList<Integer> getSampleIDs() {
		return sampleIDs;
	}

	public void setSampleIDs(ArrayList<Integer> sampleIDs) {
		this.sampleIDs = sampleIDs;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getAccept_headers() {
		return accept_headers;
	}

	public void setAccept_headers(String accept_headers) {
		this.accept_headers = accept_headers;
	}

	public String getPluginDetails() {
		return pluginDetails;
	}

	public void setPluginDetails(String pluginDetails) {
		this.pluginDetails = pluginDetails;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getScreenDetails() {
		return screenDetails;
	}

	public void setScreenDetails(String screenDetails) {
		this.screenDetails = screenDetails;
	}

	public String getFonts() {
		return fonts;
	}

	public void setFonts(String fonts) {
		this.fonts = fonts;
	}

	public boolean isCookiesEnabled() {
		return cookiesEnabled;
	}

	public void setCookiesEnabled(boolean cookiesEnabled) {
		this.cookiesEnabled = cookiesEnabled;
	}

	public String getSuperCookie() {
		return superCookie;
	}

	public void setSuperCookie(String superCookie) {
		this.superCookie = superCookie;
	}

	public String getDoNotTrack() {
		return doNotTrack;
	}

	public void setDoNotTrack(String doNotTrack) {
		this.doNotTrack = doNotTrack;
	}

}
