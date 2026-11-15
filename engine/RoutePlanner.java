package engine;

import graph.CityGraph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import model.Road;

public class RoutePlanner {

  private final CityGraph graph;

  // ===== Nested Record =====

  private record State(String id, double time) {}

  // ===== Constructor =====

  public RoutePlanner(CityGraph graph) {
    this.graph = Objects.requireNonNull(graph, "CityGraph cannot be null");
  }

  // ===== Dijkstra's Algorithm =====

  /**
   * Finds the fastest route between two intersections.
   *
   * <p>Uses Dijkstra's algorithm with a min-priority queue.
   *
   * @return Intersection IDs in route order, or an empty list if the destination is unreachable.
   */
  public List<String> fastestRoute(String from, String to) {
    if (!graph.containsIntersection(from) || !graph.containsIntersection(to)) {
      throw new IllegalArgumentException("Intersection does not exist");
    }

    Map<String, Double> distances = new HashMap<>();
    Map<String, String> previous = new HashMap<>();

    PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingDouble(State::time));

    distances.put(from, 0.0);
    pq.offer(new State(from, 0.0));

    while (!pq.isEmpty()) {
      State current = pq.poll();

      // Ignore outdated priority queue entries.
      if (current.time() > distances.getOrDefault(current.id(), Double.POSITIVE_INFINITY)) {
        continue;
      }

      // Destination reached with minimum travel time.
      if (current.id().equals(to)) {
        break;
      }

      // Examine outgoing roads.
      for (Road road : graph.getOutgoingRoads(current.id()).values()) {
        String neighbor = road.to();

        double newTime = current.time() + road.estimatedTravelTime();

        if (newTime < distances.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {

          distances.put(neighbor, newTime);
          previous.put(neighbor, current.id());

          pq.offer(new State(neighbor, newTime));
        }
      }
    }

    // No route exists.
    if (!distances.containsKey(to)) {
      return List.of();
    }

    // ===== Reconstruct Fastest Route =====

    List<String> path = new ArrayList<>();

    for (String current = to; current != null; current = previous.get(current)) {
      path.add(current);
    }

    Collections.reverse(path);
    return path;
  }

  // ===== Travel Time =====

  /** Calculates the total estimated travel time of a route in minutes. */
  public double calculateTravelTime(List<String> route) {
    Objects.requireNonNull(route, "Route cannot be null");

    if (route.isEmpty()) {
      return 0.0;
    }

    if (route.size() == 1) {
      if (!graph.containsIntersection(route.get(0))) {
        throw new IllegalArgumentException("Unknown intersection");
      }
      return 0.0;
    }

    double total = 0.0;

    for (int i = 0; i < route.size() - 1; i++) {
      Road road = graph.getRoad(route.get(i), route.get(i + 1));

      if (road == null) {
        throw new IllegalArgumentException(
            "No road exists between " + route.get(i) + " and " + route.get(i + 1));
      }

      total += road.estimatedTravelTime();
    }

    return total;
  }
}
