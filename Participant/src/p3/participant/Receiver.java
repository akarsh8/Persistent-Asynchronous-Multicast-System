package p3.participant;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable {
	
	int portNum;
	Socket mSock;
	DataInputStream instream;
	String log;
	ServerSocket ss;
	
	public Receiver(int pn, String lg) {
		this.portNum = pn;
		this.log = lg;
	} //Constructor
	
	private void writeLog(String str) {
		try {
			
			PrintWriter pw = null;
			FileWriter fw = null;
			File f = new File(log);
			if(!f.exists())
				f.createNewFile();
			
			fw = new FileWriter(f.getAbsoluteFile(), true);
			pw = new PrintWriter(fw);
			pw.println(str);
			
			pw.close();
			fw.close();
			
		} catch(Exception e) {
			System.out.println("Participant Status: Error in creating log file - " + e.getMessage());
		} //catch	
	} //writeLog
	
	public void disconnectReceiver() {
		try {
			
			if(instream != null)
				instream.close();
			if(mSock != null)
				mSock.close();
			if(ss != null)
				ss.close();
			
		} catch(IOException e) {
			System.out.println("Participant Status: Error disconnecting receiver" + e.getMessage());
		}
	} //disconnectReceiver
	
	public void run() {
		try {
			
			ss = new ServerSocket(portNum);
			mSock = ss.accept();
			instream = new DataInputStream(mSock.getInputStream());
			
			System.out.println("Participant Status: Listening on port - " + portNum);
			
			while(true) {
				
				if(instream != null) {
					String msg = instream.readUTF();
					System.out.println("Message: " + msg);
					writeLog(msg);
				} //if
			} //while
			
		} catch(Exception e) {
			System.out.println("Participant Status: Port disconnected - " + e.getMessage());
		} finally {
			disconnectReceiver();
		}
	} //run
	
}
