-- Suppression si l'utilisateur existe déjà pour éviter les erreurs au redémarrage
DELETE FROM utilisateurs WHERE username = 'admin';

-- Insertion de l'admin (le mot de passe est 'admin123' haché en BCrypt)
INSERT INTO utilisateurs (username, password, role)
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2L9.p.L.f4.NfJp9tZzP9Z2Z2Z2', 'ROLE_ADMIN');

-- Ajout de quelques voitures de test pour "RIDE WITH LAAMARI"
INSERT INTO voiture (marque, modele, prix_par_jour, disponible) VALUES
                                                                    ('Volkswagen', 'Golf 8', 50.0, true),
                                                                    ('BMW', 'Série 3', 80.0, true),
                                                                    ('Mercedes', 'Classe C', 95.0, true);