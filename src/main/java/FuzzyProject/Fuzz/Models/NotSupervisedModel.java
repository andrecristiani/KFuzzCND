package FuzzyProject.Fuzz.Models;

import FuzzyProject.Fuzz.Utils.DistanceMeasures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotSupervisedModel {
    public List<SPFMiC> spfMiCS = new ArrayList<>();

    public double classify(Example example, int N, int K) {
        List<Double> tipicidades = new ArrayList<>();
        boolean isOutlier = true;
        for(int i=0; i<this.spfMiCS.size(); i++) {
            tipicidades.add(this.spfMiCS.get(i).calculaTipicidade(example.getPonto(), N, K));
            double distancia = DistanceMeasures.calculaDistanciaEuclidiana(example, this.spfMiCS.get(i).getCentroide());
            if(distancia <= this.spfMiCS.get(i).getRadius()) {
                isOutlier = false;
            }
        }

        if(isOutlier) {
            return -1;
        }

        Double maxVal = Collections.max(tipicidades);
        int indexMax = tipicidades.indexOf(maxVal);

        return this.spfMiCS.get(indexMax).getRotulo();
    }
}
