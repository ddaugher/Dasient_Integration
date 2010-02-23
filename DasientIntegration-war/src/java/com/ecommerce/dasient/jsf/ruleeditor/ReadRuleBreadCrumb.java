package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.jsf.BreadCrumbElement;
import com.ecommerce.dasient.jsf.BreadCrumbModel;
import com.ecommerce.dasient.jsf.PrettyUrlUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadRuleBreadCrumb {

    private BreadCrumbModel crumbModel;

    public void setCrumbModel(BreadCrumbModel crumbModel) {
        this.crumbModel = crumbModel;
    }

    private ReadRuleParameters params;

    public void setParams(ReadRuleParameters params) {
        this.params = params;
    }

    public void createCrumb() {
        List<BreadCrumbElement> breadCrumb = new ArrayList<BreadCrumbElement>(Arrays.asList(
            new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("home"),
                "Home"),
            new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("ruleEditor"),
                "Rule Editor"),
            new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("readRule", params.getRuleId()),
                String.format("Rule #%d", params.getRuleId()))
        ));

        if (params.getRevisionId() != null) {
            breadCrumb.add(new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("readRuleByRevision", params.getRuleId(), params.getRevisionId()),
                String.format("Revision #%d", params.getRevisionId())));
        }

        crumbModel.setBreadCrumb(breadCrumb);
    }

}
