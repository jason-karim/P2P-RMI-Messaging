
import java.rmi.*;

public class ServerRMI {
    public static void main(String args[]) {

        // set security policy then create and install the security manager
        System.setProperty("java.security.policy","file:./security.policy");
        System.setSecurityManager(new RMISecurityManager());
        
        try {
            // create the server object
        	UserListRMIImpl myServer = new UserListRMIImpl("myServerRMI");
            System.out.println("The server is ready");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

}
