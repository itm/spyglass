package de.uniluebeck.itm.spyglass.packet;

/**
 * Represents a generic LongListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 *
 */
public class LongListPacket extends SpyglassPacket
{
	protected long[] values=new long[0];
	
	/** 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#toString()
	 */
	@Override
	public String toString()
	{
		String ret=super.toString()+", values: ";
		for(int i=0;i<values.length;i++)
		{
			if(i>0) ret=ret+", ";
			ret=ret+values[i];
		}
		return ret;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the values
	 */
	public long[] getValues()
	{
		return values;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param values the values to set
	 */
	public void setValues(long[] values)
	{
		this.values = values;
	}
}
