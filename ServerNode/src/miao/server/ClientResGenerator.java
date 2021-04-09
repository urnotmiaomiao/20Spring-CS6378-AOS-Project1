package miao.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientResGenerator implements Runnable{
	private Utility u;
	boolean isSuccess;
	Message msg;
	int uPort = 9000;
	
	public ClientResGenerator(Utility u, boolean isSuccess, Message msg){
		this.u = u;
		this.isSuccess = isSuccess;
		this.msg = msg;
	}
	
	// construct a request packet and send it
	public void sendResponse(boolean isSuccess) throws IOException{
		
		DatagramSocket ds = new DatagramSocket();
		
		String str_send = msg.getClient()+":s"+msg.getSeqNo()+":f"+msg.getFileNo()
							+(isSuccess?" successed":" Failed");
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), 
													InetAddress.getByName(msg.getClient()), 
													uPort);
		
		ds.send(dp_send);
		ds.close();
		u.increaseTimestamp();
		System.out.println("Send: "+str_send);
	}
	
	@Override
	public void run() {
		try {
			sendResponse(isSuccess);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
