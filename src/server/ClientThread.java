package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashSet;

import org.apache.log4j.Logger;

public class ClientThread extends Thread {

		private static final Logger logger = Logger.getLogger(ClientThread.class);
		private Socket socket;
		private BufferedReader in;
		private BufferedWriter out;
		private String username;
		
		public ClientThread(Socket socket) throws IOException {
			this.socket = socket;
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		}
		
		@Override
		public void run() {
			Server.clients.sendPersonalMessage(socket, "Server: enter username!");
			HashSet<String> userList =  Server.clients.getNames();
			
			try{
				username = in.readLine().trim();
				
				while(userList.contains(username) || isEmptyUsername(username)){
					
					if(isEmptyUsername(username))
						Server.clients.sendPersonalMessage(socket, 
								"Server: username can not be empty, please enter name!");
					
					if(userList.contains(username))
								Server.clients.sendPersonalMessage(socket, 
							"Server: name '" + username + "' already exists, please enter other name!");
					
					username = in.readLine().trim();
				} 
				
				logger.info(username + " connected");
				
				userList.add(username);
				
				Server.clients.sendPersonalMessage(socket, "Server: hi, " + username + "!");
				Server.clients.sendMessage(socket, "Server: " + username + " joined the chat!");
				
				while(true){
					String inMessage = in.readLine();
					
					if("exit".equals(inMessage.trim())){
						Server.clients.sendPersonalMessage(socket, "Server: goodbye, " + username + "!");
						Server.clients.getNames().remove(username);
						Server.clients.removeUser(this);
						
						socket.close();
						throw new IOException();
						
					}
					logger.info(username + ": " + inMessage);
					Server.clients.sendMessage(socket, username + ": " + inMessage);
				}
				
			} catch (IOException e) {
			} finally {
					if (socket != null){
						try {
							Server.clients.sendPersonalMessage(socket, "Server: goodbye, " + username + "!");
							Server.clients.sendMessage(socket, "Server: " + username + " leaved the chat!");
							Server.clients.getNames().remove(username);
							Server.clients.removeUser(this);
							socket.close();
							logger.info(username + " disconnect");
						} catch (IOException e) {
//							e.printStackTrace();
						}
					}
					
					close(out);
					close(in);
			}
			
		}
	
		public void sendMsg(String message){
			try {
				out.write(message);
				out.newLine();
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public Socket getSocket(){
			return socket;
		}
		
		public boolean isEmptyUsername(String name){
			if(name == null || name.trim().isEmpty()){
				return true;
			} 
			return false;
		}
		
		public void close(Closeable closeable){
			if(closeable != null){
				try {
					closeable.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
}
