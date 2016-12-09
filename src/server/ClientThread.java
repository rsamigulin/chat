package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Set;

import org.apache.log4j.Logger;

public class ClientThread extends Thread {

		private static final Logger logger = Logger.getLogger(ClientThread.class);
		private Socket socket;
		private BufferedReader in;
		private BufferedWriter out;
		private String username;
		Set<String> userList =  Server.clients.getNames();
		
		public ClientThread(Socket socket) throws IOException {
			this.socket = socket;
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		}
		
		@Override
		public void run() {
			Server.clients.sendPersonalMessage(socket, "Server: enter username!");
			
			try{
				username = in.readLine().trim();
				while(userList.contains(username) || isEmptyUsername(username)){
					readUserName();
				} 
				
				logger.info(username + " connected from " + socket.getInetAddress().getHostAddress());
				
				userList.add(username);
				
				Server.clients.sendPersonalMessage(socket, "Server: hi, " + username + "!");
				Server.clients.sendMessage(socket, "Server: " + username + " joined the chat!");
				
				while(true){
					readMessage();
				}
				
			} catch (IOException e) {
			} finally {
				finallyBlock();
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
		
		public void readUserName() throws IOException{
				if(isEmptyUsername(username))
					Server.clients.sendPersonalMessage(socket, 
							"Server: username can not be empty, please enter name!");
				
				if(userList.contains(username))
							Server.clients.sendPersonalMessage(socket, 
						"Server: name '" + username + "' already exists, please enter other name!");
				
				username = in.readLine().trim();
		}
		
		public void readMessage() throws IOException{
			String inMessage = in.readLine();
			
			if(inMessage == null){
				throw new IOException();
			}
			
			if("exit".equals(inMessage.trim())){
				Server.clients.sendPersonalMessage(socket, "Server: goodbye, " + username + "!");
				Server.clients.getNames().remove(username);
				Server.clients.removeClient(this);
				
				throw new IOException();
			}
			
			logger.info(username + ": " + inMessage);
			Server.clients.sendMessage(socket, username + ": " + inMessage);
		}
		
		public void finallyBlock(){
			if (socket != null){
				try {
					Server.clients.sendPersonalMessage(socket, "Server: goodbye, " + username + "!");
					Server.clients.sendMessage(socket, "Server: " + username + " leaved the chat!");
					Server.clients.getNames().remove(username);
					Server.clients.removeClient(this);
					socket.close();
					logger.info(username + " disconnect");
				} catch (IOException e) {
//					e.printStackTrace();
				}
			}
			
			close(out);
			close(in);
		}
}
