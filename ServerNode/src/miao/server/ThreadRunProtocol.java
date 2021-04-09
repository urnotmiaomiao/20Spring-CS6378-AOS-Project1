package miao.server;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ThreadRunProtocol implements Runnable{
	Utility u;
	int dstID;
	
	public ThreadRunProtocol(Utility u, int dstID) {
		this.u = u;
		this.dstID = dstID;
	}
	@Override
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(u.socketTONeighbors.get(dstID).getInputStream());
			Protocol.receiveServerMsg(u, ois, dstID);
		} catch (IOException e) { e.printStackTrace();}

	}

}
