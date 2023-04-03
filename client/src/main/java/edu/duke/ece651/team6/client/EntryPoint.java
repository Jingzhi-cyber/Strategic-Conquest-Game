package edu.duke.ece651.team6.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import edu.duke.ece651.team6.client.controller.LoginRegisterController;
import edu.duke.ece651.team6.client.controller.RiscController;
import edu.duke.ece651.team6.shared.GameBasicSetting;
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
    Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());

    // XML
    // TODO URL xmlResource = getClass().getResource("/ui/risc-game-main-page.xml");
    URL xmlResource = getClass().getResource("/ui/login-register-page.xml");
    FXMLLoader loader = new FXMLLoader(xmlResource);

    HashMap<Class<?>, Object> controllers = new HashMap<>();
    controllers.put(LoginRegisterController.class, new LoginRegisterController(this.client));
    controllers.put(RiscController.class, new RiscController(client));
    // TODO controllers.put(RiscController.class, new RiscController());
    loader.setControllerFactory((c) -> { // on set the call back func here. the real call happens when loader calls its
                                         // load() method
      return controllers.get(c);
    });

    // GridPane gp = FXMLLoader.load(xmlResource);
    // TODO GridPane gp = loader.load();
    TabPane tp = loader.load();

    // Create a scene
    // TODO Scene scene = new Scene(gp, 640, 480);
    Scene scene = new Scene(tp, 1600, 1000);

    // Load the CSS stylesheet
    // URL cssResource = getClass().getResource("/ui/someStyle.css");
    // scene.getStylesheets().add(cssResource.toString());

    // Scene scene = new Scene(new StackPane(l, circ), 640, 480);
    stage.setTitle("RISC");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) throws IOException, UnknownHostException, ClassNotFoundException {
    Application.launch();
  }

}
