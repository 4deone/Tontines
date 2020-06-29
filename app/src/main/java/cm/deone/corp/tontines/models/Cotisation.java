package cm.deone.corp.tontines.models;

public class Cotisation {
    private String idTontine;
    private String idCotisation;
    private String titreCotisation;
    private String montantCotisation;
    private String dateCreationCotisation;
    private String descriptionCotisation;
    private boolean activeCotisation;

    public Cotisation() {
    }

    public Cotisation(String idTontine, String idCotisation, String titreCotisation, String montantCotisation,
                      String dateCreationCotisation, String descriptionCotisation, boolean activeCotisation) {
        this.idTontine = idTontine;
        this.idCotisation = idCotisation;
        this.titreCotisation = titreCotisation;
        this.montantCotisation = montantCotisation;
        this.dateCreationCotisation = dateCreationCotisation;
        this.descriptionCotisation = descriptionCotisation;
        this.activeCotisation = activeCotisation;
    }

    public String getIdTontine() {
        return idTontine;
    }

    public void setIdTontine(String idTontine) {
        this.idTontine = idTontine;
    }

    public String getIdCotisation() {
        return idCotisation;
    }

    public void setIdCotisation(String idCotisation) {
        this.idCotisation = idCotisation;
    }

    public String getTitreCotisation() {
        return titreCotisation;
    }

    public void setTitreCotisation(String titreCotisation) {
        this.titreCotisation = titreCotisation;
    }

    public String getMontantCotisation() {
        return montantCotisation;
    }

    public void setMontantCotisation(String montantCotisation) {
        this.montantCotisation = montantCotisation;
    }

    public String getDateCreationCotisation() {
        return dateCreationCotisation;
    }

    public void setDateCreationCotisation(String dateCreationCotisation) {
        this.dateCreationCotisation = dateCreationCotisation;
    }

    public String getDescriptionCotisation() {
        return descriptionCotisation;
    }

    public void setDescriptionCotisation(String descriptionCotisation) {
        this.descriptionCotisation = descriptionCotisation;
    }

    public boolean isActiveCotisation() {
        return activeCotisation;
    }

    public void setActiveCotisation(boolean activeCotisation) {
        this.activeCotisation = activeCotisation;
    }
}
