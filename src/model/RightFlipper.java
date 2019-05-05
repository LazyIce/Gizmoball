package model;

import java.io.File;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class RightFlipper extends GizmoObject{

	private SoundClip bonk;
	
	private double angle;
	private double thick;
	
	private double speed;
	private double angVel;
	
	private double hingeX;
	private double hingeY;
	private double tipX;
	private double tipY;

	private boolean moving;
	private boolean forward;
	private boolean first;
	
	private double rotation;
	
	private int boingFwip = 2;
	
	public RightFlipper() {
		super();
		angle = 0;
		thick = 0;
		hingeX = 0;
		tipX = 0;
		hingeY = 0;
		tipY = 0;
		rotation = 0;
		angVel = 0;
	}
	
	public void Initialise(int mX, int mY, String name, int mL, int mH) {
		super.Initialise(mX, mY, name, mL, mH);
		
		super.setType("RightFlipper");
		
		thick = (double)length/8;
		
		hingeX = mX+length-thick;
		hingeY = mY+thick;
		
		tipX = mX+length-thick;
		tipY = mY+height-thick;
		
		speed = 0;
		
		moving = false;
		forward = false;
		first = false;
		
		angle = 0;
		rotation = 0;
		m_Bounce = 0.95;
		angVel = 0;
		
		update();
		bonk = new SoundClip(new File("SoundEffects/Square.WAV"));
		setSoundEffect(new File("SoundEffects/flipper1.wav"));
	}
	
	public boolean rotate() {

		double midX = (length/2) + x;
		double midY = (height/2) + y;
		
		Vect tempa = Geometry.rotateAround(
				new Vect(hingeX, hingeY),
				new Vect(midX, midY),
				new Angle(Math.toRadians(90)));
		
		hingeX = tempa.x();
		hingeY = tempa.y();
		
		Vect tempb = Geometry.rotateAround(
				new Vect(tipX, tipY),
				new Vect(midX, midY),
				new Angle(Math.toRadians(90)));
		
		tipX = tempb.x();
		tipY = tempb.y();

		rotation = rotation + 90;
		if (rotation < 0) rotation = 0;
		else if (rotation >= 360) rotation = 0;
		update();
		return true;
	}
	
	void go() {
		if (!moving) {moving = true; }
		first = true;
		forward = !forward;
	}
	
	public void reset() {
		speed = 0;	
		moving = false;
		forward = false;
		first = false;
		angle = 0;
		rotation = 0;
		angVel = 0;
		update();
	}

	void move() {
		if (moving) {
			
			if (forward) angVel = -1080;
			else speed = angVel = 1080;
			
			if (first) {first = false;} 
			else {
				double delta_t = 40.0/1000;

				speed = angVel*delta_t;
				
				angle += speed;
				if (angle <=-90) { moving = false; angle = -90;}
				if (angle >=0) {moving = false; angle = 0;}
				update();
			}
		}
		
	}
	
	public double timeToCollision(ActiveBall b) {
		double best = Double.POSITIVE_INFINITY;
		double temp = Double.POSITIVE_INFINITY;
		
		if (moving) {
			for (int i=0;i<m_Colliders.size();i++) {
				Object collider = m_Colliders.get(i);
				double angular = Math.toRadians(angVel);
				
				if (collider instanceof physics.LineSegment) {
					temp = Geometry.timeUntilRotatingWallCollision(
							(LineSegment)collider, 
							new Vect(hingeX, hingeY),
							-angular, 
							b.getCircle(), 
							b.getVelocity());
				}
				else if (collider instanceof physics.Circle)
					temp = Geometry.timeUntilRotatingCircleCollision(
							(Circle)collider, 
							new Vect(hingeX, hingeY), 
							-angular, 
							b.getCircle(), 
							b.getVelocity());
				if (temp<best) best = temp;
			}
			return best;
		}
		else {
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
	}
	
	public Vect collide(ActiveBall b) {
		double best = Double.POSITIVE_INFINITY;
		int bestID = 0;
		double temp = Double.POSITIVE_INFINITY;
		
		if (moving) {
			double angular = Math.toRadians(angVel);
			
			for (int i=0;i<m_Colliders.size();i++) {
				Object collider = m_Colliders.get(i);
				if (collider instanceof physics.LineSegment)
					temp = Geometry.timeUntilRotatingWallCollision(
							(LineSegment)collider, 
							new Vect(hingeX, hingeY), 
							-angular,
							b.getCircle(),
							b.getVelocity()
							);
				else if (collider instanceof physics.Circle)
					temp = Geometry.timeUntilRotatingCircleCollision(
							((Circle)collider), 
							new Vect(hingeX, hingeY), 
							-angular, 
							b.getCircle(), 
							b.getVelocity()
							);
				if (temp<best) {best = temp;bestID = i;}
			}
			
			Object collider = m_Colliders.get(bestID);
			Vect velocity = null;
			
			if (collider instanceof physics.LineSegment)
				velocity = Geometry.reflectRotatingWall(
						(LineSegment)collider, 
						new Vect(hingeX, hingeY), 
						-angular,
						b.getCircle(),
						b.getVelocity(), 
						b.getBounce()*m_Bounce);
			if (collider instanceof physics.Circle)
				velocity = Geometry.reflectRotatingCircle(
						((Circle)collider), 
						new Vect(hingeX, hingeY), 
						-angular, 
						b.getCircle(), 
						b.getVelocity(),
						b.getBounce()*m_Bounce);
			return velocity;
			
		}
		else {
			
			for (int i=0;i<m_Colliders.size();i++) {
				Object collider = m_Colliders.get(i);
				if (collider instanceof physics.LineSegment)
					temp = Geometry.timeUntilWallCollision((LineSegment)collider, b.getCircle(), b.getVelocity());
				else if (collider instanceof physics.Circle)
					temp = Geometry.timeUntilCircleCollision((Circle)collider, b.getCircle(), b.getVelocity());
				if (temp<best) {best = temp;bestID = i;}
			}
			
			Object collider = m_Colliders.get(bestID);
			Vect velocity = null;
			
			if (collider instanceof physics.LineSegment)
				velocity = Geometry.reflectWall((LineSegment)collider, b.getVelocity(), b.getBounce()*m_Bounce);
			if (collider instanceof physics.Circle)
				velocity = Geometry.reflectCircle(((Circle)collider).getCenter(), b.getPosition(), b.getVelocity(), b.getBounce()*m_Bounce);
			triggerConnected();
			
			bonk.play();
			
			return velocity;
		}
		
	}
	
	
	public void update() {
		double rads = Math.toRadians(360 - angle);
		
		double sin = Math.sin(rads);
		double cos = Math.cos(rads);
		
		double tempX = tipX - hingeX;
		double tempY = tipY - hingeY;
		
		double retX = (tempX * cos - tempY * sin)+hingeX;
		double retY = (tempY * cos + tempX * sin)+hingeY;

		double tempRad = Math.toRadians(angle-rotation-90);
		double xOff = Math.sin(tempRad) * thick;
		double yOff = Math.cos(tempRad) * thick;

		m_Points.clear();
		m_Points.add(new Vertex(hingeX+xOff,hingeY+yOff));
		m_Points.add(new Vertex(hingeX-xOff,hingeY-yOff));
		m_Points.add(new Vertex(retX-xOff,retY-yOff));
		m_Points.add(new Vertex(retX+xOff,retY+yOff));
		buildPhysics();
	}
	
	public double getRadius() {
		return thick;
	}
	
	
	public void buildPhysics() {
		
		m_Colliders.clear();
		
		super.buildPhysics();

		double hingeX = (m_Points.get(0).getX() + m_Points.get(1).getX())/2;
		double hingeY = (m_Points.get(0).getY() + m_Points.get(1).getY())/2;
		
		double tipX = (m_Points.get(2).getX() + m_Points.get(3).getX())/2;
		double tipY = (m_Points.get(2).getY() + m_Points.get(3).getY())/2;
		
		m_Colliders.add(new Circle(hingeX, hingeY, thick));
		m_Colliders.add(new Circle(tipX, tipY, thick));
	}

	@Override
	public void trigger() {
		go();
		bonk();
		if (boingFwip == 2) {
			boingFwip = 1;
			this.setSoundEffect(new File("SoundEffects/flipper1.wav"));
		} else {
			boingFwip = 2;
			this.setSoundEffect(new File("SoundEffects/flipper2.wav"));
		}
		
		this.getSoundEffect().play();
	}
}

