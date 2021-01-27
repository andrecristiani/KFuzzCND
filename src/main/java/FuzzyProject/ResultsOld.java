package FuzzyProject;

import FuzzyProject.Fuzz.Models.ResultsForExample;
import FuzzyProject.Fuzz.Utils.HandlesFiles;
import FuzzyProject.Fuzz.Utils.LineChart_AWT;
import org.jfree.ui.RefineryUtilities;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class ResultsOld {
    public static void main(String[] args) throws IOException, ParseException {
//        String dataset = "cover";
        String caminho = "";
//        String current = (new File(".")).getCanonicalPath();
//        caminho = current + "/" + dataset + "/";
        caminho = "/home/andre/Desktop/ResultadosFinais/";
        //rbf
        String dataset = "rbf";
        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 46586);
        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 46586);
        //cover
//        String dataset = "synedc";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 370000);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 370000);
        //kdd
//        String dataset = "kdd";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 445230);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 445230);
        //moa
//        String dataset = "moa";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 90000);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 90000);
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

        for(int i=0, j = 1; i<resultsFuzzCND.size(); j++, i++) {
            if(excFuzzCND.containsKey(resultsFuzzCND.get(i).getRealClass())) {
                excFuzzCND.replace(resultsFuzzCND.get(i).getRealClass(), excFuzzCND.get(resultsFuzzCND.get(i).getRealClass()) + 1);
            } else {
                excFuzzCND.put(resultsFuzzCND.get(i).getRealClass(), 1);
            }

            if(resultsFuzzCND.get(i).getClassifiedClass().equals("unknown")) {
                unknown++;
                if(unkRiFuzzCND.containsKey(resultsFuzzCND.get(i).getRealClass())) {
                    unkRiFuzzCND.replace(resultsFuzzCND.get(i).getRealClass(), unkRiFuzzCND.get(resultsFuzzCND.get(i).getRealClass()) + 1);
                } else {
                    unkRiFuzzCND.put(resultsFuzzCND.get(i).getRealClass(),1);
                }
            } else {
                if(Double.parseDouble(resultsFuzzCND.get(i).getClassifiedClass()) > 100) {
                    novelty++;
                } else {
                    count++;
                    if (resultsFuzzCND.get(i).getClassifiedClass().equals(resultsFuzzCND.get(i).getRealClass())) {
                        acertos++;
                        acertosCount++;
                    } else {
                        errors++;
                    }
                }
            }
            if(j==1000) {
                acuraciasFuzzCND.add(((double)acertosCount/count) * 100);
                unkRFuzzCND.add(calculaUnkR(unkRiFuzzCND, excFuzzCND));
//                acertosCount = 0;
//                unkRiFuzzCND.clear();
//                excFuzzCND.clear();
//                count = 0;
                j=0;
            }
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
                    noveltyECSMiner++;
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
//                acertosCount = 0;
//                unkRECSMiner.clear();
//                excECSMiner.clear();
//                count = 0;
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

//        LineChart_AWT chart2 = new LineChart_AWT(
//                "" ,
//                "", metricasFuzzCND, rotulos);
//
//        chart2.pack( );
//        RefineryUtilities.centerFrameOnScreen( chart2 );
//        chart2.setVisible( true );

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