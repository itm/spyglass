package de.uniluebeck.itm.spyglass.packet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Packet with the syntaxtype VariableListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 *
 */
public class VariableListPacket extends SpyglassPacket
{

	public static final int SYNTAXTYPE=SpyglassPacket.ISENSE_SPYGLASS_PACKET_VARIABLE;
	private List<Object> values=new LinkedList<Object>();
	

	/** 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	public void deserialize(byte[] buf) throws SpyglassPacketException
	{
		values.clear();
		super.deserialize(buf);
		if(syntax_type!=VariableListPacket.SYNTAXTYPE) throw new SpyglassPacketException("Wrong Syntaxtype");
		for(int i=SpyglassPacket.EXPECTED_PACKET_SIZE;i<buf.length;i++)
		{
			int type=deserializeUint8(buf[i]);
			i++;
			switch (type)
			{
			case SpyglassPacket.ISENSE_SPYGLASS_PACKET_UINT8:
				int uint8=deserializeUint8(buf[i]);
				values.add(new Integer(uint8));
				break;
			case SpyglassPacket.ISENSE_SPYGLASS_PACKET_UINT16:
				int uint16=deserializeUint16(buf[i],buf[i+1]);
				i++;
				values.add(new Integer(uint16));
				break;
			case SpyglassPacket.ISENSE_SPYGLASS_PACKET_INT16:
				int int16=deserializeInt16(buf[i],buf[i+1]);
				i++;
				values.add(new Integer(int16));
				break;
			case SpyglassPacket.ISENSE_SPYGLASS_PACKET_UINT32:
				long uint32=deserializeUint32(buf[i],buf[i+1],buf[i+2],buf[i+3]);
				i=i+3;
				values.add(new Long(uint32));
				break;
			case SpyglassPacket.ISENSE_SPYGLASS_PACKET_INT64:
				long int64=deserializeInt64(buf[i],buf[i+1],buf[i+2],buf[i+3],buf[i+4],buf[i+5],buf[i+6],buf[i+7]);
				i=i+7;
				values.add(new Long(int64));
				break;
			case SpyglassPacket.ISENSE_SPYGLASS_PACKET_FLOAT:
				float f=deserializeFloat(buf[i],buf[i+1],buf[i+2],buf[i+3]);
				i=i+3;
				values.add(new Float(f));
				break;
			default:
				break;
			}
		}
	}
	
	public Object[] getValues()
	{
		Object[] ret=new Object[values.size()];
		if(ret.length>0) ret=values.toArray(ret);
		return ret;
	}
	
	/** 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#toString()
	 */
	@Override
	public String toString()
	{
		String ret=super.toString()+", values: ";
		Iterator<Object>it=values.iterator();
		for(int i=0;it.hasNext();i++)
		{
			if(i>0) ret=ret+", ";
			ret=ret+it.next().toString();
		}
		return ret;
	}
}

