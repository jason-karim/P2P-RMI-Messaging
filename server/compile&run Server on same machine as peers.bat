javac *.java

:: Generate server stub and place it in ../peer
rmic -d ../peer UserListRMIImpl

cd ../peer
 
start rmiregistry

cd ../server

start java ServerRMI