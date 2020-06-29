package cm.deone.corp.tontines.models;

public class Poste {
    private String idUser;
    private String idPoste;
    private String titrePoste;

    public Poste() {
    }

    public Poste(String idUser, String idPoste, String titrePoste) {
        this.idUser = idUser;
        this.idPoste = idPoste;
        this.titrePoste = titrePoste;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPoste() {
        return idPoste;
    }

    public void setIdPoste(String idPoste) {
        this.idPoste = idPoste;
    }

    public String getTitrePoste() {
        return titrePoste;
    }

    public void setTitrePoste(String titrePoste) {
        this.titrePoste = titrePoste;
    }
}
