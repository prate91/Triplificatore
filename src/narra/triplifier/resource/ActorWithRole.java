package narra.triplifier.resource;

/**
 * set data to represent the User e.g name, URI
 */
public class ActorWithRole extends Entity {

	private String role;
	private String personURI;
	
	public ActorWithRole(String type) {
		this.type = type;
		this.role = "participant";
	}

	public void setRole(String role){
		this.role = role;
	}

	public void setPersonURI(String personURI) {
		this.personURI = personURI;
	}

	public String getRole(){
		return role;
	}

	public String getPersonURI() {
		return personURI;
	}
}
