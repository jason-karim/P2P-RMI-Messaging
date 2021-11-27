import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class User implements Serializable {
	
	private String name;
	private String remoteObjName;
	private String host;
	
	public User(String name) {
		this.name = name;
		this.remoteObjName = name + new Random().nextInt();
		try {
			//set user's ip to machine's networking interface
			this.host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	public String getRemoteObjName() {
		return remoteObjName;
	}
	public String getHost() {
		return host;
	}
	public User(String name, String remoteObjName) {
		this.name = name;
		this.remoteObjName = remoteObjName;
	}
	
	public boolean equals(User u) {
		//Compare User objects by there name, objName and hostIP
		if(this.name.equals(u.name) && this.remoteObjName.equals(u.remoteObjName) && this.host.equals(u.host)) {return true;}
		else {return false;}
	}

}
