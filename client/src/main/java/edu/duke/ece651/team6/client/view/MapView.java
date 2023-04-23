package edu.duke.ece651.team6.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.duke.ece651.team6.client.controller.MainPageController;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.PolygonGetter;
import edu.duke.ece651.team6.shared.Territory;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MapView {
  MainPageController mainPageController;
  // Commit currentCommit;
  GameMap gameMap;
  int playerId;

  PolygonGetter polygonGetter;

  Map<Integer, Color> playerColor;

  ArrayList<Color> colors;

  Map<Territory, Map<String, String>> previouslySeenTerritories;

  public MapView(MainPageController mainPageController, GameMap gameMap, int playerId, PolygonGetter polygonGetter,
      Map<Integer, Color> playerColor, ArrayList<Color> colors,
      Map<Territory, Map<String, String>> previouslySeenTerritories) {
    this.mainPageController = mainPageController;
    this.gameMap = gameMap;
    this.playerId = playerId;

    this.polygonGetter = polygonGetter;
    this.playerColor = playerColor;
    this.colors = colors;

    this.previouslySeenTerritories = previouslySeenTerritories;
  }

  /**
   * Sets the fill color of a given polygon to the color associated with a given
   * owner ID.
   *
   * @param polygon The polygon whose fill color is to be set.
   * @param ownerID The ID of the owner associated with the color to be used for
   *                the polygon.
   */
  protected void setPolygonColor(Polygon polygon, int ownerID) {
    polygon.setFill(this.playerColor.get(ownerID));
  }

  protected void greyOutPolygon(Polygon polygon) {
    polygon.setFill(Color.LIGHTGREY);
  }

  /**
   * Performs a simple animation on a given polygon by briefly changing its fill
   * color to light gray and then changing it back to its original color.
   *
   * @param polygon The polygon to be animated.
   */
  protected void performPolygonAnimation(Polygon polygon) {
    Color color = (Color) polygon.getFill();
    polygon.setFill(Color.LIGHTGRAY);

    new Thread(() -> {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      polygon.setFill(color);
    }).start();
  }

  protected void displayTerritoryInfo(Text territoryInfoText1, Text territoryInfoText2, Text territoryInfoText3,
      Territory territory, boolean obsolete) {
    String info1 = null;
    String info2 = null;
    String info3 = null;
    if (obsolete) {
      info1 = previouslySeenTerritories.get(territory).get("info1");
      info2 = previouslySeenTerritories.get(territory).get("info2");
      info3 = previouslySeenTerritories.get(territory).get("info3");
    } else {
      int mySpyNum = territory.getSpyNumByPlayerId(playerId);
      info1 = " Territory: " + territory.getName() + "\n OwnerID: " + territory.getOwnerId() + " \n" + " Food prod: "
          + territory.getFood() + "\n Tech prod: " + territory.getTechnology() + "\n My Spies: " + mySpyNum;
      info2 = getNeighborDistance(territory);
      info3 = getUnitsNumberByLevel(territory);

      if (!previouslySeenTerritories.containsKey(territory)) {
        previouslySeenTerritories.put(territory, new HashMap<>());
      }

      previouslySeenTerritories.get(territory).put("info1", info1);
      previouslySeenTerritories.get(territory).put("info2", info2);
      previouslySeenTerritories.get(territory).put("info3", info3);
    }
    territoryInfoText1.setText(info1);
    territoryInfoText2.setText(info2);
    territoryInfoText3.setText(info3);
  }

  /**
   * Sets a mouse click event on a given polygon that performs a polygon animation
   * when the polygon is clicked.
   *
   * @param polygon The polygon to set the mouse click event on.
   */
  protected void setPolygonMouseClick(Polygon polygon, Territory territory, boolean obsolete) {
    polygon.setOnMouseClicked((MouseEvent click_event) -> {
      performPolygonAnimation(polygon);
      displayTerritoryInfo(this.mainPageController.getTerritoryInfoText1(),
          this.mainPageController.getTerritoryInfoText2(), this.mainPageController.getTerritoryInfoText3(), territory,
          obsolete);
    });
  }

  /**
   * Sets the text of a given polygon to a given string, or creates a new Text
   * object for the polygon if one does not exist.
   *
   * @param mapPane The map pane containing the polygon.
   * @param polygon The polygon to set the text on.
   * @param text    The text to set on the polygon.
   * @return The Text object that was created or modified.
   */
  protected Text setPolygonText(Pane mapPane, Polygon polygon, String text) {
    for (Node node : mapPane.getChildren()) {
      if (node.getId().equals(polygon.getId() + "Text")) {
        Text territoryInfo = (Text) node;
        territoryInfo.setText(text);
        return territoryInfo;
      }
    }
    Text territoryInfo = new Text(text);
    territoryInfo.setId(polygon.getId() + "Text");
    territoryInfo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
    territoryInfo.setFill(Color.BLACK);
    double centerX = polygon.getBoundsInLocal().getWidth() / 2;
    double centerY = polygon.getBoundsInLocal().getHeight() / 2;
    territoryInfo.setLayoutX(polygon.getLayoutX() + polygon.getBoundsInLocal().getMinX() + centerX
        - territoryInfo.getBoundsInLocal().getWidth() / 2);
    territoryInfo.setLayoutY(polygon.getLayoutY() + polygon.getBoundsInLocal().getMinY() + centerY
        + territoryInfo.getBoundsInLocal().getHeight() / 2);
    mapPane.getChildren().add(territoryInfo);
    return territoryInfo;
  }

  /**
   *
   * This method sets the tooltip of a given Polygon with the specified text.
   *
   * @param polygon The Polygon to set the tooltip for.
   * @param text    The text to set as the tooltip for the Polygon.
   */
  protected void setPolygonTooltip(Polygon polygon, String text) {
    Tooltip tooltip = new Tooltip(text);
    Font font = Font.font("Arial", 18);
    tooltip.setFont(font);
    Tooltip.install(polygon, tooltip);
  }

  protected String getNeighborDistance(Territory territory) {
    Map<Territory, Integer> distanceMap = this.gameMap.getNeighborDist(territory);
    String info = "Neighbor distance: \n";
    for (Territory currTerritory : distanceMap.keySet()) {
      info = info + currTerritory.getName() + " : " + distanceMap.get(currTerritory) + " \n";
    }
    return info;
  }

  protected String getUnitsNumberByLevel(Territory territory) {
    String info = "Units number by level: \n";
    for (int i = 0; i < territory.getNumLevels(); i++) {
      info = info + "Level " + i + ": " + territory.getUnitsNumByLevel(i) + " \n";
    }
    return info;
  }

  /**
   *
   * Sets the polygons representing the territories on the game map. For each
   * territory in the set, a polygon is created using the PolygonGetter object,
   * and the color, tooltip, mouse click event, and text of the polygon are set.
   * The polygon is then added to the mapPane along with its text representation.
   *
   * @param mapPane:     The pane representing the game map.
   * @param territories: A set of Territory objects representing the territories
   *                     on the game map.
   */
  protected void initializeMap(Pane mapPane, Set<Territory> allTerritories, Set<Territory> visibleTerritories) {

    for (Territory currTerritory : allTerritories) {
      int ownerID = currTerritory.getOwnerId();
      String name = currTerritory.getName();

      if (!playerColor.containsKey(ownerID)) {
        this.playerColor.put(ownerID, this.colors.remove(0));
        System.out.println("OwnerId: " + ownerID + ", Color: " + this.playerColor.get(ownerID));
      }

      Polygon currPolygon = this.polygonGetter.getPolygon(currTerritory);
      currPolygon.setId(name);

      if (!visibleTerritories.contains(currTerritory)) {
        greyOutPolygon(currPolygon);
        if (!previouslySeenTerritories.containsKey(currTerritory)) {
          // 1. never visible
          mapPane.getChildren().add(currPolygon);
          // return;
        } else {
          // 2. previously visiable : display obselete information
          // int oldId =
          // Integer.valueOf(previouslySeenTerritories.get(currTerritory).get("ownerID"));
          // setPolygonColor(currPolygon, oldId);
          String oldTooltip = previouslySeenTerritories.get(currTerritory).get("tooltip");
          setPolygonTooltip(currPolygon, oldTooltip);
          setPolygonMouseClick(currPolygon, currTerritory, true);

          String oldPolygonText = previouslySeenTerritories.get(currTerritory).get("polygonText");
          Text polygonInfo = setPolygonText(mapPane, currPolygon, oldPolygonText);
          mapPane.getChildren().add(currPolygon);
          polygonInfo.toFront();
        }
      } else {
        // 3. currently visible : set animation + display all information + display Spy
        // info
        setPolygonColor(currPolygon, ownerID);

        int mySpyNum = currTerritory.getSpyNumByPlayerId(playerId);
        String tooltip = " Territory: " + name + "\n OwnerID: " + ownerID + "\n" + getNeighborDistance(currTerritory)
            + getUnitsNumberByLevel(currTerritory) + " Food prod: " + currTerritory.getFood() + "\n Tech prod: "
            + currTerritory.getTechnology() + "\n My Spies: " + mySpyNum;
        setPolygonTooltip(currPolygon, tooltip);
        setPolygonMouseClick(currPolygon, currTerritory, false);

        String spiesView = constructSpiesView(mySpyNum);
        System.out.println("Territory " + currTerritory.getName() + " SpiesView: " + spiesView);
        String polygonText = name + spiesView + " (p" + ownerID + ")";
        Text polygonInfo = setPolygonText(mapPane, currPolygon, polygonText);

        if (!previouslySeenTerritories.containsKey(currTerritory)) {
          previouslySeenTerritories.put(currTerritory, new HashMap<>());
        }

        previouslySeenTerritories.get(currTerritory).put("ownerID", String.valueOf(ownerID));
        previouslySeenTerritories.get(currTerritory).put("tooltip", tooltip);
        previouslySeenTerritories.get(currTerritory).put("polygonText", polygonText);

        mapPane.getChildren().add(currPolygon);
        polygonInfo.toFront();
      }
    }
  }

  protected void updateMap(Pane mapPane, Set<Territory> allTerritories, Set<Territory> visibleTerritories) {
    for (Territory currTerritory : allTerritories) {
      int ownerID = currTerritory.getOwnerId();
      String name = currTerritory.getName();
      for (Node node : mapPane.getChildren()) {
        if (node.getId() != null && node.getId().equals(name)) {
          // display them
          Polygon polygon = (Polygon) node;

          if (!visibleTerritories.contains(currTerritory)) {
            /* Grey out all territories */
            greyOutPolygon(polygon);
            if (!previouslySeenTerritories.containsKey(currTerritory)) {
              /* 1. never visible */
              continue;
            } else {
              /*
               * 2. Display territories with their obsolete information that were previously
               * seen but now cannot be seen
               */
              String oldPolygonText = previouslySeenTerritories.get(currTerritory).get("polygonText");
              setPolygonText(mapPane, polygon, oldPolygonText);

              Tooltip.uninstall(polygon, null);
              String oldTooltip = previouslySeenTerritories.get(currTerritory).get("tooltip");
              setPolygonTooltip(polygon, oldTooltip);

              setPolygonMouseClick(polygon, currTerritory, true);
            }
          } else {

            /*
             * 3. Display all visible territories with their newest information, and update
             * their information into the previouslySeenTerritories
             */
            // 3.1. display
            setPolygonColor(polygon, ownerID);
            setPolygonMouseClick(polygon, currTerritory, false);

            int mySpyNum = currTerritory.getSpyNumByPlayerId(playerId);
            String spiesView = constructSpiesView(mySpyNum);
            System.out.println("Terrtory " + currTerritory.getName() + " SpiesView: " + spiesView);

            String polygonText = name + spiesView + " (p" + ownerID + ")";
            setPolygonText(mapPane, polygon, polygonText); // TODO greyed out if not visible

            Tooltip.uninstall(polygon, null);
            String tooltip = " Territory: " + name + "\n OwnerID: " + ownerID + "\n"
                + getNeighborDistance(currTerritory) + getUnitsNumberByLevel(currTerritory) + " Food prod: "
                + currTerritory.getFood() + "\n Tech prod: " + currTerritory.getTechnology() + "\n My Spies: "
                + mySpyNum;
            setPolygonTooltip(polygon, tooltip);

            // 3.2. store their info
            if (!previouslySeenTerritories.containsKey(currTerritory)) {
              previouslySeenTerritories.put(currTerritory, new HashMap<>());
            }

            previouslySeenTerritories.get(currTerritory).put("ownerID", String.valueOf(ownerID));
            previouslySeenTerritories.get(currTerritory).put("tooltip", tooltip);
            previouslySeenTerritories.get(currTerritory).put("polygonText", polygonText);
          }
        }
      }
    }
  }

  private String constructSpiesView(int num) {
    String result = "";
    for (int i = 0; i < num; i++) {
      result += "*";
    }
    return result;
  }

  private void printTerritories(Set<Territory> territories) {
    for (Territory t : territories) {
      System.out.print(t.getName() + " ");
    }
    System.out.println();
  }

  public void refresh() {
    Pane mapPane = mainPageController.getMapPane();
    Set<Territory> allTerritories = gameMap.getTerritorySet();
    Set<Territory> visiableTerritories = gameMap.getVisibleTerritoriesByPlayerId(playerId);

    System.out.println("Visible territories for player " + playerId + ":");
    printTerritories(visiableTerritories);

    if (mapPane.getChildren().isEmpty()) {
      initializeMap(mapPane, allTerritories, visiableTerritories);
    } else {
      updateMap(mapPane, allTerritories, visiableTerritories);
    }
  }
}
