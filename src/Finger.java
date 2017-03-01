import java.io.*;
import java.net.*;
/**@version 1.0 build 1 Nov. 3 2015
 * Finger portion of a node
 * To be used with the Chord class*/
public class Finger implements Serializable  {
    String ip;
    int    port;
    int    id;
    public Finger(String Ip, int Port, int Id) { 
	this.ip = Ip;
	this.port = Port;
 	this.id = Id;
    }
    public int getId()
    {
	return this.id;
    }
    public String getIp()
    {
	return this.ip;
    }
    public int getPort()
    {
	return this.port;
    }
}

