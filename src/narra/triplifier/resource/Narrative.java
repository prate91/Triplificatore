package narra.triplifier.resource;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonObject;

public class Narrative {

	private String id;
	private String name;
	private String author;
	private String place; // NICO ADD 

	
	private JsonObject country;
	
	
	private ArrayList<Event> events;
	private ArrayList<Fragment> fragments;
	
	private HashMap<String, Entity> entities;

	public HashMap<String, Entity> getEntities() {
		return entities;
	}

	public void setEntities(HashMap<String, Entity> wikiResources) {
		this.entities = wikiResources;
	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<Fragment> getFragments() {
		return fragments;
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		this.fragments = fragments;
	}
	

	
	
	public void setCountry(JsonObject country) {
		this.country= country;
	}
	
	public JsonObject getCountry() {
		return country;
	}
	// NICO ADD START
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	// NICO ADD END

	
}