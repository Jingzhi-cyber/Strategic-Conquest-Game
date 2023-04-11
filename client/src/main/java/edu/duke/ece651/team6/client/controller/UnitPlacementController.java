package edu.duke.ece651.team6.client.controller;

import edu.duke.ece651.team6.client.Client;
import edu.duke.ece651.team6.client.UIGame;
import edu.duke.ece651.team6.shared.Constants.GAME_STATUS;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class UnitPlacementController extends Controller {

  public UnitPlacementController(Client client) {
    super(client);
  }

  // @FXML
  // private TextField unitsTextField;

  // private UIGame uiGame;

  // @FXML
  // private Button ackButton;

  // Added the StringProperty for the prompt text
  //  private StringProperty promptTextProperty;

  // public void setPLaceUnitsInvisibility() {
  // unitsTextField.setVisible(false);
  // ackButton.setVisible(false);
  // }

  // Updated the initialize() method to bind the promptTextProperty to the
  // TextField
  // public void initialize() {
  //   System.out.println("Initializing UnitPlacementController...");

  //   // promptTextProperty = new SimpleStringProperty("Enter your input here");
  //   // unitsTextField.promptTextProperty().bind(promptTextProperty);

  //   // ackButton.setOnAction(e -> handleAckButtonClick()); // Add an event handler
  //   // for the button click
  // }

  // Added the changePromptText() method to update the prompt text
  // public void changePromptText(String newPrompt) {
  //   promptTextProperty.set(newPrompt);
  // }

  // public void changePromptText(String newPrompt) {
  // inputTextField.setPromptText(newPrompt);
  // }

  // public void setUIGame(UIGame uiGame) {
  //   this.uiGame = uiGame;
  // }

  // public Integer readNumUnitFromUI() {
  //   Integer result = null;
  //   // String numUnitStr = unitsTextField.getText().trim();
  //   try {
  //     result = Integer.valueOf(unitsTextField.getText());
  //   } catch (NumberFormatException e) {
  //     // Display an error message to the user
  //     showError("Please enter a valid unit number");
  //     e.printStackTrace();
  //   }
  //   return result;
  // }

  // private void handleAckButtonClick() {
  // // Call this method when the user clicks the button
  // Integer numUnits = readNumUnitFromUI();

  // String result = null;
  // try {
  // result = uiGame.receiveUnitsFromUI(numUnits);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }

  // if (result != null) {
  // showError(result);
  // } else {
  // showSuccess("Successfully placed " + numUnits + " units");
  // }
  // }
}
