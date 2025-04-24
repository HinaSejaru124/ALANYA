CREATE TABLE Utilisateur (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    mot_de_passe VARCHAR(20) NOT NULL,
    ip VARCHAR(20),
    connecte BOOLEAN,
    profil VARCHAR(255)
);

-- drop table if exists Utilisateur;