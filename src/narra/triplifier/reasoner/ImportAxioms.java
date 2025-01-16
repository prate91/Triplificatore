package narra.triplifier.reasoner;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import openllet.owlapi.OpenlletReasoner;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
//import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
//import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

//import com.clarkparsia.owlapi.explanation.SatisfiabilityConverter;

/**
 * This class provide method/s to import new inferred axioms into an ontology
 *
 */
public class ImportAxioms {
	private Logger log = null;
	//private List<InferredIndividualAxiomGenerator<? extends OWLIndividualAxiom>> individualAxioms = null;//individual axiom perhaps need
	private List<InferredAxiomGenerator<? extends OWLAxiom>> gens;
	private OpenlletReasoner lastReasoner = null;
	private OWLDataFactory dataFactory = null;
	private OWLOntologyManager manager =null;
	private PrintWriter writer=null;
	
	public ImportAxioms(Logger logger, OWLOntologyManager man){
		manager = man;
		dataFactory = manager.getOWLDataFactory();
		log = logger;
		gens = new ArrayList<>();
	}
	
	/**
	 * Add new inferred axioms by openllet Resoner into the ontology
	 * @param reasoner the openllet resoner of the ontology
	 * @param manager an owl ontology manager
	 * @return a new OWLOntology that contains all axioms old and new inferred axioms. The ontology is added at the manager in input.
	 */
	public OWLOntology ontologyFromOpenllet(OpenlletReasoner reasoner){
        return ontologyFromOpenllet(reasoner, new boolean[]{true,true,true,true,true,true,true,true,true,true,true,true});
	}
	
	/**
	 * Add new inferred axioms by openllet Resoner into the ontology
	 * @param reasoner the openllet resoner of the ontology
	 * @param manager an owl ontology manager
	 * @return a new OWLOntology that contains all axioms old and new inferred axioms. The ontology is added at the manager in input.
	 */
	public OWLOntology ontologyFromOpenllet(OpenlletReasoner reasoner,  boolean[] option){
		if(option.length!=12) return null;
//        long startTime = System.currentTimeMillis();
//        reasoner.getKB().realize();// axiom by SWRL rules
//        long endTime = System.currentTimeMillis();
//        log.info("tempo totale di REALIZE: "+(endTime-startTime)+"ms");
		gens = new ArrayList<>();
        if(option[0]) gens.add(new InferredClassAssertionAxiomGenerator());
        if(option[1]) gens.add(new InferredSubClassAxiomGenerator());  
        if(option[2]) gens.add( new InferredEquivalentClassAxiomGenerator());
        if(option[3]) gens.add( new InferredDisjointClassesAxiomGenerator());
        if(option[4]) gens.add( new InferredPropertyAssertionGenerator());
        if(option[5]) gens.add( new InferredInverseObjectPropertiesAxiomGenerator());
        if(option[6]) gens.add( new InferredEquivalentObjectPropertyAxiomGenerator());
        if(option[7]) gens.add( new InferredSubObjectPropertyAxiomGenerator());
        if(option[8]) gens.add( new InferredEquivalentDataPropertiesAxiomGenerator());
        if(option[9])  gens.add( new InferredSubDataPropertyAxiomGenerator());
        //Axiom from property characteristic like: FunctionalObjectProperty, InverseFunctionalObjectProperty, ReflexiveObjectProperty, IrreflexiveObjectProperty,
        //SymmetricObjectProperty, AsymmetricObjectProperty, TransitiveObjectProperty
        if(option[10]) gens.add( new InferredObjectPropertyCharacteristicAxiomGenerator());
        //Axiom from property characteristic like: FunctionalDataProperty
        if(option[11]) gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
        //        List<InferredIndividualAxiomGenerator<? extends OWLIndividualAxiom>> individualAxioms = new ArrayList<>();
        //        gens.addAll(individualAxioms);
        reasoner.flush();
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
        lastReasoner = reasoner;
        OWLOntology infOnt = null;
		try {
			infOnt = manager.createOntology();
		} catch (OWLOntologyCreationException e) {
			log.error(e.getMessage());
		}
		iog.fillOntology(reasoner.getManager().getOWLDataFactory(), infOnt);
		return infOnt;
	}

	/**
	 * 
	 * @param reasoner
	 * @param owlOntology
	 * @param reportFile
	 * @return
	 */
	public Set<OWLAxiom> getInferredAxiom(OpenlletReasoner reasoner, OWLOntology owlOntology, String reportFile){
		//Some work done to load ontology
		//		ReasonerFactory factory = new ReasonerFactory(); TODO to add explanation
		//		BlackBoxExplanation explain = new BlackBoxExplanation(owlOntology, factory, reasoner);
		//		HSTExplanationGenerator multiEx = new HSTExplanationGenerator(explain);
		Set<OWLAxiom> allAxioms = new HashSet<OWLAxiom>();
		if(gens == null || gens.size() == 0){
			log.info("generatorList = null or generatorListSize = 0");
			return null;
		}
		if(writer==null) setWriter(reportFile);

		writer.println("List of Axiom entailed by reasoner:");
		Iterator<InferredAxiomGenerator<? extends OWLAxiom>> iterator = gens.iterator();
		while(iterator.hasNext()){
			Set<OWLAxiom> axioms = (Set<OWLAxiom>) iterator.next().createAxioms(dataFactory, reasoner);
			allAxioms.addAll(axioms);
			//SatisfiabilityConverter converter = new SatisfiabilityConverter(dataFactory);
			for (OWLAxiom ax : axioms) {
				if(!owlOntology.containsAxiom(ax)) {
					writer.println("\nAxiom :- " + ax);
				}
				//				Set<Set<OWLAxiom>> expl = multiEx.getExplanations(converter.convert(ax));
				//				System.out.println("No. of Explanations :- " + expl.size());
				//				System.out.println("Explanation :- ");
				//				for (Set<OWLAxiom> a : expl) {
				//					System.out.println(a);
				//				}
			}
		}
		writer.flush();
		return allAxioms;
	}
	
	public PrintWriter getWriter(){
		return writer;
	}

	public void setWriter(String reportFile){
		try {
			writer = new PrintWriter(reportFile, "UTF-8");
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
	}
}
