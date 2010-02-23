package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.jsf.IdentitySupportBean;
import com.ecommerce.sbs.RuleSetLocal;
import javax.el.ELContext;
import javax.faces.context.FacesContext;

public class ResurrectRuleControllerBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private IdentitySupportBean identityBean;

    public void setIdentityBean(IdentitySupportBean identityBean) {
        this.identityBean = identityBean;
    }

    private ResurrectRuleModelBean modelBean;

    public void setModelBean(ResurrectRuleModelBean modelBean) {
        this.modelBean = modelBean;
    }

    public String resurrectRule() {
        ruleSetBean.resurrectRule(
            modelBean.getRuleId(),
            modelBean.getRevisionId(),
            identityBean.getUsername(),
            modelBean.getRevisionComment()
        );

        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        ReadRuleParameters readRuleParams = (ReadRuleParameters)
                FacesContext.getCurrentInstance().getApplication()
                .getELResolver().getValue(elContext, null, "readRuleParameters");

        readRuleParams.setRuleId(modelBean.getRuleId());

        return "pretty:readRule";
    }

}
