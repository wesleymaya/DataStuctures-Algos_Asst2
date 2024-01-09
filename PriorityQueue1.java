// Name: Wesley Maya
// Student #: 300244659

// Collection that holds the nearest neighbours of a given query point
public class PriorityQueue1{

    // Variables
    LabelledPoint[] priorityQueue;
    int capasity, lastEle;
    LabelledPoint query;

    //Constructors
    public PriorityQueue1(int capasity, LabelledPoint query){
        this.priorityQueue = new LabelledPoint[capasity];
        this.query = query;
        this.capasity = capasity;
        this.lastEle = -1;
    }

    // Methods

    // Inserting point into the NearestNN array
    public void insert(LabelledPoint point){

        // Variables
        double pointDistanceToQuery = point.getDistanceToQuery();

        // If priorityQueue is empty
        if(lastEle == -1){
            priorityQueue[0] = point;
            lastEle++;
        }

        // If the PQ is full
        if((lastEle == capasity -1)){

            // If the last point's distance is less than the outside adjacent point's distance
            if(priorityQueue[lastEle].getDistanceToQuery() < pointDistanceToQuery){
                return;
            }

            // If new point is closer than all the other points in the PQ
            if(pointDistanceToQuery < priorityQueue[0].getDistanceToQuery()){
                this.insertAndShift(point, 0, true);
            }
        
            else{
                int insertPosition = binarySearch(point, pointDistanceToQuery);
                this.insertAndShift(point,insertPosition,true);
                }
                            
        }

        // If the PQ is full
        if((lastEle != capasity -1)){

            // If the last point's distance is less than the outside adjacent point's distance
            if(priorityQueue[lastEle].getDistanceToQuery() < pointDistanceToQuery){
                lastEle++;
                priorityQueue[lastEle] = point;
            }

            // If new point is closer than all the other points in the PQ
            if(pointDistanceToQuery < priorityQueue[0].getDistanceToQuery()){
                this.insertAndShift(point, 0, false);
                lastEle++;
            }

            // If new point is in between the first and last point of the PQ
            else{
                int insertPosition = binarySearch(point, pointDistanceToQuery);
                this.insertAndShift(point, insertPosition, false);
                lastEle++;
                }

        }
            
		}
    
    // Method used to insert a point at a occupied index
    private void insertAndShift(LabelledPoint p, int insertIndex, boolean full){

        // If the insert index is the last element in a full PQ
        if(full && (insertIndex == capasity -1)){
            priorityQueue[insertIndex] = p;
        }

        // Insert and shift when the PQ is full
        if(full){
            for(int i = lastEle-1; i >= 0; i--){
                if(i != insertIndex){
                    priorityQueue[i+1] = priorityQueue[i];
                }
                else{
                    priorityQueue[i+1] = priorityQueue[i];
                    priorityQueue[insertIndex] = p;
                }
                }
        }

        // Insert and shift when the PQ is not full
        if(!full){
            for(int j = lastEle; j >= 0; j--){
                if(j != insertIndex){
                    priorityQueue[j+1] = priorityQueue[j];
                }
                else{
                    priorityQueue[j+1] = priorityQueue[j];
                    priorityQueue[insertIndex] = p;
                }
                }
        }
        
    }

    // Returns where a point should be placed in the PQ based on it's distance to the query
    public int binarySearch(LabelledPoint pt, double distanceToQuery){
        int left, right, middle;
        left = 0;
        right = lastEle;

        if(distanceToQuery < 0){
            return -1;
        }

        if(distanceToQuery < priorityQueue[left].getDistanceToQuery()){
            return 0;
        }
        
        while((left <= right) && ((right - left) > 2)){
            // Middle index and labelledpoint at midpoint of PQ
            middle = (left + right)/2;
            double middleDTQ = priorityQueue[middle].getDistanceToQuery();

            if(distanceToQuery == middleDTQ){
                return middle;
            }

            if(distanceToQuery < middleDTQ){
                right = middle;
            }

            if(distanceToQuery > middleDTQ){
                left = middle;
            }
        }

        // Checks if the given point already exists in the PQ
        for(int i = left; i <= right; i++){
            if(pt.equals(priorityQueue[i])){
                return -1;
            }
        }

        // If point isn't already in PQ from for loop above, will find where the point should be inserted in the PQ
        for(int i = left; i < right; i++){
            if(distanceToQuery == priorityQueue[i].getDistanceToQuery()){
                return i;
            }
            if((distanceToQuery > priorityQueue[left].getDistanceToQuery()) && (distanceToQuery < priorityQueue[left + 1].getDistanceToQuery())){
                return left + 1;
            }
        }

        return -1;
    }


    // Returns priority queue
    public LabelledPoint[] getPQ(){
        return priorityQueue;
    }

    // Returns the last element of the PQ
    public int getLastElementIndex(){
        return lastEle;
    }

    // Returns the capasity of the PQ
    public int getCapasity(){
        return capasity;
    }

    // Returns query point
    public LabelledPoint getQuery(){
        return query;
    }

    // Returns LabelledPoint at given index
    public LabelledPoint get(int index){
        return priorityQueue[index];
    }
}