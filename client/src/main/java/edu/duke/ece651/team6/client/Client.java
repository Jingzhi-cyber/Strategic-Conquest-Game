package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

// a class handling threads for a user
public class Client {
  // private final Integer playerId;
  private final String username;
  private final String serverHostName;
  private final int serverPort;
  private int gameId = 0;

  public Client(String username) {
    // this.playerId = playerId;
    this.username = username;
    this.serverHostName = "localhost";
    this.serverPort = 8000;
  }

  public Client(String username, String serverHostName, int serverPort) {
    // this.playerId = playerId;
    this.username = username;
    this.serverHostName = serverHostName;
    this.serverPort = serverPort;
  }

  Map<Integer, UIGame> uiGames;

  // Map<Integer, SocketHandler> gameIdToSocketHandler; // to socket/thread
  void createNewGame(int numPlayer) throws IOException, ClassNotFoundException {
    // create game id and update game list

    // create socket for connection with the server and wait for server ACK
    SocketHandler newSocketHandler = new SocketHandler(serverHostName, serverPort);
    UIGame newGame = new UIGame(this.gameId, username, newSocketHandler);
    uiGames.put(this.gameId, newGame);
    newSocketHandler.sendNumPlayer(numPlayer); // TODO send username + numPlayer or only numPlayer

    // create a thread to handle the game logic
    Thread gameThread = new Thread(new GameHandler(newGame, true));
    this.gameId++;
    gameThread.start();
  }

  void joinStartedGame(Integer gameId) {
    if (!uiGames.containsKey(gameId)) {
      throw new IllegalArgumentException("Game id not found");
    }
    Thread gameThread = new Thread(new GameHandler(uiGames.get(gameId), false));
    gameThread.start();
  }

  private static class GameHandler implements Runnable {
    // int gameId;
    // SocketHandler socketHandler;
    UIGame game;

    boolean isNewGame;

    public GameHandler(UIGame game, boolean isNewGame) {
      // this.gameId = gameId;
      this.game = game;
      this.isNewGame = isNewGame;
    }

    @Override
    public void run() {
      // TODO Auto-generated method stub
      try {
        if (isNewGame) {
          game.placeUnit();
        }
        game.playGame();
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }
}
