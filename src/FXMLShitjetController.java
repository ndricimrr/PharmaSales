/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmashitje.WelcomeScreen;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang.WordUtils;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;
import org.controlsfx.control.textfield.TextFields;

/**
 * FXML Controller class
 *
 * @author Ndricim Rrapi
 */
public class FXMLShitjetController implements Initializable {

    @FXML private TextField searchArtikull;
    @FXML private TableView shitjetTable;
    @FXML private TableColumn<Produkt, String> artikulliCol;
    @FXML private TableColumn<Produkt, String> njesiaCol;
    @FXML private TableColumn<Produkt, Integer> cmimiCol;
    @FXML private TableColumn<Produkt, Integer> sasiaCol;
    @FXML private TableColumn<Produkt, Integer> totalCol;
    @FXML private TableColumn<Produkt, Integer> gjendjaCol;
    @FXML private TableColumn<Produkt, String> skacencaCol;
    ObservableList<String> searchSuggestions = FXCollections.observableArrayList();
    
     private Image icon;
    
    public FXMLShitjetController(){
      icon = new Image(FXMLFaturatController.class.getResource("icon.png").toExternalForm());
    }
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startDataAndSuggestions();
       // TextFields.bindAutoCompletion(searchArtikull, searchSuggestions);
        setStringColProperties("artikulli",artikulliCol);
        setStringColProperties("njesia",njesiaCol);
        setIntColProperties("cmimi",cmimiCol);
        cmimiCol.setOnEditCommit(new IntegerHandler("cmimi"));
        setIntColProperties("sasia",sasiaCol);
        sasiaCol.setOnEditCommit(new IntegerHandler("sasia"));
      //  setIntColProperties("total",totalCol);
       totalCol.setCellValueFactory(new PropertyValueFactory<Produkt, Integer>("total"));
      
      // totalCol.setCellValueFactory((TableColumn.CellDataFeatures<Produkt, Integer> param) -> {
     
     
      
        setIntColProperties("gjendja",gjendjaCol);
        setStringColProperties("skadenca",skacencaCol);
        AutoCompletionBinding<String> acb = TextFields.bindAutoCompletion(searchArtikull , searchSuggestions);
        acb.setOnAutoCompleted(new MySearchFieldHandler());
    }
      
    
    @FXML  private void goToHomePage(ActionEvent e) throws IOException{
        Parent home_page_parent = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene home_page_scene = new Scene(home_page_parent);
        Stage app_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        app_stage.hide();
        app_stage.setScene(home_page_scene);
        app_stage.show();
         
    }
    class MySearchFieldHandler implements EventHandler<AutoCompletionBinding.AutoCompletionEvent<String>>{
        @Override
        public void handle(AutoCompletionEvent<String> event){
            search(event.getCompletion());
            searchArtikull.clear();
        }      
    }
  
    @FXML private void anulloAction(ActionEvent event){
        shitjetTable.getItems().clear();
    }
    
    @FXML private void shitAction(ActionEvent event){
        incrementIntoBillNo(); // OK
        int row = 0;
        int  id = createIDForBill();
        while (row < shitjetTable.getItems().size() ){  
            Produkt toBeSold = (Produkt)shitjetTable.getItems().get(row);
            String artikulli = toBeSold.getArtikulli();
            int        cmimi = toBeSold.getCmimi();                        
            int        sasia = toBeSold.getSasia();
            int total = toBeSold.getTotal();
            Date        date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String koha = sdf.format(date);	
            row++;
            decrementStockNumber(artikulli, sasia);	
            insertIntoFaturat(id, artikulli, cmimi, sasia,  total,  koha);
        }
        
       if(row > 0){
           showError(Alert.AlertType.INFORMATION, "U shit", "SHITJA U KRYE ME SUKSES", "Ju sapo shitet " + row + " produkte!");
            shitjetTable.getItems().clear();
       }
    }
    
    private void setStringColProperties(String colName, TableColumn<Produkt, String> thisColumn){
        thisColumn.setCellValueFactory(new PropertyValueFactory<Produkt, String>(colName));
        thisColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }
    
    private void setIntColProperties(String colName, TableColumn<Produkt, Integer> thisColumn){
        thisColumn.setCellValueFactory(new PropertyValueFactory<Produkt, Integer>(colName));
        thisColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MyIntegerStringConverter()));
    }
    
    class MyIntegerStringConverter extends StringConverter<Integer> {
        @Override public Integer fromString(String value) {
            try{
                if (value == null) throw new NullPointerException();
                value = value.trim();
                if (value.length() < 1) throw new NullPointerException();
                if ("".equals(value))   throw new NullPointerException();
                 return Integer.valueOf(value);
            }catch(NullPointerException| NumberFormatException e){
                showError(Alert.AlertType.ERROR, "Gabim", "Gabim numerik! " , "Ju shkruat: '" + value + "'\nJu lutem shkruani nje numer!" );
                return null;
            }
        }
        @Override public String toString(Integer value) {
            // If the specified value is null, return a zero-length String
            if (value == null) {
                return "";
            }

            return (Integer.toString(((Integer)value).intValue()));
        }
    }
    
    private void showError(Alert.AlertType type, String title, String header, String content ){
               Alert alert = new Alert(type);
               alert.setTitle(title);
               alert.setHeaderText(header);
               alert.setContentText(content);
               ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(icon);
               alert.showAndWait();
    }

    private void search(String keyword){
        try{
            Connection conn = SQLiteDB.dbConnector();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM artikujt WHERE artikulli = '" + keyword  +"';");
           // shitjetTable.getItems().clear();
            while(rs.next()){
                shitjetTable.getItems().add(
                    new Produkt(
                    rs.getString("artikulli"),
                    rs.getString("njesia"),
                    rs.getInt("cmimDalje"),
                    0,
                    rs.getInt("gjendje"),
                    0,
                    rs.getString("skadence"))
                );
            }
            rs.close();
            stmt.close();
            conn.close();
             
          } catch (SQLException ex) {
            System.err.println("Error"+ex);
        }
   }
    
    private void startDataAndSuggestions(){
        try{
            Connection conn = SQLiteDB.dbConnector();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM artikujt;");
           
            while(rs.next()){
                    searchSuggestions.addAll(rs.getString("artikulli"));
            }                   
            rs.close();
            stmt.close();
            conn.close();
            
          } catch (SQLException ex) {
            System.err.println("Error "+ex);
        }
    }
    
    class IntegerHandler implements EventHandler<TableColumn.CellEditEvent<Produkt, Integer>> {
      String column;
        public IntegerHandler(String column) {
            this.column = column;
        }
        
        @Override
        public void handle(TableColumn.CellEditEvent<Produkt, Integer> t){
            if (column.contains("cmimi")){
                if (t.getNewValue()!= null){
                    int newValue = t.getNewValue();
                    int sasia =  ((Produkt) t.getTableView().getItems().get(t.getTablePosition().getRow())).getSasia();
                    if( newValue > 0){
                           ((Produkt) t.getTableView().getItems().get(t.getTablePosition().getRow())).setCmimi(newValue);
                           Produkt a =(Produkt) shitjetTable.getSelectionModel().getSelectedItem();
                           a.setTotal(newValue*sasia);
                           shitjetTable.refresh();
                    }else  
                        showError(Alert.AlertType.ERROR, "Gabim", "Gabim Numerik!", "Nuk lejohet cmim negativ ose 0 si: " + newValue );
                }
            }
        
            else if(column.contains("sasia")){
                if (t.getNewValue()!= null){
                    int newValue = t.getNewValue();
                    int cmimi =  ((Produkt) t.getTableView().getItems().get(t.getTablePosition().getRow())).getCmimi();
                    if(newValue > 0){
                        ((Produkt) t.getTableView().getItems().get(t.getTablePosition().getRow())).setSasia(newValue);
                        Produkt a =(Produkt) shitjetTable.getSelectionModel().getSelectedItem();
                        a.setTotal(newValue*cmimi);
                        shitjetTable.refresh();
                    }else
                        showError(Alert.AlertType.ERROR, "Gabim", "Gabim Numerik!", "Nuk lejohen 0 dhe numrat negativ si: " + newValue );
                }
            }
        }
    }
    
    private Integer checkIfInt(String value) {
        try{
            if (value == null) throw new NullPointerException();
            value = value.trim();
            if (value.length() < 1) throw new NullPointerException();
            if ("".equals(value))   throw new NullPointerException();
            return Integer.valueOf(value);
        }catch(NullPointerException| NumberFormatException e){
            showError(Alert.AlertType.ERROR, "Gabim", "Gabim numerik! " , "Ju shkruat: '" + value + "'\nJu lutem shkruani nje numer!" );
            return null;
        }
    }

    public void incrementIntoBillNo()  {
        String sql = "INSERT INTO billNo(empty) VALUES(5)";
        try {
            Connection c = SQLiteDB.dbConnector();
            Statement  stmt;
            stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch (SQLException ex) {
             System.out.println(ex.getMessage());
        }
    }

    public int createIDForBill(){
        int id = 0;
        String sql = "SELECT theId FROM  billNo; ";
        try{
        Connection connect = SQLiteDB.dbConnector();
        Statement stmt = connect.createStatement();
        ResultSet set =  stmt.executeQuery(sql);   
        while( set.next()){
            id  =  set.getInt("theId");
        }
        set.close();
        connect.close();
       }   catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return id;
    }
    	
    public void insertIntoFaturat( int nr, String artikulli, int cmimi, int sasia,int total, String koha) {
        String sql = "INSERT INTO faturat(nr, artikulli,cmimi,sasia,totali,koha) VALUES(?,?,?,?,?,?)";
        try {
            Connection c = SQLiteDB.dbConnector();
            PreparedStatement pstmt = c.prepareStatement(sql) ;
            pstmt.setInt(1, nr);
            pstmt.setString(2, artikulli);
            pstmt.setInt(3, cmimi);
            pstmt.setInt(4, sasia);
            pstmt.setInt(5, total);
            pstmt.setString(6,koha);
            pstmt.executeUpdate();
        } catch (SQLException e) {
          System.out.println(e.getMessage());
        }
    }
    
    public void decrementStockNumber(String artikulli, int sasia) {
        String query = "UPDATE artikujt SET gjendje = (gjendje - ? ) WHERE artikulli  = ? ;";
        try {
            Connection c = SQLiteDB.dbConnector();
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setInt(1, sasia);
            stmt.setString(2, artikulli);
            stmt.executeUpdate();
        } catch (SQLException e) {
             System.out.println(e.getMessage());
        }
    }
}
