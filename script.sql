CREATE TABLE Utilisateur (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    mot_de_passe VARCHAR(20) NOT NULL,
    ip VARCHAR(20),
    connecte BOOLEAN,
    profil VARCHAR(255)
);

CREATE TABLE Contacts (
    user_id INT NOT NULL,
    contact_id INT NOT NULL,
    PRIMARY KEY (user_id, contact_id),
    FOREIGN KEY (user_id) REFERENCES Utilisateur(user_id),
    FOREIGN KEY (contact_id) REFERENCES Utilisateur(user_id)
);

-- drop table if exists Contacts;
-- drop table if exists Utilisateur;