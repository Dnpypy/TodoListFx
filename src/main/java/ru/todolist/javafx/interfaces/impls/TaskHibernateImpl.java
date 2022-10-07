package ru.todolist.javafx.interfaces.impls;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import ru.todolist.javafx.hibernate.dialect.HibernateSessionFactoryUtil;
import ru.todolist.javafx.interfaces.TaskDao;
import ru.todolist.javafx.objects.Task;

/**
 * класс TaskHibernateImpl реализует Task DAO интерфейс(CRUD операции)
 */
public class TaskHibernateImpl implements TaskDao {

    public ObservableList<Task> tasks = FXCollections.observableArrayList();
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;

    public TaskHibernateImpl() {
        sessionFactory = getSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure();
        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }

    /**
     * Процедура добавления записей в таблицу
     */
    @Override
    public boolean addTask(Task task) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        try {
            System.out.println("Добавление записи в таблицу БД");
            Transaction tx = session.beginTransaction();
            session.save(task);
            tx.commit();
            System.out.println("\tЗаписи добавлены");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        session.close();
        return true;
    }

    @Override
    public boolean deleteTask(Task task) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        try {
            System.out.println("Удаление записи из таблицы БД");
            Transaction tx = session.beginTransaction();
            session.delete(task);
            tx.commit();
            System.out.println("\tЗапись удалена");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        session.close();
        return true;
    }

    @Override
    public boolean updateTask(Task task) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        try {
            System.out.println("Обновление записи из таблицы БД");
            Transaction tx = session.beginTransaction();
            session.update(task);
            tx.commit();
            System.out.println("\tЗапись обновлена");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        session.close();
        return true;
    }

    @Override
    public boolean completeTask(Task task, String status) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        try {
            System.out.println("Состояние записи из таблицы БД измененно!");
            Transaction tx = session.beginTransaction();
            task.setStatus(status);
            session.update(task);
            tx.commit();
            System.out.println("\tСостояние записи измененно!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        session.close();
        return true;
    }

    /**
      методом tasksclear() проверяем очистку таблицы, чтобы не было двойного заполнения таблицы, очищаем текущую.
     * tasks.addAll -> из класса HibernateSessionFactoryUtil получаем ссесию фактори и открываем сессию,
     * указываю сущность моего класса из таблицы и кладем ее в лист
     */
    public ObservableList<Task> findAll() {
        tasks.clear();
        tasks.addAll(HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Task").list());
        return tasks;
    }


    /**
     * поиск задачи по слову, там же можно удалить ее
     * метод createCriteria deprecation не поддерживается в версии более 9-ки
     */
    @SuppressWarnings("deprecation")
    @Override
    public ObservableList<Task> find(String text) {
        tasks.clear();
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();

        Criterion rest1= Restrictions.like("task", text+"%");
        Criterion rest2= Restrictions.like("time", text+"%");
        Criterion rest3= Restrictions.like("status", text+"%");

        tasks.addAll(session.createCriteria(Task.class).add(Restrictions.or(rest1, rest2, rest3)).list());
        session.close();
        return tasks;
    }

    public ObservableList<Task> getTasksList() {
        return tasks;
    }
}
