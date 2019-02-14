package com.bootcamp.core.content;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;
import java.util.*;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ThumbNailDisplay {
    @OSGiService
    private QueryBuilder queryBuilder;
    private boolean hidden = false;
    @Inject
    private Page currentPage;
    @Self
    private SlingHttpServletRequest request;
    private long totalMatches;

    public long getTotalMatches() {
        return totalMatches;
    }

    private List<Resource> resourceList = new ArrayList<>();

    @PostConstruct
    private void init() {
        String rootPath = WCMUtils.getInheritedProperty(currentPage, request.getResourceResolver(), "blogHome");
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put("path", rootPath);
        predicateMap.put("type", "cq:Page");
        predicateMap.put("p.limit", "-1");
        predicateMap.put("property", "jcr:content/blogDate");
        predicateMap.put("property.operation", "exists");
        predicateMap.put("orderby", "@jcr:created");
        predicateMap.put("orderby.sort", "desc");
        Query search = queryBuilder.createQuery(PredicateGroup.create(predicateMap),
                request.getResourceResolver().adaptTo(Session.class));
        SearchResult result = search.getResult();
        long totalMatches = result.getTotalMatches();
        if (totalMatches != 0) {
            hidden = true;
            Iterator<Resource> resourceIterator = result.getResources();
            while (resourceIterator.hasNext()) {
                Resource pageRes = resourceIterator.next();
                Page page = pageRes.adaptTo(Page.class);
                page.getTitle();
                page.getDescription();
                page.getPath();
                resourceList.add(pageRes);
            }
        }
    }

    public boolean getHidden() {
        return this.hidden;
    }

    public String getResourceList() {
        return resourceList.toString();
    }
}