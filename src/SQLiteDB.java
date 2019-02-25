/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmashitje.WelcomeScreen;

import java.sql.*;

import javax.swing.JOptionPane;
/**
 *
 * @author Ndricim Rrapi
 */
public class SQLiteDB {
    public static Connection dbConnector(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:artikujt.db");
            return conn;
        }catch(ClassNotFoundException | SQLException E){
            JOptionPane.showMessageDialog(null,E);
            return null;
        }	
    }
    
    public void initDB(){
        try{
            Connection c = dbConnector();
            Statement stmt = c.createStatement();

            DatabaseMetaData md = c.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            boolean existsA = false;
            boolean existsF = false;
            boolean existsB = false;
            boolean existsS = false;
            while (rs.next() ) {
                System.out.println(rs.getString(3) + "+");
                if (rs.getString(3).equals("artikujt")) // avoid duplicates
                    existsA = true;
                else if(rs.getString(3).equals("faturat")) // avoid duplicates
                    existsF = true;
                else if(rs.getString(3).equals("billNo")) // avoid duplicates
                    existsB = true;
                else if (rs.getString(3).equals("suggestionsTable"))
                    existsS = true;
            }
            if (!existsA){
                //create table only if it doesn't exists
                System.out.println("Creating artikujt table@@");
                stmt.executeUpdate( getArtikujtTable() );	
            }
            else if (!existsF){
                System.out.println("Creating faturat table@@");
                stmt.executeUpdate( getFaturatTable() );
            }
            else if (!existsB){
                System.out.println("Creating billNo table@@");
                stmt.executeUpdate( getBillNoTable() );
            }
            else if (!existsS){
                System.out.println("Creating suggestionsTable...");
                stmt.executeUpdate( getSuggestionsTable() );
            }
        } catch (SQLException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }   
	}
    
    public String getArtikujtTable(){
        String sql = " CREATE TABLE 'artikujt' ( "+
	"'id'	INTEGER PRIMARY KEY AUTOINCREMENT,"+
	"'kodi'	INT NOT NULL,"+
	"'artikulli'	CHAR(50),"+
	"'njesia'	TEXT NOT NULL,"+
	"'cmimHyrje'	REAL,"+
	"'cmimDalje'	REAL,"+
	"'tvsh' 	REAL,"+
	"'gjendje'	REAL,"+
	"'total'	REAL,"+
	"'skadence'	DATE";
        return sql;
    }
    
    public String getFaturatTable(){
        String sql = "CREATE TABLE 'faturat' ("+
	"'nr'         REAL,"+
	"'artikulli'  CHAR(50),"+
	"'cmimi'      REAL,"+
	"'sasia'      REAL,"+
	"'totali'     REAL,"+
	"'koha'       DATE";
        return sql;
    }
    
    public String getBillNoTable(){
        String sql = "CREATE TABLE 'billNo' ("+
	"'theId'    INTEGER PRIMARY KEY AUTOINCREMENT,"+
	"'empty'    INT NOT NULL";
        return sql;
    }
    
    public String getSuggestionsTable(){
        String sql = "CREATE TABLE 'suggestionsTable' ("+
	"'id'                INTEGER PRIMARY KEY AUTOINCREMENT,"+
	"'njesiaSuggestion'  TEXT NOT NULL";
        return sql;
    } 
}