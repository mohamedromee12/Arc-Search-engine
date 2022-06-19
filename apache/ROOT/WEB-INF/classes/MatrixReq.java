import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MatrixReq extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		    throws IOException {
		
		String name = request.getParameter("matrix");
		
		
		
		
		
		response.setContentType("text/html");
        StringBuilder page = new StringBuilder();
        page.append("<!DOCTYPE html>\r\n"
        		+ "<html lang=\"en\">\r\n"
        		+ "<head>\r\n"
        		+ "    <meta charset=\"UTF-8\">\r\n"
        		+ "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
        		+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
        		+ "    <title>Document</title>\r\n"
        		+ "</head>\r\n"
        		+ "<body>\r\n"
        		+ "    <div>"+name+"</div>\r\n"
        		+ "</body>\r\n"
        		+ "</html>");
        
        
        response.getWriter().println(page);
		
	}
}
