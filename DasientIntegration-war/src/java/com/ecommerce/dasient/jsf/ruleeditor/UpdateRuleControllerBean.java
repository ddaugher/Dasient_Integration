package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.jsf.IdentitySupportBean;
import com.ecommerce.sbs.RuleSetLocal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.el.ELContext;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.StringUtils;

public class UpdateRuleControllerBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private IdentitySupportBean identityBean;

    public void setIdentityBean(IdentitySupportBean identityBean) {
        this.identityBean = identityBean;
    }

    private UpdateRuleModelBean modelBean;

    public void setModelBean(UpdateRuleModelBean modelBean) {
        this.modelBean = modelBean;
    }

    private void filterEmptyPatterns(List<String> patterns) {
        Iterator<String> it = patterns.iterator();
        while (it.hasNext()) {
            String pattern = it.next();

            if (StringUtils.isEmpty(pattern)) {
                it.remove();
            }
        }
    }

    public String updateRule() {
        List<String> patterns = new LinkedList<String>(Arrays.asList(
            modelBean.getPattern1(),
            modelBean.getPattern2(),
            modelBean.getPattern3(),
            modelBean.getPattern4(),
            modelBean.getPattern5(),
            modelBean.getPattern6()
        ));

        filterEmptyPatterns(patterns);

        ruleSetBean.updateRule(
            modelBean.getRuleId(),
            modelBean.getRevisionId(),
            identityBean.getUsername(),
            modelBean.getRevisionComment(),
            modelBean.getDescription(),
            modelBean.getAction(),
            patterns
        );

        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        ReadRuleParameters readRuleParams = (ReadRuleParameters)
                FacesContext.getCurrentInstance().getApplication()
                .getELResolver().getValue(elContext, null, "readRuleParameters");

        readRuleParams.setRuleId(modelBean.getRuleId());

        return "pretty:readRule";
    }

}
