package app;

import java.io.Serializable;
import java.util.List;

/**
 * This is an immutable class that holds all the information for a servent.
 *
 * @author bmilojkovic
 */
public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;
	private int id;
	private final String ipAddress;
	private final int listenerPort;
	private List<Integer> neighbors;
	
	public ServentInfo(String ipAddress, int id, int listenerPort, List<Integer> neighbors) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;
		this.id = id;
		this.neighbors = neighbors;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public List<Integer> getNeighbors() {
		return neighbors;
	}

	public void addNeighbor(int serventId) {
		neighbors.add(serventId);
	}

	public void removeNeighbor(int serventId) {
		neighbors.remove(serventId);
	}

	public void setNeighbors(List<Integer> newNeighbors) {
		this.neighbors = newNeighbors;
	}
	
	@Override
	public String toString() {
		return "[" + id + "|" + ipAddress + "|" + listenerPort + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ServentInfo other) {
			return id == other.id && ipAddress.equals(other.ipAddress)
					&& listenerPort == other.listenerPort;
		}
		return false;
	}
}
