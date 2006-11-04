package com.becker.simulation.reactiondiffusion;

/**
 *
 */
final class GrayScott {
  double[][] u;
  double[][] v;
  private double[][] tmpU;
  private double[][] tmpV;
  double uMax;
  static final double DU = 2e-5;
  static final double DV = 1e-5;
  public double k = 0.059;
  public double f = 0.02;
  public double h = 0.01;
  private double duDivh2;
  private double dvDivh2;
  int width, height;

  public GrayScott(int width, int height, double f, double k, double h) {
    this.width = width;
    this.height = height;
    this.f = f;
    this.k = k;
    this.h = h;
    double v0 = f/(2*(f+k)) + Math.sqrt(f/(2*(f+k))*f/(2*(f+k)) - f*(f+k));
    double u0 = f/(v0*v0+f);
    duDivh2 = DU/(h*h);
    dvDivh2 = DV/(h*h);
    u = new double[width][height];
    v = new double[width][height];
    tmpU = new double[width][height];
    tmpV = new double[width][height];
    uMax = 0;
    initialState();
  }

  public void initialState() {
    uMax = 0.5;
    double v0 = f/(2*(f+k)) + Math.sqrt(f/(2*(f+k))*f/(2*(f+k)) - f*(f+k));
    double u0 = f/(v0*v0+f);
    for (int x = 0; x<width; x++) {
      for (int y = 0; y<height; y++) {
        tmpU[x][y] = 1;
        tmpV[x][y] = 0;
      }
    }
    for (int x = 0; x<(width/3); x++) {
      for (int y = 0; y<height/3; y++) {
        tmpU[1*width/3 + x][1*height/3 + y] = 0.5;
        tmpV[1*width/3 + x][1*height/3 + y] = 0.25;
      }
    }

    for (int x = 0; x<(width/7); x++) {
      for (int y = 0; y<height/5; y++) {
        tmpU[5*width/7 + x][3*height/5 + y] = 0.5;
        tmpV[5*width/7 + x][3*height/5 + y] = 0.25;
      }
    }
  }

  public void setF(double f) {
    this.f = f;
  }

  public void setK(double k) {
    this.k = k;
  }


  public void timeStep(double dt) {
    double newMax = 0;
    double uv2;
    /*centre*/
    for (int x = 1; x<width-1; x++) {
      for (int y = 1; y<height-1; y++) {
        uv2 = tmpU[x][y]*tmpV[x][y]*tmpV[x][y];
        u[x][y] = tmpU[x][y] + dt*(duDivh2*(tmpU[x+1][y] + tmpU[x-1][y] + tmpU[x][y+1] + tmpU[x][y-1] - 4*tmpU[x][y]) - uv2 + f*(1-tmpU[x][y]));
        if (u[x][y]<0) u[x][y] = 0;
        v[x][y] = tmpV[x][y] + dt*(dvDivh2*(tmpV[x+1][y] + tmpV[x-1][y] + tmpV[x][y+1] + tmpV[x][y-1] - 4*tmpV[x][y]) + uv2 - k*tmpV[x][y]);
        if (v[x][y]<0) v[x][y] = 0;
      }
    }
    /*edges*/
    int x, y;
    for (x = 0; x<width; x++) {
      y = 0;
      uv2 = tmpU[x][y]*tmpV[x][y]*tmpV[x][y];
      u[x][y] = tmpU[x][y] + dt*(duDivh2*(tmpU[pBC(x+1,width)][y] + tmpU[pBC(x-1,width)][y] + tmpU[x][pBC(y+1, height)] + tmpU[x][pBC(y-1, height)] - 4*tmpU[x][y]) - uv2 + f*(1-tmpU[x][y]));
      if (u[x][y]<0) u[x][y] = 0;
      v[x][y] = tmpV[x][y] + dt*(dvDivh2*(tmpV[pBC(x+1,width)][y] + tmpV[pBC(x-1,width)][y] + tmpV[x][pBC(y+1, height)] + tmpV[x][pBC(y-1, height)] - 4*tmpV[x][y]) + uv2 - k*tmpV[x][y]);
      if (v[x][y]<0) v[x][y] = 0;
      y = height - 1;
      uv2 = tmpU[x][y]*tmpV[x][y]*tmpV[x][y];
      u[x][y] = tmpU[x][y] + dt*(duDivh2*(tmpU[pBC(x+1,width)][y] + tmpU[pBC(x-1,width)][y] + tmpU[x][pBC(y+1, height)] + tmpU[x][pBC(y-1, height)] - 4*tmpU[x][y]) - uv2 + f*(1-tmpU[x][y]));
      if (u[x][y]<0) u[x][y] = 0;
      v[x][y] = tmpV[x][y] + dt*(dvDivh2*(tmpV[pBC(x+1,width)][y] + tmpV[pBC(x-1,width)][y] + tmpV[x][pBC(y+1, height)] + tmpV[x][pBC(y-1, height)] - 4*tmpV[x][y]) + uv2 - k*tmpV[x][y]);
      if (v[x][y]<0) v[x][y] = 0;
    }
    for (y = 0; y<height; y++) {
      x = 0;
      uv2 = tmpU[x][y]*tmpV[x][y]*tmpV[x][y];
      u[x][y] = tmpU[x][y] + dt*(duDivh2*(tmpU[pBC(x+1,width)][y] + tmpU[pBC(x-1,width)][y] + tmpU[x][pBC(y+1, height)] + tmpU[x][pBC(y-1, height)] - 4*tmpU[x][y]) - uv2 + f*(1-tmpU[x][y]));
      if (u[x][y]<0) u[x][y] = 0;
      v[x][y] = tmpV[x][y] + dt*(dvDivh2*(tmpV[pBC(x+1,width)][y] + tmpV[pBC(x-1,width)][y] + tmpV[x][pBC(y+1, height)] + tmpV[x][pBC(y-1, height)] - 4*tmpV[x][y]) + uv2 - k*tmpV[x][y]);
      if (v[x][y]<0) v[x][y] = 0;
      x = width - 1;
      uv2 = tmpU[x][y]*tmpV[x][y]*tmpV[x][y];
      u[x][y] = tmpU[x][y] + dt*(duDivh2*(tmpU[pBC(x+1,width)][y] + tmpU[pBC(x-1,width)][y] + tmpU[x][pBC(y+1, height)] + tmpU[x][pBC(y-1, height)] - 4*tmpU[x][y]) - uv2 + f*(1-tmpU[x][y]));
      if (u[x][y]<0) u[x][y] = 0;
      v[x][y] = tmpV[x][y] + dt*(dvDivh2*(tmpV[pBC(x+1,width)][y] + tmpV[pBC(x-1,width)][y] + tmpV[x][pBC(y+1, height)] + tmpV[x][pBC(y-1, height)] - 4*tmpV[x][y]) + uv2 - k*tmpV[x][y]);
      if (v[x][y]<0) v[x][y] = 0;
    }

    for (x = 0; x<width; x++) {
      for (y = 0; y<height; y++) {
        tmpU[x][y] = u[x][y];
        tmpV[x][y] = v[x][y];
      }
    }
  }

  private int pBC(int x, int max) {                        /*periodic boundary conditions*/
    int xp = x;
    while (xp<0) xp += max;
    while (xp>=max) xp -= max;
    return xp;
  }

}
