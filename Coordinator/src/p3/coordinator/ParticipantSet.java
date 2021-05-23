package p3.coordinator;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class ParticipantSet {

	int id;
	int portNum;
	InetAddress ip;
	String conn;
	LinkedList<Message> buff;
	DataOutputStream outstream;
	Socket parti_sock;
	
	public ParticipantSet(int id, InetAddress ip, String conn) {
		this.id = id;
		this.ip = ip;
		this.conn = conn;
		this.buff = new LinkedList<Message>();
	} //Constructor
	
	public int getPortNum() {
		return portNum;
	} //getPortNum
	
	public void setPortNum(int pn) {
		this.portNum = pn;
	} //setPortNum
	
	public DataOutputStream getOutstream() {
		return outstream;
	} //getOutstream
	
	public void setOutstream(DataOutputStream out) {
		this.outstream = out;
	} //setOutstream
	
	public void flush() {
		try {
			while(!buff.isEmpty()) {
				Message m = buff.poll();
				if((System.currentTimeMillis() - m.time) / 1000 <= Coordinator.th_time)
					outstream.writeUTF(m.msg);
			} //while
		} catch(Exception e) {
			System.out.println("Coordinator Status: Error in message - " + e.getMessage());
		} //catch
	} //flush
	
	public boolean listener(int newPortNum) throws Exception {
		this.portNum = newPortNum;
		parti_sock = new Socket(ip, portNum);
		outstream = new DataOutputStream(parti_sock.getOutputStream());
		
		return true;
	} //listener
	
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null)
			return false;
		if(getClass() != o.getClass())
			return false;
		
		ParticipantSet temp = (ParticipantSet) o;
		if(id != temp.id)
			return false;
		
		return true;
	} //equals
	
	@Override
	public int hashCode() {
		final int x = 31;
		int y = 1;
		y = x * y + id;
		
		return y;
	} //hashCode
	
	@Override
	public String toString() {
		return "[" + id + "]" + "(" + conn +")";
	} //toString
}
