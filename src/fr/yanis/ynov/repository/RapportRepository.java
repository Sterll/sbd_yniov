package fr.yanis.ynov.repository;

import fr.yanis.ynov.model.RapportModel;

public interface RapportRepository {

    void saveRapport(RapportModel rapport);

    RapportModel getRapportByClientId(int clientId);

}
