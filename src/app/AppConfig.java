package app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class contains all the global application configuration stuff.
 * @author bmilojkovic
 *
 */
public class AppConfig {

	/**
	 * Convenience access for this servent's information
	 */
	public static ServentInfo myServentInfo;
	
	public static List<ServentInfo> serventInfoList = new ArrayList<>();

	private static ServentInfo bootstrapNode = new ServentInfo("localhost", -1, 1000, new ArrayList<>());
	
	/**
	 * Print a message to stdout with a timestamp
	 * @param message message to print
	 */
	public static void timestampedStandardPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.out.println(timeFormat.format(now) + " - " + message);
	}
	
	/**
	 * Print a message to stderr with a timestamp
	 * @param message message to print
	 */
	public static void timestampedErrorPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.err.println(timeFormat.format(now) + " - " + message);
	}
	
	/**
	 * Get info for a servent selected by a given id.
	 * @param id id of servent to get info for
	 * @return {@link ServentInfo} object for this id
	 */
	public static ServentInfo getInfoById(int id) {
		if (id >= getServentCount()) {
			throw new IllegalArgumentException(
					"Trying to get info for servent " + id + " when there are " + getServentCount() + " servents.");
		}
		for(ServentInfo s : serventInfoList) {
			if(s.getId() == id) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * Get number of servents in this system.
	 */
	public static int getServentCount() {
		return serventInfoList.size();
	}

	public static void addServentInfo(ServentInfo newServent) {
		serventInfoList.add(newServent);
	}

	public static List<ServentInfo> getServentInfoList() {
		return serventInfoList;
	}

	public static List<ServentInfo> copyOfServentInfoList() {
		return new ArrayList<>(serventInfoList);
	}

	public static ServentInfo getBootstrapNode() {
		return bootstrapNode;
	}
}
