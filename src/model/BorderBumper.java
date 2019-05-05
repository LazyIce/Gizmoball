package model;

import java.io.File;

public class BorderBumper extends GizmoObject{

	BorderBumper() {super();}
	
	public void Initialise(int mX, int mY, String name, int mL, int mH) {
		super.Initialise(mX, mY, name, mL, mH);
		
		super.setType("BorderBumper");
		
		m_Points.add(new Vertex(mX,mY));
		m_Points.add(new Vertex(mX+length,mY));
		m_Points.add(new Vertex(mX+length,mY+height));
		m_Points.add(new Vertex(mX,mY+height));
		
		buildPhysics();
		this.setSoundEffect(new File("SoundEffects/Square.wav"));
		
	}
	
	public boolean rotate() {
		super.rotate();
		
		return true;
	}
	
	@Override
	public void trigger() {
		this.getSoundEffect().play();
	}
}
