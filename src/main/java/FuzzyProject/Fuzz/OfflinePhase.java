package FuzzyProject.Fuzz;

import FuzzyProject.Fuzz.Models.Ensemble;
import weka.core.Instances;

public class OfflinePhase {
    public Ensemble inicializar(String dataset, String caminho, int tComite, Instances trainSet, double fuzzification, double alpha, double theta, int C, int K, int minWeight) throws Exception {
        Ensemble ensemble = new Ensemble(dataset, caminho, tComite, fuzzification, alpha, theta, C, K, minWeight);
        ensemble.trainInitialEnsemble(trainSet);
        return ensemble;
    }
}
