/***********************************************************
* Name: Emily Chow
* Course: CPSC 418					Term: Fall 2014
* Assignment: 2
*
* Class name: decryptFile.java
*
************************************************************/

import java.io.*;
import javax.crypto.spec.*;

/**
 * This program performs the following cryptographic operations on the input file:
 *   - computes a random 128-bit key (1st 16 bits of SHA-1 hash of a user-supplied seed)
 *   - decrypts the file
 *   - extracts a HMAC-SHA1 digest of the original file contents (from the end of the
 *     decrypted data)
 *   - computes the HMAC-SHA1 digest of the decrypted file contents
 *   - outputs the encrypted data if the computed and decrypted digests are equal
 *
 * Compilation:    javac decryptFile.java
 * Execution: java decryptFile [plaintext-filename] [ciphertext-filename] [seed]
 *
 * @author Mike Jacobson
 * @version 1.0, September 25, 2013
 */
public class decryptFile{

	//A dependent class that is called from the ServerThread that will decrypt a file
	//which will be inputted as an array of bytes, returning a statement that the resulting
	//plaintext is properly written or not. 
    public boolean decryptFile (byte[] ciphertext, String destFilename, String seed){
		FileOutputStream out_file = null; //opens outfile to write.
		Boolean plainWritten = false;	//boolean to determine whether it writ properly or not. 

		try{
			// open output file
			out_file = new FileOutputStream(destFilename);

			// compute key:  1st 16 bytes of SHA-1 hash of seed
			SecretKeySpec key = CryptoUtilities.key_from_seed(seed.getBytes());

			// do AES decryption
			byte[] hashed_plaintext = CryptoUtilities.decrypt(ciphertext,key);

			// verify HMAC-SHA-1 message digest and output plaintext if valid
			if (CryptoUtilities.verify_hash(hashed_plaintext,key)) {
				System.out.println("Message digest OK. Writing file");

				// extract plaintext and output to file
				byte[] plaintext = CryptoUtilities.extract_message(hashed_plaintext);
				out_file.write(plaintext);
					out_file.close();
				plainWritten = true;	//sets the boolean to true because the plaintext would have written properly. 
			}
			else
				System.out.println("ERROR:  invalid message digest!");
		}
		catch(Exception e){
			System.out.println(e);
		}
		return plainWritten;
    }
	
}
