package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import edu.duke.ece651.team6.client.controller.LoginRegisterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class EntryPoint extends Application {

  private Client client = null;

  public EntryPoint(Client client) {
    this.client = client;
  }

  public EntryPoint() {
  }

  @Override
  public void start(Stage stage) throws Exception {

    Client client = new Client("vcm-30760.vm.duke.edu", 12345, stage);
    // Client client = new Client("localhost", 12345, stage);

    URL xmlResource = getClass().getResource("/ui/login-register-page.xml");
    FXMLLoader loader = new FXMLLoader(xmlResource);

    HashMap<Class<?>, Object> controllers = new HashMap<>();
    // Client client = new Client("localhost", 12345, stage);
    System.out.println("New client");
    controllers.put(LoginRegisterController.class, new LoginRegisterController(client));
    // controllers.put(MainPageController.class, new MainPageController(client));
    // TODO controllers.put(RiscController.class, new RiscController());
    loader.setControllerFactory((c) -> { // on set the call back func here. the real call happens when loader calls its
                                         // load() method
      return controllers.get(c);
    });

    // GridPane gp = FXMLLoader.load(xmlResource);
    // TODO GridPane gp = loader.load();
    TabPane tp = loader.load();

    // GridPane gp = loader.load();

    // Create a scene
    // TODO Scene scene = new Scene(gp, 640, 480);
    Scene scene = new Scene(tp, 1600, 1000);

    // Load the CSS stylesheet
    URL cssResource = getClass().getResource("/ui/buttonstyle.css");
    scene.getStylesheets().add(cssResource.toString());
    // Load the CSS stylesheet
    // URL cssResource = getClass().getResource("/ui/someStyle.css");
    // scene.getStylesheets().add(cssResource.toString());

    // Scene scene = new Scene(new StackPane(l, circ), 640, 480);
    stage.setTitle("RISC");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) throws IOException, UnknownHostException, ClassNotFoundException {
    Application.launch(EntryPoint.class, args);
  }
}
