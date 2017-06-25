package it.polito.tdp.formulaone.model;

import java.util.*;

import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	//per creare il grafo ci devono essere 1 arco x vertice quindi uso il SIMPLEDIRECTEDWEIGHTED
	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> graph;
	
	//i vertici non sono tutti i driver!!! sono solo quelli che hanno partcipato alle gare considerate e 
	//hanno tagliato il traguardo in almeno una gara di queste
	//i result hanno posizione!=null
	
	//1 cosa che faccio x popolare la tendina
	private List<Season>seasons ;
	
	//definisco le variabili di stato della ricorsione
	private int tassoMin;
	private List<Driver> teamMin;
	
	//PER OTTIMIZZAZIONE
	private Set<Driver> ottima;
	private int idMaggiore;
	
	public List<Season> getSeasons(){
		if(seasons == null){
			//dato che non ho fatto il new di season mi da null!
			FormulaOneDAO dao = new FormulaOneDAO();
			this.seasons=dao.getAllSeasons();			
		}
		return this.seasons;
	}
	
	public void creaGrafo(Season s){
		//crea grafo ogni volta che cambio la stagione!
		graph =new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		FormulaOneDAO dao = new FormulaOneDAO();
    	//mi faccio dire dal dao chi sono i piloti di quella stagione
		// ogni volta che cambio stagione la lista si riaggiorna
		
    	List<Driver> drivers = dao.getDriversForSeason(s);
    	
    	//aggiungo i vertici
    	Graphs.addAllVertices(graph, drivers);
    	
    	//aggiungi archi quando un pilota in una gara è arrivato prima di un altro nella stessa gara
    	//quindi devo valutare la vittorie dei piloti
    	
    	for(Driver d1: this.graph.vertexSet()){
    		for(Driver d2: this.graph.vertexSet()){
    			if(!d1.equals(d2)){
    				Integer vittorie = dao.contaVittorie(d1, d2, s);
    				if(vittorie > 0){
    					
    					//aggiungo l'arco con il SUO peso in un unico metodo
    					Graphs.addEdgeWithVertices(this.graph, d1, d2, vittorie);
    				}
    			}
    		}
    	}
    	System.out.println(graph.toString());
    	
	}
	
	public Driver getBestDriver(){
		Driver best = null;
		int max =Integer.MIN_VALUE;
		
		for(Driver d: graph.vertexSet()){
			
			int peso=0;
			//peso uguale alla gare vinte - gare perse
			
			for(DefaultWeightedEdge dwe: graph.outgoingEdgesOf(d)){
				peso += graph.getEdgeWeight(dwe);
			}
			
			for(DefaultWeightedEdge dwe: graph.incomingEdgesOf(d)){
				peso -= graph.getEdgeWeight(dwe);
			}
			
			if(peso>max){
				max=peso;
				best=d;
			}
		}
		return best;
	}
	
	public List<Driver> getDreamTeam(int k){

		//mi fermo quando il team ha dim=k 
		//vedo il tasso di sconfitta di quel team e lo confronto con quello migliore
	
		Set<Driver> team = new HashSet<>();
		tassoMin = Integer.MAX_VALUE; //la inizializzo al max cosi di sicuro scende
		teamMin= null;
		
		ricorsiva(0, team, k);
	
	return this.teamMin;	
		
	}
	
	/**
	 * in ingresso ricevo il {@code team} parziale composto da {@code passo} elementi
	 * la variabile {@code passo } parte da 0
	 * caso terminale quando {@code passo ==K } e va calcolato il tasso di sconfitta
	 * Altrimenti si procede ricorsivamente ad aggiungere un nuovo vertice(il passo +1)
	 * scegliendolo tra i vertici non ancora presenti nel {@code team}
	 * 
	 * @param passo
	 * @param team
	 * @param k
	 */
	
	private void ricorsiva(int passo, Set<Driver> team, int k){
		//caso terminale?
		if(passo == k ){
			System.out.println(team);
			
			//calcolare tasso di sconfitta del team 
			int tasso =this.tassoSconfitta(team);
			
			//eventualmente aggiornare il tasso minimo (il migliore)
			if(tasso < tassoMin){
				tassoMin= tasso;
				teamMin = new ArrayList<>(team);
				
				System.out.println(tassoMin+" "+ team.toString());
			}
		} else {
			// caso normale allora passo <k e scelgo un altro vertice tra tutti i vertici 
			//che non appartengono al set ==> creo il set candidati rimuovendo da tutti quelli del team
			Set<Driver> candidati = new HashSet<>(graph.vertexSet());
			candidati.removeAll(team);
			//altrimenti nel for vedevo se d era contenuto nel team
			
			for(Driver d: candidati){
				team.add(d);
				
				//devo evitare di avere 2 set uguali con a,b,c e b,c,a
				//per avere un set univoco devo mettere un criterio crescente
				//ed sul driver id crescente
				//modifica 1: usare la lista e non il set xk devo avere il precedente e il successivo
				//modifica 2: avrò un secondo tipo di caso terminale in cui non riesco a comporre il team
				//				quindi la ricorsione termina PRIMA CHE IL PASSO = K
				
				//riduco il numero di chiamate ricorsive di k!(k fattoriale)
				
				ricorsiva(passo+1, team, k);
				team.remove(d);
			}
		}		
	}

	private int tassoSconfitta(Set<Driver> team) {
		//calcolo la somma dei pesi di tutti gli archi di cui 
		//il vertice di partenza sia esterno al team e quello di arrivo nel team
		int tasso =0;
		
		//complessità pari al numero di archi al quadrato, ma il team è piccolo
		// quindi n vertici nel team
		for(DefaultWeightedEdge dwe: graph.edgeSet()){
			if(!team.contains(graph.getEdgeSource(dwe)) && team.contains(graph.getEdgeTarget(dwe))){
				//allora quest'arco serve xk parte da fuori il team e arriva dentro
				//quindi è una sconfitta del team selezionato!
				tasso += graph.getEdgeWeight(dwe);
			}
		}
		
		return tasso;
	}
	
	/*
	 * MODI PER OTTIMIZZARE: riduco di n!
-CONTROLLI EFFICACI NEL BLOCCO: CONDIZIONE DI TERMINAZIONE PER EVITARE DI ANALIZZARE SOLUZIONI PARZIALI NON VALIDE.
-CONTOLLO DENTRO IL CICLO FOR/WHILE EFFICACE:
SE NON NECESSARIE è MOLTO UTILE ELIMINARE LE PERMUTAZIONI :
CONSIDERO SOLO A,B,C NON ANCHE B,C,A /  A,C,B /……..

	1) CREARE IL METODO COMPARE TO NELLA CLASSE CHE COMPONE LA SOLUZIONE PARZIALE(implements Comparable<Driver>)
	
public int compareTo(Driver arg0) {
		return this.driverId-arg0.getDriverId();
}
	*/
	//2) POI NEL METODO RICORSIVO(che riceve ultimoAggiunto dall’iterata precedente come parametro)
	
	public Set<Driver> InterfacciaRicorsione (int k){
		HashSet<Driver>  parziale= new HashSet<Driver>();
		
		ottima=new HashSet<Driver>(); //definita a livello globale
		
		int livello=0;
		this.cercaIdMaggiore(); // variabile globale per avere l'id maggiore
		this. Ricorsivo(parziale, livello, k, null );
		
		return ottima;
	}
	
	private void Ricorsivo(HashSet<Driver> parziale, int livello, int k, Driver ultimoAggiunto) {
		//blocco di salvataggio
		if(livello==k){
			if(tassoSconfitta(parziale)<tassoSconfitta(ottima)){
				ottima.clear();
				ottima.addAll(parziale);
				System.out.println(ottima.toString());
			}
			return;
		}
		
		if(parziale.size()>0 && ultimoAggiunto.getDriverId()==idMaggiore)
			return;
//aggiungo questa seconda condizione di terminazione per evitare quando in ultima posizione 
//ho il pilota con id maggiore di cercare di aggiungere ugualmente piloti , 
//in quanto so che a causa del controllo if nel for non ci riuscirò, il ramo dell'albero ricorsivo 
//dove ho il pilota con id maggiore (lo posso avere solo in ultima posizione) con questo 
	//return risale velocemente i livelli e si termina.

		for(Driver d:graph.vertexSet()){
			if(parziale.size()==0 || ultimoAggiunto.compareTo(d)<0){
				
//il prossimo pilota che aggiungo: d deve avere un id maggiore dell'id del pilota aggiunto precedententemente
//:ultimoAggiunto . infatti ultimoAggiunto.compareTo(d) è <0 se id di ultimoAggiunto viene prima (è minore) di id di d 

				parziale.add(d);//metto
					Ricorsivo(parziale,livello+1,k,d);//provo
				parziale.remove(d);//tolgo
			}
		}
	}
	
	
private void cercaIdMaggiore(){
		int max=Integer.MIN_VALUE;
		
		for(Driver d :graph.vertexSet()){
			if(d.getDriverId()>max)
				max=d.getDriverId();
	}
		idMaggiore=max;//globale
}
	 
}