package Screen;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Background {
    
    private BufferedImage image;
    private double x=0,y=0;
    private double dx,dy,scale;
    private int WIDTH, HEIGHT;
    
    public Background(String file){
        try{
            image = ImageIO.read(getClass().getResourceAsStream(file));
            WIDTH = image.getWidth();
            HEIGHT = image.getHeight();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void setVector(double x, double y){
        dx=x;
        dy=y;
    }
    
    public void setScale(double scale){
        this.scale = scale;
    }
    
    public void update(double x, double y){
        this.x = x*scale;
        this.y = scale*y;
    }
    
    public void update(){
        x+=dx;
        y += dy;
    }
    
    private void checkBounds(){
        if(x>=WIDTH)x=0;
        if(x<=-WIDTH)x=0;
        if(y>=HEIGHT)y=0;
        if(y<=-HEIGHT)y=0;
    }
    
    public void draw(Graphics2D g){
        checkBounds();
        g.drawImage(image, (int)x,(int)y, null);
        g.drawImage(image, (int)x+WIDTH,(int)y, null);
        g.drawImage(image, (int)x-WIDTH,(int)y, null);
        g.drawImage(image, (int)x,(int)y+HEIGHT, null);
        g.drawImage(image, (int)x,(int)y-HEIGHT, null);
        g.drawImage(image, (int)x+WIDTH,(int)y+HEIGHT, null);
        g.drawImage(image, (int)x+WIDTH,(int)y-HEIGHT, null);
        g.drawImage(image, (int)x-WIDTH,(int)y+HEIGHT, null);
        g.drawImage(image, (int)x-WIDTH,(int)y-HEIGHT, null);
    }
    
}
