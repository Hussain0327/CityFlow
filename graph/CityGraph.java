package graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import model.Road;

public class CityGraph {

  private final Map<String, Intersection> intersections = new HashMap<>();
  private int edgeCount;

  // ===== Nested Vertex Class =====

  private static final class Intersection {

    private final String id;
    private final String name;
    private final Map<String, Road> adjacents = new HashMap<>();

    private Intersection(String id, String name) {
      this.id = validateField(id, "ID");
      this.name = validateField(name, "Name");
    }

    private static String validateField(String field, String fieldName) {
      if (field == null || field.isBlank()) {
        throw new IllegalArgumentException(fieldName + " cannot be null or blank");
      }
      return field.strip();
    }

    private boolean connect(Intersection other, Road road) {
      Objects.requireNonNull(other, "Destination cannot be null");
      Objects.requireNonNull(road, "Road cannot be null");

      if (id.equals(other.id)) {
        throw new IllegalArgumentException("An intersection cannot connect to itself");
      }

      if (!road.from().equals(id) || !road.to().equals(other.id)) {
        throw new IllegalArgumentException("Road endpoints do not match intersections");
      }

      return adjacents.putIfAbsent(other.id, road) == null;
    }

    private boolean disconnect(Intersection other) {
      return adjacents.remove(other.id) != null;
    }

    private boolean isConnected(Intersection other) {
      return adjacents.containsKey(other.id);
    }
  }

  // ===== Vertex Helpers =====

  private Intersection findIntersection(String id) {
    Objects.requireNonNull(id, "Intersection ID cannot be null");
    return intersections.get(id.strip());
  }

  private Intersection requireIntersection(String id) {
    Intersection intersection = findIntersection(id);

    if (intersection == null) {
      throw new IllegalArgumentException("Intersection does not exist: " + id);
    }

    return intersection;
  }

  // ===== Vertex Operations =====

  public boolean addIntersection(String id, String name) {
    Intersection intersection = new Intersection(id, name);

    return intersections.putIfAbsent(intersection.id, intersection) == null;
  }

  public boolean removeIntersection(String id) {
    Intersection target = findIntersection(id);

    if (target == null) {
      return false;
    }

    edgeCount -= target.adjacents.size();
    intersections.remove(target.id);

    for (Intersection vertex : intersections.values()) {
      if (vertex.adjacents.remove(target.id) != null) {
        edgeCount--;
      }
    }

    return true;
  }

  public boolean containsIntersection(String id) {
    return findIntersection(id) != null;
  }

  public Set<String> getIntersections() {
    return Set.copyOf(intersections.keySet());
  }

  public int vertexCount() {
    return intersections.size();
  }

  // ===== Edge Operations =====

  public boolean connect(String from, String to, double distance, double speedLimit) {

    Intersection source = requireIntersection(from);
    Intersection destination = requireIntersection(to);

    Road road = new Road(source.id, destination.id, distance, speedLimit);

    if (!source.connect(destination, road)) {
      return false;
    }

    edgeCount++;
    return true;
  }

  public boolean disconnect(String from, String to) {
    Intersection source = requireIntersection(from);
    Intersection destination = requireIntersection(to);

    if (!source.disconnect(destination)) {
      return false;
    }

    edgeCount--;
    return true;
  }

  public boolean hasEdge(String from, String to) {
    Intersection source = requireIntersection(from);
    Intersection destination = requireIntersection(to);

    return source.isConnected(destination);
  }

  public Road getRoad(String from, String to) {
    Intersection source = requireIntersection(from);
    Intersection destination = requireIntersection(to);

    return source.adjacents.get(destination.id);
  }

  public Set<String> getNeighbors(String id) {
    Intersection intersection = requireIntersection(id);

    return Set.copyOf(intersection.adjacents.keySet());
  }

  public int inDegree(String id) {
    Intersection target = requireIntersection(id);
    int degree = 0;

    for (Intersection vertex : intersections.values()) {
      if (vertex.adjacents.containsKey(target.id)) {
        degree++;
      }
    }

    return degree;
  }

  public int outDegree(String id) {
    return requireIntersection(id).adjacents.size();
  }

  public int edgeCount() {
    return edgeCount;
  }

  // ===== Graph Traversals =====

  public List<String> bfs(String start) {
    Intersection source = requireIntersection(start);

    List<String> order = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    Queue<String> queue = new ArrayDeque<>();

    visited.add(source.id);
    queue.offer(source.id);

    while (!queue.isEmpty()) {
      String current = queue.remove();
      order.add(current);

      for (String neighbor : intersections.get(current).adjacents.keySet()) {
        if (visited.add(neighbor)) {
          queue.offer(neighbor);
        }
      }
    }

    return order;
  }

  public List<String> dfs(String start) {
    Intersection source = requireIntersection(start);

    List<String> order = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    Deque<String> stack = new ArrayDeque<>();

    stack.push(source.id);

    while (!stack.isEmpty()) {
      String current = stack.pop();

      if (!visited.add(current)) {
        continue;
      }

      order.add(current);

      for (String neighbor : intersections.get(current).adjacents.keySet()) {
        if (!visited.contains(neighbor)) {
          stack.push(neighbor);
        }
      }
    }

    return order;
  }

  // ===== Pathfinding =====

  /**
   * Returns a path containing the fewest edges.
   *
   * <p>This does not minimize distance or travel time.
   */
  public List<String> shortestPath(String from, String to) {
    Intersection source = requireIntersection(from);
    Intersection destination = requireIntersection(to);

    Queue<String> queue = new ArrayDeque<>();
    Map<String, String> parent = new HashMap<>();

    queue.offer(source.id);
    parent.put(source.id, null);

    while (!queue.isEmpty()) {
      String current = queue.remove();

      if (current.equals(destination.id)) {
        break;
      }

      for (String neighbor : intersections.get(current).adjacents.keySet()) {
        if (!parent.containsKey(neighbor)) {
          parent.put(neighbor, current);
          queue.offer(neighbor);
        }
      }
    }

    if (!parent.containsKey(destination.id)) {
      return List.of();
    }

    List<String> path = new ArrayList<>();

    for (String current = destination.id; current != null; current = parent.get(current)) {
      path.add(current);
    }

    java.util.Collections.reverse(path);
    return path;
  }

  public boolean isReachable(String from, String to) {
    Intersection source = requireIntersection(from);
    Intersection destination = requireIntersection(to);

    if (source.id.equals(destination.id)) {
      return true;
    }

    Set<String> visited = new HashSet<>();
    Queue<String> queue = new ArrayDeque<>();

    visited.add(source.id);
    queue.offer(source.id);

    while (!queue.isEmpty()) {
      String current = queue.remove();

      for (String neighbor : intersections.get(current).adjacents.keySet()) {
        if (neighbor.equals(destination.id)) {
          return true;
        }

        if (visited.add(neighbor)) {
          queue.offer(neighbor);
        }
      }
    }

    return false;
  }

  public Map<String, Road> getOutgoingRoads(String id) {
    Intersection intersection = requireIntersection(id);
    return Map.copyOf(intersection.adjacents);
  }
}
