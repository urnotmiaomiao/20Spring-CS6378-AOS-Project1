package miao.server;

public class ThreadSocketConnect implements Runnable{
	Utility u;
	int serverNodeID;
	
	public ThreadSocketConnect(Utility u, int serverNodeID) {
		this.u = u;
		this.serverNodeID = serverNodeID;
	}
	
	@Override
	public void run() {
		u.socketConnect(serverNodeID);
	}

}
