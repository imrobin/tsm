package com.justinmobile.tsm.process.mocam;

import java.util.List;

import com.google.common.collect.Lists;

public class MocamResult {
	
	private String currentAid;
	
	private String progress;

	private String progressPercent;
	
	private ApduName apduName;

	private List<String> apdus = Lists.newArrayList();
	
	public enum ApduName {
		Ext_Auth, Select, Init_Update, Install_For_Load, Load,
		Install_For_Install, Install_For_Personalization, Install_For_Extradition,
		Store_Data, Get_Status, Get_Data, Delete, Set_Status, Put_Key, Perso_Cmd, Complete;
	}
	
	public static MocamResult getLastResult(String aid) {
		MocamResult result = new MocamResult();
		result.setApduName(ApduName.Complete);
		result.setProgress("流程完成");
		result.setProgressPercent("100");
		result.setCurrentAid(aid);
		return result;
	}

	public String getCurrentAid() {
		return currentAid;
	}

	public void setCurrentAid(String currentAid) {
		this.currentAid = currentAid;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getProgressPercent() {
		return progressPercent;
	}

	public void setProgressPercent(String progressPercent) {
		this.progressPercent = progressPercent;
	}

	public ApduName getApduName() {
		return apduName;
	}

	public void setApduName(ApduName apduName) {
		this.apduName = apduName;
	}

	public List<String> getApdus() {
		return apdus;
	}

	public void setApdus(List<String> apdus) {
		this.apdus = apdus;
	}
}
