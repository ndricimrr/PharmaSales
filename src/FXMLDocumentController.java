/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmashitje.WelcomeScreen;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author Ndricim Rrapi
 */
public class FXMLDocumentController implements Initializable {
    
   
  
     @FXML
    public void goToArtikujtPage(ActionEvent e) throws IOException{
        Parent home_page_parent = FXMLLoader.load(getClass().getResource("FXMLArtikujt.fxml"));
        Scene home_page_scene = new Scene(home_page_parent);
        Stage app_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        app_stage.hide();
        app_stage.setScene(home_page_scene);
        app_stage.show();
        app_stage.setMaximized(true);
    }
    
    @FXML
    public void goToSkadencatPage(ActionEvent e) throws IOException{
        Parent home_page_parent = FXMLLoader.load(getClass().getResource("FXMLSkadencat.fxml"));
        Scene home_page_scene = new Scene(home_page_parent);
        Stage app_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        app_stage.hide();
        app_stage.setScene(home_page_scene);
        app_stage.show();
        app_stage.setMaximized(true);
    }
    
    @FXML
    public void goToFaturatPage(ActionEvent e) throws IOException{
        Parent home_page_parent = FXMLLoader.load(getClass().getResource("FXMLFaturat.fxml"));
        Scene home_page_scene = new Scene(home_page_parent);
        Stage app_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        app_stage.hide();
        app_stage.setScene(home_page_scene);
        app_stage.show();
        app_stage.setMaximized(true);
    }
    
       @FXML
    public void goToShitjetPage(ActionEvent e) throws IOException{
        Parent home_page_parent = FXMLLoader.load(getClass().getResource("FXMLShitjet.fxml"));
        Scene home_page_scene = new Scene(home_page_parent);
        Stage app_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        app_stage.hide();
        app_stage.setScene(home_page_scene);
        app_stage.show();
        app_stage.setMaximized(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }    
    
}
