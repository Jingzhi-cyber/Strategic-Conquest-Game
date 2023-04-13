/**
The Controller class represents a controller for the client side of the application.
It handles the switching of scenes in the application, shows success and error messages
to the user, and provides functionality for logging out of the application.
*/
package edu.duke.ece651.team6.client.controller;

import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

import edu.duke.ece651.team6.client.Client;
import edu.duke.ece651.team6.client.UIGame;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Controller {
  /**
   * 
   * The client object for this controller.
   */
  protected Client client;

  /**
   * 
   * Constructor for the Controller class.
   * 
   * @param client The client object for this controller.
   */
  public Controller(Client client) {
    this.client = client;
  }

  /**
   * 
   * Returns the client object for this controller.
   * 
   * @return The client object for this controller.
   */
  public Client getClient() {
    return this.client;
  }

  /**
   * 
   * Returns a new scene based on the specified FXML file, CSS file, controllers,
   * and title.
   * 
   * @param url         The URL of the FXML file for the new scene.
   * @param cssUrl      The URL of the CSS file for the new scene.
   * @param controllers A HashMap containing the controllers for the new scene.
   * @param title       The title of the new scene.
   * @return A new scene based on the specified FXML file, CSS file, controllers,
   *         and title.
   * @throws Exception if an error occurs while creating the new scene.
   */
  public Scene getNewScene(String url, String cssUrl, HashMap<Class<?>, Object> controllers, String title)
      throws Exception {
    URL xmlResource = getClass().getResource(url); // ("/ui/risc-game-main-page.xml");
    FXMLLoader loader = new FXMLLoader(xmlResource);

    // HashMap<Class<?>, Object> controllers = new HashMap<>();
    // controllers.put(RiscController.class, new RiscController(client));

    // Set the controllers that has been passed to the FXMLLoader
    loader.setControllerFactory(param -> {
      Object controller = controllers.get(param);
      if (controller != null) {
        return controller;
      } else {
        try {
          return param.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });

    // TabPane tp = loader.load();

    Parent root = loader.load(); // Load the FXML using the FXMLLoader
    Scene scene = new Scene(root, 1600, 1000);

    // Load the CSS stylesheet
    // URL cssResource = getClass().getResource(cssUrl);
    // scene.getStylesheets().add(cssResource.toString());
    scene.getStylesheets().add(getClass().getResource(cssUrl).toExternalForm());

    // for (Object obj : controllers.values()) {
    // //@SuppressWarnings("unchecked")
    // Controller c = (Controller) obj;
    // c.setScene(scene);
    // }
    return scene;
  }

  /**
   * 
   * Updates the specified stage with the specified title and scene.
   * 
   * @param stage The stage to update.
   * @param title The title for the updated stage.
   * @param scene The scene for the updated stage.
   */
  private void updateScene(Stage stage, String title, Scene scene) {
    stage.setTitle(title);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * 
   * Switches to a new page based on the specified FXML file, CSS file,
   * controllers, and title.
   * 
   * @param url         The URL of the FXML file for the new page.
   * @param cssUrl      The URL of the CSS file for the new page.
   * @param controllers A HashMap containing the controllers for the new page.
   * @param title       The title of the new page.
   * @param cont        The control to use for the new page.
   * @throws Exception if an error occurs while switching to the new page.
   */
  public void switchToPage(String url, String cssUrl, HashMap<Class<?>, Object> controllers, String title, Control cont)
      throws Exception {
    Scene scene = getNewScene(url, cssUrl, controllers, title);

    // Get the stage
    Stage stage = (Stage) cont.getScene().getWindow();
    // Set the new scene on the stage
    updateScene(stage, title, scene);
  }

  /**
   * 
   * Switches to a new page based on the specified FXML file, CSS file,
   * controllers, and title.
   * 
   * @param url         The URL of the FXML file for the new page.
   * @param cssUrl      The URL of the CSS file for the new page.
   * @param controllers A HashMap containing the controllers for the new page.
   * @param title       The title of the new page.
   * @param stage       The stage to use for the new page.
   * @throws Exception if an error occurs while switching to the new page.
   */
  public void switchToPage(String url, String cssUrl, HashMap<Class<?>, Object> controllers, String title, Stage stage)
      throws Exception {

    Scene scene = getNewScene(url, cssUrl, controllers, title);

    // Set the new scene on the stage
    updateScene(stage, title, scene);
  }

  /**
   * 
   * Switches to a new page based on the specified FXML file, CSS file,
   * controllers, and title.
   * 
   * @param url         The URL of the FXML file for the new page.
   * @param cssUrl      The URL of the CSS file for the new page.
   * @param controllers A HashMap containing the controllers for the new page.
   * @param title       The title of the new page.
   * @param pane        The pane to use for the new page.
   * @throws Exception if an error occurs while switching to the new page.
   */
  public void switchToPage(String url, String cssUrl, HashMap<Class<?>, Object> controllers, String title, Pane pane)
      throws Exception {
    Scene scene = getNewScene(url, cssUrl, controllers, title);
    // Get the stage
    Stage stage = (Stage) pane.getScene().getWindow();

    // Set the new scene on the stage
    updateScene(stage, title, scene);
  }

  /**
   * 
   * Displays a success alert to the user with the given message.
   * 
   * @param message The message to display in the alert
   */
  public void showSuccess(String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }

  /**
   * 
   * Displays an error alert to the user with the given message.
   * 
   * @param message The message to display in the alert
   */
  public void showError(String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }

  /**
   * 
   * Logs out of the application and switches to the login/register page.
   * 
   * @throws Exception If an error occurs while switching pages.
   */
  protected void logOut() throws Exception {
    for (Integer i : client.uiGames.keySet()) {
      UIGame game = client.uiGames.get(i);
      game.exit();
      int gameId = game.getGameId();
      client.getGameLoungeController().gameLounge.removeGame(String.valueOf(gameId));
    }
    HashMap<Class<?>, Object> controllers = new HashMap<Class<?>, Object>();
    // System.out.print(this.usernameField.getText());
    controllers.put(LoginRegisterController.class, new LoginRegisterController(client));

    switchToPage("/ui/login-register-page.xml", "/ui/buttonstyle.css", controllers, "Game Lounge", client.getStage());
  }
}
