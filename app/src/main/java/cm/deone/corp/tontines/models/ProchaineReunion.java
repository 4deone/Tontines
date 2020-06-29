package cm.deone.corp.tontines.models;

public class ProchaineReunion {
    private String idProchaineReunion;
    private String dateProchaineReunion;

    public ProchaineReunion() {
    }

    public ProchaineReunion(String idProchaineReunion, String dateProchaineReunion) {
        this.idProchaineReunion = idProchaineReunion;
        this.dateProchaineReunion = dateProchaineReunion;
    }

    public String getIdProchaineReunion() {
        return idProchaineReunion;
    }

    public void setIdProchaineReunion(String idProchaineReunion) {
        this.idProchaineReunion = idProchaineReunion;
    }

    public String getDateProchaineReunion() {
        return dateProchaineReunion;
    }

    public void setDateProchaineReunion(String dateProchaineReunion) {
        this.dateProchaineReunion = dateProchaineReunion;
    }
}
