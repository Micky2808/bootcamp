package com.bootcamp.core.content;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.Resource;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ShowMe {

    @Self
    private Resource resource;

    private String display;

    @Inject
    private Page currentPage;


    public String getDisplay() {
        return this.resource.toString() + " " + this.currentPage.toString();
    }

}
