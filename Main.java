import graph.CityGraph;

public class Main {

  public static void main(String[] args) {
    CityGraph city = new CityGraph();

    city.addIntersection("A", "Main Street");
    city.addIntersection("B", "Broadway");
    city.addIntersection("C", "Park Avenue");
    city.addIntersection("D", "Wall Street");

    city.connect("A", "B", 5.0, 50.0);
    city.connect("A", "C", 3.0, 30.0);
    city.connect("B", "D", 2.0, 40.0);
    city.connect("C", "D", 4.0, 40.0);

    System.out.println("Vertices: " + city.vertexCount());
    System.out.println("Edges: " + city.edgeCount());
    System.out.println("Neighbors of A: " + city.getNeighbors("A"));

    System.out.println("BFS: " + city.bfs("A"));
    System.out.println("DFS: " + city.dfs("A"));

    System.out.println("Shortest A to D: " + city.shortestPath("A", "D"));
    System.out.println("Reachable D to A: " + city.isReachable("D", "A"));

    System.out.println("In-degree D: " + city.inDegree("D"));
    System.out.println("Out-degree A: " + city.outDegree("A"));
  }
}
