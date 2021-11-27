
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class MessagingRMIImpl extends UnicastRemoteObject implements MessagingRMI {
	public MessagingRMIImpl(String objName) throws RemoteException {
		super();
		try {
			Naming.rebind(objName, this);
		}catch(Exception e) {
			System.out.println("Exception : "+ e.getMessage());
		}
	}
	@Override
	public boolean message(User peer,String message) throws RemoteException{

		//set notification from peer
		if(!PeerRMI.notifs.contains(peer.getName())) {
			PeerRMI.notifs.add(peer.getName());
		}

		
		if(PeerRMI.inChatWith.equals(peer)) {
			//if in chat with peer who sent a message, print the message
			System.out.println(peer.getName() + " : " + message);
		}
		try {
			//chat file path
			File f = new File(".\\chats\\"+PeerRMI.myUser.getRemoteObjName()+"&"+peer.getRemoteObjName()+".chat");
			//create file if not existing already
			f.createNewFile();
			//write to file new message received
			FileWriter myWriter = new FileWriter(f,true);
			myWriter.write(peer.getName() + " : " + message + "\n");
			myWriter.close();
		} catch (IOException e) {
			System.out.println("///error///An error occurred while receiving message, couldn't open chat log");
			return false;
		}
		return true;
	}
}
