package com.justinmobile.tsm.endpoint.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResExecAPDU;

@Transactional
public interface MobileWebServiceManager {

	ResExecAPDU execAPDU(ReqExecAPDU reqExecAPDU);

}
