package p3.coordinator;

public class Message {

	String msg;
	Long time;
	
	public Message(String m, long t) {
		this.msg = m;
		this.time = t;
	} //Constructor
	
	public String getMsg() {
		return msg;
	} //getMsg
	
	public void setMsg(String m) {
		this.msg = m;
	} //setMsg
	
	public Long getTime() {
		return time;
	} //getTime
	
	public void setTime(Long t) {
		this.time = t;
	} //setTime
	
}
