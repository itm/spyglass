package de.uniluebeck.itm.spyglass.packet;

public class Time
{
	long sec_;
	int ms_;

	public String toString()
	{
		return "" + sec_ + "." + ms_;
	}
}