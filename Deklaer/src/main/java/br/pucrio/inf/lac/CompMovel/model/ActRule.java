/**
 * 
 */
package br.pucrio.inf.lac.CompMovel.model;

import java.util.ArrayList;

/**
 * @author Vitor
 *
 */
public class ActRule {
	private String _actRuleID;
	private ArrayList<ActRuleEffect> _actRuleEffects;
	
	public String getActRuleID() {
		return _actRuleID;
	}

	public void setActRuleID(String actRuleID) {
		_actRuleID = actRuleID;
	}
		
	public ArrayList<ActRuleEffect> getActRuleEffects() {
		return _actRuleEffects;
	}

	public void setActRuleEffects(ArrayList<ActRuleEffect> actRuleEffects) {
		_actRuleEffects = actRuleEffects;
	}
}
