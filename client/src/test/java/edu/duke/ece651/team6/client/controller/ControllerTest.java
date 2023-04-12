package edu.duke.ece651.team6.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.testfx.framework.junit5.ApplicationExtension;

import edu.duke.ece651.team6.client.Client;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;

@ExtendWith(ApplicationExtension.class)
public class ControllerTest {

  private Controller controller;

  @Mock
  private Client client;

  @Mock
  private Control control;

  // @BeforeEach
  // public void setUp() {
  //   client = mock(Client.class);
  //   controller = new Controller(client);
  // }

  // @Test
  // public void getClientTest() {
  //   assertEquals(client, controller.getClient());
  // }

  // @Test
  // public void showSuccessTest() {
  //   String message = "Success!";
  //   controller.showSuccess(message);
  //   // assertEquals(message, Alert.AlertType.INFORMATION, "INFORMATION");
  // }

  // @Test
  // public void showErrorTest() {
  //   String message = "Error!";
  //   controller.showError(message);
  //   // assertEquals(message, Alert.AlertType.ERROR, "Error!");
  // }

}
