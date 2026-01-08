/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaTacticalRenderer;

import java.util.ArrayList;
import JavaLineArray.POINT2;
import java.awt.Color;
import java.awt.Font;
import java.awt.TexturePaint;
import ArmyC2.C2SD.Utilities.ErrorLogger;
import ArmyC2.C2SD.Utilities.RendererException;
import JavaLineArray.TacticalLines;

/**
 * A class to encapsulate the tactical graphic object. Many of the properties
 * correspond to a client MilStdSymbol object.
 *
 * @author Michael Deutch
 */
public class TGLight {

    public ArrayList<POINT2> LatLongs;
    private static final String _className = "TGLight";

    public ArrayList<POINT2> get_LatLongs() {
        return LatLongs;
    }

    public void set_LatLongs(ArrayList<POINT2> value) {
        LatLongs = value;
    }
    public ArrayList<POINT2> Pixels;

    public ArrayList<POINT2> get_Pixels() {
        return Pixels;
    }

    public void set_Pixels(ArrayList<POINT2> value) {
        Pixels = value;
    }
    public ArrayList<Modifier2> modifiers;

    public ArrayList<Modifier2> get_Modifiers() {
        return modifiers;
    }

    public void set_Modifiers(ArrayList<Modifier2> value) {
        modifiers = value;
    }
    TexturePaint tp = null;

    public void set_TexturePaint(TexturePaint value) {
        tp = value;
    }

    public TexturePaint get_TexturePaint() {
        return tp;
    }

    boolean maskOff;

    public TGLight() {
    }
    private Font font;

    public void set_Font(Font value) {
        font = value;
    }

    public Font get_Font() {
        return font;
    }
    private int lineType;

    public void set_LineType(int value) {
        lineType = value;
    }

    public int get_LineType() {
        return lineType;
    }
    private int lineStyle;

    public void set_LineStyle(int value) {
        lineStyle = value;
    }

    public int get_LineStyle() {
        return lineStyle;
    }
    private Color lineColor;

    public Color get_LineColor() {
        return lineColor;
    }

    public void set_LineColor(Color value) {
        lineColor = value;
    }
    private int fillStyle;

    public int get_FillStyle() {
        return fillStyle;
    }

    public void set_Fillstyle(int value) {
        fillStyle = value;
    }
    
    private int patternFillType = 0;
    
    public int get_PatternFillType() {
        return patternFillType;
    }

    public void set_PatternFillType(int value) {
        patternFillType = value;
    }
    
    private Color fillColor;

    public Color get_FillColor() {
        return fillColor;
    }

    public void set_FillColor(Color value) {
        fillColor = value;
    }
    private Color fontBackColor = Color.WHITE;

    //private Color fontBackColor=RendererSettings.getInstance().getLabelBackgroundColor();
    public Color get_FontBackColor() {
        return fontBackColor;
    }

    public void set_FontBackColor(Color value) {
        fontBackColor = value;
    }
    private Color textColor;

    public Color get_TextColor() {
        return textColor;
    }

    public void set_TextColor(Color value) {
        textColor = value;
    }
    private int lineThickness;

    public int get_LineThickness() {
        return lineThickness;
    }

    public void set_LineThickness(int value) {
        lineThickness = value;
    }
    private String t = "";

    public String get_Name() {
        if (visibleModifiers) {
            return t;
        } else {
            return "";
        }
    }
    private String client = "";

    public String get_Client() {
        return client;
    }

    public void set_client(String value) {
        client = value;
    }
//    private boolean cs=false;
//    public boolean get_cs()
//    {
//        return cs;
//    }
//    public void set_cs(boolean value)
//    {
//        cs=value;
//    }

    public void set_Name(String value) {
        t = value;
    }
    private String t1 = "";

    public String get_T1() {
        //if(visibleModifiers || clsUtility.IsChange1Area(lineType, null)==true)
        //    return t1;
        //else
        //return "";
        if (visibleModifiers) {
            return t1;
        } else {
            return "";
        }
    }

    public void set_T1(String value) {
        t1 = value;
    }
    private String h = "";

    public String get_H() {
        if (visibleModifiers || lineType == TacticalLines.RECTANGULAR) {
            return h;
        } else {
            return "";
        }
    }

    public void set_H(String value) {
        h = value;
    }

    public String get_Location() {
        if (visibleModifiers) {
            return y;
        } else {
            return "";
        }
    }

    public void set_Location(String value) {
        y = value;
    }
    private String h1 = "";

    public String get_H1() {
        if (visibleModifiers) {
            return h1;
        } else {
            return "";
        }
    }

    public void set_H1(String value) {
        h1 = value;
    }
    //location
    private String y = "";

    private String n = "ENY";

    public String get_N() {
        return n;
    }

    public void set_N(String value) {
        n = value;
    }

    private String h2 = "";

    public String get_H2() {
        if (visibleModifiers || lineType == TacticalLines.RECTANGULAR) {
            return h2;
        } else {
            return "";
        }
    }

    public void set_H2(String value) {
        h2 = value;
    }
    private String w = "";

    public String get_DTG() {
        if (visibleModifiers) {
            return w;
        } else {
            return "";
        }
    }

    public void set_DTG(String value) {
        w = value;
    }
    private String w1 = "";

    public String get_DTG1() {
        if (visibleModifiers) {
            return w1;
        } else {
            return "";
        }
    }

    public void set_DTG1(String value) {
        w1 = value;
    }

    private String affiliation;

    public String get_Affiliation() {
        return affiliation;
    }

    public void set_Affiliation(String value) {
        affiliation = value;
    }
    private String echelon;

    protected String get_Echelon() {
        return echelon;
    }

    public void set_Echelon(String value) {
        echelon = value;
    }
    private String echelonSymbol = "";

    protected String get_EchelonSymbol() {
        return echelonSymbol;
    }

    public void set_EcheclonSymbol(String value) {
        echelonSymbol = value;
    }
    private String symbolId;

    public String get_SymbolId() {
        return symbolId;
    }
    private String status;

    public String get_Status() {
        if (symbolId.equalsIgnoreCase("BS_AREA--------")) {
            return "P";
        }
        return status;
    }

    public void set_Status(String value) {
        status = value;
    }

    /**
     * Sets tactical graphic properties based on a string value, either a
     * generic name or the 15 character Mil-Std-2525 symbol code.
     *
     * @param value
     */
    public void set_SymbolId(String value) {
        try {
            symbolId = value;
            char letter;
            String s;
            if (symbolId.length() == 15) //MilStd2525 15 character symbol code
            {
                status = symbolId.substring(3, 4);
                if (status.equals("A") && !value.equalsIgnoreCase("BS_AREA--------")) {
                    lineStyle = 1;    //dashed
                }
                //set the affiliation from the symbol id
                affiliation = symbolId.substring(1, 2);

                //set the echelon from the symbol id
                echelon = symbolId.substring(11, 12);
            } else if (symbolId.length() >= 20) {
                String setA = symbolId.substring(0, 10);
                String symbolSet = setA.substring(4, 6);
                if (symbolSet.equalsIgnoreCase("25")) {

                    affiliation = setA.substring(2, 4);
                    if (affiliation.equalsIgnoreCase("03")) {
                        affiliation = "F";
                    } else if (affiliation.equalsIgnoreCase("06")) {
                        affiliation = "H";
                    }
                    status = setA.substring(6, 7);
                    if (status.equalsIgnoreCase("0")) {
                        status = "P";
                    } else if (status.equalsIgnoreCase("1")) {
                        status = "A";
                    }
                    if (status.equalsIgnoreCase("A")) {
                        lineStyle = 1;    //dashed
                    }
                    echelon = setA.substring(8);
                    if (echelon.equalsIgnoreCase("11")) {
                        echelon = "A";
                    } else if (echelon.equalsIgnoreCase("12")) {
                        echelon = "B";
                    } else if (echelon.equalsIgnoreCase("13")) {
                        echelon = "C";
                    } else if (echelon.equalsIgnoreCase("14")) {
                        echelon = "D";
                    } else if (echelon.equalsIgnoreCase("15")) {
                        echelon = "E";
                    } else if (echelon.equalsIgnoreCase("16")) {
                        echelon = "F";
                    } else if (echelon.equalsIgnoreCase("17")) {
                        echelon = "G";
                    } else if (echelon.equalsIgnoreCase("18")) {
                        echelon = "H";
                    } else if (echelon.equalsIgnoreCase("21")) {
                        echelon = "I";
                    } else if (echelon.equalsIgnoreCase("22")) {
                        echelon = "J";
                    } else if (echelon.equalsIgnoreCase("23")) {
                        echelon = "K";
                    } else if (echelon.equalsIgnoreCase("24")) {
                        echelon = "L";
                    } else if (echelon.equalsIgnoreCase("M")) {
                        echelon = "M";
                    }
                }
            }
            //build the echelon symbol from the echelon
            //regarless of symbolId.length
            if(echelon != null)
            {
                if (echelon.equals("M")) //REGION
                {
                    echelonSymbol = "XXXXXX";
                } else if (echelon.equals("L")) //FRONT
                {
                    echelonSymbol = "XXXXX";
                } else if (echelon.equals("K")) //ARMY
                {
                    echelonSymbol = "XXXX";
                } else if (echelon.equals("J")) //CORPS
                {
                    echelonSymbol = "XXX";
                } else if (echelon.equals("I")) //DIVISION
                {
                    echelonSymbol = "XX";
                } else if (echelon.equals("H")) //BRIGADE
                {
                    echelonSymbol = "X";
                } else if (echelon.equals("G")) //REGIMENT
                {
                    echelonSymbol = "III";
                } else if (echelon.equals("F")) //BATTALION
                {
                    echelonSymbol = "II";
                } else if (echelon.equals("E")) //COMPANY
                {
                    echelonSymbol = "I";
                } else if (echelon.equals("D")) //PLATOON
                {
                    letter = (char) 9679;
                    s = Character.toString(letter);
                    echelonSymbol = s + s + s;
                } else if (echelon.equals("C")) //SECTION
                {
                    letter = (char) 9679;
                    s = Character.toString(letter);
                    echelonSymbol = s + s;
                } else if (echelon.equals("B")) //SQUAD
                {
                    letter = (char) 9679;
                    s = Character.toString(letter);
                    echelonSymbol = s;
                } else if (echelon.equals("A")) //GROUP
                {
                    letter = (char) 216;
                    echelonSymbol = Character.toString(letter);
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "set_SymbolId",
                    new RendererException("Failed inside set_SymbolId", exc));
        }
    }
    private boolean visibleModifiers;

    public void set_VisibleModifiers(boolean value) {
        visibleModifiers = value;
    }

    protected boolean get_VisibleModifiers() {
        return visibleModifiers;
    }
    private boolean visibleLabels;

    public void set_VisibleLabels(boolean value) {
        visibleLabels = value;
    }

    protected boolean get_VisibleLabels() {
        return visibleLabels;
    }
    int _SymbologyStandard = 0;

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
    boolean _useLineInterpolation = false;

    public boolean get_UseLineInterpolation() {
        return _useLineInterpolation;
    }

    public void set_UseLineInterpolation(boolean value) {
        _useLineInterpolation = value;
    }
    boolean _useDashArray = false;

    public boolean get_UseDashArray() {
        return _useDashArray;
    }

    public void set_UseDashArray(boolean value) {
        _useDashArray = value;
    }

    boolean _useHatchFill = false;

    public boolean get_UseHatchFill() {
        return _useHatchFill;
    }

    public void set_UseHatchFill(boolean value) {
        _useHatchFill = value;
    }
    
    private boolean _wasClipped = false;

    public void set_WasClipped(boolean value) {
        _wasClipped = value;
    }

    public boolean get_WasClipped() {
        return _wasClipped;
    }

    //boolean determines whether to add the range and azimuth modifiers for range fans
    private boolean _HideOptionalLabels = false;

    public boolean get_HideOptionalLabels() {
        return _HideOptionalLabels;
    }

    public void set_HideOptionalLabels(boolean value) {
        _HideOptionalLabels = value;
    }
}
