package narra.triplifier.model;
import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Merge ontologies
 */
public class ModelMerger {
	private OWLOntologyManager manager = null;
	private static Logger log = null;
	private OWLOntologyManager managerToAddMergedOntolgy = null;
	
	public ModelMerger(OWLOntologyManager man, Logger logger){
		log = logger;
		managerToAddMergedOntolgy = man;
	}
	/**
	 * Given a list of input ontology file paths and an output ontology IRI, merge the inputs into the output with that IRI and write it
	 *
	 * @param paths a list of paths to input ontologies
	 * @param newIRIString the IRI string of the resulting ontology
	 * @return the merged ontology
	 */
	public OWLOntology merge(List<String> paths, String newIRIString) {
		manager = OWLManager.createConcurrentOWLOntologyManager();;
		// load ontologies into manager and checks the directory of the file for imports
		int l=1;
//		List<String> objectsToRemove = new ArrayList<String>();
//		String newIRI="";
		for(int i = (paths.size() - 1); i >= 0; i--) {
			String path = paths.get(i);
//		for(String path: paths) {
			log.info("ITERAZIONE: "+l+"path: "+path);l++;
			
			try {
				File file = new File(path);
				//File parent = file.getCanonicalFile().getParentFile();
//				manager.getIRIMappers().add(new AutoIRIMapper(parent, false));
//				OWLOntology onto = manager.createOntology(new OWLOntologyID(IRI.create("urn:absolute:"+path)));
//				manager.loadOntologyFromOntologyDocument(IRI.create(path));
				
				manager.loadOntologyFromOntologyDocument(file);
//			 	if(manager.contains(new OWLOntologyID(IRI.create("urn:absolute:"+path)))) log.info("TESTSUCCESS");
			} catch (Exception e) {
				log.info("ERROR: Could not load ontology at: " + path);
				log.info(e.getMessage());
			}
			paths.remove(path);
		}
		l=1;
//		for(String o: objectsToRemove) {
//			log.info("ITERAZIONE di rimozione: "+l+"path: "+o);l++;
//			paths.remove(o);
//		}
//		objectsToRemove.stream().forEach(o -> paths.remove(o));
		
		log.info(newIRIString);
		IRI outputIRI = IRI.create(newIRIString);
		
		/*OWLOntology merged=null;
		try {
			merged = manager.createOntology();
		} catch (OWLOntologyCreationException e1) {
			e1.printStackTrace();
		}
		// merge
		try {
			OWLOntologyMerger merger = new OWLOntologyMerger(manager);
			merged = merger.createMergedOntology(manager, outputIRI);
		} catch (OWLOntologyCreationException e) {
			log.info("ERROR: Could not merge ontologyies");
			log.info(e.getMessage());
		}
		*/
		OWLOntology merged=null;
		try {
			merged = managerToAddMergedOntolgy.createOntology(outputIRI, manager.ontologies(), false);
		} catch (OWLOntologyCreationException e) {
			log.info("ERROR: Could not merge ontologyies");
			log.info(e.getMessage());
		}
		return merged;
	}
}
