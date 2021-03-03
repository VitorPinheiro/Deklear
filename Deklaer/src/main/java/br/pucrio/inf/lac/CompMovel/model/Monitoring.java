/**
 * 
 */
package br.pucrio.inf.lac.CompMovel.model;

import java.util.ArrayList;

/**
 * @author Vitor
 *
 *	
 *
 */
public class Monitoring 
{
	private Patient _patientToMonitor;
	
	// Essa lista é um ObsRule, pois tem uma lista de SensorObs
	private ArrayList<Event> _events;

	public Patient getPatientToMonitor() {
		return _patientToMonitor;
	}

	public void setPatientToMonitor(Patient patientToMonitor) {
		_patientToMonitor = patientToMonitor;
	}

	public ArrayList<Event> getEvents() {
		return _events;
	}

	public void setEvents(ArrayList<Event> events) {
		_events = events;
	}
	
	
}
