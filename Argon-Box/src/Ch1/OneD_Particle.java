package Ch1;
import java.io.*;
import java.math.*;

/* 	OneD_particle.java - Computer exercises for Harmonic oscillator, part 5

	This is a blueprint class. It was supposed to be named "1D_particle", but...
	sometimes Java is annoying like that.
 */
public class OneD_Particle {
	
	public OneD_Particle()
	{
	}

	private double delT = 1e-5;

	FileOutputStream fout;	// Creating a CSV file
	//Ch1 ch = new Ch1();
	private double dof = 1; // g is the degrees of freedom
	private double mp = 0.25; // mass of the particle
	private double wp = 2; // oscillating frequency of the particle
	private double qp = 1; // initial position
	private double Vp = potential(wp, qp);
	//	double p = momentum(m, kT/2); //initial momentum
	//	double p = Math.sqrt(kT/2*m);
	private double momp =0;
	private double Kp = kinetic(mp, momp);
	private double velp = Math.pow((2*Kp/mp), 0.5);
	//			double E0 = K + V;
	//		double pold = p; // previous momentum
	private double Fp = -mp*(momp/delT); // will assume constant initial force, just for sanity's sake.
	//			System.out.print("Initial conditions: E = " + K + " + " + V);
	//			System.out.println(" where F = " + F);


	// helper functions that calculates energies and momentum

	public double kinetic(double mass, double mom)
	{
		double k = ((mom*mom)/(2*mass));
		//		System.out.println("K: " + k);
		return k;
	}

	public double potential(double freq, double pos)
	{
		double V = (freq*(pos*pos/2));
		//		System.out.println("V: " + V);
		return V;
	}

	public float momentum(double mass, double kinetic)
	{
		double rho = 2*kinetic / mass;
		rho = Math.pow(rho, 0.5);
		return (float)(rho * mass);
	}

	public double force(double freq, double pos)
	//calculate the force
	{
		return -freq * pos;
	}

	public double QuarticP(double p, double delT, double F, double q)
	//calculates the quartic perturbation
	{
		return p + (delT)*(F + F*q*q)/2;
	}

	public double QuarticQ(double q, double p, double delT, double m)
	{
		return q + (p*delT)/m;
	}

	public void integrate(double q, double v, double F, double m)
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
