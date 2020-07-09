package FuzzyProject.Fuzz.Utils;

import FuzzyProject.Fuzz.Models.Example;
import FuzzyProject.Fuzz.Models.SPFMiC;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;

import java.util.ArrayList;
import java.util.List;

public class FuzzyFunctions {
    public static List<SPFMiC> separateExamplesByClusterClassifiedByFuzzyCMeans(List<Example> exemplos, FuzzyKMeansClusterer fuzzyClusterer, double rotulo, double alpha, double theta) {
        List<SPFMiC> sfMiCS = new ArrayList<SPFMiC>();
        double[][] matriz = fuzzyClusterer.getMembershipMatrix().getData();
        List<CentroidCluster> centroides = fuzzyClusterer.getClusters();
        for(int j=0; j<centroides.size(); j++) {
            SPFMiC sfMiC = null;
            double SSD = 0;
            for(int k=0; k<exemplos.size(); k++) {
                int indiceMaior = getIndiceDoMaiorValor(matriz[k]);
                if(indiceMaior == j) {
                    if (sfMiC == null) {
                        sfMiC = new SPFMiC(centroides.get(j).getCenter().getPoint(),
                                centroides.get(j).getPoints().size(),
                                alpha,
                                theta);
                        sfMiC.setRotulo(rotulo);
                        double valorPertinencia = matriz[k][j];
//                        sfMiC.addPointToMm(valorPertinencia);
                        double[] ex = exemplos.get(k).getPonto();
                        double distancia = DistanceMeasures.calculaDistanciaEuclidiana(sfMiC.getCentroide(), ex);
//                        SSD += valorPertinencia * Math.pow(distancia, 2);
                        SSD += distancia * Math.pow(valorPertinencia, 2);
//                        SSD += Math.pow(valorPertinencia, 2) * Math.pow(distancia, 2);

                    } else {
                        double valorPertinencia = matriz[k][j];
                        double[] ex = exemplos.get(k).getPonto();
                        double distancia = DistanceMeasures.calculaDistanciaEuclidiana(sfMiC.getCentroide(), ex);
//                        SSD += valorPertinencia * Math.pow(distancia, 2);
                        SSD += distancia * Math.pow(valorPertinencia, 2);
//                        SSD += Math.pow(valorPertinencia, 2) * Math.pow(distancia, 2);
//                        sfMiC.addPointToMm(valorPertinencia);
                    }
                }
            }
            if(sfMiC != null) {
                if(sfMiC.getN() >= 5) {
                    sfMiC.setSSDe(SSD);
                    sfMiCS.add(sfMiC);
                }
            }
        }
        return sfMiCS;
    }

    private static int getIndiceDoMaiorValor(double[] array) {
        int index = 0;
        double maior = -1000000;
        for(int i=0; i<array.length; i++) {
            if(array[i] > maior && array[i] < 1){
                index = i;
                maior = array[i];
            }
        }
        return index;
    }

}
