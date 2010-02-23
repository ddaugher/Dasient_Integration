package com.ecommerce.dasient.jsf;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.config.PrettyUrlMapping;
import com.ocpsoft.pretty.util.UrlPatternParser;
import javax.faces.context.FacesContext;

public abstract class PrettyUrlUtil {

    public static String getMappedUrl(String mappingId, Object... params) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        PrettyContext prettyContext = PrettyContext.getCurrentInstance();

        PrettyUrlMapping mapping = prettyContext.getConfig().getMappingById(mappingId);
        if (mapping == null)
            throw new RuntimeException("PrettyUrl mapping not found: " + mappingId);

        String mappedUrl = new UrlPatternParser(mapping.getPattern()).getMappedUrl(params);
        return facesContext.getExternalContext().getRequestContextPath() + mappedUrl;
    }

}
