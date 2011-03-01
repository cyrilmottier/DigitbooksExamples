package fr.digitbooks.guestbook;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

public class RateList extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
              throws IOException {
    	
    	
    	//req.toString()
    	
    	int count = 10;
    	String format = "json";
    	String countString = req.getParameter("count");
    	String formatString = req.getParameter("format");
		
		if(countString != null){
			count = Integer.parseInt(countString);
		}
		if(formatString != null){
			format = formatString;
		}
		
        resp.setContentType("text/plain;charset=UTF-8");
        PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Query query = pm.newQuery(Greeting.class);
        query.setOrdering("date desc");
        query.setRange(0, count);
        
        if(format.equalsIgnoreCase("json")){
        	String response = "{}";
	        JSONObject result = new JSONObject();
	        try {
	            List<Greeting> results = (List<Greeting>) query.execute();
	            
	            JSONArray datas = new JSONArray();
	            
	            
	            if (results.iterator().hasNext()) {
	                for (Greeting e : results) {
	                	JSONObject data  = new JSONObject();
	                	try {
							data.accumulate("rating", e.getRate());
							data.accumulate("content", e.getContent());
							data.accumulate("date", e.getDate().getTime());
							result.append("datas", data);
						} catch (JSONException e1) {
							// TODO 
						}
						
	                }
	                response = result.toString();
	            }
	            
	        } finally {
	            query.closeAll();
	        }
	        resp.getWriter().print(response);
        }
        else if(format.equalsIgnoreCase("xml"))
        {
        	String response = "<datas></datas>";
        	List<Greeting> results = (List<Greeting>) query.execute();
        	if (results.iterator().hasNext()) {
        		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
	        	    Document doc = db.newDocument();
	
	        	    Element datas = doc.createElement("datas");
	        	      
	        		//StringBuffer buffer = new StringBuffer("<datas>");
	                for (Greeting e : results) {
	                	//buffer.append("<data rating=\"" + e.getRate() + "\" comment=\"" + e.getContent() + "\" date=\"" + e.getDate().getTime() + "\"/>");
	                	Element data = doc.createElement("data");
	                	data.setAttribute("rating", String.valueOf(e.getRate()));
	                	data.setAttribute("comment", e.getContent());
	                	data.setAttribute("date", String.valueOf(e.getDate().getTime()));
	                	datas.appendChild(data);
	                }
	                doc.appendChild(datas);
	             // Output the XML
	                TransformerFactory tf = TransformerFactory.newInstance();
	                Transformer transformer = tf.newTransformer();
	                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	                StringWriter sw = new StringWriter();
	                StreamResult sr = new StreamResult(sw);
	                DOMSource source = new DOMSource(doc);
	                transformer.transform(source, sr);
	                String xmlString = sw.toString();
	                //buffer.append("</datas>");
	                response = xmlString;

				} catch (ParserConfigurationException e1) {
					
				} catch (TransformerException e) {
					
				}
        	}
        	 resp.getWriter().print(response);       
        }
        else
        {
        	resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "bad request : format parameter don't support value " + formatString);
        }
        //resp.getWriter().print(req.toString());
    }
}

