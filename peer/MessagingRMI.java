
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface MessagingRMI extends Remote{
	boolean message(User peer,String message) throws RemoteException;
}
