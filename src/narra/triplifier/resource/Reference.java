package narra.triplifier.resource;

public class Reference {

	private String textFragment;
	private String referenceFragment;
	private Work source;

	public void setSource(Work source) {
		this.source = source;
	}

	public Work getSource() {
		return source;
	}

	public String getTextFragment() {
		return textFragment;
	}

	public void setTextFragment(String textFragment) {
		this.textFragment = textFragment;
	}

	public String getReferenceFragment() {
		return referenceFragment;
	}

	public void setReferenceFragment(String referenceFragment) {
		this.referenceFragment = referenceFragment;
	}

}
