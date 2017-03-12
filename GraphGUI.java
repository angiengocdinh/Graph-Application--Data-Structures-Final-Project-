import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;   
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.util.Arrays;  
import java.text.DecimalFormat;       

/**
 *  Implements a GUI for inputting points.
 *
 *  @author  Angie Dinh
 *  @version Dec 15th, 2016
 */
public class GraphGUI {
    /** The canvas to be displayed */
    private static GraphCanvas canvas;

    /**Graph to be displayed*/
    private static Graph<String, Double> graph;

    /**text field to store users text input*/
    private JTextField textfield;

    /** Label for the input mode instructions */
    private JLabel instr;

    /** The input mode */
    private InputMode mode = InputMode.ADD_POINTS;

    /** Remembers point where last mousedown event occurred */
    private Point pointUnderMouse;

    /** node to add the edge*/
    private Graph.Node nodeToAddEdge;

    /** another node to add the edge*/
    private Graph.Node nodeToAddEdge2;

    /** node under mouse*/
    private Graph.Node nodeUnderMouse;

    /** another node under mouse*/
    private Graph.Node secondnodeUnderMouse;

    /** node to be removed*/
    private Graph.Node nodeToRemove;

    /** node being dragged*/
    private Graph.Node nodeDragged;

    /** node being added (used for setting data)*/
    private Graph.Node node_added;

    /** default for coordinate type (x,y)*/
    private static boolean long_lat = false;

    /** default that edge has no data*/
    private static boolean edge_has_data=false;

    /** default graph file*/
    private static String graph_file="";

    /** table of edge costs*/
    private static Hashtable<Graph.Edge, Double> cost_table;

    /**seperator of the file*/
    private static String seperator;

    /**
     *  Schedules a job for the event-dispatching thread
     *  creating and showing this application's GUI.
     */

    /** find EuclideanDistance of two nodes
    @param node 1, node 2, table of coordinates 
    @return the distance*/
    public static Double findEuclideanDistance(Graph.Node node1, Graph.Node node2, Hashtable<Graph.Node, Point> node_table){
        int x1 = node_table.get(node1).x;
        int y1 = node_table.get(node1).y;
        int x2 = node_table.get(node2).x;
        int y2 = node_table.get(node2).y;
        Double eucl_distance = Math.sqrt((x1 - x2)*(x1 - x2) + (y1-y2)*(y1-y2));
        return eucl_distance;
    }

    /** get the cost table
    @param the graph, does the edge has data?
    @return the cost table*/
    public static Hashtable<Graph.Edge, Double> getEdgeCost(Graph<String, Double> graph, boolean edge_has_data){
        Hashtable<Graph.Edge, Double> cost_table = new Hashtable<Graph.Edge, Double>();
        if (edge_has_data){
            for (Graph.Edge edge: graph.getEdgeList()){
                cost_table.put(edge, (Double)edge.getData());
            }
        } else {
            for (Graph.Edge edge: graph.getEdgeList()){
                cost_table.put(edge, findEuclideanDistance(edge.getHead(), edge.getTail(), canvas.getNodeTable()));
            }
        } 
        return cost_table;
    }
    
    /** read in graph file 
    @param the file, coordinate type, file seperator*/

    public static void readGraphFile (String graph_file, boolean long_lat, String seperator){
        Hashtable<Integer, Graph.Node> node_ID = new Hashtable<Integer, Graph.Node>();
        canvas = new GraphCanvas(graph, long_lat);
        try {
            File file = new File(graph_file);
            Scanner input = new Scanner (file);
            while (input.hasNextLine()) {
                String[] input_data=input.nextLine().split(seperator);
                System.out.println(input_data);
                System.out.println(input_data[0]);

                /** get the node*/
                if (input_data[0].trim().equals("n")){
                    Graph.Node new_node = graph.addNode(input_data[2].trim());
                    node_ID.put(Integer.parseInt(input_data[1].trim()),new_node);
                    System.out.println(node_ID.keySet());
                    if (input_data.length==5){
                        canvas.setNodeTable(long_lat, Double.parseDouble(input_data[3].trim()), Double.parseDouble(input_data[4].trim()), new_node);
                    } else if (input_data.length==3){
                        Double x_coord = 500*Math.random();
                        Double y_coord = 300*Math.random();
                        canvas.setNodeTable(long_lat, x_coord, y_coord, new_node);
                        canvas.resetColor(Color.RED);
                        canvas.repaint();
                    } else {
                        System.out.println("Invalid file format");
                    }

                /** get the edges*/    
                } else if (input_data[0].trim().equals("e")){
                    if (input_data.length==4){
                        edge_has_data=true;
                        graph.addEdge(Double.parseDouble(input_data[3].trim()), node_ID.get(Integer.parseInt(input_data[1].trim())), node_ID.get(Integer.parseInt(input_data[2].trim())));
                    } else if (input_data.length==3){
                        graph.addEdge(0.0,node_ID.get(Integer.parseInt(input_data[1].trim())), node_ID.get(Integer.parseInt(input_data[2].trim())));
                    } else {
                        System.out.println("Invalid file format");
                    }
                }
            }
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        }
    }

    /** write the file to the graph file 
    @param the graph, seperator, file name */
    public static void writeGraphFile (Graph<String, Double> graph, String seperator, String file_name){
        Hashtable<Graph.Node, Integer> node_ID = new Hashtable<Graph.Node, Integer>();
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file_name));
            for (int i = 1; i< graph.getNodeList().size(); i++){
                Graph<String, Double>.Node node = graph.getNodeList().get(i-1);
                node_ID.put(node, i);
                int coord_x = canvas.getNodeTable().get(node).x;
                int coord_y = canvas.getNodeTable().get(node).y;
                out.println("n"+seperator+i+seperator+node.getData()+seperator+coord_x+seperator+coord_y);
            }
            for (Graph.Edge edge: graph.getEdgeList()){
                out.println("e"+seperator+node_ID.get(edge.getHead())+seperator+node_ID.get(edge.getTail())+seperator+edge.getData());
            }
            out.close();
        } catch (IOException e){
            System.out.println("Input/Output error");
        }
    }

    /** main: to run the program*/
    public static void main(String[] args) {
        graph = new Graph<String, Double>();
        seperator=args[0];
        System.out.println(graph_file);
        readGraphFile(graph_file, long_lat, seperator);
        final GraphGUI GUI = new GraphGUI();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI.createAndShowGUI();
            }
        });
    }

    /** Sets up the GUI window */
    public void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("Graph GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add components
        createComponents(frame);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /** Puts content in the GUI window */
    public void createComponents(JFrame frame) {
        // graph display
        Container pane = frame.getContentPane();
        pane.setLayout(new FlowLayout());
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        //canvas = new GraphCanvas();
        PointMouseListener pml = new PointMouseListener();
        canvas.addMouseListener(pml);
        canvas.addMouseMotionListener(pml);
        panel1.add(canvas);

        instr = new JLabel("Click to add new points; drag to move.");        
        panel1.add(instr,BorderLayout.NORTH);
        pane.add(panel1);

        // controls
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(25,1));
        pane.add(panel2, BorderLayout.EAST);

        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(1,1));
        JLabel loadSaveLabel = new JLabel("Load and Save File");
        loadSaveLabel.setForeground(Color.BLUE);
        panel3.add(loadSaveLabel);

        JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayout(1,1));
        JLabel modifyGraphLabel = new JLabel("Modify Graph");
        modifyGraphLabel.setForeground(Color.BLUE);
        panel4.add(modifyGraphLabel);

        JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayout(1,1));
        JLabel traversalLabel = new JLabel("Traversal");
        traversalLabel.setForeground(Color.BLUE);
        panel5.add(traversalLabel);

   
        textfield=new JTextField();
        panel2.add(textfield);
        panel2.add(panel3);

        JButton importFileButton = new JButton("Import File");
        panel2.add(importFileButton);
        importFileButton.addActionListener(new importFileListener());

        JButton saveFileButton = new JButton("Save File");
        panel2.add(saveFileButton);
        saveFileButton.addActionListener(new saveFileListener());

        panel2.add(panel4);

        JButton addPointButton = new JButton("Add/Move Points");
        panel2.add(addPointButton);
        addPointButton.addActionListener(new AddPointListener());

        JButton rmvPointButton = new JButton("Remove Points");
        panel2.add(rmvPointButton);
        rmvPointButton.addActionListener(new RmvPointListener());

        JButton addEdgeButton = new JButton("Add/Remove Edge");
        addEdgeButton.addActionListener(new AddEdgeListener());
        panel2.add(addEdgeButton);

        JButton setNodeDataButton = new JButton("Set Node Data");
        panel2.add(setNodeDataButton);
        setNodeDataButton.addActionListener (new setNodeDataListener());

        JButton setEdgeDataButton = new JButton("Set Edge Data");
        panel2.add(setEdgeDataButton);
        setEdgeDataButton.addActionListener(new setEdgeDataListener());

        JButton longLatIndicatorButton = new JButton("Change to Longitude/Latitude coordinates");
        panel2.add(longLatIndicatorButton);
        longLatIndicatorButton.addActionListener(new longLatIndicatorListener());

        panel2.add(panel5);

        JButton bftButton = new JButton("Perform breadth-first traversal");
        bftButton.addActionListener(new BFTListener());
        panel2.add(bftButton);

        JButton shortestPathButton = new JButton("Find Shortest Path");
        shortestPathButton.addActionListener(new ShortestPathListener());
        panel2.add(shortestPathButton);


        JButton dftButton = new JButton("Perform depth-first traversal");
        dftButton.addActionListener(new DFTListener());
        panel2.add(dftButton);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new resetListener());
        panel2.add(resetButton);

        pane.add(panel2);

    }
   

    /** Constants for recording the input mode */
    enum InputMode {
        ADD_POINTS, RMV_POINTS, ADD_EDGE, SHORTEST_PATH, BREADTH_FIRST_TRAVERSAL, DEPTH_FIRST_TRAVERSAL, SET_NODE_DATA, SET_EDGE_DATA
    }

    /** Listener for AddPoint button */
    private class AddPointListener implements ActionListener {
        /** Event handler for AddPoint button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.ADD_POINTS;
            instr.setText("Click to add new points or change their location.");
        } 
    }

    /** Listener for RmvPoint button */
    private class RmvPointListener implements ActionListener {
        /** Event handler for RmvPoint button */
        public void actionPerformed(ActionEvent e) {
	    mode = InputMode.RMV_POINTS;
	    instr.setText("Click to remove points");
        }
    }

    /**Listener for ShortestPath button*/
    private class ShortestPathListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            mode=InputMode.SHORTEST_PATH;
            instr.setText("Click on two points to find the shortest path between them for ");
        }
    }

    /** Listener for add edge button*/
    private class AddEdgeListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            mode=InputMode.ADD_EDGE;
            instr.setText("Drag from one node to another to add an edge between them. The default edge data is the distance between the points");
        }
    }

    /** listener for breadth-first traversal*/
    private class BFTListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            mode=InputMode.BREADTH_FIRST_TRAVERSAL;
            instr.setText("Click on one point to see where you can get to from that point (using breadth-first traversal)");
        }
    }

    /** listener for depth first traversal*/
    private class DFTListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            mode=InputMode.DEPTH_FIRST_TRAVERSAL;
            instr.setText("Click on one point to see where you can get to from that point (using depth-first traversal)");
        }
    }

    /** listener for import file*/
    private class importFileListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if (textfield.getText().length()>0){
                graph_file=textfield.getText();
                graph = new Graph<String, Double>();
                System.out.println(graph_file);
                readGraphFile(graph_file, long_lat, seperator);
                canvas.resetColor(Color.RED);
            } 
            final GraphGUI GUI = new GraphGUI();
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GUI.createAndShowGUI();
                }
            });
        }
    }

    /**listener for save file*/
    private class saveFileListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if (textfield.getText().length()>0){
                writeGraphFile(graph, seperator, textfield.getText());
            }
        } 
    }

    /** listener for setNode data button*/
    private class setNodeDataListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            mode=InputMode.SET_NODE_DATA;
            instr.setText("Write data in the box and click on a node to change its data");
        }
    }

    /** listener for set edge data*/
    private class setEdgeDataListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            mode=InputMode.SET_EDGE_DATA;
            instr.setText("Write data in the box and click on a node to change its data");
        }
    }


    /**Listener to set the canvas into longitude/latitude*/
    private class longLatIndicatorListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            long_lat=true;
            graph = new Graph<String, Double>();
            System.out.println(graph_file);
            readGraphFile(graph_file, long_lat, seperator);
            canvas.repaint();

            final GraphGUI GUI = new GraphGUI();
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GUI.createAndShowGUI();
            }
        });
    }
}

    /** listener for reset button*/
    private class resetListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            canvas.resetColor(canvas.getDefaultColor());
            nodeUnderMouse=null;
            secondnodeUnderMouse=null;
            nodeToAddEdge = null;
            nodeToAddEdge2 = null;
            nodeUnderMouse = null;
            secondnodeUnderMouse = null;
            nodeToRemove = null;
            nodeDragged = null;
            canvas.repaint();
        }
    }

    /** Mouse listener for GraphCanvas element */
    private class PointMouseListener extends MouseAdapter
        implements MouseMotionListener {
        boolean new_node_dragged=false;
        /** Responds to click event depending on mode */
        public void mouseClicked(MouseEvent e) {
            switch (mode) {
            case ADD_POINTS:
            //ADD NEW POINTS
		if (canvas.findNearByNode(e.getX(), e.getY())==null) {
		    pointUnderMouse = new Point(e.getX(), e.getY());
            canvas.addToTable(pointUnderMouse);
            canvas.repaint();         
            pointUnderMouse=null;
		} 

		Toolkit.getDefaultToolkit().beep();
            break;
            case RMV_POINTS:
            //REMOVE POINTS
		if (canvas.findNearByNode(e.getX(), e.getY())!=null){
		    nodeToRemove=canvas.findNearByNode(e.getX(),e.getY());
            System.out.println("node to remove");
            System.out.println(nodeToRemove.getData());
            canvas.removeFromTable(nodeToRemove);
            canvas.repaint();
		}
        graph.print();
        canvas.printTable();
            break;
            case SHORTEST_PATH:
            //FIND SHORTEST PATH
        System.out.println("shortest path testing");
        System.out.println(canvas.findNearByNode(e.getX(),e.getY()));
        ShortestPathResult shortest_result;
        LinkedList<Graph<String, Double>.Node> shortest_path = new LinkedList<Graph<String,Double>.Node>();

        if (canvas.findNearByNode(e.getX(), e.getY())!=null && nodeUnderMouse==null){
            nodeUnderMouse= canvas.findNearByNode(e.getX(), e.getY());
            shortest_result = new ShortestPathResult(graph, nodeUnderMouse, getEdgeCost(graph, edge_has_data));
            shortest_path = shortest_result.getPath();
            System.out.println("shortest path");
            System.out.println(shortest_path);
            canvas.changeNodeGroupColor(shortest_path, Color.BLUE);
            shortest_result.printDistance();
            ArrayList<Double> distances = shortest_result.getDistance();
            DecimalFormat df = new DecimalFormat("#.##");
            for (int i=0; i< shortest_path.size(); i++){
                shortest_path.get(i).setData((String)shortest_path.get(i).getData()+"   "+ df.format(distances.get(i)));
            }
            canvas.repaint();
        } else if (canvas.findNearByNode(e.getX(), e.getY())!=null && nodeUnderMouse!= null){
            secondnodeUnderMouse= canvas.findNearByNode(e.getX(), e.getY());
            shortest_result = new ShortestPathResult(graph, nodeUnderMouse, getEdgeCost(graph, edge_has_data));
            LinkedList<Graph<String,Double>.Node> specific_path = shortest_result.getPathTwoNodes(graph, nodeUnderMouse, secondnodeUnderMouse);

            for (Graph.Node node : shortest_path){
                String data= (String)node.getData();
                node.setData(data.substring(0, data.indexOf("  ")));
                canvas.repaint();
            }
            canvas.changeNodeGroupColor(specific_path, Color.BLACK);
            shortest_result.printDistance();
            canvas.repaint();
            graph.print();
            secondnodeUnderMouse=null;
        }


        Toolkit.getDefaultToolkit().beep();
                break;
                //ADD AN EDGE
                case ADD_EDGE:
        Toolkit.getDefaultToolkit().beep();
        canvas.repaint();
            break;
            // PERFORM BREADTH_FIRST_TRAVERSAL
            case BREADTH_FIRST_TRAVERSAL:
        System.out.println(canvas.findNearByNode(e.getX(),e.getY()));
        if (canvas.findNearByNode(e.getX(), e.getY())!=null){
            nodeUnderMouse= canvas.findNearByNode(e.getX(), e.getY());
            System.out.println(nodeUnderMouse.getData());
            LinkedList<Graph<String, Double>.Edge> bft_traversible_edge = graph.BFT(nodeUnderMouse);
            for (Graph.Edge edge: bft_traversible_edge){
                System.out.println(edge.getHead().getData());
                System.out.println(edge.getTail().getData());
            }
            canvas.changeEdgeGroupColor(bft_traversible_edge, Color.BLUE);
            canvas.repaint();
            nodeUnderMouse=null;
        }
        Toolkit.getDefaultToolkit().beep();
        canvas.repaint();
            break;
            //PERFORM DEPTH FIRST TRAVERSAL
            case DEPTH_FIRST_TRAVERSAL:
        System.out.println(canvas.findNearByNode(e.getX(),e.getY()));
        if (canvas.findNearByNode(e.getX(), e.getY())!=null){
            nodeUnderMouse= canvas.findNearByNode(e.getX(), e.getY());
            System.out.println(nodeUnderMouse.getData());
            LinkedList<Graph<String,Double>.Edge> dft_traversible_edge = graph.DFT(nodeUnderMouse);
            canvas.resetColor(Color.RED);
            canvas.changeEdgeGroupColor(dft_traversible_edge, Color.BLUE);
            nodeUnderMouse=null;
            canvas.repaint();
        }
        Toolkit.getDefaultToolkit().beep();
            break;
            //SET NODE DATA
            case SET_NODE_DATA:
        if (canvas.findNearByNode(e.getX(), e.getY())==null) {
            canvas.addToTable(pointUnderMouse);
            node_added=canvas.findNearByNode(e.getX(), e.getY());
            node_added.setData(textfield.getText());
            canvas.repaint();         
        } else {
            node_added=canvas.findNearByNode(e.getX(), e.getY());
            node_added.setData(textfield.getText());
            canvas.repaint(); 
        }
            break;
        }
    }

        /** Records point under mousedown event in anticipation of possible drag */
        public void mousePressed(MouseEvent e) {
            nodeToAddEdge=null;
            pointUnderMouse=new Point(e.getX(), e.getY());
            nodeDragged=canvas.findNearByNode(e.getX(),e.getY());
            if (nodeToAddEdge==null && canvas.findNearByNode(e.getX(), e.getY())!=null){
                nodeToAddEdge=canvas.findNearByNode(e.getX(),e.getY());
            }
            canvas.repaint();
        }


        /** Responds to released event */
        public void mouseReleased(MouseEvent e) {
            if (mode==InputMode.ADD_EDGE && canvas.findNearByNode(e.getX(),e.getY())!=null){
                System.out.println("node1");
                nodeToAddEdge2= canvas.findNearByNode(e.getX(),e.getY());
                System.out.println(nodeToAddEdge.getData());
                System.out.println("node2");
                System.out.println(nodeToAddEdge2.getData());
                System.out.println("end node 1");
                //DecimalFormat df = new DecimalFormat("#.##");
                if (nodeToAddEdge2.isNeighbor(nodeToAddEdge) && nodeToAddEdge!=nodeToAddEdge2){
                    graph.removeEdge(nodeToAddEdge, nodeToAddEdge2);
                } else {
                    graph.addEdge(findEuclideanDistance(nodeToAddEdge, nodeToAddEdge2, canvas.getNodeTable()),nodeToAddEdge, nodeToAddEdge2);
                }
            } else if (mode==InputMode.ADD_POINTS && (nodeDragged!=null)) {
                System.out.println(nodeDragged);
                canvas.updateLocation(nodeDragged, pointUnderMouse);
            } else if (mode==InputMode.SET_EDGE_DATA && canvas.findNearByNode(e.getX(),e.getY())!=null){
                nodeToAddEdge2= canvas.findNearByNode(e.getX(),e.getY());
                if (nodeToAddEdge.isNeighbor(nodeToAddEdge2)){
                    nodeToAddEdge.edgeTo(nodeToAddEdge2).setData(Double.valueOf(textfield.getText()));
                }
                graph.print();
            }
            canvas.repaint();
            pointUnderMouse=null;
            new_node_dragged=false;
            nodeToAddEdge=null;
            nodeToAddEdge2=null;
        }

        /** Responds to mouse drag event */
        public void mouseDragged(MouseEvent e) {
            System.out.println(nodeDragged);
            if (mode==InputMode.ADD_POINTS && nodeDragged != null) {
                pointUnderMouse.setLocation(e.getX(), e.getY());
                //canvas.removeFromCanvas(nodeDragged);
                canvas.repaint();
            } else if (mode==InputMode.ADD_EDGE){
                pointUnderMouse.setLocation(e.getX(), e.getY());
            }
		    canvas.repaint();
            System.out.println(new_node_dragged);
	    }

	// Empty but necessary to comply with MouseMotionListener interface.
        public void mouseMoved(MouseEvent e) {}
    }
}

