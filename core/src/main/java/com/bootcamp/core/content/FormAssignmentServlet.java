package com.bootcamp.core.content;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.xml.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SlingServlet(paths = "/customer/formdata", methods = {HttpConstants.METHOD_POST}, extensions = "json")
public class FormAssignmentServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(FormAssignmentServlet.class);

    @Reference
    private transient ResourceResolverFactory resolverFactory;

    private static final String BASE_PATH = "/content/customer-data";

    private StringBuilder creationPath = null;

    private String type = null;

    @Reference
    private transient MessageGatewayService messageGatewayService;

    private transient Map<String, String> completeFormData = null;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        creationPath = new StringBuilder(BASE_PATH);
        type = "sling:Folder";
        completeFormData = new HashMap<>(1);

        /* received data from the form in the xml data form */
        String data = request.getParameter("dataXml");
        /* converting the xml data to the json format */
        JSONObject jsonObject = null;
        try {
            jsonObject = XML.toJSONObject(data.trim());
            Object afData = jsonObject.get("afData");
            Object afUnboundData = ((JSONObject) afData).get("afUnboundData");
            JSONObject data1 = (JSONObject) ((JSONObject) afUnboundData).get("data");
            Iterator<String> keys = data1.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                completeFormData.put(key, data1.get(key).toString());
            }
        } catch (JSONException e) {
            LOG.info("Log exception message {}", e.getMessage());
        }

        /* logic to get the year, month and the date ex : 2019 JAN 01  */
        LocalDate now = LocalDate.now();
        // TO-DO get the customer ID
        String[] date = {String.valueOf(now.getYear()), now.getMonth().name().substring(0, 3).toLowerCase(), String.valueOf(now.getDayOfMonth()), completeFormData.get("customer_ID")};
        int count = 0;
        while (count < 4) {
            checkResource(request, date[count++], count);
            if (count == 3) {
                type = JcrConstants.NT_UNSTRUCTURED;
            }
        }

        try {
            MessageGateway<Email> messageGateway;

            Email email = new SimpleEmail();

            String emailToRecipients = completeFormData.get("customer_Email");

            email.addTo(emailToRecipients);
            email.setSubject("Confirmation Email for form submission");
            email.setFrom("MaheshRajendramr@gmail.com");
            email.setMsg("We have received your details and we will get back to you shortly");

            //Inject a MessageGateway Service and send the message
            messageGateway = messageGatewayService.getGateway(Email.class);

            // Check the logs to see that messageGateway is not null
            messageGateway.send((Email) email);

        } catch (Exception e) {
            LOG.error("Exception occured {}", e.getMessage());
        }

    }

    private void checkResource(SlingHttpServletRequest request, String folderName, int count) {
        Resource resource = request.getResourceResolver().getResource(creationPath + "");
        if (resource != null) {
            int noOfNodesExisting = 0;
            if (count == 4) {
                LOG.info("THe resourcer is {}", resource);
                noOfNodesExisting = countNodes(resource);
                LOG.info("No of nodes present is {}", noOfNodesExisting);
            }
            Resource child = resource.getChild(folderName);
            if (child == null) {
                if (noOfNodesExisting <= 100)
                    creationPath.append("/" + folderName);
                else {
                    createFolder(creationPath.append("-" + noOfNodesExisting / 100) + "", "sling:Folder");
                    creationPath.append("/" + folderName);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LOG.error("Thread Interrupted {}", e.getMessage());
                    }
                }
                createFolder(creationPath + "", type);
                LOG.info("Folder created {}", creationPath);
            } else {
                creationPath.append("/" + folderName);
                if(JcrConstants.NT_UNSTRUCTURED.equals(child.getResourceType())){
                    createFolder(creationPath+"",type);
                }
            }
        } else {
            LOG.error("resource is null and not present {}", creationPath);
        }
    }

    private int countNodes(Resource resource) {
        int noOfNodesExisting = 0;
        if (resource != null && resource.hasChildren()) {
            Iterator<Resource> resourceIterator = resource.listChildren();
            while (resourceIterator.hasNext()) {
                noOfNodesExisting++;
                resourceIterator.next();
            }
        }
        return noOfNodesExisting;
    }

    private synchronized void createFolder(String path, String type) {
        ResourceResolver resolver = resolverFactory.getThreadResourceResolver();
        if (resolver != null) {
            Session session = resolver.adaptTo(Session.class);
            if(session!=null) {
                try {
                    Node node = JcrUtil.createPath(path, type, session);
                    if (JcrConstants.NT_UNSTRUCTURED.equals(type) && node != null) {
                        Set<Map.Entry<String, String>> fields = completeFormData.entrySet();
                        for (Map.Entry<String, String> field : fields) {
                            node.setProperty(field.getKey(), field.getValue());
                        }
                    }
                    session.save();
                    resolver.commit();
                } catch (Exception e) {
                    LOG.error("Could not create the node {} and the exception is {}", path, e);
                }
            }
        }
    }
}
