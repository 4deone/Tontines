package cm.deone.corp.tontines.models;

public class Caisse {
    private String idTontine;
    private String idCaisse;
    private String titreCaisse;
    private String montantCaisse;
    private String descriptionCaisse;
    private String dateCreationCaisse;
    private boolean activeCaisse;

    public Caisse() {
    }

    public Caisse(String idTontine, String idCaisse, String titreCaisse, String montantCaisse,
                  String descriptionCaisse, String dateCreationCaisse, boolean activeCaisse) {
        this.idTontine = idTontine;
        this.idCaisse = idCaisse;
        this.titreCaisse = titreCaisse;
        this.montantCaisse = montantCaisse;
        this.descriptionCaisse = descriptionCaisse;
        this.dateCreationCaisse = dateCreationCaisse;
        this.activeCaisse = activeCaisse;
    }

    public String getIdTontine() {
        return idTontine;
    }

    public void setIdTontine(String idTontine) {
        this.idTontine = idTontine;
    }

    public String getIdCaisse() {
        return idCaisse;
    }

    public void setIdCaisse(String idCaisse) {
        this.idCaisse = idCaisse;
    }

    public String getTitreCaisse() {
        return titreCaisse;
    }

    public void setTitreCaisse(String titreCaisse) {
        this.titreCaisse = titreCaisse;
    }

    public String getMontantCaisse() {
        return montantCaisse;
    }

    public void setMontantCaisse(String montantCaisse) {
        this.montantCaisse = montantCaisse;
    }

    public String getDescriptionCaisse() {
        return descriptionCaisse;
    }

    public void setDescriptionCaisse(String descriptionCaisse) {
        this.descriptionCaisse = descriptionCaisse;
    }

    public String getDateCreationCaisse() {
        return dateCreationCaisse;
    }

    public void setDateCreationCaisse(String dateCreationCaisse) {
        this.dateCreationCaisse = dateCreationCaisse;
    }

    public boolean isActiveCaisse() {
        return activeCaisse;
    }

    public void setActiveCaisse(boolean activeCaisse) {
        this.activeCaisse = activeCaisse;
    }
}
