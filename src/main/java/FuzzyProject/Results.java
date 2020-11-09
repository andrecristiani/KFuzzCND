package FuzzyProject;

import FuzzyProject.Fuzz.Models.ResultsForExample;
import FuzzyProject.Fuzz.Utils.HandlesFiles;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

public class Results {
    public static void main(String[] args) throws IOException {
        String dataset = "moa";
        String caminho = "";
        String current = (new File(".")).getCanonicalPath();
        caminho = current + "/" + dataset + "/";
//        List<ResultsForExample> results = HandlesFiles.loadResults(caminho, dataset, 46586);
//        List<ResultsForExample> results = HandlesFiles.loadResults(caminho, dataset, 48848);
//        List<ResultsForExample> results = HandlesFiles.loadResults(caminho, dataset, 531012);
        List<ResultsForExample> results = HandlesFiles.loadResults(caminho, dataset, 90000);
        int unknown = 0;
        int acertos = 0;
        int errors = 0;
        int novelty = 0;
        Hashtable<String, Hashtable<String, Integer>> confusionMatrix = new Hashtable();
        for(int i=0; i<results.size(); i++) {
            if(results.get(i).getClassifiedClass().equals("unknown")) {
                unknown++;
            } else {
                if(Double.parseDouble(results.get(i).getClassifiedClass()) > 100) {
                    novelty++;
                }
                if(results.get(i).getClassifiedClass().equals(results.get(i).getRealClass())) {
                    acertos++;
                } else {
                    errors++;
                }
            }
        }

        System.out.println("Acertos: " + acertos);
        System.out.println("Erros: " + errors);
        System.out.println("Desconhecidos: " + unknown);
        System.out.println("Novidades: " + novelty);
    }
}
