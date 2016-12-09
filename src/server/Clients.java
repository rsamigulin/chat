package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class Clients {
	
	private List<ClientThread> list = new CopyOnWriteArrayList<ClientThread>();
	private Set<String> names = new CopyOnWriteArraySet<String>();
	
	
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
	
	public void addClient(ClientThread client){
		list.add(client);
	}

	public void removeClient(ClientThread client){
		list.remove(client);
	}

	public int getSize(){
		return list.size();
	}
	
	public List<ClientThread> getClientsList(){
		return list;
	}
	
	public Set<String> getNames(){
		return names;
	}
}
