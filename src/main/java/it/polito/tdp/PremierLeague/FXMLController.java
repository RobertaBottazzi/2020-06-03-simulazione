/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Opponents;
import it.polito.tdp.PremierLeague.model.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnTopPlayer"
    private Button btnTopPlayer; // Value injected by FXMLLoader

    @FXML // fx:id="btnDreamTeam"
    private Button btnDreamTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtK"
    private TextField txtK; // Value injected by FXMLLoader

    @FXML // fx:id="txtGoals"
    private TextField txtGoals; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	double goals = 0;
    	try {
    		goals= Double.parseDouble(this.txtGoals.getText());
    	} catch (NumberFormatException e) {
    		this.txtResult.setText("Inserire un numero di goal valido");
    	}
    	model.creaGrafo(goals);
    	this.txtResult.appendText("Grafo creato con "+model.getGrafo().vertexSet().size()+" vertici e "+model.getGrafo().edgeSet().size()+" archi"+"\n");
    }

    @FXML
    void doDreamTeam(ActionEvent event) {
    	int k= Integer.parseInt(this.txtK.getText());
    	this.txtResult.appendText("Il dream team è: ");
    	for(Player p: model.getDreamTeam(k))
    		this.txtResult.appendText(p.toString()+"\n");
    	this.txtResult.appendText("con titolarità: "+model.calcolaTitolarita(model.getDreamTeam(k)));
    }

    @FXML
    void doTopPlayer(ActionEvent event) {
    	this.txtResult.clear();
    	if(model.getGrafo()!=null) {
    		this.txtResult.appendText("Il top player è: "+model.getOpponentsTopPlayer().get(0).getTopPlayer().toString()+"\n"+"\n"+"Avversari battuti: "+"\n"+"\n");
    		for(Opponents o: model.getOpponentsTopPlayer()) 
    			this.txtResult.appendText(o.toString()+"\n");
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnTopPlayer != null : "fx:id=\"btnTopPlayer\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnDreamTeam != null : "fx:id=\"btnDreamTeam\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtK != null : "fx:id=\"txtK\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtGoals != null : "fx:id=\"txtGoals\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
