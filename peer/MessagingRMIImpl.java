
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


		if(PeerRMI.inChatWith.compare(peer)) {
			//receive message from a peer
			System.out.println(peer.getName() + " : " + message);
		}

		try {
			//chat file path
			File f = new File(".\\chats\\"+PeerRMI.myUser.getObjName()+"&"+peer.getObjName()+".chat");
			//create file if not existing already
			f.createNewFile();
			//write to file new message received
			FileWriter myWriter = new FileWriter(f,true);
			myWriter.write(peer.getName() + " : " + message + "\n");
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred, message not sent.");
			e.printStackTrace();
		}
		return true;
	}
}
