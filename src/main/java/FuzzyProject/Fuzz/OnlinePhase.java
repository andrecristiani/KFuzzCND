package FuzzyProject.Fuzz;

import FuzzyProject.Fuzz.Models.Ensemble;
import FuzzyProject.Fuzz.Models.*;
import FuzzyProject.Fuzz.Models.Evaluation.AcuraciaMedidas;
import FuzzyProject.Fuzz.Utils.DistanceMeasures;
import FuzzyProject.Fuzz.Utils.FuzzyFunctions;
import FuzzyProject.Fuzz.Utils.HandlesFiles;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OnlinePhase {

    public List<Example> exemplosEsperandoTempo = new ArrayList<>();
    public List<ClassicMeasures> desempenho = new ArrayList<>();
    double nPCount = 100;
    double phi = 0;
    int erroSeparado = 0;
    int erroNs = 0;
    Ensemble ensemble;
    NotSupervisedModel nsModel;

    List<Example> results = new ArrayList<>();

    public void initialize(String caminho, String dataset, Ensemble comite, int latencia, int tChunk, int T, int kShort, double phi, int ts, int minWeight) {

        List<AcuraciaMedidas> acuracias = new ArrayList<>();
        this.ensemble = comite;
        this.phi = phi;
        nsModel = new NotSupervisedModel();
        DataSource source;
        Instances data;
        int acertos = 0;
        Instances esperandoTempo;
        int nExeTemp = 0;
        try {
            source = new DataSource(caminho + dataset + "-instances.arff");
            data = source.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            ArrayList<Attribute> atts = new ArrayList<>();
            for(int i=0; i<data.numAttributes(); i++) {
                atts.add(data.attribute(i));
            }
            esperandoTempo = data;
            List<Example> labeledMem = new ArrayList<>();
            List<Example> unkMem = new ArrayList<>();

            int desconhecido = 0;
            for(int i=0, j=0, h=0; i<data.size(); i++, j++, h++) {
                Instance ins = data.get(i);
                Example exemplo = new Example(ins.toDoubleArray(), true, i);
                double rotulo = comite.classifyNew(ins, i);
                exemplo.setRotuloClassificado(rotulo);
                if(rotulo == exemplo.getRotuloVerdadeiro()) {
                    acertos++;
                } else if (rotulo == -1) {
                    rotulo = nsModel.classify(exemplo, ensemble.N, ensemble.K, i);
                    exemplo.setRotuloClassificado(rotulo);
                    if(rotulo == -1) {
                        desconhecido++;
                        unkMem.add(exemplo);
                        if (unkMem.size() >= T) {
                            unkMem = this.multiClassNoveltyDetection(unkMem, kShort, phi, minWeight, i);
                        }
                    } else {
                        if(rotulo < 100 && rotulo != exemplo.getRotuloVerdadeiro()) {
                            erroNs++;
                            rotulo = nsModel.classify(exemplo, ensemble.N, ensemble.K, i);
                        }
                    }
                } else {
                    erroSeparado++;
                }
                results.add(exemplo);
                this.exemplosEsperandoTempo.add(exemplo);
                if(j >= latencia) {
                    Example labeledExample = new Example(esperandoTempo.get(nExeTemp).toDoubleArray(), true, i);
                    labeledMem.add(labeledExample);
                    if(labeledMem.size() >= tChunk) {
                        if(nsModel.spfMiCS.size() > 0) {
                            this.results = this.verifyIfExistNewClassInNSModel(labeledMem, this.results, i);
                        }
                        //TODO: pensar em uma estratégia para remover SPFMiCs que não são removidos automaticamente do NSModel
                        //TODO: calcular a tipicidade apenas para SPFMiCs que o exemplo está dentro do raio
                        nsModel.removeOldSPFMiCs(latencia + (ts*2), i);
                        ensemble.removeOldSPFMiCs(latencia*2, i);
                        System.out.println("");
                        labeledMem = comite.trainNewClassifier(labeledMem, i);
                    }
                    nExeTemp++;
                }

                this.removeOldUnknown(unkMem, ts, i);

                if(h == 1000) {
                    System.out.println(nsModel.spfMiCS.size());
                    System.out.println("Ponto: " + i);
                    System.out.println("Acertos: " + acertos);
                    System.out.println("Erros separado: " + erroSeparado);
                    System.out.println("ErrosNS: " + erroNs);
                    System.out.println("Desconhecidos:" + desconhecido);
                    h=0;
                }
            }
            HandlesFiles.salvaPredicoes(acuracias, dataset);

            System.out.println("Erros separado: " + erroSeparado);
            System.out.println("Sem classificar: " + unkMem.size());
            System.out.println("NSModel size: " + nsModel.spfMiCS.size());
            HandlesFiles.salvaResultados(results, dataset);

        } catch (Exception ex) {
            System.out.println(ex);
            System.out.println(ex.getStackTrace());
        }
    }

    private List<Example> verifyIfExistNewClassInNSModel(List<Example> labeledMem, List<Example> results, int t) {
        List<Double> frs = new ArrayList<>();
        Map<Double, List<Example>> examplesByClass = FuzzyFunctions.separateByClasses(labeledMem);
        List<Double> classes = new ArrayList<>();
        Map<Double, List<SPFMiC>> classifier = new HashMap<>();
        classes.addAll(examplesByClass.keySet());
        List<SPFMiC> spfmics = null;
        for(int j=0; j<examplesByClass.size(); j++) {
            if(examplesByClass.get(classes.get(j)).size() > this.ensemble.K) {
                FuzzyKMeansClusterer clusters = FuzzyFunctions.fuzzyCMeans(examplesByClass.get(classes.get(j)), this.ensemble.K, this.ensemble.fuzzification);
                spfmics = FuzzyFunctions.separateExamplesByClusterClassifiedByFuzzyCMeans(examplesByClass.get(classes.get(j)), clusters, classes.get(j), this.ensemble.alpha, this.ensemble.theta, this.ensemble.minWeight, t);
                classifier.put(classes.get(j), spfmics);
            }
        }

        for(int i=0; i<spfmics.size(); i++) {
            if(nsModel.spfMiCS.size() > 0) {
                if (!spfmics.get(i).isNull()) {
                    frs.clear();
                    double dist2 = Double.MAX_VALUE;
                    for (int j = 0; j < nsModel.spfMiCS.size(); j++) {
                        double dist3 = DistanceMeasures.calculaDistanciaEuclidiana(spfmics.get(i).getCentroide(), nsModel.spfMiCS.get(j).getCentroide());
                        if (dist3 < dist2) {
                            dist2 = dist3;
                        }

                        double di = nsModel.spfMiCS.get(j).getRadius();
                        double dj = spfmics.get(i).getRadius();
                        double dist = (di + dj) / DistanceMeasures.calculaDistanciaEuclidiana(nsModel.spfMiCS.get(j).getCentroide(), spfmics.get(i).getCentroide());
                        frs.add((di + dj) / dist);
                    }

                    Double minFr = Collections.min(frs);
                    int indexMinFr = frs.indexOf(minFr);
                    if (minFr <= this.phi) {
                        System.err.println("Deu um spfmic");
                        if(spfmics.get(i).getRotulo() != nsModel.spfMiCS.get(indexMinFr).getRotuloReal()) {
                            System.err.println("Rotulos não batem");
                        }
                        for(int h=0; h<results.size(); h++) {
                            if(results.get(h).getRotuloClassificado() > 100) {
                                if (results.get(h).getRotuloClassificado() == nsModel.spfMiCS.get(indexMinFr).getRotulo()) {
                                    results.get(h).setRotuloClassificado(spfmics.get(i).getRotulo());
                                }
                            }
                        }

                        List<SPFMiC> aux = new ArrayList<>();
                        for(int j=0; j<nsModel.spfMiCS.size(); j++) {
                            if(!(nsModel.spfMiCS.get(indexMinFr).getRotulo() == nsModel.spfMiCS.get(j).getRotulo()
                                    && frs.get(j) < this.phi)) {
                                aux.add(nsModel.spfMiCS.get(j));
                            }
                        }

                        nsModel.spfMiCS = aux;
                    }
                }
            }
        }
        return results;
    }

    private List<Example> multiClassNoveltyDetection(List<Example> listaDesconhecidos, int kCurto, double phi, int minWeight, int t) {
        FuzzyKMeansClusterer clusters = FuzzyFunctions.fuzzyCMeans(listaDesconhecidos, kCurto, this.ensemble.fuzzification);
        List<CentroidCluster> centroides = clusters.getClusters();
        List<Double> silhuetas = FuzzyFunctions.fuzzySilhouette(clusters, listaDesconhecidos, this.ensemble.alpha);
        List<Integer> silhuetasValidas = new ArrayList<>();

        for(int i=0; i<silhuetas.size(); i++) {
            if(silhuetas.get(i) > 0 && centroides.get(i).getPoints().size() >= minWeight) {
                silhuetasValidas.add(i);
            }
        }

        List<SPFMiC> sfMiCS = FuzzyFunctions.newSeparateExamplesByClusterClassifiedByFuzzyCMeans(listaDesconhecidos, clusters, -1, this.ensemble.alpha, this.ensemble.theta, minWeight, t);
        List<SPFMiC> sfmicsConhecidos = ensemble.getAllSPFMiCs();
        List<Double> frs = new ArrayList<>();

        for(int i=0; i<centroides.size(); i++) {
            if(silhuetasValidas.contains(i) && !sfMiCS.get(i).isNull()) {
                frs.clear();
                for (int j = 0; j < sfmicsConhecidos.size(); j++) {
                    double di = sfmicsConhecidos.get(j).getRadius();
                    double dj = sfMiCS.get(i).getRadius();
                    double dist = (di + dj) / DistanceMeasures.calculaDistanciaEuclidiana(sfmicsConhecidos.get(j).getCentroide(), sfMiCS.get(i).getCentroide());
                    frs.add((di + dj) / dist);
                }

                Double minFr = Collections.min(frs);
                int indexMinFr = frs.indexOf(minFr);
                if (minFr <= phi) {
                    sfMiCS.get(i).setRotulo(sfmicsConhecidos.get(indexMinFr).getRotulo());
                    List<Example> examples = centroides.get(i).getPoints();
                    HashMap<Double, Integer> rotulos = new HashMap<>();
                    for(int j=0; j<examples.size(); j++) {
                        listaDesconhecidos.remove(examples.get(j));
                        if(rotulos.containsKey(examples.get(j).getRotuloVerdadeiro())) {
                            rotulos.put(examples.get(j).getRotuloVerdadeiro(), rotulos.get(examples.get(j).getRotuloVerdadeiro()) + 1);
                        } else {
                            rotulos.put(examples.get(j).getRotuloVerdadeiro(), 1);
                        }
                    }

                    Double[] keys = rotulos.keySet().toArray(new Double[0]);
                    double maiorValor = Double.MIN_VALUE;
                    double maiorRotulo = -1;
                    for(int k=0; k<rotulos.size(); k++) {
                        if(maiorValor < rotulos.get(keys[k])) {
                            maiorValor = rotulos.get(keys[k]);
                            maiorRotulo = keys[k];
                        }
                    }
                    if(maiorRotulo != sfMiCS.get(i).getRotulo()) {
                        System.err.println("Rotulo Diferente");
                    }
                    sfMiCS.get(i).setRotuloReal(maiorRotulo);
                    nsModel.spfMiCS.add(sfMiCS.get(i));
                } else {
                    sfMiCS.get(i).setRotulo(this.generateNPLabel());
                    List<Example> examples = centroides.get(i).getPoints();
                    HashMap<Double, Integer> rotulos = new HashMap<>();
                    for(int j=0; j<examples.size(); j++) {
                        listaDesconhecidos.remove(examples.get(j));
                        if(rotulos.containsKey(examples.get(j).getRotuloVerdadeiro())) {
                            rotulos.put(examples.get(j).getRotuloVerdadeiro(), rotulos.get(examples.get(j).getRotuloVerdadeiro()) + 1);
                        } else {
                            rotulos.put(examples.get(j).getRotuloVerdadeiro(), 1);
                        }
                    }

                    Double[] keys = rotulos.keySet().toArray(new Double[0]);
                    double maiorValor = Double.MIN_VALUE;
                    double maiorRotulo = -1;
                    for(int k=0; k<rotulos.size(); k++) {
                        if(maiorValor < rotulos.get(keys[k])) {
                            maiorValor = rotulos.get(keys[k]);
                            maiorRotulo = keys[k];
                        }
                    }

                    sfMiCS.get(i).setRotuloReal(maiorRotulo);
                    nsModel.spfMiCS.add(sfMiCS.get(i));
                }
            }
        }
        return listaDesconhecidos;
    }

    private double generateNPLabel() {
        nPCount++;
        return nPCount;
    }

    private List<Example> removeOldUnknown(List<Example> unkMem, int ts, int ct) {
        List<Example> newUnkMem = new ArrayList<>();
        for(int i=0; i<unkMem.size(); i++) {
            if(ct - unkMem.get(i).getTime() <= ts) {
                newUnkMem.add(unkMem.get(i));
            }
        }
        return newUnkMem;
    }
}
