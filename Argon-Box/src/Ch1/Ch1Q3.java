package Ch1;
import java.math.*;
import java.io.*;

/* 	Ch1Q3.java - Computer exercises for Harmonic oscillator, part 3

	This code is meant to solve the problems in chapter 1 and part 1 of this course.
	In particular, this works on question 3, which adapts the previous code for the
	quartic perturbation method.
	
	Note on quartic perturbation: H = (p^2)/2m + (k*q^2)/2 + (kq^4)/4
	(dH/dp) = p/m
	dH/dq = kq + kq^3
	Going to use this with the Euler codebase, not the V-V. Euler works right now.
	q(N+1) = q(N) + dt(p/m)
	p(N+1) = p(N) - dt(wq + wq^3)
 */
public class Ch1Q3 {

	public static void main(String[] args)
	{
		FileOutputStream fout;	// Creating a CSV file
		//Ch1 ch = new Ch1();
		try
		{
			fout = new FileOutputStream("Ch1Q3Output.csv");
			PrintStream ps = new PrintStream(fout);  

			double g = 1; // g is the degrees of freedom
			double m = 0.25; // mass of the particle
			double w = 2; // oscillating frequency of the particle
			double q = 1; // initial position
			double delT = 3e-5;// Math.pow(10, -5); // timestep
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
			System.out.print("Initial conditions: E = " + K + " + " + V);
			System.out.println(" where F = " + F);
			int t = 0;
			int tcycles = (int)1e6; // The number of steps. Used for averaging.		
			while (t <= tcycles)
			{
				p = p + (delT)*(F + F*q*q)/2;
				//			System.out.println("delta P is: " + (p - pold));
				q = q + (p*delT)/m;
				F = -w*q; // calculate new force
				if (t % 20 == 0){
					System.out.print("Current set: p = " + p + " q = " + q + " F = " + F);
				}
				p = p + (F*delT)/2;
				if ((t % 100 == 0)&&(t>0)||(t == 5)||(t == 10)||(t == 60))
				{
					System.out.println(" t = " + t + " and new p = " + p);
					System.out.println("Energy conserved = " + (Econs/t));
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
		} catch (IOException e){
			System.out.println("Could not create file.");
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
