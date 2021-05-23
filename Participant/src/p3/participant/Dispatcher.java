package p3.participant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Dispatcher implements Runnable {
	
	public final String SUCCESS = "Successful";
	public final String FAIL = "Failure";
	
	int pID;
	String conn_data;
	int portNum;
	DataInputStream instream;
	DataOutputStream outstream;
	String log;
	ParticipantCmd cmd;
	Receiver rThread;
	
	public Dispatcher(int id, String cd, String lg) {
		this.pID = id;
		this.conn_data = cd;
		this.log = lg;
	} //Constructor
	
	private String read() {
		String str = null;
		
		try {
			str = instream.readUTF();
		} catch(IOException e) {
			e.printStackTrace();
		} //catch
		
		return str;
	} //read
	
	private void write(String str) {
		try {
			outstream.writeUTF(str);
		} catch(IOException e) {
			e.printStackTrace();
		} //catch
	} //write
	
	private ArrayList<String> parseString(String str) {
		ArrayList<String> list = new ArrayList<>();
		
		try {
			list.add((str.split(" ", 2)[0]).trim());
			if(str.split(" ", 2).length > 1)
				list.add((str.split(" ", 2)[1]).trim());
		} catch(Exception e) {
			e.printStackTrace();
		} //catch
		
		return list;
	} //parseString
	
	private void makeReceiver(int pn, String lg) {
		try {
			rThread = new Receiver(pn, lg);
			new Thread(rThread).start();
		} catch(Exception e) {
			System.out.println("Participant Status: Error in read - " + e.getMessage());
		} //catch
	} //makeReceiver
	
	private boolean dispatch(String in) {
		ArrayList<String> command = parseString(in);
		String stat;
		
		try {
			
			switch(command.get(0)) {
			
			case "register":
				if(!Participant.conn) {
					
				} //if
				else
					System.out.println("Participant Status: Already registered");
				
				cmd.register(command.get(1));
				stat = read();
				
				if(stat.equals(SUCCESS)) {
					makeReceiver(Integer.parseInt(command.get(1)), log);
					Thread.sleep(1000);
					write("listener configured");
				} //if
				else {
					System.out.println("Participant Status: Already registered");
					return false;
				}
				break;
			
			case "deregister":
				cmd.deregister();
				stat = read();
				
				if(stat.equals(SUCCESS)) {
					rThread.disconnectReceiver();
					return false;
				} //if
				else
					System.out.println("Participant Status: Cannot deregister");
				break;
				
			case "disconnect":
				if(Participant.conn) {
					cmd.disconnect();
					rThread.disconnectReceiver();
					Thread.sleep(500);
					stat = read();
					
					if(stat.equals(FAIL))
						System.out.println("Participant Status: Cannot disconnect");
				} //if
				else
					System.out.println("Participant Status: Already disconnected");
				break;
			
			case "reconnect":
				if(!Participant.conn) {
					makeReceiver(Integer.parseInt(command.get(1)), log);
					Thread.sleep(500);
					cmd.reconnect(command.get(1));
					stat = read();
					
					if(stat.equals(FAIL))
						System.out.println("Participant Status: Cannot reconnect");
				} //if
				else
					System.out.println("Participant Status: Already connected");
				break;
			
			case "msend":
				if(Participant.conn) {
					cmd.msend(command.get(1));
					stat = read();
					
					if(stat.equals(FAIL))
						System.out.println("Participant Status: Cannot msend");
				} //if
				else
					System.out.println("Participant Status: Msend incomplete, connection required");
				break;
				
			default:
				System.out.println("Participant Status: Invalid command");
			
			} //switch
			
		} catch(Exception e) {
			System.out.println("Participant Status: Error in dispatch - " + e.getMessage());
		} //catch
		
		return true;
	} //dispatch
	
	public void run() {
		try {
			
			String param[] = conn_data.split(":");
			Socket parti_sock = new Socket(param[0], Integer.parseInt(param[1]));
			instream = new DataInputStream(parti_sock.getInputStream());
			outstream = new DataOutputStream(parti_sock.getOutputStream());
			
			System.out.println(read());
			write(String.valueOf(pID));
			
			Scanner scan = new Scanner(System.in);
			String in_cmd;
			boolean pStat = true;
			cmd = new ParticipantCmd(instream, outstream);
			
			while(pStat) {
				Thread.sleep(1500);
				System.out.print("Participant-" + pID + ">> ");
				in_cmd = scan.nextLine();
				pStat = dispatch(in_cmd);
			} //while
			
			return;
			
		} catch(Exception e) {
			System.out.println("Participant Status: Error in making dispatcher" + e.getMessage());
		} //catch
	} //run

}
