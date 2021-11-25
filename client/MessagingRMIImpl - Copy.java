
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
		//receive message from a peer
		PeerRMI.notifs.add(peer.getName());
		
		try {
			//chat file path
			File f = new File(".\\chats\\"+peer.getObjName()+".txt");
			//create file if not existing already
			f.createNewFile();
			//write to file new message received
			FileWriter myWriter = new FileWriter(f,true);
			myWriter.write(peer.getName() + " : " + message + "\n\r");
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred, message not sent.");
			e.printStackTrace();
		}
		return true;
	}
}
