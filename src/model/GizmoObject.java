package model;


import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import controller.BoardAction;

import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public abstract class GizmoObject implements BoardAction {

	protected List m_Colliders;   //轮廓边界的列表
	protected List<Vertex> m_Points;   //顶点的列表

	protected Vect m_Position;   //位置
	protected double m_Bounce;
	protected Vect m_Velocity;   //速度

	private String m_Name;   //组件名

	protected int x;   //点的横坐标
	protected int y;   //点的纵坐标
	protected int length;   //组件的长度
	protected int height;   //组件的高度
	private String m_type;   //组件的类型
	private SoundClip soundEffectFile;   //组件对应的音频剪辑
	private int m_score;   //分数

	protected List<GizmoObject> m_connectedGizmos;   //组件的关联
	private int m_rotateCount;   //组件的旋转次数

	// Gizmo组件是否被小球碰撞
	boolean m_Bonk;
	int timer;

	public GizmoObject() {
		m_Name = "ERROR";
		m_Bounce = 0.0;
		m_Position = new Vect(0, 0);
		m_Colliders = new ArrayList<LineSegment>();
		m_Points = new ArrayList<Vertex>();
		m_Velocity = new Vect(0, 0);
		m_rotateCount = 0;
		m_connectedGizmos = new LinkedList<GizmoObject>();
		m_score = 100;
	}

	public void Initialise(int mX, int mY, String name, int mL, int mH) {
		m_Name = name;
		x = mX;
		y = mY;
		length = mL;
		height = mH;
		m_Bounce = 1.0;
		m_Position = new Vect(mX, mY);
		m_Velocity = new Vect(0, 0);
		m_rotateCount = 0;
		m_connectedGizmos = new LinkedList<GizmoObject>();
		m_type = "GizmoObject";
		m_Bonk = false;
		m_Points.clear();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getL() {
		return length;
	}

	public int getH() {
		return height;
	}

	public String getType() {
		return m_type;
	}

	public void bonk() {
		//组件被小球碰撞
		m_Bonk = true;
		timer = 50;
	}

	public void unbonk() {
	    //计时结束，组件的变色效果取消
		timer--;
		if (timer <= 0)
			m_Bonk = false;
	}

	public boolean bonked() {
		return m_Bonk;
	}

	void setPosition(double nx, double ny) {
		x = (int) (nx + 0.5);
		y = (int) (ny + 0.5);
		Vect v = new Vect(nx, ny);
		m_Position = v;
		int oldRot = m_rotateCount;
		Initialise(x, y, m_Name, length, height);
		for (int i=0;i<oldRot;i++) {
			rotate();
		}
	}

	public void setVelocity(Vect v) {
		m_Velocity = v;
	}

	public String getName() {
		return m_Name;
	}

	double getBounce() {
		return m_Bounce;
	}

	public Vect getPosition() {
		return m_Position;
	}

	public Vect getVelocity() {
		return m_Velocity;
	}

	public List<Vertex> getPoints() {
		return m_Points;
	}

	public int getScore() {
		return m_score;
	}

	public void setScore(int score) {
		m_score = score;
	}

	public boolean rotate() {
		m_rotateCount++;
		return false;
	}

	public double timeToCollision(ActiveBall b) {
		//计算小球最近一次发生碰撞的时间
		double best = Double.POSITIVE_INFINITY;
		double temp = Double.POSITIVE_INFINITY;
		for (int i = 0; i < m_Colliders.size(); i++) {
			Object collider = m_Colliders.get(i);

			if (collider instanceof physics.LineSegment)
				temp = Geometry.timeUntilWallCollision((LineSegment) collider,
						b.getCircle(), b.getVelocity());
			else if (collider instanceof physics.Circle)
				temp = Geometry.timeUntilCircleCollision((Circle) collider,
						b.getCircle(), b.getVelocity());
			if (temp < best)
				best = temp;
		}
		return best;
	}

	public Vect collide(ActiveBall b) {
		double best = Double.POSITIVE_INFINITY;
		int bestID = 0;
		double temp = Double.POSITIVE_INFINITY;
		for (int i = 0; i < m_Colliders.size(); i++) {
			Object collider = m_Colliders.get(i);
			if (collider instanceof physics.LineSegment)
				temp = Geometry.timeUntilWallCollision((LineSegment) collider,
						b.getCircle(), b.getVelocity());
			else if (collider instanceof physics.Circle)
				temp = Geometry.timeUntilCircleCollision((Circle) collider,
						b.getCircle(), b.getVelocity());
			if (temp < best) {
				best = temp;
				bestID = i;
			}
		}

		Object collider = m_Colliders.get(bestID);
		Vect velocity = null;

		if (collider instanceof physics.LineSegment)
			velocity = Geometry.reflectWall((LineSegment) collider,
					b.getVelocity(), b.getBounce() * m_Bounce);
		if (collider instanceof physics.Circle)
			velocity = Geometry.reflectCircle(((Circle) collider).getCenter(),
					b.getPosition(), b.getVelocity(), b.getBounce() * m_Bounce);
		// Gizmo is triggered.
		trigger();
		triggerConnected();
		setSoundVolume(b);
		return velocity;
	}

	//设置小球与组件碰撞的音效音量
	protected void setSoundVolume(ActiveBall b) {
		double d = b.getSpeed()/4;
		float f = 0.0f;
		if (d > 6.0) {
			f = 6.0f;
		} else if (d < 1.0 && d > 0.0) {
			f = ((float) d);
			f = (-10.0f) / f;
			if (f < -70.0f) {
				f = -70.0f;
			}
		}else{
			f = 0.0f;
		}
		getSoundEffect().setVolume(f);
	}

	protected void buildPhysics() {
		m_Colliders.clear();
		int total = m_Points.size();
		for (int i = 0; i < total; i++) {
			int next = i + 1;
			if (next >= total)
				next = 0;
			m_Colliders.add(new LineSegment(m_Points.get(i).getX(), m_Points.get(i).getY(),
                    m_Points.get(next).getX(), m_Points.get(next).getY()));

			m_Colliders.add(new Circle(m_Points.get(i).getX(), m_Points.get(i).getY(), 0));
		}
	}

	public void triggerConnected() {
		for (GizmoObject gizmo : m_connectedGizmos) {
			gizmo.trigger();
		}
	}

	public void trigger() {

	}

	public void connectGizmo(GizmoObject gizmo) {
		m_connectedGizmos.add(gizmo);
	}

	public void disconnectGizmo(GizmoObject gizmo) {
		m_connectedGizmos.remove(gizmo);
	}

	public List<GizmoObject> getConnectedGizmos() {
		return m_connectedGizmos;
	}

	public int getRotateCount() {
		return m_rotateCount;
	}

	public void setType(String string) {
		m_type = string;
	}

	public void setColliders(List m_Colliders) {
		this.m_Colliders = m_Colliders;
	}

	public List getColliders() {
		return m_Colliders;
	}

	public SoundClip getSoundEffect() {
		return soundEffectFile;
	}

	public void setSoundEffect(File f) {
		soundEffectFile = new SoundClip(f);
	}

}