//package FuzzyProject;
//
//import FuzzyProject.FuzzyND.Models.Avaliacao.AcuraciaMedidas;
//import FuzzyProject.FuzzyND.Utils.LineChart_AWT;
//import org.jfree.ui.RefineryUtilities;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class GerarGraficosAcuracia {
//    public static void main(String[] args) throws IOException {
//        String dataset = "moa";
//        String current = (new File(".")).getCanonicalPath();
//        String caminho = current + "/datasets-j48/" + dataset + "/moa-meu-predctions.txt";
//        String caminho2 = current + "/datasets-j48/" + dataset + "/moa-vfdt-predctions.txt";
//        String caminho3 = current + "/datasets-j48/" + dataset + "/moa-j48-predctions.txt";
//
//        List<List<AcuraciaMedidas>> acuraciasDosClassificadores = new ArrayList<>();
//        List<String> rotuloAlgoritmo = new ArrayList<>();
//
//        List<AcuraciaMedidas> acuraciaMeu = ManipulaArquivos.carregaAcuracias(caminho, 90);
//        acuraciasDosClassificadores.add(acuraciaMeu);
//        rotuloAlgoritmo.add("Meu");
//
//        List<AcuraciaMedidas> acuraciaVFDT = ManipulaArquivos.carregaAcuracias(caminho2, 90);
//        acuraciasDosClassificadores.add(acuraciaVFDT);
//        rotuloAlgoritmo.add("Hoeffding Adaptive Tree");
//
//        List<AcuraciaMedidas> acuraciaJ48 = ManipulaArquivos.carregaAcuracias(caminho3, 90);
//        acuraciasDosClassificadores.add(acuraciaJ48);
//        rotuloAlgoritmo.add("C4.5");
//
//        LineChart_AWT chart = new LineChart_AWT(
//        "" ,
//        "", acuraciasDosClassificadores, rotuloAlgoritmo);
//
//        chart.pack( );
//        RefineryUtilities.centerFrameOnScreen( chart );
//        chart.setVisible( true );
//    }
//}
