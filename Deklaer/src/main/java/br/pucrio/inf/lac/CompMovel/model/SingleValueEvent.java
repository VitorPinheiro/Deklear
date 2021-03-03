/**
 * 
 */
package br.pucrio.inf.lac.CompMovel.model;

/**
 * @author Vitor
 *
 */
public class SingleValueEvent extends Event 
{
	private double _value;
	
	
	public double getValue() {
		return _value;
	}
	public void setValue(double value) {
		_value = value;
	}
	
	public String getValueInStringFormat(String measuredValue)
	{
		return "Valor = "+ measuredValue+".";
	}
	
}
