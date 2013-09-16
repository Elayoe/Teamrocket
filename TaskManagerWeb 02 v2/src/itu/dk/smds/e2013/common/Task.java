package itu.dk.smds.e2013.common;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Serilization class for tasks
 *
 */
@XmlRootElement(name = "task")
public class Task implements Serializable {
	
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
    public Boolean required;
        
    // If you dont specify any annotation, it will be serialized as XmlElement.
        
    public String description;
    public String attendants;
}
