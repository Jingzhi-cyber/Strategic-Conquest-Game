package edu.duke.ece651.team6.client.view;

import java.util.ArrayList;
import java.util.Arrays;
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

  PolygonGetter polygonGetter;

  Map<Integer, Color> playerColor;

  ArrayList<Color> colors;

  public MapView(MainPageController mainPageController, GameMap gameMap, PolygonGetter polygonGetter,
      Map<Integer, Color> playerColor, ArrayList<Color> colors) {
    this.mainPageController = mainPageController;
    this.gameMap = gameMap;

    this.polygonGetter = polygonGetter;
    this.playerColor = playerColor;
    this.colors = colors;
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
      Territory territory) {
    String info1 = "Territory: " + territory.getName() + "\nOwnerID: " + territory.getOwnerId() + " \n" + "Food prod: "
        + territory.getFood() + "\nTech prod: " + territory.getTechnology();
    String info2 = getNeighborDistance(territory);
    String info3 = getUnitsNumberByLevel(territory);
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
  protected void setPolygonMouseClick(Polygon polygon, Territory territory) {
    polygon.setOnMouseClicked((MouseEvent click_event) -> {
      performPolygonAnimation(polygon);
      displayTerritoryInfo(this.mainPageController.getTerritoryInfoText1(),
          this.mainPageController.getTerritoryInfoText2(), this.mainPageController.getTerritoryInfoText3(), territory);
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
  protected void setMap(Pane mapPane, Set<Territory> territories) {
    for (Territory currTerritory : territories) {
      int ownerID = currTerritory.getOwnerId();
      if (!playerColor.containsKey(ownerID)) {
        this.playerColor.put(ownerID, this.colors.remove(0));
      }
      String name = currTerritory.getName();
      Polygon currPolygon = this.polygonGetter.getPolygon(currTerritory);
      currPolygon.setId(name);
      setPolygonColor(currPolygon, ownerID);

      setPolygonTooltip(currPolygon,
          "Territory: " + name + "\nOwnerID: " + ownerID + "\n" + getNeighborDistance(currTerritory)
              + getUnitsNumberByLevel(currTerritory) + "Food prod: " + currTerritory.getFood() + "\nTech prod: "
              + currTerritory.getTechnology());
      setPolygonMouseClick(currPolygon, currTerritory);
      
      Text polygonInfo = setPolygonText(mapPane, currPolygon, name + " - " + ownerID);
      mapPane.getChildren().add(currPolygon);
      polygonInfo.toFront();
    }
  }

  public void refresh() {
    Pane mapPane = mainPageController.getMapPane();
    Set<Territory> territories = gameMap.getTerritorySet();

    if (mapPane.getChildren().isEmpty()) {
      setMap(mapPane, territories);
    } else {
      for (Territory currTerritory : territories) {
        int ownerID = currTerritory.getOwnerId();
        String name = currTerritory.getName();
        for (Node node : mapPane.getChildren()) {
          if (node.getId() != null && node.getId().equals(name)) {
            Polygon polygon = (Polygon) node;
            setPolygonColor(polygon, ownerID);
            setPolygonMouseClick(polygon, currTerritory);
            setPolygonText(mapPane, polygon, name + " - " + ownerID); // TODO greyed out if not visible
            Tooltip.uninstall(polygon, null);
            setPolygonTooltip(polygon,
                "Territory: " + name + "\nOwnerID: " + ownerID + "\n" + getNeighborDistance(currTerritory)
                    + getUnitsNumberByLevel(currTerritory) + "Food prod: " + currTerritory.getFood() + "\nTech prod: "
                    + currTerritory.getTechnology());
          }
        }
      }
    }
  }
}
