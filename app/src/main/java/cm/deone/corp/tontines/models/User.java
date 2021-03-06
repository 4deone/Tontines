package cm.deone.corp.tontines.models;

public class User {

    private String idUser;
    private String nameUser;
    private String avatarUser;
    private String coverUser;
    private String phoneUser;
    private String emailUser;
    private String cniUser;
    private String deliveryCniUser;
    private String villeUser;
    private String dateCreationUser;
    private String ruleUser;
    private String onlineUser;
    private String typingTo;
    private boolean activeUser;

    public User() {
    }

    public User(String emailUser) {
        this.emailUser = emailUser;
    }

    public User(String nameUser, String phoneUser) {
        this.nameUser = nameUser;
        this.phoneUser = phoneUser;
    }

    public User(String idUser, String nameUser, String avatarUser,
                String coverUser, String phoneUser, String emailUser,
                String cniUser, String deliveryCniUser, String villeUser,
                String dateCreationUser, String ruleUser, String onlineUser,
                String typingTo, boolean activeUser) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.avatarUser = avatarUser;
        this.coverUser = coverUser;
        this.phoneUser = phoneUser;
        this.emailUser = emailUser;
        this.cniUser = cniUser;
        this.deliveryCniUser = deliveryCniUser;
        this.villeUser = villeUser;
        this.dateCreationUser = dateCreationUser;
        this.ruleUser = ruleUser;
        this.onlineUser = onlineUser;
        this.typingTo = typingTo;
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

    public String getAvatarUser() {
        return avatarUser;
    }

    public void setAvatarUser(String avatarUser) {
        this.avatarUser = avatarUser;
    }

    public String getCoverUser() {
        return coverUser;
    }

    public void setCoverUser(String coverUser) {
        this.coverUser = coverUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
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

    public String getOnlineUser() {
        return onlineUser;
    }

    public void setOnlineUser(String onlineUser) {
        this.onlineUser = onlineUser;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public boolean isActiveUser() {
        return activeUser;
    }

    public void setActiveUser(boolean activeUser) {
        this.activeUser = activeUser;
    }

    public String getDeliveryCniUser() {
        return deliveryCniUser;
    }

    public void setDeliveryCniUser(String deliveryCniUser) {
        this.deliveryCniUser = deliveryCniUser;
    }

    public String getRuleUser() {
        return ruleUser;
    }

    public void setRuleUser(String ruleUser) {
        this.ruleUser = ruleUser;
    }

    public String getVilleUser() {
        return villeUser;
    }

    public void setVilleUser(String villeUser) {
        this.villeUser = villeUser;
    }

}
