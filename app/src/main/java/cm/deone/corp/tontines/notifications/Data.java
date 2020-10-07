package cm.deone.corp.tontines.notifications;

public class Data {
    private String user;
    private String body;
    private String title;
    private String sented;
    private String notificationType;
    private Integer icon;

    public Data(String user, String body, String title, String sented, String notificationType, Integer icon) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.notificationType = notificationType;
        this.icon = icon;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}
