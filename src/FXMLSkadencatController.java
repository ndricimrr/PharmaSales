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
import java.text.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Ndricim Rrapi
 */
public class FXMLSkadencatController implements Initializable {

    @FXML private ComboBox skadencaMonthBox ;
    @FXML private ComboBox skadencaYearBox ;
    @FXML private Label updateLabel ;
    @FXML private TableColumn<Artikull, String> artikulliCol;
    @FXML private TableColumn<Artikull, Integer> gjendjeCol;
    @FXML private TableColumn<Artikull, String> skadenceCol;
    @FXML private TableView<Artikull> skadencaTable;
    @FXML private DatePicker firstDatePicker;
    @FXML private DatePicker secondDatePicker;
    StringConverter myConverter;
    ObservableList<Integer> monthList =  FXCollections.observableArrayList();
    ObservableList<Integer> yearList =  FXCollections.observableArrayList();
    private Image icon;
    
    public FXMLSkadencatController(){
        icon = new Image(FXMLArtikujtController.class.getResource("icon.png").toExternalForm());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setStringColProperties("artikulli",artikulliCol);
        setIntColProperties("gjendje",gjendjeCol);
        setStringColProperties("skadence",skadenceCol);
        monthList.addAll(1,2,3,4,5,6,7,8,9,10,11);
        yearList.addAll(0,1,2,3,4,5,6,7);
        skadencaMonthBox.setItems(monthList);
        skadencaMonthBox.getSelectionModel().select(0);
        skadencaYearBox.setItems(yearList);
        skadencaYearBox.getSelectionModel().select(0);
        myConverter =  new MyStringConverter();
        firstDatePicker.setConverter(myConverter);
        secondDatePicker.setConverter(myConverter);
    }    
      
    
    @FXML  private void goToHomePage(ActionEvent e) throws IOException{
        Parent home_page_parent = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene home_page_scene = new Scene(home_page_parent);
        Stage app_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        app_stage.hide();
        app_stage.setScene(home_page_scene);
        app_stage.show();
         
    }
    @FXML private void searchButtonAction(ActionEvent e){
        if(skadencaYearBox.getSelectionModel().getSelectedIndex()== 0){
            int month = (int)skadencaMonthBox.getSelectionModel().getSelectedItem();
            showExipiryinXmonths(month);
            updateLabel.setText("Me poshte produktet qe skadojne brenda : " + month + " muajsh");
        }
        else {
            int month = (int)skadencaMonthBox.getSelectionModel().getSelectedItem();
            int year = (int)skadencaYearBox.getSelectionModel().getSelectedItem();
            showExipiryinXmonths(month + (year*12));
            updateLabel.setText("Me poshte produktet qe skadojne brenda : " + year + " vjet e " + month + " muaj");
        }
           
    }
    
    private boolean notInteger(KeyEvent e){
        if( !(e.getCode().isDigitKey()) && (e.getCode() != KeyCode.BACK_SPACE) && !(e.getCode().isArrowKey()) && !(e.getCode() == KeyCode.DELETE) && !(e.getCode()== KeyCode.ENTER) && (e.getCode()!= KeyCode.TAB))
            return true;
        else return false;
    }
     
    private void setStringColProperties(String colName, TableColumn<Artikull, String> thisColumn){
        thisColumn.setCellValueFactory(new PropertyValueFactory<Artikull, String>(colName));
        thisColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }
    
    private void setIntColProperties(String colName, TableColumn<Artikull, Integer> thisColumn){
        thisColumn.setCellValueFactory(new PropertyValueFactory<Artikull, Integer>(colName));
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
            showError(AlertType.ERROR, "Gabim", "Gabim numerik! " , "Ju shkruat: '" + value + "'\nJu lutem shkruani nje numer!" );
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
   
    private void showExipiryinXmonths(int num_Months) {
        skadencaTable.getItems().clear();
        try (Connection conn = SQLiteDB.dbConnector()) {
           String dateNow = getSkadenca(num_Months).get(0);
	   String dateThen = getSkadenca(num_Months).get(1);
	   String sql = ( "SELECT * FROM artikujt WHERE skadence >= ? and skadence <= ? ;" );
           
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, dateNow);
                stmt.setString(2, dateThen);
               ResultSet rs = stmt.executeQuery();
               while(rs.next()){
                   System.out.println("_+");
                     Artikull artikull =  new Artikull(
                    rs.getInt("kodi"),
                    rs.getString("artikulli"),
                    rs.getString("njesia"),
                    rs.getInt("cmimHyrje"),
                    rs.getInt("cmimDalje"),
                    rs.getInt("tvsh"),
                    rs.getInt("gjendje"),
                    rs.getInt("cmimDalje")* rs.getInt("gjendje"),
                    rs.getString("skadence"));
                skadencaTable.getItems().add(artikull);
               }
            }
            } catch (SQLException ex) {
                showError(AlertType.ERROR, "GABIM", "GABIM NE INSERT DATABAZE", "ERROR: " + ex);
            }    
    }
    
    public ArrayList<String> getSkadenca(int months)    {
            DateFormat  dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date now = new Date();   
            Date then = new Date();
            Calendar myCal = Calendar.getInstance();
            myCal.setTime(now); 
            myCal.add(Calendar.MONTH, months);    
            then = myCal.getTime();
            String dateNow  = dateFormat.format(now);
            String dateThen = dateFormat.format(then);
            ArrayList<String> list = new ArrayList<String>();
            list.add(dateNow);
            list.add(dateThen);
            return list;	
    }	

    @FXML 
    private void searchBetweenDates(ActionEvent e) {
        skadencaTable.getItems().clear();
        if(firstDatePicker.getValue() != null && secondDatePicker.getValue() != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            try (Connection conn = SQLiteDB.dbConnector()) {
               String dateFirst = firstDatePicker.getValue().format(formatter);
               String dateSecond = secondDatePicker.getValue().format(formatter);
               String sql = ( "SELECT * FROM artikujt WHERE skadence >= ? and skadence <= ? ;" );
                  System.out.println(dateFirst + "__" + dateSecond);
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, dateFirst);
                    stmt.setString(2, dateSecond);
                   ResultSet rs = stmt.executeQuery();
                   while(rs.next()){
                       System.out.println("_+");
                         Artikull artikull =  new Artikull(
                        rs.getInt("kodi"),
                        rs.getString("artikulli"),
                        rs.getString("njesia"),
                        rs.getInt("cmimHyrje"),
                        rs.getInt("cmimDalje"),
                        rs.getInt("tvsh"),
                        rs.getInt("gjendje"),
                        rs.getInt("cmimDalje")* rs.getInt("gjendje"),
                        rs.getString("skadence"));
                    skadencaTable.getItems().add(artikull);
                   }
                }
                } catch (SQLException ex) {
                    showError(AlertType.ERROR, "GABIM", "GABIM NE INSERT DATABAZE", "ERROR: " + ex);
                }  
        }else{
            showError(AlertType.ERROR, "GABIM DATE", "Data qe dhate eshte e gabuar!", "Zgjidhni daten nepermjet dy kalendareve te dhene\nose shkruaj daten ne formatin dd/mm/vvvv !");
        }
    }
   
    private void showError(AlertType type, String title, String header, String content ){
               Alert alert = new Alert(type);
               alert.setTitle(title);
               alert.setHeaderText(header);
               alert.setContentText(content);
               ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(icon);
               alert.showAndWait();
    }

      
    class MyStringConverter extends StringConverter<LocalDate>{
        String pattern = "dd/MM/yyyy";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        
        @Override public String toString(LocalDate date) {
            if (date != null) {
                return dateFormatter.format(date);
            } else {return "";} }

        @Override public LocalDate fromString(String string) {
            if (string != null && !string.isEmpty()) {
                return LocalDate.parse(string, dateFormatter);
            } else { return null; } 
        }
    };
    
}
