package edu.duke.ece651.team6.client;

import java.net.URL;
import java.util.HashMap;

import edu.duke.ece651.team6.client.controller.LoginRegisterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class EntryPoint extends Application {

  @Override
  public void start(Stage stage) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());

    // XML
    // TODO URL xmlResource = getClass().getResource("/ui/risc-game-main-page.xml");
    URL xmlResource = getClass().getResource("/ui/login-register-page.xml");
    FXMLLoader loader = new FXMLLoader(xmlResource);

    HashMap<Class<?>, Object> controllers = new HashMap<>();
    controllers.put(LoginRegisterController.class, new LoginRegisterController());
    // TODO controllers.put(RiscController.class, new RiscController());
    loader.setControllerFactory((c) -> { // on set the call back func here. the real call happens when loader calls its
                                         // load() method
      return controllers.get(c);
    });

    // GridPane gp = FXMLLoader.load(xmlResource);
    // TODO GridPane gp = loader.load();
    TabPane tp = loader.load();

    // Create a scene
    // TODO  Scene scene = new Scene(gp, 640, 480);
    Scene scene = new Scene(tp, 1000, 800);

    // Load the CSS stylesheet
    // URL cssResource = getClass().getResource("/ui/someStyle.css");
    // scene.getStylesheets().add(cssResource.toString());

    // Scene scene = new Scene(new StackPane(l, circ), 640, 480);
    stage.setTitle("RISC");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    Application.launch();
  }

}
