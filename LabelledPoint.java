/* ---------------------------------------------------------------------------------
// Name: Wesley Maya
// Student #: 300244659

A vector of Floats with its associated int label
It includes also a key that can be used to order LabelledPoint instances
This new version also includes an integer key
and a boolean flag

(c) Robert Laganiere, CSI2510 2023
------------------------------------------------------------------------------------*/

class LabelledPoint implements Comparable<LabelledPoint> {
    private Float[] vector;   // the vector
	private int label;        // the id (should be unique)
	private double key;       // the key used for ordering
	private int ikey;         // an additional integer key
	private boolean checked;  // a boolean flag
	private double distanceToQuery; // the distance to a given query point
	private boolean distanceToQueryChecked; // a boolean flag to check if a distance to a query point has been set

    public LabelledPoint(Float[] vector, int label) {
        this.vector = vector;
		this.label= label;
		this.key= 0.0;
		this.ikey= -1;
		this.checked= false;
		this.distanceToQueryChecked = false;
    }
	
	// gets the label
	public int getLabel() {
	    return label;
	}
	
	// gets the vector (an array of floats)
	public Float[] getVector(){
	
	    return vector;
	}
	
	// gets the key value
	public double getKey() {
		return key;
	}
	
	// set the key value
	public void setKey(double k) {
		key= k;
	}

	// gets the int key value
	public double getIKey() {
		return ikey;
	}
	
	// set the int key value
	public void setIKey(int k) {
		key= k;
	}
	
	// set checked flag to true
	public void checked() {
		checked= true;
	}	

	// set checked flag to false
	public void unchecked() {
		checked= false;
	}
	
	// Returns boolean checked
	public boolean getChecked(){
		return checked;
	}

    // gets the length (dimension) of the vector	
	public int getLength() {
	    return vector.length;	
	}

	// Returns distance of this labelledPoint to a given query point
	public double getDistanceToQuery(){
		return distanceToQuery;
	}

    // computes the Euclidean distance between two vectors
    public double distanceTo(LabelledPoint other) {
        double sum = 0;
        for (int i = 0; i < vector.length; i++) {
            float diff = vector[i] - other.vector[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

	// Sets and returns distance from this labelledPoint to a given point query
	public double setDistanceToQuery(LabelledPoint query){
		distanceToQuery = distanceTo(query);
		distanceToQueryChecked = true;
		return distanceToQuery;
	}

	// Returns boolean of whether distance to the query point has been checked
	public boolean distanceToQueryChecked(){
		return distanceToQueryChecked;
	}

    // compares two LabelledPoint instances	
	public int compareTo(LabelledPoint o) {
		return Double.compare(this.key, o.key);
	}

	// Compares distances of two points to a query
	public int compareDistanceToQuery(LabelledPoint other){
		return Double.compare(this.distanceToQuery, other.distanceToQuery);
	}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i=0; i<4; i++) {
            sb.append(vector[i]).append(", ");
        }
        sb.append("..., ");
        for (int i=vector.length-2; i<vector.length; i++) {
            sb.append(vector[i]).append(", ");
        }
        sb.setLength(sb.length() - 2); 
        sb.append(")");
        return sb.toString();
    }

	public static void main(String[] args){
		Float[] vec1 = {1.0f,2.0f,3.0f,4.0f};
		LabelledPoint new1 = new LabelledPoint(vec1, 0);

		System.out.println(new1.getDistanceToQuery());
	}
}
