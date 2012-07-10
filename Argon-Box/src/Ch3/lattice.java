package Ch3;

public class lattice {
	
	public static void main(String[] args){
		BoxMuller bm = new BoxMuller();
		double[] arr = new double[2];
		for (int i = 0; i < 5; i++){
			arr = bm.rand();
			System.out.println("Trial " + (i+1) + ": [" + arr[0] + ", " + arr[1] + "]");
		}
	}
	

}
