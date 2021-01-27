package FuzzyProject.Fuzz.Utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

public class LineChart_AWT extends ApplicationFrame {

    public LineChart_AWT(String applicationTitle, List<List<Double>> medidasClassicas, String campo) {
        super(applicationTitle);
        XYDataset dataset;
        String label = "";
        dataset = createDatasetMnew(medidasClassicas);
        label = "Mnew";
        JFreeChart lineChart = ChartFactory.createXYLineChart(
            "",
            "Momentos de avaliação (em milhares)",label,
            dataset,
            PlotOrientation.VERTICAL,
            true,true,false);

        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
    }

    public LineChart_AWT(String applicationTitle , String chartTitle, List<List<Double>> acuracias, List<String> rotuloClassificadores) throws ParseException {
        super(applicationTitle);
        XYDataset dataset;
        dataset = createDatasetAcuracia(acuracias, rotuloClassificadores);
        String label = "Accuracy";
        JFreeChart lineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Evaluation moments",label,
                dataset,
                PlotOrientation.VERTICAL,
                true,true,false);


        XYPlot xyplot = (XYPlot) lineChart.getPlot();

        ValueAxis rangeAxis = xyplot.getRangeAxis();
        rangeAxis.setRange(0.0, 105);

        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

//        final NumberAxis rangeAxis2 = (NumberAxis) xyplot.getRangeAxis();
//        rangeAxis2.setTickUnit(new NumberTickUnit(10));

        final NumberAxis domainAxis = (NumberAxis) xyplot.getDomainAxis();
        //domainAxis.setTickUnit(new NumberTickUnit(100));
        domainAxis.setTickUnit(new NumberTickUnit(5));

//        final XYPlot plot = lineChart.getXYPlot();
//        ValueMarker marker;
//        marker = new ValueMarker(10000);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.0f));
//        plot.addDomainMarker(marker);

        //MOA
//        final XYPlot plot = lineChart.getXYPlot();
//        ValueMarker marker;
//        //vertical
//        marker = new ValueMarker(25);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(60);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);

        //RBF
        final XYPlot plot = lineChart.getXYPlot();
        ValueMarker marker;
        //vertical
        marker = new ValueMarker(8);
        marker.setPaint(Color.gray);
        marker.setStroke(new BasicStroke(1.5f));
        plot.addDomainMarker(marker);

        marker = new ValueMarker(42);
        marker.setPaint(Color.gray);
        marker.setStroke(new BasicStroke(1.5f));
        plot.addDomainMarker(marker);


        //synedc
//        final XYPlot plot = lineChart.getXYPlot();
//        ValueMarker marker;
//        //vertical
//        marker = new ValueMarker(171);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(158);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(146);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(133);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(121);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(108);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(96);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(83);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(71);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(58);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(46);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(33);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(21);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);
//
//        marker = new ValueMarker(8);
//        marker.setPaint(Color.gray);
//        marker.setStroke(new BasicStroke(1.5f));
//        plot.addDomainMarker(marker);

        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);

        XYItemRenderer r = xyplot.getRenderer();
        r.setSeriesStroke(0, new BasicStroke(3.0f));
        r.setSeriesStroke(1, new BasicStroke(3.0f ));
        r.setSeriesStroke(2, new BasicStroke(3.0f ));
        r.setSeriesStroke(3, new BasicStroke(3.0f ));
        r.setSeriesStroke(4, new BasicStroke(3.0f ));

        r.setSeriesPaint(0, new Color(95, 173, 86));
        r.setSeriesPaint(1, new Color(242, 193, 78));
        r.setSeriesPaint(2, new Color(247, 129, 84));
        r.setSeriesPaint(3, new Color(49, 116, 161));
        r.setSeriesPaint(4, new Color(180, 67, 108));

        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
    }

    private XYDataset createDatasetMnew(List<List<Double>> medidasClassicas) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Acurácia");
        for(int i=0; i<medidasClassicas.size(); i++) {
            for(int j=0; j<medidasClassicas.get(i).size(); j++) {
                series.add(Double.parseDouble(Integer.toString(j)), medidasClassicas.get(i).get(j));
            }
        }
        dataset.addSeries(series);
        return dataset;
    }

    private XYDataset createDatasetAcuracia(List<List<Double>> acuracias, List<String> rotuloClassificadores) throws ParseException {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries(rotuloClassificadores.get(0));
        XYSeries series2 = new XYSeries(rotuloClassificadores.get(1));
//        XYSeries series3 = new XYSeries(rotuloClassificadores.get(2));
//        XYSeries series4 = new XYSeries(rotuloClassificadores.get(3));
//        XYSeries series5 = new XYSeries(rotuloClassificadores.get(4));
        series1.add(NumberFormat.getInstance().parse("0"), null);
        series2.add(NumberFormat.getInstance().parse("0"), null);
//        series3.add(NumberFormat.getInstance().parse("0"), null);
//        series4.add(NumberFormat.getInstance().parse("0"), null);
//        series5.add(NumberFormat.getInstance().parse("0"), null);
        for(int i=0; i<acuracias.get(0).size(); i++) {
            series1.add(NumberFormat.getInstance().parse(Integer.toString(i+1)), acuracias.get(0).get(i));
            series2.add(NumberFormat.getInstance().parse(Integer.toString(i+1)), acuracias.get(1).get(i));
//            series3.add(NumberFormat.getInstance().parse(Integer.toString(i+1)), acuracias.get(2).get(i));
//            series4.add(NumberFormat.getInstance().parse(Integer.toString(i+1)), acuracias.get(3).get(i));
//            series5.add(NumberFormat.getInstance().parse(Integer.toString(i+1)), acuracias.get(4).get(i));
        }
        dataset.addSeries(series1);
        dataset.addSeries(series2);
//        dataset.addSeries(series3);
//        dataset.addSeries(series4);
//        dataset.addSeries(series5);
        return dataset;
    }

    private XYDataset createDataseUnkR(List<List<Double>> acuracias, List<String> rotuloClassificadores) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries(rotuloClassificadores.get(0));
        XYSeries series2 = new XYSeries(rotuloClassificadores.get(1));
        XYSeries series3 = new XYSeries(rotuloClassificadores.get(2));
//        XYSeries series4 = new XYSeries(rotuloClassificadores.get(3));
        for(int i=0; i<acuracias.get(0).size(); i++) {
            double valor = acuracias.get(1).get(i)*100;
            series1.add(i, acuracias.get(0).get(i));
            series2.add(i, valor);
            series3.add(i, acuracias.get(2).get(i));
//            series4.add(i, acuracias.get(3).get(i));
        }
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
//        dataset.addSeries(series4);
        return dataset;
    }
}