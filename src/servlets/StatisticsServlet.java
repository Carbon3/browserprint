package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAOs.StatisticsDAO;

/**
 * Servlet implementation class ChartsServlet
 */
public class StatisticsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatisticsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String chart = request.getParameter("chart");
		if(chart == null){
			return;
		}
		
		if(chart.equals("usingTor")){
			out.print(StatisticsDAO.getPercentageTorUsers());
		}
		else if(chart.equals("OS")){
			out.print(StatisticsDAO.getOSBreakdown());
		}
		else if(chart.equals("browser")){
			out.print(StatisticsDAO.getBrowserBreakdown());
		}
		else if(chart.equals("timezone")){
			out.print(StatisticsDAO.getTimezones());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
