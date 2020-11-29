package FuzzyProject;

import FuzzyProject.Fuzz.Models.Example;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FCMTest {
    public static void main(String[] args) throws Exception {
        String dataset = "insect";
        String caminho = "";
        String current = (new File(".")).getCanonicalPath();
        caminho = current + "/" + dataset + "/";

        ConverterUtils.DataSource source;
        Instances data;

        source = new ConverterUtils.DataSource(caminho + dataset + "2.arff");
        data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        ArrayList<Attribute> atts = new ArrayList<>();
        for(int i=0; i<data.numAttributes(); i++) {
            atts.add(data.attribute(i));
        }

        List<Example> examples = new ArrayList<>();

        for (int i=0; i<data.size(); i++) {
           examples.add(new Example(data.get(i).toDoubleArray(), true));
        }

        FuzzyKMeansClusterer fuzzyClusterer = new FuzzyKMeansClusterer(2, 2, 1, new EuclideanDistance());
        fuzzyClusterer.cluster(examples);

        System.out.println("Processamento finalizado!");
    }
}
