package connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class SingleConnectionBanco {
	
	private static String banco = "jdbc:postgresql://localhost:5432/curso-jsp?autoReconnect=true";
    private static String user = "postgres";
    private static String senha = "admin";
    private static  Connection connection = null;
    
    
    public static Connection getConnection() {
		return connection;
	}
    
    static {
    	conectar();
    }
    
    public SingleConnectionBanco() { // Qaundo tiver uma instância vai conectar 
		conectar();
	}
    
    private static void conectar() {
    	
    	try {
    		
    		if (connection == null) {
    			Class.forName("org.postgresql.Driver"); // Carregamento o driver de conexão do banco
    		    connection = DriverManager.getConnection(banco, user, senha);
    		    connection.setAutoCommit(false); // Para não efetuar alterações no banco sem nosso comando
    		}
			
		} catch (Exception e) {
			e.printStackTrace(); // Mostrar qualquer erro no momento de conectar
		}
    }
    
    
    
    
    
}
