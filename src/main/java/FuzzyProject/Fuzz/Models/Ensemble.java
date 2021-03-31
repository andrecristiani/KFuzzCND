package FuzzyProject.Fuzz.Models;

import FuzzyProject.Fuzz.Utils.DistanceMeasures;
import FuzzyProject.Fuzz.Utils.FuzzyFunctions;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
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

    public double classifyNew(Instance ins, int updateTime) throws Exception {
        List<SPFMiC> allSPFMiCSOfClassifier = new ArrayList<>();
        for (int i = 0; i < ensembleOfClassifiers.size(); i++) {
            Map<Double, List<SPFMiC>> classifier = this.ensembleOfClassifiers.get(i);
            allSPFMiCSOfClassifier.addAll(this.getAllSPFMiCsFromClassifier(classifier));
        }

        double rotuloVotado = this.classify(allSPFMiCSOfClassifier, new Example(ins.toDoubleArray(), true), updateTime);

        return rotuloVotado;
    }

    public double classifyComErro(Instance ins, int updateTime) throws Exception {
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
        Map<Double, List<Example>> examplesByClass = FuzzyFunctions.separateByClasses(chunk);
        List<Double> classes = new ArrayList<>();
        Map<Double, List<SPFMiC>> classifier = new HashMap<>();
        classes.addAll(examplesByClass.keySet());
        for(int j=0; j<examplesByClass.size(); j++) {
            if(examplesByClass.get(classes.get(j)).size() >= this.K * 2) {
                if(!this.knowLabels.contains(classes.get(j))) {
                    this.knowLabels.add(classes.get(j));
                }
                System.out.println("Treinar novo classificador");
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
        List<Double> todasTipicidades = new ArrayList<>();
        List<SPFMiC> auxSPFMiCs = new ArrayList<>();
        boolean isOutlier = true;
        for(int i=0; i<spfMiCS.size(); i++) {
            double distancia = DistanceMeasures.calculaDistanciaEuclidiana(example, spfMiCS.get(i).getCentroide());
            todasTipicidades.add(spfMiCS.get(i).calculaTipicidade(example.getPonto(), N, K));
            if(distancia <= spfMiCS.get(i).getRadiusWithWeight()) {
                isOutlier = false;
                distancias.add(distancia);
                tipicidades.add(spfMiCS.get(i).calculaTipicidade(example.getPonto(), N, K));
                auxSPFMiCs.add(spfMiCS.get(i));
            }
        }

        if(isOutlier) {
            return -1;
        }

        Double maxVal = Collections.max(tipicidades);
        int indexMax = tipicidades.indexOf(maxVal);

        SPFMiC spfmic = auxSPFMiCs.get(indexMax);
        int index = spfMiCS.indexOf(spfmic);
//        System.out.println(spfMiCS.get(index).getUpdated());
        spfMiCS.get(index).setUpdated(updateTime);
        return spfMiCS.get(index).getRotulo();
    }

    public double classifyWithoutTime(List<SPFMiC> spfMiCS, Example example) {
        List<Double> tipicidades = new ArrayList<>();
        List<Double> distancias = new ArrayList<>();
        boolean isOutlier = true;
        for(int i=0; i<spfMiCS.size(); i++) {
            tipicidades.add(spfMiCS.get(i).calculaTipicidade(example.getPonto(), this.N, this.K));
            double distancia = DistanceMeasures.calculaDistanciaEuclidiana(example, spfMiCS.get(i).getCentroide());
            distancias.add(distancia);
            if(distancia <= spfMiCS.get(i).getRadiusWithWeight()) {
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

    public void removeOldSPFMiCs(int ts, int currentTime) {
        for (int i = 0; i < ensembleOfClassifiers.size(); i++) {
            Map<Double, List<SPFMiC>> classifier = this.ensembleOfClassifiers.get(i);
            List<Double> keys = new ArrayList<>();
            keys.addAll(classifier.keySet());
            for(int j=0; j<classifier.size(); j++) {
                List<SPFMiC> spfMiCSatuais = classifier.get(keys.get(j));
                List<SPFMiC> spfMiCSAux = spfMiCSatuais;
                for(int k=0; k<spfMiCSatuais.size(); k++) {
                    if(currentTime - spfMiCSatuais.get(k).getT() > ts && currentTime - spfMiCSatuais.get(k).getUpdated() > ts) {
                        spfMiCSAux.remove(spfMiCSatuais.get(k));
                    }
                }
                classifier.put(keys.get(j), spfMiCSAux);
            }
        }
    }
}
