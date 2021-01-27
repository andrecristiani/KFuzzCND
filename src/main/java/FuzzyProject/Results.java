package FuzzyProject;

import FuzzyProject.Fuzz.Models.ResultsForExample;
import FuzzyProject.Fuzz.Utils.HandlesFiles;
import FuzzyProject.Fuzz.Utils.LineChart_AWT;
import org.jfree.ui.RefineryUtilities;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Results {
    public static void main(String[] args) throws IOException, ParseException {
//        String dataset = "cover";
        String caminho = "";
//        String current = (new File(".")).getCanonicalPath();
//        caminho = current + "/" + dataset + "/";
        caminho = "/home/andre/Desktop/ResultadosFinais/";
        Map<Integer, List<ResultsForExample>> resultsFuzzCND = new HashMap<>();
        Map<Integer, List<ResultsForExample>> resultsECSMiner = new HashMap<>();
        //rbf
//        String dataset = "rbf";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 46586);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 46586);
//        for(int i=0; i<5; i++) {
//            resultsFuzzCND.put(i,HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 46586, i));
////            resultsECSMiner.put(i, HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 90000, i));
//        }


        //synedc
//        String dataset = "synedc";
//        for(int i=0; i<5; i++) {
//            resultsFuzzCND.put(i,HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 370000, i));
//        }
        //cover
//        String dataset = "cover";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 531012);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 531012);
        //kdd
//        String dataset = "kdd";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 445230);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 445230);
        //moa
        String dataset = "moa";
//        List<ResultsForExample> resultsFuzzCND = HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 90000);
//        List<ResultsForExample> resultsECSMiner = HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 90000);

        for(int i=0; i<5; i++) {
            resultsFuzzCND.put(i,HandlesFiles.loadResults(caminho, dataset, "FuzzCND", 90000, i));
//            resultsECSMiner.put(i, HandlesFiles.loadResults(caminho, dataset, "ECSMiner", 90000, i));
        }

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

//        ArrayList<Double> acuraciasFuzzCND = new ArrayList<>();
        ArrayList<Double> acuraciasECSMiner = new ArrayList<>();
        Map<String, Integer> unkRiFuzzCND = new HashMap<>();
        Map<String, Integer> excFuzzCND = new HashMap<>();
        Map<String, Integer> unkRiECSMiner = new HashMap<>();
        Map<String, Integer> excECSMiner = new HashMap<>();
//        ArrayList<Double> unkRFuzzCND = new ArrayList<>();
        ArrayList<Double> unkRECSMiner = new ArrayList<>();

        Map<Integer, ArrayList<Double>> medidasAcuracias = new HashMap<>();
        Map<Integer, ArrayList<Double>> medidasUnkR = new HashMap<>();

        ArrayList<List<Double>> metricasFuzzCND = new ArrayList<>();
        ArrayList<List<Double>> metricasECSMiner = new ArrayList<>();

        for(int i=0; i<resultsFuzzCND.size(); i++) {
            List<ResultsForExample> results = resultsFuzzCND.get(i);
            ArrayList<Double> acuraciasFuzzCND = new ArrayList<>();
            ArrayList<Double> unkRFuzzCND = new ArrayList<>();
            for(int k=0, j=1; k<results.size(); j++, k++) {
                if (excFuzzCND.containsKey(results.get(k).getRealClass())) {
                    excFuzzCND.replace(results.get(k).getRealClass(), excFuzzCND.get(results.get(k).getRealClass()) + 1);
                } else {
                    excFuzzCND.put(results.get(k).getRealClass(), 1);
                }

                if (results.get(k).getClassifiedClass().equals("unknown")) {
                    unknown++;
                    if (unkRiFuzzCND.containsKey(results.get(k).getRealClass())) {
                        unkRiFuzzCND.replace(results.get(k).getRealClass(), unkRiFuzzCND.get(results.get(k).getRealClass()) + 1);
                    } else {
                        unkRiFuzzCND.put(results.get(k).getRealClass(), 1);
                    }
                } else {
                    if (Double.parseDouble(results.get(k).getClassifiedClass()) > 100) {
                        novelty++;
                    } else {
                        count++;
                        if (results.get(k).getClassifiedClass().equals(results.get(k).getRealClass())) {
                            acertos++;
                            acertosCount++;
                        } else {
                            errors++;
                        }
                    }
                }
                if (j == 1000) {
                    acuraciasFuzzCND.add(((double) acertosCount / count) * 100);
                    unkRFuzzCND.add(calculaUnkR(unkRiFuzzCND, excFuzzCND));
//                acertosCount = 0;
//                unkRiFuzzCND.clear();
//                excFuzzCND.clear();
//                count = 0;
                    j = 0;
                }
            }
            acertosCount = 0;
            unkRiFuzzCND.clear();
            excFuzzCND.clear();
            count = 0;
            medidasAcuracias.put(i, acuraciasFuzzCND);
            medidasUnkR.put(i, unkRFuzzCND);
        }

        ArrayList<Double> acuraciasFinal = new ArrayList<>();
        ArrayList<Double> unkRFinal = new ArrayList<>();

        for(int i=0; i<medidasAcuracias.get(0).size(); i++) {
            double somaAc = 0;
            double somaUnk = 0;
            for(int j=0; j<medidasAcuracias.size(); j++) {
                somaAc = somaAc + medidasAcuracias.get(j).get(i);
                somaUnk = somaUnk + medidasUnkR.get(j).get(i);
            }
            acuraciasFinal.add(somaAc/medidasAcuracias.size());
            unkRFinal.add(somaUnk/medidasAcuracias.size());
        }

//        count = 0;
//
//        for(int i=0, j=1; i<resultsECSMiner.size(); j++, i++) {
//            if(excECSMiner.containsKey(resultsECSMiner.get(i).getRealClass())) {
//                excECSMiner.replace(resultsECSMiner.get(i).getRealClass(), excECSMiner.get(resultsECSMiner.get(i).getRealClass()) + 1);
//            } else {
//                excECSMiner.put(resultsECSMiner.get(i).getRealClass(), 1);
//            }
//
//            if(resultsECSMiner.get(i).getClassifiedClass().equals("unknown")) {
//                unknownECSMiner++;
//                if(unkRiECSMiner.containsKey(resultsECSMiner.get(i).getRealClass())) {
//                    unkRiECSMiner.replace(resultsECSMiner.get(i).getRealClass(), unkRiECSMiner.get(resultsECSMiner.get(i).getRealClass()) + 1);
//                } else {
//                    unkRiECSMiner.put(resultsECSMiner.get(i).getRealClass(),1);
//                }
//            } else {
//                if(Double.parseDouble(resultsECSMiner.get(i).getClassifiedClass()) > 100) {
//                    noveltyECSMiner++;
//                } else {
//                    count++;
//                    if (resultsECSMiner.get(i).getClassifiedClass().equals(resultsECSMiner.get(i).getRealClass())) {
//                        acertosECSMiner++;
//                    } else {
//                        errorsECSMiner++;
//                    }
//                }
//            }
//
//            if(j==1000) {
//                acuraciasECSMiner.add(((double)acertosECSMiner/count) * 100);
//                unkRECSMiner.add(calculaUnkR(unkRiECSMiner, excECSMiner));
////                acertosCount = 0;
////                unkRECSMiner.clear();
////                excECSMiner.clear();
////                count = 0;
//                j=0;
//            }
//        }

        List<String> rotulos = new ArrayList<>();
        rotulos.add("Accuracy");
        rotulos.add("UnkR");

        metricasFuzzCND.add(acuraciasFinal);
        metricasFuzzCND.add(unkRFinal);

        metricasECSMiner.add(acuraciasECSMiner);
        metricasECSMiner.add(unkRECSMiner);

        LineChart_AWT chart2 = new LineChart_AWT(
                "" ,
                "", metricasFuzzCND, rotulos);

        chart2.pack( );
        RefineryUtilities.centerFrameOnScreen( chart2 );
        chart2.setVisible( true );

//        LineChart_AWT chart = new LineChart_AWT(
//                "" ,
//                "ECSMiner", metricasECSMiner, rotulos);
//
//        chart.pack( );
//        RefineryUtilities.centerFrameOnScreen( chart );
//        chart.setVisible( true );

        System.out.println("Acertos: " + acertos);
        System.out.println("Erros: " + errors);
        System.out.println("Desconhecidos: " + unknown);
        System.out.println("Novidades: " + novelty);

//        System.out.println("Acertos: " + acertosECSMiner);
//        System.out.println("Erros: " + errorsECSMiner);
//        System.out.println("Desconhecidos: " + unknownECSMiner);
//        System.out.println("Novidades: " + noveltyECSMiner);
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
