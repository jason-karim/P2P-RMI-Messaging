
import java.rmi.*;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserListRMIImpl extends UnicastRemoteObject implements UserListRMI{

	//List of User Objects
	private List<User> userList = new ArrayList<User>();

	public UserListRMIImpl(String name) throws RemoteException {
		super();
		try {
			Naming.rebind(name,this);
		}catch(Exception e){
			System.err.println("Exception on main: " + e.getMessage());
		}
	}

	@Override
	public User[] returnOnlineUsers(User callingUser) throws RemoteException{
		List<User> users = new ArrayList<User>();
		for(int i=0; i<userList.size() ; i++) {
			//			System.out.println("comparing u1 and u2");
			//			System.out.println("u1 " + userList.get(0).getName() + " " + userList.get(0).getObjName());
			//			System.out.println("u2 " + callingUser.getName() + " " + callingUser.getObjName());
			//			System.out.println(userList.get(i).compare(callingUser));
			if(!userList.get(i).compare(callingUser)) {
				users.add(userList.get(i));
			}
		}
		return (User[])users.toArray(new User[0]);
	}

	@Override
	public boolean goOnline(User callingUser) throws RemoteException{
		try{
			userList.add(callingUser);

			//Sort List after adding a User Object by Name
			//List sort takes as arguments the list to be sorted and a comparator 
			//to plug into the comparator passef the list's objects to determine which to be placed higher than the other
			Collections.sort(userList, new Comparator<User>(){
				public int compare(User u1, User u2)
				{
					//compare by name, using the String's compareTo method
					return u1.getName().compareTo(u2.getName());
				}
			});

			return true;
		}catch(Exception e) {
			System.err.println("Exception on goOnline : " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean goOffline(User callingUser) throws RemoteException{
		//cannot use userList.remove(callingUser) since the passed object from the peer to the server 
		//is not the same object that already is on the server, thus would not have a match in the list
		//instead iterate over the list to find a matching user, then remove the matching user from the list
		for(int i = 0; i<userList.size() ; i++) {
			if(userList.get(i).compare(callingUser)) {
				//remove matching user at index i
				userList.remove(i);
				return true;
			}
		}
		return false;

	}
}
