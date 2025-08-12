package model;

public class Clienti {
    private int clientID;
    private String nume;
    private String prenume;
    private String adresa;
    private String email;

    // Constructor
    public Clienti(int clientID, String nume, String prenume, String adresa, String email) {
        this.clientID = clientID;
        this.nume = nume;
        this.prenume = prenume;
        this.adresa = adresa;
        this.email = email;
    }

    // Getters și Setters
    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Override pentru metoda toString() - opțional
    @Override
    public String toString() {
        return "Clienti{" +
               "clientID=" + clientID +
               ", nume='" + nume + '\'' +
               ", prenume='" + prenume + '\'' +
               ", adresa='" + adresa + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
