public class Body{
  public double xxPos;
  public double yyPos;
  public double xxVel;
  public double yyVel;
  public double mass;
  public String imgFileName;
  public static double G = 6.67 * Math.pow(10, -11);
  public Body(double xP, double yP, double xV, double yV, double m, String img){
    xxPos = xP;
    yyPos = yP;
    xxVel = xV;
    yyVel = yV;
    mass = m;
    imgFileName = img;
  }
  public Body(Body b){
    xxPos = b.xxPos;
    yyPos = b.yyPos;
    xxVel = b.xxVel;
    yyVel = b.yyVel;
    mass = b.mass;
    imgFileName = b.imgFileName;
  }
  public double calcDistance(Body b){
    double dx = this.xxPos - b.xxPos;
    double dy = this.yyPos - b.yyPos;
    double r = Math.sqrt(dx * dx + dy * dy);
    return r;
  }
  public double calcForceExertedBy(Body b){
    double r = calcDistance(b);
    double F = G * this.mass * b.mass /(r * r);
    return F;
  }
  public double calcForceExertedByX(Body b){
    double dx = b.xxPos - this.xxPos;
    double r = calcDistance(b);
    double F = calcForceExertedBy(b);
    double Fx = F * dx / r;
    return Fx;
  }
  public double calcForceExertedByY(Body b){
    double dy = b.yyPos - this.yyPos;
    double r = calcDistance(b);
    double F = calcForceExertedBy(b);
    double Fy = F * dy / r;
    return Fy;
  }
  public double calcNetForceExertedByX(Body[] allBodys){
    double NetFx = 0;
    for(int i = 0; i < allBodys.length; i++){
      if(this.equals(allBodys[i])){
        continue;
      }
      NetFx += calcForceExertedByX(allBodys[i]);
    }
    return NetFx;
  }
  public double calcNetForceExertedByY(Body[] allBodys){
    double NetFy = 0;
    for(int i = 0; i < allBodys.length; i++){
      if(this.equals(allBodys[i])){
        continue;
      }
      NetFy += calcForceExertedByY(allBodys[i]);
    }
    return NetFy;
  }
  public void update(double dt, double fX, double fY){
    double aNetX = fX / this.mass;
    double aNetY = fY / this.mass;
    double vNewX = this.xxVel + aNetX * dt;
    double vNewY = this.yyVel + aNetY * dt;
    double pNewX = this.xxPos + vNewX * dt;
    double pNewY = this.yyPos + vNewY * dt;

    this.xxVel = vNewX;
    this.yyVel = vNewY;
    this.xxPos = pNewX;
    this.yyPos = pNewY;
  }
  public void draw(){
    String imageToDraw = "./images/" + this.imgFileName;
    StdDraw.picture(xxPos, yyPos, imageToDraw);
  }
}
