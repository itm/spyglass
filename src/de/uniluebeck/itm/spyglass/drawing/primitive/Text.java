package de.uniluebeck.itm.spyglass.drawing.primitive;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin.Position;

public class Text extends DrawingObject {

	public String text;
	
	public enum TextJustification{
		center, right, left
	}
	
	public TextJustification justification;
	
	public Text( String s, Position p, int id )
	{
		super(id);
		setPosition( p );
		text = s;
		justification = TextJustification.left;
	}
	
	public void setText(String s)
	{
		text = s;
	}

	public String getText()
	{
		return text;
	}

	public TextJustification getJustification() {
		return justification;
	}

	public void setJustification(TextJustification justification) {
		this.justification = justification;
	}
	
	public void update(DrawingObject other)
	{
		if (other instanceof Text)
		{
			super.update(other);
			Text t = (Text)other;
			setJustification(t.getJustification());
			setText(t.getText());
		}
	}
}
