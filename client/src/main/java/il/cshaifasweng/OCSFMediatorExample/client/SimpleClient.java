package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	public static String playerSymbol = "";  // "X" or "O"
	public static boolean isMyTurn = false;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	public static SimpleClient getClient(String host, int port) {
		if (client == null) {
			client = new SimpleClient(host, port);
		}
		return client;
	}

	// event bus
	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg)); // all the text and messages show here
		}
		else {
			String message = msg.toString();
			System.out.println("Message from server: " + message);

			if (message.startsWith("ROLE:")) {
				playerSymbol = message.substring(5);  // Extract "X" or "O"
				System.out.println("Assigned player symbol: " + playerSymbol);
				// Optionally post an EventBus event or update UI from GUI controller
			}
			else if(message.startsWith("TURN:")) {
				Platform.runLater(() -> {
					try {
						PrimaryController.setWaiting(false);
						PrimaryController.switchToSecondary();

					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				 if (message.equals("TURN:YOUR")) {
					isMyTurn = true;
					System.out.println("It's your turn.");
					// Notify UI to allow move
				} else if (message.equals("TURN:WAIT")) {
					isMyTurn = false;
					System.out.println("Waiting for opponent...");
					// Notify UI to block input
				}
			}

			else if (message.equals("RESET")) {
				isMyTurn = false;  // reset turn (server will resend TURN:YOUR to the starter)
				System.out.println("Game reset.");
				// Notify GUI controller to clear the board
				EventBus.getDefault().post("RESET");
			}

			else if (message.equals("Reject")) {
				System.out.println("Connection rejected by server.");
				// Show alert or close app
			}

			else if (message.startsWith("score:")) {
				EventBus.getDefault().post(message);  // e.g., "score:1"
			} else if (message.startsWith("update")) {
				EventBus.getDefault().post(message);

			} else {
				System.out.println("Unhandled message: " + message);
			}
		}
	}
	
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

}
