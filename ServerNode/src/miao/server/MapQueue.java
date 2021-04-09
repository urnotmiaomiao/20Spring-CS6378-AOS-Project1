package miao.server;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class MapQueue  {
	public BlockingQueue<RequestMsg> q;
	public HashMap <String, CommandMsg> m;
	
	public MapQueue(BlockingQueue<RequestMsg> q) {
		this.q = q;
	}
	
	public MapQueue(HashMap<String, CommandMsg> m) {
		this.m = m;
	}
}
