package Ch3;

public class atom {

	private String name, label; //name and label of atom
	private double m; //mass
	private double t; //temperature
	private double tth; //theoretical temp from velocities
	private double lj; //Lennard-Jones potential

	private double x, y, z; //Cartesian coordinates -> units of Bohr
	private double vx, vy, vz; //Cartesian velocities -> units of Bohr/atime
	private double fx, fy, fz; //Cartesian forces -> units of... something
	private BoxMuller b = new BoxMuller();

	public atom(String n, String l, double mass, double temp){
		m = mass;
		t = temp;
		name = n;
		label = l;
		vx = b.rand()[0]; 
		vy = b.rand()[0];
		vz = b.rand()[0];
		double KE = 0.5*m*(vx*vx + vy*vy + vz*vz);
		tth = (2*KE/3)*1.3806503e-23;
	}

	public atom(String n, double mass, double temp){
		this(n, "none", mass, temp);
		//		this(n, "none", mass);

	}

	public atom(double mass, double temp){
		this("Unnamed molecule", "none", mass, temp);
		//		this("Unnamed molecule", "none", mass);
	}

	public void set(String dim, double val)
	{
		if (dim.toLowerCase() == "x")
		{
			x = val;
		}
		else if (dim.toLowerCase() == "y")
		{
			y = val;
		}
		else if (dim.toLowerCase() == "z")
		{
			z = val;
		}
		else if (dim.toLowerCase() == "vx")
		{
			vx = val;
		}
		else if (dim.toLowerCase() == "vy")
		{
			vy = val;
		}
		else if (dim.toLowerCase() == "vz")
		{
			vz = val;
		}
		else if (dim.toLowerCase() == "fx")
		{
			fx = val;
		}
		else if (dim.toLowerCase() == "fy")
		{
			fy = val;
		}
		else if (dim.toLowerCase() == "fz")
		{
			fz = val;
		}
		else if (dim.toLowerCase() == "lj")
		{
			lj = val;
		}
		else
		{
			System.out.println("Variable " + dim.toLowerCase() + " does not exist.");
		}
	}

	public double get(String dim)
	{
		if (dim.toLowerCase() == "x")
		{
			return x;
		}
		else if (dim.toLowerCase() == "y")
		{
			return y;
		}
		else if (dim.toLowerCase() == "z")
		{
			return z;
		}
		else if (dim.toLowerCase() == "vx")
		{
			return vx;
		}
		else if (dim.toLowerCase() == "vy")
		{
			return vy;
		}
		else if (dim.toLowerCase() == "vz")
		{
			return vz;
		}
		else if (dim.toLowerCase() == "fx")
		{
			return fx;
		}
		else if (dim.toLowerCase() == "fy")
		{
			return fy;
		}
		else if (dim.toLowerCase() == "fz")
		{
			return fz;
		}
		else if (dim.toLowerCase() == "lj")
		{
			return lj;
		}
		else if (dim.toLowerCase() == "m")
		{
			return m;
		}
		else
		{
			System.out.println("Variable " + dim.toLowerCase() + " does not exist.");
			return -1;
		}
	}

	public void setCoords(double len, double wid, double hei){
		x = len;
		y = wid;
		z = hei;
	}
	
	public void setCoords(double[] arr){
		if (arr.length == 3)
		{
			x = arr[0];
			y = arr[1];
			z = arr[2];
		}
	}

	public double[] getCoords(){
		double[] Coords = {x, y, z};
		return Coords;
	}

	public double[] getForces(){
		double[] Forces = {fx, fy, fz};
		return Forces;
	}

	public void setForces(double len, double wid, double hei){
		fx = len;
		fy = wid;
		fz = hei;
	}

	public void setForces(double[] arr){
		if (arr.length == 3)
		{
			fx = arr[0];
			fy = arr[1];
			fz = arr[2];
		}
	}
	
	public double[] getVels(){
		double[] vels = {vx, vy, vz};
		return vels;
	}

	public void setVels(double len, double wid, double hei){
		vx = len;
		vy = wid;
		vz = hei;
	}
	
	public void setVels(double[] arr){
		if (arr.length == 3)
		{
			vx = arr[0];
			vy = arr[1];
			vz = arr[2];
		}
	}
}


