import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Client_22_5 {
	
	public static void main(String args[]) throws InterruptedException {
		Socket socket = null;
		try {
			
			int serverPort = 50000;
			String hostname = args[2];
			socket = new Socket(hostname, serverPort);
			SecretKeySpec secretKey = SecurityUtil_22_5.generateAESKey();
			Cipher cipher = Cipher.getInstance("AES");

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

				String username = args[0];
				String password = args[1];
				String hashedMsg = StringUtil_22_5.applySha256(username + password);
				String encryptedMsg = SecurityUtil_22_5.encrypt(username + password, cipher, secretKey);
				
				long clientExecTimeStart = System.currentTimeMillis();
				long clientLoginTimeStart = System.currentTimeMillis();
				
				out.writeUTF(username + password);
				out.writeUTF(hashedMsg);
				out.writeUTF(encryptedMsg);

				System.out.println("Client id: " + encryptedMsg);
				
				String clientNew = in.readUTF();
				
				long clientLoginTimeEnd = System.currentTimeMillis();
				
				long totalLoginTime = clientLoginTimeEnd - clientLoginTimeStart;
				
				System.out.println("Response time of the server: " + totalLoginTime);

				System.out.println("Client new or not: " + clientNew);
				
				
				if (clientNew.equals("New client")) {

					
					String queuePosition = in.readUTF();
					System.out.println(queuePosition);

					int queueSize = in.read();
					queueSize = queueSize * 3;
					
					while (queueSize > 0) {
						String waitTime = in.readUTF();
						System.out.println(waitTime);
						queueSize--;
					}

					String encryptedServerMsg = in.readUTF();

					String decryptedMsg = SecurityUtil_22_5.decrypt(encryptedServerMsg, cipher, secretKey);
					System.out.println("Received: " + decryptedMsg);
					System.out.println(encryptedServerMsg);
					
					String userState = in.readUTF(); 
					System.out.println(userState);

					String eventStart = in.readUTF();
					System.out.println("Event state: " + eventStart);

					for (int i = 0; i < 5; i++) {
						int waitEvent = in.readInt();
						out.writeUTF("Connected");
						System.out.println("Time until event finishes: " + waitEvent);
						Thread.sleep(1000);
					}

					String eventEnd = in.readUTF();
					System.out.println("Event state: " + eventEnd);
					
					
				} else {
					
					String state = in.readUTF();
					System.out.println(state);

					int timeLeft = in.readInt();
					for (int i = 0; i < timeLeft; i++) {
						int eventTime = in.readInt();
						System.out.println("Event time left: " + eventTime);
						Thread.sleep(1000);
					}
				}
				
				
				long clientExecTimeEnd = System.currentTimeMillis();
			
				long totalExecTime = clientExecTimeEnd - clientExecTimeStart;
			
				System.out.println("Total execution time for the system: " + totalExecTime);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("Error close:" + e.getMessage());
				}
			}
		}
	}
}