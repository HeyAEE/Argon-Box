package Ch3;

import java.util.Random; //yeah... have to use a random number generator to create a random number generator

public class BoxMuller {
	//An implementation of the Box-Muller Pseudo-Random Number Generator
	//Coding based on the guide found here: http://en.literateprograms.org/Box-Muller_transform_(C)
		
	Random gen = new Random();
	
	public BoxMuller()
	{
	}
	
	public double[] rand(){
		double x, y, r, s; // the basic numbers needed
		double xd, yd; // the resulting normally deviated "random numbers"
		//have to make sure the points are within a unit circle
		do{
			x = gen.nextDouble();
			y = gen.nextDouble();
			r = x*x + y*y;
		} while (r == 0 || r >= 1.0);
		s = Math.sqrt(r);
		double dev = Math.sqrt((-2)*Math.log(s)/s);
		xd = x*dev;
		yd = y*dev;
		double[] dtable = {xd, yd};
		return dtable;
	}

}
