package edu.duke.ece651.team6.client;

import java.net.URL;
import java.util.HashMap;

import edu.duke.ece651.team6.client.controller.RiscController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class EntryPoint extends Application {

  @Override
  public void start(Stage stage) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());

    // XML
    URL xmlResource = getClass().getResource("/ui/risc-game-main-page.xml");
    FXMLLoader loader = new FXMLLoader(xmlResource);

    HashMap<Class<?>, Object> controllers = new HashMap<>();
    controllers.put(RiscController.class, new RiscController());
    loader.setControllerFactory((c) -> { // on set the call back func here. the real call happens when loader calls its
                                         // load() method
      return controllers.get(c);
    });

    // GridPane gp = FXMLLoader.load(xmlResource);
    GridPane gp = loader.load();

    // Create a scene
    Scene scene = new Scene(gp, 640, 480);

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
