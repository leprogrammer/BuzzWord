package data;

import apptemplate.AppTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import components.AppDataComponent;
import components.AppFileComponent;
import ui.AppMessageDialogSingleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static settings.InitializationParameters.APP_WORKDIR_PATH;

/**
 * @author Tejas
 */
public class AccountLoader implements AppFileComponent{

    private ObjectMapper objectMapper;
    private URL workDirURL = AppTemplate.class.getClassLoader().getResource(APP_WORKDIR_PATH.getParameter());

    public AccountLoader(){

    }

    public boolean checkAccountNameValid(String username){
        for(int i = 0; i < username.length(); i++) {
            if (!Character.isLetterOrDigit(username.charAt(i)))
                return false;
        }
        return true;
    }

    public boolean checkAccountExists(String username){
        try {
            if (workDirURL == null)
                throw new FileNotFoundException("Work folder not found under resources.");


            String accountLocation = new File(workDirURL.getFile()).toString() + "\n" + username + ".json";
            File accountFile = new File(accountLocation);
            if(!accountFile.exists()){
                return true;
            }
            return false;
        }
        catch(IOException e){
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show("Account File Error", "There was an error while trying to validate your account.");
        }
        return false;
    }

    public AccountData createNewAccount(AccountData account, String username, String password) throws IOException {
        if (workDirURL == null)
            throw new FileNotFoundException("Work folder not found under resources.");

        File initialDir = new File(workDirURL.getPath());
        File accountFile = new File(initialDir.getAbsolutePath() + "\\" + username + ".json");
        accountFile.createNewFile();

        account.reset();
        account.setUsername(username);
        account.setAndEncryptPassword(password);
        saveData(account, Paths.get(accountFile.getAbsolutePath()));

        return account;
    }

    public AccountData loadAccount(AccountData account, String username, String password) throws LoginException, IOException, NoSuchAlgorithmException {
        if (workDirURL == null)
            throw new FileNotFoundException("Work folder not found under resources.");

        File initialDir = new File(workDirURL.getPath());
        File accountFile = new File(initialDir.getAbsolutePath() + "\\" + username + ".json");

        if(!accountFile.exists())
            throw new LoginException("Account does not exist.");

        account.reset();
        loadData(account, Paths.get(accountFile.getAbsolutePath()));

        MessageDigest encrypt = MessageDigest.getInstance("MD5");
        encrypt.update(password.getBytes());

        byte[] byteArray = encrypt.digest();
        String inputPassword = new BigInteger(1, byteArray).toString(16);

        if(!account.getPassword().equals(inputPassword))
            throw new LoginException("Incorrect Password.");
        return account;
    }

    public void saveAccount(AccountData accountData) throws IOException{
        if (workDirURL == null)
            throw new FileNotFoundException("Work folder not found under resources.");

        File initialDir = new File(workDirURL.getPath());
        File accountFile = new File(initialDir.getAbsolutePath() + "\\" + accountData.getUsername() + ".json");

        saveData(accountData, Paths.get(accountFile.getAbsolutePath()));
    }

    @Override
    public void saveData(AppDataComponent data, Path filePath) throws IOException {
        String path = filePath.toString();
        if(!path.endsWith(".json"))
            path = filePath.toString() + ".json";
        File output = new File(path);
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.enable(SerializationFeature.CLOSE_CLOSEABLE);

        objectMapper.writeValue(output, data);
    }

    @Override
    public void loadData(AppDataComponent data, Path filePath) throws IOException {
        String path = filePath.toString();
        if(!path.endsWith(".json"))
            path = filePath.toString() + ".json";
        objectMapper = new ObjectMapper();
        File output = new File(path);
        JsonNode node = objectMapper.readValue(output, JsonNode.class);

        JsonNode username = node.get("username");
        JsonNode password = node.get("password");
        JsonNode highestLevelDictionary = node.get("highestLevelDictionary");
        JsonNode highestLevelAnimal = node.get("highestLevelAnimal");
        JsonNode highestLevelPlaces = node.get("highestLevelPlaces");
        JsonNode highestLevelNames = node.get("highestLevelNames");
        JsonNode highScores = node.get("highScores");

        if(username == null || password == null || highestLevelAnimal == null || highestLevelDictionary == null
                || highestLevelNames == null || highestLevelPlaces == null || highScores == null)
            throw new IOException("Selected file does not have correct format.");

        int[][] scores = new int[4][8];
        int x = 0, y = 0;
        for(JsonNode i : highScores){
            y = 0;
            for(JsonNode j: i){
                scores[x][y] = j.asInt();
                y++;
            }
            x++;
        }

        ((AccountData) data).setUsername(username.asText());
        ((AccountData) data).setPassword(password.asText());
        ((AccountData) data).setHighestLevelAnimal(highestLevelAnimal.asInt());
        ((AccountData) data).setHighestLevelDictionary(highestLevelDictionary.asInt());
        ((AccountData) data).setHighestLevelNames(highestLevelNames.asInt());
        ((AccountData) data).setHighestLevelPlaces(highestLevelPlaces.asInt());
        ((AccountData) data).setHighScores(scores);
    }

    @Override
    public void exportData(AppDataComponent data, Path filePath) throws IOException {

    }
}
