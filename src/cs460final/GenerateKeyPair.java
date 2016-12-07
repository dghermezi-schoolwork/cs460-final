package cs460final;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

// This program generates an RSA key pair
public class GenerateKeyPair {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		
		// Check command line arguments
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
		
		// Gets the file names from the command line arguments
		String publicName = args[0];
		String privateName = args[1];
		
		// Sets up the output streams
		FileOutputStream publicfos = new FileOutputStream(publicName);
		FileOutputStream privatefos = new FileOutputStream(privateName);

		// Encodes the keys into a byte array
		byte[] publicKeyBytes = publicKey.getEncoded();
		byte[] privateKeyBytes = privateKey.getEncoded();
		
		// Writes the encoded keys to the files
		publicfos.write(publicKeyBytes);
		System.out.println("Public key saved to file: " + publicName);
		publicfos.close();
		
		privatefos.write(privateKeyBytes);
		System.out.println("Private key saved to file: " + privateName);
		privatefos.close();
		
	}
}
