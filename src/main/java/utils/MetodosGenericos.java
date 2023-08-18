package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import config.driver.DriverContext;

public class MetodosGenericos {


	public static List<LogEntry> retornaTrazaNetwork() {	
	List<LogEntry> entries = DriverContext.getDriver().manage().logs().get(LogType.PERFORMANCE).getAll();
	System.out.println(entries.size() + " " + LogType.PERFORMANCE + " log entries found");
	return entries;

	}
	public static String buscarDatoXHR(String[] parts,String datoBuscar) {
		String cadena = null;
		 for(String busqueda : parts) {
			 if (busqueda.contains(datoBuscar)) {
				 cadena=busqueda;
			 } 
		 }
		 return cadena;
		
	}

    public static List<String> consultaGenerica(String consulta, String urlDB, String userDB, String passDB){
        List<String> result = new ArrayList<>();
        Connection connection;
        try{
            connection = DriverManager.getConnection(urlDB,userDB,passDB);
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(consulta);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberColumns = rsmd.getColumnCount();
            StringBuilder primeraRow = new StringBuilder();
            for(int i = 1;i<=numberColumns;i++){
                primeraRow.append(rsmd.getColumnLabel(i)).append(",");
            }
            result.add(primeraRow.deleteCharAt(primeraRow.length()-1).toString());
            while (rs.next()) {
                int cont = 1;
                StringBuilder rowBuilder = new StringBuilder(new StringBuilder());
                while (cont <= numberColumns){
                    try{
                        rowBuilder.append(rs.getString(cont)).append(",");
                        cont++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String row = rowBuilder.deleteCharAt(rowBuilder.length()-1).toString();
                result.add(row);
            }
            rs.close();
            st.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
	
	
	
	
}
