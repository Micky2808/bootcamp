package com.bootcamp.core.content;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TagIdentifier {

    @Inject
    private Page currentPage;

    private Map<String, String> tagMap = new HashMap<>();

    @PostConstruct
    private void init() {

        if (currentPage != null) {
            Tag[] tags = currentPage.getTags();
            if (tags.length == 0) {
                //TODO: Add appropriate log message here
            } else {
                for (Tag tag : tags) {
                    tagMap.put(tag.getTagID(), tag.getTitle());
                }
            }
        }

    }

    public Map<String, String> getTagMap() {
        return tagMap;
    }
}
