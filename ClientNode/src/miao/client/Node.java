package miao.client;

public class Node {
	private int id = -1;
	private String hostname = "";
	private int uPort = 9000;
	
	public Node() {}
	public Node(String hostname) {
		this.hostname = hostname;
	}
	public Node(String hostname, int uPort) {
		this.hostname = hostname;
		this.uPort = uPort;
	}
	
	public String toString() {
		return hostname;
	}
	
	public int getNodeID() {
		return id;
	}

	public void setNodeID(int nodeID) {
		this.id = nodeID;
	}
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	
	public int getUDPPort() {
		return uPort;
	}
	
	public void setUDPPort(int p) {
		this.uPort = p;
	}
}
