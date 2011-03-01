package fr.digitbooks.guestbook;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddRate extends HttpServlet {
    private static final Logger log = Logger.getLogger(AddRate.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
    	resp.setContentType("text/plain");

        String content = req.getParameter("comment");
        String rate = req.getParameter("rating");
        Date date = new Date();
        Greeting greeting = new Greeting(Float.valueOf(rate), content, date);

        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(greeting);
        } finally {
            pm.close();
        }

        resp.getWriter().print("true");
    }
}
