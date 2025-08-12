package model;

public class FurnizoriClienti {
    private int furnizorID;
    private int clientID;
    private String numefurnizor;
    private String numeclient;

    // Constructor implicit
    public FurnizoriClienti() {}

    // Constructor cu parametri
    public FurnizoriClienti(int furnizorID, int clientID) {
        this.furnizorID = furnizorID;
        this.clientID = clientID;
    }

    // Getter și Setter pentru furnizorID
    public int getFurnizorID() {
        return furnizorID;
    }

    public void setFurnizorID(int furnizorID) {
        this.furnizorID = furnizorID;
    }

    // Getter și Setter pentru clientID
    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }
    
    public String getNumefurnizor() {
        return numefurnizor;
    }

    public void setNumefurnizor(String numefurnizor) {
        this.numefurnizor = numefurnizor;
    }
    public String getNumeclient() {
        return numeclient;
    }

    public void setNumeclient(String numeclient) {
        this.numeclient = numeclient;
    }

    @Override
    public String toString() {
        return "FurnizoriClienti{" +
               "furnizorID=" + furnizorID +
               ", clientID=" + clientID +
               '}';
    }
}
