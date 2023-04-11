package edu.duke.ece651.team6.client.controller;

import java.net.URL;
import java.util.HashMap;

import edu.duke.ece651.team6.client.Client;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Controller {
  protected Client client;

  public Controller(Client client) {
    this.client = client;
  }

  public Client getClient() {
    return this.client;
  }

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

  private void updateScene(Stage stage, String title, Scene scene) {
    stage.setTitle(title);
    stage.setScene(scene);
    stage.show();
  }

  public void switchToPage(String url, String cssUrl, HashMap<Class<?>, Object> controllers, String title, Control cont)
      throws Exception {
    Scene scene = getNewScene(url, cssUrl, controllers, title);

    // Get the stage
    Stage stage = (Stage) cont.getScene().getWindow();
    // Set the new scene on the stage
    updateScene(stage, title, scene);
  }

  public void switchToPage(String url, String cssUrl, HashMap<Class<?>, Object> controllers, String title, Stage stage)
      throws Exception {

    Scene scene = getNewScene(url, cssUrl, controllers, title);

    // Set the new scene on the stage
    updateScene(stage, title, scene);
  }

  public void switchToPage(String url, String cssUrl, HashMap<Class<?>, Object> controllers, String title, Pane pane)
      throws Exception {
    Scene scene = getNewScene(url, cssUrl, controllers, title);
    // Get the stage
    Stage stage = (Stage) pane.getScene().getWindow();

    // Set the new scene on the stage
    updateScene(stage, title, scene);
  }

  public void showSuccess(String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }

  public void showError(String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }

  protected void logOut() throws Exception {

    HashMap<Class<?>, Object> controllers = new HashMap<Class<?>, Object>();
    // System.out.print(this.usernameField.getText());
    controllers.put(LoginRegisterController.class, new LoginRegisterController(client));

    switchToPage("/ui/login-register-page.xml", "/ui/buttonstyle.css", controllers, "Game Lounge", client.getStage());
  }
}
