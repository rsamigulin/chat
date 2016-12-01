package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

public class Client {

	private static Logger logger = Logger.getLogger(Client.class);
	private static String username;
	
	public static void main(String[] args) {
		
		try(BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))){
				
			try(Socket socket = new Socket("localhost", 8082)){
				
				logger.info("client start");
			
						try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
								BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
								){
							String inviteFromServer = in.readLine().trim();
							
							logger.info(inviteFromServer);
							
							username = consoleReader.readLine().trim();
							
							out.write(username);
							out.newLine();
							out.flush();
							
							Thread t = new Thread(new Runnable() {
								
								@Override
								public void run() {
									try{
										while(true){
											String s = in.readLine();
											logger.info(s);
										}
									} catch (IOException e) {
//										e.printStackTrace();
									}
								}
							});
								
							t.start();
							
							while(true){
								String userMessage = consoleReader.readLine();
								
								if("exit".equals(userMessage.trim())){
									out.write("exit"); 
									out.newLine();
									out.flush();
									socket.close();
								}
								out.write(userMessage); 
								out.newLine();
								out.flush();
							}	
						} catch (IOException ioe) {
//							ioe.printStackTrace();
						}
						
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} 
		
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
	}
}


