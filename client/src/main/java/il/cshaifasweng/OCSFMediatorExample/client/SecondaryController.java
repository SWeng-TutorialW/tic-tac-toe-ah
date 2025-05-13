package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import java.io.IOException;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SecondaryController {

    private String[][] board = new String[3][3];
    private Button[][] buttons = new Button[3][3];

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    public SecondaryController(){
        EventBus.getDefault().register(this);
    }

    public void initialize() {
        this.buttons[0][0] = this.Btn00;
        this.buttons[0][1] = this.Btn01;
        this.buttons[0][2] = this.Btn02;
        this.buttons[1][0] = this.Btn10;
        this.buttons[1][1] = this.Btn11;
        this.buttons[1][2] = this.Btn12;
        this.buttons[2][0] = this.Btn20;
        this.buttons[2][1] = this.Btn21;
        this.buttons[2][2] = this.Btn22;
    }

    /**
     * Sample Skeleton for 'secondary.fxml' Controller Class
     */

    @FXML
    private void choose(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String buttonID = btn.getId();

        int row = Character.getNumericValue(buttonID.charAt(3));
        int col = Character.getNumericValue(buttonID.charAt(4));

        try{
            SimpleClient.getClient().sendToServer("choose " + row + " " + col);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void newGame(ActionEvent event){
        try{
            SimpleClient.getClient().sendToServer("reset game");

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    // handle all the messages from the server
    @Subscribe
    public void handleMessage(Object msg) {
        Platform.runLater(() -> {
            if (msg instanceof String) {
                String messageText = (String) msg;

                if (messageText.startsWith("score:")) {
                    String scoreText = messageText.substring(6);
                    LabelScore.setText("Score: " + scoreText);  // assuming you have a scoreLabel
                    return;
                }


                if (messageText.equals("RESET")) {
                    clearGameBoard(); // 🧼 This resets the board visually
                    LabelTurn.setText("New Game Started");
                    return;
                }

                handleStatusMessage(messageText);
            }


            if (msg instanceof Object[]) {
                Object[] update = (Object[]) msg;
                if (update.length >= 4) {
                    handleMoveUpdate(update);
                }
            }
        });
    }


    // clear the board and prepare for a new game
    private void clearGameBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = null;
                Button button = buttons[row][col];
                if (button != null) {
                    button.setText("");
                    button.setDisable(false);
                }
            }
        }
    }



    private void handleStatusMessage(String message) {
        LabelTurn.setText(message);
    }

    private void handleMoveUpdate(Object[] update) {
        try {
            int row = (Integer) update[0];
            int col = (Integer) update[1];
            String moveSymbol = (String) update[2];
            String status = update[3].toString();

            if (status.contains("OVER")) {
                LabelTurn.setText(status);
            } else {
                LabelTurn.setText(status + "'s Turn");
            }

            updateBoard(row, col, moveSymbol);
        } catch (ClassCastException | IndexOutOfBoundsException e) {
            System.err.println("Malformed move update received: " + e.getMessage());
        }
    }

    private void updateBoard(int row, int col, String moveSymbol) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            System.err.println("Invalid board coordinates: " + row + ", " + col);
            return;
        }

        Button button = buttons[row][col];
        if (button != null && button.getText().isEmpty()) {
            button.setText(moveSymbol);
            button.setDisable(true);  // prevent further clicks
        }
    }





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

    @FXML // fx:id="LabelScore"
    private Label LabelScore; // Value injected by FXMLLoader


}