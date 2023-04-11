package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import edu.duke.ece651.team6.client.controller.GameLoungeController;
import edu.duke.ece651.team6.client.controller.LoginRegisterController;
import edu.duke.ece651.team6.client.controller.MainPageController;
import edu.duke.ece651.team6.client.controller.UnitPlacementController;
import edu.duke.ece651.team6.client.model.GameLounge;
import edu.duke.ece651.team6.shared.Constants.GAME_STATUS;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

// a class handling threads for a user
public class Client {
  // private final Integer playerId;
  private String username;
  private final String serverHostName;
  private final int serverPort;
  private Integer gameId;
  private Scene scene = null;

  LoginRegisterController loginRegisterController;
  GameLoungeController gameLoungeController;
  MainPageController mainPageController;
  // UnitPlacementController unitPlacementController;

  // Integer currentGameId = null;

  GameLounge gameLounge;
  Stage stage;

  UIGame currentGame;
  Map<Integer, UIGame> uiGames;

  public void setScene(Scene scene) {
    this.scene = scene;
  }

  public Scene getScene() {
    return this.scene;
  }

  public Stage getStage() {
    return stage;
  }

  public Client(String username, Stage stage) {
    // this.playerId = playerId;
    this.username = username;
    this.serverHostName = "localhost";
    this.serverPort = 8000;
    this.gameLounge = new GameLounge();
    this.stage = stage;
    this.uiGames = new ConcurrentHashMap<>();
    this.gameId = 0;
    // this.loginRegisterController = new LoginRegisterController(this);
    this.gameLoungeController = new GameLoungeController(this, gameLounge);
  }

  public Client(String serverHostName, int serverPort, Stage stage) {
    // this.playerId = playerId;
    this.serverHostName = serverHostName;
    this.serverPort = serverPort;
    this.gameLounge = new GameLounge();
    this.stage = stage;
    this.uiGames = new ConcurrentHashMap<>();
    this.gameId = 0;
    // this.loginRegisterController = new LoginRegisterController(this);
    this.gameLoungeController = new GameLoungeController(this, gameLounge);
  }

  // TODO to remove
  public Client(Stage stage) {
    this.username = "test player";
    this.serverHostName = "localhost";
    this.serverPort = 12345;
    this.gameLounge = new GameLounge();
    this.stage = stage;
    this.uiGames = new ConcurrentHashMap<>();
    this.gameId = 0;
    // this.loginRegisterController = new LoginRegisterController(this);
    this.gameLoungeController = new GameLoungeController(this, gameLounge);
  }

  public GameLounge getGameLounge() {
    return this.gameLounge;
  }

  public LoginRegisterController getLoginRegisterController() {
    return this.loginRegisterController;
  }

  public GameLoungeController getGameLoungeController() {
    return this.gameLoungeController;
  }

  public void setUserName(String userName) {
    this.username = userName;
  }

  public UIGame getUIGameById(Integer id) {
    if (!uiGames.containsKey(id)) {
      throw new IllegalArgumentException("invalid game id");
    }
    return uiGames.get(id);
  }

  public void setCurrentGame(Integer gameId) {
    if (!uiGames.containsKey(gameId)) {
      throw new IllegalArgumentException("invalid game id");
    }
    this.currentGame = uiGames.get(gameId);
  }

  // public void playCurrentGame() throws ClassNotFoundException, IOException {
  // if (currentGame == null) {
  // return;
  // }
  // if (currentGame.gameStatus == GAME_STATUS.PLACE_UNITS) {
  // currentGame.placeUnit();
  // } else {
  // currentGame.playGame();
  // }
  // }

  // Map<Integer, SocketHandler> gameIdToSocketHandler; // to socket/thread
  public Integer createNewGame(int numPlayer) throws IOException, ClassNotFoundException {
    // create game id and update game list
    int newGameId = this.gameId++;
    // create socket for connection with the server and wait for server ACK
    SocketHandler newSocketHandler = new SocketHandler(serverHostName, serverPort);
    UIGame newGame = new UIGame(newGameId, username, newSocketHandler, new MainPageController(this));
    System.out.println("New game has been created");
    uiGames.put(newGameId, newGame);
    newSocketHandler.sendUserNameAndNumPlayer(username + " " + numPlayer); // TODO send username + numPlayer or only
                                                                           // numPlayer
    // create a thread to handle the game logic

    Thread gameThread = new Thread(new GameHandler(newGame));
    gameThread.setDaemon(true); // TODO
    gameThread.start();

    return newGameId;
  }

  public void joinStartedGame(Integer gameId) {
    if (!uiGames.containsKey(gameId)) {
      throw new IllegalArgumentException("Game id not found");
    }
    Thread gameThread = new Thread(new GameHandler(uiGames.get(gameId)));
    gameThread.start();
  }

  private static class GameHandler implements Runnable {
    // int gameId;
    // SocketHandler socketHandler;
    UIGame game;

    public GameHandler(UIGame game) {
      // this.gameId = gameId;
      this.game = game;
    }

    @Override
    public void run() {
      System.out.println("Enter the game - Client.java");
      Platform.runLater(() -> {
        try {
          game.entryPoint();
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
  }
}
