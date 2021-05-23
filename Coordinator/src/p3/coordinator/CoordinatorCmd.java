package p3.coordinator;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class CoordinatorCmd {

	DataInputStream instream;
	DataOutputStream outstream;
	DataOutputStream mstream;
	ParticipantSet parti;
	
	public CoordinatorCmd(ParticipantSet p, DataInputStream in, DataOutputStream out) {
		this.parti = p;
		this.instream = in;
		this.outstream = out;
	} //Constructor
	
	public boolean register() {
		try {
			parti.conn = "Registered";
			if(Coordinator.pSet.contains(parti))
				return false;
			
			Coordinator.pSet.add(parti);
			System.out.println("Coordinator Status: Participant registered - " + Coordinator.pSet);
		} catch(Exception e) {
			System.out.println("Coordinator Status: Error in register - " + e.getMessage());
		} //catch
		
		return true;		
	} //register
	
	public void deregister() {
		try {
			System.out.println("Coordinator Status: Participant(" + parti + ") deregistered");
			Coordinator.pSet.remove(parti);
			System.out.println("Registered participants:\n" + Coordinator.pSet);
		} catch(Exception e) {
			System.out.println("Coordinator Status: Error in deregister - " + e.getMessage());
		} //catch
	} //deregister
	
	public void reconnect() {
		try {
			int portNum = Integer.parseInt(instream.readUTF());
			
			try {
				if(parti.listener(portNum));
			} catch(Exception e) {
				System.out.println("Coordinator Status: Port connecntion lost - " + e.getMessage());
			} //catch
			
			parti.conn = "Registered";
			Thread.sleep(300);
			parti.flush();
		} catch(Exception e) {
			System.out.println("Coordinator Status: Error in reconnect - " + e.getMessage());
		}
	} //reconnect
	
	public void disconnect() {
		try {
			parti.conn = "Disconnected";
			System.out.println("Coordinator Status: Participant(" + parti + ") disconnected");
		} catch(Exception e) {
			System.out.println("Coordinator Status: Error in disconeect" + e.getMessage());
		}	
	} //disconnect
	
	public synchronized void msend() {
		try {
			String str = instream.readUTF();
			
			for(ParticipantSet p : Coordinator.pSet) {
				if(p.conn.equals("Registered") && p.buff.isEmpty())
					p.getOutstream().writeUTF(str);
				else if(p.conn.equals("Registered") && !p.buff.isEmpty()) {
					p.flush();
					p.getOutstream().writeUTF(str);
				} //else if
				else {
					Message msg = new Message(str, System.currentTimeMillis());
					p.buff.add(msg);
				} //else
			} //for
		} catch(Exception e) {
			System.out.println("Coordinator Status: Error in msend" + e.getMessage());
		} //catch
	} //msend
}
