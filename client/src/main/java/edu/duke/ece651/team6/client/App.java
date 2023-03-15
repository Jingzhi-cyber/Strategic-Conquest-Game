/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.duke.ece651.team6.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.MyName;

public class App {
  Client client;

  TextPlayer player;

  /**
   * Construct an app with 2 params
   * 
   * @param client for handling interactions (e.g. connection and communication)
   *               with server
   * @param player for handling interaction (e.g. user input for commands,
   *               initializations, play turns) with players
   */
  public App(Client client, TextPlayer player) {
    this.client = client;
    this.player = player;
  }
  // Takes an Inputstream and transfer its bytes into a string, then return the
  // string
  // private String getStringFromIS(InputStream is) throws IOException {
  // byte[] bytes = new byte[0];
  // bytes = new byte[is.available()];
  // is.read(bytes);
  // return new String(bytes);
  // }

  public String getMessage() {
    return "Hello from the client for " + MyName.getName();
  }

  public static void main(String[] args) throws IOException, UnknownHostException, ClassNotFoundException {
    /* 1. Build connection */
    Client client = null;
    GameBasicSetting setting = null;
    BufferedReader input = null;
    try {
      // create a client obejct and connect it to the server
      client = new Client("127.0.0.1", 12345);
      setting = client.recvGameBasicSetting("game setting");
      input = new BufferedReader(new InputStreamReader(System.in));
    } catch (UnknownHostException e) {
      System.out.println("UnknownHostException " + e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

    TextPlayer player = new TextPlayer(client, input, System.out, setting);
    player.displayGameSetting("game setting"); // TODO: custom message?

    // App app = new App(client, player);
    /* (Only for testing.) */
    // TestMap recvMap = (TestMap) client.recvObject(); // receive an object from
    // server and cast it to a mao
    // System.out.println(recvMap.getName());
    // TestMap sendMap = new TestMap("From player: hello server, this is the updated
    // map");
    // client.sendObject(sendMap); // send a map object to server

    /* 2. Initiation Phase */
    player.placeUnit("place units");

    /* 3. Play Phase */
    while (true) {
      try {
        player.playOneTurn(); // TODO: check win (can use return value)
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
        continue;
      }
      break;
    }

    // TODO: if lose can choose to exit or keep the game display

    // TODO: if win, the master will notify every player and end the game. How to
    // receive the message?

    /* 4. Close Phase */
    try {
      input.close();
      client.closeSocket();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
