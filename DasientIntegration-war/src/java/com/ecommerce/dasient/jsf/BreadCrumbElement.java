package com.ecommerce.dasient.jsf;

public class BreadCrumbElement {

    public BreadCrumbElement(String url, String label) {
        this.url = url;
        this.label = label;
    }

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
