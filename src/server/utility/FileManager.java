package server.utility;

import common.exceptions.FieldReadException;
import common.exceptions.NotExistException;
import common.utility.ResponseBuilder;
import common.utility.Serializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import common.models.Coordinates;
import common.models.FuelType;
import common.models.Vehicle;
import common.models.VehicleType;

import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;


public class FileManager {

    public enum ModeOfFileManager {
        READ_COLLECTION,
        WRITE_COLLECTION,
        READ_SCRIPT
    }

    public File getLoadFile() {
        return loadFile;
    }

    private File loadFile;
    private CollectionManager collectionManager;

    public FileManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Stack<Vehicle> readCollection(File loadFile) throws IOException, ParserConfigurationException, SAXException {
        Stack<Vehicle> C = new Stack<>();
        HashSet<String> setOfId = new HashSet<>();
        String nameOfFile;
        if (loadFile == null){
             nameOfFile=FieldReaderServer.askFile();
        }
        else {
            nameOfFile=loadFile.getAbsolutePath();
        }
        while (ValidatorServer.validateNameOfFile(nameOfFile,ModeOfFileManager.READ_COLLECTION)==false){
            nameOfFile=FieldReaderServer.askFile();
        }
        File file = new File(nameOfFile);
        this.loadFile = file;

        List<String> CharsOfVehicle = List.of("id", "name", "coordinates", "creationDate",
                "enginePower", "numberOfWheels", "type", "fuelType");

        ArrayList<ArrayList<String>> multiarray = new ArrayList<>();
        for (int i = 0; i < CharsOfVehicle.size(); i++) {
            multiarray.add(new ArrayList<>());
        }

        int k = 0;


        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
                @Override public void warning(org.xml.sax.SAXParseException e) {}
                @Override public void error(org.xml.sax.SAXParseException e) {}
                @Override public void fatalError(org.xml.sax.SAXParseException e) {}
            });

            Document doc = builder.parse(new InputSource(reader));

            Element collection = (Element) doc.getElementsByTagName("collection").item(0);
            int collectionCount = doc.getElementsByTagName("collection").getLength();

            if (collectionCount == 0) {
                System.out.println("В файле нет коллекции!");
                throw new NoSuchElementException("В файле нет коллекции!");
            }
            if (collectionCount > 1) {
                System.out.println("В файле более одной коллекции!");
                throw new NoSuchElementException("В файле более одной коллекции!");
            }

            int vehicleCount = collection.getElementsByTagName("vehicle").getLength();

            for (int i = 0; i < vehicleCount; i++) {
                Element vehicle = (Element) collection.getElementsByTagName("vehicle").item(i);
                for (int j = 0; j < CharsOfVehicle.size(); j++) {
                    String field = CharsOfVehicle.get(j);
                    NodeList nodes = vehicle.getElementsByTagName(field);

                    if (nodes.getLength() == 0) {
                        multiarray.get(j).add("NULL");
                        if (field.equals("id")) k++;
                    } else if (field.equals("coordinates")) {
                        Element coordinates = (Element) nodes.item(0);
                        String x = coordinates.getElementsByTagName("x").getLength() > 0 ?
                                coordinates.getElementsByTagName("x").item(0).getTextContent() : "NULL";
                        String y = coordinates.getElementsByTagName("y").getLength() > 0 ?
                                coordinates.getElementsByTagName("y").item(0).getTextContent() : "NULL";
                        multiarray.get(j).add(x + "|" + y);
                    } else if (field.equals("id")) {
                        String id = nodes.item(0).getTextContent();
                        if (setOfId.contains(id)) {
                            throw new IllegalArgumentException("Дублирование поля 'id' = " + id + "!");
                        }
                        k++;
                        setOfId.add(id);
                        multiarray.get(j).add(id);
                    } else {
                        String value = nodes.item(0).getTextContent();
                        multiarray.get(j).add(value.isEmpty() ? "" : value);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage() + " : Не удалось найти файл!");
            return readCollection(null);
        } catch (NoSuchElementException | SAXException | ParserConfigurationException | IOException ex) {
            System.out.println("XML-файл не валиден!: " + ex.getMessage());
            return readCollection(null);
        }

        
        for (int j = 0; j < k; j++) {
            ArrayList<String> ar = new ArrayList<>(CharsOfVehicle.size());
            for (int i = 0; i < CharsOfVehicle.size(); i++) {
                ar.add(multiarray.get(i).get(j));
            }

            try {
                Integer id = FieldReaderServer.readFieldId(ar.get(CharsOfVehicle.indexOf("id")), collectionManager);
                String name = FieldReaderServer.readFieldName(ar.get(CharsOfVehicle.indexOf("name")));
                Coordinates coordinates = FieldReaderServer.askCoordinates(
                        ar.get(CharsOfVehicle.indexOf("coordinates")).split("\\|")[0],
                        ar.get(CharsOfVehicle.indexOf("coordinates")).split("\\|")[1]);
                LocalDate creationDate = FieldReaderServer.readFieldCreationDate(ar.get(CharsOfVehicle.indexOf("creationDate")));
                float enginePower = FieldReaderServer.readFieldEnginePower(ar.get(CharsOfVehicle.indexOf("enginePower")));
                Long numberOfWheels = FieldReaderServer.readFieldNumberOfWheels(ar.get(CharsOfVehicle.indexOf("numberOfWheels")));
                VehicleType type = FieldReaderServer.readFieldType(ar.get(CharsOfVehicle.indexOf("type")));
                FuelType fuelType = FieldReaderServer.readFieldFuelType(ar.get(CharsOfVehicle.indexOf("fuelType")));

                C.add(new Vehicle(id, name, coordinates, creationDate, enginePower, numberOfWheels, type, fuelType));
            } catch (FieldReadException e) {
                System.out.println("Не удалось считать объект Vehicle! -> " + e.generateFullMessage());
            } catch (Exception e) {
                System.out.println("Не удалось считать объект Vehicle! -> " + e.getMessage());
            }
        }
        return C;
    }

    public boolean writeCollection() {
        Stack<Vehicle> C = collectionManager.getCollection();
        File file = this.loadFile;

        StringBuilder xml = new StringBuilder();
        xml.append("<collection>\n");

        for (Vehicle v : C) {
            xml.append("  <vehicle>\n");
            xml.append("    <id>").append(v.getId()).append("</id>\n");
            xml.append("    <name>").append(v.getName()).append("</name>\n");
            xml.append("    <coordinates>\n");
            xml.append("      <x>").append(v.getCoordinates().getX()).append("</x>\n");
            xml.append("      <y>").append(v.getCoordinates().getY()).append("</y>\n");
            xml.append("    </coordinates>\n");
            xml.append("    <creationDate>").append(v.getCreationDate()).append("</creationDate>\n");
            xml.append("    <enginePower>").append(v.getEnginePower()).append("</enginePower>\n");
            xml.append("    <numberOfWheels>").append(v.getNumberOfWheels()).append("</numberOfWheels>\n");
            xml.append("    <type>").append(v.getType()).append("</type>\n");

            if (v.getFuelType() != null) {
                xml.append("    <fuelType>").append(v.getFuelType()).append("</fuelType>\n");
            } else {
                xml.append("    <fuelType></fuelType>\n");
            }
            xml.append("  </vehicle>\n");
        }
        xml.append("</collection>\n");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            if (xml.length() == 0) {
                throw new IllegalArgumentException("Записываемые данные пусты!");
            }
            fos.write(xml.toString().getBytes("UTF-8"));
            ResponseBuilder.appendSuccess("Коллекция успешно сохранена в файл " + loadFile.getAbsolutePath());
            System.out.println("Коллекция успешно сохранена в файл " + loadFile.getAbsolutePath());
            return true;
        } catch (FileNotFoundException e) {
            ResponseBuilder.appendLn(e.getMessage() + " : Не удалось найти файл!");
            System.out.println(e.getMessage() + " : Не удалось найти файл!");
            return false;
        } catch (IllegalArgumentException e) {
            ResponseBuilder.appendLn(e.getMessage());
            System.out.println(e.getMessage());
            return false;
        } catch (IOException e) {
            ResponseBuilder.appendLn(e.getMessage() + " : Непредвиденная ошибка!");
            System.out.println(e.getMessage() + " : Непредвиденная ошибка!");
            return false;
        }
    }

}