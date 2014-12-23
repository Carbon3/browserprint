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
	private static final String insertSampleStr = "INSERT INTO `Samples`(`UserAgent`, `AcceptHeaders`, `PluginDetails`, `TimeZone`, `ScreenDetails`, `Fonts`, `CookiesEnabled`, `SuperCookie`, `DoNotTrack`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String getSampleCountStr = "SELECT COUNT(*) FROM `Samples`;";
	private static final String getUserAgentCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `UserAgent` = ?;";
	private static final String getNULLUserAgentCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `UserAgent` IS NULL;";
	private static final String getAcceptHeadersCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `AcceptHeaders` = ?;";
	private static final String getNULLAcceptHeadersCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `AcceptHeaders` IS NULL;";
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
	private static final String getDoNotTrackCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `DoNotTrack` = ?;";
	private static final String getNULLDoNotTrackCountStr = "SELECT COUNT(*) FROM `Samples` WHERE `DoNotTrack` IS NULL;";

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
			characteristics.add(
					getCharacteristicBean(conn, sampleCount, "User Agent", fingerprint.getUser_agent(),
							getUserAgentCountStr, getNULLUserAgentCountStr));
			characteristics.add(
					getCharacteristicBean(conn, sampleCount, "HTTP_ACCEPT Headers", fingerprint.getAccept_headers(),
							getAcceptHeadersCountStr, getNULLAcceptHeadersCountStr));
			characteristics.add(
					getCharacteristicBean(conn, sampleCount, "Browser Plugin Details", fingerprint.getPluginDetails(),
							getPluginDetailsCountStr, getNULLPluginDetailsCountStr));
			characteristics.add(
					getCharacteristicBean(conn, sampleCount, "Time Zone", fingerprint.getTimeZone(),
							getTimeZoneCountStr, getNULLTimeZoneCountStr));
			characteristics.add(
					getCharacteristicBean(conn, sampleCount, "Screen Size and Color Depth", fingerprint.getScreenDetails(),
							getScreenDetailsCountStr, getNULLScreenDetailsCountStr));
			characteristics.add(
					getCharacteristicBean(conn, sampleCount, "System Fonts", fingerprint.getFonts(),
							getFontsCountStr, getNULLFontsCountStr));
			characteristics.add(
					getCookiesEnabledCB(conn, sampleCount, fingerprint.isCookiesEnabled()));
			characteristics.add(
					getCharacteristicBean(conn, sampleCount, "Limited supercookie test", fingerprint.getSuperCookie(),
							getSuperCookieCountStr, getNULLSuperCookieCountStr));
			{
				CharacteristicBean doNotTrack = getCharacteristicBean(conn, sampleCount, "Do Not Track header", fingerprint.getDoNotTrack(),
						getDoNotTrackCountStr, getNULLDoNotTrackCountStr);
				if (doNotTrack.getValue().equals(NO_JAVASCRIPT)) {
					doNotTrack.setValue("No preference");
				}
				characteristics.add(doNotTrack);
			}

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
	private static CharacteristicBean getCookiesEnabledCB(Connection conn, int num_samples, boolean value) throws SQLException {
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
	private static CharacteristicBean getCharacteristicBean(Connection conn, int num_samples, String name, String value, String countQryStr, String nullCountQryStr) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		chrbean.setName(name);

		PreparedStatement getCount;
		if (value != null) {
			chrbean.setValue(StringEscapeUtils.escapeHtml4(value));

			getCount = conn.prepareStatement(countQryStr);
			getCount.setString(1, value);
		}
		else {
			chrbean.setValue(NO_JAVASCRIPT);

			getCount = conn.prepareStatement(nullCountQryStr);
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