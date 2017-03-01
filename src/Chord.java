import java.rmi.*;

import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.net.UnknownHostException;
import java.util.*;

import java.io.*;
/**@author Joe McCully
 * @version 1.0 build 1 Nov 3, 2015
 * The class for the chord potion of a node
 * Preforms the joining to other nodes and transfering of data
 */
public class Chord extends java.rmi.server.UnicastRemoteObject implements ChordMessageInterface
{
	/**Number of nodes in system*/
    public static final int M = 2;
    /**rmi registry for looking up the remote objects.*/
    Registry registry;    // rmi registry for lookup the remote objects.
	/**Finger of the next node*/
    Finger successor;
	/**Finger of the previous node*/
    Finger predecessor;
	/**@deprecated
	 * Deprecated*/
    Finger[] finger;
	/**@deprecated */
    int nextFinger;
	/**your GUID*/
    int i;   	//your id	// GUID
	/**Your port number*/
	int port;
    
	/**Description of rmiChord(String ip, int port)
	 * Create an instance of the ChordMessageInterface of the
	 * node (ip, port)*/
    public ChordMessageInterface rmiChord(String ip, int port)
    {	
		ChordMessageInterface chord = null;
		try{
		  Registry registry = LocateRegistry.getRegistry(ip, port);
		  chord = (ChordMessageInterface)(registry.lookup("Chord"));
		  return chord;
		} catch (RemoteException e) {
		  e.printStackTrace();
		} catch(NotBoundException e){
		  e.printStackTrace();
		}
		return null;
    }
    
	/**Description of in(int key, int key1, int key2)
	 * Check if key is in the semi-open interval (key1, key2]
	 * Since it is a ring, key1  can be greater than key2.
	 * In that case we verify whether key > key2 or key <= key1
	 */
    public Boolean in(int key, int key1, int key2)
    {
		if (key1 < key2)
		  return (key > key1 && key <= key2);
		else 
		  return (key > key2 || key <= key1);
    }

	/**Description of ino(int key, int key1, int key2)
	 * Check if key is in the open interval (key1, key2)
	 * Since it is a ring, key1  can be greater than key2.
	 * In that case we verify whether key > key2 or key < key1
	 */
    public Boolean ino(int key, int key1, int key2)
    {
	if (key1 < key2)
	  return (key > key1 && key < key2);
	else 
	  return (key > key2 || key < key1);
    }
    
 
    
    
    /**Description of put(int guid, byte[] data)
	 * Uploads file (guid) to a node
	 * FILE NAME HAS TO BE A NUMBER WITH A .txt EXTENSION
	 * Ex. 1.txt*/
    public void put(int guid, byte[] data) throws IOException 
    {
    	Finger success = locateSuccessor(guid);
    	if(success != null)
    	{
    		ChordMessageInterface successChord = rmiChord(success.getIp(), success.getPort());
    		successChord.write(guid, data);// Writes bytes from the specified byte array to this file output stream
				
			
    	}
		// TODO: Store data in the closest node.
		// before calling this method you need to find the node using 
		// locateSuccessor. 
    }
	/**Description of remove(int guid)
	 * Removes the file (guid)
	 * */
	public void remove(int guid) throws IOException {
		Finger success = locateSuccessor(guid);
    	if(success != null)
    	{
    		ChordMessageInterface successChord = rmiChord(success.getIp(), success.getPort());
    		successChord.delete(guid);// Writes bytes from the specified byte array to this file output stream
    	}
		// TODO: remove the file guid.
		// before calling this method you need to find the node using 
		// locateSuccessor. 
    }
	/**Description of get(int guid)
	 * retrieves the file (guid)*/
	public byte[] get(int guid) throws IOException {
		Finger success = locateSuccessor(guid);
    	if(success != null)
    	{
    		ChordMessageInterface successChord = rmiChord(success.getIp(), success.getPort());
    		return successChord.read(guid);// Writes bytes from the specified byte array to this file output stream
    	}
		// TODO: read the file
		// before calling this method you need to find the node using 
		// locateSuccessor. 
		return null;
    }
	/**Description of write(int guid, byte[] data)
	 * Writes data to a file (guid)*/
	public void write(int guid, byte[] data)throws IOException 
    {
		File file = new File(port+".\\"+Integer.toString(guid)+".txt");
		FileOutputStream fos = null;
		fos = new FileOutputStream(file);
		// Writes bytes from the specified byte array to this file output stream
		fos.write(data);
	}
	/**Description of read(int guid)
	 * Reads data from a file (guid)*/
	public byte[] read(int guid)throws IOException 
    {
		//read from file, return contents
		File file = new File(port+".\\"+Integer.toString(guid)+".txt");
		FileInputStream fis = null;
		fis = new FileInputStream(file);
		byte[] data = new byte[fis.available()];
		fis.read(data);
		return data;
    }
	/**Description of delete(int guid)
	 * Deletes a file (guid)*/
	public void delete(int guid)throws IOException 
    {
		//delete file
		File file = new File(port+".\\"+Integer.toString(guid)+".txt");
		if(file.delete()){
			System.out.println(file.getName() + " deleted");
		}else{
			System.out.println("Failed to delete "+file.getName());
		}
    }
    /**Description of getId()
	 * Returns this node's id*/
	@Override
	public int getId() throws RemoteException {
		return i;
	}
	/**Description of isAlive()
	 * Sends a message that the node is alive*/
    public boolean isAlive() throws RemoteException {
		return true;
    }
	/**Description of getPredecessor()
	 * gets the predecessor of this node*/
    public Finger getPredecessor() throws RemoteException {
		return predecessor;
    }
    /**Description of locateSuccessor(int key)
	 * Locates the successor for this node*/
    public Finger locateSuccessor(int key) throws RemoteException {
    	System.out.println(this.port +": receiving locate successor from: "+ key);
		if (key == i)  
			throw new IllegalArgumentException("Key must be distinct that  " + i);
		if (successor != null && successor.getId() != i)
		{
			if (in(key, i, successor.getId())) 
				return successor;
			Finger jguid = closestPrecedingNode(key);
			ChordMessageInterface j = rmiChord(jguid.getIp(), jguid.getPort());
            if (j == null)
				return null;
			return j.locateSuccessor(key); 
		}
		return successor;
	}
    /**Description of closestPrecedingNode(int key)
	 * Finds the closest preceding node for this node*/
    public Finger closestPrecedingNode(int key) throws RemoteException {
    	if(key != i)
    	{
    		int count;
    		for(count = M-1; count > 1; count--)
    		{
    			if (finger[count] != null &&ino(finger[count].getId(),i,key))
    			{
    				return (finger[count]);   			
    			}
    		}
    	}
		return successor;
    }
   /** Description of joinRing(String ip, int port)
	* method that starts the join process*/
   public void joinRing(String ip, int port)  throws RemoteException { 
	 System.out.println(this.port +": receiving join from: "+ port);
	 ChordMessageInterface j = rmiChord(ip, port);
     predecessor = null;
     successor = j.locateSuccessor(i);
     System.out.println();
    }
    
    /**Description of stabilize()
	 * fixes the nodes after a new node joins
	 * i.e. changes the successor and predecessor so all the nodes form a ring*/
    public void stabilize() throws RemoteException, UnknownHostException 
    { 
    	if(successor == null)
    	{
    		return;
    	}
    	ChordMessageInterface success = rmiChord(successor.getIp(), successor.getPort());
    	Finger x = success.getPredecessor();
		if(x!=null && ino(x.getId(), i, successor.getId()))
		{
			if(i != x.getId())
			{
				successor = x;
			}
		}
		InetAddress ip = InetAddress.getLocalHost();
    	success = rmiChord(successor.getIp(), successor.getPort());
    	success.notify(new Finger(ip.getHostAddress(), port, i));
    }
    /**Description of notify(Finger j)
	 * Retrieves keys from j and puts them into the predecessor node
	 * j believes it is a predecessor of i*/
    public void notify(Finger j) throws RemoteException {
		if(predecessor == null || ino(j.getId(), predecessor.getId(),i))
		{
			predecessor = j;
		}
    }
    
    /**Description of checkPredecessor()
	 * @deprecated
	 * Deprecated
	 * not utilized at all*/
    public void checkPredecessor() {      
      // TODO	
    }
    
    /**Description of Chord(int _port, int id)
	 * Constructor for the node
	 * Uses stabilize to fix nodes
	 * A timer is used for stabilize()*/
    public Chord(int _port, int id) throws RemoteException, UnknownHostException {
		finger = new Finger[(1 << M)];
		// TODO: set the fingers in the array to null
		i = id;
		port = _port;
		// TODO: determine the current IP of the machine
		
		predecessor = null;
		InetAddress ip = InetAddress.getLocalHost();
		successor = new Finger(ip.getHostAddress(), i, i);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
	    @Override
	    public void run() {
	      try {
			stabilize();
		} catch (RemoteException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      checkPredecessor();
	    }
	}, 500, 500);
	  try{
		// create the registry and bind the name and object.
		System.out.println("Starting RMI at port="+port);
		registry = LocateRegistry.createRegistry( port );
		registry.rebind("Chord", this);
      }
      catch(RemoteException e){
		throw e;
      } 
    }
}
