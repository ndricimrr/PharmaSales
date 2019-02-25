/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmashitje.WelcomeScreen;

import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Ndricim Rrapi
 */
public class PharmaShitje extends Application {
    public  Stage stage;
    private Image icon;
    
    public PharmaShitje(){
      icon = new Image(PharmaShitje.class.getResource("icon.png").toExternalForm());
    }
 
    @Override
    public void start(Stage stage) throws Exception {
        SQLiteDB createDB = new SQLiteDB();
        createDB.initDB();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        this.stage = stage;
        stage.getIcons().add(icon ); 
        Scene scene = new Scene(root);
        stage.setTitle("SHOP SALES 1.0");
        stage.setScene(scene);
        stage.show();
       
       // stage.setResizable(false);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
