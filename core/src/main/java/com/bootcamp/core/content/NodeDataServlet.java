package com.bootcamp.core.content;

import com.google.gson.Gson;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SlingServlet(paths = "/customer/node", methods = {HttpConstants.METHOD_GET}, extensions = "json")
public class NodeDataServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(NodeDataServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        CustomerService customerService = new CustomerServiceImpl();
        customerService.insertCustData();


        List<Resource> resourceList = new ArrayList();
        List<Customer> customerList = null;
        try {
            LOG.info("Invoked get method ");
            Resource resource = request.getResourceResolver().getResource("/content/customer-data");
            if (resource != null && resource.hasChildren()) {
                LOG.info("Got the parent resource from the given path {}", resource.toString());
                Iterator<Resource> yearIterator = resource.listChildren();
                while (yearIterator.hasNext()) {
                    Resource year = yearIterator.next();
                    LOG.info("year received is {}", year.toString());
                    Iterator<Resource> monthIterator = year.listChildren();
                    while (monthIterator.hasNext()) {
                        Resource month = monthIterator.next();
                        LOG.info("Month received is {}", month.toString());
                        if (month.hasChildren()) {
                            Iterator<Resource> dateIterator = month.listChildren();
                            while (dateIterator.hasNext()) {
                                Resource date = dateIterator.next();
                                LOG.info("Date received is {}", date.toString());
                                if (date.hasChildren()) {
                                    Iterator<Resource> customerNodes = date.listChildren();
                                    while (customerNodes.hasNext()) {
                                        resourceList.add(customerNodes.next());
                                    }
                                }
                            }
                        }
                    }
                }
                customerList = convertResourceToCustomer(resourceList);
            } else {
                LOG.error("Could not find the root node specified in the path");
            }
        } catch (Exception e) {
            LOG.error("Some exception occurred {}", e.getMessage());
        }
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(customerList));
    }

    private List<Customer> convertResourceToCustomer(List<Resource> resourceList) {
        LOG.info("Entered into convert resource to customer method");
        List<Customer> customerList = new ArrayList<>();
        for (Resource resource : resourceList) {
            ValueMap valueMap = resource.getValueMap();
            Customer customer = new Customer();
            customer.setCustomerId(Integer.parseInt(valueMap.get("customer_ID").toString()));
            customer.setName(valueMap.get("customerName", ""));
            customer.setState(valueMap.get("customer_State", ""));
            customer.setZipCode(Integer.parseInt(valueMap.get("zip_Code", "")));
            customer.setShippingAddress(valueMap.get("customer_Address", ""));
            customer.setEmail(valueMap.get("customer_Email", ""));
            customerList.add(customer);
        }
        return customerList;
    }
}
