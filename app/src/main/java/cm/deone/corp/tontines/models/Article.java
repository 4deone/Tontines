package cm.deone.corp.tontines.models;

public class Article {
    private String idArticle;
    private String typeArticle;
    private String titreArticle;
    private String contenuArticle;
    private String dateArticle;

    public Article() {
    }

    public Article(String idArticle, String typeArticle, String titreArticle, String contenuArticle, String dateArticle) {
        this.idArticle = idArticle;
        this.typeArticle = typeArticle;
        this.titreArticle = titreArticle;
        this.contenuArticle = contenuArticle;
        this.dateArticle = dateArticle;
    }

    public String getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(String idArticle) {
        this.idArticle = idArticle;
    }

    public String getTypeArticle() {
        return typeArticle;
    }

    public void setTypeArticle(String typeArticle) {
        this.typeArticle = typeArticle;
    }

    public String getTitreArticle() {
        return titreArticle;
    }

    public void setTitreArticle(String titreArticle) {
        this.titreArticle = titreArticle;
    }

    public String getContenuArticle() {
        return contenuArticle;
    }

    public void setContenuArticle(String contenuArticle) {
        this.contenuArticle = contenuArticle;
    }

    public String getDateArticle() {
        return dateArticle;
    }

    public void setDateArticle(String dateArticle) {
        this.dateArticle = dateArticle;
    }
}
