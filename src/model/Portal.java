package model;

import java.io.File;
import java.util.ArrayList;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/* 传送门，当把两个传送门关联起来时，小球从一个portal进去，从另一个portal出去*/
public class Portal extends GizmoObject {

	private SoundClip whoosh;

	private int rotation;
	
	private boolean hit = false;
	private boolean on = true;
	double thick;
	
	public void Initialise(int mX, int mY, String name, int mL, int mH) {
		super.Initialise(mX, mY, name, mL, mH);

		thick = 0.125 * mL; //整个组件区域的1/8
		rotation = 0;
		
		double square = 0.5*mL;
		double half = square / 2;
		super.setType("Portal");
		

		m_Points.add(new Vertex(mX,mY));
		m_Points.add(new Vertex(mX+square,mY));
		m_Points.add(new Vertex(mX+square,mY+half));
		m_Points.add(new Vertex(mX+half,mY+half));
		m_Points.add(new Vertex(mX+half,mY+height-half));
		m_Points.add(new Vertex(mX+square,mY+height-half));
		m_Points.add(new Vertex(mX+square,mY+height));
		m_Points.add(new Vertex(mX,mY+height));
		
		buildPhysics();
		
		super.setSoundEffect(new File("SoundEffects/Square.wav"));
		whoosh = new SoundClip(new File("SoundEffects/Portal.wav"));
	}
	
	public void buildPhysics() {
		
		m_Colliders.clear();
		
		super.buildPhysics();
	}
	
	//关闭传送功能
	public void enabled(boolean b) {on = b;}
	
	
	public boolean rotate() {
		super.rotate();
		double midX = (length/2) + x;
		double midY = (height/2) + y;
		
		for (int i=0;i<m_Points.size();i++) {
			Vect temp = Geometry.rotateAround(
					new Vect(m_Points.get(i).getX(), m_Points.get(i).getY()),
					new Vect(midX, midY),
					new Angle(Math.toRadians(90)));
			m_Points.set(i, new Vertex(temp.x(), temp.y()));
		}

		buildPhysics();
		rotation++;
		if (rotation>3) rotation -= 4;

		return true;
	}
	
	public double timeToCollision(ActiveBall b) {
		//检测与每条线碰撞的时间，取最小值
		double best = Double.POSITIVE_INFINITY;
		double temp = Double.POSITIVE_INFINITY;
		

		for (int i=0;i<m_Colliders.size();i++) {
			Object collider = m_Colliders.get(i);
				
			if (collider instanceof physics.LineSegment)
				temp = Geometry.timeUntilWallCollision((LineSegment)collider, b.getCircle(), b.getVelocity());
			else if (collider instanceof physics.Circle)
				temp = Geometry.timeUntilCircleCollision((Circle)collider, b.getCircle(), b.getVelocity());
			if (temp<best) best = temp;
		}
		return best;
	}
	
	public int collide(ArrayList<GizmoObject> gizmos, int ballID) {
		//获取多生成的球
		int made = 0;
		double best = Double.POSITIVE_INFINITY;
		int bestID = 0;
		double temp = Double.POSITIVE_INFINITY;
		
		ActiveBall b = (ActiveBall)gizmos.get(ballID);
		
		for (int i=0;i<m_Colliders.size();i++) {
			Object collider = m_Colliders.get(i);
			if (collider instanceof physics.LineSegment)
				temp = Geometry.timeUntilWallCollision((LineSegment)collider, b.getCircle(), b.getVelocity());
			else if (collider instanceof physics.Circle)
				temp = Geometry.timeUntilCircleCollision((Circle)collider, b.getCircle(), b.getVelocity());
			if (temp<best) {best = temp;bestID = i;}
		}
		
		int id = -1;
		int i = 0;
		boolean found = false;
		boolean self = false;
		if (bestID==6 && on) {
			while (i<m_connectedGizmos.size()) {
				if (m_connectedGizmos.get(i) instanceof Portal) {
					//找到关联的传送出口
					teleport((Portal)m_connectedGizmos.get(i), gizmos, ballID);
					made++;
					if (m_connectedGizmos.get(i).getName().equals(this.getName()))
						self = true;
					found = true;
					hit = true;
					((Portal)m_connectedGizmos.get(i)).setHit(true);
				}
				i++;
			}
			if (found) {
				//删除原来的球
				gizmos.remove(ballID);
				trigger();
				if (self) {hit = true; self = false;}
				triggerConnected();
				return made-1;
			}
		}
			
		Object collider = m_Colliders.get(bestID);
		Vect velocity = null;
		
		if (collider instanceof physics.LineSegment)
			velocity = Geometry.reflectWall((LineSegment)collider, b.getVelocity(), b.getBounce()*m_Bounce);
		if (collider instanceof physics.Circle)
			velocity = Geometry.reflectCircle(((Circle)collider).getCenter(), b.getPosition(), b.getVelocity(), b.getBounce()*m_Bounce);
		trigger();
		triggerConnected();
		this.setSoundVolume(b);
		b.setVelocity(velocity);
		
		trigger();
		triggerConnected();
		
		return 0;
	}
	
	public void teleport(Portal out, ArrayList<GizmoObject> gizmos, int ballID) {
		
		ActiveBall b = (ActiveBall)gizmos.get(ballID);

		double ballX = b.getPosition().x() - x;
		double ballY = b.getPosition().y() - y;

		int mul = getRotateCount() - out.getRotateCount();
		
		Vect newpos = Geometry.rotateAround(
				new Vect(ballX, ballY), 
				new Vect(1,1), 
				new Angle(-Math.toRadians(90*mul))
		);
		
		Vect vel = b.getVelocity();
		
		if (Math.abs(mul)%4==0)mul = 2;
		else if (Math.abs(mul)%4==2)mul = 0;
		//
		Vect newvel = Geometry.rotateAround(
				b.getVelocity(), 
				new Vect(0,0), 
				new Angle(Math.toRadians(90*mul))
		);
		
		
		Vect offset = out.getPosition().plus(newpos);
		
		ActiveBall dupe = b.clone();
		dupe.setPosition(offset.x(), offset.y());
		dupe.setVelocity(newvel);
		gizmos.add(dupe);
		out.setHit(true);
		return;
	}
	
	public void setHit(boolean h) {hit = h;}
	
	
	@Override
	public void trigger() {
		if (hit)
			whoosh.play();
		else getSoundEffect().play();
		bonk();
		hit = false;
	}

}
