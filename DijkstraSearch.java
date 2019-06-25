import java.io.*;
import java.awt.*;
import javax.swing.*;
import trubgp.*;  // TRU Board Games Playground package


public class DijkstraSearch
{
  static Board board;  // Game board
  static Graph graph;
  static int SIZE = 20;
 static PriorityQueue<VertexString> queue;
static NodePriorityQueueVertex nodes[];
 // static QueueNodeGraph queue;
  //static StackNodeGraph stack;
    
  
  public static void main(String[] args)
  {
    // Creat a game board
    create();
  }
  
  
  // Create a new board
  
  static void create()
  {
    // Construct a new board
    
    board = new Board(SIZE, SIZE, 40*SIZE, 40*SIZE, "Line", Color.WHITE);  // Line or NoLine
    board.setTitle("Graph Search");
    
    board.button1SetName("Read a graph data file");
    board.button1ClickEventListener(new BGPEventListener() { @Override public void clicked(int row, int col) {
      read();
    }});

    board.button2SetName("Search the graph - start|end term");
    board.button2ClickEventListener(new BGPEventListener() { @Override public void clicked(int row, int col) {
      search();
    }});
    
    board.setText("graph.txt");
  }
  
  
  static void read()
  {
    String fileName;
    String line;

    try {
      fileName = board.getText();
      
      // FileReader reads text files in the default encoding.
      FileReader fileReader = new FileReader(fileName);

      // Always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader = new BufferedReader(fileReader);
            
      int count = 0;
      String words[];
      
      // Read the number of nodes, and create an empty graph
      
      while((line = bufferedReader.readLine()) != null) {
        line = line.trim();
        if (line.length() == 0)
          continue;
        if (line.charAt(0) == '/' && line.charAt(1) == '/')
          continue;
        
        graph = new Graph(Integer.parseInt(line));
        
        break;
      }
      nodes = new NodePriorityQueueVertex[graph.size()];
      // Read node contents and keep them in the graph
      
      int id = 0;
      while(id < graph.size()) {
        line = bufferedReader.readLine();
        line = line.trim();
        if (line.length() == 0)
          continue;
        if (line.charAt(0) == '/' && line.charAt(1) == '/')
          continue;
        
        words = line.split("[ \t]+");  // Should be an integer and string
        System.out.println(words[0] + ":" + words[1]);
        int node_id = Integer.parseInt(words[0]);
        graph.keepVertex(node_id, new VertexString(node_id, words[1]));
        nodes[id] = new NodePriorityQueueVertex(new VertexString(words[1]));
        id++;
      }   

      // Read adjaceny information and keep it in the graph
      
      String costs[];
 
      id = 0;
      while(id < graph.size()) {
        line = bufferedReader.readLine();
        line = line.trim();
        if (line.length() == 0)
          continue;
        if (line.charAt(0) == '/' && line.charAt(1) == '/')
          continue;
        
        costs = line.split("[ \t]+");
        for ( int i=0; i<graph.size(); i++) {
        graph.setNeighbors(id, i, (Double.parseDouble(costs[i])));
        }
        

//graph.setNeighbors() ???
        id++;
      }   

      // Always close files.
      bufferedReader.close();  
      
      // Display the graph on the board
      displayGraph();

      JOptionPane.showMessageDialog(null, "Reading done");
      
      // Just for searching
      board.setText("bfs A F");
    }
    catch(FileNotFoundException ex) {
      JOptionPane.showMessageDialog(null, "File not found");
    }
    catch(IOException ex) {
      JOptionPane.showMessageDialog(null, "File i/o error");
    }
  }
  
  
  static void displayGraph()
  {
    for (int col = 1; col < graph.size() + 1; col++) {
      board.cellContent(0, col, "" + (col - 1));
      board.cellBackgroundColor(0, col, Color.YELLOW);
    }
      
    for (int row = 1; row < graph.size() + 1; row++) {
      board.cellContent(row, 0, "" + (row -1));
      board.cellBackgroundColor(row, 0, Color.YELLOW);
      for (int col = 1; col < graph.size()+1; col++) {
        board.cellContent(row, col, "" + graph.cost(row-1, col-1));
        board.cellBackgroundColor(row, col, Color.CYAN);
      }
    }
    
    for (int row = 1; row < graph.size() + 1; row++) {
      board.cellContent(row, graph.size()+2, graph.find(row-1).getContent());
      board.cellBackgroundColor(row, graph.size()+2, Color.CYAN);
    }

    for (int row = graph.size() + 1; row < SIZE; row++) {
      for (int col = 0; col < SIZE; col++) {
        board.cellContent(row, col, "");
        board.cellBackgroundColor(row, col, Color.WHITE);
      }
    }
  }
  
  static void search()
  {
    // read the text from the text field
    
    String line = board.getText().trim();
    System.out.println(line);
    if (line.length() == 0) return;
    String words[] = line.split("[ \t]+");
    if (words.length < 3) return;
    
    // search method and search term
    String search = words[0];
    String source = words[1];
    String endVertex = words[2];
    
    // bfs
    if (search.equals("bfs")) {
      bfs(source, endVertex, SIZE-1);  // Display ... at the bottom row; It uses a queue.
    }
    
    else
      System.out.println("Wrong search method!");
  }
  
  static void bfs(String source, String endVertex, int row)
  {
	  graph.reset();
	  displayGraph();
	  
	  
	  PriorityQueue<VertexString> queue= new PriorityQueue<VertexString>();
	  
	  VertexString startVertex= new VertexString(source);
	  for(int i = 0; i < graph.size(); i ++) {
		  System.out.print(graph.find(i).getContent() + " " + graph.find(i).getId());
		  if(graph.find(i).getContent().equals(source)) {
			  
			  startVertex = graph.find(i);
			  //break;
		  }
	  }
	  
	  NodePriorityQueue<VertexString> start_node = new NodePriorityQueue<VertexString>(startVertex);
	  start_node.setPriority(0);
	  queue.addElement(start_node);
	  
	  nodes[startVertex.getId()].setPriority(0);
	  
	  nodes[startVertex.getId()].setParent(start_node);
	  //queue.addElement(source);
	  VertexString currentNode= startVertex;
	  while(!queue.isEmpty()){
	  
		  NodePriorityQueue<VertexString> node_priority= queue.removeMin();
		  VertexString node = node_priority.getContent();
	    if(node.getContent().equals(endVertex)) {
	    	currentNode = node;
	    	break;
	    }
	    else{
	    	node.visited();
	      
	      
	      boolean[] neighbors = graph.getNeighbors(node.getId());
	      double distance = nodes[node.getId()].getPriority();
	      for (int i = 0; i < neighbors.length; i++) {
	        if(neighbors[i] && !graph.find(i).isVisited())
	        {
	        	VertexString nex_node = graph.find(i);
	        	double nex_distance = distance + graph.cost(node.getId(), nex_node.getId());
	        	System.out.println(nex_distance + " : " + node.getContent() + "-" + nex_node.getContent());
	        	if(!nex_node.isExpanded()) {
	        		NodePriorityQueue<VertexString> add_node = new NodePriorityQueue<VertexString>(nex_node);
	        		start_node.setPriority(nex_distance);
	        		queue.addElement(add_node);
	        		//queue.addElement(nex_distance, nex_node);
	        		nodes[nex_node.getId()].setPriority(nex_distance);
	        		nodes[nex_node.getId()].setParent(add_node);
	        		nex_node.setParent(node);
	        		nex_node.expanded();
	        	} else {
	        		if(nodes[nex_node.getId()].getPriority() > nex_distance) {
	        			nex_node.setParent(node);
	        			nodes[nex_node.getId()].setPriority(nex_distance);
	        			NodePriorityQueue<VertexString> cur_node = (NodePriorityQueue<VertexString>) nodes[nex_node.getId()].getParent();
	        			cur_node.setPriority(nex_distance);
	        			queue.update(cur_node);
	        		}
	        	}
	        }
	      }
	    }
	  }
	  double answer_distance = nodes[currentNode.getId()].getPriority();
	  String[] answer = new String[graph.size()];
	  int cnt = 0;
	  while(true) {
		  
		  answer[cnt ++] = currentNode.getContent();		  
		  if(currentNode.getContent().equals(source)) {
			  break;
		  }
		  currentNode = (VertexString) currentNode.getParent();
	  }
	  board.clearText();
	  board.cellContent(graph.size() + 2, 0, "PATH: ");
	  board.cellBackgroundColor(graph.size() + 2, 0, Color.GREEN);
	  for(int i = cnt - 1; i >= 0; i --) {
		  board.appendText(answer[i]);
        board.cellContent(graph.size() + 2, (cnt - i) * 2, answer[i]);
        board.cellBackgroundColor(graph.size() + 2, (cnt - i) * 2, Color.GREEN);
	  }
	  board.cellContent(graph.size() +4, 0, "Length ");
	  board.cellBackgroundColor(graph.size() +4, 0, Color.RED);
	  board.cellContent(graph.size() +4, 2, "" + answer_distance);
	  board.cellBackgroundColor(graph.size() +4, 2, Color.RED);
	  
	  
  }
  
}