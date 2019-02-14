package com.bootcamp.core.content;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;
import java.util.*;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BlogModel {
    private static final Logger LOG = LoggerFactory.getLogger(BlogModel.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @OSGiService
    private QueryBuilder queryBuilder;

    private List<Map<String, String>> blogs = new ArrayList<>();

    public List<Map<String, String>> getBlogs() {
        return blogs;
    }


    private boolean resultLen;

    private long totalResults;

    @PostConstruct
    private void init() {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put("path", currentPage.getPath());
        predicateMap.put("type", NameConstants.NT_PAGE);
        predicateMap.put("p.limit", "-1");
        predicateMap.put("property.operation", "exists");
        predicateMap.put("property", "jcr:content/blogDate");
        predicateMap.put("orderby", "jcr:content/@blogDate");
        predicateMap.put("orderby.sort", "desc");
        Query search = queryBuilder.createQuery(PredicateGroup.create(predicateMap),
                resourceResolver.adaptTo(Session.class));
        SearchResult result = search.getResult();
        totalResults = result.getTotalMatches();
        if (totalResults >= 3) {
            resultLen = true;
        }
        Iterator<Resource> resourceIterator = result.getResources();
        int count = 0;

        while (resourceIterator.hasNext()) {
            if (count++ < 3) {
                Resource pageRes = resourceIterator.next();
                Page blogPage = pageRes.adaptTo(Page.class);
                Map<String, String> blogData = new HashMap<>();
                if (blogPage != null) {
                    blogData.put("title", blogPage.getTitle());
                    blogData.put("description", blogPage.getDescription());
                    blogData.put("path", LinkRewriterUtil.getLink(blogPage.getPath()));
                    blogData.put("image", getBlogImage(blogPage));
                }
                blogs.add(blogData);
            }else{
                break;
            }
        }

    }

    private String getBlogImage(Page blogPage) {

        return blogPage.getProperties().get("thumbimage") != null ? blogPage.getProperties().get("thumbimage") + "" : "";

    }

    public boolean getResultLen() {
        return resultLen;
    }

    public Long getTotalResults() {
        return this.totalResults;
    }

}