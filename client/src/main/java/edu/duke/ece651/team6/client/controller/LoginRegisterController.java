package edu.duke.ece651.team6.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginRegisterController {

  @FXML
  private TabPane tabPane;

  @FXML
  private Tab loginTab;

  @FXML
  private Tab registerTab;

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button loginButton;

  @FXML
  private TextField registerUsernameField;

  @FXML
  private PasswordField registerPasswordField;

  @FXML
  private Button registerButton;

  public void initialize() {
    // Set up the login button event handler
    loginButton.setOnAction(event -> {
      String username = usernameField.getText();
      String password = passwordField.getText();

      if (loginUser(username, password)) {
        // Show the main game window
        showMainWindow();
      } else {
        // Show an error message
        showError("Invalid username or password.");
      }
    });

    // Set up the register button event handler
    registerButton.setOnAction(event -> {
      String username = registerUsernameField.getText();
      String password = registerPasswordField.getText();

      if (registerUser(username, password)) {
        // Show a success message
        showSuccess("Registration successful. Please log in.");
      } else {
        // Show an error message
        showError("Failed to register user.");
      }
    });
  }

  private boolean loginUser(String username, String password) {
    // Check if the username and password match an existing user in the database
    // Return true if the user is successfully authenticated, false otherwise
    return true;
  }

  private boolean registerUser(String username, String password) {
    // Add the new user to the database
    // Return true if the user was successfully registered, false otherwise
    return true;
  }

  private void showMainWindow() {
    // Load the main game window
    // ...
  }

  private void showSuccess(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
