package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Clients {
	
	private ArrayList<ClientThread> list = new ArrayList<ClientThread>();
	private HashSet<String> names = new HashSet<String>();
	
	public void addUser(ClientThread client){
		list.add(client);
	}
	
	public void sendMessage(Socket socket, String message) {
		Iterator<ClientThread> iter = list.iterator();
		while(iter.hasNext()){
			ClientThread elem = iter.next();
			if(!((elem.getSocket()).equals(socket))){
				elem.sendMsg(message);
			}	
		}
	}
	
	public void sendPersonalMessage(Socket socket, String message) {
		Iterator<ClientThread> iter = list.iterator();
		while(iter.hasNext()){
			ClientThread elem = iter.next();
			if(((elem.getSocket()).equals(socket))){
				elem.sendMsg(message);
			}	
		}
	}
	
	public int getSize(){
		return list.size();
	}
	
	public void removeUser(ClientThread client){
		list.remove(client);
	}

	public ArrayList<ClientThread> getClientsList(){
		return list;
	}
	
	public HashSet<String> getNames(){
		
		return names;
	}
}
