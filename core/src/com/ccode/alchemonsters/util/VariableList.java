package com.ccode.alchemonsters.util;

import java.util.HashMap;

public class VariableList {

	private HashMap<String, Object> variables = new HashMap<>();
	
	public HashMap<String, Object> getVariables() {
		return variables;
	}
	
	public boolean setVariable(String name, Object value) {
		return variables.put(name, value) != null;
	}
	
	public Object getVariable(String name) {
		return variables.get(name);
	}
	
	public int getAsInt(String name) {
		return (int) variables.get(name);
	}
	
	public String getAsString(String name) {
		return (String) variables.get(name);
	}
	
	public float getAsFloat(String name) {
		return (float) variables.get(name);
	}
	
	public void clear() {
		variables.clear();
	}
	
}
