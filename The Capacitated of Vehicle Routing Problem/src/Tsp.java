import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 *The Tsp class has all the information for the problem instance, the cluster clients from the
 * which route will be created 
 */
public class Tsp {
	private InstanceProblem instance;
	private LinkedList <Customer> cluster;  //does not include the depot 
    private Route route; // includes depot 
	
	public Tsp(InstanceProblem instance, LinkedList <Customer> cluster) {
		this.instance = instance;
		this.cluster=cluster;
		createRouteFromCluster();
	}
	
	//the route is created by placing all customers on it. The depot is placed at the beginning and end of the route 
    public void createRouteFromCluster(){
    	Customer depot=instance.getDepot();
		route = new Route(cluster);
		route.getRoute().addFirst(depot);
		route.getRoute().addLast(depot);
    }

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	/**
	 * The Simulated Annealing algorithm is applied to the route created
	 * @return the lowest cost route 
	 */
	public double simulatedAnnealing () {
		
		int sizeRoute = route.getRoute().size();   
		
		Customer c1;
		Customer c2;
		//Simple cases
		if(sizeRoute == 3) //just a customer 
			return  route.computeCostOfRoute(instance); 
		else if(sizeRoute == 4) { // two customers
			double cost = route.computeCostOfRoute(instance);
			//Switch the two position customers on the route and see if there is a better solution 
			c1 = route.getRoute().get(1);
			c2 = route.getRoute().get(2);
			route.getRoute().set(1, c2);
			route.getRoute().set(2, c1);
			
			double newCost = route.computeCostOfRoute(instance);
			
			if(newCost <= cost) return newCost;
			else { // otherwise, undo this exchange 
				route.getRoute().set(1, c1);
				route.getRoute().set(2, c2);
				return cost;
			}
		}
		
		//General case, cooling function 
		//initialize temperature variables, cooling 
 		double temperature = (double) (sizeRoute-2)*(sizeRoute-2);
		double coolingRate = (double) (1/(double)((sizeRoute-2)*(sizeRoute-2)));
		int numberOfChanges = 0;
		int maxNumberOfChanges = (sizeRoute-2)*(sizeRoute-2);
		
		//creating a random route to serve as the first solution 
		Collections.shuffle(cluster);
		createRouteFromCluster();
		
		//the route cost is created 
		double cost = route.computeCostOfRoute(instance);
		
		//while the condition is met, a neighboring solution is created by swapping two customers at random 
		while(temperature >= coolingRate || isLessThanMaxNumberOfChanges(numberOfChanges,maxNumberOfChanges)) {
			int pos1;// = randomInt(0, sizeRoute);
			int pos2;// = randomInt(0, sizeRoute);
			//two random customer positions on the route are taken to be exchanged 
			do{
				pos1 = randomInt(1, sizeRoute-1);
				pos2 = randomInt(1, sizeRoute-1);
			}while(pos1 == pos2);// || pos1 == 0 || pos2 == 0 || pos1 == sizeRoute-1 || pos2 == sizeRoute-1);


			//swap pos1 pos2
			c1 = route.getRoute().get(pos1);
			c2 = route.getRoute().get(pos2);
			route.getRoute().set(pos1, c2);
			route.getRoute().set(pos2, c1);
			double newCost = route.computeCostOfRoute(instance);

			boolean changed = false;
			
			//if the cost of this new route with this exchange of positions is lower, the new solution is accepted as the best 
			if(newCost > cost) { 
				//if the new cost is higher, we only accept this new solution if acceptation is true 
				boolean acceptation = accept(cost, newCost, temperature);
				if(acceptation == false) { //the exchange is undone 
					route.getRoute().set(pos1, c1);
					route.getRoute().set(pos2, c2);
					changed = false;
				}
				else {
					cost = newCost;
					changed = true;
				}
			}
			else { //we have a new solution that is better 
				cost = newCost;
				changed = true;
			}
			
			
			if(changed == true) {
				numberOfChanges = numberOfChanges + 1;
			}
			else numberOfChanges = numberOfChanges - 1;
			//the temperature is lowered, the system is cooling down 
			temperature = temperature - (temperature*coolingRate);
		}
 	    return cost; 
	}

    // returns a random [min, max]
    public int randomInt (int min, int max) {
	      Random r = new Random();
	      double d = min + r.nextDouble()*(max-min);
	      return (int) d;
    }
    
    //returns a random [0.1] 
    public static double randomDouble() {
    	  Random r = new Random();
	      return r.nextInt(1000)/1000.0;
    }
	
    //returns whether or not to accept the new neighboring solution that is worse
    private static boolean accept(double cost, double newCost, double temperature) {
		double deltaE = cost - newCost; //deltaE is always negative 
		double prob = Math.pow(Math.E, deltaE/temperature); //acceptance probability function is e ^ (deltaE / temperature) 

		double random = randomDouble();
		//accept returns true if the probability of acceptance is greater than a generated random 
		if(prob > random) return true;
		else return false;
	}
    
    //checks if the number of exchanges made by the algorithm is between -maxNumberOfChange and maxNumberOfChange 
    private static boolean  isLessThanMaxNumberOfChanges(int numberOfChanges, int maxNumberOfChanges) {
		int inverse = maxNumberOfChanges * -1;
		if(numberOfChanges <= maxNumberOfChanges && numberOfChanges >= inverse) return true;
		return false;
	}

}