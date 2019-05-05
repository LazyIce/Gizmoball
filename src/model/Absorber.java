package model;

import java.io.File;
import java.util.ArrayList;

import physics.Angle;
import physics.Geometry;
import physics.Vect;

public class Absorber extends GizmoObject{
	ArrayList<ActiveBall> ballList;

	private boolean trig;
	
	public Absorber() {super();}

	public void Initialise(int mX, int mY, String name, int mL, int mH) {
		super.Initialise(mX, mY, name, mL, mH);
		
		super.setType("Absorber");
		
		super.setScore(0);
		
		ballList = new ArrayList<ActiveBall>();
		
		m_Points.add(new Vertex(mX,mY));
		m_Points.add(new Vertex(mX+length,mY));
		m_Points.add(new Vertex(mX+length,mY+height));
		m_Points.add(new Vertex(mX,mY+height));
		
		buildPhysics();
		setSoundEffect(new File("SoundEffects/Launch.wav"));
	
		trig = false;
	}

	//往吸收器中加一个小球
	public void addBall(ActiveBall b) {
		ballList.add(b);
	}
	
	public void clear() {
		//进入build模式时，清空吸收器中的球
		ballList.clear();
	}
	
	public void go() {
		//发射器
		System.out.println(ballList.size());
		if (ballList.size() > 0) {
			ballList.get(0).setMoving(true);
			ballList.get(0).setLaunched(true);
			ballList.get(0).setDelay(false);
			ballList.get(0).setVelocity(new Vect(0,-50));
			ballList.remove(0);
		}
		
	}
	
	public Vertex getCorner() {
		return m_Points.get(2);
	}
	
	public int getBallCount() {
		return ballList.size();
	}

	@Override
	public void trigger() {
		go();
		bonk();
		getSoundEffect().play();
		trig = true;
	}
	
	public boolean rotate() {
		super.rotate();
		double midX = 0.5 + x;
		double midY = 0.5 + y;

		int temp = length;
		length = height;
		height = temp;
		
		m_Points.clear();
		m_Points.add(new Vertex(x,y));
		m_Points.add(new Vertex(x+length,y));
		m_Points.add(new Vertex(x+length,y+height));
		m_Points.add(new Vertex(x,y+height));
		
		buildPhysics();
		
		return true;
	}
	
	@Override
	public void triggerConnected() {
		for (GizmoObject gizmo : m_connectedGizmos) {
			if(gizmo!= this)
				gizmo.trigger();
			else if (!trig) {
				gizmo.trigger();
			}
			trig = false;
		}
	}
}
