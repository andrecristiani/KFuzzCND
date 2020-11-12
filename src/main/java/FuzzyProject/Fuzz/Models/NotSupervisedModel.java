package FuzzyProject.Fuzz.Models;

import FuzzyProject.Fuzz.Utils.DistanceMeasures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotSupervisedModel {
    public List<SPFMiC> spfMiCS = new ArrayList<>();

    public double classify(Example example, int N, int K, int updated) {
        List<Double> tipicidades = new ArrayList<>();
        List<SPFMiC> auxSPFMiCs = new ArrayList<>();
        boolean isOutlier = true;
        for(int i=0; i<this.spfMiCS.size(); i++) {
            double distancia = DistanceMeasures.calculaDistanciaEuclidiana(example, this.spfMiCS.get(i).getCentroide());
            if(distancia <= this.spfMiCS.get(i).getRadius()) {
                isOutlier = false;
                tipicidades.add(this.spfMiCS.get(i).calculaTipicidade(example.getPonto(), N, K));
                auxSPFMiCs.add(this.spfMiCS.get(i));
            }
        }

        if(isOutlier) {
            return -1;
        }

        Double maxVal = Collections.max(tipicidades);
        int indexMax = tipicidades.indexOf(maxVal);

        SPFMiC spfmic = auxSPFMiCs.get(indexMax);
        int index = this.spfMiCS.indexOf(spfmic);

        this.spfMiCS.get(index).setUpdated(updated);
        return this.spfMiCS.get(index).getRotulo();
    }

    public void removeOldSPFMiCs(int ts, int currentTime) {
        List<SPFMiC> spfMiCSAux = new ArrayList<>();
        for(int i=0; i<spfMiCS.size(); i++) {
            if(currentTime - spfMiCS.get(i).getT() > ts) {
                spfMiCSAux.remove(spfMiCS.get(i));
            }
        }
        this.spfMiCS = spfMiCSAux;
    }
}
