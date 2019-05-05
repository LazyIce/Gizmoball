package model;

import java.io.File;
import java.util.ArrayList;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class Block extends GizmoObject {

    private double BlockX;
    private double BlockY;
    private double velocity;
    private double acceleration;
    public double xOff;
    private boolean right;

    public Block() {
        super();
    }

    public void Initialise(int mX, int mY, String name, int mL, int mH) {
        super.Initialise(mX, mY, name, mL, mH);

        super.setType("Block");

        right = true;
        BlockX = mX;
        BlockY = mY;

        m_Points.add(new Vertex(mX,mY));
        m_Points.add(new Vertex(mX+length,mY));
        m_Points.add(new Vertex(mX+length,mY+height));
        m_Points.add(new Vertex(mX,mY+height));

        buildPhysics();
        setSoundEffect(new File("SoundEffects/Launch.wav"));

    }

    public boolean rotate() {
        super.rotate();
        return true;
    }

    void move() {
        double delta_t = 40.0 / 1000;
        velocity = 10;
        xOff = velocity * delta_t;
        if (right)
            BlockX += xOff;
        else
            BlockX -= xOff;
        if (BlockX+length >= 20)
            right = false;
        if (BlockX <= 0)
            right = true;
        update();
        }

    public void update() {
        m_Points.clear();
        m_Points.add(new Vertex(BlockX , BlockY));
        m_Points.add(new Vertex(BlockX +length, BlockY));
        m_Points.add(new Vertex(BlockX +length, BlockY + height));
        m_Points.add(new Vertex(BlockX , BlockY + height));
        buildPhysics();
    }

    @Override
    public void trigger() {
        bonk();
        move();
        this.getSoundEffect().play();
    }

    public double timeToCollision(ActiveBall b) {
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

    }


