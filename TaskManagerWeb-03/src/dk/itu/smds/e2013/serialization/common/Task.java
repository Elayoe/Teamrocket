/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.serialization.common;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rao
 * Date: 2013-09-03
 */
    @XmlRootElement(name = "task")
    public class Task implements Serializable{
    	
    	private static final long serialVersionUID = 1L;

        @XmlID
        @XmlAttribute
        public String id;
        
        @XmlAttribute
        public String name;
        
        @XmlAttribute
        public String date;
        
        @XmlAttribute
        public String status;

        @XmlAttribute
        public String required;

        @XmlElement
        public String description;
        
        @XmlElement
        public String attendants;
    	
        public Task(){
        	
        }
    	
    	public Task(String id, String name, String date, String status, String required, String description, String attendants) {
		this.id = id; 
		this.name = name;
		this.date = date;
		this.status = status;
		this.required = required;
		this.description = description;
		this.attendants = attendants;
    	}
        
    }