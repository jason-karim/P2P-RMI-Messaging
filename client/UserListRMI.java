import java.rmi.RemoteException;
import java.rmi.Remote;

public interface UserListRMI extends Remote{
	User[] returnOnlineUsers(User callingUser) throws RemoteException;
	boolean goOnline(User callingUser) throws RemoteException;
	void goOffine(User callingUser) throws RemoteException;
}