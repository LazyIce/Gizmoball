package model;

public class Vertex {

	double x;
	double y;
	
	Vertex(double mX, double mY) {
		x = mX;
		y = mY;
	}
	
	public double getX() {return x;}
	public double getY() {return y;}
	
	void setX(double mX) {x = mX;}
	void setY(double mY) {y = mY;}
}