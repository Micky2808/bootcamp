package com.bootcamp.core.content;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

@SlingServlet(resourceTypes = "demo/components/content/blog-listing",
        methods = HttpConstants.METHOD_GET,
        extensions = "json",
        selectors = "blogthumbs")
public class BlogListingServlet extends SlingSafeMethodsServlet {
    private String offset = "";
    private static final int INITIAL_DISPLAY_THUMBS = 3;

    @Reference
    private transient QueryBuilder queryBuilder;

    private static final Logger LOG = LoggerFactory.getLogger(BlogListingServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        Resource resource = request.getResource();
        ValueMap valueMap = resource.getValueMap();
        String blogRootPath = valueMap.get("blogrootpath", "");
        List<Map<String, Object>> blogs = new ArrayList<>(1);
        if ("".equals(blogRootPath)) {
            LOG.warn("Blog Root Path Not configured");
        } else {
            String[] selectors = request.getRequestPathInfo().getSelectors();
            if (selectors.length >= 1) {
                offset = selectors[1];
                LOG.debug("offset value is {}", offset);
            }
            LOG.debug("*************** rootpath is {}", blogRootPath);
            Map<String, String> predicateMap = new HashMap<>();
            predicateMap.put("path", blogRootPath);
            predicateMap.put("type", NameConstants.NT_PAGE);
            predicateMap.put("p.offset", offset);
            predicateMap.put("p.limit", INITIAL_DISPLAY_THUMBS + "");
            predicateMap.put("property.operation", "exists");
            predicateMap.put("property", "jcr:content/blogDate");
            predicateMap.put("orderby", "jcr:content/@blogDate");
            predicateMap.put("orderby.sort", "desc");

            ResourceResolver resourceResolver = resource.getResourceResolver();
            Query search = queryBuilder.createQuery(PredicateGroup.create(predicateMap),
                    resourceResolver.adaptTo(Session.class));

            SearchResult result = search.getResult();
            Iterator<Resource> resourceIterator = result.getResources();
            int count = 0;
            while (resourceIterator.hasNext()) {
                if (count++ <= INITIAL_DISPLAY_THUMBS) {
                    LOG.info("count is {}", count);
                    Resource pageRes = resourceIterator.next();
                    Page blogPage = pageRes.adaptTo(Page.class);
                    Map<String, Object> blogData = new HashMap<>();
                    if (blogPage != null) {
                        blogData.put("title", blogPage.getTitle());
                        blogData.put("description", blogPage.getDescription());
                        blogData.put("path", LinkRewriterUtil.getLink(blogPage.getPath()));
                        blogData.put("image", getBlogImage(blogPage));
                    }
                    blogs.add(blogData);
                } else {
                    return;
                }
            }

        }
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(blogs));
    }

    private String getBlogImage(Page blogPage) {

        return blogPage.getProperties().get("thumbimage") != null ? blogPage.getProperties().get("thumbimage") + "" : "";

    }


}

