package narra.triplifier.model;

import org.apache.jena.vocabulary.OWL;

import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import narra.triplifier.resource.Vocabulary;
import narra.triplifier.util.Log4JClass;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;

public class OWLOntologyCreator {

	private enum PrefixNs{rdf, rdfs, owl, ecrm, crminf, efrbroo, cnt, dc, dctypes, narra, time, base}

	// Get logger instance
	private Logger log = null;
	private OWLOntology model;
	private OWLOntologyManager manager;
	private OWLDataFactory dataFactory;

	public OWLOntologyCreator(OWLOntologyManager manager, OWLOntology ontology) {
		dataFactory = manager.getOWLDataFactory();
		this.manager = manager;		
		this.model = ontology;
		log = Log4JClass.getLogger();
	}
	
	public static PrefixDocumentFormat setRdfPrefix(OWLOntology ontology){
		PrefixDocumentFormat preMan = new RDFXMLDocumentFormat();
		//		this.oNamespaceManager = new OWLOntologyXMLNamespaceManager(ontology, owlDocFormat);
		preMan.setPrefix(PrefixNs.rdfs.toString(),            Vocabulary.rdfs);
		preMan.setPrefix(PrefixNs.ecrm.toString(),            Vocabulary.ecrm);
		preMan.setPrefix(PrefixNs.crminf.toString(),          Vocabulary.crminf);
		preMan.setPrefix(PrefixNs.efrbroo.toString(),         Vocabulary.efrbroo);
		preMan.setPrefix(PrefixNs.cnt.toString(),             Vocabulary.cnt);
		preMan.setPrefix(PrefixNs.dc.toString(),              Vocabulary.dc);
		preMan.setPrefix(PrefixNs.dctypes.toString(),         Vocabulary.dctypes);
		preMan.setPrefix(PrefixNs.narra.toString(),           Vocabulary.narra);
		preMan.setPrefix(PrefixNs.time.toString(),            Vocabulary.time);
		preMan.setDefaultPrefix(Vocabulary.base);
		return preMan;
	}

	public void createOntology() {
		try {
			/*
			// Informazioni sull'ontologia
			Ontology onto = model.createOntology(Vocabulary.narr);

			model.add(onto, RDFS.label, "An Ontology for Narratives");
			model.add(onto, RDFS.comment, "");
			model.add(onto, DC.date, "2017-02-20");
			model.add(onto, OWL.versionInfo, "1.0");
			model.add(onto, DC.creator, "Carlo Meghini, Valentina Bartalesi, Daniele Metilli, Filippo Benedetti");
			model.add(onto, DC.publisher, "ISTI-CNR");
			//model.add(onto, DC.rights, "http://creativecommons.org/licenses/by-nc-sa/4.0/");

			// Importazione di ontologie di riferimento
			onto.addImport(model.createResource(Vocabulary.cnt));
			onto.addImport(model.createResource(Vocabulary.ecrm));
			onto.addImport(model.createResource(Vocabulary.efrbroo));
			onto.addImport(model.createResource(Vocabulary.time));
			//onto.addImport(model.createResource(Vocabulary.dc));
			//onto.addImport(model.createResource(Vocabulary.dctypes));
			*/

			// CIDOC CRM classes
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E41_Appellation.toString(), null);
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E7_Activity.toString(), null);
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E65_Creation.toString(), null);
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E39_Actor.toString(), null);
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(), null);
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E53_Place.toString(), null);
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E18_Physical_Thing.toString(), null);
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E65_Creation.toString(), null);
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E73_Information_Object.toString(), null);
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E77_Persistent_Item.toString(), null);
			//AGGIUNTE//
			createClassWithDescription(Vocabulary.ecrm + Vocabulary.ecrmNames.E94_Space_primitive.toString(),null);
			
			
			// CRMinf classes
			createClassWithDescription(Vocabulary.crminf + Vocabulary.crmsciNames.S15_Observable_Entity.toString(), null);
			createClassWithDescription(Vocabulary.crminf + Vocabulary.crmsciNames.S4_Observation.toString(), null);
			createClassWithDescription(Vocabulary.crminf + Vocabulary.crminfNames.I2_Belief.toString(), null);
			createClassWithDescription(Vocabulary.crminf + Vocabulary.crminfNames.I5_Inference_Making.toString(), null);
			createClassWithDescription(Vocabulary.crminf + Vocabulary.crminfNames.I4_Proposition_Set.toString(), null);
			
			// EFRBRoo classes
			createClassWithDescription(Vocabulary.efrbroo + Vocabulary.efrbrooNames.F23_Expression_Fragment.toString(), null);

			// GEOSPARQL classes
			createClassWithDescription(Vocabulary.geo + Vocabulary.geoNames.SpatialObject.toString(), "The class Spatial Object represents everything that can have a spatial representation. It is superclass of feature  and geometry");

			createClassWithDescriptionAndSuperclass(
				Vocabulary.geo + Vocabulary.geoNames.Feature.toString(),
				"This class represents the top-level feature type. This class is equivalent to GFI_Feature defined in ISO 19156, and it is superclass of all feature types.",
				Vocabulary.geo + Vocabulary.geoNames.SpatialObject.toString()
			);

			createClassWithDescriptionAndSuperclass(
				Vocabulary.geo + Vocabulary.geoNames.Geometry.toString(),
				"The class represents the top-level geometry type. This class is equivalent to the UML class GM_Object defined in ISO 19107, and it is superclass of all geometry types.",
				Vocabulary.geo + Vocabulary.geoNames.SpatialObject.toString()
			);
			
			// Class narra:Narrative
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Narrative.toString(),
				"This class represents a narrative.",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E73_Information_Object.toString()
			);
			
			// Class narra:Biography
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Biography.toString(),
				"This class represents a biographical narrative.",
				Vocabulary.narra + Vocabulary.narraNames.Narrative.toString()
			);
			
			// Class narra:ActorWithRole
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.ActorWithRole.toString(),
				"In order to assign a role to an actor, this reification class was introduced."
				+ "Through the property hadParticipant an event is related with this class."
				+ "ActorWithRole is related with the class Actor through the property hasSubject"
				+ "and to a literal that represents the role through the property hasRole.",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E1_CRM_Entity.toString()
			);
			
			// Class narra:Role
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Role.toString(),
				"This class represents a role in the event.",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E1_CRM_Entity.toString()
			);

			// Class crmgeo:SP4_Spatial_Coordinate_Reference_System
			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP2_Phenomenal_Place.toString(),
				"This class compromises systems that are used to describe locations.",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E53_Place.toString()
			);

			// Class crmgeo:SP4_Spatial_Coordinate_Reference_System
			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP2_Phenomenal_Place.toString(),
				"This class compromises systems that are used to describe locations.",
				Vocabulary.geo + Vocabulary.geoNames.Feature.toString()
			);

			// Class crmgeo:SP4_Spatial_Coordinate_Reference_System
			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP4_Spatial_Coordinate_Reference_System.toString(),
				"This class compromises systems that are used to describe locations.",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E29_Design_or_Procedure.toString()
			);

			// Class crmgeo:SP5_Geometric_Place_Expression
			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP5_Geometric_Place_Expression.toString(),
				"This class comprises definitions of places by quantitative expressions.",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E94_Space_primitive.toString()
			);

			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP5_Geometric_Place_Expression.toString(),
				"This class comprises definitions of places by quantitative expressions.",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E47_Spatial_Coordinates	.toString()
			);

			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP5_Geometric_Place_Expression.toString(),
				"This class comprises definitions of places by quantitative expressions.",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E73_Information_Object.toString()
			);

			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP5_Geometric_Place_Expression.toString(),
				"This class comprises definitions of places by quantitative expressions.",
				Vocabulary.geo + Vocabulary.geoNames.Geometry.toString()
			);


			// Class crmgeo:SP6_Declarative_Place
			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP6_Declarative_Place.toString(),
				"This class comprises instances of E53 Place (S) whose extent (U) and position is defined by an E94 Space Primitive (S).",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E53_Place.toString()
			);

			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP6_Declarative_Place.toString(),
				"This class comprises instances of E53 Place (S) whose extent (U) and position is defined by an E94 Space Primitive (S).",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E89_Propositional_Object.toString()
			);

			createClassWithDescriptionAndSuperclass(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP6_Declarative_Place.toString(),
				"This class comprises instances of E53 Place (S) whose extent (U) and position is defined by an E94 Space Primitive (S).",
				Vocabulary.geo + Vocabulary.geoNames.Geometry.toString()
			);
			
			// Class narra:Proposition
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(),
				"This class represents a proposition endowed with a subject, predicate, and object.",
				Vocabulary.ecrm + Vocabulary.ecrmNames.E1_CRM_Entity.toString()
			);

			// Property geo:hasGeometry
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.geo + Vocabulary.geoNames.hasGeometry.toString(),
				Vocabulary.geo + Vocabulary.geoNames.Feature.toString(),
				Vocabulary.geo + Vocabulary.geoNames.Geometry.toString(),
				"This property relates a proposition to its subject (an event)."
			);

			
			// Property geo:hasDefaultGeometry
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.geo + Vocabulary.geoNames.hasDefaultGeometry.toString(),
				Vocabulary.geo + Vocabulary.geoNames.Feature.toString(),
				Vocabulary.geo + Vocabulary.geoNames.Geometry.toString(),
				"The default geometry to be used in spatial calculations, usually the most detailed geometry."
			);

			createObjectPropertySuperproperty(Vocabulary.geo + Vocabulary.geoNames.hasDefaultGeometry.toString(), Vocabulary.geo + Vocabulary.geoNames.hasGeometry.toString());

			// Property geo:hasSerialization
			createDataPropertyWithDomainAndRange(
				Vocabulary.geo + Vocabulary.geoNames.hasSerialization.toString(),
				Vocabulary.geo + Vocabulary.geoNames.Geometry.toString(),
				OWLRDFVocabulary.RDFS_LITERAL.getIRI().toString()
			);

			// Property narra:propSubject
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.propSubject.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
				"This property relates a proposition to its subject (an event)."
			);

			// Property narra:propPredicate
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.propPredicate.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(),
				OWL.ObjectProperty.toString(),
				"This property relates a proposition to its predicate (a property)."
			);

			// Property narra:propObject
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.propObject.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E1_CRM_Entity.toString(),
				"This property relates a proposition to its object (a CRM entity)."
			);

			// Property narra:hasText
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hasText.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Narrative.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E90_Symbolic_Object.toString(),
				"This property relates a narrative to the text that expresses it."
			);
			
			// Property narra:partOfNarrative
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.partOfNarrative.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Narrative.toString(),
				"This property relates an event to the narrative that contains it."
			);
			
			// Property narra:partOfNarrative
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.holdsBelief.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E39_Actor.toString(),
				Vocabulary.crminf + Vocabulary.crminfNames.I2_Belief.toString(),
				"This property relates an actor to the belief held by him/her."
			);
			
			// Property narra:hasEntity
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hasEntity.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E1_CRM_Entity.toString(),
				"This property relates an entity to his event."
			);
			
			// Property narra:isEntityOf
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.isEntityOf.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E1_CRM_Entity.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
				"This property relates an event to his entity."
			);

			// Property narra:instantEquals
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.instantEquals.toString(),
					Vocabulary.time + Vocabulary.timeNames.Instant.toString(),
					Vocabulary.time + Vocabulary.timeNames.Instant.toString(),
					"This property relates an instant with another instant that is equal to it."
							+ "This is needed to match uncertain instants that are inferred to be the same by the reasoner."
					);

			// Property narra:hadParticipant
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.hadParticipant.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
					Vocabulary.narra + Vocabulary.narraNames.ActorWithRole.toString(),
					"This property relates an event with an instance of the class ActorWithRole."
					);

			// Property narra:hasSubject
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.hasSubject.toString(),
					Vocabulary.narra + Vocabulary.narraNames.ActorWithRole.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E39_Actor.toString(),
					"This property relates the class ActorWithRole with the class E39 Actor."
					);

			// Property narra:hasRole
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.hasRole.toString(),
					Vocabulary.narra + Vocabulary.narraNames.ActorWithRole.toString(),
					Vocabulary.narra + Vocabulary.narraNames.Role.toString(),
					"This property relates the class ActorWithRole with a literal that represents."
					);

			// Property narra:hasSource
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.hasSource.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E73_Information_Object.toString(),
					Vocabulary.crminf + Vocabulary.crmsciNames.S15_Observable_Entity.toString(),
					"This property directly relates a proposition with an observable entity."
					);

			// Property narra:hasTextFragment
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.hasTextFragment.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E73_Information_Object.toString(),
					Vocabulary.efrbroo + Vocabulary.efrbrooNames.F23_Expression_Fragment.toString(),
					"This property relates a proposition with a text fragment."
					);

			// Property narra:hasReference
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.hasReference.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E73_Information_Object.toString(),
					Vocabulary.efrbroo + Vocabulary.efrbrooNames.F23_Expression_Fragment.toString(),
					"This property relates a proposition with a reference fragment."
							+ "For instance, the reference fragment \"Inferno II, 121\" +"
							+ "refers to a specific part of the work \"Divine Comedy\"."
					);

			// Property narra:causallyDependsOn
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.causallyDependsOn.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
					"This property relates an event with another event that caused it."
							+ "This property connects events that in normal discourse are predicated"
							+ "to have a cause-effect relation, e.g. the eruption of the Vesuvius"
							+ "caused the destruction of Pompeii."
					);
			
			// NICO ADD START 
			// Property narra:isAboutCountry
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.isAboutCountry.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E89_Propositional_Object.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E1_CRM_Entity.toString(),
					"This property relates a narratives with a country."
					);
			
			createObjectPropertySuperproperty(Vocabulary.narra + Vocabulary.narraNames.isAboutCountry.toString(), Vocabulary.ecrm + Vocabulary.ecrmNames.P129_is_about.toString());

			// Property narra:isAboutLAU
			createObjPropertyWithDomainRangeAndDescription(
					Vocabulary.narra + Vocabulary.narraNames.isAboutLAU.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E89_Propositional_Object.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E1_CRM_Entity.toString(),
					"This property relates a narratives with a LAU."
					);

			createObjectPropertySuperproperty(Vocabulary.narra + Vocabulary.narraNames.isAboutLAU.toString(), Vocabulary.ecrm + Vocabulary.ecrmNames.P129_is_about.toString());

			// NICO ADD END
			
			// Property hasDescription
			createDataPropertyWithDomainAndRange(
					Vocabulary.narra + Vocabulary.narraNames.hasDescription.toString(),
					Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
					XSDVocabulary.STRING.toString()
					);
			
			// Property geo:asWKT
			createDataPropertyWithDomainAndRange(
				Vocabulary.geo + Vocabulary.geoNames.asWKT.toString(),
				Vocabulary.geo + Vocabulary.geoNames.Geometry.toString(),
				"http://www.opengis.net/ont/geosparql#wktLiteral"
				);
			
			createDataPropertySuperproperty(Vocabulary.geo + Vocabulary.geoNames.asWKT.toString(), Vocabulary.geo + Vocabulary.geoNames.hasSerialization.toString());

			// Property time:before
			createObjPropertyWithDomainAndRange(
					Vocabulary.time + Vocabulary.timeNames.before.toString(),
					Vocabulary.time + Vocabulary.timeNames.Instant.toString(),
					Vocabulary.time + Vocabulary.timeNames.Instant.toString()
					);

			// Property time:after
			createObjPropertyWithDomainAndRange(
					Vocabulary.time + Vocabulary.timeNames.after.toString(),
					Vocabulary.time + Vocabulary.timeNames.Instant.toString(),
					Vocabulary.time + Vocabulary.timeNames.Instant.toString()
					);

			// Property cnt:chars
			createDataPropertyWithDomainAndRange(
					Vocabulary.cnt + Vocabulary.cntNames.chars.toString(),
					Vocabulary.efrbroo + Vocabulary.efrbrooNames.F23_Expression_Fragment.toString(),
					XSDVocabulary.STRING.toString()
					);

			// Property crminf:O8_observed
			createObjPropertyWithDomainAndRange(
					Vocabulary.crminf + Vocabulary.crmsciNames.O8_observed.toString(),
					Vocabulary.crminf + Vocabulary.crmsciNames.S15_Observable_Entity.toString(),
					Vocabulary.crminf + Vocabulary.crmsciNames.S4_Observation.toString()
					);

			// Property crminf:O16_observed_value
			createObjPropertyWithDomainAndRange(
					Vocabulary.crminf + Vocabulary.crmsciNames.O16_observed_value.toString(),
					Vocabulary.crminf + Vocabulary.crmsciNames.S4_Observation.toString(),
					Vocabulary.crminf + Vocabulary.crminfNames.I4_Proposition_Set.toString()
					);

			// Property crminf:J1_was_premise_for
			createObjPropertyWithDomainAndRange(
					Vocabulary.crminf + Vocabulary.crminfNames.J1_was_premise_for.toString(),
					Vocabulary.crminf + Vocabulary.crminfNames.I2_Belief.toString(),
					Vocabulary.crminf + Vocabulary.crminfNames.I5_Inference_Making.toString()
					);

			// Property crminf:J2_concluded_that
			createObjPropertyWithDomainAndRange(
					Vocabulary.crminf + Vocabulary.crminfNames.J2_concluded_that.toString(),
					Vocabulary.crminf + Vocabulary.crminfNames.I5_Inference_Making.toString(),
					Vocabulary.crminf + Vocabulary.crminfNames.I2_Belief.toString()
					);

			// Property crminf:J4_that
			createObjPropertyWithDomainAndRange(
					Vocabulary.crminf + Vocabulary.crminfNames.J4_that.toString(),
					Vocabulary.crminf + Vocabulary.crminfNames.I2_Belief.toString(),
					Vocabulary.crminf + Vocabulary.crminfNames.I4_Proposition_Set.toString()
					);	
			
			// Property crmgeo:Q7_describes
			createObjPropertyWithDomainAndRange(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.Q7_describes.toString(),
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP4_Spatial_Coordinate_Reference_System.toString(),
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP3_Reference_Space.toString()
				);	
			
			// Property crmgeo:Q5_defined_in
			createObjPropertyWithDomainAndRange(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.Q5_defined_in.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E53_Place.toString(),
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP3_Reference_Space.toString()
				);	
		
			
			

		}
		// GESTIONE ECCEZIONI PRINCIPALE
		catch (Exception e) {
			log.error("EXCEPTION - In OwlOntologyCreator: " + e.getMessage());
			e.getStackTrace();

		}
	}//endMethod

	/**
	 * Add a new declaration for the ObjectProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range class (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createObjPropertyWithDomainAndRange(IRI p, String d, String r) {
		OWLObjectProperty objProperty = dataFactory.getOWLObjectProperty(p);
		//TODO to add the possibility to add characteristic property
		//		OWLTransitiveObjectPropertyAxiom propertyChAxiom = dataFactory.getOWLTransitiveObjectPropertyAxiom(objProperty);
		//		manager.addAxiom(model, propertyChAxiom);
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(objProperty);
		manager.addAxiom(model, declarationAxiom);
		OWLObjectPropertyDomainAxiom domainAxiom = dataFactory.getOWLObjectPropertyDomainAxiom(objProperty, dataFactory.getOWLClass(d));
		manager.addAxiom(model, domainAxiom);
		OWLObjectPropertyRangeAxiom rangeAxiom = dataFactory.getOWLObjectPropertyRangeAxiom(objProperty, dataFactory.getOWLClass(r));
		manager.addAxiom(model, rangeAxiom);
	}
	/**
	 * Add a new declaration for the DataProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range primitive type (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createDataPropertyWithDomainAndRange(IRI p, String d, String r) {
		OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(p);
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(dataProperty);
		manager.addAxiom(model, declarationAxiom);
		OWLDataPropertyDomainAxiom domainAxiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, dataFactory.getOWLClass(d));
		manager.addAxiom(model, domainAxiom);
		OWLDataPropertyRangeAxiom rangeAxiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, new OWLDatatypeImpl(IRI.create(r)));
		manager.addAxiom(model, rangeAxiom);
	}
	/**
	 * Add a new declaration for the ObjectProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range class (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createObjPropertyWithDomainAndRange(String p, String d, String r) {
		IRI propertyIRI = IRI.create(p);
		createObjPropertyWithDomainAndRange(propertyIRI, d, r);
	}
	/**
	 * Add a new declaration for the DataProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range primitive type (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createDataPropertyWithDomainAndRange(String p, String d, String r) {
		IRI propertyIRI = IRI.create(p);
		createDataPropertyWithDomainAndRange(propertyIRI, d, r);
	}
	/**
	 * Add a new declaration for the ObjectProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range class (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createObjPropertyWithDomainRangeAndDescription(String p, String d, String r, String desc) {
		IRI propertyIRI = IRI.create(p);
		createObjPropertyWithDomainAndRange(propertyIRI, d, r);
		//Add Annotation
		OWLAnnotationAssertionAxiom owlAnnotation = dataFactory.getOWLAnnotationAssertionAxiom(
				new OWLAnnotationPropertyImpl(IRI.create(RDFS.COMMENT.toString())), propertyIRI, new OWLLiteralImpl(desc, "", new OWLDatatypeImpl(XSDVocabulary.STRING.getIRI())));
		manager.addAxiom(model, owlAnnotation);
	}
	/**
	 * Add a new declaration for the DataProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range primitive type (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createDataPropertyWithDomainRangeAndDescription(String p, String d, String r, String desc) {
		IRI propertyIRI = IRI.create(p);
		createDataPropertyWithDomainAndRange(propertyIRI, d, r);
		//Add Annotation
		OWLAnnotationAssertionAxiom owlAnnotation = dataFactory.getOWLAnnotationAssertionAxiom(
				new OWLAnnotationPropertyImpl(IRI.create(RDFS.COMMENT.toString())), propertyIRI, new OWLLiteralImpl(desc, "", new OWLDatatypeImpl(XSDVocabulary.STRING.getIRI())));
		manager.addAxiom(model, owlAnnotation);
	}
	/**
	 * Add a Declaration for class c with description desc (note desc may be null for class without description)
	 * @param c the class for which create a new declaration axiom
	 * @param desc a description for the class added like annotation
	 */
	protected void createClassWithDescription(String c, String desc) {
		//Add class
		IRI classIRI = IRI.create(c);
		OWLClass newClass = dataFactory.getOWLClass(classIRI);
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newClass);
		manager.addAxiom(model, declarationAxiom);
		if(desc!=null && !"".equals(desc)){
			//Add Annotation
			OWLAnnotationAssertionAxiom owlAnnotation = dataFactory.getOWLAnnotationAssertionAxiom(
					new OWLAnnotationPropertyImpl(IRI.create(RDFS.COMMENT.toString())), classIRI, new OWLLiteralImpl(desc, "", new OWLDatatypeImpl(XSDVocabulary.STRING.getIRI())));
			manager.addAxiom(model, owlAnnotation);
		}

	}

	/**
	 * Add a Declaration for class c with description desc (note desc may be null for class without description)
	 * @param c the class for which create a new declaration axiom
	 * @param desc a description for the class added like annotation
	 * @param s the super class of c
	 */
	protected void createClassWithDescriptionAndSuperclass(String c, String desc, String s) {
		//Add class
		IRI classIRI = IRI.create(c);
		IRI superclassIRI = IRI.create(s);
		OWLClass newClass = dataFactory.getOWLClass(classIRI);
		OWLClass superClass = dataFactory.getOWLClass(superclassIRI);		
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newClass);
		manager.addAxiom(model, declarationAxiom);
		if(desc!=null && !"".equals(desc)){
			//Add subClassAxiom
			OWLSubClassOfAxiom subClassAxiom = dataFactory.getOWLSubClassOfAxiom(newClass, superClass);
			manager.addAxiom(model, subClassAxiom);
			
			//Add Annotation
			OWLAnnotationAssertionAxiom owlAnnotation = dataFactory.getOWLAnnotationAssertionAxiom(
					new OWLAnnotationPropertyImpl(IRI.create(RDFS.COMMENT.toString())), classIRI, new OWLLiteralImpl(desc, "", new OWLDatatypeImpl(XSDVocabulary.STRING.getIRI())));
			manager.addAxiom(model, owlAnnotation);
		}
		
	}

	/**
	 * Add object property p as subproperty of objectproperty s
	 * @param p the object property
	 * @param s the super property of p
	 */
	protected void createObjectPropertySuperproperty(String p, String s) {
		//Add class
		IRI propertyIRI = IRI.create(p);
		IRI superpropertyIRI = IRI.create(s);
		OWLObjectProperty newProperty = dataFactory.getOWLObjectProperty(propertyIRI);
		OWLObjectProperty superProperty = dataFactory.getOWLObjectProperty(superpropertyIRI);		
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newProperty);
		manager.addAxiom(model, declarationAxiom);

		//Add subPropertyAxiom
		OWLSubObjectPropertyOfAxiom subPropertyAxiom = dataFactory.getOWLSubObjectPropertyOfAxiom(newProperty, superProperty);
		manager.addAxiom(model, subPropertyAxiom);
		
	}
	/**
	 * Add object property p as subproperty of objectproperty s
	 * @param p the object property
	 * @param s the super property of p
	 */
	protected void createDataPropertySuperproperty(String p, String s) {
		//Add class
		IRI propertyIRI = IRI.create(p);
		IRI superpropertyIRI = IRI.create(s);
		OWLDataProperty newProperty = dataFactory.getOWLDataProperty(propertyIRI);
		OWLDataProperty superProperty = dataFactory.getOWLDataProperty(superpropertyIRI);		
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newProperty);
		manager.addAxiom(model, declarationAxiom);

		//Add subPropertyAxiom
		OWLSubDataPropertyOfAxiom subPropertyAxiom = dataFactory.getOWLSubDataPropertyOfAxiom(newProperty, superProperty);
		manager.addAxiom(model, subPropertyAxiom);
		
	}
}

