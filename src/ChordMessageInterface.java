import java.rmi.*;
import java.io.*;
/**@version 1.0 build 1 Nov. 3 2015
 * Interface for the methods used in the Chord class*/
public interface ChordMessageInterface extends Remote
{
    public Finger getPredecessor()  throws RemoteException;
    Finger locateSuccessor(int key) throws RemoteException;
    Finger closestPrecedingNode(int key) throws RemoteException;
    public void joinRing(String Ip, int port)  throws RemoteException;
    public void notify(Finger j) throws RemoteException;
    public boolean isAlive() throws RemoteException;
    public int getId() throws RemoteException;
    public void put(int guid, byte[] data) throws IOException, RemoteException;
	public void remove(int guid) throws IOException, RemoteException;
	public byte[] get(int guid) throws IOException, RemoteException;
	public void write(int guid, byte[] data)throws IOException; 
	public byte[] read(int guid)throws IOException;
	public void delete(int guid)throws IOException;
}
