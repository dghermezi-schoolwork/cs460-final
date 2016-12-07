package cs460final;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Scanner;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Receiver {

	private static PrivateKey privateKey;

	public static void main(String[] args) throws Exception {

		// Checks the command line arguments.
		if (args.length != 2) {
			System.out.println("Incorrect usage of program! Must pass one command line argument!");
			System.out.println("Argument 1: reciever's private key.");
			System.out.println("Argument 2: HMAC key");
			System.exit(0);
		}

		// Gets private key from file
		privateKey = getPrivateKeyFromFile(args[0]);
		
		// Gets some info from the user
		System.out.println("Enter the path to the encrypted file");
		Scanner sc = new Scanner(System.in);
		String path = sc.nextLine();
		
		System.out.println("Where do you want to save the decrypted message? Include the file name in the path. This will overwrite the file if it already exists!");
		String savePath = sc.nextLine();
		
		// Reads the encrypted file into a byte array
		File dataFile = new File(path);
		byte[] data = new byte[(int) dataFile.length()];
		FileInputStream datafis = new FileInputStream(dataFile);
		datafis.read(data);
		datafis.close();
		sc.close();

		// There will be three parts, the AES-encrypted message, the RSA-encrypted AES key, and the HMAC
		// We know the size of the encrypted key and the HMAC
		byte[] givenHMAC = new byte[32];
		byte[] encryptedAESKey = new byte[256];
		byte[] encryptedData = new byte[data.length - givenHMAC.length - encryptedAESKey.length];

		// The first part is the encrypted data
		for (int i = 0; i < encryptedData.length; i++) {
			encryptedData[i] = data[i];
		}
		
		// Next 256 bytes are the AES key (2048 bit key)
		for (int i = 0; i < encryptedAESKey.length; i++) {
			encryptedAESKey[i] = data[encryptedData.length + i];
		}
		
		// And finally, the 256 bit HMAC
		for (int i = 0; i < givenHMAC.length; i++) {
			givenHMAC[i] = data[encryptedData.length + encryptedAESKey.length + i];
		}

		// Get the AES key
		SecretKey AESKey = getAESKey(encryptedAESKey);
		// Use the AES key to decrypt the data
		byte[] rawData = Encryption.decryptAES(encryptedData, AESKey);
		// Calculate the HMAC on the unencrypted data
		byte[] calculatedHMAC = Encryption.calculateHMAC(rawData, args[1]);

		// Then check if the calculated HMAC matches the given HMAC
		if (verifyHMAC(givenHMAC, calculatedHMAC)) {
			
			// If so, save the decrypted data to the file
			saveDecryptedDataToFile(rawData, savePath);
			System.out.println("Here's the contets of the data. This might look like a mess if it wasn't originally a string!");
			System.out.println(new String(rawData));
		} else {
			System.out.println("HMAC did not match. Something went wrong!");
		}
	}

	// Gets the RSA private key from the command line argument
	private static PrivateKey getPrivateKeyFromFile(String path) throws Exception {
		PrivateKey key = null;
		
		// Same as before, use a FileInputStream to read to a byte array
		try {
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			byte[] privateKeyBytes = new byte[(int) file.length()];
			fis.read(privateKeyBytes);
			fis.close();
			
			// Conver the byte array to an RSA PrivateKey
			key = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
		} catch (FileNotFoundException e) {
			System.out.println("Private key file not found! Make sure you are using the correct file path.");
			e.printStackTrace();
			System.exit(0);
		}
		return key;
	}

	// Checks if the HMAC's match
	private static boolean verifyHMAC(byte[] givenHMAC, byte[] calculatedHMAC) {
		// If they aren't the same length, we know they don't match
		if (givenHMAC.length != calculatedHMAC.length) {
			return false;	
		} else {
			// Otherwise, go through byte by byte and see if each byte matches
			for (int i = 0; i < givenHMAC.length; i++) {
				if (givenHMAC[i] != calculatedHMAC[i]) {
					return false;
				}
			}
		}

		System.out.println("HMAC matches!");
		return true;
	}

	// Gets the RSA-encrypted AES key
	public static SecretKey getAESKey(byte[] encryptedAESKey) throws Exception {
		byte[] decryptedAESKey = Encryption.decryptRSA(encryptedAESKey, privateKey);

		// Converts the byte array to an AES SecretKey
		return new SecretKeySpec(decryptedAESKey, "AES");

	}

	// Saves the decrypted data to a file
	private static void saveDecryptedDataToFile(byte[] data, String savePath) throws IOException {
		File file = new File(savePath);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.close();
		
	}

}
