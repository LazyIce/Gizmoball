package model;

import java.io.File;

public class SquareBumper extends GizmoObject{

	public SquareBumper() {super();}
	
	public void Initialise(int mX, int mY, String name, int mL, int mH) {
		super.Initialise(mX, mY, name, mL, mH);
		
		super.setType("Square");
		
		m_Points.add(new Vertex(mX,mY));
		m_Points.add(new Vertex(mX+length,mY));
		m_Points.add(new Vertex(mX+length,mY+height));
		m_Points.add(new Vertex(mX,mY+height));
		
		buildPhysics();
		
		super.setSoundEffect(new File("SoundEffects/Square.wav"));
	}
	
	public boolean rotate() {
		//旋转正方形90度
		super.rotate();
		return true;
	}
	
	@Override
	public void trigger() {
		bonk();
		getSoundEffect().play();
	}
}
