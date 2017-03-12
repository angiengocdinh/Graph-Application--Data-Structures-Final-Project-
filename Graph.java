import java.util.ArrayList;
import java.lang.Object;
import java.util.HashSet;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 

/** Class Graph to implement a graph 
	@author: Angie Dinh
	@version: Dec 3rd, 2016 */

public class Graph<V,E>{
	/*field nodes: master list of nodes */
	private ArrayList<Node> nodes;
	/*field edges: master list of edges */
	private ArrayList<Edge> edges;

	/**Constructor for class Graph */
	public Graph(){
		this.nodes=new ArrayList<Node>();
		this.edges=new ArrayList<Edge>();
		this.check();
	}

	/**addEdge: adds an edge to the graph
		@param edge data, edge head, and edge tail
		@return the added edge */
	public Edge addEdge(E data, Node head, Node tail){
		Edge edge =null;
		if (head!=tail){
			edge = new Edge(data, head, tail);
			edges.add(edge);
			edge.head.addEdgeRef(edge);
			edge.tail.addEdgeRef(edge);
		}
		return edge;
	}

	/**addNode: adds a node to the graph
		@param data of the node
		@return the added node */
	public Node addNode(V data){
		Node node = new Node(data);
		nodes.add(node);
		return node;
	}

	public ArrayList<Node> getNodeList(){
		return this.nodes;
	}

	public ArrayList<Edge> getEdgeList(){
		return this.edges;
	}

	/**BFT: performs breadth-first traversal 
		@param starting node
		@return the edges that have been traversed*/
	public LinkedList<Edge> BFT(Node start){
		HashSet<Node> visited_node = new HashSet<Node>();
		Queue<Node> queue_node = new LinkedList<Node>();
		LinkedList<Edge> path = new LinkedList<Edge>();
		queue_node.add(start);
		System.out.println("Nodes: ");
		while (! queue_node.isEmpty()){
			Node out_node = queue_node.remove();
			System.out.println(out_node.getData());
			visited_node.add(out_node);
			for (Edge edge : out_node.getEdgeList()){
				if (! (visited_node.contains(edge.getHead())) && !(path.contains(edge))){
					visited_node.add(edge.getHead());
					queue_node.add(edge.getHead());
					path.add(edge);
				} else if (!(visited_node.contains(edge.getTail())) && !(path.contains(edge))){
					visited_node.add(edge.getTail());
					queue_node.add(edge.getTail());
					path.add(edge);
				}
			}
		}
		return path;
	}

	/**check: check for consistent data structures: 
		if nothing wrong, prints nothing
		if something is wrong, prints error message*/
	public void check(){

		/*heads and tails of edges are not null*/
		for (Edge edge: edges){
			if (edge.getTail()==null || edge.getHead()==null){
				System.out.println("Edge head or tail is null");
				System.out.println("Edge: "+edge.getData());
				System.out.println("Head of Edge: "+edge.getHead());
				System.out.println("Tail of Edge: "+edge.getTail());
			}
		}

		/*edges and links are not null */
		for(Node node: nodes){
			for (Edge edge: node.getEdgeList()){
				if (edge==null){
					System.out.println("Edge of node is null");
					System.out.println("Node: "+node.getData());
				}
			}
		}

		for (Edge edge: edges){
			/*Head of an edge links back to that edge*/

			if (!(edge.getHead().getEdgeList().contains(edge))){
				System.out.println("Edge head not pointing to the correct node:");
				System.out.println("Edge: "+ edge.getData());
				System.out.println("Head: "+ edge.getHead());
				System.out.println("List of edges in head:");
				for (Edge edge_in_head : edge.getHead().getEdgeList()){
					System.out.println(edge_in_head.getData());
				}
			}

			/*Tail of an edge links back to that edge*/

			if (!(edge.getTail().getEdgeList().contains(edge))){
				System.out.println("Edge tail not pointing to the correct node:");
				System.out.println("Edge: "+ edge.getData());
				System.out.println("Tail: "+ edge.getTail());
				System.out.println("List of edges in tail:");
				for (Edge edge_in_tail : edge.getHead().getEdgeList()){
					System.out.println(edge_in_tail.getData());
				}			
			}

			/*Every head/tail of an edge is in the central list*/
			if (!(nodes.contains(edge.getHead()))){
				System.out.println("Head of an edge is not in the central list");
				System.out.println("Edge: "+edge.getData());
				System.out.println("Head: "+edge.getHead().getData());
			}

			if (!(nodes.contains(edge.getTail()))){
				System.out.println("Tail of an edge is not in the central list");
				System.out.println("Edge: "+edge.getData());
				System.out.println("Tail: "+edge.getTail().getData());
			}
		}

		for (Node node: nodes){
			/*Every edge referenced by node is in control list */
			for ( Edge edge: node.getEdgeList()){
				if (!(edges.contains(edge))){
					System.out.println("Edge referenced by node is not in the control list");
					System.out.println("Node: "+node.getData());
					System.out.println("Edge: "+edge.getData());
				}

				if (!(edge.getTail()==node||edge.getHead()==node)){
					System.out.println("Edge listed for a node does not link to node");
					System.out.println("Node: "+node.getData());
					System.out.println("Edge: "+edge.getData());
					System.out.println("Head of Edge: "+edge.getHead());
					System.out.println("Tail of Edge: "+edge.getTail());
				}
			}
		}
	}

	private LinkedList<Edge> DFT(Node start, HashSet<Node> visited, LinkedList<Edge> path){
		LinkedList<Edge> next_step= new LinkedList<Edge>();
		System.out.println(visited.contains(start));
		if (visited.contains(start)){
			System.out.println(start.getData());
			return path;
		} else {
			visited.add(start);
			for (Node node: visited){
				System.out.println("vt"+node.getData());
			}
			System.out.println("nb "+start.getNeighbors().get(0).getData());
			for (Graph.Node neighbor: start.getNeighbors()){
				path.add(start.edgeTo(neighbor));
				next_step = this.DFT(neighbor, visited, path);
			}
			return next_step;
		}
	}


	public LinkedList<Edge> DFT(Node start){
		HashSet<Node> visited = new HashSet<Node>();
		LinkedList<Edge> path = new LinkedList<Edge>();
		return this.DFT(start, visited, path);
	}

	/** endpoints: return endpoints of edges 
		@param list of edges
		@return nodes that are endpoints of edges in the list */
	public HashSet<Node> endpoints(HashSet<Edge> edges){ 
		HashSet<Node> end_point = new HashSet<Node>();
		for (Edge edge: edges){
			if (this.edges.contains(edge)){
				end_point.add(edge.getTail());
				if(!(end_point.contains(edge.getHead()))){
					end_point.add(edge.getHead());
				}
			}
		}
		return end_point;
	}

	/** getEdge: get an edge by its index
		@param index
		@return the edge of that index */
	public Edge getEdge(int i){
		return edges.get(i);
	}

	/**getEdgeRef: get an edge by its head and tail 
		@param head and tail node of the edge
		@return the referenced edge */
	public Edge getEdgeRef(Node head, Node tail){
		Edge return_value = null;
		if (this.edges.size()!=0){
			Edge compared_edge = new Edge(null, head, tail);
			int k=0;
			for (int i=0; i<edges.size(); i++){
				if (edges.get(i).equals(compared_edge)){
					k=i;
				}
			}
			return_value= edges.get(k);
		}
		return return_value;
	}

	/**getNode: get a node by its index 
		@param index
		@return the node of that index */
	public Node getNode(int i){
		return nodes.get(i);
	}

	/**numEdges: get the number of edges 
		@return number of edges */
	public int numEdges(){
		return edges.size();
	}


	/**numEdges: get the number of nodes
		@return number of nodes */
	public int numNodes(){
		return nodes.size();
	}

	/**otherNodes: get nodes that are not in a given node list 
		@param a HashSet of nodes
		@return a HashSet of nodes not in the group of the parameter*/
	public HashSet<Node> otherNodes(HashSet<Node> group){
		HashSet<Node> other_nodes = new HashSet<Node>();
		for (Node node: nodes){
			if (!(group.contains(node))){
				other_nodes.add(node);
			}
		}
		return other_nodes;
	}

	/**print: print the graph*/
	public void print(){
		System.out.println("_____________");
		System.out.println("START GRAPH");
		for (Node node: nodes){
			System.out.println("Node: "+node.data);
			System.out.println("   Edge list:");
			for (Edge edge_in_node : node.node_edges){
				System.out.println("       "+edge_in_node.head.getData()+ ", "+ edge_in_node.data + ", "+ edge_in_node.tail.getData());
			}
		}
		System.out.println("________________");
		System.out.println("LIST OF GRAPH EDGES");
		for (Edge edge: edges){
			System.out.println("       "+edge.head.getData() + ", " + edge.data + ", " + edge.tail.getData());
		}
		System.out.println("______________");
		System.out.println("END GRAPH");
		System.out.println(" ");
	}

	/** removeEdge: removes an edge
		@param the edge we want to remove*/
	public void removeEdge(Edge edge){
		edge.head.removeEdgeRef(edge);
		edge.tail.removeEdgeRef(edge);
		edges.remove(edge);
	}

	/**removeEdge: removes an edge knowing its head and tail
		@param the head and tail of the edge we want to remove*/
	public void removeEdge(Node head, Node tail){
		Edge found_edge=this.getEdgeRef(head, tail);
		this.removeEdge(found_edge);
	}

	/** removeNode: removes a node
		@param the node we want to remove*/
	public void removeNode (Node node){
		for (Node neighbor: node.getNeighbors()){
			System.out.println(neighbor);
			if (neighbor.edgeTo(node)!=null){
				System.out.println("nb");
				System.out.println(neighbor.edgeTo(node));
				this.removeEdge(node,neighbor);
			}
		}
		for (Edge edge: node.node_edges){
			System.out.println(edge);
			System.out.println(edge);
			edges.remove(edge);
		}
		nodes.remove(node);		
	}

	/** Nested class Node to implement a node in a graph */
	public class Node {
		/** field data to store the data of the node */
		private V data;
		
		/** field node_edges to store the edge list of the node */
		private ArrayList<Edge> node_edges;

		/** Constructor for nested class Node */
		public Node(V data){
			this.data = data;
			this.node_edges= new ArrayList<Edge>();
		}

		/** Accessor for data */
		public V getData(){
			return this.data;
		}

		/** Manipulator for data */
		public void setData (V data){
			this.data = data;
		}

		/** addEdgeRef: adds an edge to the edge list 
			@param an edge */
		protected void addEdgeRef(Edge edge){
			this.node_edges.add(edge);
		}

		/** Method removeEdgeRef: removes an edge from the edge list
			@param an edge */
		protected void removeEdgeRef(Edge edge){
			this.node_edges.remove(edge);
		}

		/** edgeTo: Returns the edge to a specified node, or null if there is none
			@param Node a neighbor node 
			@return the edge to that node */
		public Edge edgeTo (Node neighbor){
			Edge edge_neighbor = null;
			for (Edge edge : this.node_edges){
				if (edge.getHead() == neighbor || edge.getTail() == neighbor){
					edge_neighbor=edge;
				}
			}
			return edge_neighbor;
		}

		/** Method getNeighbors: returns a list of neighbors
			@return a list of neighbors */
		public ArrayList<Node> getNeighbors(){ 
			ArrayList<Node> neighbors = new ArrayList<Node>();
			for (Edge edge: this.node_edges){
				if(!(edge.getHead()==this)){
					neighbors.add(edge.getHead());
				}
				if ((!(edge.getTail()==this)) && (!(neighbors.contains(edge.getTail())))){
					neighbors.add(edge.getTail());
				}
			}
			return neighbors;
		}

		/**isNeighbor: check if one node is the neighbor of another (one node
		can go to another node with just one edge)
			@param a node
			@return true if the node in the parameter is a neighbor of the referenced node
					false otherwise */
		public boolean isNeighbor (Node node){
			return this.getNeighbors().contains(node);
		}

		/**getEdgeList: get the list of edges for a given node
			@return the list of edges*/
		protected ArrayList<Edge> getEdgeList(){
			return this.node_edges;
		}
	}

	/** Nested class edge to implement an edge in the graph*/
	public class Edge {
		/**field data to store the data of the edge*/
		private E data;

		/**field head to store the head of the edge*/
		private Node head;

		/**field tail to store the tail of the edge*/
		private Node tail;
		public Edge(E data, Node head, Node tail){
			this.data=data;
			this.head=head;
			this.tail=tail;
		}

		/** equals: check if two objects are equal. For edges, two edges are equal if they
			have the same endpoints, regardless of which endpoint is a head and which is a tail
			@return true if equal*/
		public boolean equals (Object o){
			boolean result = false;
	    	if (this.getClass() == o.getClass()) {
                @SuppressWarnings("unchecked")
                Edge edge = (Edge)o;
				if ((edge.head==this.head && edge.tail==this.tail)||(edge.head==this.tail && edge.tail==this.head)){
					result = true;
				}
            }
            return result;
		}
		/** getData: get data of the edge
			@return the data*/
		public E getData(){
			return this.data;
		}

		/**getHead: get head of an edge
			@return the head node*/
		public Node getHead(){
			return this.head;
		}

		/**getTail: get tail of an edge
			@return the tail node*/
		public Node getTail(){
			return this.tail;
		}

		/**oppositeTo: get the opposite node given one node of the edge
			@param a node
			@return the opposite node from the referenced edge*/
		public Node oppositeTo(Node node){
			Node opposite_node = null;
			if (this.head==node){
				opposite_node=this.tail;
			} else if (this.tail==node){
				opposite_node=this.head;
			}
			return opposite_node;
		}

		/**setData: set data for the edge
			@param the data */
		public void setData(E data){
			this.data=data;
		}

		/**hashCode: redefine hashCode
			@return the redefined hashCode, which is the multiplication of the hashCode of head & tail*/
		public int hashCode(){
			return this.head.hashCode()*this.tail.hashCode();
		}
	}
}