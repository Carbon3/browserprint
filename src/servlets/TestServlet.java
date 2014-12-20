package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAOs.FingerprintDAO;
import beans.CharacteristicsBean;
import datastructures.Fingerprint;

/**
 * Servlet implementation class TestServlet
 */
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String js_enabled = request.getParameter("js_enabled");
		if (js_enabled == null) {
			/*
			 * The non-JS version of the page.
			 * Perform just a basic fingerprinting.
			 * None of the characteristics that require javascript.
			 */
			Fingerprint fingerprint = getBasicFingerprint(request);
			serveRequest(request, response, fingerprint);
			return;
		} else {
			/*
			 * The JS enabled version of the page.
			 * Do a full fingerprinting.
			 * Will perform javascript fingerprinting then submit fingerprint via a POST request.
			 */
			request.getRequestDispatcher("/WEB-INF/JsTest.jsp").forward(request, response);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Fingerprint fingerprint = getBasicFingerprint(request);

		/*
		 * Extract the rest of the fingerprint from the POST details.
		 */
		fingerprint.setPluginDetails(request.getParameter("PluginDetails"));
		fingerprint.setTimeZone(request.getParameter("TimeZone"));
		fingerprint.setScreenDetails(request.getParameter("ScreenDetails"));
		fingerprint.setFonts(request.getParameter("Fonts"));
		fingerprint.setSuperCookie(request.getParameter("SuperCookie"));

		serveRequest(request, response, fingerprint);
	}

	public void serveRequest(HttpServletRequest request, HttpServletResponse response, Fingerprint fingerprint) throws ServletException, IOException {
		CharacteristicsBean chrsbean = new CharacteristicsBean();
		Integer sampleID = FingerprintDAO.processFingerprint(fingerprint, chrsbean);
		request.setAttribute("chrBean", chrsbean);

		/*
		 * Save SampleID in a cookie if we have one now.
		 */
		Cookie sampleIdCookie = new Cookie("SampleID", sampleID.toString());
		response.addCookie(sampleIdCookie);

		/*
		 * Forward to the output page.
		 */
		request.getRequestDispatcher("/WEB-INF/NoJsTest.jsp").forward(request, response);
	}

	public Fingerprint getBasicFingerprint(HttpServletRequest request) {
		Fingerprint fingerprint = new Fingerprint();
		fingerprint.setUser_agent(request.getHeader("User-Agent"));
		fingerprint.setAccept_headers(getAcceptHeadersString(request));
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			fingerprint.setCookiesEnabled(true);

			/*
			 * Get SampleID if the cookies have one.
			 */
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("SampleID")) {
					try {
						Integer sampleID = Integer.parseInt(cookies[i].getValue());
						fingerprint.setSampleID(sampleID);
					} catch (NumberFormatException ex) {
						// Ignore. Pretend invalid sampleID doesn't exist.
					}
					break;
				}
			}
		}
		else {
			fingerprint.setCookiesEnabled(false);
		}
		return fingerprint;
	}

	public String getAcceptHeadersString(HttpServletRequest request) {
		return request.getHeader("accept") + " "
				+ request.getHeader("accept-encoding") + " "
				+ request.getHeader("accept-language");
	}
}
