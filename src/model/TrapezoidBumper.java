package model;

import java.io.File;

public class TrapezoidBumper extends GizmoObject {

    public TrapezoidBumper() {super();}

    public void Initialise(int mX, int mY, String name, int mL, int mH) {
        super.Initialise(mX, mY, name, mL, mH);

        super.setType("Trapezoid");

        double thick = length/3.0;

        System.out.println(thick);

        m_Points.add(new Vertex(mX+thick,mY));
        m_Points.add(new Vertex(mX+2.0*thick,mY));
        m_Points.add(new Vertex(mX+3.0*thick,mY+3.0*thick));
        m_Points.add(new Vertex(mX,mY+3.0*thick));

        buildPhysics();

        super.setSoundEffect(new File("SoundEffects/Square.wav"));
    }

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
        double onethirdX = minX + (lengthX/3.0);
        double twothirdX = minX + (2.0*lengthX/3.0);
        double onethirdY = minY + (lengthY/3.0);
        double twothirdY = minY + (2.0*lengthY/3.0);

        for (int i=0;i<m_Points.size();i++) {
            double curX = m_Points.get(i).getX();
            double curY = m_Points.get(i).getY();
            if (curX == onethirdX && curY == minY) {
                m_Points.get(i).setX(maxX);
                m_Points.get(i).setY(onethirdY);
            } else if (curX == twothirdX && curY == minY) {
                m_Points.get(i).setX(maxX);
                m_Points.get(i).setY(twothirdY);
            } else if (curX == maxX && curY == onethirdY) {
                m_Points.get(i).setX(twothirdX);
                m_Points.get(i).setY(maxY);
            } else if (curX == maxX && curY == twothirdY) {
                m_Points.get(i).setX(onethirdX);
                m_Points.get(i).setY(maxY);
            } else if (curX == onethirdX && curY == maxY) {
                m_Points.get(i).setX(minX);
                m_Points.get(i).setY(onethirdY);
            } else if (curX == twothirdX && curY == maxY) {
                m_Points.get(i).setX(minX);
                m_Points.get(i).setY(twothirdY);
            } else if (curX == minX && curY == onethirdY) {
                m_Points.get(i).setX(twothirdX);
                m_Points.get(i).setY(minY);
            } else if (curX == minX && curY == twothirdY) {
                m_Points.get(i).setX(onethirdX);
                m_Points.get(i).setY(minY);
            } else if (curX < onethirdX && curY < onethirdY) {
                m_Points.get(i).setX(curX + lengthX);
            } else if (curX > twothirdX && curY < onethirdY) {
                m_Points.get(i).setY(curY + lengthY);
            } else if (curX > twothirdX && curY > twothirdY) {
                m_Points.get(i).setX(curX - lengthX);
            } else if (curX < onethirdX && curY > twothirdY) {
                m_Points.get(i).setY(curY - lengthY);
            }
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
