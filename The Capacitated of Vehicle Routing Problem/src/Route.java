import java.util.LinkedList;

public class Route {
	private LinkedList <Customer> route; //route includes the deposit at the beginning and at the end 
	private double costRoute=0;
	
	
	public Route(LinkedList <Customer> route) {
		this.route = (LinkedList<Customer>) route.clone();
	}
	
		
	public LinkedList<Customer> getRoute() {
		return route;
	}

	public void setRoute(LinkedList<Customer> route) {
		this.route = route;
	}
	
	
	public double getCostRoute() {
		return costRoute;
	}


	public void setCostRoute(double costRoute) {
		this.costRoute = costRoute;
	}


	@Override
	public String toString() {
		return "Route = " + route + ", Cost = " + costRoute + "\n";
	}

    /**
     * Calculates the total cost of the route 
     * @param instance
     * @return the total cost 
     */
	public double computeCostOfRoute (InstanceProblem instance) {
		double totalCost = 0;
		for (int i = 0; i < route.size(); i++) {
			Customer from = route.get(i);
			Customer to;
			if (i+1 < route.size()) {
				to = route.get(i+1);
			}
			else {
				to = route.get(0);
			}
			//takes the cost between the client from and to the Adjacency matrix 
			double distFromTo = instance.getAdjacencyMatrix()[from.getId()-1][to.getId()-1];
			totalCost = totalCost + distFromTo;
		}
		return totalCost;
	}
}
