/**
 * 
 */
package br.pucrio.inf.lac.CompMovel.model;

/**
 * @author Vitor
 *
 */
public class DoubleValueEvent extends Event 
{
	private Double[] _values;
	
	public DoubleValueEvent()
	{
		_values = new Double[2];
	}

	public Double[] getValues() {
		return _values;
	}

	public void setValues(Double value1, Double value2) {
		_values[0] = value1;
		_values[1] = value2;
	}
	
	public String getValueInStringFormat(String measuredValue1, String measuredValue2)
	{
		return "Valor = "+ measuredValue1+" / "+measuredValue2+".";
	}
	
}
