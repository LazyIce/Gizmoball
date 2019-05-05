package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class HighScores {

	ArrayList<Score> scores;
	
	HighScores() {
		scores = new ArrayList<Score>();
		readFile();
	}
	
	private void readFile() {
		try {
			File file = new File("scores.txt");
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] temp = line.split("\t");
				if (temp.length>1) {
					try {
						addScore(temp[0], Integer.parseInt(temp[1]));
					}
					catch (NumberFormatException e){
						System.out.println("Corrupt high score:" + line);
					}
				}
			}
		} catch (FileNotFoundException e) {
			scores.clear();
		}
	}
	
	public void writeFile() {
		
		try {
			FileWriter fstream = new FileWriter("scores.txt");
	        BufferedWriter out = new BufferedWriter(fstream);
	    
			for (int i=0;i<scores.size();i++) {
				String s = scores.get(i).getName() + "\t" + scores.get(i).getScore() + "\n";
				out.write(s);
			}
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addScore(String a, int b) {
		Score s = new Score(a,b);
		int pos = 0;
		while (pos < scores.size() && s.getScore() <= scores.get(pos).getScore()) {
			pos++;
		}
		if (pos < scores.size()) 
			scores.add(pos, s);
		else scores.add(s);

		if (scores.size() > 5) {
			scores.remove(5);
		}
	}
	
	public boolean isHigh(int s) {
		if (scores.size() < 5)
			return true;
		for (int i=0;i<scores.size();i++) {
			if (s > scores.get(i).getScore()) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Score> getScores() {return scores;}
}
