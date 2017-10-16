package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import Screen.Background;
import Screen.Button;
import Screen.List;

public class SelectionState extends GameState{
	
	private List maps = new List(10,40);
	
	private boolean viewd = true;

	private Button play, edit, defalt, custom, newM, del;
	
	public SelectionState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void init() {
		play = new Button("Play", 20, 400, Color.black, new Color(110,0,20), 40);
		edit = new Button("Edit", 160, 400, Color.black, new Color(110,0,20), 40);
		newM = new Button("New", 300, 400, Color.black, new Color(110,0,20), 40);
		del = new Button("Delete", 420, 400, Color.black, new Color(110,0,20), 40);
		defalt = new Button("Default", 460, 100, Color.black, new Color(110,0,20), 40);
		custom = new Button("Custom", 460, 140, Color.black, new Color(110,0,20), 40);
		bg = new Background("/Backgrounds/Blank.png");
		
		File hasdir = new File(".");
		boolean has = false;
        for( File f : hasdir.listFiles()){
            if(f.getName().equals("Saves"))has = true;
        }
		if(!has){
			new File("Saves").mkdir();
		}
		getData();
	}
	
	private void getData(){
		if (viewd) {
			String[] s = {"Map1","Map2"};
			maps.setData(s);
		} else {
			File actual = new File("Saves/.");
			String[] s = new String[actual.listFiles().length];
			for (int i = 0; i < s.length; i++) {
				s[i] = actual.listFiles()[i].getName();
			}
			maps.setData(s);
		}

	}

	public void update() {
		play.update();
		edit.update();
		newM.update();
		del.update();
		defalt.update();
		custom.update();
		maps.update();
		if(play.isClicked() && maps.getSelected() != null){
			if(viewd){
				SinglePlayerState.setPath("/Maps/" + maps.getSelected(),viewd);
			}else{
				SinglePlayerState.setPath("Saves/" + maps.getSelected(),viewd);
			}
			gsm.setState(GameStateManager.SINGLESTATE);
		}
		if(defalt.isClicked()){
			viewd = true;
			try{
				getData();
			}catch(Exception e){}
		}
		if(custom.isClicked()){
			viewd = false;
			try{
				getData();
			}catch(Exception e){}
		}
		if(newM.isClicked() && !viewd){
			String name = JOptionPane.showInputDialog("Enter map name");
			int width = Integer.parseInt(JOptionPane.showInputDialog("Enter Width"));
			int height = Integer.parseInt(JOptionPane.showInputDialog("Enter Height"));
			if(!name.equals("")){
				File hasdir = new File("Saves/.");
				boolean has = false;
		        for( File f : hasdir.listFiles()){
		            if(f.getName().equals(name))has = true;
		        }
		        Writer writer= null;
				if(!has){
					try {
					    writer = new BufferedWriter(new OutputStreamWriter(
					          new FileOutputStream("Saves/"+name), "utf-8"));
					    writer.write("Width=" + width + "\n");
					    writer.write("Height=" + height + "\n");
					    for(int y = 0; y < height; y ++){
					    	for(int x = 0; x < width; x ++){
					    		writer.write("0 ");
					    	}
					    	writer.write("\n");
					    }
					} catch (IOException ex) {
					  ex.printStackTrace();
					} finally {
					   try {writer.close();System.out.println("writen");} catch (Exception ex) {}
					}
					EditState.setPath("Saves/"+name, false);
					gsm.setState(GameStateManager.EDITSTATE);
				}else{
					JOptionPane.showMessageDialog(null, "Name already in use");
				}
			}
		}
		if(edit.isClicked() && !viewd  && maps.getSelected() != null){
			EditState.setPath("Saves/" + maps.getSelected(),viewd);
			gsm.setState(GameStateManager.EDITSTATE);
		}
		if(del.isClicked() && !viewd  && maps.getSelected() != null){
			int click = JOptionPane.showConfirmDialog(null, "Your map will be lost forever, are you sure you want to continue?", "Confirmation", JOptionPane.YES_NO_OPTION);
			if(click == 0)try {
				Files.delete(Paths.get("Saves/"+maps.getSelected()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			getData();
		}
	}

	public void draw(Graphics2D g) {
		bg.draw(g);
		maps.draw(g);
		play.draw(g);
		edit.draw(g);
		newM.draw(g);
		del.draw(g);
		defalt.draw(g);
		custom.draw(g);
	}

	public boolean loaded() {
		if(!loaded){
			loaded = true;
			return false;
		}
		return true;
	}
	
}
