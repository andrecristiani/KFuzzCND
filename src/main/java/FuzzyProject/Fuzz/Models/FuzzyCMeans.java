package FuzzyProject.Fuzz.Models;

import FuzzyProject.Fuzz.Utils.FuzzyFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FuzzyCMeans {
    public List<Example> data;
    public ArrayList<ArrayList<Float>> clusterCenters;
    public ArrayList<Cluster> clusters;
    private double u[][];
    private double u_pre[][];
    private int clusterCount;
    private int iteration;
    private int dimension;
    private int fuzziness;
    private double epsilon;
    public double finalError;

    public FuzzyCMeans(){
        data = new ArrayList<>();
        clusterCenters = new ArrayList<>();
        clusters = new ArrayList<>();
        fuzziness = 2;
        epsilon = 0.01;
    }

    public void run(int clusterNumber, int iter, List<Example> data){
        this.clusterCount = clusterNumber;
        this.iteration = iter;
        this.data = data;

        //start algorithm
        //1 assign initial membership values
        assignInitialMembership();

        for (int i = 0; i < iteration; i++) {
            //2 calculate cluster centers
            calculateClusterCenters();

            //3
            updateMembershipValues();

            //4
//            updateNumPoints();

            //5
            finalError = checkConvergence();
            if(finalError <= epsilon)
                break;
        }
    }

    /**
     * this function generate membership value for each data
     */
    private void assignInitialMembership(){
        u = new double[data.size()][clusterCount];
        u_pre = new double[data.size()][clusterCount];
        Random r = new Random();
        for (int i = 0; i < data.size(); i++) {
            float sum = 0;
            for (int j = 0; j < clusterCount; j++) {
                u[i][j] = r.nextDouble() * 10 + 1;
                sum += u[i][j];
            }
            for (int j = 0; j < clusterCount; j++) {
                u[i][j] = u[i][j] / sum;
            }
        }
    }

    /**
     * in this function we calculate value of each cluster
     */
    private void calculateClusterCenters(){
        clusterCenters.clear();
        clusters.clear();
        for (int i = 0; i < clusterCount; i++) {
            ArrayList<Float> tmp = new ArrayList<>();
            for (int j = 0; j < this.data.get(0).getPonto().length; j++) {
                float cluster_ij;
                float sum1 = 0;
                float sum2 = 0;
                for (int k = 0; k < data.size(); k++) {
                    double tt = Math.pow(u[k][i], fuzziness);
                    sum1 += tt * data.get(k).getPontoPorPosicao(j);
                    sum2 += tt;
                }
                cluster_ij = sum1/sum2;
                tmp.add(cluster_ij);
            }
            clusterCenters.add(tmp);
            clusters.add(new Cluster(tmp));
        }
    }

    /**
     * in this function we will update membership value
     */
    private void updateMembershipValues(){
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < clusterCount; j++) {
                u_pre[i][j] = u[i][j];
                float sum = 0;
                float upper = Distance(data.get(i).getPoint(), clusterCenters.get(j));
                for (int k = 0; k < clusterCount; k++) {
                    float lower = Distance(data.get(i).getPoint(), clusterCenters.get(k));
                    sum += Math.pow((upper/lower), 2/(fuzziness -1));
                }
                u[i][j] = 1/sum;
            }
            int maxIndex = getMaxIndex(u[i]);
            this.clusters.get(maxIndex).addPoint(data.get(i));
        }
    }

    /**
     * get norm 2 of two point
     * @param p1
     * @param p2
     * @return
     */
    private float Distance(double[] p1, ArrayList<Float> p2){
        float sum = 0;
        for (int i = 0; i < p2.size(); i++) {
            sum += Math.pow(p1[i] - p2.get(i), 2);
        }
        sum = (float) Math.sqrt(sum);
        return sum;
    }

    /**
     * we calculate norm 2 of ||U - U_pre||
     * @return
     */
    private double checkConvergence(){
        double sum = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < clusterCount; j++) {
                sum += Math.pow(u[i][j] - u_pre[i][j], 2);
            }
        }
        return Math.sqrt(sum);
    }

    private void updateNumPoints() {
        ArrayList<Integer> clusterPoints = new ArrayList<>();
        for(int i=0; i<clusterCount; i++) {
            clusterPoints.add(0);
        }

        for(int i=0; i<u.length; i++) {
            int maxIndex = this.getMaxIndex(u[i]);
            int val = clusterPoints.get(maxIndex);
            clusterPoints.set(maxIndex, val + 1);
        }

        for(int i=0; i<clusterCount; i++) {
//            clusters.get(i).setPoints(clusterPoints.get(i));
        }
    }

    private int getMaxIndex(double[] array) {
        int index = 0;
        double maior = -1000000;
        for(int i=0; i<array.length; i++) {
            if(array[i] > maior && array[i] < 1){
                index = i;
                maior = array[i];
            }
        }
        return index;
    }

    public double[][] getMembershipMatrix() {
        return this.u;
    }
}