package Ch1;
import java.io.*;
import java.math.*;

/* 	Ch1Q4.java - Computer exercises for Harmonic oscillator, part 4

	This is an exercise to build a function performing integration steps in "a separate routine."
 */

public class Ch1Q4 {

	private static final double delT = 1e-5;

	public static void main(String[] args)
	{
		FileOutputStream fout;	// Creating a CSV file
		//Ch1 ch = new Ch1();
		double g = 1; // g is the degrees of freedom
		double m = 0.25; // mass of the particle
		double w = 2; // oscillating frequency of the particle
		double q = 1; // initial position
		double V = potential(w, q);
		//	double p = momentum(m, kT/2); //initial momentum
		//	double p = Math.sqrt(kT/2*m);
		double p =0;
		double K = kinetic(m, p);
		double vel = Math.pow((2*K/m), 0.5);
		//			double E0 = K + V;
		//		double pold = p; // previous momentum
		double F = -m*(p/delT); // will assume constant initial force, just for sanity's sake.
		//			System.out.print("Initial conditions: E = " + K + " + " + V);
		//			System.out.println(" where F = " + F);
		integrate(q, vel, F, m);

	}


	// helper functions that calculates energies and momentum

	private static double kinetic(double m, double p)
	{
		double k = ((p*p)/(2*m));
		//		System.out.println("K: " + k);
		return k;
	}

	private static double potential(double w, double q)
	{
		double V = (w*(q*q/2));
		//		System.out.println("V: " + V);
		return V;
	}

	private static float momentum(double mass, double kinetic)
	{
		double rho = 2*kinetic / mass;
		rho = Math.pow(rho, 0.5);
		return (float)(rho * mass);
	}

	private static double force(double w, double q)
	//calculate the force
	{
		return -w * q;
	}

	private static double QuarticP(double p, double delT, double F, double q)
	//calculates the quartic perturbation
	{
		return p + (delT)*(F + F*q*q)/2;
	}

	private static double QuarticQ(double q, double p, double delT, double m)
	{
		return q + (p*delT)/m;
	}

	private static void integrate(double q, double v, double F, double m)
	{
		double delT = 1e-5;// Math.pow(10, -5); // timestep
		double kT = 4;
		int t = 0;
		int tcycles = (int)1e6; // The number of steps. Used for averaging.
		double p = 0;
		double w = 2; // oscillating frequency of the particle
		double E0 = kinetic(m, p) + potential(w, 1);
		double E = E0; // Storing the previous E for energy loss calculations
		double Econs = 0;	// Energy conservation sum
		try
		{
			FileOutputStream fout = new FileOutputStream("Ch1Q4Output.csv");
			PrintStream ps = new PrintStream(fout);  

			while (t <= tcycles)
			{
				p = QuarticP(p, delT, F, q);
				//			System.out.println("delta P is: " + (p - pold));
				q = QuarticQ(q, p, delT, m);
				F = force(w, q); // calculate new force
				if (t % 20 == 0){
					//				System.out.print("Current set: p = " + p + " q = " + q + " F = " + F);
				}
				p = QuarticP(p, delT, F, q);
				if ((t % 100 == 0)&&(t>0)||(t == 5)||(t == 10)||(t == 60))
				{
					//				System.out.println(" t = " + t + " and new p = " + p);
					//				System.out.println("Energy conserved = " + (Econs/t));
					ps.println(t + ","  + (Econs/t));	// prints to file

				}
				E = potential(w,q) + kinetic(m, p);
				Econs += Math.abs((E - E0)/E0);
				//			pold = p;
				t++;
			}
			ps.println(t + "," + (Econs/tcycles));

			ps.close();
			fout.close();
		}
		catch (IOException e)
		{
			System.out.println("Could not create file.");
		}
	}
}
