/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArmyC2.C2SD.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author michael.spinelli
 */
public class MilStdSymbol {

        //private SymbolDef _symbolDefinition = null;
    //private UnitDef _unitDefinition = null;
    /**
     * modifiers
     */
    private Map<String, String> _Properties = null;

    //for tactical graphics
    private ArrayList<Double> _X_Altitude = null;
    private ArrayList<Double> _AM_Distance = null;
    private ArrayList<Double> _AN_Azimuth = null;

    private String _symbolID = "";

    /**
     * unique ID for this symbol, for client use
     */
    private String _UUID = null;

    private ArrayList<ShapeInfo> _SymbolShapes;

    /**
     * collection of shapes for the modifiers
     */
    private ArrayList<ShapeInfo> _ModifierShapes;

    private ArrayList<Point2D.Double> _Coordinates;

    private int _UnitSize = 0;
    private double _scale = 0;
    private Boolean _KeepUnitRatio = true;

    Integer _LineWidth = 3;
    Color _IconColor = null;
    Color _LineColor = null;
    Color _FillColor = null;
    Color _TextColor = null;
    Color _TextBackgroundColor = null;
    TexturePaint _FillStyle = null;
    int _PatternFillType = 0;
    boolean _usePatternFill = false;
    double _Rotation = 0.0;//DEGREES

    //outline singlepoint TGs
    boolean _Outline = false;
    //if null, renderer determines outline Color.
    Color _OutLineColor = null;
    int _OutLineWidth = 0;

    private boolean isPluginSymbol = false;
    /**
     * 2525Bch2 and USAS 13/14 symbology
     */
    public static final int Symbology_2525Bch2_USAS_13_14 = 0;
    /**
     * 2525C, which includes 2525Bch2 & USAS 13/14
     */
    public static final int Symbology_2525C = 1;

    private static int _SymbologyStandard = 0;

    private static String _AltitudeMode = "";
    
    private static boolean _HideOptionalLabels = false;

    private static boolean _UseDashArray = true;

    private static boolean _DrawAffiliationModifierAsLabel = true;

    private static boolean _UseLineInterpolation = false;
    
    private boolean _wasClipped = false;

    Object _Tag = null;

    /**
     * Used to hold metadata for each segment of the symbol for multi-point
     * symbols. Each segment can contain one object.
     */
		//private Map _segmentData;
		// Constants for dynamic properties
/*
     public static final String SYMBOL_ID = "Symbol ID";
     //public static final String SOURCE = "Source";
     //public static final String EDITOR_CLASS_TYPE = "Editor Class Type";
     public static final String URN = "URN";
     public static final String UIC = "UIC";
     public static final String ANGLE_OF_ROTATION = "Angle of Rotation";
     public static final String LENGTH = "Length";
     public static final String WIDTH = "Width";
     public static final String RADIUS = "Radius";
     public static final String SEGMENT_DATA = "Segment Data";
     */

    /*
     public static final String GEO_POINT = "point";
     public static final String GEO_LINE = "line";
     public static final String GEO_POLYGON = "area";
     public static final String GEO_TEXT = "text";
     public static final String GEO_CIRCLE = "circle";
     public static final String GEO_RECTANGLE = "rectangle";
     public static final String GEO_ARC = "arc";
     public static final String GEO_SQUARE = "square";
     */
    /*
     private static final String _COORDINATES = "Coordinates";
     private static final String _GEOMETRY = "Geometry";
     private static final String _FILL_COLOR = "Fill Color";
     private static final String _FILL_ALPHA = "Fill Alpha";
     private static final String _FILL_STYLE = "Fill Style";
     private static final String _LINE_WIDTH = "Line Width";
     private static final String _LINE_COLOR = "Line Color";
     private static final String _LINE_ALPHA = "Line Alpha";
     private static final String _TEXT_BACKGROUND_COLOR = "Background Color";
     private static final String _TEXT_FOREGROUND_COLOR = "Foreground Color";
     private static final String _USE_FILL = "Use Fill";
     */
    /*
     protected static const _COORDINATES:String = "Coordinates";
     protected static const _GEOMETRY:String = "Geometry";
     protected static const _FILL_COLOR:String = "Fill Color";
     protected static const _FILL_ALPHA:String = "Fill Alpha";
     private int _FILL_STYLE:String = "Fill Style";
     protected static const _LINE_WIDTH:String = 0;
     private Color _LINE_COLOR = Color.BLACK;
     private int _LINE_ALPHA:String = 0;
     private Color _TEXT_BACKGROUND_COLOR = Color.WHITE;
     private Color _TEXT_FOREGROUND_COLOR = Color.BLACK;
     private bool _USE_FILL:String = "Use Fill";*/
    /**
     * Creates a new MilStdSymbol.
     *
     * @param symbolID code, 15 characters long that represents the symbol
     * @param uniqueUD for the client's use
     *
     *
     */
    /**
     *
     * @param symbolID code, 15 characters long that represents the symbol
     * @param uniqueID for the client's use
     * @param modifiers use keys from ModifiersTG or ModifiersUnits.
     * @param Coordinates
     * @throws RendererException NULL is a valid value if you have no set
     * modifiers
     */
    public MilStdSymbol(String symbolID, String uniqueID, ArrayList<Point2D.Double> Coordinates, Map<String, String> modifiers) {
        this(symbolID, uniqueID, Coordinates, modifiers, true);
    }

    /**
     *
     * @param symbolID code, 15 characters long that represents the symbol
     * @param uniqueID for the client's use
     * @param modifiers use keys from ModifiersTG or ModifiersUnits.
     * @param Coordinates
     * @param keepUnitRatio - default TRUE
     * @throws RendererException NULL is a valid value if you have no set
     * modifiers
     */
    public MilStdSymbol(String symbolID, String uniqueID, ArrayList<Point2D.Double> Coordinates, Map<String, String> modifiers, Boolean keepUnitRatio) {

        if (modifiers == null) {
            _Properties = new HashMap<String, String>();
        } else {
            _Properties = modifiers;
        }

        if (_Properties.containsKey(MilStdAttributes.Renderer) == true
                || _Properties.containsKey(MilStdAttributes.Renderer.toLowerCase()) == true) {
            isPluginSymbol = true;
        }

        _UUID = uniqueID;
        setCoordinates(Coordinates);

        // Set the given symbol id
        setSymbolID(symbolID);

        // Set up default line and fill colors based on affiliation
        setLineColor(SymbolUtilities.getLineColorOfAffiliation(_symbolID));
        //if(SymbolUtilities.isWarfighting(_symbolID))
        if (SymbolUtilities.hasDefaultFill(_symbolID)) {
            setFillColor(SymbolUtilities.getFillColorOfAffiliation(_symbolID));
        }
                        //if(SymbolUtilities.isNBC(_symbolID) && !(SymbolUtilities.isDeconPoint(symbolID)))
        //    setFillColor(SymbolUtilities.getFillColorOfAffiliation(_symbolID));
        setKeepUnitRatio(keepUnitRatio);

        setSymbologyStandard(RendererSettings.getInstance().getSymbologyStandard());

        _DrawAffiliationModifierAsLabel = RendererSettings.getInstance().getDrawAffiliationModifierAsLabel();

        _UseLineInterpolation = RendererSettings.getInstance().getUseLineInterpolation();

        int outlineWidth = RendererSettings.getInstance().getSinglePointSymbolOutlineWidth();
        if (outlineWidth > 0 && SymbolUtilities.isTacticalGraphic(symbolID)) {
            this.setOutlineEnabled(true, outlineWidth);
            this.setOutlineColor(SymbolDraw.getIdealTextBackgroundColor(_LineColor));
        }
    }

    /**
     * Controls what symbols are supported. Set this before loading the
     * renderer.
     *
     * @param symbologyStandard Like
     * RendererSettings.Symbology_2525Bch2_USAS_13_14
     */
    public void setSymbologyStandard(int standard) {
        _SymbologyStandard = standard;
    }

    /**
     * Current symbology standard
     *
     * @return symbologyStandard Like
     * RendererSettings.Symbology_2525Bch2_USAS_13_14
     */
    public int getSymbologyStandard() {
        return _SymbologyStandard;
    }

    public String getAltitudeMode() {
        return _AltitudeMode;
    }

    public void setAltitudeMode(String value) {
        _AltitudeMode = value;
    }

    public boolean getUseDashArray() {
        return _UseDashArray;
    }

    public void setUseDashArray(boolean value) {
        _UseDashArray = value;
    }
    
    public boolean getHideOptionalLabels() {
        return _HideOptionalLabels;
    }

    public void setHideOptionalLabels(boolean value) {
        _HideOptionalLabels = value;
    }

    public void setUseLineInterpolation(boolean value) {
        _UseLineInterpolation = value;
    }

    public boolean getUseLineInterpolation() {
        return _UseLineInterpolation;
    }

    /**
     * Determines how to draw the Affiliation Modifier. True to draw as modifier
     * label in the "E/F" location. False to draw at the top right corner of the
     * symbol
     */
    public void setDrawAffiliationModifierAsLabel(boolean value) {
        _DrawAffiliationModifierAsLabel = value;
    }

    /**
     * True to draw as modifier label in the "E/F" location. False to draw at
     * the top right corner of the symbol
     */
    public boolean getDrawAffiliationModifierAsLabel() {
        return _DrawAffiliationModifierAsLabel;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getModifierMap() {
        return _Properties;
    }

    /**
     *
     * @param modifiers
     */
    public void setModifierMap(Map<String, String> modifiers) {
        _Properties = modifiers;
    }

    /**
     *
     * @param modifier
     * @return
     */
    public String getModifier(String modifier) {
        if (_Properties.containsKey(modifier)) {
            return _Properties.get(modifier);
        } else {
            return getModifier(modifier, 0);
        }
    }

    /**
     *
     * @param modifier
     * @param value
     */
    public void setModifier(String modifier, String value) {
        if (value.equals("") == false) {
            if (!(modifier.equalsIgnoreCase(ModifiersTG.AM_DISTANCE)
                    || modifier.equalsIgnoreCase(ModifiersTG.AN_AZIMUTH)
                    || modifier.equalsIgnoreCase(ModifiersTG.X_ALTITUDE_DEPTH)) || this.getSymbolID().charAt(0) == 'W') {
                _Properties.put(modifier, value);
            } else {
                setModifier(modifier, value, 0);
            }
        }
    }

    /**
     *
     * @param modifier
     * @param index
     * @return
     */
    public String getModifier(String modifier, int index) {
        if (_Properties.containsKey(modifier)) {
            return _Properties.get(modifier);
        } else if (modifier.equalsIgnoreCase(ModifiersTG.AM_DISTANCE)
                || modifier.equalsIgnoreCase(ModifiersTG.AN_AZIMUTH)
                || modifier.equalsIgnoreCase(ModifiersTG.X_ALTITUDE_DEPTH)) {
            String value = String.valueOf(getModifier_AM_AN_X(modifier, index));
            if (value != null && !value.equalsIgnoreCase("null") && !value.equalsIgnoreCase("")) {
                return value;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     *
     * @param modifier
     * @param index
     * @return
     */
    public Double getModifier_AM_AN_X(String modifier, int index) {
        ArrayList<Double> modifiers = null;
        if (modifier.equalsIgnoreCase(ModifiersTG.AM_DISTANCE)) {
            modifiers = _AM_Distance;
        } else if (modifier.equalsIgnoreCase(ModifiersTG.AN_AZIMUTH)) {
            modifiers = _AN_Azimuth;
        } else if (modifier.equalsIgnoreCase(ModifiersTG.X_ALTITUDE_DEPTH)) {
            modifiers = _X_Altitude;
        } else {
            return null;
        }

        if (modifiers != null && modifiers.size() > index) {
            Double value = null;
            value = modifiers.get(index);
            if (value != null) {
                return value;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Modifiers must be added in order. No setting index 2 without first
     * setting index 0 and 1. If setting out of order is attempted, the value
     * will just be added to the end of the list.
     *
     * @param modifier
     * @param value
     * @param index
     */
    public void setModifier(String modifier, String value, int index) {
        if (value.equals("") == false) {
            if (!(modifier.equalsIgnoreCase(ModifiersTG.AM_DISTANCE)
                    || modifier.equalsIgnoreCase(ModifiersTG.AN_AZIMUTH)
                    || modifier.equalsIgnoreCase(ModifiersTG.X_ALTITUDE_DEPTH))) {
                _Properties.put(modifier, value);
            } else {
                Double dblValue = Double.valueOf(value);
                if (dblValue != null) {
                    setModifier_AM_AN_X(modifier, dblValue, index);
                }
            }
        }
    }

    public void setModifier_AM_AN_X(String modifier, Double value, int index) {
        if ((modifier.equalsIgnoreCase(ModifiersTG.AM_DISTANCE)
                || modifier.equalsIgnoreCase(ModifiersTG.AN_AZIMUTH)
                || modifier.equalsIgnoreCase(ModifiersTG.X_ALTITUDE_DEPTH))) {
            ArrayList<Double> modifiers = null;
            if (modifier.equalsIgnoreCase(ModifiersTG.AM_DISTANCE)) {
                if (_AM_Distance == null) {
                    _AM_Distance = new ArrayList<Double>();
                }
                modifiers = _AM_Distance;
            } else if (modifier.equalsIgnoreCase(ModifiersTG.AN_AZIMUTH)) {
                if (_AN_Azimuth == null) {
                    _AN_Azimuth = new ArrayList<Double>();
                }
                modifiers = _AN_Azimuth;
            } else if (modifier.equalsIgnoreCase(ModifiersTG.X_ALTITUDE_DEPTH)) {
                if (_X_Altitude == null) {
                    _X_Altitude = new ArrayList<Double>();
                }
                modifiers = _X_Altitude;
            }
            if (index + 1 > modifiers.size()) {
                modifiers.add(value);
            } else {
                modifiers.set(index, value);
            }
        }
    }

    public ArrayList<Double> getModifiers_AM_AN_X(String modifier) {
        if (modifier.equalsIgnoreCase(ModifiersTG.AM_DISTANCE)) {
            return _AM_Distance;
        } else if (modifier.equalsIgnoreCase(ModifiersTG.AN_AZIMUTH)) {
            return _AN_Azimuth;
        } else if (modifier.equalsIgnoreCase(ModifiersTG.X_ALTITUDE_DEPTH)) {
            return _X_Altitude;
        }

        return null;
    }

    public void setModifiers_AM_AN_X(String modifier, ArrayList<Double> modifiers) {
        if (modifier.equalsIgnoreCase(ModifiersTG.AM_DISTANCE)) {
            _AM_Distance = modifiers;
        } else if (modifier.equalsIgnoreCase(ModifiersTG.AN_AZIMUTH)) {
            _AN_Azimuth = modifiers;
        } else if (modifier.equalsIgnoreCase(ModifiersTG.X_ALTITUDE_DEPTH)) {
            _X_Altitude = modifiers;
        }
    }

    /**
     *
     * @param value
     */
    public void setFillColor(Color value) {
        _FillColor = value;
    }

    /**
     *
     * @return
     */
    public Color getFillColor() {
        return _FillColor;
    }

    /**
     *
     * @param value
     */
    public void setTextColor(Color value) {
        _TextColor = value;
    }

    /**
     *
     * @return
     */
    public Color getTextColor() {
        return _TextColor;
    }

    /**
     *
     * @param value
     */
    public void setTextBackgroundColor(Color value) {
        _TextBackgroundColor = value;
    }

    /**
     *
     * @return
     */
    public Color getTextBackgroundColor() {
        return _TextBackgroundColor;
    }

    /**
     *
     * @param value
     */
    public void setFillStyle(TexturePaint value) {
        _FillStyle = value;
    }

    /**
     *
     * @return
     */
    public TexturePaint getFillStyle() {
        return _FillStyle;
    }
    /**
     * 
     * @param value 
     */
    public void setPatternFillType(int value) {
        _PatternFillType = value;
    }
    /**
     * 
     * @return 
     */
    public int getPatternFillType()
    {
        return _PatternFillType;
    }    
        /**
     *
     * @param value
     */
    public void setUsePatternFill(boolean value) {
        _usePatternFill = value;
    }

    /**
     *
     * @return
     */
    public boolean getUsePatternFill() {
        return _usePatternFill;
    }

    /**
     *
     * @param value
     */
    public void setLineWidth(int value) {
        _LineWidth = value;
    }

    /**
     *
     * @return
     */
    public int getLineWidth() {
        return _LineWidth;
    }

    /**
     *
     * @param value
     */
    public void setLineColor(Color value) {
        _LineColor = value;
    }

    /**
     *
     * @return
     */
    public Color getLineColor() {
        return _LineColor;
    }

    /**
     *
     * @param value
     */
    public void setIconColor(Color value) {
        _IconColor = value;
    }

    /**
     *
     * @return
     */
    public Color getIconColor() {
        return _IconColor;
    }

    /**
     * determines if we outline the symbol
     *
     * @param value
     */
    public void setOutlineEnabled(boolean value) {
        _Outline = value;
        if (value) {
            _OutLineWidth = 1;
        } else {
            _OutLineWidth = 0;
        }
        if (_OutLineColor == null) {
            _OutLineColor = SymbolDraw.getIdealTextBackgroundColor(_LineColor);
        }
    }

    /**
     * determines if we outline the symbol
     *
     * @param value true to outline symbol
     * @param width outline width
     */
    public void setOutlineEnabled(boolean value, int width) {
        _Outline = value;
        if (width > 0) {
            _OutLineWidth = width;
        }
        if (_OutLineColor == null) {
            _OutLineColor = SymbolDraw.getIdealTextBackgroundColor(_LineColor);
        }
    }

    public boolean getOutlineEnabled() {
        return _Outline;
    }

    public int getOutlineWidth() {
        return _OutLineWidth;
    }

    /**
     * if null, renderer will white or black for the outline based on the color
     * of the symbol. Otherwise, it will used the passed color value.
     *
     * @param value
     */
    public void setOutlineColor(Color value) {
        _OutLineColor = value;
    }

    public Color getOutlineColor() {
        return _OutLineColor;
    }

    /**
     * rotation in DEGREES!
     */
    public void setRotation(double value) {
        _Rotation = value;
    }

    /**
     * rotation in DEGREES!
     *
     * @return
     */
    public double getRotation() {
        return _Rotation;
    }

    /**
     * Extra value for client. defaults to null. Not used for rendering by
     * JavaRenderer
     *
     * @param value
     */
    public void setTag(Object value) {
        _Tag = value;
    }

    /**
     * Extra value for client. defaults to null. Not used for rendering by
     * JavaRenderer
     *
     * @return
     */
    public Object getTag() {
        return _Tag;
    }

    /**
     *
     * @param value
     */
    public void setCoordinates(ArrayList<Point2D.Double> value) {
        _Coordinates = value;
    }

    /**
     *
     * @return
     */
    public ArrayList<Point2D.Double> getCoordinates() {
        return _Coordinates;
    }

    /**
     * Shapes that represent the symbol modifiers
     *
     * @param value ArrayList<Shape>
     */
    public void setModifierShapes(ArrayList<ShapeInfo> value) {
        _ModifierShapes = value;
    }

    /**
     * Shapes that represent the symbol modifiers
     *
     * @return
     */
    public ArrayList<ShapeInfo> getModifierShapes() {
        return _ModifierShapes;
    }

    /**
     * the java shapes that make up the symbol
     *
     * @param value ArrayList<ShapeInfo>
     */
    public void setSymbolShapes(ArrayList<ShapeInfo> value) {
        _SymbolShapes = value;
    }

    /**
     * the java shapes that make up the symbol
     *
     * @return
     */
    public ArrayList<ShapeInfo> getSymbolShapes() {
        return _SymbolShapes;
    }

    /**
     * The Symbol Id of the MilStdSymbol.
     *
     * @return
     */
    public String getSymbolID() {
        return _symbolID;
    }

    /**
     * Unique ID of the Symbol. For client use.
     *
     * @return
     */
    public String getUUID() {
        return _UUID;
    }

    /**
     * Unique ID of the Symbol. For client use.
     *
     * @param ID
     */
    public void setUUID(String ID) {
        _UUID = ID;
    }

    /**
     * Sets the Symbol ID for the symbol. Should be a 15 character string from
     * the milstd.
     *
     * @param value
     * @throws RendererException
     */
    public void setSymbolID(String value) {

        if (isPluginSymbol == false) {
            String current = _symbolID;

            try {
                //set symbolID
                if (value != null && !value.equals("") && !current.equals(value)) {
                    _symbolID = value;
                }

                //if hostile and specific TG, need to set 'N' to "ENY"
                if (SymbolUtilities.getAffiliation(value).equals("H")) {
                    String basicID = SymbolUtilities.getBasicSymbolID(value);
                    if (SymbolUtilities.isObstacle(basicID) || //any obstacle
                            basicID.equals("G*M*NZ----****X") ||//ground zero
                            basicID.equals("G*M*NEB---****X") ||//biological
                            basicID.equals("G*M*NEC---****X"))//chemical )
                    {
                        this.setModifier(ModifiersTG.N_HOSTILE, "ENY");
                    }
                }

                            // <editor-fold defaultstate="collapsed" desc="Old validity check">
                            /* //used to do hardcore check and not even create milstdSymbol
                 //if we couldn't draw it.

                 if(value != null && !value.equals("") && !current.equals(value))
                 {
                 value = SymbolUtilities.ReconcileSymbolID(value);

                 current = value;
                 }
                 if(SymbolUtilities.isTacticalGraphic(current) ||
                 SymbolUtilities.isEngineeringOverlayGraphic(current) )
                 {
                 if(SymbolDefTable.getInstance().HasSymbolDef(SymbolUtilities.getBasicSymbolID(current)))
                 {
                 _symbolID = current;
                 }
                 else
                 {
                 ErrorLogger.LogMessage("MilStdSymbol", "setSymbolID", value + " is not a valid symbol ID.");
                 }

                 }
                 else if(SymbolUtilities.isWarfighting(current))
                 {
                 if(UnitDefTable.getInstance().HasUnitDef(SymbolUtilities.getBasicSymbolID(current)))
                 {
                 _symbolID = current;
                 }
                 else
                 {
                 ErrorLogger.LogMessage("MilStdSymbol", "setSymbolID", value + " is not a valid symbol ID.");
                 }
                 }//*/
                            // </editor-fold>
            }// End try
            catch (Exception e) {
                // Log Error
                ErrorLogger.LogException("MilStdSymbol", "setSymbolID" + " - Did not fall under TG or FE", e);
            }
        } else//plugin, don't alter
        {
            _symbolID = value;
        }
    }	// End set SymbolID

    /**
     * Used to pass segment data in and out as a group. Mostly used for data
     * storage.
     */
    private Map getSegmentData() {
        return null;//this[SEGMENT_DATA] as Dictionary;
    }

    /**
     * @private
     */
    private void setSegmentData(Map value) {
			// Do not check to see if segmentData already equals value, after we set this once it always will since
        //	segmentData and value point to the same place in memory, and we always want to update
        //	segmentData so any properties binded to it get updated
        //this[SEGMENT_DATA] = value;
        //_Properties.put(SEGMENT_DATA, value);
        // NOTE: 9/24/09 - Temporarily comment out
			/*switch(geometry)
         {
         case "line":
         // We have to create a new instance of segmentData, otherwise we are only able to set
         //	segmentData once, I am guessing because after the first time segmentData
         //	points to the same place in memory as this.segmentData so segmentData does not think it
         //	needs to execute. If segmentData does not execute, binded properties do not get updated
         this.segmentData = new Dictionary();
         this.segmentData = value;
         break;
         }

         // if MilStdSymbol is not in an "updating" state, dispatch an "UPDATED" DataCacheEntryEvent
         dispatchUpdatedEvent();*/
    }

    /**
     * Used to determine which editor to bring up for a particular symbol. If
     * the value is null, the default symbol editor will be used.
     */
//		/**
//		 * The URN of the MilStdSymbol.
//		 */
//		public String getUrn()
//		{
//                    return (String)_Properties.get(URN);
//		}
//
//		/**
//		 * @private
//		 */
//		public void setUrn(String value)
//		{
//                    _Properties.put(URN, value);
//		}
//
//		/**
//		 * The UIC of the MilStdSymbol.
//		 */
//		public String getUic()
//		{
//                    return (String)_Properties.get(UIC);
//		}
//
//		/**
//		 * @private
//		 */
//		public void setUic(String value)
//		{
//                    _Properties.put(UIC, value);
//		}
//
//		/**
//		 * Get/Sets what angle, in degrees, the symbol should be rotated at from True North. If the value has not been
//		 * set, it will return 0.
//		 */
//
//		private int getAngleOfRotation()
//		{
//                    return (Integer)_Properties.get(ANGLE_OF_ROTATION);
//		}
//
//		/**
//		 * @private
//		 */
//		private void setAngleOfRotation(int value)
//		{
//                    _Properties.put(ANGLE_OF_ROTATION, value);
//		}
    /**
     *
     * @return a rectangle of the extent (pixels) of the symbol (not including
     * modifiers) and null if the shape collections wasn't created. TODO: only
     * works for single points. need to update for multipoints
     */
    public Rectangle getSymbolExtent() {
        Rectangle bounds = null;
        Rectangle temp = null;
        if (_SymbolShapes != null && _SymbolShapes.size() > 0) {
            if (SymbolUtilities.isWarfighting(_symbolID)) {
                for (int i = 0; i < _SymbolShapes.size(); i++) {
                    if (_SymbolShapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_FILL
                            || _SymbolShapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_FRAME
                            || _SymbolShapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_OUTLINE) {
                        if (bounds == null) {
                            bounds = _SymbolShapes.get(i).getBounds();
                        } else {
                            temp = _SymbolShapes.get(i).getBounds();
                            bounds = bounds.union(temp);
                        }
                    }
                }

                if (bounds == null) {
                    for (int i = 0; i < _SymbolShapes.size(); i++) {
                        if (_SymbolShapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_SYMBOL1
                                || _SymbolShapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_SYMBOL2) {
                            if (bounds == null) {
                                bounds = _SymbolShapes.get(i).getBounds();
                            } else {
                                bounds = bounds.union(_SymbolShapes.get(i).getBounds());
                            }
                        }
                    }
                }

            } else if (SymbolUtilities.isTacticalGraphic(_symbolID)) {
                //bounds = _SymbolShapes.get(0).getBounds();
                for (int i = 0; i < _SymbolShapes.size(); i++) {
                    if (_SymbolShapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_TG_SP_FRAME
                            || _SymbolShapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_TG_SP_FILL
                            || _SymbolShapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_TG_SP_OUTLINE) {
                        if (bounds == null) {
                            bounds = _SymbolShapes.get(i).getBounds();
                        } else {
                            temp = _SymbolShapes.get(i).getBounds();
                            bounds = bounds.union(temp);
                        }
                    }
                }
            } else {
                bounds = _SymbolShapes.get(0).getBounds();
            }

        }

        return bounds;
    }

    /**
     *
     * @return a rectangle of the extent (pixels) of the symbol (including
     * display modifiers) and null if the shape collections wasn't created.
     */
    public Rectangle getSymbolExtentWithDisplayModifiers() {
        Rectangle bounds = null;

        if (_SymbolShapes != null && _SymbolShapes.size() > 0) {
            bounds = _SymbolShapes.get(0).getBounds();
            int symbolCount = _SymbolShapes.size();

            for (int lcv = 1; lcv < symbolCount; lcv++) {
                bounds = bounds.union(_SymbolShapes.get(lcv).getBounds());
            }

        }

        return bounds;
    }

    /**
     *
     * @return a rectangle of the extent (pixels) of the symbol (including
     * display & label modifiers) and null if the shape collections wasn't
     * created.
     */
    public Rectangle getSymbolExtentFull() {

        Rectangle bounds = null;
        Rectangle temp = null;
        ShapeInfo siTemp = null;
        int lineWidth = 0;
        try {

            if (_LineWidth > 0) {
                lineWidth = _LineWidth;
            }
            if (_SymbolShapes != null && _SymbolShapes.size() > 0) {
                siTemp = _SymbolShapes.get(0);
                bounds = siTemp.getBounds();
                Shape sTemp = null;

                int symbolCount = _SymbolShapes.size();

                for (int lcv = 1; lcv < symbolCount; lcv++) {
                    siTemp = _SymbolShapes.get(lcv);
                    temp = siTemp.getBounds();

                    //System.out.println("temp: " + bounds.toString());
                    bounds = bounds.union(temp);
                    //System.out.println("union: " + bounds.toString());
                }

            }

            if (_ModifierShapes != null && _ModifierShapes.size() > 0) {
                int symbolCount2 = _ModifierShapes.size();

                for (int lcv2 = 0; lcv2 < symbolCount2; lcv2++) {
                    temp = _ModifierShapes.get(lcv2).getBounds();

                    bounds = bounds.union(temp);

                }

            }
        } catch (Exception exc) {
            ErrorLogger.LogException("MilStdSymbol", "getFullSymbolExtent", exc);
        }

        return bounds;
    }

    /**
     *
     * @return a rectangle of the extent (pixels) of the symbol (including
     * display & label modifiers) and null if the shape collections wasn't
     * created.
     * @deprecated
     */
    public Rectangle getPictureExtent() {

        Rectangle bounds = null;
        Rectangle temp = null;

        try {

            if (_SymbolShapes != null && _SymbolShapes.size() > 0) {
                bounds = _SymbolShapes.get(0).getBounds();
                int symbolCount = _SymbolShapes.size();

                for (int lcv = 1; lcv < symbolCount; lcv++) {
                    temp = _SymbolShapes.get(lcv).getBounds();
                    // System.out.println("temp: " + bounds.toString());
                    bounds = bounds.union(temp);
                    //System.out.println("union: " + bounds.toString());
                }

            }

            if (_ModifierShapes != null && _ModifierShapes.size() > 0) {
                int symbolCount2 = _ModifierShapes.size();

                for (int lcv2 = 0; lcv2 < symbolCount2; lcv2++) {
                    temp = _ModifierShapes.get(lcv2).getBounds();

                    bounds = bounds.union(temp);

                }

            }
        } catch (Exception exc) {
            ErrorLogger.LogException("MilStdSymbol", "getFullSymbolExtent", exc);
        }

        return bounds;
    }

    /**
     * Provides a hit test on the shapes that makeup the symbol. Does not
     * include modifiers
     *
     * @param point in pixels
     * @return
     */
    public Boolean HitTest(Point2D point) {
        Rectangle2D rect = null;
        double hitBuffer = 0;

        try {
            if (_SymbolShapes != null && _SymbolShapes.size() > 0) {
                Shape symbol = null;
                BasicStroke sTemp = null;
                int symbolCount = _SymbolShapes.size();

                if (symbolCount > 0) {
                    ShapeInfo siTemp = null;

                    for (int lcv = 0; lcv < symbolCount; lcv++) {
                        siTemp = _SymbolShapes.get(lcv);

                        if (siTemp.getStroke() instanceof BasicStroke) {
                            sTemp = (BasicStroke) siTemp.getStroke();
                        }
                        if (sTemp != null) {
                            hitBuffer = sTemp.getLineWidth();
                            rect = new Rectangle2D.Double(point.getX() - hitBuffer, point.getY() - hitBuffer, hitBuffer + hitBuffer, hitBuffer + hitBuffer);
                        } else {
                            rect = new Rectangle2D.Double(point.getX() - 4, point.getY() - 4, 8.0, 8.0);
                        }

                        if (siTemp.getShapeType() == ShapeInfo.SHAPE_TYPE_TG_SP_FRAME
                                || siTemp.getShapeType() == ShapeInfo.SHAPE_TYPE_TG_SP_FILL
                                || siTemp.getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_FILL
                                || siTemp.getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_FRAME
                                || siTemp.getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_SYMBOL1
                                || siTemp.getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_SYMBOL2
                                || (siTemp.getShapeType() == ShapeInfo.SHAPE_TYPE_POLYLINE && _FillColor != null && _FillColor.getAlpha() > 150)) {
                            if (siTemp.getShape() != null) {
                                symbol = siTemp.getShape();
                                if (siTemp.getAffineTransform() != null) {
                                    symbol = siTemp.getAffineTransform().createTransformedShape(symbol);
                                }
                            } else {
                                symbol = siTemp.getBounds();

                            }

                            if (symbol.intersects(rect)) {
                                return true;
                            }

                        } else if (siTemp.getShapeType() == ShapeInfo.SHAPE_TYPE_POLYLINE) {
                            if (siTemp.getShape() != null) {
                                symbol = siTemp.getShape();
                                if (siTemp.getAffineTransform() != null) {
                                    symbol = siTemp.getAffineTransform().createTransformedShape(symbol);
                                }
                            } else {   //shouldn't get here for polylines
                                symbol = siTemp.getBounds();
                            }

                                //fill either doesn't exist or has too low an alpha value
                            //want a hit only if it intersects the line, not contained
                            //by the shape
                            Path2D gp = null;
                            if (symbol instanceof Path2D) {
                                gp = (Path2D) symbol;

                                PathIterator itr = gp.getPathIterator(null);
                                double[] coords = new double[6];
                                itr.currentSegment(coords);
                                Point2D start = new Point2D.Double(coords[0], coords[1]);

                                while (itr.isDone() == false) {
                                    itr.currentSegment(coords);
                                    itr.next();
                                }

                                Point2D end = new Point2D.Double(coords[0], coords[1]);
                                itr = null;
                                itr = gp.getPathIterator(null);
                                if (symbol.intersects(rect) == true
                                        && symbol.contains(rect) == false) {
                                    if (start.equals(end)) {
                                        return true;//polygon, points are valid
                                    } else {
                                            //not a polygon, check what would
                                        //be the closing line if it were
                                        //a polygon
                                        Line2D tempLine = new java.awt.geom.Line2D.Double(start, end);
                                        if (tempLine.intersects(rect) == true
                                                && tempLine.contains(rect) == false) {
                                            return false;
                                        } else {
                                            return true;
                                        }
                                    }

                                } else {
                                    return false;
                                }

                            } else if (symbol.intersects(rect) == true
                                    && symbol.contains(rect) == false) {
                                return true;
                            }

                        }
                    }
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException("MilStdSymbol", "HitTest", exc);
        }
        return false;
    }

    /**
     * size height & width of the square that will contain the symbol (modifiers
     * may be outside of this square). Only applies to force elements (units).
     * If KeepUnitRatio is set, Symbols will be drawn with respect to each
     * other. Unknown unit is the all around biggest, neutral unit is the
     * smallest. if size is 35, neutral would be (35/1.5)*1.1=25.7
     *
     * @param size
     */
    public void setUnitSize(int size) {
        _UnitSize = size;
    }

    public int getUnitSize() {
        return _UnitSize;
    }

    /**
     * Setting tactical graphic single points to a specific size can ruin their
     * scale with respect to each other. A mine with direction should be taller
     * than a regular mine. but if you specify a 35 pixel size, the mine might
     * look fine but the mine with direction would be squished to fit in the
     * same space. By applying a scale, we can grow or shrink the tactical
     * single points in a consistent manner.
     *
     * @param scale
     */
    public void setScale(double scale) {
        _scale = scale;
    }

    public double getScale() {
        return _scale;
    }

    /**
     * If KeepUnitRatio is set AND Unit Size > 0, Symbols will be drawn with
     * respect to each other. Unknown unit is the all around biggest, neutral
     * unit is the smallest. if size is 35
     *
     * If Unit Size = 0, units will have proper ratio relative to each other.
     *
     * @param value
     */
    public void setKeepUnitRatio(Boolean value) {
        _KeepUnitRatio = value;
    }

    public Boolean getKeepUnitRatio() {
        return _KeepUnitRatio;
    }

    public void set_WasClipped(boolean value) {
        _wasClipped = value;
    }

    public boolean get_WasClipped() {
        return _wasClipped;
    }
    
    
    /**
     * Determines if the symbol has integral or modifier/amplifier text that would
     * be impacted if the maps is zoomed in or out after initial draw.
     * @return 0=not sensitive, 1=slightly little zoom in sensitive, 2=zoom in sensitive, 3=zoom in/out sensitive
     */
    public int isTextScaleSensitive()
    {
        ArrayList<ShapeInfo> modifiers = this.getModifierShapes();
        if(_Properties == null)
            return 0;//no scale sensitive text
        if (_Properties.isEmpty())
            return 0;
        else if(_symbolID.startsWith("G"))
        {
            String id = SymbolUtilities.getBasicSymbolID(_symbolID);
            SymbolDef sd = SymbolDefTable.getInstance().getSymbolDef(id, this.getSymbologyStandard());
            if(sd != null)
            {
                int dr = sd.getDrawCategory();
                switch (dr)
                {
                    case SymbolDef.DRAW_CATEGORY_ARROW:
                        if(_Properties.containsKey(ModifiersTG.W_DTG_1) ||
                                _Properties.containsKey(ModifiersTG.W1_DTG_2))
                            return 3;
                        else
                            return 0;

                    case SymbolDef.DRAW_CATEGORY_LINE://Linear Targets (2407##)
                        if(id.startsWith("G*F*LT"))
                        {
                            if(_Properties.containsKey(ModifiersTG.T1_UNIQUE_DESIGNATION_2))
                                return 3;
                            else if(_Properties.containsKey(ModifiersTG.T_UNIQUE_DESIGNATION_1))
                                return 2;
                            else
                                return 0;    
                        }
                        break;
                    case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE:
                        if(modifiers != null && modifiers.size() > 1)
                            return 3;
                        else
                            return 0;
                    case SymbolDef.DRAW_CATEGORY_CIRCULAR_PARAMETERED_AUTOSHAPE:
                        if(modifiers != null && modifiers.size() > 1)
                            return 3;
                        else
                            return 0;

                    default:
                        break;
                }
            }

            switch (id)
            {

                //A Little Zoom in sensitive (1)
                case "G*G*GLP---****X"://Phase line, only 5% sensitive
                case "G*G*DLF---****X"://Forward Edge of Battle, only 5% sensitive
                case "G*S*LCM---****X"://Moving Convoy
                case "G*S*LCH---****X"://Halted Convoy
                    return 1;

                //Zoom in sensitive (2)
                case "G*G*GLL---****X"://Light Line
                case "G*G*OLF---****X"://Final Coordination Line
                case "G*G*OLL---****X"://Limit of Advance
                case "G*G*OLT---****X"://Line of Departure
                case "G*G*OLC---****X"://Line of Departure / Line of Contact
                case "G*G*OLP---****X"://Probable Line of Deployment
                case "G*G*SLB---****X"://Bridgehead Line
                case "G*G*SLH---****X"://Holding Line
                case "G*G*SLR---****X"://Release Line
                case "G*G*SAN---****X"://Named Area of Interest Line (NAI)
                case "G*S*LRM---****X"://Main Supply Route (MSR)
                case "G*S*LRO---****X"://One Way Traffic
                case "G*S*LRW---****X"://Two Way Traffic
                case "G*S*LRT---****X"://Alternating Traffic
                case "G*S*LRA---****X"://Alternate Supply Route (ASR)
                    return 2;
                case "G*G*GLF---****X"://FLOT
                case "G*G*GLC---****X"://LOC
                    if(SymbolUtilities.getAffiliation(_symbolID).equals("H"))
                        return 2;
                    else
                        return 1;

                    //Very Zoom in/out sensitive (multi-line text) (3)
                    //friendly and more than 1 text
                    //Hostile and more than 3 text assuming ENY is present
                case "G*G*GLB---****X"://boundary line
                    if(_Properties.containsKey(ModifiersTG.T_UNIQUE_DESIGNATION_1) ||
                            _Properties.containsKey(ModifiersTG.T1_UNIQUE_DESIGNATION_2))
                        return 3;
                    else
                        return 0;


                case "G*G*GAY---****X"://Limited Access Area if sector 1 modifier present
                    if(_Properties.containsKey(ModifiersTG.H_ADDITIONAL_INFO_1))
                        return 3;
                    else
                        return 0;


                //Labels all contained in area but can drift away from each-other or overlap
                case "G*G*GAG---****X": //Generic
                case "G*G*AAH---****X": //High-Density Airspace Control Zone
                case "G*G*AAR---****X": //Restricted Operations Zone (ROZ)
                case "G*G*AAM---****X": //Missile Engagement Zone (MEZ)
                case "G*G*AAML--****X": //Low (Altitude) Missile Engagement Zone (LOMEZ)
                case "G*G*AAMH--****X": //High (Altitude) Missile Engagement Zone (HIMEZ)
                case "G*G*AAF---****X": //Short Range Air Defense Engagement Zone (SHORADEZ)
                case "G*G*AAW---****X": //Weapons Free Zone
                case "G*F*ACAI--****X": //Airspace Coordination Area (ACA) - Irregular
                case "G*F*ACFI--****X": //Free Fire Area (FFA) - Irregular
                case "G*F*ACNI--****X": //No Fire Area (NFA) - Irregular
                case "G*F*ACRI--****X": //Restricted Fire Area (RFA) - Irregular
                case "G*F*ATS---****X": //Smoke
                case "G*F*ACSI--****X"://Fire Support Area - Irregular
                case "G*F*AZII--****X": //Artillery Target Intelligence Zone (ATI), - Irregular
                case "G*F*AZXI--****X": //Call For Fire Zone (CFFZ) - Irregular
                case "G*F*AZCI--****X": //Censor Zone, - Irregular
                case "G*F*AZFI--****X": //Critical Friendly Zone (CFZ), - Irregular
                case "G*F*ACDI--****X": //Dead Space Area (DA), - Irregular
                case "G*F*ACEI--****X": //Sensor Zone, Irregular
                case "G*F*ACBI--****X": //Target Build-up Area, Irregular
                case "G*F*ACVI--****X": //Target Value Area, Irregular
                case "G*F*ACZI--****X": //Zone of Responsibility, Irregular
                //case "G*F*ACT---****X": //Terminally Guided Munition Footprint (TGMF)
                case "G*F*AKBI--****X": //Blue Kill Box, Irregular
                case "G*F*AKPI--****X": //Purple Kill Box, Irregular
                case "G*M*OGF---****X": //Obstacle Free Zone
                case "G*M*BCL---****X": //Lane
                case "G*S*AD----****X": //Detainee Holding Area
                case "G*S*AE----****X": //Enemy Prisoner of War Holding Area
                case "G*S*AR----****X": //Forward Arming and Refueling Point (FARP)
                case "G*S*AH----****X": //Refugee Holding Area
                case "G*S*ASR---****X": //Regimental Support Area (RSA)
                case "G*G*ALC---****X": //Air Corridor
                case "G*G*ALM---****X": //MRR
                case "G*G*ALS---****X": //SAAFR
                case "G*G*ALU---****X": //UA
                case "G*G*ALL---****X": //LLTR
                    if(modifiers != null && modifiers.size()>1)
                        return 3;
                    else
                        return 0;

                case "G*F*LCF---****X"://Fire Support Coordination Line (FSCL)
                case "G*F*LCC---****X"://Coordinated Fire Line (CFL)
                case "G*F*LCN---****X"://No Fire Line
                case "G*F*LCR---****X"://Restrictive Fire Line
                    if(_Properties.containsKey(ModifiersTG.W_DTG_1) ||
                            _Properties.containsKey(ModifiersTG.W1_DTG_2))
                        return 3;
                    else
                        return 2;

                case "G*F*LCM---****X"://Munition Flight Path
                case "G*T*L-----****X"://Delay
                    if(_Properties.containsKey(ModifiersTG.W_DTG_1) ||
                            _Properties.containsKey(ModifiersTG.W1_DTG_2))
                        return 2;
                    else
                        return 0;


                default://No Scale Sensitive text
                    return 0;
            }
        }

        return 0;
    }

    /**
     * Checks if the symbol has features that make it scale aware and would require a refresh
     * on zooming in or out.
     * @return 0=No,1=arrowheads,2=decoratedLines,3=patternFills
     */
    public int isSymbolScaleSensitive()
    {
        //return SymbolUtilities.isScaleAware(this._symbolID);
        String id = SymbolUtilities.getBasicSymbolID(_symbolID);

        switch (id)
        {
            //ArrowHead or smaller detail
            case "G*G*OAF---****X"://Attack By Fire
            case "G*G*OAS---****X"://Support By Fire
            case "G*G*GAS---****X"://Search Area/Reconnaissance Area
            case "G*G*SLA---****X"://Ambush
            case "G*G*OLKA--****X"://Airborne/Aviation
            case "G*G*OLKGM-****X"://Direction of Main attack
            case "G*G*OLKGS-****X"://Direction of Supporting attack
            case "G*F*LT----****X"://Linear Target
            case "G*F*LTS---****X"://Linear Smoke Target
            case "G*F*LTF---****X"://Final Protective Fire
            case "G*T*T-----****X"://Disrupt
            case "G*M*OET---****X"://Turn
            case "G*M*BDE---****X"://Obstacle Bypass Easy
            case "G*M*BDD---****X"://Obstacle Bypass Difficult
            case "G*M*BDI---****X"://Obstacle Bypass Impossible
            case "G*M*BCB---****X"://Bridge or Gap?
            case "G*M*BCA---****X"://Assault Crossing?
            case "G*M*OS----****X"://Abatis?
            case "G*M*BCL---****X"://?Lane?
            case "G*M*BCF---****X"://Ferry
            case "G*M*BCR---****X"://Raft Site
            case "G*T*H-----****X"://Breach
            case "G*T*Y-----****X"://Bypass
            case "G*T*C-----****X"://Canalize
            case "G*T*X-----****X"://Clear
            case "G*T*L-----****X"://Delay
            case "G*M*OED---****X"://Disrupt
            case "G*T*A-----****X"://Follow and Assume
            case "G*T*AS----****X"://Follow and Support
                //case "G*T*O-----****X"://Occupy, details relative to size
                //case "G*T*P-----****X"://Penetrate, details relative to size
                //case "G*T*R-----****X"://Relief in Place (RIP), details relative to size
                //case "G*T*M-----****X"://Retire/Retirement, details relative to size
            case "G*T*S-----****X"://Secure, details relative to size
            case "G*T*UC----****X"://Cover, details relative to size
            case "G*T*UG----****X"://Guard, details relative to size
            case "G*T*US----****X"://Screen, details relative to size
            case "G*T*Z-----****X"://Seize, details relative to size
            case "G*T*W-----****X"://Withdraw, details relative to size
            case "G*T*WP----****X"://Withdraw under pressure, details relative to size
                return 1;//arrowhead

            //Decorated Lines
            case "G*G*GLF---****X"://FLOT
            case "G*G*GLC---****X"://Line of Contact
                //case 140500://?Principal Direction of Fire?
            case "G*G*GAF---****X"://Fortified Area
            case "G*G*SAE---****X"://Encirclement
            case "G*M*SP----****X"://Strong Point
            case "G*G*DABP--****X"://Battle Position Prepared (P) but not Occupied
            case "G*T*J-----****X"://Contain
            case "G*T*Q-----****X"://Retain
            case "G*M*OGB---****X"://Obstacle Belt
            case "G*M*OGL---****X"://Obstacle Line
            case "G*M*OGZ---****X"://Obstacle Zone
            case "G*M*OGF---****X"://Obstacle Free Area
            case "G*M*OGR---****X"://Obstacle Restricted Area
                //case "G*MPOEF---****X"://Fix?
            case "G*G*PY----****X"://Mined Area, Fenced
            case "G*M*OHO---****X"://Overhead Wire
            case "G*M*OADU--****X"://Ditch Under Construction
            case "G*M*OADC--****X"://Ditch Completed
            case "G*M*OAR---****X"://Ditch Reinforced
            case "G*M*OAW---****X"://Antitank Wall
            case "G*M*OWU---****X"://Wire Obstacles, Unspecified
            case "G*M*OWS---****X"://Wire Obstacles, Single Fence
            case "G*M*OWD---****X"://Wire Obstacles, Double Fence
            case "G*M*OWA---****X"://Wire Obstacles, Double Apron Fence
            case "G*M*OWL---****X"://Wire Obstacles, Low Wire Fence
            case "G*M*OWH---****X"://Wire Obstacles, High Wire Fence
            case "G*M*OWCS--****X"://Wire Obstacles, Single Concertina
            case "G*M*OWCD--****X"://Wire Obstacles, Double Strand Concertina
            case "G*M*OWCT--****X"://Wire Obstacles, Triple Strand Concertina
            case "G*M*SL----****X"://Fortified Line
            case "G*S*LCM---****X"://Moving Convoy
            case "G*S*LCH---****X"://Halted Convoy
            case "G*S*LRO---****X"://MSR One Way Traffic
            case "G*S*LRW---****X"://MSR Two Way Traffic
            case "G*S*LRT---****X"://MSR Alternating Traffic
            case "G*T*F-----****X"://Fix
                //case 341500://Isolate, most detail contained inside
                //case 342600://Cordon and Knock, most detail contained inside
                //case 342700://Cordon and Search, most detail contained inside
                return 2;//decoration

            //Areas with Pattern Fill
            case "G*G*GAY---****X"://Limited Access Area
            case "G*G*AAW---****X"://Weapons Free Zone
            case "G*F*ACNI--****X"://NFA Irregular
            case "G*F*ACNR--****X"://NFA Rectangular
            case "G*F*ACNC--****X"://NFA Circular
            case "G*M*NB----****X"://Bio Contaminated Area
            case "G*M*NC----****X"://Chem Contaminated Area
            case "G*M*NR----****X"://Rad Contaminated Area
                if(!this.getUsePatternFill())
                    return 3;//pattern fill
                else
                    return 0;
            default:
                return 0;
        }
    }    

    /**
     * returns just the symbol as an ImageInfo object.
     *
     * @return
     */
    public ImageInfo toImageInfo() {
        ImageInfo returnVal = null;
        returnVal = ConvertShapesToImageInfo(BufferedImage.TYPE_INT_ARGB, false);
        return returnVal;
    }

    /**
     * returns just the symbol as an ImageInfo object.
     *
     * @param type Such as BufferedImage.TYPE_INT_ARGB
     * @return
     */
    public ImageInfo toImageInfo(int type) {
        ImageInfo returnVal = null;
        returnVal = ConvertShapesToImageInfo(type, false);
        return returnVal;
    }

    /**
     * returns just the symbol as an ImageInfo object.
     *
     * @param type Such as BufferedImage.TYPE_INT_ARGB
     * @param addBuffer add 1 pixel border.
     * @return
     */
    public ImageInfo toImageInfo(int type, Boolean addBuffer) {
        ImageInfo returnVal = null;
        returnVal = ConvertShapesToImageInfo(type, addBuffer);
        return returnVal;
    }

    /**
     * returns just the symbol as an ImageInfo object.
     *
     * @param addBuffer add 1 pixel border.
     * @return
     */
    public ImageInfo toImageInfo(Boolean addBuffer) {
        ImageInfo returnVal = null;
        returnVal = ConvertShapesToImageInfo(BufferedImage.TYPE_INT_ARGB, addBuffer);
        return returnVal;
    }

    /**
     * takes a collection of ShapeInfo objects and converts it into an ImageInfo
     * object.
     *
     * @param type Such as BufferedImage.TYPE_INT_ARGB
     * @param addBuffer default true, adds a 1 pixel buffer around symbol.
     * @return
     */
    private ImageInfo ConvertShapesToImageInfo(int type, Boolean addBuffer) {

        ImageInfo returnVal = null;
        int widthBuffer = 0;//2;
        int heightBuffer = 0;//2;
        int offsetX = 0;//1;
        int offsetY = 0;//1;

        try {

            if (this._SymbolShapes != null && this._SymbolShapes.size() > 0) {
                Rectangle bounds = null;
                Rectangle boundsFull = null;
                //int symbolCount = shapes.size();

                bounds = getSymbolExtent();
                boundsFull = getSymbolExtentFull();

                    //System.out.println("bounds: " + bounds.toString());
                //System.out.println("full bounds: " + boundsFull.toString());
                    //getSymbolExtent() seems to properly account for
                //anti-aliasing, below may no longer be necessary 
                //add some room for anti-aliasing
                if (addBuffer) {
                    widthBuffer += 2;//2;
                    heightBuffer += 2;//2;
                    //half of buffer so image is centered
                    offsetX += 1;//1;
                    offsetY += 1;//1;
                }

                    //anti-aliasing gets cut off for some reason on mobility
                //no choice here, must add buffer
                if (SymbolUtilities.isMobility(_symbolID)) {
                    //add some room for anti-aliasing
                    widthBuffer += 4;
                    heightBuffer += 2;
                    //half of buffer so image is centered
                    offsetX += 2;
                    offsetY += 0;
                }
                /*//Echelons and modifiers getting properly accounted for.
                 // no longer need this check.
                 else if(SymbolUtilities.getEchelonText(_symbolID).equals("")==false)
                 {
                 //add some room for anti-aliasing
                 widthBuffer += 0;
                 heightBuffer += 2;
                 //half of buffer so image is centered
                 offsetX += 0;
                 offsetY += 2;
                 }//*/

                //BufferedImage image = new BufferedImage(Math.round(boundsFull.width+boundsFull.x) + widthBuffer, Math.round(boundsFull.height+boundsFull.y) + heightBuffer, type);
                BufferedImage image = new BufferedImage(Math.round(boundsFull.width) + widthBuffer, Math.round(boundsFull.height) + heightBuffer, type);
                Graphics2D g2d = (Graphics2D) image.createGraphics();
                    //set antialiasing. if not, buffers & offsets should be 0
                //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                //AffineTransform oldTransform = g2d.getTransform(); 

                int newX = boundsFull.x - offsetX;
                int newY = boundsFull.y - offsetY;
                    //g2d.translate(-(newX), -(newY));

                    //test
//                    g2d.setColor(Color.white);
//                    g2d.fill(boundsFull);
                    //draw symbol to bufferedImage
                //SymbolDraw.Draw(this, g2d, 0, 0);
                SymbolDraw.Draw(this, g2d, -(newX), -(newY));

                    //System.out.println("image dimensions: width " + String.valueOf(image.getWidth()) + " height " + String.valueOf(image.getHeight()));
                //create ImageInfo which holds image and coords to draw at
                int centerX = 0;
                int centerY = 0;

                if (SymbolUtilities.isWarfighting(_symbolID)) {

                    if (SymbolUtilities.isHQ(_symbolID) && RendererSettings.getInstance().getCenterOnHQStaff() == true) {
                        //HQ, we must center on the bottom of the staff

                        centerX = bounds.x - boundsFull.x;//0;//offsetX;
                        //centerY = offsetY + boundsFull.height;// + offsetY;
                        ///////////////////////////////////////////////////////////////////////////////////////////////
                        String affiliation = this.getSymbolID().substring(1, 2);
                        if(affiliation.equals("F") ||
                                affiliation.equals("A") ||
                                affiliation.equals("D") ||
                                affiliation.equals("M") ||
                                affiliation.equals("J") ||
                                affiliation.equals("K") ||
                                affiliation.equals("N") ||
                                affiliation.equals("L"))
                        {
                            centerY = (int)((bounds.y - boundsFull.y + (bounds.height / 2)) + bounds.height * 1.5);
                        }
                        else
                        {
                            centerY = (int)((bounds.y - boundsFull.y + (bounds.height / 2)) + bounds.height);
                        }
                        
                        
                    } else {//else center of the symbol in the image
                        centerX = offsetX + bounds.x - boundsFull.x + (bounds.width / 2);
                        centerY = offsetY + bounds.y - boundsFull.y + (bounds.height / 2);
                    }
                    returnVal = new ImageInfo(image, newX, newY, centerX, centerY, new Rectangle2D.Double(bounds.getX() - newX, bounds.getY() - newY, bounds.getWidth(), bounds.getHeight()));
                } else//is tactical graphic
                {
                    ShapeInfo temp = null;

                    for (int i = 0; i < _SymbolShapes.size(); i++) {
                        temp = _SymbolShapes.get(i);
                        if (temp.getShapeType() == ShapeInfo.SHAPE_TYPE_TG_SP_FRAME) {
                            i = _SymbolShapes.size();
                        }
                    }

                    Boolean isMultiPoint = false;
                    if (temp.getGlyphPosition() != null) {
                        centerX = offsetX + (int) temp.getGlyphPosition().getX() - boundsFull.x;
                        centerY = offsetY + (int) temp.getGlyphPosition().getY() - boundsFull.y;
                    } else//multipoint
                    {
                        centerX = offsetX + (int) boundsFull.getWidth() / 2;
                        centerY = offsetY + (int) boundsFull.getHeight() / 2;
                        isMultiPoint = true;
                    }

                    if (isMultiPoint) {
                        bounds = boundsFull;
                    }
                    returnVal = new ImageInfo(image, newX, newY, centerX, centerY, new Rectangle2D.Double(bounds.getX() - newX, bounds.getY() - newY, bounds.getWidth(), bounds.getHeight()));
                }

                g2d.dispose();

            } else {
                return null;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException("MilStdSymbol", "ConvertShapesToImageInfo()", exc);
        }
        return returnVal;
    }

}
