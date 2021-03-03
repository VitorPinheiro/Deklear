package br.pucrio.inf.lac.CompMovel;

import java.util.ArrayList;

import br.pucrio.inf.lac.CompMovel.logic.ContextDbManager;
import br.pucrio.inf.lac.CompMovel.logic.ObsActDbManager;
import br.pucrio.inf.lac.CompMovel.model.ActRule;
import br.pucrio.inf.lac.CompMovel.model.ActRuleNotification;
import br.pucrio.inf.lac.CompMovel.model.DoubleValueEvent;
import br.pucrio.inf.lac.CompMovel.model.Event;
import br.pucrio.inf.lac.CompMovel.model.Monitoring;
import br.pucrio.inf.lac.CompMovel.model.SingleValueEvent;

/**
 * Hello world!
 *
 */
public class App 
{
	private static IDatabaseManager _databaseManager = ObsActDbManager.getInstance();
	
	public static void testOntologyDatabase()
	{
		System.out.println( "Hello World! "+System.getProperty("sun.arch.data.model") );
        
        
        String patientUUID = "df932351-cc67-4527-9a37-938d560625be";
        
        Monitoring monitoring = _databaseManager.getAllMonitoringInformation(patientUUID);
        
        ArrayList<Event> events = monitoring.getEvents();

        System.out.println("Nome do paciente para monitorar: "+monitoring.getPatientToMonitor().get_name());
        System.out.println("MHubID do paciente para monitorar: "+monitoring.getPatientToMonitor().get_mHubID());
        
        for(int i=0; i<events.size();i++)
        {
        	System.out.println("Evento ObsRuleID = "+events.get(i).getObsRuleID());
        	System.out.println("Evento "+(i+1)+" nome = "+events.get(i).getLogicalCondition());
        	System.out.println("Evento "+(i+1)+" sensor_name = "+events.get(i).getSensorName());
        	
        	if(events.get(i) instanceof SingleValueEvent)
        	{
        		SingleValueEvent sEvent = (SingleValueEvent) events.get(i);
        		System.out.println("sou um SingleValueEvent");
        		System.out.println("Evento "+(i+1)+" value = "+sEvent.getValue());
        		
        		System.out.println("ActRule "+(i+1)+" do "+events.get(i).getObsRuleID()+" ActRuleID = "+sEvent.getActRule().getActRuleID());
        		
        		ActRule actRule = sEvent.getActRule();
        		
        		for(int j=0; j<actRule.getActRuleEffects().size();j++)
        		{ 
        			if(sEvent.getActRule().getActRuleEffects().get(j) instanceof ActRuleNotification)
        			{
        				ActRuleNotification actNot = (ActRuleNotification)sEvent.getActRule().getActRuleEffects().get(j);
        				System.out.println("ActRuleEffect "+(j+1)+" da ActRule "+sEvent.getActRule().getActRuleID()+" MHubIdDestination = "+actNot.getMHubIdDestinations());
        				System.out.println("ActRuleEffect "+(j+1)+" da ActRule "+sEvent.getActRule().getActRuleID()+" Notification = "+actNot.getNotification());
        			}
        			else
        			{
        				System.out.println("Tipo de ActRule nao implementado no codigo.");
        			}
        			
        		}
        		
        	}
        	else if(events.get(i) instanceof DoubleValueEvent)
        	{
        		DoubleValueEvent dEvent = (DoubleValueEvent) events.get(i);
        		System.out.println("sou um DoubleValueEvent");
        		System.out.println("Evento "+(i+1)+" sensor_name = "+dEvent.getValues());        		
        	}
        }
	}
	
	public static void testTimeScaleDB()
	{
		ContextDbManager _db = ContextDbManager.getInstance();        
	}
	
    public static void main( String[] args )
    {
    	//testOntologyDatabase();
    	testTimeScaleDB();
    }
}
