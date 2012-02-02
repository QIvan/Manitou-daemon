package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionDB
{
    private static ConnectionDB impl;
    // ReadMe: for postgreSQL
    //  "org.postgresql.Driver",
    //  "jdbc:postgresql:test"
    private String classForName = "org.sqlite.JDBC";
    private String driverManager = "jdbc:sqlite:DB/MsgDB";
    private Connection connect;

    private ConnectionDB()
    {
//        this.setClassForName(classForName);
//        this.setDriverManager(driverManager);
    }

    public static ConnectionDB getInstance()
    {
        if (impl == null)
            impl = new ConnectionDB();

        return impl;
    }

    public Connection getConnect() throws Exception
    {
        if (connect == null)
        {
            Class.forName(this.classForName);
            DriverManager.setLogStream(System.out);
            this.connect = DriverManager.getConnection(this.driverManager);
        }
        return this.connect;
    }

    public static Statement createStatement()
    {
        try
        {
            return ConnectionDB.getInstance().getConnect().createStatement();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
            return null;
        }
    }

    /**
     * Просто выполняет нужный Select к базе.
     * @param query - выполняемый запрос к базе.
     * @return количество обновлённых строк
     * @since Столкнулся с проблемой закрытия Statement. По-этому создал эту функцию.
     * @see java.sql.Statement executeQuery
     */
    public static ResultSet executeSelect (String query)
    {
        try
        {
            Statement statement = ConnectionDB.getInstance().getConnect().createStatement();
            ResultSet result = statement.executeQuery(query);
            statement.close();
            return result;

        }
        catch (Exception ex)
        {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }


    /**
     * Выполняет нужный Update к базе.
     * @param query - выполняемый запрос к базе.
     * @return количество обновлённых строк
     * @since Столкнулся с проблемой закрытия Statement. По-этому создал эту функцию.
     * @see java.sql.Statement executeUpdate
     */
    public static int executeUpdate (String query)
    {
        try
        {
            Statement statement = ConnectionDB.getInstance().getConnect().createStatement();
            int result = statement.executeUpdate(query);
            statement.close();
            return result;

        }
        catch (Exception ex)
        {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     * Выполняет нужный Insert к базе.
     * @param query - выполняемый запрос к базе.
     * @return количество обновлённых строк
     * @since Столкнулся с проблемой закрытия Statement. По-этому создал эту функцию.
     */
    public static int executeInsert (String query)
    {
        return executeUpdate(query);
    }



    /**
     * Geter's and Seter's
     */

    public String getClassForName()
    {
        return classForName;
    }

    public void setClassForName(String classForName)
    {
        this.classForName = classForName;
    }

    public String getDriverManager()
    {
        return driverManager;
    }

    public void setDriverManager(String driverManager)
    {
        this.driverManager = driverManager;
    }
}
