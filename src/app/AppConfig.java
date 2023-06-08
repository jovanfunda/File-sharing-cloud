package app;

import servent.message.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AppConfig {

	public static ServentInfo myServentInfo;
	
	public static List<ServentInfo> serventInfoList = new ArrayList<>();

	private static final ServentInfo bootstrapNode = new ServentInfo("localhost", -1, 1000, new ArrayList<>());

	public static Map<Integer, List<String>> serventFiles = new HashMap<>();

	public static final Set<Message> receivedMessages = Collections.newSetFromMap(new ConcurrentHashMap<Message, Boolean>());

	public static boolean gotPong = false;

	public static void timestampedStandardPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.out.println(timeFormat.format(now) + " - " + message);
	}

	public static void timestampedErrorPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.err.println(timeFormat.format(now) + " - " + message);
	}

	public static ServentInfo getInfoById(int id) {
		for(ServentInfo s : serventInfoList) {
			if(s.getId() == id) {
				return s;
			}
		}
		return null;
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
					} else {
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

	public static ServentInfo nextNode(ServentInfo servent) {
		int id = -1;
		for(int i = 0; i < serventInfoList.size(); i++) {
			if(serventInfoList.get(i) == servent) {
				id = i;
				break;
			}
		}
		if(id == -1) {
			timestampedErrorPrint("Error in nextNode app config");
		}
		int nextNodeId = (id+1) % serventInfoList.size();
		return serventInfoList.get(nextNodeId);
	}

	public static ServentInfo previousNode(ServentInfo servent) {
		int id = -1;
		for(int i = 0; i < serventInfoList.size(); i++) {
			if(serventInfoList.get(i) == servent) {
				id = i;
				break;
			}
		}
		if(id == -1) {
			timestampedErrorPrint("Error in previousNode app config");
		}
		int previousNodeId = (id-1);
		return previousNodeId >= 0 ? serventInfoList.get(id-1) : serventInfoList.get(serventInfoList.size()-1);
	}
}
