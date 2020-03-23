package org.javautil.document.style;

import java.awt.Color;
import java.awt.Font;

// TODO eliminate the interface and convert StyleImpl
/**
 * An abstraction of a cell style.
 * 
 * Initially designed as an abstract represention of the presentation
 * information for the content of a table data
 * 
 * A Style containing java.awt and non-document specific representations of
 * style components. The java.awt.Color and java.awt.Font objects are used
 * frequently because of their good out of the box support.
 * 
 */
public interface Style {

	/**
	 * Returns the name of the style, should be unique across other style instances
	 * when contained in a DocumentStyle
	 * 
	 * @return name The name
	 */
	public String getName();

	/**
	 * Sets the name of the style
	 * 
	 * @param name The name of the style
	 */
	public void setName(String name);

	/**
	 * Returns the platform's native font name that is used
	 * 
	 * @return font The font
	 */
	public Font getFont();

	/**
	 * Sets the name of the native font to be used
	 * 
	 * @param font The font to be used
	 */
	public void setFont(Font font);

	/**
	 * Returns a set of attributes to be used in conjunction with the font and
	 * fontColor when rendering the font
	 * 
	 * @return fontAttributes the fontAttributes
	 */
	public FontAttributes getFontAttributes();

	/**
	 * Sets the fontAttributes
	 * 
	 * @param fontAttributes The fontAttributes
	 */
	public void setFontAttributes(FontAttributes fontAttributes);

	/**
	 * Returns the color to be used when rendering the font
	 * 
	 * @return fontColor the fontColor
	 */
	public Color getFontColor();

	/**
	 * Sets the color to be used when rendering the font
	 * 
	 * @param fontColor The font color
	 */
	public void setFontColor(Color fontColor);

	/**
	 * Returns the vertical alignment
	 * 
	 * @return verticalAlignment
	 */
	public VerticalAlignment getVerticalAlignment();

	/**
	 * Sets the vertical alignment
	 * 
	 * @param verticalAlignment The verticalAlignment
	 */
	public void setVerticalAlignment(VerticalAlignment verticalAlignment);

	/**
	 * Gets the horizontal alignment
	 * 
	 * @return horizontalAlignment
	 */
	public HorizontalAlignment getHorizontalAlignment();

	/**
	 * Sets the horizontal alignment
	 * 
	 * @param horizontalAlignment The horizontalAlignment
	 */
	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment);

	/**
	 * Gets the borders
	 * 
	 * @return borders the borders
	 */
	public Borders getBorders();

	/**
	 * Sets the borders
	 * 
	 * @param borders The borders
	 */
	public void setBorders(Borders borders);

	/**
	 * Gets the background color
	 * 
	 * @return backgroundColor the backgroundColor
	 */
	public Color getBackgroundColor();

	/**
	 * Sets the background color
	 * 
	 * @param color The color to set.
	 */
	public void setBackgroundColor(Color color);

	/**
	 * Gets the format mask to be used when rendering
	 * 
	 * @return formatMask the formatMask
	 */
	public String getFormatMask();

	/**
	 * Sets the format mask to be used when rendering
	 * 
	 * @param formatMask The formatMask
	 */
	public void setFormatMask(String formatMask);

	/**
	 * Produces a new copy of the style, that return true for the equals method
	 * 
	 * @return a copy of this style
	 */
	public Style copyOf();

	public void applyAttributes(Style style);

}
