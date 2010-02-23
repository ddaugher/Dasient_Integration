package com.ecommerce.dasient.jsf;

import java.util.List;

public class BreadCrumbModel {

    private List<BreadCrumbElement> breadCrumb;

    public List<BreadCrumbElement> getBreadCrumb() {
        return breadCrumb;
    }

    public void setBreadCrumb(List<BreadCrumbElement> breadCrumb) {
        this.breadCrumb = breadCrumb;
    }

}
