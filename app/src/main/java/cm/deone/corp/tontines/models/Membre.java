package cm.deone.corp.tontines.models;

public class Membre {
    private String bureau;
    private String date;
    private String name;

    public Membre() {
    }

    public Membre(String bureau, String date, String name) {
        this.bureau = bureau;
        this.date = date;
        this.name = name;
    }

    public String getBureau() {
        return bureau;
    }

    public void setBureau(String bureau) {
        this.bureau = bureau;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
