
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
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PeerRMI {
	
	protected static User myUser;
	private static Scanner s = new Scanner(System.in);
	protected static List<String> notifs = new ArrayList<String>();
	protected static User inChatWith = new User("nobody");
	
	
	
	public static void main(String args[]) {

		// set security policy then create and install the security manager
		System.setProperty("java.security.policy","security.policy");
		System.setSecurityManager(new RMISecurityManager());

		try {
			System.out.println("Input your name");

			String name = s.nextLine();
			myUser = new User(name);
			
			System.out.println("Communicating with Server at " + args[0]);

			// create the Client object
			MessagingRMIImpl myUserRemote = new MessagingRMIImpl(myUser.getObjName());

			//Register with server as online
			UserListRMI myServerRMI = (UserListRMI)Naming.lookup("rmi://"+ args[0] +"/myServerRMI");//TODO change if not on same net

			//have the Server add you to its list of online users
			myServerRMI.goOnline(myUser);

			System.out.println("The Client is ready");
			boolean repeat = true;
			while(repeat==true) {
				repeat = chooseChat(myServerRMI);
			}

			myServerRMI.goOffline(myUser);
			myUserRemote.unexportObject(myUserRemote, true);
			System.out.println("Shutting down...");
			
			TimeUnit.SECONDS.sleep(5);
			
			
			//This code deletes chat logs when Peer is closing
			
			// Lists all files in chats directory
			File folder = new File(".//chats");
			File fList[] = folder.listFiles();
			// Searches /chat
			File toDel;
			for (int i = 0; i < fList.length; i++) {
			     toDel = fList[i];
			    if (toDel.getName().startsWith(myUser.getObjName()) && toDel.getName().endsWith(".chat")) {
			        // and deletes file
			        toDel.delete();
			    }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void displayOnlineUsers(User[] users) {
		if(users.length==0) {System.out.println("sorry, nobody is online"); return;}
		for(int i=0; i<users.length;i++) {
			System.out.println(i + " : " + users[i].getName());
		}
	}

	public static boolean chooseChat(UserListRMI myServerRMI) {
		try {
			//clear CMD
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			
			System.out.println("USER: "+myUser.getName());
			
			if(notifs.size()>0) {getNotifications();}
			else {
				System.out.println();
				System.out.println("//////////////////////////////////////");
				System.out.println("No new Messages");
				System.out.println("//////////////////////////////////////");
				System.out.println();
			}
			//ask user which chat to enter
			System.out.println("Who's chat do you want to enter (-1 to refresh list; -2 to end program)");
			User[] users;
			users = myServerRMI.returnOnlineUsers(myUser);
			displayOnlineUsers(users);
			String input = s.nextLine();
			int choice = -1;
			try{
				choice = Integer.parseInt(input);
			}catch(Exception e) {
				choice = -1;
			}
			if(choice>=0 && choice<users.length) {
				enterChat(users[choice]);
			}
			if(choice == -2) {return false;}
			return true;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public static void enterChat(User peer) throws InterruptedException, IOException, NotBoundException{
		//remove notification of chat entered
		notifs.remove(peer.getName());
		
		inChatWith = peer;
		
		//clear CMD
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		System.out.println("USER: "+myUser.getName());
		System.out.println("TALKING TO: "+peer.getName());
		System.out.println();
		
		//get peer object
		MessagingRMI myPeer = null;
		myPeer = (MessagingRMI)Naming.lookup("rmi://"+peer.getHost()+"/"+peer.getObjName());

		File f = new File(".\\chats\\"+myUser.getObjName()+"&"+peer.getObjName()+".chat");
		if(f.createNewFile()) {
			//Creating new File for new chat
			System.out.println("////Creating new chat////");
			System.out.println("///TO EXIT TYPE '$exit'///");
		}else {
			//reading chat from already existing file
			System.out.println("////Reading past Chat from file////");
			System.out.println("///TO EXIT TYPE '$exit'///");
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
			try {
				if(myPeer!=null) {myPeer.message(myUser,msg);}
				else {throw new Exception();}
			} catch (Exception e) {
				System.out.println("///error///User not reachable, perhaps went offline");
				System.out.println("///error///Try again, or exit chat using '$exit' and see if peer is online");
			}

			//create file if not existing already
			f.createNewFile();
			//write to file new message received
			FileWriter myWriter = new FileWriter(f,true);
			myWriter.write(myUser.getName() + " : " + msg + "\n");
			myWriter.close();
		}
		
	}
	public static void getNotifications() {
		System.out.println();
		System.out.println("//////////////////////////////////////");
		System.out.print("You have new messages from : ");
		for (int i = 0; i < notifs.size()-1; i++)
		{
		  System.out.print(notifs.get(i) +", ");
		}
		System.out.println(notifs.get(notifs.size()-1));
		System.out.println("//////////////////////////////////////");
		System.out.println();
	}

}