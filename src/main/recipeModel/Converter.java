package main.recipeModel;

public class Converter {

    public double getRoundMoldVolume(double r, double h){ return Math.PI*r*r*h;}

    public double getRectangularMoldVolume(double a, double b, double h) {return a*b*h;}

}
