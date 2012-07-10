package Ch1;
import java.math.*;
import java.io.*;

/* 	Ch1Q2.java - Computer exercises for Harmonic oscillator, part 2

	This code is meant to solve the problems in chapter 1 and part 1 of this course.
	In particular, this works on question 2, which adapts the previous code for the
	Velocity-Verlet method.
 */

public class Ch1Q2
{
	public static void main(String[] args)
	{
		FileOutputStream fout;	// will create a file for output of data. Data analyzed with GRACE.
		//Ch1 ch = new Ch1();
		FileOutputStream fout2;
		try {
			for (int i = 1; i < 6; i++)
			{
				double g = 1; // g is the degrees of freedom
				double m = 0.25; // mass of the particle
				double w = 2; // oscillating frequency of the particle
				double q = 1; // initial position
				double delT = Math.pow(10, -i);// Math.pow(10, -5); // timestep
				double kT = 4;
				double V = potential(w, q);
				//	double p = momentum(m, kT/2); //initial momentum
				//	double p = Math.sqrt(kT/2*m);
				double p =0;
				double K = kinetic(m, p);
				double E0 = K + V;
				double E = E0; // Storing the previous E for energy loss calculations
				double Econs = 0;	// Energy conservation sum
				//		double pold = p; // previous momentum
				double F = -m*(p/delT); // will assume constant force, just for sanity's sake.
				double FOld = F;
				System.out.print("Initial conditions: E = " + K + " + " + V);
				System.out.println(" where F = " + F);
				int t = 0;
				int tcycles = (int)1e5; // The number of steps. Used for averaging.


				fout = new FileOutputStream("Ch1Q2OutputPower" + i + ".csv");
				fout2 = new FileOutputStream("Ch1Q2OutputPower" + i + "PS.csv");
				PrintStream ps = new PrintStream(fout);
				PrintStream ps2 = new PrintStream(fout2);
				while (t <= tcycles)
				{
					//p = p + ((delT)/2)*(F + FOld);
					//			System.out.println("delta P is: " + (p - pold));
					q = q + (p*delT)/m + F*(delT*delT)/(2*m);
				    FOld = F;
					F = -w*q; // calculate new force
					if (t % 100 == 0){
						System.out.print("Current set: p = " + p + " q = " + q + " F = " + F);
					}
					p = p + ((delT)/2)*(F + FOld);
					E = potential(w,q) + kinetic(m, p);
					if ((t % 100 == 0)&&(t>0)||(t == 5)||(t == 10)||(t == 60))
					{
						System.out.println(" t = " + t + " and new p = " + p);
						System.out.println("Energy conserved = " + (Econs/t));
						ps.println(t + "  "  + (Econs/t));	// prints to file
						ps2.println(q + "  " + p);
					}
					Econs += Math.abs((E - E0)/E0);
					//			pold = p;
					t++;
				}
					 

				ps.println(t + ","  + (Econs/tcycles));
				ps.close();
				ps2.close();
				fout.close();
				fout2.close();
			}
		}catch (IOException e){
			System.out.println("Could not open file.");
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
