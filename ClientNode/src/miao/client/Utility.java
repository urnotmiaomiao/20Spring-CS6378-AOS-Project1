package miao.client;

import java.io.*;
import java.net.*;
import java.util.*;

public class Utility {

	// private static final int TIMEOUT = 5000;
	private static final String PROPERTIES_FILE_NAME = "config.properties";
		
	private Node myAddress;
	
	public HashMap<Integer, String> files = new HashMap<Integer, String>();
	public HashMap<Integer, Node> servers = new HashMap<Integer, Node>();
	
	private String content = "";
	
	public Utility() {
		try {
			getConfig();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getConfig() throws IOException {
//		String proFilePath = System.getProperty("user.dir") + "\\config\\"+ PROPERTIES_FILE_NAME;
		String proFilePath = System.getProperty("user.dir") + "/config/"+ PROPERTIES_FILE_NAME;
		System.out.println(proFilePath);
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
		ResourceBundle rb = new PropertyResourceBundle(inputStream);
		
		content = rb.getString("content");
		String[] filesInfo = rb.getString("files").split(" ");
		int files_num = filesInfo.length;
		
		// get files' name and no
		if(files_num > 0 && files_num%2 == 0) {
			for(int i = 0; i < files_num; i+=2) {
				files.put(Integer.parseInt(filesInfo[i+1]), filesInfo[i]);
				System.out.print(files.get(i/2+1)+" ");
			}
		}else {
			System.out.println("Items in \"files\" should be even.");
		}
		
		// get hostnames and port numbers of servers
		String[] serverNodes = rb.getString("server_nodes").split(" ");
		int server_nodes_length = serverNodes.length;
			
		if(server_nodes_length > 0 && server_nodes_length%2 == 0) {
			for(int i = 0; i < server_nodes_length; i+=2) {
				Node server = new Node(serverNodes[i]);
				server.setNodeID(Integer.parseInt(serverNodes[i+1]));
				servers.put(server.getNodeID(), server);
				System.out.print(server+" ");
			}
		}else {
			System.out.println("Items in \"server nodes\" should be even.");
		}
		
		myAddress = new Node(InetAddress.getLocalHost().toString().split("\\.")[0]);
	}

	public int pickServer() {
		int randomNo = (int)(Math.random()*this.servers.size())+1;
		return randomNo;
	}
	
	public int pickFileNo() {
		return (int)(Math.random()*files.size())+1;
//		return 2;
	}
	
	public String getContent() {
		return content;
	}
	
	//Create the package message to send to a server.
	public String createSendStr(int seqNo, int fileNo) {
		String str_send = ""+seqNo;	// 0: sequenceNo
		str_send += "-" + this.myAddress.getHostname(); // 1: client hostname
		str_send += "-" + fileNo; // 2: fileNo
		str_send += "-" + this.getContent()+"_"+this.myAddress.getHostname()+"_"+seqNo+"\n"; // 3: content
		return str_send;
	}
	
	public Node getMyAddr() {
		return myAddress;
	}
	
	public Node getServer(int serverID) {
		return servers.get(serverID);
	}
	
}
