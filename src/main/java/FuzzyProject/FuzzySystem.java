package FuzzyProject;

import FuzzyProject.Fuzz.Models.Ensemble;
import FuzzyProject.Fuzz.Models.MaxTipicity;
import FuzzyProject.Fuzz.OfflinePhase;
import FuzzyProject.Fuzz.OnlinePhase;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FuzzySystem {
    public static void main(String[] args) throws IOException, Exception {
        String dataset = "rbf";
        String caminho = "";
        String current = (new File(".")).getCanonicalPath();
        caminho = current + "/" + dataset + "/";
        double fuzzyfication = 2;
        double alpha = 2;
        double theta = 1;

        ConverterUtils.DataSource source1;
        Instances data1;

        source1 = new ConverterUtils.DataSource(caminho + dataset + "-train.arff");
        data1 = source1.getDataSet();
        data1.setClassIndex(data1.numAttributes() - 1);

        List<Instances> chunks = new ArrayList<>();
        chunks.add(data1);

        //Datasets ok - KDD, MOA, RBF, CoverType


//        OfflinePhase offlinePhase = new OfflinePhase();
//        Ensemble ensemble = offlinePhase.inicializar("moa", current + "/moa/", 6, data1, fuzzyfication, alpha, theta, 2000, 4, 1);
//        OnlinePhase onlinePhase = new OnlinePhase();
//        ensemble.N = 2;
//        onlinePhase.initialize(current + "/moa/", "moa", ensemble, 2000, 500, 40, 4, 0.2, 200, 25);

        OfflinePhase offlinePhase = new OfflinePhase();
        Ensemble ensemble = offlinePhase.inicializar("rbf", current + "/rbf/", 6, data1, fuzzyfication, alpha, theta, 2000, 4, 0);
        OnlinePhase onlinePhase = new OnlinePhase();
        ensemble.N = 2;
        onlinePhase.initialize(current + "/rbf/", "rbf", ensemble, 2000, 500, 40, 4, 0.2, 200, 25);

//        OfflinePhase offlinePhase = new OfflinePhase();
//        Ensemble ensemble = offlinePhase.inicializar("synedc", current + "/synedc/", 6, data1, fuzzyfication, alpha, theta, 40000, 8, 5);
//        OnlinePhase onlinePhase = new OnlinePhase();
//        ensemble.N = 2;
//        onlinePhase.initialize(current + "/synedc/", "synedc", ensemble, 2000, 500, 80, 8, 0.2, 200, 25);

//        OfflinePhase offlinePhase = new OfflinePhase();
//        Ensemble ensemble = offlinePhase.inicializar("cover", current + "/cover/", 6, data1, fuzzyfication, alpha, theta, 50000, 8, 1);
//        OnlinePhase onlinePhase = new OnlinePhase();
//        ensemble.N = 2;
//        onlinePhase.initialize(current + "/cover/", "cover", ensemble, 2000, 500, 40, 8, 0.2, 1000, 20);

//        OfflinePhase offlinePhase = new OfflinePhase();
//        Ensemble ensemble = offlinePhase.inicializar("kdd", current + "/kdd/", 6, data1, fuzzyfication, alpha, theta, 48791, 8, 1);
//        OnlinePhase onlinePhase = new OnlinePhase();
//        ensemble.N = 2;
//        onlinePhase.initialize(current + "/kdd/", "kdd", ensemble, 2000, 1000, 40, 8, 0.2, 1000, 20);

    }
}



