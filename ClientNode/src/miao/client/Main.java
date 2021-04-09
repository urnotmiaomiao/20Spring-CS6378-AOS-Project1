package miao.client;

public class Main {

	public static void main(String[] args) {
		Utility u = new Utility();
		System.out.println(u.createSendStr(1, 1));
		Request req = new Request(u);
		Thread reqThread = new Thread(req);
		reqThread.start();
		req.receiveResponse();
	}

}
