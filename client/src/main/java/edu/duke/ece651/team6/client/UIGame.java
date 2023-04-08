package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.net.UnknownHostException;

import edu.duke.ece651.team6.shared.GlobalMapInfo;

public class UIGame extends Game {

  int gameId; // unique for games that are of each player
  // Integer playerId;
  String username;

  public UIGame(int gameId, String username, SocketHandler socketHandler) {
    super(socketHandler);
    this.gameId = gameId;
    this.username = username;
  }

  @Override
  public void placeUnit() throws IOException, ClassNotFoundException {
    // TODO Auto-generated method stub

  }

  @Override
  public String playOneTurn() throws IOException, ClassNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void playGame() throws IOException, UnknownHostException, ClassNotFoundException {
    // TODO Auto-generated method stub

  }

  @Override
  public GlobalMapInfo updateAndDisplayMapInfo() throws IOException, ClassNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

}
