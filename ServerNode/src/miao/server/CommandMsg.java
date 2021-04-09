package miao.server;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CommandMsg extends Message implements Serializable {
	private boolean isSuccess = false;
	private String type = "";
	
	public CommandMsg(Utility u, RequestMsg rMsg) {
		super(u);
		super.setTimestamp(rMsg.getTimestamp());
		super.setFileNo(rMsg.getFileNo());
		super.setSeqNo(rMsg.getSeqNo());
		super.setServer(rMsg.getServer());
		super.setClient(rMsg.getClient());
	}
	
	public String getKey() {
		return super.getKey()+"-"+getServer();
	}
	
	public void setSuccess(boolean s) {
		this.isSuccess = s;
	}
	
	public boolean getSuccess() {
		return this.isSuccess;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
