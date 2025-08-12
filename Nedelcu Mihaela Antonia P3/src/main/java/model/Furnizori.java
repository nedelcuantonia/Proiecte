package model;

public class Furnizori {
    private int furnizorID;
    private String numeFurnizor;
    private String adresa;

    // Constructor
    public Furnizori(int furnizorID, String numeFurnizor, String adresa) {
        this.furnizorID = furnizorID;
        this.numeFurnizor = numeFurnizor;
        this.adresa = adresa;
    }

    // Getters și Setters
    public int getFurnizorID() {
        return furnizorID;
    }

    public void setFurnizorID(int furnizorID) {
        this.furnizorID = furnizorID;
    }

    public String getNumeFurnizor() {
        return numeFurnizor;
    }

    public void setNumeFurnizor(String numeFurnizor) {
        this.numeFurnizor = numeFurnizor;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    // Override pentru metoda toString() - opțional
    @Override
    public String toString() {
        return "Furnizor{" +
               "furnizorID=" + furnizorID +
               ", numeFurnizor='" + numeFurnizor + '\'' +
               ", adresa='" + adresa + '\'' +
               '}';
    }
}
