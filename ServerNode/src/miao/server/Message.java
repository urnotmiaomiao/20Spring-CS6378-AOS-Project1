package miao.server;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Comparable<Message>, Serializable {
	
	private boolean isSent = false;
	
	// Request's sequence# generated in client
	private int seqNo = -1;;
	
	private int fileNo = -1;
	
	private int	timestamp = -1;

	// which client this request came from
	private String client = "";
	
	// which server this request came from
	private int server = -1;
	
	public Message(Utility u) {
		 timestamp = u.getTimestamp();
//		 server = u.getMyAddr().getID();
	}
	
	public String toString() {
		return "t"+timestamp+":c"+client+":n"+seqNo+":f"+fileNo+":s"+server;
	}

	public String getKey() {
		return getClient()+"-"+getSeqNo();
	}
	
	// isSent
	public void setIsSent(boolean is) {
		this.isSent = is;
	}
	
	public boolean getIsSent() {
		return this.isSent;
	}
	
	// seqNo
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}	
	
	public int getSeqNo() {
		return seqNo;
	}
	
	// fileNo
	public void setFileNo(int fileNo) {
		this.fileNo = fileNo;
	}
	
	public int getFileNo() {
		return fileNo;
	}
	
	// timestamp
	public void setTimestamp(int ts) {
		this.timestamp = ts;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	// client
	public void setClient(String c) {
		this.client = c;
	}
	
	public String getClient() {
		return client;
	}

	// server
	public void setServer(int s) {
		this.server = s;
	}
	
	public int getServer() {
		return this.server;
	}

	public int getClientNum() {
		return Integer.valueOf(client.split("dc")[1]);
	}
	
	@Override
	public int compareTo(Message msg) {
		if(this.getTimestamp() == msg.getTimestamp()) {
			return this.getServer() - msg.getServer();
		}
		return this.getTimestamp() - msg.getTimestamp();
	}
}
