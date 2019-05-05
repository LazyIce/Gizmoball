package view;

import controller.BoardViewController;
import model.*;
import physics.Circle;
import physics.LineSegment;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Observable;
import java.util.Observer;

public class BoardView extends JComponent implements Observer {
	private static final long serialVersionUID = 1L;
	
	public boolean wireframe;        //是否为线框模式（F3改变）
	public boolean direction;        //是否要显示小球当前运动方向（F4改变）
	public boolean ballcount;      //确定是否要显示当前小球数（F5改变）
	
	double m_ScaleX;        //实际x/逻辑x坐标
	double m_ScaleY;        //实际y/逻辑y坐标
	
	int m_PushX = 0;    //微小的x轴偏移量
	int m_PushY = 0;    //微小的y轴偏移量
	
	Board board;

	private BoardViewController m_boardViewController;
	
	public BoardView()
	{
		wireframe = false;
		board = new Board();
		board.addObserver(this);
	}
	
	public BoardView(Board b) {
		board = b;
		board.addObserver(this);
	}
	
	
	public void setScale() {//int width, int height) {    //当画板大小改变时改变比例
		int width = this.getWidth();
		int height = this.getHeight();
		
		m_ScaleX = (double)width/board.getMaxX();         //实际画面与物理世界的横坐标比
		m_ScaleY = (double)height/board.getMaxY();
		
		m_ScaleX = Math.min(m_ScaleX, m_ScaleY);
		m_ScaleY=m_ScaleX;
		
		int diff = (width-height)/2;
		
		m_PushX = (diff>0) ? diff : 0;
		m_PushY = (diff<0) ? -diff : 0;
	}
	
	public void setScaleForNewGameDialog() {//int width, int height) {
		int width = this.getWidth();
		int height = this.getHeight();
		
		m_ScaleX = (double)width/board.getMaxX();
		m_ScaleY = (double)height/board.getMaxY();
		
		//m_PushX = (diff>0) ? diff : 0;
		//m_PushY = (diff<0) ? -diff : 0;
	}
	
	public int toScreenX(double logX) {     //逻辑x坐标转为实际显示的x坐标
		int retval = (int) Math.round(logX * m_ScaleX);
		return retval;
	}
	
	public int toScreenY(double logY) {
		int retval = (int) Math.round(logY * m_ScaleY);
		return retval;
	}
	
	public double toLogicalX(int scrX) {
		//double retval = (scrX-m_PushX)*m_ScaleX;
		
		double retval = ((double)scrX / m_ScaleX) - ((double)m_PushX / m_ScaleX);
		System.out.println(retval);
		return retval;
	}
	
	public double toLogicalY(int scrY) {
		//double retval = (scrY-m_PushY)*m_ScaleY;
		double retval = ((double)scrY / m_ScaleY) - ((double)m_PushY / m_ScaleY);
		return retval;
	}
	
	public void draw() {               //画图
		if (ballcount) {
			System.out.println("目前场上"+board.balls+"个球！");
			ballcount = false;
		}
		repaint();
	}
	
	public void paint(Graphics g) {
        super.paint(g);

        for (int i=0;i<board.getGizmos().size();i++) {   //对每个Gizmo进行绘制
        	GizmoObject giz = board.getGizmos().get(i);
        	
        	if (wireframe) {      //在该模式下只显示gizmo的轮廓，没有填充颜色。可以更清楚看到碰撞边界
        		if (giz instanceof BorderBumper) {     //画画板边界也就是填充底板颜色
	        		g.setColor(new Color(0, 0, 0));
		        	paintPolygon(giz,g);
	        	}
        		else if (giz instanceof ActiveBall) {        //画小球圆弧
        			((Graphics2D)g).setPaint(Color.yellow);
        			
        			Ellipse2D t = ((ActiveBall) giz).getCircle().toEllipse2D();
        			
        			int x = toScreenX(t.getX())+m_PushX;
					int y = toScreenY(t.getY())+m_PushY;
					int w = toScreenX(t.getWidth());
					int h = toScreenY(t.getHeight());
					
					((Graphics2D)g).draw(
							new Ellipse2D.Double(x,y,w,h)
									);
        		}
        		else {                                      //画除边界和小球外各gizmo碰撞边界
        			int size = giz.getColliders().size();    //得到该Gizmo有多少条碰撞检测边界
        			((Graphics2D)g).setPaint(Color.red);
        			
        			for (int j=0;j<size;j++) {
        				
        				
        				if (giz.getColliders().get(j) instanceof LineSegment) {  //画线型碰撞边界
        					Line2D t = ((LineSegment)(giz.getColliders().get(j))) .toLine2D();
        					
        					int x1 = toScreenX(t.getX1())+m_PushX;
        					int x2 = toScreenX(t.getX2())+m_PushX;
        					int y1 = toScreenY(t.getY1())+m_PushY;
        					int y2 = toScreenY(t.getY2())+m_PushY;
        					
        					((Graphics2D)g).draw(
        							new Line2D.Double(x1, y1, x2, y2)
        									);
        				}
        				else if (giz.getColliders().get(j) instanceof Circle) {    //画圆型碰撞边界
        					Ellipse2D t = ((Circle)(giz.getColliders().get(j))).toEllipse2D();
        					
        					int x = toScreenX(t.getX())+m_PushX;
        					int y = toScreenY(t.getY())+m_PushY;
        					int w = toScreenX(t.getWidth());
        					int h = toScreenY(t.getHeight());
        					
        					((Graphics2D)g).draw(
        							new Ellipse2D.Double(x,y,w,h)
        									);
        				}
   
        			}
        		}
        		
        	}
        	//非线框模式下绘图，默认行为，和上面类似，不过填充了颜色
        	else {
	        	if (giz instanceof BorderBumper) {
	        		g.setColor(new Color(0, 0, 0));
		        	paintPolygon(giz,g);
	        	}
	        	else if (giz instanceof ActiveBall) {           //画球

	                g.setColor(new Color(255,0,0));
	                g.fillOval(
	                		toScreenX(giz.getPosition().x()-((ActiveBall) giz).getRadius())+ m_PushX, 
	                		toScreenY(giz.getPosition().y()-((ActiveBall) giz).getRadius())+ m_PushY, 
	        	            toScreenX(((ActiveBall) giz).getRadius()*2), 
	        	            toScreenY(((ActiveBall) giz).getRadius()*2)
	        	            );
	        	}
	        	else if (giz instanceof SquareBumper) {          //画方块
	        		if (giz.bonked())	g.setColor(new Color(0,255,0));
	        		else g.setColor(new Color(0,0,255));
		        	paintPolygon(giz,g);
	        	}
	        	else if (giz instanceof TriangleBumper) {          //画三角形
	        		if (giz.bonked())	g.setColor(new Color(0,255,255));
	        		else g.setColor(new Color(0,255,0));
		        	paintPolygon(giz,g);
	        	}
	        	else if (giz instanceof CircleBumper) {               //画圆形
	        		if (giz.bonked())	g.setColor(new Color(0,255,0));
	        		else g.setColor(new Color(255,255,0));
		        	paintOval(giz,g);
	        	}
	        	else if (giz instanceof TrapezoidBumper) {                    //画梯形
                    if (giz.bonked())   g.setColor(new Color(0,255,0));
                    else g.setColor(new Color(0x42D7F9));
                    paintPolygon(giz,g);
                }
                else if (giz instanceof Block) {                //画Block
                    if (giz.bonked())  g.setColor(new Color(0,255,0));
                    else g.setColor(new Color(0x42D7F9));
                    paintPolygon(giz,g);
                }
	        	else if (giz instanceof LeftFlipper) {           //画左挡板
	        		g.setColor(new Color(0,255,255));
	        		paintFlipper(giz,g);
	        	}
	        	else if (giz instanceof RightFlipper) {          //画右挡板
	        		g.setColor(new Color(0,255,255));
	        		paintFlipper(giz,g);
	        	}
	        	else if (giz instanceof Portal) {           //画传送门
	        		paintPortal(giz,g);
	        	}
	        	else if (giz instanceof Absorber) {           //画吸收器
	        		g.setColor(new Color(255,0,255));
		        	paintPolygon(giz,g);
	        	}
	        }
        }

        for (int i=0;i<board.getGizmos().size();i++) {       //再遍历一遍gizmo，找出小球对象
        	GizmoObject giz = board.getGizmos().get(i);

        	if (direction) {               //如果需要显示小球当前方向，则画出方向线段（由F4控制）
        		if (giz instanceof ActiveBall) {
        			g.setColor(new Color(0,255,0));
	        		//获取逻辑位置向量
        			double ax = giz.getPosition().x();
        			double ay = giz.getPosition().y();
                    //获取逻辑速度向量
        			double bx = giz.getVelocity().x();
        			double by = giz.getVelocity().y();
        			
        			//标准化速度向量大小，使之成为单位向量
        			double c = Math.sqrt((bx*bx)+(by*by));
        			double cx = bx/c;      //cos
        			double cy = by/c;      //sin
        			
	                g.drawLine(
	                		toScreenX(ax)+m_PushX, 
	                		toScreenY(ay)+m_PushY, 
	                		toScreenX(ax+cx)+m_PushX,
	                		toScreenY(ay+cy)+m_PushY);
	        	}
            }
        }
        
        
        if (board.getMode() == Mode.BUILD_MODE)	paintGrid(g);       //画辅助线
    }
	
	public void paintPolygon(GizmoObject giz, Graphics g) {   //画实心多边形
		Polygon p = new Polygon();
    	for (int j=0;j<giz.getPoints().size();j++) {
    		p.addPoint(
    				toScreenX(giz.getPoints().get(j).getX())+ m_PushX,
    				toScreenY(giz.getPoints().get(j).getY())+ m_PushY
    				);
    	}
    	g.fillPolygon(p);
	}
	
	public void paintOval(GizmoObject giz, Graphics g) {         //画实心圆
		
		double xStart = giz.getPoints().get(0).getX();
		double yStart = giz.getPoints().get(0).getY();
		double xEnd = giz.getPoints().get(2).getX();
		double yEnd = giz.getPoints().get(2).getY();
		
		double length = (xEnd-xStart);
		double height = (yEnd-yStart);

    	g.fillOval(
    			toScreenX(xStart)+ m_PushX,
				toScreenY(yStart)+ m_PushY,
				toScreenX(length),
				toScreenX(height)
    			);
	}
	
	public void paintPortal(GizmoObject giz, Graphics g) {      //画传送门
		
		g.setColor(new Color(0,255,255));
		Polygon p = new Polygon();
    	for (int j=2;j<6;j++) {
    		p.addPoint(
    				toScreenX(giz.getPoints().get(j).getX())+ m_PushX,
    				toScreenY(giz.getPoints().get(j).getY())+ m_PushY
    				);
    	}
    	g.fillPolygon(p);
    	g.setColor(new Color(255,128,64));
		paintPolygon(giz,g);
		
		
	}
	
	public void paintFlipper(GizmoObject giz, Graphics g) {      //画旋转挡板
		//Paints a flipper object
		//Paint circles at the ends.
		double thickness = 0;
		
		if (giz instanceof LeftFlipper) thickness = ((LeftFlipper)giz).getRadius();
		if (giz instanceof RightFlipper) thickness = ((RightFlipper)giz).getRadius();
		
		double hingeX = (giz.getPoints().get(0).getX() + giz.getPoints().get(1).getX())/2;
		double hingeY = (giz.getPoints().get(0).getY() + giz.getPoints().get(1).getY())/2;
		
		double tipX = (giz.getPoints().get(2).getX() + giz.getPoints().get(3).getX())/2;
		double tipY = (giz.getPoints().get(2).getY() + giz.getPoints().get(3).getY())/2;
		
		g.fillOval(                    //先画铰链处地实心圆
				toScreenX(hingeX-thickness)+ m_PushX -1,
				toScreenY(hingeY-thickness)+ m_PushY,
				toScreenX(thickness*2)+1,
				toScreenY(thickness*2)
				);
		
		g.fillOval(                      //再画尖端处地实心圆
				toScreenX(tipX-thickness)+ m_PushX -1,
				toScreenY(tipY-thickness)+ m_PushY,
				toScreenX(thickness*2)+1,
				toScreenY(thickness*2)
				);
		
		Polygon p = new Polygon();
    	for (int j=0;j<giz.getPoints().size();j++) {             //最后填充四顶点围成地长方形
    		p.addPoint(
    				toScreenX(giz.getPoints().get(j).getX())+ m_PushX,
    				toScreenY(giz.getPoints().get(j).getY())+ m_PushY
    				);
    	}
    	g.fillPolygon(p);
		
	}
	
	void paintGrid(Graphics g) {      //画辅助线
		double j = board.squareY();         //获得一格逻辑块高度
		double i = board.squareX();          //获得一格逻辑快长度
		
		g.setColor(new Color(0,255,0));
		
		while (i<board.getMaxX()) {          //画出所有竖线
			
			g.drawLine(
					toScreenX(i)+m_PushX, 
					toScreenY(0)+m_PushY, 
					toScreenX(i)+m_PushX, 
					toScreenY(20)+m_PushY
					);
			
			i = i + board.squareX();
		}
		
		while (j<board.getMaxY()) {           //画出所有横线

			g.drawLine(
					toScreenX(0)+m_PushX, 
					toScreenY(j)+m_PushY, 
					toScreenX(20)+m_PushX, 
					toScreenY(j)+m_PushY
					);
			
			j = j + board.squareY();
		}
	}
	

	@Override
	public void update(Observable arg0, Object arg1) {        //repaint后draw（）一下
		draw();
	}

	public Board getModel()
	{
		return board;
	}      //获得当前所观察地Board
	
	public int getLivesLeft()
	{
		return board.getLives();
	}
	
	public BoardViewController getController ()
	{
		return m_boardViewController;
	}
	
	public boolean isFocusable ()
	{
		return true;
	}
}