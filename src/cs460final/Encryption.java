package cs460final;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// This class handles all the encryption, decryption, and HMAC calculations
// The method names should sufficiently explain what each method does.
public class Encryption {

	public static byte[] encryptAES(byte[] data, SecretKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		return cipher.doFinal(data);
	}

	public static byte[] decryptAES(byte[] encryptedData, SecretKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		return cipher.doFinal(encryptedData);
	}

	public static byte[] decryptRSA(byte[] encryptedData, PrivateKey RSAKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, RSAKey);

		return cipher.doFinal(encryptedData);
	}

	public static byte[] encryptRSA(byte[] data, PublicKey RSAKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, RSAKey);

		return cipher.doFinal(data);
	}

	public static byte[] calculateHMAC(byte[] message, String key) throws Exception {
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(signingKey);
		
		return mac.doFinal(message);
	}
}
