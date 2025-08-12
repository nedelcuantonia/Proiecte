package model;

public class Produse {
    private int id;
    private String denumire;
    private int cantitate;

    // Constructori, getter și setter
    public Produse() {}
    
    public Produse(int id, String denumire, int cantitate) {
        this.id = id;
        this.denumire = denumire;
        this.cantitate = cantitate;
    }

    // Getters și setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void setCantitate(int cantitate) {
        this.cantitate = cantitate;
    }
}
