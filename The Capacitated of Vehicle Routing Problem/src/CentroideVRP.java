import java.util.ArrayList;

/**
 * The CentroideVRP class has the list of clusters created in the algorithm and the instance of the problem that will be solved 
 */
public class CentroideVRP {
	
	private ArrayList <Cluster> clusters = new ArrayList <Cluster>();
	private InstanceProblem instance;
	
	
	public CentroideVRP(InstanceProblem instance) {
		this.instance = instance;
	}
	
	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(ArrayList<Cluster> clusters) {
		this.clusters = clusters;
	}

	/**
	 * Centroid-based algorithm applied to the instance of the CVRP
	 * @param maxIterSimulated 
	 * @return best solution with routes and total cost 
	 */
	public Solution runCentroideVRP(int maxIterSimulated) {
		constructCluster (); //phase 1: construction of clusters
		Solution solution = aplyingTSPClusters(maxIterSimulated); //find the best solution by solving the TSP in each cluster 
		Solution bestSolution = solution;
		
		do {
			//phase 2: adjustment of clusters 
			boolean resp = clusterAdjustment();
			if (!resp) {
				break;
			}
			else {
				 //if clusters were adjusted, TSP was solved using Simulated Annealing for each cluster 
                 solution = aplyingTSPClusters(maxIterSimulated); 
                 if (solution.getTotalCost() < bestSolution.getTotalCost()){
                	 bestSolution = solution;
                 }
 			}
				
		} while (true);
		return bestSolution;
	}
	
	/**
	 * Solves TSP using Simulated Annealing for each cluster 
	 * @param maxIterSimulated
	 * @return all routes and the total cost of all routes 
	 */
	public Solution aplyingTSPClusters(int maxIterSimulated){
		Solution solution =  new Solution();
		
        double totalCost = 0;
		for (int i = 0; i < clusters.size(); i++) {
			Tsp tspi = new Tsp(instance, clusters.get(i).getCustomers());
			//for cluster i it is called Simulated Annealing to find the route with the best cost 
			double bestCost = tspi.simulatedAnnealing();
			tspi.getRoute().setCostRoute(bestCost);
			Route bestRoute=tspi.getRoute();
			
			/* As Simulated Annealing can give different results for each call due to the random values that are 
			 * used, we call the Simulated Annealing maxIterSimulated times and we ask for the best route found 
			 * in these maxIterSimulated iterations 
			 */
			for (int j = 1; j < maxIterSimulated; j++){
				double cost = tspi.simulatedAnnealing();
				tspi.getRoute().setCostRoute(cost);
	            if (cost<bestCost){
	            	bestCost=cost;
	            	bestRoute = tspi.getRoute();
	            }
			}
			
			solution.addRoute(bestRoute);
			totalCost = totalCost + bestCost ;
		}
		solution.setTotalCost(totalCost);
		return solution;
	}
	
	
    //to generate the seed for each cluster, the client is found that is furthest from the depot and has not yet been clustered 
	public Customer findFarthestUnClusteredCustomerToDepot () {
		Customer farthestCustomer = null;
		double farthestDistance = 0;
		double distanceToDepot = 0;
		int idDepot = instance.getIdDepot();
		
		for (int i = 0; i < instance.getNumberOfCustomers(); i++) {
			Customer c = instance.getCustomers()[i];
			if (i+1 == idDepot || c.isInCluster() == true) { 
				continue;
			}
			else {
				/* 	If client c is not in the cluster and is not the depot, then take the adjacency matrix 
				 *  the distance to the deposit 
				 */
				distanceToDepot = instance.getAdjacencyMatrix()[i][idDepot-1];
				if (distanceToDepot > farthestDistance) {
					farthestDistance = distanceToDepot;
					farthestCustomer = c;
				}
			}
			
		}
		return farthestCustomer;
		
	}
	
	//Phase 1: build all clusters of the problem intent 
    public void constructCluster () {
		
		int Q = Truck.capacity;
		//while there are still clients not yet visited, build the clusters 
		while (instance.getVisitedCustomers().size() < (instance.getNumberOfCustomers()-1)) {
			Customer vj = findFarthestUnClusteredCustomerToDepot (); //vj will be the seed of the cluster 
			Cluster l = new Cluster ();
			//cluster l is created and added to the cluster set 
			clusters.add(l);
			l.setAvailableCapacity(Q);
			//while there are still customers not yet visited and customer demand does not exceed the remaining capacity available in the vehicle  
			while (instance.getVisitedCustomers().size() < (instance.getNumberOfCustomers()-1) && l.getAvailableCapacity() >= vj.getDemand()) {
				// then the vj client is added to the cluster and the geometric center is recalculated
				l.addCustomerToCluster (vj, instance);
				l.setAvailableCapacity(l.getAvailableCapacity() - vj.getDemand());
				l.setGCx(l.computeGCx());
				l.setGCy(l.computeGCy());
				vj = l.findClosestUnClusteredCustomerToGC (instance);
			}
		}
    }
    
    //Phase 2: adjustment of the clusters generated in phase 1
    public boolean clusterAdjustment () {
    	boolean doAdjustment = false;
    	for (int i = 0; i < clusters.size(); i++) {
    		Cluster li=clusters.get(i);
    		/* It is checked for each client i in the cluster li if it is closer to the centroid 
    		 * neighboring cluster lk than the centroid of your current cluster. 
    		 * If this occurs and the capacity of the neighboring cluster is not exceeded, then 
    		 * this client goes to the neighboring cluster and the centroid of both groups are recalculated.
    		 */
    		for (int k=0; k< li.getCustomers().size();k++) {
    			Customer vk=li.getCustomers().get(k);
    			for (int j = 0; j < clusters.size(); j++) {
    				Cluster lj = clusters.get(j);
    				
    				double distanceVkToGCLj = vk.euclideanDistance (lj.getGCx(), lj.getGCy());
    				double distanceVkToGCLi = vk.euclideanDistance (li.getGCx(), li.getGCy());
    				if (i != j && distanceVkToGCLj < distanceVkToGCLi && lj.getAvailableCapacity() >= vk.getDemand()) {
    					li.getCustomers().remove(vk);
    					lj.getCustomers().add(vk);
    					li.setAvailableCapacity(li.getAvailableCapacity() + vk.getDemand());
    					lj.setAvailableCapacity(lj.getAvailableCapacity() - vk.getDemand());
    					
    					// recompute geometric center 
    					li.setGCx(li.computeGCx());
    					li.setGCy(li.computeGCy());
    					
    					lj.setGCx(lj.computeGCx());
    					lj.setGCy(lj.computeGCy());
    					
    					
    					doAdjustment = true;
    				}
    			}
    		}
    	}
    	return doAdjustment;
    }

}
