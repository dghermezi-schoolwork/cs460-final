import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Reciever {
	
	public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, ClassNotFoundException, IOException {
		
		if (args.length != 1) {
			System.out.println("Incorrect usage of program! Must pass one command line argument!");
			System.out.println("Argument: reciever's private key.");
			System.exit(0);
		}
		
		// gets private key from file
		try {
			ObjectInputStream privateois = new ObjectInputStream(new FileInputStream(new File(args[0])));
			PrivateKey privateKey = (PrivateKey) privateois.readObject();
			privateois.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found! Make sure you are using the correct file path.");
			e.printStackTrace();
			System.exit(0);
		}

		
	}
	
}
