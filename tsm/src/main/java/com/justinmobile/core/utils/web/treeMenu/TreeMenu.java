package com.justinmobile.core.utils.web.treeMenu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.justinmobile.core.utils.encode.JsonBinder;


public class TreeMenu implements Serializable {

	private static final long serialVersionUID = -3009349503550646203L;

	private Property property;
	
	private State state;
	
	private String type = Type.folder.name();
	
	private Map<String, String> data = new HashMap<String, String>();
	
	private TreeMenu[] children;
	
	public enum Type {
		folder, file
	}

	public Property getProperty() {
		return property;
	}


	public void setProperty(Property property) {
		this.property = property;
	}


	public State getState() {
		return state;
	}


	public void setState(State state) {
		this.state = state;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Map<String, String> getData() {
		return data;
	}


	public void setData(Map<String, String> data) {
		this.data = data;
	}


	public TreeMenu[] getChildren() {
		return children;
	}


	public void setChildren(TreeMenu[] children) {
		this.children = children;
	}
	
	public void addChild(TreeMenu child) {
		this.setChildren((TreeMenu[])ArrayUtils.add(this.getChildren(), child));
	}


	public static void main(String[] args) {
		TreeMenu tree = new TreeMenu();
		tree.setProperty(new Property("root", false));
		TreeMenu children = new TreeMenu();
		children.setProperty(new Property("node1", true));
		children.setState(new State(true));
		children.getData().put("id", "wowowowwo");
		tree.setChildren(new TreeMenu[]{children});
		System.out.println(JsonBinder.buildNonNullBinder().toJson(tree));
	}

}
