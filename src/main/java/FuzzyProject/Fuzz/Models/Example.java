package FuzzyProject.Fuzz.Models;

import org.apache.commons.math3.ml.clustering.Clusterable;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;

public class Example implements Instance, Clusterable {
    private double[] ponto;
    private double rotuloVerdadeiro;
    private double rotuloClassificado;
    private boolean desconhecido;

    public Example(Instance instance) {
        this.ponto = instance.toDoubleArray();
        this.rotuloVerdadeiro = instance.classValue();
    }

    public Example(double[] ponto, boolean comRotulo) {
        if(comRotulo) {
            double[] novoPonto = new double[ponto.length-1];
            for(int i=0; i<ponto.length-1; i++) {
                novoPonto[i] = ponto[i];
            }
            this.rotuloVerdadeiro = ponto[ponto.length-1];
            this.ponto = novoPonto;
        } else {
            this.ponto = ponto;
            this.rotuloVerdadeiro = -1;
        }
    }

    public double getRotuloVerdadeiro() {
        return this.rotuloVerdadeiro;
    }

    public void setRotuloVerdadeiro(double rotuloVerdadeiro) {
        this.rotuloVerdadeiro = rotuloVerdadeiro;
    }

    public double getRotuloClassificado() {
        return this.rotuloClassificado;
    }

    public void setRotuloClassificado(double rotuloClassificado) {
        this.rotuloClassificado = rotuloClassificado;
    }

    public double[] getPonto() {
        return ponto;
    }

    public void setPonto(double[] ponto) {
        this.ponto = ponto;
    }

    public boolean isDesconhecido() {
        return desconhecido;
    }

    public void setDesconhecido() {
        this.desconhecido = true;
    }

    public double getPontoPorPosicao(int i) {
        return this.ponto[i];
    }

    public void setDesconhecido(boolean desconhecido) {
        this.desconhecido = desconhecido;
    }

    public String[] arrayToStringArray() {
        String exemplo[] = new String[this.ponto.length];
        for(int i=0; i<this.ponto.length; i++) {
            exemplo[i] = Double.toString(this.ponto[i]);
        }
        return exemplo;
    }

    public String arrayToString() {
        String exemplo = Double.toString(this.ponto[0]);
        for(int i=1; i<this.ponto.length; i++) {
            exemplo += "\t" + this.ponto[i];
        }
        exemplo += "\t" + this.rotuloVerdadeiro;
        return exemplo;
    }

    @Override
    public Attribute attribute(int i) {
        return null;
    }

    @Override
    public Attribute attributeSparse(int i) {
        return null;
    }

    @Override
    public Attribute classAttribute() {
        return null;
    }

    @Override
    public int classIndex() {
        return 0;
    }

    @Override
    public boolean classIsMissing() {
        return false;
    }

    @Override
    public double classValue() {
        return 0;
    }

    @Override
    public Instance copy(double[] doubles) {
        return null;
    }

    @Override
    public Instances dataset() {
        return null;
    }

    @Override
    public void deleteAttributeAt(int i) {

    }

    @Override
    public Enumeration<Attribute> enumerateAttributes() {
        return null;
    }

    @Override
    public boolean equalHeaders(Instance instance) {
        return false;
    }

    @Override
    public String equalHeadersMsg(Instance instance) {
        return null;
    }

    @Override
    public boolean hasMissingValue() {
        return false;
    }

    @Override
    public int index(int i) {
        return 0;
    }

    @Override
    public void insertAttributeAt(int i) {

    }

    @Override
    public boolean isMissing(int i) {
        return false;
    }

    @Override
    public boolean isMissingSparse(int i) {
        return false;
    }

    @Override
    public boolean isMissing(Attribute attribute) {
        return false;
    }

    @Override
    public Instance mergeInstance(Instance instance) {
        return null;
    }

    @Override
    public int numAttributes() {
        return 0;
    }

    @Override
    public int numClasses() {
        return 0;
    }

    @Override
    public int numValues() {
        return 0;
    }

    @Override
    public void replaceMissingValues(double[] doubles) {

    }

    @Override
    public void setClassMissing() {

    }

    @Override
    public void setClassValue(double v) {

    }

    @Override
    public void setClassValue(String s) {

    }

    @Override
    public void setDataset(Instances instances) {

    }

    @Override
    public void setMissing(int i) {

    }

    @Override
    public void setMissing(Attribute attribute) {

    }

    @Override
    public void setValue(int i, double v) {

    }

    @Override
    public void setValueSparse(int i, double v) {

    }

    @Override
    public void setValue(int i, String s) {

    }

    @Override
    public void setValue(Attribute attribute, double v) {

    }

    @Override
    public void setValue(Attribute attribute, String s) {

    }

    @Override
    public void setWeight(double v) {

    }

    @Override
    public Instances relationalValue(int i) {
        return null;
    }

    @Override
    public Instances relationalValue(Attribute attribute) {
        return null;
    }

    @Override
    public String stringValue(int i) {
        return null;
    }

    @Override
    public String stringValue(Attribute attribute) {
        return null;
    }

    @Override
    public double[] toDoubleArray() {
        return new double[0];
    }

    @Override
    public String toStringNoWeight(int i) {
        return null;
    }

    @Override
    public String toStringNoWeight() {
        return null;
    }

    @Override
    public String toStringMaxDecimalDigits(int i) {
        return null;
    }

    @Override
    public String toString(int i, int i1) {
        return null;
    }

    @Override
    public String toString(int i) {
        return null;
    }

    @Override
    public String toString(Attribute attribute, int i) {
        return null;
    }

    @Override
    public String toString(Attribute attribute) {
        return null;
    }

    @Override
    public double value(int i) {
        return 0;
    }

    @Override
    public double valueSparse(int i) {
        return 0;
    }

    @Override
    public double value(Attribute attribute) {
        return 0;
    }

    @Override
    public double weight() {
        return 0;
    }

    @Override
    public Object copy() {
        return null;
    }

    @Override
    public double[] getPoint() {
        return this.ponto;
    }
}
