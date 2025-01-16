package narra.triplifier;

import java.util.List;


import org.apache.jena.tdb.TDBBackup;
import org.apache.jena.tdb2.DatabaseMgr;
import org.apache.jena.tdb2.store.DatasetGraphSwitchable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLException;

import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;
import openllet.owlapi.PelletReasoner;
import openllet.owlapi.explanation.PelletExplanation;
import openllet.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.util.AutoIRIMapper;

import narra.triplifier.model.JsonToJava;
import narra.triplifier.model.ModelMerger;
import narra.triplifier.model.OWLOntologyCreator;
import narra.triplifier.model.OWLOntologyPopulator;
import narra.triplifier.reasoner.ExplanationReasonerModel;
import narra.triplifier.reasoner.OWLReasonerAndTest;
import narra.triplifier.resource.Narrative;
import narra.triplifier.util.Log4JClass;
import narra.triplifier.store.BlazegraphLoader;
import narra.triplifier.resource.Vocabulary;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.sparql.core.DatasetGraph; 



public class Triplify {


	private static Logger log = null;
	
	public static int countIdLAU = 0; //start0, 257 part 2 / start300, 557 part 2

	public static HashMap<String, Integer> LAUS = new HashMap<String, Integer>();

	public static void main(String[] args) throws Exception {
		// Get logger instance
		log = Log4JClass.getLogger();

		
		// Load configuration file
		Properties properties = new Properties();
		try {
			properties.load(Triplify.class.getClassLoader().getResourceAsStream("config.properties"));
		}
		catch (IOException e) {
			log.error("\nINSERT - Errore nel caricamento della configurazione");
		}
		PropertyConfigurator.configure(properties);

		OWLOntologyManager manager = OWLManager.createConcurrentOWLOntologyManager();
		AutoIRIMapper mapper=new AutoIRIMapper(new File(properties.getProperty("folderOntologies")), true);
		manager.getIRIMappers().add(mapper);
		String importedSwrlRules = properties.getProperty("swrl_rules");
		OWLOntology ontology = null;

		

		String jsonPath = null;
		if (args.length > 0 && args[0] != null && !"".equals(args[0])) {
			jsonPath = args[0];
		} else {
			jsonPath = properties.getProperty("jsonpath");
		}
		

		// Get OWL output path from argument or property
		String owlPath = null;
		if (args.length > 1 && args[1] != null && !"".equals(args[1])) {
			owlPath = args[1];
		} else {
			owlPath = properties.getProperty("owl_path");
		}

		// Get baseUrl to distinguish VRE users from login users for dataset fuseki connection
		//String baseUrl = "tool.dlnarratives.eu";
		//if (args.length > 2 && args[2] != null && !"".equals(args[2])) {
			//baseUrl = args[2];
		//}
		String local = "";
		if (args.length > 2 && args[2] != null && !"".equals(args[2])) {
			local = args[2];
		}
		
		// Get OWL output path from argument or property
		String reportPath = null;
		if (args.length > 3 && args[3] != null && !"".equals(args[3])) {
			reportPath = args[3];
		} else {
			reportPath = properties.getProperty("classTreesReportFile");
		}
		

		// Load JSON data from file and convert it to Java objects
		// Get JSON input path from argument or property
		File dir = new File(jsonPath);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
			// Do something with child
				// System.out.println();
			int i = child.getName().lastIndexOf('.');
			String name = child.getName().substring(0,i);
			owlPath = "./OWL/" + name + ".owl";
			Narrative narrative = JsonToJava.loadJSON(child.toString());
			
		

		// Load SWRL rules and imported ontologies
		if (importedSwrlRules != null && !"".equalsIgnoreCase(importedSwrlRules)) {
			ModelMerger merger = new ModelMerger(manager, log);
			List<String> listOfOntologies = new ArrayList<String>();
			
			/*String importedOntologies= properties.getProperty("importedOntologies");
			List<String> importedOntologiesConvertion = new ArrayList<String>(Arrays.asList(importedOntologies.split(",")));
			for (int i = 0; i < importedOntologiesConvertion.size(); i++){
				String elem= importedOntologiesConvertion.get(i);
				listOfOntologies.add(elem);
			}*/

			
			listOfOntologies.add(properties.getProperty("importedOntologies"));
			listOfOntologies.add(importedSwrlRules);
			ontology = merger.merge(listOfOntologies, "https://dlnarratives.eu");
		} else {
			ontology = manager.loadOntologyFromOntologyDocument(new File(properties.getProperty("imported_ontologies")));
		}
		log.debug("Loaded the ontology: " + ontology);
			
		boolean consistent = false;
		
		try {
			ExplanationReasonerModel erm = new ExplanationReasonerModel(ontology, reportPath);
			PrintWriter writer = erm.getWriter();
			OpenlletReasoner reasoner = erm.getReasoner();
			consistent = reasoner.isConsistent();

			if("true".equalsIgnoreCase(properties.getProperty("expanseOntology"))){
				
						
				// Create ontology
				OWLOntologyCreator ontoCreator = new OWLOntologyCreator(manager, ontology);
				ontoCreator.createOntology();
				consistent = reasoner.isConsistent();

				// Populate ontology
				OWLOntologyPopulator ontoPopulator = new OWLOntologyPopulator();
				ontoPopulator.populateOntology(ontology, narrative);
			}

			int result = 1;//result check
			consistent = reasoner.isConsistent();
			long endTime = System.currentTimeMillis();

			log.info("Ontology consistent: " + consistent + "\n");

			if(consistent && "true".equalsIgnoreCase(properties.getProperty("doReasoning"))){

				// Ask the reasoner to do all the necessary work now
				reasoner.precomputeInferences();

				// We can determine if the ontology is actually consistent
				consistent = reasoner.isConsistent();
				log.debug("Post precomputedInference Consistent: " + consistent);
				log.debug("\n");

				if (consistent) {
					// Get a list of unsatisfiable classes (a class is unsatisfiable if it can't possibly have any instances).
					Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
					// Procedure to disable temporarily std output
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos);
					PrintStream old = System.out;// Save the old System.out
					System.setOut(ps);
					System.out.println("ClassTree:\n");
					reasoner.getKB().printClassTree();
					System.out.flush();
					System.setOut(old);
					writer.println(baos.toString());
					// This node contains owl:Nothing and all the classes that are equivalent to owl:Nothing
					Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();

					if (!unsatisfiable.isEmpty()) {
						log.info("The following classes are unsatisfiable: ");
						for (OWLClass cls : unsatisfiable) {
							log.info("    " + cls);
						}
					} else {
						log.info("There are no instantiated unsatisfiable classes\n");
					}
					log.info("\n");
				}
				else {
					result = -1;
					log.info("ERROR inconsistent ontology");
					erm.getWriter().print(erm.getCompleteExplanation().toString());
				}
			}
			else if (!consistent) {
				result = -2;
				log.info("ERROR inconsistent ontology");
				erm.getWriter().print(erm.getCompleteExplanation().toString());
			}
			OWLOntology inferredOntology = ontology;

			//if ontology passes consistency checking perform reasoning
			if (result > 0 && "true".equalsIgnoreCase(properties.getProperty("doReasoning"))) {
				boolean[] howInferredAxioms = createBooleanArrayForReasoner(properties);
				OpenlletReasoner reasonerToWrite = new PelletReasoner(ontology, BufferingMode.NON_BUFFERING);
				OWLReasonerAndTest orat = new OWLReasonerAndTest();
				result = orat.createOwl2ExampleModel(ontology, manager, reasonerToWrite, howInferredAxioms, properties.getProperty("reportFile"));
				inferredOntology = orat.getInferredOntology();
				//Print Class Tree With Inferred Axioms with procedure to disable temporarily std output
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);
				PrintStream old = System.out;// Save the old System.out
				System.setOut(ps);
				System.out.println("ClassTreeWithInferredAxioms:\n");
				reasonerToWrite.getKB().printClassTree();
				System.out.flush();
				System.setOut(old);
				writer.println(baos.toString());
				result = (reasonerToWrite.isConsistent())?1:0;
			}
			writer.close();

			// If ontology is consistent store the ontology in a file
			if (result > 0) {

				RDFXMLDocumentFormat rdfDocFormat = ("true".equalsIgnoreCase(properties.getProperty("expandOntology")))
						? (RDFXMLDocumentFormat) OWLOntologyCreator.setRdfPrefix(inferredOntology) :new RDFXMLDocumentFormat();
						
						// Save a local copy of the ontology
						// log.info("Starting graph storage operations");
						long startTime = System.currentTimeMillis();

						URI fileURI = new File(owlPath).toURI();
						manager.saveOntology(inferredOntology, rdfDocFormat, IRI.create(fileURI));
						endTime = System.currentTimeMillis();
						// log.info(fileURI.toString() + " ...saved");
						log.debug("tempo totale di scrittura: " + (endTime-startTime) + "ms");

						startTime = System.currentTimeMillis();
						
						/* If the narrative is one of our case studies, update it on Blazegraph
						 * Similar code should be used to publish the narratives created by users of the tool
						 * 
						 
						String provv= "C:/Users/emale/Desktop/narra-triple-main/target/vale.owl";
						String nid = narrative.getId();
											
						
						if ((nid.equals("N7")) || nid.equals("N2") || nid.equals("N3") || nid.equals("N4")) {
							String graph = Vocabulary.base + "narrative/" + nid;
							BlazegraphLoader bg = new BlazegraphLoader();
							bg.removeNamedGraph("narra7", graph);
							//log.info("Removed named graph");
							//bg.insertNamedGraphFromFile("narra5", fileURI.toString().split(":")[1], graph);
							bg.insertNamedGraphFromFile("narra7", provv, graph);
						}*/
						
						//String local="";
						//if(baseUrl.equals("dlnarratives.moving.d4science.org")) {
							//local= "https://tool.dlnarratives.eu/fuseki/Narration2";
						//} else {
							//local= "https://tool.dlnarratives.eu/fuseki/narratives";
						//}
						
						//local= "https://tool.dlnarratives.eu/fuseki/moving"; 
						
						
						// local= "http://localhost:3030/moving/";
						local= "https://geosparql.isti.cnr.it/fuseki/moving/";

						RDFConnection conn = RDFConnection.connect(local);
						// conn.update("DROP GRAPH <https://geosparql.isti.cnr.it/fuseki/seminar-openllet/> ;");
						conn.put("./OWL/grafo_completo.owl");
						// conn.delete(name);
						// conn.put(name, owlPath);


						
						
						//conn.delete("https://tool.dlnarratives.eu/adminNarra.1_N1238");
						//conn.update("DROP GRAPH <https://tool.dlnarratives.eu/MOVING_NM_1>");
						
						//conn.fetchDataset();
						//DatasetGraph dsg = ds.asDatasetGraph();
						//DatabaseMgr.backup(dsg);
						
						//DatabaseMgr.backup(dsg.asDatasetGraph());
						//DatabaseMgr.backup(dsg);
						

						//conn.load(fileURI.toString().split(":")[1]); 
						//conn.put("https://tool.dlnarratives.eu/" + narrative.getAuthor() + "_" + narrative.getId(), fileURI.toString().split(":")[1]);
						//conn.put("https://tool.dlnarratives.eu/" + narrative.getAuthor() + "_" + narrative.getId(), "./Target/n.owl");
						//conn.put("https://tool.dlnarratives.eu/" + narrative.getAuthor() + "_" + narrative.getId(), owlPath);
						

						//conn.delete("https://tool.dlnarratives.eu/adminNarra.1_N767");
						//conn.load( "grafo1", "./Target/n.owl");
						//conn.put("https://tool.dlnarratives.eu/manzoni", "./Target/n.owl");
						


						}
			else {
				log.error("ERROR in reasoning: " + result + " Graph loading canceled");
			}
			// log.info("Procedure completed");
			endTime = System.currentTimeMillis();
		}
		catch (Exception e) {
			log.error(getStackTrace(e));
		}
	}
		}

		// QUI FINE FOR
		
	}

	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}


	public void doExplanation(OWLOntologyManager manager, String file) throws OWLException, IOException, UnsupportedOperationException
	{
		PelletExplanation.setup();

		// The renderer is used to pretty print clashExplanation
		final ManchesterSyntaxExplanationRenderer renderer = new ManchesterSyntaxExplanationRenderer();
		// The writer used for the clashExplanation rendered
		final PrintWriter out = new PrintWriter(System.out);
		renderer.startRendering(out);

		// manager allows to load an ontology file and create OWLEntities
		final OWLOntology ontology = manager.loadOntology(IRI.create(file));

		// Create the reasoner and load the ontology
		final OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);

		// Create an clashExplanation generator
		final PelletExplanation expGen = new PelletExplanation(reasoner);

		Iterator<OWLAxiom> iter = ontology.axioms().iterator();
		while(iter.hasNext()){
			OWLAxiom next = iter.next();
			if(next.isOfType(AxiomType.CLASS_ASSERTION)){
				AxiomType<OWLClassAssertionAxiom> owlAssertionType = (AxiomType<OWLClassAssertionAxiom>) next.getAxiomType();
				Class<OWLClassAssertionAxiom> classOwlAssertionAxiom = owlAssertionType.getActualClass();
				final OWLClassExpression owlClassExp = classOwlAssertionAxiom.cast(next).getClassExpression();
				Set<Set<OWLAxiom>> exp = expGen.getUnsatisfiableExplanations(owlClassExp);
				out.println("follow because "+owlClassExp+" concept is unsatisfiable");
				renderer.render(exp);
			}
		}
		renderer.endRendering();
	}


	private static boolean[] createBooleanArrayForReasoner(Properties properties){
		boolean[] aryToReason = new boolean[12];
		aryToReason[0]="yes".equalsIgnoreCase(properties.getProperty("InferClassAssertionAxiom"))?true:false;
		aryToReason[1]="yes".equalsIgnoreCase(properties.getProperty("InferSubClassAxiom"))?true:false;
		aryToReason[2]="yes".equalsIgnoreCase(properties.getProperty("InferEquivalentClassAxiom"))?true:false;
		aryToReason[3]="yes".equalsIgnoreCase(properties.getProperty("InferDisjointClassesAxiom"))?true:false;
		aryToReason[4]="yes".equalsIgnoreCase(properties.getProperty("InferPropertyAssertion"))?true:false;
		aryToReason[5]="yes".equalsIgnoreCase(properties.getProperty("InferInverseObjectPropertiesAxiom"))?true:false;
		aryToReason[6]="yes".equalsIgnoreCase(properties.getProperty("InferEquivalentObjectPropertyAxiom"))?true:false;
		aryToReason[7]="yes".equalsIgnoreCase(properties.getProperty("InferSubObjectPropertyAxiom"))?true:false;
		aryToReason[8]="yes".equalsIgnoreCase(properties.getProperty("InferEquivalentDataPropertiesAxiom"))?true:false;
		aryToReason[9]="yes".equalsIgnoreCase(properties.getProperty("InferSubDataPropertyAxiom"))?true:false;
		aryToReason[10]="yes".equalsIgnoreCase(properties.getProperty("InferObjectPropertyCharacteristicAxiom"))?true:false;
		aryToReason[11]="yes".equalsIgnoreCase(properties.getProperty("InferDataPropertyCharacteristicAxiom"))?true:false;
		return aryToReason;
	}


}
