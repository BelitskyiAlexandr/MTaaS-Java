package demo.convexhull;

public final class Point2D implements Comparable<Point2D> {
    public final double x,y;
    public Point2D(double x,double y){this.x=x;this.y=y;}
    public Point2D add(double dx,double dy){return new Point2D(x+dx,y+dy);}
    public Point2D rotate(double t){double c=Math.cos(t),s=Math.sin(t);return new Point2D(c*x-s*y, s*x+c*y);}
    public Point2D scale(double k){return new Point2D(k*x,k*y);}
    @Override public int compareTo(Point2D o){int cx=Double.compare(x,o.x); return cx!=0?cx:Double.compare(y,o.y);}
}
