-- Crearea bazei de date
CREATE DATABASE GestionareProduse;

-- Selectarea bazei de date
USE GestionareProduse;

-- Crearea tabelului Produse
CREATE TABLE Produse (
    produsID INT AUTO_INCREMENT PRIMARY KEY,
    denumire VARCHAR(255) NOT NULL,
    cantitate INT NOT NULL
);
INSERT INTO Produse (Denumire, Cantitate) VALUES
('Laptop', 10),
('Telefon', 15),
('Tabletă', 8);

-- Crearea tabelului Furnizori
CREATE TABLE Furnizori (
    furnizorID INT AUTO_INCREMENT PRIMARY KEY,
    nume VARCHAR(255) NOT NULL,
    adresa TEXT NOT NULL
);
INSERT INTO Furnizori (NumeFurnizor, Adresa) VALUES
('Furnizor A', 'Bucuresti'),
('Furnizor B', 'Ploiesti'),
('Furnizor C', 'Cluj');

-- Crearea tabelului Clienti
CREATE TABLE Clienti (
    clientID INT AUTO_INCREMENT PRIMARY KEY,
    nume VARCHAR(255) NOT NULL,
    prenume VARCHAR(255) NOT NULL,
    adresa VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);
Insert into Clienti(Nume,Prenume,Adresa,Email) VALUES ('Ion','Cristi','Bucuresti','ioncristi@yahoo.com'),('Dumitru','Ana','Ploiesti','dumitraa@yahoo.com'),('Nedelcu','Antonia','Pitesti','anedelcu43@yahoo.com');
-- Crearea tabelului intermediar pentru relația M:N dintre Produse și Furnizori
CREATE TABLE ProduseFurnizori (
    produsID INT NOT NULL,
    furnizorID INT NOT NULL,
    PRIMARY KEY (produsID, furnizorID),
    FOREIGN KEY (produsID) REFERENCES Produse(produsID) ON DELETE CASCADE,
    FOREIGN KEY (furnizorID) REFERENCES Furnizori(furnizorID) ON DELETE CASCADE
);

-- Crearea tabelului intermediar pentru relația M:N dintre Furnizori și Clienti
CREATE TABLE FurnizoriClienti (
    furnizorID INT NOT NULL,
    clientID INT NOT NULL,
    PRIMARY KEY (furnizorID, clientID),
    FOREIGN KEY (furnizorID) REFERENCES Furnizori(furnizorID) ON DELETE CASCADE,
    FOREIGN KEY (clientID) REFERENCES Clienti(clientID) ON DELETE CASCADE
);
SELECT 
    fc.furnizorID, 
    f.numefurnizor, 
    fc.clientID, 
    c.numeclient
FROM 
    FurnizoriClienti fc
JOIN 
    Furnizori  ON fc.furnizorID = Furnizori.furnizorID
JOIN 
    Clienti  ON fc.clientID = Clienti.clientID;
