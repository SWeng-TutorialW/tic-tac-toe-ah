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
	private String[][] board = new String[3][3];


	private final Map<ConnectionToClient, Integer> playerScores = new HashMap<>();



	private final Map<ConnectionToClient, String> playerSymbols = new HashMap<>();
	private boolean gameStarted = false;

	private boolean OTurn = true;


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

				playerScores.put(client, 0);


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

			playerScores.remove(client);
			playerSymbols.remove(client);

			System.out.println("Client removed. Remaining clients: " + SubscribersList.size());

			if (SubscribersList.isEmpty()) {
				System.out.println("All clients disconnected. Shutting down server...");
				try {
					close();  // closes server socket and listening thread
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);  // optional: kill JVM
			}

		}else if (msgString.equals("reset game")) {
			gameStarted = false;

			// Clear the game board
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					board[i][j] = null;
				}
			}

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
		}else if (msgString.startsWith("choose")) {
			handleMove(msgString, client);
		}else if(msgString.startsWith("ready")){
			try {
				if (playerSymbols.size() == 2 && !gameStarted) {
					gameStarted = true;
					ArrayList<ConnectionToClient> players = new ArrayList<>(playerSymbols.keySet());
					int rand = (int) (Math.random() * 2);
					ConnectionToClient starter = players.get(rand);
					ConnectionToClient other = players.get(1 - rand);

					starter.sendToClient("TURN:YOUR");
					other.sendToClient("TURN:WAIT");
					System.out.println("Game started. Starter: " + playerSymbols.get(starter));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleMove(String msgString, ConnectionToClient client) {
		String[] parts = msgString.trim().split(" ");
		int row = Integer.parseInt(parts[2]);
		int col = Integer.parseInt(parts[3]);

		if (!isPlayerTurn(client)) return;
		if (!isCellEmpty(row, col)) return;

		String playerSymbol = OTurn ? "X" : "O";
		board[row][col] = playerSymbol;

		String nextPlayer = playerSymbol.equals("X") ? "O" : "X";
		String updateMessage = "update board " + row + " " + col + " " + playerSymbol + " Turn " + nextPlayer;
		sendToAllClients(updateMessage);

		if (checkWin()) {
			sendToAllClients("done " + row + " " + col + " " + playerSymbol);

			int currentScore = playerScores.getOrDefault(client, 0);
			playerScores.put(client, currentScore + 1);

			for (ConnectionToClient player : playerScores.keySet()) {
				try {
					int score = playerScores.get(player);
					player.sendToClient("score:" + score);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else if (isFullBoard()) {
			sendToAllClients("over " + row + " " + col + " " + playerSymbol);
		} else {
			OTurn = !OTurn;
		}
	}



	// check if a player won
	private boolean checkWin() {
		// Check rows
		for (int row = 0; row < 3; row++) {
			if (board[row][0] != null &&
					board[row][0].equals(board[row][1]) &&
					board[row][1].equals(board[row][2])) {
				return true;
			}
		}

		// Check columns
		for (int col = 0; col < 3; col++) {
			if (board[0][col] != null &&
					board[0][col].equals(board[1][col]) &&
					board[1][col].equals(board[2][col])) {
				return true;
			}
		}

		// Check main diagonal
		if (board[0][0] != null &&
				board[0][0].equals(board[1][1]) &&
				board[1][1].equals(board[2][2])) {
			return true;
		}

		// Check anti-diagonal
		if (board[0][2] != null &&
				board[0][2].equals(board[1][1]) &&
				board[1][1].equals(board[2][0])) {
			return true;
		}

		return false;
	}



	// check the board if its full
	private boolean isFullBoard() {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (board[row][col] == null) {
					return false;  // found an empty cell
				}
			}
		}
		return true;  // all cells are filled
	}



	private boolean isPlayerTurn(ConnectionToClient client) {
		String role = playerSymbols.get(client);  // "X" or "O"

		if (role == null) return false;

		return (OTurn && role.equals("X")) || (!OTurn && role.equals("O"));
	}


	private boolean isCellEmpty(int row, int col) {
		return board[row][col] == null;
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
