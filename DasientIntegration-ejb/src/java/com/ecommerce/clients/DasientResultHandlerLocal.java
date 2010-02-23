package com.ecommerce.clients;

import com.ecommerce.dasient.model.ScanHistory;
import com.ecommerce.dasient.vo.result.DasientJSONResult;
import javax.ejb.Local;

@Local
public interface DasientResultHandlerLocal {

	public ScanHistory persistScanHistory(DasientJSONResult result, String rawResponse);

}
