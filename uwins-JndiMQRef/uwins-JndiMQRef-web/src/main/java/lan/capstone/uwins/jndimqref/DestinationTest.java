/*
 * Copyright 2018 Mike Neilson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lan.capstone.uwins.jndimqref;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Mike Neilson
 */
@WebServlet(name = "DestinationTest", urlPatterns = {"/DestinationTest"})
public class DestinationTest extends HttpServlet {

    @Resource( lookup = "uwinsl:/jms/TEST/QUEUE")
    javax.jms.Destination dest;
    
    @Resource( lookup ="uwinsl:/jms/NOTAQUEUE")
    javax.jms.Destination dest2;
    
    @Resource( lookup ="uwinsl:/jms/data/rc")
    javax.jms.Destination dest3;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            javax.jms.Destination d3 = (javax.jms.Destination)InitialContext.doLookup("uwinsl:/jms/TEST/QUEUE");
            
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DestinationTest</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DestinationTest at " + request.getContextPath() + "</h1>");
            out.println("<h1>Destination physical name is " + dest.toString() + "</h1>");
            out.println("<h1>Is of Type " + dest.getClass().getName() + "</h1>");
            out.println("<h1>Dest2 " + dest2 + "</h1>");
            out.println("<h1>" + System.getProperty(Context.URL_PKG_PREFIXES) + "</h1>");
            out.println("<h1>" + d3 + "</h1>");
            out.println("<h1>" + dest3 + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } catch (NamingException ex) {
            Logger.getLogger(DestinationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
