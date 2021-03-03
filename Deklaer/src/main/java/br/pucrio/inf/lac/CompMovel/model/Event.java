/**
 * 
 */
package br.pucrio.inf.lac.CompMovel.model;

import java.util.ArrayList;

import br.pucrio.inf.lac.CompMovel.model.ObsActVocabulary.LogicalConditions;

/**
 * @author Vitor
 *
 *	Um ObsRule (Cada ObsRule só tem um SensorObs)
 *
 */
public class Event 
{
	private String _sensorName;	
	private String _typeOfData;                                                       // Ex: batimento cardiaco, pressao arterial ...
	private String _unityOfMeasurement;
	private ActRule _actRule;	
	private String _obsRuleID;
	private LogicalConditions _logicalCondition;

	public String getUnityOfMeasurement() {
		return _unityOfMeasurement;
	}

	public void setUnityOfMeasurement(String unityOfMeasurement) {
		_unityOfMeasurement = unityOfMeasurement;
	}

	public String getTypeOfData() {
		return _typeOfData;
	}

	public void setTypeOfData(String typeOfData) {
		this._typeOfData = typeOfData;
	}
	
	public ActRule getActRule() {
		return _actRule;
	}

	public void setActRule(ActRule actRule) {
		_actRule = actRule;
	}

	public void setLogicalConditionStr(String logicalCondition)
	{
		if(logicalCondition.equalsIgnoreCase(LogicalConditions.EqualTo.toString()))
			_logicalCondition = LogicalConditions.EqualTo;
		else if(logicalCondition.equalsIgnoreCase(LogicalConditions.GreaterOrEqualThan.toString()))
			_logicalCondition = LogicalConditions.GreaterOrEqualThan;
		else if(logicalCondition.equalsIgnoreCase(LogicalConditions.GreaterThan.toString()))
			_logicalCondition = LogicalConditions.GreaterThan;
		else if(logicalCondition.equalsIgnoreCase(LogicalConditions.LessOrEqualThan.toString()))
			_logicalCondition = LogicalConditions.LessOrEqualThan;
		else if(logicalCondition.equalsIgnoreCase(LogicalConditions.LessThan.toString()))
			_logicalCondition = LogicalConditions.LessThan;
	}
	
	public void setLogicalCondition(LogicalConditions logicalCondition)
	{
		_logicalCondition = logicalCondition; 
	}
	
	public LogicalConditions getLogicalCondition()
	{
		return _logicalCondition;
	}
	
	public String getObsRuleID() {
		return _obsRuleID;
	}
	
	public void setObsRuleID(String obsRuleID) {
		_obsRuleID = obsRuleID;
	}

	public String getSensorName() {
		return _sensorName;
	}
	public void setSensorName(String sensorName) {
		_sensorName = sensorName;
	}
}
