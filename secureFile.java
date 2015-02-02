/***********************************************************
* Name: Emily Chow
* Course: CPSC 418					Term: Fall 2014
* Assignment: 2
*
* Class name: secureFile.java
*
************************************************************/

import java.io.*;
import javax.crypto.spec.*;

/**
 * This program performs the following cryptographic operations on the input file:
 *   - computes a random 128-bit key (1st 16 bits of SHA-1 hash of a user-supplied seed)
 *   - computes a HMAC-SHA1 hash of the file's contents
 *   - encrypts the file+hash using AES-128-CBC
 *   - outputs the encrypted data
 *
 * Compilation:    javac secureFile.java
 * Execution: java secureFile [plaintext-filename] [ciphertext-filename] [seed]
 *
 * @author Mike Jacobson
 * @version 1.0, September 25, 2013
 */
public class secureFile{
	
	//A dependent method that is called from the Client class to encrypt a file. 
	//Returns the ciphertext file as an array of bytes as output.
    public byte[] secureFile(byte[] inputFile, String seed) throws Exception {
		byte[] aes_ciphertext = null;	//resulting ciphertext
		byte[] hashed_msg; //hashed message + digest
		SecretKeySpec key;	// key 
		
		try{
			// compute key:  1st 16 bytes of SHA-1 hash of seed
			key = CryptoUtilities.key_from_seed(seed.getBytes());

			// append HMAC-SHA-1 message digest
			hashed_msg = CryptoUtilities.append_hash(inputFile,key);

			// do AES encryption
			aes_ciphertext = CryptoUtilities.encrypt(hashed_msg,key);
			
		}
		catch(Exception e){
			System.out.println(e);
		}
		return aes_ciphertext;
    }

}
