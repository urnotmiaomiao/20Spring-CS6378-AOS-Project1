package miao.server;

public class Node {
	private int id = -1;
	private String hostname = "";
	private int uPort = 9000;
	private int tPort = 3000;
	
	public Node() {}

	public Node(String hostname) {
		this.hostname = hostname;
	}
	
	public Node(String hostname, int uPort) {
		this.hostname = hostname;
		this.uPort = uPort;
	}
	
	public Node(String hostname, int uPort, int tPort) {
		this.hostname = hostname;
		this.uPort = uPort;
		this.tPort = tPort;
	}
	
	public String toString() {
		return ""+id+" "+hostname;
	}
//	id
	public int getID() {
		return id;
	}
	
	public void setID(int nodeID) {
		this.id = nodeID;
	}
	
//	ip
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
//	UDP port
	public int getUDPPort() {
		return uPort;
	}
	
	public void setUDPPort(int uport) {
		this.uPort = uport;
	}

//	TCP port
	public int getTCPPort() {
		return tPort;
	}
	
	public void setTCPPort(int tport) {
		this.tPort = tport;
	}
}
