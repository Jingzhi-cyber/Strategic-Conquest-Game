package edu.duke.ece651.team6.client.controller;

import java.io.IOException;
import java.util.HashMap;

import edu.duke.ece651.team6.client.Client;
import edu.duke.ece651.team6.client.model.GameLounge;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class LoginRegisterController extends Controller {

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

  public LoginRegisterController(Client client) {
    super(client);
  }

  public String getUserName() {
    return this.usernameField.getText();
  }

  private void loginOrRegister(boolean result) throws IOException, ClassNotFoundException {
    if (result) {
      try {
        // switchToGameMainPage();
        switchToGameLoungePage();
      } catch (Exception e) {
        // Show an error message if the window fails to load
        e.printStackTrace();
        showError("Failed to load next window.");
      }
    } else {
      // Show an error message
      showError("Invalid username or password.");
    }
  }

  public void initialize() {
    // Set up the login button event handler
    loginButton.setOnAction(event -> {
      String username = usernameField.getText();
      String password = passwordField.getText();

      try {
        loginOrRegister(loginUser(username, password));
      } catch (ClassNotFoundException | IOException e) {
        e.printStackTrace();
        showError(e.getMessage());
      }
    });

    // Set up the register button event handler
    registerButton.setOnAction(event -> {
      String username = registerUsernameField.getText();
      String password = registerPasswordField.getText();

      try {
        loginOrRegister(registerUser(username, password));
      } catch (ClassNotFoundException | IOException e) {
        e.printStackTrace();
        showError(e.getMessage());
      }
    });
  }

  private boolean loginUser(String username, String password) throws IOException, ClassNotFoundException {
    // Check if the username and password match an existing user in the database
    // Return true if the user is successfully authenticated, false otherwise
    // TODO
    // username is empty / null
    // TODO
    // client.sendLoginInfo(username);
    return true;
  }

  private boolean registerUser(String username, String password) throws IOException, ClassNotFoundException {
    // Add the new user to the database
    // Return true if the user was successfully registered, false otherwise
    // TODO
    // client.sendLoginInfo(username);
    return true;
  }

  private void switchToGameMainPage() throws Exception {
    // Load the main game window
    // ...
    HashMap<Class<?>, Object> controllers = new HashMap<Class<?>, Object>();
    // System.out.print(this.usernameField.getText());
    controllers.put(MainPageController.class, new MainPageController(client));
    switchToPage("/ui/mainPage.xml", "/ui/buttonstyle.css", controllers, "Main Page", tabPane);
  }

  private void switchToGameLoungePage() throws Exception {
    // Load the main game window
    // ...
    HashMap<Class<?>, Object> controllers = new HashMap<Class<?>, Object>();
    // System.out.print(this.usernameField.getText());
    controllers.put(GameLoungeController.class, new GameLoungeController(client, new GameLounge()));
    switchToPage("/ui/game-lounge-page.xml", "/ui/buttonstyle.css", controllers, "Game Lounge", tabPane);
  }
}
