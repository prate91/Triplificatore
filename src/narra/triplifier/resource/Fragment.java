package narra.triplifier.resource;

public class Fragment {
	
	private int bookNumber = 0;
	private int chapterNumber = 0;
	//private String secondarySourceURI;
	private String text;
	private String uri;

	public void setBookNumber(int bookNumber){
		this.bookNumber = bookNumber;
	}

	public void setChapterNumber(int chapterNumber){
		this.chapterNumber = chapterNumber;
	}

	public int getBookNumber(){
		return bookNumber;
	}

	public int getChapterNumber(){
		return chapterNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	/*
	public String getSecondarySourceURI() {
		return secondarySourceURI;
	}

	public void setSecondarySourceURI(String secondarySourceURI) {
		this.secondarySourceURI = secondarySourceURI;
	}
	*/
}
