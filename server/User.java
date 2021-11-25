import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class User implements Serializable {
	
	private String name;
	private String objName;
	private String host;
	public String getName() {
		return name;
	}
	public String getObjName() {
		return objName;
	}
	public String getHost() {
		return host;
	}
	public User(String name) {
		this.name = name;
		this.objName = name + new Random().nextInt();
		try {
			this.host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	public User(String name, String oName) {
		this.name = name;
		this.objName = oName;
	}
	
	public boolean compare(User u) {
		//Compare User objects by there name an objName
		//Add new properties of User objects to comparison if added
		if(this.name.equals(u.name) && this.objName.equals(u.objName) && this.host.equals(u.host)) {return true;}
		else {return false;}
	}

}
