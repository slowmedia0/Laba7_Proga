package client.utility;

import common.exceptions.NotExistException;
import common.exceptions.ValidateDataException;
import common.models.*;

import java.io.File;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Stack;


public class ValidatorClient {

    
    public static boolean validateCoordinatesVehicle(Coordinates coordinates) throws ValidateDataException {
        try {
            if (coordinates==null){
                throw new NullPointerException("Поле 'coordinates' не может быть null!");
            }
            return true;
        }
        catch (NullPointerException e){
            throw new ValidateDataException("Поле 'coordinates' не валидно!",e);
        }
    }

    
    public static boolean validateXCoordinates(Long x) throws ValidateDataException {
        try {
            if (x==null){
                throw new NullPointerException("Поле 'x' не может быть null!");
            }
            return true;
        }
        catch (NullPointerException e){
            throw new ValidateDataException("Поле 'x' не валидно!",e);
        }
    }

    
    public static boolean validateYCoordinates(Double y) throws ValidateDataException {
        try {
            if (y==null){
                throw new NullPointerException("Поле 'y' не может быть null!");
            }
            return true;
        }
        catch (NullPointerException e){
            throw new ValidateDataException("Поле 'y' не валидно!",e);
        }
    }

    
    public static boolean validateIdVehicle(Integer id, Stack<Vehicle> C) throws ValidateDataException {
        try {
            if (id==null){
                throw new NullPointerException("Поле 'id' не может быть null!");
            }
            int dub=0;
            for (int i = 0; i < C.size(); i++) {
                if (C.get(i).getId().equals(id)) {
                    dub+=1;
                }
            }
            if (dub>1) {
                throw new IllegalArgumentException("Поле 'id' должно быть уникальным!");
            }
            return true;
        }
        catch (NullPointerException | IllegalStateException e){
            throw new ValidateDataException("Поле 'id' не валидно!",e);
        }
    }

    public static boolean validateIdVehicle2(Integer id) throws ValidateDataException {
        try {
            if (id==null){
                throw new NullPointerException("Поле 'id' не может быть null!");
            }
            return true;
        }
        catch (NullPointerException | IllegalStateException e){
            throw new ValidateDataException("Поле 'id' не валидно!",e);
        }
    }

    
    public static boolean validateIdVehicle(Integer id) throws ValidateDataException {
        try {
            if (id==null){
                throw new NullPointerException("Поле 'id' не может быть null!");
            }
            return true;
        }
        catch (NullPointerException e){
            throw new ValidateDataException("Поле 'id' не валидно!",e);
        }
    }

    
    public static boolean validateNameVehicle(String name) throws ValidateDataException {
        try{
            if (name.isEmpty()){
                throw new NullPointerException("Поле 'name' не может быть null!");
            }
            if (name.trim().isEmpty()){
                throw new IllegalArgumentException("Для поля 'name' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
            }
            return true;
        }
        catch (NullPointerException | IllegalArgumentException e){
            throw new ValidateDataException("Поле 'name' не валидно!",e);
        }
    }

    
    public static boolean validateCreationDateVehicle(LocalDate creationDate) throws ValidateDataException {
        try {
            if (creationDate==null){
                throw new NullPointerException("Полe 'creationDate' не может быть null!");
            }
            return true;
        }
        catch (NullPointerException e){
            throw new ValidateDataException("Поле 'creationDate' не валидно!",e);
        }
    }

    
    public static boolean validateNumberOfWheelsVehicle(Long numberOfWheels) throws ValidateDataException {
        try {
            if (numberOfWheels==null){
                throw new NullPointerException("Поле 'numberOfWheels' не может быть null!");
            }
            return true;
        }
        catch (NullPointerException e){
            throw new ValidateDataException("Поле 'numberOfWheels' не валидно!",e);
        }
    }

    
    public static boolean validateTypeVehicle(VehicleType type) throws ValidateDataException {
        try {
            if (type==null){
                throw new NullPointerException("Поле 'type' не может быть null!");
            }
            return true;
        }
        catch (NullPointerException e) {
            throw new ValidateDataException("Поле 'type' не валидно!",e);
        }
    }

    
    public static boolean validateNameOfFile(String nameOfFile, FileManagerClient.ModeOfFileManager modeOfFileManager){
        File file = new File(nameOfFile);
        switch (modeOfFileManager) {
            case READ_COLLECTION:
                try {
                    if (file.exists()==false){
                        throw new NotExistException("Не удалось найти файл '" + nameOfFile + "'! Проверьте, что он действительно существует или что вы корректно указали файл!");
                    }
                    if (file.isDirectory()){
                        throw new IllegalArgumentException("Вы указали директорию, а надо файл формата xml!");
                    }
                    if (nameOfFile.contains(".xml")==false){
                        throw new IllegalArgumentException("Файл должен быть формата xml!");
                    }
                    if (file.canRead()==false){
                        throw new AccessDeniedException(file.getAbsolutePath(),null,"Нет права на чтения файла!");
                    }
                    if (file.canWrite()==false){
                        throw new AccessDeniedException(file.getAbsolutePath(),null,"Нет права на запись в файл!");
                    }
                    if (file.length()==0){
                        throw new IllegalArgumentException("Файл пуст!");
                    }
                    return true;
                }
                catch (NotExistException e){
                    System.out.println(e.getMessage());
                    return false;
                }
                catch (AccessDeniedException e){
                    System.out.println(e.getMessage());
                    return false;
                }
                catch (IllegalArgumentException e){
                    System.out.println(e.getMessage());
                    return false;
                }
            case READ_SCRIPT:
                try {
                    if (file.exists()==false){
                        throw new NotExistException("Не удалось найти файл '" + nameOfFile + "'! Проверьте, что он действительно существует или что вы корректно указали файл!");
                    }
                    if (file.isDirectory()){
                        throw new IllegalArgumentException("Вы указали директорию, а надо файл!");
                    }
                    if (file.canRead()==false){
                        throw new AccessDeniedException(file.getAbsolutePath(),null,"Нет права на чтения файла-скрипта!");
                    }
                    if (file.length()==0){
                        throw new IllegalArgumentException("Файл пуст!");
                    }
                    return true;
                }
                catch (NotExistException e){
                    System.out.println(e.getMessage());
                    return false;
                }
                catch (AccessDeniedException e){
                    System.out.println(e.getMessage());
                    return false;
                }
                catch (IllegalArgumentException e){
                    System.out.println(e.getMessage());
                    return false;
                }
        }
        return false;
    }
}