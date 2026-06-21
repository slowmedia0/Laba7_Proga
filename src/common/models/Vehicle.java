package common.models;

import java.io.Serializable;
import java.time.LocalDate;

public class Vehicle implements Comparable<Vehicle>, Serializable {
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private java.time.LocalDate creationDate;
    private java.time.LocalDate lastUpdateDate = null;
    private float enginePower;
    private Long numberOfWheels;
    private VehicleType type;
    private FuelType fuelType;

    
    private Long ownerId;
    private String ownerLogin;

    

    public Vehicle(Integer id, String name, Coordinates coordinates, java.time.LocalDate creationDate,
                   float enginePower, Long numberOfWheels, VehicleType type, FuelType fuelType) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.enginePower = enginePower;
        this.numberOfWheels = numberOfWheels;
        this.type = type;
        this.fuelType = fuelType;
    }

    public Vehicle(String name, Coordinates coordinates, float enginePower,
                   Long numberOfWheels, VehicleType type, FuelType fuelType) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDate.now();
        this.enginePower = enginePower;
        this.numberOfWheels = numberOfWheels;
        this.type = type;
        this.fuelType = fuelType;
    }

    @Override
    public int compareTo(Vehicle element) {
        return this.name.compareTo(element.getName());
    }

    @Override
    public String toString() {
        return "Vehicle {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", enginePower=" + enginePower +
                ", numberOfWheels=" + numberOfWheels +
                ", type=" + type +
                ", fuelType=" + fuelType +
                ", ownerId=" + ownerId +
                ", ownerLogin='" + ownerLogin + '\'' +
                '}';
    }

    public void getAllChar() {
        System.out.println();
        System.out.println("id: " + this.id);
        System.out.println("name: " + this.name);
        System.out.println("coordinates: x=" + this.coordinates.getX() + "; y=" + this.coordinates.getY());
        System.out.println("creationDate: " + this.creationDate);
        System.out.println("lastUpdateDate: " + this.lastUpdateDate);
        System.out.println("enginePower: " + this.enginePower);
        System.out.println("numberOfWheels: " + this.numberOfWheels);
        System.out.println("type: " + this.type);
        System.out.println("fuelType: " + this.fuelType);
        System.out.println("ownerId: " + this.ownerId);
        System.out.println("ownerLogin: " + this.ownerLogin);
    }

    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setFakeCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDate lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public float getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(float enginePower) {
        this.enginePower = enginePower;
    }

    public Long getNumberOfWheels() {
        return numberOfWheels;
    }

    public void setNumberOfWheels(Long numberOfWheels) {
        this.numberOfWheels = numberOfWheels;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public LocalDate generateCreationDate() {
        return LocalDate.now();
    }
}