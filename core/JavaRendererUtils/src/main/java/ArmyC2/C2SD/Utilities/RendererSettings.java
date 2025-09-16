/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ArmyC2.C2SD.Utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;


/**
 *Static class that holds the setting for the JavaRenderer.
 * Allows different parts of the renderer to know what
 * values are being used.
 * @author michael.spinelli
 */
public class RendererSettings {

    private static RendererSettings _instance = null;

    //outline approach.  none, filled rectangle, outline (default),
    //outline quick (outline will not exceed 1 pixels).
    private static int _TextBackgroundMethod = 3;
    /**
     * There will be no background for text
     */
    public static final int TextBackgroundMethod_NONE = 0;

    /**
     * There will be a colored box behind the text
     */
    public static final int TextBackgroundMethod_COLORFILL = 1;

    /**
     * There will be an adjustable outline around the text (expensive)
     * Outline width of 4 is recommended.
     */
    public static final int TextBackgroundMethod_OUTLINE = 2;

    /**
     * A different approach for outline which is quicker and seems to use
     * less memory.  Also, you may do well with a lower outline thickness setting
     * compared to the regular outlining approach.  Outline Width of 2 is
     * recommended.  Only works with RenderMethod_NATIVE.
     */
    public static final int TextBackgroundMethod_OUTLINE_QUICK = 3;

    /**
     * Value from 0 to 255. The closer to 0 the lighter the text color has to be
     * to have the outline be black. Default value is 160.
     */
    private static int _TextBackgroundAutoColorThreshold = 160;

    //if TextBackgroundMethod_OUTLINE is set, This value determnies the width of that outline.
    private static int _TextOutlineWidth = 2;

    //label foreground color, uses line color of symbol if null.
    private static Color _ColorLabelForeground = null; //Color.BLACK;
    //label background color, used if TextBackGroundMethod = TextBackgroundMethod_COLORFILL && not null
    private static Color _ColorLabelBackground = Color.WHITE;

    private static int _SymbolRenderMethod = 1;
    private static int _UnitRenderMethod = 1;
    private static int _TextRenderMethod = 1;
    /**
     * Collapse labels for fire support areas when the symbol isn't large enough to show all
     * the labels.
     */
    private static boolean _AutoCollapseModifiers = true;
    
    private static int _SymbolOutlineWidth = 1;
    
    

    /**
     * If true (default), when HQ Staff is present, location will be indicated by the free
     * end of the staff
     */
    private static Boolean _CenterOnHQStaff = true;

    /**
     * Everything that comes back from the Renderer is a Java Shape.  Simpler,
     * but can be slower when rendering modifiers or a large number of single
     * point symbols. Not recommended
     */
    public static final int RenderMethod_SHAPES = 0;
    /**
     * Adds a level of complexity to the rendering but is much faster for 
     * certain objects.  Modifiers and single point graphics will render faster.
     * MultiPoints will still be shapes.  Recommended
     */
    public static final int RenderMethod_NATIVE = 1;

    /**
     * 2525Bch2 and USAS 11-12 symbology
     */
    public static final int Symbology_2525B = 0;
    
    /**
     * 2525Bch2 and USAS 13/14 symbology
     * @deprecated use Symbology_2525B, 13-14 applies to 2525C
     */
    public static final int Symbology_2525Bch2_USAS_13_14 = 0;
    /**
     * 2525C, which includes 2525Bch2 & USAS 13/14
     */
    public static final int Symbology_2525C = 1;
    /**
     * 2525D, not support yet so defaults to 2525C is selected
     */
    public static final int Symbology_2525D = 1;
    
    private static int _SymbologyStandard = 0;

    public static int OperationalConditionModifierType_SLASH = 0;
    public static int OperationalConditionModifierType_BAR = 1;
    private static int _OCMType = 1;
    
    private static boolean _UseLineInterpolation = true;

    //private static Font _ModifierFont = new Font("arial", Font.TRUETYPE_FONT, 12);
    private static String _ModifierFontName = "arial";
    //private static int _ModifierFontType = Font.TRUETYPE_FONT;
    private static int _ModifierFontType = Font.BOLD;
    private static int _ModifierFontSize = 12;
    private static int _ModifierFontKerning = 0;//0=off, 1=on (TextAttribute.KERNING_ON)
    private static float _ModifierFontTracking = 0;//TextAttribute.TRACKING_LOOSE;//loose=0.4f;
    private boolean _scaleEchelon = false;
    private boolean _DrawAffiliationModifierAsLabel = true;
    
    private static String _MPModifierFontName = "arial";
    private static int _MPModifierFontType = Font.BOLD;
    private static int _MPModifierFontSize = 12;
    private static float _KMLLabelScale = 1.0f;
    
    private static int _DPI = 90;
    
    //acevedo - 11/29/2017 - adding option to render only 2 labels.
    private boolean _TwoLabelOnly = true;

    private double _overscanScale = 1.0;

    //acevedo - 12/8/17 - allow the setting of affiliation colors.
    private   Color _friendlyUnitFillColor = AffiliationColors.FriendlyUnitFillColor;
    /// <summary>
    /// Friendly Unit Fill Color.
    /// </summary>
    private   Color _hostileUnitFillColor = AffiliationColors.HostileUnitFillColor;//new Color(255,130,132);//Color.RED;
    /// <summary>
    /// Hostile Unit Fill Color.
    /// </summary>
    private   Color _neutralUnitFillColor = AffiliationColors.NeutralUnitFillColor;//new Color(144,238,144);//Color.GREEN;//new Color(0,255,0);//new Color(144,238,144);//light green//Color.GREEN;new Color(0,226,0);
    /// <summary>
    /// Neutral Unit Fill Color.
    /// </summary>
    private Color _unknownUnitFillColor = AffiliationColors.UnknownUnitFillColor;// new Color(255,255,128);//Color.YELLOW;
    /// <summary>
    /// UnknownUn Graphic Fill Color.
    /// </summary>
    private   Color _friendlyGraphicFillColor = AffiliationColors.FriendlyGraphicFillColor;//Crystal Blue //Color.CYAN;
    /// <summary>
    /// Friendly Graphic Fill Color.
    /// </summary>
    private Color _hostileGraphicFillColor = AffiliationColors.HostileGraphicFillColor;//salmon
    /// <summary>
    /// Hostile Graphic Fill Color.
    /// </summary>
    private   Color _neutralGraphicFillColor = AffiliationColors.NeutralGraphicFillColor;//Bamboo Green //new Color(144,238,144);//light green
    /// <summary>
    /// Neutral Graphic Fill Color.
    /// </summary>
    private   Color _unknownGraphicFillColor = AffiliationColors.UnknownGraphicFillColor;//light yellow  new Color(255,255,224);//light yellow
    /// <summary>
    /// Unknown Unit Line Color.
    /// </summary>
    private   Color _friendlyUnitLineColor = AffiliationColors.FriendlyUnitLineColor;
    /// <summary>
    /// Friendly Unit Line Color.
    /// </summary>
    private   Color _hostileUnitLineColor = AffiliationColors.HostileUnitLineColor;
    /// <summary>
    /// Hostile Unit Line Color.
    /// </summary>
    private   Color _neutralUnitLineColor = AffiliationColors.NeutralUnitLineColor;
    /// <summary>
    /// Neutral Unit Line Color.
    /// </summary>
    private   Color _unknownUnitLineColor = AffiliationColors.UnknownUnitLineColor;
    /// <summary>
    /// Unknown Graphic Line Color.
    /// </summary>
    private   Color _friendlyGraphicLineColor = AffiliationColors.FriendlyGraphicLineColor;
    /// <summary>
    /// Friend Graphic Line Color.
    /// </summary>
    private   Color _hostileGraphicLineColor = AffiliationColors.HostileGraphicLineColor;
    /// <summary>
    /// Hostile Graphic Line Color.
    /// </summary>
    private   Color _neutralGraphicLineColor = AffiliationColors.NeutralGraphicLineColor;
    /// <summary>
    /// Neutral Graphic Line Color.
    /// </summary>
    private   Color _unknownGraphicLineColor = AffiliationColors.UnknownGraphicLineColor;

    /*private   Color WeatherRed = new Color(198,16,33);//0xC61021;// 198,16,33
    private   Color WeatherBlue = new Color(0,0,255);//0x0000FF;// 0,0,255

    private   Color WeatherPurpleDark = new Color(128,0,128);//0x800080;// 128,0,128 Plum Red
    private   Color WeatherPurpleLight = new Color(226,159,255);//0xE29FFF;// 226,159,255 Light Orchid

    private   Color WeatherBrownDark = new Color(128,98,16);//0x806210;// 128,98,16 Safari
    private   Color WeatherBrownLight = new Color(210,176,106);//0xD2B06A;// 210,176,106 Khaki
    */

    private RendererSettings()
    {
        Init();

    }

    public static synchronized RendererSettings getInstance()
    {
        if(_instance == null)
            _instance = new RendererSettings();

        return _instance;
    }
    
    private void Init()
    {

    }

    /**
     * None, outline (default), or filled background.
     * If set to OUTLINE, TextOutlineWidth changed to default of 4.
     * If set to OUTLINE_QUICK, TextOutlineWidth changed to default of 2.
     * Use setTextOutlineWidth if you'd like a different value.
     * @param method like RenderSettings.TextBackgroundMethod_NONE
     */
    synchronized public void setTextBackgroundMethod(int textBackgroundMethod)
    {
        _TextBackgroundMethod = textBackgroundMethod;
        if(_TextBackgroundMethod == TextBackgroundMethod_OUTLINE)
            _TextOutlineWidth = 4;
        else if(_TextBackgroundMethod == TextBackgroundMethod_OUTLINE_QUICK)
            _TextOutlineWidth = 2;
    }

    /**
     * None, outline (default), or filled background.
     * @return method like RenderSettings.TextBackgroundMethod_NONE
     */
    synchronized public int getTextBackgroundMethod()
    {
        return _TextBackgroundMethod;
    }
    
    /**
     * determines what kind of java objects will be generated when processing
     * a symbol. RenderMethod_SHAPES is simpler as everything is treated
     * the same. RenderMethod_NATIVE is faster but, in addition to shapes,
     * uses GlyphVectors and TextLayouts.
     * @param method like RendererSetting.RenderMethod_NATIVE
     */
    public void setSymbolRenderMethod(int symbolRenderMethod)
    {
        _SymbolRenderMethod = symbolRenderMethod;
    }

    /**
     * Maps to RendererSetting.RenderMethod_SHAPES or
     * RendererSetting.RenderMethod_NATIVE
     * @return method like RendererSetting.RenderMethod_NATIVE
     */
    public int getSymbolRenderMethod()
    {
        return _SymbolRenderMethod;
    }
    
    /**
     * Controls what symbols are supported.
     * Set this before loading the renderer.
     * @param symbologyStandard
     * Like RendererSettings.Symbology_2525Bch2_USAS_13_14
     */
    public void setSymbologyStandard(int standard)
    {
        _SymbologyStandard = standard;

        /*ErrorLogger.LogMessage("symStd set to: " + String.valueOf(standard));
        System.out.println(ErrorLogger.getStackTrace(new Exception()));//*/
    }

    /**
     * Current symbology standard
     * @return symbologyStandard
     * Like RendererSettings.Symbology_2525Bch2_USAS_13_14
     */
    public int getSymbologyStandard()
    {
        return _SymbologyStandard;
    }
    
        /**
     * Set the operational condition modifier to be slashes or bars
     * @param value like RendererSettings.OperationalConditionModifierType_SLASH
     */
    public void setOperationalConditionModifierType(int value)
    {
        _OCMType = value;
    }

    public int getOperationalConditionModifierType()
    {
        return _OCMType;
    }
    
    /**
     * For lines symbols with "decorations" like FLOT or LOC, when points are 
     * too close together, we will start dropping points until we get enough 
     * space between 2 points to draw the decoration.  Without this, when points
     * are too close together, you run the chance that the decorated line will
     * look like a plain line because there was no room between points to
     * draw the decoration.
     * @param value 
     */
    public void setUseLineInterpolation(boolean value)
    {
        _UseLineInterpolation = value;
    }
    
    /**
     * Returns the current setting for Line Interpolation.
     * @return 
     */
    public boolean getUseLineInterpolation()
    {
        return _UseLineInterpolation;
    }
    
    /**
     * set the screen DPI so the renderer can take DPI into account when 
     * rendering for things like dashed lines and decorated lines.
     * @param value 
     */
    public void setDeviceDPI(int value)
    {
        _DPI = value;
    }
    public int getDeviceDPI()
    {
        return _DPI;
    }
    /**
     * Collapse Modifiers for fire support areas when the symbol isn't large enough to show all
     * the labels.  Identifying label will always be visible.  Zooming in, to make the symbol larger,
     * will make more modifiers visible.  Resizing the symbol can also make more modifiers visible.
     * @param value
     */
    public void setAutoCollapseModifiers(boolean value) {_AutoCollapseModifiers = value;}

    public boolean getAutoCollapseModifiers() {return _AutoCollapseModifiers;}

    /**
     * determines what kind of java objects will be generated when processing
     * a symbol. RenderMethod_SHAPES is simpler as everything is treated
     * the same. RenderMethod_NATIVE is faster but, in addition to shapes,
     * uses GlyphVectors and TextLayouts.
     * @param method like RendererSetting.RenderMethod_SHAPES
     */

    /**
     * determines what kind of java objects will be generated when processing
     * a symbol. RenderMethod_SHAPES is simpler as everything is treated
     * the same. RenderMethod_NATIVE is faster but, in addition to shapes,
     * uses GlyphVectors and TextLayouts.
     * @param method like RendererSetting.RenderMethod_SHAPES
     */
    public void setUnitRenderMethod(int symbolRenderMethod)
    {
        _UnitRenderMethod = symbolRenderMethod;
    }

    /**
     * Maps to RendererSetting.RenderMethod_SHAPES or
     * RendererSetting.RenderMethod_NATIVE
     * @return method like RendererSetting.RenderMethod_NATIVE
     */
    public int getUnitRenderMethod()
    {
        return _UnitRenderMethod;
    }

    /**
     * if true (default), when HQ Staff is present, location will be indicated by the free
     * end of the staff
     * @param value
     */
    public void setCenterOnHQStaff(Boolean value)
    {
        _CenterOnHQStaff = value;
    }

    /**
     * if true (default), when HQ Staff is present, location will be indicated by the free
     * end of the staff
     * @param value
     */
    public Boolean getCenterOnHQStaff()
    {
        return _CenterOnHQStaff;
    }

     /**
     * determines what kind of java objects will be generated when processing
     * a symbol. RenderMethod_SHAPES is simpler as everything is treated
     * the same. RenderMethod_NATIVE is faster but, in addition to shapes,
     * uses GlyphVectors and TextLayouts.  In the case of text, NATIVE tends to
     * render sharper and clearer text.
     * @param method like RendererSetting.RenderMethod_SHAPES
     */
    public void setTextRenderMethod(int symbolRenderMethod)
    {
        _TextRenderMethod = symbolRenderMethod;
    }

    /**
     * Maps to RendererSetting.RenderMethod_SHAPES or
     * RendererSetting.RenderMethod_NATIVE
     * @return
     */
    public int getTextRenderMethod()
    {
        return _TextRenderMethod;
    }

    /**
     * if RenderSettings.TextBackgroundMethod_OUTLINE is used,
     * the outline will be this many pixels wide.
     * @param method
     */
    synchronized public void setTextOutlineWidth(int width)
    {
        _TextOutlineWidth = width;
    }

    /**
     * if RenderSettings.TextBackgroundMethod_OUTLINE is used,
     * the outline will be this many pixels wide.
     * @param method
     * @return
     */
    synchronized public int getTextOutlineWidth()
    {
        return _TextOutlineWidth;
    }

     /**
     * Refers to text color of modifier labels
     * @return
      *  
     */
    public Color getLabelForegroundColor()
    {
        return _ColorLabelForeground;
    }

    /**
     * Refers to text color of modifier labels
     * Default Color is Black.  If NULL, uses line color of symbol
     * @param value
     * 
     */
    synchronized public void setLabelForegroundColor(Color value)
    {
       _ColorLabelForeground = value;
    }

    /**
     * Refers to background color of modifier labels
     * @return
     * 
     */
    public Color getLabelBackgroundColor()
    {
        return _ColorLabelBackground;
    }

    /**
     * Refers to text color of modifier labels
     * Default Color is White.
     * Null value means the optimal background color (black or white)
     * will be chose based on the color of the text.
     * @param value
     * 
     */
    synchronized public void setLabelBackgroundColor(Color value)
    {
        _ColorLabelBackground = value;
    }

    /**
     * Value from 0 to 255. The closer to 0 the lighter the text color has to be
     * to have the outline be black. Default value is 160.
     * @param value
     */
    public void setTextBackgroundAutoColorThreshold(int value)
    {
        _TextBackgroundAutoColorThreshold = value;
    }

    /**
     * Value from 0 to 255. The closer to 0 the lighter the text color has to be
     * to have the outline be black. Default value is 160.
     * @return
     */
    public int getTextBackgroundAutoColorThreshold()
    {
        return _TextBackgroundAutoColorThreshold;
    }
    
    /**
     * This applies to Single Point Tactical Graphics.
     * Setting this will determine the default value for milStdSymbols when created.
     * 0 for no outline,
     * 1 for outline thickness of 1 pixel, 
     * 2 for outline thickness of 2 pixels,
     * greater than 2 is not currently recommended.
     * @param width
     */
    synchronized public void setSinglePointSymbolOutlineWidth(int width)
    {
        _SymbolOutlineWidth = width;
    }

    /**
     * This applies to Single Point Tactical Graphics.
     * @return
     */
    synchronized public int getSinglePointSymbolOutlineWidth()
    {
        return _SymbolOutlineWidth;
    }
    
    /**
     * false to use label font size
     * true to scale it using symbolPixelBounds / 3.5
     * @param value 
     */
    public void setScaleEchelon(boolean value)
    {
        _scaleEchelon = value;
    }
    /**
     * Returns the value determining if we scale the echelon font size or
     * just match the font size specified by the label font.
     * @return true or false
     */
    public boolean getScaleEchelon()
    {
        return _scaleEchelon;
    }
    
     /**
     * Determines how to draw the Affiliation modifier.
     * True to draw as modifier label in the "E/F" location.
     * False to draw at the top right corner of the symbol
     */
    public void setDrawAffiliationModifierAsLabel(boolean value)
    {
        _DrawAffiliationModifierAsLabel = value;
    }
    /**
     * True to draw as modifier label in the "E/F" location.
     * False to draw at the top right corner of the symbol
     */
    public boolean getDrawAffiliationModifierAsLabel()
    {
        return _DrawAffiliationModifierAsLabel;
    }

    /**
     * Sets the font to be used for modifier labels
     * @param name Like "arial"
     * @param type Like Font.TRUETYPE_FONT
     * @param size Like 12
     */
    public void setLabelFont(String name, int type, int size)
    {
        _ModifierFontName = name;
        _ModifierFontType = type;
        _ModifierFontSize = size;
        _ModifierFontKerning = 0;
        _ModifierFontTracking = TextAttribute.TRACKING_LOOSE;
    }
    
    public void setMPLabelFont(String name, int type, int size)
    {
        _MPModifierFontName = name;
        _MPModifierFontType = type;
        _MPModifierFontSize = size;
        _KMLLabelScale = 1.0f;
        //_MPModifierFontKerning = 0;
        //_MPModifierFontTracking = TextAttribute.TRACKING_LOOSE;
    }
    
    public void setMPLabelFont(String name, int type, int size, float kmlScale)
    {
        _MPModifierFontName = name;
        _MPModifierFontType = type;
        _MPModifierFontSize = Math.round(size * kmlScale);
        _KMLLabelScale = kmlScale;
        //_MPModifierFontKerning = 0;
        //_MPModifierFontTracking = TextAttribute.TRACKING_LOOSE;
    }
    

    /**
     * 
     * @param name Like "arial"
     * @param type Like Font.BOLD
     * @param size Like 12
     * @param kerning - default false. The default advances of single characters are not
     * appropriate for some character sequences, for example "To" or
     * "AWAY".  Without kerning the adjacent characters appear to be
     * separated by too much space.  Kerning causes selected sequences
     * of characters to be spaced differently for a more pleasing
     * visual appearance. 
     * @param Tracking - default 0.04 (TextAttribute.TRACKING_LOOSE).  
     * The tracking value is multiplied by the font point size and
     * passed through the font transform to determine an additional
     * amount to add to the advance of each glyph cluster.  Positive
     * tracking values will inhibit formation of optional ligatures.
     * Tracking values are typically between -0.1 and
     * 0.3 values outside this range are generally not desirable.
     */
    public void setLabelFont(String name, int type, int size, Boolean kerning, float tracking)
    {
        _ModifierFontName = name;
        _ModifierFontType = type;
        _ModifierFontSize = size;
        if(kerning==false)
            _ModifierFontKerning = 0;
        else
            _ModifierFontKerning = TextAttribute.KERNING_ON;
        _ModifierFontTracking = tracking;
    }
    
    /*public void setLabelFont(Font font)
    {
        Map<TextAttribute, Object> temp = (Map<TextAttribute, Object>) font.getAttributes();
        System.out.println(temp.toString());
        _ModifierFontSize = font.getSize();
        _ModifierFontName = temp.get(TextAttribute.FAMILY).toString();
        Object weight = null;
        float fWeight = 2;
        if(temp.containsKey(TextAttribute.WEIGHT))
        {
            weight = temp.get(TextAttribute.WEIGHT);
            if(weight != null)
                fWeight = (Float)weight;
            //convert weight to font type    
        }
        _ModifierFontType
    }//*/

    /**
     * the font name to be used for modifier labels
     * @return name of the label font
     */
    public String getLabelFontName()
    {
        return _ModifierFontName;
    }

    /**
     * Like Font.BOLD
     * @return type of the label font
     */
    public int getLabelFontType()
    {
        return _ModifierFontType;
    }

    /**
     * get font point size
     * @return size of the label font
     */
    public int getLabelFontSize()
    {
        return _ModifierFontSize;
    }
    
    /**
     * 
     * @return 0=off, 1=on.
     */
    public int getLabelFontKerning()
    {
        return _ModifierFontKerning;
    }
    
    /**
     * 
     * @return 
     */
    public float getLabelFontTracking()
    {
        return _ModifierFontTracking;
    }


    /**
     * get font object used for labels
     * @return Font object
     */
    public Font getLabelFont()
    {
        try
        {
            Map<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
//            map.put(TextAttribute.FONT, _ModifierFontName);
//            map.put(TextAttribute.SIZE, _ModifierFontSize);
//            map.put(TextAttribute.WEIGHT, _ModifierFontType);
            map.put(TextAttribute.KERNING, _ModifierFontKerning);
            map.put(TextAttribute.TRACKING, _ModifierFontTracking);
            
            Font temp = new Font(_ModifierFontName, _ModifierFontType, _ModifierFontSize);
                    
            return temp.deriveFont(map);
        }
        catch(Exception exc)
        {
            String message = "font creation error, returning \"" + _ModifierFontName + "\" font, " + _ModifierFontSize + "pt. Check font name and type.";
            ErrorLogger.LogMessage("RendererSettings", "getLabelFont", message);
            ErrorLogger.LogMessage("RendererSettings", "getLabelFont", exc.getMessage());
            return new Font("arial", Font.BOLD, 12);
        }
    }
    
        /**
     * get font object used for labels
     * @return Font object
     */
    public Font getMPLabelFont()
    {
        try
        {
            Map<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
//            map.put(TextAttribute.FONT, _ModifierFontName);
//            map.put(TextAttribute.SIZE, _ModifierFontSize);
//            map.put(TextAttribute.WEIGHT, _ModifierFontType);
            //map.put(TextAttribute.KERNING, _ModifierFontKerning);
            //map.put(TextAttribute.TRACKING, _ModifierFontTracking);
            
            Font temp = new Font(_MPModifierFontName, _MPModifierFontType, _MPModifierFontSize);
                    
            return temp;//.deriveFont(map);
        }
        catch(Exception exc)
        {
            String message = "font creation error, returning \"" + _MPModifierFontName + "\" font, " + _MPModifierFontSize + "pt. Check font name and type.";
            ErrorLogger.LogMessage("RendererSettings", "getMPLabelFont", message);
            ErrorLogger.LogMessage("RendererSettings", "getMPLabelFont", exc.getMessage());
            return new Font("arial", Font.BOLD, 12);
        }
    }
    
    public float getKMLLabelScale()
    {
        return _KMLLabelScale;
    }
    
    /**
  	 ** Get a boolean indicating between the use of ENY labels in all segments (false) or 
 	 * to only set 2 labels one at the north and the other one at the south of the graphic (true).
  	 * @returns {boolean}
  	 */
  	public boolean getTwoLabelOnly()
  	{
  			return _TwoLabelOnly;
  	}
  	
  	/**
 	 * Set a boolean indicating between the use of ENY labels in all segments (false) or 
 	 * to only set 2 labels one at the north and the other one at the south of the graphic (true).
 	 * @param TwoLabelOnly
 	 */
 	public void setTwoLabelOnly(boolean TwoLabelOnly )
 	{
 		_TwoLabelOnly = TwoLabelOnly;
 	}

    /**
     * Optionally expand multipoint rendering outside bounding box by a scale factor.
     * Useful when panning map before rendering with updated bounding box.
     * Only referenced when bounding box is a valid rectangle.
     * For example, setting overscanScale to 3 would render all shapes within range 3 * the width and 3 * the height of the bounding box
     * @param overscanScale default is 1 and minimum is 1
     */
    public void setOverscanScale(double overscanScale) {
        this._overscanScale = Math.max(overscanScale, 1);
    }

    public double getOverscanScale() {
        return this._overscanScale;
    }

    /**
     * get the preferred fill affiliation color for units.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getFriendlyUnitFillColor() {
        return _friendlyUnitFillColor;
    }
    /**
     * Set the preferred fill affiliation color for units
     *
     * @param friendlyUnitFillColor Color like  Color(255, 255, 255)
     *
     * */
    public void setFriendlyUnitFillColor(Color friendlyUnitFillColor) {
 	    if (friendlyUnitFillColor != null)
        _friendlyUnitFillColor = friendlyUnitFillColor;
    }
    /**
     * get the preferred fill affiliation color for units.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getHostileUnitFillColor() {
        return _hostileUnitFillColor;
    }
    /**
     * Set the preferred fill affiliation color for units
     *
     * @param hostileUnitFillColor Color like  Color(255, 255, 255)
     *
     * */
    public void setHostileUnitFillColor(Color hostileUnitFillColor) {
        if (hostileUnitFillColor != null)
        _hostileUnitFillColor = hostileUnitFillColor;
    }
    /**
     * get the preferred fill affiliation color for units.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getNeutralUnitFillColor() {
        return _neutralUnitFillColor;
    }
    /**
     * Set the preferred line affiliation color for units
     *
     * @param neutralUnitFillColor Color like  Color(255, 255, 255)
     *
     * */
    public void setNeutralUnitFillColor(Color neutralUnitFillColor) {
        if (neutralUnitFillColor != null)
        _neutralUnitFillColor = neutralUnitFillColor;
    }
    /**
     * get the preferred fill affiliation color for units.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getUnknownUnitFillColor() {
        return _unknownUnitFillColor;
    }
    /**
     * Set the preferred fill affiliation color for units
     *
     * @param unknownUnitFillColor Color like  Color(255, 255, 255)
     *
     * */
    public void setUnknownUnitFillColor(Color unknownUnitFillColor) {
        if (unknownUnitFillColor != null)
        _unknownUnitFillColor = unknownUnitFillColor;
    }
    /**
     * get the preferred fill affiliation color for graphics.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public   Color getHostileGraphicFillColor() {
        return _hostileGraphicFillColor;
    }
    /**
     * Set the preferred fill affiliation color for graphics
     *
     * @param hostileGraphicFillColor Color like  Color(255, 255, 255)
     *
     * */
    public  void setHostileGraphicFillColor(Color hostileGraphicFillColor) {
        if (hostileGraphicFillColor != null)
        _hostileGraphicFillColor = hostileGraphicFillColor;
    }
    /**
     * get the preferred fill affiliation color for graphics.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getFriendlyGraphicFillColor() {
        return _friendlyGraphicFillColor;
    }
    /**
     * Set the preferred fill affiliation color for graphics
     *
     * @param friendlyGraphicFillColor Color like  Color(255, 255, 255)
     *
     * */
    public void setFriendlyGraphicFillColor(Color friendlyGraphicFillColor) {
        if (friendlyGraphicFillColor != null)
        _friendlyGraphicFillColor = friendlyGraphicFillColor;
    }
    /**
     * get the preferred fill affiliation color for graphics.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getNeutralGraphicFillColor() {
        return _neutralGraphicFillColor;
    }
    /**
     * Set the preferred fill affiliation color for graphics
     *
     * @param neutralGraphicFillColor Color like  Color(255, 255, 255)
     *
     * */
    public void setNeutralGraphicFillColor(Color neutralGraphicFillColor) {
        if (neutralGraphicFillColor != null)
        _neutralGraphicFillColor = neutralGraphicFillColor;
    }
    /**
     * get the preferred fill affiliation color for graphics.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getUnknownGraphicFillColor() {
        return _unknownGraphicFillColor;
    }
    /**
     * Set the preferred fill affiliation color for graphics
     *
     * @param unknownGraphicFillColor Color like  Color(255, 255, 255)
     *
     * */
    public void setUnknownGraphicFillColor(Color unknownGraphicFillColor) {
        if (unknownGraphicFillColor != null)
        _unknownGraphicFillColor = unknownGraphicFillColor;
    }
    /**
     * get the preferred line affiliation color for units.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getFriendlyUnitLineColor() {
        return _friendlyUnitLineColor;
    }
    /**
     * Set the preferred line affiliation color for units
     *
     * @param friendlyUnitLineColor Color like  Color(255, 255, 255)
     *
     * */
    public void setFriendlyUnitLineColor(Color friendlyUnitLineColor) {
        if (friendlyUnitLineColor != null)
        this._friendlyUnitLineColor = friendlyUnitLineColor;
    }
    /**
     * get the preferred line   affiliation color for units.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getHostileUnitLineColor() {
        return _hostileUnitLineColor;
    }
    /**
     * Set the preferred line affiliation color for units
     *
     * @param hostileUnitLineColor Color like  Color(255, 255, 255)
     *
     * */
    public void setHostileUnitLineColor(Color hostileUnitLineColor) {
        if (hostileUnitLineColor != null)
        this._hostileUnitLineColor = hostileUnitLineColor;
    }
    /**
     * get the preferred line affiliation color for units.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getNeutralUnitLineColor() {
        return _neutralUnitLineColor;
    }
    /**
     * Set the preferred line affiliation color for units
     *
     * @param neutralUnitLineColor Color like  Color(255, 255, 255)
     *
     * */
    public void setNeutralUnitLineColor(Color neutralUnitLineColor) {
        if (neutralUnitLineColor != null)
        this._neutralUnitLineColor = neutralUnitLineColor;
    }
    /**
     * get the preferred line affiliation color for units.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getUnknownUnitLineColor() {
        return _unknownUnitLineColor;
    }
    /**
     * Set the preferred line affiliation color for units
     *
     * @param unknownUnitLineColor Color like  Color(255, 255, 255)
     *
     * */
    public void setUnknownUnitLineColor(Color unknownUnitLineColor) {
        if (unknownUnitLineColor != null)
        this._unknownUnitLineColor = unknownUnitLineColor;
    }
    /**
     * get the preferred line affiliation color for graphics.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getFriendlyGraphicLineColor() {
        return _friendlyGraphicLineColor;
    }
    /**
     * Set the preferred line affiliation color for graphics
     *
     * @param friendlyGraphicLineColor Color like  Color(255, 255, 255)
     *
     * */
    public void setFriendlyGraphicLineColor(Color friendlyGraphicLineColor) {
        if (friendlyGraphicLineColor != null)
        this._friendlyGraphicLineColor = friendlyGraphicLineColor;
    }
    /**
     * get the preferred line affiliation color for graphics.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getHostileGraphicLineColor() {
        return _hostileGraphicLineColor;
    }
    /**
     * Set the preferred line affiliation color for graphics
     *
     * @param hostileGraphicLineColor Color like  Color(255, 255, 255)
     *
     * */
    public void setHostileGraphicLineColor(Color hostileGraphicLineColor) {
        if (hostileGraphicLineColor != null)
        this._hostileGraphicLineColor = hostileGraphicLineColor;
    }
    /**
     * get the preferred line affiliation color for graphics.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getNeutralGraphicLineColor() {
        return _neutralGraphicLineColor;
    }
    /**
     * Set the preferred line affiliation color for graphics
     *
     * @param neutralGraphicLineColor Color like  Color(255, 255, 255)
     *
     * */
    public void setNeutralGraphicLineColor(Color neutralGraphicLineColor) {
        if (neutralGraphicLineColor != null)
        this._neutralGraphicLineColor = neutralGraphicLineColor;
    }
    /**
     * get the preferred line affiliation color for graphics.
     *
     * @return Color like  Color(255, 255, 255)
     *
     * */
    public Color getUnknownGraphicLineColor() {
        return _unknownGraphicLineColor;
    }
    /**
     * Set the preferred line affiliation color for graphics
     *
     * @param unknownGraphicLineColor Color like  Color(255, 255, 255)
     *
     * */
    public void setUnknownGraphicLineColor(Color unknownGraphicLineColor) {
        if (unknownGraphicLineColor != null)
        this._unknownGraphicLineColor = unknownGraphicLineColor;
    }

    /**
     * Set the preferred line and fill affiliation color for tactical graphics.
     *
     * @param friendlyGraphicLineColor Color
     * @param hostileGraphicLineColor Color
     * @param neutralGraphicLineColor Color
     * @param unknownGraphicLineColor Color
     * @param friendlyGraphicFillColor Color
     * @param hostileGraphicFillColor Color
     * @param neutralGraphicFillColor Color
     * @param unknownGraphicFillColor Color
     */
    public void setGraphicPreferredAffiliationColors(Color friendlyGraphicLineColor,
                                                     Color hostileGraphicLineColor,
                                                     Color neutralGraphicLineColor,
                                                     Color unknownGraphicLineColor,
                                                     Color friendlyGraphicFillColor,
                                                     Color hostileGraphicFillColor,
                                                     Color neutralGraphicFillColor,
                                                     Color unknownGraphicFillColor) {


          setFriendlyGraphicLineColor(friendlyGraphicLineColor);
          setHostileGraphicLineColor(hostileGraphicLineColor);
          setNeutralGraphicLineColor(neutralGraphicLineColor);
          setUnknownGraphicLineColor(unknownGraphicLineColor);
          setFriendlyGraphicFillColor(friendlyGraphicFillColor);
          setHostileGraphicFillColor(hostileGraphicFillColor);
          setNeutralGraphicFillColor(neutralGraphicFillColor);
          setUnknownGraphicFillColor(unknownGraphicFillColor);
    }

    /**
     * Set the preferred line and fill affiliation color for units and tactical graphics.
     *
     * @param friendlyUnitLineColor Color like  Color(255, 255, 255). Set to null to ignore setting
     * @param hostileUnitLineColor Color
     * @param neutralUnitLineColor Color
     * @param unknownUnitLineColor Color
     * @param friendlyUnitFillColor Color
     * @param hostileUnitFillColor Color
     * @param neutralUnitFillColor Color
     * @param unknownUnitFillColor Color
     */
    public void setUnitPreferredAffiliationColors(   Color friendlyUnitLineColor,
                                                     Color hostileUnitLineColor,
                                                     Color neutralUnitLineColor,
                                                     Color unknownUnitLineColor,
                                                     Color friendlyUnitFillColor,
                                                     Color hostileUnitFillColor,
                                                     Color neutralUnitFillColor,
                                                     Color unknownUnitFillColor) {

        setFriendlyUnitLineColor(friendlyUnitLineColor);
        setHostileUnitLineColor(hostileUnitLineColor);
        setNeutralUnitLineColor(neutralUnitLineColor);
        setUnknownUnitLineColor(unknownUnitLineColor);
        setFriendlyUnitFillColor(friendlyUnitFillColor);
        setHostileUnitFillColor(hostileUnitFillColor);
        setNeutralUnitFillColor(neutralUnitFillColor);
        setUnknownUnitFillColor(unknownUnitFillColor);
    }

}
