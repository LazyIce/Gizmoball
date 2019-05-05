package model;

import controller.BoardAction;
import controller.KeyConnection;
import controller.KeyConnection.Direction;
import physics.Vect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;

/* 观察者模式，棋盘区域是被观察者 */
public class Board extends Observable {
	
	public boolean debug;

	private double m_MaxX;   //x轴方向的边界
	private double m_MaxY;   //y轴方向的边界

	private double m_Gravity;   //重力值
	private double m_Mu;   //摩擦系数1
	private double m_Mu2;   //摩擦系数2

	private int m_X;   //横坐标
	private int m_Y;   //纵坐标

	private Mode m_Mode;   //游戏模式

	private ArrayList<GizmoObject> m_Gizmos;   //Gizmo组件列表

	private List<KeyConnection> m_keys;   //Gizmo组件关联的按键
	
	private HighScores m_highScores;   //该场景的高分
	private int m_score;   //获得的分数
	
	private int m_lives;   //生命条数
	public int balls;   //小球的数量
	
	/* 格式枚举类型确定解析器可以存储和加载的文件*/
	public static enum Format { STANDARD, EXTENDED };

	/* 构造函数 */
	public Board() {

		debug = false;
		
		m_Mode = Mode.PLAY_MODE_RUNNING;

		m_MaxX = 20;
		m_MaxY = 20;

		m_X = 20;
		m_Y = 20;
		balls = 0;
		m_Gravity = 25;

		setFriction(0.025, 0.025);

		m_Gizmos = new ArrayList<GizmoObject>();

        /*游戏区域边界模型*/
		BorderBumper border = new BorderBumper();
		border.Initialise(0, 0, "OuterWalls", m_X, m_Y);
		m_Gizmos.add(border);

		m_keys = new ArrayList<KeyConnection>();
		
		m_score = 0;
		m_highScores = new HighScores();
		m_lives = 3;

		int balls = 0;
		
	}
	
	public Format getFormat()
	{
		Format format = Format.STANDARD;
		
		for (GizmoObject g : getGizmos()) {
			if (g instanceof Portal) {
				format = Format.EXTENDED;
				break;
			}
		}
		
		return format;
	}

	public void resetScore()
	{
		m_score = 0;
	}
	
	public int getLives()
	{
		return m_lives;
	}
	
	public void setLives(int lives)
	{
		m_lives = lives;
	}
	
	public void loseLife()
	{
		m_lives -= 1;
	}
	
	public void addToScore(int score)
	{
		m_score += 10;
	}
	
    /* 添加Gizmo组件的按键*/
	public void addKeyConnection(KeyConnection connection) throws Exception {
		if (m_keys.contains(connection)) {
			throw new Exception ("A key connection with the key " + connection.toString() + " already exists on the board.");
		} else {
			m_keys.add(connection);	
		}
	}

    /* 去除Gizmo组件的按键*/
	public void removeKeyConnection(KeyConnection connection) throws Exception {
		if (m_keys.contains(connection)) {
			m_keys.remove(connection);
		} else {
			throw new Exception ("A key connection with the key " + connection.getKey() + " does not exist on the board.");
		}
	}

	/*获取Gzimo组件的按键*/
	public List<KeyConnection> getKeyConnections() {
		return m_keys;
	}

	/*清楚游戏区域中的所有Gizmo组件和关联的按键，初始化游戏区域 */
	public void clear() {
		m_keys.clear();
		m_Gizmos.clear();
		BorderBumper border = new BorderBumper();
		border.Initialise(0, 0, "OuterWalls", m_X, m_Y);
		m_Gizmos.add(border);
		update();
	}

	/*添加Gizmo组件*/
	public void addGizmo(GizmoObject gizmo, int x, int y, String name) throws Exception {
		try {
                addGizmo(gizmo, x, y, 1, 1, name);
		}
		catch (Exception e) {
			throw e;
		}
	}

	public void addGizmo(GizmoObject gizmo, double x, double y, String name) throws Exception {
		try {
			addGizmo(gizmo, (int) x, (int) y, name);
		}
		catch (Exception e) {
			throw e;
		}
	}

	public void addGizmo(GizmoObject gizmo, int x, int y, int length, int height, String name) throws Exception {
		if (gizmo instanceof LeftFlipper || gizmo instanceof RightFlipper || gizmo instanceof Portal ) {
			length = length * 2;
			height = height * 2;
		}
		if (gizmo instanceof Block) {
		    length = length * 3;
        }
		if (gizmo instanceof ActiveBall) {
			balls++;
		}
		gizmo.Initialise(x, y, name, length, height);
		boolean nameDuplicate = false;
		boolean occupied = false;
		for (int i = 0; i < m_Gizmos.size(); i++) {
			if (!nameDuplicate)
				nameDuplicate = m_Gizmos.get(i).getName().equals(name);
			if (!occupied) {
				int myX = gizmo.getX();
				int myY = gizmo.getY();
				int myL = gizmo.getL();
				int myH = gizmo.getH();
				occupied = occupied(myX, myY, myL, myH, -1);
				if (occupied) {
					System.out.println("Occupied!");
				}
			}
		}

		if (x > m_X && y > m_Y) {
			throw new Exception("The coordinates x and y are out of bounds for gizmo " + name + ".");
		} else if (x > m_X) {
			throw new Exception("The x coordinate is out of bounds for gizmo " + name + ".");
		} else if (y > m_Y) {
			throw new Exception("The y coordinate is out of bounds for gizmo " + name + ".");
		} else if (x + length > m_X) {
			throw new Exception("The width goes beyond the bounds of the board for gizmo " + name + ".");
		} else if (y + height > m_Y) {
			throw new Exception("The height goes beyond the bounds of the board for gizmo " + name + ".");
		}

		if (nameDuplicate) {
			throw new Exception("A gizmo already exists on the board with the name " + name + ".");
		} else if (occupied) {
			throw new Exception("A gizmo already exists on the board at position " + x + " " + y + ".");
		} else {
			m_Gizmos.add(gizmo);
		}
	}

	// needs big cleanup
	public void addAbsorber(Absorber gizmo, int x1, int y1, int x2, int y2, String name) throws Exception {
		if (x1 > m_X && x2 > m_X) {
			throw new Exception("x1 and x2 position for absorber gizmo " + name
					+ " is beyond the bounds of the board.");
		} else if (x1 > m_X) {
			throw new Exception("x1 position for absorber gizmo " + name
					+ " is beyond the bounds of the board.");
		} else if (x2 > m_X) {
			throw new Exception("x2 position for absorber gizmo " + name
					+ " is beyond the bounds of the board.");
		}

		int length = x2 - x1;
		int height = y2 - y1;

		if (length > m_X && height > m_Y) {
			throw new Exception("The length and height for absorber gizmo "
					+ name + " is beyond the bounds of the board.");
		} else if (length > m_X) {
			throw new Exception("The length for absorber gizmo " + name
					+ " is beyond the bounds of the board.");
		} else if (height > m_Y) {
			throw new Exception("The height for absorber gizmo " + name
					+ " is beyond the bounds of the board.");
		} else {
			addGizmo(gizmo, x1, y1, length, height, name);
		}
	}

	/* 添加小球 */
	public void addBall(ActiveBall ball, double x, double y, double vx, double vy, String gizmoName) throws Exception {
		ball.setVelocity(new Vect(x, y));
		addGizmo(ball, x, y, gizmoName);
	}

	//判断这块游戏区域是否已被使用
	boolean occupied(int myX, int myY, int myL, int myH, int index) {

		for (int i = 0; i < m_Gizmos.size(); i++) {
			GizmoObject temp = m_Gizmos.get(i);

			if (!(temp instanceof BorderBumper) && i != index) {
				int itX = temp.getX();
				int itY = temp.getY();
				int itL = temp.getL();
				int itH = temp.getH();

				boolean foundX = false;
				boolean foundY = false;
				
				/*遍历游戏区域的每个点，判断其是否为Gizmo组件的边界*/
				
				for (int j=myX; j<myX+myL;j++) {
					for (int k=itX;k<itX+itL;k++) {
						if (j==k) foundX = true;
					}
				}
				
				for (int j=myY; j<myY+myH;j++) {
					for (int k=itY;k<itY+itH;k++) {
						if (j==k) foundY = true;
					}
				}
				
				if (foundX&&foundY) {
					return true;
				}
			}
		}
		return false;
	}

	//去除Gizmo组件
	public void removeGizmo(int x, int y) throws Exception {
		if (x > m_X && y > m_Y) {
			throw new Exception("The specified x and y coordinates are out of bounds.");
		} else if (x > m_X) {
			throw new Exception("The specified x coordinate is out of bounds.");
		} else if (y > m_Y) {
			throw new Exception("The specified y coordinate is out of bounds.");
		}
		
		boolean found = false;
		for (int i = 0; i < m_Gizmos.size(); i++) {
			int xPos = m_Gizmos.get(i).getX();
			int yPos = m_Gizmos.get(i).getY();
			String name = m_Gizmos.get(i).getName();
			
			if (xPos == x && yPos == y && !(m_Gizmos.get(i) instanceof BorderBumper)) {
				if (m_Gizmos.get(i) instanceof ActiveBall) {
					balls--;
				}
				m_Gizmos.remove(i);
				found = true;
				
				
				/* 删除两个关联的Gizmo组件的关联的关系*/
				for (GizmoObject g : m_Gizmos) {
					for (GizmoObject connected : g.getConnectedGizmos()) {
						if (connected.getX() == x && connected.getY() == y) {
							g.getConnectedGizmos().remove(connected);
							
							break;
						}
					}
				}
				
				/* 删除我们所关联的Gizmo组件和按键的关联关系 */
				for (KeyConnection key : getKeyConnections()) {
					for (Entry<Direction, List<BoardAction>> entry : key.getActions().entrySet())
					{
					    List<BoardAction> actions = entry.getValue();
					    for (BoardAction action : actions) {
					    	if (action.getName().equals(name)) {
					    		key.clearActions(name);
					    		break;
					    	}
					    }
					}
				}
			}
		}
		if (!found)
			throw new Exception("A gizmo does not exist at position " + x + "," + y + ".");
	}

	public void removeGizmo(String name) throws Exception {
		boolean found = false;
		
		for (GizmoObject g : m_Gizmos) {
			if (g.getName().equals(name)) {
				if (g instanceof ActiveBall) {
					balls--;
				}
				m_Gizmos.remove(g);
				found = true;
				break;
			}
		}
		
		if (!found)
			throw new Exception("Can't find gizmo on the board with name " + name + " to remove.");
		else {
			/* 删除已经所删除的Gizmo组件和其他组件的关联 */
			for (GizmoObject gizmo : m_Gizmos) {
				for (GizmoObject connected : gizmo.getConnectedGizmos()) {
					if (connected.getName().equals(name)) {
						gizmo.getConnectedGizmos().remove(connected);
						break;
					}
				}
			}
			
			/* 删除已经删除掉的组件和她所关联的按键的关联 */
			for (KeyConnection key : getKeyConnections()) {
				for (Entry<Direction, List<BoardAction>> entry : key.getActions().entrySet())
				{
				    List<BoardAction> actions = entry.getValue();
				    for (BoardAction action : actions) {
				    	if (action.getName().equals(name)) {
				    		key.clearActions(name);
				    		break;
				    	}
				    }
				}
			}
		}
	}

	/* 移动Gizmo组件的位置 */
	public void moveGizmo(String name, int x, int y) throws Exception {
		if (x > m_X && y > m_Y) {
			throw new Exception("The coordinates x and y are out of bounds for gizmo " + name + ".");
		} else if (x > m_X) {
			throw new Exception("The x coordinate is out of bounds for gizmo " + name + ".");
		} else if (y > m_Y) {
			throw new Exception("The y coordinate is out of bounds for gizmo " + name + ".");
		}
		
		
		boolean found = false;
		for (int i = 0; i < m_Gizmos.size(); i++) {
			String tempName = m_Gizmos.get(i).getName();
			if (tempName.equals(name)) {
				found = true;
				int myL = m_Gizmos.get(i).getL();
				int myH = m_Gizmos.get(i).getH();
				if (x+myL > m_X) {
					throw new Exception("The x coordinate is out of bounds for gizmo " + name + ".");
				} else if (y+myH > m_Y) {
					throw new Exception("The y coordinate is out of bounds for gizmo " + name + ".");
				}
				if (occupied(x, y, myL, myH, i)) {
					throw new Exception("A gizmo already exists within the bounds of position " + x + " " + y + ".");
				} else {
					m_Gizmos.get(i).setPosition(x, y);
					if (m_Gizmos.get(i) instanceof ActiveBall) {
						((ActiveBall)m_Gizmos.get(i)).reposition((int)x,(int)y);
					}
				}
				System.out.println(x + "," + y);
			}
		}
		if (!found)
			throw new Exception("A gizmo with the name " + name + " does not exist on the board.");
	}

	public void moveGizmo(String name, double x, double y) throws Exception {
		if (x > m_X && y > m_Y) {
			throw new Exception("The coordinates x and y are out of bounds for gizmo " + name + ".");
		} else if (x > m_X) {
			throw new Exception("The x coordinate is out of bounds for gizmo " + name + ".");
		} else if (y > m_Y) {
			throw new Exception("The y coordinate is out of bounds for gizmo " + name + ".");
		}
		
		boolean found = false;
		for (int i = 0; i < m_Gizmos.size(); i++) {
			String tempName = m_Gizmos.get(i).getName();
			if (tempName.equals(name)) {
				found = true;
				int myL = m_Gizmos.get(i).getL();
				int myH = m_Gizmos.get(i).getH();
				
				if (x+myL > m_X) {
					throw new Exception("The x coordinate is out of bounds for gizmo " + name + ".");
				} else if (y+myH > m_Y) {
					throw new Exception("The y coordinate is out of bounds for gizmo " + name + ".");
				}
				if (occupied((int) x, (int) y, myL, myH, i)) {
					throw new Exception("A gizmo already exists within the bounds of position " + x + " " + y + ".");
				} else {
					m_Gizmos.get(i).setPosition(x, y);
					if (m_Gizmos.get(i) instanceof ActiveBall) {
						((ActiveBall)m_Gizmos.get(i)).reposition((int)x,(int)y);
					}
				}
			}
		}
		if (!found)
			throw new Exception("Can't find gizmo " + name + " to move on the board.");
	}

	/*通过组件名称获取组件 */
	public GizmoObject getGizmoByName(String name) {
		for (int i = 0; i < m_Gizmos.size(); i++) {
			String tempName = m_Gizmos.get(i).getName();
			if (tempName.equals(name)) {
				return m_Gizmos.get(i);
			}
		}
		return null;
	}

	/* 通过组件位置获取组件*/
	public GizmoObject getGizmoByPosition(int x, int y) {
		for (int i = 1; i < m_Gizmos.size(); i++) {
			int tempX = m_Gizmos.get(i).getX();
			int tempY = m_Gizmos.get(i).getY();
			int length = m_Gizmos.get(i).getL();
			int height = m_Gizmos.get(i).getH();
			
			for (int j=tempY;j<tempY+height;j++) {
				for (int k=tempX;k<tempX+length;k++) {
					if (k == x && j == y) {
						return m_Gizmos.get(i);
					}
				}
			}
			
		}
		return null;
	}

	/*旋转组件*/
	public void rotateGizmo(String name) throws Exception {
		boolean found = false;
		for (int i = 0; i < m_Gizmos.size(); i++) {
			String tempName = m_Gizmos.get(i).getName();
			if (tempName.equals(name)) {
				found = true;
				GizmoObject temp = m_Gizmos.get(i);

				temp.rotate();
				int itX = temp.getX();
				int itY = temp.getY();
				int itL = temp.getL();
				int itH = temp.getH();

				if (occupied(itX, itY, itL, itH, i)) {
					//旋转回去初始状态
					temp.rotate();
					temp.rotate();
					temp.rotate();
					throw new Exception(
							"A gizmo or gizmos are getting in the way of the gizmo " + name + " being rotated.");
				}
				if (itX+itL > m_X) {
					// 旋转回去初始状态
					temp.rotate();
					temp.rotate();
					temp.rotate();
					throw new Exception("The x coordinate is out of bounds for gizmo " + name + ".");
				} else if (itY+itH > m_Y) {
					// 旋转回去初始状态
					temp.rotate();
					temp.rotate();
					temp.rotate();
					throw new Exception("The y coordinate is out of bounds for gizmo " + name + ".");
				}

			}
		}
		if (!found)
			throw new Exception("Can't find gizmo " + name + " to move on the board.");
	}

    /* 关联两个组件*/
	public void connectGizmo(String gizmo1, String gizmo2) throws Exception {
		/* a generic search method for the board would be nice */
		GizmoObject foundGizmo1 = null;
		GizmoObject foundGizmo2 = null;

		for (GizmoObject gizmo : m_Gizmos) {
			if (gizmo.getName().equals(gizmo1)) {
				foundGizmo1 = gizmo;
			}
			if (gizmo.getName().equals(gizmo2)) {
				foundGizmo2 = gizmo;
			}
		}
		
		if (foundGizmo1 != null && foundGizmo2 != null) {
			if (foundGizmo1.getConnectedGizmos().contains(foundGizmo2)) {
				throw new Exception ("A connection already exists between " + foundGizmo1.getName() + " and " + foundGizmo2.getName() + ".");
			} else {
				foundGizmo1.connectGizmo(foundGizmo2);
			}
		} else if (foundGizmo1 != null) {
			throw new Exception("No gizmo exists on the board with the name "
					+ gizmo2);
		} else if (foundGizmo2 != null) {
			throw new Exception("No gizmo exists on the board with the name "
					+ gizmo1);
		} else {
			throw new Exception("Gizmos " + gizmo1 + " " + gizmo2
					+ " do not exist on the board.");
		}
	}

	/*解除两个组件的关联*/
	public void disconnectGizmo (String gizmo1, String gizmo2) throws Exception {
		GizmoObject foundGizmo1 = null;
		GizmoObject foundGizmo2 = null;

		for (GizmoObject gizmo : m_Gizmos) {
			if (gizmo.getName().equals(gizmo1)) {
				foundGizmo1 = gizmo;
			}
			if (gizmo.getName().equals(gizmo2)) {
				foundGizmo2 = gizmo;
			}
		}
		if (foundGizmo1 != null && foundGizmo2 != null) {
			if (!foundGizmo1.getConnectedGizmos().contains(foundGizmo2)) {
				throw new Exception ("A connection does not exist between " + foundGizmo1.getName() + " and " + foundGizmo2.getName() + ".");
			} else {
				foundGizmo1.disconnectGizmo(foundGizmo2);
			}
		} else if (foundGizmo1 != null) {
			throw new Exception("No gizmo exists on the board with the name "
					+ gizmo2);
		} else if (foundGizmo2 != null) {
			throw new Exception("No gizmo exists on the board with the name "
					+ gizmo1);
		} else {
			throw new Exception("Gizmos " + gizmo1 + " " + gizmo2
					+ " do not exist on the board.");
		}
	}


	public ArrayList<GizmoObject> getGizmos() {
		return m_Gizmos;
	}

	/* 重置游戏*/
	public void resetGame() {
		for (int i = 0; i < m_Gizmos.size(); i++) {
			if (m_Gizmos.get(i) instanceof ActiveBall) {
				((ActiveBall) m_Gizmos.get(i)).reset();
			}
			if (m_Gizmos.get(i) instanceof Absorber) {
				((Absorber) m_Gizmos.get(i)).clear();
			}
			if (m_Gizmos.get(i) instanceof LeftFlipper) {
				((LeftFlipper) m_Gizmos.get(i)).reset();
			}
			if (m_Gizmos.get(i) instanceof RightFlipper) {
				((RightFlipper) m_Gizmos.get(i)).reset();
			}
		}
		
		//清除传送门制造的多余的小球
		for (int i = 0; i < m_Gizmos.size(); i++) {
			if (m_Gizmos.get(i) instanceof ActiveBall) {
				ActiveBall b1 = ((ActiveBall) m_Gizmos.get(i));
				for (int j = 0; j < m_Gizmos.size(); j++) {
					if (m_Gizmos.get(j) instanceof ActiveBall) {
						ActiveBall b2 = ((ActiveBall) m_Gizmos.get(j));
						if (b1.sX()==b2.sX() && b1.sY()==b2.sY() && i !=j) {
							m_Gizmos.remove(j);
							j--;
						}
					}
				}
			}
		}
		
		m_score = 0;
		m_lives = 3;
	}
	
	
	public void checkCollision() {
		for (int i = 0; i < m_Gizmos.size(); i++) {
			m_Gizmos.get(i).unbonk();
			if (m_Gizmos.get(i) instanceof ActiveBall) {
				updateBall((ActiveBall) m_Gizmos.get(i), i, 0);
			}
		}
		
		for (int i = 0; i < m_Gizmos.size(); i++) {
			if (m_Gizmos.get(i) instanceof LeftFlipper) {
				((LeftFlipper) m_Gizmos.get(i)).move();
			} else if (m_Gizmos.get(i) instanceof RightFlipper) {
				((RightFlipper) m_Gizmos.get(i)).move();
			}
		}
	}
	
	public void updateBall(ActiveBall ball, int b, int max) {
		//b是小球的序号
		//max是递归调用的最大次数
		if (ball.getMoving()) {
			double delta_t = 0.04;
			int bestID = 0;
			double best = Double.POSITIVE_INFINITY;
			double temp = Double.POSITIVE_INFINITY;
			/*如果小球发生碰撞的时间在下次刷新屏幕前，先执行小球碰撞，否则定时刷新屏幕*/
			for (int j = 0; j < m_Gizmos.size(); j++) {
				if (!ball.getLaunched() || !(m_Gizmos.get(j) instanceof Absorber)) {
					temp = m_Gizmos.get(j).timeToCollision(ball);
				} else
					temp = Double.POSITIVE_INFINITY;

				if (temp < best) {
					best = temp;
					bestID = j;
				}
			}

			double thresh = 1 * delta_t;

            //碰撞发生在刷新小球位置之前
			if (best <= thresh) {
				//ABSORBER
				if (m_Gizmos.get(bestID) instanceof Absorber) {
					ball.setVelocity(new Vect(0, 0));

					Absorber abs = (Absorber) m_Gizmos.get(bestID);

					Vertex corner = abs.getCorner();
					double xShunt = squareX() / 2;
					double yShunt = squareY() / 2;

					double myX = corner.getX() - xShunt;
					double myY = corner.getY() - yShunt;
					ball.setMoving(false);
					ball.setPosition(myX, myY);
					abs.addBall(ball);
					
					abs.triggerConnected();

					loseLife();
					
				}
				//PORTAL
				else if (m_Gizmos.get(bestID) instanceof Portal) {
					balls += ((Portal)m_Gizmos.get(bestID)).collide(m_Gizmos, b);
					ball.setLaunched(false);
				}
				//BOUNCE
				else {
					ball.setCollided(best);
					ball.setLaunched(false);
					ball.setVelocity(m_Gizmos.get(bestID).collide(ball));
					addToScore(bestID);
			//if (m_Gizmos.get(bestID) instanceof LeftFlipper ||
			//		m_Gizmos.get(bestID) instanceof RightFlipper)
					if (max < 2)
						updateBall(ball,b, max+1);
				}
			} else {
				//没有碰撞，直接更新小球位置
				ball.move(m_Gravity, m_Mu, m_Mu2);
			}
			//小球受重力和摩擦力的影响
			ball.degrade(m_Gravity, m_Mu, m_Mu2);
			ball.setDelay(true);
		}
	}
	
	
	public Mode getMode() {
		return m_Mode;
	}

	public void setMode(Mode b) {
		m_Mode = b;
	}
	
	public double getGravity() {
		return m_Gravity;
	}

	public double getMu() {
		return m_Mu;
	}

	public double getMu2() {
		return m_Mu2;
	}

	public double getMaxX() {
		return m_MaxX;
	}

	public double getMaxY() {
		return m_MaxY;
	}
	
	public double squareX() {
		return m_MaxX / m_X;
	}

	public double squareY() {
		return m_MaxY / m_Y;
	}
	
	public void setGravity(double g) {
		m_Gravity = g;
	}

	public void setFriction(double mu, double mu2) {
		m_Mu = mu;
		m_Mu2 = mu2;
	}

	public int getScore() {return m_score;}

	private boolean ballsStopped()
	{
		boolean loseLife = true;
		for (GizmoObject giz : m_Gizmos)
		{
			if (giz instanceof ActiveBall)
			{
				loseLife = !((ActiveBall)giz).getMoving();
			}
		}
		
		return loseLife;
	}

	public void update() {
		if (m_Mode == Mode.PLAY_MODE_RUNNING) {
			checkCollision();
			refresh();
		}
	}
	
	public void refresh() {
		this.setChanged();
		this.notifyObservers();
	}

	public boolean highscore(int s) {
		return m_highScores.isHigh(s);
	}
	
	public void addHigh(String s, int i) {
		m_highScores.addScore(s,i);
	}
	
	public ArrayList<Score> getScores() {
		return m_highScores.getScores();
	}
	
	public void writeScores() {
		m_highScores.writeFile();
	}

	public int indexOfGizmo ( GizmoObject checked ) {
        for ( int i=0 ;i < m_Gizmos.size(); i++ ){
            GizmoObject temp= m_Gizmos.get(i);
            if (checked.equals(temp)) {
                return i;
            }
        }
        return -1;
    }

}