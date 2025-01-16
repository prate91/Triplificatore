package narra.triplifier.resource;

/**
 * set data to represent the User e.g name, URI
 */
public class Entity {
	
	 private String URI;
	 private String id;
	 private String name;
	 private String desc;
	 protected String type;
			
		public void setURI(String URI){
			this.URI = URI;
		}
		
		public String getURI(){
			return URI;
		}
		
		public String getType() {
			return type;
		}
		
		public void setID(String id){
			this.id = id;
		}
		
		public String getID(){
			return id;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setDescription(String desc) {
			this.desc = desc;
		}
		
		public String getDescription() {
			return desc;
		}
}
