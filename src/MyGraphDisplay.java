import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;


public class MyGraphDisplay {

	public static final int WIDTH = 400;
	public static final int HEIGHT = 400;

	private static final int POINT_SIZE = 8;
	private Graph graph;
	private boolean showTotalWeight;
	private boolean showLineWeight = true;
	boolean isSetUp;

	public MyGraphDisplay() {
		randomGraph();
		//		setUpGraph();
	}

	public void randomGraph() {
		graph = Graph.randomGraph(20, 100.0);
	}

	public void setUpGraph() {
		graph = new Graph();
		graph.addVertex(10, 200);
		graph.addVertex(124, 101);
		graph.addVertex(150, 250);
		graph.addVertex(218, 198);
		graph.addVertex(258, 297);
		graph.addVertex(325, 125);
		graph.addVertex(360, 194);
		graph.connectVertices(0, 1);
		graph.connectVertices(0, 2);
		graph.connectVertices(1, 3);
		graph.connectVertices(2, 3);
		graph.connectVertices(3, 4);
		graph.connectVertices(4, 5);
		graph.connectVertices(5, 6);
		graph.connectVertices(2, 4);
		graph.connectVertices(3, 5);
		graph.connectVertices(4, 6);
		isSetUp = true;
	}

	public void display() {
		JFrame frame = new JFrame("Graph");
		final GraphPanel panel = new GraphPanel();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		JButton prims = new JButton("Prims");
		prims.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker<Void, Void>() {

					protected Void doInBackground() throws Exception {
						graph.computePrims();
						return null;
					}

					protected void done() {
						showTotalWeight = true;
						panel.repaint();
					}

				}.execute();
			}
		});

		JButton kruskal = new JButton("Kruskal");
		kruskal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker<Void, Void>() {

					protected Void doInBackground() throws Exception {
						graph.computeKruskal();
						return null;
					}

					protected void done() {
						showTotalWeight = true;
						panel.repaint();
					}

				}.execute();
			}
		});

		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker<Void, Void>() {

					protected Void doInBackground() throws Exception {
						if (isSetUp) setUpGraph();
						else randomGraph();
						return null;
					}

					protected void done() {
						showTotalWeight = false;
						panel.repaint();
					}

				}.execute();
			}
		});

		JCheckBox lineWeight = new JCheckBox("Show Weight", true);
		lineWeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showLineWeight ^= true;
				showTotalWeight ^= true;
				panel.repaint();
			}
		});

		panel.add(prims);
		panel.add(kruskal);
		panel.add(new JLabel(new String(new char[10]).replace("\0", " ")));
		panel.add(reset);
		panel.add(lineWeight);
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	class GraphPanel extends JPanel {

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			for (int i = 0; i < graph.getNumberOfVertices(); i++) {
				Graph.Position vertexPosition = graph.getPosition(i);
				g.setColor(Color.BLACK);
				g.fillOval(vertexPosition.getX() - POINT_SIZE / 2, vertexPosition.getY() - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
				g.setColor(Color.BLUE);
				g.drawString(Integer.toString(i), vertexPosition.getX(), vertexPosition.getY() - POINT_SIZE / 2);
			}
			g.setColor(Color.red);
			List<Graph.Edge> edges = graph.getEdges();
			for (Graph.Edge edge : edges) {
				Graph.Position p1 = graph.getPosition(edge.getFromVertex());
				Graph.Position p2 = graph.getPosition(edge.getToVertex());
				int x1 = p1.getX();
				int y1 = p1.getY();
				int x2 = p2.getX();
				int y2 = p2.getY();
				g.drawLine(x1, y1, x2, y2);

				if (showLineWeight) {
					String weight = Double.toString(edge.getWeight());
					int centreX;
					int centreY;
					if (x1 > x2) centreX = x2 + ((x1 - x2) / 2);
					else centreX = x1 + ((x2 - x1) / 2);
					if (y1 > y2) centreY = y2 + ((y1 - y2) / 2);
					else centreY = y1 + ((y2 - y1) / 2);
					g.drawChars(weight.toCharArray(), 0, weight.indexOf("."), centreX, centreY);
				}
			}

			if (showTotalWeight) g.drawString("Total weight: " + graph.getTotalWeight(), MyGraphDisplay.WIDTH / 2, MyGraphDisplay.HEIGHT - 8);
		}

	}

	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		final MyGraphDisplay gd = new MyGraphDisplay();
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				gd.display();
			}
		});
	}

}
