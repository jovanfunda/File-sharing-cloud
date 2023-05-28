package app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

	public static ServentInfo getBootstrapNode() {
		return bootstrapNode;
	}

	public static void reorganizeArchitecture() {

		int myId = myServentInfo.getId();

		serventInfoList.sort(Comparator.comparingInt(ServentInfo::getId));

		Set<Integer> myNewNeighbors = new HashSet<>();

		if(serventInfoList.size() <= 5) {
			for (ServentInfo serventInfo : serventInfoList) {
				myNewNeighbors.add(serventInfo.getId());
			}
		} else {
			for (int i = 0; i < serventInfoList.size(); i++) {
				if (serventInfoList.get(i).getId() == myId) {
					if (i >= 2) {
						myNewNeighbors.add(serventInfoList.get(i - 1).getId());
						myNewNeighbors.add(serventInfoList.get(i - 2).getId());
					} else if (i == 1) {
						myNewNeighbors.add(serventInfoList.get(0).getId());
						myNewNeighbors.add(serventInfoList.get(serventInfoList.size() - 1).getId());
					} else if (i == 0) {
						myNewNeighbors.add(serventInfoList.get(serventInfoList.size() - 1).getId());
						myNewNeighbors.add(serventInfoList.get(serventInfoList.size() - 2).getId());
					}
					// poslednji element
					if (i == serventInfoList.size() - 1) {
						myNewNeighbors.add(serventInfoList.get(0).getId());
						myNewNeighbors.add(serventInfoList.get(1).getId());
						// pretposlednji element
					} else if (i == serventInfoList.size() - 2) {
						myNewNeighbors.add(serventInfoList.get(serventInfoList.size() - 1).getId());
						myNewNeighbors.add(serventInfoList.get(0).getId());
					} else {
						myNewNeighbors.add(serventInfoList.get(i + 1).getId());
						myNewNeighbors.add(serventInfoList.get(i + 2).getId());
					}
					break;
				}
			}
		}

		myNewNeighbors.remove(myServentInfo.getId());
		AppConfig.myServentInfo.setNeighbors(myNewNeighbors.stream().toList());
	}
}
