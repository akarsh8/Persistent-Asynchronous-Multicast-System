package p3.participant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Participant {

	public static boolean conn = false;
	
	private static ArrayList<String> getConfData(String conf) {
		ArrayList<String> conf_data = new ArrayList<String>();
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(conf));
			String str = null;
			
			while((str = reader.readLine()) != null)
				conf_data.add(str.trim());
			
			if(reader != null)
				reader.close();
			
		} catch(Exception e) {
			System.out.println("Participant Status: Error in file - " + e.getMessage());
		} //catch
		
		return conf_data;
	} //getConfData
	
	private static void makeDispatcher(int id, String cd, String lg) {
		try {
			
			new Thread(new Dispatcher(id, cd, lg)).start();
			return;
			
		} catch(Exception e) {
			System.out.println("Participant Status: Error in making dispatcher - " + e.getMessage());
		} //catch
	} //makeThread
	
	public static void main(String[] args) throws InterruptedException {
		
		String conf = args[0];
		ArrayList<String> conf_data = getConfData(conf);
		int pID = Integer.parseInt(conf_data.get(0));
		String log = conf_data.get(1);
		String conn_data = conf_data.get(2);
		
		System.out.println("Participant Status: Online");
		
		makeDispatcher(pID, conn_data, log);
		
	} //main
	
}
