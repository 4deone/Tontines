package cm.deone.corp.tontines.models;

public class Exercice {
    private String idTontine;
    private String idExercice;
    private String dateDebutExercice;
    private String dateFinExercice;

    public Exercice() {
    }

    public Exercice(String idTontine, String idExercice, String dateDebutExercice, String dateFinExercice) {
        this.idTontine = idTontine;
        this.idExercice = idExercice;
        this.dateDebutExercice = dateDebutExercice;
        this.dateFinExercice = dateFinExercice;
    }

    public String getIdTontine() {
        return idTontine;
    }

    public void setIdTontine(String idTontine) {
        this.idTontine = idTontine;
    }

    public String getIdExercice() {
        return idExercice;
    }

    public void setIdExercice(String idExercice) {
        this.idExercice = idExercice;
    }

    public String getDateDebutExercice() {
        return dateDebutExercice;
    }

    public void setDateDebutExercice(String dateDebutExercice) {
        this.dateDebutExercice = dateDebutExercice;
    }

    public String getDateFinExercice() {
        return dateFinExercice;
    }

    public void setDateFinExercice(String dateFinExercice) {
        this.dateFinExercice = dateFinExercice;
    }
}
