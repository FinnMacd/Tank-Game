package Main;

import javax.swing.JFrame;

public class Game {
	
	public static JFrame frame = new JFrame();
	public static String title = "Tank Wars Alpha 1.0";
	
	public static void main(String[] args) {
		new Game();
	}
	
	public Game(){
		
		GamePanel gp = new GamePanel();
		
		frame.setTitle(title);
		frame.add(gp);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		
		gp.start();
		
	}
	
}
