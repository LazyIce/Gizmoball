package model;

import physics.Circle;
import physics.Geometry;
import physics.Geometry.VectPair;
import physics.LineSegment;
import physics.Vect;

//小球采用了原型模式
public class ActiveBall extends GizmoObject implements Cloneable {

	private double startX;
	private double startY;
	
	private double m_Radius;
	private Circle m_Circle;
	
	private boolean moving;
	private boolean launched;
	private boolean delay;
	
	public ActiveBall() {super();}

	public void Initialise(int mX, int mY, String name, int mL, int mH) {
		super.Initialise(mX, mY, name, mL, mH);
		startX = mX;
		startY = mY;
		super.setType("Ball");
		super.setScore(0);
		
		moving = true;
		delay = true;
		launched = false;
		m_Radius = (double)length/4;
		m_Circle = new Circle(m_Position,m_Radius);
		m_Velocity = new Vect(0,0);
		buildPhysics();
	}
	
	public void reposition(int tx, int ty) {
		startX = tx; startY = ty;
	}
	
	public ActiveBall clone() {
		try {
			return (ActiveBall) super.clone();
		}
		catch( CloneNotSupportedException e ){return null;}
    } 
	
	public void reset() {
		m_Position = new Vect(startX,startY);
		m_Velocity = new Vect(0,0);
		launched = false;
		moving = true;
		delay = true;
	}
	
	public double sX() {return startX;}
	public double sY() {return startY;}
	
	public boolean rotate() {
		return true;
	}
	
	public double getRadius() {return m_Radius;}
	public void setRadius(double rad) {m_Radius = rad;} 
	
	public Circle getCircle() {return m_Circle;}
	
	public void move(double g, double mu, double mu2) {
		if (moving) {
			
			/*限制小球运动的最大速度*/

			if (getSpeed() > 200) {
				//速度不能大于200
				double ax = m_Velocity.x();
				double ay = m_Velocity.y();

				double norm = Math.sqrt((ax*ax)+(ay*ay));
    			double nx = ax/norm;
    			double ny = ay/norm;

    			m_Velocity = new Vect(nx*200, ny*200);
 
			}
			
			double delta_t = 0.04;
			
			double px = m_Position.x();
			double py = m_Position.y();
			double vx = m_Velocity.x();
			double vy = m_Velocity.y();
			
			
			m_Position = new Vect(px+(vx*delta_t), py+(vy*delta_t));
			m_Circle = new Circle(m_Position,m_Radius);

		}
	}

	//小球的速度受重力和摩擦力的影响
	public void degrade(double g, double mu, double mu2) {
		if (moving && delay) {
			
			double delta_t = 0.04;
			
			double px = m_Position.x();
			double py = m_Position.y();
			double vx = m_Velocity.x();
			double vy = m_Velocity.y();
			
			
			//摩擦力的影响
			vx = vx * (1 - mu * delta_t - mu2 * Math.abs(vx) * delta_t);
		    vy = vy * (1 - mu * delta_t - mu2 * Math.abs(vy) * delta_t);
		    //重力的影响
		    vy = vy + (g * delta_t);

			m_Velocity = new Vect(vx,vy);
		}
	}
	

	//检测碰撞时间
	public double timeToCollision(ActiveBall b) {
		double best = Double.POSITIVE_INFINITY;
		double temp = Double.POSITIVE_INFINITY;
		
		//小球在吸收器中
		if (moving)
			temp = Geometry.timeUntilBallBallCollision(b.getCircle(), b.getVelocity(), m_Circle, m_Velocity);
		else temp = Double.POSITIVE_INFINITY;
		return temp;
	}
	
	public Vect collide(ActiveBall b) {
		VectPair temp = Geometry.reflectBalls(m_Circle.getCenter(), 1, m_Velocity, b.getCircle().getCenter(), 1, b.getVelocity());
		m_Velocity = temp.v1;
		trigger();
		return temp.v2;
	}
	
	void setCollided(double time) {
		//设置小球的位置为碰撞的位置
		double x = m_Position.x();
		double y = m_Position.y();
		
		x = x + m_Velocity.x()*time;
		y = y + m_Velocity.y()*time;
		setPosition(x,y);

	}
	
	public double getSpeed() {
		//计算小球的速度（标量）
		double x = m_Velocity.x();
		double y = m_Velocity.y();
		
		return Math.sqrt((x*x)+(y*y));
	}
	
	public void setMoving(boolean v) {
		moving = v;
	}
	boolean getMoving() {return moving;}

	public void setPosition(double myX, double myY) {
		m_Position = new Vect(myX, myY);
		m_Circle = new Circle(m_Position,m_Radius);
		
	}
	
	public void setLaunched(boolean v) {
		launched = v;
	}
	public boolean getLaunched() {return launched;}
	
	public void setDelay(boolean b) {delay = b;}
	

}
