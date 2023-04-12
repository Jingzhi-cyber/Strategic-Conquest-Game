package edu.duke.ece651.team6.client.controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import edu.duke.ece651.team6.client.Client;
import edu.duke.ece651.team6.client.model.GameLounge;
import edu.duke.ece651.team6.client.SocketHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

  // private String username;

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

  private GameLounge gameLounge;

  public LoginRegisterController(Client client) {
    super(client);
  }

  public String getUserName() {
    // TODO
    return this.usernameField.getText();
  }

  public void initialize() {
    // Set up the login button event handler
    loginButton.setOnAction(event -> {
      String username = usernameField.getText();
      String password = passwordField.getText();
      try {
        loginUser(username, password);
        client.setUserName(username);
        client.setPassword(password);
        switchToGameLoungePage();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        showError(e.getMessage());
      } catch (Exception e) {
        e.printStackTrace();
        showError("Failed to load next window.");
      }
    });

    // Set up the register button event handler
    registerButton.setOnAction(event -> {
      String username = registerUsernameField.getText().trim();
      String password = registerPasswordField.getText().trim();
      try {
        registerUser(username, password);
        showSuccess("User has been registered successfully! Now you can login.");
      } catch (IllegalArgumentException e) {
        showError(e.getMessage());
      }
    });
  }

  private void loginUser(String username, String password) {
    if (username.isEmpty() || password.isEmpty()) {
      throw new IllegalArgumentException("username and password cannot be empty!");
    }
    try (Socket socket = new Socket(client.serverHostName, client.serverPort)) {
      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      oos.writeObject(username + " " + password + " -1");
      if (socket.getInputStream().read() == -1) {
        throw new IOException();
      }
      oos.close();
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid username and password!");
    }
  }

  private void registerUser(String username, String password) {
    if (username.isEmpty() || password.isEmpty()) {
      throw new IllegalArgumentException("username and password can't be empty!");
    }
    try (Socket socket = new Socket(client.serverHostName, client.serverPort)) {
      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      oos.writeObject(username + " " + password + " -2");
      if (socket.getInputStream().read() == -1) {
        throw new IOException();
      }
      oos.close();
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid username and password!");
    }
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
    HashMap<Class<?>, Object> controllers = new HashMap<>();
    // System.out.print(this.usernameField.getText());
    controllers.put(GameLoungeController.class, new GameLoungeController(client, gameLounge));
    System.out.println("username ready -> switching game lounge");
    switchToPage("/ui/game-lounge-page.xml", "/ui/buttonstyle.css", controllers, "Game Lounge", tabPane);
  }
}
