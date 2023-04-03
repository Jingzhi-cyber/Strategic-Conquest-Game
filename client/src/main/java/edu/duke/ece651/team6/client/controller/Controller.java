package edu.duke.ece651.team6.client.controller;

import java.net.URL;
import java.util.HashMap;

import edu.duke.ece651.team6.client.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

public class Controller {

  private Client client;

  public Controller(Client client) {
    this.client = client;
  }

  public Client getClient() {
    return this.client;
  }

  public void switchToPage(String url, HashMap<Class<?>, Object> controllers, String title, Control cont)
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

    // Set the new controller class for the main game window
    // Set the new scene on the stage
    Stage stage = (Stage) cont.getScene().getWindow();
    stage.setTitle(title);
    stage.setScene(scene);
    stage.show();
  }

}
