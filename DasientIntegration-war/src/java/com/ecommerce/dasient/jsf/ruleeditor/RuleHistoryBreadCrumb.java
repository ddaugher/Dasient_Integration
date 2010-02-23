package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.jsf.BreadCrumbElement;
import com.ecommerce.dasient.jsf.BreadCrumbModel;
import com.ecommerce.dasient.jsf.PrettyUrlUtil;
import java.util.Arrays;

public class RuleHistoryBreadCrumb {

    private BreadCrumbModel crumbModel;

    public void setCrumbModel(BreadCrumbModel crumbModel) {
        this.crumbModel = crumbModel;
    }

    private RuleHistoryParameters params;

    public void setParams(RuleHistoryParameters params) {
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
                PrettyUrlUtil.getMappedUrl("readRule", params.getRuleId()),
                String.format("Rule #%d", params.getRuleId())),
            new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("ruleHistory", params.getRuleId()),
                String.format("History", params.getRuleId()))
        ));
    }

}
