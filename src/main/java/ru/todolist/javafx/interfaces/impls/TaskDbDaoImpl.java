//package ru.todolist.javafx.interfaces.impls;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import ru.todolist.javafx.db.SQLiteConnection;
//import ru.todolist.javafx.interfaces.TaskDao;
//import ru.todolist.javafx.objects.Task;
//
//import java.sql.*;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//
//public class TaskDbDaoImpl implements TaskDao {
//    public ObservableList<Task> tasks = FXCollections.observableArrayList();
//
//    /**
//     * записи сразу добавляются в базу данных
//     *
//     * @param task текущая задача, которую хочу добавить
//     * @return если все прошло нормально истина, иначе ложь
//     */
//    @Override
//    public boolean addTask(Task task) {
//        try (Connection con = SQLiteConnection.getConnection();
//             PreparedStatement statement = con.prepareStatement("insert into todo(task, task_create_time, status)" +
//                     " values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
//            statement.setString(1, task.getTask());
//            statement.setString(2, task.getTime());
//            statement.setString(3, task.isStatus());
//
//            int result = statement.executeUpdate();
//
//            if (result > 0) {
//                int id = statement.getGeneratedKeys().getInt(1);// получить сгенерированный id вставленной записи
//                task.setId(id);
//                tasks.add(task); // вот оно добавление, у меня изначально было
//                return true;
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(TaskDbDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return false;
//    }
//
//    /**
//     * удаление объекта происходит по id
//     *
//     * @param task текущая задача
//     * @return истина если прошло условие, ложь нет
//     */
//    @Override
//    public boolean deleteTask(Task task) {
//        try (Connection con = SQLiteConnection.getConnection();
//             Statement statement = con.createStatement()) {
//            int result = statement.executeUpdate("delete from todo where id=" + task.getId());
//            if (result > 0) {
//                tasks.remove(task);
//                return true;
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return false;
//    }
//
//    /**
//     * обновление текущей задачи
//     * @param task текущая задача
//     * @return истина задача обновилась, ложь нет
//     */
//    public boolean updateTask(Task task) {
//        try (Connection con = SQLiteConnection.getConnection();
//             PreparedStatement statement = con.prepareStatement("update todo set task=?, task_create_time=?, status=? where id=?")) {
//            statement.setString(1, task.getTask());
//            statement.setString(2, task.getTime());
//            statement.setString(3, task.isStatus());
//            statement.setInt(4, task.getId());
//
//
//            int result = statement.executeUpdate();
//            if (result>0) {
//                // обновление в коллекции происходит автоматически, после нажатия ОК в окне редактирования
//                return true;
//            }
//        } catch (SQLException ex) {
//            throw new RuntimeException(ex);
//        }
//        return false;
//    }
//    @Override
//    public boolean completeTask(Task task, String text) {
//        try (Connection con = SQLiteConnection.getConnection(); PreparedStatement statement = con.prepareStatement("update todo set task=?, task_create_time=?, status=? where id=?")) {
//            statement.setString(1, task.getTask());
//            statement.setString(2, task.getTime());
//            statement.setString(3, text);
//            statement.setInt(4, task.getId());
//
//
//            int result = statement.executeUpdate();
//            if (result>0) {
//                // обновление в коллекции происходит автоматически, после нажатия ОК в окне редактирования
//                return true;
//            }
//        } catch (SQLException ex) {
//            throw new RuntimeException(ex);
//        }
//        return false;
//    }
//    public ObservableList<Task> getTasksList() {
//        return tasks;
//    }
//
//    /**
//     * получение всех записей из базы данных todolist.db при загрузках программы
//     *
//     * @return список записей.
//     */
//    @Override
//    public ObservableList<Task> findAll() {
//
//        try (Connection con = SQLiteConnection.getConnection();
//             Statement statement = con.createStatement();
//             ResultSet rs = statement.executeQuery("select * from todo");) {
//            while (rs.next()) {
//                Task task = new Task();
//                task.setId(rs.getInt("id"));
//                task.setTask(rs.getString("task"));
//                task.setTime(rs.getString("task_create_time"));
//                task.setStatus(rs.getString("status"));
//
//                tasks.add(task);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(TaskDbDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return tasks;
//    }
//    @Override
//    public ObservableList<Task> find(String text) {
//        tasks.clear();
//
//        try (Connection con = SQLiteConnection.getConnection();
//             PreparedStatement statement = con.prepareStatement("select * from todo where task like ? or task_create_time like ? or status like ?");) {
//
//            String searchStr = "%"+text+"%";
//
//            statement.setString(1, searchStr);
//            statement.setString(2, searchStr);
//            statement.setString(3, searchStr);
//
//            ResultSet rs = statement.executeQuery();
//
//            while (rs.next()) {
//                Task task  = new Task();
//                task.setId(rs.getInt("id"));
//                task.setTask(rs.getString("task"));
//                task.setTime(rs.getString("task_create_time"));
//                task.setStatus(rs.getString("status"));
//                tasks.add(task);
//            }
//        }catch (SQLException ex){
//            Logger.getLogger(TaskDbDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return tasks;
//    }
//
//}
