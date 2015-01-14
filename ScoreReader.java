

import java.io.*;
import java.util.Vector;
/*
 * This class is created to keep track of the best score in this game
 */

public class ScoreReader {

	private Vector<String> scores;
	int numG;

	ScoreReader(int numGames) {
		numG = numGames;
		scores = new Vector<String>();
		try
		{
			File file = new File("scores.txt");
			if (!file.exists()) {
				file.createNewFile();
				String sc = "-1";
				for (int i =0;i < numGames; i++) {
					scores.add(sc);
				}
				save();
			}
			FileInputStream puzzle = new FileInputStream(file);
			DataInputStream in = new DataInputStream(puzzle);
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			
			for(int i=0; i < numG; i++) {
				line = reader.readLine();
				scores.add(line);
			}
		}
		catch (Exception e)
		{
			System.err.println("Error: "+e.getMessage());
		}
	}

	public int getScore(int game){
		return Integer.parseInt(scores.get(game-1));
	}
	
	public void updateScore(int game, int score) {
		Vector<String> newScores= new Vector<String>();
		for (int i=0; i < numG; i++) {
			if((i) == (game-1)) {
				newScores.add(String.valueOf(score));
				continue;
			}
			newScores.add(scores.get(i));
		}
		scores = newScores;
		save();
	}

	//save a file scores.txt 
	public void save() {
		try
		{
			File file = new File("scores.txt");
			file.createNewFile();
			FileOutputStream output = new FileOutputStream(file);
			DataOutputStream outp = new DataOutputStream(output);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outp));

			for (int i =0;i < numG; i++) {
				writer.write(scores.get(i));
				writer.newLine();
			}
			writer.close();
			outp.close();
			output.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: "+e.getMessage());
		}

	}

}
