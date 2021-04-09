package miao.server;

import java.io.File;

public class Main {
	public static void main(String[] args) {
		Utility u = new Utility();
		for(int i = 1; i<=u.files.size(); i++) {
			u.findFile(i);
		}
		new Thread(new ClientReqReceiver(u)).start();
		for(int i = 1; i <= u.files.size(); i++) {
			new Thread(new ThreadQueueProcess(u, i)).start();
			System.out.println("start queque for file #"+i);
		}
	}

}
