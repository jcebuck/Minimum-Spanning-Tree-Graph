import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class Graph {

	List<Position> positions = new ArrayList<>();
	List<Edge> edges = new ArrayList<>();

	public static Graph randomGraph(int numberOfVertices, double edgeProbability) {
		Graph graph = new Graph();
		Random random = new Random();
		for (int i = 0; i < numberOfVertices; i++) {
			graph.addVertex(random.nextInt(MyGraphDisplay.WIDTH + 1), random.nextInt(MyGraphDisplay.HEIGHT + 1));
		}


		for (int i = 0; i < numberOfVertices; i++){
			for (int j = 0; j < numberOfVertices; j++) {
				if (j <= i) continue;
				if (Math.random() <= edgeProbability / 100) graph.connectVertices(i, j);
			}
		}
		return graph;
	}

	public void addVertex(int x, int y) {
		positions.add(new Position(x, y));
	}

	public void connectVertices(int vertex1, int vertex2) {
		if (getEdge(vertex1, vertex2) == null) edges.add(new Edge(vertex1, vertex2));
	}

	public Position getPosition(int vertex) {
		return positions.get(vertex);
	}

	public int getNumberOfVertices() {
		return positions.size();
	}

	public int getNumberOfEdges() {
		return edges.size();
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public Edge getEdge(int vertex1, int vertex2) {
		for (Edge edge : edges) {
			if ((edge.getFromVertex() == vertex1 && edge.getToVertex() == vertex2)
					|| (edge.getToVertex() == vertex1 && edge.getFromVertex() == vertex2))
				return edge;
		}
		return null;
	}

	public boolean edgeExists(int vertex1, int vertex2) {
		return getEdge(vertex1, vertex2) != null;
	}

	public double getWeight(int vertex1, int vertex2) {
		Edge edge = getEdge(vertex1, vertex2);
		if (edge == null) throw new RuntimeException();
		return edge.getWeight();
	}

	public List<Integer> getNeighbours(int vertex) {
		List<Integer> connectedVertices = new ArrayList<>();

		for (Edge edge : edges) {
			if (edge.getFromVertex() == vertex) connectedVertices.add(edge.getToVertex());
			else if (edge.getToVertex() == vertex) connectedVertices.add(edge.getFromVertex());
		}

		return connectedVertices;
	}

	public List<Edge> getEdgesConnectedTo(int vertex) {
		List<Edge> edges = new ArrayList<>();

		for (Edge edge : this.edges) {
			if (edge.getFromVertex() == vertex) edges.add(edge);
			else if (edge.getToVertex() == vertex) edges.add(edge);
		}

		return edges;
	}

	public void computePrims() {
		if (getNumberOfVertices() < 1) return;
		List<Edge> bestEdges = new ArrayList<>();
		List<Integer> usedVertices = new ArrayList<>();
		usedVertices.add(0);
		List<Edge> neighbours = new ArrayList<>();
		while (usedVertices.size() < getNumberOfVertices()) {
			neighbours.addAll(getEdgesConnectedTo(usedVertices.get(usedVertices.size() - 1)));
			Collections.sort(neighbours, new EdgeComparator());

			boolean hasFoundEdge = false;

			for (Edge edge : neighbours) {
				if (usedVertices.contains(edge.getFromVertex()) && !usedVertices.contains(edge.getToVertex())) {
					bestEdges.add(edge);
					usedVertices.add(edge.getToVertex());
					hasFoundEdge = true;
					break;
				}
				else if (usedVertices.contains(edge.getToVertex()) && !usedVertices.contains(edge.getFromVertex())) {
					bestEdges.add(edge);
					usedVertices.add(edge.getFromVertex());
					hasFoundEdge = true;
					break;
				}
			}

			if (!hasFoundEdge) break;
		}
		edges.clear();
		edges.addAll(bestEdges);
	}

	public void computeKruskal() {
		if (getNumberOfEdges() < getNumberOfVertices() - 1) return;

		List<Edge> oldEdges = new ArrayList<>(edges);
		edges.clear();
		Collections.sort(oldEdges, new EdgeComparator());
		int count = 0;
		for (int i = count; i < oldEdges.size() && getNumberOfEdges() < getNumberOfVertices() - 1; i++) {
			Edge edge = oldEdges.get(i);
			if (!isCycle(edge.getFromVertex(), edge.getToVertex())) {
				edges.add(edge);
			}
			count++;
		}
	}

	public double getTotalWeight() {
		double totalWeight = 0;
		for (Edge edge : edges) totalWeight += edge.getWeight();
		return totalWeight;
	}

	public boolean isCycle(int fromVertex, int toVertex) {
		return recursiveNeighbourSearch(toVertex, toVertex, fromVertex, 1);
	}

	public boolean recursiveNeighbourSearch(int vertex, int previousVertex, int vertexToFind, int vertexCount) {
		if (vertexCount > getNumberOfVertices()) return false;
		List<Integer> neighbours = getNeighbours(vertex);
		neighbours.remove(new Integer(previousVertex));
		if (neighbours.contains(vertexToFind)) return true;
		for (Integer neighbour : neighbours) {
			if (recursiveNeighbourSearch(neighbour, vertex, vertexToFind, ++vertexCount)) return true;
		}
		return false;
	}


	public class Position {

		private int x;
		private int y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}

	public class Edge {

		private int fromVertex;
		private int toVertex;
		private double weight;

		public Edge(int fromVertex, int toVertex) {
			this.fromVertex = fromVertex;
			this.toVertex = toVertex;

			Position p1 = getPosition(fromVertex);
			Position p2 = getPosition(toVertex);
			int x1 = p1.getX();
			int y1 = p1.getY();
			int x2 = p2.getX();
			int y2 = p2.getY();
			int xRange = x1 > x2 ? x1 - x2 : x2 - x1;
			int yRange = y1 > y2 ? y1 - y2 : y2 - y1;
			weight = Math.sqrt(Math.pow(xRange, 2) + Math.pow(yRange, 2));
		}

		public int getFromVertex() {
			return fromVertex;
		}

		public int getToVertex() {
			return toVertex;
		}

		public double getWeight() {
			return weight;
		}

	}

	class EdgeComparator implements Comparator<Edge> {

		public int compare(Edge o1, Edge o2) {
			return (int) (o1.getWeight() - o2.getWeight());
		}

	}

}
