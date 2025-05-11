package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;


public class PrimaryController {

    @FXML
    void sendWarning(ActionEvent event) {
    	try {
			SimpleClient.getClient().sendToServer("#warning");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }



	@FXML
	void initialize(){
		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sample Skeleton for 'primary.fxml' Controller Class
	 */




		@FXML // fx:id="BtnStart"
		private Button BtnStart; // Value injected by FXMLLoader

		@FXML // fx:id="LabelWait"
		private Label LabelWait; // Value injected by FXMLLoader

		@FXML // fx:id="LabelWelcome"
		private Label LabelWelcome; // Value injected by FXMLLoader




}


