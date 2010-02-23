package com.ecommerce.dasient.jsf;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

public class LogoutControllerBean {

    public String logout() throws NamingException, IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        if (session != null)
            session.invalidate();

        String casLogoutUrl = (String) new InitialContext().lookup("java:comp/env/cas/casServerLogoutUrl");

        FacesContext.getCurrentInstance().getExternalContext().redirect(casLogoutUrl);

        return null;
    }

}
