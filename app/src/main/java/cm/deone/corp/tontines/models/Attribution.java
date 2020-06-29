package cm.deone.corp.tontines.models;

public class Attribution {
    private String idPoste;
    private String idAttribution;
    private String titreAttribution;

    public Attribution() {
    }

    public Attribution(String idPoste, String idAttribution, String titreAttribution) {
        this.idPoste = idPoste;
        this.idAttribution = idAttribution;
        this.titreAttribution = titreAttribution;
    }

    public String getIdPoste() {
        return idPoste;
    }

    public void setIdPoste(String idPoste) {
        this.idPoste = idPoste;
    }

    public String getIdAttribution() {
        return idAttribution;
    }

    public void setIdAttribution(String idAttribution) {
        this.idAttribution = idAttribution;
    }

    public String getTitreAttribution() {
        return titreAttribution;
    }

    public void setTitreAttribution(String titreAttribution) {
        this.titreAttribution = titreAttribution;
    }
}
