package cm.deone.corp.tontines.models;

public class User {
    private String idUser;
    private String nameUser;
    private String phoneUser;
    private String photoUser;
    private String emailUser;
    private String cniUser;
    private String dateDelivrancecCniUser;
    private String villeUser;
    private String dateCreationUser;
    private boolean activeUser;

    public User() {
    }

    public User(String idUser, String nameUser, String phoneUser, String photoUser, String emailUser, String cniUser,
                String dateDelivrancecCniUser, String villeUser, String dateCreationUser, boolean activeUser) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.phoneUser = phoneUser;
        this.photoUser = photoUser;
        this.emailUser = emailUser;
        this.cniUser = cniUser;
        this.dateDelivrancecCniUser = dateDelivrancecCniUser;
        this.villeUser = villeUser;
        this.dateCreationUser = dateCreationUser;
        this.activeUser = activeUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public String getPhotoUser() {
        return photoUser;
    }

    public void setPhotoUser(String photoUser) {
        this.photoUser = photoUser;
    }

    public String getCniUser() {
        return cniUser;
    }

    public void setCniUser(String cniUser) {
        this.cniUser = cniUser;
    }

    public String getDateCreationUser() {
        return dateCreationUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public void setDateCreationUser(String dateCreationUser) {
        this.dateCreationUser = dateCreationUser;
    }

    public boolean isActiveUser() {
        return activeUser;
    }

    public void setActiveUser(boolean activeUser) {
        this.activeUser = activeUser;
    }

    public String getDateDelivrancecCniUser() {
        return dateDelivrancecCniUser;
    }

    public void setDateDelivrancecCniUser(String dateDelivrancecCniUser) {
        this.dateDelivrancecCniUser = dateDelivrancecCniUser;
    }

    public String getVilleUser() {
        return villeUser;
    }

    public void setVilleUser(String villeUser) {
        this.villeUser = villeUser;
    }
}
