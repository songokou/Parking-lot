
/*
 * This class is created to store the position information which contains two int x, y
 * They correspond to the actual position on the board
 */



public class Position {
	public int x;
	public int y;
	
	public Position(int xPos, int yPos) {
		x = xPos;
		y = yPos;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String toString() {
		return x + "," + y;
	}
}
