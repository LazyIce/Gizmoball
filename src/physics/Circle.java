package physics;/********************************************************************** * Copyright (C) 1999, 2000 by the Massachusetts Institute of Technology, *                      Cambridge, Massachusetts. * *                        All Rights Reserved * * Permission to use, copy, modify, and distribute this software and * its documentation for any purpose and without fee is hereby * granted, provided that the above copyright notice appear in all * copies and that both that copyright notice and this permission * notice appear in supporting documentation, and that MIT's name not * be used in advertising or publicity pertaining to distribution of * the software without specific, written prior permission. * * THE MASSACHUSETTS INSTITUTE OF TECHNOLOGY DISCLAIMS ALL WARRANTIES * WITH REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF * MERCHANTABILITY AND FITNESS.  IN NO EVENT SHALL THE MASSACHUSETTS * INSTITUTE OF TECHNOLOGY BE LIABLE FOR ANY SPECIAL, INDIRECT OR * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS * OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE. * * * @author   Lik Mui * @version  $Id: Circle.java,v 1.1 2002/08/21 21:49:40 kirky Exp $ * @date     $Date: 2002/08/21 21:49:40 $ * *********************************************************************/import java.io.Serializable;import java.awt.geom.Point2D;import java.awt.geom.Ellipse2D;/** 定义圆形的物理状态 */public final class Circle implements Serializable {  private final Vect centerPoint;    //圆心向量来表示坐标  private final double radius;    //圆的半径  // Constructors -----------------------------------  /** 构造一个圆形 */  public Circle(Vect center, double r) {    if ((r < 0) || (center == null)) {      throw new IllegalArgumentException();    }    centerPoint = center;    radius = r;  }  public Circle(double cx, double cy, double r) {    this(new Vect(cx, cy), r);  }  public Circle(Point2D center, double r) {    this(new Vect(center), r);  }  // Observers --------------------------------------  /** 得到圆心坐标 */  public Vect getCenter() {    return centerPoint;  }  /** 获取圆的半径 */  public double getRadius() {    return radius;  }  /** 构建圆的图像对象 */  public Ellipse2D toEllipse2D() {    return new Ellipse2D.Double(centerPoint.x() - radius,				centerPoint.y() - radius,				2 * radius,				2 * radius);  }  // Object methods --------------------------------------  public boolean equals(Circle c) {    if (c == null) return false;    return (radius == c.radius) && centerPoint.equals(c.centerPoint);  }  public boolean equals(Object o) {    if (o instanceof Circle)      return equals((Circle) o);    else      return false;  }  public String toString() {    return "[Circle center=" + centerPoint + " radius=" + radius + "]";  }  public int hashCode() {    return centerPoint.hashCode() + 17 * (new Double(radius)).hashCode();  }}