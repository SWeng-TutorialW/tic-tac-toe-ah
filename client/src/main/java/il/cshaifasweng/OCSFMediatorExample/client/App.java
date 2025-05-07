package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private SimpleClient client;

    @Override
    public void start(Stage stage) throws IOException {

        TextInputDialog ipDialog = new TextInputDialog("127.0.0.1");
        ipDialog.setTitle("Server Connection");
        ipDialog.setHeaderText("Enter Server IP Address");
        ipDialog.setContentText("IP:");

        Optional<String> ipResult = ipDialog.showAndWait();
        if (!ipResult.isPresent()) {
            System.out.println("No IP entered. Exiting.");
            Platform.exit();
            return;
        }

        TextInputDialog portDialog = new TextInputDialog("3000");
        portDialog.setTitle("Server Connection");
        portDialog.setHeaderText("Enter Server Port");
        portDialog.setContentText("Port:");

        Optional<String> portResult = portDialog.showAndWait();
        if (!portResult.isPresent()) {
            System.out.println("No port entered. Exiting.");
            Platform.exit();
            return;
        }

        String ip = ipResult.get();
        int port = Integer.parseInt(portResult.get());

        EventBus.getDefault().register(this);

        // Create and connect the client
        client = SimpleClient.getClient();
        client.setHost(ip);
        client.setPort(port);
        client.openConnection();

	    // we request toask teh client to write the ip and the port, and we takes this and use it in the getclient
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    

    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
        client.sendToServer("remove client");
        client.closeConnection();
		super.stop();
	}
    
    @Subscribe
    public void onWarningEvent(WarningEvent event) {
    	Platform.runLater(() -> {
    		Alert alert = new Alert(AlertType.WARNING,
        			String.format("Message: %s\nTimestamp: %s\n",
        					event.getWarning().getMessage(),
        					event.getWarning().getTime().toString())
        	);
        	alert.show();
    	});
    	
    }

	public static void main(String[] args) {
        launch();
    }

}
