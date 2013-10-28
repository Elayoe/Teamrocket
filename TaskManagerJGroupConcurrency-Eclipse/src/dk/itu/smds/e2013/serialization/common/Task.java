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
 */
@XmlRootElement(name = "task")
public class Task implements Serializable {

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
    @XmlElement
    public String role;
}