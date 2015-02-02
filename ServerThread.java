/***********************************************************
* Name: Emily Chow
* Course: CPSC 418					Term: Fall 2014
* Assignment: 2
*
* Class name: ServerThread.java
*
************************************************************/

import java.net.*;
import java.io.*;
import java.lang.Integer;

/**
 * Thread to deal with clients who connect to Server.  Put what you want the
 * thread to do in it's run() method.
 */

public class ServerThread extends Thread
{
    private Socket sock;  //The socket it communicates with the client on.
    private Server parent;  //Reference to Server object for message passing.
    private int idnum;  //The client's id number.
	
    /**
     * Constructor, does the usual stuff.
     * @param s Communication Socket.
     * @param p Reference to parent thread.
     * @param id ID Number.
     */
    public ServerThread (Socket s, Server p, int id)
    {
	parent = p;
	sock = s;
	idnum = id;
    }
	
    /**
     * Getter for id number.
     * @return ID Number
     */
    public int getID ()
    {
	return idnum;
    }
	
    /**
     * Getter for the socket, this way the parent thread can
     * access the socket and close it, causing the thread to
     * stop blocking on IO operations and see that the server's
     * shutdown flag is true and terminate.
     * @return The Socket.
     */
    public Socket getSocket ()
    {
	return sock;
    }
	
	
	
	
    /**
     * This is what the thread does as it executes. 
     */
    public void run ()
    {
		BufferedReader stdIn = null; //input from cmd
		DataInputStream cipherIn = null;
		PrintWriter out;
		BufferedReader in = null;	//input from client
		decryptFile fileDecrypt = null;
		String incoming = null;
		String userinput_key;
		String userinput_destination;
		String fileSize;
		int fileSize_int;
		Boolean debugIsOn = false;
		Boolean successfulDecrypt = false;
		
		try {
			//initializes all of the necessary readers and debug flag
			debugIsOn = parent.getDebugFlag();
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			in = new BufferedReader (new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream());
			cipherIn = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
			fileDecrypt = new decryptFile();
		}
		catch (UnknownHostException e) {
			System.out.println ("Unknown host error.");
			return;
		}
		catch (IOException e) {
			System.out.println ("Could not establish communication.");
			return;
		}
		try {
			//prompts user for key and starts transfer
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Getting key (seed) from user");
			System.out.print("Please enter seed for key derivation: ");
			userinput_key = stdIn.readLine();
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Starting file transfer");
			
			//waits for client to send information on destination file name before printing it out. 
			userinput_destination = in.readLine();
			if (debugIsOn == true) { /*debug message*/
				System.out.println("Debug Server: Receiving output file name");
				System.out.println("Debug Server: Got file name = " + userinput_destination);
			}
			System.out.println("Output file: " + userinput_destination);
			
			//waits for client to send information on file size before printing it out
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Receiving file size");
			fileSize = in.readLine();
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Got file size = " + fileSize);
			System.out.println("File size: " + fileSize);
			fileSize_int = Integer.parseInt(fileSize);
			
			//waits for client to send the ciphertext before decrypting it. 
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Receiving file");
			byte[] incom = new byte[fileSize_int];
			cipherIn.read(incom); //reads the ciphertext via a dataInputStream
			killTime(); //time delay
			if (debugIsOn == true){ /*debug message*/
				toHexDisplay(incom);
			
				System.out.println("Debug Server: Decrypting file");
			}
			successfulDecrypt = fileDecrypt.decryptFile(incom, userinput_destination, userinput_key); //decrypts file, returns success/fail
			
			//prints out a success/fail message. 
			if (successfulDecrypt == true) {
				if (debugIsOn == true) /*debug message*/
					System.out.println("Debug Server: Sending \"passed\" acknowledgement.");
				System.out.println("File written successfully.");
			} else {
				if (debugIsOn == true) /*debug message*/
					System.out.println("Debug Server: Sending \"failed\" acknowledgement.");
				System.out.println("File written unsuccessfully.");
			}
			
			//sends message to client whether success/fail decryption + write
			out.println(successfulDecrypt);
			out.flush();
			
			//informs that current client is being killed
			System.out.println("Killing Client");
			
			} catch (Exception e) {
		}
    }
	
	//provides a time delay
	private void killTime(){
		try {
			Thread.sleep(500L);    // 0.5 seconds
			}
		catch (Exception e) {}
	}
	
	//displays input in hex value to the screen. 
	private void toHexDisplay(byte[] input)
	{
		StringBuilder strbuild = new StringBuilder();
		for (byte by : input)
		{
			strbuild.append(String.format("%02X:", by));
		}
		strbuild.deleteCharAt(strbuild.length()-1);
		System.out.println(strbuild.toString());
	}
}
