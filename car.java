
/*
 * this class is created to store the information of each car
 * It has Vector<Position> positions to store the position a car has in a board
 * It has boolean vert, horiz to determine if this car can move vert or horiz
 * It has boolean playerBlock to see if this is the target car Z
 * It has a char name to store the car's name
 * It has Rectangle rec to later paint it on the board, it contains the dimensions of this car
 */

import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JButton;


@SuppressWarnings("serial")
public class car extends JButton{
	private Vector<Position> positions;
	private boolean vert, horiz;
	private boolean playerBlock;
	private char name;
	private Rectangle rec;
	//contstructor
	public car(Vector<Position> pos,
				boolean h, boolean v, boolean p,
				char n) {
		positions = pos;
		vert = v;
		horiz = h;
		playerBlock = p;
		name = n;
		super.setText(String.valueOf(name));
	}
	
	public Rectangle getRec(){
		return rec;
	}
	//to set the rectangle for each car 
	public void setRec(int s) {
		int startX =1000000, startY=1000000, maxX=-1, maxY=-1;
		for(int i=0; i < positions.size(); i++) {
			Position c = positions.get(i);
			//check x
			if(c.x <= startX)
				startX = c.x;
			if(c.x >= maxX)
				maxX = c.x;
			//check y
			if (c.y <= startY)
				startY = c.y;
			if (c.y >= maxY)
				maxY = c.y;
		}
		int length = maxX-startX + 1;
		int height = maxY-startY + 1;
		
		rec = new Rectangle(s*startX, s*startY, s*length, s*height);
	}
	
	public Vector<Position> getPositions() {
		return positions;
	}
	
	public boolean hasPosition(Position p) {
		for (int i=0; i < positions.size(); i++) {
			if (positions.get(i).x == p.x &&positions.get(i).y == p.y) return true;
		}
		return false;
	}
	// to see if a given piece is in a Position p
	public boolean isInPosition(Position p) {
		int size = positions.size();
		for(int i =0; i < size; i++)
			if(p.x == positions.get(i).x &&p.y == positions.get(i).y) 
				return  true;
		return false;
	}
	
	public void setNewPosition(Vector<Position> p) {
		positions = p;
	}
	
	public boolean isVertical() {
		return vert;
	}
	
	public boolean isHorizontal () {
		return horiz;
	}
	
	public boolean isPlayer() {
		return playerBlock;
	}
	
	
	public char getCarName() {
		return name;
	}

	public void setName(char c) {
		name = c;
		super.setText(String.valueOf(name));
		
	}

	public void setPlayer(boolean b) {
		playerBlock = true;
	}
	
	public String toString() {
		String ret = name + " ";
		if (vert)
			ret += "vertical ";
		if (horiz)
			ret += "horizontal";
		return ret;
	}
	
}
