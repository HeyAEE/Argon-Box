package Ch3;

import java.io.*;

public class equilibrate {

	/**
	 * This is the biggie. Time to equilibrate the box!
	 */

	public static class RDFArray
	{
		public RDFArray(){}
		public double rpdr; //r plus dr
		public int counter; //counts the number
	}

	public static void main(String[] args) throws IOException
	{
		/* Parameters for args:
		 * args[0] - Timestep: The timestep in femtoseconds
		 * args[1] - Number of timesteps
		 * 
		 * How the program SHOULD work:
		 * Create new lattice w/ initial positions
		 * For each atom:
		 * -Multiply timestep by velocity to determine distance traveled.
		 * -Add to current positions
		 * -Calculate new forces using iterative method from intial lattice
		 * -Calculate new velocities
		 * 
		 * How to calculate velocities?
		 * F = m*a = m*(dv/dt) = (m/dt)*dv
		 * dv = (F/m)*dt
		 * So change in velocity is the force multiplied by the timestep, divided by the mass.
		 * 
		 * To calculate the velocities, calculate dv and add it to the current velocity. WARNING: This may be quite wrong.
		 * 
		 */
		if (args.length != 2)
		{
			System.out.println("Arguments entered incorrectly. They should be entered as follows:");
			System.out.println("args[0]: timestep in femtoseconds. args[1]: number of timesteps.");
			System.out.println("The program will now exit. Good bye!");
			return;
		}
		double timestep = Double.parseDouble(args[0]);
		double numsteps = Double.parseDouble(args[1]);
		double currentSteps = 0;
		double maxDist; // calculate max distance between atoms
		FileOutputStream f = new FileOutputStream("Ch3lattice.xyz");
		FileOutputStream f2 = new FileOutputStream("ForceAnalysis.txt");
		FileOutputStream f3 = new FileOutputStream("RDFCalcs.txt");
		PrintStream p = new PrintStream(f3);

		argonLattice lat = new argonLattice(f, f2);
		atom[] box = lat.getSystem();
		//get all the way down to the individual atom!
		/*		for (int x = 0; x < box.length; x++)
		{
			for (int y = 0; y < box[x].length; y++)
			{
				for (int z = 0; z < box[x][y].length; z++)
				{
					for (int atom = 0; atom < box[x][y][z].a.length; atom++)
		 */		
		for (int ts = 0; ts < numsteps; ts++ )
		{
			lat.ps.print("108\n\n");
			for	(int j = 0; j < box.length; j++)
			{
				double[] coords = box[j].getCoords();
				double[] vels = box[j].getVels();
				double[] forces = box[j].getForces();
				double dist, dv;
				for (int i = 0; i < vels.length; i++)
				{
					//first step: calculate new coordinates
					dist = vels[i]*timestep;
					dist += forces[i]*timestep*timestep/box[j].get("m"); //trying something new. Throwback to previous work.
					coords[i] += dist;
					while (Math.abs(coords[i]) > lat.sideLength) 
					{
						System.out.println("Dec: Atom " + j + " timestep " + ts + " coord " + i + " = " + coords[i]);
						//						coords[i] = coords[i] - lat.sideLength;
						coords[i] = coords[i] % lat.sideLength;
					}
					while (coords[i] < 0)
					{
						System.out.println("Inc: Atom " + j + " coord " + i + " = " + coords[i]);
						coords[i] += lat.sideLength;
					}
					//second step: calculate new velocities
					dv = forces[i]*timestep / box[j].get("m"); //dv = Fdt/m
					vels[i] += dv;
				}
				box[j].setCoords(coords); //sets new coordinates
				box[j].setVels(vels); //sets new velocities
				lat.ps.printf("Ar  %.5f %.5f %.5f\n", (coords[0]/0.52918), (coords[1]/0.52918), (coords[2]/0.52918));
/*				lat.ps2.println("Atom " + j + " now at (" + coords[0] + ", " + coords[1] + ", " + coords[2] + ") has force (" + forces[0] + ", " + forces[1] + ", " + forces[2] + ")");
				lat.ps2.println("Atom " + j + " now at (" + coords[0] + ", " + coords[1] + ", " + coords[2] + ") has velocities (" + vels[0] + ", " + vels[1] + ", " + vels[2] + ")");
				lat.ps2.println("Atom " + j + " now at (" + coords[0] + ", " + coords[1] + ", " + coords[2] + ") has force (" + forces[0] + ", " + forces[1] + ", " + forces[2] + ")");
*/
			}
			//third step: calculate new forces
			lat.calculateForce(); //calculates the new force and sets the current box back into the lattice structure
		}
		//hmm, let's just calculate RDF for the first atom of the thing.
		int maxRadInt = (int)Math.ceil(lat.maxRad());
		int minRadInt = (int)Math.ceil(lat.minRad());
		RDFArray[] rdf = new RDFArray[maxRadInt];
		for (int something = 0; something < maxRadInt; something++)
		{
			rdf[something] = new RDFArray();
		}
		atom a = box[0];
		double[] acoords = a.getCoords();
		double[] bcoords;
		int b;
		double radSum = 0;
		p.println("Max radius (in theory): " + lat.maxRad());
		for (b = 1; b < box.length; b++)
		{
			bcoords = box[b].getCoords();
			double[] netcoords = new double[3];
			for (int num = 0; num < 3; num++)
			{
				netcoords[num] = Math.pow(acoords[num] + bcoords[num], 2);
			}
			double radius = Math.sqrt((netcoords[0] + netcoords[1] + netcoords[2]));
			radSum += radius;
			p.print("Current radius: " + radius + " Current sum: " + radSum);
			p.println(" \nCurrent avg: " + radSum/b);
			p.println("----------");
			for (int rad = maxRadInt-1; rad > minRadInt; rad--) //dr is 1 length unit (1 Bohr)
			{
				/*				if (rdf[rad].rpdr == 0)
				{
					rdf[rad].rpdr = rad + lat.minRad();
				}
				if (radius >= rad)
				{
					rdf[rad].counter += 1;
					break;
				}
				 */			
				}
		}
		/*		p.println("Length is: " + b);
		p.print("RDF array: [");
		for (int i = 0; i < rdf.length - 1; i++)
		{
			p.print("{" + rdf[i].rpdr + ", " + rdf[i].counter + "},");
		}
		p.println("{" + rdf[i].rpdr + ", " + rdf[i].counter + "}]");
		 */		lat.closeFiles();
	}
}
