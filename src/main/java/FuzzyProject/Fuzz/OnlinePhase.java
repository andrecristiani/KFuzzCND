package FuzzyProject.Fuzz;

import FuzzyProject.Fuzz.Models.Ensemble;
import FuzzyProject.Fuzz.Models.*;
import FuzzyProject.Fuzz.Models.Evaluation.AcuraciaMedidas;
import FuzzyProject.Fuzz.Utils.DistanceMeasures;
import FuzzyProject.Fuzz.Utils.Evaluation;
import FuzzyProject.Fuzz.Utils.FuzzyFunctions;
import FuzzyProject.Fuzz.Utils.HandlesFiles;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.*;

public class OnlinePhase {

    public List<Example> exemplosEsperandoTempo = new ArrayList<>();
    public List<ClassicMeasures> desempenho = new ArrayList<>();
    int acertos = 0;
    int acertosTotal = 0;
    int erros = 0;
    int errosTotal = 0;
    Ensemble ensemble;

    int novidadesClassificadas = 0;
    int exemplosClassificados = 0;
    public int fpGlobal;
    public int fnGlobal;

    public void initialize(String caminho, String dataset, Ensemble comite, int latencia, int tChunk, int T, int kShort, double phi) {

        List<AcuraciaMedidas> acuracias = new ArrayList<>();
        this.ensemble = comite;
        DataSource source;
        Instances data;
        Instances esperandoTempo;
        int nExeTemp = 0;
        try {
            source = new DataSource(caminho + dataset + "-instances.arff");
            data = source.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            ArrayList<Attribute> atts = new ArrayList<>();
            for(int i=0; i<data.numAttributes(); i++) {
                atts.add(data.attribute(i));
            }
            esperandoTempo = source.getDataSet();
            List<Example> labeledMem = new ArrayList<>();
            List<Example> unkMem = new ArrayList<>();

            int desconhecido = 0;
            for(int i=0, j=0, h=0; i<data.size(); i++, j++, h++) {
                if(i==25000) {
                    System.out.println(i);
                }
                Instance ins = data.get(i);
                Example exemplo = new Example(ins.toDoubleArray(), true);
                double rotulo = comite.classify(ins);
                exemplo.setRotuloClassificado(rotulo);
                if(rotulo == exemplo.getRotuloVerdadeiro()) {
                    acertos++;
                    acertosTotal++;
                } else if (rotulo == -1) {
                    desconhecido++;
                    unkMem.add(exemplo);
                    if(unkMem.size() >= T) {
                        unkMem = this.newBinaryNoveltyDetection(unkMem, kShort, phi, T);
                    }
                } else {
                    erros++;
                    errosTotal++;
                }

                this.exemplosEsperandoTempo.add(exemplo);
                if(j >= latencia) {
                    Example labeledExample = new Example(esperandoTempo.get(nExeTemp).toDoubleArray(), true);
                    labeledMem.add(labeledExample);
                    if(labeledMem.size() >= tChunk) {
                        System.err.println("Treinando nova árvore no ponto: " + i);
                        labeledMem = comite.trainNewClassifier(labeledMem);
                    }
                    nExeTemp++;
                }

                if(h == 1000) {
                    h=0;
                    acuracias.add(Evaluation.calculaAcuracia(acertos, 730, i));
                    System.out.println("I: " + i + "|| erros: " + erros);
                    acertos = 0;
                    erros = 0;
                }
            }
            acuracias.add(Evaluation.calculaAcuracia(acertos, 730, data.size()));
            HandlesFiles.salvaPredicoes(acuracias, dataset);
            System.out.println("Acertos: " + acertosTotal);
            System.out.println("Erros: " + errosTotal);
            System.out.println("Desconhecidos: " + desconhecido);
            System.out.println("Sem classificar: " + unkMem.size());

        } catch (Exception ex) {
            System.out.println(ex);
            System.out.println(ex.getStackTrace());
        }
    }

    private List<Example> newBinaryNoveltyDetection(List<Example> listaDesconhecidos, int kCurto, double phi, int T) {
//        System.out.println("Executando DN");
        int minWeight = T/4;
        FuzzyKMeansClusterer clusters = FuzzyFunctions.fuzzyCMeans(listaDesconhecidos, kCurto, this.ensemble.fuzzification);
        List<CentroidCluster> centroides = clusters.getClusters();
        List<Double> silhuetas = FuzzyFunctions.fuzzySilhouette(clusters, listaDesconhecidos, this.ensemble.alpha);
        List<Integer> silhuetasValidas = new ArrayList<>();

        for(int i=0; i<silhuetas.size(); i++) {
            if(silhuetas.get(i) > 0 && centroides.get(i).getPoints().size() >= minWeight) {
                silhuetasValidas.add(i);
            }
        }

        List<SPFMiC> sfMiCS = FuzzyFunctions.newSeparateExamplesByClusterClassifiedByFuzzyCMeans(listaDesconhecidos, clusters, -1, this.ensemble.alpha, this.ensemble.theta, minWeight);
        List<SPFMiC> sfmicsConhecidos = ensemble.getAllSPFMiCs();
        List<Double> frs = new ArrayList<>();
        List<List<Double>> frsList = new ArrayList<>();

        for(int i=0; i<sfMiCS.size(); i++) {
            if(!sfMiCS.get(i).isNull()) {
                frs.clear();
                double dist2 = Double.MAX_VALUE;
                SPFMiC spfMiCMenorDistancia = new SPFMiC();
                for (int j = 0; j < sfmicsConhecidos.size(); j++) {
                    double dist3 = DistanceMeasures.calculaDistanciaEuclidiana(sfMiCS.get(i).getCentroide(), sfmicsConhecidos.get(j).getCentroide());
                    if(dist3 < dist2) {
                        dist2 = dist3;
                        spfMiCMenorDistancia = sfmicsConhecidos.get(j);
                    }

                    double di = sfmicsConhecidos.get(j).getRadius();
                    double dj = sfMiCS.get(i).getRadius();
                    double dist = (di + dj) / DistanceMeasures.calculaDistanciaEuclidiana(sfmicsConhecidos.get(j).getCentroide(), sfMiCS.get(i).getCentroide());
                    frs.add((di + dj) / dist);
                }

                Double minFr = Collections.min(frs);
                int indexMinFr = frs.indexOf(minFr);
                if (minFr <= phi) {
                    sfMiCS.get(i).minFr = minFr;
                    sfMiCS.get(i).rotuloMenorDistancia = spfMiCMenorDistancia.getRotulo();
                    sfMiCS.get(i).setRotulo(sfmicsConhecidos.get(indexMinFr).getRotulo());
                } else {
                    sfMiCS.get(i).minFr = minFr;
                    sfMiCS.get(i).rotuloMenorDistancia = spfMiCMenorDistancia.getRotulo();
                    sfMiCS.get(i).setRotulo(-2);

                }
                frsList.add(frs);
            }
        }

        for(int i=0; i<centroides.size(); i++) {
            if(silhuetasValidas.contains(i)) {
                List<Example> examplesOfCluster = centroides.get(i).getPoints();
                for(int j=0; j<examplesOfCluster.size(); j++) {
                    examplesOfCluster.get(j).setRotuloClassificado(sfMiCS.get(i).getRotulo());
                    novidadesClassificadas++;
                    exemplosClassificados++;
                    if (this.ensemble.knowLabels.contains(examplesOfCluster.get(j).getRotuloVerdadeiro()) && examplesOfCluster.get(j).getRotuloClassificado() == -2) {
//                    fp++;
                        fpGlobal++;
                        errosTotal++;
                      System.err.println("Verdadeiro: " + examplesOfCluster.get(j).getRotuloVerdadeiro() + " classificou como: " + examplesOfCluster.get(j).getRotuloClassificado() + " FR: " + sfMiCS.get(i).minFr + " rótulo menor distância: " + sfMiCS.get(i).rotuloMenorDistancia + " Cluster: " + i);
                    } else if (this.ensemble.knowLabels.contains(examplesOfCluster.get(j).getRotuloVerdadeiro()) && examplesOfCluster.get(j).getRotuloClassificado() != examplesOfCluster.get(j).getRotuloVerdadeiro()) {
//                    fe++;
//                    feGlobal++;
                        errosTotal++;
                        System.err.println("Verdadeiro: " + examplesOfCluster.get(j).getRotuloVerdadeiro() + " classificou como: " + examplesOfCluster.get(j).getRotuloClassificado() + " FR: " + sfMiCS.get(i).minFr + " rótulo menor distância: " + sfMiCS.get(i).rotuloMenorDistancia + " Cluster: " + i);
                    } else {
                        acertosTotal++;
                    }
                    listaDesconhecidos.remove(examplesOfCluster.get(j));
                }
            }
        }

        return listaDesconhecidos;
    }

    private int getIndiceDoMaiorValor(double[] array) {
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
