/***********************************************************
 Name: Emily Chow
* Course: CPSC 418					Term: Fall 2014
* Assignment: 2
*
* Class name: Client.java
*
************************************************************/

import java.io.*;
import java.net.*;

/**
 * Client program.  Connects to the server and sends file across
 */

public class Client 
{
    private Socket sock;  //Socket to communicate with.
	public static boolean debugIsOn = false; //debug flag status
	
    /**
     * Main method, starts the client.
     * @param args args[0] needs to be a hostname, args[1] a port number.
     */
    public static void main (String [] args) throws Exception
    {
		if (args.length != 2) {
			if (!args[2].equals("debug")) { //If the second argument is NOT debug, then the program will break. 
				System.out.println ("Usage: java Client hostname port#");
				System.out.println ("hostname is a string identifying your server");
				System.out.println ("port is a positive integer identifying the port to connect to the server");
				return;
			} else {
				debugIsOn = true;	//otherwise debug is on. 
			}
		}

		try {
			Client c = new Client (args[0], Integer.parseInt(args[1]), debugIsOn);
		}
		catch (NumberFormatException e) {
			System.out.println ("Usage: java Client hostname port#");
			System.out.println ("Second argument was not a port number");
			return;
		}
    }
	
    /**
     * Constructor, in this case does everything.
     * @param ipaddress The hostname to connect to.
     * @param port The port to connect to.
     */
    public Client (String ipaddress, int port, Boolean debugIsOn) throws Exception
    {
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)); //Allows us to get input from the keyboard.
		BufferedReader inFromServer = null;	//input from server
		secureFile fileSecure = new secureFile();	//allows us to use the methods in the secureFile class
		byte[] ciphertext;
		String userinput_key;
		String userinput_destination;
		String userinput_source;
		String strReturned = "";
		Boolean decryptionConfirmed = false;
		
		DataOutputStream cipherOut; //allows us to send the ciphertext to the server. 
		PrintWriter out; //allows us to send other text data to the server. 
			
		/* Try to connect to the specified host on the specified port. */
		try {
			sock = new Socket (InetAddress.getByName(ipaddress), port);
		}
		catch (UnknownHostException e) {
			System.out.println ("Usage: java Client hostname port#");
			System.out.println ("First argument is not a valid hostname");
			return;
		}
		catch (IOException e) {
			System.out.println ("Could not connect to " + ipaddress + ".");
			return;
		}
			
		/* Status info */
		System.out.println ("Connected to " + sock.getInetAddress().getHostAddress() + " on port " + port);
		try {
			//initializes both reading and writing from and to the server
			cipherOut = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
			out = new PrintWriter(sock.getOutputStream());
			inFromServer = new BufferedReader (new InputStreamReader(sock.getInputStream()));
		}
		catch (IOException e) {
			System.out.println ("Could not create output stream.");
			return;
		}
			
		try {
			//retrieves information from user.
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Getting key (seed) from user");
			System.out.print("Please enter seed for key derivation: ");
			userinput_key = stdIn.readLine();
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Starting file transfer");
			System.out.print("Please enter the source filename: ");
			userinput_source = stdIn.readLine();
			System.out.print("Please enter the destination filename: ");
			userinput_destination = stdIn.readLine();
			
			//sends filename to server. 
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Sending destination filename");
			out.println(userinput_destination);
			out.flush();
			
			//open input stream to read file and reads the file. 
			FileInputStream in_file = new FileInputStream(userinput_source);	// open input 
			byte[] msg = new byte[in_file.available()];		// read input file into a byte array
			int read_bytes = in_file.read(msg);
			
			//encrypts the file and stores it into a byte array called ciphertext
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Encrypting and sending file");
			ciphertext = fileSecure.secureFile(msg, userinput_key);
			
			//Sends file size to server. 
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Sending file size = " + ciphertext.length);
			out.println(ciphertext.length);
			out.flush();
			
			//time delay. 
			killTime();
			if (debugIsOn == true) { /*debug message*/
				toHexDisplay(ciphertext); 
			}
			
			//sends ciphertext to server. 
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Sending encrypted file.");
			cipherOut.write(ciphertext, 0, ciphertext.length); //sends ciphertext
			cipherOut.flush();
			
			//waits for a response from the server to confirm/deny proper decryption.
			//prints according message (pass/fail)
			if (debugIsOn == true) /*debug message*/
				System.out.println("Debug Server: Waiting for server acknowledgement");
			strReturned = inFromServer.readLine();
			if (strReturned.equals("true")) {
				decryptionConfirmed = true;
			}
			if (decryptionConfirmed == true) {
				if (debugIsOn == true) /*debug message*/
					System.out.println("Debug Server: Got acknowledgement = Passed");
				System.out.println("File received and verified.");
			} else {
				System.out.println("File not properly received and/or unverified properly.");
				if (debugIsOn == true) /*debug message*/
					System.out.println("Debug Server: Got acknowledgement = Failed");
			}
			
			//closes client. 
			System.out.println("Shutting down client.");
			cipherOut.close();
			stdIn.close();
			out.close();
			sock.close();
			
		} catch (IOException e) {
			System.out.println ("Could not read from input.");
			return;
		}		
    }
	
	//Makes a time delay of 0.5 seconds
	private void killTime(){
		try {
			Thread.sleep(500L);    // 0.5 seconds
			}
		catch (Exception e) {}
	}
	
	//Converts byte array input into hexadecimal and prints it out on the terminal
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
