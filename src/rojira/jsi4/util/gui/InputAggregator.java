package rojira.jsi4.util.gui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.event.MouseInputListener;


public class InputAggregator implements KeyListener, MouseInputListener, MouseWheelListener
{
	private final ArrayList<InputListener> listeners = new ArrayList<InputListener>();
	
	
	public void listen_to( Component component )
	{
		component.addKeyListener( this );
		
		component.addMouseListener( this );
		
		component.addMouseMotionListener( this );
		
		component.addMouseWheelListener( this );
	}
	
	
	public void add_listener( InputListener listener )
	{
		listeners.add( listener );
	}
	
	
	public void remove_listener( InputListener listener )
	{
		listeners.remove( listener );
	}
	
	
	public void keyTyped( KeyEvent e )
	{
		char key = e.getKeyChar();
		
		int code = e.getKeyCode();
		
		for( InputListener listener : listeners ) listener.key_typed( key, code, e );
	}
	
	public void keyPressed( KeyEvent e )
	{
		char key = e.getKeyChar();
		
		int code = e.getKeyCode();
		
		for( InputListener listener : listeners ) listener.key_pressed( key, code, e );
	}
	
	public void keyReleased( KeyEvent e )
	{
		char key = e.getKeyChar();
		
		int code = e.getKeyCode();
		
		for( InputListener listener : listeners ) listener.key_released( key, code, e );
	}
	
	public void mouseClicked( MouseEvent e )
	{
		for( InputListener listener : listeners ) listener.mouse_clicked( e );
	}
	
	public void mousePressed( MouseEvent e )
	{
		for( InputListener listener : listeners ) listener.mouse_pressed( e );
	}
	
	public void mouseReleased( MouseEvent e )
	{
		for( InputListener listener : listeners ) listener.mouse_released( e );
	}
	
	public void mouseEntered( MouseEvent e )
	{
		for( InputListener listener : listeners ) listener.mouse_entered( e );
	}
	
	public void mouseExited( MouseEvent e )
	{
		for( InputListener listener : listeners ) listener.mouse_exited( e );
	}
	
	public void mouseDragged( MouseEvent e )
	{
		for( InputListener listener : listeners ) listener.mouse_dragged( e );
	}
	
	public void mouseMoved( MouseEvent e )
	{
		for( InputListener listener : listeners ) listener.mouse_moved( e );
	}
	
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		for( InputListener listener : listeners ) listener.mouse_wheel_moved( e );
	}
}
