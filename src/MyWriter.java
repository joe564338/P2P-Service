import java.util.*;
import java.io.*;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
/**
 * @author Joe McCully
 * @version 1.0 build 1 Nov. 3, 2015
 * The class that runs the program
 * Contains the interface for the user
 */
public class MyWriter 
{
	/**The node for this instance*/
    Chord chord;

	/**Description of MyWriter(final int _port, int id) throws RemoteException, UnknownHostException
	 * Constructor for the client UI*/
	public MyWriter(final int _port, int id) throws RemoteException, UnknownHostException
	{

		chord = new Chord(_port, id);
		/** Timer for stablizing*/
		Timer timer1 = new Timer();
		timer1.scheduleAtFixedRate(new TimerTask() {
			/** Description of run()
			 * The running method for the timer*/
			@Override
			public void run() {
				int guidNo;
				String guid;
				/** Byte array for transfering data*/
				byte[] data = null;
				/**File object for transfering files*/
				File file;
				Scanner scan= new Scanner(System.in);
				while (true)
				{
					// TODO User interface: join, put, get, print, remove from Chord
					// print must show the state (all the variables of chord) of the system
					System.out.println("Input command: JOIN, PUT, GET, PRINT, REMOVE");
					// get their input as a String
					String option = scan.next().toUpperCase();

					switch (option) {
						case "JOIN"://pass ip and port you want to connect to

							// prompt for the ip and port to connect to
							System.out.println("Input IP to connect to: ");
							String ip = scan.next();

							System.out.println("Input Port to use: ");
							int port = scan.nextInt();
							System.out.println("Sending Request...");
							try {
								chord.joinRing(ip, port);
								System.out.println("\nJoined at port: " + port);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							break;

						case "PUT": //pass GUID of file and file contents in bytes
							System.out.println("Input file GUID to upload:");
							guid = scan.next();
							file = new File(".\\" + Integer.toString(_port) + "\\" + guid +".txt");
							FileInputStream fis = null;
							System.out.println(file.toString());
							try
							{
								fis = new FileInputStream(file);
								data = new byte[fis.available()];
								fis.read(data);
								guidNo = Integer.parseInt(guid);
								chord.put(guidNo, data);
								System.out.println("File "+ guid +" uploaded!");
							}catch (IOException e)
							{
								System.out.println("Invalid Input");
							}

							break;

						case "GET"://pass GUID
							System.out.println("Input file to retrieve:");
							guid = scan.next();
							guidNo = Integer.parseInt(guid);
							try {
								data = chord.get(guidNo);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							file = new File(".\\" + Integer.toString(_port) + "\\" + guid + ".txt");
							FileOutputStream fos = null;
							try {
								fos = new FileOutputStream(file);
								// Writes bytes from the specified byte array to this file output stream
								fos.write(data);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("File "+ guid +" retrieved");
							break;

						case "REMOVE": //remove file from system, pass the GUID
							System.out.println("Input file to remove:");
							guid = scan.next();
							guidNo = Integer.parseInt(guid);
							try {
								chord.remove(guidNo);
								System.out.println("File "+ guid +" removed");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;

						case "PRINT":
							System.out.println("Status of Chord:");
							System.out.println("GUID: " + chord.i);
							System.out.println("Port: " + chord.port);
							if(chord.predecessor != null)
							{
								System.out.println("Predecessor: "+ chord.predecessor.getId());
							}
							else
							{
								System.out.println("Predecessor: "+ chord.predecessor);
							}
							if(chord.successor != null)
							{
								System.out.println("Successor: "+ chord.successor.getId());
							}
							else
							{
								System.out.println("Predecessor: "+ chord.successor);
							}

							System.out.println("Next Finger: "+ chord.nextFinger);

							break;

						default:
							System.out.println("Bad Command");
							break;
					}
				}
			}
		}, 500, 500);
	}
    /** Description of main()
	 * Main method that runs the Program
	 * MyWriter is used here*/
	static public void main(String args[])
    {

		if (args.length < 1 ) {  
			throw new IllegalArgumentException("Parameter: <guid> <port>");//guid is user id
		}
        try{
			MyWriter myWriter =new MyWriter( Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		}
		catch (Exception e) {
           e.printStackTrace();
           System.exit(1);
		}
     }
}