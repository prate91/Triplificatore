package narra.triplifier.store;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.openrdf.query.GraphQueryResult;

import com.bigdata.rdf.sail.webapp.SD;
import com.bigdata.rdf.sail.webapp.client.ConnectOptions;
import com.bigdata.rdf.sail.webapp.client.JettyResponseListener;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

import narra.triplifier.util.Log4JClass;

public class BlazegraphLoader {
	String conn_str;
	Properties configFile;
	String login;
	String pw;
	
	//Logger
	private Logger log;
	
	private static final String sparqlEndpoint = "https://dlnarratives.eu/blazegraph";
	
	public BlazegraphLoader(){
		//get Logger Instance
		log = Log4JClass.getLogger();
	}
	
	// Insert multiple named graphs
	public void insertMultipleNamedGraphs(String namespace, ArrayList<String> files, ArrayList<String> namedGraphs) {
		for (ListIterator<String> iter = files.listIterator(); iter.hasNext();) {
			insertNamedGraphFromFile(namespace, iter.next(), namedGraphs.get(iter.nextIndex()));
		}
	}
	
	// Insert named graph from file
	public void insertNamedGraphFromFile(String namespace, String rdfFile, String namedGraph) {
		// Load the RDF/XML file and convert it to Trig
		Model tripleModel = new LinkedHashModel();
		RDFParser parser = Rio.createParser(RDFFormat.RDFXML);
		parser.setRDFHandler(new StatementCollector(tripleModel));
		FileInputStream inputStream = null;
		
		try {
			inputStream = new FileInputStream(rdfFile);
		} catch (Exception e) {
			log.error("Error opening input file: " + rdfFile);
		}
		
		try {
			parser.parse(inputStream, rdfFile);
		} catch (Exception e) {
			log.error("Error parsing RDF/XML file: " + rdfFile);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				log.error("Error closing RDF/XML file: " + rdfFile);
			}
		}

		String trigFile = rdfFile.split("\\.")[0] + ".trig";
		FileOutputStream outputStream = null;
				
		try {
			outputStream = new FileOutputStream(trigFile);
		} catch (Exception e) {
			log.error("Error opening output file");
		}
		
		ModelBuilder builder = new ModelBuilder();
		for (Statement st: tripleModel) {
			builder.namedGraph(namedGraph).add(st.getSubject(), st.getPredicate(), st.getObject());
		}
		Model quadModel = builder.build();
		RDFWriter trigWriter = Rio.createWriter(RDFFormat.TRIG, outputStream);
					
		try {
			trigWriter.startRDF();
			for (Statement st: quadModel) {
				trigWriter.handleStatement(st);
			}
			trigWriter.endRDF();
		} catch (Exception e) {
			log.error("Error writing Trig file");
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
				log.error("Error closing Trig file");
			}
		}
		
		insertGraphFromFile(namespace, trigFile);
	}
	
	// Insert graph from single RDF file
	public void insertGraphFromFile(String namespace, String rdfFile) {
	    RemoteRepositoryManager repo = null;

		try {
		repo = new RemoteRepositoryManager(sparqlEndpoint);
		} catch(java.lang.NoSuchMethodError e) {}
				
		try {
			//JettyResponseListener response = getStatus(repo);
			//log.info(response.getResponseBody());

			final Properties properties = new Properties();
			properties.setProperty("com.bigdata.rdf.sail.namespace", namespace);

			log.info(namespaceExists(repo, namespace));

			if (!namespaceExists(repo, namespace)) {
				log.info(String.format("Create namespace %s...", namespace));
				repo.createRepository(namespace, properties);
				log.info(String.format("Create namespace %s done", namespace));
			} else {
				log.info(String.format("Namespace %s already exists", namespace));
			}

			// get properties for namespace
			//log.info(String.format("Property list for namespace %s", namespace));
			//response = getNamespaceProperties(repo, namespace);
			//log.info(response.getResponseBody());

			/*
			 * Load data from file located in the resource folder
			 * src/main/resources/data.n3
			 */
			loadDataFromResource(repo, namespace, rdfFile);

			// execute query
			/*
			TupleQueryResult result = repo.getRepositoryForNamespace(namespace)
					.prepareTupleQuery("SELECT * {?s ?p ?o} LIMIT 100")
					.evaluate();
			
			try {
				while (result.hasNext()) {
					BindingSet bs = result.next();
					log.info(bs);
				}
			} finally {
				result.close();
			}
			*/

		}
		catch (Exception e) {
			log.error("ECCEZIONE - BlazegraphLoader: " + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
	 finally {
		 	try {
		 		repo.close();
		 	}
		 	catch (Exception e) {
				log.error("ECCEZIONE - BlazegraphLoader: " + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
			}
		}
	}
	
	// Insert graph from single RDF file
		public void removeNamedGraph(String namespace, String graphURI) {
			
		    RemoteRepositoryManager repo = null;

			repo = new RemoteRepositoryManager(
					sparqlEndpoint, false /* useLBS */);

			
			try {
				JettyResponseListener response = getStatus(repo);
				log.info(response.getResponseBody());

				final Properties properties = new Properties();
				properties.setProperty("com.bigdata.rdf.sail.namespace", namespace);
				if (!namespaceExists(repo, namespace)) {
					log.info(String.format("Create namespace %s...", namespace));
					repo.createRepository(namespace, properties);
					log.info(String.format("Create namespace %s done", namespace));
				} else {
					log.info(String.format("Namespace %s already exists", namespace));
				}

				// get properties for namespace
				log.info(String.format("Property list for namespace %s", namespace));
				response = getNamespaceProperties(repo, namespace);
				log.info(response.getResponseBody());

				removeDataFromResource(repo, namespace, graphURI);
			}
			catch (Exception e) {
				log.error("ECCEZIONE - BlazegraphLoader: " + getStackTrace(e));
			}
		 finally {
			 	try {
			 		repo.close();
			 	}
			 	catch (Exception e) {
					log.error("ECCEZIONE - BlazegraphLoader: " + getStackTrace(e));
				}
			}
		}
		
		public static String getStackTrace(final Throwable throwable) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw, true);
			throwable.printStackTrace(pw);
			return sw.getBuffer().toString();
		}

		/*
		 * Status request.
		 */
		private static JettyResponseListener getStatus(RemoteRepositoryManager repo)
				throws Exception {

			ConnectOptions opts = new ConnectOptions(sparqlEndpoint + "/status");
			opts.method = "GET";
			return repo.doConnect(opts);

		}

		/*
		 * Check namespace already exists.
		 */
		private static boolean namespaceExists(RemoteRepositoryManager repo,
				String namespace) throws Exception {
			
			GraphQueryResult res = repo.getRepositoryDescriptions();
			try {
				while (res.hasNext()) {
					org.openrdf.model.Statement stmt = res.next();
					if (stmt.getPredicate()
							.toString()
							.equals(SD.KB_NAMESPACE.stringValue())) {
						if (namespace.equals(stmt.getObject().stringValue())) {
							return true;
						}
					}
				}
			} finally {
				res.close();
			}
			return false;
		}

		/*
		 * Get namespace properties.
		 */
		private static JettyResponseListener getNamespaceProperties(
				RemoteRepositoryManager repo, String namespace) throws Exception {

			ConnectOptions opts = new ConnectOptions(sparqlEndpoint + "/namespace/"
					+ namespace + "/properties");
			opts.method = "GET";
			return repo.doConnect(opts);

		}

		/*
		 * Load data into namespace.
		 */
		private static void loadDataFromResource(RemoteRepositoryManager repo,
				String namespace, String resource) throws Exception {
			FileInputStream is = new FileInputStream(resource);
			try {
				repo.getRepositoryForNamespace(namespace).add(
					new RemoteRepository.AddOp(is, org.openrdf.rio.RDFFormat.TRIG)
				);
			} finally {
				is.close();
			}
		}
		
		/*
		 * Remove data from namespace.
		 */
		private static void removeDataFromResource(RemoteRepositoryManager repo,
				String namespace, String graphURI) throws Exception {
			repo.getRepositoryForNamespace(namespace)
					.prepareUpdate("CLEAR GRAPH <" + graphURI + ">")
					.evaluate();
		}
	}

