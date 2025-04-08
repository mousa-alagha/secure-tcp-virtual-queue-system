import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;

public class Server_22_5 {

	static LinkedList<Socket> prequeue = new LinkedList<>();
	static Hashtable<String, String> clientsLogin = new Hashtable();
	static Hashtable<String, String> clientsEvent = new Hashtable();
	static String clientid;

	public static void main(String args[]) throws InterruptedException {

		try {
			prequeue = new LinkedList<>();
			int serverPort = 50000;
			ServerSocket listenSocket = new ServerSocket(serverPort);

			System.out.println("Connecting to Event ... ");
			Socket eventSocket = new Socket(args[0], 60000);
			DataOutputStream outEvent = new DataOutputStream(eventSocket.getOutputStream());
			DataInputStream inEvent = new DataInputStream(eventSocket.getInputStream());
			System.out.println("Connected to Event ... ");
			
			System.out.println("Server is ready and waiting for requests ... ");

			while (true) {
				Socket clientSocket = listenSocket.accept();
				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
				Connection c = new Connection(clientSocket, outEvent, inEvent);

			}
		} catch (IOException e) {
			System.out.println("Error Listen socket: " + e.getMessage());
		}
	}
}

class Connection extends Thread {
	Socket clientSocket;
	DataInputStream in, inEvent;
	DataOutputStream out, outEvent;
	SecretKeySpec secretKey;
	Cipher cipher;

	public Connection(Socket aClientSocket, DataOutputStream outEvent, DataInputStream inEvent) {
		try {
			clientSocket = aClientSocket;
			this.outEvent = outEvent;
			this.inEvent = inEvent;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			secretKey = SecurityUtil_22_5.generateAESKey();
			cipher = Cipher.getInstance("AES");

			this.start();
		} catch (IOException e) {
			System.out.println("Error Connection: " + e.getMessage());

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			
			long loginTimeStart = System.currentTimeMillis();
			
			String login = in.readUTF();
			String hashedLoginReceived = in.readUTF();
			String encryptedMsg = in.readUTF();

			System.out.println("New client connected: " + clientSocket);

			Server_22_5.clientid = encryptedMsg;

			System.out.println("Clientid: " + Server_22_5.clientid);

			String clientNumber = Server_22_5.clientsLogin.get(Server_22_5.clientid);

			if (clientNumber == null) {
				
				out.writeUTF("New client");
				Server_22_5.prequeue.add(clientSocket);
				
				int position = Server_22_5.prequeue.indexOf(clientSocket) + 1;
				int waitTime = position * 3;

				System.out.println("Queue Size: " + Server_22_5.prequeue.size());

				out.writeUTF("Your position in the queue is: " + position + "\nThe queue size is: "
						+ Server_22_5.prequeue.size());

				out.write(Server_22_5.prequeue.size());

				while (waitTime > 0) {

					out.writeUTF("Estimated wait time is: " + waitTime + " s \n Your Position:" + position);
					waitTime--;
					Thread.sleep(1000);
				}
				
				String hashLogin = StringUtil_22_5.applySha256(login); 

				String decryptedMsg = SecurityUtil_22_5.decrypt(encryptedMsg, cipher, secretKey);
				
				String reEncryptedMsg = SecurityUtil_22_5.encrypt(decryptedMsg, cipher, secretKey);
				System.out.println("Received: (" + encryptedMsg + ") and decrypted to: (" + decryptedMsg + ")");
				out.writeUTF(reEncryptedMsg);

				System.out.println("Hash received from client: " + hashedLoginReceived);
				System.out.println("Hashed data: " + hashLogin);
				
				int compareHash = hashedLoginReceived.compareTo(hashLogin);

				if (compareHash == 0) {
					System.out.println("The hashes are equal");

				} else {
					System.out.println("The hashes are not equal");
				}
				
				long loginTimeEnd = System.currentTimeMillis();
				System.out.println("Login successful ");
				long loginTime = loginTimeEnd - loginTimeStart;
				System.out.println("Login time: " + loginTime);
				
				Server_22_5.clientsLogin.put(Server_22_5.clientid, hashLogin);
				Server_22_5.prequeue.remove(clientSocket);
				
				out.writeUTF("Waiting for event to start");

				
				outEvent.writeUTF(Server_22_5.clientid);
				
				String eventStart = inEvent.readUTF();
				System.out.println("Event state: " + eventStart);
				out.writeUTF(eventStart);
				
				for (int i = 0; i < 5; i++) {

					int time = inEvent.readInt();
					out.writeInt(time);
					String timeLeft = String.valueOf(time);
					String clientState = in.readUTF();
					System.out.println("Client state: " + clientState);

					if (clientState != null) {
						Server_22_5.clientsEvent.put(Server_22_5.clientid, timeLeft);
						System.out.println("Event time left: " + time);

						Thread.sleep(1000);
					}

				}

				String eventEnd = inEvent.readUTF();
				System.out.println("Event state: " + eventEnd);
				out.writeUTF(eventEnd);
				
			} else {
				
				out.writeUTF("No");
				out.writeUTF("Already in the event queue");

				String time = Server_22_5.clientsEvent.get(Server_22_5.clientid);
				int timeLeft = Integer.parseInt(time);
				out.writeInt(timeLeft);

				for (int i = timeLeft; i > 0; i--) {
					System.out.println("Event time left: " + i);
					out.writeInt(i);
					Thread.sleep(1000);

				}

			}

			System.out.println("------------------------------");

		} catch (EOFException e) {
			System.out.println("Error EOF: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error readline:" + e.getMessage());
			System.out.println("Client left or login failed");

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
			}
		}

	}
}
