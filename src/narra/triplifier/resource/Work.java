package narra.triplifier.resource;

public class Work extends Entity {

	public Work() {
		this.type = "work";
	}
	
	private String title;
	private String type;
	private String sourceType = null;
	private String date;
	private ActorWithRole author = null;

	public void setTitle(String titoloOpera){
		this.title = titoloOpera;
	}

	public void setAuthor(ActorWithRole author) {
		this.author = author;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}
	
	public String getSourceType() {
		return sourceType;
	}

	public String getDate() {
		return date;
	}
	
	public ActorWithRole getAuthor() {
		return author;
	}
}
