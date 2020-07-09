package FuzzyProject.Fuzz.Utils;

import FuzzyProject.Fuzz.Models.Evaluation.AcuraciaMedidas;
import FuzzyProject.Fuzz.Models.Example;
import FuzzyProject.Fuzz.Models.ClassicMeasures;

import java.util.List;

public class Evaluation {

    public static ClassicMeasures calculaMedidasClassicas(int fp, int fn, int fe, int n, int nc, int i) {
        ClassicMeasures md = new ClassicMeasures();
        double mnew = ((double) fn * 100) / nc;
        double fnew = ((double) fp * 100) / (n - nc);
        double err = (((double) (fp+fn+fe) * 100) / n);
        md.setMnew(mnew);
        md.setFnew(fnew);
        md.setErr(err);
        md.setIndice(i);
        return md;
    }

    public static void  calculaMedidasFariaEtAl(List<Example> exemplos, int nPoints) {
        for(int i=0; i<exemplos.size(); i++) {

        }
    }

    public static AcuraciaMedidas calculaAcuracia(int numAcertos, int numTotalExemplos, int indice) {
        AcuraciaMedidas acuraciaMedidas = new AcuraciaMedidas(indice, (Double.parseDouble(String.valueOf(numAcertos))/numTotalExemplos)*100);
        return acuraciaMedidas;
    }
}
