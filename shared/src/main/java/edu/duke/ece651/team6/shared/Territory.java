package edu.duke.ece651.team6.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Territory implements java.io.Serializable, Cloneable {
  private final String name;
  private int ownerId;
  private List<Deque<Unit>> units;
  private Map<Integer, Deque<Spy>> spies;
  private final int numLevel;
  private WarZone warZone;
  private boolean underWar;
  private int food;
  private int technology;
  private int cloakedTurns;
  private List<Edge> edges;
  private List<Point2D> points;
  private int[] color;

  public Territory() {
    this("Default Territory");
  }

  /**
   * Constructs a territory with a name, that does not have owner and any units
   */
  public Territory(String name) {
    this.name = name;
    this.ownerId = -1;
    numLevel = 7;
    unitsInit();
    this.spies = new HashMap<>();
    warZone = null;
    underWar = false;
    food = 5;
    technology = 5;
    cloakedTurns = 0;
  }

  public Territory(String name, int ownerId) {
    this.name = name;
    this.ownerId = ownerId;
    numLevel = 7;
    unitsInit();
    this.spies = new HashMap<>();
    warZone = null;
    underWar = false;
    food = 5;
    technology = 5;
    cloakedTurns = 0;
  }

  /**
   * Constructs a territory that has a specified name, with owner and some units
   * 
   * @param name  the name of territory
   * @param ownerId the owner of territory
   * @param units the number of units on the territory
   */
  public Territory(String name, int ownerId, int units) {
    this.name = name;
    if (units < 0) {
      throw new IllegalArgumentException("Territory's unit must be non-negative but is " + units);
    }
    this.ownerId = ownerId;
    numLevel = 7;
    unitsInit();
    this.units.get(0).addAll(UnitManager.newUnits(units));
    this.spies = new HashMap<>();
    warZone = null;
    underWar = false;
    food = 5;
    technology = 5;
    cloakedTurns = 0;
  }

  @Override
  public Object clone() {
    Territory territory = new Territory(this.name, this.ownerId);
    for (int i = 0; i < numLevel; i++) {
      Deque<Unit> sameLevelUnits = territory.units.get(i);
      for (Unit unit : this.units.get(i)) {
        sameLevelUnits.add((Unit) unit.clone());
      }
    }
    territory.spies.putAll(this.spies);
    return territory;
  }

  private void unitsInit() {
    units = new ArrayList<>();
    for (int i = 0; i < numLevel; i++) {
      units.add(new LinkedList<>());
    }
  }

  public String getName() {
    return this.name;
  }

  public int getOwnerId() {
    return this.ownerId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  public int getNumUnits() {
    int total = 0;
    for (Deque<Unit> unit : units) {
      total += unit.size();
    }
    return total;
  }

  private Army dispatchArmy(int[] numUnits) {
    Army army = new Army(ownerId);
    for (int i = 0; i < numLevel; i++) {
      for (int j = 0; j < numUnits[i]; j++) {
        army.add(units.get(i).poll());
      }
    }
    return army;
  }

  /**
   * Absorb an army. If the owner of this territory is the same as the owner of
   * army, the numUnits will update; If not, an IllegalArgumentException will be
   * thrown. If this territory has no Unit, the owner of the Army will become this
   * territory's owner.
   * 
   * @param army to join this territory
   */
  protected void absorbArmy(Army army) {
    if (Arrays.equals(getAllUnitsNum(), new int[numLevel])) {
      ownerId = army.getOwnerId();
    }
    for (Unit u : army.getUnitsList()) {
      units.get(u.level()).add(u);
    }
  }

  /**
   * Attacked by other armies. This action will make this territory has a new
   * warzone.
   * 
   * @param army which attackes this territory.
   */
  private void attackedBy(Army army) {
    if (!underWar) {
      underWar = true;
      warZone = new RoundFight(this);
    }
    warZone.add(army);
  }

  /**
   * Move numUnits Units to dest. Pre: dest territory has the same owner as this
   * territory, numUnits is less than numUnits of this territory. Post: Move
   * action done.
   * 
   * @param dest
   * @param numUnits
   */
  public void moveTo(Territory dest, int numUnits) {
    int[] a = new int[numLevel];
    a[0] = numUnits;
    moveTo(dest, a);
  }

  /**
   * Try to move some units to another Territory.
   * 
   * @param dest     the territory to move to.
   * @param numUnits an array, represents how many units of each level are engaged
   *                 in this move.
   */
  public void moveTo(Territory dest, int[] numUnits) {
    for (int i = 0; i < numLevel; i++) {
      if (numUnits[i] > units.get(i).size() || numUnits[i] < 0) {
        throw new IllegalArgumentException("Invalid move number: " + numUnits[i] + " in level: " + i + " Currently has "
            + units.get(i).size() + " units");
      }
    }
    if (ownerId != dest.getOwnerId()) {
      throw new IllegalArgumentException("Cannot move to a different player's territory!");
    }
    dest.absorbArmy(dispatchArmy(numUnits));
  }

  /**
   * Make an Army of size numUnits to attack target. Pre: target is adjacent to
   * this territory, numUnits is less than numUnits of this territory. Post:
   * attack is registered.
   * 
   * @param target   the territory to attack.
   * @param numUnits number of Units of this attack.
   */
  public void attack(Territory target, int numUnits) {
    int[] a = new int[numLevel];
    a[0] = numUnits;
    attack(target, a);
  }

  /**
   * Make an Army to attack the target.
   * 
   * @param target   the target to attack.
   * @param numUnits an array, indicate the number of each level of units in this
   *                 attack.
   */
  public void attack(Territory target, int[] numUnits) {
    if (ownerId == target.getOwnerId()) {
      throw new IllegalArgumentException("Player cannot attack own territory!");
    }
    for (int i = 0; i < numLevel; i++) {
      if (numUnits[i] > units.get(i).size() || numUnits[i] < 0) {
        throw new IllegalArgumentException("Invalid attack number: " + numUnits[i] + " in level: " + i
            + " Currently has " + units.get(i).size() + " units");
      }
    }
    target.attackedBy(dispatchArmy(numUnits));
  }

  private void recover() {
    units.get(0).add(UnitManager.newUnit());
  }

  /**
   * Notify the territory to run the war, update the status of territory. Post:
   * all attacks done.
   */
  public void update() {
    if (underWar) {
      int[] allUnitsNum = getAllUnitsNum();
      if (!Arrays.equals(allUnitsNum, new int[numLevel])) {
        warZone.add(dispatchArmy(allUnitsNum));
      }
      warZone.startWar();
      underWar = false;
      warZone = null;
    }
    recover();
    if (cloakedTurns > 0) {
      cloakedTurns--;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (o.getClass().equals(getClass())) {
      Territory t = (Territory) o;
      return t.name.equals(name);
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "(name: " + name + ", ownerId: " + ownerId + ")";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /* Initialize units information after player gives the information. */
  public void initNumUnits(int units) {
    this.units.get(0).addAll(UnitManager.newUnits(units));
  }

  /**
   * Set the amount of food produced by this territory
   * 
   * @param food the amount of food
   */
  public void setFood(int food) {
    this.food = food;
  }

  /* Get the amount of food produced by this territory. */
  public int getFood() {
    return this.food;
  }

  /**
   * Set the amount of technology produced by this territory
   * 
   * @param technology the amount of technology
   */
  public void setTechnology(int technology) {
    this.technology = technology;
  }

  /* Get the amount of technology produced by this territory. */
  public int getTechnology() {
    return this.technology;
  }

  /**
   * Get the total number of unit levels
   * 
   * @return this.numLevel
   */
  public int getNumLevels() {
    return this.numLevel;
  }

  public int getUnitsNumByLevel(int level) {
    return units.get(level).size();
  }

  public int getAllUnits() {
    return units.get(0).size();
  }

  /**
   * Get how many units of each level are left in this territory.
   * 
   * @return
   */
  public int[] getAllUnitsNum() {
    int[] a = new int[numLevel];
    for (int i = 0; i < numLevel; i++) {
      a[i] = units.get(i).size();
    }
    return a;
  }

  /**
   * Upgrade a unit with level now to level target.
   * 
   * @param now
   * @param target
   */
  public void upgradeOneUnit(int now, int target) {
    if (units.get(now).size() == 0) {
      throw new IllegalArgumentException("No unit with level " + now + ", upgrade failed!");
    }
    if (target < now || now < 0 || target < 0 || now >= numLevel || target >= numLevel) {
      throw new IllegalArgumentException("Invalid level!");
    }
    Unit unit = units.get(now).poll();
    UnitManager.upgrade(unit, target);
    units.get(target).add(unit);
  }

  /**
   * Add spy owned by the player to this territory
   * @param playerId
   * @param num the number of spy
   */
  public void addSpy(int playerId, int num) {
    if (!this.spies.containsKey(playerId)) {
      this.spies.put(playerId, new LinkedList<Spy>());
    }
    Deque<Spy> spy = this.spies.get(playerId);
    for (int i = 0; i < num; i++) {
      spy.add(new Spy(playerId));
    }
  }

  /**
   * Get the number of spy owned by a player on the territory
   * @param playerId
   * @return the number of spies
   */
  public int getSpyNumByPlayerId(int playerId) {
    Deque<Spy> spy = this.spies.getOrDefault(playerId, null);
    if (spy == null) {
      return 0;
    }
    return spy.size();
  }

  /**
   * Move spy to another territory
   * @param playerId is the playerId that makes this order
   * @param dest is the destination territory
   * @param num is the number of spies
   */
  public void moveSpyTo(int playerId, Territory dest, int num) {
    /** 
     * TODO: implement spy move order
     * 
     * 1. check if there is enough spy to move
     * 2. if the spy is on enemy's territory, can only move to adjacent territory
     * 3. if the spy is on owned territory, apply the same move rule as the units
     * 
     */
  }

  public void setCloakedTurn(int turn) {
    this.cloakedTurns = turn;
  }

  public int getCloakedTurn() {
    return this.cloakedTurns;
  }

  public void addEdge(Edge e) {
    if (edges == null) {
      edges = new LinkedList<>();
    }
    edges.add(e);
  }

  public List<Point2D> getPoints() {
    if (points != null) {
      return points;
    }
    points = new ArrayList<>();
    updateEdges();
    Edge e = edges.get(0);
    Point2D p = e.p1;
    Point2D pleft = e.p2;
    Edge adEdge = getAdjacentEdge(e, e.p1);
    Point2D pright = adEdge.p1;
    if (adEdge.p1.equals(p)) {
      pright = adEdge.p2;
    }
    double angleLeft = Math.asin((pleft.y - p.y) / p.dist(pleft));
    double angleRight = Math.asin((pright.y - p.y) / p.dist(pright));
    double angle;
    if (angleLeft > angleRight) {
      angle = angleLeft - angleRight;
    } else {
      angle = Math.PI - (angleRight - angleLeft);
    }
    if (angle > Math.PI / 2) {
      Point2D temp = pleft;
      pleft = pright;
      pright = temp;
    }
    points.add(pleft);
    points.add(p);
    points.add(pright);
    Edge ei = new Edge(p, pright);
    Point2D pi = pright;
    while (true) {
      Edge edge = getAdjacentEdge(ei, pi);
      Point2D next = edge.p1;
      if (edge.p1.equals(pi)) {
        next = edge.p2;
      }
      if (points.get(0).equals(next)) {
        break;
      }
      points.add(next);
      pi = next;
      ei = edge;
    }
    return points;
  }

  private void updateEdges() {
    Set<Point2D> set = new HashSet<>();
    for (Edge e : edges) {
      if (e.p1.isOnRectangle(1000, 500)) {
        set.add(e.p1);
      }
      if (e.p2.isOnRectangle(1000, 500)) {
        set.add(e.p2);
      }
    }
    if (set.size() == 2) {
      List<Point2D> list = new ArrayList<>(set);
      Point2D p1 = list.get(0);
      Point2D p2 = list.get(1);
      if (p2.x < p1.x) {
        Point2D temp = p1;
        p1 = p2;
        p2 = temp;
      }
      if (Math.abs(p1.x - p2.x) < 0.000001 || Math.abs(p1.y - p2.y) < 0.000001) {
        edges.add(new Edge(p1, p2));
      } else {
        Point2D p;
        if (Math.abs(p1.x) < 0.000001) {
          p = new Point2D(p1.x, p2.y);
        } else {
          p = new Point2D(p2.x, p1.y);
        }
        edges.add(new Edge(p, p1));
        edges.add(new Edge(p, p2));
      }
    }
  }

  private Edge getAdjacentEdge(Edge e, Point2D p) {
    for (Edge edge : edges) {
      if (e.equals(edge)) {
        continue;
      }
      if (edge.p1.equals(p) || edge.p2.equals(p)) {
        return edge;
      }
    }
    return null;
  }

  public void setColor(int r, int g, int b) {
    if (color == null) {
      color = new int[3];
    }
    color[0] = r;
    color[1] = g;
    color[2] = b;
  }

  public int[] getColor() {
    if (color == null) {
      setColor(235, 233, 243);
    }
    return color;
  }
}
