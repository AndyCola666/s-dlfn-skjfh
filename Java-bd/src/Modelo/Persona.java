package Modelo;

public class Persona {

    int ID;
    String Nom;
    String Ape;
    String Corr;
    String Tel;

    public Persona() {

    }

    public Persona(int ID, String Nom, String Ape, String Corr, String Tel) {
        this.ID = ID;
        this.Nom = Nom;
        this.Ape = Ape;
        this.Corr = Corr;
        this.Tel = Tel;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String Nom) {
        this.Nom = Nom;
    }

    public String getApe() {
        return Ape;
    }

    public void setApe(String Ape) {
        this.Ape = Ape;
    }

    public String getCorr() {
        return Corr;
    }

    public void setCorr(String Corr) {
        this.Corr = Corr;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String Tel) {
        this.Tel = Tel;
    }
    
}
