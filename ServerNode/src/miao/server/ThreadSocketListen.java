package miao.server;

import java.io.IOException;

public class ThreadSocketListen implements Runnable {
	Utility u;
	
	public ThreadSocketListen(Utility u){
		this.u = u;
	}
	@Override
	public void run() {
		try {
			u.socketListen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
