package model;

import java.io.File;

public class TriangleBumper extends GizmoObject{

	public TriangleBumper() {super();}

	public void Initialise(int mX, int mY, String name, int mL, int mH) {
		super.Initialise(mX, mY, name, mL, mH);
		
		super.setType("Triangle");
		
		m_Points.add(new Vertex(mX,mY));
		m_Points.add(new Vertex(mX+length,mY));
		m_Points.add(new Vertex(mX,mY+height));
		
		buildPhysics();
		
		super.setSoundEffect(new File("SoundEffects/Square.wav"));
	}

	//旋转三角形90度角
	public boolean rotate() {
		super.rotate();
		
		double minX = m_Points.get(0).getX();
		double maxX = m_Points.get(0).getX();
		double minY = m_Points.get(0).getY();
		double maxY = m_Points.get(0).getY();
		
		for (int i=0;i<m_Points.size();i++) {
			double curX = m_Points.get(i).getX();
			double curY = m_Points.get(i).getY();
			if (curX < minX) minX = curX;
			if (curX > maxX) maxX = curX;
			if (curY < minY) minY = curY;
			if (curY > maxY) maxY = curY;
		}
		
		double lengthX = maxX-minX;
		double lengthY = maxY-minY;
		double midX = minX + (lengthX/2);
		double midY = minY + (lengthY/2);
		
		for (int i=0;i<m_Points.size();i++) {
			double curX = m_Points.get(i).getX();
			double curY = m_Points.get(i).getY();
			if (curX < midX && curY < midY) 
				m_Points.get(i).setX(curX+lengthX);
			else if (curX < midX && curY > midY) 
				m_Points.get(i).setY(curY-lengthY);
			else if (curX > midX && curY < midY) 
				m_Points.get(i).setY(curY+lengthY);
			else if (curX > midX && curY > midY) 
				m_Points.get(i).setX(curX-lengthX);
		}
		
		buildPhysics();
		return true;
	}
	
	@Override
	public void trigger() {
		bonk();
		getSoundEffect().play();
	}
}
