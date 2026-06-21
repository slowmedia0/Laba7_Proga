package server.database;

import common.models.*;
import server.utility.CollectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

public class VehicleDAO {

    
    public static List<Vehicle> loadAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = """
            SELECT v.*, u.login as owner_login 
            FROM vehicles v 
            LEFT JOIN users u ON v.owner_id = u.id 
            ORDER BY v.id
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapToVehicle(rs));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки коллекции из БД: " + e.getMessage());
        }
        return vehicles;
    }

    private static Vehicle mapToVehicle(ResultSet rs) throws SQLException {
        Coordinates coordinates = new Coordinates(rs.getLong("x"), rs.getDouble("y"));

        Vehicle vehicle = new Vehicle(
                rs.getString("name"),
                coordinates,
                rs.getFloat("engine_power"),
                rs.getLong("number_of_wheels"),
                VehicleType.valueOf(rs.getString("type")),
                rs.getString("fuel_type") != null ? FuelType.valueOf(rs.getString("fuel_type")) : null
        );

        vehicle.setId(rs.getInt("id"));
        vehicle.setCreationDate(rs.getDate("creation_date").toLocalDate());

        try {
            Date lastUpdate = rs.getDate("last_update_date");
            if (lastUpdate != null) {
                vehicle.setLastUpdateDate(lastUpdate.toLocalDate());
            }
        } catch (Exception ignored) {}

        vehicle.setOwnerId(rs.getLong("owner_id"));
        vehicle.setOwnerLogin(rs.getString("owner_login"));

        return vehicle;
    }

    
    public static boolean add(Vehicle vehicle, Long ownerId) {
        if (vehicle == null || ownerId == null) return false;

        String sql = """
        INSERT INTO vehicles (owner_id, name, x, y, creation_date, engine_power, 
                              number_of_wheels, type, fuel_type)
        VALUES (?, ?, ?, ?, CURRENT_DATE, ?, ?, ?, ?)
        RETURNING id
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ownerId);
            ps.setString(2, vehicle.getName());
            ps.setDouble(3, vehicle.getCoordinates().getX());
            ps.setDouble(4, vehicle.getCoordinates().getY());
            ps.setInt(5, (int) vehicle.getEnginePower());
            ps.setInt(6, vehicle.getNumberOfWheels().intValue());
            ps.setObject(7, vehicle.getType().name(), Types.OTHER);
            if (vehicle.getFuelType() != null) {
                ps.setObject(8, vehicle.getFuelType().name(), Types.OTHER);
            } else {
                ps.setNull(8, Types.OTHER);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt("id");
                    vehicle.setId(newId);        
                    System.out.println(" Добавлен объект с id = " + newId);
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка добавления в БД: " + e.getMessage());
        }
        return false;
    }

    
    public static boolean update(Integer id, Vehicle vehicle, Long ownerId) {
        if (id == null || vehicle == null || ownerId == null) return false;

        String sql = """
            UPDATE vehicles 
            SET name = ?, x = ?, y = ?, engine_power = ?, 
                number_of_wheels = ?, type = ?, fuel_type = ?, 
                last_update_date = CURRENT_DATE
            WHERE id = ? AND owner_id = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehicle.getName());
            ps.setDouble(2, vehicle.getCoordinates().getX());
            ps.setDouble(3, vehicle.getCoordinates().getY());
            ps.setFloat(4, vehicle.getEnginePower());
            ps.setLong(5, vehicle.getNumberOfWheels());
            ps.setObject(6, vehicle.getType().name(), Types.OTHER);

            if (vehicle.getFuelType() != null) {
                ps.setObject(7, vehicle.getFuelType().name(), Types.OTHER);
            } else {
                ps.setNull(7, Types.OTHER);
            }

            ps.setInt(8, id);
            ps.setLong(9, ownerId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка обновления в БД: " + e.getMessage());
            return false;
        }
    }

    
    public static boolean removeById(Integer id, Long ownerId) {
        if (id == null || ownerId == null) return false;

        String sql = "DELETE FROM vehicles WHERE id = ? AND owner_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setLong(2, ownerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка удаления из БД: " + e.getMessage());
            return false;
        }
    }

    
    public static boolean clear(Long ownerId) {
        if (ownerId == null) return false;

        String sql = "DELETE FROM vehicles WHERE owner_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ownerId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка очистки БД: " + e.getMessage());
            return false;
        }
    }


    
    public static boolean removeGreater(Vehicle vehicle, Long ownerId) {
        if (vehicle == null || ownerId == null) return false;

        String sql = """
            DELETE FROM vehicles 
            WHERE owner_id = ? 
              AND (name > ? 
               OR engine_power > ? 
               OR number_of_wheels > ?)
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ownerId);
            ps.setString(2, vehicle.getName());
            ps.setFloat(3, vehicle.getEnginePower());
            ps.setLong(4, vehicle.getNumberOfWheels());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Ошибка remove_greater в БД: " + e.getMessage());
            return false;
        }
    }


    
    
    public static boolean saveAll(CopyOnWriteArrayList<Vehicle> vehicles) {
        String currentLogin = CollectionManager.getCurrentUserLogin();
        if (currentLogin == null || currentLogin.isEmpty()) {
            System.out.println(" saveAll: currentUserLogin is null");
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            
            String updateSql = """
                UPDATE vehicles 
                SET name = ?, x = ?, y = ?, engine_power = ?, 
                    number_of_wheels = ?, type = ?, fuel_type = ?, 
                    last_update_date = CURRENT_DATE
                WHERE id = ? AND owner_id = (SELECT id FROM users WHERE login = ?)
                """;

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                for (Vehicle v : vehicles) {
                    if (v.getId() != null && currentLogin.equals(v.getOwnerLogin())) {
                        ps.setString(1, v.getName());
                        ps.setDouble(2, v.getCoordinates().getX());
                        ps.setDouble(3, v.getCoordinates().getY());
                        ps.setFloat(4, v.getEnginePower());
                        ps.setLong(5, v.getNumberOfWheels());
                        ps.setObject(6, v.getType().name(), Types.OTHER);

                        if (v.getFuelType() != null) {
                            ps.setObject(7, v.getFuelType().name(), Types.OTHER);
                        } else {
                            ps.setNull(7, Types.OTHER);
                        }

                        ps.setInt(8, v.getId());
                        ps.setString(9, currentLogin);
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }

            
            String insertSql = """
                INSERT INTO vehicles 
                (owner_id, name, x, y, creation_date, engine_power, 
                 number_of_wheels, type, fuel_type)
                VALUES 
                ((SELECT id FROM users WHERE login = ?), ?, ?, ?, CURRENT_DATE, ?, ?, ?, ?)
                RETURNING id
                """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Vehicle v : vehicles) {
                    if (v.getId() == null && currentLogin.equals(v.getOwnerLogin())) {
                        ps.setString(1, currentLogin);
                        ps.setString(2, v.getName());
                        ps.setDouble(3, v.getCoordinates().getX());
                        ps.setDouble(4, v.getCoordinates().getY());
                        ps.setFloat(5, v.getEnginePower());
                        ps.setLong(6, v.getNumberOfWheels());
                        ps.setObject(7, v.getType().name(), Types.OTHER);

                        if (v.getFuelType() != null) {
                            ps.setObject(8, v.getFuelType().name(), Types.OTHER);
                        } else {
                            ps.setNull(8, Types.OTHER);
                        }

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                v.setId(rs.getInt("id"));
                                System.out.println(" Новый объект добавлен с ID = " + v.getId());
                            }
                        }
                    }
                }
            }

            conn.commit();
            System.out.println(" Коллекция успешно синхронизирована (существующие ID сохранены)");
            return true;

        } catch (Exception e) {
            System.out.println(" Ошибка saveAll: " + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }

    
    
    public static void resetSequence() {
        String sql = """
            SELECT setval('vehicles_id_seq', 
                COALESCE((SELECT MAX(id) FROM vehicles), 1)
            );
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();

        } catch (SQLException e) {
            System.out.println("Не удалось сбросить sequence: " + e.getMessage());
        }
    }
}