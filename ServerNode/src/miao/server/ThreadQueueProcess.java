package miao.server;

public class ThreadQueueProcess implements Runnable{
	private Utility u;
	private int fileNo;
	
	public ThreadQueueProcess(Utility u, int fileNo) {
		this.u = u;
		this.fileNo = fileNo;
	}
	
	@Override
	public void run() {
//		int peekKey = 0;
//		try {
//			Protocol.requestMsgList[fileNo-1].q.add(new RequestMsg(u));
//			peekKey = Protocol.requestMsgList[fileNo-1].q.peek().hashCode();
//			Protocol.requestMsgList[fileNo-1].q.take();
//		} catch (InterruptedException e) { e.printStackTrace();}
		
		while(true) {
			if(Protocol.requestMsgList[fileNo-1].q.peek()!=null) {
//				 && peekKey!=Protocol.requestMsgList[fileNo-1].q.peek().hashCode()
//				peekKey = Protocol.requestMsgList[fileNo-1].q.peek().hashCode();
				Protocol.processRequestMsg(u, Protocol.requestMsgList[fileNo-1].q.peek(), fileNo);
			}
		}
	}

}
