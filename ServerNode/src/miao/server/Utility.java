package miao.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Utility {

	// private static final int TIMEOUT = 5000;
	private static final String PROPERTIES_FILE_NAME = "config.properties";
	
	private Node myAddress;
	public HashMap<Integer, String> files = new HashMap<Integer, String>();
	public HashMap<Integer, Node> servers = new HashMap<Integer, Node>();
	public HashMap<Integer, Socket> socketTONeighbors = new HashMap<Integer, Socket>();
	public HashMap<Integer, ObjectOutputStream> oStream = new HashMap<Integer, ObjectOutputStream>();
	
	private boolean socketListening = false;
	private AtomicInteger atomTimestamp = new AtomicInteger(0);
	
	public Utility() {
		try {
			// Get configuration from config.propoties.
			getConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Protocol.setMsgQueueList(this);
		
		// Create TCP connection between servers.
		createConnections();
	}
	
	public void getConfig() throws IOException {
		// String proFilePath = System.getProperty("user.dir") + "\\config\\"+ PROPERTIES_FILE_NAME;
		String proFilePath = System.getProperty("user.dir") + "/config/"+ PROPERTIES_FILE_NAME;
		System.out.println(proFilePath);
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
		ResourceBundle rb = new PropertyResourceBundle(inputStream);
		
		int tPort = Integer.parseInt(rb.getString("my_tcp"));
		int uPort = Integer.parseInt(rb.getString("my_udp"));
		
		String[] filesInfo = rb.getString("files").split(" ");
		int files_num = filesInfo.length;
		
		// get files' name and no
		if(files_num > 0 && files_num%2 == 0) {
			for(int i = 0; i < files_num; i+=2) {
				String fileName = filesInfo[i];
				files.put(Integer.valueOf(filesInfo[i+1]), fileName);
				System.out.print(files.get(Integer.valueOf(filesInfo[i+1]))+" ");
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
				server.setID(Integer.parseInt(serverNodes[i+1]));
				server.setTCPPort(tPort);
				server.setUDPPort(uPort);
				servers.put(server.getID(), server);
				System.out.println(server);
			}
		}else {
			System.out.println("Items in \"server nodes\" should be even.");
		}

		myAddress = new Node(InetAddress.getLocalHost().toString().split("\\.")[0]);
		for (int i=1; i<=servers.size(); i++) {
			String host = servers.get(i).getHostname();
			if( host.equals(this.myAddress.getHostname()) ) {
				this.myAddress.setID(servers.get(i).getID());
				System.out.println(servers.get(i).getID());
				break;
			}
		}
	}
	
	public void createConnections() {
		for (int i=1; i<=servers.size(); i++) {
			int sID = servers.get(i).getID();
			if ((myAddress.getID() < sID) && !(socketListening)) {
//				socketListen();
				socketListening = true;
				new Thread(new ThreadSocketListen(this)).start();
			}
			else if (myAddress.getID() > sID) {
				socketConnect(i);
//				new Thread(new ThreadSocketConnect(this, i)).start();
			}
		}
	}
	
//	Called in Class ThreadSocketListen @Thread
	public void socketListen() throws IOException {
		ServerSocket serverSock = new ServerSocket(this.myAddress.getTCPPort());  // Create a server socket service at port
		System.out.println( this.myAddress.getHostname() +" (" + this.myAddress.getID() + ")" + " server socket listening ("+ this.myAddress.getTCPPort() +")"+"...");
	
		while(true){
			Socket sock = serverSock.accept();             //    Wait for client connection
//			new Thread(new ThreadSocketWait(this, sock)).start();
			String dstHostName = sock.getInetAddress().getHostName().split("\\.")[0];
			int dstID = findDstID(dstHostName);
			
			System.out.println(getMyAddr().getID() + " - " + dstID + " channel created");
			
			try{
				// Save output Stream
				ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
				socketTONeighbors.put(dstID, sock);
				oStream.put(dstID, oos);
				
				// Run input stream
				new Thread(new ThreadRunProtocol(this, dstID)).start();        //    Start new thread to handle client connection
			}catch(IOException e){e.printStackTrace();} 
		}
	}
	
	public int findDstID(String  dstHostName) {
		int dstID = -1;
		for (int i=1; i<=servers.size(); i++) {
			String host = servers.get(i).getHostname();
			if( host.equals(dstHostName) ) {
				dstID = servers.get(i).getID();
				break;
			}
		}
		return dstID;
	}
	
	public void socketConnect(int serverNodeID) {
		boolean tryConnect = true;
		Node serverNode = servers.get(serverNodeID);
		String hostName = serverNode.getHostname();
		int port = serverNode.getTCPPort();
		while(tryConnect){
			try{
				InetAddress address = InetAddress.getByName(hostName);
				System.out.println (this.myAddress.getID() + " send connection request to " + serverNode.getID() + " at " + serverNode.getTCPPort());
				Socket clientSocket = new Socket(address, port);
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

				socketTONeighbors.put(serverNode.getID(), clientSocket);
				oStream.put(serverNode.getID(), oos);
				
				// Handle input stream in new thread
				new Thread(new ThreadRunProtocol(this, serverNode.getID())).start();  
				
				tryConnect = false;
			} catch (IOException e) {}
		}
	}
	
	public boolean findFile(int fileNo) {
		String filename = this.files.get(fileNo);
		File file = new File(System.getProperty("user.dir")+"//"+filename);
//		System.out.print("file: "+file);
//		System.out.print(", filename: "+filename);
//		System.out.println(", exists: "+file.exists());
		return file.exists();
	}
	
	public void appendFile(int fileNo, String content) {
		String filename = this.files.get(fileNo);
		File file = new File(System.getProperty("user.dir")+"//"+filename);
		try {
			FileOutputStream fos = new FileOutputStream(file,true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.append(content);
			osw.close();
		} catch (FileNotFoundException e) {e.printStackTrace();
		} catch (UnsupportedEncodingException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		}
	}
	
	public Node getMyAddr() {
		return myAddress;
	}
	
	public void setTimestamp(int ts) {
		this.atomTimestamp.set(ts);
	}
	
	public int getTimestamp() {
		return this.atomTimestamp.get();
	}
	
	public int increaseTimestamp() {
		return this.atomTimestamp.getAndIncrement();
	}
	
	public int handleTimestamp(int ts) {
		if(ts>this.getTimestamp()) {
			this.setTimestamp(ts);
		}
		return this.getTimestamp();
	}
	
	// server, socket, ostream
	public Node getServer(int i) {
		return servers.get(i);
	}
	
	public Socket getSocket(int i) {
		return socketTONeighbors.get(i);
	}
	
	public ObjectOutputStream getOStream(int i) {
		return oStream.get(i);
	}
}
