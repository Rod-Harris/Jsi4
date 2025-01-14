package rojira.jsi4.util.gui;

import java.awt.image.*;

import static rojira.jsi4.LibGUI.*;


public class HorizontalSeamlessImageShader extends ImageShader
{
	private BufferedImage source;

	private int w;


	public HorizontalSeamlessImageShader( BufferedImage source )
	{
		this.source = source;

		w = source.getWidth();
	}


	public int shade( int x, int y, int argb )
	{
		float fx = x;

		fx /= ( w - 1 );

		if( fx <= 0.5f )
		{
			fx += 0.5f;
		}
		else
		{
			fx = 1.5f - fx;
		}

		int ix = w - 1 - x;

		int argb2 = source.getRGB( ix, y );

		return mix( argb, argb2, fx );
	}
}
