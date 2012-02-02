package db;

import java.sql.Connection;
import java.sql.DriverManager;
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
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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
