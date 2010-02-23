package com.ecommerce.sbs;

import com.ecommerce.dasient.exceptions.UnableToProcessDasientResultException;
import javax.ejb.Local;

@Local
public interface DasientResultBrokerLocal {

    void submitResult(String requestId, String jsonString, boolean isInfected) throws UnableToProcessDasientResultException;

}
