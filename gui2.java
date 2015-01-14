import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.MouseInputListener;


/**
 *
 * We support two directional pieces
 * Note: to move two directional piece in direction you
 * want make sure to move it quickly in direction you want
 * it to move, other wise it is stuck with direction
 * you first moved it
 *
 *
 *
 */


@SuppressWarnings("serial")
public class gui2 extends JFrame implements MouseInputListener, MouseMotionListener,
ActionListener{
    
	private board currentState; //how does the board look like
	private Point mouseOrigin; //which car is player moving
	private car beforeMove; //where was a car before the player has moved it
	private int startDir = 0; //keeps track of bidirectional dudes
	private int level = 1; //what level is the player on
	private JPanel playPanel; //current level
	private JPanel scorePanel;
	private JLabel scoreLabel; //score keeping
	private ScoreReader sr; //reads in the score and saves it
	private Solver solution; //solver to the current Board (currentState)
	
	private final int SIZEOFEACHPIECE = 75; //size of the game, each square is 50x50
	private final int NUMLEVELS = 10; //number of levels
	
	public gui2(){
		super("Rush hour");
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		sr = new ScoreReader(NUMLEVELS);
		JMenuBar menu = new JMenuBar();
		//levels
		JMenu levels = new JMenu("levels");
		JMenuItem levelArray[] = new JMenuItem[10];
		for (int i = 0; i < NUMLEVELS; i++) {
			levelArray[i] = new JMenuItem(String.valueOf(i+1));
			levelArray[i].addActionListener(this);
			levels.add(levelArray[i]);
		}
		menu.add(levels);
		menu.add(createOptions()); //adding options
		this.setJMenuBar(menu);
		generateLevel("1");
		setEnabled(true);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//update label
	private void updateScore() {
		String sScore = "Moves: ";
		sScore += String.valueOf(currentState.getMoves().size());
		sScore += "  Best Score: ";
		int bestScore = sr.getScore(level);
		if (bestScore != -1)
			sScore+=String.valueOf(bestScore);
		scoreLabel.setText(sScore);
	}
	
	
	//create score keeping thing
	private JPanel scorePanel() {
		scorePanel = new JPanel();
		String text = "Moves: 0  Best Score: ";
		int bestScore = sr.getScore(level);
		if (bestScore != -1)
			text+=String.valueOf(bestScore);
		scoreLabel = new JLabel(text);
		scoreLabel.setVisible(true);
		scoreLabel.setForeground(Color.WHITE);
		scorePanel.add(scoreLabel);
		scorePanel.setVisible(true);
		scorePanel.setBackground(Color.BLACK);
		//updateScore();
		return scorePanel;
	}
	
	
	//create options menu
	private JMenu createOptions() {
		JMenu options = new JMenu("options");
		//reset button
		JMenuItem resetButton = new JMenuItem("reset");
		resetButton.addActionListener(this);
		JMenuItem solveButton = new JMenuItem("solve");
		solveButton.addActionListener(this);
		JMenuItem hintButton = new JMenuItem("hint");
		hintButton.addActionListener(this);
		JMenuItem aboutButton = new JMenuItem("about");
		aboutButton.addActionListener(this);
		JMenuItem helpButton = new JMenuItem("help");
		helpButton.addActionListener(this);
		JMenuItem exitButton = new JMenuItem("exit");
		exitButton.addActionListener(this);
		
		options.add(resetButton);
		options.add(solveButton);
		options.add(hintButton);
		options.add(aboutButton);
		options.add(helpButton);
		options.add(exitButton);
		
		return options;
	}
	
	
	private void generateLevel(String lev) {
		//all of the levels will have to be named
		/****** proj3-levelX.txt *******/
		//where X is the level to play
		String forJP = "proj3-level" + lev + ".txt";
		makeGuiForGame(forJP);
		//makeGuiForGame("proj3g.txt");//for checking the input file reading
	}
    
	private void makeGuiForGame(String s) {
		Container container = getContentPane();
		ReadFile f = new ReadFile();
		f.setPuzzle(s);
		currentState = f.getBoard();
		//startSolver(); //start solving that board
		int numPixelsX = currentState.getLength() * SIZEOFEACHPIECE;
		int numPixelsY = currentState.getWidth() * SIZEOFEACHPIECE;
		JPanel playPanel = new JPanel(null);
		playPanel.setBounds(0, 0, numPixelsX, numPixelsY);
        
		//add the buttons at each position
		int size = currentState.getCars().size();
		for (int i=0; i<size; i++) {
			car c = currentState.getCars().get(i);
			//make the rectangle out of each car
			c.setRec(SIZEOFEACHPIECE);
            
			//add it to the board and place it as a rectangle
			playPanel.add(c);
            
			//make sure it is at a given position
			c.setBounds(c.getRec());
			//SET COLORS TO CARS MOVING IN DIFFERENT DIRECTIONS
			if (c.isVertical()) c.setForeground(Color.RED);
			if (c.isHorizontal()) c.setForeground(Color.BLUE);
			if (c.isVertical() && c.isHorizontal()) c.setForeground(Color.GREEN);
			if (c.isPlayer()) c.setForeground(Color.YELLOW);
			c.addMouseMotionListener(this);
			c.addMouseListener(this);
		}
		playPanel.setSize(numPixelsX + 10, numPixelsY + 10); //magic constants :P
		setSize(numPixelsX + 10, numPixelsY + 80); //another magic constant! :P
		this.playPanel = playPanel;
		scorePanel();
		container.add(scorePanel, BorderLayout.SOUTH);
		container.add(playPanel, BorderLayout.CENTER);
		container.repaint();
	}
    
	//somethig along the lines of message pop up asking if
	//player wants to move on to the next level.
	//if yes, load next level up, else exit
	private void hasPlayerWon() {
		//if won, ask to go to next level
		if (goalReached()) {
			//check best score
			int currentScore = currentState.getMoves().size();
			int bScore = sr.getScore(level);
			if (bScore == -1) {
				//set new score
				sr.updateScore(level, currentScore);
			}
			else if (bScore > currentScore) {
				//set new score also
				sr.updateScore(level, currentScore);
			}
			if (level != NUMLEVELS) {
				//make a pop up asking to move on to next level
				//or exit the game
				int reply = JOptionPane.showConfirmDialog(null, "Level Passed, continue?",
                                                          "Next Level?",  JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION)
				{
					Container container = getContentPane();
					container.remove(playPanel);
					container.remove(scorePanel);
					level++;
					generateLevel(String.valueOf(level));
					//updateScore();
					container.repaint();
				}
				else {
					System.exit(0);
				}
			}
			//if level is 10 do something else
			else {
				//make pop up
				//congratulate and exit
				JOptionPane.showMessageDialog(null, "You won the last level, Exiting!");
				System.exit(0);
			}
		}
		
	}
	
	//checks current board if the Z piece has made it to the right side
	private boolean goalReached() {
		//get index of Z-car
		int index = 0;
		for (int i = 0; i < currentState.getCars().size(); i++)
			if (currentState.getCars().get(i).getCarName() == 'Z') index = i;
		Vector<Position> pos = currentState.getCars().get(index).getPositions();
		//if one of the positions in board length - 1, than player has won
		for (int i = 0; i < pos.size(); i++) {
			if (pos.get(i).x + 1 == currentState.getLength()) return true;
		}
		return false;
	}
    
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		gui2 g = new gui2();
	}
    
	@Override
	public void mouseClicked(MouseEvent e) {
		//nothing
	}
    
	@Override
	public void mouseEntered(MouseEvent e) {
		//nothing
	}
    
	@Override
	public void mouseExited(MouseEvent e) {
		//nothing
	}
    
	@Override
	public void mousePressed(MouseEvent e) {
		mouseOrigin = e.getPoint();
		car c = (car) e.getSource();
		car newC = new car(c.getPositions(), c.isHorizontal(),
                           c.isVertical(), c.isPlayer(), c.getCarName());
		beforeMove = newC;
		startDir = 0;
	}
    
	@Override
	public void mouseReleased(MouseEvent e) {
		Move m = null;
		//start a thread to solve a puzzle
		char name = beforeMove.getCarName();
		car c = null;
		for (int i = 0; i < currentState.getCars().size(); i++) {
			if (name == currentState.getCars().get(i).getCarName()) {
				c = currentState.getCars().get(i);
				break;
			}
		}
		//if y changed vertical
		int movY = c.getPositions().get(0).y - beforeMove.getPositions().get(0).y;
		if (movY < 0) {
			m = new Move(name, -1, -1*movY, true);
		}
		else if (movY > 0) {
			//moved down
			m = new Move(name, 1, movY, true);
		}
		//if x changed, horizontal
		int movX = c.getPositions().get(0).x - beforeMove.getPositions().get(0).x;
		if (movX < 0) {
			m = new Move(name, -1, -1*movX, false);
		}
		else if (movX > 0) {
			//moved right
			m = new Move(name, 1, movX, false);
		}
		//if no change in x or y, no move
		if (movY != 0 || movX != 0) {
			currentState.addMove(m);
			updateScore();
			//startSolver();
		}
		startDir = 0;
		hasPlayerWon();
	}
    
	@Override
	public void mouseDragged(MouseEvent e) {
		Point current = e.getPoint();
		car c = (car) e.getSource();
		if (startDir==0) {
			if (Math.abs(current.y - mouseOrigin.y) >
                Math.abs(current.x - mouseOrigin.x))
				startDir = 1;
			else startDir = -1;
		}
		if (c.isVertical()) {
			if ((c.isHorizontal() && startDir == 1) ||
                !c.isHorizontal()) {
				int movedV = current.y - mouseOrigin.y;
				//check if legal move
				movedV = movedV / SIZEOFEACHPIECE;
				//	System.out.println("Mouse moved in vertical: " + movedV);
				if (checkMoveCar(c, movedV, true)) { //legal move
					//repaint the car
					//			System.out.println("Possible to move the car!");
					paintCar(c, movedV, true);
				}
			}
		}
		if (c.isHorizontal()) {
			if ((c.isVertical() && startDir == -1) ||
                !c.isVertical()) {
                
				int movedH = current.x - mouseOrigin.x;
                
				movedH = movedH / SIZEOFEACHPIECE;
				if (checkMoveCar(c, movedH, false)) { //legal move
					//repaint the car
					paintCar(c, movedH, false);
				}
			}
		}
	}
    
	@Override
	public void mouseMoved(MouseEvent e) {
		//nothing
	}
    
	//make a new rectangle for a car
	//assuming a legal move here
	private void paintCar(car c, int numS, boolean vert) {
		Vector<Position> oldP = c.getPositions();
		Vector<Position> newP = new Vector<Position>();
		if (vert) {
			for(int i = 0; i < oldP.size(); i++) {
				Position p = new Position(oldP.get(i).x, oldP.get(i).y + numS);
				newP.add(p);
			}
		}
		else {
			for(int i = 0; i < oldP.size(); i++) {
				Position p = new Position(oldP.get(i).x + numS, oldP.get(i).y);
				newP.add(p);
			}
		}
		//paint the car on the screen
		c.setNewPosition(newP);
		c.setRec(SIZEOFEACHPIECE);
		c.setBounds(c.getRec());
	}
    
	//return true if legal move for the car
	private boolean checkMoveCar(car c, int numS, boolean Vert) {
		if (Vert) {
			Vector<Position> pos = c.getPositions();
			for (int i = 0; i < pos.size(); i++) {
				Position p = new Position(pos.get(i).x, pos.get(i).y+numS );
				if (!((currentState.isEmptyAt(p) || c.hasPosition(p)) &&
                      (p.y >= 0 && p.y < currentState.getWidth())))
					return false;
			}
		}
		else {
			Vector<Position> pos = c.getPositions();
			for (int i = 0; i < pos.size(); i++) {
				Position p = new Position(pos.get(i).x+numS , pos.get(i).y);
				if (!((currentState.isEmptyAt(p) || c.hasPosition(p)) &&
                      (p.x >= 0 && p.x < currentState.getLength())))
					return false;
			}
		}
		return true;
	}
    
	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem mitem = (JMenuItem) e.getSource();
		Container container = getContentPane();
		if (mitem.getText().equals("reset")) {
			container.remove(playPanel);
			container.remove(scorePanel);
			generateLevel(String.valueOf(level));
			//updateScore();
			container.repaint();
		}
		else if (mitem.getText().equals("about")) {
			JOptionPane.showMessageDialog(null, "Created by: Mateusz Wszolek and Ze Li" +
                                          "\nUniversity of Illinois at Chicago\n" +
                                          "CS342, Project 3");
		}
		else if (mitem.getText().equals("hint")) {
			startSolver(false);
		}
		else if (mitem.getText().equals("solve")) {
			startSolver(true);
		}
		else if (mitem.getText().equals("help")) {
			JOptionPane.showMessageDialog(null, "Try to move car Z to the right of this board","Help",JOptionPane.PLAIN_MESSAGE);
            
		}
		else if (mitem.getText().equals("exit")){
			System.exit(0);
		}
		else {
			level = Integer.parseInt(mitem.getText());
			container.remove(playPanel);
			container.remove(scorePanel);
			generateLevel(mitem.getText());
			//updateScore();
			//container.repaint();
		}
	}
	
	
	private void startSolver(boolean displaySolution) {
		solution = new Solver(currentState);
		Vector<Move> movesSol = solution.Solve();
		if (movesSol.size() > 0) {
			if (displaySolution) {
				String answer = "";
				for(int i =0;i < movesSol.size(); i++)
					answer += movesSol.get(i).toString() + "\n";
				JOptionPane.showMessageDialog(null, answer);
			}
			else {
				JOptionPane.showMessageDialog(null, movesSol.get(0).toString());
			}
		}
		else
			JOptionPane.showMessageDialog(null, "There is no solution");
	}
}


