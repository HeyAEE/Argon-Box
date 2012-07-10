package Ch3;

import java.io.*;

public class argonLattice {

	private argonUnitLattice[][][] box = new argonUnitLattice[3][3][3];
	private atom[] system = new atom[108];
	private int systemCounter = 0; //global variable to count where in array system the atom is at.

	public final double sig = 6.43448; //Lennard-Jones sigma
	public final double temp = 9500447.5; //Atomic temp unit
	public final double eps = 119.8/315777;/* * 1.3806503e-23*/; //Lennard-Jones epsilon
	private double vlj = 0; //The defined location for Lennard-Jones calculation
	private final double boxSide = 5.405; // side of the unit lattice box.
//	public final double sideLength = 25.53479; //calculated max side length
	public final double sideLength = 3*boxSide; //calculated max side length

	private final double halfSide = boxSide/2;
	private double rij = 0, rij2 = 0; //Defined location for distance between atoms - rij2 leaves it unrooted
	private double maxRad = 0, minRad = 0;
	private FileOutputStream file1, file2;
	public PrintStream ps, ps2; //made public so they can be accessed from outside
	// Other units are not needed here b/c they were declared in unit lattice.
	/* After all that construction, it's finally time to create the full lattice.
	 * Assuming an argon lattice b/c I don't feel like generalizing yet.
	 * 
	 * Each side (the distance between origin atoms) is 7.99552251 Bohr, which
	 * I will simplify to 8 Bohr.
	 */
	//public static void main(String[] args) throws IOException

	public argonLattice(FileOutputStream f /*Lattice output file*/, FileOutputStream f2 /*Extra information file*/) throws IOException
	{
		//		FileOutputStream f = new FileOutputStream("Ch3lattice.xyz");
		//		FileOutputStream f2 = new FileOutputStream("ForceAnalysis.txt");
		file1 = f;
		file2 = f2;
		ps = new PrintStream(file1);
		ps2 = new PrintStream(file2);



		//box = new argonUnitLattice[3][3][3];
		for (int x = 0; x < 3; x++)
		{
			for (int y = 0; y < 3; y++)
			{
				for (int z = 0; z < 3; z++)
				{
					if (x == 0 && y == 0 && z == 0)
					{
						ps.println("108");
						ps.print("Comment here: Argon lattice");
						ps.println(" initial conditions");

					}
					box[x][y][z] = new argonUnitLattice(boxSide, boxSide*x, boxSide*y, boxSide*z);
					for (int i = 0; i < 4; i++){
						system[systemCounter] = box[x][y][z].a[i];
						double[] arr = system[systemCounter].getCoords();
						for (int l = 0; l < arr.length; l++)
						{
							arr[l] = arr[l]/0.52918;
						}
						system[systemCounter].setCoords(arr);
						ps.printf("Ar  %.5f %.5f %.5f\n", (arr[0]), (arr[1]), (arr[2]));
						systemCounter++;
						//The 2 in that case is the distance between atoms (8 Bohr^3 per lattice)
						//The above is actually wrong. It's 8 Bohr^3 per atom. Cube rooting that gives a cubic root of an atom. No good.
						// Instead, this means that, since each box contains four atoms, total volume should be 32 Bohr^3.
						// Cube-rooting that makes for sides of 3.17 Bohr 
					}
				}
			}
		}

		calculateForce(/*system*/);

		//The first atom for comparison
		/*		for (int x1 = 0; x1 < 3; x1++)
		{
			ps2.print("Atom 1: (" + x1 + ", ");
			for (int y1 = 0; y1 < 3; y1++)
			{
				ps2.print(y1 + ", ");
				for (int z1 = 0; z1 < 3; z1++)
				{
					ps2.println(z1 + ")");
					//					for (int atom1 = 0; atom1 < 3; atom1++){
					//The second atom for comparison
					for (int x2 = x1; x2 < 3; x2++)
					{
						ps2.print("Atom 2: (" + x2 + ", ");

						for (int y2 = y1; y2 < 3; y2++)
						{
							ps2.print(y2 + ", ");
							for (int z2 = z1; z2 < 3; z2++)
							{
								ps2.println(z2 + ")");
								//									for (int atom2 = 0; atom2 < 4; atom2++){
								argonUnitLattice a1 = box[x1][y1][z1]; //first unit lattice to use
								argonUnitLattice a2 = box[x2][y2][z2]; //second unit lattice to use
								for (int atom1 = 0; atom1 < 3; atom1++)
								{
									vlj = 0; //have to reset the LJ potential for the next atom.
									for (int atom2 = 0; atom2 < 4; atom2++)
									{
										double[] coord1 = a1.a[atom1].getCoords();
										double[] coord2 = a2.a[atom2].getCoords();
										rij2 = 0;
										for (int i = 0; i < coord1.length; i++)
										{
											rij2 += Math.pow(coord1[i]-coord2[i],2);
										}
										rij = Math.sqrt(Math.abs(rij2)); //The distance between the atoms - yeah, it took all that.
										if (rij > 0) {


											vlj += 4*eps*(Math.pow(sig/rij, 6) - Math.pow(sig/rij, 12));
											// now need to use dV/dr and dr/dx (or dr/dy or dr/dz) to determine the forces acting on each particle.
											// dV/dr => 24*eps*(2*sig^12 * r^-13 - sig^6 * r^-7)
											// dr/dx = delX/sqrt(delX^2 + delY^2 + delZ^2)
											// Therefore, F = 24*eps*(2*sig^12 * r^-13 - sig^6 * r^-7) * delX / r
											double[] Fatom1 = a1.a[atom1].getForces();
											double[] Fatom2 = a2.a[atom2].getForces();
											double coef = 24*eps*(2*Math.pow(sig, 12)/Math.pow(rij, 13) - Math.pow(sig, 6)/Math.pow(rij, 7))/rij;
											ps2.println("Rij is: " + rij);
											for (int j = 0; j < Fatom1.length; j++)
											{
												Fatom1[j] += coef*(coord2[j]-coord1[j]); //Force of atom 2 on atom 1 in each direction
												Fatom2[j] += coef*(coord1[j]-coord2[j]); //Force of atom 1 on atom 2 in each direction.
											}
											//										ps2.println("Force between atoms is (" + Fatom1[0] + ", " + Fatom1[1] + ", " + Fatom1[2] + ")");
											a1.a[atom1].setForces(Fatom1);
											a2.a[atom2].setForces(Fatom2); // set the forces for each atom
											ps2.println("Atom " + atom1 + " at (" + x1 + ", " + y1 + ", " + z1 + ") has force (" + Fatom1[0] + ", " + Fatom1[1] + ", " + Fatom1[2] + ")");
											ps2.println("Atom " + atom2 + " at (" + x2 + ", " + y2 + ", " + z2 + ") has force (" + Fatom2[0] + ", " + Fatom2[1] + ", " + Fatom2[2] + ")");

										}
									}
								}
							}
						}
					}
				}
			}

		}
		ps.close();
		f.close();
		 */	
	}

	public argonUnitLattice[][][] box()
	{
		//Simple method to return the box.
		return box;

	}

	public atom[] getSystem()
	{
		//simple method to return the system
		return system;
	}

	public void setBox(argonUnitLattice[][][] b)
	{
		box = b;
	}

	public void calculateForce(/*atom[] b*/) throws IOException
	{
		// calculateForce() was originally part of the constructor method.
		// It has now been moved to its own method so it can be reused outside of the constructor.
		// That is to say, it will be called each time forces need to be calculated.
		// It uses FileOutputStream and PrintStream created in the constructor which are global variables.


		//The first atom for comparison
		/*		for (int x1 = 0; x1 < 3; x1++)
		{
			ps2.print("Atom 1: (" + x1 + ", ");
			for (int y1 = 0; y1 < 3; y1++)
			{
				ps2.print(y1 + ", ");
				for (int z1 = 0; z1 < 3; z1++)
				{
					ps2.println(z1 + ")");
		 */				
		for (int a1 = 0; a1 < 108; a1++)
		{

			//					for (int atom1 = 0; atom1 < 3; atom1++){
			//The second atom for comparison
			/*		 for (int x2 = x1; x2 < 3; x2++)
		 {
			 ps2.print("Atom 2: (" + x2 + ", ");

			 for (int y2 = y1; y2 < 3; y2++)
			 {
				 ps2.print(y2 + ", ");
				 for (int z2 = z1; z2 < 3; z2++)
				 {
					 ps2.println(z2 + ")");
					 //									for (int atom2 = 0; atom2 < 4; atom2++){
			 */			
			atom atom1 = system[a1]; //first atom
			double Fatom1[] = atom1.getForces();
			
			for (int a2 = a1; a2 < 108; a2++)
			{
//				ps2.println("Atom 1: " + a1);
//				ps2.println("Atom 2: " + a2);


				atom atom2 = system[a2]; //second atom
				double Fatom2[];

				//for 5/1 --> adjust program to use individual atoms, not lattices
				//also loop the atoms around. Periodic boundary conditions means that,
				//if the atom is more than half the lattices away, it is actually less than half in the other direction. 
				//				for (int atomNum1 = 0; atomNum1 < 3; atomNum1++)
				//				{
				vlj = 0; //have to reset the LJ potential for the next atom.
				/*					for (int atomNum2 = 0; atomNum2 < 4; atomNum2++)
					{
						double[] coord1 = system[atomNum1].getCoords();
						double[] coord2 = system[atomNum2].getCoords();
				 */		
				double[] coord1 = atom1.getCoords();
				double[] coord2 = atom2.getCoords();
				double del;
				rij2 = 0;
				for (int i = 0; i < coord1.length; i++)
				{
					del = coord2[i]-coord1[i];
					while (del > halfSide)
					{
//						ps2.println("del is greater than halfSide.");
//						ps2.print("Original del: " + del);
						coord2[i] -= sideLength;
						del = (coord2[i]) - coord1[i];
//						ps2.println(" New del: " + del);
					}
					rij2 += Math.pow(del,2);
				}
				rij = Math.sqrt(Math.abs(rij2)); //The distance between the atoms - yeah, it took all that.
//				ps2.println("Dist between " + a1 + " and " + a2 + "is: " + rij);
				if (rij > maxRad) // find max radius of system
				{
					maxRad = rij;
				}
				else if (rij < minRad) // find min radius of system
				{
					minRad = rij;					
				}
				

				Fatom1 = atom1.getForces();
				Fatom2 = atom2.getForces();
				if (rij2 > 0) {


					vlj += 4*eps*(Math.pow(sig/rij, 6) - Math.pow(sig/rij, 12));
					// now need to use dV/dr and dr/dx (or dr/dy or dr/dz) to determine the forces acting on each particle.
					// dV/dr => 24*eps*(2*sig^12 * r^-13 - sig^6 * r^-7)
					// dr/dx = delX/sqrt(delX^2 + delY^2 + delZ^2)
					// Therefore, F = 24*eps*(2*sig^12 * r^-13 - sig^6 * r^-7) * delX / r
					double coef = 24*eps*(2*Math.pow(sig, 12)/Math.pow(rij, 13) - Math.pow(sig, 6)/Math.pow(rij, 7))/rij;
//					ps2.println("Rij is: " + rij);
					for (int j = 0; j < Fatom1.length; j++)
					{
						Fatom1[j] += coef*(coord2[j]-coord1[j]); //Force of atom 2 on atom 1 in each direction
						Fatom2[j] += coef*(coord1[j]-coord2[j]); //Force of atom 1 on atom 2 in each direction.
					}
					//										ps2.println("Force between atoms is (" + Fatom1[0] + ", " + Fatom1[1] + ", " + Fatom1[2] + ")");
					atom1.setForces(Fatom1);
					atom2.setForces(Fatom2); // set the forces for each atom
				}
//				ps2.println("Atom " + a1 + /*" at (" + x1 + ", " + y1 + ", " + z1 + */ " has force (" + Fatom1[0] + ", " + Fatom1[1] + ", " + Fatom1[2] + ")");
//				ps2.println("Atom " + a2 + /* " at (" + x2 + ", " + y2 + ", " + z2 + */ " has force (" + Fatom2[0] + ", " + Fatom2[1] + ", " + Fatom2[2] + ")");

			}
			ps2.println("Atom " + a1 + /*" at (" + x1 + ", " + y1 + ", " + z1 + */ " has force (" + Fatom1[0] + ", " + Fatom1[1] + ", " + Fatom1[2] + ")");
		}





//		system = b;
		//		ps.close();
		//		file1.close();
	}

	/*	public void calculateForce(argonUnitLattice[][][] b) throws IOException
	{
		// calculateForce() was originally part of the constructor method.
		// It has now been moved to its own method so it can be reused outside of the constructor.
		// That is to say, it will be called each time forces need to be calculated.
		// It uses FileOutputStream and PrintStream created in the constructor which are global variables.


		//The first atom for comparison
		/*		for (int x1 = 0; x1 < 3; x1++)
		{
			ps2.print("Atom 1: (" + x1 + ", ");
			for (int y1 = 0; y1 < 3; y1++)
			{
				ps2.print(y1 + ", ");
				for (int z1 = 0; z1 < 3; z1++)
				{
					ps2.println(z1 + ")");
		 /				
		for (int a1 = 0; a1 < 108; a1++)
		{

			//					for (int atom1 = 0; atom1 < 3; atom1++){
			//The second atom for comparison
			/*		 for (int x2 = x1; x2 < 3; x2++)
		 {
			 ps2.print("Atom 2: (" + x2 + ", ");

			 for (int y2 = y1; y2 < 3; y2++)
			 {
				 ps2.print(y2 + ", ");
				 for (int z2 = z1; z2 < 3; z2++)
				 {
					 ps2.println(z2 + ")");
					 //									for (int atom2 = 0; atom2 < 4; atom2++){
			 /				
			for (int a2 = a1; a2 < 108; a2++)
			{
				ps2.println("Atom 1: " + a1);
				ps2.println("Atom 2: " + a2);


				atom atom1 = system[a1]; //first atom
				atom atom2 = system[a2]; //second atom

				//for 5/1 --> adjust program to use individual atoms, not lattices
				//also loop the atoms around. Periodic boundary conditions means that,
				//if the atom is more than half the lattices away, it is actually less than half in the other direction. 
				//				for (int atomNum1 = 0; atomNum1 < 3; atomNum1++)
				//				{
				vlj = 0; //have to reset the LJ potential for the next atom.
				/*					for (int atomNum2 = 0; atomNum2 < 4; atomNum2++)
					{
						double[] coord1 = system[atomNum1].getCoords();
						double[] coord2 = system[atomNum2].getCoords();
				 /		
				double[] coord1 = atom1.getCoords();
				double[] coord2 = atom2.getCoords();
				rij2 = 0;
				for (int i = 0; i < coord1.length; i++)
				{
					rij2 += Math.pow(coord1[i]-coord2[i],2);
				}
				rij = Math.sqrt(Math.abs(rij2)); //The distance between the atoms - yeah, it took all that.

				double[] Fatom1 = atom1.getForces();
				double[] Fatom2 = atom2.getForces();
				if (rij2 > 0) {


					vlj += 4*eps*(Math.pow(sig/rij, 6) - Math.pow(sig/rij, 12));
					// now need to use dV/dr and dr/dx (or dr/dy or dr/dz) to determine the forces acting on each particle.
					// dV/dr => 24*eps*(2*sig^12 * r^-13 - sig^6 * r^-7)
					// dr/dx = delX/sqrt(delX^2 + delY^2 + delZ^2)
					// Therefore, F = 24*eps*(2*sig^12 * r^-13 - sig^6 * r^-7) * delX / r
					double coef = 24*eps*(2*Math.pow(sig, 12)/Math.pow(rij, 13) - Math.pow(sig, 6)/Math.pow(rij, 7))/rij;
					ps2.println("Rij is: " + rij);
					for (int j = 0; j < Fatom1.length; j++)
					{
						Fatom1[j] += coef*(coord2[j]-coord1[j]); //Force of atom 2 on atom 1 in each direction
						Fatom2[j] += coef*(coord1[j]-coord2[j]); //Force of atom 1 on atom 2 in each direction.
					}
					//										ps2.println("Force between atoms is (" + Fatom1[0] + ", " + Fatom1[1] + ", " + Fatom1[2] + ")");
					atom1.setForces(Fatom1);
					atom2.setForces(Fatom2); // set the forces for each atom
				}
				//ps2.println("Atom " + a1 + /*" at (" + x1 + ", " + y1 + ", " + z1 + / " has force (" + Fatom1[0] + ", " + Fatom1[1] + ", " + Fatom1[2] + ")");
				//ps2.println("Atom " + a2 + /* " at (" + x2 + ", " + y2 + ", " + z2 + / " has force (" + Fatom2[0] + ", " + Fatom2[1] + ", " + Fatom2[2] + ")");

			}
		}





		box = b;
		//		ps.close();
		//		file1.close();
	}
	 */	


	public double maxRad() //returns the max radius
	{
		return maxRad;
	}


	public double minRad() //returns the max radius
	{
		return minRad;
	}
	
	public void closeFiles() throws IOException
	{
		ps.close();
		ps2.close();
		file1.close();
		file2.close();
	}

}
