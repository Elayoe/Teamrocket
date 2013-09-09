<%@page import="itu.dk.smds.e2013.servlets.GetAllTasksServlet"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="org.jdom2.JDOMException"%>
<%@page import="org.jdom2.output.XMLOutputter"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="itu.dk.smds.e2013.common.TasksJDOMParser"%>
<%@page import="org.jdom2.Document"%>
<%@page import="java.io.InputStream"%>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Insert title here</title>

<style type="text/css">

body{
	background-color: #6fc378;
	color: #FFFFFF;
}

</style>
</head>
<body>

	<form method="POST">
		<input type="text" name="query" placeholder="search"><br/>
		<input type="radio" name="searchBy" value="id" checked> ID<br/>
		<input type="radio" name="searchBy" value="attendants"> Owner<br/>
		<input type="radio" name="searchBy" value="date"> Date<br/>
		<input type="submit" value="Search!"> <br/>
	</form>

	<%

            try {

				String q = request.getParameter("query");
				String search = request.getParameter("searchBy");
				System.out.println(request.getParameter("searchBy"));
            	System.out.println(request.getParameter("query"));
            	
                InputStream xmlStream = getServletContext().getResourceAsStream("/WEB-INF/task-manager-xml.xml");

                String query = "";
                
                if(search == null){
                	search = "id";
                }
                
                if(search.equals("attendants")){
                	System.out.println("hello");
                	//query = "//task/attendants[contains(text(),'" + q + "')]/description";
                	query = "//task[attendants= '" + q +"']/description";
                } else {
                 	query = "//task[@" + search + "='" + q + "']";
                }
                
               

                Document tasksDoc = TasksJDOMParser.GetTasksByQuery(xmlStream, query);


                new XMLOutputter().output(tasksDoc, out);



            } catch (JDOMException ex) {
                Logger.getLogger(GetAllTasksServlet.class.getName()).log(Level.SEVERE, null, ex);
            }


        %>
	
</body>
</html>