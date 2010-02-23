package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.jsf.BreadCrumbElement;
import com.ecommerce.dasient.jsf.BreadCrumbModel;
import com.ecommerce.dasient.jsf.PrettyUrlUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListRulesBreadCrumb {

    private ListRulesParameters params;

    public void setParams(ListRulesParameters params) {
        this.params = params;
    }

    private BreadCrumbModel crumbModel;

    public void setCrumbModel(BreadCrumbModel crumbModel) {
        this.crumbModel = crumbModel;
    }

    public void createCrumb() {
        List<BreadCrumbElement> breadCrumb = new ArrayList<BreadCrumbElement>(Arrays.asList(
            new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("home"),
                "Home"),
            new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("ruleEditor"),
                "Rule Editor")
        ));

        if (params.isShowAll()) {
            breadCrumb.add(new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("listAllRules"),
                "List All Rules"));
        }
        else {
            breadCrumb.add(new BreadCrumbElement(
                PrettyUrlUtil.getMappedUrl("listRules"),
                "List Rules"));
        }

        if (params.getRevisionId() != null) {
            if (params.isShowAll()) {
                breadCrumb.add(new BreadCrumbElement(
                    PrettyUrlUtil.getMappedUrl("listAllRulesByRevision", params.getRevisionId()),
                    String.format("Revision #%d", params.getRevisionId())));
            }
            else {
                breadCrumb.add(new BreadCrumbElement(
                    PrettyUrlUtil.getMappedUrl("listRulesByRevision", params.getRevisionId()),
                    String.format("Revision #%d", params.getRevisionId())));
            }
        }

        crumbModel.setBreadCrumb(breadCrumb);
    }

}
