List of files submitted:
	- Client.java
		--> Connects to the server and will send an encrypted file to said server. 
	- CryptoUtilities.java
		--> Provides all the necessary methods needed to perform encryption/decryption in the secureFile and decryptFile
		--> Virtually untouched from originally provided file
	- decryptFile.java
		--> decrypts a given file (whose input will come from the ServerThread)
	- secureFile.java
		--> encrypts a given file (whose input will be from the user inputting it from the Client file)
	- Server.java 
		--> Essentially opens up a server thread and listens for clients. For our purposes will also set/get debug flag
		--> Mainly unmodified from original provided file.
	- ServerThread.java
		--> Connects with the client and will receive an encrypted file from the client and will send acknowledgement in the event that the decryption passes/fails
		
How to compile:
	- compile using the provided Makefile please
	
Known bugs:
	- Must use 4-digit socket? 
	- By that I mean that when I tried with a socket within 0-100 it didn't work. 
	
File Transfer Protocol:
	- Protocol messages are sent through the PrintWriter. They are parsed as strings and (if necessary) converted to an integer.
	- The data is encrypted and a HMAC-SHA-1 message digest is also written with the encrypted data as to provide integrity.
	- Encryption is protected such that should the input keys not match up, then the resulting file will not be decrypted. 
