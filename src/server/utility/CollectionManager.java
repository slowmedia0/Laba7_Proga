package server.utility;

import common.exceptions.NotExistException;
import common.models.Vehicle;
import common.utility.ResponseBuilder;
import server.database.DatabaseManager;
import server.database.UserDAO;
import server.database.VehicleDAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class CollectionManager {

    private Stack<Vehicle> C = new Stack<>();
    private LocalDate creationDate;
    private ArrayList<Integer> arrayId = new ArrayList<>();
    private Integer recentId = 0;

    private static final ReentrantLock lock = new ReentrantLock();
    private static String currentUserLogin = null;

    // ====================== ЛР7 ======================

    public void loadFromDatabase() {
        lock.lock();
        try {
            C.clear();
            arrayId.clear();

            List<Vehicle> vehiclesFromDb = VehicleDAO.loadAll();

            System.out.println("Загружаем " + vehiclesFromDb.size() + " объектов из БД...");

            C.addAll(vehiclesFromDb);

            initializeArrayId();

            if (!C.isEmpty()) {
                this.creationDate = C.get(0).getCreationDate();
            }

            System.out.println("✅ Успешно загружено " + C.size() + " объектов из PostgreSQL.");

        } catch (Exception e) {
            System.err.println("Критическая ошибка загрузки из БД: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Добавление нового объекта
     */
    public static boolean add(Vehicle vehicle, Long ownerId) {
        if (vehicle == null || ownerId == null || vehicle.getCoordinates() == null) {
            System.err.println("VehicleDAO.add: неверные входные данные");
            return false;
        }

        String sql = """
            INSERT INTO vehicles 
            (owner_id, name, x, y, creation_date, engine_power, 
             number_of_wheels, type, fuel_type)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ownerId);
            ps.setString(2, vehicle.getName());
            ps.setDouble(3, vehicle.getCoordinates().getX());
            ps.setDouble(4, vehicle.getCoordinates().getY());

            // Правильная конвертация LocalDate → java.sql.Date
            java.sql.Date sqlDate = vehicle.getCreationDate() != null
                    ? java.sql.Date.valueOf(vehicle.getCreationDate())
                    : java.sql.Date.valueOf(java.time.LocalDate.now());

            ps.setDate(5, sqlDate);

            ps.setFloat(6, vehicle.getEnginePower());
            ps.setLong(7, vehicle.getNumberOfWheels());
            ps.setObject(8, vehicle.getType().name(), Types.OTHER);

            if (vehicle.getFuelType() != null) {
                ps.setObject(9, vehicle.getFuelType().name(), Types.OTHER);
            } else {
                ps.setNull(9, Types.OTHER);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vehicle.setId(rs.getInt("id"));
                    System.out.println("✅ Объект успешно добавлен в БД. ID = " + vehicle.getId());
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Ошибка добавления в БД: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Обновление объекта — только если владелец совпадает
     */
    public static boolean update(Integer id, Vehicle vehicle, Long ownerId) {
        if (id == null || vehicle == null || ownerId == null || vehicle.getCoordinates() == null) {
            System.err.println("VehicleDAO.update: неверные входные данные");
            return false;
        }

        String sql = """
            UPDATE vehicles 
            SET name = ?, 
                x = ?, 
                y = ?, 
                engine_power = ?, 
                number_of_wheels = ?, 
                type = ?, 
                fuel_type = ?, 
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

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Объект id=" + id + " успешно обновлён в БД");
                return true;
            } else {
                System.out.println("⚠️ Объект id=" + id + " не найден или нет прав на обновление");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Ошибка обновления в БД: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Обновление элемента — сначала в БД, потом в памяти (ЛР7)
     */
    /**
     * Добавление нового объекта — ID приходит из БД
     */
    /**
     * Добавление — только после успеха в БД
     */
    public Stack<Vehicle> addToCollection(Vehicle vehicle) {
        if (vehicle == null) return C;

        Long ownerId = getCurrentUserId();
        if (ownerId == null) {
            ResponseBuilder.append("Ошибка: пользователь не авторизован");
            return C;
        }

        if (VehicleDAO.add(vehicle, ownerId)) {
            lock.lock();
            try {
                vehicle.setOwnerId(ownerId);
                vehicle.setOwnerLogin(currentUserLogin);
                C.add(vehicle);
                initializeArrayId();
                ResponseBuilder.append("Элемент успешно добавлен в коллекцию. Присвоен id = " + vehicle.getId());

                VehicleDAO.resetSequence();   // ← сброс
            } finally {
                lock.unlock();
            }
        } else {
            ResponseBuilder.append("Ошибка при добавлении в базу данных");
        }
        return C;
    }

    /**
     * Обновление элемента — сначала в БД, потом в памяти (ЛР7)
     */
    public Stack<Vehicle> updateElementById(Integer id, Vehicle newVehicle) {
        if (newVehicle == null || id == null) {
            ResponseBuilder.append("Неверные данные для обновления");
            return C;
        }

        Long ownerId = getCurrentUserId();
        if (ownerId == null) {
            ResponseBuilder.append("Ошибка: пользователь не авторизован");
            return C;
        }

        if (VehicleDAO.update(id, newVehicle, ownerId)) {
            lock.lock();
            try {
                for (int i = 0; i < C.size(); i++) {
                    if (C.get(i).getId().equals(id)) {
                        newVehicle.setId(id);
                        newVehicle.setOwnerId(ownerId);
                        newVehicle.setOwnerLogin(currentUserLogin);
                        newVehicle.setCreationDate(C.get(i).getCreationDate());
                        newVehicle.setLastUpdateDate(LocalDate.now());
                        C.set(i, newVehicle);
                        break;
                    }
                }
                initializeArrayId();
                ResponseBuilder.append("Элемент с id = " + id + " успешно обновлён");

                VehicleDAO.resetSequence();   // ← сброс
            } finally {
                lock.unlock();
            }
        } else {
            ResponseBuilder.append("Ошибка при обновлении элемента (нет прав или объект не найден)");
        }
        return C;
    }

    /**
     * Сохраняет всю текущую коллекцию в БД (полная синхронизация)
     */
    public boolean saveToDatabase() {
        lock.lock();
        try {
            System.out.println("💾 Сохранение коллекции в БД...");
            boolean result = VehicleDAO.saveAll(C);
            if (result) {
                VehicleDAO.resetSequence();   // ← сброс
                System.out.println("✅ Коллекция успешно сохранена");
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    public Stack<Vehicle> removeById(Integer id) {
        if (id == null) {
            ResponseBuilder.append("Не указан id");
            return C;
        }

        Long ownerId = getCurrentUserId();
        if (ownerId == null) {
            ResponseBuilder.append("Ошибка: пользователь не авторизован");
            return C;
        }

        if (VehicleDAO.removeById(id, ownerId)) {
            lock.lock();
            try {
                C.removeIf(v -> Objects.equals(v.getId(), id));
                arrayId.remove(id);
                ResponseBuilder.append("Элемент с id = " + id + " успешно удалён.");

                VehicleDAO.resetSequence();   // ← сброс
            } finally {
                lock.unlock();
            }
        } else {
            ResponseBuilder.append("Элемент не найден или нет прав");
        }
        return C;
    }

    /**
     * Clear — удаляем только объекты текущего пользователя
     * (из памяти и из БД)
     */
    /**
     * Clear — удаляем ТОЛЬКО объекты текущего пользователя
     */
    public Stack<Vehicle> clearCollection() {
        String currentLogin = getCurrentUserLogin();
        Long ownerId = getCurrentUserId();

        if (currentLogin == null || ownerId == null) {
            ResponseBuilder.append("Ошибка: пользователь не авторизован");
            return C;
        }

        if (VehicleDAO.clear(ownerId)) {
            lock.lock();
            try {
                C.removeIf(vehicle -> currentLogin.equals(vehicle.getOwnerLogin()));
                initializeArrayId();

                VehicleDAO.resetSequence();   // ← сброс

                ResponseBuilder.append("Коллекция пользователя успешно очищена.");
            } finally {
                lock.unlock();
            }
        } else {
            ResponseBuilder.append("Ошибка при очистке коллекции в базе данных");
        }
        return C;
    }

    public Stack<Vehicle> removeGreater(Vehicle element) {
        if (element == null) return C;

        Long ownerId = getCurrentUserId();
        if (ownerId == null) {
            ResponseBuilder.append("Ошибка: пользователь не авторизован");
            return C;
        }

        if (VehicleDAO.removeGreater(element, ownerId)) {
            lock.lock();
            try {
                C.removeIf(v -> currentUserLogin != null &&
                        currentUserLogin.equals(v.getOwnerLogin()) &&
                        v.compareTo(element) > 0);
                initializeArrayId();

                VehicleDAO.resetSequence();   // ← сброс

                ResponseBuilder.append("Элементы, превышающие заданный, успешно удалены");
            } finally {
                lock.unlock();
            }
        } else {
            ResponseBuilder.append("Ошибка при удалении элементов");
        }
        return C;
    }

    // ====================== АВТОРИЗАЦИЯ ======================

    public static void setCurrentUser(String login) {
        currentUserLogin = login;
    }

    public static String getCurrentUserLogin() {
        return currentUserLogin;
    }

    private Long getCurrentUserId() {
        if (currentUserLogin == null || currentUserLogin.isEmpty()) {
            return null;
        }
        return UserDAO.getUserIdByLogin(currentUserLogin);
    }

    // ====================== СТАРАЯ ЛОГИКА (полностью сохранена) ======================

    public void initializeArrayId() {
        arrayId.clear();
        arrayId.addAll(C.stream()
                .map(Vehicle::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        Collections.sort(arrayId);
    }

    public void setCollection(Stack<Vehicle> c) {
        this.C = c != null ? c : new Stack<>();
    }

    public Stack<Vehicle> getCollection() {
        return C;
    }

    public ArrayList<Integer> getArrayId() {
        return arrayId;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void infoAboutCollection() {
        ResponseBuilder.append("Тип коллекции: " + C.getClass().getSimpleName());
        ResponseBuilder.append("Дата создания: " + creationDate);
        ResponseBuilder.append("Количество элементов: " + C.size());
    }

    public void showElementsOfCollection() {
        if (C.isEmpty()) {
            ResponseBuilder.append("Коллекция пуста.");
            return;
        }

        String elements = C.stream()
                .map(Vehicle::toString)
                .collect(Collectors.joining("\n"));

        ResponseBuilder.append("Элементы коллекции (" + C.size() + " шт.):");
        ResponseBuilder.append(elements);
    }

    /**
     * Улучшенный show с поддержкой пагинации
     * show          -> все элементы (старая логика)
     * show 2        -> вторая страница (по 10 элементов)
     */
    /**
     * Пагинированный show с подсказкой
     * show          -> все элементы
     * show 2        -> вторая страница
     */
    /**
     * Пагинированный show с информацией о страницах
     * show          -> все элементы + информация о страницах
     * show 2        -> вторая страница + информация о страницах
     */
    public String show(String argument) {
        lock.lock();
        try {
            if (C.isEmpty()) {
                return "Коллекция пуста.";
            }

            int pageSize = 10;
            int page = 1;
            boolean isPagination = false;

            // Обработка аргумента (номер страницы)
            if (argument != null && !argument.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(argument.trim());
                    if (page < 1) page = 1;
                    isPagination = true;
                } catch (NumberFormatException e) {
                    return "Ошибка: номер страницы должен быть целым положительным числом.\n" +
                            "Использование: show [номер_страницы]";
                }
            }

            int total = C.size();
            int totalPages = (total + pageSize - 1) / pageSize;

            StringBuilder sb = new StringBuilder();

            if (isPagination) {
                // Пагинированный вывод
                if (page > totalPages) {
                    sb.append(String.format("Страница %d не существует.\n", page));
                } else {
                    int start = (page - 1) * pageSize;
                    int end = Math.min(start + pageSize, total);

                    sb.append(String.format("Элементы коллекции (страница %d из %d, всего элементов: %d):\n\n",
                            page, totalPages, total));

                    for (int i = start; i < end; i++) {
                        sb.append(C.get(i)).append("\n");
                    }
                }
            } else {
                // Вывод всех элементов (старая логика)
                sb.append(String.format("Элементы коллекции (всего: %d):\n\n", total));
                for (Vehicle v : C) {
                    sb.append(v).append("\n");
                }
            }

            // === Всегда показываем информацию о страницах ===
            sb.append("\n").append("=".repeat(60)).append("\n");
            sb.append(String.format("Всего элементов: %d | Страниц: %d (по %d элементов)\n",
                    total, totalPages, pageSize));
            sb.append("Использование: show [номер_страницы]   (например: show 2)\n");
            sb.append("=".repeat(60));

            return sb.toString();

        } finally {
            lock.unlock();
        }
    }


    public void sumEnginePower() {
        double sum = C.stream()
                .mapToDouble(Vehicle::getEnginePower)
                .sum();
        ResponseBuilder.append("Сумма enginePower всех элементов: " + sum);
    }

    public void printAscendingNumberOfWheels() {
        List<Long> wheels = C.stream()
                .map(Vehicle::getNumberOfWheels)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        ResponseBuilder.append("Количество колес по возрастанию: " + wheels);
    }

    public void printDescendingNumberOfWheels() {
        List<Long> wheels = C.stream()
                .map(Vehicle::getNumberOfWheels)
                .filter(Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        ResponseBuilder.append("Количество колес по убыванию: " + wheels);
    }

    public Stack<Vehicle> reorderCollection() {
        List<Vehicle> list = new ArrayList<>(C);
        Collections.reverse(list);
        C.clear();
        C.addAll(list);
        ResponseBuilder.append("Коллекция успешно отсортирована в обратном порядке");
        return C;
    }

    public Stack<Vehicle> sortCollection() {
        List<Vehicle> list = new ArrayList<>(C);
        Collections.sort(list);
        C.clear();
        C.addAll(list);
        ResponseBuilder.append("Коллекция успешно отсортирована в естественном порядке");
        return C;
    }

    public boolean existId(Integer id) {
        return arrayId.contains(id);
    }
}