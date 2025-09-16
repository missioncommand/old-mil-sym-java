/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaTacticalRenderer;

import JavaLineArray.POINT2;
import JavaLineArray.ref;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.FontMetrics;
import JavaLineArray.TacticalLines;
import JavaLineArray.lineutility;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import JavaLineArray.Shape2;
import java.util.ArrayList;
import java.awt.Shape;
import java.awt.Polygon;

import java.awt.font.FontRenderContext;
import ArmyC2.C2SD.Utilities.ErrorLogger;
import ArmyC2.C2SD.Utilities.RendererException;
import ArmyC2.C2SD.Utilities.RendererSettings;
import ArmyC2.C2SD.Utilities.IPointConversion;
//import ArmyC2.C2SD.Utilities.SymbolUtilities;
import java.awt.geom.PathIterator;
import ArmyC2.C2SD.Utilities.ShapeInfo;
import JavaLineArray.Channels;
import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.font.TextLayout;
import java.util.HashMap;
import java.awt.geom.Line2D;
/*
 * This class handles everything having to do with text for a
 * tactical graphic. Note: labels are handled the same as text modifiers.
 * @author Michael Deutch
 * 
 */

public class Modifier2 {

    public POINT2[] textPath;
    public String textID;
    public String featureID;
    public String text;
    public int iteration;
    public int justify;
    public int type;
    public double lineFactor;
    public static final String _className = "Modifier2";
    public boolean isIntegral = false;
    public boolean fitsMBR = true;        //added 7-9-12 M. Deutch
    private static RendererSettings rendererSettings = RendererSettings.getInstance();

    Modifier2() {
        textPath = new POINT2[2];
    }
    //static methods and properties
    public static final int toEnd = 1;  //use both points
    public static final int aboveMiddle = 2;    //use both points
    public static final int area = 3;   //use one point
    public static final int screen = 4;   //use one point, screen, cover, guard points
    public static final int aboveEnd = 5;   //rev D mod to replace toEnd
    public static final double fillAlphaCanObscureText = 50d;

    private static boolean DoublesBack(POINT2 pt0, POINT2 pt1, POINT2 pt2) {
        boolean result = true;
        try {
            //theta = Math.atan2(y2 - y1, x2 - x1);
            double theta1 = Math.atan2(pt2.y - pt1.y, pt2.x - pt1.x);
            double theta0 = Math.atan2(pt0.y - pt1.y, pt0.x - pt1.x);
            double beta = Math.abs(theta0 - theta1);
            if (beta > 0.1) {
                result = false;
            }

        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "DoublesBack",
                    new RendererException("Failed inside DoublesBack", exc));
        }
        return result;
    }

    /**
     * Returns a generic label for the symbol per Mil-Std-2525
     *
     * @param tg
     * @return
     */
    private static String GetCenterLabel(TGLight tg) {
        //int linetype=tg.get_LineType();
        String label = "";
        try {
            switch (tg.get_LineType()) {
                case TacticalLines.SAAFR:
                    label = "SAAFR";
                    break;
                case TacticalLines.AC:
                    label = "AC";
                    break;
                case TacticalLines.SECURE:
                case TacticalLines.SEIZE:
                case TacticalLines.SEIZE_REVC:
                    label = "S";
                    break;
                case TacticalLines.RETAIN:
                    label = "R";
                    break;
                case TacticalLines.PENETRATE:
                    label = "P";
                    break;
                case TacticalLines.OCCUPY:
                    label = "O";
                    break;
                case TacticalLines.ISOLATE:
                    label = "I";
                    break;
                case TacticalLines.FIX:
                    label = "F";
                    break;
                case TacticalLines.DISRUPT:
                    label = "D";
                    break;
                case TacticalLines.CANALIZE:
                case TacticalLines.CLEAR:
                    label = "C";
                    break;
                case TacticalLines.BREACH:
                case TacticalLines.BYPASS:
                    label = "B";
                    break;
                case TacticalLines.CORDONKNOCK:
                    label = "C/K";
                    break;
                case TacticalLines.CORDONSEARCH:
                    label = "C/S";
                    break;
                case TacticalLines.UXO:
                    label = "UXO";
                    break;
                case TacticalLines.RETIRE:
                    label = "R";
                    break;
                case TacticalLines.BRDGHD:
                case TacticalLines.BRDGHD_GE:
                    //label="BRIDGEHEAD LINE";
                    label = "BL ";
                    break;
                case TacticalLines.HOLD:
                case TacticalLines.HOLD_GE:
                    //label="HOLDING LINE";
                    label = "HL ";
                    break;
                case TacticalLines.PL:
                    label = "PL ";
                    break;
                case TacticalLines.LL:
                    label = "LL";
                    break;
                case TacticalLines.SCREEN:
                case TacticalLines.SCREEN_REVC:
                    label = "S";
                    break;
                case TacticalLines.COVER:
                case TacticalLines.COVER_REVC:
                    label = "C";
                    break;
                case TacticalLines.GUARD:
                case TacticalLines.GUARD_REVC:
                    label = "G";
                    break;
                case TacticalLines.RIP:
                    label = "RIP";
                    break;
                case TacticalLines.WITHDRAW:
                    label = "W";
                    break;
                case TacticalLines.WDRAWUP:
                    label = "WP";
                    break;
                case TacticalLines.CATK:
                case TacticalLines.CATKBYFIRE:
                    label = "CATK";
                    break;
                case TacticalLines.FLOT:
                    label = "FLOT";
                    break;
                case TacticalLines.ASSY:
                    label = "AA";
                    break;
                case TacticalLines.EA:
                    label = "EA";
                    break;
                case TacticalLines.DZ:
                    label = "DZ";
                    break;
                case TacticalLines.EZ:
                    label = "EZ";
                    break;
                case TacticalLines.LZ:
                    label = "LZ";
                    break;
                case TacticalLines.PZ:
                    label = "PZ";
                    break;
                case TacticalLines.MRR:
                case TacticalLines.MRR_USAS:
                    label = "MRR";
                    break;
                case TacticalLines.UAV:
                    if (tg.getSymbologyStandard() == RendererSettings.Symbology_2525C) {
                        label = "UA";
                    } else {
                        label = "UAV";
                    }
                    break;
                case TacticalLines.UAV_USAS:
                    label = "UAV";
                    break;
                case TacticalLines.LLTR:
                    label = "LLTR";
                    break;
                case TacticalLines.ROZ:
                    label = "ROZ";
                    break;
                case TacticalLines.FAADZ:
                    //label = "FAADEZ";
                    if (tg.getSymbologyStandard() == RendererSettings.Symbology_2525C) {
                        label = "SHORADEZ";
                    } else {
                        label = "FAADEZ";
                    }
                    break;
                case TacticalLines.HIDACZ:
                    label = "HIDACZ";
                    break;
                case TacticalLines.MEZ:
                    label = "MEZ";
                    break;
                case TacticalLines.LOMEZ:
                    label = "LOMEZ";
                    break;
                case TacticalLines.HIMEZ:
                    label = "HIMEZ";
                    break;
                case TacticalLines.WFZ:
                    label = "WFZ";
                    break;
                case TacticalLines.DMA:
                    label = "M";
                    break;
                case TacticalLines.MINED:
                case TacticalLines.DMAF:
                    label = "M";
                    break;
                case TacticalLines.FEBA:
                    label = "FEBA";
                    break;
                case TacticalLines.PDF:
                    label = "(PDF)";
                    break;
                case TacticalLines.PNO:
                    label = "(P)";
                    break;
                case TacticalLines.EA1:
                    label = "EA ";
                    break;
                case TacticalLines.OBJ:
                    label = "OBJ ";
                    if (tg.get_Client().equalsIgnoreCase("ge")) {
                        label = "OBJ  ";
                    }
                    break;
                case TacticalLines.NAI:
                    label = "NAI";
                    break;
                case TacticalLines.TAI:
                    label = "TAI";
                    break;
                case TacticalLines.LINTGTS:
                    label = "SMOKE";
                    break;
                case TacticalLines.FPF:
                    label = "FPF";
                    break;
                case TacticalLines.ATKPOS:
                    label = "ATK";
                    break;
                case TacticalLines.FCL:
                    label = "FINAL CL";
                    break;
                case TacticalLines.LOA:
                    label = "LOA";
                    break;
                case TacticalLines.LOD:
                    label = "LD";
                    break;
                case TacticalLines.PLD:
                    label = "PLD";
                    break;
                case TacticalLines.DELAY:
                    label = "D";
                    break;
                case TacticalLines.RELEASE:
                    label = "RL";
                    break;
                case TacticalLines.SMOKE:
                    label = "SMOKE";
                    break;
                case TacticalLines.NFL:
                    label = "NFL";
                    break;
                case TacticalLines.MFP:
                    label = "MFP";
                    break;
                case TacticalLines.FSCL:
                    label = " FSCL";
                    break;
                case TacticalLines.CFL:
                    label = "CFL ";
                    break;
                case TacticalLines.RFL:
                    label = "RFL ";
                    break;
                case TacticalLines.AO:
                    label = "AO ";
                    break;
                case TacticalLines.BOMB:
                    label = "BOMB";
                    break;
                case TacticalLines.TGMF:
                    label = "TGMF";
                    break;
                case TacticalLines.FSA:
                    label = "FSA ";
                    break;
                case TacticalLines.FSA_CIRCULAR:
                case TacticalLines.FSA_RECTANGULAR:
                    label = "FSA";
                    break;
                case TacticalLines.ACA:
                case TacticalLines.ACA_CIRCULAR:
                case TacticalLines.ACA_RECTANGULAR:
                    label = "ACA";
                    break;
                case TacticalLines.FFA:
                case TacticalLines.FFA_CIRCULAR:
                case TacticalLines.FFA_RECTANGULAR:
                    label = "FFA";
                    break;
                case TacticalLines.NFA:
                case TacticalLines.NFA_CIRCULAR:
                case TacticalLines.NFA_RECTANGULAR:
                    label = "NFA";
                    break;
                case TacticalLines.RFA:
                case TacticalLines.RFA_CIRCULAR:
                case TacticalLines.RFA_RECTANGULAR:
                    label = "RFA";
                    break;
                case TacticalLines.ATI:
                case TacticalLines.ATI_CIRCULAR:
                case TacticalLines.ATI_RECTANGULAR:
                    label = "ATI ZONE";
                    break;
                case TacticalLines.PAA:
                case TacticalLines.PAA_CIRCULAR:
                case TacticalLines.PAA_RECTANGULAR:
                case TacticalLines.PAA_RECTANGULAR_REVC:
                    label = "PAA";
                    break;
                case TacticalLines.CFFZ:
                case TacticalLines.CFFZ_CIRCULAR:
                case TacticalLines.CFFZ_RECTANGULAR:
                    label = "CFF ZONE";
                    break;
                case TacticalLines.CFZ:
                case TacticalLines.CFZ_CIRCULAR:
                case TacticalLines.CFZ_RECTANGULAR:
                    label = "CF ZONE";
                    break;
                case TacticalLines.SENSOR:
                case TacticalLines.SENSOR_CIRCULAR:
                case TacticalLines.SENSOR_RECTANGULAR:
                    label = "SENSOR ZONE";
                    break;
                case TacticalLines.CENSOR:
                case TacticalLines.CENSOR_CIRCULAR:
                case TacticalLines.CENSOR_RECTANGULAR:
                    label = "CENSOR ZONE";
                    break;
                case TacticalLines.DA:
                case TacticalLines.DA_CIRCULAR:
                case TacticalLines.DA_RECTANGULAR:
                    label = "DA";
                    break;
                case TacticalLines.ZOR:
                case TacticalLines.ZOR_CIRCULAR:
                case TacticalLines.ZOR_RECTANGULAR:
                    label = "ZOR";
                    break;
                case TacticalLines.TBA:
                case TacticalLines.TBA_CIRCULAR:
                case TacticalLines.TBA_RECTANGULAR:
                    label = "TBA";
                    break;
                case TacticalLines.TVAR:
                case TacticalLines.TVAR_CIRCULAR:
                case TacticalLines.TVAR_RECTANGULAR:
                    label = "TVAR";
                    break;
                case TacticalLines.KILLBOXBLUE:
                case TacticalLines.KILLBOXBLUE_CIRCULAR:
                case TacticalLines.KILLBOXBLUE_RECTANGULAR:
                    label = "BKB";
                    break;
                case TacticalLines.KILLBOXPURPLE:
                case TacticalLines.KILLBOXPURPLE_CIRCULAR:
                case TacticalLines.KILLBOXPURPLE_RECTANGULAR:
                    label = "PKB";
                    break;
                case TacticalLines.MSR:
                    label = "MSR ";
                    break;
                case TacticalLines.ASR:
                    label = "ASR ";
                    break;
                case TacticalLines.ALT:
                case TacticalLines.ONEWAY:
                case TacticalLines.TWOWAY:
                    label = "MSR ";
                    break;
                case TacticalLines.LDLC:
                    label = "LD/LC";
                    break;
                case TacticalLines.AIRHEAD:
                    label = "AIRHEAD LINE";
                    break;
                case TacticalLines.BLOCK:
                case TacticalLines.BEARING:
                    label = "B";
                    break;
                case TacticalLines.ELECTRO:
                    label = "E";
                    break;
                case TacticalLines.ACOUSTIC:
                    label = "A";
                    break;
                case TacticalLines.TORPEDO:
                    label = "T";
                    break;
                case TacticalLines.OPTICAL:
                    label = "O";
                    break;
                case TacticalLines.FARP:
                    label = "FARP";
                    break;
                case TacticalLines.BSA:
                    label = "BSA";
                    break;
                case TacticalLines.DSA:
                    label = "DSA";
                    break;
                case TacticalLines.RSA:
                    label = "RSA";
                    break;
                case TacticalLines.CONTAIN:
                    label = "ENY";
                    break;
                case TacticalLines.OBSFAREA:
                    label = "FREE";
                    break;
                default:
                    break;
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.GetCenterLabel");
            ErrorLogger.LogException(_className, "GetCenterLabel",
                    new RendererException("Failed inside GetCenterLabel", exc));
        }
        return label;
    }
    //non CPOF clients using best fit need these accessors

    public POINT2[] get_TextPath() {
        return textPath;
    }

    protected void set_TextPath(POINT2[] value) {
        textPath = value;
    }

    protected void set_IsIntegral(boolean value) {
        isIntegral = value;
    }

    protected boolean get_IsIntegral() {
        return isIntegral;
    }

    /**
     * Creates and adds a new modifier to the tactical graphic modifers
     * collection
     *
     * @param tg
     * @param text
     * @param type
     * @param lineFactor factor above or below the line
     * @param startIndex starting client point for the modifier
     * @param endIndex ending client point for the modifier
     */
    private static void AddModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            int startIndex,
            int endIndex) {
        try {
            if (text == null || text.equals("")) {
                return;
            }
            //if(_isTextFlipped)
            //  lineFactor = -lineFactor;

            Modifier2 modifier = new Modifier2();
            if (text == null || text.equals("")) {
                return;
            }
            if (tg.Pixels == null || tg.Pixels.size() < 2) {
                return;
            }

            modifier.text = text;
            modifier.set_IsIntegral(false);
            //if(fillCanObscureText(tg))
            //modifier.set_IsIntegral(true);

            modifier.type = type;

            modifier.lineFactor = lineFactor;

            if (tg.Pixels.size() > endIndex) {
                modifier.textPath[0] = tg.Pixels.get(startIndex);
                modifier.textPath[1] = tg.Pixels.get(endIndex);
                tg.modifiers.add(modifier);
            }
        } catch (Exception exc) {
            //String err=e.getMessage();
            //clsUtility.WriteFile("Error in Modifier2.AddModifier");
            ErrorLogger.LogException(_className, "AddModifier",
                    new RendererException("Failed inside AddModifier", exc));
        }
        return;
    }

    private static void AddOffsetModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            int startIndex,
            int endIndex,
            double spaces,
            String rightOrLeft) {
        try {
            if (text == null || text.equals("")) {
                return;
            }
            //if(_isTextFlipped)
            //  lineFactor = -lineFactor;

            Modifier2 modifier = new Modifier2();
            if (text == null || text.equals("")) {
                return;
            }
            if (tg.Pixels == null || tg.Pixels.size() < 2) {
                return;
            }

            modifier.text = text;
            modifier.set_IsIntegral(false);

            modifier.type = type;

            modifier.lineFactor = lineFactor;

            if (tg.Pixels.size() > endIndex) {
                modifier.textPath[0] = tg.Pixels.get(startIndex);
                modifier.textPath[1] = tg.Pixels.get(endIndex);
                if (rightOrLeft != null) {
                    if (rightOrLeft.equals("left")) {
                        modifier.textPath[0].x -= spaces;
                        modifier.textPath[1].x -= spaces;
                    } else {
                        modifier.textPath[0].x += spaces;
                        modifier.textPath[1].x += spaces;
                    }
                }
                tg.modifiers.add(modifier);
            }
        } catch (Exception exc) {
            //String err=e.getMessage();
            //clsUtility.WriteFile("Error in Modifier2.AddModifier");
            ErrorLogger.LogException(_className, "AddOffsetModifier",
                    new RendererException("Failed inside AddOffsetModifier", exc));
        }
        return;
    }

    /**
     *
     * @param tg
     * @param text
     * @param type
     * @param lineFactor
     * @param ptStart
     * @param ptEnd
     */
    private static void AddModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            POINT2 ptStart,
            POINT2 ptEnd) {
        try {
            if (text == null || text.equals("")) {
                return;
            }
            //if(_isTextFlipped)
            //  lineFactor = -lineFactor;

            Modifier2 modifier = new Modifier2();
            if (text == null || text.equals("")) {
                return;
            }
            if (tg.Pixels == null || tg.Pixels.size() < 2) {
                return;
            }

            modifier.text = text;
            modifier.set_IsIntegral(false);
            //if(fillCanObscureText(tg))
            //modifier.set_IsIntegral(true);

            modifier.type = type;

            modifier.lineFactor = lineFactor;

            //if(tg.Pixels.size()>endIndex)
            //{
            modifier.textPath[0] = ptStart;
            modifier.textPath[1] = ptEnd;
            tg.modifiers.add(modifier);
            //}
        } catch (Exception exc) {
            //String err=e.getMessage();
            //clsUtility.WriteFile("Error in Modifier2.AddModifier");
            ErrorLogger.LogException(_className, "AddModifier",
                    new RendererException("Failed inside AddModifier", exc));
        }
        return;
    }

    private static void AddModifier2(TGLight tg,
            String text,
            int type,
            double lineFactor,
            POINT2 pt0,
            POINT2 pt1,
            boolean isIntegral) {
        try {
            if (text == null || text.equals("")) {
                return;
            }
            //if(fillCanObscureText(tg))
            //isIntegral=true;

            Modifier2 modifier = new Modifier2();
            if (text == null || text.equals("")) {
                return;
            }
            //if (tg.Pixels == null || tg.Pixels.size() == 0) {
            //  return;
            //}

            modifier.text = text;
            modifier.set_IsIntegral(isIntegral);

            modifier.type = type;

            //if(type==aboveMiddle)
            //  lineFactor*=factor;
            modifier.lineFactor = lineFactor;

            modifier.textPath[0] = pt0;//tg.Pixels.get(startIndex);
            modifier.textPath[1] = pt1;//tg.Pixels.get(endIndex);
            modifier.isIntegral = isIntegral;
            tg.modifiers.add(modifier);
        } catch (Exception exc) {
            //String err=e.getMessage();
            //clsUtility.WriteFile("Error in Modifier2.AddModifier");
            ErrorLogger.LogException(_className, "AddModifier",
                    new RendererException("Failed inside AddModifier", exc));
        }
        return;
    }

    private static void AddModifier2(TGLight tg,
            String text,
            int type,
            double lineFactor,
            POINT2 pt0,
            POINT2 pt1,
            boolean isIntegral,
            String modifierType) {
        try {
            if (text == null || text.equals("")) {
                return;
            }
            //if(fillCanObscureText(tg))
            //isIntegral=true;

            Modifier2 modifier = new Modifier2();
            if (text == null || text.equals("")) {
                return;
            }
            //if (tg.Pixels == null || tg.Pixels.size() == 0) {
            //  return;
            //}

            modifier.text = text;
            modifier.set_IsIntegral(isIntegral);

            modifier.type = type;

            //if(type==aboveMiddle)
            //  lineFactor*=factor;
            modifier.lineFactor = lineFactor;

            modifier.textPath[0] = pt0;//tg.Pixels.get(startIndex);
            modifier.textPath[1] = pt1;//tg.Pixels.get(endIndex);
            modifier.isIntegral = isIntegral;
            modifier.textID = modifierType;
            tg.modifiers.add(modifier);
        } catch (Exception exc) {
            //String err=e.getMessage();
            //clsUtility.WriteFile("Error in Modifier2.AddModifier");
            ErrorLogger.LogException(_className, "AddModifier",
                    new RendererException("Failed inside AddModifier", exc));
        }
        return;
    }

    private static void AddIntegralModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            int startIndex,
            int endIndex) {
        try {
            if (text == null || text.equals("")) {
                return;
            }

            Modifier2 modifier = new Modifier2();
            modifier.set_IsIntegral(true);

            if (text == null || text.equals("")) {
                return;
            }
            if (tg.Pixels == null || tg.Pixels.isEmpty()) {
                return;
            }

            modifier.text = text;

            modifier.type = type;

            //if(type==aboveMiddle)
            //  lineFactor*=factor;
            modifier.lineFactor = lineFactor;

            if (tg.Pixels.size() > endIndex) {
                modifier.textPath[0] = tg.Pixels.get(startIndex);
                modifier.textPath[1] = tg.Pixels.get(endIndex);
                tg.modifiers.add(modifier);
            }
        } catch (Exception exc) {
            //String err=e.getMessage();
            //clsUtility.WriteFile("Error in Modifier2.AddModifier");
            ErrorLogger.LogException(_className, "AddModifier",
                    new RendererException("Failed inside AddIntegralModifier", exc));
        }
        return;
    }

    private static void AddIntegralModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            int startIndex,
            int endIndex,
            Boolean isIntegral) {
        try {
            if (text == null || text.equals("")) {
                return;
            }

            //if(fillCanObscureText(tg))
            //isIntegral=true;
            Modifier2 modifier = new Modifier2();
            modifier.set_IsIntegral(isIntegral);

            if (text == null || text.equals("")) {
                return;
            }
            if (tg.Pixels == null || tg.Pixels.isEmpty()) {
                return;
            }

            modifier.text = text;

            modifier.type = type;

            modifier.lineFactor = lineFactor;

            if (tg.Pixels.size() > endIndex) {
                modifier.textPath[0] = tg.Pixels.get(startIndex);
                modifier.textPath[1] = tg.Pixels.get(endIndex);
                tg.modifiers.add(modifier);
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "AddModifier",
                    new RendererException("Failed inside AddIntegralModifier", exc));
        }
        return;
    }

    private static void AddIntegralModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            int startIndex,
            int endIndex,
            Boolean isIntegral,
            String modifierType) {
        try {
            if (text == null || text.equals("")) {
                return;
            }

            //if(fillCanObscureText(tg))
            //isIntegral=true;
            Modifier2 modifier = new Modifier2();
            modifier.set_IsIntegral(isIntegral);

            if (text == null || text.equals("")) {
                return;
            }
            if (tg.Pixels == null || tg.Pixels.isEmpty()) {
                return;
            }

            modifier.text = text;

            modifier.type = type;

            modifier.lineFactor = lineFactor;

            modifier.textID = modifierType;
            if (tg.Pixels.size() > endIndex) {
                modifier.textPath[0] = tg.Pixels.get(startIndex);
                modifier.textPath[1] = tg.Pixels.get(endIndex);
                tg.modifiers.add(modifier);
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "AddModifier",
                    new RendererException("Failed inside AddIntegralModifier", exc));
        }
        return;
    }

    /**
     * Creates and adds center modifiers for generic areas
     *
     * @param tg
     * @param text
     * @param type
     * @param lineFactor
     * @param pt0
     * @param pt1
     */
    private static void AddAreaModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            POINT2 pt0,
            POINT2 pt1) {
        try {
            if (text == null || text.equals("")) {
                return;
            }

            Modifier2 modifier = new Modifier2();
            modifier.text = text;
            modifier.set_IsIntegral(true);
            //if (text == null || text.equals(""))
            //{
            //    return;
            //}

            if (pt0 == null || pt1 == null) {
                return;
            }

            modifier.type = type;
            modifier.lineFactor = lineFactor;
            modifier.textPath[0] = pt0;
            modifier.textPath[1] = pt1;
            tg.modifiers.add(modifier);
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.AddAreaModifier");
            ErrorLogger.LogException(_className, "AddAreaModifier",
                    new RendererException("Failed inside AddAreaModifier", exc));
        }

        return;
    }

    /**
     * sets modifier.textId to the modifier type, e.g. label, T, T1, etc.
     *
     * @param tg
     * @param text
     * @param type
     * @param lineFactor
     * @param pt0
     * @param pt1
     * @param modifierType
     */
    private static void AddAreaModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            POINT2 pt0,
            POINT2 pt1,
            String modifierType) {
        try {
            if (text == null || text.equals("")) {
                return;
            }

            Modifier2 modifier = new Modifier2();
            modifier.text = text;
            modifier.set_IsIntegral(true);
            //if (text == null || text.equals(""))
            //{
            //    return;
            //}

            if (pt0 == null || pt1 == null) {
                return;
            }

            modifier.type = type;
            modifier.textID = modifierType;
            modifier.lineFactor = lineFactor;
            modifier.textPath[0] = pt0;
            modifier.textPath[1] = pt1;
            tg.modifiers.add(modifier);
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.AddAreaModifier");
            ErrorLogger.LogException(_className, "AddAreaModifier",
                    new RendererException("Failed inside AddAreaModifier", exc));
        }

        return;
    }

    /**
     * added to identify the modifier type (label, T, T1, W, etc) sets
     * modifier.textId to the modifier type passed
     *
     * @param tg
     * @param text
     * @param type
     * @param lineFactor
     * @param pt0
     * @param pt1
     * @param modifierType
     */
    private static void AddIntegralAreaModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            POINT2 pt0,
            POINT2 pt1,
            String modifierType) {
        try {
            if (text == null || text.equals("")) {
                return;
            }

            Modifier2 modifier = new Modifier2();
            modifier.set_IsIntegral(false);

            //if(fillCanObscureText(tg))
            //modifier.set_IsIntegral(true);
            modifier.text = text;
            if (text == null || text.equals("")) {
                return;
            }

            if (pt0 == null || pt1 == null) {
                return;
            }

            modifier.type = type;
            modifier.lineFactor = lineFactor;
            modifier.textPath[0] = pt0;
            modifier.textPath[1] = pt1;
            modifier.textID = modifierType;
            tg.modifiers.add(modifier);
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.AddAreaModifier");
            ErrorLogger.LogException(_className, "AddAreaModifier",
                    new RendererException("Failed inside AddAreaModifier", exc));
        }

        return;
    }

    private static void AddIntegralAreaModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            POINT2 pt0,
            POINT2 pt1,
            Boolean isIntegral) {
        try {
            if (text == null || text.equals("")) {
                return;
            }
            //if(fillCanObscureText(tg))
            //isIntegral=true;

            Modifier2 modifier = new Modifier2();
            modifier.set_IsIntegral(isIntegral);
            modifier.text = text;
            if (text == null || text.equals("")) {
                return;
            }

            if (pt0 == null || pt1 == null) {
                return;
            }

            modifier.type = type;
            modifier.lineFactor = lineFactor;
            modifier.textPath[0] = pt0;
            modifier.textPath[1] = pt1;
            tg.modifiers.add(modifier);
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.AddAreaModifier");
            ErrorLogger.LogException(_className, "AddAreaModifier",
                    new RendererException("Failed inside AddAreaModifier", exc));
        }

        return;
    }

    private static void AddIntegralAreaModifier(TGLight tg,
            String text,
            int type,
            double lineFactor,
            POINT2 pt0,
            POINT2 pt1,
            Boolean isIntegral,
            String modifierType) {
        try {
            if (text == null || text.equals("")) {
                return;
            }
            //if(fillCanObscureText(tg))
            //isIntegral=true;

            Modifier2 modifier = new Modifier2();
            modifier.set_IsIntegral(isIntegral);
            modifier.text = text;
            if (text == null || text.equals("")) {
                return;
            }

            if (pt0 == null || pt1 == null) {
                return;
            }

            modifier.type = type;
            modifier.lineFactor = lineFactor;
            modifier.textPath[0] = pt0;
            modifier.textPath[1] = pt1;
            modifier.textID = modifierType;
            tg.modifiers.add(modifier);
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.AddAreaModifier");
            ErrorLogger.LogException(_className, "AddAreaModifier",
                    new RendererException("Failed inside AddAreaModifier", exc));
        }

        return;
    }

    /**
     * Returns symbol MBR. Assumes points have been initialized with value of
     * 0th point
     *
     * @param tg the tactical graphic object
     * @param ptUl OUT - MBR upper left
     * @param ptUr OUT - MBR upper right
     * @param ptLr OUT - MBR lower right
     * @param ptLl OUT - MBR lower left
     */
    public static void GetMBR(TGLight tg,
            POINT2 ptUl,
            POINT2 ptUr,
            POINT2 ptLr,
            POINT2 ptLl) {
        try {
            int j = 0;
            double x = 0;
            double y = 0;
            ptUl.x = tg.Pixels.get(0).x;
            ptUl.y = tg.Pixels.get(0).y;
            ptUr.x = tg.Pixels.get(0).x;
            ptUr.y = tg.Pixels.get(0).y;
            ptLl.x = tg.Pixels.get(0).x;
            ptLl.y = tg.Pixels.get(0).y;
            ptLr.x = tg.Pixels.get(0).x;
            ptLr.y = tg.Pixels.get(0).y;
            for (j = 1; j < tg.Pixels.size(); j++) {
                x = tg.Pixels.get(j).x;
                y = tg.Pixels.get(j).y;
                if (x < ptLl.x) {
                    ptLl.x = x;
                    ptUl.x = x;
                }
                if (x > ptLr.x) {
                    ptLr.x = x;
                    ptUr.x = x;
                }
                if (y > ptLl.y) {
                    ptLl.y = y;
                    ptLr.y = y;
                }
                if (y < ptUl.y) {
                    ptUl.y = y;
                    ptUr.y = y;
                }
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.GetMBR");
            ErrorLogger.LogException(_className, "GetMBR",
                    new RendererException("Failed inside GetMBR", exc));
        }
    }

    private static void GetBoundaryMiddleSegment(TGLight tg, Graphics2D g2d, POINT2 pt0, POINT2 pt1) {
        int middleSegment = tg.Pixels.size() / 2 - 1;
        boolean tooShort = GetLineTooShort(tg, g2d);
        int stringWidthENY = 0;
        int stringWidthEchelonSymbol = 0;
        g2d.setFont(tg.get_Font());
        FontMetrics metrics = g2d.getFontMetrics();
        POINT2 midpt = lineutility.MidPointDouble(tg.Pixels.get(middleSegment), tg.Pixels.get(middleSegment + 1), 0);
        POINT2 ptTemp = null;
        double dist = 0;
        if (tooShort) {
            if (tg.get_Affiliation().equals("H")) {
                stringWidthENY = metrics.stringWidth(tg.get_N());
            }
            if (tg.get_EchelonSymbol() != null) {
                stringWidthEchelonSymbol = metrics.stringWidth(tg.get_EchelonSymbol());
            }
            dist = 1.5 * (2 * stringWidthENY + stringWidthEchelonSymbol);
            ptTemp = lineutility.ExtendAlongLineDouble(midpt, tg.Pixels.get(middleSegment), dist / 2);
            pt0.x = ptTemp.x;
            pt0.y = ptTemp.y;
            ptTemp = lineutility.ExtendAlongLineDouble(midpt, tg.Pixels.get(middleSegment + 1), dist / 2);
            pt1.x = ptTemp.x;
            pt1.y = ptTemp.y;
        } else {
            ptTemp = tg.Pixels.get(middleSegment);
            pt0.x = ptTemp.x;
            pt0.y = ptTemp.y;
            ptTemp = tg.Pixels.get(middleSegment + 1);
            pt1.x = ptTemp.x;
            pt1.y = ptTemp.y;
        }
    }

    private static boolean GetLineTooShort(TGLight tg, Graphics2D g2d) {
        boolean lineTooShort = false;
        try {
            int middleSegment = tg.Pixels.size() / 2 - 1;
            g2d.setFont(tg.get_Font());
            FontMetrics metrics = g2d.getFontMetrics();
            String echelonSymbol = null;
            int stringWidthEchelonSymbol = 0;
            int stringWidthENY = 0;

            POINT2 pt0 = tg.Pixels.get(middleSegment);
            POINT2 pt1 = tg.Pixels.get(middleSegment + 1);
            double dist = lineutility.CalcDistanceDouble(pt0, pt1);

            echelonSymbol = tg.get_EchelonSymbol();
            if (tg.get_Affiliation().equals("H")) {
                stringWidthENY = metrics.stringWidth(tg.get_N());
            }

            if (echelonSymbol != null) {
                stringWidthEchelonSymbol = metrics.stringWidth(echelonSymbol);
            }

            switch (tg.get_LineType()) {
                case TacticalLines.BOUNDARY:
                    if (dist < 1.5 * (stringWidthENY * 2 + stringWidthEchelonSymbol)) {
                        lineTooShort = true;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.GetMBR");
            ErrorLogger.LogException(_className, "GetBoundaryLineTooShort",
                    new RendererException("Failed inside GetBoundaryLineTooShort", exc));
        }
        return lineTooShort;
    }

    /**
     * Tests segment of a Boundary
     *
     * @param tg
     * @param g2d
     * @param middleSegment
     * @return
     */
    private static boolean GetBoundarySegmentTooShort(TGLight tg,
            Graphics2D g2d,
            int middleSegment) {
        boolean lineTooShort = false;
        try {
            //int middleSegment = tg.Pixels.size() / 2 - 1;
            g2d.setFont(tg.get_Font());
            FontMetrics metrics = g2d.getFontMetrics();
            String echelonSymbol = null;
            int stringWidthEchelonSymbol = 0;
            int stringWidthENY = 0;

            POINT2 pt0 = tg.Pixels.get(middleSegment);
            POINT2 pt1 = tg.Pixels.get(middleSegment + 1);
            double dist = lineutility.CalcDistanceDouble(pt0, pt1);

            echelonSymbol = tg.get_EchelonSymbol();
            if (tg.get_Affiliation() != null && tg.get_Affiliation().equals("H")) {
                stringWidthENY = metrics.stringWidth(tg.get_N());
            }
            if (echelonSymbol != null) {
                stringWidthEchelonSymbol = metrics.stringWidth(echelonSymbol);
            }

            int tWidth = 0, t1Width = 0;
            if (tg.get_Name() != null && !tg.get_Name().isEmpty()) {
                tWidth = metrics.stringWidth(tg.get_Name());
            }
            if (tg.get_T1() != null && !tg.get_T1().isEmpty()) {
                t1Width = metrics.stringWidth(tg.get_T1());
            }

            int totalWidth = stringWidthENY * 2 + stringWidthEchelonSymbol;
            if (totalWidth < tWidth) {
                totalWidth = tWidth;
            }
            if (totalWidth < t1Width) {
                totalWidth = t1Width;
            }

            switch (tg.get_LineType()) {
                case TacticalLines.BOUNDARY:
                    if (dist < 1.25 * (totalWidth)) {
                        lineTooShort = true;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.GetMBR");
            ErrorLogger.LogException(_className, "GetBoundaryLineTooShort",
                    new RendererException("Failed inside GetBoundaryLineTooShort", exc));
        }
        return lineTooShort;
    }

    /**
     * Handles the line breaks for Boundary
     *
     * @param tg
     * @param g2d
     */
    private static void AddBoundaryModifiers(TGLight tg,
            Graphics2D g2d,
            Rectangle2D clipBounds) {
        try {
            //FontMetrics metrics = g2d.getFontMetrics();
            int j = 0;
            double csFactor = 1d;
            //int middleSegment = tg.Pixels.size() / 2 - 1;
            Boolean foundSegment = false;
            POINT2 pt0 = null, pt1 = null, ptLast = null;
            double TLineFactor = 0, T1LineFactor = 0;
            String affiliation = tg.get_Affiliation();
            Boolean lineTooShort = false;
            if (tg.get_Client().equals("cpof3d")) {
                csFactor = 0.85d;
            }

            int middleSegment = getVisibleMiddleSegment(tg, clipBounds);

            for (j = 0; j < tg.Pixels.size() - 1; j++) {
                if (tg.get_Client().equalsIgnoreCase("ge")) {
                    if (j != middleSegment) {
                        continue;
                    }
                }

                pt0 = tg.Pixels.get(j);
                pt1 = tg.Pixels.get(j + 1);
                //GetBoundaryMiddleSegment(tg, g2d, pt0, pt1);
                if (pt0.x < pt1.x) {
                    TLineFactor = -1.3;
                    T1LineFactor = 1;
                } else if (pt0.x == pt1.x) {
                    if (pt1.y < pt0.y) {
                        TLineFactor = -1;
                        T1LineFactor = 1;
                    } else {
                        TLineFactor = 1;
                        T1LineFactor = -1;
                    }
                } else {
                    TLineFactor = 1;
                    T1LineFactor = -1.3;
                }
                //is the segment too short?
                lineTooShort = GetBoundarySegmentTooShort(tg, g2d, j);
                //diagnostic
                //clsUtility.WriteFile("line too short = " +lineTooShort.toString());

                if (lineTooShort == false) {
                    foundSegment = true;
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, TLineFactor * csFactor, j, j + 1, true);
                    //the echelon symbol
                    if (!tg.get_EchelonSymbol().equals("")) {
                        AddIntegralModifier(tg, tg.get_EchelonSymbol(), aboveMiddle, -0.20 * csFactor, j, j + 1, true);
                    }
                    //the T1 modifier
                    AddIntegralModifier(tg, tg.get_T1(), aboveMiddle, T1LineFactor * csFactor, j, j + 1, true);

                    if (affiliation != null && affiliation.equals("H")) {
                        //ENY label
                        ptLast = lineutility.MidPointDouble(pt0, pt1, 0);
                        ptLast = lineutility.MidPointDouble(pt0, ptLast, 0);
                        AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, ptLast, true);
                        //ENY label
                        ptLast = lineutility.MidPointDouble(pt1, pt0, 0);
                        ptLast = lineutility.MidPointDouble(pt1, ptLast, 0);
                        AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt1, ptLast, true);
                    }
                }
            }//end for loop
            if (foundSegment == false) {
                //diagnostic
                //TLineFactor=-1;
                //T1LineFactor=1;
                //end section
                pt0 = new POINT2();
                pt1 = new POINT2();
                GetBoundaryMiddleSegment(tg, g2d, pt0, pt1);
                AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, TLineFactor * csFactor, middleSegment, middleSegment + 1, true);
                //the echelon symbol
                if (!tg.get_EchelonSymbol().equals("")) {
                    AddIntegralModifier(tg, tg.get_EchelonSymbol(), aboveMiddle, -0.2020 * csFactor, middleSegment, middleSegment + 1, true);
                }
                //the T1 modifier
                AddIntegralModifier(tg, tg.get_T1(), aboveMiddle, T1LineFactor * csFactor, middleSegment, middleSegment + 1, true);

                if (affiliation != null && affiliation.equals("H")) {
                    //echelonWidth=(double)metrics.stringWidth(tg.get_N());
                    //ENY label
                    ptLast = lineutility.MidPointDouble(pt0, pt1, 0);
                    ptLast = lineutility.MidPointDouble(pt0, ptLast, 0);
                    //ptLast=lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth/2);
                    AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, ptLast, true);
                    //ENY label
                    ptLast = lineutility.MidPointDouble(pt1, pt0, 0);
                    ptLast = lineutility.MidPointDouble(pt1, ptLast, 0);
                    //ptLast=utility.ExtendAlongLineDouble(pt1, pt0, stringWidth/2);
                    AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt1, ptLast, true);
                }
            }//end if foundSegment==false
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "AddBoundaryModifiers",
                    new RendererException("Failed inside AddBoundaryModifiers", exc));
        }
        return;
    }

    private static void AddBoundaryModifiers(TGLight tg,
            Graphics2D g2d,
            ArrayList<Point2D> clipBounds) {
        try {
            //FontMetrics metrics = g2d.getFontMetrics();
            int j = 0;
            double csFactor = 1d;
            //int middleSegment = tg.Pixels.size() / 2 - 1;
            Boolean foundSegment = false;
            POINT2 pt0 = null, pt1 = null, ptLast = null;
            double TLineFactor = 0, T1LineFactor = 0;
            String affiliation = tg.get_Affiliation();
            Boolean lineTooShort = false;
            if (tg.get_Client().equals("cpof3d")) {
                csFactor = 0.85d;
                //clsUtility.WriteFile(Double.toString(csFactor));
            }

            int middleSegment = getVisibleMiddleSegment(tg, clipBounds);

            for (j = 0; j < tg.Pixels.size() - 1; j++) {
                if (tg.get_Client().equalsIgnoreCase("ge")) {
                    if (j != middleSegment) {
                        continue;
                    }
                }

                pt0 = tg.Pixels.get(j);
                pt1 = tg.Pixels.get(j + 1);
                //GetBoundaryMiddleSegment(tg, g2d, pt0, pt1);
                if (pt0.x < pt1.x) {
                    TLineFactor = -1.3;
                    T1LineFactor = 1;
                } else if (pt0.x == pt1.x) {
                    if (pt1.y < pt0.y) {
                        TLineFactor = -1;
                        T1LineFactor = 1;
                    } else {
                        TLineFactor = 1;
                        T1LineFactor = -1;
                    }
                } else {
                    TLineFactor = 1;
                    T1LineFactor = -1.3;
                }
                //is the segment too short?
                lineTooShort = GetBoundarySegmentTooShort(tg, g2d, j);
                //diagnostic
                //clsUtility.WriteFile("line too short = " +lineTooShort.toString());

                if (lineTooShort == false) {
                    foundSegment = true;
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, TLineFactor * csFactor, j, j + 1, true);
                    //the echelon symbol
                    if (!tg.get_EchelonSymbol().equals("")) {
                        AddIntegralModifier(tg, tg.get_EchelonSymbol(), aboveMiddle, -0.20 * csFactor, j, j + 1, true);
                    }
                    //the T1 modifier
                    AddIntegralModifier(tg, tg.get_T1(), aboveMiddle, T1LineFactor * csFactor, j, j + 1, true);

                    if (affiliation != null && affiliation.equals("H")) {
                        //ENY label
                        ptLast = lineutility.MidPointDouble(pt0, pt1, 0);
                        ptLast = lineutility.MidPointDouble(pt0, ptLast, 0);
                        AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, ptLast, true);
                        //ENY label
                        ptLast = lineutility.MidPointDouble(pt1, pt0, 0);
                        ptLast = lineutility.MidPointDouble(pt1, ptLast, 0);
                        AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt1, ptLast, true);
                    }
                }
            }//end for loop
            if (foundSegment == false) {
                //diagnostic
                //TLineFactor=-1;
                //T1LineFactor=1;
                //end section
                pt0 = new POINT2();
                pt1 = new POINT2();
                GetBoundaryMiddleSegment(tg, g2d, pt0, pt1);
                AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, TLineFactor * csFactor, middleSegment, middleSegment + 1, true);
                //the echelon symbol
                if (!tg.get_EchelonSymbol().equals("")) {
                    AddIntegralModifier(tg, tg.get_EchelonSymbol(), aboveMiddle, -0.2020 * csFactor, middleSegment, middleSegment + 1, true);
                }
                //the T1 modifier
                AddIntegralModifier(tg, tg.get_T1(), aboveMiddle, T1LineFactor * csFactor, middleSegment, middleSegment + 1, true);

                if (affiliation != null && affiliation.equals("H")) {
                    //echelonWidth=(double)metrics.stringWidth(tg.get_N());
                    //ENY label
                    ptLast = lineutility.MidPointDouble(pt0, pt1, 0);
                    ptLast = lineutility.MidPointDouble(pt0, ptLast, 0);
                    //ptLast=lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth/2);
                    AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, ptLast, true);
                    //ENY label
                    ptLast = lineutility.MidPointDouble(pt1, pt0, 0);
                    ptLast = lineutility.MidPointDouble(pt1, ptLast, 0);
                    //ptLast=lineutility.ExtendAlongLineDouble(pt1, pt0, stringWidth/2);
                    AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt1, ptLast, true);
                }
            }//end if foundSegment==false
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "AddBoundaryModifiers",
                    new RendererException("Failed inside AddBoundaryModifiers", exc));
        }
        return;
    }

    /**
     * added for USAS
     *
     * @param tg
     * @param metrics
     */
    private static void AddNameAboveDTG(TGLight tg, FontMetrics metrics) {
        try {
            double csFactor = 1;
            //if(tg.get_cs()==true)
            //csFactor=0.667;
            if (tg.get_Client().equals("cpof3d")) {
                csFactor = 0.667;
            }
            String label = GetCenterLabel(tg);
            POINT2 pt0 = new POINT2(tg.Pixels.get(0));
            POINT2 pt1 = new POINT2(tg.Pixels.get(1));
            int lastIndex = tg.Pixels.size() - 1;
            int nextToLastIndex = tg.Pixels.size() - 2;
            POINT2 ptLast = new POINT2(tg.Pixels.get(lastIndex));
            POINT2 ptNextToLast = new POINT2(tg.Pixels.get(nextToLastIndex));
            shiftModifierPath(tg, pt0, pt1, ptLast, ptNextToLast);
            double stringWidth = metrics.stringWidth(label + " " + tg.get_Name());
            //AddIntegralModifier(tg, label + " " + tg.get_Name(), toEnd, 0, 0, 1,false);
            AddIntegralAreaModifier(tg, label + " " + tg.get_Name(), toEnd, 0, pt0, pt1, false);
            pt1 = lineutility.ExtendAlongLineDouble(tg.Pixels.get(0), tg.Pixels.get(1), -1.5 * stringWidth);
            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
            //AddIntegralModifier(tg, label + " " + tg.get_Name(), toEnd, 0, lastIndex, nextToLastIndex,false);
            AddIntegralAreaModifier(tg, label + " " + tg.get_Name(), toEnd, 0, ptLast, ptNextToLast, false);
            pt0 = tg.Pixels.get(lastIndex);
            pt1 = lineutility.ExtendAlongLineDouble(tg.Pixels.get(lastIndex), tg.Pixels.get(nextToLastIndex), -1.5 * stringWidth);
            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "AddBoundaryModifiers",
                    new RendererException("Failed inside AddBoundaryModifiers", exc));
        }
        return;
    }

    /**
     * @deprecated Return false for symbols with text but which cannot be
     * obscured by the fill because they do not have fill or because the text is
     * positioned such that the fill cannot obscure the text or the fill alpha
     * is too low to obscure the text.
     * @param tg
     * @return true if fill can obscure the text
     */
    private static Boolean fillCanObscureText(TGLight tg) {
        Boolean result = true;
        try {
            switch (tg.get_LineType()) {
                case TacticalLines.BLOCK:
                case TacticalLines.CONTAIN:
                case TacticalLines.DELAY:
                case TacticalLines.FOLLA:
                case TacticalLines.FOLSP:
                case TacticalLines.RETIRE:
                case TacticalLines.SCREEN:
                case TacticalLines.COVER:
                case TacticalLines.GUARD:
                case TacticalLines.WITHDRAW:
                case TacticalLines.WDRAWUP:

                case TacticalLines.SARA:
                case TacticalLines.FPF:
                case TacticalLines.PDF:
                case TacticalLines.AIRHEAD:
                case TacticalLines.GAP:
                case TacticalLines.BRIDGE:
                    return false;
                default:
                    Color fillColor = tg.get_FillColor();
                    int alpha = -1;
                    if (fillColor != null) {
                        alpha = fillColor.getAlpha();
                        if (alpha > fillAlphaCanObscureText) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "fillCanObscureText",
                    new RendererException("Failed inside fillCanObscureText", exc));
        }
        return result;
    }

    /**
     * returns the starting index of the highest segment
     *
     * @param tg
     * @return
     */
    private static int getHighestSegmentIndex(TGLight tg) {
        int upperIndex = 0;
        try {
            int sz = tg.Pixels.size();
            int j = 0;
            double upper = Double.MAX_VALUE;
            //get the highest point
            for (j = 0; j < tg.Pixels.size(); j++) {
                if (tg.Pixels.get(j).y < upper) {
                    upper = tg.Pixels.get(j).y;
                    upperIndex = j;
                }
            }
            //find the next highest point on either side of the highest point
//            if(upperIndex==0)
//            {
//                if(tg.Pixels.get(1).y<tg.Pixels.get(sz-1).y)
//                    upperIndex=sz-2;
//            }
//            else if(upperIndex==sz-1)
//            {
//                if(tg.Pixels.get(sz-2).y<tg.Pixels.get(0).y)
//                    upperIndex-=1;
//            }
//            else
//            {
//                if(tg.Pixels.get(upperIndex-1).y<tg.Pixels.get(upperIndex+1).y)
//                    upperIndex-=1;
//            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetHighestSegmentIndex",
                    new RendererException("Failed inside GetHighestSegmentIndex", exc));
        }
        return upperIndex;
    }

    /**
     * returns the lowest segment
     *
     * @param tg
     * @return
     */
    private static int getLowestSegmentIndex(TGLight tg) {
        int lowerIndex = 0;
        try {
            int sz = tg.Pixels.size();
            int j = 0;
            double lower = -Double.MAX_VALUE;
            //get the highest point
            for (j = 0; j < tg.Pixels.size(); j++) {
                if (tg.Pixels.get(j).y > lower) {
                    lower = tg.Pixels.get(j).y;
                    lowerIndex = j;
                }
            }
            //find the next highest point on either side of the highest point
//            if(lowerIndex==0)
//            {
//                if(tg.Pixels.get(sz-2).y>tg.Pixels.get(1).y)
//                    lowerIndex=sz-2;
//            }
//            else if(lowerIndex==sz-1)
//            {
//                if(tg.Pixels.get(sz-2).y>tg.Pixels.get(0).y)
//                    lowerIndex-=1;
//            }
//            else
//            {
//                if(tg.Pixels.get(lowerIndex-1).y>tg.Pixels.get(lowerIndex+1).y)
//                    lowerIndex-=1;
//            }

        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "getLowestSegmentIndex",
                    new RendererException("Failed inside getLowestSegmentIndex", exc));
        }
        return lowerIndex;
    }

    /**
     * returns blank string the desired width
     *
     * @param metrics
     * @param width
     * @return
     */
    private static String blankString(FontMetrics metrics, int width) {
        String str = "";
        try {
            while (metrics.stringWidth(str) < width) {
                str += " ";
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "getLowestSegmentIndex",
                    new RendererException("Failed inside getLowestSegmentIndex", exc));
        }
        return str;
    }

    /**
     * shifts the path for modifiers that use toEnd to prevent vertical paths
     *
     * @param tg
     * @param pt0
     * @param pt1
     * @param ptLast
     * @param ptNextToLast
     */
    private static void shiftModifierPath(TGLight tg,
            POINT2 pt0,
            POINT2 pt1,
            POINT2 ptLast,
            POINT2 ptNextToLast) {
        try {
            POINT2 p0 = null, p1 = null;
            double last = -1.0;
            switch (tg.get_LineType()) {
                case TacticalLines.BOUNDARY:
                    for (int j = 0; j < tg.Pixels.size() - 1; j++) {
                        p0 = tg.Pixels.get(j);
                        p1 = tg.Pixels.get(j + 1);
                        //if (p0.x == p1.x) 
                        if (Math.abs(p0.x - p1.x) < 1) {
                            p1.x += last;
                            last = -last;
                        }
                    }
                    break;
                case TacticalLines.PDF:
                case TacticalLines.PL:
                case TacticalLines.LOA:
                case TacticalLines.LOD:
                case TacticalLines.RELEASE:
                case TacticalLines.LDLC:
                case TacticalLines.LL:
                case TacticalLines.FCL:
                case TacticalLines.PLD:
                case TacticalLines.NFL:
                case TacticalLines.FLOT:
                case TacticalLines.LC:
                case TacticalLines.HOLD:
                case TacticalLines.BRDGHD:
                case TacticalLines.HOLD_GE:
                case TacticalLines.BRDGHD_GE:
                    //if (pt0 != null && pt1 != null && pt0.x == pt1.x) 
                    if (pt0 != null && pt1 != null && Math.abs(pt0.x - pt1.x) < 1) {
                        pt1.x += 1;
                    }
                    //if (ptLast != null && ptNextToLast != null && ptNextToLast.x == ptLast.x) 
                    if (ptLast != null && ptNextToLast != null && Math.abs(ptNextToLast.x - ptLast.x) < 1) {
                        ptNextToLast.x += 1;
                    }
                    break;
                default:
                    return;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "shiftModifierPath",
                    new RendererException("Failed inside shiftModifierPath", exc));
        }
        return;
    }

    /**
     * Areas with two alternating labels.
     *
     * @param tg
     * @param label
     * @param eny
     * @param g2d
     * @return
     */
    private static boolean areasWithTwoLabels(TGLight tg, String label, String eny, Graphics2D g2d) {
        boolean result = false;
        try {
            switch (tg.get_LineType()) {
                case TacticalLines.DUMMY:
                    if (!tg.get_Affiliation().equalsIgnoreCase("H")) {
                        eny = "";
                    }
                    break;
                default:
                    return false;
            }
            FontMetrics metrics = g2d.getFontMetrics();
            double labelLength = metrics.stringWidth(label);
            double enyLength = metrics.stringWidth(eny);
            int j = 0;
            POINT2 pt0 = null, pt1 = null, pt2;
            String last = eny;
            double dist = 0;
            int sumLabel = 0, sumENY = 0;
            for (j = 0; j < tg.Pixels.size() - 1; j++) {
                if(eny.isEmpty() && last.equalsIgnoreCase(label))
                    last=eny;
                if(label.isEmpty() && last.equalsIgnoreCase(eny))
                    last=label;
                pt0 = tg.Pixels.get(j);
                pt1 = tg.Pixels.get(j + 1);
                dist = lineutility.CalcDistanceDouble(pt0, pt1);
                if (dist > 1.5 * labelLength && last.equalsIgnoreCase(eny)) {
                    last = label;
                    sumLabel++;
                } else if (dist > 1.5 * enyLength && last.equalsIgnoreCase(label)) {
                    sumENY++;
                    last = eny;
                }
            }
            if (eny.isEmpty()) {
                if (sumENY < 2) {
                    sumENY = 2;
                }
            }
            if (sumLabel + sumENY < 4) {
                return false;
            }
            //at this point we have valid pixels for alternating labels, i.e. at least one of each will appear
            last=eny;
            for (j = 0; j < tg.Pixels.size() - 1; j++) {
                if(eny.isEmpty() && last.equalsIgnoreCase(label))
                    last=eny;
                if(label.isEmpty() && last.equalsIgnoreCase(eny))
                    last=label;
                pt0 = tg.Pixels.get(j);
                pt1 = tg.Pixels.get(j + 1);
                dist = lineutility.CalcDistanceDouble(pt0, pt1);
                if (dist > 1.5 * labelLength && last.equalsIgnoreCase(eny)) {
                    //add the label
                    if(!label.isEmpty())
                        AddIntegralAreaModifier(tg, label, aboveMiddle, 0, pt0, pt1, true);
                    last = label;
                } else if (dist > 1.5 * enyLength && last.equalsIgnoreCase(label)) {
                    //add the eny
                    if(!eny.isEmpty())
                        AddIntegralAreaModifier(tg, eny, aboveMiddle, 0, pt0, pt1, true);
                    last = eny;
                }
            }
            return true;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "areasWithTwoLabels",
                    new RendererException("Failed inside areasWithTwoLabels", exc));
        }
        return result;
    }

    /**
     * don't add affiliation for too short segments
     *
     * @param tg
     * @param g2d
     */
    private static void areasWithENY(TGLight tg, Graphics2D g2d) {
        try {
            FontMetrics metrics = g2d.getFontMetrics();
            String label = null;
            int middleSegment = tg.Pixels.size() / 2 - 1;
            int middleSegment2 = tg.Pixels.size() - 2;
            int startIndex = 0, j = 0;
            int linetype = tg.get_LineType();
            String affiliation = tg.get_Affiliation();
            //String echelonSymbol=tg.get_Echelon();
            String echelonSymbol = tg.get_EchelonSymbol();
            if (affiliation != null && affiliation.equals("H")) {
                label = tg.get_N();
            }
            if (tg.Pixels.size() > 3) {
                middleSegment = tg.Pixels.size() / 4;
            }
            if (tg.Pixels.size() > 3) {
                middleSegment2 = 3 * tg.Pixels.size() / 4;
            }

            switch (linetype) {
                case TacticalLines.DMA: // function is being used for the M label only
                case TacticalLines.DMAF:
                case TacticalLines.MINED:
                    label = "M";
                    break;
                case TacticalLines.UXO:
                    label = "UXO";
                    break;
                case TacticalLines.BATTLE:// index 0 used for echelon
                case TacticalLines.PNO:
                    if (echelonSymbol != null && !echelonSymbol.isEmpty()) {
                        startIndex = 1;
                    }
                    break;
                default:
                    break;
            }
            if (label == null || label.isEmpty()) {
                return;
            }

            int stringWidth = metrics.stringWidth(label);
            boolean foundLongSegment = false;
            double dist = 0;
            POINT2 pt0 = null, pt1 = null;
            
            int northestPtIndex = 0;
            int southestPtIndex = 0;
            POINT2 northestPt = null;
            POINT2 southestPt = null;
            
            //acevedo - 11/30/2017 - adding option to render only 2 labels.
            if (rendererSettings.getInstance().getTwoLabelOnly() == false) {

                for (j = startIndex; j < tg.Pixels.size() - 1; j++) {
                    pt0 = tg.Pixels.get(j);
                    pt1 = tg.Pixels.get(j + 1);
                    dist = lineutility.CalcDistanceDouble(pt0, pt1);
                    if (dist > 1.5 * stringWidth) {
                        foundLongSegment = true;
                        AddIntegralAreaModifier(tg, label, aboveMiddle, 0, pt0, pt1, true);
                    }
                }
                if (foundLongSegment == false)//we did not find a long enough segment
                {
                    if (middleSegment != startIndex) {
                        AddIntegralModifier(tg, label, aboveMiddle, 0, middleSegment, middleSegment + 1, true);
                    }

                    AddIntegralModifier(tg, label, aboveMiddle, 0, middleSegment2, middleSegment2 + 1, true);

                }
            }
            else {
                // 2 labels one to the north and the other to the south of graphic.
                for (j = startIndex; j < tg.Pixels.size() - 1; j++) {
                    pt0 = tg.Pixels.get(j);

        			if (northestPt == null)
        			{
        				northestPt = pt0;
        				northestPtIndex = j;
        			}
        			if (southestPt == null)
        			{
        				southestPt = pt0;
        				southestPtIndex = j;
        			}
        			if (pt0.y >= northestPt.y)
        			{
        				northestPt = pt0;
        				northestPtIndex = j;
        			}

        			if (pt0.y <= southestPt.y)
        			{
        				southestPt = pt0;
        				southestPtIndex = j;
        			}
                }//for

        	middleSegment = northestPtIndex;
        	middleSegment2 = southestPtIndex;
                if (middleSegment != startIndex)
                	AddIntegralModifier(tg, label, aboveMiddle, 0, middleSegment, middleSegment + 1, true);
                AddIntegralModifier(tg, label, aboveMiddle, 0, middleSegment2, middleSegment2 + 1, true);
            }//else
            
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "areasWithENY",
                    new RendererException("Failed inside areasWithENY", exc));
        }
        return;
    }

    private static int getVisibleMiddleSegment(TGLight tg, Rectangle2D clipBounds) {
        int middleSegment = -1;
        try {
            POINT2 pt0 = null, pt1 = null;
            int j = 0;
            double dist = 0;
            POINT2 lastPt = null;
            boolean doublesBack = false;
            long lineType = tg.get_LineType();
            //we want the middle segment to be visible            
            //middleSegment=tg.Pixels.size() / 2 - 1;
            middleSegment = (tg.Pixels.size() + 1) / 2 - 1;

            Boolean foundVisibleSegment = false;
            if (clipBounds == null) {
                return middleSegment;
            }

            //walk through the segments to find the first visible segment from the middle
            for (j = middleSegment; j < tg.Pixels.size() - 1; j++) {
                pt0 = tg.Pixels.get(j);
                pt1 = tg.Pixels.get(j + 1);
                dist = lineutility.CalcDistanceDouble(pt0, pt1);
                if (dist < 5) {
                    continue;
                }
                //diagnostic
                if (j > 0 && lineType == TacticalLines.BOUNDARY) {
                    if (lastPt == null) {
                        lastPt = tg.Pixels.get(j - 1);
                    }
                    doublesBack = DoublesBack(lastPt, pt0, pt1);
                    if (doublesBack == true) {
                        continue;
                    }

                    lastPt = null;
                }
                //if either of the points is within the bound then most of the segment is visible
                if (clipBounds.contains(pt0.x, pt0.y) || clipBounds.contains(pt1.x, pt1.y)) {
                    middleSegment = j;
                    foundVisibleSegment = true;
                    break;
                }
            }

            if (foundVisibleSegment == false) {
                for (j = middleSegment; j > 0; j--) {
                    pt0 = tg.Pixels.get(j);
                    pt1 = tg.Pixels.get(j - 1);
                    dist = lineutility.CalcDistanceDouble(pt0, pt1);
                    if (dist < 5) {
                        continue;
                    }
                    //diagnostic
                    if (j > 0 && lineType == TacticalLines.BOUNDARY) {
                        if (lastPt == null) {
                            lastPt = tg.Pixels.get(j - 1);
                        }
                        doublesBack = DoublesBack(lastPt, pt0, pt1);
                        if (doublesBack == true) {
                            continue;
                        }

                        lastPt = null;
                    }
                    //if either of the points is within the bound then most of the segment is visible
                    if (clipBounds.contains(pt0.x, pt0.y) || clipBounds.contains(pt1.x, pt1.y)) {
                        middleSegment = j - 1;
                        foundVisibleSegment = true;
                        break;
                    }
                }
            }

            if (foundVisibleSegment == false) {
                middleSegment = tg.Pixels.size() / 2 - 1;
                //middleSegment=-1;
            }

        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "getMiddleSegment",
                    new RendererException("Failed inside getMiddleSegment", exc));
        }
        return middleSegment;
    }

    /**
     * uses array instead of rectangle
     *
     * @param tg
     * @param clipBounds
     * @return
     */
    private static int getVisibleMiddleSegment(TGLight tg, ArrayList<Point2D> clipBounds) {
        int middleSegment = -1;
        try {
            Polygon clipBoundsPoly = new Polygon();
            POINT2 pt0 = null, pt1 = null;
            int j = 0;
            int x = 0, y = 0;
            double dist = 0;
            POINT2 lastPt = null;
            long lineType = tg.get_LineType();
            boolean doublesBack = false;
            //we want the middle segment to be visible            
            middleSegment = (tg.Pixels.size() + 1) / 2 - 1;

            Boolean foundVisibleSegment = false;
            if (clipBounds == null) {
                return middleSegment;
            }

            for (j = 0; j < clipBounds.size(); j++) {
                x = (int) clipBounds.get(j).getX();
                y = (int) clipBounds.get(j).getY();
                clipBoundsPoly.addPoint(x, y);
            }

            //walk through the segments to find the first visible segment from the middle
            for (j = middleSegment; j < tg.Pixels.size() - 1; j++) {
                pt0 = tg.Pixels.get(j);
                pt1 = tg.Pixels.get(j + 1);
                dist = lineutility.CalcDistanceDouble(pt0, pt1);
                if (dist < 5) {
                    continue;
                }
                //diagnostic
                if (j > 0 && lineType == TacticalLines.BOUNDARY) {
                    if (lastPt == null) {
                        lastPt = tg.Pixels.get(j - 1);
                    }
                    doublesBack = DoublesBack(lastPt, pt0, pt1);
                    if (doublesBack == true) {
                        continue;
                    }

                    lastPt = null;
                }
                //if either of the points is within the bound then most of the segment is visible
                if (clipBoundsPoly.contains(pt0.x, pt0.y) || clipBoundsPoly.contains(pt1.x, pt1.y)) {
                    middleSegment = j;
                    foundVisibleSegment = true;
                    break;
                }
            }

            if (foundVisibleSegment == false) {
                for (j = middleSegment; j > 0; j--) {
                    pt0 = tg.Pixels.get(j);
                    pt1 = tg.Pixels.get(j - 1);
                    dist = lineutility.CalcDistanceDouble(pt0, pt1);
                    if (dist < 5) {
                        continue;
                    }
                    //diagnostic
                    if (j > 0 && lineType == TacticalLines.BOUNDARY) {
                        if (lastPt == null) {
                            lastPt = tg.Pixels.get(j - 1);
                        }
                        doublesBack = DoublesBack(lastPt, pt0, pt1);
                        if (doublesBack == true) {
                            continue;
                        }

                        lastPt = null;
                    }
                    //if either of the points is within the bound then most of the segment is visible
                    if (clipBoundsPoly.contains(pt0.x, pt0.y) || clipBoundsPoly.contains(pt1.x, pt1.y)) {
                        middleSegment = j - 1;
                        foundVisibleSegment = true;
                        break;
                    }
                }
            }

            if (foundVisibleSegment == false) {
                middleSegment = tg.Pixels.size() / 2 - 1;
                //middleSegment=-1;
            }

        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "getMiddleSegment",
                    new RendererException("Failed inside getMiddleSegment", exc));
        }
        return middleSegment;
    }

    /**
     * @deprecated extracts largest segment width to display for H2 modifier for
     * the air corridors
     * @param tg
     * @return
     */
    private static String getLargestACWidth(TGLight tg) {
        String h2 = null;
        try {
            int linetype = tg.get_LineType();
            switch (linetype) {
                case TacticalLines.UAV:
                case TacticalLines.MRR:
                case TacticalLines.LLTR:
                case TacticalLines.AC:
                case TacticalLines.SAAFR:
                    break;
                default:
                    return null;
            }
            if (tg.get_H2() == null || tg.get_H2().isEmpty()) {
                return "0";
            }

            String[] H2 = tg.get_H2().split(",");
            double max = -1;
            int j = 0;
            for (j = 0; j < H2.length; j++) {
                if (Double.parseDouble(H2[j]) > max) {
                    max = Double.parseDouble(H2[j]);
                }
            }
            return Double.toString(max);
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "getLargestACWidth",
                    new RendererException("Failed inside getLargestACWidth", exc));
        }
        return h2;
    }

    /**
     * called repeatedly by RemoveModifiers to remove modifiers which fall
     * outside the symbol MBR
     *
     * @param tg
     * @param modifierType
     */
    private static void removeModifier(TGLight tg,
            String modifierType) {
        try {
            int j = 0;
            Modifier2 modifier = null;
            for (j = 0; j < tg.modifiers.size(); j++) {
                modifier = tg.modifiers.get(j);

                if (modifier.textID == null) {
                    continue;
                }

                if (modifier.textID.equalsIgnoreCase(modifierType)) {
                    tg.modifiers.remove(modifier);
                    break;
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "removeModifier",
                    new RendererException("Failed inside removeModifier", exc));
        }
        return;
    }

    /**
     * removes text modifiers for CPOF tactical areas which do not fit inside
     * the symbol MBR
     *
     * @param tg
     * @param g2d
     * @param isTextFlipped true if text is flipped from the last segment
     * orientation
     * @param iteration the instance count for this modifier
     */
    public static void RemoveModifiers(TGLight tg,
            Graphics2D g2d,
            boolean isTextFlipped,
            int iteration) {
        try {
            //CPOF clients only
            if (!tg.get_Client().equalsIgnoreCase("cpof2d") && !tg.get_Client().equalsIgnoreCase("cpof3d")) {
                return;
            }

            int j = 0;
            Polygon mbrPoly = null;
            //POINT2 ptUL=new POINT2(),ptUR=new POINT2(),ptLR=new POINT2(),ptLL=new POINT2();
            //if it's a change 1 rectangular area then use the pixels instead of the mbr
            //because those use aboveMiddle to build angular text
            switch (tg.get_LineType()) {
                case TacticalLines.RECTANGULAR:
                case TacticalLines.ACA_RECTANGULAR: //aboveMiddle modifiers: slanted text
                case TacticalLines.FFA_RECTANGULAR:
                case TacticalLines.NFA_RECTANGULAR:
                case TacticalLines.RFA_RECTANGULAR:
                case TacticalLines.KILLBOXBLUE_RECTANGULAR:
                case TacticalLines.KILLBOXPURPLE_RECTANGULAR:
                case TacticalLines.FSA_RECTANGULAR:
                case TacticalLines.ATI_RECTANGULAR:
                case TacticalLines.CFFZ_RECTANGULAR:
                case TacticalLines.SENSOR_RECTANGULAR:
                case TacticalLines.CENSOR_RECTANGULAR:
                case TacticalLines.DA_RECTANGULAR:
                case TacticalLines.CFZ_RECTANGULAR:
                case TacticalLines.ZOR_RECTANGULAR:
                case TacticalLines.TBA_RECTANGULAR:
                case TacticalLines.TVAR_RECTANGULAR:
                case TacticalLines.ACA_CIRCULAR:
                case TacticalLines.CIRCULAR:
                case TacticalLines.FSA_CIRCULAR:
                case TacticalLines.ATI_CIRCULAR:
                case TacticalLines.CFFZ_CIRCULAR:
                case TacticalLines.SENSOR_CIRCULAR:
                case TacticalLines.CENSOR_CIRCULAR:
                case TacticalLines.DA_CIRCULAR:
                case TacticalLines.CFZ_CIRCULAR:
                case TacticalLines.ZOR_CIRCULAR:
                case TacticalLines.TBA_CIRCULAR:
                case TacticalLines.TVAR_CIRCULAR:
                case TacticalLines.FFA_CIRCULAR:
                case TacticalLines.NFA_CIRCULAR:
                case TacticalLines.RFA_CIRCULAR:
                case TacticalLines.KILLBOXBLUE_CIRCULAR:
                case TacticalLines.KILLBOXPURPLE_CIRCULAR:
                    if (tg.modifiers == null || tg.modifiers.isEmpty() || iteration != 1) {
                        return;
                    }

                    mbrPoly = new Polygon();
                    for (j = 0; j < tg.Pixels.size(); j++) {
                        mbrPoly.addPoint((int) tg.Pixels.get(j).x, (int) tg.Pixels.get(j).y);
                    }

                    break;
                default:    //area modifiers: horizontal text
                    if (clsUtility.isClosedPolygon(tg.get_LineType()) == false || iteration != 0) {
                        return;
                    }
                    if (tg.modifiers == null || tg.modifiers.isEmpty()) {
                        return;
                    }

                    mbrPoly = new Polygon();
                    for (j = 0; j < tg.Pixels.size(); j++) {
                        mbrPoly.addPoint((int) tg.Pixels.get(j).x, (int) tg.Pixels.get(j).y);
                    }
            }

            Font font = null;
            font = tg.get_Font();    //might have to change this
            if (font == null) {
                font = g2d.getFont();
            }
            g2d.setFont(font);
            FontMetrics metrics = g2d.getFontMetrics();

            double stringWidth = 0, stringHeight = 0;
            boolean wfits = true, w1fits = true, ww1fits = true, hfits = true, h1fits = true, h2fits = true;
            Modifier2 modifier = null;
            String modifierType = "";
            String s = "";
            POINT2 pt0 = null, pt1 = null, pt2 = null, pt3 = null, pt4 = null;
            double lineFactor = 0;
            double x = 0, y = 0;
            double x1 = 0, y1 = 0, x2 = 0, y2 = 0;            //logic as follows:
            //we have to loop through to determine if each modifiers fits and set its fitsMBR member
            //then run a 2nd loop to remove groups of modifiers based on whether any of the others do not fit
            //e.g. if W does not fit then remove W and W1 modifiers
            for (j = 0; j < tg.modifiers.size(); j++) {
                modifier = tg.modifiers.get(j);
                if (modifier.textID == null || modifier.textID.isEmpty()) {
                    continue;
                }

                modifierType = modifier.textID;
                lineFactor = modifier.lineFactor;

                if (isTextFlipped) {
                    lineFactor = -lineFactor;
                }

                s = modifier.text;
                if (s == null || s.equals("")) {
                    continue;
                }
                stringWidth = (double) metrics.stringWidth(s) + 1;
                stringHeight = (double) font.getSize();

                if (modifier.type == area) {
                    pt0 = modifier.textPath[0];
                    x1 = pt0.x;
                    y1 = pt0.y;
                    x = (int) x1 - (int) stringWidth / 2;
                    y = (int) y1 + (int) (stringHeight / 2) + (int) (1.25 * lineFactor * stringHeight);
                    //pt1 = modifier.textPath[1];
                    x2 = (int) x1 + (int) stringWidth / 2;
                    y2 = (int) y1 + (int) (stringHeight / 2) + (int) (1.25 * lineFactor * stringHeight);
                    if (mbrPoly.contains(x, y) && mbrPoly.contains(x2, y2)) {
                        modifier.fitsMBR = true;
                    } else {
                        modifier.fitsMBR = false;
                    }
                } else if (modifier.type == aboveMiddle) {
                    pt0 = modifier.textPath[0];
                    pt1 = modifier.textPath[1];
                    //double dist=lineutility.CalcDistanceDouble(pt0, pt1);
                    POINT2 ptCenter = lineutility.MidPointDouble(pt0, pt1, 0);
                    pt0 = lineutility.ExtendAlongLineDouble(ptCenter, pt0, stringWidth / 2);
                    pt1 = lineutility.ExtendAlongLineDouble(ptCenter, pt1, stringWidth / 2);

                    if (lineFactor >= 0) {
                        pt2 = lineutility.ExtendDirectedLine(ptCenter, pt0, pt0, 3, Math.abs((lineFactor) * stringHeight));
                    } else {
                        pt2 = lineutility.ExtendDirectedLine(ptCenter, pt0, pt0, 2, Math.abs((lineFactor) * stringHeight));
                    }

                    if (lineFactor >= 0) {
                        pt3 = lineutility.ExtendDirectedLine(ptCenter, pt1, pt1, 3, Math.abs((lineFactor) * stringHeight));
                    } else {
                        pt3 = lineutility.ExtendDirectedLine(ptCenter, pt1, pt1, 2, Math.abs((lineFactor) * stringHeight));
                    }

                    x1 = pt2.x;
                    y1 = pt2.y;
                    x2 = pt3.x;
                    y2 = pt3.y;
                    if (mbrPoly.contains(x1, y1) && mbrPoly.contains(x2, y2)) {
                        modifier.fitsMBR = true;
                    } else {
                        modifier.fitsMBR = false;
                    }
                } else {
                    modifier.fitsMBR = true;
                }
            }
            for (j = 0; j < tg.modifiers.size(); j++) {
                modifier = tg.modifiers.get(j);
                if (modifier.textID == null || modifier.textID.isEmpty()) {
                    continue;
                }

                if (modifier.fitsMBR == false) {
                    if (modifier.textID.equalsIgnoreCase("W")) {
                        wfits = false;
                    } else if (modifier.textID.equalsIgnoreCase("W1")) {
                        w1fits = false;
                    } else if (modifier.textID.equalsIgnoreCase("W+W1")) {
                        ww1fits = false;
                    } else if (modifier.textID.equalsIgnoreCase("H")) {
                        hfits = false;
                    } else if (modifier.textID.equalsIgnoreCase("H1")) {
                        h1fits = false;
                    } else if (modifier.textID.equalsIgnoreCase("H2")) {
                        h2fits = false;
                    }
                }
            }
            if (wfits == false || w1fits == false) {
                removeModifier(tg, "W");
                removeModifier(tg, "W1");
            }
            if (ww1fits == false) {
                removeModifier(tg, "W+W1");
            }
            if (hfits == false || h1fits == false || h2fits == false) {
                removeModifier(tg, "H");
                removeModifier(tg, "H1");
                removeModifier(tg, "H2");
            }

        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "RemoveModifeirs",
                    new RendererException("Failed inside RemoveModifiers", exc));
        }
    }

    /**
     * Calculates a segment in the pixels middle by length to hold a string.
     *
     * @param tg
     * @param stringWidth
     * @param segPt0
     * @param segPt1
     */
    private static void getPixelsMiddleSegment(TGLight tg,
            double stringWidth,
            POINT2 segPt0,
            POINT2 segPt1) {
        try {
            switch (tg.get_LineType()) {
                case TacticalLines.CFL:
                    //case TacticalLines.FSCL:
                    //case TacticalLines.RFL:
                    break;
                default:
                    return;
            }
            int totalLength = 0;
            int j = 0;
            double dist = 0;
            double mid = 0;
            double remainder = 0;
            POINT2 pt0 = null, pt1 = null, pt2 = null, pt3 = null;
            POINT2 midPt = null;
            //first get the total length of all the segments
            for (j = 0; j < tg.Pixels.size() - 1; j++) {
                dist = lineutility.CalcDistanceDouble(tg.Pixels.get(j), tg.Pixels.get(j + 1));
                totalLength += dist;
            }
            mid = totalLength / 2;
            totalLength = 0;
            //walk thru the segments to find the middle
            for (j = 0; j < tg.Pixels.size() - 1; j++) {
                dist = lineutility.CalcDistanceDouble(tg.Pixels.get(j), tg.Pixels.get(j + 1));
                totalLength += dist;
                if (totalLength >= mid)//current segment contains the middle
                {
                    remainder = totalLength - mid;
                    pt0 = tg.Pixels.get(j);
                    pt1 = tg.Pixels.get(j + 1);
                    //calculate the pixels mid point
                    midPt = lineutility.ExtendAlongLineDouble2(pt1, pt0, remainder);
                    pt2 = lineutility.ExtendAlongLineDouble2(midPt, pt0, stringWidth / 2);
                    pt3 = lineutility.ExtendAlongLineDouble2(midPt, pt1, stringWidth / 2);
                    segPt0.x = pt2.x;
                    segPt0.y = pt2.y;
                    segPt1.x = pt3.x;
                    segPt1.y = pt3.y;
                    break;
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "getPixelsMidpoint",
                    new RendererException("Failed inside getPixelsMidpoint", exc));
        }
        return;
    }

    private static double getChange1Height(TGLight tg) {
        double height = 0;
        try {
            switch (tg.get_LineType()) {
                //case TacticalLines.PAA_RECTANGULAR_REVC:
                //case TacticalLines.PAA_RECTANGULAR:
                case TacticalLines.FSA_RECTANGULAR:
                case TacticalLines.FFA_RECTANGULAR:
                case TacticalLines.ACA_RECTANGULAR:
                case TacticalLines.NFA_RECTANGULAR:
                case TacticalLines.RFA_RECTANGULAR:
                case TacticalLines.ATI_RECTANGULAR:
                case TacticalLines.CFFZ_RECTANGULAR:
                case TacticalLines.SENSOR_RECTANGULAR:
                case TacticalLines.CENSOR_RECTANGULAR:
                case TacticalLines.DA_RECTANGULAR:
                case TacticalLines.CFZ_RECTANGULAR:
                case TacticalLines.ZOR_RECTANGULAR:
                case TacticalLines.TBA_RECTANGULAR:
                case TacticalLines.TVAR_RECTANGULAR:
                case TacticalLines.KILLBOXBLUE_RECTANGULAR:
                case TacticalLines.KILLBOXPURPLE_RECTANGULAR:
                    break;
                default:
                    return 0;
            }
            double x1 = tg.Pixels.get(0).x;
            double y1 = tg.Pixels.get(0).y;
            double x2 = tg.Pixels.get(1).x;
            double y2 = tg.Pixels.get(1).y;
            double deltax = x2 - x1;
            double deltay = y2 - y1;
            height = Math.sqrt(deltax * deltax + deltay * deltay);
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "getChange1Height",
                    new RendererException("Failed inside getChange1Height", exc));
        }
        return height;
    }

    /**
     * scale the line factor for closed areas
     *
     * @param tg
     */
    private static void scaleModifiers(TGLight tg) {
        try {
            if (RendererSettings.getInstance().getAutoCollapseModifiers() == false) {
                return;
            }
            if (!tg.get_Client().equalsIgnoreCase("ge")) {
                return;
            }
            //exit if there are no modifiers or it's not a closed area
            if (tg.modifiers == null || tg.modifiers.isEmpty()) {
                return;
            }
            int linetype = tg.get_LineType();
            boolean isClosedPolygon = clsUtility.isClosedPolygon(linetype);
            boolean isChange1Area = clsUtility.IsChange1Area(linetype, null);
            if (!isClosedPolygon && !isChange1Area) {
                return;
            }
            switch (linetype) {
                case TacticalLines.PAA_CIRCULAR:
                case TacticalLines.PAA_RECTANGULAR:
                case TacticalLines.PAA_RECTANGULAR_REVC:
                case TacticalLines.RANGE_FAN:
                case TacticalLines.RANGE_FAN_SECTOR:
                    return;
                default:
                    break;
            }
            POINT2 ptUl = new POINT2(), ptUr = new POINT2(), ptLr = new POINT2(), ptLl = new POINT2();
            GetMBR(tg, ptUl, ptUr, ptLr, ptLl);
            int sz = tg.get_Font().getSize();
            //heightMBR is half the MBR height
            //double heightMBR=Math.abs(ptLr.y-ptUr.y)/2;
            double heightMBR = 0;
            double change1Height = getChange1Height(tg);
            if (change1Height <= 0) {
                heightMBR = Math.abs(ptLr.y - ptUr.y) / 2;
            } else {
                heightMBR = change1Height;
            }

            double heightModifiers = 0;
            ArrayList<Modifier2> modifiers = tg.modifiers;
            Modifier2 modifier = null;
            double minLF = Integer.MAX_VALUE;
            int j = 0;
            boolean isValid = false;
            for (j = 0; j < modifiers.size(); j++) {
                modifier = modifiers.get(j);
                //if(modifier.type == area)
                //type3Area=true;
                if (modifier.type == toEnd) {
                    continue;
                }
                if (modifier.type == aboveMiddle && isChange1Area == false) {
                    continue;
                }
                if (modifier.lineFactor < minLF) {
                    minLF = modifier.lineFactor;
                }
                isValid = true;
            }
            //if there are no 'area' modifiers then exit early
            if (!isValid) {
                return;
            }

            heightModifiers = Math.abs(minLF) * sz;
            boolean expandModifiers = false, shrinkModifiers = false;
            if (heightModifiers > heightMBR) {
                shrinkModifiers = true;
            } else if (heightModifiers < 0.5 * heightMBR) {
                expandModifiers = true;
            }

            boolean addEllipsis = false;
            //modifierE is ellipses modifier
            Modifier2 modifierE = new Modifier2();
            if (expandModifiers) {
                double factor = heightMBR / heightModifiers;
                factor = 1 + (factor - 1) / 4;
                if (factor > 2) {
                    factor = 2;
                }
                for (j = 0; j < modifiers.size(); j++) {
                    modifier = modifiers.get(j);
                    if (modifier.type == aboveMiddle) {
                        if (isChange1Area == false) {
                            continue;
                        }
                    } else if (modifier.type != area) {
                        continue;
                    }
                    modifier.lineFactor *= factor;
                }
            } else if (shrinkModifiers) {
                double deltaLF = (heightModifiers - heightMBR) / sz;
                double newLF = 0;
                //use maxLF for the ellipsis modifier
                double maxLF = 0;
                for (j = 0; j < modifiers.size(); j++) {
                    modifier = modifiers.get(j);
                    if (modifier.type == aboveMiddle) {
                        if (isChange1Area == false) {
                            continue;
                        }
                    } else if (modifier.type != area) {
                        continue;
                    }
                    newLF = modifier.lineFactor + deltaLF;
                    if (Math.abs(newLF * sz) >= heightMBR) {
                        //flag the modifier to remove
                        if (modifier.lineFactor > minLF) {
                            modifierE.type = modifier.type;
                            modifier.type = 7;
                            if (!modifier.text.isEmpty()) {
                                addEllipsis = true;
                            }
                        }
                        modifier.lineFactor = newLF;
                        //modifierE.type=area;
                        //modifierE.type=modifier.type;
                        modifierE.textPath = modifier.textPath;
                        continue;
                    }
                    modifier.lineFactor = newLF;
                }
                ArrayList<Modifier2> modifiers2 = new ArrayList();
                for (j = 0; j < modifiers.size(); j++) {
                    modifier = modifiers.get(j);
                    if (modifier.type != 7) {
                        if (modifier.lineFactor > maxLF) {
                            maxLF = modifier.lineFactor;
                        }
                        modifiers2.add(modifier);
                    }
                }
                if (addEllipsis) {
                    Character letter = (char) 9679;
                    String s = Character.toString(letter);
                    String echelonSymbol = s + s + s;
                    modifierE.text = echelonSymbol;
                    modifierE.lineFactor = maxLF + 1;
                    modifiers2.add(modifierE);
                }
                tg.modifiers = modifiers2;
            }   //end shrink modifiers
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "scaleModifiers",
                    new RendererException("Failed inside scaleModifiers", exc));
        }
    }

    /**
     * Function to replace AddModifiers for most clients. The additional
     * converter parameter enables better accuracy locating the modifiers. Do
     * not deprecate AddModifers for clients which still use it.
     *
     * @param tg
     * @param g2d
     * @param clipBounds
     * @param converter
     */
    public static void AddModifiersGeo(TGLight tg,
            Graphics2D g2d,
            Object clipBounds,
            IPointConversion converter) {
        try {
            //exit early for those not affected
            //_isTextFlipped=isTextFlipped;
            //boolean lineTooShort=false;
            if (tg.Pixels == null || tg.Pixels.isEmpty()) {
                return;
            }
            //ArrayList<POINT2>origPoints=lineutility.getDeepCopy(tg.Pixels);
            ArrayList<POINT2> origPoints = null;
            Font font = tg.get_Font();
            boolean shiftLines = Channels.getShiftLines();
            boolean usas = false, foundSegment = false;
            double csFactor = 1d, dist = 0, dist2 = 0;//this will be used for text spacing the 3d map (CommandCight)
            POINT2 midPt = null;
            boolean isChange1Area = clsUtility.IsChange1Area(tg.get_LineType(), null);
            int northestPtIndex = 0;
            int southestPtIndex = 0;
            POINT2 northestPt = null;
            POINT2 southestPt = null;
            if (isChange1Area) {
                return;
            }

            Rectangle2D clipRect = null;
            ArrayList<Point2D> clipArray = null;
            if (clipBounds != null && ArrayList.class.isAssignableFrom(clipBounds.getClass())) {
                clipArray = (ArrayList<Point2D>) clipBounds;
            }
            if (clipBounds != null && Rectangle2D.Double.class.isAssignableFrom(clipBounds.getClass())) {
                clipRect = (Rectangle2D.Double) clipBounds;
            }

            FontMetrics metrics = g2d.getFontMetrics();
            int stringWidth = 0, stringWidth2 = 0;
            String dash = "";
            if (tg.get_DTG() != null && tg.get_DTG1() != null && tg.get_DTG().isEmpty() == false && tg.get_DTG1().isEmpty() == false) {
                dash = " - ";
            }

            //if(tg.get_cs()==true)
            //csFactor=0.667;
            if (tg.get_Client().equals("cpof3d")) {
                csFactor = 0.9d;
            }

            switch (tg.get_LineType()) {
                case TacticalLines.DUMMY:
                case TacticalLines.SERIES:
                case TacticalLines.ALT:
                case TacticalLines.ONEWAY:
                case TacticalLines.TWOWAY:
                case TacticalLines.DHA:
                case TacticalLines.EPW:
                case TacticalLines.UXO:
                case TacticalLines.FARP:
                case TacticalLines.BSA:
                case TacticalLines.DSA:
                case TacticalLines.RSA:
                case TacticalLines.THUNDERSTORMS:
                case TacticalLines.ICING:
                case TacticalLines.FREEFORM:
                case TacticalLines.RHA:
                case TacticalLines.MSR:
                case TacticalLines.ASR:
                case TacticalLines.LINTGT:
                case TacticalLines.LINTGTS:
                case TacticalLines.FPF:
                case TacticalLines.GAP:
                case TacticalLines.DEPICT:
                case TacticalLines.AIRHEAD:
                case TacticalLines.CONVOY:
                case TacticalLines.HCONVOY:
                case TacticalLines.FSA:
                case TacticalLines.DIRATKAIR:
                case TacticalLines.OBJ:
                case TacticalLines.EA1:
                case TacticalLines.AO:
                case TacticalLines.ACA:
                case TacticalLines.FFA:
                case TacticalLines.NFA:
                case TacticalLines.RFA:
                case TacticalLines.ATI:
                case TacticalLines.CFFZ:
                case TacticalLines.CFZ:
                case TacticalLines.TBA:
                case TacticalLines.TVAR:
                case TacticalLines.KILLBOXBLUE:
                case TacticalLines.KILLBOXPURPLE:
                case TacticalLines.ZOR:
                case TacticalLines.DA:
                case TacticalLines.SENSOR:
                case TacticalLines.CENSOR:
                case TacticalLines.SMOKE:
                case TacticalLines.BATTLE:
                case TacticalLines.PNO:
                case TacticalLines.PDF:
                case TacticalLines.FEBA:
                case TacticalLines.NAI:
                case TacticalLines.TAI:
                case TacticalLines.ATKPOS:
                case TacticalLines.ASSAULT:
                case TacticalLines.WFZ:
                case TacticalLines.OBSFAREA:
                case TacticalLines.OBSAREA:
                case TacticalLines.ROZ:
                case TacticalLines.FAADZ:
                case TacticalLines.HIDACZ:
                case TacticalLines.MEZ:
                case TacticalLines.LOMEZ:
                case TacticalLines.HIMEZ:
                case TacticalLines.SAAFR:
                case TacticalLines.AC:
                //case TacticalLines.HOLD:
                //case TacticalLines.BRDGHD:
                case TacticalLines.MRR:
                case TacticalLines.UAV:
                case TacticalLines.MRR_USAS:
                case TacticalLines.UAV_USAS:
                case TacticalLines.LLTR:
                case TacticalLines.AIRFIELD:
                case TacticalLines.GENERAL:
                case TacticalLines.FORT:
                case TacticalLines.ENCIRCLE:
                case TacticalLines.ASSY:
                case TacticalLines.EA:
                case TacticalLines.DZ:
                case TacticalLines.EZ:
                case TacticalLines.LZ:
                case TacticalLines.PZ:
                case TacticalLines.BOUNDARY:
                case TacticalLines.DMA:
                case TacticalLines.DMAF:
                case TacticalLines.MINED:
                case TacticalLines.PL:
                case TacticalLines.FCL:
                case TacticalLines.LOA:
                case TacticalLines.LOD:
                case TacticalLines.LL:
                case TacticalLines.RELEASE:
                case TacticalLines.LDLC:
                case TacticalLines.PLD:
                case TacticalLines.NFL:
                case TacticalLines.MFP:
                case TacticalLines.FSCL:
                case TacticalLines.CFL:
                case TacticalLines.RFL:
                case TacticalLines.FLOT:
                case TacticalLines.LC:
                case TacticalLines.CATK:
                case TacticalLines.CATKBYFIRE:
                case TacticalLines.AAFNT:
                case TacticalLines.DIRATKFNT:
                case TacticalLines.IL:
                case TacticalLines.DRCL:
                case TacticalLines.RETIRE:
                case TacticalLines.WITHDRAW:
                case TacticalLines.WDRAWUP:
                //case TacticalLines.FOLLA:
                //case TacticalLines.FOLSP:
                case TacticalLines.BEARING:
                case TacticalLines.ELECTRO:
                case TacticalLines.ACOUSTIC:
                case TacticalLines.TORPEDO:
                case TacticalLines.OPTICAL:
                case TacticalLines.RIP:
                case TacticalLines.BOMB:
                case TacticalLines.BELT:
                case TacticalLines.BELT1:
                case TacticalLines.ZONE:
                case TacticalLines.AT:
                case TacticalLines.STRONG:
                case TacticalLines.MSDZ:
                case TacticalLines.SCREEN:
                case TacticalLines.COVER:
                case TacticalLines.GUARD:
                case TacticalLines.SCREEN_REVC:
                case TacticalLines.COVER_REVC:
                case TacticalLines.GUARD_REVC:
//                case TacticalLines.RECTANGULAR:
//                case TacticalLines.CIRCULAR:
                case TacticalLines.DELAY:
                case TacticalLines.TGMF:
                case TacticalLines.GENERIC:
                case TacticalLines.BS_LINE:
                case TacticalLines.BS_AREA:
                case TacticalLines.BBS_LINE:
                case TacticalLines.BBS_AREA:
                    //case TacticalLines.LAA:
                    origPoints = lineutility.getDeepCopy(tg.Pixels);
                    break;
                default:    //exit early for those not applicable
                    return;
            }

            double factor = 1;//10d/tg.get_Font().getSize();
            int linetype = tg.get_LineType();
            //boolean visibleModifiers=tg.get_VisibleLabels();
            int j = 0, k = 0;
            double x = 0, y = 0;

            if (tg.get_Font() != null && tg.get_Font().getSize() > 0) {
                factor = 10d / tg.get_Font().getSize();
            } else {
                return;
            }

            int lastIndex = tg.Pixels.size() - 1;
            int nextToLastIndex = tg.Pixels.size() - 2;
            POINT2 pt0 = new POINT2(tg.Pixels.get(0));
            POINT2 pt1 = null;
            POINT2 pt2 = null, pt3 = null;
            POINT2 ptLast = new POINT2(tg.Pixels.get(lastIndex));
            POINT2 ptNextToLast = null;

            if (lastIndex > 0) {
                ptNextToLast = new POINT2(tg.Pixels.get(lastIndex - 1));
            }

            if (tg.Pixels.size() > 1) {
                pt1 = new POINT2(tg.Pixels.get(1));
            }

            //prevent vertical paths for modifiers that use toEnd
            shiftModifierPath(tg, pt0, pt1, ptLast, ptNextToLast);

            String label = GetCenterLabel(tg);
            Object[] pts = tg.Pixels.toArray();
            //need this for areas and some lines
            //POINT2 ptCenter = lineutility.CalcCenterPointDouble2(pts, pts.length);
            POINT2 ptCenter = null;
            if (converter != null) //cpof uses latlonconverter so cpof passes null for this               
            {
                ptCenter = mdlGeodesic.geodesic_center(tg.LatLongs);
                if (ptCenter != null) {
                    //Point pt = converter.GeoToPixels(new Point2D.Double(ptCenter.x, ptCenter.y));
                    Point2D pt2d = new Point2D.Double(ptCenter.x, ptCenter.y);
                    pt2d = converter.GeoToPixels(pt2d);
                    ptCenter.x = pt2d.getX();
                    ptCenter.y = pt2d.getY();
                } else {
                    ptCenter = lineutility.CalcCenterPointDouble2(pts, pts.length);
                }
            } else {
                ptCenter = lineutility.CalcCenterPointDouble2(pts, pts.length);
            }

            int middleSegment = (tg.Pixels.size() + 1) / 2 - 1;
            int middleSegment2 = 0;

            if (clipRect != null) {
                middleSegment = getVisibleMiddleSegment(tg, clipRect);
            } else if (clipArray != null) {
                middleSegment = getVisibleMiddleSegment(tg, clipArray);
            }
            //String echelonSymbol=tg.get_EchelonSymbol();
            String affiliation = tg.get_Affiliation();
            if (tg.Pixels.size() > 2) {
                pt2 = tg.Pixels.get(2);
            }
            if (tg.Pixels.size() > 3) {
                pt3 = tg.Pixels.get(3);
            }
            //int boundaryDirection=-1;
            double TLineFactor = 0, T1LineFactor = 0;
            POINT2 lr = new POINT2(tg.Pixels.get(0));
            POINT2 ll = new POINT2(tg.Pixels.get(0));
            POINT2 ul = new POINT2(tg.Pixels.get(0));
            POINT2 ur = new POINT2(tg.Pixels.get(0));
            int index = 0;
            int nextIndex = 0;
            int size = tg.Pixels.size();
            Line2D line = null;
            //double dist=0;
            //int startIndex = 0;
            double dAngle0, dAngle1;
            int stringHeight;

            switch (linetype) {
                case TacticalLines.GENERIC:
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, true);
                    break;
                case TacticalLines.DUMMY:
                    if(areasWithTwoLabels(tg,tg.get_H(),tg.get_N(),g2d)==true)
                        break;
                    if (affiliation != null && affiliation.equals("H")) {
                        AddIntegralModifier(tg, tg.get_N(), aboveMiddle, 0, 0, 1, true);
                        AddIntegralModifier(tg, tg.get_N(), aboveMiddle, 0, lastIndex / 2, lastIndex / 2 + 1, true);
                    }
                    if (lastIndex > 3) {
                        //AddIntegralModifier(tg, tg.get_H(), aboveMiddle,0, lastIndex/2-1, lastIndex/2,true);
                        //AddModifier2(tg, tg.get_H(), aboveMiddle,0, tg.Pixels.get(lastIndex/2-1), tg.Pixels.get(lastIndex/2),true);
                        line = clsUtility.getExtendedLine(tg, lastIndex / 2 - 1, font.getSize());
                        pt0 = new POINT2(line.getX1(), line.getY1());
                        pt1 = new POINT2(line.getX2(), line.getY2());
                        AddModifier2(tg, tg.get_H(), aboveMiddle, 0, pt0, pt1, true);
                    }

                    //AddIntegralModifier(tg, tg.get_H(), aboveMiddle, 0, lastIndex/2+1, lastIndex/2+2,true);
                    //AddModifier2(tg, tg.get_H(), aboveMiddle, 0, tg.Pixels.get(lastIndex/2+1), tg.Pixels.get(lastIndex/2+2),true);
                    line = clsUtility.getExtendedLine(tg, lastIndex / 2 + 1, font.getSize());
                    pt0 = new POINT2(line.getX1(), line.getY1());
                    pt1 = new POINT2(line.getX2(), line.getY2());
                    AddModifier2(tg, tg.get_H(), aboveMiddle, 0, pt0, pt1, true);
                    break;
                case TacticalLines.SERIES:
                    //begin by assuming the 0th point is the highest point
                    y = pt0.y;
                    index = 0;
                    for (j = 1; j < size - 1; j++) {
                        if (tg.Pixels.get(j).y < y) {
                            y = tg.Pixels.get(j).y;
                            index = j;
                        }
                        if (index > 0) {
                            if (tg.Pixels.get(index - 1).y < tg.Pixels.get(index + 1).y) {
                                nextIndex = index - 1;
                            } else {
                                nextIndex = index + 1;
                            }
                        }
                        if (index == 0) {
                            if (pt1.y < ptNextToLast.y) {
                                nextIndex = 1;
                            } else {
                                nextIndex = nextToLastIndex;
                            }
                        }
                    }
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, 0, index, nextIndex, true);
                    break;
                case TacticalLines.SCREEN:
                case TacticalLines.COVER:
                case TacticalLines.GUARD:
                    //stringWidth = (int) (1.5 * (double) metrics.stringWidth(label));
                    stringHeight = (int) (0.5 * (double) metrics.getHeight());
                    dAngle0 = Math.atan2(tg.Pixels.get(0).y - tg.Pixels.get(1).y, tg.Pixels.get(0).x - tg.Pixels.get(1).x);
                    dAngle1 = Math.atan2(tg.Pixels.get(0).y - tg.Pixels.get(2).y, tg.Pixels.get(0).x - tg.Pixels.get(2).x);
                    pt0 = new POINT2(tg.Pixels.get(0));
                    pt0.x -= 30 * Math.cos(dAngle0);
                    pt0.y -= 30 * Math.sin(dAngle0) + stringHeight;
                    pt1 = new POINT2(tg.Pixels.get(0));
                    pt1.x -= 30 * Math.cos(dAngle1);
                    pt1.y -= 30 * Math.sin(dAngle1) + stringHeight;
                    AddIntegralAreaModifier(tg, label, area, 0, pt0, pt0, true);
                    AddIntegralAreaModifier(tg, label, area, 0, pt1, pt1, true);
                    break;
                case TacticalLines.SCREEN_REVC:
                case TacticalLines.COVER_REVC:
                case TacticalLines.GUARD_REVC:
                    stringWidth = (int) (1.5 * (double) metrics.stringWidth(label));
                    pt1 = new POINT2(tg.Pixels.get(1));
                    //pt1.x+=2*stringWidth;
                    pt2 = new POINT2(tg.Pixels.get(2));
                    //pt2.x-=2*stringWidth;
                    AddIntegralAreaModifier(tg, label, area, 0, pt1, pt1, true);
                    AddIntegralAreaModifier(tg, label, area, 0, pt2, pt2, true);
                    break;
                case TacticalLines.MFP:
                    pt0 = tg.Pixels.get(middleSegment);
                    pt1 = tg.Pixels.get(middleSegment + 1);
                    AddIntegralModifier(tg, label, aboveMiddle, 0, middleSegment, middleSegment + 1, true);
                    AddIntegralModifier(tg, tg.get_DTG(), aboveMiddle, 1 * factor * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, tg.get_DTG1(), aboveMiddle, 2 * factor * csFactor, middleSegment, middleSegment + 1, false);
                    break;
                case TacticalLines.ALT:
//                    pt0 = tg.Pixels.get(middleSegment);
//                    pt1 = tg.Pixels.get(middleSegment + 1);
//                    if (pt0.x < pt1.x || (pt0.x == pt1.x && pt0.y > pt1.y))
//                    {
//                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -3.5*factor*csFactor, middleSegment, middleSegment + 1,false);
//                        AddIntegralModifier(tg, "ALT", aboveMiddle, -1.5*factor*csFactor, middleSegment, middleSegment + 1,true);
//                    }
//                    else
//                    {
//                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -2 * factor*csFactor, middleSegment, middleSegment + 1,false);
//                        AddIntegralModifier(tg, "ALT", aboveMiddle, 0.7 *csFactor, middleSegment, middleSegment + 1,true);
//                    }                    
                    stringWidth = (int) (1.5 * (double) metrics.stringWidth("ALT"));
                    stringWidth2 = (int) (1.5 * (double) metrics.stringWidth(label + tg.get_Name()));
                    if (stringWidth2 > stringWidth) {
                        stringWidth = stringWidth2;
                    }

                    foundSegment = false;
                    //acevedo - 11/30/2017 - adding option to render only 2 labels.
                    if (rendererSettings.getTwoLabelOnly() == false) {
                    for (j = 0; j < tg.Pixels.size() - 1; j++) {
                        pt0 = tg.Pixels.get(j);
                        pt1 = tg.Pixels.get(j + 1);
                        dist = lineutility.CalcDistanceDouble(pt0, pt1);
                        if (dist < stringWidth) {
                            continue;
                        } else {
                            if (pt0.x < pt1.x || (pt0.x == pt1.x && pt0.y > pt1.y)) {
                                AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -3.5 * factor * csFactor, j, j + 1, false);
                                AddIntegralModifier(tg, "ALT", aboveMiddle, -1.5 * factor * csFactor, j, j + 1, true);
                            } else {
                                AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -2 * factor * csFactor, j, j + 1, false);
                                AddIntegralModifier(tg, "ALT", aboveMiddle, 0.7 * csFactor, j, j + 1, true);
                            }
                            foundSegment = true;
                        }
                    }
                    if (foundSegment == false) {
                        pt0 = tg.Pixels.get(middleSegment);
                        pt1 = tg.Pixels.get(middleSegment + 1);
                        if (pt0.x < pt1.x || (pt0.x == pt1.x && pt0.y > pt1.y)) {
                            AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -3.5 * factor * csFactor, middleSegment, middleSegment + 1, false);
                            AddIntegralModifier(tg, "ALT", aboveMiddle, -1.5 * factor * csFactor, middleSegment, middleSegment + 1, true);
                        } else {
                            AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -2 * factor * csFactor, middleSegment, middleSegment + 1, false);
                            AddIntegralModifier(tg, "ALT", aboveMiddle, 0.7 * csFactor, middleSegment, middleSegment + 1, true);
                        }
                    }
                   }
                     else {
                        // 2 labels one to the north and the other to the south of graphic.
                        for (j = 0; j < tg.Pixels.size(); j++) {
                            pt0 = tg.Pixels.get(j);

                            if (northestPt == null)
                            {
                                northestPt = pt0;
                                northestPtIndex = j;
                            }
                            if (southestPt == null)
                            {
                                southestPt = pt0;
                                southestPtIndex = j;
                            }
                            if (pt0.y >= northestPt.y)
                            {
                                northestPt = pt0;
                                northestPtIndex = j;
                            }

                            if (pt0.y <= southestPt.y)
                            {
                                southestPt = pt0;
                                southestPtIndex = j;
                            }
                        }//for

                        middleSegment = northestPtIndex;
                        middleSegment2 = southestPtIndex;

                        if (middleSegment  == tg.Pixels.size() -1) {
                            middleSegment-=1;
                        }
                        if (middleSegment2  == tg.Pixels.size() -1) {
                            middleSegment2-=1;
                       }
                        if (middleSegment == middleSegment2) {
                            middleSegment2-=1;
                        }

                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "ALT", aboveMiddle, -1.5 * factor * csFactor, middleSegment, middleSegment + 1, false);

                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, 0, middleSegment2, middleSegment2 + 1, false);
                        AddIntegralModifier(tg, "ALT", aboveMiddle, -1.5 * factor * csFactor, middleSegment2, middleSegment2 + 1, false);
                    }//else
                    break;
                case TacticalLines.ONEWAY:
//                    pt0 = tg.Pixels.get(middleSegment);
//                    pt1 = tg.Pixels.get(middleSegment + 1);
//                    if (pt0.x < pt1.x || (pt0.x == pt1.x && pt0.y > pt1.y))
//                    {
//                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -3 * factor*csFactor, middleSegment, middleSegment + 1,false);
//                    } 
//                    else
//                    {
//                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -2 * factor*csFactor, middleSegment, middleSegment + 1,false);
//                    }                                        
                    stringWidth = (int) (1.5 * (double) metrics.stringWidth(label + tg.get_Name()));
                    foundSegment = false;
                     //acevedo - 11/30/2017 - adding option to render only 2 labels.
                    if (rendererSettings.getTwoLabelOnly() == false) {
                    for (j = 0; j < tg.Pixels.size() - 1; j++) {
                        pt0 = tg.Pixels.get(j);
                        pt1 = tg.Pixels.get(j + 1);
                        dist = lineutility.CalcDistanceDouble(pt0, pt1);
                        if (dist < stringWidth) {
                            continue;
                        } else {
                            if (pt0.x < pt1.x || (pt0.x == pt1.x && pt0.y > pt1.y)) {
                                AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -3 * factor * csFactor, j, j + 1, false);
                            } else {
                                AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -2 * factor * csFactor, j, j + 1, false);
                            }
                            foundSegment = true;
                        }
                    }
                    if (foundSegment == false) {
                        pt0 = tg.Pixels.get(middleSegment);
                        pt1 = tg.Pixels.get(middleSegment + 1);
                        if (pt0.x < pt1.x || (pt0.x == pt1.x && pt0.y > pt1.y)) {
                            AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -3 * factor * csFactor, middleSegment, middleSegment + 1, false);
                        } else {
                            AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -2 * factor * csFactor, middleSegment, middleSegment + 1, false);
                        }
                    }
                     }
                    else {
                        // 2 labels one to the north and the other to the south of graphic.
                        for (j = 0; j < tg.Pixels.size(); j++) {
                            pt0 = tg.Pixels.get(j);

                            if (northestPt == null)
                            {
                                northestPt = pt0;
                                northestPtIndex = j;
                            }
                            if (southestPt == null)
                            {
                                southestPt = pt0;
                                southestPtIndex = j;
                            }
                            if (pt0.y >= northestPt.y)
                            {
                                northestPt = pt0;
                                northestPtIndex = j;
                            }

                            if (pt0.y <= southestPt.y)
                            {
                                southestPt = pt0;
                                southestPtIndex = j;
                            }
                        }//for
                        middleSegment = northestPtIndex;
                        middleSegment2 = southestPtIndex;

                        if (middleSegment  == tg.Pixels.size() -1) {
                            middleSegment-=1;
                        }
                        if (middleSegment2  == tg.Pixels.size() -1) {
                            middleSegment2-=1;
                        }
                        if (middleSegment == middleSegment2) {
                            middleSegment2-=1;
                        }


                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);

                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, 0, middleSegment2, middleSegment2 + 1, false);

                    }//else
                    break;
                case TacticalLines.TWOWAY:
//                    pt0 = tg.Pixels.get(middleSegment);
//                    pt1 = tg.Pixels.get(middleSegment + 1);
//                    if (pt0.x < pt1.x || (pt0.x == pt1.x && pt0.y > pt1.y))
//                    {
//                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -4.5 * factor*csFactor, middleSegment, middleSegment + 1,false);
//                    } 
//                    else
//                    {
//                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -2 * factor*csFactor, middleSegment, middleSegment + 1,false);
//                    }                                        
                    stringWidth = (int) (1.5 * (double) metrics.stringWidth(label + tg.get_Name()));
                    foundSegment = false;
                     //acevedo - 11/30/2017 - adding option to render only 2 labels.
                    if (rendererSettings.getTwoLabelOnly() == false) {
                    for (j = 0; j < tg.Pixels.size() - 1; j++) {
                        pt0 = tg.Pixels.get(j);
                        pt1 = tg.Pixels.get(j + 1);
                        dist = lineutility.CalcDistanceDouble(pt0, pt1);
                        if (dist < stringWidth) {
                            continue;
                        } else {
                            if (pt0.x < pt1.x || (pt0.x == pt1.x && pt0.y > pt1.y)) {
                                AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -4.5 * factor * csFactor, j, j + 1, false);
                            } else {
                                AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -2 * factor * csFactor, j, j + 1, false);
                            }
                            foundSegment = true;
                        }
                    }
                    if (foundSegment == false) {
                        pt0 = tg.Pixels.get(middleSegment);
                        pt1 = tg.Pixels.get(middleSegment + 1);
                        if (pt0.x < pt1.x || (pt0.x == pt1.x && pt0.y > pt1.y)) {
                            AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -4.5 * factor * csFactor, middleSegment, middleSegment + 1, false);
                        } else {
                            AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -2 * factor * csFactor, middleSegment, middleSegment + 1, false);
                        }
                    }
                     }
                    else {
                        // 2 labels one to the north and the other to the south of graphic.
                        for (j = 0; j < tg.Pixels.size()  ; j++) {
                            pt0 = tg.Pixels.get(j);

                            if (northestPt == null)
                            {
                                northestPt = pt0;
                                northestPtIndex = j;
                            }
                            if (southestPt == null)
                            {
                                southestPt = pt0;
                                southestPtIndex = j;
                            }
                            if (pt0.y >= northestPt.y)
                            {
                                northestPt = pt0;
                                northestPtIndex = j;
                            }

                            if (pt0.y <= southestPt.y)
                            {
                                southestPt = pt0;
                                southestPtIndex = j;
                            }
                        }//for
                        middleSegment = northestPtIndex;
                        middleSegment2 = southestPtIndex;

                        if (middleSegment  == tg.Pixels.size() -1) {
                            middleSegment-=1;
                        }
                        if (middleSegment2  == tg.Pixels.size() -1) {
                            middleSegment2-=1;
                        }
                        if (middleSegment == middleSegment2) {
                            middleSegment2-=1;
                        }

                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);

                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, 0, middleSegment2, middleSegment2 + 1, false);

                    }//else
                    break;
                case TacticalLines.DHA:
                    AddIntegralAreaModifier(tg, "DETAINEE", area, -1.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "HOLDING", area, -0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "AREA", area, 0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 1.5 * csFactor, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.EPW:
                    AddIntegralAreaModifier(tg, "EPW", area, -1.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "HOLDING", area, -0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "AREA", area, 0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 1.5 * csFactor, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.UXO:
//                    for (j = 0; j < tg.Pixels.size(); j++)
//                    {
//                        AddIntegralModifier(tg, label, aboveMiddle, 0, j, j + 1,true);
//                    }
                    areasWithENY(tg, g2d);
                    break;
                case TacticalLines.FARP:
                case TacticalLines.BSA:
                case TacticalLines.DSA:
                case TacticalLines.RSA:
                    AddIntegralAreaModifier(tg, label, area, -0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0.5 * csFactor, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.THUNDERSTORMS:
                case TacticalLines.ICING:
                case TacticalLines.FREEFORM:
                    AddAreaModifier(tg, tg.get_H(), area, -0.5, ptCenter, ptCenter, "H");
                    AddAreaModifier(tg, tg.get_H1(), area, 0.5, ptCenter, ptCenter, "H1");
                    break;
                case TacticalLines.RHA:
                    AddIntegralAreaModifier(tg, "REFUGEE", area, -1.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "HOLDING", area, -0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "AREA", area, 0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 1.5 * csFactor, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.MSR:
                case TacticalLines.ASR:
                    //AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -1*csFactor, middleSegment, middleSegment + 1,false);
                    foundSegment = false;
                    //acevedo - 11/30/2017 - adding option to render only 2 labels.
                    if ( rendererSettings.getTwoLabelOnly() == false) {
                    for (j = 0; j < tg.Pixels.size() - 1; j++) {
                        pt0 = tg.Pixels.get(j);
                        pt1 = tg.Pixels.get(j + 1);
                        stringWidth = (int) (1.5 * (double) metrics.stringWidth(label + tg.get_Name()));
                        dist = lineutility.CalcDistanceDouble(pt0, pt1);
                        if (dist < stringWidth) {
                            continue;
                        } else {
                            //AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -1*csFactor, middleSegment, middleSegment + 1,false);                        
                            AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -1 * csFactor, j, j + 1, false);
                            foundSegment = true;
                        }
                    }
                    if (foundSegment == false) {
                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -1 * csFactor, middleSegment, middleSegment + 1, false);
                    }
                    }
                     else {
                        // 2 labels one to the north and the other to the south of graphic.
                        for (j = 0; j < tg.Pixels.size()  ; j++) {
                            pt0 = tg.Pixels.get(j);

                            if (northestPt == null)
                            {
                                northestPt = pt0;
                                northestPtIndex = j;
                            }
                            if (southestPt == null)
                            {
                                southestPt = pt0;
                                southestPtIndex = j;
                            }
                            if (pt0.y >= northestPt.y)
                            {
                                northestPt = pt0;
                                northestPtIndex = j;
                            }

                            if (pt0.y <= southestPt.y)
                            {
                                southestPt = pt0;
                                southestPtIndex = j;
                            }
                        }//for
                        middleSegment = northestPtIndex;
                        middleSegment2 = southestPtIndex;

                        if (middleSegment  == tg.Pixels.size() -1) {
                            middleSegment-=1;
                        }
                        if (middleSegment2  == tg.Pixels.size() -1) {
                            middleSegment2-=1;
                        }
                        if (middleSegment == middleSegment2) {
                            middleSegment2-=1;
                        }

                       // if (middleSegment != middleSegment2) {
                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);
                        //}
                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, 0, middleSegment2, middleSegment2 + 1, false);

                    }//else
                    break;
                case TacticalLines.LINTGT:
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, -0.8 * csFactor, middleSegment, middleSegment + 1, false);
                    break;
                case TacticalLines.LINTGTS:
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, -0.8 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, label, aboveMiddle, 0.8 * csFactor, middleSegment, middleSegment + 1, false);
                    break;
                case TacticalLines.FPF:
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, -1 * csFactor, 0, 1, false);
                    AddIntegralModifier(tg, label, aboveMiddle, 1 * csFactor, 0, 1, false);
                    AddIntegralModifier(tg, tg.get_T1(), aboveMiddle, 2 * csFactor, 0, 1, false);
                    break;
                case TacticalLines.GAP:
                    if (tg.Pixels.get(1).y > tg.Pixels.get(0).y) {
                        pt0 = tg.Pixels.get(1);
                        pt1 = tg.Pixels.get(3);
                        pt2 = tg.Pixels.get(0);
                        pt3 = tg.Pixels.get(2);
                    } else {
                        pt0 = tg.Pixels.get(0);
                        pt1 = tg.Pixels.get(2);
                        pt2 = tg.Pixels.get(1);
                        pt3 = tg.Pixels.get(3);
                    }
                    pt2 = lineutility.ExtendAlongLineDouble2(pt0, pt2, -20);
                    pt3 = lineutility.ExtendAlongLineDouble2(pt1, pt3, -20);
                    //AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 1, pt0, pt1,false);
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 0, pt2, pt3, false);
                    break;
                case TacticalLines.DEPICT:
                    //use the highest point for H modifier
                    GetMBR(tg, ul, ur, lr, ll);
                    AddIntegralAreaModifier(tg, tg.get_H(), aboveMiddle, -1.5 * factor * csFactor, ul, ur, false);
                    AddIntegralAreaModifier(tg, tg.get_DTG(), aboveMiddle, 1.5 * factor * csFactor, ll, lr, false);
                    areasWithENY(tg, g2d);
                    break;
                case TacticalLines.AIRHEAD:
                    GetMBR(tg, ul, ur, lr, ll);
                    AddIntegralAreaModifier(tg, label, aboveMiddle, 1.35 * factor * csFactor, ll, lr, false);
                    //AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, 1.1 * (factor * csFactor + csFactor), ll, lr, false);
                    break;
                case TacticalLines.CONVOY:
                case TacticalLines.HCONVOY:
                    String convoyBlankString = blankString(metrics, 35);
                    //AddIntegralModifier(tg, tg.get_H() + "      " + tg.get_H1(), aboveMiddle, 0, 0, 1,false);
                    AddIntegralModifier(tg, tg.get_H() + convoyBlankString + tg.get_H1(), aboveMiddle, 0, 0, 1, false);
                    AddIntegralModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 1.2 * csFactor, 0, 1, false);
                    break;
                case TacticalLines.DIRATKAIR:
                    if (affiliation != null && affiliation.equals("H")) {
                        k = tg.Pixels.size();
                        j = lineutility.GetDirAtkAirMiddleSegment((POINT2[]) tg.Pixels.toArray(new POINT2[tg.Pixels.size()]), tg.Pixels.size());
                        pt1 = tg.Pixels.get(k - j - 1);
                        pt0 = tg.Pixels.get(k - j);
                        dist = lineutility.CalcDistanceDouble(pt0, pt1);
                        pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 2 * dist / 3);
                        AddModifier2(tg, tg.get_N(), aboveMiddle, 0, pt0, pt1, true);
                    }
                    break;
                case TacticalLines.OBJ:
                case TacticalLines.EA1:
                case TacticalLines.AO:
                    AddIntegralAreaModifier(tg, label + tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.ACA:
                    AddIntegralAreaModifier(tg, label, area, -3 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, -2 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "MIN ALT: " + tg.get_H(), area, -1 * csFactor, ptCenter, ptCenter, false, "H");
                    AddIntegralAreaModifier(tg, "MAX ALT: " + tg.get_H1(), area, 0, ptCenter, ptCenter, false, "H1");
                    AddIntegralAreaModifier(tg, "Grids: " + tg.get_H2(), area, 1 * csFactor, ptCenter, ptCenter, false, "H2");
                    AddIntegralAreaModifier(tg, "EFF: " + tg.get_DTG(), area, 2 * csFactor, ptCenter, ptCenter, false, "W");
                    AddIntegralAreaModifier(tg, tg.get_DTG1(), area, 3 * csFactor, ptCenter, ptCenter, false, "W1");
                    break;
                case TacticalLines.FFA:
                case TacticalLines.RFA:
                case TacticalLines.KILLBOXBLUE:
                    AddIntegralAreaModifier(tg, label, area, -1 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1 * csFactor, ptCenter, ptCenter, false, "W+W1");
                    break;
                case TacticalLines.KILLBOXPURPLE:
                    AddIntegralAreaModifier(tg, label, area, -1 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1 * csFactor, ptCenter, ptCenter, false, "W+W1");
                    AddIntegralAreaModifier(tg, tg.get_H1(), area, 2 * csFactor, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.NFA:
                    AddIntegralAreaModifier(tg, label, area, -1 * csFactor, ptCenter, ptCenter, true);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, true);
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1 * csFactor, ptCenter, ptCenter, true, "W+W1");
                    break;
                case TacticalLines.FSA:
                    AddIntegralAreaModifier(tg, label + " " + tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    //AddIntegralAreaModifier(tg, tg.get_Name(), area, 1 * csFactor, ptCenter, ptCenter, false);
                    GetMBR(tg, ul, ur, lr, ll);
                    POINT2 ptLeftFSA = ul;
                    POINT2 ptRightFSA = ur;
                    if (tg.get_Client().equalsIgnoreCase("ge")) {
                        ptLeftFSA.x -= font.getSize() / 2;
                        ptRightFSA.x -= font.getSize() / 2;
                    }
                    AddIntegralAreaModifier(tg, tg.get_DTG(), toEnd, 0.5 * csFactor, ptLeftFSA, ptRightFSA, false, "W");
                    AddIntegralAreaModifier(tg, tg.get_DTG1(), toEnd, 1.5 * csFactor, ptLeftFSA, ptRightFSA, false, "W1");
                    break;
                case TacticalLines.ATI:
                case TacticalLines.CFFZ:
                case TacticalLines.CFZ:
                case TacticalLines.TBA:
                case TacticalLines.TVAR:
                case TacticalLines.ZOR:
                case TacticalLines.DA:
                case TacticalLines.SENSOR:
                case TacticalLines.CENSOR:
                    AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 1 * csFactor, ptCenter, ptCenter, false);
                    GetMBR(tg, ul, ur, lr, ll);
                    POINT2 ptLeft = ul;
                    POINT2 ptRight = ur;
                    if (tg.get_Client().equalsIgnoreCase("ge")) {
                        ptLeft.x -= font.getSize() / 2;
                        ptRight.x -= font.getSize() / 2;
                    }
                    AddIntegralAreaModifier(tg, tg.get_DTG(), toEnd, 0.5 * csFactor, ptLeft, ptRight, false, "W");
                    AddIntegralAreaModifier(tg, tg.get_DTG1(), toEnd, 1.5 * csFactor, ptLeft, ptRight, false, "W1");
                    break;
                case TacticalLines.SMOKE:
                    AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1 * csFactor, ptCenter, ptCenter, false, "W+W1");
                    break;
                case TacticalLines.BATTLE:
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    AddIntegralModifier(tg, tg.get_EchelonSymbol(), aboveMiddle, -0.20 * csFactor, 0, 1, true);
                    areasWithENY(tg, g2d);
                    break;
                case TacticalLines.PNO:
                    AddIntegralAreaModifier(tg, label + tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    AddIntegralModifier(tg, tg.get_EchelonSymbol(), aboveMiddle, -0.20 * csFactor, 0, 1, true);
                    areasWithENY(tg, g2d);
                    break;
                case TacticalLines.PDF:
                    //AddIntegralAreaModifier(tg, label, toEnd, 0, pt1, pt0,false);
                    pt1 = lineutility.ExtendAlongLineDouble(pt1, pt0, -22, 0);
                    AddIntegralAreaModifier(tg, label, area, 0, pt1, pt1, false);
                    break;
                case TacticalLines.FEBA:
                    stringWidth = metrics.stringWidth(label);
                    pt1 = new POINT2(pt0);
                    pt1.x -= stringWidth + 8;
                    AddIntegralAreaModifier(tg, label, area, 0, pt1, pt1, false);
                    pt1 = new POINT2(ptLast);
                    pt1.x += 40;
                    AddIntegralAreaModifier(tg, label, area, 0, pt1, pt1, false);
                    break;
                case TacticalLines.NAI:
                case TacticalLines.TAI:
                case TacticalLines.ATKPOS:
                    AddIntegralAreaModifier(tg, label, area, -0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0.5 * csFactor, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.ASSAULT:
                    AddIntegralAreaModifier(tg, "ASLT", area, -1 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "PSN", area, 0, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 1 * csFactor, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.WFZ:
                    AddIntegralAreaModifier(tg, label, area, -1.5 * csFactor, ptCenter, ptCenter, true);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, -0.5 * csFactor, ptCenter, ptCenter, true);
                    AddIntegralAreaModifier(tg, "TIME FROM: " + tg.get_DTG(), area, 0.5 * csFactor, ptCenter, ptCenter, true, "W");
                    AddIntegralAreaModifier(tg, "TIME TO: " + tg.get_DTG1(), area, 1.5 * csFactor, ptCenter, ptCenter, true, "W1");
                    break;
                case TacticalLines.OBSFAREA:
                    AddIntegralAreaModifier(tg, label, area, -1.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, -0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_DTG(), area, 0.5 * csFactor, ptCenter, ptCenter, false, "W");
                    AddIntegralAreaModifier(tg, tg.get_DTG1(), area, 1.5 * csFactor, ptCenter, ptCenter, false, "W1");
                    break;
                case TacticalLines.OBSAREA:
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, -1 * csFactor, ptCenter, ptCenter, true);
                    AddIntegralAreaModifier(tg, tg.get_DTG(), area, 0, ptCenter, ptCenter, true, "W");
                    AddIntegralAreaModifier(tg, tg.get_DTG1(), area, 1 * csFactor, ptCenter, ptCenter, true, "W1");
                    break;
                case TacticalLines.ROZ:
                case TacticalLines.FAADZ://SHOADZ for rev C
                case TacticalLines.HIDACZ:
                case TacticalLines.MEZ:
                case TacticalLines.LOMEZ:
                case TacticalLines.HIMEZ:
                    AddIntegralAreaModifier(tg, label, area, -2.5, ptCenter, ptCenter, false, "");
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, -1.5, ptCenter, ptCenter, false, "T");
                    AddIntegralAreaModifier(tg, "MIN ALT: " + tg.get_H(), area, -0.5, ptCenter, ptCenter, false, "H");
                    AddIntegralAreaModifier(tg, "MAX ALT: " + tg.get_H1(), area, 0.5, ptCenter, ptCenter, false, "H1");
                    AddIntegralAreaModifier(tg, "TIME FROM: " + tg.get_DTG(), area, 1.5, ptCenter, ptCenter, false, "W");
                    AddIntegralAreaModifier(tg, "TIME TO: " + tg.get_DTG1(), area, 2.5, ptCenter, ptCenter, false, "W1");
                    break;
                case TacticalLines.SAAFR:
                    pt0 = new POINT2(tg.Pixels.get(middleSegment));
                    dist = pt0.style / tg.get_Font().getSize();
                    dist /= 2;
                    if (tg.getSymbologyStandard() == RendererSettings.Symbology_2525C) {
                        AddIntegralModifier(tg, "SAAFR " + tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Max Alt: " + tg.get_H1(), aboveMiddle, -4 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Min Alt: " + tg.get_H(), aboveMiddle, -5 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Width: " + tg.get_H2(), aboveMiddle, -6 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Name: " + tg.get_Name(), aboveMiddle, -7 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "DTG Start: " + tg.get_DTG(), aboveMiddle, -3 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "DTG End: " + tg.get_DTG1(), aboveMiddle, -2 * csFactor - dist, middleSegment, middleSegment + 1, false);
                    } else {
                        AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Max Alt: " + tg.get_H1(), aboveMiddle, -2 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Min Alt: " + tg.get_H(), aboveMiddle, -3 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Width: " + tg.get_H2(), aboveMiddle, -4 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Name: " + tg.get_Name(), aboveMiddle, -5 * csFactor - dist, middleSegment, middleSegment + 1, false);
                    }
                    break;
                case TacticalLines.AC:
                    pt0 = new POINT2(tg.Pixels.get(middleSegment));
                    dist = pt0.style / tg.get_Font().getSize();
                    dist /= 2;
                    if (tg.getSymbologyStandard() == RendererSettings.Symbology_2525C) {
                        AddIntegralModifier(tg, label + " " + tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Max Alt: " + tg.get_H1(), aboveMiddle, -4 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Min Alt: " + tg.get_H(), aboveMiddle, -5 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Width: " + tg.get_H2(), aboveMiddle, -6 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "Name: " + tg.get_Name(), aboveMiddle, -7 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "DTG Start: " + tg.get_DTG(), aboveMiddle, -3 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, "DTG End: " + tg.get_DTG1(), aboveMiddle, -2 * csFactor - dist, middleSegment, middleSegment + 1, false);
                    } else {
                        //AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, -0.5 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        //AddIntegralModifier(tg, tg.get_T1(), aboveMiddle, 0.5 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, -1.0 * csFactor - dist, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, tg.get_T1(), aboveMiddle, 1.0 * csFactor - dist, middleSegment, middleSegment + 1, false);
                    }
                    break;
                case TacticalLines.MRR_USAS:
                case TacticalLines.UAV_USAS:
                case TacticalLines.MRR:
                case TacticalLines.UAV:
                case TacticalLines.LLTR:
                    if (tg.getSymbologyStandard() == RendererSettings.Symbology_2525C) {
                        AddIntegralModifier(tg, label + " " + tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);

                        pt0 = new POINT2(tg.Pixels.get(middleSegment));
                        pt1 = new POINT2(tg.Pixels.get(middleSegment + 1));
                        if (pt0.y < pt1.y) {
                            pt1.y = pt0.y;
                        } else {
                            pt0.y = pt1.y;
                        }
                        pt0.y -= pt0.style / 2;
                        pt1.y -= pt0.style / 2;
                        //AddIntegralAreaModifier(tg, label + " " + tg.get_Name(), aboveMiddle, 0, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, "Max Alt: " + tg.get_H1(), aboveMiddle, -4 * csFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, "Min Alt: " + tg.get_H(), aboveMiddle, -5 * csFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, "Width: " + tg.get_H2(), aboveMiddle, -6 * csFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, "Name: " + tg.get_Name(), aboveMiddle, -7 * csFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, "DTG Start: " + tg.get_DTG(), aboveMiddle, -3 * csFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, "DTG End: " + tg.get_DTG1(), aboveMiddle, -2 * csFactor, pt0, pt1, false);

                    } else {
                        AddIntegralModifier(tg, label, aboveMiddle, -0.5, middleSegment, middleSegment + 1, false);
                        AddIntegralModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 0.5, middleSegment, middleSegment + 1, false);
                    }
                    break;
                case TacticalLines.AIRFIELD:
//                    if (affiliation != null && affiliation.equals("H"))
//                    {
//                        for (j = 0; j < tg.Pixels.size() - 1; j++) {
//                            AddIntegralModifier(tg, tg.get_N(), aboveMiddle, 0, j, j + 1,true);
//                        }
//                    }
                    areasWithENY(tg, g2d);
                    break;
                case TacticalLines.GENERAL:
//                    if (affiliation != null && affiliation.equals("H"))
//                    {
//                        for (j = 0; j < tg.Pixels.size() - 1; j++)
//                        {
//                            AddIntegralModifier(tg, tg.get_N(), aboveMiddle, 0, j, j + 1,true);
//                        }
//                    }
                    areasWithENY(tg, g2d);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.FORT:
                case TacticalLines.ENCIRCLE:
                    if (affiliation != null && affiliation.equals("H")) {
                        AddIntegralModifier(tg, tg.get_N(), aboveMiddle, 0, 0, 1, true);
                        AddIntegralModifier(tg, tg.get_N(), aboveMiddle, 0, middleSegment, middleSegment + 1, true);
                    }
                    break;
                case TacticalLines.ASSY:
                case TacticalLines.EA:
                case TacticalLines.DZ:
                case TacticalLines.EZ:
                case TacticalLines.LZ:
                case TacticalLines.PZ:
                    areasWithENY(tg, g2d);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, label, area, -1 * csFactor, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.BOUNDARY:
                    //AddBoundaryModifiers(tg,g2d,clipBounds);
                    if (clipRect != null) {
                        AddBoundaryModifiers(tg, g2d, clipRect);
                    } else if (clipArray != null) {
                        AddBoundaryModifiers(tg, g2d, clipArray);
                    } else {
                        AddBoundaryModifiers(tg, g2d, (Rectangle2D) null);
                    }
                    break;
                case TacticalLines.DMA:
                case TacticalLines.DMAF:
                    areasWithENY(tg, g2d);
                    if (affiliation != null && affiliation.equals("H")) {
                        pt1 = lineutility.MidPointDouble(pt0, pt1, 0);
                        AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, pt1, true);
                        if (middleSegment != 0) {
                            pt0 = tg.Pixels.get(middleSegment);
                            pt1 = tg.Pixels.get(middleSegment + 1);
                            pt1 = lineutility.MidPointDouble(pt0, pt1, 0);
                            AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, pt1, true);
                        }
                    }
                    break;
                case TacticalLines.MINED:
                    areasWithENY(tg, g2d);
                    if (affiliation != null && affiliation.equals("H")) {
                        pt1 = lineutility.MidPointDouble(pt0, pt1, 0);
                        AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, pt1, true);
                        if (middleSegment != 0) {
                            pt0 = tg.Pixels.get(middleSegment);
                            pt1 = tg.Pixels.get(middleSegment + 1);
                            pt1 = lineutility.MidPointDouble(pt0, pt1, 0);
                            AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, pt1, true);
                        }
                    }
                    break;
                case TacticalLines.PL:
                    //AddIntegralModifier(tg, label + tg.get_Name(), toEnd, T1LineFactor, 0, 1,false);
                    //AddIntegralModifier(tg, label + tg.get_Name(), toEnd, T1LineFactor, lastIndex, nextToLastIndex,false);
                    AddIntegralAreaModifier(tg, label + tg.get_Name(), toEnd, T1LineFactor, pt0, pt1, false);
                    AddIntegralAreaModifier(tg, label + tg.get_Name(), toEnd, T1LineFactor, ptLast, ptNextToLast, false);
                    break;
                case TacticalLines.BS_LINE:
                case TacticalLines.BBS_LINE:
                    if (tg.get_T1() == null || tg.get_T1().isEmpty()) {
                        AddIntegralAreaModifier(tg, tg.get_Name(), toEnd, T1LineFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, tg.get_Name(), toEnd, T1LineFactor, ptLast, ptNextToLast, false);
                    } else {
                        if (tg.get_T1().equalsIgnoreCase("1")) {
                            for (j = 0; j < tg.Pixels.size() - 1; j++) {
                                AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, 0, tg.Pixels.get(j), tg.Pixels.get(j + 1), false);
                            }
                        } else if (tg.get_T1().equalsIgnoreCase("2")) {
                            AddIntegralAreaModifier(tg, tg.get_Name(), toEnd, T1LineFactor, pt0, pt1, false);
                            AddIntegralAreaModifier(tg, tg.get_Name(), toEnd, T1LineFactor, ptLast, ptNextToLast, false);
                        } else if (tg.get_T1().equalsIgnoreCase("3")) {
                            //either end of the polyline
                            dist = lineutility.CalcDistanceDouble(pt0, pt1);
                            stringWidth = metrics.stringWidth(tg.get_Name());
                            stringWidth /= 2;
                            pt2 = lineutility.ExtendAlongLineDouble2(pt1, pt0, dist + stringWidth);
                            AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, pt2, pt2, false);
                            dist = lineutility.CalcDistanceDouble(ptNextToLast, ptLast);
                            pt2 = lineutility.ExtendAlongLineDouble2(ptNextToLast, ptLast, dist + stringWidth);
                            AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, pt2, pt2, false);
                            //the intermediate points
                            for (j = 1; j < tg.Pixels.size() - 1; j++) {
                                AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, tg.Pixels.get(j), tg.Pixels.get(j), false);
                            }
                        } else //t1 is set inadvertantly or for other graphics
                        {
                            AddIntegralAreaModifier(tg, tg.get_Name(), toEnd, T1LineFactor, pt0, pt1, false);
                            AddIntegralAreaModifier(tg, tg.get_Name(), toEnd, T1LineFactor, ptLast, ptNextToLast, false);
                        }
                    }
                    break;
                case TacticalLines.BS_AREA:
                case TacticalLines.BBS_AREA:
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.LOA:
                case TacticalLines.LOD:
                case TacticalLines.LDLC:
                    if (!tg.get_Name().isEmpty()) {
                        AddIntegralAreaModifier(tg, "(PL " + tg.get_Name() + ")", toEnd, 1 * csFactor, pt0, pt1, false);
                    }
                    AddIntegralAreaModifier(tg, label, toEnd, 0, pt0, pt1, false);
                    if (!tg.get_Name().isEmpty()) {
                        AddIntegralAreaModifier(tg, "(PL " + tg.get_Name() + ")", toEnd, 1 * csFactor, ptLast, ptNextToLast, false);
                    }
                    AddIntegralAreaModifier(tg, label, toEnd, 0, ptLast, ptNextToLast, false);
                    break;
                case TacticalLines.RELEASE:
                    //AddIntegralAreaModifier(tg, "(PL " + tg.get_Name()+ ")", toEnd, 1*csFactor, pt0, pt1,false);
                    AddIntegralAreaModifier(tg, label, toEnd, -csFactor, pt0, pt1, false);
                    //AddIntegralAreaModifier(tg, "(PL " + tg.get_Name() +")", toEnd, 1*csFactor, ptLast, ptNextToLast,false);
                    AddIntegralAreaModifier(tg, label, toEnd, -csFactor, ptLast, ptNextToLast, false);
                    break;
                case TacticalLines.LL:
                case TacticalLines.FCL:
                case TacticalLines.PLD:
                    //uncomment one line for USAS
                    usas = true;
                    if (usas == false) {
                        AddIntegralAreaModifier(tg, "(PL " + tg.get_Name() + ")", toEnd, 1 * csFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, label, toEnd, 0, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, "(PL " + tg.get_Name() + ")", toEnd, 1 * csFactor, ptLast, ptNextToLast, false);
                        AddIntegralAreaModifier(tg, label, toEnd, 0, ptLast, ptNextToLast, false);
                    } else {
                        //AddNameAboveDTG(tg,metrics);
                        AddIntegralAreaModifier(tg, label, toEnd, -csFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, label, toEnd, -csFactor, ptLast, ptNextToLast, false);
                    }
                    break;
                case TacticalLines.NFL:
                    //uncomment one line for usas
                    usas = true;
                    if (usas == false) {
                        AddIntegralAreaModifier(tg, "(PL " + tg.get_Name() + ")", toEnd, 1 * csFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, label, toEnd, 0, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, "(PL " + tg.get_Name() + ")", toEnd, 1 * csFactor, ptLast, ptNextToLast, false);
                        AddIntegralAreaModifier(tg, label, toEnd, 0, ptLast, ptNextToLast, false);
                    } else {
                        //AddNameAboveDTG(tg,metrics);
                        AddIntegralAreaModifier(tg, label, toEnd, -csFactor, pt0, pt1, false);
                        AddIntegralAreaModifier(tg, label, toEnd, -csFactor, ptLast, ptNextToLast, false);
                    }
                    break;
                case TacticalLines.FSCL:
                    pt0 = tg.Pixels.get(0);
                    pt1 = tg.Pixels.get(1);
                    pt2 = tg.Pixels.get(tg.Pixels.size() - 1);
                    pt3 = tg.Pixels.get(tg.Pixels.size() - 2);
                    dist = lineutility.CalcDistanceDouble(pt0, pt1);
                    dist2 = lineutility.CalcDistanceDouble(pt2, pt3);
                    stringWidth = (int) ((double) metrics.stringWidth(tg.get_Name() + " " + label));
                    stringWidth2 = (int) ((double) metrics.stringWidth(tg.get_DTG()));
                    if (stringWidth2 > stringWidth) {
                        stringWidth = stringWidth2;
                    }

                    if (tg.Pixels.size() == 2) //one segment
                    {
                        pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                        //pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1.7 * stringWidth);
                        AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        if (dist > 3.5 * stringWidth)//was 28stringwidth+5
                        {
                            pt0 = tg.Pixels.get(tg.Pixels.size() - 1);
                            pt1 = tg.Pixels.get(tg.Pixels.size() - 2);
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1.7 * stringWidth);
                            AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                    } else //more than one semgent
                    {
                        double dist3 = lineutility.CalcDistanceDouble(pt0, pt2);
                        if (dist > stringWidth + 5 || dist >= dist2 || dist3 > stringWidth + 5) {
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1.7 * stringWidth);
                            AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                        if (dist2 > stringWidth + 5 || dist2 > dist || dist3 > stringWidth + 5) {
                            pt0 = tg.Pixels.get(tg.Pixels.size() - 1);
                            pt1 = tg.Pixels.get(tg.Pixels.size() - 2);
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1.7 * stringWidth);
                            AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                    }
                    break;
                case TacticalLines.CFL:
                    //AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -0.7*csFactor, middleSegment, middleSegment + 1,false);
                    //AddIntegralModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 0.7*csFactor, middleSegment, middleSegment + 1,false);
                    stringWidth = (int) ((double) metrics.stringWidth(label + tg.get_Name()));
                    stringWidth2 = (int) ((double) metrics.stringWidth(tg.get_DTG() + dash + tg.get_DTG1()));
                    if (stringWidth2 > stringWidth) {
                        stringWidth = stringWidth2;
                    }
//                    pt0=tg.Pixels.get(middleSegment);
//                    pt1=tg.Pixels.get(middleSegment+1);
//                    dist=lineutility.CalcDistanceDouble(pt0, pt1);
//                    if(dist<1.5*stringWidth && tg.Pixels.size()>2)
//                    {
//                        pt0=lineutility.ExtendAlongLineDouble(pt1, pt0, 1.7*stringWidth);
//                        AddModifier2(tg, label + tg.get_Name(), aboveMiddle, -0.7*csFactor, pt0, pt1,false);
//                        AddModifier2(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 0.7*csFactor, pt0, pt1,false);
//                    }
//                    else
//                    {
//                        AddIntegralModifier(tg, label + tg.get_Name(), aboveMiddle, -0.7*csFactor, middleSegment, middleSegment + 1,false);
//                        AddIntegralModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 0.7*csFactor, middleSegment, middleSegment + 1,false);                        
//                    }
                    pt0 = new POINT2(tg.Pixels.get(middleSegment));
                    pt1 = new POINT2(tg.Pixels.get(middleSegment + 1));
                    getPixelsMiddleSegment(tg, stringWidth, pt0, pt1);
                    AddModifier2(tg, label + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                    AddModifier2(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                    break;
                case TacticalLines.RFL:
                    pt0 = tg.Pixels.get(0);
                    pt1 = tg.Pixels.get(1);
                    pt2 = tg.Pixels.get(tg.Pixels.size() - 1);
                    pt3 = tg.Pixels.get(tg.Pixels.size() - 2);
                    dist = lineutility.CalcDistanceDouble(pt0, pt1);
                    dist2 = lineutility.CalcDistanceDouble(pt2, pt3);
                    stringWidth = (int) ((double) metrics.stringWidth(label + " " + tg.get_Name()));
                    stringWidth2 = (int) ((double) metrics.stringWidth(tg.get_DTG()));
                    if (stringWidth2 > stringWidth) {
                        stringWidth = stringWidth2;
                    }

                    if (tg.Pixels.size() == 2) //one segment
                    {
                        //diagnostic
                        //pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1.7 * stringWidth);
                        pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1 * stringWidth);
                        //end section
                        AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        if (dist > 3.5 * stringWidth)//was 2*stringwidth+5
                        {
                            pt0 = tg.Pixels.get(tg.Pixels.size() - 1);
                            pt1 = tg.Pixels.get(tg.Pixels.size() - 2);
                            //diagnostic
                            //pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1.7 * stringWidth);
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1 * stringWidth);
                            //end section
                            AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);

                        }
                    } else //more than one semgent
                    {
                        double dist3 = lineutility.CalcDistanceDouble(pt0, pt2);
                        if (dist > stringWidth + 5 || dist >= dist2 || dist3 > stringWidth + 5) {
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1.7 * stringWidth);
                            AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                        if (dist2 > stringWidth + 5 || dist2 > dist || dist3 > stringWidth + 5) {
                            pt0 = tg.Pixels.get(tg.Pixels.size() - 1);
                            pt1 = tg.Pixels.get(tg.Pixels.size() - 2);
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, 1.7 * stringWidth);
                            AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                    }
                    break;
                case TacticalLines.FLOT://if usas then ENY has to be above or below by quadrant
                    //AddIntegralModifier(tg, label, toEnd, 0, 0, 1,false);
                    AddIntegralAreaModifier(tg, label, toEnd, 0, pt0, pt1, false);
                    if (affiliation != null && affiliation.equals("H")) {
                        AddIntegralAreaModifier(tg, tg.get_N(), toEnd, -1 * csFactor, pt0, pt1, false);
                    }
                    AddIntegralAreaModifier(tg, label, toEnd, 0, ptLast, ptNextToLast, false);
                    if (affiliation != null && affiliation.equals("H")) {
                        AddIntegralAreaModifier(tg, tg.get_N(), toEnd, -1 * csFactor, ptLast, ptNextToLast, false);
                    }
                    break;
                case TacticalLines.LC:
                    double shiftFactor = 1d;
                    if (shiftLines) {
                        shiftFactor = 0.5d;
                    }
                    if (affiliation != null && affiliation.equals("H")) {
                        if (pt0.x < pt1.x) {
                            TLineFactor = -shiftFactor;//was -1
                        } else {
                            TLineFactor = shiftFactor;//was 1
                        }
                        //AddIntegralModifier(tg, tg.get_N(), toEnd, TLineFactor, 0, 1,false);
                        AddIntegralAreaModifier(tg, tg.get_N(), toEnd, TLineFactor, pt0, pt1, false);
                        if (ptNextToLast.x < ptLast.x) {
                            TLineFactor = -shiftFactor;//was -1
                        } else {
                            TLineFactor = shiftFactor;//was 1
                        }
                        //AddIntegralModifier(tg, tg.get_N(), toEnd, TLineFactor, lastIndex, nextToLastIndex,false);
                        AddIntegralAreaModifier(tg, tg.get_N(), toEnd, TLineFactor, ptLast, ptNextToLast, false);
                    }
                    break;
                case TacticalLines.CATK:
                    //case TacticalLines.CATKBYFIRE:
                    AddIntegralModifier(tg, label, aboveMiddle, 0, 1, 0, false);
                    break;
                case TacticalLines.CATKBYFIRE:
                    //pt2=lineutility.MidPointDouble(pt0, pt1, 0);
                    stringWidth = (int) (1.5 * (double) metrics.stringWidth(label));
                    pt2 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                    AddModifier2(tg, label, aboveMiddle, 0, pt1, pt2, false);
                    break;
                case TacticalLines.DIRATKFNT:
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, -0.7 * csFactor, 1, 0, false);
                    break;
                case TacticalLines.AAFNT:
                    //AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, 0, 1, 0,false);
                    midPt = lineutility.MidPointDouble(tg.Pixels.get(lastIndex - 1), tg.Pixels.get(nextToLastIndex - 1), 0);
                    AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, 0, tg.Pixels.get(lastIndex - 1), midPt, false);
                    break;
                case TacticalLines.IL:
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, 0, 1, 0, false);
                    break;
                case TacticalLines.DRCL:
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, 0, 1, 0, true);
                    break;
                case TacticalLines.RETIRE:
                case TacticalLines.WITHDRAW:
                case TacticalLines.WDRAWUP:
                    AddIntegralModifier(tg, label, aboveMiddle, 0, 0, 1, true);
                    break;
                case TacticalLines.BEARING:
                case TacticalLines.ELECTRO:
                case TacticalLines.ACOUSTIC:
                case TacticalLines.TORPEDO:
                case TacticalLines.OPTICAL:
                    AddIntegralAreaModifier(tg, label, aboveMiddle, 0, tg.Pixels.get(0), tg.Pixels.get(1), true);
                    break;
                case TacticalLines.RIP:
                case TacticalLines.BOMB:
                case TacticalLines.TGMF:
                    AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter, true);
                    break;
                case TacticalLines.BELT:
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, -0.5 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_T1(), area, 0.5 * csFactor, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.BELT1:
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);
                    break;
                case TacticalLines.ZONE:
                case TacticalLines.AT:
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.STRONG:
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    AddIntegralModifier(tg, tg.get_EchelonSymbol(), aboveMiddle, 0, 0, 1, true);
                    break;
                case TacticalLines.MSDZ:
                    AddIntegralAreaModifier(tg, "1", area, 0, pt1, pt1, true);
                    AddIntegralAreaModifier(tg, "2", area, 0, pt2, pt2, true);
                    AddIntegralAreaModifier(tg, "3", area, 0, pt3, pt3, true);
                    break;
                case TacticalLines.RECTANGULAR:
                case TacticalLines.CIRCULAR:
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, pt0, pt0, false);
                    break;
                case TacticalLines.DELAY:
                    AddIntegralModifier(tg, tg.get_DTG(), aboveMiddle, -1 * csFactor, 0, 1, false);
                    AddIntegralModifier(tg, label, aboveMiddle, 0, 0, 1, true);
                    break;
                default:
                    break;
            }
            tg.Pixels = origPoints;
            scaleModifiers(tg);
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.AddModifiers");
            ErrorLogger.LogException(_className, "AddModifiers",
                    new RendererException("Failed inside AddModifiers", exc));
        }

    }

    /**
     * RFA, NFA, FFA need these for line spacing
     *
     * @param tg
     * @return
     */
    private static int getRFALines(TGLight tg) {
        int lines = 1;
        try {
            if (tg.get_Name() != null && !tg.get_Name().isEmpty()) {
                lines++;
            }
            if (tg.get_DTG() != null && !tg.get_DTG().isEmpty()) {
                lines++;
            } else if (tg.get_DTG1() != null && !tg.get_DTG1().isEmpty()) {
                lines++;
            }

            switch (tg.get_LineType()) {
                case TacticalLines.KILLBOXBLUE_RECTANGULAR:
                case TacticalLines.KILLBOXPURPLE_RECTANGULAR:
                    if (tg.get_H1() != null && !tg.get_H1().isEmpty()) {
                        lines++;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "AddModifiers",
                    new RendererException("Failed inside AddModifiers", exc));
        }
        return lines;
    }

    /**
     * Channels don't return points in tg.Pixels. For Channels modifiers we only
     * need to collect the points, don't need internal arrays, and can calculate
     * on which segments the modifiers lie.
     *
     * @param shape
     * @return
     */
    private static ArrayList<POINT2> getShapePoints(Shape shape) {
        try {
            ArrayList<Point2D> ptsPoly = new ArrayList();
            Point2D ptPoly = null;
            float[] coords = new float[6];
            int zeros = 0;
            for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()) {
                int type = i.currentSegment(coords);
                if (type == 0 && zeros == 2) {
                    break;
                }
                switch (type) {
                    case PathIterator.SEG_MOVETO:
                        ptPoly = new Point2D.Double(coords[0], coords[1]);
                        ptsPoly.add(ptPoly);
                        zeros++;
                        break;
                    case PathIterator.SEG_LINETO:
                        ptPoly = new Point2D.Double(coords[0], coords[1]);
                        ptsPoly.add(ptPoly);
                        break;
                    case PathIterator.SEG_QUADTO: //quadTo was never used
                        break;
                    case PathIterator.SEG_CUBICTO:  //curveTo was used for HOLD, BRDGHD and some METOC's
                        break;
                    case PathIterator.SEG_CLOSE:    //closePath was never used
                        break;
                }
            }
            if (ptsPoly.size() > 0) {
                ArrayList<POINT2> pts = null;
                pts = new ArrayList();
                for (int j = 0; j < ptsPoly.size(); j++) {
                    Point2D pt2d = ptsPoly.get(j);
                    POINT2 pt = new POINT2(pt2d.getX(), pt2d.getY());
                    pts.add(pt);
                }
                return pts;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "getshapePoints",
                    new RendererException("Failed inside getShapePoints", exc));
        }
        return null;
    }

    /**
     * Handles rev D codes
     *
     * @param tg
     */
    public static void AddModifiers2RevD(TGLight tg, ArrayList<Shape2> shapes) {
        if (tg.get_SymbolId().length() < 20) {
            Modifier2.AddModifiers2(tg);
            return;
        }
        try {
            String symbolId = tg.get_SymbolId();
            //String setA = getSetA(tg.get_SymbolId());
            String setA = symbolId.substring(0, 10);
            //String setB = getSetB(tg.get_SymbolId());
            String setB = symbolId.substring(10);
            //String code = getCode(setB);
            String code = setB.substring(0, 6);
            int nCode = Integer.parseInt(code);
            //String symbolSet = getSymbolSet(setA);
            String symbolSet = setA.substring(4, 6);
            int nSymbol = Integer.parseInt(symbolSet);
            //default values for modifiers AP and V
            String country = "US";  //country AS modifier
            String v = "MORTAR";    //type            
            String ap = "QC 1968";    //target designator    AP modifier
            POINT2 pt0 = null, pt1 = null;
            double csFactor = 1d;
            int n = tg.Pixels.size();
            POINT2 ptLeft = null, ptRight = null, ptCenter = null;
            String label = getRevDLabel(nCode);
            String dash = " - ";
//            POINT2 ptUl=null,ptUr=null,ptLl=null,ptLr=null;
            switch (nCode) {
                case 200202:
                    ptLeft = lineutility.MidPointDouble(tg.Pixels.get(0), tg.Pixels.get(1), 0);
                    ptRight = lineutility.MidPointDouble(tg.Pixels.get(2), tg.Pixels.get(3), 0);
                    AddIntegralAreaModifier(tg, label + " - " + tg.get_Name(), aboveMiddle, -csFactor / 2, ptLeft, ptRight, false);
                    break;
                case 290600:
                    //pt0=tg.Pixels.get(7);
                    //pt1=tg.Pixels.get(5);
                    pt0 = tg.Pixels.get(4);
                    pt1 = tg.Pixels.get(2);
                    if (tg.Pixels.get(0).y < tg.Pixels.get(1).y) {
                        AddIntegralAreaModifier(tg, tg.get_DTG() + " - " + tg.get_DTG1(), aboveMiddle, csFactor / 2, pt0, pt1, false);
                    } else {
                        AddIntegralAreaModifier(tg, tg.get_DTG() + " - " + tg.get_DTG1(), aboveMiddle, -csFactor / 2, pt0, pt1, false);
                    }
                    break;
                case 200402:
                    if (tg.Pixels.get(0).x > tg.Pixels.get(3).x) {
                        AddIntegralAreaModifier(tg, label, aboveMiddle, csFactor, tg.Pixels.get(0), tg.Pixels.get(3), false);
                    } else {
                        AddIntegralAreaModifier(tg, label, aboveMiddle, csFactor, tg.Pixels.get(1), tg.Pixels.get(2), false);
                    }
                    break;
                case 141500:
                case 141400:
                case 200300:
                case 240804:
                    break;
                case 151407:    //eny spt confirmed
                case 151408:    //eny spt anticipated
                    Shape2 shape = shapes.get(shapes.size() - 1);
                    ArrayList<POINT2> pts = getShapePoints(shape.getShape());
                    n = pts.size(); //was tg.Pixels.size()
                    if (n == 4) //was 3
                    {
                        pt0 = pts.get(0);
                        pt1 = pts.get(1);
                        pt1 = lineutility.MidPointDouble(pt0, pt1, 0);
                    } else if (n == 6) {
                        pt0 = pts.get(3);
                        pt1 = pts.get(4);
                    } else {
                        pt0 = pts.get(1);   //was n-4
                        pt1 = pts.get(2);   //was n-3                     
                    }
                    AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, pt1, false);
                    if (n == 4) {
                        pt0 = pts.get(2);  //was pts.size()-9
                        pt1 = pts.get(3);  //was pts.size()-8
                        pt1 = lineutility.MidPointDouble(pt0, pt1, 0);
                    } else if (n == 6) {
                        pt0 = pts.get(0);
                        pt1 = pts.get(1);
                    } else {
                        pt0 = pts.get(n / 2 + 1); //was pts.size()-10
                        pt1 = pts.get(n / 2 + 2);  //was pts.get(pts.size()-9
                    }
                    AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt0, pt1, false);
                    break;
                default:
                    int saveStd = tg.getSymbologyStandard();
                    tg.setSymbologyStandard(RendererSettings.Symbology_2525C);
                    AddModifiers2(tg);
                    tg.setSymbologyStandard(saveStd);
                    break;
            }

        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.AddModifiers");
            ErrorLogger.LogException(_className, "AddModifiers2RevD",
                    new RendererException("Failed inside AddModifiers2RevD", exc));
        }
    }

    /**
     * Called by the renderer after tg.Pixels has been filled with the
     * calculated points. The modifier path depends on points calculated by
     * CELineArray.
     *
     * @param tg
     */
    public static void AddModifiers2(TGLight tg) {
        try {
            if (tg.Pixels == null || tg.Pixels.isEmpty()) {
                return;
            }
            //diagnostic
            //add early return for linetypes not affected
            switch (tg.get_LineType()) {
                case TacticalLines.BS_RECTANGLE:
                case TacticalLines.BBS_RECTANGLE:
                case TacticalLines.BREACH:
                case TacticalLines.BYPASS:
                case TacticalLines.CANALIZE:
                case TacticalLines.PENETRATE:
                case TacticalLines.CLEAR:
                case TacticalLines.DISRUPT:
                case TacticalLines.FIX:
                case TacticalLines.ISOLATE:
                case TacticalLines.OCCUPY:
                case TacticalLines.RETAIN:
                case TacticalLines.SECURE:
                case TacticalLines.CONTAIN:
                case TacticalLines.SEIZE:
                case TacticalLines.SEIZE_REVC:
                case TacticalLines.CORDONKNOCK:
                case TacticalLines.CORDONSEARCH:
                case TacticalLines.FOLLA:
                case TacticalLines.FOLSP:
                case TacticalLines.ACA_RECTANGULAR:
                case TacticalLines.ACA_CIRCULAR:
                case TacticalLines.RECTANGULAR:
                case TacticalLines.CIRCULAR:
                case TacticalLines.BBS_POINT:
                case TacticalLines.FSA_CIRCULAR:
                case TacticalLines.ATI_CIRCULAR:
                case TacticalLines.CFFZ_CIRCULAR:
                case TacticalLines.SENSOR_CIRCULAR:
                case TacticalLines.CENSOR_CIRCULAR:
                case TacticalLines.DA_CIRCULAR:
                case TacticalLines.CFZ_CIRCULAR:
                case TacticalLines.ZOR_CIRCULAR:
                case TacticalLines.TBA_CIRCULAR:
                case TacticalLines.TVAR_CIRCULAR:
                case TacticalLines.FFA_CIRCULAR:
                case TacticalLines.NFA_CIRCULAR:
                case TacticalLines.RFA_CIRCULAR:
                case TacticalLines.KILLBOXBLUE_CIRCULAR:
                case TacticalLines.KILLBOXPURPLE_CIRCULAR:
                case TacticalLines.BLOCK:
                case TacticalLines.HOLD:
                case TacticalLines.BRDGHD:
                case TacticalLines.HOLD_GE:
                case TacticalLines.BRDGHD_GE:
                case TacticalLines.FFA_RECTANGULAR:
                case TacticalLines.NFA_RECTANGULAR:
                case TacticalLines.RFA_RECTANGULAR:
                case TacticalLines.KILLBOXBLUE_RECTANGULAR:
                case TacticalLines.KILLBOXPURPLE_RECTANGULAR:
                case TacticalLines.FSA_RECTANGULAR:
                case TacticalLines.ATI_RECTANGULAR:
                case TacticalLines.CFFZ_RECTANGULAR:
                case TacticalLines.SENSOR_RECTANGULAR:
                case TacticalLines.CENSOR_RECTANGULAR:
                case TacticalLines.DA_RECTANGULAR:
                case TacticalLines.CFZ_RECTANGULAR:
                case TacticalLines.ZOR_RECTANGULAR:
                case TacticalLines.TBA_RECTANGULAR:
                case TacticalLines.TVAR_RECTANGULAR:
                case TacticalLines.PAA_RECTANGULAR:
                case TacticalLines.PAA_RECTANGULAR_REVC:
                case TacticalLines.PAA_CIRCULAR:
                case TacticalLines.RANGE_FAN:
                case TacticalLines.RANGE_FAN_SECTOR:
                    break;
                default:
                    return;
            }
            //end section
            ArrayList<POINT2> origPoints = lineutility.getDeepCopy(tg.Pixels);
            if (tg.modifiers == null) {
                tg.modifiers = new ArrayList();
            }
            Font font = tg.get_Font();
            POINT2 ptCenter = null;
            double csFactor = 1d;//this will be used for text spacing the 3d map (CommandCight)
            //String affiliation=tg.get_Affiliation();
            int linetype = tg.get_LineType();
            POINT2 pt0 = null, pt1 = null, pt2 = null, pt3 = null;
            int j = 0, k = 0;
            double dist = 0;
            String label = GetCenterLabel(tg);
            String[] H1 = null;
            int lastIndex = tg.Pixels.size() - 1;
            int nextToLastIndex = 0;
            if (tg.Pixels.size() > 1) {
                nextToLastIndex = tg.Pixels.size() - 2;
            }
            POINT2 ptLast = new POINT2(tg.Pixels.get(lastIndex));
            POINT2 ptNextToLast = null;
            if (tg.Pixels.size() > 1) {
                ptNextToLast = new POINT2(tg.Pixels.get(nextToLastIndex));
            }
            String dash = "";
            if (tg.get_DTG() != null && tg.get_DTG1() != null && tg.get_DTG().isEmpty() == false && tg.get_DTG1().isEmpty() == false) {
                dash = " - ";
            }

            POINT2 ptLeft = null, ptRight = null;
            BufferedImage bi = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            FontMetrics metrics = g2d.getFontMetrics();
            int stringWidth = 0, rfaLines = 0;
            pt0 = new POINT2(tg.Pixels.get(0));
            if (tg.Pixels.size() > 1) {
                pt1 = new POINT2(tg.Pixels.get(1));
            }

            POINT2[] pts = null;
            // if the client is the 3d map (CS) then we want to shrink the spacing bnetween 
            // the lines of text
            //if(tg.get_cs())
            //csFactor=0.667;
            if (tg.get_Client().equals("cpof3d")) {
                csFactor = 0.9d;
            }

            shiftModifierPath(tg, pt0, pt1, ptLast, ptNextToLast);
            //added section 3-12-12
            //if(tg.get_Revision().equalsIgnoreCase("C"))
            if (tg.getSymbologyStandard() == RendererSettings.Symbology_2525C) {
                switch (linetype) {
                    case TacticalLines.BS_RECTANGLE:
                    case TacticalLines.BBS_RECTANGLE:
                        pts = new POINT2[4];
                        for (j = 0; j < 4; j++) {
                            pts[j] = tg.Pixels.get(j);
                        }
                        ptCenter = lineutility.CalcCenterPointDouble2(pts, 4);
                        AddIntegralAreaModifier(tg, tg.get_Name(), area, -0.125 * csFactor, ptCenter, ptCenter, false);
                        break;
                    case TacticalLines.BREACH:
                    case TacticalLines.BYPASS:
                    case TacticalLines.CANALIZE:
                        pt0 = tg.Pixels.get(1);
                        pt1 = tg.Pixels.get(2);
                        //pt1=lineutility.ExtendAlongLineDouble(pt1, pt0, -10);
                        AddIntegralAreaModifier(tg, label, aboveMiddle, -0.125 * csFactor, pt0, pt1, true);
                        break;
                    case TacticalLines.PENETRATE:
                    case TacticalLines.CLEAR:
                        pt0 = tg.Pixels.get(2);
                        pt1 = tg.Pixels.get(3);
                        //pt1=lineutility.ExtendAlongLineDouble(pt1, pt0, -10);
                        AddIntegralAreaModifier(tg, label, aboveMiddle, -0.125 * csFactor, pt0, pt1, true);
                        break;
                    case TacticalLines.DISRUPT:
                        pt0 = tg.Pixels.get(4);
                        pt1 = tg.Pixels.get(5);
                        //pt1=lineutility.ExtendAlongLineDouble(pt1, pt0, -10);
                        AddIntegralAreaModifier(tg, label, aboveMiddle, -0.125 * csFactor, pt0, pt1, true);
                        break;
                    case TacticalLines.FIX:
                        pt0 = tg.Pixels.get(0);
                        pt1 = tg.Pixels.get(1);
                        //pt1=lineutility.ExtendAlongLineDouble(pt1, pt0, -10);
                        AddIntegralAreaModifier(tg, label, aboveMiddle, -0.125 * csFactor, pt0, pt1, true);
                        break;
                    case TacticalLines.ISOLATE:
                    case TacticalLines.OCCUPY:
                    case TacticalLines.RETAIN:
                    case TacticalLines.SECURE:
                        pt0 = tg.Pixels.get(13);
                        pt1 = tg.Pixels.get(14);
                        //pt1=lineutility.ExtendAlongLineDouble(pt1, pt0, -10);
                        AddIntegralAreaModifier(tg, label, aboveMiddle, -0.125 * csFactor, pt0, pt1, true);
                        break;
                    case TacticalLines.CONTAIN:
                        pt0 = tg.Pixels.get(13);
                        pt1 = tg.Pixels.get(14);
                        //pt1=lineutility.ExtendAlongLineDouble(pt1, pt0, -10);
                        AddIntegralAreaModifier(tg, "C", aboveMiddle, -0.125 * csFactor, pt0, pt1, true);
                        break;
                    case TacticalLines.SEIZE:
                    case TacticalLines.SEIZE_REVC:
                        pt0 = tg.Pixels.get(26);
                        pt1 = tg.Pixels.get(27);
                        //pt1=lineutility.ExtendAlongLineDouble(pt1, pt0, -10);
                        AddIntegralAreaModifier(tg, label, aboveMiddle, -0.125 * csFactor, pt0, pt1, true);
                        break;
                    default:
                        break;
                }
            }
            //end section
            switch (linetype) {
                case TacticalLines.CORDONKNOCK:
                case TacticalLines.CORDONSEARCH:
                    pt0 = tg.Pixels.get(13);
                    pt1 = tg.Pixels.get(0);
                    stringWidth = metrics.stringWidth(label);
                    if (pt0.x < pt1.x) {
                        stringWidth = -stringWidth;
                    }
                    //pt1=lineutility.ExtendAlongLineDouble2(pt0, pt1, 1.25*stringWidth);//use for CPOF
                    pt1 = lineutility.ExtendAlongLineDouble2(pt0, pt1, 0.75 * stringWidth);
                    AddIntegralAreaModifier(tg, label, aboveMiddle, 0, pt0, pt1, true);
                    break;
                case TacticalLines.FOLLA:
                    pt0 = tg.Pixels.get(0);
                    pt1 = lineutility.MidPointDouble(tg.Pixels.get(5), tg.Pixels.get(6), 0);
                    pt1 = lineutility.ExtendAlongLineDouble(pt1, pt0, -10);
                    AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, 0, pt0, pt1, true);
                    break;
                case TacticalLines.FOLSP:
                    pt0 = tg.Pixels.get(3);
                    pt1 = tg.Pixels.get(6);
                    pt1 = lineutility.ExtendAlongLineDouble(pt1, pt0, -10);
                    AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, 0, pt0, pt1, true);
                    break;
                case TacticalLines.ACA_RECTANGULAR:
                    ptLeft = lineutility.MidPointDouble(tg.Pixels.get(0), tg.Pixels.get(1), 0);
                    ptRight = lineutility.MidPointDouble(tg.Pixels.get(2), tg.Pixels.get(3), 0);
                    //ptCenter=lineutility.MidPointDouble(ptLeft, ptRight, 0);
                    AddModifier2(tg, label, aboveMiddle, -3 * csFactor, ptLeft, ptRight, false);
                    //AddIntegralAreaModifier(tg, label, area, -3*csFactor, ptCenter, ptCenter,false);
                    AddModifier2(tg, tg.get_Name(), aboveMiddle, -2 * csFactor, ptLeft, ptRight, false);
                    //AddIntegralAreaModifier(tg, tg.get_Name(), area, -2*csFactor, ptCenter, ptCenter,false);
                    AddModifier2(tg, "MIN ALT: " + tg.get_H(), aboveMiddle, -1 * csFactor, ptLeft, ptRight, false, "H");
                    //AddIntegralAreaModifier(tg, "MIN ALT: " + tg.get_H(), area, -1*csFactor, ptCenter, ptCenter,false,"H");
                    AddModifier2(tg, "MAX ALT: " + tg.get_H1(), aboveMiddle, 0, ptLeft, ptRight, false, "H1");
                    //AddIntegralAreaModifier(tg, "MAX ALT: " + tg.get_H1(), area, 0, ptCenter, ptCenter,false,"H1");
                    AddModifier2(tg, "Grids: " + tg.get_H2(), aboveMiddle, 1 * csFactor, ptLeft, ptRight, false, "H2");
                    //AddIntegralAreaModifier(tg, "Grids: " + tg.get_H2(), area, 1*csFactor, ptCenter, ptCenter,false,"H2");
                    AddModifier2(tg, "EFF: " + tg.get_DTG(), aboveMiddle, 2 * csFactor, ptLeft, ptRight, false, "W");
                    //AddIntegralAreaModifier(tg, "EFF: " + tg.get_DTG(), area, 2*csFactor, ptCenter, ptCenter,false,"W");
                    AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 3 * csFactor, ptLeft, ptRight, false, "W1");
                    //AddIntegralAreaModifier(tg, tg.get_DTG1(), area, 3*csFactor, ptCenter, ptCenter,false,"W1");
                    break;
                case TacticalLines.ACA_CIRCULAR:
                    ptCenter = lineutility.CalcCenterPointDouble2(tg.Pixels.toArray(), tg.Pixels.size());
                    AddIntegralAreaModifier(tg, label, area, -3 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, -2 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "MIN ALT: " + tg.get_H(), area, -1 * csFactor, ptCenter, ptCenter, false, "H");
                    AddIntegralAreaModifier(tg, "MAX ALT: " + tg.get_H1(), area, 0, ptCenter, ptCenter, false, "H1");
                    AddIntegralAreaModifier(tg, "Grids: " + tg.get_H2(), area, 1 * csFactor, ptCenter, ptCenter, false, "H2");
                    AddIntegralAreaModifier(tg, "EFF: " + tg.get_DTG(), area, 2 * csFactor, ptCenter, ptCenter, false, "W");
                    AddIntegralAreaModifier(tg, tg.get_DTG1(), area, 3 * csFactor, ptCenter, ptCenter, false, "W1");
                    break;
                case TacticalLines.RECTANGULAR:
                case TacticalLines.CIRCULAR:
                case TacticalLines.BBS_POINT:
                    ptCenter = lineutility.CalcCenterPointDouble2(tg.Pixels.toArray(), tg.Pixels.size());
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    break;
                case TacticalLines.FSA_CIRCULAR:
                case TacticalLines.ATI_CIRCULAR:
                case TacticalLines.CFFZ_CIRCULAR:
                case TacticalLines.SENSOR_CIRCULAR:
                case TacticalLines.CENSOR_CIRCULAR:
                case TacticalLines.DA_CIRCULAR:
                case TacticalLines.CFZ_CIRCULAR:
                case TacticalLines.ZOR_CIRCULAR:
                case TacticalLines.TBA_CIRCULAR:
                case TacticalLines.TVAR_CIRCULAR:
                    ptCenter = lineutility.MidPointDouble(tg.Pixels.get(0), tg.Pixels.get(tg.Pixels.size() / 2), 0);
                    AddIntegralAreaModifier(tg, label, area, -1 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    //W, W1 modifiers
                    //String fsaBlanks=blankString(metrics,(int)(5d/csFactor));
                    AddOffsetModifier(tg, tg.get_DTG(), toEnd, -1 * csFactor, tg.Pixels.size() / 2, 0, 4, "left");
                    AddOffsetModifier(tg, tg.get_DTG1(), toEnd, 0, tg.Pixels.size() / 2, 0, 4, "left");
                    break;
                case TacticalLines.FFA_CIRCULAR:
                case TacticalLines.NFA_CIRCULAR:
                case TacticalLines.RFA_CIRCULAR:
                    rfaLines = getRFALines(tg);
                    ptCenter = lineutility.MidPointDouble(tg.Pixels.get(0), tg.Pixels.get(51), 0);
                    switch (rfaLines) {
                        case 3: //2 valid modifiers and a label
                            AddIntegralAreaModifier(tg, label, area, -1 * csFactor, ptCenter, ptCenter, true);
                            AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, true);
                            AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1 * csFactor, ptCenter, ptCenter, true, "W+W1");
                            break;
                        case 2: //one valid modifier and a label
                            AddIntegralAreaModifier(tg, label, area, -0.5 * csFactor, ptCenter, ptCenter, true);
                            if (tg.get_Name() != null && !tg.get_Name().isEmpty()) {
                                AddIntegralAreaModifier(tg, tg.get_Name(), area, 0.5 * csFactor, ptCenter, ptCenter, true);
                            } else {
                                AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 0.5 * csFactor, ptCenter, ptCenter, true, "W+W1");
                            }
                            break;
                        default:    //one label only
                            AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter, true);
                            break;
                    }
                    break;
                case TacticalLines.KILLBOXBLUE_CIRCULAR:
                case TacticalLines.KILLBOXPURPLE_CIRCULAR:
                    rfaLines = getRFALines(tg);
                    //ptCenter = lineutility.MidPointDouble(tg.Pixels.get(0), tg.Pixels.get(51), 0);
                    ptCenter = lineutility.CalcCenterPointDouble2(tg.Pixels.toArray(), tg.Pixels.size());
                    switch (rfaLines) {
                        case 4: //2 valid modifiers and a label
                            AddIntegralAreaModifier(tg, label, area, -1 * csFactor, ptCenter, ptCenter, true);
                            AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, true);
                            AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1 * csFactor, ptCenter, ptCenter, true, "W+W1");
                            AddIntegralAreaModifier(tg, tg.get_H1(), area, 2 * csFactor, ptCenter, ptCenter, true, "H1");
                            break;
                        case 3: //2 valid modifiers and a label
                            AddIntegralAreaModifier(tg, label, area, -1 * csFactor, ptCenter, ptCenter, true);
                            AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, true);
                            AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1 * csFactor, ptCenter, ptCenter, true, "W+W1");
                            break;
                        case 2: //one valid modifier and a label
                            AddIntegralAreaModifier(tg, label, area, -0.5 * csFactor, ptCenter, ptCenter, true);
                            if (tg.get_Name() != null && !tg.get_Name().isEmpty()) {
                                AddIntegralAreaModifier(tg, tg.get_Name(), area, 0.5 * csFactor, ptCenter, ptCenter, true);
                            } else {
                                AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 0.5 * csFactor, ptCenter, ptCenter, true, "W+W1");
                            }
                            break;
                        default:    //one label only
                            AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter, true);
                            break;
                    }
                    break;
                case TacticalLines.BLOCK:
                    for (j = 0; j < tg.Pixels.size(); j++) {
                        if (tg.Pixels.get(j).style == 14) {
                            AddIntegralModifier(tg, label, aboveMiddle, 0, j, j + 1);
                            break;
                        }
                    }
                    break;
                case TacticalLines.CONTAIN:
                    for (j = 0; j < tg.Pixels.size(); j++) {
                        if (tg.Pixels.get(j).style == 14) {
                            pt0 = tg.Pixels.get(j);
                            pt1 = tg.Pixels.get(j + 1);
                            AddIntegralAreaModifier(tg, label, aboveMiddle, 0, pt0, pt1, true);
                            break;
                        }
                    }
                    break;
                case TacticalLines.HOLD:
                case TacticalLines.BRDGHD:
                case TacticalLines.HOLD_GE:
                case TacticalLines.BRDGHD_GE:
//                    stringWidth=metrics.stringWidth(label);
//                    pt1=lineutility.ExtendLineDouble(pt1, pt0, 13);
//                    AddIntegralAreaModifier(tg, label, area, 0, pt1, pt1,false);
//                    pt1=lineutility.ExtendLineDouble(ptNextToLast, ptLast, 13);   
//                    AddIntegralAreaModifier(tg, label, area, 0, pt1, pt1,false);                                                            

                    if (ptLast.x < pt0.x) {
                        pt1 = new POINT2(ptLast);
                        ptLast = new POINT2(pt0);
                        pt0 = new POINT2(pt1);
                    }
                    stringWidth = metrics.stringWidth(label);
                    pt1 = new POINT2(pt0);
                    pt1.x -= stringWidth / 1.5;
                    pt1.y -= font.getSize() / 2;
                    AddIntegralAreaModifier(tg, label, area, 0, pt1, pt1, false);
                    pt1 = new POINT2(ptLast);
                    pt1.x += stringWidth / 1.5;
                    pt1.y -= font.getSize() / 2;
                    AddIntegralAreaModifier(tg, label, area, 0, pt1, pt1, false);
                    break;
                case TacticalLines.FFA_RECTANGULAR:
                case TacticalLines.NFA_RECTANGULAR:
                case TacticalLines.RFA_RECTANGULAR:
                    rfaLines = getRFALines(tg);
                    pt0 = lineutility.MidPointDouble(tg.Pixels.get(0), tg.Pixels.get(1), 0);
                    pt1 = lineutility.MidPointDouble(tg.Pixels.get(2), tg.Pixels.get(3), 0);
                    //ptCenter=lineutility.MidPointDouble(pt0, pt1, 0);
                    switch (rfaLines) {
                        case 3: //two valid modifiers and one label
                            AddModifier2(tg, label, aboveMiddle, -1 * csFactor, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, label, area, -1*csFactor, ptCenter, ptCenter,false);
                            AddModifier2(tg, tg.get_Name(), aboveMiddle, 0, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter,false);
                            AddModifier2(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 1 * csFactor, pt0, pt1, false, "W+W1");
                            //AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1*csFactor, ptCenter, ptCenter,false,"W+W1");
                            break;
                        case 2: //one valid modifier and one label
                            AddModifier2(tg, label, aboveMiddle, -0.5 * csFactor, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, label, area, -0.5*csFactor, ptCenter, ptCenter,false);
                            if (tg.get_Name() != null && !tg.get_Name().isEmpty()) {
                                AddModifier2(tg, tg.get_Name(), aboveMiddle, 0.5 * csFactor, pt0, pt1, false);
                                //AddIntegralAreaModifier(tg, tg.get_Name(), area, 0.5*csFactor, ptCenter, ptCenter,false);
                            } else {
                                AddModifier2(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 0.5 * csFactor, pt0, pt1, false, "W+W1");
                                //AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 0.5*csFactor, ptCenter, ptCenter,false,"W+W1");
                            }
                            break;
                        default:    //one label only
                            AddModifier2(tg, label, aboveMiddle, 0, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter,false);
                            break;
                    }
                    break;
                case TacticalLines.KILLBOXBLUE_RECTANGULAR:
                case TacticalLines.KILLBOXPURPLE_RECTANGULAR:
                    rfaLines = getRFALines(tg);
                    pt0 = lineutility.MidPointDouble(tg.Pixels.get(0), tg.Pixels.get(1), 0);
                    pt1 = lineutility.MidPointDouble(tg.Pixels.get(2), tg.Pixels.get(3), 0);
                    //ptCenter=lineutility.MidPointDouble(pt0, pt1, 0);
                    switch (rfaLines) {
                        case 4: //2 valid modifiers and a label
                            AddModifier2(tg, label, aboveMiddle, -1 * csFactor, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, label, area, -1*csFactor, ptCenter, ptCenter,false);
                            AddModifier2(tg, tg.get_Name(), aboveMiddle, 0, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter,false);
                            AddModifier2(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 1 * csFactor, pt0, pt1, false, "W+W1");
                            //AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1*csFactor, ptCenter, ptCenter,false,"W+W1");
                            AddModifier2(tg, tg.get_H1(), aboveMiddle, 2 * csFactor, pt0, pt1, false, "H1");
                            //AddIntegralAreaModifier(tg, tg.get_H1(), area, 2*csFactor, ptCenter, ptCenter,false,"H1");
                            break;
                        case 3: //two valid modifiers and one label
                            AddModifier2(tg, label, aboveMiddle, -1 * csFactor, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, label, area, -1*csFactor, ptCenter, ptCenter,false);
                            AddModifier2(tg, tg.get_Name(), aboveMiddle, 0, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter,false);
                            AddModifier2(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 1 * csFactor, pt0, pt1, false, "W+W1");
                            //AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1*csFactor, ptCenter, ptCenter,false,"W+W1");
                            break;
                        case 2: //one valid modifier and one label
                            AddModifier2(tg, label, aboveMiddle, -0.5 * csFactor, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, label, area, -0.5*csFactor, ptCenter, ptCenter,false);
                            if (tg.get_Name() != null && !tg.get_Name().isEmpty()) {
                                AddModifier2(tg, tg.get_Name(), aboveMiddle, 0.5 * csFactor, pt0, pt1, false);
                                //AddIntegralAreaModifier(tg, tg.get_Name(), area, 0.5*csFactor, ptCenter, ptCenter,false);
                            } else {
                                AddModifier2(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 0.5 * csFactor, pt0, pt1, false, "W+W1");
                                //AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 0.5*csFactor, ptCenter, ptCenter,false,"W+W1");
                            }
                            break;
                        default:    //one label only
                            AddModifier2(tg, label, aboveMiddle, 0, pt0, pt1, false);
                            //AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter,false);
                            break;
                    }
                    break;
                case TacticalLines.FSA_RECTANGULAR:
                case TacticalLines.ATI_RECTANGULAR:
                case TacticalLines.CFFZ_RECTANGULAR:
                case TacticalLines.SENSOR_RECTANGULAR:
                case TacticalLines.CENSOR_RECTANGULAR:
                case TacticalLines.DA_RECTANGULAR:
                case TacticalLines.CFZ_RECTANGULAR:
                case TacticalLines.ZOR_RECTANGULAR:
                case TacticalLines.TBA_RECTANGULAR:
                case TacticalLines.TVAR_RECTANGULAR:
                    ptLeft = lineutility.MidPointDouble(tg.Pixels.get(0), tg.Pixels.get(1), 0);
                    ptRight = lineutility.MidPointDouble(tg.Pixels.get(2), tg.Pixels.get(3), 0);
                    //ptCenter=lineutility.MidPointDouble(ptLeft, ptRight, 0);
                    AddModifier2(tg, label, aboveMiddle, 0, ptLeft, ptRight, false);
                    //AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter,false);
                    AddModifier2(tg, tg.get_Name(), aboveMiddle, 1 * csFactor, ptLeft, ptRight, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 1 * csFactor, ptCenter, ptCenter, false);
                    pt0 = tg.Pixels.get(0);
                    pt1 = tg.Pixels.get(1);
                    pt2 = tg.Pixels.get(2);
                    pt3 = tg.Pixels.get(3);
                    if (tg.get_Client().equalsIgnoreCase("ge")) {
                        pt0.x -= font.getSize() / 2;
                        pt2.x -= font.getSize() / 2;
                    }
                    if (!tg.get_Client().equalsIgnoreCase("ge"))//added 2-27-12
                    {
                        clsUtility.shiftModifiersLeft(pt0, pt3, 12.5);
                        clsUtility.shiftModifiersLeft(pt1, pt2, 12.5);
                    }
                    if (ptLeft.x == ptRight.x) {
                        ptRight.x += 1;
                    }
                    if (ptLeft.x < ptRight.x) {
                        AddModifier(tg, tg.get_DTG(), toEnd, 0, pt0, pt3);//was 1,2 switched for CPOF
                        AddModifier(tg, tg.get_DTG1(), toEnd, 1 * csFactor, pt0, pt3);//was 1,2
                    } else {
                        AddModifier(tg, tg.get_DTG(), toEnd, 0, pt2, pt1);//was 3,0 //switched for CPOF
                        AddModifier(tg, tg.get_DTG1(), toEnd, 1, pt2, pt1);//was 3,0
                    }

                    break;
                case TacticalLines.PAA_RECTANGULAR:
                case TacticalLines.PAA_RECTANGULAR_REVC:
                    AddIntegralModifier(tg, label, aboveMiddle, 0, 0, 1, true);
                    AddIntegralModifier(tg, label, aboveMiddle, 0, 1, 2, true);
                    AddIntegralModifier(tg, label, aboveMiddle, 0, 2, 3, true);
                    AddIntegralModifier(tg, label, aboveMiddle, 0, 3, 0, true);
                    break;
                case TacticalLines.PAA_CIRCULAR:
                    pt0 = tg.Pixels.get(0);
                    AddIntegralAreaModifier(tg, label, area, 0, pt0, pt0, true);
                    pt0 = tg.Pixels.get(25);
                    AddIntegralAreaModifier(tg, label, area, 0, pt0, pt0, true);
                    pt0 = tg.Pixels.get(50);
                    AddIntegralAreaModifier(tg, label, area, 0, pt0, pt0, true);
                    pt0 = tg.Pixels.get(75);
                    AddIntegralAreaModifier(tg, label, area, 0, pt0, pt0, true);
                    break;
                case TacticalLines.RANGE_FAN:
                    if (tg.get_H1() != null && tg.get_H1().equals("") == false) {
                        H1 = tg.get_H1().split(",");
                        for (j = 0; j < H1.length; j++) {
                            if (tg.Pixels.size() > j * 102 + 25) {
                                pt0 = tg.Pixels.get(j * 102 + 25);
                                AddAreaModifier(tg, "ALT " + H1[j], area, 0, pt0, pt0);
                            }
                        }
                    }
                    if(!tg.get_HideOptionalLabels())
                    {
                        String H2 = tg.get_H2();
                        String[] am = H2.split(",");
                        for(j=0;j<am.length;j++)
                        {
                            if (tg.Pixels.size() > j * 102 + 25) {
                                pt0 = tg.Pixels.get(j * 102 + 25);
                                //AddAreaModifier(tg, "RG " + am[j], area, -1, pt0, pt0);
                                if(j==0)
                                    AddAreaModifier(tg, "MIN RG " + am[j], 3, -1, pt0, pt0);
                                else
                                    AddAreaModifier(tg, "MAX RG " + "(" + Integer.toString(j) + ") " + am[j], 3, -1, pt0, pt0);
                            }                            
                        }
                    }// end if set range fan text
                    break;
                case TacticalLines.RANGE_FAN_SECTOR:
//                    if (tg.get_H1() != null && tg.get_H1().equals("") == false) {
//                        H1 = tg.get_H1().split(",");
//                        for (j = 0; j < H1.length; j++) {
//                            if (tg.Pixels.size() > j * 203 + 151)//was j*203 + 50
//                            {
//                                pt0 = tg.Pixels.get(j * 203 + 151);//was j*203 + 50
//                                AddAreaModifier(tg, "ALT " + H1[j], area, 0, pt0, pt0);
//                            }
//                        }
//                    }                    
                    break;
                default:
                    break;
            }//end switch
            scaleModifiers(tg);
            tg.Pixels = origPoints;
            g2d.dispose();
            g2d = null;
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.AddModifiers2");
            ErrorLogger.LogException(_className, "AddModifiers2",
                    new RendererException("Failed inside AddModifiers2", exc));
        }
    }

    /**
     * Added sector range fan modifiers based using the calculated orientation
     * indicator points
     *
     * @param tg
     * @param converter
     * @return
     */
    public static boolean addSectorModifiers(TGLight tg, IPointConversion converter) {
        try {
            int linetype = tg.get_LineType();
            if (linetype != TacticalLines.RANGE_FAN_SECTOR) {
                return false;
            }

            ArrayList<Double> AM = new ArrayList();
            ArrayList<Double> AN = new ArrayList();
            //get the number of sectors
            String H2 = tg.get_H2();
            String H1 = tg.get_H1();
            String T1 = tg.get_T1();
            String T = tg.get_Name();
            //String[] altitudes = H1.split(",");
            String[] altitudes = null;
            String[] am = T1.split(",");
            String[] az = T.split(",");
            double min = 0, max = 0;
            //diagnostic
            double left=0,right=0;
            //end section
            int numSectors = az.length / 2;
            if (!H1.isEmpty()) {
                altitudes = H1.split(",");
            }
            //there must be at least one sector
            if (numSectors < 1) {
                return false;
            }

            try {
                for (int k = 0; k < am.length; k++) {
                    min = Double.parseDouble(am[k]);
                    AM.add(min);
                }
            } catch (NumberFormatException e) {
                return false;
            }
            //diagnostic
            try {
                for (int k = 0; k < az.length/2; k++) {
                    left = Double.parseDouble(az[2*k]);
                    right = Double.parseDouble(az[2*k+1]);
                    AN.add(left);
                    AN.add(right);
                }
            } catch (NumberFormatException e) {
                return false;
            }
            //end section
            if (numSectors + 1 > AM.size()) {
                if (Double.parseDouble(am[0]) != 0d) {
                    AM.add(0, 0d);
                }
            }

            int n = tg.Pixels.size();
            //pt0 and pt1 are points for the location indicator
            POINT2 pt0 = tg.Pixels.get(n - 5);
            POINT2 pt1 = tg.Pixels.get(n - 4);
            Point2D pt02d = new Point2D.Double(pt0.x, pt0.y);
            Point2D pt12d = new Point2D.Double(pt1.x, pt1.y);
            pt02d = converter.PixelsToGeo(pt02d);
            pt12d = converter.PixelsToGeo(pt12d);
            pt0.x = pt02d.getX();
            pt0.y = pt02d.getY();
            pt1.x = pt12d.getX();
            pt1.y = pt12d.getY();
            //azimuth of the orientation indicator
            double az12 = mdlGeodesic.GetAzimuth(pt0, pt1);

            POINT2 pt2 = null;
            ArrayList<POINT2> locModifier = new ArrayList();
            //diagnostic
            POINT2 ptLeft=null,ptRight=null;
            ArrayList<POINT2> locAZModifier = new ArrayList();
            //end section
            Point2D pt22d = null;
            double radius = 0;
            for (int k = 0; k < numSectors; k++) {
                if (AM.size() < k + 2) {
                    break;
                }
                radius = (AM.get(k) + AM.get(k + 1)) / 2;
                pt2 = mdlGeodesic.geodesic_coordinate(pt0, radius, az12);
                //need locModifier in geo pixels                
                pt22d = new Point2D.Double(pt2.x, pt2.y);
                pt22d = converter.GeoToPixels(pt22d);
                pt2.x = pt22d.getX();
                pt2.y = pt22d.getY();
                locModifier.add(pt2);
                //diagnostic
                if(tg.get_HideOptionalLabels())
                    continue;
                ptLeft = mdlGeodesic.geodesic_coordinate(pt0, radius, AN.get(2*k));
                //need ptLeft in geo pixels                
                pt22d = new Point2D.Double(ptLeft.x, ptLeft.y);
                pt22d = converter.GeoToPixels(pt22d);
                ptLeft.x = pt22d.getX();
                ptLeft.y = pt22d.getY();
                ptRight = mdlGeodesic.geodesic_coordinate(pt0, radius, AN.get(2*k+1));
                //need ptRight in geo pixels                
                pt22d = new Point2D.Double(ptRight.x, ptRight.y);
                pt22d = converter.GeoToPixels(pt22d);
                ptRight.x = pt22d.getX();
                ptRight.y = pt22d.getY();
                locAZModifier.add(ptLeft);
                locAZModifier.add(ptRight);
                //end section
            }
            if (altitudes != null) {
                for (int k = 0; k < altitudes.length; k++) {
                    if (k >= locModifier.size()) {
                        break;
                    }
                    pt0 = locModifier.get(k);
                    AddAreaModifier(tg, "ALT " + altitudes[k], area, 0, pt0, pt0);
                }
            }
            //diagnostic    add range and azimuth modifiers
            //add early eixt if tg.setRangeFanAZLabels is false
            if(tg.get_HideOptionalLabels())
                return true;
            for(int k=0;k<numSectors;k++)
            {
                pt0 = locModifier.get(k);
                AddAreaModifier(tg, "RG " + AM.get(k+1), area, -1, pt0, pt0);
                ptLeft=locAZModifier.get(2*k);
                ptRight=locAZModifier.get(2*k+1);
                AddAreaModifier(tg, az[2*k], area, 0, ptLeft, ptLeft);                        
                AddAreaModifier(tg, az[2*k+1], area, 0, ptRight, ptRight);                        
            }
            //end section
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.AddModifiers2");
            ErrorLogger.LogException(_className, "addSectorModifiers",
                    new RendererException("Failed inside addSectorModifiers", exc));
        }
        return true;
    }

    /**
     * Displays the tg modifiers using a client Graphics2D, this is an option
     * provided to clients for displaying modifiers without using shapes
     *
     * @param tg the tactical graphic
     * @param g2d the graphics object for drawing
     */
    public static void DisplayModifiers(TGLight tg,
            Graphics2D g2d) {
        try {
            AffineTransform id = new AffineTransform();
            id.setToIdentity();
            g2d.setTransform(id);
            Font font = g2d.getFont();
            int j = 0;
            Modifier2 modifier = null;
            g2d.setBackground(Color.white);
            POINT2 pt = null;
            double theta = 0;
            int stringWidth = 0, stringHeight = 0;
            FontMetrics metrics = g2d.getFontMetrics();
            String s = "";
            int x = 0, y = 0;
            POINT2 pt1 = null, pt2 = null;
            int quadrant = -1;
            for (j = 0; j < tg.modifiers.size(); j++) {
                modifier = (Modifier2) tg.modifiers.get(j);
                double lineFactor = modifier.lineFactor;
                //s=GetModifierTextFromID(tg,modifier.textID);
                s = modifier.text;
                double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                pt = modifier.textPath[0];
                x1 = pt.x;
                y1 = pt.y;
                pt = modifier.textPath[1];
                x2 = pt.x;
                y2 = pt.y;
                theta = Math.atan2(y2 - y1, x2 - x1);
                //color=g2d.getBackground();
                POINT2 midPt;
                if (x1 > x2) {
                    theta -= Math.PI;
                }
                switch (modifier.type) {
                    case toEnd: //corresponds to LabelAndTextBeforeLineTG
                        g2d.rotate(theta, x1, y1);
                        stringWidth = metrics.stringWidth(s);
                        stringHeight = font.getSize();
                        if (x1 < x2 || (x1 == x2 && y1 > y2)) {
                            x = (int) x1 - stringWidth;
                            y = (int) y1 - (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                            g2d.setColor(tg.get_FontBackColor());
                            g2d.clearRect(x, y, stringWidth, stringHeight);
                            y = (int) y1 + (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                            g2d.setColor(tg.get_TextColor());
                            g2d.drawString(s, x, y);
                        } else {
                            x = (int) x1;
                            y = (int) y1 - (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                            g2d.setColor(tg.get_FontBackColor());
                            g2d.clearRect(x, y, stringWidth, stringHeight);
                            y = (int) y1 + (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                            g2d.setColor(tg.get_TextColor());
                            g2d.drawString(s, x, y);
                        }
                        break;
                    case aboveMiddle:
                        midPt = new POINT2((x1 + x2) / 2, (y1 + y2) / 2);
                        g2d.rotate(theta, midPt.x, midPt.y);
                        stringWidth = metrics.stringWidth(s);
                        stringHeight = font.getSize();
                        x = (int) midPt.x - stringWidth / 2;
                        y = (int) midPt.y - (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                        g2d.setColor(tg.get_FontBackColor());
                        g2d.clearRect(x, y, stringWidth, stringHeight);
                        y = (int) midPt.y + (int) (stringHeight / 2) + (int) (lineFactor * stringHeight);
                        g2d.setColor(tg.get_TextColor());
                        g2d.drawString(s, x, y);
                        break;
                    case area:
                        g2d.rotate(0, x1, y1);
                        stringWidth = metrics.stringWidth(s);
                        stringHeight = font.getSize();

                        x = (int) x1 - stringWidth / 2;
                        y = (int) y1 - (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                        g2d.setColor(tg.get_FontBackColor());
                        g2d.clearRect(x, y, stringWidth, stringHeight);
                        y = (int) y1 + (int) (stringHeight / 2) + (int) (lineFactor * stringHeight);
                        g2d.setColor(tg.get_TextColor());
                        g2d.drawString(s, x, y);
                        break;
                    case screen:    //for SCREEN, GUARD, COVER
                        if (tg.Pixels.size() >= 14) {
                            pt1 = tg.Pixels.get(3);
                            pt2 = tg.Pixels.get(10);
                            quadrant = lineutility.GetQuadrantDouble(pt1, pt2);
                            theta = Math.atan2(pt2.y - pt1.y, pt2.x - pt1.x);
                            switch (quadrant) {
                                case 1:
                                    theta += Math.PI / 2;
                                    break;
                                case 2:
                                    theta -= Math.PI / 2;
                                    break;
                                case 3:
                                    theta -= Math.PI / 2;
                                    break;
                                case 4:
                                    theta += Math.PI / 2;
                                    break;
                                default:
                                    break;
                            }

                            g2d.rotate(theta, x1, y1);
                            stringWidth = metrics.stringWidth(s);
                            stringHeight = font.getSize();

                            x = (int) x1 - stringWidth / 2;
                            y = (int) y1 - (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                            g2d.setColor(tg.get_FontBackColor());
                            g2d.clearRect(x, y, stringWidth, stringHeight);
                            y = (int) y1 + (int) (stringHeight / 2) + (int) (lineFactor * stringHeight);
                            g2d.setColor(tg.get_TextColor());
                            g2d.drawString(s, x, y);
                        } else {
                            stringWidth = metrics.stringWidth(s);
                            stringHeight = font.getSize();
                            x = (int) tg.Pixels.get(0).x;//(int) x1 - stringWidth / 2;
                            y = (int) tg.Pixels.get(0).y;//(int) y1 - (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                            g2d.setColor(tg.get_FontBackColor());
                            g2d.clearRect(x, y, stringWidth, stringHeight);
                            y = (int) y + (int) (stringHeight / 2) + (int) (lineFactor * stringHeight);
                            g2d.setColor(tg.get_TextColor());
                            g2d.drawString(s, x, y);
                        }
                        break;
                    default:
                        break;
                }   //end switch
                g2d.setTransform(id);
            }   //end for
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.DsiplayModifiers");
            ErrorLogger.LogException(_className, "DisplayModifiers",
                    new RendererException("Failed inside DisplayModifiers", exc));
        }
    }//end function

    /**
     * Returns a Shape object for the text background for labels and modifiers
     *
     * @param tg the tactical graphic object
     * @param pt0 1st point of segment
     * @param pt1 last point of segment
     * @param stringWidth string width
     * @param stringHeight string height
     * @param lineFactor number of text lines above or below the segment
     * @param isTextFlipped true if text is flipped
     * @return the modifier shape
     */
    public static Shape2 BuildModifierShape(
            TGLight tg,
            POINT2 pt0,
            POINT2 pt1,
            int stringWidth,
            int stringHeight,
            double lineFactor,
            boolean isTextFlipped) {
        Shape2 modifierFill = null;
        try {

            POINT2 ptTemp0 = new POINT2(pt0), ptTemp1 = new POINT2(pt1);

            if (isTextFlipped) {
                lineFactor += 1;
            }

            if (lineFactor < 0) //extend pt0,pt1 above the line
            {
                ptTemp0 = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 2, -lineFactor * stringHeight);
                ptTemp1 = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 2, -lineFactor * stringHeight);
            }
            if (lineFactor > 0) //extend pt0,pt1 below the line
            {
                ptTemp0 = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 3, lineFactor * stringHeight);
                ptTemp1 = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 3, lineFactor * stringHeight);
            }
            if (ptTemp0.y == ptTemp1.y) {
                ptTemp0.y += 1;
            }

            POINT2 pt3 = null, pt4 = null, pt5 = null, pt6 = null, pt7 = null;
            pt3 = lineutility.ExtendAlongLineDouble(ptTemp0, ptTemp1, -stringWidth);
            pt4 = lineutility.ExtendDirectedLine(ptTemp1, ptTemp0, pt3, 0, stringHeight / 2);
            pt5 = lineutility.ExtendDirectedLine(ptTemp1, ptTemp0, pt3, 1, stringHeight / 2);
            pt6 = lineutility.ExtendDirectedLine(ptTemp1, ptTemp0, ptTemp0, 1, stringHeight / 2);
            pt7 = lineutility.ExtendDirectedLine(ptTemp1, ptTemp0, ptTemp0, 0, stringHeight / 2);
            modifierFill = new Shape2(Shape2.SHAPE_TYPE_MODIFIER_FILL);
            //modifierFill = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);

            modifierFill.moveTo(pt4);
            modifierFill.lineTo(pt5);
            modifierFill.lineTo(pt6);
            modifierFill.lineTo(pt7);
            modifierFill.lineTo(pt4);
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "BuildModifierShape",
                    new RendererException("Failed inside BuildModifierShape", exc));
        }
        return modifierFill;
    }

    /**
     * Spinelli is going to send the segment colors within a modifier. Format is
     * 0:FFBBBB,4:FFAAAA,... For the time being will assume the modifier being
     * used is the H modifier
     *
     * @param tg
     * @return
     */
//    protected static HashMap<Integer,Color> getMSRColors(TGLight tg)
//    {
//        HashMap hMap=null;
//        try
//        {
//            int linetype=tg.get_LineType();
//            switch(linetype)
//            {
//                case TacticalLines.MSR:
//                case TacticalLines.ASR:
//                case TacticalLines.BOUNDARY:
//                    if(tg.get_H()==null)
//                        return null;
//                    if(tg.get_H().isEmpty())
//                        return null;
//                    hMap=new HashMap<Integer,Color>();
//                    break;
//                default:
//                    return null;
//            }
//            if(!tg.get_H().contains(","))
//                return null;
//            String[]colorStrs=tg.get_H().split(",");
//            int j=0,numSegs=colorStrs.length;
//            String segPlusColor="";
//            String[]seg=null;     
//            Color color=null;
//            int index=-1;
//            for(j=0;j<numSegs;j++)
//            {
//                segPlusColor=colorStrs[j];
//                if(!segPlusColor.contains(":"))
//                    continue;
//                seg=segPlusColor.split(":");
//                color=SymbolUtilities.getColorFromHexString(seg[1]);
//                index=Integer.parseInt(seg[0]);
//                hMap.put(index, color);
//            }
//        }
//        catch (Exception exc)
//        {
//            ErrorLogger.LogException(_className ,"getMSRColors",
//                    new RendererException("Failed inside getMSRColors", exc));
//        }
//        return hMap;
//    }
    /**
     * For BOUNDARY and other line types which require breaks for the integral
     * text. Currently only boundary uses this
     *
     * @param tg
     * @param g2d the graphics object for drawing
     * @param shapes the shape array
     */
    public static void GetIntegralTextShapes(TGLight tg,
            Graphics2D g2d,
            ArrayList<Shape2> shapes) {
        try {
            //ArrayList<Shape2>shapes = null;
            if (tg.Pixels == null || shapes == null) {
                return;
            }

            //diagnostic 1-22-13
            HashMap<Integer, Color> hmap = clsUtility.getMSRSegmentColors(tg);
            Color color = null;
            //end section

            Shape2 shape = null;
            Shape2 segShape = null;//diangostic 1-22-13
            g2d.setFont(tg.get_Font());
            int j = 0;
            //double csFactor=1;
            //boolean beginLine=true;
            String affiliation = null;
            //String echelonSymbol=null;
            FontMetrics metrics = g2d.getFontMetrics();
            String echelonSymbol = null;
            //String strENY=null;
            int stringWidthEchelonSymbol = 0;
            int stringWidthENY = 0;
            //boolean lineTooShort = false;
            //int middleSegment = tg.Pixels.size() / 2 - 1;
            POINT2 ptEchelonStart = null, ptEchelonEnd = null, midpt,
                    ptENY0Start = null, ptENY0End = null, ptENY1Start, ptENY1End, pt0 = null, pt1 = null;
            double dist = 0;
            BasicStroke stroke = null;
            switch (tg.get_LineType()) {
                case TacticalLines.BOUNDARY:
                    echelonSymbol = tg.get_EchelonSymbol();
                    //shapes = new ArrayList();
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.setLineColor(tg.get_LineColor());
                    shape.set_Style(tg.get_LineStyle());
                    affiliation = tg.get_Affiliation();
                    stroke = clsUtility.getLineStroke(tg.get_LineThickness(), shape.get_Style(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                    //shape.setStroke(new BasicStroke(tg.get_LineThickness()));
                    shape.setStroke(stroke);
                    //echelonSymbol=tg.get_EchelonSymbol();
                    if (tg.get_Affiliation() != null && tg.get_Affiliation().equals("H")) {
                        stringWidthENY = metrics.stringWidth(tg.get_N());
                    }
                    if (echelonSymbol != null && !echelonSymbol.isEmpty()) {
                        stringWidthEchelonSymbol = metrics.stringWidth(echelonSymbol);
                    }
                    //diagnostic
                    if (hmap == null || hmap.isEmpty()) {
                        shape.moveTo(tg.Pixels.get(0));
                        for (j = 1; j < tg.Pixels.size(); j++) {
                            shape.lineTo(tg.Pixels.get(j));
                        }
                        shapes.add(shape);
                        break;
                    }
                    //end section
                    for (j = 0; j < tg.Pixels.size() - 1; j++) {
                        segShape = null;
                        if (hmap != null) {
                            if (hmap.containsKey(j)) {
                                color = (Color) hmap.get(j);
                                segShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                                segShape.setLineColor(color);
                                segShape.set_Style(tg.get_LineStyle());
                                segShape.setStroke(stroke);
                            }
                        }
                        //end section

                        pt0 = tg.Pixels.get(j);
                        pt1 = tg.Pixels.get(j + 1);
                        //lineTooShort = GetBoundarySegmentTooShort(tg, g2d, j);
                        if (segShape != null) {
                            segShape.moveTo(pt0);
                        } else {
                            shape.moveTo(pt0);
                        }

                        //uncoment comment to remove line breaks for GE
                        //if (lineTooShort || tg.get_Client().equals("ge")) 
                        if (tg.get_Client().equals("ge") || GetBoundarySegmentTooShort(tg, g2d, j) == true) {
                            if (segShape != null) {
                                segShape.lineTo(pt1);
                                shapes.add(segShape);
                                continue;
                            } else {
                                shape.lineTo(pt1);
                                continue;
                            }
                        }

                        midpt = lineutility.MidPointDouble(pt0, pt1, 0);
                        if (stringWidthENY > 0) {
                            //line break for the first N modifier
                            midpt = lineutility.MidPointDouble(pt0, midpt, 0);
                            midpt = lineutility.MidPointDouble(pt0, midpt, 0);
                            dist = lineutility.CalcDistanceDouble(pt0, midpt) - stringWidthENY / 1.5;
                            ptENY0Start = lineutility.ExtendAlongLineDouble(pt0, pt1, dist);
                            dist = lineutility.CalcDistanceDouble(pt0, midpt) + stringWidthENY / 1.5;
                            ptENY0End = lineutility.ExtendAlongLineDouble(pt0, pt1, dist);
                            if (segShape != null) {
                                segShape.moveTo(pt0);
                                segShape.lineTo(ptENY0Start);
                                segShape.moveTo(ptENY0End);
                            } else {
                                shape.moveTo(pt0);
                                shape.lineTo(ptENY0Start);
                                shape.moveTo(ptENY0End);
                            }
                        } else {
                            if (segShape != null) {
                                segShape.moveTo(pt0);
                            } else {
                                shape.moveTo(pt0);
                            }
                        }

                        if (stringWidthEchelonSymbol > 0) {
                            midpt = lineutility.MidPointDouble(pt0, pt1, 0);
                            dist = lineutility.CalcDistanceDouble(pt0, midpt) - stringWidthEchelonSymbol / 1.5;
                            ptEchelonStart = lineutility.ExtendAlongLineDouble(pt0, pt1, dist);
                            dist = lineutility.CalcDistanceDouble(pt0, midpt) + stringWidthEchelonSymbol / 1.5;
                            ptEchelonEnd = lineutility.ExtendAlongLineDouble(pt0, pt1, dist);
                            if (segShape != null) {
                                segShape.lineTo(ptEchelonStart);
                                segShape.moveTo(ptEchelonEnd);
                            } else {
                                shape.lineTo(ptEchelonStart);
                                shape.moveTo(ptEchelonEnd);
                            }
                        }
                        if (stringWidthENY > 0) {
                            //line break for the last N modifier
                            midpt = lineutility.MidPointDouble(pt0, pt1, 0);
                            midpt = lineutility.MidPointDouble(pt1, midpt, 0);
                            midpt = lineutility.MidPointDouble(pt1, midpt, 0);
                            dist = lineutility.CalcDistanceDouble(pt1, midpt) - stringWidthENY / 1.5;
                            ptENY1Start = lineutility.ExtendAlongLineDouble(pt1, pt0, dist);
                            dist = lineutility.CalcDistanceDouble(pt1, midpt) + stringWidthENY / 1.5;
                            ptENY1End = lineutility.ExtendAlongLineDouble(pt1, pt0, dist);
                            if (segShape != null) {
                                segShape.lineTo(ptENY1End);
                                segShape.moveTo(ptENY1Start);
                                segShape.lineTo(pt1);
                            } else {
                                shape.lineTo(ptENY1End);
                                shape.moveTo(ptENY1Start);
                                shape.lineTo(pt1);
                            }
                        } else {
                            if (segShape != null) {
                                segShape.lineTo(pt1);
                            } else {
                                shape.lineTo(pt1);
                            }
                        }
                        if (segShape != null) {
                            shapes.add(segShape);
                        }
                    }//end for
                    shapes.add(shape);
                    break;
                default:
                    break;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetIntegralTextShapes",
                    new RendererException("Failed inside GetIntegralTextShapes", exc));
        }
    }

    private static int switchDirection(int direction) {
        int result = -1;
        switch (direction) {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return 3;
            case 3:
                return 2;
        }
        return result;
    }

    /**
     * GE seems to work with aboveMiddle and not toEnd modifiers
     *
     * @param modifier
     * @param stringWidth
     */
    private static void toEndToAboveMiddle(Modifier2 modifier,
            double stringWidth) {
        try {
            if (modifier.type != toEnd) {
                return;
            }

            double geFactor = 0.5;
            POINT2 pt0 = modifier.textPath[0];
            POINT2 pt1 = modifier.textPath[1];
            POINT2 pt2 = null, pt3 = null;
            if (pt0.x < pt1.x) {
                pt2 = lineutility.ExtendLineDouble(pt1, pt0, geFactor * stringWidth);
                pt3 = pt0;
            } else {
                pt2 = pt0;
                pt3 = lineutility.ExtendLineDouble(pt1, pt0, geFactor * stringWidth);
            }
            modifier.textPath[0] = pt2;
            modifier.textPath[1] = pt3;
            modifier.type = aboveMiddle;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "toEndToAboveMiddle",
                    new RendererException("Failed inside toEndToAboveMiddle", exc));
        }
        return;
    }

    /**
     * Displays the modifiers to a Graphics2D from a BufferedImage
     *
     * @param tg the tactical graphic
     * @param g2d the Graphic for drawing
     * @param shapes the shape array
     * @param isTextflipped true if text is flipped
     * @param converter to convert between geographic and pixel coordinates
     */
    public static void DisplayModifiers2(TGLight tg,
            Graphics2D g2d,
            ArrayList<Shape2> shapes,
            boolean isTextFlipped,
            IPointConversion converter) {
        try {
            if (shapes == null) {
                return;
            }

            if (tg.modifiers == null || tg.modifiers.isEmpty()) {
                return;
            }

            AffineTransform id = new AffineTransform();
            id.setToIdentity();
            g2d.setTransform(id);
            Font font = null;
            int j = 0;
            Modifier2 modifier = null;
            AffineTransform tx = null;
            Color fontBackColor = tg.get_FontBackColor();
            Color textColor = tg.get_TextColor();
            //POINT2 pt=null;
            double theta = 0;
            double stringWidth = 0, stringHeight = 0;
            String s = "";
            int x = 0, y = 0;
            POINT2 pt0 = null, pt1 = null, pt2 = null, pt3 = null;
            int quadrant = -1;
            Shape2 shape2 = null;
            long lineType = tg.get_LineType();
            font = tg.get_Font();    //might have to change this
            if (font == null) {
                font = g2d.getFont();
            }
            if (font.getSize() == 0) {
                return;
            }
            g2d.setFont(font);
            FontMetrics metrics = g2d.getFontMetrics();
            //we need a background color
            if (fontBackColor != null) {
                g2d.setBackground(fontBackColor);
            } else {
                g2d.setBackground(Color.white);
            }
            if (textColor == null) {
                textColor = tg.get_LineColor();
            }
            int direction = -1;
            Point glyphPosition = null;
            //boolean isIntegral=false;
            for (j = 0; j < tg.modifiers.size(); j++) {
                modifier = (Modifier2) tg.modifiers.get(j);

                //isIntegral=modifier.get_IsIntegral();
                //double lineFactor = modifier.lineFactor*multFactor+ addFactor;
                double lineFactor = modifier.lineFactor;

                if (isTextFlipped) {
                    lineFactor = -lineFactor;
                }

                s = modifier.text;
                if (s == null || s.equals("")) {
                    continue;
                }
                stringWidth = (double) metrics.stringWidth(s) + 1;
                stringHeight = (double) font.getSize();

                double x1 = 0, y1 = 0, x2 = 0, y2 = 0, dist = 0;
                pt0 = modifier.textPath[0];
                x1 = Math.round(pt0.x);
                y1 = Math.round(pt0.y);
                pt1 = modifier.textPath[1];
                x2 = Math.round(pt1.x);
                y2 = Math.round(pt1.y);
                theta = Math.atan2(y2 - y1, x2 - x1);
                POINT2 midPt;
                if (x1 > x2) {
                    theta -= Math.PI;
                }
                pt0 = new POINT2(x1, y1);
                pt1 = new POINT2(x2, y2);
                midPt = new POINT2((x1 + x2) / 2, (y1 + y2) / 2);
                Point2D modifierPosition = null;  //use this if using justify
                int justify = ShapeInfo.justify_left;
                switch (modifier.type) {
                    case aboveEnd:
                        if (x1 == x2) {
                            x2 += 1;
                        }

                        if (x1 < x2) {
                            x = (int) x1 - (int) stringWidth;
                            //x = (int) x1;
                            y = (int) y1 + (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                        } else {
                            x = (int) x1;
                            //x = (int) x1 - (int) stringWidth;
                            y = (int) y1 + (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                        }
                        tx = new AffineTransform();
                        tx.rotate(theta, x1, y1);
                        tx.translate(x, y);
                        //dist = lineutility.CalcDistanceDouble(pt0, pt1);
                        //direction = 2;
                        if (lineFactor >= 0) {
                            direction = 2;
                        } else {
                            direction = 3;
                        }

                        if (lineType == TacticalLines.LC || tg.get_Client().equalsIgnoreCase("ge")) {
                            direction = switchDirection(direction);
                        }

                        if (x1 < x2) {
                            justify = ShapeInfo.justify_right;
                        } else {
                            justify = ShapeInfo.justify_left;
                        }

                        pt3 = lineutility.ExtendDirectedLine(pt1, pt0, pt0, direction, lineFactor * stringHeight);
                        glyphPosition = new Point((int) pt3.x, (int) pt3.y);
                        modifierPosition = new Point2D.Double(pt3.x, pt3.y);
                        break;
                    case toEnd: //corresponds to LabelAndTextBeforeLineTG                                                
                        if (x1 == x2) {
                            x2 += 1;
                        }

                        if (x1 < x2) {
                            x = (int) x1 - (int) stringWidth;
                            y = (int) y1 + (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                        } else {
                            x = (int) x1;
                            y = (int) y1 + (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                        }
                        tx = new AffineTransform();
                        tx.rotate(theta, x1, y1);
                        tx.translate(x, y);
                        if (lineFactor >= 0) {
                            direction = 2;
                        } else {
                            direction = 3;
                        }

                        if (lineType == TacticalLines.LC || tg.get_Client().equalsIgnoreCase("ge")) {
                            direction = switchDirection(direction);
                        }

                        if (x1 < x2) {
                            justify = ShapeInfo.justify_right;
                        } else {
                            justify = ShapeInfo.justify_left;
                        }

                        pt3 = lineutility.ExtendDirectedLine(pt1, pt0, pt0, direction, lineFactor * stringHeight);

                        glyphPosition = new Point((int) pt3.x, (int) pt3.y);
                        modifierPosition = new Point2D.Double(pt3.x, pt3.y);
                        break;
                    case aboveMiddle:
                        //use the geo midpoint for this, otherwise it strays
                        dist = lineutility.CalcDistanceDouble(pt0, pt1);
                        if (converter != null && dist > 100 && lineType != TacticalLines.BOUNDARY) {
                            //Point2D.Double pt1Geo = converter.PixelsToGeo(new Point((int) pt0.x, (int) pt0.y));
                            Point2D pt2d = new Point2D.Double(pt0.x, pt0.y);
                            Point2D pt1Geo = converter.PixelsToGeo(pt2d);
                            //Point2D.Double pt2Geo = converter.PixelsToGeo(new Point((int) pt1.x, (int) pt1.y));
                            pt2d = new Point2D.Double(pt1.x, pt1.y);
                            Point2D pt2Geo = converter.PixelsToGeo(pt2d);
                            ref<double[]> a12 = new ref<double[]>();
                            ref<double[]> a21 = new ref<double[]>();
                            a12.value = new double[1];
                            a21.value = new double[1];
                            dist = mdlGeodesic.geodesic_distance(new POINT2(pt1Geo.getX(), pt1Geo.getY()), new POINT2(pt2Geo.getX(), pt2Geo.getY()), a12, a21);
                            POINT2 midPtGeo = mdlGeodesic.geodesic_coordinate(new POINT2(pt1Geo.getX(), pt1Geo.getY()), dist / 2, a12.value[0]);
                            Point2D midPt2D = converter.GeoToPixels(new Point2D.Double(midPtGeo.x, midPtGeo.y));
                            midPt.x = midPt2D.getX();
                            midPt.y = midPt2D.getY();
                        }
                        //g2d.rotate(theta, midPt.x, midPt.y);
                        //calculate midpoint using geo
                        x = (int) midPt.x - (int) stringWidth / 2;
                        y = (int) midPt.y + (int) (stringHeight / 2) + (int) (lineFactor * stringHeight);
                        //g2d.setColor(tg.get_LineColor());
                        //tx = g2d.getTransform();
                        tx = new AffineTransform();
                        tx.rotate(theta, midPt.x, midPt.y);
                        tx.translate(x, y);

                        if (pt0.x <= pt1.x) {
                            pt2 = lineutility.ExtendAlongLineDouble(midPt, pt0, 0.5 * stringWidth);
                        } else {
                            pt2 = lineutility.ExtendAlongLineDouble(midPt, pt1, 0.5 * stringWidth);
                        }

                        if (tg.get_Client().equals("2D")) {
                            lineFactor += 0.5;
                        }

                        if (lineFactor >= 0) {
                            pt3 = lineutility.ExtendDirectedLine(pt0, pt2, pt2, 3, Math.abs((lineFactor) * stringHeight));
                            midPt = lineutility.ExtendDirectedLine(pt0, midPt, midPt, 3, Math.abs((lineFactor) * stringHeight));
                        } else {
                            pt3 = lineutility.ExtendDirectedLine(pt0, pt2, pt2, 2, Math.abs((lineFactor) * stringHeight));
                            midPt = lineutility.ExtendDirectedLine(pt0, midPt, midPt, 2, Math.abs((lineFactor) * stringHeight));
                        }
                        //pt3=lineutility.ExtendDirectedLine(pt0, pt2, pt2, 2, lineFactor*stringHeight);
                        if (x1 == x2 && y1 > y2) {
                            pt3 = lineutility.ExtendDirectedLine(pt0, pt2, pt2, 1, Math.abs((lineFactor) * stringHeight));
                            midPt = lineutility.ExtendDirectedLine(pt0, midPt, midPt, 1, Math.abs((lineFactor) * stringHeight));
                        }
                        if (x1 == x2 && y1 < y2) {
                            pt3 = lineutility.ExtendDirectedLine(pt0, pt2, pt2, 0, Math.abs((lineFactor) * stringHeight));
                            midPt = lineutility.ExtendDirectedLine(pt0, midPt, midPt, 0, Math.abs((lineFactor) * stringHeight));
                        }

                        glyphPosition = new Point((int) pt3.x, (int) pt3.y);
                        //diagnostic
                        //glyphPosition = new Point((int) midPt.x, (int) midPt.y);
                        justify = ShapeInfo.justify_center;
                        modifierPosition = new Point2D.Double(midPt.x, midPt.y);
                        break;
                    case area:
                        theta = 0;

                        x = (int) x1 - (int) stringWidth / 2;
                        y = (int) y1 + (int) (stringHeight / 2) + (int) (1.25 * lineFactor * stringHeight);

                        int mpy = (int)(y1 + (1.25 * lineFactor * stringHeight));//need the line factor but not the offset
                        
                        tx = new AffineTransform();
                        tx.translate(x, y);

                        //pt2 = new POINT2(midPt.x + stringWidth / 2 - 1, midPt.y);
                        //pt3 = new POINT2(midPt.x + 1.5 * stringWidth + 1, midPt.y);
                        glyphPosition = new Point(x, y);//Placement for Graphics2D
                        //diagnostic
                        //glyphPosition = new Point((int)x1, y);
                        justify = ShapeInfo.justify_center;
                        modifierPosition = new Point2D.Double(x1, mpy);//Modifier Anchor position for KML/GJSON
                        break;
                    case screen:    //for SCREEN, GUARD, COVER, not currently used
                        if (tg.Pixels.size() >= 14) {
                            pt1 = tg.Pixels.get(3);
                            pt2 = tg.Pixels.get(10);
                            quadrant = lineutility.GetQuadrantDouble(pt1, pt2);
                            theta = Math.atan2(pt2.y - pt1.y, pt2.x - pt1.x);
                            if (Math.abs(theta) < Math.PI / 8) {
                                if (theta < 0) {
                                    theta -= Math.PI / 2;
                                } else {
                                    theta += Math.PI / 2;
                                }
                            }
                            switch (quadrant) {
                                case 1:
                                    theta += Math.PI / 2;
                                    break;
                                case 2:
                                    theta -= Math.PI / 2;
                                    break;
                                case 3:
                                    theta -= Math.PI / 2;
                                    break;
                                case 4:
                                    theta += Math.PI / 2;
                                    break;
                                default:
                                    break;
                            }

                            //g2d.rotate(theta, x1, y1);
                            x = (int) x1 - (int) stringWidth / 2;
                            y = (int) y1 - (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                            y = (int) y1 + (int) (stringHeight / 2) + (int) (lineFactor * stringHeight);
                            tx = new AffineTransform();
                            tx.rotate(theta, x1, y1);
                            tx.translate(x, y);
                        } else {
                            theta = 0;
                            x = (int) tg.Pixels.get(0).x;
                            y = (int) tg.Pixels.get(0).y;
                            x = (int) x - (int) stringWidth / 2;
                            y = (int) y - (int) stringHeight / 2 + (int) (lineFactor * stringHeight);
                            y = (int) y + (int) (stringHeight / 2) + (int) (lineFactor * stringHeight);
                            tx = g2d.getTransform();
                            tx.translate(x, y);
                        }

                        glyphPosition = new Point(x, y);
                        justify = ShapeInfo.justify_center;
                        modifierPosition = new Point2D.Double(x1, y1);
                        break;
                    default:
                        break;
                }   //end switch

                if (isTextFlipped) {
                    tx.scale(1, -1);
                }

                shape2 = new Shape2(Shape2.SHAPE_TYPE_MODIFIER_FILL);

                shape2.setStroke(new BasicStroke(0, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3));

                if (tg.get_TextColor() != null) {
                    shape2.setFillColor(tg.get_TextColor());
                } else if (tg.get_LineColor() != null) {
                    shape2.setFillColor(tg.get_LineColor());
                }
                if (tg.get_LineColor() != null) {
                    shape2.setLineColor(tg.get_LineColor());
                }
                TextLayout tl = new TextLayout(s, font, g2d.getFontMetrics().getFontRenderContext());
                shape2.setTextLayout(tl);
                shape2.setAffineTransform(tx);
                //only GE uses the converter, generic uses the affine transform and draws at 0,0
                if (converter != null) {
                    shape2.setGlyphPosition(glyphPosition);
                } else {
                    shape2.setGlyphPosition(new Point2D.Double(0, 0));
                }
                //shape2.setGlyphPosition(new Point(0,0));
                //added two settings for use by GE
                shape2.setModifierString(s);
                //clients should use getModifierStringPosition if they are using horizontal justification to calculate text placement
                shape2.setModifierStringPosition(modifierPosition);
                //shape2.setModifierStringPosition(glyphPosition);//M. Deutch 7-6-11
                shape2.setModifierStringAngle(theta * 180 / Math.PI);
                shape2.setTextJustify(justify);
                if (shape2 != null) {
                    shapes.add(shape2);
                }

                g2d.setTransform(id);
            }   //end for
        } //end try
        catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.DisplayModifiers2");
            ErrorLogger.LogException(_className, "DisplayModifiers2",
                    new RendererException("Failed inside DisplayModifiers2", exc));
        }
    }//end function

    /**
     * Builds a shape object to wrap text
     *
     * @param g2d the Graphic object for drawing
     * @param str text to wrap
     * @param font the draw font
     * @param tx the drawing transform, text rotation and translation
     * @return
     */
    public static Shape getTextShape(Graphics2D g2d,
            String str,
            Font font,
            AffineTransform tx) {
        TextLayout tl = null;
        FontRenderContext frc = null;
        try {
            frc = g2d.getFontRenderContext();
            tl = new TextLayout(str, font, frc);
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in Modifier2.getTextShape");
            ErrorLogger.LogException(_className, "getTextShape",
                    new RendererException("Failed inside getTextShape", exc));
        }
        return tl.getOutline(tx);
    }

    /**
     * Creates text outline as a shape
     *
     * @param originalText the original text
     * @return text shape
     */
    public static Shape2 createTextOutline(Shape2 originalText) {
        Shape2 siOutline = null;
        try {
            //Shape outline = new GeneralPath(originalText.get_Shape());
            Shape outline = originalText.getShape();

            siOutline = new Shape2(Shape2.SHAPE_TYPE_MODIFIER_FILL);
            siOutline.setShape(outline);

            if (originalText.getFillColor().getRed() == 255
                    && originalText.getFillColor().getGreen() == 255
                    && originalText.getFillColor().getBlue() == 255) {
                siOutline.setLineColor(Color.BLACK);
            } else {
                siOutline.setLineColor(Color.WHITE);
            }

            int width = RendererSettings.getInstance().getTextOutlineWidth();

            siOutline.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND, 3));

        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "createTextOutline",
                    new RendererException("Failed inside createTextOutline", exc));
        }
        return siOutline;
    }

    /**
     * labels for Rev D symbols
     *
     * @param code Rev D entity code
     * @return
     */
    private static String getRevDLabel(int code) {
        switch (code) {

            case 200401:
            case 200402:
                return "AOI";
            case 200300:
                return "N";
            case 200101:
                return "LA";
            case 200201:
            case 200202:
                return "DA";
            case 170800:
                return "BDZ";
            case 150501:
                return "JTAA";
            case 150502:
                return "SAA";
            case 150503:
                return "SGSA";
            case 140700:
                return "FCL";
            case 151500:
                return "ASLT";
            case 170400:
                return "SL";
            case 170600:
                return "TC";
            case 171100:
                return "AARROZ";
            case 171200:
                return "UAROZ";
            case 171300:
                return "WEZ";
            case 171400:
                return "FEZ";
            case 171500:
                return "JEZ";
            case 171900:
                return "SHORADEZ";
            case 190100:
                return "IFF OFF";
            case 190200:
                return "IFF ON";
            case 220102:
                return "EW";
            case 220107:
                return "J";
            case 220108:
                return "RDF";
            case 260300:    //nfl handle like fscl
                return "NFL";
            case 260400:
                return "BCL";
            case 300100:
                return "ICL";
            case 140400:
            case 140401:
                return "FEBA";
            case 140900:
                return "LOA";
            default:
                return "";
        }
    }

    /**
     * The new public interface to handle Mil-Std-2525 Rev D, replaces or calls
     * AddModifiersGeo
     *
     * @param tg
     * @param g2d
     * @param clipBounds
     * @param converter
     */
    public static void AddModifiersGeo2(TGLight tg,
            Graphics2D g2d,
            Object clipBounds,
            IPointConversion converter) {
        if (tg.get_SymbolId().length() < 20) {
            Modifier2.AddModifiersGeo(tg, g2d, clipBounds, converter);
            return;
        }
        try {
            String symbolId = tg.get_SymbolId();
            //String setA = getSetA(tg.get_SymbolId());
            String setA = symbolId.substring(0, 10);
            //String setB = getSetB(tg.get_SymbolId());
            String setB = symbolId.substring(10);
            //String code = getCode(setB);
            String code = setB.substring(0, 6);
            int nCode = Integer.parseInt(code);
            //String symbolSet = getSymbolSet(setA);
            String symbolSet = setA.substring(4, 6);
            int nSymbol = Integer.parseInt(symbolSet);
            //default values for modifiers AP and V
            String country = "US";  //country AS modifier
            String v = "MORTAR";    //type            
            String ap = "QC 1968";    //target designator    AP modifier
            //assume we are using tg.get_Location() for the Y modifier
            //uncomment 3 lines after the methods become available           
            //country=tg.get_AS();
            //v=tg.get_V();
            //ap=tg.get_AP();
            String t = tg.get_Name();
            String label = GetCenterLabel(tg);
            double csFactor = 1d;
            if (nSymbol == 45 || nSymbol == 46) {
                Modifier2.AddModifiersGeo(tg, g2d, clipBounds, converter);
                return;
            }
            if (nSymbol != 25) {
                Modifier2.AddModifiersGeo(tg, g2d, clipBounds, converter);
                return;
            }
            //at this point the symbol is a control measure
            double factor = 1;//10d/tg.get_Font().getSize();
            //int linetype = tg.get_LineType();
            //boolean visibleModifiers=tg.get_VisibleLabels();
            int j = 0, k = 0;
            double x = 0, y = 0;

            if (tg.get_Font() != null && tg.get_Font().getSize() > 0) {
                factor = 10d / tg.get_Font().getSize();
            } else {
                return;
            }
            FontMetrics metrics = g2d.getFontMetrics();
            int stringWidth = 0, stringWidth2 = 0;
            String dash = "";
            if (tg.get_DTG() != null && tg.get_DTG1() != null && tg.get_DTG().isEmpty() == false && tg.get_DTG1().isEmpty() == false) {
                dash = " - ";
            }
            int lastIndex = tg.Pixels.size() - 1;
            int nextToLastIndex = tg.Pixels.size() - 2;
            POINT2 pt0 = new POINT2(tg.Pixels.get(0));
            POINT2 pt1 = null;
            POINT2 pt2 = null, pt3 = null;
            POINT2 ptLast = new POINT2(tg.Pixels.get(lastIndex));
            POINT2 ptNextToLast = null, midPt = null;

            if (lastIndex > 0) {
                ptNextToLast = new POINT2(tg.Pixels.get(lastIndex - 1));
            }

            if (tg.Pixels.size() > 1) {
                pt1 = new POINT2(tg.Pixels.get(1));
            }
            if (tg.Pixels.size() > 2) {
                pt2 = new POINT2(tg.Pixels.get(2));
            }
            if (tg.Pixels.size() > 3) {
                pt3 = new POINT2(tg.Pixels.get(3));
            }
            //String label = GetCenterLabel(tg);
            Object[] pts = tg.Pixels.toArray();
            POINT2 ptCenter = null;
            if (converter != null) //cpof uses latlonconverter so cpof passes null for this               
            {
                ptCenter = mdlGeodesic.geodesic_center(tg.LatLongs);
                if (ptCenter != null) {
                    Point2D pt2d = new Point2D.Double(ptCenter.x, ptCenter.y);
                    //Point pt = converter.GeoToPixels(new Point2D.Double(ptCenter.x, ptCenter.y));
                    pt2d = converter.GeoToPixels(pt2d);
                    ptCenter.x = pt2d.getX();
                    ptCenter.y = pt2d.getY();
                } else {
                    ptCenter = lineutility.CalcCenterPointDouble2(pts, pts.length);
                }
            } else {
                ptCenter = lineutility.CalcCenterPointDouble2(pts, pts.length);
            }
            POINT2 lr = new POINT2(tg.Pixels.get(0));
            POINT2 ll = new POINT2(tg.Pixels.get(0));
            POINT2 ul = new POINT2(tg.Pixels.get(0));
            POINT2 ur = new POINT2(tg.Pixels.get(0));
            Rectangle2D clipRect = null;
            ArrayList<Point2D> clipArray = null;
            if (clipBounds != null && ArrayList.class.isAssignableFrom(clipBounds.getClass())) {
                clipArray = (ArrayList<Point2D>) clipBounds;
            }
            if (clipBounds != null && Rectangle2D.Double.class.isAssignableFrom(clipBounds.getClass())) {
                clipRect = (Rectangle2D.Double) clipBounds;
            }
            int middleSegment = (tg.Pixels.size() + 1) / 2 - 1;

            if (clipRect != null) {
                middleSegment = getVisibleMiddleSegment(tg, clipRect);
            } else if (clipArray != null) {
                middleSegment = getVisibleMiddleSegment(tg, clipArray);
            }
            double dist = 0, dist2 = 0;
            Font font = null;
            font = tg.get_Font();    //might have to change this
            if (font == null) {
                font = g2d.getFont();
            }
            g2d.setFont(font);
            POINT2 ptUl = null, ptUr = null, ptLl = null, ptLr = null;
            //switch adds the new modifiers or calls the old function if the modifiers did not change        
            switch (nCode) {
                case 200401:
                    ptUr = new POINT2();
                    ptUl = new POINT2();
                    ptLl = new POINT2();
                    ptLr = new POINT2();
                    Modifier2.GetMBR(tg, ptUl, ptUr, ptLr, ptLl);
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label, aboveMiddle, csFactor, ptLl, ptLr, false);
                    break;
                case 110101:    //lateral boundary
                case 110102:    //fwd boundary
                case 110103:    //rear boundary
                    t += " " + "(" + country + ")";
                    tg.set_Name(t);
                    tg.set_T1(t);
                    Modifier2.AddModifiersGeo(tg, g2d, clipBounds, converter);
                    break;
                case 110200:    //LL
                case 141000:    //LD
                case 141100:    //LDLC
                case 141200:    //PLD
                case 141400:    //BL
                case 141500:    //HOLD
                case 141600:    //release
                    AddIntegralAreaModifier(tg, label, aboveEnd, -csFactor, pt0, pt1, false);
                    AddIntegralAreaModifier(tg, label, aboveEnd, -csFactor, ptLast, ptNextToLast, false);
                    break;
                case 120400:
                    ptUr = new POINT2();
                    ptUl = new POINT2();
                    ptLl = new POINT2();
                    ptLr = new POINT2();
                    Modifier2.GetMBR(tg, ptUl, ptUr, ptLr, ptLl);
                    stringWidth = metrics.stringWidth(tg.get_H());
                    pt0.x = ptUr.x + stringWidth / 2 + 1;
                    //pt0.x=ptUr.x+1;
                    pt0.y = (ptUr.y + ptLr.y) / 2 - metrics.getFont().getSize();
                    AddIntegralAreaModifier(tg, tg.get_H(), area, csFactor, pt0, pt0, false);
                    break;
                case 200101:
                case 200201:
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label + " - " + tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    break;
                case 140700:
                case 140900:
                case 190100:
                case 190200:
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label, aboveEnd, -csFactor, pt0, pt1, false);
                    AddIntegralAreaModifier(tg, label, aboveEnd, -csFactor, ptLast, ptNextToLast, false);
                    break;
                case 140103:
                case 140104:
                    //AddIntegralAreaModifier(tg, label, toEnd, 0, pt0, pt1, false);
                    AddIntegralAreaModifier(tg, tg.get_N(), toEnd, -1 * csFactor, pt0, pt1, false);
                    //AddIntegralAreaModifier(tg, label, toEnd, 0, ptLast, ptNextToLast, false);
                    AddIntegralAreaModifier(tg, tg.get_N(), toEnd, -1 * csFactor, ptLast, ptNextToLast, false);
                    break;
                case 200402:
//                    label = getRevDLabel(nCode);                    
//                    AddIntegralAreaModifier(tg, label, aboveMiddle, 0, pt0, pt1, false);
//                    break;
                case 140101:    //flot has no labels
                case 140102:
                case 150101:
                case 150102:
                //case 151801:
                case 151900:
                case 152000:
                case 152100:
                case 152200:
                case 141700:
                    break;
                case 140400:    //feba has labels at end    
                case 140401:
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label, toEnd, 0, pt0, pt1, false);
                    AddIntegralAreaModifier(tg, label, toEnd, 0, ptLast, ptNextToLast, false);
                    break;
                case 150103:
                case 150104:
                    //case 151802:
                    areasWithENY(tg, g2d);
                    break;
                case 150501:
                case 150502:
                case 150503:
                    areasWithENY(tg, g2d);
                    //AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label + " " + tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, csFactor, ptCenter, ptCenter, false);
                    break;
                case 151000:    //fort now has T modifier
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    break;
                case 151401:    //airaoa
                case 151402:    //rotary
                    if (tg.Pixels.size() == 3) //one segment           
                    {
                        midPt = lineutility.MidPointDouble(pt0, pt1, 0);
                        AddIntegralAreaModifier(tg, tg.get_DTG(), aboveMiddle, 0, midPt, midPt, false);
                        AddIntegralAreaModifier(tg, tg.get_DTG1(), aboveMiddle, csFactor, midPt, midPt, false);
                        AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, 2 * csFactor, midPt, midPt, false);

                    } else if (tg.Pixels.size() == 4) //2 segments
                    {
                        midPt = lineutility.MidPointDouble(pt1, pt2, 0);
                        AddIntegralAreaModifier(tg, tg.get_DTG(), aboveMiddle, 0, midPt, midPt, false);
                        AddIntegralAreaModifier(tg, tg.get_DTG1(), aboveMiddle, csFactor, midPt, midPt, false);
                        AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, 2 * csFactor, midPt, midPt, false);
                    } else // 3 or more segments
                    {
                        midPt = lineutility.MidPointDouble(pt1, pt2, 0);
                        AddIntegralAreaModifier(tg, tg.get_DTG(), aboveMiddle, -csFactor / 2, midPt, midPt, false);
                        AddIntegralAreaModifier(tg, tg.get_DTG1(), aboveMiddle, csFactor / 2, midPt, midPt, false);
                        midPt = lineutility.MidPointDouble(pt2, pt3, 0);
                        AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, -csFactor / 2, midPt, midPt, false);
                    }
                    break;
                case 151403:    //main
                case 151404:    //spt
                case 151405:
                case 151406:
                case 151407:
                case 151408:
                    if (tg.Pixels.size() == 3) //one segment           
                    {
                        midPt = lineutility.MidPointDouble(pt0, pt1, 0);
                        AddIntegralAreaModifier(tg, tg.get_DTG(), aboveMiddle, 0, midPt, midPt, false);
                        AddIntegralAreaModifier(tg, tg.get_DTG1(), aboveMiddle, csFactor, midPt, midPt, false);
                        AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, 2 * csFactor, midPt, midPt, false);

                    } else //2 or more segments
                    {
                        midPt = lineutility.MidPointDouble(pt0, pt1, 0);
                        AddIntegralAreaModifier(tg, tg.get_DTG(), aboveMiddle, -csFactor / 2, midPt, midPt, false);
                        AddIntegralAreaModifier(tg, tg.get_DTG1(), aboveMiddle, csFactor / 2, midPt, midPt, false);
                        midPt = lineutility.MidPointDouble(pt1, pt2, 0);
                        AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, -csFactor / 2, midPt, midPt, false);
                    }
                    break;
                case 140601:    //diratkair
                case 140602:
                case 140603:
                case 140604:
                case 140605:
                    midPt = lineutility.MidPointDouble(pt0, pt1, 0);
                    //midPt=lineutility.MidPointDouble(pt0, midPt, 0);
                    AddIntegralAreaModifier(tg, tg.get_Name(), aboveMiddle, 0, pt0, midPt, false);
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, csFactor, pt0, pt1, false);
                    break;
                case 140606:
                case 140607:
                    midPt = lineutility.MidPointDouble(pt0, pt1, 0);
                    AddIntegralAreaModifier(tg, tg.get_N(), aboveMiddle, 0, pt1, midPt, false);
                    break;
                case 170800:    //bdz
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label, aboveMiddle, 0, ptCenter, ptCenter, false);
                    break;
                case 151500:    //assault pos
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label + " " + tg.get_Name(), aboveMiddle, 0, ptCenter, ptCenter, false);
                    break;
                case 151600:    //atk pos
                case 151700:    //obj
                    AddIntegralAreaModifier(tg, label + " " + tg.get_Name(), aboveMiddle, 0, ptCenter, ptCenter, false);
                    break;
                case 141300:
                    GetMBR(tg, ul, ur, lr, ll);
                    AddIntegralAreaModifier(tg, label, aboveMiddle, 1.35 * factor * csFactor, ll, lr, false);
                    break;
                case 170100:
                case 170200:
                case 170300:
                case 170500:
                case 170700:
                    AddIntegralModifier(tg, label + " " + tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "Max Alt: " + tg.get_H1(), aboveMiddle, -4 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "Min Alt: " + tg.get_H(), aboveMiddle, -5 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "Width: " + tg.get_H2(), aboveMiddle, -6 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "Name: " + tg.get_Name(), aboveMiddle, -7 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "DTG Start: " + tg.get_DTG(), aboveMiddle, -3 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "DTG End: " + tg.get_DTG1(), aboveMiddle, -2 * csFactor, middleSegment, middleSegment + 1, false);
                    break;
                case 170400:
                case 170600:
                    label = getRevDLabel(nCode);
                    AddIntegralModifier(tg, label + " " + tg.get_Name(), aboveMiddle, 0, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "Max Alt: " + tg.get_H1(), aboveMiddle, -4 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "Min Alt: " + tg.get_H(), aboveMiddle, -5 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "Width: " + tg.get_H2(), aboveMiddle, -6 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "Name: " + tg.get_Name(), aboveMiddle, -7 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "DTG Start: " + tg.get_DTG(), aboveMiddle, -3 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, "DTG End: " + tg.get_DTG1(), aboveMiddle, -2 * csFactor, middleSegment, middleSegment + 1, false);
                    break;
                case 171100:
                case 171200:
                case 171300:
                case 171400:
                case 171500:
                case 171900:
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label, area, -2.5, ptCenter, ptCenter, false, "");
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, -1.5, ptCenter, ptCenter, false, "T");
                    AddIntegralAreaModifier(tg, "MIN ALT: " + tg.get_H(), area, -0.5, ptCenter, ptCenter, false, "H");
                    AddIntegralAreaModifier(tg, "MAX ALT: " + tg.get_H1(), area, 0.5, ptCenter, ptCenter, false, "H1");
                    AddIntegralAreaModifier(tg, "TIME FROM: " + tg.get_DTG(), area, 1.5, ptCenter, ptCenter, false, "W");
                    AddIntegralAreaModifier(tg, "TIME TO: " + tg.get_DTG1(), area, 2.5, ptCenter, ptCenter, false, "W1");
                    break;
                case 200300:
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label, aboveMiddle, -1, pt0, pt0, false); //ENY or N?
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, csFactor, pt0, pt0, false);
                    break;
                case 220107:
                case 220108:
                    label = getRevDLabel(nCode);
                    AddIntegralAreaModifier(tg, label, aboveMiddle, 0, pt0, pt1, false); //ENY or N?
                    break;
                case 240101:
                    AddIntegralAreaModifier(tg, label, area, -3 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, -2 * csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, "MIN ALT: " + tg.get_H(), area, -1 * csFactor, ptCenter, ptCenter, false, "H");
                    AddIntegralAreaModifier(tg, "MAX ALT: " + tg.get_H1(), area, 0, ptCenter, ptCenter, false, "H1");
                    AddIntegralAreaModifier(tg, tg.get_Location(), area, 1 * csFactor, ptCenter, ptCenter, false, "H2");
                    AddIntegralAreaModifier(tg, tg.get_DTG(), area, 2 * csFactor, ptCenter, ptCenter, false, "W");
                    AddIntegralAreaModifier(tg, tg.get_DTG1(), area, 3 * csFactor, ptCenter, ptCenter, false, "W1");
                    break;
                case 300100:    //icl
                    label = getRevDLabel(nCode);
                    pt0 = tg.Pixels.get(0);
                    pt1 = tg.Pixels.get(1);
                    pt2 = tg.Pixels.get(tg.Pixels.size() - 1);
                    pt3 = tg.Pixels.get(tg.Pixels.size() - 2);
                    dist = lineutility.CalcDistanceDouble(pt0, pt1);
                    dist2 = lineutility.CalcDistanceDouble(pt2, pt3);
                    stringWidth = (int) ((double) metrics.stringWidth(tg.get_Name() + " " + label));
                    stringWidth2 = (int) ((double) metrics.stringWidth(tg.get_DTG()));
                    if (stringWidth2 > stringWidth) {
                        stringWidth = stringWidth2;
                    }

                    if (tg.Pixels.size() == 2) //one segment
                    {
                        pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                        //AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        if (dist > 3.5 * stringWidth)//was 28stringwidth+5
                        {
                            pt0 = tg.Pixels.get(tg.Pixels.size() - 1);
                            pt1 = tg.Pixels.get(tg.Pixels.size() - 2);
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                    } else //more than one semgent
                    {
                        double dist3 = lineutility.CalcDistanceDouble(pt0, pt2);
                        if (dist > stringWidth + 5 || dist >= dist2 || dist3 > stringWidth + 5) {
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                        if (dist2 > stringWidth + 5 || dist2 > dist || dist3 > stringWidth + 5) {
                            pt0 = tg.Pixels.get(tg.Pixels.size() - 1);
                            pt1 = tg.Pixels.get(tg.Pixels.size() - 2);
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                    }
                    break;
                case 260300:    //nfl handle like fscl
                case 260400:
                    label = getRevDLabel(nCode);
                    pt0 = tg.Pixels.get(0);
                    pt1 = tg.Pixels.get(1);
                    pt2 = tg.Pixels.get(tg.Pixels.size() - 1);
                    pt3 = tg.Pixels.get(tg.Pixels.size() - 2);
                    dist = lineutility.CalcDistanceDouble(pt0, pt1);
                    dist2 = lineutility.CalcDistanceDouble(pt2, pt3);
                    stringWidth = (int) ((double) metrics.stringWidth(tg.get_Name() + " " + label));
                    stringWidth2 = (int) ((double) metrics.stringWidth(tg.get_DTG()));
                    if (stringWidth2 > stringWidth) {
                        stringWidth = stringWidth2;
                    }

                    if (tg.Pixels.size() == 2) //one segment
                    {
                        pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                        //AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                        AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        if (dist > 3.5 * stringWidth)//was 28stringwidth+5
                        {
                            pt0 = tg.Pixels.get(tg.Pixels.size() - 1);
                            pt1 = tg.Pixels.get(tg.Pixels.size() - 2);
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                    } else //more than one semgent
                    {
                        double dist3 = lineutility.CalcDistanceDouble(pt0, pt2);
                        if (dist > stringWidth + 5 || dist >= dist2 || dist3 > stringWidth + 5) {
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                        if (dist2 > stringWidth + 5 || dist2 > dist || dist3 > stringWidth + 5) {
                            pt0 = tg.Pixels.get(tg.Pixels.size() - 1);
                            pt1 = tg.Pixels.get(tg.Pixels.size() - 2);
                            pt1 = lineutility.ExtendAlongLineDouble(pt0, pt1, stringWidth);
                            //AddModifier2(tg, tg.get_Name() + " " + label, aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, label + " " + tg.get_Name(), aboveMiddle, -0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG(), aboveMiddle, 0.7 * csFactor, pt0, pt1, false);
                            AddModifier2(tg, tg.get_DTG1(), aboveMiddle, 1.7 * csFactor, pt0, pt1, false);
                        }
                    }
                    break;
                case 260600:    //mfp
                    pt0 = tg.Pixels.get(middleSegment);
                    pt1 = tg.Pixels.get(middleSegment + 1);
                    AddIntegralModifier(tg, label, aboveMiddle, 0, middleSegment, middleSegment + 1, true);
                    AddIntegralModifier(tg, tg.get_DTG(), aboveEnd, 1 * csFactor, 0, 1, false);
                    AddIntegralModifier(tg, tg.get_DTG1(), aboveEnd, 2 * csFactor, 0, 1, false);
                    break;
                case 240701:    //lintgt
                    AddIntegralModifier(tg, ap, aboveMiddle, -0.8 * csFactor, middleSegment, middleSegment + 1, false);
                    break;
                case 240702:    //smoke
                    AddIntegralModifier(tg, ap, aboveMiddle, -0.8 * csFactor, middleSegment, middleSegment + 1, false);
                    AddIntegralModifier(tg, label, aboveMiddle, 0.8 * csFactor, middleSegment, middleSegment + 1, false);
                    break;
                case 240703:
                    AddIntegralModifier(tg, ap, aboveMiddle, -1 * csFactor, 0, 1, false);
                    AddIntegralModifier(tg, label, aboveMiddle, 1 * csFactor, 0, 1, false);
                    AddIntegralModifier(tg, tg.get_T1(), aboveMiddle, 2 * csFactor, 0, 1, false);
                    AddIntegralModifier(tg, v, aboveMiddle, 3 * csFactor, 0, 1, false);
                    break;
                case 240801:
                    AddIntegralAreaModifier(tg, ap, area, 0, ptCenter, ptCenter, false);
                    break;
                case 240802:
                case 240803:
                    AddIntegralAreaModifier(tg, ap, area, 0, pt0, pt0, false);
                    break;
                case 240804:
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 0, ptCenter, ptCenter, false);
                    break;
                case 240806:    //smoke
                case 240807:
                    AddIntegralAreaModifier(tg, ap, area, -csFactor, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), area, 1 * csFactor, ptCenter, ptCenter, false, "W+W1");
                    break;
                case 242301:    //kill boxes irregular
                case 242304:
                    AddIntegralAreaModifier(tg, label, area, 0, ptCenter, ptCenter, false);
                    AddIntegralAreaModifier(tg, tg.get_Name(), area, 1 * csFactor, ptCenter, ptCenter, false);
                    GetMBR(tg, ul, ur, lr, ll);
                    POINT2 ptLeft = ul;
                    POINT2 ptRight = ur;
                    if (tg.get_Client().equalsIgnoreCase("ge")) {
                        ptLeft.x -= font.getSize() / 2;
                        ptRight.x -= font.getSize() / 2;
                    }
                    AddIntegralAreaModifier(tg, tg.get_DTG(), toEnd, 0.5 * csFactor, ptLeft, ptRight, false, "W");
                    AddIntegralAreaModifier(tg, tg.get_DTG1(), toEnd, 1.5 * csFactor, ptLeft, ptRight, false, "W1");
                    break;
                case 242302:    //kill box rect, circ have W,W1 outside use AddModifiers2RevD
                case 242303:
                case 242305:
                case 242306:
                case 140500:    //pdf has no label
                    break;
                case 290100:    //zone
                    AddIntegralModifier(tg, tg.get_Name(), aboveMiddle, csFactor, middleSegment, middleSegment + 1, false);
                    break;
                case 290600:    //lane new W-W1 use addModifiers2RevD
                    break;
                case 270800:    //mined
                    GetMBR(tg, ul, ur, lr, ll);
                    AddIntegralAreaModifier(tg, tg.get_H(), aboveMiddle, -1.5 * factor * csFactor, ul, ur, false);
                    AddIntegralAreaModifier(tg, tg.get_DTG(), aboveMiddle, 1.5 * factor * csFactor, ll, lr, false);
                    areasWithENY(tg, g2d);
                    break;
                case 271300:    //ASLTXING uses W-W1 like gap
                    if (tg.Pixels.get(1).y > tg.Pixels.get(0).y) {
                        pt0 = tg.Pixels.get(1);
                        pt1 = tg.Pixels.get(3);
                        pt2 = tg.Pixels.get(0);
                        pt3 = tg.Pixels.get(2);
                    } else {
                        pt0 = tg.Pixels.get(0);
                        pt1 = tg.Pixels.get(2);
                        pt2 = tg.Pixels.get(1);
                        pt3 = tg.Pixels.get(3);
                    }
                    pt2 = lineutility.ExtendAlongLineDouble2(pt0, pt2, -20);
                    pt3 = lineutility.ExtendAlongLineDouble2(pt1, pt3, -20);
                    AddIntegralAreaModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 0, pt2, pt3, false);
                    break;
                case 330100:    //convoys
                case 330200:
                    String convoyBlankString = blankString(metrics, 35);
                    AddIntegralModifier(tg, v + convoyBlankString + tg.get_H(), aboveMiddle, 0, 0, 1, false);
                    AddIntegralModifier(tg, tg.get_DTG() + dash + tg.get_DTG1(), aboveMiddle, 1.2 * csFactor, 0, 1, false);
                    break;
                default:
                    Modifier2.AddModifiersGeo(tg, g2d, clipBounds, converter);
                    break;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "AddModifiersGeo2",
                    new RendererException("Failed inside AddModifiersGeo2", exc));
        }
    }
}
