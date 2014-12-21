function getScreenDetails() {
	return screen.width + "x" + screen.height + "x" + screen.colorDepth;
}

function getTimeZone(){
	return new Date().getTimezoneOffset();
}