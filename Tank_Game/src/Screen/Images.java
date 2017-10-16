package Screen;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Images {
	
	public static BufferedImage p_tank,p_turret,bullet,ebullet,t_base,t_turret,bg_blank,block = null,box,green;
	
	public static void loadImages(){
		try{
			p_tank = ImageIO.read(Images.class.getResourceAsStream("/Entities/Tank.png"));
			p_turret = ImageIO.read(Images.class.getResourceAsStream("/Entities/Turret.png"));
			bullet = ImageIO.read(Images.class.getResourceAsStream("/Entities/Bullet.png"));
			ebullet = ImageIO.read(Images.class.getResourceAsStream("/Entities/EBullet.png"));
			t_base = ImageIO.read(Images.class.getResourceAsStream("/Entities/EnemyBase.png"));
			t_turret = ImageIO.read(Images.class.getResourceAsStream("/Entities/EnemyTurret.png"));
			bg_blank = ImageIO.read(Images.class.getResourceAsStream("/Backgrounds/Blank.png"));
			block = ImageIO.read(Images.class.getResourceAsStream("/Tiles/Block.png"));
			box = ImageIO.read(Images.class.getResourceAsStream("/Tiles/Box.png"));
			green = ImageIO.read(Images.class.getResourceAsStream("/Tiles/Green.png"));
		}catch(Exception e){
			System.err.println("could not load images.");
			JOptionPane.showMessageDialog(null, "could not load images");
		}
	}
	
}
