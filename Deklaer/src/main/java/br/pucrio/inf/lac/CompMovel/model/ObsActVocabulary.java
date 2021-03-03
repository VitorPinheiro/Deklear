/**
 * 
 */
package br.pucrio.inf.lac.CompMovel.model;

/**
 * @author Vitor
 *
 */
public class ObsActVocabulary {
	public static final String obsact_uri = "http://obsact/vocabulary#";
	
	// ----- Classes ----- 
	public static final String actRule_ClassURI = obsact_uri+"ActRule";
	public static final String actuationEffect_ClassURI = obsact_uri+"ActuationEffect";
	public static final String actuator_ClassURI = obsact_uri+"Actuator";
	public static final String notification_ClassURI = obsact_uri+"Notification";
	public static final String entity_ClassURI = obsact_uri+"Entity";
	public static final String logicalCondition_ClassURI = obsact_uri+"LogicalCondition";
	public static final String lessOrEqualThan_ClassURI = obsact_uri+"LessOrEqualThan";
	public static final String lessThan_ClassURI = obsact_uri+"LessThan";
	public static final String equalTo_ClassURI = obsact_uri+"EqualTo";
	public static final String greaterOrEqualThan_ClassURI = obsact_uri+"GreaterOrEqualThan";
	public static final String greaterThan_ClassURI = obsact_uri+"GreaterThan";
	public static final String obsActRule_ClassURI = obsact_uri+"ObsActRule";
	public static final String obsRule_ClassURI = obsact_uri+"ObsRule";
	public static final String sensor_ClassURI = obsact_uri+"Sensor";
	public static final String sensorObs_ClassURI = obsact_uri+"SensorObs";	
	
	// ----- Object Properties ----- 
	public static final String has_actuationEffect_OPropURI = obsact_uri+"has_actuationEffect";
	public static final String has_sensorObs_OPropURI = obsact_uri+"has_sensorObs";	
	public static final String has_actRule_OPropURI = obsact_uri+"has_actRule";
	public static final String has_actuator_OPropURI = obsact_uri+"has_actuator";
	public static final String has_obsActRule_OPropURI = obsact_uri+"has_obsActRule";
	public static final String has_obsRule_OPropURI = obsact_uri+"has_obsRule";
	public static final String has_sensor_OPropURI = obsact_uri+"has_sensor";
	
	// ----- Data Properties -----
	public static final String has_notificationText_DPropURI = obsact_uri+"has_notificationText";
	public static final String has_mhubIdDestination_DPropURI = obsact_uri+"has_mhubIdDestination";
	public static final String has_actuatorName_DPropURI = obsact_uri+"has_actuatorName";
	public static final String has_id_DPropURI = obsact_uri+"has_id";
	public static final String has_name_DPropURI = obsact_uri+"has_name";
	public static final String has_numberOfValues_DPropURI = obsact_uri+"has_numberOfValues";
	public static final String has_sensorName_DPropURI = obsact_uri+"has_sensorName";
	public static final String has_valueOneRestriction_DPropURI = obsact_uri+"has_valueOneRestriction";
	public static final String has_valueTwoRestriction_DPropURI = obsact_uri+"has_valueTwoRestriction";
	public static final String has_logicalCondition_DPropURI = obsact_uri+"has_logicalCondition";
	public static final String has_unityOfMeasurement_DPropURI = obsact_uri+"has_unityOfMeasurement";
	public static final String isAbout_DPropURI = obsact_uri+"isAbout";
	
	// possible values por the Data property: has_logicalCondition_DPropURI
	public static enum LogicalConditions
	{
		GreaterOrEqualThan, GreaterThan, EqualTo, LessThan, LessOrEqualThan;
		
		private String mathSignal;
		
		static {
			GreaterOrEqualThan.mathSignal = ">=";
			GreaterThan.mathSignal = ">";
			EqualTo.mathSignal = "==";
			LessThan.mathSignal = "<";
			LessOrEqualThan.mathSignal = "<=";
	    }
		
		public String getMathSignal()
		{			
			return mathSignal;
		}
	}	
	
	// possible values for the Data Property: has_numberOfValues_DPropURI
	public static enum NumberOfValues
	{
		One ("1"), Two ("2");
		
		private final String name;       

	    private NumberOfValues(String s) {
	        name = s;
	    }
	    
	    public boolean equalsName(String otherName) {
	        // (otherName == null) check is not needed because name.equals(null) returns false 
	        return name.equals(otherName);
	    }

	    public String toString() {
	       return this.name;
	    }
	}
	
}
