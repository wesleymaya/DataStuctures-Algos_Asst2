/* ---------------------------------------------------------------------------------
// Name: Wesley Maya
// Student #: 300244659

The GraphA1NN class is the starting class for the graph-based ANN search

(c) Robert Laganiere, CSI2510 2023
------------------------------------------------------------------------------------*/

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Random;


class GraphA1NN {
	
	// UndirectedGraph of LabelledPoints and their adjacent LabelledPoints
	UndirectedGraph<LabelledPoint> annGraph;
	// List of LabelPoints
    private PointSet dataset;
	// List of adjacent LabelledPoints to the LabelledPoints in the user's dataset
	private ArrayList<List<Integer>> adjacentPoints;
	// Value used for making the size of the array for find1NN
	private Integer s; 
	// Nearest Neighbours PQ
	private PriorityQueue1 nearestNeighbours;

	// construct a graph from a file
    public GraphA1NN(String fvecs_filename) {

	    annGraph= new UndirectedGraph<>();
		dataset= new PointSet(PointSet.read_ANN_SIFT(fvecs_filename));
    }
	
	// Constructs a graph from a file representing a PointSet and a file representing a adjacenty list of points to the points in the PointSet
	public GraphA1NN(String fvecs_filename, String adjacencylist_file, int numberOfVertices) {

		try{
			annGraph= new UndirectedGraph<>();
			dataset= new PointSet(PointSet.read_ANN_SIFT(fvecs_filename));
			adjacentPoints = readAdjacencyFile(adjacencylist_file, numberOfVertices);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
    }


	// construct a graph from a dataset
    public GraphA1NN(PointSet set){
		
	   annGraph= new UndirectedGraph<>();
       this.dataset = set;
    }

	// Sets adjacent points list from given list
	public void setAdjacentPoints(ArrayList<List<Integer>> adjacencyList){
		adjacentPoints = adjacencyList;
	}

	// Sets adjacent points list used when constructing the KNNGraph
	public void setAdjacentPoints(String fileName, int numberOfVertices){

		try{
			adjacentPoints = readAdjacencyFile(fileName, numberOfVertices);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

    // build the graph
    public void constructKNNGraph(int k) {

		if(k > sizeDataset()){
			System.out.println(k +" points do not exist in this dataset. Try a smaller number.");
			return;
		}

		// Checks if adjacentPoints list is null
		if(adjacentPoints == null){
			System.out.println("No adjacentPoints list found");
			return;
		}
		
		// Creates a graph of K nearest neigbours
		for (int i=0; i < sizeDataset(); i++) { // For each point in the dataset < k
			List<Integer> adjPoints = adjacentPoints.get(i);

			//Adds an edge between adjacent vertex j of vertex i
			for (int j=0; j<k; j++) {
				LabelledPoint vertex1 = dataset.getPointsList().get(i);
				int vertex2loc = adjPoints.get(j);
				LabelledPoint vertex2 = dataset.getPointsList().get(vertex2loc);
				annGraph.addEdge(vertex1, vertex2);
				}
		}
	}
	
	public static ArrayList<List<Integer> > readAdjacencyFile(String fileName, int numberOfVertices) 
	                                                            	 throws Exception, IOException
	{	
		ArrayList<List<Integer> > adjacency= new ArrayList<List<Integer> >(numberOfVertices);
		for (int i=0; i<numberOfVertices; i++) 
			adjacency.add(new LinkedList<>());
		
		// read the file line by line
	    String line;
        BufferedReader flightFile = 
        	      new BufferedReader( new FileReader(fileName));
        
		// each line contains the vertex number followed 
		// by the adjacency list
        while( ( line = flightFile.readLine( ) ) != null ) {
			StringTokenizer st = new StringTokenizer( line, ":,");
			int vertex= Integer.parseInt(st.nextToken().trim());
			while (st.hasMoreTokens()) { 
			    adjacency.get(vertex).add(Integer.parseInt(st.nextToken().trim()));
			}
        } 
	
	    return adjacency;
	}

	// Finds nearest neighbour to a given point pt
	public LabelledPoint find1NN(LabelledPoint pt){

		// Checks if s value has been set or if it is less than 1
		if(s == null || s < 1){
			System.out.println("S value has not been set or is not valid");
			return null;
		}

		// Checks if a graph has been constructed
		if(annGraph == null){
			System.out.println("There is no graph to be searched."
					  + "\n" + "Please create a graph using the function constructKNNGraph.");
			return null;
		}

		// Variables needed for find1NN algorithm
		nearestNeighbours = new PriorityQueue1(s,pt);
		LabelledPoint nearestN;
		ArrayList<LabelledPoint> pointsList = dataset.getPointsList();
		Random random = new Random();
		int startingkey = random.nextInt(0,10000);
		int lastEle = -1;
		boolean finished = false;

		// Calculates distance to given query point, and then inserts the LabelledPoint into the array nearestNeighbours
		LabelledPoint startingPoint = pointsList.get(startingkey);
		startingPoint.setDistanceToQuery(pt);
		nearestNeighbours.insert(startingPoint);

		while(!finished){
			lastEle = nearestNeighbours.getLastElementIndex();
			for(int i = 0; i <= lastEle; i++){
				if(nearestNeighbours.get(i).getChecked() == false){
					nearestN = nearestNeighbours.get(i);
					this.checkAdjNeighbours(nearestN, pt, pointsList);
					break;
				}
				if(i == lastEle){
					finished = true;
				}
			}
		}

		// Returns the nearest neighbour to the query point pt
		return nearestNeighbours.get(0);
	
	}

	// Going through adjacent points of nearestN and checking if they've been checked
	// If not, we compute their distance to the query point, and then insert it into the nearestNs array
	private void checkAdjNeighbours(LabelledPoint nearestN, LabelledPoint queryPt, ArrayList<LabelledPoint> pointsList){
		List<LabelledPoint> adjacentNs = annGraph.getNeighbors(nearestN);
			for(int j = 0; j < adjacentNs.size(); j++){

				// Getting point in adjaceny list
				int adjPLabel = adjacentNs.get(j).getLabel();
				LabelledPoint adjPoint = pointsList.get(adjPLabel);

				// Checking if the adjPoint has been checked against Query point, then inserting into nearestNeighbours collection if not checked
				if(adjPoint.distanceToQueryChecked() == false){
					adjPoint.setDistanceToQuery(queryPt);

					// Checks whether adjPoint already exists in the nearest neighbours list
					int position = nearestNeighbours.binarySearch(adjPoint, adjPoint.getDistanceToQuery());

					if(position == -1){
						return;
					}
					else{
						nearestNeighbours.insert(adjPoint);
					}


				}
			}
			nearestN.checked();
	}

	// Sets s value for desired size of find1NN array
	public void setS(int s){
		this.s = s;
	}
	
	// Returns ANNGraph
	public UndirectedGraph<LabelledPoint> getGraph(){
		return annGraph;
	}

	// Returns neighbours a vertex in the annGraph
	public List<LabelledPoint> getNeighbours(LabelledPoint vertex){
		return annGraph.getNeighbors(vertex);
	}

	// Returns nearest neighbours PQ created in Find1NN algorithm
	public PriorityQueue1 getA1NNPQ(){
		return nearestNeighbours;
	}

	// Returns original ArrayList of LabelledPoints of your dataset
	public ArrayList<LabelledPoint> getDataset(){
		return dataset.getPointsList();
	}

	// Returns the amount of verticies in your graph
	public int size() { return annGraph.size(); }

	// Returns the amount of data points in your dataset
	public int sizeDataset(){ return dataset.getPointsList().size(); }

	
	
	// args array should contain variables as follows: int k, int s, basefile.fvecs, queryfile.fvecs
    public static void main(String[] args) throws IOException, Exception {
		
		// Creating the graph from the base fvecs file
		GraphA1NN graph = new GraphA1NN(args[2]);
		int graphSize = graph.sizeDataset();
		
		// Creating a PointSet for the query points
		ArrayList<LabelledPoint> queryPoints = PointSet.read_ANN_SIFT(args[3]);

		// Creating and setting the adjacent verticies to the points in the graph
		ArrayList<List<Integer> > adjacency= GraphA1NN.readAdjacencyFile("knn.txt", 10000);
		graph.setAdjacentPoints(adjacency);
		
		// Constructing the graph and setting variable S
		graph.constructKNNGraph(Integer.parseInt(args[0]));
		graph.setS(Integer.parseInt(args[1]));

		// Constructing the given KNN of each point
		//ArrayList<List<Integer> > correctKNN = GraphA1NN.readAdjacencyFile("knn_3_10_100_10000.txt", 100);
		
		// Presentation of the approximate nearest neighbour for each point
		System.out.println();
		System.out.println("Nearest neighbours of points 0 to 99:");
		System.out.println("K value: " + Integer.parseInt(args[0]));
		System.out.println("S value: " + Integer.parseInt(args[1]));
		System.out.println();


		// Testing accuracy and execution of A1NN class
		double avgSearchTime = 0;
		int accuracyRate = 0;
		
		for(int i = 0; i < 100; i++){
			double startTime = System.currentTimeMillis();

			LabelledPoint queryPoint = queryPoints.get(i);
			LabelledPoint approxNN = graph.find1NN(queryPoint);
			int ANNLabel = approxNN.getLabel();
			
			double endTime = System.currentTimeMillis();

			// Commented out part of code that would've compared each resulting A1NN to the correntKNN values
			// Then would've added them to the tally of accurate A1NNs and divided the total accurate values by the value of k(100)

			//List list = correctKNN.get(j);
			//System.out.println(list);

			//for(int j = 0; j < 10; j++){
				
				//if(ANNLabel == list.get(j).intValue()){
				//	accuracyRate++;
				//	break;
				//}
			//}
			
			avgSearchTime += (endTime - startTime);
			System.out.println(i +": " + approxNN.getLabel());
		}
		avgSearchTime = avgSearchTime/100;
		//accuracyRate = accuracyRate/100;


		// Statistics of the A1NN Algorithm
		System.out.println();
		System.out.println("Average time to find the A1NN of a point: " + avgSearchTime + "ms");
		//System.out.println("Accuracy rate of the A1NN: " + accuracyRate);

	}
}

