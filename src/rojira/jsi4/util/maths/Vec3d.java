package rojira.jsi4.util.maths;

//import org.simpleframework.xml.*;

public class Vec3d extends Vec2d
{
	//@Element
	//public double x, y, z;
	public double z;

	public String print_format = "%.2f";


	public Vec3d()
	{
	}


	public Vec3d( Vec3d other )
	{
		set( other );
	}


	public Vec3d( double x, double y, double z )
	{
		set( x, y, z );
	}


	public Vec3d set( Vec3d other )
	{
		return set( other.x, other.y, other.z );
	}


	public Vec3d set( double x, double y, double z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}


	public Vec3d zero()
	{
		x = y = z = 0;
		
		return this;
	}


	public String toString()
	{
		return String.format( String.format( "( %1$s, %1$s, %1$s )", print_format ), x, y, z );
	}


	public double length_squared()
	{
		return x * x + y * y + z * z;
	}


	public double length()
	{
		return Math.sqrt( length_squared() );
	}


	public Vec3d normalise()
	{
		scale( 1.0 / length() );
		
		return this;
	}


	/**
	adds the arguments to this vector ( this vector is not zeroed first )
	*/
	public Vec3d add( Vec3d... vs )
	{
		for( Vec3d v : vs )
		{
			x += v.x;
			y += v.y;
			z += v.z;
		}
		
		return this;
	}


	/**
	set this vector to be equals to the sum of the arguments ( this vector is zeroed first )
	*/
	public Vec3d sum( Vec3d... vs )
	{
		zero();

		for( Vec3d v : vs )
		{
			x += v.x;
			y += v.y;
			z += v.z;
		}
		
		return this;
	}


	/**
	this += s * v;
	*/
	public Vec3d add( double s, Vec3d v )
	{
		x += s * v.x;
		y += s * v.y;
		z += s * v.z;
		
		return this;
	}


	/**
	this -= v;
	*/
	public Vec3d sub( Vec3d v )
	{
		x -= v.x;
		y -= v.y;
		z -= v.z;
		
		return this;
	}

	/**
	this -= v;
	*/
	public Vec3d sub( double s, Vec3d v )
	{
		x -= s * v.x;
		y -= s * v.y;
		z -= s * v.z;
		
		return this;
	}


	/**
	this = v0 - v1;
	*/
	public Vec3d sub( Vec3d v0, Vec3d v1 )
	{
		x = v0.x - v1.x;
		y = v0.y - v1.y;
		z = v0.z - v1.z;
		
		return this;
	}


	/**
	this *= s;
	*/
	public Vec3d scale( double s )
	{
		mult( s );
		
		return this;
	}

	/**
	this *= s;
	*/
	public Vec3d mult( double s )
	{
		x *= s;
		y *= s;
		z *= s;
		
		return this;
	}

	/**
	this = s * v
	*/
	public Vec3d set( double s, Vec3d v )
	{
		x = v.x * s;
		y = v.y * s;
		z = v.z * s;
		
		return this;
	}


	/**
	this = v1 x v2
	*/
	public Vec3d cross( Vec3d v1, Vec3d v2 )
	{
		x = v1.y * v2.z - v1.z * v2.y;
		y = v1.z * v2.x - v1.x * v2.z;
		z = v1.x * v2.y - v1.y * v2.x;
		
		return this;
	}


	/**
	returns ( this . v )
	*/
	public double dot( Vec3d v )
	{
		return x * v.x + y * v.y + z * v.z;
	}


	/**
	sets this vector to be the average of the arguments ( this vectors initial values are not included )
	*/
	public Vec3d avg( Vec3d... vs )
	{
		zero();

		for( Vec3d v : vs )
		{
			x += v.x;
			y += v.y;
			z += v.z;
		}

		x /= vs.length;
		y /= vs.length;
		z /= vs.length;
		
		return this;
	}
}
