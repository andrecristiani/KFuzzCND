package FuzzyProject.Fuzz.Utils;

import FuzzyProject.Fuzz.Models.Example;

import java.util.Vector;

public class DistanceMeasures {
    /***
     * Function used to calculate the euclidian distance between two point, used to tipicity calc
     */
    public static double calculaDistanciaEuclidiana(double[] ponto1, double[] ponto2) {
        double somatorio = 0;
        for(int i=0; i<ponto1.length; i++) {
            somatorio = somatorio + Math.pow((ponto1[i]-ponto2[i]),2);
        }
        return Math.sqrt(somatorio);
    }

    public static double calculaDistanciaEuclidiana(double[] ponto1, float[] ponto2) {
        double somatorio = 0;
        for(int i=0; i<ponto1.length; i++) {
            somatorio = somatorio + Math.pow((ponto1[i]-ponto2[i]),2);
        }
        return Math.sqrt(somatorio);
    }

    public static double calculaDistanciaEuclidiana(Example ponto1, double[] ponto2) {
        double somatorio = 0;
        for(int i=0; i<ponto1.getPonto().length; i++) {
            somatorio = somatorio + Math.pow((ponto1.getPonto()[i]-ponto2[i]),2);
        }
        return Math.sqrt(somatorio);
    }
}
