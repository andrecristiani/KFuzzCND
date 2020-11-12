package FuzzyProject.Fuzz.Models;

import FuzzyProject.Fuzz.Utils.DistanceMeasures;
import FuzzyProject.Fuzz.Utils.FuzzyFunctions;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

public class Ensemble {

    public int tamanhoMaximo;
    public String dataset;
    public String caminho;
    public double fuzzification;
    public double alpha;
    public double theta;
    public int C;
    public int K;
    public int N;
    public int minWeight;
    public MaxTipicity allTipMax;
    public double thetaAdapter = 0;
    public List<Map<Double, List<SPFMiC>>> ensembleOfClassifiers = new ArrayList<>();
    public List<Double> knowLabels = new ArrayList<>();

    public Ensemble(String dataset, String caminho, int tamanhoMaximo, double fuzzification, double alpha, double theta, int C, int K, int minWeight) {
        this.dataset = dataset;
        this.caminho = caminho;
        this.tamanhoMaximo = tamanhoMaximo;
        this.fuzzification = fuzzification;
        this.alpha = alpha;
        this.theta = theta;
        this.C = C;
        this.K = K;
        this.minWeight = minWeight;
    }

    public void trainInitialEnsemble(Instances trainSet) throws Exception {
        List<Example> chunk = new ArrayList<>();
        for(int i=0; i<trainSet.size(); i++) {
            Example ex = new Example(trainSet.instance(i).toDoubleArray(), true);
            chunk.add(ex);
            if(chunk.size() == C) {
                Map<Double, List<Example>> examplesByClass = FuzzyFunctions.separateByClasses(chunk);
                List<Double> classes = new ArrayList<>();
                Map<Double, List<SPFMiC>> classifier = new HashMap<>();
                classes.addAll(examplesByClass.keySet());
                for(int j=0; j<examplesByClass.size(); j++) {
                    if(examplesByClass.get(classes.get(j)).size() > this.K) {
                        if (!this.knowLabels.contains(classes.get(j))) {
                            this.knowLabels.add(classes.get(j));
                        }
                        FuzzyKMeansClusterer clusters = FuzzyFunctions.fuzzyCMeans(examplesByClass.get(classes.get(j)), this.K, this.fuzzification);
                        List<SPFMiC> spfmics = FuzzyFunctions.separateExamplesByClusterClassifiedByFuzzyCMeans(examplesByClass.get(classes.get(j)), clusters, classes.get(j), this.alpha, this.theta, this.minWeight, 0);
                        classifier.put(classes.get(j), spfmics);
                    }
                }
                this.ensembleOfClassifiers.add(classifier);
                chunk.clear();
            }
        }
    }

//    public double classify(Instance ins) throws Exception {
//
//        Map<Double, Integer> numeroVotos = new HashMap<>();
//
//        List<String> votos = new ArrayList<>();
//        for (int i = 0; i < ensembleOfClassifiers.size(); i++) {
//            Map<Double, List<SPFMiC>> classifier = this.ensembleOfClassifiers.get(i);
//            List<SPFMiC> allSPFMiCSOfClassifier = this.getAllSPFMiCsFromClassifier(classifier);
//            double rotuloVotado = this.classify(allSPFMiCSOfClassifier, new Example(ins.toDoubleArray(), true));
//            if(numeroVotos.containsKey(rotuloVotado)) {
//                numeroVotos.replace(rotuloVotado, numeroVotos.get(rotuloVotado) + 1);
//            } else {
//                numeroVotos.put(rotuloVotado, 1);
//            }
//        }
//
//        int valorMaior = -1;
//        double indiceMaior = 0;
//
//        Set<Double> chavesAux = numeroVotos.keySet();
//        Object[] chaves = chavesAux.toArray();
//        for(int i=0; i<numeroVotos.size(); i++) {
//            if(valorMaior < numeroVotos.get(chaves[i])) {
//                valorMaior = numeroVotos.get(chaves[i]);
//                indiceMaior = (double) chaves[i];
//            }
//        }
//
//        return indiceMaior;
//    }

    public double classifyNew(Instance ins, int updateTime) throws Exception {
        List<SPFMiC> allSPFMiCSOfClassifier = new ArrayList<>();
        for (int i = 0; i < ensembleOfClassifiers.size(); i++) {
            Map<Double, List<SPFMiC>> classifier = this.ensembleOfClassifiers.get(i);
            allSPFMiCSOfClassifier.addAll(this.getAllSPFMiCsFromClassifier(classifier));
        }

        double rotuloVotado = this.classify(allSPFMiCSOfClassifier, new Example(ins.toDoubleArray(), true), updateTime);

        return rotuloVotado;
    }

    private void removeWorstClassifier(List<Example> exemplosRotulados) throws Exception {
        System.err.println("removendo classificador");
        double[] pontuacaoArvores = new double[this.ensembleOfClassifiers.size()];
        for(int i=0; i<this.ensembleOfClassifiers.size(); i++) {
            pontuacaoArvores[i] = 0;
        }

        for(int i=0; i<exemplosRotulados.size(); i++) {
            for(int k=0; k<this.ensembleOfClassifiers.size(); k++) {
                Map<Double, List<SPFMiC>> classifier = this.ensembleOfClassifiers.get(k);
                List<SPFMiC> allSPFMiCSOfClassifier = this.getAllSPFMiCsFromClassifier(classifier);
                double rotuloVotado = this.classifyWithoutTime(allSPFMiCSOfClassifier, exemplosRotulados.get(i));
                if(rotuloVotado == exemplosRotulados.get(i).getRotuloVerdadeiro()) {
                    pontuacaoArvores[k]++;
                }
            }
        }

        List<Double> acuraciaArvores = new ArrayList<>();
        double acuraciaMinima = 100;
        int index = 0;
        for(int i=0; i<this.ensembleOfClassifiers.size(); i++) {
            double acuracia = ((pontuacaoArvores[i]/exemplosRotulados.size())*100);
            acuraciaArvores.add(acuracia);
            if(acuracia < acuraciaMinima) {
                acuraciaMinima = acuracia;
                index = i;
            }
        }
        this.ensembleOfClassifiers.remove(index);
    }

    public List<Example> trainNewClassifier(List<Example> chunk, int t) throws Exception {
        List<Example> newChunk = new ArrayList<>();
        if(this.ensembleOfClassifiers.size() >= tamanhoMaximo) {
            this.removeWorstClassifier(chunk);
        }
        Map<Double, List<Example>> examplesByClass = FuzzyFunctions.separateByClasses(chunk);
        List<Double> classes = new ArrayList<>();
        Map<Double, List<SPFMiC>> classifier = new HashMap<>();
        classes.addAll(examplesByClass.keySet());
        for(int j=0; j<examplesByClass.size(); j++) {
            if(examplesByClass.get(classes.get(j)).size() >= this.K) {
                if(!this.knowLabels.contains(classes.get(j))) {
                    this.knowLabels.add(classes.get(j));
                }
                FuzzyKMeansClusterer clusters = FuzzyFunctions.fuzzyCMeans(examplesByClass.get(classes.get(j)), this.K, this.fuzzification);
                List<SPFMiC> spfmics = FuzzyFunctions.separateExamplesByClusterClassifiedByFuzzyCMeans(examplesByClass.get(classes.get(j)), clusters, classes.get(j), this.theta, this.alpha, this.minWeight, t);
                classifier.put(classes.get(j), spfmics);
            } else {
                newChunk.addAll(examplesByClass.get(classes.get(j)));
            }
        }
        this.ensembleOfClassifiers.add(classifier);
        return newChunk;
    }

    private List<SPFMiC> getAllSPFMiCsFromClassifier(Map<Double, List<SPFMiC>> classifier) {
        List<SPFMiC> spfMiCS = new ArrayList<>();
        List<Double> keys = new ArrayList<>();
        keys.addAll(classifier.keySet());
        for(int i=0; i<classifier.size(); i++) {
            spfMiCS.addAll(classifier.get(keys.get(i)));
        }
        return spfMiCS;
    }

    public List<SPFMiC> getAllSPFMiCs() {
        List<SPFMiC> spfMiCS = new ArrayList<>();
        for(int i=0; i<this.ensembleOfClassifiers.size(); i++) {
            Map<Double, List<SPFMiC>> classifier = this.ensembleOfClassifiers.get(i);
            spfMiCS.addAll(this.getAllSPFMiCsFromClassifier(classifier));
        }
        return spfMiCS;
    }

    public double classify(List<SPFMiC> spfMiCS, Example example, int updateTime) {
        List<Double> tipicidades = new ArrayList<>();
        List<Double> distancias = new ArrayList<>();
        boolean isOutlier = true;
        double minDistance = Double.MAX_VALUE;
        int indexMinDistance = -2;
        for(int i=0; i<spfMiCS.size(); i++) {
            tipicidades.add(spfMiCS.get(i).calculaTipicidade(example.getPonto(), this.N, this.K));
            double distancia = DistanceMeasures.calculaDistanciaEuclidiana(example, spfMiCS.get(i).getCentroide());
            distancias.add(distancia);
            if(distancia < minDistance) {
                minDistance = distancia;
                indexMinDistance = i;
            }
            if(distancia <= spfMiCS.get(i).getRadius()) {
                isOutlier = false;
            }
        }

        Double minVal = Collections.min(distancias);
        int indexMin = tipicidades.indexOf(minVal);

//        System.out.println("Distância: " + distancias.get(indexMinDistance) + ", Raio: " + spfMiCS.get(indexMinDistance).getRadius() + ", Rotulo SPFMic: " + spfMiCS.get(indexMinDistance).getRotulo() + ", Rotulo Exemplo: " + example.getRotuloVerdadeiro() + ", Outlier: " + isOutlier);

        if(isOutlier) {
            return -1;
        }

        Double maxVal = Collections.max(tipicidades);
        int indexMax = tipicidades.indexOf(maxVal);
        spfMiCS.get(indexMax).setUpdated(updateTime);
        return spfMiCS.get(indexMax).getRotulo();
    }

    public double classifyWithoutTime(List<SPFMiC> spfMiCS, Example example) {
        List<Double> tipicidades = new ArrayList<>();
        List<Double> distancias = new ArrayList<>();
        boolean isOutlier = true;
        for(int i=0; i<spfMiCS.size(); i++) {
            tipicidades.add(spfMiCS.get(i).calculaTipicidade(example.getPonto(), this.N, this.K));
            double distancia = DistanceMeasures.calculaDistanciaEuclidiana(example, spfMiCS.get(i).getCentroide());
            distancias.add(distancia);
            if(distancia <= spfMiCS.get(i).getRadius()) {
                isOutlier = false;
            }
        }

        if(isOutlier) {
            return -1;
        }

        Double maxVal = Collections.max(tipicidades);
        int indexMax = tipicidades.indexOf(maxVal);
        return spfMiCS.get(indexMax).getRotulo();
    }
}
