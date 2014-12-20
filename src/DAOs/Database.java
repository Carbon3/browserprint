package DAOs;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Database {
	/*
	 * Init our connection pool.
	 * Configured in META-INF/context.xml
	 */
	private static DataSource ds;
	static {
		InitialContext initContext = null;
		Context envContext = null;
		try {
			initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/lpanopticlickDB");
		} catch (NamingException e) {
			e.printStackTrace();
			ds = null;
			// Close contexts if they're open
			try {
				if (initContext != null) {
					initContext.close();
				}
				if (envContext != null) {
					envContext.close();
				}
			} catch (Exception e2) {
				throw new Error(e2);
			}
		}
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
}
