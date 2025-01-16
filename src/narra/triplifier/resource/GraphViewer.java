package narra.triplifier.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GraphViewer {
	
	HashMap<String, Set<String>> graphView = null;
	
	public GraphViewer(){
		graphView = new HashMap<String, Set<String>>();
	}
	
	public boolean addEdge(String father, String children){
		if(graphView.containsKey(father)){
			Set<String> childrenSet = graphView.get(father);
			childrenSet.add(children);
		}else{
			Set<String> childrenSet = new HashSet<String>();
			graphView.put(father, childrenSet);
			childrenSet.add(children);
		}
		return true;
	}
	
	public Set<String> getChildrenList(String father){
		return graphView.get(father);
	}
	
	
	
}
