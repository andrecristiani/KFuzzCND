package FuzzyProject.Fuzz.Models;

public class ClassicMeasures {
    private double fnew;
    private double mnew;
    private double err;
    private int indice;

    public double getFnew() {
        return fnew;
    }

    public void setFnew(double fnew) {
        this.fnew = fnew;
    }

    public double getMnew() {
        return mnew;
    }

    public void setMnew(double mnew) {
        this.mnew = mnew;
    }

    public double getErr() {
        return err;
    }

    public void setErr(double err) {
        this.err = err;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }
}
