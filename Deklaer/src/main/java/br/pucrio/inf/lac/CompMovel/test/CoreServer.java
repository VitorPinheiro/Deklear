package br.pucrio.inf.lac.CompMovel.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.ClientLibProtocol.PayloadSerialization;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory.SupportedDDSVendors;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;

public class CoreServer implements UDIDataReaderListener<ApplicationObject> {
	/** DEBUG */
	private static final String TAG = CoreServer.class.getSimpleName();

	/** SDDL Elements */
    private Object receiveMessageTopic;
    private Object toMobileNodeTopic;
    private static SddlLayer core;
    
    /** Mobile Hubs Data */
    private static final Map<UUID, UUID> mMobileHubs = new HashMap<>();
    
    /** Input reader */
    private static Scanner sc = new Scanner( System.in );
    
	public static void main( String[] args ) {
		new CoreServer();
		
		do {
			UUID nodeDest = null;
			String message = null, result;
			
			// List of keys (UUID of the M-Hubs)
        	List<UUID> nodes = new ArrayList<UUID>( mMobileHubs.keySet() );
        	
			// Destination options to select
        	System.out.println( "\nSelect an option for your message:" );
        	
        	for( int i = 0; i < nodes.size(); ++i ) 
        		System.out.println( i + ": " + nodes.get( i ) );
        	
        	System.out.println( "r: refresh" );
        	System.out.println( "q: quit" );
        	
        	// Input the value
        	do {
        		result = sc.nextLine();
        		if( isNumber( result ) ) {
        			int option = Integer.parseInt( result );
        			// If the number is outside range print an error message.
        			if( option < 0 || option >= nodes.size() )
        				System.out.println( "Input doesn't match specifications. Try again." );
        			else {
        				nodeDest = nodes.get( option );
        				break;
        			}
        		}
            } while( !result.equals( "r" ) && !result.equals( "q" ) );
        	
        	if( result.equals( "r" ) )
        		continue;
        	else if( result.equals( "q" ) )
        		break;
        	
        	// Input the message
        	System.out.println( "\nInsert the message:" );
        	message = sc.nextLine();
        	
        	// Send the message
        	ApplicationMessage appMsg = new ApplicationMessage();
		    appMsg.setPayloadType( PayloadSerialization.JSON );
		    appMsg.setContentObject( "[" + message + "]" );
		    sendUnicastMSG( appMsg, nodeDest );
        	
		    System.out.println( "\nMessage sent! " );
		} while( true );
		
		 if( sc != null )
	        sc.close();
	}
	
	/**
     * Constructor
     */
    private CoreServer() {
    	// Create a layer and participant
        core = UniversalDDSLayerFactory.getInstance( SupportedDDSVendors.OpenSplice );
        core.createParticipant( UniversalDDSLayerFactory.CNET_DOMAIN );
        // Receive and write topics to domain
        core.createPublisher();
        core.createSubscriber();
        // ClientLib Events
        receiveMessageTopic = core.createTopic( Message.class, Message.class.getSimpleName() );
        core.createDataReader( this, receiveMessageTopic );
        // To ClientLib
        toMobileNodeTopic = core.createTopic( PrivateMessage.class, PrivateMessage.class.getSimpleName() );
        core.createDataWriter( toMobileNodeTopic );
    }
    
    /**
     * Sends a message to all the components (BROADCAST)
     * @param appMSG The application message (e.g. a String message)
     */
    public static void sendBroadcastMSG( ApplicationMessage appMSG ) {
		PrivateMessage privateMSG = new PrivateMessage();
		privateMSG.setGatewayId( UniversalDDSLayerFactory.BROADCAST_ID );
		privateMSG.setNodeId( UniversalDDSLayerFactory.BROADCAST_ID );
		privateMSG.setMessage( Serialization.toProtocolMessage( appMSG ) );
		
		sendCoreMSG( privateMSG );
    }
    
    /**
     * Sends a message to a unique component (UNICAST)
     * @param appMSG The application message (e.g. a String message)
     * @param nodeID The UUID of the receiver
     */
    public static void sendUnicastMSG( ApplicationMessage appMSG, UUID nodeID ) {
		PrivateMessage privateMSG = new PrivateMessage();
		privateMSG.setGatewayId( UniversalDDSLayerFactory.BROADCAST_ID );
		privateMSG.setNodeId( nodeID );
		privateMSG.setMessage( Serialization.toProtocolMessage( appMSG ) );
		
		sendCoreMSG( privateMSG );
    }
    
    /**
     * Writes the message (send)
     * @param privateMSG The message
     */
    private static void sendCoreMSG( PrivateMessage privateMSG ) {
        core.writeTopic( PrivateMessage.class.getSimpleName(), privateMSG );
    }
    
    /**
     * Handle different events identified by a label
     * @param label The identifier of the event
     * @param data The data content of the event in JSON
     * @throws ParseException 
     */
    private void handleEvent( final String label, final String data ) throws ParseException {
    	JSONParser parser = new JSONParser();
    	JSONObject object = (JSONObject) parser.parse( data );
    	
    	System.out.println( "\n===========================" );
    	
    	switch( label ) {
    		case "MaxAVG":
    			Double avg = (Double) object.get( "average" );
    			if( avg > 30 )
    				System.out.println( "Feels like hell!" );
    			else if( avg >= 20 && avg <= 30 )
    				System.out.println( "The weather is perfect!" );
    			else
    				System.out.println( "It is freezing here!" );
    		break;
    		
    		default:
    			break;
    	}
    	
    	System.out.println( "===========================\n" );
    }
    
    /**
     * Handle messages (e.g. error or reply)
     * @param object The JSONObject that contains the information
     * @throws ParseException 
     */
    private void handleMessage( final String tag, final JSONObject object ) throws ParseException {
    	final String component = (String) object.get( "component" );
		final String message   = (String) object.get( "message" );
		System.out.println( "\n>>" + tag + "(" + component + "): " + message + "\n" );
    }

	@Override
	public void onNewData( ApplicationObject topicSample ) {
		Message msg = null;
		
		if( topicSample instanceof Message ) {
			msg = (Message) topicSample;
			UUID nodeId = msg.getSenderId();
			UUID gatewayId = msg.getGatewayId();
			
			if( !mMobileHubs.containsKey( nodeId ) ){
				mMobileHubs.put( nodeId, gatewayId );
				System.out.println( ">>" + TAG + ": Client connected" );
			}
			
			String content = new String( msg.getContent() );
			JSONParser parser = new JSONParser();
			
			try {
	        	JSONObject object = (JSONObject) parser.parse( content );
	        	String tag = (String) object.get( "tag" );
	        	
	        	switch( tag ) {
	        		case "SensorData":
	        		break;
	        		
	        		case "EventData":
	        			final String label = (String) object.get( "label" );
	        			final String data  = (String) object.get( "data" );
	        			handleEvent( label, data );
		        	break;
		        	
	        		case "ReplyData":		        	
	        		case "ErrorData":
	        			handleMessage( tag, object );
			        break;
	        	}
			} catch( Exception ex ) {
				System.out.println( ex.getMessage() );
			}
		}
	}
	
	/**
	 * A simple check to see if a string is a valid number 
	 * 
	 * @param s The number to be checked.
	 * @return true  It is a number.
	 *         false It is not a number.
	 */
	public static Boolean isNumber( String s ) {
		try {
            Integer.parseInt( s );
        }
		catch( NumberFormatException e ) {
			return false;			
		}
		return true;
	}
}
