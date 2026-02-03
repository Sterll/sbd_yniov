package fr.yanis.ynov.repository;

import fr.yanis.ynov.model.RapportModel;
import fr.yanis.ynov.service.CryptoService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileRapportRepository implements RapportRepository {

    private static final String VAULT_DIR = "vault";
    private static final String PRIMARY_DIR = "primary";
    private static final String BACKUP_DIR = "backup";
    private final CryptoService cryptoService;

    public FileRapportRepository() {
        this.cryptoService = new CryptoService();
        initDirectories();
    }

    private void initDirectories() {
        try {
            Files.createDirectories(Paths.get(VAULT_DIR, PRIMARY_DIR));
            Files.createDirectories(Paths.get(VAULT_DIR, BACKUP_DIR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getPrimaryPath(int clientId) {
        return Paths.get(VAULT_DIR, PRIMARY_DIR, clientId + ".vault");
    }

    private Path getBackupPath(int clientId) {
        return Paths.get(VAULT_DIR, BACKUP_DIR, clientId + ".vault");
    }

    @Override
    public void saveRapport(RapportModel rapport) {
        try {
            byte[] encrypted = cryptoService.encrypt(rapport.content().getBytes(StandardCharsets.UTF_8));
            Files.write(getPrimaryPath(rapport.clientId()), encrypted);
            Files.write(getBackupPath(rapport.clientId()), encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RapportModel getRapportByClientId(int clientId) {
        byte[] data = null;
        String source = "primary";
        try {
            Path primaryPath = getPrimaryPath(clientId);
            if (Files.exists(primaryPath)) {
                data = Files.readAllBytes(primaryPath);
                String content = new String(cryptoService.decrypt(data), StandardCharsets.UTF_8);
                return new RapportModel(clientId, content);
            }
        } catch (Exception e) {
            source = "backup";
        }
        try {
            Path backupPath = getBackupPath(clientId);
            if (Files.exists(backupPath)) {
                data = Files.readAllBytes(backupPath);
                String content = new String(cryptoService.decrypt(data), StandardCharsets.UTF_8);
                System.out.println("[AVERTISSEMENT] Fichier principal indisponible, lecture depuis la sauvegarde.");
                return new RapportModel(clientId, content);
            }
        } catch (Exception e) {
            throw new RuntimeException("Impossible de lire le rapport: fichiers principal et backup corrompus ou inexistants.");
        }
        return null;
    }
}
