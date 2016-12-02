import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.Scanner;

public class Sender {
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		if (args.length != 1) {
			System.out.println("Incorrect usage of program! Must pass one command line argument!");
			System.out.println("Argument: reciever's public key.");
			System.exit(0);
		}

		// gets public key from file.
		try {
			ObjectInputStream publicois = new ObjectInputStream(new FileInputStream(new File(args[0])));
			PublicKey publicKey = (PublicKey) publicois.readObject();
			publicois.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found! Make sure you are using the correct file path.");
			e.printStackTrace();
			System.exit(0);
		}

		menu();
	}

	public static void menu() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Would you like to send a file, or send a typed message?");
		System.out.println("0: File");
		System.out.println("1: Typed message");

		String input = sc.nextLine();
		sc.close();
		if (input.equals("0")) {
			sendFile();
		} else if (input.equals("1")) {
			sendMessage();
		} else {
			System.out.println("Invalid option!");
			menu();
		}

	}
	
	public static void sendFile() {
		
	}
	
	public static void sendMessage() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Type a message to be sent: ");
		String message = sc.nextLine();
		byte[] byteMessage = message.getBytes();
		sc.close();
		
		encode(byteMessage);
	}
	
	public static void encode(byte[] data) {
		
	}

}
