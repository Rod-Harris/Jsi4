package rojira.jsi4.util.gui;

import java.awt.image.*;

import static rojira.jsi4.LibGUI.*;


public class VerticalSeamlessImageShader extends ImageShader
{
	private BufferedImage source;

	private int h;


	public VerticalSeamlessImageShader( BufferedImage source )
	{
		this.source = source;

		h = source.getHeight();
	}


	public int shade( int x, int y, int argb )
	{
		float fy = y;

		fy /= ( h - 1 );

		if( fy <= 0.5f )
		{
			fy += 0.5f;
		}
		else
		{
			fy = 1.5f - fy;
		}

		int iy = h - 1 - y;

		int argb2 = source.getRGB( x, iy );

		return mix( argb, argb2, fy );
	}
}
