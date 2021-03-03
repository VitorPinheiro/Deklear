/**
 * 
 */
package br.pucrio.inf.lac.CompMovel.logic;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Vitor
 *
 */
public class ContextDbManager 
{
    private static String _jdbcUrl = "jdbc:postgresql://192.168.0.6:5432/icare";
    private static String _username = "postgres";
    private static String _password = "icaredb";
    private static ContextDbManager _instance;
    
    
    public static ContextDbManager getInstance()
    {
    	if(_instance == null)
    	{
    		_instance = new ContextDbManager();
    	}
    	
    	return _instance;
    }
    
    private ContextDbManager()
    {
    	Initialize();
    }
    
    private void Initialize()
    {
	    String rs = null;
	    String rs2 = null;
	    
	    rs = createPatientTableForSingleValue();	    
	    String makeHyperTable = "SELECT create_hypertable('sensorObs', 'time');";	    
	    rs2 = executeQuery(makeHyperTable);

		System.out.println(rs);
		System.out.println(rs2);	    
	    
		rs = createPatientTableForDoubleValue();
	    makeHyperTable = "SELECT create_hypertable('sensorObsDouble', 'time');";	    
	    rs2 = executeQuery(makeHyperTable);	    

		System.out.println(rs);
		System.out.println(rs2);
    }

    
    /**
     * Colocar aqui uma tabela que salva todo dado que vem de um paciente monitorado.
     * Escreve essa tabela na mao antes de digitar aqui.
     * LEMBRE: 	Depois de chamar esse metodo de criar, vc tem que transformar ela em uma hypertable! Ai sim ele aproveita o timescaleDb
     * 			-- This creates a hypertable that is partitioned by time
				--   using the values in the `time` column.

				SELECT create_hypertable('conditions', 'time');
     */
    public String createPatientTableForSingleValue()
    {
    	String query = "CREATE TABLE sensorObs (" +
    						"time        TIMESTAMPTZ       NOT NULL," +
    						"patientMhubID TEXT            NOT NULL," +    			
    						"latitude    DOUBLE PRECISION  NULL," +
    						"langitude   DOUBLE PRECISION  NULL," +    						
    						"value       DOUBLE PRECISION  NOT NULL," +
    						"notification TEXT			   NULL," +
    						"sensor      TEXT 			   NULL," +
    						"dataType    TEXT   		   NULL," +    						
    						"unity       TEXT   		   NULL" +    						    						
    					");";
    	
    	return executeQuery(query);
    }
    
    public String createPatientTableForDoubleValue()
    {
    	String query = "CREATE TABLE sensorObsDouble (" +
    						"time        TIMESTAMPTZ       NOT NULL," +
    						"patientMhubID TEXT            NOT NULL," +    			
    						"latitude    DOUBLE PRECISION  NULL," +
    						"langitude   DOUBLE PRECISION  NULL," +    						
    						"valueOne       DOUBLE PRECISION  NOT NULL," +
    						"valueTwo       DOUBLE PRECISION  NOT NULL," +    						
    						"notification TEXT			   NULL," +
    						"sensor      TEXT 			   NULL," +
    						"dataType    TEXT   		   NULL," +    						
    						"unity       TEXT   		   NULL" +    						    						
    					");";
    	
    	return executeQuery(query);
    }
    
    /**
     * Pode ser otimizado, é possivel mandar varias linhas para serem persistidas em uma mesma query.
     * No futuro faça isso.
     * 
     * @param mHubID
     * @param latitude
     * @param longitude
     * @param value
     * @param notification
     * @param sensor_name
     * @param dataType
     * @param unity
     * @return
     */
    public String insertPatientDataSingleValue(String mHubID, Double latitude, 
    		Double longitude, Double value, String notification, String sensor_name, String dataType, String unity)
    {
    	String query = "INSERT INTO sensorObs\r\n" + 
    					"  VALUES\r\n" + 
    					"    (NOW(), '"+mHubID+"', "+latitude+", "+longitude+", "+value+", '"+notification+"', '"+sensor_name+"',"
    							+ " '"+dataType+"', '"+unity+"');";
    	
    	System.out.println("Query1 to DB = "+query);
    	
    	return executeQuery(query);    	
    }
    
    public String insertPatientDataDoubleValue(String mHubID, Double latitude, 
    		Double longitude, Double value1, Double value2, String notification, String sensor_name, String dataType, String unity)
    {
    	String query = "INSERT INTO sensorObsDouble\r\n" + 
    					"  VALUES\r\n" + 
    					"    (NOW(), '"+mHubID+"', "+latitude+", "+longitude+", "+value1+", "+value2+", '"+notification+"', '"+sensor_name+"',"
    							+ " '"+dataType+"', '"+unity+"');";
    	
    	System.out.println("Query2 to DB = "+query);
    	
    	return executeQuery(query);    	
    }
    
    public String executeQuery(String query)
    {
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    String retMsg = null;
	    
	    try {
	    
	      // Step 2 - Open connection
	      conn = DriverManager.getConnection(_jdbcUrl, _username, _password);

	      // Step 3 - Execute statement
	      stmt = conn.createStatement();
	      rs = stmt.executeQuery(query);

	      // Step 4 - Get result
	      if(rs != null)
		      if (rs.next()) {
		        System.out.println(rs.getString(1));
		        retMsg = rs.getString(1);
		      }
	    }
	    catch(SQLException e) {
	        
  	        retMsg = e.getMessage();
  	        
  	        if(retMsg.contains("already exists"))
  	        	return retMsg;
  	        else if(retMsg.contains("Nenhum resultado foi retornado pela consulta"))
  	        	return retMsg;
  	        
  	        e.printStackTrace();
  	        
	    } finally {
	      try {

	        // Step 5 Close connection
	        if (stmt != null) {
	          stmt.close();
	        }
	        if (rs != null) {
	          rs.close();
	        }
	        if (conn != null) {
	          conn.close();
	        }
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	    }
	    return retMsg;
    }
}
