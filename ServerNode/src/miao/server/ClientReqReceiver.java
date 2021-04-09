package miao.server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

// udp request with client nodes
public class ClientReqReceiver implements Runnable {
	public Utility u;
	
	public ClientReqReceiver(Utility u) {
		this.u = u;
	}
	
	@SuppressWarnings("resource")
	public void receiveReqfromClient() {
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(u.getMyAddr().getUDPPort());
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] buf = new byte[10000];
		DatagramPacket dp_receive = new DatagramPacket(buf, buf.length);

		boolean receivedResponse = false;
		
		while(true) {
			try {
				ds.receive(dp_receive);
				receivedResponse = true;
			} catch (IOException e) {
				e.printStackTrace();
			} 

			if(receivedResponse) {
				String str_receive =new String(dp_receive.getData(), 0, dp_receive.getLength());
				// Create RequestMessage 
				String[] msgInfo = str_receive.split("-");
				
				u.increaseTimestamp();
				RequestMsg msg = new RequestMsg(u);
				
				msg.setServer(u.getMyAddr().getID());
				msg.setSeqNo(Integer.parseInt(msgInfo[0]));
				msg.setClient(msgInfo[1]);
				msg.setFileNo(Integer.parseInt(msgInfo[2]));
				
				if(msgInfo.length == 4) { msg.setContent(msgInfo[3]);}
				
				// Add new RequestMessage to Queue
				
				Protocol.requestMsgList[msg.getFileNo()-1].q.add(msg);
				System.out.println("server "+u.getMyAddr().getID() + " receives: ["+msg+"] from client "+msgInfo[1]);
//				System.out.println(msg.getFileNo()+" Peek: "+Protocol.requestMsgList[msg.getFileNo()-1].q.peek());
				dp_receive.setLength(buf.length);
				receivedResponse = false;
				
//				new Thread(new ClientResGenerator(u, true, msg)).start();
			}
		}
	}
	
	@Override
	public void run() {
		while(true) {
			receiveReqfromClient();
		}
		
	}
}
