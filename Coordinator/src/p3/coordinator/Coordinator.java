package p3.coordinator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;

public class Coordinator {
	
	static int th_time;
	static int coord_port;
	
	static HashSet<ParticipantSet> pSet = new HashSet<ParticipantSet>();
	
	private static ArrayList<Integer> getConfData(String conf) {		
		ArrayList<Integer> conf_data = new ArrayList<Integer>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(conf));
			String str = null;
			
			while((str = reader.readLine()) != null)
				conf_data.add(Integer.parseInt(str.trim()));
			
			if(reader != null)
				reader.close();
		} catch(Exception e) {
			System.out.println("Coordinator Status: Error in file - " + e.getMessage());
		} //catch
		
		return conf_data;	
	} //getConfData
	
	public static void main(String[] args) throws IOException {
		
		String conf = args[0];
		ArrayList<Integer> conf_data = getConfData(conf);
		
		coord_port = conf_data.get(0);
		th_time = conf_data.get(1);
		ServerSocket coord_sock = new ServerSocket(coord_port);
		
		System.out.println("Coordinator online - Port#: " + coord_port);
		
		while(true)
			new Thread(new ParticipantThread(coord_sock.accept())).start();
		
	} //main

} //class
