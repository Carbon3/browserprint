package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TorCheck {
	/**
	 * Check whether a given IP address belongs to an exit node that allows connections to this server on the given port.
	 * Does this by querying TorDNSEL: https://www.torproject.org/projects/tordnsel.html.en
	 * 
	 * @param localIP
	 *            IP address of this server.
	 * @param localPort
	 *            Port used on this server.
	 * @param exitIP
	 *            IP address of alleged exit relay.
	 * @return
	 */
	public static Boolean isUsingTor(String localIP, int localPort, String exitIP) {
		/*
		 * Reverse both IP addresses
		 */
		String reversedMyIP = reverseIP(localIP);
		String reversedExitIP = reverseIP(exitIP);

		/*
		 * Perform the dig.
		 */
		Process proc;
		try {
			proc = Runtime.getRuntime().exec("dig " + reversedExitIP + "." + localPort + "." + reversedMyIP + ".ip-port.exitlist.torproject.org");
		} catch (IOException e) {
			// e.printStackTrace();
			return null;
		}
		try {
			proc.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			boolean retval = false;
			while (reader.ready()) {
				String line = reader.readLine();
				// System.out.println(line);

				if (line.equals(";; ANSWER SECTION:") && reader.ready()) {
					line = reader.readLine();
					// System.out.println(line);

					/*
					 * Check that DNSEL returned 127.0.0.2, indicating that there is an exit node at that address that allows us to contact our local address / port.
					 */
					Pattern p = Pattern.compile("^" + reversedExitIP + "." + localPort + "." + reversedMyIP + ".ip-port.exitlist.torproject.org. \\d+ IN A 127.0.0.2$");
					if (p.matcher(line).matches()) {
						retval = true;
						break;
					}
				}
			}
			reader.close();
			return retval;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		} finally {
			proc.destroy();
		}
	}

	/**
	 * Reverse the order of the octets in a given IPv4 address.
	 * 
	 * @param addr
	 * @return
	 */
	public static String reverseIP(String addr) {
		Pattern p = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)$");
		Matcher m = p.matcher(addr);
		return m.replaceAll("$4.$3.$2.$1");
	}

	/**
	 * Test the checker.
	 * @param args
	 */
	public static void main(String args[]) {
		String myIP = "1.2.3.5";
		String exitIP = "27.124.124.122";

		System.out.println("Using Tor? " + isUsingTor(myIP, 443, exitIP));
	}
}
