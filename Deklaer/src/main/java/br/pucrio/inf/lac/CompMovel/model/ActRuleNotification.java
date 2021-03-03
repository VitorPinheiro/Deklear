/**
 * 
 */
package br.pucrio.inf.lac.CompMovel.model;

import java.util.ArrayList;

/**
 * @author Vitor
 *
 */
public class ActRuleNotification extends ActRuleEffect {
	private String _notification;
	private ArrayList<String> _mHubIdDestination;
	
	public String getNotification() {
		return _notification;
	}
	public void setNotification(String notification) {
		_notification = notification;
	}
	public ArrayList<String> getMHubIdDestinations() {
		return _mHubIdDestination;
	}
	public void setMHubIdDestinations(ArrayList<String> mHubIdDestination) {
		_mHubIdDestination = mHubIdDestination;
	}
	public void addMHubIdDestination(String mHubIdDestination)
	{
		if(_mHubIdDestination == null)
			_mHubIdDestination = new ArrayList<String>();
		
		_mHubIdDestination.add(mHubIdDestination);
	}
}
