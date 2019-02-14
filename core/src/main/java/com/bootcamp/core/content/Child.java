package com.bootcamp.core.content;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Child {


    @Inject
    @Named("ctatitle")
    private String title;


    @Inject
    @Named("ctaredirection")
    private String redirectionLink;

    @Inject
    Resource resource;

    public String getTitle() {
        return title;
    }

    public String getRedirectionLink() {
        return LinkRewriterUtil.getLink(redirectionLink);
    }
}
