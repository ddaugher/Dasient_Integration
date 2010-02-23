package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.jsf.BreadCrumbElement;
import com.ecommerce.dasient.jsf.BreadCrumbModel;
import com.ecommerce.dasient.jsf.PrettyUrlUtil;
import java.util.Arrays;

public class ReadAuthorBreadCrumb {

    private BreadCrumbModel crumbModel;

    public void setCrumbModel(BreadCrumbModel crumbModel) {
        this.crumbModel = crumbModel;
    }

    private ReadAuthorParameters params;

    public void setParams(ReadAuthorParameters params) {
        this.params = params;
    }

    public void createCrumb() {
        crumbModel.setBreadCrumb(Arrays.asList(
            new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("home"),
                "Home"),
            new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("ruleEditor"),
                "Rule Editor"),
            new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("readAuthor", params.getAuthor()),
                String.format("Author %s", params.getAuthor()))
        ));
    }

}
