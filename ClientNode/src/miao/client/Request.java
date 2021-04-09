package miao.client;

import java.io.IOException;
import java.net.*;

public class Request implements Runnable {
	
	Utility u;
	
	public Request(Utility u) {
		this.u = u;
	}
	
	// construct a request packet and send it
	public void generateRequest(int seqNo) throws IOException{
		int serverID = u.pickServer();
		Node serverAddr = u.getServer(serverID); // pick a server node randomly
		
		DatagramSocket ds = new DatagramSocket();

		int fileNo = u.pickFileNo();
		String str_send = u.createSendStr(seqNo, fileNo);
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), InetAddress.getByName(serverAddr.getHostname()), serverAddr.getUDPPort());
		
		ds.send(dp_send);
		ds.close();
		System.out.println("Client requests: \""+str_send+"\" to server "+(serverID)+" for file #"+fileNo);
	}
	
	@SuppressWarnings("resource")
	public void receiveResponse() {
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
				String str_receive ="client receives " +new String(dp_receive.getData(), 0, dp_receive.getLength()) + " from " + dp_receive.getAddress().getHostName() + ":" + dp_receive.getPort();
				System.out.println(str_receive);
				dp_receive.setLength(buf.length);
				receivedResponse = false;
			}
		}
	}
	
	@Override
	public void run() {
		int seqNo = 1;
		while(seqNo > 0){
//		while(seqNo <= 100) {
			try {
				generateRequest(seqNo++);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
//				Thread.sleep(1000*(int)(Math.random());
				Thread.sleep(1000*((int)(Math.random()*5)+1));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("gracefully shutdown");
	}
}
