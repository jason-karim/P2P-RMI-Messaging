
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
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PeerRMI {

	protected static User myUser;
	private static Scanner s = new Scanner(System.in);

	//List of User names that have sent a message to the user.
	protected static List<String> notifs = new ArrayList<String>();

	//Peer that the User is in chat with
	//used in MessaginRMIImpl
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

			// create the user's peer object
			MessagingRMIImpl myUserRemote = new MessagingRMIImpl(myUser.getRemoteObjName());

			//Get server's object handle
			UserListRMI myServerRMI = (UserListRMI)Naming.lookup("rmi://"+ args[0] +"/myServerRMI");//TODO change if not on same net

			//Register with server as online
			//have the Server add you to its list of online users
			myServerRMI.goOnline(myUser);

			System.out.println("The Client is ready");


			boolean repeat = true;
			while(repeat==true) {
				repeat = chooseChat(myServerRMI);
			}

			//have server remove user from its list of online peers
			myServerRMI.goOffline(myUser);

			//unregister user's peer object from the rmi registry
			UnicastRemoteObject.unexportObject(myUserRemote, true);

			System.out.println("Shutting down...");

			//This code deletes chat logs when Peer is closing

			// Lists all files in chats directory
			File folder = new File(".//chats");
			File fList[] = folder.listFiles();
			// Searches /chats
			File toDel; //chat to be deleted if matches
			for (int i = 0; i < fList.length; i++) {
				toDel = fList[i];
				if (toDel.getName().startsWith(myUser.getRemoteObjName()) && toDel.getName().endsWith(".chat")) {
					// if chat file starts with user's objName and ends with extension ".chat"
					//delete chat file
					toDel.delete();

					//this condition prevents multiple peers on the same system from deleting chat logs of other peers on the system, since they all share the same chats directory
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		s.close();

	}

	public static void displayOnlineUsers(User[] users) {
		//display users with an index next to their name, and none if no users are available
		if(users.length==0) {System.out.println("sorry, nobody is online"); return;}
		for(int i=0; i<users.length;i++) {
			System.out.println(i + " : " + users[i].getName());
		}
	}

	public static boolean chooseChat(UserListRMI myServerRMI) {
		try {
			//get operating system's name
			String operatingSystem = System.getProperty("os.name"); 

			//Check the current operating system and clear console accordingly
			if(operatingSystem.contains("Windows")){        
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				new ProcessBuilder("clear").inheritIO().start().waitFor();
			} 


			System.out.println("USER: "+myUser.getName());

			//display any new messages
			getNotifications();

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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public static void enterChat(User peer) throws InterruptedException, NotBoundException{
		//remove notification of chat entered
		notifs.remove(peer.getName());

		//set inChatWith to have MessaginRMIImpl print messages received from peer in chat
		inChatWith = peer;
		try {

			//get operating system's name
			String operatingSystem = System.getProperty("os.name"); 

			//Check the current operating system and clear console accordingly
			if(operatingSystem.contains("Windows")){        
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				new ProcessBuilder("clear").inheritIO().start().waitFor();
			}


			System.out.println("USER: "+myUser.getName());
			System.out.println("TALKING TO: "+peer.getName());
			System.out.println();

			//get peer object
			MessagingRMI myPeer = null;
			myPeer = (MessagingRMI)Naming.lookup("rmi://"+peer.getHost()+"/"+peer.getRemoteObjName());
			
			//set path to corresponding chat log
			File f = new File(".\\chats\\"+myUser.getRemoteObjName()+"&"+peer.getRemoteObjName()+".chat");
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
				//send message to peer
				//message() return true if message received, false if error occurred on peer's side
				try {
					//create file if not existing already
					f.createNewFile();
					//write to file message sent
					FileWriter myWriter = new FileWriter(f,true);
					
					if(!myPeer.message(myUser,msg)) {
						throw new Exception();
					}
					
					myWriter.write(myUser.getName() + " : " + msg + "\n");
					myWriter.close();
				}catch (RemoteException e) {
					System.out.println("///error///User not reachable, perhaps went offline");
					System.out.println("///error///Try again, or exit chat using '$exit' and see if user is online");
				}catch (IOException e) {
					System.out.println("///error///An error occurred while writing sent message to log, couldn't open chat log, message not sent");
				}catch (Exception e) {
					System.out.println("///error///Error on receiver side, message not sent nor written to chat log");
					System.out.println("///error///Exit chat using '$exit'");
				}
				
			}
		}catch (IOException e) {
			System.out.println("///error///" + e.getMessage());
		}

	}
	public static void getNotifications() {

		//Display if messages are available
		//notifs[] contains names of users from which messages have been received

		if(notifs.size()>0) {
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
		else {
			System.out.println();
			System.out.println("////////");
			System.out.println("No new Messages");
			System.out.println("////////");
			System.out.println();
		}
	}

}