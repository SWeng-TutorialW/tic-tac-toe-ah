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
			boardButtons = new Button[][] {
					{ Btn00, Btn01, Btn13 },
					{ Btn10, Btn11, Btn23},
					{ Btn20, Btn21, Btn33 }
			};
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Button[][] boardButtons;

	private int MyScore = 0;

	private int OtherScore = 0;

	@FXML // fx:id="Btn00"
	private Button Btn00; // Value injected by FXMLLoader

	@FXML // fx:id="Btn01"
	private Button Btn01; // Value injected by FXMLLoader

	@FXML // fx:id="Btn10"
	private Button Btn10; // Value injected by FXMLLoader

	@FXML // fx:id="Btn11"
	private Button Btn11; // Value injected by FXMLLoader

	@FXML // fx:id="Btn13"
	private Button Btn13; // Value injected by FXMLLoader

	@FXML // fx:id="Btn20"
	private Button Btn20; // Value injected by FXMLLoader

	@FXML // fx:id="Btn21"
	private Button Btn21; // Value injected by FXMLLoader

	@FXML // fx:id="Btn23"
	private Button Btn23; // Value injected by FXMLLoader

	@FXML // fx:id="Btn33"
	private Button Btn33; // Value injected by FXMLLoader

	@FXML // fx:id="BtnSt"
	private Button BtnSt; // Value injected by FXMLLoader

	@FXML // fx:id="LbScor"
	private Label LbScor; // Value injected by FXMLLoader

	@FXML // fx:id="TFOPP"
	private TextField TFOPP; // Value injected by FXMLLoader

	@FXML // fx:id="TFTXT"
	private TextField TFTXT; // Value injected by FXMLLoader

	@FXML // fx:id="TFYou"
	private TextField TFYou; // Value injected by FXMLLoader

	@FXML
	private void assignXO(ActionEvent event) {
		Button clicked = (Button) event.getSource();

		if (!SimpleClient.isMyTurn) return;
		if (!clicked.getText().isEmpty()) return;

		clicked.setText(SimpleClient.playerSymbol);
		clicked.setDisable(true);

		setBoardEnabled(false);

	}

	private void clearBoard() {
		for (Button[] row : boardButtons) {
			for (Button cell : row) {
				cell.setText("");
				cell.setDisable(false);  // make sure they're clickable again
			}
		}
	}


	private void setBoardEnabled(boolean enabled) {
		for (Button[] row : boardButtons) {
			for (Button cell : row) {
				if (cell.getText().isEmpty()) {
					cell.setDisable(!enabled);
				}
			}
		}
	}

}


