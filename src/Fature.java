
package pharmashitje.WelcomeScreen;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Ndricim Rrapi
 */
public class Fature {
    
    private  StringProperty artikulli;
    private  IntegerProperty cmimi;
    private  IntegerProperty sasia;
    private  IntegerProperty total;
    private  StringProperty data;
   
    public Fature ( String artikulli, int cmimi, int sasia, int total , String data){
       
        this.artikulli = new SimpleStringProperty(artikulli);
        this.cmimi = new SimpleIntegerProperty(cmimi);
        this.sasia    = new SimpleIntegerProperty( sasia);
        this.total     = new SimpleIntegerProperty(total);
        this.data = new SimpleStringProperty(data);
    }
       
     public String getArtikulli() {
        return artikulli.get();
    }

    public void setArtikulli(String artikull) {
        this.artikulli = new SimpleStringProperty(artikull);
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
        return total.get();
    }

    public void setTotal(int tot) {
        this.total= new SimpleIntegerProperty(tot);
    }
   
    public String getData() {
        return data.get();
    }

    public void setData(String shitur) {
        this.data = new SimpleStringProperty(shitur);
    }
    
    @Override public String toString(){
        return getArtikulli();
    }
    
}

