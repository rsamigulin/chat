package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;

public class Server {

	private static final Logger logger = Logger.getLogger(Server.class);
	
	public static Clients clients;
	public static void main(String[] args) {

		ClientThread client = null;
		clients = new Clients();
		
		try(ServerSocket serverSocket = new ServerSocket(8082)){
			logger.info("wait for a client...");
			
			while(true){
				Socket socket = serverSocket.accept();
				client = new ClientThread(socket);
				client.start();
				clients.addUser(client);
				logger.info("new client connected");
			}
			
		} catch (IOException e) {
//			e.printStackTrace();
		} 
	}
}






