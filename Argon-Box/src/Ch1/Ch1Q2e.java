package Ch1;
import java.io.*;
import java.math.*;

/* 	Ch1Q1.java - Computer exercises for Harmonic oscillator, part 1

	This code is meant to solve the problems in chapter 1 and part 1 of this course.
	In particular, this works on question 1, which uses Euler's method.
 */

public class Ch1Q2e {

	public static void main(String[] args) throws FileNotFoundException
	{
		FileOutputStream fout = new FileOutputStream("Ch1Q2eOutput.txt");	// Creating a CSV file
		//		FileOutputStream fout2;
		//Ch1 ch = new Ch1();
		//		try
		/*			fout = new FileOutputStream("Ch1Q2bOutput.csv");
			fout2 = new FileOutputStream("Ch1Q2bOutputPS.csv");
			PrintStream ps = new PrintStream(fout); 
			PrintStream ps2 = new PrintStream(fout2);
		 */
		PrintStream ps = new PrintStream(fout); 
		for (int power = -1; power >= -7; power--) // 10^power -> power shrinks, value increases
		{
			for (int base = 10; base > 0; base--) //base goes down as value shrinks - looking for LARGEST value
			{		
				double g = 1; // g is the degrees of freedom
				double m = 0.25; // mass of the particle
				double w = 2; // oscillating frequency of the particle
				double q = 1; // initial position
				double delT = base * Math.pow(10, power);
				double kT = 4;
				double V = potential(w, q);
				//	double p = momentum(m, kT/2); //initial momentum
				//	double p = Math.sqrt(kT/2*m);
				double p =0;
				double K = kinetic(m, p);
				double E0 = K + V;
				double E = E0; // Storing the previous E for energy loss calculations
				double Econs = 0;	// Energy conservation sum
				double EconsMax = 1e-4;
				//		double pold = p; // previous momentum
				double F = -m*(p/delT); // will assume constant force, just for sanity's sake.
				ps.print("Initial conditions: E = " + K + " + " + V);
				ps.println(" where F = " + F);
				int t = 0;
				int tcycles = (int)1e5; // The number of steps. Used for averaging.
				ps.println("Delta T is: " + delT);

				while (t <= tcycles)
				{
					p = p + (F*delT)/2;
					//			System.out.println("delta P is: " + (p - pold));
					q = q + (p*delT)/m;
					F = -w*q; // calculate new force
					//					if (t % 100 == 0){
					//						ps.print("Current set: p = " + p + " q = " + q + " F = " + F);
					//}
					p = p + (F*delT)/2;
					if ((t % 1000 == 0)&&(t>0)||(t == 5)||(t == 10)||(t == 60))
					{
						//						ps.println(" t = " + t + " and new p = " + p);
						//						ps.println("Energy conserved = " + (Econs/t));
						ps.println("Econs: " + Econs);
						//					ps.println(t + " "  + (Econs/t));	// prints to file
						//					ps2.println(q + "  " + p);

					}
					E = potential(w,q) + kinetic(m, p);
					Econs += Math.abs((E - E0)/E0);
					//			pold = p;
					t++;
				}
				if (Econs < EconsMax){
					ps.println("A timestep of " + delT + " is the largest which holds energy conservation below " + EconsMax + " after 10,000 steps.");
					System.out.println("A timestep of " + delT + " is the largest which holds energy conservation below " + EconsMax + " after 10,000 steps.");
					System.out.println("The final energy conservation is:" + Econs);
					System.out.println("For the data, check out Ch1Q2eOutput.txt. Thanks!");
					return;
				}
				//			ps.println(t + "," + (Econs/tcycles));

				/*			ps.close();
			ps2.close();
			fout.close();
			fout2.close();
		} catch (IOException e){
			ps.println("Could not create file.");
				 */		}
		}
	}


	// helper functions that calculates energies and momentum

	public static double kinetic(double m, double p)
	{
		double k = ((p*p)/(2*m));
		//		System.out.println("K: " + k);
		return k;
	}

	public static double potential(double w, double q)
	{
		double V = (w*(q*q/2));
		//		System.out.println("V: " + V);
		return V;
	}

	public static float momentum(double mass, double kinetic)
	{
		double rho = 2*kinetic / mass;
		rho = Math.pow(rho, 0.5);
		return (float)(rho * mass);
	}
}