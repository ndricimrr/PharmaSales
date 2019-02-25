/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmashitje.WelcomeScreen;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Ndricim Rrapi
 */
public class Artikull {
    //private  IntegerProperty id;
    private  IntegerProperty kodi;
    private  StringProperty artikulli;
    private  StringProperty njesia;
    private  IntegerProperty cmimHyrje;
    private  IntegerProperty cmimDalje;
    private IntegerProperty tvsh;
    private  IntegerProperty gjendje;
    private  IntegerProperty totali;
    private  StringProperty skadence;
   
    public Artikull ( int kodi, String artikulli, String njesia, int cmim_hyrje, int cmim_dalje, int tvsh, int gjendje, int totali, String skadence){
       
        this.kodi     = new SimpleIntegerProperty(kodi);
        this.artikulli = new SimpleStringProperty(artikulli);
        this.njesia   = new SimpleStringProperty(njesia);
        this.cmimHyrje = new SimpleIntegerProperty(cmim_hyrje);
        this.cmimDalje    = new SimpleIntegerProperty( cmim_dalje);
        this.tvsh     = new SimpleIntegerProperty(tvsh);
        this.gjendje  = new SimpleIntegerProperty(gjendje);
        this.totali   = new SimpleIntegerProperty(totali);
        this.skadence = new SimpleStringProperty(skadence);
    }
       
     public int getKodi() {
        return kodi.get();
    }

    public void setKodi(int kodi) {
        this.kodi = new SimpleIntegerProperty(kodi);
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

    public void setNjesia(String njesia) {
        this.njesia = new SimpleStringProperty(njesia);
    }
    
    public int getCmimHyrje() {
        return cmimHyrje.get();
    }

    public void setCmim_Hyrje(int cmimH) {
        this.cmimHyrje = new SimpleIntegerProperty(cmimH);
    }
    
    public int getCmimDalje() {
        return cmimDalje.get();
    }

    public void setCmim_Dalje(int cmimD) {
        this.cmimDalje = new SimpleIntegerProperty(cmimD);
    }
    
    public int getTvsh() {
        return tvsh.get();
    }

    public void setTvsh(int TVSH) {
        this.tvsh = new SimpleIntegerProperty(TVSH);
    }
    
     public int getGjendje() {
        return gjendje.get();
    }

    public void setGjendje(int gjendje) {
        this.gjendje = new SimpleIntegerProperty(gjendje);
    }
    
    public int getTotali() {
        return totali.get();
    }

    public void setTotali(int totali) {
        this.totali = new SimpleIntegerProperty(totali);
    }
    
    public String getSkadence() {
        return skadence.get();
    }

    public void setSkadence(String skadence) {
        this.skadence = new SimpleStringProperty(skadence);
    }
    
    @Override public String toString(){
        return getArtikulli();
    }
    
}
