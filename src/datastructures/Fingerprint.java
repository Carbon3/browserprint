package datastructures;


public class Fingerprint {
	private Integer sampleSetID;
	
	private String user_agent;
	private String accept_headers;

	private String pluginDetails;
	private String timeZone;
	private String screenDetails;
	private String fonts;

	private boolean cookiesEnabled;

	private String superCookie;
	private String doNotTrack;

	private Long clockDifference;

	private String dateTime;
	private String mathTan;

	private boolean usingTor;
	private String ipAddress;
	
	private Boolean adsBlocked;
	
	private String canvas;
	private String webGL;
	private String webGLVendor;
	private String webGLRenderer;

	public Fingerprint() {
		sampleSetID = null;
		user_agent = null;
		accept_headers = null;
		pluginDetails = null;
		timeZone = null;
		screenDetails = null;
		fonts = null;
		cookiesEnabled = false;
		superCookie = null;
		doNotTrack = null;
		clockDifference = null;
		dateTime = null;
		mathTan = null;
		usingTor = false;
		ipAddress = null;
		adsBlocked = null;
		canvas = null;
		webGL = null;
	}

	public Integer getSampleSetID() {
		return sampleSetID;
	}

	public void setSampleSetID(Integer sampleSetID) {
		this.sampleSetID = sampleSetID;
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

	public Long getClockDifference() {
		return clockDifference;
	}

	public void setClockDifference(Long clockDifference) {
		this.clockDifference = clockDifference;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getMathTan() {
		return mathTan;
	}

	public void setMathTan(String mathTan) {
		this.mathTan = mathTan;
	}

	public boolean isUsingTor() {
		return usingTor;
	}

	public void setUsingTor(boolean usingTor) {
		this.usingTor = usingTor;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Boolean getAdsBlocked() {
		return adsBlocked;
	}

	public void setAdsBlocked(Boolean adsBlocked) {
		this.adsBlocked = adsBlocked;
	}

	public String getCanvas() {
		return canvas;
	}

	public void setCanvas(String canvas) {
		this.canvas = canvas;
	}

	public String getWebGL() {
		return webGL;
	}

	public void setWebGL(String webGL) {
		this.webGL = webGL;
	}

	public String getWebGLVendor() {
		return webGLVendor;
	}

	public void setWebGLVendor(String webGLVendor) {
		this.webGLVendor = webGLVendor;
	}

	public String getWebGLRenderer() {
		return webGLRenderer;
	}

	public void setWebGLRenderer(String webGLRenderer) {
		this.webGLRenderer = webGLRenderer;
	}
}