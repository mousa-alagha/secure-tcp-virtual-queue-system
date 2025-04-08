import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class SecurityUtil_22_5 {

	 public static SecretKeySpec generateAESKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        KeySpec spec = new PBEKeySpec("MySecretKey".toCharArray(), "salt".getBytes(), 65536, 128);
	        SecretKey tmp = factory.generateSecret(spec);
	        return new SecretKeySpec(tmp.getEncoded(), "AES");
	    }

	    public static String encrypt(String data, Cipher cipher, SecretKeySpec secretKey)
	            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
	        return Base64.getEncoder().encodeToString(encryptedBytes);
	    }

	    public static String decrypt(String data, Cipher cipher, SecretKeySpec secretKey)
	            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
	        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data));
	        return new String(decryptedBytes);
	    }
}
