
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
		System.out.println(peer.getName() + " : " + message);
		return true;
	}
}
