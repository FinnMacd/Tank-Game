package Main;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Inputs implements KeyListener, MouseListener,MouseMotionListener,FocusListener,MouseWheelListener{
	
	public static volatile boolean left = false,right = false,up = false,down = false,mleft = false,cmleft = false,mright = false,focus = false,space = false;
	public static int mx = 0,my = 0,notches = 0;
	
	public static boolean[] cheat = new boolean[3];
	
	private GamePanel gp;
	
	public Inputs(GamePanel gp){
		this.gp = gp;
	}
	
	public void focusGained(FocusEvent e) {
		focus = true;
		left  = right = up = down = mleft = mright = false;
	}

	public void focusLost(FocusEvent e) {
		focus = false;
	}

	public void mouseDragged(MouseEvent e) {
		mx = e.getX()/GamePanel.scale;
		my = e.getY()/GamePanel.scale;
	}

	public void mouseMoved(MouseEvent e) {
		mx = e.getX()/GamePanel.scale;
		my = e.getY()/GamePanel.scale;
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == e.BUTTON1)cmleft = true;
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)mleft = true;
		if(e.getButton() == MouseEvent.BUTTON3)mright = true;
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)mleft = false;
		if(e.getButton() == MouseEvent.BUTTON3)mright = false;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_P && GamePanel.scale == 2){
			GamePanel.scale = 1;
			gp.setSize();
		}
		else if(key == KeyEvent.VK_P && GamePanel.scale == 1){
			GamePanel.scale = 2;
			gp.setSize();
		}
		
		if(key == KeyEvent.VK_W)up = true;
		if(key == KeyEvent.VK_S)down = true;
		if(key == KeyEvent.VK_D)right = true;
		if(key == KeyEvent.VK_A)left = true;
		if(key == KeyEvent.VK_SPACE)space = true;
		
		if(key == KeyEvent.VK_CONTROL)cheat[0] = true;
		if(key == KeyEvent.VK_C)cheat[1] = true;
		if(key == KeyEvent.VK_H)cheat[2] = true;
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W)up = false;
		if(key == KeyEvent.VK_S)down = false;
		if(key == KeyEvent.VK_D)right = false;
		if(key == KeyEvent.VK_A)left = false;
		if(key == KeyEvent.VK_SPACE)space = false;

		if(key == KeyEvent.VK_CONTROL)cheat[0] = false;
		if(key == KeyEvent.VK_C)cheat[1] = false;
		if(key == KeyEvent.VK_H)cheat[2] = false;
	}

	public void keyTyped(KeyEvent e) {
		
	}

	
	public void mouseWheelMoved(MouseWheelEvent e) {
		notches += e.getWheelRotation();
	}

}
