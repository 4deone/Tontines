package cm.deone.corp.tontines.models;

public class Tontine {
    private String idTontine;
    private String nameTontine;
    private String modeTontine;
    private String descriptionTontine;
    private String dateCreationTontine;
    private boolean activeTontine;

    public Tontine() {
    }

    public Tontine(String idTontine, String nameTontine, String modeTontine, String descriptionTontine, String dateCreationTontine, boolean activeTontine) {
        this.idTontine = idTontine;
        this.nameTontine = nameTontine;
        this.modeTontine = modeTontine;
        this.descriptionTontine = descriptionTontine;
        this.dateCreationTontine = dateCreationTontine;
        this.activeTontine = activeTontine;
    }

    public String getIdTontine() {
        return idTontine;
    }

    public void setIdTontine(String idTontine) {
        this.idTontine = idTontine;
    }

    public String getNameTontine() {
        return nameTontine;
    }

    public void setNameTontine(String nameTontine) {
        this.nameTontine = nameTontine;
    }

    public String getModeTontine() {
        return modeTontine;
    }

    public void setModeTontine(String modeTontine) {
        this.modeTontine = modeTontine;
    }

    public String getDescriptionTontine() {
        return descriptionTontine;
    }

    public void setDescriptionTontine(String descriptionTontine) {
        this.descriptionTontine = descriptionTontine;
    }

    public String getDateCreationTontine() {
        return dateCreationTontine;
    }

    public void setDateCreationTontine(String dateCreationTontine) {
        this.dateCreationTontine = dateCreationTontine;
    }

    public boolean isActiveTontine() {
        return activeTontine;
    }

    public void setActiveTontine(boolean activeTontine) {
        this.activeTontine = activeTontine;
    }
}
