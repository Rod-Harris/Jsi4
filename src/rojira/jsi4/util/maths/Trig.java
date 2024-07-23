package rojira.jsi4.util.maths;


public class Trig
{
	public static final double PI = Math.PI;

	public static final double RAD_TO_DEG = 180.0 / PI;

	public static final double DEG_TO_RAD = PI / 180.0;

	private double angle_in_mult = 1.0;

	private double angle_out_mult = 1.0;

	private boolean locked;


	/**
	 * <p>Creates an object for doing trigonometry calculations
	 * <p>The units can be changed at runtime by calling the degrees() or radians() methods, if the Trig object is mutable
	 * <p>The Statics class has immutable public static instances of both a degrees Trig and a radians Trig by default and they're thread safe so there's probably not much reason to create new ones of these
	 */
	public Trig()
	{
	}


	/**
	 * <p>Creates an object for doing trigonometry calculations assuming input and output units are degrees
	 * <p>The Statics class has an immutable public static instance of one of these by default and they're thread safe so there's probably not much reason to create new ones of these
	 */
	public static Trig create_immutable_degrees_trig()
	{
		Trig trig = new Trig();

		trig.degrees();

		trig.locked = true;

		return trig;
	}


	/**
	 * <p>Creates an object for doing trigonometry calculations assuming input and output units are radians
	 * <p>The Statics class has an immutable public static instance of one of these by default and they're thread safe so there's probably not much reason to create new ones of these
	 */
	public static Trig create_immutable_radians_trig()
	{
		Trig trig = new Trig();

		trig.radians();

		trig.locked = true;

		return trig;
	}


	/**
	 * <p>use radians - all calls to this Trig object's functions and returned values are now assumed to be in radians
	 *
	 * @throws IllegalStateException if this Trig object is immutable
	*/
	public Trig radians()
	{
		if( locked ) throw new IllegalStateException( "This Trig instance is immutable" );

		angle_in_mult = 1;

		angle_out_mult = 1;

		return this;
	}

	/**
	 * <p>use degrees - all calls to this Trig object's functions and returned values are now assumed to be in degrees
	 *
	 * @throws IllegalStateException if this Trig object is immutable
	*/
	public Trig degrees()
	{
		if( locked ) throw new IllegalStateException( "This Trig instance is immutable" );

		angle_in_mult = DEG_TO_RAD;

		angle_out_mult = RAD_TO_DEG;

		return this;
	}


	private double angle_in( double angle )
	{
		return angle_in_mult * angle;
	}


	private double angle_out( double angle )
	{
		return angle_out_mult * angle;
	}


	/**
	 * <p>Convert the given angle in degrees to radians
	 */
	public static double radians( double degrees )
	{
		return degrees * DEG_TO_RAD;
	}


	/**
	 * <p>Convert the given angle in radians to degrees
	 */
	public static double degrees( double radians )
	{
		return radians * RAD_TO_DEG;
	}


	public double cos( double angle )
	{
		return Math.cos( angle_in( angle ) );
	}


	public double sin( double angle )
	{
		return Math.sin( angle_in( angle ) );
	}


	public double tan( double angle )
	{
		return Math.tan( angle_in( angle ) );
	}


	public double atan( double v )
	{
		return angle_out( Math.atan( v ) );
	}


	public double acos( double v )
	{
		return angle_out( Math.acos( v ) );
	}


	public double asin( double v )
	{
		return angle_out( Math.asin( v ) );
	}


	/**
	 * <p>Creates a new Vec3d and returns the conversion
	 */
	public Vec3d cylindrical_to_cartesian( double r, double theta, double h )
	{
		Vec3d result = new Vec3d();

		cylindrical_to_cartesian( r, theta, h, result );

		return result;
	}


	/**
	 * <p>Returns the conversion in the given Vec3d
	 */
	public void cylindrical_to_cartesian( double r, double theta, double h, Vec3d result )
	{
		double cos_theta = cos( theta );

		double sin_theta = sin( theta );

		result.x = r * cos_theta;
		result.y = h;
		result.z = r * sin_theta;
	}


	/**
	 * <p>Creates a new Vec3d and returns the conversion
	 */
	 public Vec3d polar_to_cartesian( double r, double theta, double phi )
	{
		Vec3d result = new Vec3d();

		polar_to_cartesian( r, theta, phi, result );

		return result;
	}


	/**
	 * <p>Returns the conversion in the given Vec3d
	 */
	public void polar_to_cartesian( double r, double theta, double phi, Vec3d result )
	{
		double cos_theta = cos( theta );
		double cos_phi = cos( phi );

		double sin_theta = sin( theta );
		double sin_phi = sin( phi );

		result.x = r * cos_theta * cos_phi;
		result.y = r * sin_phi;
		result.z = r * sin_theta * cos_phi;
	}

	/**
	 * <p>Rotate a point p by angle theta around an arbitrary axis r
	 * <p>Positive angles are anticlockwise looking down the axis towards the origin.
	 * <p>Assume right hand coordinate system.
	 *
	 * @param p the point to be rotated
	 * @param theta the angle
	 * @param r the axis of rotation
	 * @return a new rotated point
	*/
	public Vec3d rotate( Vec3d p, double theta, Vec3d r )
	{
		Vec3d q = new Vec3d();

		r.normalise();

		double costheta = cos( theta );
		double sintheta = sin( theta );

		q.x += ( costheta + ( 1 - costheta ) * r.x * r.x ) * p.x;
		q.x += ( ( 1 - costheta ) * r.x * r.y - r.z * sintheta ) * p.y;
		q.x += ( ( 1 - costheta ) * r.x * r.z + r.y * sintheta ) * p.z;

		q.y += ( ( 1 - costheta ) * r.x * r.y + r.z * sintheta ) * p.x;
		q.y += ( costheta + ( 1 - costheta ) * r.y * r.y ) * p.y;
		q.y += ( ( 1 - costheta ) * r.y * r.z - r.x * sintheta ) * p.z;

		q.z += ( ( 1 - costheta ) * r.x * r.z - r.y * sintheta ) * p.x;
		q.z += ( ( 1 - costheta ) * r.y * r.z + r.x * sintheta ) * p.y;
		q.z += ( costheta + ( 1 - costheta ) * r.z * r.z ) * p.z;

		return q;
	}


	/**
	 * <p>Rotate a point p by angle theta around an arbitrary axis r
	 * <p>Positive angles are anticlockwise looking down the axis towards the origin.
	 * <p>Assume right hand coordinate system.
	 *
	 * @param p the point to be rotated
	 * @param theta the angle
	 * @param r the axis of rotation
	 * @param q the new rotated point
	 * @return q
	*/
	public Vec3d rotate( Vec3d p, double theta, Vec3d r, Vec3d q )
	{
		q.zero();
		
		r.normalise();

		double costheta = cos( theta );
		double sintheta = sin( theta );

		q.x += ( costheta + ( 1 - costheta ) * r.x * r.x ) * p.x;
		q.x += ( ( 1 - costheta ) * r.x * r.y - r.z * sintheta ) * p.y;
		q.x += ( ( 1 - costheta ) * r.x * r.z + r.y * sintheta ) * p.z;

		q.y += ( ( 1 - costheta ) * r.x * r.y + r.z * sintheta ) * p.x;
		q.y += ( costheta + ( 1 - costheta ) * r.y * r.y ) * p.y;
		q.y += ( ( 1 - costheta ) * r.y * r.z - r.x * sintheta ) * p.z;

		q.z += ( ( 1 - costheta ) * r.x * r.z - r.y * sintheta ) * p.x;
		q.z += ( ( 1 - costheta ) * r.y * r.z + r.x * sintheta ) * p.y;
		q.z += ( costheta + ( 1 - costheta ) * r.z * r.z ) * p.z;

		return q;
	}

	/**
	 * <p>The angle between 2 points (rays) given in right-ascension, declination corrdinates
	 */
	public double angle( double ra1, double dec1, double ra2, double dec2 )
	{
		Vec3d v1 = polar_to_cartesian( 1, ra1, dec1 );

		Vec3d v2 = polar_to_cartesian( 1, ra2, dec2 );

		return angle( v1, v2 );
	}


	/**
	 * <p>Returns the angle between the 2 given vectors
	 * <p>The 2 input vectors are normalised in this process
	 */
	public double angle( Vec3d v1, Vec3d v2 )
	{
		v1.normalise();
		
		v2.normalise();
		
		double dot = rojira.jsi4.LibMaths.dot( v1, v2 );
		
		return angle_out( acos( dot ) );
	}
}
