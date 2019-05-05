package model;

public class Score {

	private int m_Score;
	private String m_Name;
	
	public Score(String name, int score) {
		m_Score = score;
		m_Name = name;
	}
	
	public int getScore() {return m_Score;}
	public String getName() {return m_Name;}
	
}
