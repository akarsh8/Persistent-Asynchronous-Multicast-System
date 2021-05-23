package p3.participant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParticipantCmd {

	DataInputStream instream;
	DataOutputStream outstream;
	
	public ParticipantCmd(DataInputStream in, DataOutputStream out) {
		this.instream = in;
		this.outstream = out;
	} //Constructor
	
	private void write(String str) {
		try {
			outstream.writeUTF(str);
		} catch(IOException e) {
			e.printStackTrace();
		} //catch
	} //write
	
	public void register(String portNum) {
		write("register");
		write(portNum);
		
		Participant.conn = true;
	} //register
	
	public void deregister() {
		System.out.println("Participant Status: Deregistered");
		
		write("deregister");
		
		Participant.conn = false;
	} //deregister
	
	public void disconnect() {
		write("disconnect");
		
		Participant.conn = false;
	} //disconnect
	
	public void reconnect(String newPortNum) {
		write("reconnect");
		write(newPortNum);
		
		Participant.conn = true;
	} //reconnect
	
	public void msend(String msg) {
		try {
			write("msend");
			write(msg);
		} catch(Exception e) {
			e.printStackTrace();
		}
	} //msend
	
}
