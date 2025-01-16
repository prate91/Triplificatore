package narra.triplifier.resource;

import java.util.ArrayList;

public class Proposition {
	
	private String resourceID = null;
	private String notes = null;
	private Fragment secondarySourceFragment = null;
	private ArrayList<Reference> referenceList = new ArrayList<Reference>();

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	
	public void setSecondarySourceFragment(Fragment f) {
		this.secondarySourceFragment = f;
	}

	public Fragment getSecondarySourceFragment() {
		return secondarySourceFragment;
	}

	public ArrayList<Reference> getReferenceList() {
		return referenceList;
	}

	public void setReferenceList(ArrayList<Reference> referenceList) {
		this.referenceList = referenceList;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}
