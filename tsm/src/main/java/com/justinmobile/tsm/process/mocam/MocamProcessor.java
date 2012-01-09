package com.justinmobile.tsm.process.mocam;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Transactional
public interface MocamProcessor {

	MocamResult process(LocalTransaction localTransaction, ReqExecAPDU reqExecAPDU);
}
