package FuzzyProject.Fuzz.Utils;

import FuzzyProject.Fuzz.Models.Example;
import FuzzyProject.Fuzz.Models.SPFMiC;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.*;

public class FuzzyFunctions {
    public static FuzzyKMeansClusterer fuzzyCMeans(List<Example> examples, int K, double fuzzification) {
        FuzzyKMeansClusterer fuzzyClusterer = new FuzzyKMeansClusterer(K, fuzzification);
        try {
            System.out.println("Antes");
            System.out.println("Quantidade de exemplos: " + examples.size());
            fuzzyClusterer.cluster(examples);
            System.out.println("Depois");
        } catch (Exception ex) {
            System.err.println("Deu algum erro mas continuei");
        }
        return fuzzyClusterer;
    }

    public static List<Double> fuzzySilhouette(FuzzyKMeansClusterer clusters, List<Example> desconhecidos, double alpha) {
        int nExemplos = desconhecidos.size();
        double[][] matriz = clusters.getMembershipMatrix().getData();
        double numerador = 0;
        double denominador = 0;
        double apj = 0;
        List<Double> dqj = new ArrayList<>();
        List<Double> silhuetas = new ArrayList<>();
        for(int i=0; i<clusters.getK(); i++) {
            for (int j = 0; j < nExemplos; j++) {
                int indexClasse = getIndiceDoMaiorValor(matriz[j]);
                if (indexClasse == i) {
                    for (int k = 0; k < nExemplos; k++) {
                        if (getIndiceDoMaiorValor(matriz[k]) == indexClasse) {
                            apj += DistanceMeasures.calculaDistanciaEuclidiana(desconhecidos.get(j).getPoint(), desconhecidos.get(k).getPoint());
                        } else {
                            dqj.add(DistanceMeasures.calculaDistanciaEuclidiana(desconhecidos.get(j).getPoint(), desconhecidos.get(k).getPoint()));
                        }
                    }

                    apj = apj / nExemplos;
                    if(dqj.size() != 0) {
                        double bpj = Collections.min(dqj);
                        double sj = (bpj - apj) / Math.max(apj, bpj);
                        double[] maiorESegundaMeiorPertinencia = getFirstAndSecondBiggerPertinence(matriz[j], j);
                        double upj = maiorESegundaMeiorPertinencia[0];
                        double uqj = maiorESegundaMeiorPertinencia[1];
                        numerador += Math.pow((upj - uqj), alpha) * sj;
                        denominador += Math.pow((upj - uqj), alpha);
                    }
                }
            }
            double fs = numerador / denominador;
            silhuetas.add(fs);
        }
        return silhuetas;
    }

    private static double[] getFirstAndSecondBiggerPertinence(double valores[], int j) {
        double[] resultado = new double[2];
        List<Double> lista = new ArrayList<>();
        for(int i=0; i<valores.length; i++) {
            lista.add(valores[i]);
        }
        Collections.sort(lista, Collections.reverseOrder());
        resultado[0] = lista.get(0);
        resultado[1] = lista.get(1);
        return resultado;
    }

    public static List<SPFMiC> separateExamplesByClusterClassifiedByFuzzyCMeans(List<Example> exemplos, FuzzyKMeansClusterer fuzzyClusterer, double rotulo, double alpha, double theta, int minWeight, int t) {
        List<SPFMiC> sfMiCS = new ArrayList<SPFMiC>();
        double[][] matriz = fuzzyClusterer.getMembershipMatrix().getData();
        List<CentroidCluster> centroides = fuzzyClusterer.getClusters();
        for(int j=0; j<centroides.size(); j++) {
            SPFMiC sfMiC = null;
            double SSD = 0;
            double teste = 0;
            for(int k=0; k<exemplos.size(); k++) {
                int indiceMaior = getIndiceDoMaiorValor(matriz[k]);
                if(indiceMaior == j) {
                    if (sfMiC == null) {
                        sfMiC = new SPFMiC(centroides.get(j).getCenter().getPoint(),
                                centroides.get(j).getPoints().size(),
                                alpha,
                                theta, t);
                        sfMiC.setRotulo(rotulo);
                        double valorPertinencia = matriz[k][j];
                        double[] ex = exemplos.get(k).getPonto();
                        double distancia = DistanceMeasures.calculaDistanciaEuclidiana(sfMiC.getCentroide(), ex);
                        EuclideanDistance dist = new EuclideanDistance();
                        double distN = dist.compute(sfMiC.getCentroide(), ex);
                        teste += Math.pow(valorPertinencia, 2);
                        SSD += valorPertinencia * Math.pow(distancia, 2);

                    } else {
                        double valorPertinencia = matriz[k][j];
                        double[] ex = exemplos.get(k).getPonto();
                        double distancia = DistanceMeasures.calculaDistanciaEuclidiana(sfMiC.getCentroide(), ex);
//                        SSD += distancia * Math.pow(valorPertinencia, 2);
                        teste += Math.pow(valorPertinencia, 2);
                        SSD += valorPertinencia * Math.pow(distancia, 2);
//                        System.out.println("Valor: " + SSD);
                    }
                }
            }
            if(sfMiC != null) {
                if(sfMiC.getN() >= minWeight) {
                    sfMiC.setSSDe(SSD);
                    sfMiCS.add(sfMiC);
                }
            }
        }
        return sfMiCS;
    }

    public static List<SPFMiC> newSeparateExamplesByClusterClassifiedByFuzzyCMeans(List<Example> exemplos, FuzzyKMeansClusterer fuzzyClusterer, double rotulo, double alpha, double theta, int minWeight, int t) {
        List<SPFMiC> sfMiCS = new ArrayList<SPFMiC>();
        double[][] matriz = fuzzyClusterer.getMembershipMatrix().getData();
        List<CentroidCluster> centroides = fuzzyClusterer.getClusters();
        for(int j=0; j<centroides.size(); j++) {
            SPFMiC sfMiC = null;
            double SSD = 0;
            double teste = 0;
            List<Example> examples = centroides.get(j).getPoints();
            for(int k=0; k<examples.size(); k++) {
                int indexExample = exemplos.indexOf(examples.get(k));
                if (sfMiC == null) {
                    sfMiC = new SPFMiC(centroides.get(j).getCenter().getPoint(),
                            centroides.get(j).getPoints().size(),
                            alpha,
                            theta, t);
                    sfMiC.setRotulo(rotulo);
                    double valorPertinencia = matriz[indexExample][j];
                    double[] ex = exemplos.get(k).getPonto();
                    double distancia = DistanceMeasures.calculaDistanciaEuclidiana(sfMiC.getCentroide(), ex);
//                    SSD += distancia * Math.pow(valorPertinencia, 2);
                    teste += distancia * Math.pow(valorPertinencia, 2);
                    SSD += valorPertinencia * Math.pow(distancia, 2);
                } else {
                    double valorPertinencia = matriz[k][j];
                    double[] ex = exemplos.get(k).getPonto();
                    double distancia = DistanceMeasures.calculaDistanciaEuclidiana(sfMiC.getCentroide(), ex);
//                    SSD += distancia * Math.pow(valorPertinencia, 2);
                    teste += distancia * Math.pow(valorPertinencia, 2);
                    SSD += valorPertinencia * Math.pow(distancia, 2);
                }
            }
            if(sfMiC != null) {
                if(sfMiC.getN() >= minWeight) {
                    sfMiC.setSSDe(SSD);
                }
            }
            sfMiCS.add(sfMiC);
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

    public static Map<Double, List<Example>> separateByClasses(List<Example> chunk) {
        Map<Double, List<Example>> examplesByClass = new HashMap<>();
        for(int i=0; i<chunk.size(); i++) {
            if(examplesByClass.containsKey(chunk.get(i).getRotuloVerdadeiro())) {
                examplesByClass.get(chunk.get(i).getRotuloVerdadeiro()).add(chunk.get(i));
            } else {
                examplesByClass.put(chunk.get(i).getRotuloVerdadeiro(), new ArrayList<>());
                examplesByClass.get(chunk.get(i).getRotuloVerdadeiro()).add(chunk.get(i));
            }
        }
        return examplesByClass;
    }

}
