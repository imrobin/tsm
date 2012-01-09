function info(reviewDate, opinion) {
	var message = '处理时间：'+reviewDate+'<br/>处理意见：'+opinion+'<br/>请修改后重新审核';
	new LightFace.MessageBox().info(message);
}