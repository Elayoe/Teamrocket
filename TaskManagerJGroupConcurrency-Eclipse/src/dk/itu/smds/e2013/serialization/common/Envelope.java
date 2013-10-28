/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.serialization.common;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rao
 */
@XmlRootElement(name = "envelope")
public class Envelope {

    public String command;
    public String lock;

    @XmlElementWrapper(name = "data")
    @XmlElement(name = "task")
    public List<Task> data = new ArrayList<>();

    public String taskId;

    public String initiator;

   

}
