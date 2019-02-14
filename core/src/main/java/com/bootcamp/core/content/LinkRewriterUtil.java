package com.bootcamp.core.content;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinkRewriterUtil {

    private LinkRewriterUtil(){}

    public static String getLink(String link){
            if(link!=null && link.startsWith("/content") && !link.endsWith(".html")) {
                    link = link + ".html";
            }
            return link;
    }
}
