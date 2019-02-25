/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmashitje.WelcomeScreen;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Ndricim Rrapi
 */
public class Produkt {
    private  StringProperty  artikulli;
    private  StringProperty  njesia;
    private  IntegerProperty cmimi;
    private  IntegerProperty sasia;
    private  IntegerProperty gjendja;
    private  StringProperty  skadenca;
    private  IntegerProperty total ;
    
    public Produkt ( String artikulli, String njesia, int cmimi, int sasia, int gjendja,int total, String data){
       
        this.artikulli = new SimpleStringProperty(artikulli);
        this.njesia    = new SimpleStringProperty(njesia);
        this.cmimi     = new SimpleIntegerProperty(cmimi);
        this.sasia     = new SimpleIntegerProperty( sasia);
        this.total     = new SimpleIntegerProperty(total);
        this.gjendja   = new SimpleIntegerProperty(gjendja);
        this.skadenca  = new SimpleStringProperty(data);
    }
       
    public String getArtikulli() {
        return artikulli.get();
    }

    public void setArtikulli(String artikull) {
        this.artikulli = new SimpleStringProperty(artikull);
    }
    
    public String getNjesia() {
        return njesia.get();
    }

    public void setNjesia(String artikull) {
        this.njesia = new SimpleStringProperty(artikull);
    }
    public int getCmimi() {
        return cmimi.get();
    }

    public void setCmimi(int cmimH) {
        this.cmimi = new SimpleIntegerProperty(cmimH);
    }
    
    public int getSasia() {
        return sasia.get();
    }

    public void setSasia(int cmimD) {
        this.sasia = new SimpleIntegerProperty(cmimD);
    }
    
    public int getTotal() {
        return  total.get();
    }
    
    public void setTotal(int newT){
        this.total = new SimpleIntegerProperty(newT);
    }
    
    public int getGjendja() {
        return gjendja.get();
    }

    public void setGjendja(int tot) {
        this.gjendja= new SimpleIntegerProperty(tot);
    }
    
   
    public String getSkadenca() {
        return skadenca.get();
    }

    public void setSkadenca(String shitur) {
        this.skadenca = new SimpleStringProperty(shitur);
    }
    
    @Override public String toString(){
        return getArtikulli();
    }

}
