package edu.duke.ece651.team6.client.controller;

import java.net.URL;
import java.util.HashMap;

import edu.duke.ece651.team6.client.SocketHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Controller {

  protected SocketHandler client;

  public Controller(SocketHandler client) {
    this.client = client;
  }

  public SocketHandler getClient() {
    return this.client;
  }

  public Scene getNewScene(String url, String cssUrl, HashMap<Class<?>, Object> controllers, String title)
      throws Exception {
    URL xmlResource = getClass().getResource(url); // ("/ui/risc-game-main-page.xml");
    FXMLLoader loader = new FXMLLoader(xmlResource);

    // HashMap<Class<?>, Object> controllers = new HashMap<>();
    // controllers.put(RiscController.class, new RiscController(client));

    loader.setControllerFactory((c) -> {
      return controllers.get(c);
    });

    // TabPane tp = loader.load();

    Parent p = loader.load();
    Scene scene = new Scene(p, 1600, 1000);

    // Load the CSS stylesheet
    URL cssResource = getClass().getResource(cssUrl);
    scene.getStylesheets().add(cssResource.toString());

    // for (Object obj : controllers.values()) {
    //   //@SuppressWarnings("unchecked")
    //   Controller c = (Controller) obj;
    //   c.setScene(scene);
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

  public void switchToPage(String url, String cssUrl, HashMap<Class<?>, Object> controllers, String title, Pane pane)
      throws Exception {
    Scene scene = getNewScene(url, cssUrl, controllers, title);
    // Get the stage
    Stage stage = (Stage) pane.getScene().getWindow();

    // Set the new scene on the stage
    updateScene(stage, title, scene);
  }

  protected void showSuccess(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setContentText(message);
    alert.showAndWait();
  }

  protected void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
