package narra.triplifier.reasoner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import openllet.aterm.ATermAppl;
import openllet.owlapi.OWLException;
import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.PelletReasoner;
import openllet.owlapi.explanation.PelletExplanation;
import openllet.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.BufferingMode;

public class ExplanationReasonerModel {

	private Logger log = null;
	private OpenlletReasoner reasoner = null;
	private PrintWriter writer=null;

	public ExplanationReasonerModel(OWLOntology ontology, String reportFile){
		PelletExplanation.setup();
		reasoner = new PelletReasoner(ontology, BufferingMode.NON_BUFFERING);
		reasoner.getKB().setDoExplanation(true);
		try {
			writer = new PrintWriter(reportFile);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		}
	}
	
	public OpenlletReasoner getReasoner(){
		return reasoner;
	}
	
	public void getExplanation(Stream<OWLClassExpression> conceptsExp) throws OWLOntologyCreationException, OWLException, IOException
	{
		// The renderer is used to pretty print clashExplanation
		final ManchesterSyntaxExplanationRenderer renderer = new ManchesterSyntaxExplanationRenderer();
		// The writer used for the clashExplanation rendered
//		final PrintWriter out = new PrintWriter(System.out);
		renderer.startRendering(writer);

		// Create the reasoner and load the ontology
		//			final OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);

		// Create an clashExplanation generator
		final PelletExplanation expGen = new PelletExplanation(reasoner);

		//			// Create some concepts
		//			final OWLClass animalLover = OWL.Class(NS + "animal+lover");
		//			final OWLClass petOwner = OWL.Class(NS + "pet+owner");
		Iterator<OWLClassExpression> iter = conceptsExp.iterator();
		while(iter.hasNext()){
			OWLClassExpression concept = iter.next();
			Set<Set<OWLAxiom>> exp = expGen.getUnsatisfiableExplanations(concept);
			//		out.println("Why is " + concept + " concept unsatisfiable?");
			try {
				renderer.render(exp);
			} catch (UnsupportedOperationException e) {
				log.error(e.toString());
			} catch (org.semanticweb.owlapi.model.OWLException e) {
				log.error(e.toString());
			}
		}
		/*
		// Now explain why aClass is a sub class of bClass
		exp = expGen.getSubClassExplanations(aClass, bClass);
		out.println("Why is " + aClass + " subclass of " + bClass + "?");
		try {
			renderer.render(exp);
		} catch (UnsupportedOperationException e) {
			log.error(e.toString());
		} catch (org.semanticweb.owlapi.model.OWLException e) {
			log.error(e.toString());
		}
		*/
		renderer.endRendering();
	}
	
	/**
	 * create a complete explanation for the ontology inconsistency
	 */
	public StringBuffer getCompleteExplanation(){
		Set<ATermAppl> explanationSet = reasoner.getKB().getExplanationSet();
		StringBuffer bufferString = new StringBuffer();
		writer.append("The following expressions bring to an inconsistent ontology:\n");
    	for (Iterator<ATermAppl> iter = explanationSet.iterator(); iter.hasNext();) {
    		ATermAppl aterm = iter.next();
    		bufferString.append(aterm.toString());
    		bufferString.append("\n");
    	}
    	return bufferString;
	}
	
	/**
	 * @return a reference to write in report file
	 */
	public PrintWriter getWriter(){
		return writer;
	}

	/**
	 * @param reportFile a path to initialize a report file
	 */
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
