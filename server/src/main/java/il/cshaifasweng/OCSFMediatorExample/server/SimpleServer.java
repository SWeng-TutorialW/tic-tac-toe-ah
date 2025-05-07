package il.cshaifasweng.OCSFMediatorExample.server;
// we dont use event buse here, there is no need
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	private final Map<ConnectionToClient, String> playerSymbols = new HashMap<>();
	private boolean gameStarted = false;


	// constructor
	public SimpleServer(int port) {
		super(port); // send the port
		
	}

	// everything from the client happens here
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning); // we send the object to the client
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(msgString.startsWith("add client")){
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				if(playerSymbols.size() >= 2){
					client.sendToClient("Reject");
					client.close();
					return;
				}

				String symbol = playerSymbols.size() == 0 ? "X" : "O";
				playerSymbols.put(client, symbol);
				client.sendToClient("Role:" + symbol);

				if(playerSymbols.size() == 2 && !gameStarted){
					gameStarted = true;
					ArrayList<ConnectionToClient> players = new ArrayList<>(playerSymbols.keySet());
					int rand = (int) (Math.random() * 2);
					ConnectionToClient starter = players.get(rand);
					ConnectionToClient other = players.get(1 - rand);

					starter.sendToClient("TURN:YOUR");
					other.sendToClient("TURN:WAIT");
					System.out.println("Game started. Starter: " + playerSymbols.get(starter));
				}

				client.sendToClient("client added successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if(msgString.startsWith("remove client")){
			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}else if (msgString.equals("reset game")) {
			gameStarted = false;

			ArrayList<ConnectionToClient> players = new ArrayList<>(playerSymbols.keySet());
			int rand = (int) (Math.random() * 2);
			ConnectionToClient starter = players.get(rand);
			ConnectionToClient other = players.get(1 - rand);

			try {
				// Send reset notification
				sendToAllClients("RESET");

				// Send new turn assignment
				starter.sendToClient("TURN:YOUR");
				other.sendToClient("TURN:WAIT");

				gameStarted = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// send to all the clients
	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
