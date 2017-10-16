package Screen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class List {
	
	private ArrayList<Button> list;
	private int selected = -1;
	private int x,y;
	
	public List(int x,int y){
		this.x = x; 
		this.y = y;
	}
	
	public void setData(String[] s){
		selected = -1;
		list = new ArrayList<Button>();
		for(int i = 0; i < s.length; i++){
			list.add(new Button(s[i], x, y + (i*36),Color.black,Color.black,40));
		}
	}
	
	public void update(){
		
		for(int i = 0; i < list.size(); i++){
			list.get(i).update();
			if(list.get(i).isClicked()){
				selected = i;
			}
		}
	}
	
	public String getSelected(){
		if(selected == -1)return null;
		return list.get(selected).getText();
	}
	
	public void draw(Graphics2D g){
		for(int i = 0; i < list.size(); i++){
			list.get(i).draw(g);
			if(selected == i){
				g.drawRect(x-5, y-32 + (i*36), 300, 38);
			}
		}
	}
	
}
