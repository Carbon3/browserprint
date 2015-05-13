package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.TorCheck;
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
		fingerprint.setDateTime(request.getParameter("DateTime"));
		fingerprint.setMathTan(request.getParameter("MathTan"));

		{
			long ourTime = new Date().getTime();
			long theirTime;
			try {
				theirTime = Long.parseLong(request.getParameter("Time"));
			} catch (NumberFormatException ex) {
				// Difference of 0.
				theirTime = ourTime;
			}

			// Get how many minutes our times differ by.
			long difference = (ourTime - theirTime) / (1000 * 60);
			fingerprint.setClockDifference(difference);
		}

		serveRequest(request, response, fingerprint);
	}

	/**
	 * Finalise a request then forward it to the output page.
	 * 
	 * @param request
	 * @param response
	 * @param fingerprint
	 * @throws ServletException
	 * @throws IOException
	 */
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

	/**
	 * Get the basic fingerprint of a request.
	 * This consists of fingerprint properties that can be taken without JavaScript.
	 * 
	 * @param request
	 * @return
	 */
	public Fingerprint getBasicFingerprint(HttpServletRequest request) {
		Fingerprint fingerprint = new Fingerprint();

		fingerprint.setUser_agent(getUserAgentHeaderString(request));
		fingerprint.setAccept_headers(getAcceptHeadersString(request));
		fingerprint.setDoNotTrack(getDoNotTrackHeaderString(request));
		fingerprint.setUsingTor(TorCheck.isUsingTor(
				getServletContext().getInitParameter("serversPublicIP"),
				request.getLocalPort(),
				request.getRemoteAddr(),
				getServletContext().getInitParameter("TorDNSELServer")
				) == true);
		fingerprint.setIpAddress(request.getRemoteAddr());

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

	/**
	 * Get the sample IDs from a request.
	 * Each sample ID represents a different fingerprint that was offered up by this browser in the past.
	 * The browser keeps track of previous sample IDs to prevent double counting of fingerprints.
	 * 
	 * @param request
	 * @return
	 */
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

	/**
	 * Save a set of sample IDs to a cookie in the HTTP response.
	 * 
	 * @param response
	 * @param sampleIDs
	 */
	public void saveSampleIDs(HttpServletResponse response, ArrayList<Integer> sampleIDs) {
		String sampleIDstr = "";
		for (Integer sampleID : sampleIDs) {
			sampleIDstr += sampleID + ",";
		}
		Cookie sampleIdCookie = new Cookie("SampleIDs", sampleIDstr);
		sampleIdCookie.setMaxAge(60 * 60 * 24 * 30);// 30 days
		response.addCookie(sampleIdCookie);
	}

	/**
	 * Get the User-Agent string of a request.
	 * 
	 * @param request
	 * @return
	 */
	public String getUserAgentHeaderString(HttpServletRequest request) {
		String useragent;
		try {
			// We get the header in this more long-winded way so that it may have unicode characters in it, such as Chinese.
			useragent = new String(request.getHeader("User-Agent").getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception e) {
			// Fallback to regular method.
			useragent = request.getHeader("User-Agent");
		}
		return useragent;
	}

	/**
	 * Get the accept headers of a request.
	 * 
	 * @param request
	 * @return
	 */
	public String getAcceptHeadersString(HttpServletRequest request) {
		try {
			// We get the headers this more long-winded way so that they may have unicode characters inside them.
			return new String(request.getHeader("accept").getBytes("ISO8859-1"), "UTF-8") + " "
					+ new String(request.getHeader("accept-encoding").getBytes("ISO8859-1"), "UTF-8") + " "
					+ new String(request.getHeader("accept-language").getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception e) {
			// Fallback to regular method.
			return request.getHeader("accept") + " "
					+ request.getHeader("accept-encoding") + " "
					+ request.getHeader("accept-language");
		}
	}

	/**
	 * Get the DNT header string of a request.
	 * 
	 * @param request
	 * @return
	 */
	public String getDoNotTrackHeaderString(HttpServletRequest request) {
		String dnt;
		try {
			// We get the header in this more long-winded way so that it may have unicode characters in it, such as Chinese.
			dnt = new String(request.getHeader("DNT").getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception e) {
			// Fallback to regular method.
			dnt = request.getHeader("DNT");
		}
		return dnt;
	}
}
