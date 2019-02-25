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
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import np.com.ngopal.control.AutoFillTextBox;
import org.apache.commons.lang.WordUtils;
import org.controlsfx.control.textfield.TextFields;

/**
 * FXML Controller class
 *
 * @author Ndricim Rrapi
 */
public class FXMLArtikujtController implements Initializable {
    
    @FXML private TableView<Artikull> artikujtTable;
    @FXML private TableColumn<Artikull, Integer> kodiCol;
    @FXML private TableColumn<Artikull, String> artikulliCol;
    @FXML private TableColumn<Artikull, String> njesiaCol;
    @FXML private TableColumn<Artikull, Integer> cmimHCol;
    @FXML private TableColumn<Artikull, Integer> cmimDCol;
    @FXML private TableColumn<Artikull, Integer> TVSHcol;
    @FXML private TableColumn<Artikull, Integer> gjendjeCol;
    @FXML private TableColumn<Artikull, Integer> totaliCol;
    @FXML private TableColumn<Artikull, String> skadenceCol;
    @FXML private TableColumn<Artikull, String> buttonCol ; 
    @FXML private TextField kodi_Field;
    @FXML private TextField artikulli_Field;
    @FXML private ComboBox njesiaField;
    @FXML private TextField cmimH_Field;
    @FXML private TextField cmimD_Field;
    @FXML private ComboBox TVSH_Field;
    @FXML private TextField gjendja_Field;
    @FXML private DatePicker datePicker;
    @FXML private Button addRowButton;
    @FXML private TextField searchField;
    @FXML private CheckBox kodi_checkbox;;
    StringConverter myConverter;
    @FXML private AutoFillTextBox  box;
    @FXML private VBox vbox;
    private int newCode;
   
    ObservableList<String> popUpList = FXCollections.observableArrayList();
    ObservableList<String> njesiaSuggesions = FXCollections.observableArrayList();
    ObservableList<String> TVSH_Suggesions = FXCollections.observableArrayList();
    private Image icon;
    
    public FXMLArtikujtController(){
      icon = new Image(FXMLArtikujtController.class.getResource("icon.png").toExternalForm());
    }
    
     @Override
    public void initialize(URL url, ResourceBundle rb){
        myConverter =  new MyStringConverter();
        datePicker.setConverter(myConverter);
        
        // set formatters and cell editing functionality
        setIntColProperties("kodi",kodiCol);
        setStringColProperties("artikulli",artikulliCol);
        setStringColProperties("njesia",njesiaCol);
        setIntColProperties("cmimHyrje",cmimHCol);
        setIntColProperties("cmimDalje",cmimDCol);
        setIntColProperties("tvsh",TVSHcol);
        setIntColProperties("gjendje",gjendjeCol);
        totaliCol.setCellValueFactory(new PropertyValueFactory<>("totali"));
        setStringColProperties("skadence",skadenceCol);
        buttonCol.setCellValueFactory(new PropertyValueFactory<>("Deleter"));
        buttonCol.setCellFactory(new ButtonColFactory());
        ObservableList<String> cbValues = FXCollections.observableArrayList("1", "2", "3");
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Generates new code automatically");
        kodi_checkbox.setTooltip(tooltip);
        createListOfSuggestions();
        njesiaField.setItems(njesiaSuggesions);
        njesiaField.getSelectionModel().select(0);
        TextFields.bindAutoCompletion(njesiaField.getEditor(), njesiaSuggesions);
        
        TVSH_Field.setItems(TVSH_Suggesions);
        TVSH_Field.getSelectionModel().select(0);
        TextFields.bindAutoCompletion(TVSH_Field.getEditor(), TVSH_Suggesions);
        
        refreshTable(); 
        
        searchField = TextFields.createClearableTextField();
        TextFields.bindAutoCompletion(searchField, popUpList);
        vbox.getChildren().add( searchField);
        searchField.setPromptText("Search...");  
        searchField.textProperty().addListener(new MySearchFieldListener());
    }
    
    private void deleteCertainRow(Artikull row){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Mesazh Konfirmimi");
        alert.setHeaderText("Deshironi te fshini '" +  row.getArtikulli() +"' ?");

        ButtonType buttonTypeYes= new ButtonType("PO");
        ButtonType buttonTypeNo = new ButtonType("JO", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll( buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeYes){
            artikujtTable.getItems().remove(row);
            showError(AlertType.INFORMATION, "SUKSES", "Fshirja u krye me sukses", "Ju sapo fshite '" + row.getArtikulli() + "'");
        }
    }
    
    @FXML private void checkBoxAction(ActionEvent e){
        if(kodi_Field.isDisabled()){
            kodi_Field.setDisable(false);
        }
        else 
            kodi_Field.setDisable(true);
    }
    
    @FXML private void deleteRow(ActionEvent e){
        try{
            Artikull selectedItem = artikujtTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Mesazh Konfirmimi");
            alert.setHeaderText("Deshironi te fshini '" +  selectedItem.getArtikulli() +"' ?");

            ButtonType buttonTypeYes= new ButtonType("PO");
            ButtonType buttonTypeNo = new ButtonType("JO", ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll( buttonTypeYes, buttonTypeNo);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeYes){
                artikujtTable.getItems().remove(selectedItem);
                showError(AlertType.INFORMATION, "SUKSES", "Fshirja u krye me sukses", "Ju sapo fshite '" + selectedItem.getArtikulli() + "'");
            }
        }catch(NullPointerException ex){
            showError(AlertType.WARNING, "LAJMERIM", "Ju nuk selektuat asnje artikull!", "Selektoni nje artikull dhe provoni perseri!");
        }
    }
   
    @FXML  private void goToHomePage(ActionEvent e) throws IOException{
        Parent home_page_parent = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene home_page_scene = new Scene(home_page_parent);
        Stage app_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        app_stage.hide();
        app_stage.setScene(home_page_scene);
        app_stage.show();
         
    }
   
    private boolean notInteger(KeyEvent e){
        if( !(e.getCode().isDigitKey()) && (e.getCode() != KeyCode.BACK_SPACE) && !(e.getCode().isArrowKey()) && !(e.getCode() == KeyCode.DELETE) && !(e.getCode()== KeyCode.ENTER) && (e.getCode()!= KeyCode.TAB))
            return true;
        else return false;
    }
    
    private boolean oneIsEmpty(){
        if(kodi_Field.isDisabled())
            return ( (artikulli_Field.getText().trim().equals("")) || ((String)(njesiaField.getSelectionModel().getSelectedItem())).trim().equals("") 
                    || (cmimH_Field.getText().trim().equals("")) || (cmimD_Field.getText().trim().equals("")) 
                    || ((String)(TVSH_Field.getSelectionModel().getSelectedItem())).trim().equals("") || (gjendja_Field.getText().trim().equals(""))) ;
        else  
           return ( kodi_Field.getText().trim().equals("")) ||(artikulli_Field.getText().trim().equals(""))
               || ((String)(njesiaField.getSelectionModel().getSelectedItem())).trim().equals("") ||(cmimH_Field.getText().trim().equals("")) 
               || (cmimD_Field.getText().trim().equals("")) || ((String)(TVSH_Field.getSelectionModel().getSelectedItem())).trim().equals("")
               || (gjendja_Field.getText().trim().equals("")) ;
    }
       
    @FXML private void onEnterKodi(KeyEvent e){
        if(notInteger(e)){    
            kodi_Field.setEditable(false);
            showError(AlertType.ERROR, "WARNING", "WRONG CODE", "The code should be a number" );
            kodi_Field.setEditable(true);
        }
        else if (e.getCode()== KeyCode.ENTER || e.getCode()== KeyCode.TAB ){
            if(checkIfInt(kodi_Field.getText())== null){
                kodi_Field.clear();
            } 
            else if (checkIfInt(kodi_Field.getText()) != null){
                if(checkIfInt(kodi_Field.getText()) <= 0 ){
                    showError(AlertType.ERROR, "GABIM", "GABIM NUMERIK!", "Nuk lejohen numrat negativ dhe 0!" );
                      kodi_Field.clear();
                }
                else artikulli_Field.requestFocus();
            }
        }   
    }
       
    @FXML private void onEnterArtikulli(KeyEvent e){
        if (e.getCode()== KeyCode.ENTER || e.getCode()== KeyCode.TAB){
            if(artikulli_Field.getText().trim().equals("")){
                showError(AlertType.ERROR, "GABIM", "Kujdes!", "Ju lutem plotesoni fushen e Artikullit!" );
            }
            else njesiaField.requestFocus();
        }   
    }

    @FXML private void onEnterNjesia(KeyEvent e){
        if (e.getCode()== KeyCode.ENTER || e.getCode()== KeyCode.TAB){
            if(((String)njesiaField.getSelectionModel().getSelectedItem()).trim().equals("")){
                showError(AlertType.ERROR, "GABIM", "Kujdes!", "Ju lutem plotesoni fushen e Njesise!" );
            }
            else  cmimH_Field.requestFocus();
        }
    }
    
    @FXML private void onEnterCmimH(KeyEvent e){
        if(notInteger(e)){  
            cmimH_Field.setEditable(false);
            showError(AlertType.ERROR, "GABIM", "VENDOSJE E GABUAR E 'CMIMIT HYRES'", "CMIMI DUHET TE JETE NUMER" );
            cmimH_Field.setEditable(true);
        }
        else if (e.getCode()== KeyCode.ENTER || e.getCode()== KeyCode.TAB){
           if(checkIfInt(cmimH_Field.getText())== null){
                cmimH_Field.clear();
            } 
            else if (checkIfInt(cmimH_Field.getText()) != null){
                if(checkIfInt(cmimH_Field.getText()) <= 0 ){
                    showError(AlertType.ERROR, "GABIM", "GABIM NUMERIK!", "Nuk lejohen numrat negativ dhe 0!" );
                    cmimH_Field.clear();
                }
                else cmimD_Field.requestFocus();
            }
        }
    }
    
    @FXML private void onEnterCmimD(KeyEvent e){
        if(notInteger(e)){  
            cmimD_Field.setEditable(false);
            showError(AlertType.ERROR, "GABIM", "VENDOSJE E GABUAR E 'Cmimit Dales'", "'Cmimi Dales' DUHET TE JETE NUMER" );
            cmimD_Field.setEditable(true);
        }
        else if (e.getCode()== KeyCode.ENTER || e.getCode()== KeyCode.TAB){
            if(checkIfInt(cmimD_Field.getText())== null){
                cmimD_Field.clear();
            } 
            else if (checkIfInt(cmimD_Field.getText()) != null){
                if(checkIfInt(cmimD_Field.getText()) <= 0 ){
                    showError(AlertType.ERROR, "GABIM", "GABIM NUMERIK!", "Nuk lejohen numrat negativ dhe 0!" );
                      cmimD_Field.clear();
                }
                else
                    TVSH_Field.requestFocus();
            }
        }
    }
    
    @FXML private void onEnterTVSH(KeyEvent e){
        if(notInteger(e)){    
            TVSH_Field.setEditable(false);
            showError(AlertType.ERROR, "GABIM", "VENDOSJE E GABUAR E TVSH", "TVSH DUHET TE JETE NUMER" );
            TVSH_Field.setEditable(true);
        }
        else if (e.getCode()== KeyCode.ENTER || e.getCode()== KeyCode.TAB){
            if(checkIfInt(((String)(TVSH_Field.getSelectionModel().getSelectedItem()))) == null){
                TVSH_Field.getSelectionModel().clearSelection();
            }else if (checkIfInt(((String)(TVSH_Field.getSelectionModel().getSelectedItem()))) != null){
                if(checkIfInt(((String)(TVSH_Field.getSelectionModel().getSelectedItem()))) < 0 ){
                    showError(AlertType.ERROR, "GABIM", "GABIM NUMERIK!", "Nuk lejohen numrat negativ!" );
                    TVSH_Field.getSelectionModel().clearSelection(newCode);
                }
                else
                    gjendja_Field.requestFocus();
            }
        }
    }
    
    @FXML private void onEnterGjendja(KeyEvent e){
        if(notInteger(e)){   
            gjendja_Field.setEditable(false);
            showError(AlertType.ERROR, "GABIM", "VENDOSJE E GABUAR E 'Gjendjes'", "'Gjendja' DUHET TE JETE NUMER" );
            gjendja_Field.setEditable(true);
        }
        else if (e.getCode()== KeyCode.ENTER || e.getCode()== KeyCode.TAB){
            if(checkIfInt(gjendja_Field.getText())== null){
                gjendja_Field.clear();
            } 
            else if (checkIfInt(gjendja_Field.getText()) != null){
                if(checkIfInt(gjendja_Field.getText()) <= 0 ){
                    showError(AlertType.ERROR, "GABIM", "GABIM NUMERIK!", "Nuk lejohen numrat negativ dhe 0!" );
                    gjendja_Field.clear();
                }
                else
                    datePicker.requestFocus();
            }
        }
    }
    
    @FXML private void onEnterSkadenca(KeyEvent e){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedString = null;
        try{
            formattedString = datePicker.getValue().format(formatter);
        }catch(NullPointerException b){
            showError(AlertType.ERROR, "GABIM", "DATA E SKADENCES NUK ESHTE E SAKTE.", "ERROR: "+ b+"\nSIGUROHUNI QE DATA TE JETE E PLOTE \nDHE JO E KALUAR p.sh: 01/02/2020 ");
        }
        if (e.getCode()== KeyCode.ENTER || e.getCode()== KeyCode.TAB){
            if(isValidDate(formattedString)){
                addRowButton.requestFocus();
            }
            else{
                String retry = "KUJDES\nPlotesoni nje date te formatit -> date/muaj/viti\nPerdorni zero perpara dates dhe muajit\nGABIM -> 1/2/2019 \nE SAKTE -> 01/02/2019\nGjithashtu sigurohuni qe data nuk eshte e kaluar!";
                showError(AlertType.WARNING, "INFORMACION", "INFORMACION MBI VENDOSJEN E SKADENCES '" +formattedString +"' NUK ESHTE E SAKTE.", retry);
            }
        }
    }
    
    @FXML private void onEnterButton(KeyEvent e){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedString = null;
        try{
            formattedString = datePicker.getValue().format(formatter);
        }catch(NullPointerException b){
            showError(AlertType.ERROR, "GABIM DATE", "DATA E SKADENCES NUK ESHTE E SAKTE.\nPROVONI NJE DATE TE SAKTE", "ERROR: "+ b);
        }
        if (e.getCode()== KeyCode.ENTER){
            if(isValidDate(formattedString))
            {
                if(chechAllInts())
                   addToTable();
            }
        }
    }
    
    @FXML private void addRowToTable(ActionEvent event) {
        if(chechAllInts())
                   addToTable();
    }         
   
    private void addToTable(){
        if(datePicker.getValue() == null){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Gabim");
            alert.setHeaderText("FUSHA E SKADENCES BOSH!");
            alert.setContentText("Ju lutem plotesoni Daten e Skadences!");
            alert.showAndWait();
        }
        else if (oneIsEmpty()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Gabim");
            alert.setHeaderText("Kujdes!");
            alert.setContentText("Ju lutem plotesoni te gjitha fushat e kerkuara!");
            alert.showAndWait();
        }
        else{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedString = datePicker.getValue().format(formatter);
            if (isValidDate(formattedString)){
                if( checkIfArtikulliDoesntExist(WordUtils.capitalizeFully(artikulli_Field.getText()))){
                    if(kodi_Field.isDisabled()){
                        newCode = generateRandomNuber();
                        System.out.println("out->" + newCode);
                    }
                    else if(!kodi_Field.isDisabled()){
                        newCode = Integer.parseInt(kodi_Field.getText());
                    }
                    if( checkIfKodiDoesntExist(newCode)){   
                        if(Integer.parseInt(cmimH_Field.getText()) < Integer.parseInt(cmimD_Field.getText())){
                        Artikull artikull = new Artikull(
                            newCode,
                            WordUtils.capitalizeFully(artikulli_Field.getText()),
                            ((String)njesiaField.getSelectionModel().getSelectedItem()).toLowerCase(),
                            Integer.parseInt(cmimH_Field.getText()),
                            Integer.parseInt( cmimD_Field.getText()),
                            Integer.parseInt(((String)(TVSH_Field.getSelectionModel().getSelectedItem()))),
                            Integer.parseInt( gjendja_Field.getText()),
                            Integer.parseInt(gjendja_Field.getText())*Integer.parseInt(cmimD_Field.getText()),
                            formattedString);
                        
                        try{
                            Connection conn = SQLiteDB.dbConnector();
                            String sql = "INSERT INTO artikujt(kodi,artikulli,njesia,cmimHyrje,cmimDalje,tvsh,gjendje,total,skadence) VALUES(?,?,?,?,?,?,?,?,?)";
                            PreparedStatement stmt = conn.prepareStatement(sql);
                            stmt.setInt(1,artikull.getKodi());
                            stmt.setString(2,artikull.getArtikulli());
                            stmt.setString(3,artikull.getNjesia());
                            stmt.setInt(4,artikull.getCmimHyrje());
                            stmt.setInt(5,artikull.getCmimDalje());
                            stmt.setInt(6,artikull.getTvsh());
                            stmt.setInt(7,artikull.getGjendje() );
                            stmt.setInt(8,artikull.getGjendje()*artikull.getCmimDalje());
                            stmt.setString(9,artikull.getSkadence() );
                             System.out.println("Inserting...");
                            stmt.executeUpdate();
                            stmt.close();
                            conn.close();
                        } catch (SQLException ex) {
                            showError(AlertType.ERROR, "GABIM", "GABIM NE INSERT DATABAZE", "ERROR: " + ex);
                        } 
                        artikujtTable.getItems().add(artikull);
                        popUpList.add(artikull.getArtikulli());
                        showError(AlertType.INFORMATION, "INFO", "RREGJISTRIMI U KRYE ME SUKSES", "\""+artikull.getArtikulli() + "\" eshte shtuar ne databaze :)");
                        }
                        else{
                            showError(AlertType.ERROR, "GABIM TEORIK", "PRODUKTI NUK U RREGJISTRUA !", "Cmimi hyres: "+ Integer.parseInt(cmimH_Field.getText()) 
                            + "\nCmimi dales: " + Integer.parseInt(cmimD_Field.getText()) + "\nCMIM DALES DUHET TE JETE ME I MADH SE CMIMI DALES !!");
                        }             
                    }
                }
            } else {
                    showError(AlertType.ERROR, "GABIM", "GABIM DATE SKADENCE", formattedString + " NUK ESHTE DATE E VLEFSHME SKADENCE. PROVONI NJE DATE TE SAKTE");
            }
        }
    }
    
    private void refreshTable(){
        artikujtTable.getItems().clear();
        try{
            Connection conn = SQLiteDB.dbConnector();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM artikujt;");
           
            while(rs.next()){
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
                artikujtTable.getItems().add(artikull);
                popUpList.add(artikull.getArtikulli());
                   
                
            }
            rs.close();
            stmt.close();
            conn.close();
            //myList = artikujtTable.getItems();
             
          } catch (SQLException ex) {
            System.err.println("Error"+ex);
        }
    }
    
    @FXML private void refreshAction(ActionEvent e){
        refreshTable();
    }
    
    @FXML private void showDate(ActionEvent e){
           //datePicker.setConverter(myConverter);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            if (datePicker.getValue() != null){
            String formattedString = datePicker.getValue().format(formatter);
            System.out.println(formattedString);}
    }
       
    public void createListOfSuggestions(){
        njesiaSuggesions.addAll("cope", "flakon","pako","kg","ml","gr","kuti",
               "tubet","bidon","bolus","litra","ampula","shirit","doze","tableta",
               "pipeta","meter","bombul","fije","kanace","kubik","qese","shiringe","vazo");
        TVSH_Suggesions.addAll("5","10","15","20","25","30");
    }
    
    class MySearchFieldListener implements  ChangeListener<String>{
         @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            search(newValue);
        }
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
    
    class ButtonColFactory implements Callback<TableColumn<Artikull, String>, TableCell<Artikull, String>> {
        @Override
        public TableCell<Artikull, String> call(TableColumn<Artikull, String> param) {
            final TableCell<Artikull, String> cell = new TableCell<Artikull, String>() {
                    final Button btn = new Button("Delete");
                 
                    @Override
                    public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        btn.setOnAction(event -> {
                            Artikull row = getTableView().getItems().get(getIndex());
                            deleteCertainRow(row);
                            deleteRowFromDB(row.getArtikulli());
                        });
                        setGraphic(btn);
                        setText(null);
                    }
                }
            };
            return cell;
        }        
    }
              
    class ComboBoxColFactory implements Callback<TableColumn<Artikull, String>, TableCell<Artikull, String>> {
        @Override
        public TableCell<Artikull, String> call(TableColumn<Artikull, String> param) {
            final TableCell<Artikull, String> cell = new MyTableCell() ; 
            return cell;
        }        
    }
    
    class MyTableCell extends TableCell<Artikull, String>{
        final Button btn = new Button();

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    }else {
                        System.out.println(item + "___________");
                         
                         btn.setOnAction(event -> {
                            Artikull row = getTableView().getItems().get(getIndex());
                        //    deleteCertainRow(row);
                        });
                        // getTableView().getSelectionModel().get
                        setGraphic(btn);
                        setText(null);
                    }
                }
    }
  
    private void search(String keyword){
        try{
            Connection conn = SQLiteDB.dbConnector();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM artikujt WHERE artikulli LIKE '%" + keyword  +"%';");
            artikujtTable.getItems().clear();
            while(rs.next()){
                artikujtTable.getItems().add(
                    new Artikull(
                    rs.getInt("kodi"),
                    rs.getString("artikulli"),
                    rs.getString("njesia"),
                    rs.getInt("cmimHyrje"),
                    rs.getInt("cmimDalje"),
                    rs.getInt("tvsh"),
                    rs.getInt("gjendje"),
                     rs.getInt("cmimDalje")* rs.getInt("gjendje"),
                    rs.getString("skadence")));
            }
            rs.close();
            stmt.close();
            conn.close();
             
          } catch (SQLException ex) {
            System.err.println("Error"+ex);
        }
   }
    
    private void setIntColProperties(String colName, TableColumn<Artikull, Integer> thisColumn){
        thisColumn.setCellValueFactory(new PropertyValueFactory<Artikull, Integer>(colName));
        thisColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MyIntegerStringConverter()));
        thisColumn.setOnEditCommit(new IntegerHandler(colName));
    }
   
    private void setStringColProperties(String colName, TableColumn<Artikull, String> thisColumn){
        thisColumn.setCellValueFactory(new PropertyValueFactory<Artikull, String>(colName));
        thisColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        thisColumn.setOnEditCommit(new StringHandler(colName));
    }
    
    class  StringHandler implements EventHandler<CellEditEvent<Artikull, String>> {
        private String column;
        
        public StringHandler(String column){
            this.column = column;
        }
        
        @Override
        public void handle(CellEditEvent<Artikull, String> t) {
            if (column.contains("artikulli")){
                try{        
                    String newValue = t.getNewValue();
                    String oldValue = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getArtikulli();
                    if (!"".equals(newValue)){
                        if( checkIfArtikulliDoesntExist(newValue)){
                            ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).setArtikulli(WordUtils.capitalizeFully(t.getNewValue()));
                            try (Connection conn = SQLiteDB.dbConnector()) {
                                String sql =  "UPDATE artikujt SET artikulli = ? WHERE artikulli  = ? ;";
                                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                    stmt.setString(1,WordUtils.capitalizeFully(newValue));
                                    stmt.setString(2,oldValue);
                                    stmt.executeUpdate();
                                }
                            }
                        }
                    }
                    else{
                        ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).setArtikulli(WordUtils.capitalizeFully(oldValue));
                        showError(AlertType.ERROR, "Gabim", "FUSHA ESHTE BOSH", "JU LUTEM PLOTESONI FUSHEN E ARTIKULLIT" );
                    }
                }catch(SQLException e){
                    showError(AlertType.ERROR, "Gabim", "Gabim ne rifreskim databaze!", "ERROR: "+ (e) );
                }
            }
            else if(column.contains("njesia")){
               try{
                    String newValue = t.getNewValue();
                    String oldValue = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getArtikulli();
                    String oldValueN = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getNjesia();
                    if (!"".equals(newValue)){
                        ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).setNjesia(t.getNewValue().toLowerCase());
                        try (Connection conn = SQLiteDB.dbConnector()) {
                            String sql =  "UPDATE artikujt SET njesia = ? WHERE artikulli  = ? ;";
                            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setString(1,newValue.toLowerCase());
                                stmt.setString(2,oldValue);
                                stmt.executeUpdate();
                            }
                        }
                   }
                   else{
                       ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).setNjesia(oldValueN.toLowerCase());
                        showError(AlertType.ERROR, "Gabim", "FUSHA ESHTE BOSH", "JU LUTEM PLOTESONI FUSHEN E NJESISE" );
                   }
                }catch(SQLException e){
                    System.err.println("Database connection error: " + e );
                    showError(AlertType.ERROR, "Gabim", "Gabim ne rifreskim databaze!", "ERROR: "+ (e) );
                }
            }
            else if(column.contains("skadence")){
                String oldValue = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getArtikulli();
                String oldValueN = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getSkadence();
                String newValue = t.getNewValue();
                if (isValidDate( newValue)){
                    if(returnFixed(newValue) != null){
                        System.out.println("Changed from " + newValue +" to  ");
                        newValue = returnFixed(newValue);
                        System.out.println(newValue);
                    }
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    LocalDate date = LocalDate.parse(newValue, dateFormatter);
                    LocalDate now = LocalDate.now();
                    try{
                        System.out.println( "this-> " + oldValue);
                        System.out.println("new-> "+ newValue);
                        try (Connection conn = SQLiteDB.dbConnector()) {
                            String sql =  "UPDATE artikujt SET skadence = ? WHERE artikulli  = ? ;";
                            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setString(1,newValue);
                                stmt.setString(2,oldValue);
                                stmt.executeUpdate();
                            }
                        }
                    }catch(SQLException e){
                        System.err.println("Database connection error: " + e );
                        showError(AlertType.ERROR, "Gabim", "Gabim ne rifreskim databaze!", "ERROR: "+ (e) );
                    }
                }
                else {
                    ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).setSkadence(oldValueN);
                    showError(AlertType.ERROR, "GABIM", "DATE E GABUAR!", "'"+newValue +  "' nuk eshte date e vlefshme");
                }
            }
        }
    }
  
    class IntegerHandler implements EventHandler<CellEditEvent<Artikull, Integer>> {
      String column;
        public IntegerHandler(String column) {
         this.column = column;
        }
        
        @Override
        public void handle(CellEditEvent<Artikull, Integer> t){
            if (column.contains("kodi")){
                String oldValue = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getArtikulli();
                if (t.getNewValue()!= null){
                    int newValue = t.getNewValue();
                    if( newValue > 0){
                        if ( checkIfKodiDoesntExist(newValue)){
                            try (Connection conn = SQLiteDB.dbConnector()) {
                                String sql =  "UPDATE artikujt SET kodi = ? WHERE artikulli  = ? ;";
                                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                    stmt.setInt(1,newValue);
                                    stmt.setString(2,oldValue);
                                    stmt.executeUpdate();
                                }
                            }catch(SQLException e){
                                showError(AlertType.ERROR, "Gabim", "Gabim ne rifreskim databaze!", "ERROR: "+ (e) );
                            }
                        }
                    }else  
                        showError(AlertType.ERROR, "Gabim", "Gabim Numerik!", "Nuk lejohen 0 dhe numrat negativ si: " + newValue );
                }
            }
        
            else if(column.contains("cmimHyrje")){
                String oldValue = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getArtikulli();
                int    cmimD = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getCmimDalje();
                if (t.getNewValue()!= null){
                    int newValue = t.getNewValue();
                    if(newValue > 0){
                        if (newValue < cmimD){
                            try (Connection conn = SQLiteDB.dbConnector()) {
                                String sql =  "UPDATE artikujt SET cmimHyrje = ? WHERE artikulli  = ? ;";
                                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                    stmt.setInt(1,newValue);
                                    stmt.setString(2,oldValue);
                                    stmt.executeUpdate();
                                }
                            }catch(SQLException e){
                                showError(AlertType.ERROR, "Gabim", "Gabim ne rifreskim databaze!", "ERROR: "+ (e) );
                            }
                        }else{
                             showError(AlertType.ERROR, "GABIM TEORIK", "NDRYSHIMI NUK U KRYE!", "Cmimi hyres: " + newValue + "\nCmimi dales: " + cmimD
                            + " \nCmimi Hyres duhet te jete me i vogel se Cmimi Dales!");
                        }  
                    }else
                        showError(AlertType.ERROR, "Gabim", "Gabim Numerik!", "Nuk lejohen 0 dhe numrat negativ si: " + newValue );
                }
            }
            else if(column.contains("cmimDalje")){
                String oldValue = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getArtikulli();
                int cmimH = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getCmimHyrje();
                if (t.getNewValue()!= null){
                    int newValue = t.getNewValue();
                    if(newValue > 0){
                        if(newValue > cmimH){
                            try (Connection conn = SQLiteDB.dbConnector()) {
                                String sql =  "UPDATE artikujt SET cmimDalje = ? WHERE artikulli  = ? ;";
                                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                    stmt.setInt(1,newValue);
                                    stmt.setString(2,oldValue);
                                    stmt.executeUpdate();
                                }
                            }catch(SQLException e){
                                showError(AlertType.ERROR, "Gabim", "Gabim ne rifreskim databaze!", "ERROR: "+ (e) );
                            }
                        }else{
                            showError(AlertType.ERROR, "GABIM TEORIK", "NDRYSHIMI NUK U KRYE!", "Cmimi hyres: " + cmimH  + "\nCmimi dales: " + newValue
                            + " \nCmimi Dales duhet te jete me i madh se Cmimi Hyres!");
                        }
                    }else
                        showError(AlertType.ERROR, "Gabim", "Gabim Numerik!", "Nuk lejohen 0 dhe numrat negativ si: " + newValue );
                }
            }
            else if(column.contains("tvsh")){
                String oldValue = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getArtikulli();
                if (t.getNewValue()!= null){
                    int newValue = t.getNewValue();
                    if(newValue >= 0)
                        try (Connection conn = SQLiteDB.dbConnector()) {
                            String sql =  "UPDATE artikujt SET tvsh = ? WHERE artikulli  = ? ;";
                            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setInt(1,newValue);
                                stmt.setString(2,oldValue);
                                stmt.executeUpdate();
                            }
                        }catch(SQLException e){
                            showError(AlertType.ERROR, "Gabim", "Gabim ne rifreskim databaze!", "ERROR: "+ (e) );
                        }
                    else
                        showError(AlertType.ERROR, "Gabim", "Gabim Numerik!", "Nuk lejohen numrat negativ si: " + newValue );
                }
            }
            else if(column.contains("gjendje")){
                String oldValue = ((Artikull) t.getTableView().getItems().get(t.getTablePosition().getRow())).getArtikulli();
                if (t.getNewValue()!= null){
                    int newValue = t.getNewValue();
                    if(newValue > 0)
                        try (Connection conn = SQLiteDB.dbConnector()) {
                            String sql =  "UPDATE artikujt SET gjendje = ? WHERE artikulli  = ? ;";
                            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setInt(1,newValue);
                                stmt.setString(2,oldValue);
                                stmt.executeUpdate();
                            }
                        }catch (SQLException ex) {
                            showError(AlertType.ERROR, "Gabim", "Gabim ne rifreskim databaze!", "ERROR: "+ (ex) );
                        }
                    else{
                        showError(AlertType.ERROR, "Gabim", "Gabim Numerik!", "Nuk lejohen 0 dhe numrat negativ si: " + newValue );
                    }
                } 
            }
        }
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
            showError(AlertType.ERROR, "ERROR", "NUMBER FORMAT EXECEPTION!! " , "You wrote: '" + value + "'\nThis field requires a number!" );
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
   
    private void showError(AlertType type, String title, String header, String content ){
               Alert alert = new Alert(type);
               alert.setTitle(title);
               alert.setHeaderText(header);
               alert.setContentText(content);
               ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(icon);
               alert.showAndWait();
    }

    private  boolean isValidDate(String input) {
        String formatString = "yyyy/MM/dd";
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            if(returnFixed(input) != null){
                input = returnFixed(input);
            }
            format.setLenient(false);
            format.parse(input);
            System.out.println(Integer.parseInt(input.substring(0, 4)) + " > " + Calendar.getInstance().get(Calendar.YEAR));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate date = LocalDate.parse(input, dateFormatter);
            LocalDate now = LocalDate.now();
            if (!date.isAfter(now)){
                showError(AlertType.ERROR, "DATE ERROR", "PAST DATE ENTERED!", "PLEASE ENTER A VALID EXPIRY DATE!" );
              return false;
            }
        } catch (NullPointerException |DateTimeParseException |ParseException  | IllegalArgumentException e ) {
            return false;
        }
        return true;
    }
    
    // gets String date and fixes from 2012/1/1 to 2012/01/01 to skip adding columns of date and better sorting
    private String returnFixed(String input){
    String a = null ;
    try{
        if(input.length() == 9){
            if(input.substring(6,7).contains("/")){
                return input.substring(0,5) + "0" + input.substring(5,6) + input.substring(6,input.length());
            }
            else 
                return input.substring(0,8) + "0" +input.substring(8,input.length());
        }
        else if (input.length() == 8)
            return input.substring(0,5) + "0" + input.substring(5,6) + input.substring(6,7) + "0" + input.substring(7, input.length());
    }catch(NullPointerException ex){
        return null;
    }
    
    return null;	
    }

    private Integer checkIfInt(String value) {
        try{
            if (value == null) throw new NullPointerException();
            value = value.trim();
            if (value.length() < 1) throw new NullPointerException();
            if ("".equals(value))   throw new NullPointerException();
            return Integer.valueOf(value);
        }catch(NullPointerException| NumberFormatException e){
            showError(AlertType.ERROR, "ERROR", "NUMBER FORMAT EXECEPTION! " , "You wrote: '" + value + "'\nPlease write a number!" );
            return null;
        }
    }
    
    private boolean chechAllInts(){
        if (!kodi_Field.isDisabled()){
            if (checkIfInt(kodi_Field.getText())  == null || checkIfInt(cmimH_Field.getText()) == null ||
                checkIfInt(cmimD_Field.getText()) == null || ((String)(TVSH_Field.getSelectionModel().getSelectedItem())) == null ||
                    checkIfInt(gjendja_Field.getText()) == null ){
                    kodi_Field.clear();  cmimD_Field.clear(); cmimH_Field.clear();  TVSH_Field.getSelectionModel().clearSelection(); gjendja_Field.clear();
                    return false;
            }
            else if (checkIfInt(kodi_Field.getText()) <= 0 || checkIfInt(cmimH_Field.getText()) <= 0 ||
                     checkIfInt(cmimD_Field.getText()) <= 0 || checkIfInt(((String)(TVSH_Field.getSelectionModel().getSelectedItem()))) < 0 || checkIfInt(gjendja_Field.getText()) <=0 ){
                kodi_Field.clear();  cmimD_Field.clear(); cmimH_Field.clear();  TVSH_Field.getSelectionModel().clearSelection(); gjendja_Field.clear();
                showError(AlertType.ERROR, "GABIM", "GABIM NUMERIK", "NUK LEJOHEN NUMRAT NEGATIVE");
                return false;
            }
        }
        else{
            if ( checkIfInt(cmimH_Field.getText()) == null || checkIfInt(cmimD_Field.getText()) == null ||
                checkIfInt(((String)(TVSH_Field.getSelectionModel().getSelectedItem()))) == null  || checkIfInt(gjendja_Field.getText()) == null ){
                cmimD_Field.clear(); cmimH_Field.clear();  TVSH_Field.getSelectionModel().clearSelection(); gjendja_Field.clear();
                return false;
            }
            else if (checkIfInt(cmimH_Field.getText()) <= 0 || checkIfInt(cmimD_Field.getText()) <= 0 ||
                     checkIfInt(((String)(TVSH_Field.getSelectionModel().getSelectedItem()))) < 0 || checkIfInt(gjendja_Field.getText()) <=0 ){
                cmimD_Field.clear(); cmimH_Field.clear();  TVSH_Field.getSelectionModel().clearSelection(); gjendja_Field.clear();
                showError(AlertType.ERROR, "GABIM", "GABIM NUMERIK", "NUK LEJOHEN NUMRAT NEGATIVE");
                return false;
            }
        }
        return true;
    }

    private boolean checkIfArtikulliDoesntExist(String field){
        boolean verify = true;
        try{    
            Connection conn = SQLiteDB.dbConnector();
            String sql = "SELECT * FROM artikujt WHERE artikulli = ? ;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, field);
            System.out.println("para if = " +verify);
            System.out.println("para if stmt.exeq = " + stmt.executeQuery().next());
            if( stmt.executeQuery().next() ){
                showError(AlertType.ERROR, "GABIM", "PROVONI EMER TJETER!", field + " EKZISTON!\nNUK MUND TE SHTONI DY ARTIKUJ ME TE NJEJTIN EMER !");
                verify = false;
                artikulli_Field.requestFocus();
                System.out.println("verify hyn brenda dhe behet false");
            }
            stmt.close();
            conn.close();
        }catch(SQLException e){
            System.out.println("error: "  + e);
        }
        System.out.println("verify at the end = " + verify);
        return verify;
    }
    
    private boolean checkIfKodiDoesntExist(int field){
        boolean verify = true;
        try{    
            Connection conn = SQLiteDB.dbConnector();
            String sql = "SELECT * FROM artikujt WHERE kodi = ? ;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, field);
            
            if( stmt.executeQuery().next() ){
                showError(AlertType.ERROR, "GABIM", "PROVONI KOD TJETER!", field + " EKZISTON!\nNUK MUND TE SHTONI DY ARTIKUJ ME TE NJEJTIN KOD !");
                verify = false;
               // kodi_Field.requestFocus();
            }
            stmt.close();
            conn.close();
        }catch(SQLException e){
            System.out.println("error: "  + e);
        }
     return verify;   
    }
     
    private void deleteRowFromDB(String name){
         try{    
            Connection conn = SQLiteDB.dbConnector();
            String sql = "DELETE FROM artikujt WHERE artikulli = ? ;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        }catch(SQLException e){
            System.out.println("error: "  + e);
        }
    }

    private Integer generateRandomNuber() {
        int newInteger = 0 ;
        boolean tableIsEmpty = true;
        try{
            String sql = "select kodi from artikujt order by kodi desc limit 1;";
            Connection c = SQLiteDB.dbConnector();
            Statement  stmt = c.createStatement();
            ResultSet set = stmt.executeQuery(sql);
            if (set.next()){
               newInteger =  set.getInt("kodi");
               tableIsEmpty = false; // query didn't return null so table aint empty
               newInteger = newInteger + 1;
            }
            set.close();
            stmt.close();
            c.close();
        }catch(SQLException E){
            showError(AlertType.ERROR, "DESHTIM", "NUK U ARRIT" +
            "TE KRIJOHET NJE KOD ARTIKULLI AUTOMATIKISHT", "PROVONI PERSERI\n"+
            "NE RAST SE PROBLEMI VAZHDON VENDOSNI MANUALISHT NJE KOD ARTIKULLI\nFALEMINDERIT!");
        }
        if (tableIsEmpty){
            newInteger = 1;
        }
        System.out.println(newInteger);
       return newInteger;
    }
}