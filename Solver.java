
/*
 * This class is created for solve and hint of this puzzle
 * It has a board boardToSolve which is the board read from ReadFile
 * It has a Queue<board> bQ for bfs and store the board 
 * It has a Map<String, String> previous to store the string which is converted from createHash(board)
 * so it will make sure we don't add a same board to bQ
 * It has an int to store the target car index so it can decide if this car reaches the targeted position
 * It also has information for car moving directions
 */

import java.util.*;



public class Solver {
	
	private board boardToSolve; //board to solve
	private Queue<board> bQ; //Q of the boards to check
	private int Zindex = 0; //Index of a car that has to get all the way to the right
	private Map<String, String> previous; //hashmap for the Q to see what was checked

	/***********************DEFINE DIRECTIONS*/
	private int right = 1;
	private int up = -1;
	private int left = -1;
	private int down = 1;
	
	//constructor of Solver
	public Solver(board BoardToSolve) {
		boardToSolve = duplicateBoard(BoardToSolve);
		bQ = new LinkedList<board>();
		previous = new HashMap<String, String>();
		findZ();
	}
	//find the target index
	private void findZ () {
		Vector<car> cars = boardToSolve.getCars();
		for (int i = 0; i < cars.size(); i++)	{
			if (cars.get(i).getCarName() == 'Z') {Zindex = i;break; }
		}
	}
	
	//Run BFS algorithm given by the professor
	public Vector<Move> Solve() {
		int numMoves = 999999;
		board shortSolved  = duplicateBoard(boardToSolve);
		makeQ(createHash(boardToSolve), null, boardToSolve);//put the board I'm supposed to solve into the Q
		//System.out.println("Solve: "+createHash(shortSolved));
		boolean solve = false;//a flag showing this has not been solved
		while (!bQ.isEmpty()) {//while Q is not Empty
			board b = bQ.remove();
			if (goalReached(b)&&!solve) { 	
				int steps = b.numMoves() - shortSolved.numMoves();//calculate how many steps have been taken
				if (steps < numMoves) {
					numMoves = steps;
					shortSolved = b;
				}
				solve=true;
				break;
			}	
			exploreMoves(b);//continue checking for shortest path
		}	
		Vector<Move> moves = new Vector<Move>();//return the moves, if not solvable, return empty vector
		for (int i=boardToSolve.numMoves(); i < shortSolved.numMoves(); i++)
			moves.add(shortSolved.getMoves().get(i));
		return moves;
	}

	//check for possible moves, if any, add to Q
	private void exploreMoves(board b) {
		Vector<car> cars = b.getCars();//check car by car if there is a move to be made
		for(int i=0; i < cars.size(); i++) {
			makeVerticalMove(b,i,up,true);				
			makeVerticalMove(b,i,down,true);
			makeHorizontalMove(b,i,right,false);
			makeHorizontalMove(b,i,left,false);						
		}		
	}
	
	//check if car is movable in a certain direction on a given board	
	private boolean checkIfMovable(board b, int index, int dir, boolean Vertical, int s) {
		car CarToCheck = b.getCars().get(index);
		Vector<Position> pos = CarToCheck.getPositions();
		int numS = s*dir;
		if (Vertical) {
			if (!(CarToCheck.isVertical())) return false;
			for (int i=0; i < pos.size(); i++) {
				Position p = new Position(pos.get(i).x,pos.get(i).y+numS);
				//either it is empty, or the car that I'm moving is there now
				if (!((b.isEmptyAt(p) || CarToCheck.hasPosition(p)) &&(p.y>= 0 && p.y < b.getLength())))
					return false;
			}
			return true;
		}
		else {		//same loop as above, but in horizontal
			if (!(CarToCheck.isHorizontal())) return false;
			for (int i=0; i < pos.size(); i++) {
				Position p = new Position(pos.get(i).x+numS,pos.get(i).y);
				if (!((b.isEmptyAt(p) || CarToCheck.hasPosition(p))&&(p.x >= 0 && p.x < b.getWidth())))
					return false;
			}
			return true;
		}	
	}
		// make a vertical move for a car
	private void makeVerticalMove(board b,int i,int dir,boolean vertical)
	{
		int j=0;
		String hash = createHash(b);//create this hash value
		while (checkIfMovable(b, i, dir, vertical, ++j)) {//a movable vertical piece
			board dupB = duplicateBoard(b);//add new board to the Q after new car position has been made
			car carToMove = dupB.getCars().get(i);
			Vector<Position> pos=carToMove.getPositions();//set new cars location		
			for (int k=0; k < pos.size(); k++) {//update the positions of this car
				pos.get(k).y+=dir*j;
			}	
			Move newMove = new Move(carToMove.getCarName(), dir, j, vertical);//add the move to the list
			dupB.addMove(newMove);
			makeQ(createHash(dupB), hash, dupB);//add to the Q
		}
	}
	// make a horizontal move for a car
	private void makeHorizontalMove(board b, int i, int dir, boolean horizontal)
	{
		int j=0;
		String hash = createHash(b);
		while (checkIfMovable(b, i, dir, horizontal,++j)) {	//a movable horizontal piece	
			board dupB = duplicateBoard(b);//add new board to the Q after new car position has been made
			car carToMove = dupB.getCars().get(i);
			Vector<Position> pos=carToMove.getPositions();//set new cars location
			for (int k=0; k < pos.size(); k++) {
				pos.get(k).x+=j*dir;
			}	
			Move newMove = new Move(carToMove.getCarName(), dir, j, horizontal);			//add the move to the list
			dupB.addMove(newMove);
			makeQ(createHash(dupB), hash, dupB);//add to the Q
			//hash = createHash(dupB);
		}
	}
	
	//make a copy of the board
	private board duplicateBoard(board b) {
		//check each car if it is movable if it is add it to the Q
		Vector<car> cars = new Vector<car>();
		for (int i=0; i < b.getCars().size(); i++) {		
			car cpcar = b.getCars().get(i);//copy cars
			Vector<Position> cpm = new Vector<Position>();
			for (int j=0; j < cpcar.getPositions().size(); j++) {			
				cpm.add(new Position(cpcar.getPositions().get(j).x,//copy the moves
						cpcar.getPositions().get(j).y));//previously is .x
			}
			cars.add(new car(cpm, cpcar.isHorizontal(), cpcar.isVertical(),//previously is isValid()
					cpcar.isPlayer(), cpcar.getCarName()));
		}		
		Vector<Move> mvs = new Vector<Move>();//copy moves
		for (int i = 0; i < b.getMoves().size(); i++) {
			Move mv = new Move(b.getMoves().get(i).getCarName(),
					b.getMoves().get(i).getDir(),
					b.getMoves().get(i).getnumS(),
					b.getMoves().get(i).getVertical());
			mvs.add(mv);
		}
		board bForRet = new board(b.getLength(), b.getWidth(), cars, mvs);
		//System.out.println("Board after duplicate");
		//System.out.println(bForRet.toString());
		return bForRet;
	}
	
	//is finalCar touching the right border?
	private boolean goalReached(board b) {
		car finalCar = b.getCars().get(Zindex);//find the final car
		Vector<Position> pos = finalCar.getPositions();
		for (int i = 0; i < pos.size(); i++) {
								//y is from 0 to n-1, width is n
			if((pos.get(i).x + 1) == (boardToSolve.getLength())) return true;//in ReadFile x is treated y
		}
		return false;
	}
	
	//adds the board to a Q if it never has been added before
	private void makeQ(String next, String prev, board b) {
      if (!previous.containsKey(next)) {
          previous.put(next,prev);
          bQ.add(b);
      }
  }
	//creates possibly long hash of the map
	private String createHash(board b) {
		String retVal = "";
		int l = b.getLength();
		int w = b.getWidth();
		Vector<car> cars = b.getCars();
		int size = cars.size();
		boolean added = false;
		for (int i =0; i < l; i++) {
			for (int j=0; j < w; j++)	{
				Position p= new Position(j,i);
				for (int c=0; c < size;c++) {
					if (cars.get(c).isInPosition(p)) {
						retVal += cars.get(c).getCarName();
						added = true;
						break;
					}
				}
				if (!added)
					retVal += '.';
				added = false;
			}
		}
		return retVal;
	}
}
