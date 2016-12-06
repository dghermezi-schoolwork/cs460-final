package cs460final;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

// This program generates a 2048 bit RSA key pair
public class GenerateKeyPair {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		
		if (args.length != 2) {
			System.out.println("Incorrect usage of program! Must pass two command line argument!");
			System.out.println("First argument: desired public key file name (relative path).");
			System.out.println("Second argument: desired private key file name (relative path).");
			System.exit(0);
		}
		
		//generates the RSA keypair
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		KeyPair keyPair = keyGen.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		
		String publicName = args[0];
		String privateName = args[1];
		
		// sets up the output streams
		FileOutputStream publicfos = new FileOutputStream(publicName);
		FileOutputStream privatefos = new FileOutputStream(privateName);
		ObjectOutputStream publicoos = new ObjectOutputStream(publicfos);
		ObjectOutputStream privateoos = new ObjectOutputStream(privatefos);
		
		
		// writes the keys to their respective files
		publicoos.writeObject(publicKey);
		publicoos.close();
		publicfos.close();
		
		privateoos.writeObject(privateKey);
		privateoos.close();
		privatefos.close();
	}
}
