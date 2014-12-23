package servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAOs.FingerprintDAO;
import beans.CharacteristicBean;
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
		CharacteristicBean uniquenessbean = new CharacteristicBean();
		Integer sampleID = FingerprintDAO.processFingerprint(fingerprint, chrsbean, uniquenessbean);
		request.setAttribute("chrBean", chrsbean);
		request.setAttribute("uniquessbean", uniquenessbean);

		/*
		 * Save SampleID in a cookie if we have one now.
		 */
		fingerprint.getSampleIDs().add(sampleID);
		saveSampleIDs(response, fingerprint.getSampleIDs());

		/*
		 * Forward to the output page.
		 */
		request.getRequestDispatcher("/WEB-INF/Output.jsp").forward(request, response);
	}

	public Fingerprint getBasicFingerprint(HttpServletRequest request) {
		Fingerprint fingerprint = new Fingerprint();
		fingerprint.setUser_agent(request.getHeader("User-Agent"));
		fingerprint.setAccept_headers(getAcceptHeadersString(request));
		fingerprint.setDoNotTrack(request.getHeader("DNT"));
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			fingerprint.setCookiesEnabled(true);

		}
		else {
			fingerprint.setCookiesEnabled(false);
		}
		fingerprint.setSampleIDs(getSampleIDs(request));

		return fingerprint;
	}

	public ArrayList<Integer> getSampleIDs(HttpServletRequest request) {
		Cookie cookies[] = request.getCookies();

		ArrayList<Integer> sampleIDs = new ArrayList<Integer>();
		if (cookies == null) {
			// No SampleIDs. Just return an empty list.
			return sampleIDs;
		}

		// Find the SampleIDs cookie.
		for (int i = 0; i < cookies.length; ++i) {
			if (cookies[i].getName().equals("SampleIDs")) {
				// Cookie found. Split it into an array of SampleIDs.
				String sampleIDstrs[] = cookies[i].getValue().split(",");
				for (String sampleIDstr : sampleIDstrs) {
					try {
						// Add the SampleID to our list.
						Integer sampleID = Integer.parseInt(sampleIDstr);
						sampleIDs.add(sampleID);
					} catch (NumberFormatException ex) {
						// Ignore. Pretend invalid sampleID doesn't exist.
					}
				}
				break;
			}
		}
		return sampleIDs;
	}

	public void saveSampleIDs(HttpServletResponse response, ArrayList<Integer> sampleIDs) {
		String sampleIDstr = "";
		for (Integer sampleID : sampleIDs) {
			sampleIDstr += sampleID + ",";
		}
		Cookie sampleIdCookie = new Cookie("SampleIDs", sampleIDstr);
		sampleIdCookie.setMaxAge(60 * 60 * 24 * 30);// 30 days
		response.addCookie(sampleIdCookie);
	}

	public String getAcceptHeadersString(HttpServletRequest request) {
		return request.getHeader("accept") + " "
				+ request.getHeader("accept-encoding") + " "
				+ request.getHeader("accept-language");
	}
}
