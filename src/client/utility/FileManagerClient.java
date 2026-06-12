package client.utility;

import java.io.*;
import java.util.*;


public class FileManagerClient {
    static public enum ModeOfFileManager {
        READ_COLLECTION,
        WRITE_COLLECTION,
        READ_SCRIPT
    }
    private String nameOfFile;
    private String nameOfLoadFile;
    public void setNameOfFile(String nameOfFile) {
        this.nameOfFile = nameOfFile;
    }


    public ArrayList<String> readScript(String argument) {
        ArrayList<String> commands = new ArrayList<>();
        String nameOfFile = argument;
        File file = new File(nameOfFile);
        if (ValidatorClient.validateNameOfFile(nameOfFile, ModeOfFileManager.READ_SCRIPT)==false)
        {
            return null;
        }
        
        try (BufferedReader in = new BufferedReader(new FileReader(nameOfFile))) {
            String st;
            while ((st = in.readLine()) != null) {
                commands.add(st);
            }
            return commands;
        }
        catch (FileNotFoundException e){
            System.out.println(e.getMessage() + " : Не удалось найти файл! Проверьте, что он действительно существует или что вы корректно указали файл!");
            return null;
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + " : Непредвиденная ошибка! Возможно файл используется уже кем-то.");
            return null;
        }
    }
}