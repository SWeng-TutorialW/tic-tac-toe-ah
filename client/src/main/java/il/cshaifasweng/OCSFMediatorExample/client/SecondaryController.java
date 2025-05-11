package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    /**
     * Sample Skeleton for 'secondary.fxml' Controller Class
     */


    @FXML // fx:id="Btn00"
    private Button Btn00; // Value injected by FXMLLoader

    @FXML // fx:id="Btn01"
        private Button Btn01; // Value injected by FXMLLoader

        @FXML // fx:id="Btn02"
        private Button Btn02; // Value injected by FXMLLoader

        @FXML // fx:id="Btn10"
        private Button Btn10; // Value injected by FXMLLoader

        @FXML // fx:id="Btn11"
        private Button Btn11; // Value injected by FXMLLoader

        @FXML // fx:id="Btn12"
        private Button Btn12; // Value injected by FXMLLoader

        @FXML // fx:id="Btn20"
        private Button Btn20; // Value injected by FXMLLoader

        @FXML // fx:id="Btn21"
        private Button Btn21; // Value injected by FXMLLoader

        @FXML // fx:id="Btn22"
        private Button Btn22; // Value injected by FXMLLoader

        @FXML // fx:id="BtnNew"
        private Button BtnNew; // Value injected by FXMLLoader

        @FXML // fx:id="Grid"
        private GridPane Grid; // Value injected by FXMLLoader

        @FXML // fx:id="LabelTurn"
        private Label LabelTurn; // Value injected by FXMLLoader


}