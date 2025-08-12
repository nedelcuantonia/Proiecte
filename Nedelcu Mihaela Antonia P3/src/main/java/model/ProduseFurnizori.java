package model;

public class ProduseFurnizori {
    private int produsID;
    private String numeprodus;
    private int furnizorID;
    private String numefurnizor;

    // Getteri și setteri
    public int getProdusID() {
        return produsID;
    }

    public void setProdusID(int produsID) {
        this.produsID = produsID;
    }

    public String getNumeprodus() {
        return numeprodus;
    }

    public void setNumeprodus(String numeprodus) {
        this.numeprodus = numeprodus;
    }

    public int getFurnizorID() {
        return furnizorID;
    }

    public void setFurnizorID(int furnizorID) {
        this.furnizorID = furnizorID;
    }

    public String getNumefurnizor() {
        return numefurnizor;
    }

    public void setNumefurnizor(String numefurnizor) {
        this.numefurnizor = numefurnizor;
    }
}
