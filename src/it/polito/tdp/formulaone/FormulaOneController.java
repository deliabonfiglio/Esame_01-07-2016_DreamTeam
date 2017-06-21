package it.polito.tdp.formulaone;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.db.FormulaOneDAO;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import it.polito.tdp.formulaone.model.Season;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FormulaOneController {
	
	Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Season> boxAnno;

    @FXML
    private TextField textInputK;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	
    	Season s= boxAnno.getValue();
    	if(s==null){
    		txtResult.appendText("Errore, selezionare anno\n");
    		return;
    	}
    	
    	model.creaGrafo(s);
    	Driver best = model.getBestDriver();
    	
    	txtResult.appendText("Il pilota migliore del "+s.getYear()+" è "+best.toString()+"\n");
    }

    @FXML
    void doTrovaDreamTeam(ActionEvent event) {
    	txtResult.clear();
    	
    	String ks= textInputK.getText();
    	
    	int kvalore=0;
    	
    	try{  	
    		kvalore = Integer.parseInt(ks);

    	} catch (NumberFormatException e){
    		txtResult.appendText("Errore: inserire un numero\n");
    		return;
    	}
    	
    	
    	if (kvalore > 0 ){
    		List<Driver> bestteam =	model.getDreamTeam(kvalore);
    		txtResult.appendText("Il dream team migliore con "+kvalore+" piloti e': \n");
    		txtResult.appendText(bestteam.toString()+"\n");
    		
    	} else {
    		txtResult.appendText("Errore inserire k positivo\n");
    	}
    }

    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }
    
    public void setModel(Model model){
    	this.model = model;
    	boxAnno.getItems().addAll(this.model.getSeasons());
    }
}
