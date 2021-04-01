package FuzzyProject;

import FuzzyProject.Fuzz.Models.ResultsForExample;
import FuzzyProject.Fuzz.Utils.HandlesFiles;
import FuzzyProject.Fuzz.Utils.LineChart_AWT;
import org.jfree.ui.RefineryUtilities;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class ResultsExtreme {
    public static void main(String[] args) throws IOException, ParseException {
//        String dataset = "cover";
        String caminho = "";
//        String current = (new File(".")).getCanonicalPath();
//        caminho = current + "/" + dataset + "/";
        caminho = "/home/andre/Desktop/ResultadosFinais/";
        //rbf
//        String dataset = "rbf";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 46586);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 46586);
        //cover
//        String dataset = "synedc";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 370000);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 370000);
        //kdd
//        String dataset = "kdd";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 445230);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 445230);
        //moa
        String dataset = "moa";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 90000);
        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 90000);
        int unknown = 0;
        int acertos = 0;
        int acertosCount = 0;
        int count = 0;
        int errors = 0;
        int novelty = 0;

        int unknownECSMiner = 0;
        int acertosECSMiner = 0;
        int errorsECSMiner = 0;
        int noveltyECSMiner = 0;

        ArrayList<Double> acuraciasFuzzCND = new ArrayList<>();
        ArrayList<Double> acuraciasECSMiner = new ArrayList<>();
        Map<String, Integer> unkRiFuzzCND = new HashMap<>();
        Map<String, Integer> excFuzzCND = new HashMap<>();
        Map<String, Integer> unkRiECSMiner = new HashMap<>();
        Map<String, Integer> excECSMiner = new HashMap<>();
        ArrayList<Double> unkRFuzzCND = new ArrayList<>();
        ArrayList<Double> unkRECSMiner = new ArrayList<>();

        ArrayList<List<Double>> metricasFuzzCND = new ArrayList<>();
        ArrayList<List<Double>> metricasECSMiner = new ArrayList<>();
        Map<String, Map<String, Integer>> map = new HashMap<>();

        for(int i=0, j=1; i<resultsECSMiner.size(); j++, i++) {
            if(!resultsECSMiner.get(i).getClassifiedClass().equals("unknown")) {
                if (Double.parseDouble(resultsECSMiner.get(i).getClassifiedClass()) > 100) {
                    if (!map.containsKey(resultsECSMiner.get(i).getClassifiedClass())) {
                        Map aux = new HashMap();
                        aux.put(resultsECSMiner.get(i).getRealClass(), 1);
                        map.put(resultsECSMiner.get(i).getClassifiedClass(), aux);
                    } else {
                        if (map.get(resultsECSMiner.get(i).getClassifiedClass()).containsKey(resultsECSMiner.get(i).getRealClass())) {
                            map.get(resultsECSMiner.get(i).getClassifiedClass()).replace(resultsECSMiner.get(i).getRealClass(), map.get(resultsECSMiner.get(i).getClassifiedClass()).get(resultsECSMiner.get(i).getRealClass()) + 1);
                        } else {
                            map.get(resultsECSMiner.get(i).getClassifiedClass()).put(resultsECSMiner.get(i).getRealClass(), 1);
                        }
                    }
                }
            }
        }

        List<String> keys = new ArrayList<>();
        keys.addAll(map.keySet());
        Map<String, String> rotulosNovidades = new HashMap<>();
        for(int i=0; i<keys.size(); i++) {
            List<String> keysAux = new ArrayList<>();
            keysAux.addAll(map.get(keys.get(i)).keySet());
            int maiorQuantidade = 0;
            String rotuloMaior = new String();
            for(int j=0; j<keysAux.size(); j++) {
                if(map.get(keys.get(i)).get(keysAux.get(j)) > maiorQuantidade) {
                    maiorQuantidade = map.get(keys.get(i)).get(keysAux.get(j));
                    rotuloMaior = keysAux.get(j);
                }
            }
            rotulosNovidades.put(keys.get(i), rotuloMaior);
        }

        for(int i=0, j=1; i<resultsECSMiner.size(); j++, i++) {
            if(excECSMiner.containsKey(resultsECSMiner.get(i).getRealClass())) {
                excECSMiner.replace(resultsECSMiner.get(i).getRealClass(), excECSMiner.get(resultsECSMiner.get(i).getRealClass()) + 1);
            } else {
                excECSMiner.put(resultsECSMiner.get(i).getRealClass(), 1);
            }

            if(resultsECSMiner.get(i).getClassifiedClass().equals("unknown")) {
                unknownECSMiner++;
                if(unkRiECSMiner.containsKey(resultsECSMiner.get(i).getRealClass())) {
                    unkRiECSMiner.replace(resultsECSMiner.get(i).getRealClass(), unkRiECSMiner.get(resultsECSMiner.get(i).getRealClass()) + 1);
                } else {
                    unkRiECSMiner.put(resultsECSMiner.get(i).getRealClass(),1);
                }
            } else {
                if(Double.parseDouble(resultsECSMiner.get(i).getClassifiedClass()) > 100) {
                    if(rotulosNovidades.get(resultsECSMiner.get(i).getClassifiedClass()).equals(resultsECSMiner.get(i).getRealClass())) {
                        acertosECSMiner++;
                        acertosCount++;
                        count++;
                    }
                } else {
                    count++;
                    if (resultsECSMiner.get(i).getClassifiedClass().equals(resultsECSMiner.get(i).getRealClass())) {
                        acertosECSMiner++;
                        acertosCount++;
                    } else {
                        errorsECSMiner++;
                    }
                }
            }

            if(j==1000) {
                acuraciasECSMiner.add(((double)acertosCount/count) * 100);
                unkRECSMiner.add(calculaUnkR(unkRiECSMiner, excECSMiner));
                j=0;
            }
        }

        List<String> rotulos = new ArrayList<>();
        rotulos.add("Accuracy");
        rotulos.add("UnkR");

        metricasFuzzCND.add(acuraciasFuzzCND);
        metricasFuzzCND.add(unkRFuzzCND);

        metricasECSMiner.add(acuraciasECSMiner);
        metricasECSMiner.add(unkRECSMiner);

        LineChart_AWT chart = new LineChart_AWT(
                "" ,
                "", metricasECSMiner, rotulos);

        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );

        System.out.println("Acertos: " + acertos);
        System.out.println("Erros: " + errors);
        System.out.println("Desconhecidos: " + unknown);
        System.out.println("Novidades: " + novelty);

        System.out.println("Acertos: " + acertosECSMiner);
        System.out.println("Erros: " + errorsECSMiner);
        System.out.println("Desconhecidos: " + unknownECSMiner);
        System.out.println("Novidades: " + noveltyECSMiner);
    }

    public static double calculaUnkR(Map<String, Integer> unki, Map<String, Integer> exci) {
        List<String> rotulos = new ArrayList<>();
        rotulos.addAll(unki.keySet());
        double unkR = 0;
        for(int i=0; i< unki.size(); i++) {
            double unk = unki.get(rotulos.get(i));
            double exc = exci.get(rotulos.get(i));
            unkR += (unk/exc);
        }
        return (unkR/ exci.size()) * 100;
    }
}