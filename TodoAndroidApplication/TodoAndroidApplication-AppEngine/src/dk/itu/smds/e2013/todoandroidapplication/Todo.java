package dk.itu.smds.e2013.todoandroidapplication;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity

public class Todo {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;

	
	public Key getKey() {
		return key;
	}

	
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String summary;
	
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}


	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
