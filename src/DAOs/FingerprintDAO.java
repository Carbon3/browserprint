package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;

import beans.CharacteristicBean;
import beans.CharacteristicsBean;
import datastructures.Fingerprint;

public class FingerprintDAO {
	/**
	 * Get the threadID of a post.
	 * 
	 * @param board
	 * @param postID
	 * @return the threadID of the post if successful. Else returns null if the
	 *         post doesn't exist or an error occurs.
	 */
	private static final String insertSampleStr = "INSERT INTO `Samples`(`UserAgent`, `AcceptHeaders`, `PluginDetails`, `TimeZone`, `ScreenDetails`, `Fonts`, `CookiesEnabled`, `SuperCookie`, `DoNotTrack`, `ClockDifference`, `DateTime`, `MathTan`, `UsingTor`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String getSampleCountStr = "SELECT COUNT(*) FROM `Samples`;";

	private static final String NO_JAVASCRIPT = "no javascript";

	public static final Integer processFingerprint(Fingerprint fingerprint, CharacteristicsBean chrsbean, CharacteristicBean uniquenessbean) {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(false);

			Integer currentSampleID = null;
			ArrayList<Integer> sampleIDs = fingerprint.getSampleIDs();
			boolean log_sample = true;
			for (Integer sampleID : sampleIDs) {
				if (sampleID != null) {
					/*
					 * We have seen this user before. Check if their fingerprint has changed.
					 */
					log_sample = checkSampleChanged(conn, sampleID, fingerprint);
					if (log_sample == false) {
						// We've seen this exact sample before, including SampleID.
						currentSampleID = sampleID;
						break;
					}
				}
			}

			/*
			 * Write fingerprint sample to database.
			 */
			if (log_sample) {
				currentSampleID = insertSample(conn, fingerprint);
			}

			/*
			 * Get number of samples.
			 */
			int sampleCount = getSampleCount(conn);

			/*
			 * Get uniqueness.
			 */
			int sampleOccurrences = getSampleOccurrences(conn, fingerprint);
			uniquenessbean.setName("");
			if (sampleOccurrences == 1) {
				uniquenessbean.setValue("unique");
			} else {
				uniquenessbean.setValue("");
			}
			uniquenessbean.setInX(((double) sampleCount) / ((double) sampleOccurrences));
			uniquenessbean.setBits(Math.abs(Math.log(uniquenessbean.getInX()) / Math.log(2)));

			/*
			 * Get each characteristic.
			 */
			ArrayList<CharacteristicBean> characteristics = chrsbean.getCharacteristics();
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "UserAgent", fingerprint.getUser_agent());
				bean.setName("User Agent");
				bean.setNameHoverText("The User-Agent header sent with the HTTP request for the page.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "AcceptHeaders", fingerprint.getAccept_headers());
				bean.setName("HTTP_ACCEPT Headers");
				bean.setNameHoverText("The concatenation of three headers from the HTTP request:"
						+ " The Accept request header, the Accept-Encoding request header, and the Accept-Language request header.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "PluginDetails", fingerprint.getPluginDetails());
				bean.setName("Browser Plugin Details");
				bean.setNameHoverText("A list of the browsers installed plugins as detected using JavaScript.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "TimeZone", fingerprint.getTimeZone());
				bean.setName("Time Zone");
				bean.setNameHoverText("The time-zone configured on the client's machine.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "ScreenDetails", fingerprint.getScreenDetails());
				bean.setName("Screen Size and Color Depth");
				bean.setNameHoverText("The screen size and colour depth of the monitor displaying the client's web browser.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "Fonts", fingerprint.getFonts());
				if (bean.getValue().equals("")) {
					bean.setValue("No fonts detected");
				}
				bean.setName("System Fonts");
				bean.setNameHoverText("The fonts installed on the client's machine, detected using Flash.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "CookiesEnabled", fingerprint.isCookiesEnabled());
				bean.setName("Are Cookies Enabled?");
				bean.setNameHoverText("Whether cookies are enabled.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "SuperCookie", fingerprint.getSuperCookie());
				bean.setName("Limited supercookie test");
				bean.setNameHoverText("Three tests of whether DOM storage is supported (and enabled) in the client's web browser."
						+ " Tests for localStorage, sessionStorage, and Internet Explorer's userData.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "DoNotTrack", fingerprint.getDoNotTrack());
				if (bean.getValue().equals(NO_JAVASCRIPT)) {
					bean.setValue("No preference");
				}
				bean.setName("Do Not Track header");
				bean.setNameHoverText("The value of the DNT (Do Not Track) header from the HTTP request.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "ClockDifference", fingerprint.getClockDifference());
				bean.setName("Client/server time difference (minutes)");
				bean.setNameHoverText("The approximate amount of difference between the time on the client's computer and the clock on the server."
						+ " i.e., the clock on the client's computer is 5 minutes ahead of the clock on the server.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "DateTime", fingerprint.getDateTime());
				bean.setName("Date/Time format");
				bean.setNameHoverText("When the JavaScript function toLocaleString() is called on a date it can reveal information about the language of the browser via the names of days and months."
						+ " For instance the output 'Thursday January 01, 10:30:00 GMT+1030 1970' reveals that English is our configured language because 'Thursday' is English."
						+ " Additionally different browsers tend to return differently formatted results."
						+ " For instance Opera returns the above whereas Firefox returns '1/1/1970 9:30:00 am' for the same date (UNIX epoch)."
						+ " Additionally timezone information may be revealed."
						+ " For instance the above were taken on a computer configured for CST (+9:30), which is why the times shown aren't midnight.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "MathTan", fingerprint.getMathTan());
				bean.setName("Math/Tan function");
				bean.setNameHoverText("The same math functions run on different platforms and browsers can produce different results."
						+ " In particular we are interested in the output of Math.tan(-1e300), which has been observed to produce different values depending on operating system."
						+ " For instance on a 64bit Linux machine it produces the value -1.4214488238747245 and on a Windows machine it produces the value -4.987183803371025.");
				characteristics.add(bean);
			}
			{
				CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "UsingTor", fingerprint.isUsingTor());
				bean.setName("Using Tor?");
				bean.setNameHoverText("Checks whether a client's request came from a Tor exit node, and hence whether they're using Tor."
						+ " It does so by performing a TorDNSEL request for each client.");
				characteristics.add(bean);
			}

			return currentSampleID;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the connection
			// Finally triggers even if we return
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// Ignore
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param conn
	 * @param fingerprint
	 * @return The sample ID of the inserted sample.
	 * @throws SQLException
	 */
	private static Integer insertSample(Connection conn, Fingerprint fingerprint) throws SQLException {
		PreparedStatement insertSample = conn.prepareStatement(insertSampleStr, Statement.RETURN_GENERATED_KEYS);
		insertSample.setString(1, fingerprint.getUser_agent());
		insertSample.setString(2, fingerprint.getAccept_headers());
		insertSample.setString(3, fingerprint.getPluginDetails());
		insertSample.setString(4, fingerprint.getTimeZone());
		insertSample.setString(5, fingerprint.getScreenDetails());
		insertSample.setString(6, fingerprint.getFonts());
		insertSample.setBoolean(7, fingerprint.isCookiesEnabled());
		insertSample.setString(8, fingerprint.getSuperCookie());
		insertSample.setString(9, fingerprint.getDoNotTrack());
		if (fingerprint.getClockDifference() != null) {
			insertSample.setLong(10, fingerprint.getClockDifference());
		} else {
			insertSample.setNull(10, java.sql.Types.BIGINT);
		}
		insertSample.setString(11, fingerprint.getDateTime());
		insertSample.setString(12, fingerprint.getMathTan());
		insertSample.setBoolean(13, fingerprint.isUsingTor());

		insertSample.execute();

		ResultSet rs = insertSample.getGeneratedKeys();
		Integer sampleID = null;
		if (rs.next()) {
			sampleID = rs.getInt(1);
		}
		rs.close();
		return sampleID;
	}

	/**
	 * Check whether a fingerprint with all the given details, including matching SampleID,
	 * is already inside the database.
	 * 
	 * @param conn
	 * @param fingerprint
	 * @return false if we've seen this exact sample (including SampleID) before.
	 * @throws SQLException
	 */
	private static boolean checkSampleChanged(Connection conn, int sampleID, Fingerprint fingerprint) throws SQLException {
		boolean in_database = true;
		/*
		 * We have seen this user before. Check if their fingerprint has changed.
		 */
		String query = "SELECT TRUE FROM `Samples` WHERE `SampleID` = ?"
				+ " AND `UserAgent`" + (fingerprint.getUser_agent() == null ? " IS NULL" : " = ?")
				+ " AND `AcceptHeaders`" + (fingerprint.getAccept_headers() == null ? " IS NULL" : " = ?")
				+ " AND `PluginDetails`" + (fingerprint.getPluginDetails() == null ? " IS NULL" : " = ?")
				+ " AND `TimeZone`" + (fingerprint.getTimeZone() == null ? " IS NULL" : " = ?")
				+ " AND `ScreenDetails`" + (fingerprint.getScreenDetails() == null ? " IS NULL" : " = ?")
				+ " AND `Fonts`" + (fingerprint.getFonts() == null ? " IS NULL" : " = ?")
				+ " AND `CookiesEnabled` = ?"
				+ " AND `SuperCookie`" + (fingerprint.getSuperCookie() == null ? " IS NULL" : " = ?")
				+ " AND `DoNotTrack`" + (fingerprint.getDoNotTrack() == null ? " IS NULL" : " = ?")
				+ " AND `ClockDifference`" + (fingerprint.getClockDifference() == null ? " IS NULL" : " = ?")
				+ " AND `DateTime`" + (fingerprint.getDateTime() == null ? " IS NULL" : " = ?")
				+ " AND `MathTan`" + (fingerprint.getMathTan() == null ? " IS NULL" : " = ?")
				+ " AND `UsingTor` = ?"
				+ ";";
		PreparedStatement checkExists = conn.prepareStatement(query);
		checkExists.setInt(1, sampleID);

		int index = 2;
		if (fingerprint.getUser_agent() != null) {
			checkExists.setString(index, fingerprint.getUser_agent());
			++index;
		}
		if (fingerprint.getAccept_headers() != null) {
			checkExists.setString(index, fingerprint.getAccept_headers());
			++index;
		}
		if (fingerprint.getPluginDetails() != null) {
			checkExists.setString(index, fingerprint.getPluginDetails());
			++index;
		}
		if (fingerprint.getTimeZone() != null) {
			checkExists.setString(index, fingerprint.getTimeZone());
			++index;
		}
		if (fingerprint.getScreenDetails() != null) {
			checkExists.setString(index, fingerprint.getScreenDetails());
			++index;
		}
		if (fingerprint.getFonts() != null) {
			checkExists.setString(index, fingerprint.getFonts());
			++index;
		}
		checkExists.setBoolean(index, fingerprint.isCookiesEnabled());
		++index;
		if (fingerprint.getSuperCookie() != null) {
			checkExists.setString(index, fingerprint.getSuperCookie());
			++index;
		}
		if (fingerprint.getDoNotTrack() != null) {
			checkExists.setString(index, fingerprint.getDoNotTrack());
			++index;
		}
		if (fingerprint.getClockDifference() != null) {
			checkExists.setLong(index, fingerprint.getClockDifference());
			++index;
		}
		if (fingerprint.getDateTime() != null) {
			checkExists.setString(index, fingerprint.getDateTime());
			++index;
		}
		if (fingerprint.getMathTan() != null) {
			checkExists.setString(index, fingerprint.getMathTan());
			++index;
		}
		checkExists.setBoolean(index, fingerprint.isUsingTor());
		++index;

		ResultSet rs = checkExists.executeQuery();

		if (rs.next()) {
			/*
			 * We've seen this sample before and the fingerprint hasn't changed,
			 * don't log it.
			 */
			in_database = false;
		}
		rs.close();
		checkExists.close();
		return in_database;
	}

	private static int getSampleCount(Connection conn) throws SQLException {
		PreparedStatement getSampleCount = conn.prepareStatement(getSampleCountStr);
		ResultSet rs = getSampleCount.executeQuery();
		rs.next();
		int sampleCount = rs.getInt(1);
		rs.close();
		return sampleCount;
	}

	/**
	 * Check whether a fingerprint with all the given details, including matching SampleID,
	 * is already inside the database.
	 * 
	 * @param conn
	 * @param fingerprint
	 * @return
	 * @throws SQLException
	 */
	private static int getSampleOccurrences(Connection conn, Fingerprint fingerprint) throws SQLException {
		/*
		 * We have seen this user before. Check if their fingerprint has changed.
		 */
		String query = "SELECT COUNT(*) FROM `Samples` WHERE"
				+ " `UserAgent`" + (fingerprint.getUser_agent() == null ? " IS NULL" : " = ?")
				+ " AND `AcceptHeaders`" + (fingerprint.getAccept_headers() == null ? " IS NULL" : " = ?")
				+ " AND `PluginDetails`" + (fingerprint.getPluginDetails() == null ? " IS NULL" : " = ?")
				+ " AND `TimeZone`" + (fingerprint.getTimeZone() == null ? " IS NULL" : " = ?")
				+ " AND `ScreenDetails`" + (fingerprint.getScreenDetails() == null ? " IS NULL" : " = ?")
				+ " AND `Fonts`" + (fingerprint.getFonts() == null ? " IS NULL" : " = ?")
				+ " AND `CookiesEnabled` = ?"
				+ " AND `SuperCookie`" + (fingerprint.getSuperCookie() == null ? " IS NULL" : " = ?")
				+ " AND `DoNotTrack`" + (fingerprint.getDoNotTrack() == null ? " IS NULL" : " = ?")
				+ " AND `ClockDifference`" + (fingerprint.getClockDifference() == null ? " IS NULL" : " = ?")
				+ " AND `DateTime`" + (fingerprint.getDateTime() == null ? " IS NULL" : " = ?")
				+ " AND `MathTan`" + (fingerprint.getMathTan() == null ? " IS NULL" : " = ?")
				+ " AND `UsingTor` = ?"
				+ ";";
		PreparedStatement checkExists = conn.prepareStatement(query);

		int index = 1;
		if (fingerprint.getUser_agent() != null) {
			checkExists.setString(index, fingerprint.getUser_agent());
			++index;
		}
		if (fingerprint.getAccept_headers() != null) {
			checkExists.setString(index, fingerprint.getAccept_headers());
			++index;
		}
		if (fingerprint.getPluginDetails() != null) {
			checkExists.setString(index, fingerprint.getPluginDetails());
			++index;
		}
		if (fingerprint.getTimeZone() != null) {
			checkExists.setString(index, fingerprint.getTimeZone());
			++index;
		}
		if (fingerprint.getScreenDetails() != null) {
			checkExists.setString(index, fingerprint.getScreenDetails());
			++index;
		}
		if (fingerprint.getFonts() != null) {
			checkExists.setString(index, fingerprint.getFonts());
			++index;
		}
		checkExists.setBoolean(index, fingerprint.isCookiesEnabled());
		++index;
		if (fingerprint.getSuperCookie() != null) {
			checkExists.setString(index, fingerprint.getSuperCookie());
			++index;
		}
		if (fingerprint.getDoNotTrack() != null) {
			checkExists.setString(index, fingerprint.getDoNotTrack());
			++index;
		}
		if (fingerprint.getClockDifference() != null) {
			checkExists.setLong(index, fingerprint.getClockDifference());
			++index;
		}
		if (fingerprint.getDateTime() != null) {
			checkExists.setString(index, fingerprint.getDateTime());
			++index;
		}
		if (fingerprint.getMathTan() != null) {
			checkExists.setString(index, fingerprint.getMathTan());
			++index;
		}
		checkExists.setBoolean(index, fingerprint.isUsingTor());
		++index;

		ResultSet rs = checkExists.executeQuery();

		rs.next();
		int count = rs.getInt(1);
		rs.close();
		checkExists.close();
		return count;
	}

	/**
	 * Create the cookies enabled CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getCharacteristicBean(Connection conn, int num_samples, String dbname, boolean value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		if (value) {
			chrbean.setValue("Yes");
		}
		else {
			chrbean.setValue("No");
		}

		PreparedStatement getCount;
		getCount = conn.prepareStatement("SELECT COUNT(*) FROM `Samples` WHERE `" + dbname + "` = ?");
		getCount.setBoolean(1, value);

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();

		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create a CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getCharacteristicBean(Connection conn, int num_samples, String dbname, String value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE `" + dbname + "`";
		if (value != null) {
			chrbean.setValue(StringEscapeUtils.escapeHtml4(value));
			querystr += " = ?;";

			getCount = conn.prepareStatement(querystr);
			getCount.setString(1, value);
		}
		else {
			chrbean.setValue(NO_JAVASCRIPT);

			querystr += " IS NULL;";
			getCount = conn.prepareStatement(querystr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create a CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getCharacteristicBean(Connection conn, int num_samples, String dbname, Long value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE `" + dbname + "`";
		if (value != null) {
			chrbean.setValue(value.toString());
			querystr += " = ?;";

			getCount = conn.prepareStatement(querystr);
			getCount.setLong(1, value);
		}
		else {
			chrbean.setValue(NO_JAVASCRIPT);

			querystr += " IS NULL;";
			getCount = conn.prepareStatement(querystr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}
}