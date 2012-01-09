package com.justinmobile.tsm.endpoint.webservice;

import com.justinmobile.tsm.endpoint.webservice.dto.sp.OperationResultMessage;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.OperationResultResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.PreOperationMessage;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.PreOperationResponse;

public interface ProviderService {

	/**
	 * 预处理，内部调用接口
	 * 
	 * @param operationNotifyMessage
	 * @return 业务平台响应
	 */
	PreOperationResponse preOperation(PreOperationMessage preOperationMessage);

	/**
	 * 结果通知
	 * 
	 * @param buildOperationResultMessage
	 * @return 业务平台响应
	 */
	OperationResultResponse operationResult(OperationResultMessage buildOperationResultMessage);
}
