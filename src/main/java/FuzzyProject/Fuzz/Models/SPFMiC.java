package FuzzyProject.Fuzz.Models;

import FuzzyProject.Fuzz.Utils.DistanceMeasures;

public class SPFMiC {
    private double Mm; //soma linear das pertinências elevadas a m
    private double Tn; //soma linear das tipicidades elevadas a n
    private double CF1pertinencias[]; //soma linear dos ex ponderados por suas pertinencias
    private double CF1tipicidades[]; //soma linear dos ex ponderados por suas tipicidades
    private double SSDe; //soma das distâncias dos exemplos para o protótipo do micro-grupo, elevadas a m
    private double N;
    private double t;
    private double rotulo;
    private double centroide[];
    private double alpha;
    private double theta;
    private boolean isNull;
    public double minFr;
    public double rotuloMenorDistancia;

    public SPFMiC(double[] centroide, int N, double alpha, double theta) {
        this.CF1pertinencias = centroide;
        this.CF1tipicidades = centroide;
        this.centroide = centroide;
        this.N = N;
        this.alpha = alpha;
        this.theta = theta;
        this.Mm = 1;
        this.Tn = 1;
        this.SSDe = 0;
        this.t = 0;
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
        return Math.sqrt((this.SSDe/this.N));
    }
}
