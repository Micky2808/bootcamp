package com.bootcamp.core.content;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.*;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CTAGenerator {

    @Self
    public Resource resource;

    private List<Map<String, String>> ctaLabelList = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (resource != null && resource.hasChildren()) {
            Resource childResource = resource.getChild("imageText");
            if (childResource != null) {
                List<Resource> subChildResourceList = getSubChildResource(childResource);
                Iterator<Resource> subChildListIterator = subChildResourceList.iterator();
                while (subChildListIterator.hasNext()) {
                    Map<String, String> test = new HashMap<>();
                    Resource subChildResource = subChildListIterator.next();
                    ValueMap valueMap = subChildResource.getValueMap();
                    test.put("title", valueMap.get("ctatitle", ""));
                    test.put("link", LinkRewriterUtil.getLink(valueMap.get("ctaredirection", "")));
                    ctaLabelList.add(test);
                }
            }

        }
    }

    private List<Resource> getSubChildResource(Resource resource) {
        List<Resource> subList = new ArrayList<>();
        if (resource.hasChildren()) {
            Iterator<Resource> resourceIterator = resource.listChildren();
            while (resourceIterator.hasNext())
                subList.add(resourceIterator.next());
        }
        return subList;
    }

    public List<Map<String, String>> getCtaLabelList() {
        return ctaLabelList;
    }
}
