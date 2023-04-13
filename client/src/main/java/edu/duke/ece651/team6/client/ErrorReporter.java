package edu.duke.ece651.team6.client;

import javafx.scene.control.Alert;

public class ErrorReporter implements Thread.UncaughtExceptionHandler {

  @Override
  public void uncaughtException(Thread thread, Throwable error) {
    // // put this in for debugging: error.printStackTrace();
    // while (error.getCause() != null) {
    // error = error.getCause();
    // }
    // Alert dialog = new Alert(Alert.AlertType.ERROR);
    // dialog.setHeaderText(error.getClass().getName());
    // dialog.setContentText(error.getMessage());
    // dialog.showAndWait();
  }
}
