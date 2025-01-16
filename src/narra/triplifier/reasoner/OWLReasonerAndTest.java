package narra.triplifier.reasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import openllet.owlapi.OpenlletReasoner;

import org.apache.log4j.Logger;
import org.jgrapht.alg.util.Pair;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;

import narra.triplifier.resource.GraphViewer;
import narra.triplifier.util.Log4JClass;


/**
 * @author filippo
 * Class that have a method to find cycles into graph and 
 * other methods to reasoning or rather test consistency and infer knowledge
 */
public class OWLReasonerAndTest {
	//for selecting name and value 
	//private final int NAME = 0;
	//private final int VALUE = 1;
	//Logger
	private Logger log = Log4JClass.getLogger();
//	private Individual lastind;
	private OWLOntology infmod = null;
	
	public OWLReasonerAndTest(){
		//get Logger Instance
		log = Log4JClass.getLogger();
//		lastind = null;
		countFour = 0;
	}
	
	/**
	 * @return inferred ontology (can be null if there is not an inferred ontology)
	 */
	public OWLOntology getInferredOntology(){
		return infmod;
	}
	/**
	 * 
	 *
	 */
	enum VisitColor { Open,Close }
	private int countFour = 0;
	/**
	 * 
	 * @param infmod
	 * @param URIproperty
	 * @return
	 */
	public ArrayList<ArrayList<String>> findAllCycleForRel(OWLOntology model, String URIproperty){
		//verify if there is a cycle and found the set of Starter Node
		HashSet<String> setOfStarterNode = new HashSet<String>();
		Iterator<OWLAxiom> iterator = model.aboxAxioms(Imports.INCLUDED).iterator();//TODO Select only the axiom of URIproperty
		int count = 0; int countTwo = 0;
		int countThree = 0;int tempCount = 0;
		GraphViewer graph = new GraphViewer();
		while(iterator.hasNext()){
			count++;
			OWLAxiom axiom = iterator.next();
			if(axiom.isOfType(AxiomType.OBJECT_PROPERTY_ASSERTION)){
				countTwo++;
				OWLObjectPropertyAssertionAxiom objPropertyAssertion = (OWLObjectPropertyAssertionAxiom)axiom;
				String name = objPropertyAssertion.getProperty().getNamedProperty().getIRI().toString();
//				if(countTwo==1)log.debug("objPropertyName[0]= "+name);
				if(name.contains(URIproperty)){
//					log.debug("objPropertyNameX= "+name);
					countThree++;
					String subjectIRI = objPropertyAssertion.getSubject().asOWLNamedIndividual().getIRI().toString();
					String objectIRI = objPropertyAssertion.getObject().asOWLNamedIndividual().getIRI().toString();
					graph.addEdge(subjectIRI, objectIRI);
					if(subjectIRI!=null&&subjectIRI.equals(objPropertyAssertion.getObject().asOWLNamedIndividual().getIRI().toString())){
						if(tempCount==0){log.info("La Propietà= \""+name+"\" può contenere cicli");tempCount++;}
						countFour++; setOfStarterNode.add(subjectIRI);
//						break;//TODO to verify the necessity
					}
				}
			}
		}
		log.debug("Counted "+count+" Axioms; CountTwo= "+countTwo+"; CountThree= "+countThree+"; CountFour= "+countFour+".");
		//Get Only the triple with predicate URIproperty 
		Set<String> setOfChildrens = new HashSet<String>();
		ArrayList<ArrayList<String>> cycleList = new ArrayList<ArrayList<String>>();
		int contatoreStart = 0;
		while(!setOfStarterNode.isEmpty()){
			log.debug("contatoreStart: "+contatoreStart);
			contatoreStart++;
			Stack<Pair<String, String>> stck = new Stack<Pair<String,String>>();
			String starterNode = setOfStarterNode.iterator().next();
			stck.push(new Pair("", starterNode));
			setOfStarterNode.remove(starterNode);
			HashMap<String, VisitColor> Evisit = new HashMap<String, VisitColor>();
			HashMap<String, String> visitTree = new HashMap<String, String>();
			int contatoreStack = 0;
			while(stck.size()>0){
				log.debug("contatoreStack: "+contatoreStack);
				contatoreStack++;
				Pair<String,String> zu = stck.pop();
				String z = zu.getFirst();//a father node
				String u = zu.getSecond();
				if("".equals(u)){
					Evisit.put(z, VisitColor.Close);
				}else{
					if(Evisit.get(u)==null){
						Evisit.put(u, VisitColor.Open);
						visitTree.put(u, z);
						stck.push(new Pair(u, ""));
						//XXX se possibile implementare un metodo che trova tutte e sole le triple subj=Node.createURI(u), pred=Node.createURI(URIproperty), obj=Node.ANY
						setOfChildrens = graph.getChildrenList(u);//getTriplesForSubject(new RDFResourceIRI(IRI.create(u))).toArray(arrayOfTriple);
//						log.debug("arrayOfTriples.size="+arrayOfTriple.length);
						Iterator<String> iterChildrens = setOfChildrens.iterator();
						while(iterChildrens.hasNext()){
								String nearNode = iterChildrens.next();
								if(!u.equals(nearNode)){
									stck.push(new Pair(u, nearNode));
									setOfStarterNode.remove(nearNode);
								}
						}
					}else{
						if(Evisit.get(u).equals(VisitColor.Open)){
							cycleList.add(getPath(visitTree, z, u));
						}
					}
				}
			}
		}
		return cycleList;
	}//end method
	
	private ArrayList<String> getPath(HashMap<String, String> visitTree, String z, String u){
		ArrayList<String> path = new ArrayList<String>();
		path.add(z);
		String t=visitTree.get(z);
		path.add(t);
		while(!t.equalsIgnoreCase(u)){
			t=visitTree.get(t);
			path.add(t);
		}
		return path;
	}
	
	private void makecyclicalityPairSetList(OWLOntology infmod, String prop, HashMap<String, String> propValue, String value, Set<String> alredyVisited,
			ArrayList<ArrayList<String>> cyclicalityList, ArrayList<Pair<String, ArrayList<ArrayList<String>>>> cyclicalityPairSetList){
		String val = propValue.get(prop);
		if(val!=null && val.equals(value)){
			alredyVisited.add(prop);
			log.debug("Analisi della Propietà: "+prop);
			cyclicalityList = findAllCycleForRel(infmod, prop);
			log.debug("cyclicality="+cyclicalityList.size());
			if(cyclicalityList.size()>0){
				cyclicalityPairSetList.add(new Pair<String, ArrayList<ArrayList<String>>>(prop, cyclicalityList));
			}
		}
	}
	
	/**
	 * Main method to invoke for reasoning
	 * @param pmodel the Ontology Model where want add Axioms or data Triple
	 * @param manager manager of the ontology
	 * @param reasonerToWrite the reasoner that we want to use
	 * @param howInferredAxioms a boolean array to define how axioms we want to infer and how we do not want to infer
	 * @param reportFile a file on which we want to print a report
	 * @return 0 if the validation is ok, -1 instead
	 */
	public int createOwl2ExampleModel(OWLOntology pmodel, OWLOntologyManager manager, OpenlletReasoner reasonerToWrite, boolean[] howInferredAxioms, String reportFile){
		int result = 1;
		// owlAnnotationProperties are the properties used to represent annotated axioms in RDF/XML.
		long startTime = System.currentTimeMillis();
		log.info("Start inference reasoning");
		ImportAxioms impAxms = new ImportAxioms(log, manager);
		infmod = impAxms.ontologyFromOpenllet(reasonerToWrite, howInferredAxioms);
//		Set<OWLAxiom> allAxioms = impAxms.getInferredAxiom(reasonerToWrite, pmodel, reportFile);

		/*try {
			infmod = manager.createOntology(infmod.axioms());
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}*/
		long endTime = System.currentTimeMillis();
		log.info("End inference reasoning. Total Time: "+(endTime-startTime)+"ms");

		//Set<OWLAxiom> allAxioms = 
		//invoke this method to print a reportFile that contain a list of axiom entailed by reasoner
		impAxms.getInferredAxiom(reasonerToWrite, pmodel, reportFile);
		
		
		//Controllo dei cicli TODO da rivedere
	    Iterator<OWLAxiom> iterator = pmodel.tboxAxioms(Imports.INCLUDED).iterator();
		//Test cyclicality on base model
//		Iterator<OWLObjectPropertyDomainAxiom> iteratorDom = pmodel.axioms(AxiomType.OBJECT_PROPERTY_DOMAIN).iterator();//TODO Select only the axiom of URIproperty
//		Iterator<OWLObjectPropertyRangeAxiom> iteratorRan = pmodel.axioms(AxiomType.OBJECT_PROPERTY_RANGE).iterator();//TODO Select only the axiom of URIproperty		
		/*RDFGraph graph = (new RDFTranslator(pmodel.getOWLOntologyManager(), pmodel, false, new OWLAnonymousIndividualsWithMultipleOccurrences(), new OWLAxiomsWithNestedAnnotations(), new AtomicInteger())).getGraph();
		Set<RDFTriple> allTriplesOfGraph = graph.getAllTriples();
		Iterator<RDFTriple> iteratorGraph = allTriplesOfGraph.iterator();*/
	    
	    /* Assiomi
	     * 
	     *
		if(iterator.hasNext()){
			log.debug("Trovati Assiomi");
//		if(iteratorDom.hasNext() && iteratorRan.hasNext()){
//			log.debug("Trovati Assiomi Dom & Ran");
			//cyclicalityPairSetList.left is propertyName cyclicalityPairSetList.right are all the sub-sets of nodes that are a cycle
			ArrayList<Pair<String, ArrayList<ArrayList<String>>>> cyclicalityPairSetList = new ArrayList<Pair<String, ArrayList<ArrayList<String>>>>();
			startTime = System.currentTimeMillis();
			//Nell'array tutti e soli i nodi appartenenti ad un ciclo
			ArrayList<ArrayList<String>> cyclicalityList = null;
			//XXX if we are sure that for each property we have at most only one propertyDomainAxiom, and the same for propertyRangeAxiom we need only one HashMap
			//FromPropertyToDomain
			HashMap<String, String> propDom = new HashMap<String, String>();
			//FromPropertyToRange
			HashMap<String, String> propRan = new HashMap<String, String>();
			Set<String> alredyVisited = new HashSet<String>();
			int count = 0;
			do {//selezione delle sole property di interesse
				count++;
//				OWLAxiom axiom = iteratorDom.next();
				OWLAxiom axiom = iterator.next();
				if(axiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN) || axiom.isOfType(AxiomType.OBJECT_PROPERTY_RANGE)){
					if(axiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN)){
//						log.debug("found ObjectPropertyDomainAxiom");
						OWLObjectPropertyDomainAxiom objPropertyDomain = (OWLObjectPropertyDomainAxiom)axiom;
						String prop = objPropertyDomain.getProperty().getNamedProperty().getIRI().toString();//ObjectPropertyExpression.getName.getIRI
//						log.debug("trovato assioma Domain su property: "+prop);
						if(!alredyVisited.contains(prop)){
							String dom = objPropertyDomain.getDomain().getClassExpressionType().getIRI().toString();//ClassExpression.getName.getIRI
							propDom.put(prop, dom);
							makecyclicalityPairSetList(infmod, prop, propRan, dom, alredyVisited, cyclicalityList, cyclicalityPairSetList);
						}
//			}while(iteratorDom.hasNext());
//			count = 0;
//			do{
//				log.debug("iteration="+count);count++;
//				OWLAxiom axiom = iteratorRan.next();
					}else{
//						log.debug("found ObjectPropertyRangeAxiom");
						OWLObjectPropertyRangeAxiom objPropertyRange = (OWLObjectPropertyRangeAxiom)axiom;
						String prop = objPropertyRange.getProperty().getNamedProperty().getIRI().toString();//ObjectPropertyExpression.getName.getIRI
//						log.debug("trovato assioma Range su property: "+prop);
						if(!alredyVisited.contains(prop)){
							String ran = objPropertyRange.getRange().getClassExpressionType().getIRI().toString();//ClassExpression.getName.getIRI
							propRan.put(prop, ran);
							makecyclicalityPairSetList(infmod, prop, propDom, ran, alredyVisited, cyclicalityList, cyclicalityPairSetList);
						}
					}
				}
//			}while(iteratorRan.hasNext());
			}while(iterator.hasNext());
			log.debug("have counted "+count+" iterations");
			endTime = System.currentTimeMillis();
			log.info("INITIMING: Tempo Controllo cicli sull'intero modello= "+(endTime - startTime)+"ms");

			if(cyclicalityPairSetList.size()>0) {
				//log.info("La property dove è stato trovato il ciclo ha URI="+URIproperty);
				for(Pair<String, ArrayList<ArrayList<String>>> aPair: cyclicalityPairSetList){
					cyclicalityList = aPair.getSecond();
					if(impAxms.getWriter()==null) impAxms.setWriter(reportFile);
					impAxms.getWriter().println();
					impAxms.getWriter().println("La proprietà con URI "+aPair.getFirst()+" contiene "+cyclicalityList.size()+" cicli con i seguenti nodi:");
					for(ArrayList<String> listOfCycle: cyclicalityList){
						impAxms.getWriter().println("{");
						for(String nodeURI: listOfCycle){
							impAxms.getWriter().println(nodeURI);
						}
						impAxms.getWriter().println("}");
					}
				}
				impAxms.getWriter().close();
				return -1;
			}
		}
		/*TODO
		//validazione del modello
	    ValidityReport validity = pmodel.validate();
	    if (validity.isValid()) {
	    	log.info("OWL2TESTMODEL - Il modello generato dopo owl2text e' stato validato con successo");
	    }
	    else {
	    	// STAMPA ERRORI DI VALIDAZIONE
	    	Iterator<Report> reports = validity.getReports();
	    	while(reports.hasNext()) {
	    		log.error("OWL2TESTMODEL - Errore - Il modello generato dopo owl2text non e' valido: " + reports.next() + "\n");
	    	}
	    	result = -1;
	    }
	    */
		if(impAxms.getWriter()!=null)impAxms.getWriter().close();
		return result;

	}//endmethod

}

/*
trovacicli (s):
-S = []				# S è uno STACK
-S.push((Nul,s))		# inizialmente metto nello STACK l'arco (Nul, s): Il padre di s è un nodo fittizio

# E[u] può valere: 
#	O se ho iniziato a visitare il sottoalbero radicato in u ma se ancora la visita del sottoalbero non è stata conclusa.
#	C se la visita del sottoalbero radicato in u è terminata.
# 	Nul se il nodo u non è ancora stato visitato dall'algoritmo.
-E = [Nul]∗n			
-T = [Nul]∗n			# T rappresenta l'albero di visita: T[u] = z indica che il nodo z è PADRE del nodo u nell'albero T

-while len(S)>0:		# Finchè lo STACK S contiene elementi
-(z,u) = S.pop()		# Estrai un elemento (un arco) dalla pila e metti il  NODO padre in z ed il NODO figlio in u

-if u == Nul:		# Se il nodo figlio è Nul significa che la visita del sottoalbero radicato in z è stata completata
-	E[z] = C	# marco come CHIUSO il sottoalbero radicato nel nodo padre z

-else:			# altrimenti se il nodo di destinazione non è Nul, il sottoalbero radicato in z potrebbe non essere completato

-	if E[u] == Nul:	# se il nodo di destinazione u non è mai stato visitato dall'algoritmo, lo visito:
-		E[u] = O	# marco come O la posizione E[u] perchè ho iniziato a visitare il sottoalbero radicato in u
-		T[u] = z	# il nodo z è PADRE del nodo u nell'albero di visita T

-		S.append((u, Nul))	# metto l'arco (u, Nul) nello STACK S

-		for v in G[u]:		# e poi metto nello STACK S tutti i nodi v vicini al nodo u
-			S.append((u,v))
	
	# Se invece X[u] è diverso da False significherebbe che l'algoritmo è tornato su un nodo già visitato, se il sottoalbero radicato in u
	# non è stato chiuso, allora significa che l'arco (z,u) è un arco all'indietro e che quindi ho TROVATO un ciclo
-	elif E[u] == O:
-		L = path (T,z,u)	# metto in L il PERCORSO tra u e z che rappresenta il ciclo: path(T,z,u)={L.add(z)
																									t=T[z]
																									L.add(t)
																									while(t!=u)
																										t=T[t]
																										L.add(t)
																									return L}
-		SetOfL.add(L)     # Aggiunge L ad un insieme degli insiemi dei ciclo

-return SetOfL	# l'algoritmo termina restituendo l'insieme degli insiemi dei cicli trovati
 */
