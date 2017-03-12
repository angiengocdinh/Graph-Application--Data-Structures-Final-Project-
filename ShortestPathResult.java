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

/** class to perform Dijkstra's algorithm
@author Angie Dinh
@version Dec 2016*/

public class ShortestPathResult {
	/** ArrayList of distances*/
	private ArrayList<Double> distances;

	/** list of visited nodes*/
    private LinkedList<Graph<String,Double>.Node> visited_nodes;

	/** hash table of homesign table*/
	private Hashtable<Graph.Node, Graph.Node> homesign_table;

	/** Constructor*/
	public ShortestPathResult(Graph<String, Double> graph, Graph.Node start, Hashtable<Graph.Edge, Double> distance_table){
		distances = new ArrayList<Double>();
		visited_nodes = new LinkedList<Graph<String,Double>.Node>();
		updateDistancePath (graph, start, distance_table);
	}

	/** perform the algorithm to update the path 
	@param the graph, start node, table of distances */
	public void updateDistancePath (Graph<String, Double> graph, Graph.Node start, Hashtable<Graph.Edge, Double> distance_table){
		homesign_table = new Hashtable<Graph.Node, Graph.Node>();
        Hashtable<Graph.Node, Double> cost_table = new Hashtable<Graph.Node, Double>();
        LinkedList<Graph.Node> unvisited_nodes = new LinkedList<Graph.Node>();
        cost_table.put(start,0.0);
        Double cost_prev=0.0;
        for (Graph.Node node: graph.getNodeList()){
            unvisited_nodes.add(node);
            if (node!=start){
                cost_table.put(node,Double.POSITIVE_INFINITY);
            }
        }
        while (unvisited_nodes.size()>0){
        	Graph.Node lowest_cost_node = unvisited_nodes.get(0);
            for (Graph.Node node: unvisited_nodes){
                if (cost_table.get(node) < cost_table.get(lowest_cost_node)){
                    lowest_cost_node = node;
                    System.out.println(lowest_cost_node.getData());
                    cost_prev = cost_table.get(node);
                    System.out.println("previous_cost");
                    System.out.println(cost_prev);
                }
            }
            if(cost_table.get(lowest_cost_node)<Double.POSITIVE_INFINITY && (lowest_cost_node.getData() !=null)){
            	visited_nodes.addLast(lowest_cost_node);
            }
            System.out.println("test visited nodes");
        	for (Graph.Node node: visited_nodes){
        		System.out.println(node);
            	System.out.println(node.getData());
        	}
            unvisited_nodes.remove(lowest_cost_node);
            if (lowest_cost_node.getNeighbors()!=null){
            ArrayList<Graph.Node> lowest_cost_node_neighbor = lowest_cost_node.getNeighbors();
            if (lowest_cost_node.getData() !=null){
            	for (Graph.Node neighbor: lowest_cost_node_neighbor){
            		if (!(visited_nodes.contains(neighbor))){
            			System.out.println(neighbor);
                		System.out.println(lowest_cost_node);
                		System.out.println(lowest_cost_node.edgeTo(neighbor));
                		System.out.println(distance_table.get(lowest_cost_node.edgeTo(neighbor)));
                		System.out.println("neighbor");
               			System.out.println(neighbor.getData());
                		Double cost_next = cost_prev + distance_table.get(lowest_cost_node.edgeTo(neighbor));
                		System.out.println(cost_next);
                		if (cost_next < cost_table.get(neighbor)){
                			if (cost_table.keySet().contains(neighbor)){
                				cost_table.remove(neighbor);
                			} if (homesign_table.keySet().contains(neighbor)){
                				 homesign_table.remove(neighbor);
                			}
                    		cost_table.put(neighbor, cost_next);
                    		homesign_table.put(neighbor, lowest_cost_node);
                		}
            		}
            	}
        	}
        	System.out.println("end loop");
        }
    }
        
    for (int i=0; i<visited_nodes.size(); i++){
        	System.out.println(visited_nodes.size());
            Double added_cost_data=cost_table.get(visited_nodes.get(i));
            System.out.println(added_cost_data);
            visited_nodes.get(i).setData(visited_nodes.get(i).getData());
            distances.add(added_cost_data);
        }
        System.out.println("end dist");
        printable();
    }

    /** Accessor for distances 
    @return list of distances */
    public ArrayList<Double> getDistance(){
    	return distances;
    }

    /** get the path of visisted nodes
    @return the list of visited nodes*/
    public LinkedList<Graph<String,Double>.Node> getPath(){
    	System.out.print("check nodes");
    	System.out.println(visited_nodes);
    	return visited_nodes;
    }

    /** print for debugging*/
    public void printDistance(){
    	System.out.println("distance");
    	for (int i=0; i<distances.size(); i++){
    		System.out.println(distances.get(i));
    	}
    	System.out.println("end distance");
    }

    /** get specific path between two nodes
    @param graph, start node, end node
    @return a list of nodes that form the path*/
    public LinkedList<Graph<String,Double>.Node> getPathTwoNodes (Graph<String,Double> graph, Graph.Node start, Graph.Node end){
    	LinkedList<Graph<String,Double>.Node> specific_path = new LinkedList<Graph<String,Double>.Node>();
    	Graph.Node mark = end;
    	while (mark!=start){
    		specific_path.addLast(mark);
    		System.out.println("Specific_path");
    		System.out.println(mark.getData());
    		mark=homesign_table.get(mark);
    	}
    	specific_path.addLast(start);
    	return specific_path;
    }
    
    /**print for debugging**/
    public void printable(){
    	for (Graph.Node node: homesign_table.keySet()){
    		System.out.println("key");
    		System.out.println(node.getData());
    		System.out.println("value");
    		System.out.println(homesign_table.get(node).getData());
    	}
    }
}