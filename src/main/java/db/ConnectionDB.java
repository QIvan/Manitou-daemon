package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionDB{

	private String classForName="";
	private String driverManager="";
	private Connection connect ;

	public ConnectionDB(String classForName, String driverManager) throws Exception
	{
            this.setClassForName(classForName);
            this.setDriverManager(driverManager);
		
	}
	
	/**
	 * @return the classForName
	 */
	public String getClassForName() {
            return classForName;
	}

	/**
	 * @param classForName the classForName to set
	 */
	public void setClassForName(String classForName) {
            this.classForName = classForName;
	}

	/**
	 * @return the driverManager
	 */
	public String getDriverManager() {
            return driverManager;
	}

	/**
	 * @param driverManager the driverManager to set
	 */
	public void setDriverManager(String driverManager) {
            this.driverManager = driverManager;
	}
	
	public Connection getConnect() throws Exception{
            if(connect==null){
                    Class.forName(this.classForName);
                    this.connect = DriverManager.getConnection(this.driverManager);
            }
            return this.connect;
	}

	/**
	 * @param connect the connect to set
	 */
	public void setConnect(Connection connect) {
            this.connect = connect;
	}
}
