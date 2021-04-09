package miao.server;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Protocol {
//	public static HashMap<Integer, BlockingQueue<RequestMsg>> requestMsgFile;
//	public static HashMap<Integer, BlockingQueue<CommandMsg>> commandMsgFile;
	
	public static MapQueue[] requestMsgList;
	public static MapQueue[] commandMsgListREL;
	public static MapQueue[] commandMsgListACK;
	public static void setMsgQueueList(Utility u) {
		requestMsgList = new MapQueue[u.files.size()];
		commandMsgListREL = new MapQueue[u.files.size()];
		commandMsgListACK = new MapQueue[u.files.size()];
		
		for(int i = 1; i <= u.files.size(); i++) {
			
			BlockingQueue<RequestMsg> reqMsgQueue = new PriorityBlockingQueue<RequestMsg>();
			requestMsgList[i-1] = new MapQueue(reqMsgQueue);
//			Protocol.requestMsgFile.put(Integer.valueOf(i), reqMsgQueue);
			
//			
//			BlockingQueue<CommandMsg> cmdMsgQueue = new PriorityBlockingQueue<CommandMsg>();
			HashMap<String, CommandMsg> cmdMsgHM = new HashMap<String, CommandMsg>();
			commandMsgListREL[i-1] = new MapQueue(cmdMsgHM);
			commandMsgListACK[i-1] = new MapQueue(cmdMsgHM);
//			Protocol.commandMsgFile.put(Integer.valueOf(i), cmdMsgQueue);
		}
	}
	
	public static void receiveServerMsg(Utility u, ObjectInputStream ois, int dstID) {
		
		while(true) {
			try {
				Message message = (Message) ois.readObject();
				u.handleTimestamp(message.getTimestamp());
				// Receive Command message
				if(message instanceof CommandMsg){
					System.out.println("server "+u.getMyAddr().getID()+" receives: CMD ["+message+"] from server "+dstID);
					handleCommandMsg(u, (CommandMsg)message);
				}
				// Receive Request message
				if(message instanceof RequestMsg){
					System.out.println("server "+u.getMyAddr().getID()+" receives: REQ ["+message+"] from server "+dstID);
					handleRequestMsg(u, (RequestMsg)message);
				}
				
//				System.out.println(message.getFileNo()+" Peek: "+Protocol.requestMsgList[message.getFileNo()-1].q.peek());
			} catch (ClassNotFoundException | IOException e) { e.printStackTrace();}
//				message.setIsSent(false);
		}
	}
	
	// RequestMsg: Request message from other server
	// put in Queue
	public static void handleRequestMsg(Utility u, RequestMsg message) {
		Protocol.requestMsgList[message.getFileNo()-1].q.add(message);
	}
	
	// CommandMsg: 
	public static void handleCommandMsg(Utility u, CommandMsg message) {
		if(message.getType().equals("REL")){
			Protocol.commandMsgListREL[message.getFileNo()-1].m.put(message.getKey(), message);
		}else {
			Protocol.commandMsgListACK[message.getFileNo()-1].m.put(message.getKey(), message);
		}
	}
	
	// Get the first message in queue 
	// If the message is from MyAddress
	// - check if get all ACKMessages from other servers
	// 		- then process it
	// - else 
	//		- check if required file exists	
	//			- send ACKMessage to original server
	// 		- wait for commandMsg 
	// 			- Release or Delete 
	public static void processRequestMsg(Utility u, RequestMsg message, int fileNo) {
		if(message.getServer() == u.getMyAddr().getID()) {
			if(message.getIsSent()) {
				
			}else {
				
			}
			processOwnRequest(u, message, fileNo);
			
		}else {
			processOtherRequest(u, message, fileNo);
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) { e.printStackTrace();}
	}

	public static void processOtherRequest(Utility u, RequestMsg message, int fileNo) {
		if(!message.getIsSent()) {
			CommandMsg cMsg = new CommandMsg(u, message);
			// ack Message
			cMsg.setType("ACK");
			cMsg.setSuccess(u.findFile(message.getFileNo()));
			cMsg.setServer(u.getMyAddr().getID());
			sendMsg(u, message.getServer(), cMsg);
			message.setIsSent(true);
		}else if(commandMsgListREL[message.getFileNo()-1].m.containsKey(message.getKey()+"-"+message.getServer())) {
			try {
				// append if message.getSuccess() is true
				System.out.println("Delete ["+Protocol.requestMsgList[fileNo-1].q.take()+"]");
			} catch (InterruptedException e) { e.printStackTrace();}
		}
	}

	public static void processOwnRequest(Utility u, RequestMsg message, int fileNo) {
		
		CommandMsg cMsg = new CommandMsg(u, message);
		cMsg.setServer(u.getMyAddr().getID());
		cMsg.setType("REL");
		
		if(!message.getIsSent()) {
			if(!u.findFile(message.getFileNo())) {
				try {
					System.out.println("Delete ["+Protocol.requestMsgList[fileNo-1].q.take()+"]");
				} catch (InterruptedException e) { e.printStackTrace();}
				new Thread(new ClientResGenerator(u, false, message)).start();
				
			}else {
				sendMsgToServers(u, message);
			}
		}else {
			boolean checked = false;
			if(!commandMsgListACK[message.getFileNo()-1].m.isEmpty()) {
				for(int i = 1; i<=u.servers.size(); i++) {
					if(commandMsgListACK[message.getFileNo()-1].m.containsKey(message.getKey()+"-"+i)) {
						CommandMsg c = commandMsgListACK[message.getFileNo()-1].m.get(message.getKey()+"-"+i);
						commandMsgListACK[message.getFileNo()-1].m.remove(message.getKey()+"-"+i);
						if(!c.getSuccess()) {
							cMsg.setSuccess(false);
							checked = true;
							break;
						}
						message.setFlag(c.getServer(), true);
					}
				}
				if(message.getFlags()) {
					checked = true;
					cMsg.setSuccess(true);
				}
			}
				
			if(checked) {
				try {
					System.out.println("Delete ["+Protocol.requestMsgList[fileNo-1].q.take()+"]");
				} catch (InterruptedException e) { e.printStackTrace();}
				// do append
				u.appendFile(message.getFileNo(), message.getTimestamp()+message.getContent());
				// release Message
				sendMsgToServers(u,cMsg);
				
				// Send Response to client
				new Thread(new ClientResGenerator(u, cMsg.getSuccess(), message)).start();
			}
		}
	}
	
	public static void sendMsg(Utility u, int dstID, RequestMsg msg) {
		try {
			ObjectOutputStream oos = u.oStream.get(dstID);
			u.increaseTimestamp();
			oos.writeObject(msg);
			oos.flush();
			System.out.println("server "+u.getMyAddr().getID()+" sends "+"REQ"+" ["+msg+"] to server "+dstID);		
		}catch (IOException e) {System.out.println(e);}
	}
	
	public static void sendMsg(Utility u, int dstID, CommandMsg msg) {
		try {
			ObjectOutputStream oos = u.oStream.get(dstID);
			
			u.increaseTimestamp();
			msg = (CommandMsg) msg;
			oos.writeObject(msg);
			oos.flush();
			System.out.println("server "+u.getMyAddr().getID()+" sends "+msg.getType()+" ["+msg+"] to server "+dstID);
		
			
		}catch (IOException e) {System.out.println(e);}
	}
	
	public static void sendMsgToServers(Utility u, RequestMsg message) {
		// Send Request message to other servers.
		
		if(!message.getIsSent()) {
			for(int i = 1; i <= u.servers.size(); i++) {
				if(i != u.getMyAddr().getID()) {
					sendMsg(u, i, message);
				}
			}
		}
		message.setIsSent(true);
	}
	
	public static void sendMsgToServers(Utility u, CommandMsg message) {
		// Send Request message to other servers.
		
		if(!message.getIsSent()) {
			for(int i = 1; i <= u.servers.size(); i++) {
				if(i != u.getMyAddr().getID()) {
					sendMsg(u, i, message);
				}
			}
		}
		message.setIsSent(true);
	}
}
