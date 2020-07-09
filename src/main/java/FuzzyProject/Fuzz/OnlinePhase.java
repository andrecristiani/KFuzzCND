package FuzzyProject.Fuzz;

import FuzzyProject.Fuzz.Models.Ensemble;
import FuzzyProject.Fuzz.Models.*;
import FuzzyProject.Fuzz.Models.Evaluation.AcuraciaMedidas;
import FuzzyProject.Fuzz.Utils.DistanceMeasures;
import FuzzyProject.Fuzz.Utils.Evaluation;
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



    public void initialize(String caminho, String dataset, Ensemble comite, int latencia, int tChunk, int T, int kShort, double phi) {

        List<AcuraciaMedidas> acuracias = new ArrayList<>();
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
                Instance ins = data.get(i);
                Example exemplo = new Example(ins.toDoubleArray(), true);
                double rotulo = comite.classify(ins);
                if(rotulo == exemplo.getRotuloVerdadeiro()) {
                    acertos++;
                    acertosTotal++;
                } else if (rotulo == -1) {
                    unkMem.add(exemplo);
                    if(unkMem.size() >= T) {
//                        unkMem = this.detectaNovidadesBinarioFuzzyCMeans(unkMem, kShort, comite, phi);
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

                if(h == 730) {
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

        } catch (Exception ex) {
            System.out.println(ex);
            System.out.println(ex.getStackTrace());
        }
    }

//    private List<Example> detectaNovidadesBinarioFuzzyCMeans(List<Example> listaDesconhecidos, int kCurto, ComiteArvores comite, double phi) {
//        FuzzyKMeansClusterer clusters = this.fuzzyCMeans(listaDesconhecidos, kCurto);
//        List<CentroidCluster> centroides = clusters.getClusters();
//        List<Double> silhuetas = this.calculaSilhuetaFuzzy2(clusters, listaDesconhecidos);
//        List<Integer> silhuetasValidas = new ArrayList<>();
//        double[][] matrizPertinencia = clusters.getMembershipMatrix().getData();
//
//        for(int i=0; i<silhuetas.size(); i++) {
//            if(silhuetas.get(i) > 0 && centroides.get(i).getPoints().size() >= pesoMinimoGrupo) {
//                silhuetasValidas.add(i);
//            }
//        }
//
//        List<SPFMiC> sfMiCS = FuncoesDeClassificacao.separaExemplosPorGrupoClassificado(listaDesconhecidos, clusters, this.fuzzificacao, alpha, betha);
//        List<SPFMiC> sfmicsConhecidos = comite.getTodosSFMiCs();
//        List<Double> frs = new ArrayList<>();
//
//        for(int i=0; i<sfMiCS.size(); i++) {
//            if(!sfMiCS.get(i).isNull()) {
//                frs.clear();
//                for (int j = 0; j < sfmicsConhecidos.size(); j++) {
//                    double di = sfmicsConhecidos.get(j).getDispersao();
//                    double dj = sfMiCS.get(i).getDispersao();
//                    double dist = (di + dj) / DistanceMeasures.calculaDistanciaEuclidiana(sfmicsConhecidos.get(j).getCentroide(), sfMiCS.get(i).getCentroide());
//                    frs.add((di + dj) / dist);
//                }
//
//                Double maxVal = Collections.min(frs);
//                int indexMax = frs.indexOf(maxVal);
//                if (maxVal > phi) {
//                    sfMiCS.get(i).setRotulo(sfmicsConhecidos.get(indexMax).getRotulo());
//                } else {
//                    sfMiCS.get(i).setRotulo(-2);
//                }
//            }
//        }
//
//        for(int i=0; i<listaDesconhecidos.size(); i++) {
//            int cluster = this.getIndiceDoMaiorValor(matrizPertinencia[i]);
//            if(silhuetasValidas.contains(cluster)) {
//                listaDesconhecidos.get(i).setRotuloClassificado(sfMiCS.get(cluster).getRotulo());
//                listaDesconhecidos.remove(i);
//            }
//        }
//
//        return listaDesconhecidos;
//    }
//
//    private int getIndiceDoMaiorValor(double[] array) {
//        int index = 0;
//        double maior = -1000000;
//        for(int i=0; i<array.length; i++) {
//            if(array[i] > maior && array[i] < 1){
//                index = i;
//                maior = array[i];
//            }
//        }
//        return index;
//    }
}
