import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class chat extends Thread{
	User peer;
	public chat(User peer) {
		this.peer = peer;
		this.start();
	}
	public void run() {
		try {
			MessagingRMI myPeer = (MessagingRMI)Naming.lookup("rmi://"+peer.getHost()+"/"+peer.getObjName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
