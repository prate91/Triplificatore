package narra.triplifier.model;

import java.io.FileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.NumericEntityEscaper;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import narra.triplifier.resource.ActorWithRole;
import narra.triplifier.resource.Concept;
import narra.triplifier.resource.Other;
import narra.triplifier.resource.Entity;
import narra.triplifier.resource.Event;
import narra.triplifier.resource.Fragment;
import narra.triplifier.resource.Narrative;
import narra.triplifier.resource.PhysicalObject;
import narra.triplifier.resource.Place;
import narra.triplifier.resource.Proposition;
import narra.triplifier.resource.Reference;
import narra.triplifier.resource.Vocabulary;
import narra.triplifier.resource.Work;
import narra.triplifier.util.Log4JClass;


public class JsonToJava {	
	/**
	 * Escape strings for XML
	 * @param string The string
	 * @return an escaped string
	 */
	protected static String escapeString(String string) {
		return NumericEntityEscaper.below(0x20).translate(StringEscapeUtils.escapeXml10(string));
	}
	
	/**
	 * Loads JSON from file and converts its contents to Java objects
	 * @param filepath The file path
	 * @return a narrative object
	 * @throws IOException 
	 */
	public static Narrative loadJSON(String filePath) throws IOException {
		
		Logger log = Log4JClass.getLogger();
		
		// Containers of Wikidata resources, events, fragments
		HashMap<String,Entity> wikiResources = new HashMap<>();
		ArrayList<Event> eventList = new ArrayList<Event>();
		ArrayList<Fragment> fragList = new ArrayList<Fragment>();
		        		
		// Load JSON object from path and parse it
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(new FileReader(filePath)).getAsJsonObject();
		// Load events, entities, and relations from JSON object
		JsonObject narra = json.get("narra").getAsJsonObject();
		JsonObject resources = json.get("entities").getAsJsonObject();
		Set<Entry<String, JsonElement>> events = json.get("events").getAsJsonObject().entrySet();
		//Set<Entry<String, JsonElement>> relations = json.get("relations").getAsJsonObject().entrySet();
		
		Narrative narrative = new Narrative();
		String narrativeID = (narra.get("id").getAsString().startsWith("N")) ? narra.get("id").getAsString() : "N" + narra.get("id").getAsString();
		log.info("Narrative ID: " + narrativeID);
		
		narrative.setId(narrativeID);
		narrative.setName(narra.get("name").getAsString());
		narrative.setAuthor(narra.get("author").getAsString());
		
		
		// For each event entry...
		for (Entry<String, JsonElement> eventEntry : events) {
			
			// Load JSON object (event) from event entry
			JsonObject event = (JsonObject) eventEntry.getValue();

			// Load object properties
			String id = escapeString(event.get("_id").getAsString());
			String start = escapeString(event.get("start").getAsString());
			String end = escapeString(event.get("end").getAsString());
			String title = escapeString(event.get("title").getAsString());
			String notes = "";
			String description = "";
			String latitude = "";
			String longitude="";
			String eventMedia="";
			String type="";
			String polygon=""; // NICO ADD 
			
			
			if (event.get("type") != null) {
				type = escapeString(event.get("type").getAsString());
			}
			
			if (event.get("notes") != null) {
				notes = escapeString(event.get("notes").getAsString());
			}
			if (event.get("description") != null) {
				description = escapeString(event.get("description").getAsString());
			}
			ArrayList<String> digitalObjects = new ArrayList<String>();
						
			if (event.get("objurl") != null && event.get("objurl").isJsonArray()) {
				JsonArray objurl = event.get("objurl").getAsJsonArray();
				
				// if is new digital object with {title,url}
				if (!objurl.isJsonNull() && objurl.get(0).isJsonObject()) {
					for (int i=0; i < objurl.size(); i++) {

						digitalObjects.add(objurl.get(i).getAsJsonObject().get("url").getAsString());
						//System.out.println(objurl.get(i).getAsJsonObject().get("url").getAsString());
					}
				// if is old digital object (list of only links)
				} else {
					for (int i=0; i < objurl.size(); i++) {
						digitalObjects.add(objurl.get(i).getAsString());
					}				
				}
				
			}
			
			if (event.get("latitud") != null) {
				latitude = escapeString(event.get("latitud").getAsString());
			}
			if (event.get("longitud") != null) {
				longitude = escapeString(event.get("longitud").getAsString());
			}
			if (event.get("eventMedia") != null) {
				eventMedia = escapeString(event.get("eventMedia").getAsString());
			}
			// NICO ADD START
			if (event.get("polygon") != null) {
				polygon = escapeString(event.get("polygon").getAsString());
			}
			// NICO ADD END

						
			// Create event instance
			Event e = new Event();
			
			// Set event properties
			e.setUri("event/" + id);
			e.setEventType(type);
			e.setStartDate(start);
			e.setEndDate(end);
			e.setEventTitle(title);
			e.setEventNotes(notes);
			e.setDescription(description);
			e.setLatitude(latitude);
			e.setLongitude(longitude);
			e.setEventMedia(eventMedia);
			e.setPolygon(polygon); // NICO ADD 
			
			e.setDigitalObjects(digitalObjects);

			// Log event URI, type, dates, title
			log.debug(String.join("\n - ", "\nEVENT " + e.getUri(), e.getEventType(),
					e.getStartDate(), e.getEndDate(), e.getEventTitle()));
			
			// Log digital object and event notes if present
			//if (objurl != null && objurl.length() > 0) log.debug(" - " + e.getDigitalObject());
			if (notes.length() > 0) log.debug(" - " + e.getEventNotes());
			
			// Load causal and mereological relations attached to event
			if (event.getAsJsonObject("rels") != null) {
				JsonElement causedBy = event.getAsJsonObject("rels").get("causedBy");
				JsonElement partOf = event.getAsJsonObject("rels").get("partOf");
				
				if (causedBy != null)
					e.setCausedBy(new Gson().fromJson(causedBy, new TypeToken<ArrayList<String>>() {}.getType()));
				
				if (partOf != null)
					e.setPartOf(new Gson().fromJson(partOf, new TypeToken<ArrayList<String>>() {}.getType()));
			}

			// Load propositions attached to event
			JsonObject props = (JsonObject) event.get("props");
			Set<Map.Entry<String,JsonElement>> keys = props.entrySet();
			ArrayList<Proposition> propositions = new ArrayList<Proposition>();
			
			// For each proposition...
			for (Entry<String, JsonElement> k: keys) {
				
				Proposition p = new Proposition();
				
				JsonObject value = (JsonObject) k.getValue();
				String propClass = escapeString(value.get("class").getAsString());
				//System.out.println("TIPO: " + propClass);
				
				String resourceID = k.getKey();
				String resourceName = "";
				String resourceDesc = "";
				
				try {
					if (resources.get(k.getKey()) != null) {
						JsonObject resourceObj = (JsonObject) resources.get(k.getKey());
						resourceName = escapeString(resourceObj.get("enName").getAsString());
						if (resourceName.equals("")) {
							resourceName = escapeString(resourceObj.get("itName").getAsString());
						}
						if (resourceName.equals("")) {
							log.info("UNNAMED ENTITY (string): " + k);
						}
					}
					
				} catch (NullPointerException e1) {
					log.info("UNNAMED ENTITY (null): " + value);
				}
				
				try {
					if (resources.get(k.getKey()) != null) {
						JsonObject resourceObj = (JsonObject) resources.get(k.getKey());
						resourceDesc = escapeString(resourceObj.get("enDesc").getAsString());
						if (resourceDesc.equals("")) {
							log.debug("UNDESCRIBED ENTITY (string): " + k);
						}
					}
				} catch (NullPointerException e1) {
					log.debug("UNDESCRIBED ENTITY (null): " + value);
				}
				
				if (value.has("notes")) {
					String propNotes = escapeString(value.get("notes").getAsString());
					p.setNotes(propNotes);
				}
								
				if (wikiResources.get(resourceID) == null) {
					
					if (propClass.equals("person") || propClass.equals("organization")) {
						ActorWithRole actorWithRole = new ActorWithRole(propClass);
						String finalID = null;
						if (value.get("role") == null || value.get("role").isJsonNull()) {
							finalID = resourceID + "/participant";
							actorWithRole.setID(finalID);
						}
						else {
							finalID = resourceID + "/" + escapeString(value.get("role").getAsString());
							actorWithRole.setID(finalID);
						}
						actorWithRole.setURI(Vocabulary.base + "resource/" + finalID);
						actorWithRole.setPersonURI(Vocabulary.base + "resource/" + resourceID);
						actorWithRole.setName(resourceName);
						
						actorWithRole.setDescription(resourceDesc);
						log.debug("\n" + finalID);
						log.debug(actorWithRole.getName());
						wikiResources.put(finalID, actorWithRole);
						log.debug(wikiResources.get(finalID).getURI());
						p.setResourceID(finalID);
					}
	
					else if (propClass.equals("place")) {
						Place place = new Place();
						place.setID(resourceID);
						place.setName(resourceName);
						place.setDescription(resourceDesc);
						place.setURI(Vocabulary.base + "resource/" + k.getKey());
						wikiResources.put(resourceID, place);
						p.setResourceID(resourceID);
					}
					
					else if (propClass.equals("concept")) {
						Concept concept = new Concept();
						concept.setID(resourceID);
						concept.setName(resourceName);
						concept.setDescription(resourceDesc);
						concept.setURI(Vocabulary.base + "resource/" + k.getKey());
						wikiResources.put(resourceID, concept);
						p.setResourceID(resourceID);
					}
					
					else if (propClass.equals("object")) {
						PhysicalObject object = new PhysicalObject();
						object.setID(resourceID);
						object.setName(resourceName);
						object.setDescription(resourceDesc);
						object.setURI(Vocabulary.base + "resource/" + k.getKey());
						wikiResources.put(resourceID, object);
						p.setResourceID(resourceID);
					}
					
					else if (propClass.equals("work")) {
						Work work = new Work();
						work.setID(resourceID);
						work.setTitle(resourceName);
						work.setDescription(resourceDesc);
						//if (resourceID.startsWith("Q"))
						//	work.setURI(Vocabulary.narr + "resource.html?" + k.getKey());
						//else
						work.setURI(Vocabulary.base + "resource/" + k.getKey());
						wikiResources.put(resourceID, work);
						p.setResourceID(resourceID);
					} 
					else if (propClass.equals("other")) {
						Other other = new Other();
						other.setID(resourceID);
						other.setName(resourceName);
						other.setDescription(resourceDesc);
						other.setURI(Vocabulary.base + "resource/" + k.getKey());
						wikiResources.put(resourceID, other);
						p.setResourceID(resourceID);
						p.setResourceID(resourceID);
					}
				}
				
				else {
					p.setResourceID(resourceID);
				}
				
				log.debug(String.join("\n", "", " - " + resourceID, " - " + propClass));
				
				JsonArray primary = new JsonArray();
				JsonArray secondary = new JsonArray();
				
				if (value.has("primary")) {
					primary = value.get("primary").getAsJsonArray();
				}
				if (value.has("secondary")) {
					secondary = value.get("secondary").getAsJsonArray();
				}
				
				ArrayList<Reference> referenceList = new ArrayList<Reference>();
				referenceList.addAll(makeReferenceList(primary, wikiResources, "primary"));
				referenceList.addAll(makeReferenceList(secondary, wikiResources, "secondary"));
								
				p.setReferenceList(referenceList);
				propositions.add(p);	
				
				//System.out.println("aggiunto: " + p.getResourceID());
			}
			
			e.setPropositionList(propositions);
			eventList.add(e);
		}
		log.info("Narrative contains " + eventList.size() + " events");
		
		
		
		// if there is a principal country of a narrative
		
		if(narra.get("country") != null ) {
			 
			narrative.setCountry(narra.get("country").getAsJsonObject());
			JsonObject country= narrative.getCountry();
			
			String idWikiCountry= country.get("id").getAsString().substring(country.get("id").getAsString().lastIndexOf("/") + 1);
			System.out.println(idWikiCountry);
			
			Place place = new Place();
			place.setID(idWikiCountry);
			place.setName(country.get("name").getAsString());
			place.setDescription(country.get("description").getAsString());
			place.setURI(Vocabulary.base + "resource/"+idWikiCountry );
			wikiResources.put(idWikiCountry, place);
			

		}

		//NICO ADD START

		if(narra.get("place") != null ) {
			narrative.setPlace(narra.get("place").getAsString());
		
		}
		//NICO ADD END
		

		//System.out.println(wikiResources);
		
		narrative.setEntities(wikiResources);
		narrative.setEvents(eventList);
		narrative.setFragments(fragList);
		
		
		
		return narrative;
	}
	
	private static ArrayList<Reference> makeReferenceList(JsonArray sources, HashMap<String, Entity> wikiResources, String sourceType) {
				
		ArrayList<Reference> referenceList = new ArrayList<Reference>();
		
		for (int l=0; l < sources.size(); l++) {
			
			JsonObject m = (JsonObject) sources.get(l);
									
		    if (m.get("author") != null || m.get("title") != null) {
		    String authorID = "A" + UUID.randomUUID();
		    	String sourceID = "S" + UUID.randomUUID();
		    //String authorID = "A" + l;
		    	//String sourceID = "S" + l;
		    	String authorName = escapeString(m.get("author").getAsString());
		    	String sourceTitle = escapeString(m.get("title").getAsString());
		    	String textFragment = "";
		    	String referenceFragment = "";
		    	
		    	if (!authorName.equals("") || !sourceTitle.equals("")) {
					
					try {
						textFragment = escapeString(m.get("text").getAsString());
					} catch (NullPointerException exc) {}
					try {
						referenceFragment = escapeString(m.get("reference").getAsString());
					} catch (NullPointerException exc) {}
				
					ActorWithRole author = new ActorWithRole("person");
					Work primarySource = new Work();
				
					if (wikiResources.get(authorID) == null) {
						author.setURI(Vocabulary.base + "resource/" + authorID);
						author.setName(authorName);
						wikiResources.put(authorID, author);
					}
					else author = (ActorWithRole) wikiResources.get(authorID);
					
					if (wikiResources.get(sourceID) == null) {
						primarySource.setURI(Vocabulary.base + "resource/" + sourceID);
						primarySource.setAuthor(author);
						primarySource.setTitle(sourceTitle);
						primarySource.setSourceType(sourceType);
						wikiResources.put(sourceID, primarySource);
					}
					primarySource = (Work) wikiResources.get(sourceID);

					if (primarySource.getAuthor() == null) {
						primarySource.setAuthor(author);
						wikiResources.put(sourceID, primarySource);
					}

					Reference ref = new Reference();
					ref.setSource(primarySource);
					ref.setReferenceFragment(referenceFragment);
					ref.setTextFragment(textFragment);
					referenceList.add(ref);
		    	}
		    }	
		}
		
		return referenceList;
	}
}
