/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package itu.dk.smds.e2013.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 *
 * @author rao
 */
public class TasksJDOMParser {

        public static Document GetTasksByQuery(InputStream stream, String query) throws JDOMException, IOException {



        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(stream);
        } catch (IOException ex) {
            Logger.getLogger(TasksJDOMParser.class.getName()).log(Level.SEVERE, null, ex);

            throw ex;
        }


        XPathFactory xpfac = XPathFactory.instance();

        XPathExpression<Element> xp = xpfac.compile(query, new ElementFilter());
        
        

        List<Element> tasks = (List<Element>) xp.evaluate(doc);

        
        Document xmlDoc = new Document();

        Element root = new Element("tasks");

        for (int index = 0; index < tasks.size(); index++) {

            root.addContent(tasks.get(index).clone());

        }





        xmlDoc.addContent(root);


        return xmlDoc;
    }
}
