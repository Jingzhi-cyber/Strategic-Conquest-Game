package edu.duke.ece651.team6.client;

import edu.duke.ece651.team6.client.controller.MainPageController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import static edu.duke.ece651.team6.shared.Constants.GAME_STATUS.*;
import static org.junit.jupiter.api.Assertions.*;

class UIGameTest {
    @Disabled
    @Test
    void superCoolTest() throws IOException, ExecutionException, ClassNotFoundException, InterruptedException {
        new Thread(() -> {
            try {
                ServerSocket socket = new ServerSocket(6666);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        UIGame game = new UIGame(0, "hello", new SocketHandler(new Socket("localhost", 6666)), new MainPageController(null));
        game.setScene(null);
        game.getScene();
        game.getStatus();
        game.setGameLounge(null);
        game.getMainPageController();
        game.exit();
        game.setMainPageController(null);
        game.updateGameStatus(PLACE_UNITS);
        game.constructIntegersFrom0UpTo(2);
    }

}
