

package br.pucrio.inf.lac.CompMovel;

import java.util.ArrayList;

import com.google.gson.*;

/**
 * @author vitor
 *
 */
public class Utils 
{
	private static Utils _utils;
	
	private Utils()
	{
		Initialize();
	}
	
	void Initialize()
	{
		
	}
	
	public static Utils getInstance()
	{
		if(_utils == null)		
			_utils = new Utils();		
		
		return _utils;
	}
	
	
	// {"uuid":"2174ae7c-6c96-43ca-97a4-5d34bb64a61c","source":"00000000-0451-4000-b000-000000000023","action":"read","signal":-29,"sensor_name":"zephyr","sensor_value":[20],"tag":"SensorData","timestamp":1509639752}	
	/**
	 * Retorna o valor de um atributo de uma mensagem no formato json. S'o funciona para atributos simples (String).
	 * @param jsonMsgStr A mensagem string no formato json.
	 * @param attribute O atributo desejado da mensagem.
	 * @return
	 */
	public String getAttributeFromMsg(String jsonMsgStr, String attribute)
	{
		JsonObject jsonMsg; 
		String ret = null;
		JsonParser parser = new JsonParser();
		try {
			jsonMsg = parser.parse(jsonMsgStr).getAsJsonObject(); //new Gson().fromJson(jsonMsgStr, JsonObject.class);
			ret = jsonMsg.get(attribute).toString().replaceAll("\"" , "");
		}
		catch (Exception e){
			//e.printStackTrace();
			System.out.println("Invalid json format");
			return null;
		}
		
		System.out.println("Valid json format");
		return ret;
	}
	
	public ArrayList getAttributeArrayFromMsg(String jsonMsgStr, String attribute)
	{
		Gson googlejson = new Gson();
		JsonObject jsonMsg = googlejson.fromJson(jsonMsgStr, JsonObject.class);
		JsonArray jArray = jsonMsg.get(attribute).getAsJsonArray();
		
		ArrayList jObjects = googlejson.fromJson(jArray, ArrayList.class);
		return jObjects;
	}
}
