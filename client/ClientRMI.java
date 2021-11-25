
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Scanner;

public class ClientRMI {
	private static User myUser;
	private static Scanner s = new Scanner(System.in);
	public static void main(String args[]) {

		// set security policy then create and install the security manager
		System.setProperty("java.security.policy",".\\security.policy");
		System.setSecurityManager(new RMISecurityManager());

		try {
			System.out.println("Input your name");

			String name = s.nextLine();
			myUser = new User(name);

			// create the Client object
			MessagingRMIImpl myUserRemote = new MessagingRMIImpl(myUser.getObjName());

			//Register with server as online
			UserListRMI myServerRMI = (UserListRMI)Naming.lookup("rmi://127.0.0.1/myServerRMI");//TODO change if not on same net

			//have the Server add you to its list of online users
			myServerRMI.goOnline(myUser);

			System.out.println("The Client is ready");
			boolean repeat = true;
			while(repeat==true) {
				repeat = connectToPeer(myServerRMI);
			}
			
			//should implement an object for connecting, and a local object that handles different clients in different cmd windows

		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}

	public static void displayOnlineUsers(User[] users) {
		if(users.length==0) {System.out.println("sorry, nobody is online"); return;}
		for(int i=0; i<users.length;i++) {
			System.out.println(i + " : " + users[i].getName());
		}
	}

	public static boolean connectToPeer(UserListRMI myServerRMI) {
		try {
			System.out.println("Who do you want to message (-1 to refresh list; -2 to end program)");
			User[] users = myServerRMI.returnOnlineUsers(myUser);
			displayOnlineUsers(users);
			int choice = Integer.parseInt(s.nextLine());
			if(choice>=0 && choice<users.length) {
				//			chat c = new chat(users[choice]);				//start thread to speak to peer
				//get peer object to call its message() method
				MessagingRMI myPeer = (MessagingRMI)Naming.lookup("rmi://"+users[choice].getHost()+"/"+users[choice].getObjName());
				System.out.println("what message do you wanna send them?");
				String msg = s.nextLine();
				//call peer's message method to send them message
				myPeer.message(myUser, msg);
			}else {
				switch(choice) {
				case -1: {return true;}
				case -2: {return false;}
				default : {System.out.println("Out of range");}
				}
			}

			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
	}

}