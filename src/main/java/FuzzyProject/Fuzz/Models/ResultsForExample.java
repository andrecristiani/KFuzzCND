package FuzzyProject.Fuzz.Models;

public class ResultsForExample {
    private String realClass;
    private String classifiedClass;

    public ResultsForExample(String realClass, String classifiedClass) {
        this.realClass = realClass;
        this.classifiedClass = classifiedClass;
    }

    public String getRealClass() {
        return realClass;
    }

    public void setRealClass(String realClass) {
        this.realClass = realClass;
    }

    public String getClassifiedClass() {
        return classifiedClass;
    }

    public void setClassifiedClass(String classifiedClass) {
        this.classifiedClass = classifiedClass;
    }
}
