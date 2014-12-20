package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
	private static final String insertSampleStr = "INSERT INTO `Samples`(`UserAgent`, `AcceptHeaders`, `PluginDetails`, `TimeZone`, `ScreenDetails`, `Fonts`, `CookiesEnabled`, `SuperCookie`) VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String getSampleCountStr = "SELECT COUNT(*) FROM `Samples`;";
	private static final String getUserAgentCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `UserAgent` = ?;";
	private static final String getAcceptHeadersCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `AcceptHeaders` = ?;";
	private static final String getPluginDetailsCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `PluginDetails` = ?;";
	private static final String getNULLPluginDetailsCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `PluginDetails` IS NULL;";
	private static final String getTimeZoneCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `TimeZone` = ?;";
	private static final String getNULLTimeZoneCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `TimeZone` IS NULL;";
	private static final String getScreenDetailsCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `ScreenDetails` = ?;";
	private static final String getNULLScreenDetailsCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `ScreenDetails` IS NULL;";
	private static final String getFontsCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `Fonts` = ?;";
	private static final String getNULLFontsCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `Fonts` IS NULL;";
	private static final String getCookiesEnabledStr = "SELECT COUNT(*) FROM `Samples` WHERE `CookiesEnabled` = ?;";
	private static final String getSuperCookieCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `SuperCookie` = ?;";
	private static final String getNULLSuperCookieCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `SuperCookie` IS NULL;";

	private static final String NO_JAVASCRIPT = "no javascript";

	public static final Integer processFingerprint(Fingerprint fingerprint, CharacteristicsBean chrsbean) {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(false);

			Integer sampleID = fingerprint.getSampleID();
			{
				boolean log_sample = true;
				if (fingerprint.getSampleID() != null) {
					/*
					 * We have seen this user before. Check if their fingerprint has changed.
					 */
					log_sample = checkSampleChanged(conn, fingerprint);
				}

				/*
				 * Write fingerprint sample to database.
				 */
				if (log_sample) {
					sampleID = insertSample(conn, fingerprint);
				}
			}

			/*
			 * --------------------------------------------------
			 * Get the fingerprint's statistics.
			 * --------------------------------------------------
			 */

			/*
			 * Get number of samples.
			 */
			int sampleCount = getSampleCount(conn);

			ArrayList<CharacteristicBean> characteristics = chrsbean.getCharacteristics();

			characteristics.add(getUserAgentCB(conn, sampleCount, fingerprint.getUser_agent()));
			characteristics.add(getAcceptHeadersCB(conn, sampleCount, fingerprint.getAccept_headers()));
			characteristics.add(getPluginDetailsCB(conn, sampleCount, fingerprint.getPluginDetails()));
			characteristics.add(getTimeZoneCB(conn, sampleCount, fingerprint.getTimeZone()));
			characteristics.add(getScreenDetailsCB(conn, sampleCount, fingerprint.getScreenDetails()));
			characteristics.add(getFontsCB(conn, sampleCount, fingerprint.getFonts()));
			characteristics.add(getCookiesEnabledCB(conn, sampleCount, fingerprint.isCookiesEnabled()));
			characteristics.add(getSuperCookieCB(conn, sampleCount, fingerprint.getSuperCookie()));

			/*
			 * <Debug stuff>
			 */
			CharacteristicBean chrbean = new CharacteristicBean();
			characteristics.add(chrbean);
			chrbean.setName("Sample count [DEBUG]");
			chrbean.setInX(sampleCount);
			/*
			 * </Debug stuff>
			 */

			return sampleID;

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
	public static Integer insertSample(Connection conn, Fingerprint fingerprint) throws SQLException {
		PreparedStatement insertSample = conn.prepareStatement(insertSampleStr, Statement.RETURN_GENERATED_KEYS);
		insertSample.setString(1, fingerprint.getUser_agent());
		insertSample.setString(2, fingerprint.getAccept_headers());
		insertSample.setString(3, fingerprint.getPluginDetails());
		insertSample.setString(4, fingerprint.getTimeZone());
		insertSample.setString(5, fingerprint.getScreenDetails());
		insertSample.setString(6, fingerprint.getFonts());
		insertSample.setBoolean(7, fingerprint.isCookiesEnabled());
		insertSample.setString(8, fingerprint.getSuperCookie());
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
	 * @return
	 * @throws SQLException
	 */
	public static boolean checkSampleChanged(Connection conn, Fingerprint fingerprint) throws SQLException {
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
				+ ";";
		PreparedStatement checkExists = conn.prepareStatement(query);
		checkExists.setInt(1, fingerprint.getSampleID());

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

	public static int getSampleCount(Connection conn) throws SQLException {
		PreparedStatement getSampleCount = conn.prepareStatement(getSampleCountStr);
		ResultSet rs = getSampleCount.executeQuery();
		rs.next();
		int sampleCount = rs.getInt(1);
		rs.close();
		return sampleCount;
	}

	/**
	 * Create the user-agent CharacteristicBean.
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
	public static CharacteristicBean getUserAgentCB(Connection conn, int num_samples, String value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		chrbean.setName("User Agent");
		chrbean.setValue(value);

		PreparedStatement getCount = conn.prepareStatement(getUserAgentCountStr);
		getCount.setString(1, value);
		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) count) / ((double) num_samples));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create the Accept Headers CharacteristicBean.
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
	public static CharacteristicBean getAcceptHeadersCB(Connection conn, int num_samples, String value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		chrbean.setName("HTTP_ACCEPT Headers");
		chrbean.setValue(value);

		PreparedStatement getCount = conn.prepareStatement(getAcceptHeadersCountStr);
		getCount.setString(1, value);
		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) count) / ((double) num_samples));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create the Plugin Details CharacteristicBean.
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
	public static CharacteristicBean getPluginDetailsCB(Connection conn, int num_samples, String value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		chrbean.setName("Browser Plugin Details");

		PreparedStatement getCount;
		if (value != null) {
			chrbean.setValue(value);

			getCount = conn.prepareStatement(getPluginDetailsCountStr);
			getCount.setString(1, value);
		}
		else {
			chrbean.setValue(NO_JAVASCRIPT);

			getCount = conn.prepareStatement(getNULLPluginDetailsCountStr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) count) / ((double) num_samples));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create the Time Zone CharacteristicBean.
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
	public static CharacteristicBean getTimeZoneCB(Connection conn, int num_samples, String value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		chrbean.setName("Time Zone");

		PreparedStatement getCount;
		if (value != null) {
			chrbean.setValue(value);

			getCount = conn.prepareStatement(getTimeZoneCountStr);
			getCount.setString(1, value);
		}
		else {
			chrbean.setValue(NO_JAVASCRIPT);

			getCount = conn.prepareStatement(getNULLTimeZoneCountStr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) count) / ((double) num_samples));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create the Screen Details CharacteristicBean.
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
	public static CharacteristicBean getScreenDetailsCB(Connection conn, int num_samples, String value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		chrbean.setName("Screen Size and Color Depth");

		PreparedStatement getCount;
		if (value != null) {
			chrbean.setValue(value);

			getCount = conn.prepareStatement(getScreenDetailsCountStr);
			getCount.setString(1, value);
		}
		else {
			chrbean.setValue(NO_JAVASCRIPT);

			getCount = conn.prepareStatement(getNULLScreenDetailsCountStr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) count) / ((double) num_samples));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create the System Fonts CharacteristicBean.
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
	public static CharacteristicBean getFontsCB(Connection conn, int num_samples, String value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		chrbean.setName("System Fonts");

		PreparedStatement getCount;
		if (value != null) {
			chrbean.setValue(value);

			getCount = conn.prepareStatement(getFontsCountStr);
			getCount.setString(1, value);
		}
		else {
			chrbean.setValue(NO_JAVASCRIPT);

			getCount = conn.prepareStatement(getNULLFontsCountStr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) count) / ((double) num_samples));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
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
	public static CharacteristicBean getCookiesEnabledCB(Connection conn, int num_samples, boolean value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		chrbean.setName("Are Cookies Enabled?");
		if (value) {
			chrbean.setValue("Yes");
		}
		else {
			chrbean.setValue("No");
		}

		PreparedStatement getCount = conn.prepareStatement(getCookiesEnabledStr);
		getCount.setBoolean(1, value);
		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) count) / ((double) num_samples));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create the Super Cookie CharacteristicBean.
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
	public static CharacteristicBean getSuperCookieCB(Connection conn, int num_samples, String value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		chrbean.setName("Limited supercookie test");

		PreparedStatement getCount;
		if (value != null) {
			chrbean.setValue(value);

			getCount = conn.prepareStatement(getSuperCookieCountStr);
			getCount.setString(1, value);
		}
		else {
			chrbean.setValue(NO_JAVASCRIPT);

			getCount = conn.prepareStatement(getNULLSuperCookieCountStr);
		}


		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setInX(((double) count) / ((double) num_samples));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}
}