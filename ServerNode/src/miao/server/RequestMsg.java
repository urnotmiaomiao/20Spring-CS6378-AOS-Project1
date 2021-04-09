package miao.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class RequestMsg extends Message implements Serializable{

	// content needed to be appended
	private String content = "";
	private HashMap<Integer, Boolean> flags = new HashMap<Integer, Boolean>();

	public RequestMsg(Utility u) {
		super(u);
		for(int i = 1; i <= u.servers.size(); i++) {
			if(u.socketTONeighbors.containsKey(i)) {
				flags.put(Integer.valueOf(i), Boolean.FALSE);
			}
		}
	}
	
	// content
	public void setContent(String c) {
		this.content = c;
	}
	
	public String getContent() {
		return content;
	}
	
	// flags
	public void setFlag(int serverID, boolean f) {
		flags.replace(Integer.valueOf(serverID), Boolean.valueOf(f));
	}
	
	public boolean getFlags() {
		boolean f = true;
//		for(int i = 1; i <= flags.size(); i++) {
//			f = f && flags.get(Integer.valueOf(i)).booleanValue();
//		}
		Iterator<Entry<Integer, Boolean>> entries = flags.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Integer, Boolean> entry = entries.next();
			f = f && entry.getValue().booleanValue();
		}

		return f;
	}
}
