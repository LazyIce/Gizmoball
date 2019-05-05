package model;

import java.io.File;
import java.util.Random;

import physics.Circle;
import physics.LineSegment;

public class CircleBumper extends GizmoObject{
	
	Random rnd = new Random();
	
	public CircleBumper() {super();}

	public void Initialise(int mX, int mY, String name, int mL, int mH) {
		super.Initialise(mX, mY, name, mL, mH);
		
		super.setType("Circle");
		
		m_Points.add(new Vertex(mX,mY));
		m_Points.add(new Vertex(mX+length,mY));
		m_Points.add(new Vertex(mX+length,mY+height));
		m_Points.add(new Vertex(mX,mY+height));
		
		buildPhysics();
		
		super.setSoundEffect(new File("SoundEffects/Square.wav"));
	}
	
	public boolean rotate() {
		super.rotate();
		return true;
	}
	
	protected void buildPhysics() {

		double xStart = m_Points.get(0).getX();
		double yStart = m_Points.get(0).getY();
		double xEnd = m_Points.get(2).getX();
		
		double radius = (xEnd-xStart)/2;
		double posX = xStart + radius;
		double posY = yStart + radius;
		
		m_Colliders.add(new Circle(posX, posY, radius));

	}

	@Override
	public void trigger() {
		bonk();
		getSoundEffect().play();
	}
	
}
