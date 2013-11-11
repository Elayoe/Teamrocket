package dk.itu.smds.e2013.rest.dao;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import dk.itu.smds.e2013.serialization.common.TaskManager;

/**
 * Servlet implementation class TaskLoaderServlet
 */
@WebServlet("/TaskLoaderServlet")
public class TaskLoaderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TaskLoaderServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.setContentType("text/plain");
		
		response.getWriter().println(
				"No of tasks: "
						+ TaskManagerDAOEnum.INSTANCE.getTaskManager().tasks
								.size());
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	public void init(ServletConfig config) throws ServletException {

		InputStream stream = config.getServletContext().getResourceAsStream(
				"/WEB-INF/resources/task-manager-xml.xml");

		JAXBContext jaxbContext;
		try {

			jaxbContext = JAXBContext.newInstance(TaskManager.class);

			TaskManager taskManager = (TaskManager) jaxbContext
					.createUnmarshaller().unmarshal(stream);

			TaskManagerDAOEnum.INSTANCE.setTaskManager(taskManager);
			System.out.println(taskManager);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}