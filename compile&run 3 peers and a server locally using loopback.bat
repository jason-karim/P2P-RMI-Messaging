javac ./client/*.java
javac ./server/*.java

cd server

rmic -d ../peer UserListRMIImpl

cd ../peer

rmic MessagingRMIImpl

start rmiregistry

start java PeerRMI 127.0.0.1
start java PeerRMI 127.0.0.1
start java PeerRMI 127.0.0.1

cd ../server

start java ServerRMI