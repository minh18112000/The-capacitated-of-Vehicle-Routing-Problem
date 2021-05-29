import java.util.LinkedList;

public class Cluster {
	
	private double GCx; //x coordinate of the geometric center of the cluster
	private double GCy; //y coordinate of the geometric center of the cluster 
	private  LinkedList <Customer> customers = new LinkedList <Customer>(); //list of clients in the cluster 
	private int availableCapacity; //currently available capacity 
	public Cluster() {
	}	
	
	@Override
	public String toString() {
		return "Cluster [GCx=" + GCx + ", GCy=" + GCy + ", customers="
				+ customers + ", availableCapacity=" + availableCapacity + "]\n";
	}

	public int getAvailableCapacity() {
		return availableCapacity;
	}

	public void setAvailableCapacity(int availableCapacity) {
		this.availableCapacity = availableCapacity;
	}

	public double getGCx() {
		return GCx;
	}

	public void setGCx(double gCx) {
		GCx = gCx;
	}

	public double getGCy() {
		return GCy;
	}

	public void setGCy(double gCy) {
		GCy = gCy;
	}

	public LinkedList<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(LinkedList<Customer> customers) {
		this.customers = customers;
	}
	
	/**
	 * Calculate which client has not yet been clustered and is closest to the cluster's geometric center 
	 * @param instance
	 * @return client closest to the cluster GC
	 */
	public Customer findClosestUnClusteredCustomerToGC (InstanceProblem instance) {
		Customer closestCustomer = null;
		double closestDistance = Double.POSITIVE_INFINITY;
		double distanceToGC = 0;
		int idDepot = instance.getIdDepot();
		
		for (int i = 0; i < instance.getNumberOfCustomers(); i++) {
			Customer c = instance.getCustomers()[i];
			if (i+1 == idDepot || c.isInCluster() == true) { 
				continue;
			}
			else {
				//calculate the distance from the customer c to the geometric center of the cluster 
				distanceToGC = c.euclideanDistance (this.GCx, this.GCy);
				if (distanceToGC < closestDistance) {
					closestDistance = distanceToGC;
				    closestCustomer = c;
				}
			}
		}
		return closestCustomer;
		
	}
	
	//calculate the x coordinate of the geometric center 
	public double computeGCx () {
		int sum = 0;
		for (int i = 0; i < customers.size(); i++) {
			sum = sum + customers.get(i).getX();
		}
		return sum/customers.size();
	}
	
	//calculate the y coordinate of the geometric center 
	public double computeGCy () {
		int sum = 0;
		for (int i = 0; i < customers.size(); i++) {
			sum = sum + customers.get(i).getY();
		}
		return sum/customers.size();
	}
	
	//add client c to the cluster, and add c to the set of visited clients of the instance 
	public void addCustomerToCluster (Customer c, InstanceProblem instance) {
		customers.add(c);
		c.setInCluster(true);
		instance.getVisitedCustomers().add(c);
	}

}

