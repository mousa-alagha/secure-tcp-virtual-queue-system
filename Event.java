import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;

public class Event_22_5 {

	static LinkedList<String> eventQueue = new LinkedList<>();
	static Hashtable<String, Integer> clientInEvent = new Hashtable();

	public static void main(String args[]) {

		try {
			
			eventQueue = new LinkedList<>();
			
			System.out.println("Waiting for Server to connect ... ");
			ServerSocket listenSocket = new ServerSocket(60000);
			Socket serverSocket = listenSocket.accept();
			
			DataInputStream inServer = new DataInputStream(serverSocket.getInputStream());
			DataOutputStream outServer = new DataOutputStream(serverSocket.getOutputStream());
			System.out.println("Connected with Server ... ");

			while (true) {
				
				String clientID = inServer.readUTF();
				
				eventQueue.add(clientID);
				
				Collections.shuffle(eventQueue);
				
				int time = 5;

				String eventStart = "Event started";
				clientInEvent.put(clientID, time);

				System.out.println("Event started");
				outServer.writeUTF(eventStart);

				for (int i = 5; i > 0; i--) {
					System.out.println("Event time left: " + time);
					time--;
					clientInEvent.put(clientID, time);
					outServer.writeInt(i);
					Thread.sleep(1000);
				}

				String eventEnd = "Event ended";
				System.out.println("Event ended");
				outServer.writeUTF(eventEnd);
				eventQueue.remove(clientID);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {



		}
	}
}