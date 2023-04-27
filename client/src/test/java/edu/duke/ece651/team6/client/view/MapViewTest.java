package edu.duke.ece651.team6.client.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

import edu.duke.ece651.team6.client.controller.MainPageController;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.PolygonGetter;
import edu.duke.ece651.team6.shared.Territory;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MapViewTest {

  MainPageController mainPageController = Mockito.mock(MainPageController.class);
  GameMap gameMap = Mockito.mock(GameMap.class);
  int playerId = 0;
  PolygonGetter polygonGetter = Mockito.mock(PolygonGetter.class);
  Map<Integer, Color> playerColor = new HashMap<>();
  ArrayList<Color> colors = new ArrayList<>();
  Map<Territory, Map<String, String>> previouslySeenTerritories = new HashMap<>();
  MapView mapView = null;

  @BeforeEach
  public void setup() {
    mapView = new MapView(mainPageController, gameMap, playerId, polygonGetter, playerColor, colors,
        previouslySeenTerritories);
  }

  @Test
  public void test_() {
  }

  @Test
  void testSetPolygonColor() {
    Polygon polygon = new Polygon();
    mapView.setPolygonColor(polygon, playerId);
    assertEquals(mapView.playerColor.get(playerId), polygon.getFill());
  }

  @Test
  void testGreyOutPolygon() {
    Polygon polygon = new Polygon();
    mapView.greyOutPolygon(polygon);
    assertEquals(Color.LIGHTGREY, polygon.getFill());
  }

  @Test
  void testPerformPolygonAnimation() throws InterruptedException {
    // Create a polygon
    Polygon polygon = new Polygon();
    Color originalColor = Color.RED;
    Color expectedColor = Color.LIGHTGRAY;
    polygon.setFill(originalColor);
    mapView.performPolygonAnimation(polygon);
    assertEquals(expectedColor, polygon.getFill());
    Thread.sleep(200);
    assertEquals(originalColor, polygon.getFill());
  }

  @Test
  void testDisplayTerritoryInfo() {
    // Create the necessary objects and variables
    Text territoryInfoText1 = new Text();
    Text territoryInfoText2 = new Text();
    Text territoryInfoText3 = new Text();
    Territory territory = new Territory(/* ... */);
    boolean obsolete = false;

    // Call the displayTerritoryInfo method
    mapView.displayTerritoryInfo(territoryInfoText1, territoryInfoText2, territoryInfoText3, territory, obsolete);

    // Perform assertions on the Text objects
    assertEquals(
        " Territory: " + territory.getName() + "\n OwnerID: " + territory.getOwnerId() + " \n" + " Food prod: "
            + territory.getFood() + "\n Tech prod: " + territory.getTechnology() + "\n My Spies: "
            + (territory.getSpyNumByPlayerId(playerId, true) + territory.getSpyNumByPlayerId(playerId, false)),
        territoryInfoText1.getText());
    assertEquals(mapView.getNeighborDistance(territory), territoryInfoText2.getText());
    assertEquals(mapView.getUnitsNumberByLevel(territory), territoryInfoText3.getText());

    // Check if the information is stored in previouslySeenTerritories
    assertTrue(mapView.previouslySeenTerritories.containsKey(territory));
    assertEquals(
        " Territory: " + territory.getName() + "\n OwnerID: " + territory.getOwnerId() + " \n" + " Food prod: "
            + territory.getFood() + "\n Tech prod: " + territory.getTechnology() + "\n My Spies: "
            + (territory.getSpyNumByPlayerId(playerId, true) + territory.getSpyNumByPlayerId(playerId, false)),
        mapView.previouslySeenTerritories.get(territory).get("info1"));
    assertEquals(mapView.getNeighborDistance(territory), mapView.previouslySeenTerritories.get(territory).get("info2"));
    assertEquals(mapView.getUnitsNumberByLevel(territory),
        mapView.previouslySeenTerritories.get(territory).get("info3"));
  }

  @Test
  void testSetPolygonMouseClick() {
    Polygon polygon = new Polygon();
    Territory territory = new Territory();
    boolean obsolete = false;

    Text territoryInfoText1 = new Text();
    Text territoryInfoText2 = new Text();
    Text territoryInfoText3 = new Text();
    Mockito.when(mainPageController.getTerritoryInfoText1()).thenReturn(territoryInfoText1);
    Mockito.when(mainPageController.getTerritoryInfoText2()).thenReturn(territoryInfoText2);
    Mockito.when(mainPageController.getTerritoryInfoText3()).thenReturn(territoryInfoText3);

    mapView.setPolygonMouseClick(polygon, territory, obsolete);

    MouseEvent clickEvent = mock(MouseEvent.class);

    polygon.getOnMouseClicked().handle(clickEvent);

    mapView.performPolygonAnimation(polygon);
    mapView.displayTerritoryInfo(territoryInfoText1, territoryInfoText2, territoryInfoText3, territory, obsolete);

    verify(mainPageController, times(1)).getTerritoryInfoText1();
    verify(mainPageController, times(1)).getTerritoryInfoText2();
    verify(mainPageController, times(1)).getTerritoryInfoText3();
  }

  @Disabled
  @Test
  void testSetPolygonText() {
    Polygon polygon = new Polygon();
    Pane mapPane = new Pane();
    String text = "Sample Text";

    // Call the setPolygonText method
    Text result = mapView.setPolygonText(mapPane, polygon, text);

    // Verify that the Text object is added to the mapPane
    assertTrue(mapPane.getChildren().contains(result));

    // Verify that the Text object has the correct properties set
    assertEquals(text, result.getText());
    assertEquals(polygon.getId() + "Text", result.getId());
    assertEquals(Font.font("Arial", FontWeight.BOLD, 22), result.getFont());
    assertEquals(Color.BLACK, result.getFill());

    boolean hasTextWithId = false;
    for (Node node : mapPane.getChildren()) {
      if (node.getId().equals(polygon.getId() + "Text")) {
        hasTextWithId = true;
        break;
      }
    }
    assertTrue(hasTextWithId);
  }

  @Disabled
  @Test
  void testSetPolygonTooltip() {
    // Create the necessary objects and variables
    Polygon polygon = new Polygon();
    String text = "Sample Tooltip";

    // Call the setPolygonTooltip method
    mapView.setPolygonTooltip(polygon, text);

    // Verify that the tooltip is installed on the polygon
    // verify(polygon, times(1)).setOnMouseClicked(any());
    // verify(polygon, times(1)).setTooltip(any(Tooltip.class));
  }
}
