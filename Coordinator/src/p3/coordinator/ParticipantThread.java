package p3.coordinator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ParticipantThread implements Runnable{
	
	public final String SUCCESS = "Successful";
	public final String FAIL = "Failure";
	public final String REGISTERED = "Registered";
	public final String DISCONNECTED = "Disconnected";
	
	DataInputStream instream;
	DataOutputStream outstream;
	InetAddress parti_addr;
	CoordinatorCmd coord_cmd;
	
	public ParticipantThread(Socket coord_sock) throws IOException {
		instream = new DataInputStream(coord_sock.getInputStream());
		outstream = new DataOutputStream(coord_sock.getOutputStream());
		parti_addr = coord_sock.getInetAddress();
	} //Constructor
	
	private void write(String str) {
		try {
			outstream.writeUTF(str);
		} catch(IOException e) {
			System.out.println("Coordinator Status: Error in write - " + e.getMessage());
		} //catch
	} //write
	
	private String read() {
		String str = null;
		
		try {
			str = instream.readUTF();
		} catch(IOException e) {
			System.out.println("Coordinator Status: Error in read - " + e.getMessage());
		} //catch
		
		return str;
	} //read
	
	public void run() {
		try {
			
			write("Coordinator Status: Connection established");
			
			String parti_data = read();
			
			ParticipantSet parti = new ParticipantSet(Integer.parseInt(parti_data), parti_addr, null);
			coord_cmd = new CoordinatorCmd(parti, instream, outstream);
			
			System.out.println("Coordinator Status: Thread created - " + parti);
			
			while(true) {
				
				String cmd = read();
				
				if(!cmd.equals("deregister")) {
					
					switch(cmd) {
						
					case "register":
						if(parti.conn == null) {
							parti.setPortNum(Integer.parseInt(read()));
							if(coord_cmd.register()) {
								write(SUCCESS);
								String stat = read();
								if(stat.equals("listener configured"))
									parti.listener(parti.getPortNum());
							} //if
							else {
								write(FAIL);
								System.out.println("Coordinator Status: Duplicate registered ID");
							} //else
						} //if
						else {
							write(FAIL);
							System.out.println("Coordinator Status: Participant already exist");
						} //else
						break;
					
					case "reconnect":
						if(parti.conn != null && parti.conn.equals(DISCONNECTED)) {
							coord_cmd.reconnect();
							System.out.println("Coordinator Status: Post reconnect - " + parti.conn);
							write(SUCCESS);
						} //if
						else
							write(FAIL);
						break;
						
					case "disconnect":
						if(parti.conn != null && parti.conn.equals(REGISTERED)) {
							coord_cmd.disconnect();
							write(SUCCESS);
						} //if
						else
							write(FAIL);
						break;
					
					case "msend":
						if(parti.conn != null && parti.conn.equals(REGISTERED)) {
							coord_cmd.msend();
							write(SUCCESS);
						} //if
						else
							write(FAIL);
						break;
					
					default:
						System.out.println("Coordinator Status: Error invalid command - ");
					
					} //switch
					
				} //if
				else {
					
					if(parti.conn != null) {
						coord_cmd.deregister();
						write(SUCCESS);
						break;
					}
					else
						write(FAIL);
					
				} //else
				
			} //while
			
		} catch(IOException e1) {
			System.out.println("Coordinator Status: Coonection terminated with participant - " + e1.getMessage());
		} catch (Exception e2) {
			System.out.println("Coordinator Status: Error in running commands - " + e2.getMessage());
		} //catch
	} //run

}
