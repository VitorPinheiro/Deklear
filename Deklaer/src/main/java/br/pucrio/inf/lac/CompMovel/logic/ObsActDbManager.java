/**
 * 
 */
package br.pucrio.inf.lac.CompMovel.logic;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import br.pucrio.inf.lac.CompMovel.IDatabaseManager;
import br.pucrio.inf.lac.CompMovel.model.ActRule;
import br.pucrio.inf.lac.CompMovel.model.ActRuleEffect;
import br.pucrio.inf.lac.CompMovel.model.ActRuleNotification;
import br.pucrio.inf.lac.CompMovel.model.DoubleValueEvent;
import br.pucrio.inf.lac.CompMovel.model.Event;
import br.pucrio.inf.lac.CompMovel.model.Monitoring;
import br.pucrio.inf.lac.CompMovel.model.ObsActVocabulary;
import br.pucrio.inf.lac.CompMovel.model.ObsActVocabulary.NumberOfValues;
import br.pucrio.inf.lac.CompMovel.model.Patient;
import br.pucrio.inf.lac.CompMovel.model.SingleValueEvent;

/**
 * @author Vitor
 *
 */
public class ObsActDbManager implements IDatabaseManager {
	private OWLOntologyManager _manager;
	private String _currentLoadedPatient = null;
	
	private String _appDirectory = getApplicationPath(); 
	private String _primaryRepositoryDirectory = _appDirectory + "\\patientsOntologies\\";
	
	private IRI[] _ontologiesURIs;
	private IRI[] _ontologiesLocalPaths;
	
	private static ObsActDbManager _db = null;
	
	// Esse grupo de variaveis pode-se ler de um XML ou txt
	// onde o engenheiro de ontologias vai dizer para o sistema qual
	// grupo de ontologias ele vai usar.
	private int _TOTAL_ONTOLOGIES = 1;
	private int ObsActOntologyNumber 	    = 0;	
	private String[] owlFilesPaths = new String[_TOTAL_ONTOLOGIES];
	
	
	public static ObsActDbManager getInstance()
	{
		if(_db == null)
		{
			_db = new ObsActDbManager();
		}
		
		return _db;
	}
	
	/**
	 * Singleton
	 */
	 private ObsActDbManager() 
	 {
		 _ontologiesURIs       = new IRI[_TOTAL_ONTOLOGIES];
		 _ontologiesLocalPaths = new IRI[_TOTAL_ONTOLOGIES];
		 
		owlFilesPaths[ObsActOntologyNumber]    = "obsact.owl";
		
		registerPatients();
		
		// An OWLOntologyManager for handling ontologies
  	  	_manager = OWLManager.createOWLOntologyManager();
	 }
	 
	 /**
	  * Colocar aqui todos os pacientes que vao existir.
	  */
	 private void registerPatients()
	 {
		 registerNewPatient("df932351-cc67-4527-9a37-938d560625be","C:\\Users\\Vitor\\git\\idosocare\\MHubJava\\ontologies\\");
	 }
	   
	   /**
	    * Constroi o caminho onde está salvo as ontologias de um determinado paciente.
	    * @param patientMHub
	    * @return
	    */
	   private String makePatientOntologiesRepositoryPath(String patientMHub)
	   {
		   return _primaryRepositoryDirectory + patientMHub + "\\";
	   }
	   
	   /**
	    * ontologiesPath - Supondo que a ontologiesPath vai vir formatada bonitinha com os "//" todos e com
	    * um "//" no final.
	    */
	   public Boolean registerNewPatient(String patientMHubID, String ontologiesPath)
	   {
		   if(patientMHubID == null || ontologiesPath == null)
		   {// nao foi passado o telefone ou o caminho das ontologias.
			   return false;
		   }
		   
		   // Verifico se já foi criado o repositorio principal.
			verifyPrimaryRepositoryDirectory();
			 
			
			// Diretorio onde a app vai salvar as ontologias.
			String saveDirectory = makePatientOntologiesRepositoryPath(patientMHubID);
			
			if (verifyDirectory(saveDirectory))
			{
				// Diretorio já existia antes, nao foi criado e nao vai ser alterado.
				return false;
			}
		   
		   
		    int i=0;
			File[] files = new File[owlFilesPaths.length];
			
			// Path de onde deve ser copiado as ontologias.
			for(i=0; i < owlFilesPaths.length ; i++)
			{
				String path = ontologiesPath+owlFilesPaths[i];
				
				if(path.contains("\\"))
				{
					path = path.replace("\\", "/");
				}
				files[i] = new File(path);
				
			}
			
			try 
		       {
		       	OWLOntology ontology;
		       	SimpleIRIMapper map;
		       	// C:\Users\Vitor\git\idosocare\MHubJava\ontologies
		       	for(int j=0;j<files.length;j++)
		       	{		       		
		       		// Load ontology from given user path
		       		ontology = _manager.loadOntologyFromOntologyDocument(files[j]);   
		       		
		           	
		           	// map the ontology URI to the physical location that the app will persist it.
		       		_ontologiesURIs[j] = ontology.getOntologyID().getOntologyIRI();
		            File file = new File(saveDirectory+owlFilesPaths[j]);		            
		            map = new SimpleIRIMapper(_ontologiesURIs[j], IRI.create(file.getPath()));		           	
		           	_manager.addIRIMapper(map);	

		           	// saves the ontology
		            _manager.saveOntology(ontology, IRI.create(file));
		       	}

				} 
		       catch (OWLOntologyCreationException e) 
		       {
					System.out.println("Error when loading the ontologies from the file with the OWL API.");
					e.printStackTrace();			
				} 
		       catch (OWLOntologyStorageException e) 
		       {
		    	   System.out.println("Error when saving the ontologies with the OWL API.");
				e.printStackTrace();
			} 
		       
		       printOntologiesInManager();
		       
		       _currentLoadedPatient = patientMHubID;
		       return true;
	   }
	   
	   /**
	    * Imprime as URIs das ontologias que estao no ontology manager e as classes delas.
	    */
	   private void printOntologiesInManager()
	   {
		   Iterator<OWLOntology> itr = _manager.getOntologies().iterator();
	       
	       System.out.println("Ontologies in manager:");
	       while( itr.hasNext() )
	       {
	       	OWLOntology ontology = itr.next();
	       	
	       	System.out.println("Ontology URI: "+ ontology.getOntologyID().getOntologyIRI());
	       	System.out.println("Loaded ontology: " + ontology);
	       	System.out.println("Classes:");
	           printOntologiesClasses(ontology);
	           
	        System.out.println("");   
	       }
	   }
	   
	   /**
	    * Load all the 2 ontologies of a patient in the ontology manager.
	    * E apaga o manager antigo.
	    * 
	    * @param patientMHubID
	    */
	   public Boolean loadPatientOntologies(String patientMHubID)
	   {
		   // Diretorio onde a app vai salvar as ontologias.
			String patientDirectory = makePatientOntologiesRepositoryPath(patientMHubID);
			
			_manager = OWLManager.createOWLOntologyManager(); 
			for(int i=0;i<owlFilesPaths.length;i++)
			{
				File file = new File(patientDirectory+owlFilesPaths[i]);
				
				try 
				{
					OWLOntology ontology = _manager.loadOntologyFromOntologyDocument(file);
					
					_ontologiesURIs[i] = ontology.getOntologyID().getOntologyIRI();
				} 
				catch (OWLOntologyCreationException e) 
				{
					System.out.println("Error while loading ontology from path: "+file.getPath());
					e.printStackTrace();
					return false;
				}
			}

			_currentLoadedPatient = patientMHubID;
			return true;
	   }
	   	
	   
	   public Monitoring getAllMonitoringInformation(String patientMHubID)
	   {
		   if (_currentLoadedPatient == null || !_currentLoadedPatient.equalsIgnoreCase(patientMHubID))
			{// carrego as ontologias do paciente se elas nao tiverem sido carregadas.
				loadPatientOntologies(patientMHubID);
			}
		   
		   OWLDataFactory factory = _manager.getOWLDataFactory();		   
		   OWLOntology obsActOntology = _manager.getOntology(_ontologiesURIs[ObsActOntologyNumber]);
		   
		   // Crio um reasoner.
		   OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		   OWLReasoner reasoner = reasonerFactory.createReasoner(obsActOntology);		   
		   
		   OWLClass EntityClass = factory.getOWLClass(IRI.create(ObsActVocabulary.entity_ClassURI));
		   OWLObjectProperty has_sensorObjProperty = factory.getOWLObjectProperty
					(IRI.create(ObsActVocabulary.has_sensor_OPropURI));
		   OWLObjectProperty has_obsRuleObjProperty = factory.getOWLObjectProperty
					(IRI.create(ObsActVocabulary.has_obsRule_OPropURI));
		   
		   OWLObjectProperty has_sensorObsObjProperty = factory.getOWLObjectProperty
					(IRI.create(ObsActVocabulary.has_sensorObs_OPropURI));		   
		   
		   OWLDataProperty hasIdDataProperty = factory.getOWLDataProperty
					(IRI.create(ObsActVocabulary.has_id_DPropURI));		   
		   OWLDataProperty hasNameProperty = factory.getOWLDataProperty
					(IRI.create(ObsActVocabulary.has_name_DPropURI));
		   OWLDataProperty hasSensorNameProperty = factory.getOWLDataProperty
					(IRI.create(ObsActVocabulary.has_sensorName_DPropURI));
		   
		   OWLDataProperty hasNumberOfValuesDataProperty = factory.getOWLDataProperty
					(IRI.create(ObsActVocabulary.has_numberOfValues_DPropURI));
		   
		   Monitoring monitoring = new Monitoring();
		   Patient patient = new Patient();
		   
		   
		   Set<OWLIndividual> entityClassSet = EntityClass.getIndividuals(obsActOntology);
		   OWLNamedIndividual correctEntityInd = null;
		   for(OWLIndividual entityInd : entityClassSet)
		   {			   
			   // ver se esse é o paciente certo, se for é pq estou no monitoring certo.
			   String valueMhubID = entityInd.getDataPropertyValues(hasIdDataProperty, obsActOntology).iterator().next().getLiteral();
			   String patientName = entityInd.getDataPropertyValues(hasNameProperty, obsActOntology).iterator().next().getLiteral();
			   
			   if(valueMhubID.equalsIgnoreCase(patientMHubID))
			   { // achei meu paciente				   
				   patient.set_name(patientName);
				   patient.set_mHubID(valueMhubID);
				   
				   monitoring.setPatientToMonitor(patient);
				   
				   correctEntityInd = entityInd.asOWLNamedIndividual();
			   }
		   }
		   
		   if(correctEntityInd == null)
		   {
			   System.out.println("Nao encontrei um entity instancia para o paciente "+patientMHubID);
			   return null;
		   }
		   
		   Set<OWLNamedIndividual> obsRulesInd = reasoner
	       			.getObjectPropertyValues(correctEntityInd.asOWLNamedIndividual(), has_obsRuleObjProperty).getFlattened();
		   

		   ArrayList<Event> events = new ArrayList<Event>();
		   for(OWLIndividual obsRuleInd : obsRulesInd)
		   {
			   String obsRuleID = obsRuleInd.getDataPropertyValues(hasIdDataProperty, obsActOntology).iterator().next().getLiteral();
			   
			   Set<OWLNamedIndividual> sensorObsInds = reasoner
		       			.getObjectPropertyValues(obsRuleInd.asOWLNamedIndividual(), has_sensorObsObjProperty).getFlattened();
			   
			   if(sensorObsInds.size() != 1)
			   {
				   System.out.println("Erro na construcao da ontologia. Cada ObsAct só pode ter um SensorObs.");
				   return null;
			   }
			   
			   
			   OWLDataProperty hasValuesOneRestrictionDataProperty = factory.getOWLDataProperty
						(IRI.create(ObsActVocabulary.has_valueOneRestriction_DPropURI));
			   OWLDataProperty hasValuesTwoRestrictionDataProperty = factory.getOWLDataProperty
						(IRI.create(ObsActVocabulary.has_valueTwoRestriction_DPropURI));			   
			   OWLDataProperty hasLogicalConditionDataProperty = factory.getOWLDataProperty
						(IRI.create(ObsActVocabulary.has_logicalCondition_DPropURI));	
			   OWLDataProperty isAboutDataProperty = factory.getOWLDataProperty
						(IRI.create(ObsActVocabulary.isAbout_DPropURI));	
			   OWLDataProperty hasUnityOfMeasurementDataProperty = factory.getOWLDataProperty
						(IRI.create(ObsActVocabulary.has_unityOfMeasurement_DPropURI));	
			   
			   OWLIndividual sensorObsInd = sensorObsInds.iterator().next();
			   
			   OWLNamedIndividual sensorInd = reasoner
		       			.getObjectPropertyValues(sensorObsInd.asOWLNamedIndividual(), has_sensorObjProperty).getFlattened().iterator().next();
			   
			   String sensorName = sensorInd.getDataPropertyValues(hasSensorNameProperty, obsActOntology).iterator().next().getLiteral();				   
			   String numberOfValues = sensorObsInd.getDataPropertyValues(hasNumberOfValuesDataProperty, obsActOntology).iterator().next().getLiteral();
			   String valueOneRestriction = sensorObsInd.getDataPropertyValues(hasValuesOneRestrictionDataProperty, obsActOntology).iterator().next().getLiteral();
			   String logicalCondition = sensorObsInd.getDataPropertyValues(hasLogicalConditionDataProperty, obsActOntology).iterator().next().getLiteral();				   
			   
			   String isAbout = sensorObsInd.getDataPropertyValues(isAboutDataProperty, obsActOntology).iterator().next().getLiteral();
			   String unityOfMeasurement = sensorObsInd.getDataPropertyValues(hasUnityOfMeasurementDataProperty, obsActOntology).iterator().next().getLiteral();
			   
			   // obsRuleID
			   
			   if(numberOfValues.trim().equalsIgnoreCase(NumberOfValues.One.toString()))
			   { // é um single event
				   SingleValueEvent sEvent = new SingleValueEvent();
				   
				   sEvent.setSensorName(sensorName);
				   valueOneRestriction = valueOneRestriction.trim();
				   sEvent.setValue(Double.parseDouble(valueOneRestriction));
				   sEvent.setLogicalConditionStr(logicalCondition);
				   sEvent.setObsRuleID(obsRuleID);
				   sEvent.setActRule(getAllActRuleFromObsRule(obsRuleInd));
				   sEvent.setTypeOfData(isAbout);
				   sEvent.setUnityOfMeasurement(unityOfMeasurement);
				   
				   events.add(sEvent);
			   }
			   else if(numberOfValues.trim().equalsIgnoreCase(NumberOfValues.Two.toString()))
			   { // é um double event
				   DoubleValueEvent dEvent = new DoubleValueEvent();
				   
				   String valueTwoRestriction = sensorObsInd.getDataPropertyValues(hasValuesTwoRestrictionDataProperty, obsActOntology).iterator().next().getLiteral();
				   valueTwoRestriction = valueTwoRestriction.trim();
				   
				   dEvent.setSensorName(sensorName);
				   dEvent.setValues(Double.parseDouble(valueOneRestriction), Double.parseDouble(valueTwoRestriction));
				   dEvent.setLogicalConditionStr(logicalCondition);
				   dEvent.setObsRuleID(obsRuleID);
				   dEvent.setActRule(getAllActRuleFromObsRule(obsRuleInd));
				   dEvent.setTypeOfData(isAbout);
				   dEvent.setUnityOfMeasurement(unityOfMeasurement);				   
				   
				   events.add(dEvent);
			   }
			   
			   
		   }
		   		
		   monitoring.setEvents(events);
		   
		   return monitoring;
	   }
	   
	   private ActRule getAllActRuleFromObsRule(OWLIndividual obsRuleInd)
	   {
		   OWLDataFactory factory = _manager.getOWLDataFactory();		   
		   OWLOntology obsActOntology = _manager.getOntology(_ontologiesURIs[ObsActOntologyNumber]);
		   
		   // Crio um reasoner.
		   OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		   OWLReasoner reasoner = reasonerFactory.createReasoner(obsActOntology);
		   
		   OWLObjectProperty has_actRuleObjProperty = factory.getOWLObjectProperty
					(IRI.create(ObsActVocabulary.has_actRule_OPropURI));
		   OWLObjectProperty has_actuationEffectObjProperty = factory.getOWLObjectProperty
					(IRI.create(ObsActVocabulary.has_actuationEffect_OPropURI));
		   
		   OWLDataProperty hasIdDataProperty = factory.getOWLDataProperty
					(IRI.create(ObsActVocabulary.has_id_DPropURI));	
		   OWLDataProperty hasMhubIdDestinationDataProperty = factory.getOWLDataProperty
					(IRI.create(ObsActVocabulary.has_mhubIdDestination_DPropURI));
		   OWLDataProperty hasNotificationTextDataProperty = factory.getOWLDataProperty
					(IRI.create(ObsActVocabulary.has_notificationText_DPropURI));			   
		   
		   Set<OWLNamedIndividual> actRuleInds = reasoner
	       			.getObjectPropertyValues(obsRuleInd.asOWLNamedIndividual(), has_actRuleObjProperty).getFlattened();	
		   
		   if(actRuleInds.size() != 1)
		   {
			   System.out.println("Erro na construcao da ontologia. Cada ObsAct só pode ter um ActRule.");
			   return null;
		   }
		   
		   ActRule actRule = new ActRule();
		   ArrayList<ActRuleEffect> actRuleEffects = new ArrayList<ActRuleEffect>();
		   OWLIndividual actRuleInd = actRuleInds.iterator().next();
		   		   
		   // actRules
		   String actRuleID = actRuleInd.getDataPropertyValues(hasIdDataProperty, obsActOntology).iterator().next().getLiteral();			   
		   actRule.setActRuleID(actRuleID);
		   
		   Set<OWLNamedIndividual> acctuationEffectInds = reasoner
	       			.getObjectPropertyValues(actRuleInd.asOWLNamedIndividual(), has_actuationEffectObjProperty).getFlattened();
		   
		   for(OWLIndividual acctuationEffectInd : acctuationEffectInds)
		   {
			   NodeSet<OWLNamedIndividual> notificationIndivuduals = getAllInstancesOfClass(obsActOntology, ObsActVocabulary.notification_ClassURI);
			   if(notificationIndivuduals.containsEntity(acctuationEffectInd.asOWLNamedIndividual()))
			   { // é um notification
				   ActRuleNotification actRuleEffect = new ActRuleNotification();
				   
				   String mHubIdDestination = acctuationEffectInd.getDataPropertyValues(hasMhubIdDestinationDataProperty, obsActOntology).iterator().next().getLiteral();
				   String notificationText = acctuationEffectInd.getDataPropertyValues(hasNotificationTextDataProperty, obsActOntology).iterator().next().getLiteral();
				   actRuleEffect.addMHubIdDestination(mHubIdDestination);
				   actRuleEffect.setNotification(notificationText);
				   
				   actRuleEffects.add(actRuleEffect);
			   }					   
		   }		   
			   
		   actRule.setActRuleEffects(actRuleEffects);
		   
		   return actRule;
	   }
	   
	   /**
		 * Pega todas as instancias de uma determinada classe (classURI).
		 * Pega somente as instancias associadas ao grupo de ontologias do paciente (patientPhone).
		 * 
		 * @param patientPhone
		 * @param classURI
		 * @return
		 */
		private NodeSet<OWLNamedIndividual> getAllInstancesOfClass(OWLOntology ontology, String classURI)
		{
			OWLDataFactory factory = _manager.getOWLDataFactory();
			
			// Pego a classe.
			OWLClass classObj = factory.getOWLClass(IRI.create(classURI));
			System.out.println("Instances for class: "+classObj.getIRI());	
			
			// Crio um reasoner.
			OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
			
			// Imprimo todos os individuos diretos da classe.
			NodeSet<OWLNamedIndividual> indivuduals = reasoner.getInstances(classObj, true);		
			
			return indivuduals;
		}   
	
	
	/**
	 * Print all classes than an ontology use.
	 * For debug.
	 * @param ontology
	 */
	private static void printOntologiesClasses(OWLOntology ontology)
	{
		for (OWLClass cls : ontology.getClassesInSignature()) {
           System.out.println("	"+cls);
       }
	}
	
	//---------------------------------------------------------------------//
	
	
   
	/**
	 * Verifica se o diretorio principal onde estarao os pacientes foi criado.
	 */
   private void verifyPrimaryRepositoryDirectory()
   {
	   File file = new File(_primaryRepositoryDirectory);
	   if(!file.exists())
		   file.mkdir(); 	   
   }
   
   /**
    * Retorna falso se nao existe o diretorio criado. E assim ele criou.
    * Retorna true se o diretorio já existe e tem algum arquivo dentro.
    * 
    * @param directory
    * @return
    */
   private Boolean verifyDirectory(String directory)
   {
	   File file = new File(directory);
	   
	   if(!file.exists())
	   {
		   file.mkdir();
	   }
	   
	   File[] files = file.listFiles();	   
	   if(files.length == 0)
	   {
		   return false;
	   }
	   
	   return true;
   }
   
   /**
    * Codifica a url para que ela possa representar um nome de pasta no SO.
    * @param url
    * @return String
    */
   private String encodeUrl(String url)
   {
	   url = url.replace("/", "-");
	   url = url.replace(":", "_");
	   
	   return url;
   }
   
   /**
    * Decodifica um nome de pasta para a url correspondente.
    * @param url
    * @return String
    */
   private String decodeUrl(String url)
   {
	   url = url.replace("-", "/");
	   url = url.replace("_", ":");
	   
	   return url;
   }
   
   /** 
    * Retorna o caminho onde a aplicação está sendo executada 
    * @return caminho da aplicação 
    */  
   private String getApplicationPath() {  
      String url = getClass().getResource(getClass().getSimpleName() + ".class").getPath();  
        File dir = new File(url).getParentFile();  
        String path = null;  
          
        if (dir.getPath().contains(".jar"))  
            path = findJarParentPath(dir);  
        else  
            path = dir.getPath();  
  
        try {  
            return URLDecoder.decode(path, "UTF-8");  
        }  
        catch (UnsupportedEncodingException e) {                  
            return path.replace("%20", " ");  
        }  
   }
   
   /** 
    *  retorna o caminho quando a aplicao est dentro de um  
    *  arquivo .jar 
    * @param jarFile 
    * @return 
    */  
   private String findJarParentPath(File jarFile) {  
        while (jarFile.getPath().contains(".jar"))  
            jarFile = jarFile.getParentFile();  
          
        return jarFile.getPath().substring(6);  
    }
	
	
	//---------------------------------------------------------------------//
}
