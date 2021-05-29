
public class Test {

	public static void main(String[] args) {
		
		//a problem is created from the input file 
		InstanceProblem instance = new InstanceProblem();
		instance.inicializeInstanceFromFIle ("C:\\Users\\PC\\Downloads\\CVRP-master (4)\\CVRP-master\\VRPFinal\\Entradas\\A-n33-k5.vrp");
		
		System.out.println(instance);
		
		long initialTime = System.currentTimeMillis();
		//the centroid algorithm is run for the vehicle routing problem based on the instance created earlier 
		CentroideVRP cen=new CentroideVRP(instance);
		
		Solution solution=cen.runCentroideVRP(5);
		long finalTime = System.currentTimeMillis();
		long runTime = finalTime-initialTime;
		System.out.println("Runtime: " + runTime + " ms");
		//the execution time of the VRPC centroid is calculated for this instance in ms 
		System.out.println(solution);
			
	}
	
}
