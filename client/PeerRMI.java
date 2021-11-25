
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Scanner;

public class PeerRMI {
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
				repeat = chooseChat(myServerRMI);
			}


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
			e.printStackTrace();
			return true;
		}
	}
	public static boolean chooseChat(UserListRMI myServerRMI) {
		try {

			System.out.println("Who do you want to message (-1 to refresh list; -2 to end program)");
			User[] users;
			users = myServerRMI.returnOnlineUsers(myUser);
			displayOnlineUsers(users);
			int choice = Integer.parseInt(s.nextLine());
			if(choice>=0 && choice<users.length) {
				enterChat(users[choice]);
			}else {
				switch(choice) {
				case -1: {return true;}
				case -2: {return false;}
				default : {System.out.println("Out of range");}
				}
			}
			return true;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public static void enterChat(User peer) throws NotBoundException, IOException, InterruptedException {
		//clear CMD
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

		//get peer object
		MessagingRMI myPeer = (MessagingRMI)Naming.lookup("rmi://"+peer.getHost()+"/"+peer.getObjName());

		File f = new File(".\\chats\\"+peer.getObjName()+".txt");
		if(f.createNewFile()) {
			//Creating new File for new chat
			System.out.println("////Creating new chat////");
		}else {
			//reading chat from already existing file
			System.out.println("////Reading past Chat from file////");
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line;
			while((line = in.readLine()) != null)
			{
				System.out.println(line);
			}
			in.close();
		}

		while(true) {
			String msg = s.nextLine();
			if(msg.equals("$exit")){break;}
			myPeer.message(myUser,msg);

			//create file if not existing already
			f.createNewFile();
			//write to file new message received
			FileWriter myWriter = new FileWriter(f,true);
			myWriter.write(myUser.getName() + " : " + msg + "\n\r");
			myWriter.close();
		}
	}

}