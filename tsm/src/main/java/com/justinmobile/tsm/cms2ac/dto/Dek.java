package com.justinmobile.tsm.cms2ac.dto;

import com.justinmobile.tsm.cms2ac.security.scp02.SecureAlgorithm;

public class Dek extends Key {

	public Dek() {
		algorithm = SecureAlgorithm.TRIPLE_DES_ECB;
	}
}
