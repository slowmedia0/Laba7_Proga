package client.utility;

import common.ExitCodeCommand;
import common.models.*;
import common.exceptions.*;



import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class FieldReaderClient {
    
    private static final Scanner userScanner = new Scanner(System.in);
    
    private static UserHandler userHandler;

    public static void setUserHandler(UserHandler userHandler) {
        FieldReaderClient.userHandler = userHandler;
    }

    
    public static String askFile(){
        try {
            System.out.println("Введите имя файла или его путь");
            if (!userScanner.hasNextLine()) {
                throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
            }
            String data = userScanner.nextLine().trim();
            if (data.trim().split("\\s+").length > 1) {
                throw new WrongAmountOfElementsException("Вы указали больше одного файла, а надо один!");
            }
            return data;
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            System.exit(0);
            return null;
        } catch (WrongAmountOfElementsException e) {
            System.out.println(e.getMessage());
            System.out.println("Повторите попытку ввода");
            return askFile();
        }
    }
    

    
    public static Vehicle askVehicleObject() throws ValidateDataException, NotExistException, FieldReadException {
        try {
            if (!userHandler.isFlagScript()) {
                System.out.println("Для того чтобы заполнить объект типа Vehicle, выполните следующее:");
            }
            return new Vehicle(FieldReaderClient.readFieldName(null), FieldReaderClient.askCoordinates(null, null), FieldReaderClient.readFieldEnginePower(null), FieldReaderClient.readFieldNumberOfWheels(null), FieldReaderClient.readFieldType(null), FieldReaderClient.readFieldFuelType(null));
        } catch (FieldReadException e) {
            throw new FieldReadException("Не удалось инициализировать поля объекта типа Vehicle!");
        }
    }

    
    public static String readFieldName(String argument) throws ValidateDataException, FieldReadException {
        if (!userHandler.isFlagScript() && !userHandler.isFlagReadCollection()) {
            try {
                System.out.println("Введите строку для поля 'name'");
                if (!userScanner.hasNextLine()) {
                    throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
                }
                String name = userScanner.nextLine();
                int maxLenOfName = 1500;
                if (name.length()>maxLenOfName){
                    throw new ValueOutOfBoundsException("Максимальная длина поля 'name' = " + maxLenOfName);
                }
                ValidatorClient.validateNameVehicle(name);
                return name;
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
                userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            
                System.exit(0);
                return null;
            }
            catch (ValueOutOfBoundsException e){
                System.out.println(e.getMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldName(null);
            }
            catch (ValidateDataException e) {
                System.out.println(e.generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldName(null);
            }
        }
        else {
            try {
                String name;
                if (userHandler.isFlagReadCollection()){
                    name = argument;
                    if (name.equals("NULL")) {
                        throw new NotExistException("Отсутствует тэг 'name'! Поле 'name' не инициализировано!");
                    }
                }
                else {
                    name = userHandler.getFields().get(0);
                }
                int maxLenOfName = 1500;
                if (name.length()>maxLenOfName){
                    throw new ValueOutOfBoundsException("Максимальная длина поля 'name' = " + maxLenOfName);
                }
                ValidatorClient.validateNameVehicle(name);
                return name;
            }
            catch (ValueOutOfBoundsException e){
                throw new FieldReadException("Не удалось считать поле 'name'!",e);
            }
            catch (NotExistException | ValidateDataException e){
                throw new FieldReadException("Не удалось считать поле 'name'! Поле 'name' должно представлять собой название транспортного средства!",e);
            }
        }
    }

    
    public static Coordinates askCoordinates(String argX, String argY) throws ValidateDataException, FieldReadException {
        if (!userHandler.isFlagScript() && !userHandler.isFlagReadCollection()) {
            try {
                System.out.println("Для того чтобы заполнить поле 'coordinates', выполните следующее:");
                Long x = readFieldX("");
                Double y = readFieldY("");
                return new Coordinates(x, y);
            }
            catch (FieldReadException e){
                System.out.println(e.generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return askCoordinates(null,null);
            }
        }
        else {
            try {
                Long x;
                Double y;
                Coordinates coordinates = null;
                if (userHandler.isFlagReadCollection()){
                    x = readFieldX(argX);
                    y = readFieldY(argY);
                    coordinates = new Coordinates(x, y);
                }
                else {
                    x = readFieldX("");
                    y = readFieldY("");
                    coordinates = new Coordinates(x,y);
                }
                ValidatorClient.validateCoordinatesVehicle(coordinates);
                return coordinates;
            }
            catch (FieldReadException e){
                throw new FieldReadException("Не удалось считать поле 'coordinates'!",e);
            }
        }
    }

    
    public static Long readFieldX(String argument) throws FieldReadException {
        if (!userHandler.isFlagScript() && !userHandler.isFlagReadCollection()) {
            try {
                System.out.println("Введите целое число для поля 'x'");
                if (!userScanner.hasNextLine()) {
                    throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
                }
                String data = userScanner.nextLine();
                Long x;
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'x' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    x = null;
                } else if (data.trim().isEmpty()) {
                    throw new NumberFormatException("Для поля 'x' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                } else {
                    BigDecimal a;
                    try {
                        data=data.replace(",",".").trim();
                        data=data.replaceAll("\\.0+$","");
                        a = new BigDecimal(data);
                    }
                    catch (NumberFormatException e){
                        throw new NumberFormatException("");
                    }
                    if (a.remainder(BigDecimal.ONE) != BigDecimal.ZERO) {
                        throw new NumberFormatException("Поле 'x' не может быть дробным числом!");
                    }
                    BigInteger b = new BigInteger(data);
                    BigInteger startOfBounds = BigInteger.valueOf(Integer.MIN_VALUE);
                    BigInteger endOfBounds = BigInteger.valueOf(Integer.MAX_VALUE);
                    if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0) {
                        throw new ValueOutOfBoundsException("Поле 'x' должно находиться в диапазоне: " + startOfBounds + "<=x<=" + endOfBounds);
                    }
                    x = Long.valueOf(data);
                }
                ValidatorClient.validateXCoordinates(x);
                return x;
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
                userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            
                System.exit(0);
                return null;
            } catch (WrongAmountOfElementsException | NumberFormatException | ValidateDataException e) {
                System.out.println(new FieldReadException("Не удалось считать поле 'x'! Поле 'x' должно быть целым числом!", e).generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldX(null);
            } catch (ValueOutOfBoundsException e) {
                System.out.println(new FieldReadException("Не удалось считать поле 'x'!", e).generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldX(null);
            }
        } else {
            try {
                String data;
                Long x;
                if (userHandler.isFlagReadCollection()) {
                    data = argument;
                    if (data.equals("NULL")) {
                        throw new NotExistException("Отсутствует тэг 'x'! Поле 'x' не инициализировано!");
                    }
                } else {
                    data = userHandler.getFields().get(1);
                }
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'x' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    x = null;
                } else if (data.trim().isEmpty()) {
                    throw new NumberFormatException("Для поля 'x' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                } else {
                    BigDecimal a;
                    try {
                        data=data.replace(",",".").trim();
                        data=data.replaceAll("\\.0+$","");
                        a = new BigDecimal(data);
                    }
                    catch (NumberFormatException e){
                        throw new NumberFormatException("");
                    }
                    if (a.remainder(BigDecimal.ONE) != BigDecimal.ZERO) {
                        throw new NumberFormatException("Поле 'x' не может быть дробным числом!");
                    }
                    BigInteger b = new BigInteger(data);
                    BigInteger startOfBounds = BigInteger.valueOf(Integer.MIN_VALUE);
                    BigInteger endOfBounds = BigInteger.valueOf(Integer.MAX_VALUE);
                    if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0) {
                        throw new ValueOutOfBoundsException("Поле 'x' должно находиться в диапазоне: " + startOfBounds + "<=x<=" + endOfBounds);
                    }
                    x = Long.valueOf(data);
                }
                ValidatorClient.validateXCoordinates(x);
                return x;
            } catch (WrongAmountOfElementsException | NumberFormatException | ValidateDataException e) {
                throw new FieldReadException("Не удалось считать поле 'x'! Поле 'x' должно быть целым числом!", e);
            } catch (NotExistException | ValueOutOfBoundsException e) {
                throw new FieldReadException("Не удалось считать поле 'x'!", e);
            }
        }
    }

    
    public static Double readFieldY(String argument) throws ValidateDataException, FieldReadException {
        if (!userHandler.isFlagScript() && !userHandler.isFlagReadCollection()) {
            try {
                System.out.println("Введите число для поля 'y'");
                if (!userScanner.hasNextLine()) {
                    throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
                }
                String data = userScanner.nextLine();
                Double y;
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'y' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    y = null;
                } else if (data.trim().isEmpty()) {
                    throw new NumberFormatException("Для поля 'y' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                } else {
                    BigDecimal b;
                    try {
                        data = data.replace(",", ".").trim();
                        b = new BigDecimal(data);
                    }
                    catch (NumberFormatException e){
                        throw new NumberFormatException("");
                    }
                    BigDecimal startOfBounds = BigDecimal.valueOf(-Double.MAX_VALUE);
                    BigDecimal endOfBounds = BigDecimal.valueOf(414);
                    if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0) {
                        throw new ValueOutOfBoundsException("Поле 'y' должно находиться в диапазоне: " + startOfBounds + "<=y<=" + endOfBounds);
                    }
                    y = Double.valueOf(data);
                    BigDecimal visualY = new BigDecimal(String.valueOf(y));
                    visualY = visualY.stripTrailingZeros();
                    if (b.stripTrailingZeros().compareTo(visualY)!=0){
                        System.out.println("Предупреждаем, что число потеряло точность! Вот какое число в действительности считалось для поля 'y': " + y);
                        System.out.println("Если вы хотите изменить число, то введите единицу. Если - нет, то введите 0");
                        String dataNew = userScanner.nextLine().trim();
                        while (!dataNew.equals("0") & !dataNew.equals("1")){
                            System.out.println("Если вы хотите изменить число, то введите единицу. Если - нет, то введите 0");
                            dataNew = userScanner.nextLine().trim();
                        }
                        if (dataNew.trim().equals("1")){
                            return readFieldY(null);
                        }
                    }
                }
                ValidatorClient.validateYCoordinates(y);
                return y;
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
                userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            
                System.exit(0);
                return null;
            } catch (WrongAmountOfElementsException | NumberFormatException | ValidateDataException e) {
                System.out.println(new FieldReadException("Не удалось считать поле 'y'! Поле 'y' должно быть числом!",e).generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldY(null);
            }
            catch (ValueOutOfBoundsException e ){
                System.out.println(new FieldReadException("Не удалось считать поле 'y'!",e).generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldY(null);
            }
        }
        else {
            try {
                String data;
                Double y;
                if (userHandler.isFlagReadCollection()) {
                    data = argument;
                    if (data.equals("NULL")) {
                        throw new NotExistException("Отсутствует тэг 'y'! Поле 'y' не инициализировано!");
                    }
                } else {
                    data = userHandler.getFields().get(2);
                }
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'y' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    y = null;
                } else if (data.trim().isEmpty()) {
                    throw new NumberFormatException("Для поля 'y' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                } else {
                    BigDecimal b;
                    try {
                        data = data.replace(",", ".");
                        b = new BigDecimal(data);
                    }
                    catch (NumberFormatException e){
                        throw new NumberFormatException("");
                    }
                    BigDecimal startOfBounds = BigDecimal.valueOf(-Double.MAX_VALUE);
                    BigDecimal endOfBounds = BigDecimal.valueOf(414);
                    if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0) {
                        throw new ValueOutOfBoundsException("Поле 'y' должно находиться в диапазоне: " + startOfBounds + "<=y<=" + endOfBounds);
                    }
                    y = Double.valueOf(data);
                    BigDecimal visualY = new BigDecimal(String.valueOf(y));
                    visualY = visualY.stripTrailingZeros();
                    if (b.stripTrailingZeros().compareTo(visualY)!=0){
                        System.out.println("Предупреждаем, что число потеряло точность! Вот какое число в действительности считалось для поля 'y': " + y);
                    }
                }
                ValidatorClient.validateYCoordinates(y);
                return y;
            }
            catch (WrongAmountOfElementsException | NumberFormatException | ValidateDataException e) {
                throw new FieldReadException("Не удалось считать поле 'y'! Поле 'y' должно быть числом!",e);
            }
            catch (NotExistException |ValueOutOfBoundsException e ){
                throw new FieldReadException("Не удалось считать поле 'y'!",e);
            }
        }
    }

    
    public static LocalDate readFieldCreationDate(String argument) throws ValidateDataException, FieldReadException {
        try {
            String data = argument;
            LocalDate creationDate;
            if (data.trim().split("\\s+").length>1){
                throw new WrongAmountOfElementsException("Для поля 'creationDate' указано более одного аргумента!");
            }
            if (data.isEmpty()) {
                creationDate = null;
            } else if (data.trim().isEmpty()) {
                throw new NumberFormatException("Для поля 'creationDate' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
            } else if (data.equals("NULL")) {
                creationDate = LocalDate.now();
            } else {
                try {
                    creationDate = LocalDate.parse(argument);
                }
                catch (DateTimeParseException e){
                    throw new IllegalArgumentException("");
                }
            }
            ValidatorClient.validateCreationDateVehicle(creationDate);
            return creationDate;
        }
        catch (WrongAmountOfElementsException | IllegalArgumentException | ValidateDataException e){
            throw new FieldReadException("Не удалось считать поле 'creationDate'! Поле 'creationDate' должно представлять собой дату в формате: yyyy-MM-dd !",e);
        }
    }

    
    public static float readFieldEnginePower(String argument) throws ValidateDataException, FieldReadException {
        if (!userHandler.isFlagScript() && !userHandler.isFlagReadCollection()) {
            try {
                System.out.println("Введите число для поля 'enginePower'");
                if (!userScanner.hasNextLine()) {
                    throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
                }
                String data = userScanner.nextLine();
                float enginePower;
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'enginePower' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    throw new IllegalArgumentException("Поле 'enginePower' не может быть пустой строкой!");
                }
                else if (data.trim().isEmpty()){
                    throw new NumberFormatException("Для поля 'enginePower' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                }
                else {
                    BigDecimal b;
                    try {
                        data = data.replace(",",".").trim();
                        b = new BigDecimal(data);
                    }
                    catch (NumberFormatException e){
                        throw new NumberFormatException("");
                    }
                    BigDecimal startOfBounds = BigDecimal.ZERO;
                    BigDecimal endOfBounds = BigDecimal.valueOf(Float.MAX_VALUE);
                    if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0){
                        throw new ValueOutOfBoundsException("Поле 'enginePower' должно находиться в диапазоне: " + startOfBounds + "<=enginePower<=" + endOfBounds);
                    }
                    

                    enginePower = Float.parseFloat(data);
                    BigDecimal visualEnginePower = new BigDecimal(String.valueOf(enginePower));
                    visualEnginePower = visualEnginePower.stripTrailingZeros();
                    if (b.stripTrailingZeros().compareTo(visualEnginePower)!=0){
                        System.out.println("Предупреждаем, что число потеряло точность! Вот какое число в действительности считалось для поля 'enginePower': " + enginePower);
                        System.out.println("Если вы хотите изменить число, то введите единицу. Если - нет, то введите 0");
                        String dataNew = userScanner.nextLine().trim();
                        while (!dataNew.equals("0") & !dataNew.equals("1")){
                            System.out.println("Если вы хотите изменить число, то введите единицу. Если - нет, то введите 0");
                            dataNew= userScanner.nextLine().trim();
                        }
                        if (dataNew.equals("1")){
                            return readFieldEnginePower(null);
                        }
                    }
                }
                return enginePower;
            }catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
                userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            

                System.exit(0);
                return 0;
            } catch (ValueOutOfBoundsException e) {
                System.out.println(new FieldReadException("Не удалось считать поле 'enginePower'!",e).generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldEnginePower(null);
            }
            catch (WrongAmountOfElementsException | NumberFormatException e) {
                System.out.println(new FieldReadException("Не удалось считать поле 'enginePower'! Поле 'enginePower' должно быть числом!",e).generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldEnginePower(null);
            }
            catch (IllegalArgumentException e) {
                System.out.println(new FieldReadException("Не удалось считать поле 'enginePower'! Поле 'enginePower' должно быть числом!",e).generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldEnginePower(null);
            }
        }
        else {
            try {
                String data;
                if (userHandler.isFlagReadCollection()){
                    data=argument;
                    if(data.equals("NULL")){
                        throw new NotExistException("Отсутствует тэг 'enginePower'! Поле 'enginePower' не инициализировано!");
                    }
                }
                else {
                    data = userHandler.getFields().get(3);
                }
                float enginePower;
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'enginePower' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    throw new IllegalArgumentException("Поле 'enginePower' не может быть пустой строкой!");
                }
                else if (data.trim().isEmpty()){
                    throw new NumberFormatException("Для поля 'enginePower' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                }
                else {
                    BigDecimal b;
                    try {
                        data = data.replace(",",".").trim();
                        b = new BigDecimal(data);
                    }
                    catch (NumberFormatException e){
                        throw new NumberFormatException("");
                    }
                    BigDecimal startOfBounds = BigDecimal.ZERO;
                    BigDecimal endOfBounds = BigDecimal.valueOf(Float.MAX_VALUE);
                    if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0){
                        throw new ValueOutOfBoundsException("Поле 'enginePower' должно находиться в диапазоне: " + startOfBounds + "<=enginePower<=" + endOfBounds);
                    }
                    
                    enginePower = Float.parseFloat(data);
                    BigDecimal visualEnginePower = new BigDecimal(String.valueOf(enginePower));
                    visualEnginePower = visualEnginePower.stripTrailingZeros();
                    if (b.stripTrailingZeros().compareTo(visualEnginePower)!=0){
                        System.out.println("Предупреждаем, что число потеряло точность! Вот какое число в действительности считалось для поля 'enginePower': " + enginePower);
                    }
                }
                return enginePower;
            } catch (NotExistException | ValueOutOfBoundsException e) {
                throw  new FieldReadException("Не удалось считать поле 'enginePower'!",e);
            }
            catch (WrongAmountOfElementsException | NumberFormatException e) {
                throw  new FieldReadException("Не удалось считать поле 'enginePower'! Поле 'enginePower' должно быть числом!",e);
            }
            catch (IllegalArgumentException e) {
                throw  new FieldReadException("Не удалось считать поле 'enginePower'! Поле 'enginePower' должно быть числом!",e);
            }
        }
    }

    
    public static Long readFieldNumberOfWheels(String argument) throws ValidateDataException, FieldReadException {
        if (!userHandler.isFlagScript() && !userHandler.isFlagReadCollection()) {
            try {
                System.out.println("Введите целое число для поля 'numberOfWheels'");
                if (!userScanner.hasNextLine()) {
                    throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
                }
                String data = userScanner.nextLine();
                Long numberOfWheels;
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'numberOfWheels' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    numberOfWheels = null;
                }
                else if (data.trim().isEmpty()){
                    throw new NumberFormatException("Для поля 'numberOfWheels' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                }
                else {
                    BigDecimal a;
                    try {
                        data = data.replace(",",".").trim();
                        data=data.replaceAll("\\.0+$","");
                        a = new BigDecimal(data);
                    }
                    catch (NumberFormatException e){
                        throw new NumberFormatException("");
                    }
                    if (a.remainder(BigDecimal.ONE)!=BigDecimal.ZERO){
                        throw new NumberFormatException("Поле 'numberOfWheels' не может быть дробным числом!");
                    }
                    BigInteger b = new BigInteger(data);
                    BigInteger startOfBounds = BigInteger.ZERO;
                    BigInteger endOfBounds = BigInteger.valueOf(Integer.MAX_VALUE);
                    if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0){
                        throw new ValueOutOfBoundsException("Поле 'numberOfWheels' должно находиться в диапазоне: " + startOfBounds + "<=numberOfWheels<=" + endOfBounds);
                    }
                    numberOfWheels = Long.valueOf(data);
                }
                ValidatorClient.validateNumberOfWheelsVehicle(numberOfWheels);
                return numberOfWheels;
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
                userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            

                System.exit(0);
                return null;
            } catch (ValueOutOfBoundsException e) {
                System.out.println(new FieldReadException("Не удалось считать поле 'numberOfWheels'!",e).generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldNumberOfWheels(null);
            }
            catch (WrongAmountOfElementsException | NumberFormatException | ValidateDataException e) {
                System.out.println(new FieldReadException("Не удалось считать поле 'numberOfWheels'! Поле 'numberOfWheels' должно быть целым числом!",e).generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldNumberOfWheels(null);
            }
        }
        else {
            try {
                String data;
                Long numberOfWheels;
                if (userHandler.isFlagReadCollection()) {
                    data = argument;
                    if (data.equals("NULL")) {
                        throw new NotExistException("Отсутствует тэг 'numberOfWheels'! Поле 'numberOfWheels' не инициализировано!");
                    }
                } else {
                    data = userHandler.getFields().get(4);
                }
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'numberOfWheels' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    numberOfWheels = null;
                } else if (data.trim().isEmpty()) {
                    throw new NumberFormatException("Для поля 'numberOfWheels' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                } else {
                    BigDecimal a;
                    try {
                        data = data.replace(",",".").trim();
                        data=data.replaceAll("\\.0+$","");
                        a = new BigDecimal(data);
                    }
                    catch (NumberFormatException e){
                        throw new NumberFormatException("");
                    }
                    if (a.remainder(BigDecimal.ONE) != BigDecimal.ZERO) {
                        throw new NumberFormatException("Поле 'numberOfWheels' не может быть дробным числом!");
                    }
                    BigInteger b = new BigInteger(data);
                    BigInteger startOfBounds = BigInteger.ZERO;
                    BigInteger endOfBounds = BigInteger.valueOf(Integer.MAX_VALUE);
                    if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0) {
                        throw new ValueOutOfBoundsException("Поле 'numberOfWheels' должно находиться в диапазоне: " + startOfBounds + "<=numberOfWheels<=" + endOfBounds);
                    }
                    numberOfWheels = Long.valueOf(data);
                }
                ValidatorClient.validateNumberOfWheelsVehicle(numberOfWheels);
                return numberOfWheels;
            }
            catch (NotExistException | ValueOutOfBoundsException e) {
                throw  new FieldReadException("Не удалось считать поле 'numberOfWheels'!",e);
            }
            catch (WrongAmountOfElementsException | NumberFormatException | ValidateDataException e) {
                throw  new FieldReadException("Не удалось считать поле 'numberOfWheels'! Поле 'numberOfWheels' должно быть целым числом!",e);
            }
        }
    }

    
    public static VehicleType readFieldType(String argument) throws NotExistException, ValidateDataException, FieldReadException, EnumConstantNotPresentException {
        if (!userHandler.isFlagScript() && !userHandler.isFlagReadCollection()) {
            System.out.println("Введите одно из значений поля 'type': ");
            System.out.println(Arrays.toString(VehicleType.class.getEnumConstants()));
            System.out.println();
            try {
                if (!userScanner.hasNextLine()) {
                    throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
                }
                String data = userScanner.nextLine();
                VehicleType type;
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'type' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    type = null;
                } else if (data.trim().isEmpty()) {
                    throw new IllegalArgumentException("Для поля 'type' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                } else {
                    try {
                        type = VehicleType.valueOf(data.trim());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Несуществующая константа для поля 'type'!");
                    }
                }
                ValidatorClient.validateTypeVehicle(type);
                return type;
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
                userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            

                System.exit(0);
                return null;
            } catch (WrongAmountOfElementsException  | IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldType("");
            } catch (ValidateDataException e) {
                System.out.println(e.generateFullMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldType("");
            }
        }
        else {
            try {
                String data;
                VehicleType type;
                if (userHandler.isFlagReadCollection()) {
                    data = argument;
                    if (data.equals("NULL")) {
                        throw new NotExistException("Отсутствует тэг 'type'! Поле 'type' не инициализировано!");
                    }
                } else {
                    data = userHandler.getFields().get(5);
                }
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'type' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    type = null;
                } else if (data.trim().isEmpty()) {
                    throw new IllegalArgumentException("Для поля 'type' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                } else {
                    try {
                        type = VehicleType.valueOf(data.trim());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Несуществующая константа для поля 'type'!");
                    }
                }
                ValidatorClient.validateTypeVehicle(type);
                return type;
            }
            catch (NotExistException e) {
                throw new FieldReadException("Не удалось считать поле 'type'!",e);
            }
            catch (WrongAmountOfElementsException | ValidateDataException | IllegalArgumentException e) {
                throw new FieldReadException("Не удалось считать поле 'type'! Поле 'type' должно быть одной из констант набора " + Arrays.toString(VehicleType.class.getEnumConstants()) + " !",e);
            }
        }
    }

    
    public static FuelType readFieldFuelType(String argument) throws NotExistException, FieldReadException {
        if (!userHandler.isFlagScript() && !userHandler.isFlagReadCollection()) {
            System.out.println("Введите одно из значений поля 'fuelType': ");
            System.out.println(Arrays.toString(FuelType.class.getEnumConstants()));
            System.out.println();
            try {
                if (!userScanner.hasNextLine()) {
                    throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
                }
                String data = userScanner.nextLine();
                FuelType fuelType;
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'fuelType' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    fuelType = null;
                }
                else if (data.trim().isEmpty()){
                    throw new IllegalArgumentException("Для поля 'fuelType' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                }
                else {
                    try {
                        fuelType = FuelType.valueOf(data.trim());
                    }
                    catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Несуществующая константа для поля 'fuelType'!");
                    }
                }
                return fuelType;
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
                userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            

                System.exit(0);
                return null;
            } catch (WrongAmountOfElementsException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("Повторите попытку ввода");
                return readFieldFuelType("");
            }
        }
        else {
            try {
                String data;
                FuelType fuelType;
                if (userHandler.isFlagReadCollection()) {
                    data = argument;
                    if (data.equals("NULL")) {
                        throw new NotExistException("Отсутствует тэг 'fuelType'! Поле 'fuelType' не инициализировано!");
                    }
                } else {
                    data = userHandler.getFields().get(6);
                }
                if (data.trim().split("\\s+").length>1){
                    throw new WrongAmountOfElementsException("Для поля 'fuelType' указано более одного аргумента!");
                }
                if (data.isEmpty()) {
                    fuelType = null;
                } else if (data.trim().isEmpty()) {
                    throw new IllegalArgumentException("Для поля 'fuelType' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
                } else {
                    try {
                        fuelType = FuelType.valueOf(data.trim());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Несуществующая константа для поля 'fuelType'!");
                    }
                }
                return fuelType;
            }
            catch (NotExistException e) {
                throw new FieldReadException("Не удалось считать поле 'fuelType'!",e);
            }
            catch (WrongAmountOfElementsException | IllegalArgumentException e) {
                throw new FieldReadException("Не удалось считать поле 'fuelType'! Поле 'fuelType' должно быть одной из констант набора " + Arrays.toString(FuelType.class.getEnumConstants()) +" !",e);
            }
        }
    }




    public static Integer askPort(String argument)  {
        try {
            System.out.println("Введите целое число для поля 'port'");
            if (!userScanner.hasNextLine()) {
                throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
            }
            String data = userScanner.nextLine();
            Integer port;
            if (data.trim().split("\\s+").length > 1) {
                throw new WrongAmountOfElementsException("Для поля 'port' указано более одного аргумента!");
            }
            if (data.isEmpty()) {
                throw new NumberFormatException("Поле 'port' не может быть null!");
            } else if (data.trim().isEmpty()) {
                throw new NumberFormatException("Для поля 'port' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
            } else {
                BigDecimal a;
                try {
                    data = data.replace(",", ".").trim();
                    data = data.replaceAll("\\.0+$", "");
                    a = new BigDecimal(data);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("");
                }
                if (a.remainder(BigDecimal.ONE) != BigDecimal.ZERO) {
                    throw new NumberFormatException("Поле 'port' не может быть дробным числом!");
                }
                BigInteger b = new BigInteger(data);
                BigInteger startOfBounds = BigInteger.valueOf(1024);
                BigInteger endOfBounds = BigInteger.valueOf(65535);
                if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0) {
                    throw new ValueOutOfBoundsException("Поле 'port' должно находиться в диапазоне: " + startOfBounds + "<=port<=" + endOfBounds);
                }
                port = Integer.valueOf(data);
            }
            return port;
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            System.exit(0);
            return null;
        } catch (WrongAmountOfElementsException | NumberFormatException e) {
            System.out.println("Не удалось считать поле 'port'! Поле 'port' должно быть целым числом!" + " " + e.getMessage());
            System.out.println("Повторите попытку ввода");
            return askPort(null);
        } catch (ValueOutOfBoundsException e) {
            System.out.println("Не удалось считать поле 'port'!" + " " + e.getMessage());
            System.out.println("Повторите попытку ввода");
            return askPort(null);
        }
    }

    public static Integer readPort(String argument)  {
        try {
            String data = argument;
            Integer port;
            if (data.trim().split("\\s+").length > 1) {
                throw new WrongAmountOfElementsException("Для поля 'port' указано более одного аргумента!");
            }
            if (data.isEmpty()) {
                throw new NumberFormatException("Поле 'port' не может быть null!");
            } else if (data.trim().isEmpty()) {
                throw new NumberFormatException("Для поля 'port' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
            } else {
                BigDecimal a;
                try {
                    data = data.replace(",", ".").trim();
                    data = data.replaceAll("\\.0+$", "");
                    a = new BigDecimal(data);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("");
                }
                if (a.remainder(BigDecimal.ONE) != BigDecimal.ZERO) {
                    throw new NumberFormatException("Поле 'port' не может быть дробным числом!");
                }
                BigInteger b = new BigInteger(data);
                BigInteger startOfBounds = BigInteger.valueOf(1024);
                BigInteger endOfBounds = BigInteger.valueOf(65535);
                if (b.compareTo(startOfBounds) < 0 || b.compareTo(endOfBounds) > 0) {
                    throw new ValueOutOfBoundsException("Поле 'port' должно находиться в диапазоне: " + startOfBounds + "<=port<=" + endOfBounds);
                }
                port = Integer.valueOf(data);
            }
            return port;
        } catch (WrongAmountOfElementsException | NumberFormatException e) {
            System.out.println("Не удалось считать поле 'port'! Поле 'port' должно быть целым числом!" + " " + e.getMessage());
            return askPort(null);
        } catch (ValueOutOfBoundsException e) {
            System.out.println("Не удалось считать поле 'port'!" + " " + e.getMessage());
            return askPort(null);
        }
    }


    public static String askHost(String argument)  {
        try {
            System.out.println("Введите адрес сервера для поля 'host'");
            if (!userScanner.hasNextLine()) {
                throw new NoSuchElementException("Вы использовали Ctrl+D. Ввод прерван.");
            }

            String data = userScanner.nextLine();


            if (data.isEmpty()) {
                throw new IllegalArgumentException("Поле 'host' не может быть пустым!");
            }
            else if (data.trim().isEmpty()) {
                throw new IllegalArgumentException("Для поля 'host' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
            }
            String host = data.trim();

            if (host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1")) {
                return "localhost";
            }

            if (host.length() > 253) {
                throw new ValueOutOfBoundsException("Адрес сервера слишком длинный (максимум 253 символа)!");
            }

            if (host.matches("^\\d+$")) {
                throw new IllegalArgumentException("Host не может состоять только из цифр (это выглядит как порт)!");
            }

             if (!host.matches("^[a-zA-Z0-9.\\-:\\[\\]]+$")) {
                throw new IllegalArgumentException("Адрес сервера содержит недопустимые символы!");
            }

            return host;

        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            System.exit(0);
            return null;

        } catch (IllegalArgumentException | ValueOutOfBoundsException e) {
            System.out.println("Не удалось считать поле 'host'!" + " " + e.getMessage());
            System.out.println("Повторите попытку ввода");
            return askHost(null);   
        }
    }

    public static String readHost(String argument)  {
        try {
            String data = argument;

            if (data.isEmpty()) {
                throw new IllegalArgumentException("Поле 'host' не может быть пустым!");
            }
            else if (data.trim().isEmpty()) {
                throw new IllegalArgumentException("Для поля 'host' была введена последовательность, состоящая из 'пустых символов' (табуляция, пробелы и т.п.)!");
            }
            String host = data.trim();

            if (host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1")) {
                return "localhost";
            }

            if (host.length() > 253) {
                throw new ValueOutOfBoundsException("Адрес сервера слишком длинный (максимум 253 символа)!");
            }

            if (host.matches("^\\d+$")) {
                throw new IllegalArgumentException("Host не может состоять только из цифр (это выглядит как порт)!");
            }

            if (!host.matches("^[a-zA-Z0-9.\\-:\\[\\]]+$")) {
                throw new IllegalArgumentException("Адрес сервера содержит недопустимые символы!");
            }

            return host;

        } catch (IllegalArgumentException | ValueOutOfBoundsException e) {
            System.out.println("Не удалось считать поле 'host'!" + " " + e.getMessage());
            return askHost(null);   
        }
    }

    // ====================== LOGIN / REGISTER ======================

    public static String askLogin() {
        try {
            System.out.print("Введите login: ");
            if (!userScanner.hasNextLine()) {
                throw new NoSuchElementException("Вы использовали Ctrl+D.");
            }
            String login = userScanner.nextLine().trim();

            if (login.isEmpty()) {
                System.out.println("Логин не может быть пустым!");
                return askLogin();
            }
            return login;
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            System.exit(0);
            return null;
        }
    }

    public static String askPassword() {
        try {
            System.out.print("Введите password: ");
            if (!userScanner.hasNextLine()) {
                throw new NoSuchElementException("Вы использовали Ctrl+D.");
            }
            String password = userScanner.nextLine().trim();

            if (password.isEmpty()) {
                System.out.println("Пароль не может быть пустым!");
                return askPassword();
            }
            return password;
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            userHandler.setExitCodeCommandStatus(ExitCodeCommand.CTRL_D);
            System.exit(0);
            return null;
        }
    }

}