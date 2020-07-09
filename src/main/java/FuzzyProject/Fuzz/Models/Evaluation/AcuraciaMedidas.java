package FuzzyProject.Fuzz.Models.Evaluation;

public class AcuraciaMedidas {
    private int ponto;
    private double acuracia;

    public AcuraciaMedidas(int ponto, double acuracia) {
        this.ponto = ponto;
        this.acuracia = acuracia;
    }

    public int getPonto() {
        return ponto;
    }

    public void setPonto(int ponto) {
        this.ponto = ponto;
    }

    public double getAcuracia() {
        return acuracia;
    }

    public void setAcuracia(double acuracia) {
        this.acuracia = acuracia;
    }
}
