package Ch3;

public class argonUnitLattice {
	
	// Blueprint class for the argon unit lattice. One input is "side", the length
	// of a side of the box. This is dictated by the density.
	// The other inputs are the origin of the box. As it is multiplied, the origin
	// will change so the individual atoms will have distance from each other.
	
	atom a[] = new atom[4];
	double mass = 39.938;
	double temp = 9500447.5;

	public argonUnitLattice(double side, double ox, double oy, double oz)
	{
		/* The four atoms in the unit lattice:
		 * a[0]: the bottom left corner atom (origin atom)
		 * a[1]: the xy face atom
		 * a[2]: the xz face atom
		 * a[3]: the yz face atom
		 */
		for (int i = 0; i < 4; i++){
			a[i] = new atom("Argon", mass, temp);
		}
		a[0].setCoords(ox, oy, oz);
		a[1].setCoords(ox + side/2, oy + side/2, oz);
		a[2].setCoords(ox + side/2, oy, oz + side/2);
		a[3].setCoords(ox, oy + side/2, oz + side/2);
	}
	
	public boolean isNull(argonUnitLattice a)
	{
		return (a == null);
	}

}
