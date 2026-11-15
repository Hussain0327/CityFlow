import engine.RoutePlanner;
import graph.CityGraph;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    CityGraph city = new CityGraph();

    city.addIntersection("A", "Main Street");
    city.addIntersection("B", "Broadway");
    city.addIntersection("C", "Park Avenue");

    city.connect("A", "B", 10.0, 30.0); // 20 minutes
    city.connect("A", "C", 2.0, 30.0); // 4 minutes
    city.connect("C", "B", 2.5, 30.0); // 5 minutes

    RoutePlanner planner = new RoutePlanner(city);

    List<String> route = planner.fastestRoute("A", "B");

    System.out.println("Fastest route: " + route);
    System.out.println("Travel time: " + planner.calculateTravelTime(route) + " minutes");
  }
}
