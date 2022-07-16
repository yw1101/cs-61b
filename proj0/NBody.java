public class NBody{
  public static double readRadius(String f){
    In in = new In(f);
    int num = in.readInt();
    double radius = in.readDouble();
    return radius;
  }
  public static Body[] readBodies(String f){
    In in = new In(f);
    int num = in.readInt();
    Body[] bodies = new Body[num]; //create the array
    double rad = in.readDouble();
    for (int i = 0; i < num; i++){
      double xPos = in.readDouble();
      double yPos = in.readDouble();
      double xVel = in.readDouble();
      double yVel = in.readDouble();
      double m = in.readDouble();
      String img = in.readString();
      bodies[i] = new Body(xPos, yPos, xVel, yVel, m, img);
    }
    return bodies;
  }
  public static void main (String [] args){
    double T = Double.parseDouble(args[0]);
    double dt = Double.parseDouble(args[1]);
    String filename = args[2];
    Body[] bodies = readBodies(filename);
    double radius = readRadius(filename);

    String imageToDraw = "./images/starfield.jpg";
    StdDraw.setScale(-radius, radius);
    StdDraw.clear();
    StdDraw.picture(0, 0, imageToDraw);

    for (int i = 0; i < bodies.length; i++){
      bodies[i].draw();
    }
    StdDraw.enableDoubleBuffering();

    for (double time = 0; time < T; time += dt){
      double[] xForces = new double[bodies.length];
      double[] yForces = new double[bodies.length];
      for(int j = 0; j < bodies.length; j++){
        xForces[j] = bodies[j].calcNetForceExertedByX(bodies);
        yForces[j] = bodies[j].calcNetForceExertedByY(bodies);
      }

      StdDraw.picture(0,0,imageToDraw);

      for(int k = 0; k < bodies.length; k++){
        bodies[k].update(dt, xForces[k], yForces[k]);
        bodies[k].draw();
      }
      StdDraw.show();
      StdDraw.pause(10);
    }
    StdOut.printf("%d\n", bodies.length);
    StdOut.printf("%.2e\n", radius);
    for (int i = 0; i < bodies.length; i++) {
      StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",   bodies[i].xxPos, bodies[i].yyPos, bodies[i].xxVel,
      bodies[i].yyVel, bodies[i].mass, bodies[i].imgFileName); 
    }

  }
}
