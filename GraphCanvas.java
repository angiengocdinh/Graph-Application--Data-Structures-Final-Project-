import java.util.*;
import java.awt.*;
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
 *  Implements a graphical canvas that displays a list of nodes in the graph.
 *
 *  @author  Angie Dinh
 *  @version Dec 10th, 2016
 */

public class GraphCanvas extends JComponent {
    /** The points */
    private Graph<String, Double> graph;

    /** Hashtable of nodes and correspoding coordinates (point)*/
    private Hashtable<Graph.Node, Point> node_table;

    /** Hashtable of nodes and corresponding color */
    private Hashtable<Graph.Node, Color> node_color;

    /** Hashtable of edges and corresponding color */
    private Hashtable<Graph.Edge, Color> edge_color;

    /** height and width for longitude/latitude conversion*/
    private int height = 250;
    private int width = 300;

    /** Size of the circle of the nodes*/
    private int nodeSize = 12;

    /** Default color of nodes and edges */
    private Color default_color = Color.RED;

    /** Constructor 
    @param the graph, boolean: are the coordinates x,y or longitude/latitude? */
    public GraphCanvas(Graph<String, Double> graph, boolean coord_type_xy) {
        this.graph=graph;
        this.node_table=new Hashtable<Graph.Node, Point>();
        this.setPreferredSize(new Dimension(1000,800));
        this.node_color=new Hashtable<Graph.Node, Color>();
        for (Graph.Node node: graph.getNodeList()){
            node_color.put(node, default_color);
        }
        this.edge_color=new Hashtable<Graph.Edge, Color>();
        for (Graph.Edge edge: graph.getEdgeList()){
            edge_color.put(edge, default_color);
        }
    }

    /** set color of a specific node 
    @param node, color*/
    public void setNodeColor(Graph.Node node, Color color){
        node_color.remove(node);
        node_color.put(node, color);
    }

    /** change color of a group of edges
    @param group of edges and new color */
    public void changeEdgeGroupColor(LinkedList<Graph<String,Double>.Edge> edge_group, Color new_color){
        for (Graph.Edge edge: edge_group){
            edge_color.remove(edge);
            edge_color.put(edge, new_color);
            node_color.remove(edge.getHead());
            node_color.remove(edge.getTail());
            node_color.put(edge.getHead(), new_color);
            node_color.put(edge.getTail(), new_color);
            node_color.put(edge.getHead(), new_color);
        } 
    }

    /** reset color of all nodes and edges into the base color
    @param base color*/
    public void resetColor(Color base_color){
        for (Graph.Node node: graph.getNodeList()){
            node_color.remove(node);
            node_color.put(node, base_color);
        }
        for (Graph.Edge edge: graph.getEdgeList()){
            edge_color.remove(edge);
            edge_color.put(edge, base_color);
        }
        repaint();
    }

    /** change color of a group of nodes 
    @param group of nodes, new color */
    public void changeNodeGroupColor(LinkedList<Graph<String,Double>.Node> node_group, Color new_color){
        for (Graph.Node node: node_group){
            if (!node_color.get(node).equals(new_color) && node!=null){
                node_color.remove(node);
                node_color.put(node, new_color);
            }
        } 
        for (Graph.Edge edge: graph.getEdgeList()){
            if (node_color.get(edge.getHead()).equals(new_color) && node_color.get(edge.getTail()).equals(new_color)){
                edge_color.remove(edge);
                edge_color.put(edge, new_color);
            }
        }
    }

    /** accessor for graph
    @return the graph*/
    public Graph getGraph(){
        return this.graph;
    }

    /** add a node to the graph and the table knowing its point coordinates
    @param point with coordinates of the node */
    public void addToTable(Point point){
        Graph.Node node =graph.addNode(" ");
        System.out.println(node);
        node_table.put(node,point);
        node_color.remove(node);
        node_color.put(node, default_color);
    }

    /** change the coordinates(points) of the node in the graph
    @param the node, the new coordinates */
    public void updateLocation(Graph.Node node, Point point){
        node_table.remove(node);
        node_table.put(node,point);
    }

    /** remove a node from the graph and the table 
    @param the node */
    public void removeFromTable(Graph.Node node){
        System.out.println(node);
        graph.removeNode(node);
        node_table.remove(node);
    }

    /** find the nearby node of a point given its coordinates
    @param x and y coordinates
    @return the node*/
    public Graph.Node findNearByNode(int x, int y) {
        Graph.Node node2 = null;
        for (Graph.Node node: graph.getNodeList()){
            //System.out.println(node.getData());
            Point point = node_table.get(node);
            System.out.println(point);
            if (point.distance(x,y)<nodeSize+1){
                node2=node;
                System.out.println(node2.getData());
            }
        }
        return node2;
    }

    /** change coordinates from longitude, latitude to x,y 
    @param longitude, latitude
    @return point of coordinate x,y*/
    public Point longLatConversion(Double longi, Double lat){
        Double x_y_ratio=((lat * height/180.0) + (height/2))/((longi * width/360.0) + (width/2));
        int y = -15*(int)Math.round((lat * height/180.0) + (height/2))+2950;
        int x = 15*(int)Math.round((longi * width/360.0) + (width/2))-2000*(1/(int)Math.round(x_y_ratio))-600;
        Point coord = new Point(x,y);
        return coord;
    }

    /** create a table of nodes and coordinates 
    @param x-y or longitude-latitude?, x coordinates, y coordinates, and a given node
    @return the table */
    public void setNodeTable(boolean long_lat, Double x_coord, Double y_coord, Graph.Node node){
        Point coord;
        if (!long_lat){ 
            coord = new Point(x_coord.intValue(), y_coord.intValue());
        } else {
            coord = longLatConversion(x_coord, y_coord);
        } 
        node_table.put(node, coord);
    }

    /** painComponent: to paint the graph
    @param graphics g*/
    public void paintComponent(Graphics g) {
	   for (Graph.Node node : node_table.keySet()){
            Point graph_coord = node_table.get(node);
            g.setColor(node_color.get(node));
            g.fillOval(graph_coord.x, graph_coord.y,nodeSize,nodeSize);
            g.drawString((String)node.getData(), graph_coord.x, graph_coord.y);
	    }

        for (Graph.Edge edge: graph.getEdgeList()){
            g.setColor(edge_color.get(edge));
            g.drawLine(node_table.get(edge.getHead()).x+nodeSize/2, node_table.get(edge.getHead()).y+nodeSize/2, node_table.get(edge.getTail()).x+nodeSize/2, node_table.get(edge.getTail()).y+nodeSize/2);
            Double pointEdgeLabelX = (node_table.get(edge.getHead()).x+node_table.get(edge.getTail()).x)/2.0;
            Double pointEdgeLabelY = (node_table.get(edge.getHead()).y+node_table.get(edge.getTail()).y)/2.0;
            DecimalFormat df = new DecimalFormat("#.##");
            g.drawString(df.format(edge.getData()), pointEdgeLabelX.intValue(), pointEdgeLabelY.intValue());
            g.setColor(default_color);
        }
    }

    /**accessor for the table
    @return the table*/
    public Hashtable<Graph.Node, Point> getNodeTable(){
        return node_table;
    }

    /** accessor for default color */

    public Color getDefaultColor(){
        return default_color;
    }


    /**
     *  The component will look bad if it is sized smaller than this
     *
     *  @returns The minimum dimension
     */
    public Dimension getMinimumSize() {
        return new Dimension(500,300);
    }

    /**
     *  The component will look best at this size
     *
     *  @returns The preferred dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(1000,800);
    }
    
    /**print for debugging*/
    public void printTable(){
        for (Graph.Node key: node_table.keySet()){
            System.out.println("node: "+key.getData());
            System.out.println("point: "+node_table.get(key).x);
            System.out.println("point: "+node_table.get(key).y);
        }
        System.out.println("end");
    }
}
