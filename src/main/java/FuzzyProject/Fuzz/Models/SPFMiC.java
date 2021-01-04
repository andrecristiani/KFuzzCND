package FuzzyProject.Fuzz.Models;

import FuzzyProject.Fuzz.Utils.DistanceMeasures;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;

public class SPFMiC implements Instance {
    private double Mm; //soma linear das pertinências elevadas a m
    private double Tn; //soma linear das tipicidades elevadas a n
    private double CF1pertinencias[]; //soma linear dos ex ponderados por suas pertinencias
    private double CF1tipicidades[]; //soma linear dos ex ponderados por suas tipicidades
    private double SSDe; //soma das distâncias dos exemplos para o protótipo do micro-grupo, elevadas a m
    private double N;
    private double t;
    private double updated;
    private double rotulo;
    private double rotuloReal;
    private double centroide[];
    private double alpha;
    private double theta;
    private boolean isNull;
    public double minFr;
    public double rotuloMenorDistancia;

    public SPFMiC(double[] centroide, int N, double alpha, double theta, int t) {
        this.CF1pertinencias = centroide;
        this.CF1tipicidades = centroide;
        this.centroide = centroide;
        this.N = N;
        this.alpha = alpha;
        this.theta = theta;
        this.Mm = 1;
        this.Tn = 1;
        this.updated = t;
        this.SSDe = 0;
        this.t = t;
    }

    public SPFMiC() {
        this.isNull = true;
    }

    public double getLSm() {
        return Mm;
    }

    public void setLSm(double LSm) {
        this.Mm = LSm;
    }

    public double getLSn() {
        return Tn;
    }

    public void setLSn(double LSn) {
        this.Tn = LSn;
    }

    public double[] getCF1pertinencias() {
        return CF1pertinencias;
    }

    public void setCF1pertinencias(double[] CF1pertinencias) {
        this.CF1pertinencias = CF1pertinencias;
    }

    public double[] getCF1tipicidades() {
        return CF1tipicidades;
    }

    public void setCF1tipicidades(double[] CF1tipicidades) {
        this.CF1tipicidades = CF1tipicidades;
    }

    public double getSSDe() {
        return SSDe;
    }

    public void setSSDe(double SSDe) {
        this.SSDe = SSDe;
    }

    public double getN() {
        return N;
    }

    public void setN(double n) {
        N = n;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public double getRotulo() {
        return this.rotulo;
    }

    public void setRotulo(double rotulo) {
        this.rotulo = rotulo;
    }

    public double[] getCentroide() {
        return this.centroide;
    }

    public void setCentroide(double[] centroide) {
        this.centroide = centroide;
    }

    public double getMm() {
        return Mm;
    }

    public void setMm(double mm) {
        Mm = mm;
    }

    public double getTn() {
        return Tn;
    }

    public void setTn(double tn) {
        Tn = tn;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    public void addPointToMm(double pertinencia) {
        this.Mm += Math.pow(pertinencia, 2);
    }

    public double getRotuloReal() {
        return rotuloReal;
    }

    public void setRotuloReal(double rotuloReal) {
        this.rotuloReal = rotuloReal;
    }

    public double getMinFr() {
        return minFr;
    }

    public void setMinFr(double minFr) {
        this.minFr = minFr;
    }

    public double getRotuloMenorDistancia() {
        return rotuloMenorDistancia;
    }

    public void setRotuloMenorDistancia(double rotuloMenorDistancia) {
        this.rotuloMenorDistancia = rotuloMenorDistancia;
    }

    public double getUpdated() {
        return updated;
    }

    public void setUpdated(double updated) {
        this.updated = updated;
    }

    /***
     * Updates the center position.
     */
    private void atualizaCentroide(){
        int nAtributos = this.CF1pertinencias.length;
        this.centroide = new double[nAtributos];
        for(int i=0; i<nAtributos; i++) {
            this.centroide[i] = (
                    (this.alpha * CF1pertinencias[i] + this.theta * CF1tipicidades[i]) /
                            (this.alpha * this.Tn + this.theta * Mm)
            );
        }

    }

    /***
     * Function used to assign an exemple to SPFMiC
     */
    public void atribuiExemplo(Example exemplo, double pertinencia, double tipicidade, double m, double n) {
        double dist = DistanceMeasures.calculaDistanciaEuclidiana(exemplo.getPonto(), this.centroide);
        this.N++;
        this.Mm += Math.pow(pertinencia, m);
        this.Tn += Math.pow(tipicidade, n);
        this.SSDe += Math.pow(dist, m) * pertinencia;
        for(int i=0; i<this.centroide.length; i++) {
            this.CF1pertinencias[i] += exemplo.getPontoPorPosicao(i) * pertinencia;
            this.CF1tipicidades[i] = exemplo.getPontoPorPosicao(i) * tipicidade;
        }
        this.atualizaCentroide();
    }

    /***
     * Function used to calculate an attribute used in the typicality function
     */
    public double calculaTipicidade(double[] exemplo, double n, double K) {
        double tipicidadeI = this.getTipicidadeI(K);
        double dist = DistanceMeasures.calculaDistanciaEuclidiana(exemplo, this.centroide);
        return (1 /
                (1 + Math.pow(((this.theta/tipicidadeI) * dist),
                        (1/(n-1))
                )
                ));
    }

    /***
     * Function used to calculate an attribute used in the typicality function
     */
    private double getTipicidadeI(double K) {
        return  (this.SSDe / this.Mm);
    }

    public double getRadius() {
        return Math.sqrt((this.SSDe/this.N)) * 2;
    }

    public double getRadiusNsModel() {
        return Math.sqrt((this.SSDe/this.N));
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
        return this.centroide;
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
}
