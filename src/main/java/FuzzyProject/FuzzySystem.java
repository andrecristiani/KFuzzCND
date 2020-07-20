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
        String dataset = "unsw";
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

        //MOA
//        FaseOffline faseOffline = new FaseOffline();
//        ComiteArvores comite = faseOffline.inicializar("moa", caminho, 6, chunks);
//        FaseOnline faseOnline = new FaseOnline();
//        faseOnline.inicializarFuzzyCMeans(current + "/moa/", "moa", comite, 2000, 2000);

        //rbf
//        FaseOffline faseOffline = new FaseOffline();
//        ComiteArvores comite = faseOffline.inicializar("rbf", current + "/rbf/", 6, chunks);
//        FaseOnline faseOnline = new FaseOnline();
//        faseOnline.inicializarFuzzyCMeans(current + "/rbf/", "rbf", comite, 2000, 2000);

//        //forest
//        FaseOffline faseOffline = new FaseOffline();
//        ComiteArvores comite = faseOffline.inicializar("forest", current + "/forest/", 6, chunks);
//        FaseOnline faseOnline = new FaseOnline();
//        faseOnline.inicializarFuzzyCMeans(current + "/forest/", "forest", comite, 2000, 2000);

        //kdd
//        FaseOffline faseOffline = new FaseOffline();
//        ComiteArvores comite = faseOffline.inicializar("kdd", current + "/kdd/", 6, chunks);
//        FaseOnline faseOnline = new FaseOnline();
//        faseOnline.inicializarFuzzyCMeans(current + "/kdd/", "kdd", comite, 2000, 2000);

//        FaseOffline faseOffline = new FaseOffline();
//        ComiteArvores comite = faseOffline.inicializar("elec", current + "/elec/", 6, chunks);
//        FaseOnline faseOnline = new FaseOnline();
//        faseOnline.inicializarFuzzyCMeans(current + "/elec/", "elec", comite, 48, 720);

//        OfflinePhase offlinePhase = new OfflinePhase();
//        Ensemble ensemble = offlinePhase.inicializar(dataset, current + "/"+dataset+"/", 6, data1, fuzzyfication, alpha, theta, 365, 4);
//        OnlinePhase onlinePhase = new OnlinePhase();
//        ensemble.allTipMax = new MaxTipicity(0.70);
//        ensemble.thetaAdapter = 0.60;
//        ensemble.N = 2;
//        onlinePhase.initialize(current + "/"+dataset+"/", dataset, ensemble, 365, 365);

//        OfflinePhase offlinePhase = new OfflinePhase();
//        Ensemble ensemble = offlinePhase.inicializar(dataset, current + "/"+dataset+"/", 6, data1, fuzzyfication, alpha, theta, 720, 12);
//        OnlinePhase onlinePhase = new OnlinePhase();
//        ensemble.allTipMax = new MaxTipicity(0.70);
//        ensemble.thetaAdapter = 0.50;
//        ensemble.N = 2;
//        onlinePhase.initialize(current + "/"+dataset+"/", dataset, ensemble, 240, 720);

//        OfflinePhase offlinePhase = new OfflinePhase();
//        Ensemble ensemble = offlinePhase.inicializar("keystroke", current + "/keystroke/", 6, data1, fuzzyfication, alpha, theta, 365, 4);
//        OnlinePhase onlinePhase = new OnlinePhase();
//        ensemble.allTipMax = new MaxTipicity(0.70);
//        ensemble.thetaAdapter = 0.60;
//        ensemble.N = 2;
//        onlinePhase.initialize(current + "/keystroke/", "keystroke", ensemble, 365, 365);

//        OfflinePhase offlinePhase = new OfflinePhase();
//        Ensemble ensemble = offlinePhase.inicializar("moa", current + "/moa/", 6, data1, fuzzyfication, alpha, theta, 2000, 4);
//        OnlinePhase onlinePhase = new OnlinePhase();
//        ensemble.allTipMax = new MaxTipicity(0.95);
//        ensemble.thetaAdapter = 0.31;
//        ensemble.N = 2;
//        onlinePhase.initialize(current + "/moa/", "moa", ensemble, 2000, 2000, 80, 4, 0.8);

        OfflinePhase offlinePhase = new OfflinePhase();
        Ensemble ensemble = offlinePhase.inicializar(dataset, current + "/"+dataset+"/", 12, data1, fuzzyfication, alpha, theta, 152148, 32, 5);
        OnlinePhase onlinePhase = new OnlinePhase();
        ensemble.allTipMax = new MaxTipicity(0.70);
        ensemble.thetaAdapter = 0.60;
        ensemble.N = 2;
        onlinePhase.initialize(current + "/"+dataset+"/", dataset, ensemble, 2000, 15000, 40, 4, 0.5);
    }
}



