package narra.triplifier.resource;

/**
 * a class to represent the used vocabulary
 */
public class Vocabulary {
	
	// Prefixes
	public static final String foaf = "http://xmlns.com/foaf/0.1/";
	public static final String efrbroo = "http://erlangen-crm.org/efrbroo/";
	public static final String cnt = "http://www.w3.org/2011/content#";
	public static final String ecrm = "http://erlangen-crm.org/current/";
	public static final String crminf = "https://dlnarratives.eu/crminf/";
	public static final String crmgeo = "https://dlnarratives.eu/crmgeo/";
	public static final String crmsci = "http://www.ics.forth.gr/isl/CRMsci/";
	public static final String dc = "http://purl.org/dc/elements/1.1/";
	public static final String dctypes = "http://purl.org/dc/dcmitype/";
	public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String dcterms = "http://purl.org/dc/terms/";
	public static final String narra = "https://dlnarratives.eu/ontology#";
	public static final String wikidata = "http://wikidata.org/entity/";
	public static final String time = "http://www.w3.org/2006/time#";
	public static final String base = "https://dlnarratives.eu/";
	public static final String geo = "http://www.opengis.net/ont/geosparql#";
	
	

	
	// CRM names
	public static enum ecrmNames {
		E1_CRM_Entity, E5_Event, E7_Activity, E18_Physical_Thing, E19_Physical_Object, E21_Person, E28_Conceptual_Object, E29_Design_or_Procedure,
		E39_Actor, E41_Appellation, E42_Identifier, E47_Spatial_Coordinates, E53_Place, E65_Creation, E67_Birth, E69_Death, E73_Information_Object, 
		E77_Persistent_Item, E85_Joining, E86_Leaving, E89_Propositional_Object, E90_Symbolic_Object, P3_has_note, P7_took_place_at,
		P8_took_place_on_or_within, P9_consists_of, P12_occurred_in_the_presence_of, P14_carried_out_by,
		P79_beginning_is_qualified_by, P80_end_is_qualified_by, P94_has_created, P106_is_composed_of, P129_is_about,
		P67_refers_to, P1_is_identified_by,
		E52_Time_Span { public String toString() { return "E52_Time-Span"; }}, // Java does not accept dash (-) in enum name
		P4_has_time_span { public String toString() { return "P4_has_time-span"; }} // Java does not accept dash (-) in enum name
, E94_Space_primitive, P168_place_is_defined_by, P48_has_preferred_identifier, P161_has_spatial_projection
	}
	
	// Narra names NICO
	public static enum narraNames {
		ActorWithRole, hadParticipant, hasSubject, hasRole, hasSource, hasBibliographicReference, hasTextFragment,
		hasReference, causallyDependsOn, numBook, numChapter, instantEquals, timeSpanStartedBy, timeSpanFinishedBy,
		hasFabula, hasEvent, hasText, Narrative, Biography, Fabula, partOfNarrative, Role, propSubject, propObject,
		propPredicate, Proposition, holdsBelief, hasEntity, isEntityOf, hasDescription, isAboutCountry, isAboutLAU
		// NICO ADD isAboutCountry, isAboutLAU 
	}
	
	// CRMinf names
	public static enum crminfNames {
		I2_Belief, I4_Proposition_Set, I5_Inference_Making, J1_was_premise_for, J2_concluded_that, J4_that
	}
	
	// CRMsci names 
	public static enum crmsciNames {
		O8_observed, O16_observed_value, S4_Observation, S15_Observable_Entity
	}

	// CRMgeo names 
	public static enum crmgeoNames {
		SP2_Phenomenal_Place, SP3_Reference_Space, SP4_Spatial_Coordinate_Reference_System, SP5_Geometric_Place_Expression, SP6_Declarative_Place, Q10_defines_place, Q9_is_expressed_in_terms_of, Q11_approximates, Q11i_is_approximated_by, Q7_describes, Q5_defined_in

	}

	// Geosparql names 
	public static enum geoNames {
		SpatialObject, Feature, Geometry, hasGeometry, hasDefaultGeometry, hasSerialization, asWKT, asGML, sfContains, sfWithin, hasBoundingBox, hasCentroid
		// NICO ADD sfContains, sfWithin

	}

	// Time names
	public static enum timeNames {Instant, before, after, inXSDDate}
	
	// CNT names
	public static enum cntNames {ContentAsText, chars}
		
	// eFRBRoo names
	public static enum efrbrooNames {F23_Expression_Fragment}
	
	

}
