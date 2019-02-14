package com.bootcamp.core.content;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;

import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CtaUsingChildResource {

    @Self
    public Resource resource;

    @ChildResource
    public List<Child> imageText;

    public List<Child> getChildList() {
        return imageText;
    }


    public Resource getResource() {
        return resource;
    }

    public List<Child> getImageText() {
        return imageText;
    }
}
