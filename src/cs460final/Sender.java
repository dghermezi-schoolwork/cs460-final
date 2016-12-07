package cs460final;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Sender {

	private static SecretKey AESKey; // The AES key used for encrypting the data.
	private static PublicKey publicKey; // The RSA public key used for encrypting the AES key
	static String hmacKey; // The key used for calculated HMAC.

	public static void main(String[] args) throws Exception {

		// If incorrect amount of command line arguments
		if (args.length != 2) {
			System.out.println("Incorrect usage of program! Must pass one command line argument!");
			System.out.println("Argument 1: reciever's public key.");
			System.out.println("Argument 2: HMAC key");
			System.exit(0);
		}

		// Gets the HMAC key from command line
		hmacKey = args[1];
		
		// Gets public key from file
		getPublicKeyFromFile(args[0]);

		// Generates an AES key
		KeyGenerator AESKeyGen = KeyGenerator.getInstance("AES");
		AESKeyGen.init(128);
		AESKey = AESKeyGen.generateKey();
		
		menu();
	}

	// The main menu presented to the sender
	public static void menu() throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Would you like to send a file, or send a typed message?");
		System.out.println("0: File");
		System.out.println("1: Typed message");

		String input = sc.nextLine();
		if (input.equals("0")) {
			sendFile(AESKey);
		} else if (input.equals("1")) {
			sendMessage(AESKey);
		} else {
			System.out.println("Invalid option!");
			menu();
		}

	}

	// This method uses a file as it's input to create the output
	public static void sendFile(SecretKey key) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the path to the file: ");
		String path = sc.nextLine();

		// sets up the byte array to read the file into.
		byte[] data;

		try {
			// Outputs the file to byte array
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

		} catch (FileNotFoundException e) {
			System.out.println("Invalid file path!");
			e.printStackTrace();
			menu();
			return;
		}

		// Calculate the HMAC using the HMAC key
		byte[] hmac = Encryption.calculateHMAC(data, hmacKey);
		// Encrypt the data using the AES key
		byte[] encryptedData = Encryption.encryptAES(data, key);
		
		// append "encrypted_" to the file name and save
		String newPath = "encrypted_" + path;
		saveEncryptedDataToFile(encryptedData, hmac, key, newPath);

	}

	// Simialr to the above method, but uses System.in instead of a file.
	public static void sendMessage(SecretKey key) throws Exception {
		
		// Read input from keyboard and convert to byte array
		Scanner sc = new Scanner(System.in);
		System.out.println("Type a message to be sent: ");
		String message = sc.nextLine();
		byte[] byteMessage = message.getBytes();
		sc.close();

		// Same as before, get hmac and encrypt the data
		byte[] hmac = Encryption.calculateHMAC(byteMessage, hmacKey);
		byte[] encryptedData = Encryption.encryptAES(byteMessage, key);

		// These messages by default just get saved to a file called "encrypted_message"
		saveEncryptedDataToFile(encryptedData, hmac, key, "encrypted_message");
	}

	// Takes the encrypted data, the encrypted AES key, and the HMAC and writes it to a file (simulates sending).
	public static void saveEncryptedDataToFile(byte[] encryptedData, byte[] hmac, SecretKey key, String path) throws Exception {
		System.out.println("Saving encrypted data...");

		// First, encrypt the AES key using the public RSA key.
		byte[] encryptedKey = Encryption.encryptRSA(key.getEncoded(), publicKey);
		
		// The data part of our message is the encrypted data + the encrypted key
		byte[] data = new byte[encryptedData.length + encryptedKey.length];

		// Fill the first part with the encrypted data
		for (int i = 0; i < encryptedData.length; i++) {
			data[i] = encryptedData[i];
		}

		// Append the encrypted key to the end
		for (int i = 0; i < encryptedKey.length; i++) {
			data[i + encryptedData.length] = encryptedKey[i];
		}

		// Now we add the HMAC to the end of that as well
		byte[] totalData = new byte[data.length + hmac.length];
		for (int i = 0; i < data.length; i++) {
			totalData[i] = data[i];
		}
		for (int i = 0; i < hmac.length; i++) {
			totalData[i + data.length] = hmac[i];
		}

		// And write it to the file
		FileOutputStream fos = new FileOutputStream(new File(path));
		fos.write(totalData);
		fos.close();
		System.out.println("Saved!");
	}
	
	// Gets the RSA public key from the file in the command line argument.
	public static void getPublicKeyFromFile(String path) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		try {
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			byte[] publicKeyBytes = new byte[(int) file.length()]; // Setup the byte array
			fis.read(publicKeyBytes); // Read the file into the byte array
			publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes)); // Create the key from the byte array
			fis.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Public key file not found! Make sure you are using the correct file path.");
			e.printStackTrace();
			System.exit(0);
		}
	}

}
