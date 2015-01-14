/*
 * This contains the information a board has
 * It has int length and width to decide the dimension of this puzzle
 * It has Vector<car> cars to store all the cars in a board
 * It has Vector<Move> movesDone to store all the Moves done so far
 */

import java.util.Vector;


public class board {
	private int length;
	private int width;
	private Vector<car> cars;
	private Vector<Move> movesDone;
	
	//constructor of board
	public board(int x, int y, Vector<car> w, Vector<Move> m) {
		length = x;
		width = y;
		cars = w;
		movesDone = m;
	}
	//constructor of board
	public board(int x, int y, Vector<car> w) {
		length = x;
		width = y;
		cars = w;
		movesDone = new Vector<Move>();
	}
	
	public Vector<Move> getMoves() {
		return movesDone;
	}
	
	public boolean isEmptyAt(Position p) {
		for (int i=0; i < cars.size(); i++) {
			if(cars.get(i).isInPosition(p)) return false;
		}
		return true;
	}
	
	public void addMove(Move m) {
		movesDone.add(m);
	}
	
	public int numMoves() {
		return movesDone.size();
	}
	
	public Vector<car> getCars() {
		return cars;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getWidth() {
		return width;
	}
}
