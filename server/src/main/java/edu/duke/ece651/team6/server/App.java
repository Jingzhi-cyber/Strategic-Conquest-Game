/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.duke.ece651.team6.server;
import edu.duke.ece651.team6.shared.MyName;
import java.io.IOException;
import edu.duke.ece651.team6.shared.SampleMap;
import edu.duke.ece651.team6.shared.SimpleMap;
import edu.duke.ece651.team6.shared.GameMap;

public class App {
  public String getMessage() {
    return "Hello from the server for "+ MyName.getName();
  }
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Invalid Argument! Usage: ./server/build/install/server/bin/server <server_port> <player_num>");
    }
    /**
     * Generate a random GameMap according to playerNum
     */
    // MapGenerator mapGenerator = new MapGenerator(24);
    // GameMap gameMap = new GameMap(mapGenerator.getTheMap());

    // Testing: Use SampleMap
    SampleMap sampleMap = new SampleMap();
    GameMap gameMap = new GameMap(sampleMap.getAdjList());

    // Testing: Use SimpleMap
    // SimpleMap simpleMap = new SimpleMap();
    // GameMap gameMap = new GameMap(simpleMap.getAdjList());

    RiscMaster riscMaster = new RiscMaster(Integer.parseInt(args[0]), Integer.parseInt(args[1]), gameMap);
    riscMaster.init();
    riscMaster.setUpGameBasicSettings();
    while (true) {
      if (riscMaster.playOneTurn()) {
        break;
      }
    }
    riscMaster.finish();
  }
}
