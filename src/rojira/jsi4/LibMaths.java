package rojira.jsi4;

import static rojira.jsi4.LibSystem._double;
import static rojira.jsi4.LibSystem.check_arg;

import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import rojira.jsi4.util.maths.Trig;
import rojira.jsi4.util.maths.Vec3d;


public class LibMaths
{
	private static final Random rnd = new Random();

	public static final Trig deg = Trig.create_immutable_degrees_trig();

	public static final Trig rad = Trig.create_immutable_radians_trig();

	/**
	 * 1, 0, 0
	 */
	public static final Vec3d xi = new Vec3d( 1, 0, 0 );

	/**
	 * 0, 1, 0
	 */
	public static final Vec3d yi = new Vec3d( 0, 1, 0 );

	/**
	 * 0, 0, 1
	 */
	public static final Vec3d zi = new Vec3d( 0, 0, 1 );

	/**
	 * 0, 0, 0
	 */
	public static final Vec3d O = new Vec3d( 0, 0, 0 );
	
	
	private LibMaths(){}


	// random functions

	/**
	 * implementation should be self-evident
	 */
	public static int rnd()
	{
		return rnd.nextInt();
	}

	/**
	 * implementation should be self-evident
	 */
	public static int rnd( int max )
	{
		return rnd.nextInt( max );
	}


	/**
	 * implementation should be self-evident
	 */
	public static int rnd( int min, int max )
	{
		return rnd.nextInt( max - min ) + min;
	}


	/**
	 * implementation should be self-evident
	 */
	public static float rndf( float max )
	{
		return rndf( 0.0f, max );
	}


	/**
	 * implementation should be self-evident
	 */
	public static float rndf( float min, float max )
	{
		return rnd.nextFloat() * ( max - min ) + min;
	}


	/**
	 * implementation should be self-evident
	 */
	public static double rndd( double max )
	{
		return rndd( 0.0, max );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double rndd( double min, double max )
	{
		return rnd.nextDouble() * ( max - min ) + min;
	}


	/**
	 * implementation should be self-evident
	 */
	public static long rndl( long max )
	{
		return rndl( 0, max );
	}


	/**
	 * implementation should be self-evident
	 */
	public static long rndl( long min, long max )
	{
		return rnd.nextLong() * ( max - min ) + min;
	}


	/**
	 * implementation should be self-evident
	 */
	public static boolean rndb()
	{
		return rndiv( 0, 1 ) == 1;
	}


	/**
	choose randomly one of the specified values
	*/
	public static double rndfv( float... vals )
	{
		return vals[ rnd.nextInt( vals.length ) ];
	}


	/**
	choose randomly one of the specified values
	*/
	public static double rnddv( double... vals )
	{
		return vals[ rnd.nextInt( vals.length ) ];
	}


	/**
	choose randomly one of the specified values
	*/
	public static int rndiv( int... vals )
	{
		return vals[ rnd.nextInt( vals.length ) ];
	}


	/**
	 * implementation should be self-evident
	 */
	public static void set_seed( long seed )
	{
		rnd.setSeed( seed );
	}


	/**
	 * returns a hex string representing a random 32bit integer
	 */
	 public static String rnd_hex()
	 {
		 return String.format( "%x", rnd.nextInt() );
	 }

	// transformations


	/**
	 * implementation should be self-evident
	 */
	public static double abs( double v )
	{
		return Math.abs( v );
	}


	/**
	 * implementation should be self-evident
	 */
	public static int abs( int v )
	{
		return (int) Math.abs( v );
	}


	/**
	 * returns a value that has the same relation to y0 and y1 as x does to x0 and x1 respectively
	 */
	public static double pack( double x, double x0, double x1, double y0, double y1 )
	{
		if( x1 - x0 == 0 ) return y0;

		return ( x - x0 ) * ( y1 - y0 ) / ( x1 - x0 ) + y0;
	}


	/**
	 * implementation should be self-evident
	 */
	public static double ln( double x )
	{
		return Math.log( x );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double log( double base, double x )
	{
		return ln( x ) / ln( base );
	}


	/**
	 * implementation should be self-evident
	 */
	public static boolean isPowOf2( double x )
	{
		return ( log( 2, x ) % 1 ) == 0;
	}


	/**
	 * implementation should be self-evident
	 */
	public static int min( int i1, int i2 )
	{
		return Math.min( i1, i2 );
	}


	/**
	 * implementation should be self-evident
	 */
	public static int max( int i1, int i2 )
	{
		return Math.max( i1, i2 );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double max( double i1, double i2 )
	{
		return Math.max( i1, i2 );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double pow( double v, double e )
	{
		return Math.pow( v, e );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double clamp( double v, double min, double max )
	{
		if( v < min ) return min;

		if( v > max ) return max;

		return v;
	}


	/**
	 * implementation should be self-evident
	 */
	public static int clamp( int v, int min, int max )
	{
		if( v < min ) return min;

		if( v > max ) return max;

		return v;
	}


	/**
	 * implementation should be self-evident
	 */
	public static double avg( double... vals )
	{
		double d = 0;

		for( double v : vals ) d += v;

		return d / vals.length;
	}


	/**
	 * adds i to v
	 * but if the resulting value is outside min -> max
	 * wraps the result
	 */
	public static int add( int v, int i, int min, int max )
	{
		v += i;

		v -= min;

		v = v % ( max - min );

		v += min;

		if( v < min ) v += max - min;

		return v;
	}


	/**
	 * round v to the nearest integer
	 */
	public static int rint( double v )
	{
		return ( int ) Math.round( v );
	}
	
	public static int floor( double d )
	{
		return (int) Math.floor( d );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double sqrt( double v )
	{
		return Math.sqrt( v );
	}




/*
	// Trig - Use the Trig class

	public static void radians()
	{
		angle_in_mult = 1;

		angle_out_mult = 1;
	}

	public static void degrees()
	{
		angle_in_mult = DEG_TO_RAD;

		angle_out_mult = RAD_TO_DEG;
	}


	private static double angle_in( double angle )
	{
		return angle_in_mult * angle;
	}


	private static double angle_out( double angle )
	{
		return angle_out_mult * angle;
	}


	public static double radians( double degrees )
	{
		return degrees * DEG_TO_RAD;
	}


	public static double degrees( double radians )
	{
		return radians * RAD_TO_DEG;
	}


	public static double cos( double angle )
	{
		return Math.cos( angle_in( angle ) );
	}


	public static double sin( double angle )
	{
		return Math.sin( angle_in( angle ) );
	}


	public static double tan( double angle )
	{
		return Math.tan( angle_in( angle ) );
	}


	public static double atan( double v )
	{
		return angle_out( Math.atan( v ) );
	}
*/


	/*
	 * Vectors, pythagoras, etc
	 */


	/**
	 * implementation should be self-evident
	 */
	public static double dist( Vec3d v0, Vec3d v1 )
	{
		return dist( v0.x, v0.y, v0.z, v1.x, v1.y, v1.z );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double dist( double x1, double y1, double z1, double x2, double y2, double z2 )
	{
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		return sqrt( dx * dx + dy * dy + dz * dz );
	}


	/**
	 * 2D distance
	 */
	public static double dist( double x1, double y1, double x2, double y2 )
	{
		double dx = x2 - x1;
		double dy = y2 - y1;

		return sqrt( dx * dx + dy * dy );
	}


	/**
	 * wraps a double to a value between the specified min and max ( max is not reached )
	*/
	public static double wrap( double d, double min, double max )
	{
		d -= min;

		d = d % ( max - min );

		if( d < 0 )
		{
			d += max;
		}
		else
		{
			d += min;
		}

		return d;
	}


	/**
	 * wraps a int to a value between the specified min and max ( max is not reached )
	*/
	public static int wrap( int d, int min, int max )
	{
		d -= min;

		d = d % ( max - min );

		if( d < 0 )
		{
			d += max;
		}
		else
		{
			d += min;
		}

		return d;
	}



	/*
	 * Bit operations
	 */

	/**
	 * returns 0x000000(b3)
	 */
	public static int i1( byte b3 )
	{
		return ( b3 & 0xff ) << 0;
	}


	/**
	 * returns 0x0000(b2)(b3)
	 */
	public static int i2( byte b2, byte b3 )
	{
		return ( b2 & 0xff ) << 8 | ( b3 & 0xff ) << 0;
	}


	/**
	 * returns 0x(b0)(b1)(b2)(b3)
	 */
	public static int i4( byte b0, byte b1, byte b2, byte b3 )
	{
		return ( b0 & 0xff ) << 24 | ( b1 & 0xff ) << 16 | ( b2 & 0xff ) << 8 | ( b3 & 0xff ) << 0;
	}


	/**
	 * converts 0x(b0)(b1)(b2)(b3) to 0x000000(b0) and returns 0x(b0)
	 */
	public static byte b0( int i )
	{
		return ( byte ) ( ( i >> 24 ) & 0xff );
	}


	/**
	 * converts 0x(b0)(b1)(b2)(b3) to 0x000000(b1) and returns 0x(b1)
	 */
	public static byte b1( int i )
	{
		return ( byte ) ( ( i >> 16 ) & 0xff );
	}


	/**
	 * converts 0x(b0)(b1)(b2)(b3) to 0x000000(b2) and returns 0x(b2)
	 */
	public static byte b2( int i )
	{
		return ( byte ) ( ( i >> 8 ) & 0xff );
	}


	/**
	 * converts 0x(b0)(b1)(b2)(b3) to 0x000000(b3) and returns 0x(b3)
	 */
	public static byte b3( int i )
	{
		return ( byte ) ( ( i >> 0 ) & 0xff );
	}



	/*
	 * Other
	 */

	/**
	 * t( 0 ) = a0
	 * t( 1 ) = a1
	 * values are not clamped to a0 and a1
	 */
	public static double interpolate( double a0, double a1, double t )
	{
		return ( a1 - a0 ) * t + a0;
	}


	/**
	 * t( 0 ) = v0
	 * t( 1 ) = v1
	 * values are not clamped to v0 and v1
	 * values are returned in the vector r
	 */
	public static void interpolate( Vec3d v0, Vec3d v1, double t, Vec3d r )
	{
		r.x = interpolate( v0.x, v1.x, t );

		r.y = interpolate( v0.y, v1.y, t );

		r.z = interpolate( v0.z, v1.z, t );
	}




	/**
	 * implementation should be self-evident
	 */
	public static double distance( Vec3d p0, Vec3d p1 )
	{
		double dx = p1.x - p0.x;
		double dy = p1.y - p0.y;
		double dz = p1.z - p0.z;

		return sqrt( dx * dx + dy * dy + dz * dz );
	}


	/**
	 * a is the point in question.
	 * n is the normal of the plane
	 * b is a point on the plane
	*/
	public static double distance_between_point_and_plane( Vec3d a, Vec3d n, Vec3d b )
	{
		return ( ( n.x * ( a.x - b.x ) + n.y * ( a.y - b.y ) + n.z * ( a.z - b.z ) ) / sqrt( n.x * n.x + n.y * n.y + n.z * n.z ) );
	}


	/**
	 * returns -1 if f < 0
	 * else
	 * returns 1
	 */
	public static double sign( double f )
	{
		if( f < 0 ) return -1;

		return 1;
	}


	/**
	 * returns -1 if f > 0
	 * else
	 * returns 1
	 */
	public static double isign( double f )
	{
		if( f > 0 ) return -1;

		return 1;
	}


	/**
	 * implementation should be self-evident
	 * returns a new vector
	 */
	public static Vec3d add( Vec3d a, Vec3d b )
	{
		return new Vec3d( a.x + b.x, a.y + b.y, a.z + b.z );
	}


	/**
	 * implementation should be self-evident
	 * returns a new vector
	 */
	public static Vec3d sub( Vec3d a, Vec3d b )
	{
		return new Vec3d( a.x - b.x, a.y - b.y, a.z - b.z );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double length( Vec3d v )
	{
		return sqrt( v.x * v.x + v.y * v.y + v.z * v.z );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double length( double x, double y, double z )
	{
		return sqrt( x * x + y * y + z * z );
	}



	/**
	 * returns a new vector that is a scaled version of the parameter
	 */
	public static Vec3d scale( double f, Vec3d v )
	{
		return new Vec3d( f * v.x, f * v.y, f * v.z );
	}


	/**
	 * implementation should be self-evident
	 */
	public static double dot( Vec3d v1, Vec3d v2 )
	{
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}


	/**
	 * returns a new vector that is the cross product of the of the parameters
	 */
	public static Vec3d cross( Vec3d v1, Vec3d v2 )
	{
		Vec3d v = new Vec3d();

		v.x = v1.y * v2.z - v1.z * v2.y;
		v.y = v1.z * v2.x - v1.x * v2.z;
		v.z = v1.x * v2.y - v1.y * v2.x;

		return v;
	}


	/**
	 * returns a new vector that is a normalised version of the parameter
	 */
	public static Vec3d normalise( Vec3d v )
	{
		Vec3d n = new Vec3d();

		double l = length( v );

		n.x = v.x / l ;
		n.y = v.y / l ;
		n.z = v.z / l ;

		return n;
	}


	/**
	 * implementation should be self-evident
	 */
	public static double hypotenuse( double opp, double adj )
	{
		return sqrt( opp * opp + adj * adj );
	}


	/**
	 * checks if the absolute difference between v1 and v2 is less than or equal to e
	 * (make sure e >= 0)
	 */
	public static boolean epsilon_equals( double v1, double v2, double e )
	{
		return abs( v2 - v1 ) <= e;
	}


	public static double average( Number[] values )
	{
		check_arg( values != null, "values array is null" );

		check_arg( values.length > 0, "values array has no data" );

		double total = 0;

		for( int i=0; i<values.length; i++ )
		{
			//check_arg( values[ i ] != null, "values array element %d is null", i );

			total += values[ i ].doubleValue();
		}

		return total / values.length;
	}


	public static double average( double[] values )
	{
		check_arg( values != null, "values array is null" );

		check_arg( values.length > 0, "values array has no data" );

		double total = 0;

		for( int i=0; i<values.length; i++ )
		{
			//check_arg( values[ i ] != null, "values array element %d is null", i );

			total += values[ i ];
		}

		return total / values.length;
	}


	public static double standard_deviation( double[] values )
	{
		check_arg( values != null, "values array is null" );

		check_arg( values.length > 0, "values array has no data" );

		final int N = values.length;

		if( N == 1 ) return 0;

		double[] s = new double[ 3 ];

		for( int j=0; j<s.length; j++ )
		{
			double sj = 0;

			for( int k=1; k<N; k++ )
			{
				int i = k-1;

				//check_arg( values[ i ] != null, "values array element %d is null", i );

				sj += pow( values[ i ], j );
			}

			s[ j ] = sj;

			if( j == 0 && sj == 0 ) return 0;
		}

		//cdebug.println( "s0 = %f", s[0] );

		//cdebug.println( "s1 = %f", s[1] );

		//cdebug.println( "s2 = %f", s[2] );

		double d = s[0] * s[2] - s[1] * s[1];

		//cdebug.println( "d = %f", d );

		if( d <= 0 ) return 0;

		return sqrt( d ) / s[0];
	}


	public static double standard_deviation( Number[] values )
	{
		check_arg( values != null, "values array is null" );

		check_arg( values.length > 0, "values array has no data" );

		final int N = values.length;

		if( N == 1 ) return 0;

		double[] s = new double[ 3 ];

		for( int j=0; j<s.length; j++ )
		{
			double sj = 0;

			for( int k=1; k<N; k++ )
			{
				int i = k-1;

				//check_arg( values[ i ] != null, "values array element %d is null", i );

				sj += pow( values[ i ].doubleValue(), j );
			}

			s[ j ] = sj;

			if( j == 0 && sj == 0 ) return 0;
		}

		//cdebug.println( "s0 = %f", s[0] );

		//cdebug.println( "s1 = %f", s[1] );

		//cdebug.println( "s2 = %f", s[2] );

		double d = s[0] * s[2] - s[1] * s[1];

		//cdebug.println( "d = %f", d );

		if( d <= 0 ) return 0;

		return sqrt( d ) / s[0];
	}


	public static double eval( String expr ) throws Throwable
	{
		ScriptEngineManager sem = new ScriptEngineManager();

		ScriptEngine e = sem.getEngineByName( "ECMAScript" );

		ScriptEngineFactory f = e.getFactory();

		String program = f.getProgram( expr );

		Object ret = e.eval(program);

		return _double( ret.toString() );
	}


	public static int compare( double d1, double d2 )
	{
		if( d1 < d2 )
		{
			return -1;
		}

		if( d1 > d2 )
		{
			return 1;
		}

		return 0;
	}

	public static int compare( long l1, long l2 )
	{
		if( l1 < l2 )
		{
			return -1;
		}

		if( l1 > l2 )
		{
			return 1;
		}

		return 0;
	}


	public static int compare( Comparable c1, Comparable c2 )
	{
		return c1.compareTo( c2 );
	}
	

	public static double radians( double degrees )
	{
		return degrees * Trig.DEG_TO_RAD;
	}


	public static double degrees( double radians )
	{
		return radians * Trig.RAD_TO_DEG;
	}

}

/*

	/**
	 * t( 0 ) = p0
	 * t( 1 ) = p1
	 * values are not clamped to p0 and p1
	 * values are returned in a new vector

	public static Vec2d interpolate( Vec2d p0, Vec2d p1, double t )
	{
		return new Vec2d( interpolate( p0.x, p1.x, t ), interpolate( p0.y, p1.y, t ) );
	}


	/**
	 * implementation should be self-evident

	public static Vec2d average( Vec2d... ps )
	{
		Vec2d vr = new Vec2d();

		for( Vec2d v : ps )
		{
			vr.x += v.x;

			vr.y += v.y;
		}

		vr.x /= ps.length;

		vr.y /= ps.length;

		return vr;
	}
*/
