package FuzzyProject.Fuzz.Models;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private double[] center;
    public ArrayList<Example> points;

    public Cluster(ArrayList<Float> center) {
        this.center = new double[center.size()];
        for(int i=0; i<center.size(); i++) {
            this.center[i] = center.get(i);
        }
        this.points = new ArrayList<>();
    }

    public double[] getCenter() {
        return center;
    }

    public void setCenter(double[] center) {
        this.center = center;
    }

    public List<Example> getPoints() {
        return points;
    }

    public void addPoint(Example point) {
        this.points = points;
    }
}
