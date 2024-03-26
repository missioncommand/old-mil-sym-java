/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaTacticalRenderer;

import JavaLineArray.TacticalLines;
import JavaLineArray.lineutility;
import JavaLineArray.POINT2;
import JavaLineArray.ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import JavaLineArray.Shape2;
import java.awt.Color;
import java.io.*;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;
import ArmyC2.C2SD.Utilities.SymbolUtilities;
import ArmyC2.C2SD.Utilities.ErrorLogger;
import ArmyC2.C2SD.Utilities.RendererException;
import ArmyC2.C2SD.Utilities.RendererSettings;
import ArmyC2.C2SD.Utilities.ShapeInfo;
import java.awt.Polygon;
import java.awt.geom.Line2D;
/**
 * A general utility class for the tactical renderer
 * @author Michael Deutch
 */
public final class clsUtility {
    private static Map<String,Object> linetypes=null;
    private static Map<String,Object> metocs=null;
    private static final String _className = "clsUtility";
    protected static Point2D POINT2ToPoint2D(POINT2 pt2) {
        if (pt2 == null) {
            return null;
        }

        double x = pt2.x;
        double y = pt2.y;
        Point2D pt = new Point2D.Double(x, y);
        return pt;
    }
    /**
     * returns true if the line segments are all outside the bounds
     * @param tg the tactical graphic
     * @param clipBounds the pixels based clip bounds
     * @return 
     */
    public static boolean linesOutsideClipBounds(TGLight tg,
            Rectangle2D clipBounds)
    {
        try
        {
            boolean isAutoshape=isAutoshape(tg);
            if(isAutoshape)
                return false;

            double xmin=clipBounds.getMinX();
            double xmax=clipBounds.getMaxX();
            double ymin=clipBounds.getMinY();
            double ymax=clipBounds.getMaxY();
            int j=0;
            POINT2 pt0=null,pt1=null;
            Line2D boundsEdge=null,ptsLine=null;            
            for(j=0;j<tg.Pixels.size()-1;j++)
            {
                pt0=tg.Pixels.get(j);
                pt1=tg.Pixels.get(j+1);
                
                //if either point is inside the bounds return false
                if(clipBounds.contains(pt0.x, pt0.y))
                    return false;
                if(clipBounds.contains(pt1.x, pt1.y))
                    return false;
                
                ptsLine=new Line2D.Double(pt0.x,pt0.y,pt1.x,pt1.y);
                
                //if the pt0-pt1 line intersects any clip bounds edge then return false
                boundsEdge=new Line2D.Double(xmin,ymin,xmax,ymin);
                if(ptsLine.intersectsLine(boundsEdge))
                    return false;                
                
                boundsEdge=new Line2D.Double(xmax,ymin,xmax,ymax);
                if(ptsLine.intersectsLine(boundsEdge))
                    return false;                
                
                boundsEdge=new Line2D.Double(xmax,ymax,xmin,ymax);
                if(ptsLine.intersectsLine(boundsEdge))
                    return false;                
                
                boundsEdge=new Line2D.Double(xmin,ymax,xmin,ymin);
                if(ptsLine.intersectsLine(boundsEdge))
                    return false;                
            }
        }
        catch (Exception exc) 
        {
            ErrorLogger.LogException(_className ,"linesOutsideClipBounds",
                    new RendererException("Failed inside linesOutsideClipBounds", exc));
        }    
        return true;
    }
    /**
     * @deprecated 
     * test to see if the pixels outline contains the clip bounds
     * @param tg
     * @param clipBounds
     * @return true if pixels outline contains the clip bounds
     */
    public static boolean graphicContainsClipBounds(TGLight tg,
            Rectangle2D clipBounds)
    {
        try
        {
            boolean isClosedArea=isClosedPolygon(tg.get_LineType());
            boolean isAutoshape=isAutoshape(tg);
            if(!isClosedArea)
                return false;
            if(isAutoshape)
                return false;
            
            double x=0;
            double y=0;
            Polygon poly=new Polygon();
            int j=0;
            for(j=0;j<tg.Pixels.size();j++)
            {
                x=tg.Pixels.get(j).x;
                y=tg.Pixels.get(j).y;
                poly.addPoint((int)x, (int)y);                
            }            
            POINT2 pt0=tg.Pixels.get(0);
            POINT2 ptLast=tg.Pixels.get(tg.Pixels.size()-1);
            if(pt0.x != ptLast.x || pt0.y != ptLast.y)
            {
                x=ptLast.x;
                y=ptLast.y;
                poly.addPoint((int)x, (int)y);                
            }
            if(poly.contains(clipBounds))                
                return true;            
        }
        catch (Exception exc) 
        {
            ErrorLogger.LogException(_className ,"graphicContainsClipBounds",
                    new RendererException("Failed inside graphicContainsClipBounds", exc));
        }    
        return false;
    }
    /**
     * Returns the minimum client points needed for the symbol
     * @param lineType line type
     * @return minimum number of clients required to render the line
     */
    public static int GetMinPoints(int lineType) {
        int result = -1;
        switch (lineType) {
            case TacticalLines.RECTANGULAR:
            case TacticalLines.PBS_RECTANGLE:
            case TacticalLines.PBS_SQUARE:
            case TacticalLines.PBS_ELLIPSE:
            case TacticalLines.BS_ELLIPSE:
            case TacticalLines.PBS_CIRCLE:
                result = 1;
                break;
            case TacticalLines.BBS_POINT:
            case TacticalLines.CIRCULAR:
            case TacticalLines.FSA_CIRCULAR:
            case TacticalLines.FFA_CIRCULAR:
            case TacticalLines.NFA_CIRCULAR:
            case TacticalLines.RFA_CIRCULAR:
            case TacticalLines.ACA_CIRCULAR:
            case TacticalLines.PAA_CIRCULAR:
            case TacticalLines.ATI_CIRCULAR:
            case TacticalLines.CFFZ_CIRCULAR:
            case TacticalLines.SENSOR_CIRCULAR:
            case TacticalLines.CENSOR_CIRCULAR:
            case TacticalLines.DA_CIRCULAR:
            case TacticalLines.CFZ_CIRCULAR:
            case TacticalLines.ZOR_CIRCULAR:
            case TacticalLines.TBA_CIRCULAR:
            case TacticalLines.TVAR_CIRCULAR:
            case TacticalLines.KILLBOXBLUE_CIRCULAR:
            case TacticalLines.KILLBOXPURPLE_CIRCULAR:
            case TacticalLines.LAUNCH_AREA:
                result = 1; //was 2
                break;
            case TacticalLines.RANGE_FAN:
            case TacticalLines.RANGE_FAN_SECTOR:
                result = 1;
                break;
            case TacticalLines.PAA_RECTANGULAR_REVC:
            case TacticalLines.FSA_RECTANGULAR:
            case TacticalLines.FFA_RECTANGULAR:
            case TacticalLines.RFA_RECTANGULAR:
            case TacticalLines.NFA_RECTANGULAR:
            case TacticalLines.ACA_RECTANGULAR:
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
                result = 2; //was 3
                break;
            case TacticalLines.SPTBYFIRE:
            case TacticalLines.RIP:
            case TacticalLines.GAP:
            case TacticalLines.ASLTXING:
            case TacticalLines.BRIDGE:
            case TacticalLines.MSDZ:
            case TacticalLines.SCREEN_REVC:
            case TacticalLines.COVER_REVC:
            case TacticalLines.GUARD_REVC:
            case TacticalLines.SEIZE_REVC:
                result = 4;
                break;
            case TacticalLines.BYPASS:
            case TacticalLines.BLOCK:
            case TacticalLines.BREACH:
            case TacticalLines.CANALIZE:
            case TacticalLines.CLEAR:
            case TacticalLines.CONTAIN:
            case TacticalLines.DELAY:
            case TacticalLines.DISRUPT:
            case TacticalLines.PENETRATE:
            case TacticalLines.RETIRE:
            case TacticalLines.SCREEN:
            case TacticalLines.COVER:
            case TacticalLines.GUARD:
            case TacticalLines.SEIZE:
            case TacticalLines.WITHDRAW:
            case TacticalLines.WDRAWUP:
            //non task autoshapes
            case TacticalLines.SARA:
            case TacticalLines.DECEIVE:
            case TacticalLines.DUMMY:
            case TacticalLines.PDF:
            case TacticalLines.IL:
            case TacticalLines.ATKBYFIRE:
            case TacticalLines.AMBUSH:
            case TacticalLines.HOLD:
            case TacticalLines.RELEASE:
            case TacticalLines.BRDGHD:
            case TacticalLines.MNFLDBLK:
            case TacticalLines.MNFLDDIS:
            case TacticalLines.TURN:
            case TacticalLines.PLANNED:
            case TacticalLines.ESR1:
            case TacticalLines.ESR2:
            case TacticalLines.ROADBLK:
            case TacticalLines.TRIP:
            case TacticalLines.EASY:
            case TacticalLines.BYDIF:
            case TacticalLines.BYIMP:
            case TacticalLines.FORDSITE:
            case TacticalLines.FORDIF:
            //METOCs
            case TacticalLines.IFR:
            case TacticalLines.MVFR:
            case TacticalLines.TURBULENCE:
            case TacticalLines.ICING:
            case TacticalLines.NON_CONVECTIVE:
            case TacticalLines.CONVECTIVE:
            case TacticalLines.FROZEN:
            case TacticalLines.THUNDERSTORMS:
            case TacticalLines.FOG:
            case TacticalLines.SAND:
            case TacticalLines.FREEFORM:
            case TacticalLines.DEPTH_AREA:
            case TacticalLines.ISLAND:
            case TacticalLines.BEACH:
            case TacticalLines.WATER:
            case TacticalLines.WEIRS:
            case TacticalLines.SWEPT_AREA:
            case TacticalLines.OIL_RIG_FIELD:
            case TacticalLines.FOUL_GROUND:
            case TacticalLines.KELP:
            case TacticalLines.BEACH_SLOPE_MODERATE:
            case TacticalLines.BEACH_SLOPE_STEEP:
            case TacticalLines.ANCHORAGE_AREA:
            case TacticalLines.TRAINING_AREA:
            case TacticalLines.FORESHORE_AREA:
            case TacticalLines.DRYDOCK:
            case TacticalLines.LOADING_FACILITY_AREA:
            case TacticalLines.PERCHES:
            case TacticalLines.UNDERWATER_HAZARD:
            case TacticalLines.DISCOLORED_WATER:
            case TacticalLines.BEACH_SLOPE_FLAT:
            case TacticalLines.BEACH_SLOPE_GENTLE:
            case TacticalLines.MARITIME_AREA:
            case TacticalLines.OPERATOR_DEFINED:
            case TacticalLines.SUBMERGED_CRIB:
            case TacticalLines.VDR_LEVEL_12:
            case TacticalLines.VDR_LEVEL_23:
            case TacticalLines.VDR_LEVEL_34:
            case TacticalLines.VDR_LEVEL_45:
            case TacticalLines.VDR_LEVEL_56:
            case TacticalLines.VDR_LEVEL_67:
            case TacticalLines.VDR_LEVEL_78:
            case TacticalLines.VDR_LEVEL_89:
            case TacticalLines.VDR_LEVEL_910:
            case TacticalLines.SOLID_ROCK:
            case TacticalLines.CLAY:
            case TacticalLines.VERY_COARSE_SAND:
            case TacticalLines.COARSE_SAND:
            case TacticalLines.MEDIUM_SAND:
            case TacticalLines.FINE_SAND:
            case TacticalLines.VERY_FINE_SAND:
            case TacticalLines.VERY_FINE_SILT:
            case TacticalLines.FINE_SILT:
            case TacticalLines.MEDIUM_SILT:
            case TacticalLines.COARSE_SILT:
            case TacticalLines.BOULDERS:
            case TacticalLines.OYSTER_SHELLS:
            case TacticalLines.PEBBLES:
            case TacticalLines.SAND_AND_SHELLS:
            case TacticalLines.BOTTOM_SEDIMENTS_LAND:
            case TacticalLines.BOTTOM_SEDIMENTS_NO_DATA:
            case TacticalLines.BOTTOM_ROUGHNESS_SMOOTH:
            case TacticalLines.BOTTOM_ROUGHNESS_MODERATE:
            case TacticalLines.BOTTOM_ROUGHNESS_ROUGH:
            case TacticalLines.CLUTTER_LOW:
            case TacticalLines.CLUTTER_MEDIUM:
            case TacticalLines.CLUTTER_HIGH:
            case TacticalLines.IMPACT_BURIAL_0:
            case TacticalLines.IMPACT_BURIAL_10:
            case TacticalLines.IMPACT_BURIAL_20:
            case TacticalLines.IMPACT_BURIAL_75:
            case TacticalLines.IMPACT_BURIAL_100:
            case TacticalLines.BOTTOM_CATEGORY_A:
            case TacticalLines.BOTTOM_CATEGORY_B:
            case TacticalLines.BOTTOM_CATEGORY_C:
            case TacticalLines.BOTTOM_TYPE_A1:
            case TacticalLines.BOTTOM_TYPE_A2:
            case TacticalLines.BOTTOM_TYPE_A3:
            case TacticalLines.BOTTOM_TYPE_B1:
            case TacticalLines.BOTTOM_TYPE_B2:
            case TacticalLines.BOTTOM_TYPE_B3:
            case TacticalLines.BOTTOM_TYPE_C1:
            case TacticalLines.BOTTOM_TYPE_C2:
            case TacticalLines.BOTTOM_TYPE_C3:
                result = 3;
                break;
            case TacticalLines.MRR:
            case TacticalLines.UAV:
            case TacticalLines.LLTR:
            case TacticalLines.FEBA:
            case TacticalLines.DIRATKAIR:
            case TacticalLines.ABATIS:
            case TacticalLines.CLUSTER:
            case TacticalLines.MNFLDFIX:
            case TacticalLines.FERRY:
            case TacticalLines.MFLANE:
            case TacticalLines.RAFT:
            case TacticalLines.FOXHOLE:
            case TacticalLines.LINTGT:
            case TacticalLines.LINTGTS:
            case TacticalLines.FPF:
            case TacticalLines.CONVOY:
            case TacticalLines.HCONVOY:
                result = 2;
                break;
            default:
                result = 2;
                break;
        }
        if (isClosedPolygon(lineType)) {
            result = 3;
        }
        //add code for change 1 areas
        return result;
    }
    /**
     * 
     * @param linetype the line type
     * @return true if the line is a basic shape
     */
    public static boolean isBasicShape(int linetype)
    {
        switch(linetype)
        {
            case TacticalLines.BS_AREA:
            case TacticalLines.BS_LINE:
            case TacticalLines.BS_CROSS:
            case TacticalLines.BS_ELLIPSE:
            case TacticalLines.PBS_ELLIPSE:
            case TacticalLines.PBS_CIRCLE:
            case TacticalLines.PBS_SQUARE:
            case TacticalLines.PBS_RECTANGLE:
            case TacticalLines.BS_RECTANGLE:
            case TacticalLines.BBS_AREA:
            case TacticalLines.BBS_LINE:
            case TacticalLines.BBS_POINT:
            case TacticalLines.BBS_RECTANGLE:
            case TacticalLines.BS_BBOX:
                return true;
            default:
                return false;
        }
    }
    /**
     * @param linetype line type
     * @return true if the line is a closed area
     */
    public static boolean isClosedPolygon(int linetype) {
        boolean result = false;
        switch (linetype) {    
            case TacticalLines.BBS_AREA:
            case TacticalLines.BS_BBOX:
            case TacticalLines.AT:
            case TacticalLines.DEPICT:
            case TacticalLines.DZ:
            case TacticalLines.MINED:
            case TacticalLines.UXO:
            case TacticalLines.ROZ:
            case TacticalLines.FAADZ:
            case TacticalLines.HIDACZ:
            case TacticalLines.MEZ:
            case TacticalLines.LOMEZ:
            case TacticalLines.HIMEZ:
            case TacticalLines.WFZ:
            case TacticalLines.DUMMY:
            case TacticalLines.PNO:
            case TacticalLines.BATTLE:
            case TacticalLines.EA:
            case TacticalLines.EZ:
            case TacticalLines.LZ:
            case TacticalLines.PZ:
            case TacticalLines.GENERAL:
            case TacticalLines.BS_AREA:
            case TacticalLines.EA1:
            case TacticalLines.ASSAULT:
            case TacticalLines.ATKPOS:
            case TacticalLines.OBJ:
            case TacticalLines.AO:
            case TacticalLines.AIRHEAD:
            case TacticalLines.NAI:
            case TacticalLines.TAI:
            case TacticalLines.OBSFAREA:
            case TacticalLines.OBSAREA:
            case TacticalLines.ZONE:
            case TacticalLines.BELT:
            case TacticalLines.STRONG:
            case TacticalLines.DRCL:
            case TacticalLines.FSA:
            case TacticalLines.ACA:
            case TacticalLines.ASSY:
            case TacticalLines.BSA:
            case TacticalLines.NFA:
            case TacticalLines.RFA:
            case TacticalLines.FARP:
            case TacticalLines.AIRFIELD:
            case TacticalLines.LAA:
            case TacticalLines.DMA:
            case TacticalLines.DMAF:
            case TacticalLines.BOMB:
            case TacticalLines.FFA:
            case TacticalLines.SMOKE:
            case TacticalLines.PAA:
            case TacticalLines.ENCIRCLE:
            case TacticalLines.DHA:
            case TacticalLines.EPW:
            case TacticalLines.RHA:
            case TacticalLines.DSA:
            case TacticalLines.RSA:
            case TacticalLines.FORT:
            case TacticalLines.PEN:
            case TacticalLines.BIO:
            case TacticalLines.RAD:
            case TacticalLines.CHEM:
            //case TacticalLines.MSDZ:
            case TacticalLines.SERIES:
            case TacticalLines.ATI:
            case TacticalLines.TBA:
            case TacticalLines.TVAR:
            case TacticalLines.CFFZ:
            case TacticalLines.CENSOR:
            case TacticalLines.SENSOR:
            case TacticalLines.ZOR:
            case TacticalLines.DA:
            case TacticalLines.CFZ:
            case TacticalLines.KILLBOXBLUE:
            case TacticalLines.KILLBOXPURPLE:
            //METOCs
            case TacticalLines.IFR:
            case TacticalLines.MVFR:
            case TacticalLines.TURBULENCE:
            case TacticalLines.ICING:
            case TacticalLines.NON_CONVECTIVE:
            case TacticalLines.CONVECTIVE:
            case TacticalLines.FROZEN:
            case TacticalLines.THUNDERSTORMS:
            case TacticalLines.FOG:
            case TacticalLines.SAND:
            case TacticalLines.FREEFORM:
            case TacticalLines.DEPTH_AREA:
            case TacticalLines.ISLAND:
            case TacticalLines.BEACH:
            case TacticalLines.WATER:
            case TacticalLines.WEIRS:
            case TacticalLines.SWEPT_AREA:
            case TacticalLines.OIL_RIG_FIELD:
            case TacticalLines.FOUL_GROUND:
            case TacticalLines.KELP:
            case TacticalLines.BEACH_SLOPE_MODERATE:
            case TacticalLines.BEACH_SLOPE_STEEP:
            case TacticalLines.ANCHORAGE_AREA:
            case TacticalLines.TRAINING_AREA:
            case TacticalLines.FORESHORE_AREA:
            case TacticalLines.DRYDOCK:
            case TacticalLines.LOADING_FACILITY_AREA:
            case TacticalLines.PERCHES:
            case TacticalLines.UNDERWATER_HAZARD:
            case TacticalLines.DISCOLORED_WATER:
            case TacticalLines.BEACH_SLOPE_FLAT:
            case TacticalLines.BEACH_SLOPE_GENTLE:
            case TacticalLines.MARITIME_AREA:
            case TacticalLines.OPERATOR_DEFINED:
            case TacticalLines.SUBMERGED_CRIB:
            case TacticalLines.VDR_LEVEL_12:
            case TacticalLines.VDR_LEVEL_23:
            case TacticalLines.VDR_LEVEL_34:
            case TacticalLines.VDR_LEVEL_45:
            case TacticalLines.VDR_LEVEL_56:
            case TacticalLines.VDR_LEVEL_67:
            case TacticalLines.VDR_LEVEL_78:
            case TacticalLines.VDR_LEVEL_89:
            case TacticalLines.VDR_LEVEL_910:
            case TacticalLines.SOLID_ROCK:
            case TacticalLines.CLAY:
            case TacticalLines.VERY_COARSE_SAND:
            case TacticalLines.COARSE_SAND:
            case TacticalLines.MEDIUM_SAND:
            case TacticalLines.FINE_SAND:
            case TacticalLines.VERY_FINE_SAND:
            case TacticalLines.VERY_FINE_SILT:
            case TacticalLines.FINE_SILT:
            case TacticalLines.MEDIUM_SILT:
            case TacticalLines.COARSE_SILT:
            case TacticalLines.BOULDERS:
            case TacticalLines.OYSTER_SHELLS:
            case TacticalLines.PEBBLES:
            case TacticalLines.SAND_AND_SHELLS:
            case TacticalLines.BOTTOM_SEDIMENTS_LAND:
            case TacticalLines.BOTTOM_SEDIMENTS_NO_DATA:
            case TacticalLines.BOTTOM_ROUGHNESS_SMOOTH:
            case TacticalLines.BOTTOM_ROUGHNESS_MODERATE:
            case TacticalLines.BOTTOM_ROUGHNESS_ROUGH:
            case TacticalLines.CLUTTER_LOW:
            case TacticalLines.CLUTTER_MEDIUM:
            case TacticalLines.CLUTTER_HIGH:
            case TacticalLines.IMPACT_BURIAL_0:
            case TacticalLines.IMPACT_BURIAL_10:
            case TacticalLines.IMPACT_BURIAL_20:
            case TacticalLines.IMPACT_BURIAL_75:
            case TacticalLines.IMPACT_BURIAL_100:
            case TacticalLines.BOTTOM_CATEGORY_A:
            case TacticalLines.BOTTOM_CATEGORY_B:
            case TacticalLines.BOTTOM_CATEGORY_C:
            case TacticalLines.BOTTOM_TYPE_A1:
            case TacticalLines.BOTTOM_TYPE_A2:
            case TacticalLines.BOTTOM_TYPE_A3:
            case TacticalLines.BOTTOM_TYPE_B1:
            case TacticalLines.BOTTOM_TYPE_B2:
            case TacticalLines.BOTTOM_TYPE_B3:
            case TacticalLines.BOTTOM_TYPE_C1:
            case TacticalLines.BOTTOM_TYPE_C2:
            case TacticalLines.BOTTOM_TYPE_C3:
            case TacticalLines.TGMF:
                result = true;
            default:
                break;
        }
        return result;
    }

    /**
     * Closes the polygon for areas
     * @param Pixels the client points
     */
    public static void ClosePolygon(ArrayList<POINT2> Pixels) {
        try {
            POINT2 pt0 = Pixels.get(0);
            POINT2 pt1 = Pixels.get(Pixels.size() - 1);
            if (pt0.x != pt1.x || pt0.y != pt1.y) {
                Pixels.add(new POINT2(pt0.x, pt0.y));
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsRenderer2.ClosePolygon");
               ErrorLogger.LogException(_className ,"ClosePolygon",
                    new RendererException("Failed inside ClosePolygon", exc));
        }
    }
    /**
     * for change 1 symbol the W/w1 modifiers run too close to the symbol outline
     * so it shifts the line along the line away from the edge
     * @param pt0
     * @param pt1
     */
    protected static void shiftModifiersLeft(POINT2 p1, POINT2 p2, double shift)
    {
        try
        {
            POINT2 pt1=new POINT2(p1);
            POINT2 pt2=new POINT2(p2);
            double dist=lineutility.CalcDistanceDouble(pt1, pt2);
            if(pt1.x<pt2.x || (pt1.x==pt2.x && pt1.y<pt2.y))
            {
                pt1=lineutility.ExtendAlongLineDouble(pt2, pt1, dist+shift);
                pt2=lineutility.ExtendAlongLineDouble(pt1, pt2, dist-shift);
            }
            else
            {
                pt1=lineutility.ExtendAlongLineDouble(pt2, pt1, dist-shift);
                pt2=lineutility.ExtendAlongLineDouble(pt1, pt2, dist+shift);
            }
            p1.x=pt1.x;
            p1.y=pt1.y;
            p2.x=pt2.x;
            p2.y=pt2.y;
        }
        catch (Exception exc) {
               ErrorLogger.LogException(_className ,"shiftModifiersLeft",
                    new RendererException("Failed inside shiftModifiersLeft", exc));
        }
        return;
    }
    /**
     * Overrides shape properties for symbols based on Mil-Std-2525
     * @param tg
     * @param shape
     */
    protected static void ResolveModifierShape(TGLight tg, Shape2 shape) {
        try {
            //shape style was set by CELineArray and takes precedence
            //whenever it is set
            int shapeStyle = shape.get_Style();
            int lineStyle = tg.get_LineStyle();
            int lineType = tg.get_LineType();
            boolean hasFill=LinesWithFill(lineType);
            int bolMETOC=clsMETOC.IsWeather(tg.get_SymbolId());
            if(bolMETOC>0)
                return;
            int fillStyle=0;
            //for some of these the style must be dashed
            //clsUtility.WriteFile("ResolveModifierShape before switch " + Integer.toString(lineType));
            switch (tg.get_LineType()) {
                case TacticalLines.FEBA:
                    shape.setFillColor(null);
                    shape.set_Style(tg.get_LineStyle());
                    shape.setLineColor(tg.get_LineColor());                    
                    break;
                case TacticalLines.NFA:
                case TacticalLines.NFA_CIRCULAR:
                case TacticalLines.NFA_RECTANGULAR:
                case TacticalLines.KILLBOXBLUE:
                case TacticalLines.KILLBOXPURPLE:
                case TacticalLines.KILLBOXBLUE_RECTANGULAR:
                case TacticalLines.KILLBOXPURPLE_RECTANGULAR:
                case TacticalLines.KILLBOXBLUE_CIRCULAR:
                case TacticalLines.KILLBOXPURPLE_CIRCULAR:
                case TacticalLines.BIO:
                case TacticalLines.CHEM:
                case TacticalLines.RAD:
                case TacticalLines.WFZ:
                //case TacticalLines.OBSAREA:
                    fillStyle=3;
                    if(tg.get_UseHatchFill())
                        fillStyle=0;
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.set_Style(tg.get_LineStyle());
                        shape.setLineColor(tg.get_LineColor());
                        shape.set_Fillstyle(fillStyle /*GraphicProperties.FILL_TYPE_RIGHT_SLANTS*/);   //waa 3
                        shape.setFillColor(tg.get_FillColor());
                    }
                    break;
                case TacticalLines.OBSAREA:
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.set_Style(tg.get_LineStyle());
                        shape.setLineColor(tg.get_LineColor());
                        shape.set_Fillstyle(0 /*GraphicProperties.FILL_TYPE_RIGHT_SLANTS*/);
                        shape.setFillColor(tg.get_FillColor());
                    }
                    break;
                case TacticalLines.LAA:
                    fillStyle=2;
                    if(tg.get_UseHatchFill())
                        fillStyle=0;
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.set_Style(tg.get_LineStyle());
                        shape.setLineColor(tg.get_LineColor());
                        shape.set_Fillstyle(fillStyle /*GraphicProperties.FILL_TYPE_LEFT_SLANTS*/);//was 2
                        shape.setFillColor(tg.get_FillColor());
                    }
                    break;
                case TacticalLines.OVERHEAD_WIRE_LS:
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_FILL) {
                        shape.set_Fillstyle(1 /*GraphicProperties.FILL_TYPE_SOLID*/);
                        shape.setFillColor(tg.get_LineColor());
                    }
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.set_Style(1);
                        shape.setLineColor(tg.get_LineColor());
                    }
                    break;
                case TacticalLines.DIRATKAIR:
                case TacticalLines.ATDITCHC:
                case TacticalLines.ATDITCHM:
                case TacticalLines.SARA:
                case TacticalLines.FOLSP:
                case TacticalLines.FERRY:
                case TacticalLines.MNFLDFIX:
                case TacticalLines.TURN:
                case TacticalLines.MNFLDDIS:
                case TacticalLines.EASY:
                case TacticalLines.BYDIF:
                case TacticalLines.BYIMP:
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_FILL) {
                        shape.set_Fillstyle(1 /*GraphicProperties.FILL_TYPE_SOLID*/);
                        shape.setFillColor(tg.get_LineColor());
                    }
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.set_Style(tg.get_LineStyle());
                        shape.setLineColor(tg.get_LineColor());
                    }
                    break;
                case TacticalLines.DECEIVE: //any shape for these symbols is dashed
                case TacticalLines.CLUSTER:
                case TacticalLines.CATK:
                case TacticalLines.CATKBYFIRE:
                case TacticalLines.PLD:
                case TacticalLines.PLANNED:
                case TacticalLines.CFL:
                case TacticalLines.FORDSITE:
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.set_Style(1 /*GraphicProperties.LINE_TYPE_DASHED*/);
                        shape.setLineColor(tg.get_LineColor());
                    }
                    break;
                case TacticalLines.PNO: //always dashed
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.set_Style(1 /*GraphicProperties.LINE_TYPE_DASHED*/);
                        shape.setLineColor(tg.get_LineColor());
                        shape.setFillColor(tg.get_FillColor());
                        shape.set_Fillstyle(tg.get_FillStyle());
                    }
                    break;
                case TacticalLines.DMA: //these symbols are partially dashed
                case TacticalLines.DMAF:
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.setLineColor(tg.get_LineColor());
                        if (shapeStyle != lineStyle) {
                            if (shapeStyle != 1 /*GraphicProperties.LINE_TYPE_DASHED*/) {
                                shape.set_Style(lineStyle);
                            }
                        }
                    }
                    break;
                case TacticalLines.DUMMY:
                case TacticalLines.DIRATKFNT:
                case TacticalLines.FOLLA:
                case TacticalLines.ESR1:
                case TacticalLines.FORDIF:
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.setLineColor(tg.get_LineColor());
                        if (shapeStyle != lineStyle) {
                            if (shapeStyle != 1 /*GraphicProperties.LINE_TYPE_DASHED*/) {
                                shape.set_Style(lineStyle);
                            }
                        }
                    }
                    break;
                case TacticalLines.AAFNT:
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        shape.setLineColor(tg.get_LineColor());
                        if (shapeStyle != lineStyle) {
                            if (shapeStyle != 2 /*GraphicProperties.LINE_TYPE_DOTTED*/) {
                                shape.set_Style(lineStyle);
                            }
                        }
                    }
                    break;
                default:
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_FILL) {
                        shape.set_Fillstyle(tg.get_FillStyle());
                        shape.setFillColor(tg.get_FillColor());
                    }
                    if (shape.getShapeType() == Shape2.SHAPE_TYPE_POLYLINE) {
                        if (lineType != TacticalLines.LC)//shape linecolor set by Channels class for LC
                        {
                            shape.setLineColor(tg.get_LineColor());
                        }
                        shape.set_Style(lineStyle);
                        if (hasFill || clsUtility.isClosedPolygon(lineType) || clsUtility.IsChange1Area(lineType, null))
                        {
//                            if (lineType != TacticalLines.RANGE_FAN && lineType != TacticalLines.RANGE_FAN_SECTOR)
//                            {
//                                shape.set_Fillstyle(tg.get_FillStyle());
//                                shape.setFillColor(tg.get_FillColor());
//                            }
                            switch(lineType)
                            {
                                case TacticalLines.RANGE_FAN:
                                case TacticalLines.RANGE_FAN_SECTOR:
                                case TacticalLines.BBS_AREA:
                                case TacticalLines.BBS_RECTANGLE:
                                case TacticalLines.PBS_RECTANGLE:
                                case TacticalLines.PBS_SQUARE:
                                case TacticalLines.PBS_CIRCLE:
                                case TacticalLines.PBS_ELLIPSE:
                                    shape.setFillColor(null);
                                    break;
                                default:
                                    shape.set_Fillstyle(tg.get_FillStyle());
                                    shape.setFillColor(tg.get_FillColor());
                                    break;
                            }
                        }
                        switch(lineType)
                        {
                            case TacticalLines.BS_ELLIPSE:
                            //case TacticalLines.PBS_ELLIPSE:
                            //case TacticalLines.PBS_CIRCLE:
                            case TacticalLines.BS_RECTANGLE:
                            //case TacticalLines.BBS_RECTANGLE:
                                shape.set_Fillstyle(tg.get_FillStyle());
                                shape.setFillColor(tg.get_FillColor());
                                break;
                            case TacticalLines.BBS_RECTANGLE:
                            case TacticalLines.BBS_AREA:
                            case TacticalLines.BBS_LINE:
                            case TacticalLines.BBS_POINT:
                            case TacticalLines.PBS_RECTANGLE:
                            case TacticalLines.PBS_SQUARE:
                            case TacticalLines.PBS_ELLIPSE:
                            case TacticalLines.PBS_CIRCLE:
                                shape.setFillColor(null);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
            }

        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsRenderer2.ResolveModifierShape");
               ErrorLogger.LogException(_className ,"ResolveModifierShape",
                    new RendererException("Failed inside ResolveModifierShape", exc));
        }
        return;
    }
    public static Color GetOpaqueColor(Color color)
    {
        int r=color.getRed();
        int g=color.getGreen();
        int b=color.getBlue();
        return new Color(r,g,b);
    }
    /**
     * These lines allow fill
     * @param linetype
     * @return
     */
    public static boolean LinesWithFill(int linetype)
    {
        boolean result=false;
        try
        {
            switch(linetype)
            {
                case TacticalLines.BS_LINE:
                case TacticalLines.PAA_RECTANGULAR:
                case TacticalLines.CFL:
                case TacticalLines.DIRATKFNT:
                case TacticalLines.DIRATKAIR:
                case TacticalLines.BOUNDARY:
                case TacticalLines.ISOLATE:
                case TacticalLines.CORDONKNOCK:
                case TacticalLines.CORDONSEARCH:
                case TacticalLines.OCCUPY:
                case TacticalLines.RETAIN:
                case TacticalLines.SECURE:
                case TacticalLines.FLOT:
                case TacticalLines.LC:
                case TacticalLines.PL:
                case TacticalLines.LL:
//                case TacticalLines.AC:
//                case TacticalLines.SAAFR:
                case TacticalLines.FEBA:
                case TacticalLines.DIRATKGND:
                case TacticalLines.DIRATKSPT:
                case TacticalLines.FCL:
                case TacticalLines.LOA:
                case TacticalLines.LOD:
                case TacticalLines.LDLC:
                case TacticalLines.RELEASE:
                case TacticalLines.LINE:
                case TacticalLines.ABATIS:
                case TacticalLines.ATDITCH:
                case TacticalLines.ATWALL:
                case TacticalLines.SFENCE:
                case TacticalLines.DFENCE:
                case TacticalLines.UNSP:
                case TacticalLines.PLD:
                case TacticalLines.DOUBLEA:
                case TacticalLines.LWFENCE:
                case TacticalLines.HWFENCE:
                //case TacticalLines.BBS_LINE:
                case TacticalLines.SINGLEC:
                case TacticalLines.DOUBLEC:
                case TacticalLines.TRIPLE:
                case TacticalLines.FORTL:
                case TacticalLines.LINTGT:
                case TacticalLines.LINTGTS:
                case TacticalLines.FSCL:
                case TacticalLines.NFL:
                case TacticalLines.MFP:
                case TacticalLines.RFL:
                case TacticalLines.CONVOY:
                case TacticalLines.HCONVOY:
                case TacticalLines.MSR:
                case TacticalLines.ASR:
                case TacticalLines.ONEWAY:
                case TacticalLines.TWOWAY:
                case TacticalLines.ALT:
                    result = true;
                    break;
                default:
                    result = false;
                    break;
            }
        }
        catch(Exception exc)
        {
               ErrorLogger.LogException(_className ,"LinesWithFill",
                    new RendererException("Failed inside LinesWithFill", exc));
        }
        return result;
    }
    /**
     * @deprecated
     * if the line color and fill color are the same or very close then we want to
     * tweak the fill color a bit to make the line appear distinct from the fill.
     * @param tg
     */
    public static void tweakFillColor(TGLight tg)
    {
        try
        {
            if(isSameColor(tg.get_LineColor(),tg.get_FillColor())==false)
                return;

            Color fillColor=tg.get_FillColor();
            int r=fillColor.getRed(),g=fillColor.getGreen(),b=fillColor.getBlue();
            int alpha=fillColor.getAlpha();

            r*=0.9;
            g*=0.9;
            b*=0.9;
            alpha*=0.8;

            fillColor=new Color(r,g,b,alpha);
            tg.set_FillColor(fillColor);
        }
        catch(Exception exc)
        {
               ErrorLogger.LogException(_className ,"tweakFillColor",
                    new RendererException("Failed inside tweakFillColor", exc));
        }
        return;
    }
    /**
     * @deprecated
     * Test to see if two colors are similar
     * @param c1
     * @param c2
     * @return true is same (or similar) color
     */
    public static Boolean isSameColor(Color c1, Color c2)
    {
        try
        {
            if(c1==null || c2==null)
                return true;

            int r1=c1.getRed(),r2=c2.getRed(),g1=c1.getGreen(),g2=c2.getGreen(),
                    b1=c1.getBlue(),b2=c2.getBlue();

            if(Math.abs(r1-r2)<5)
                if(Math.abs(g1-g2)<5)
                    if(Math.abs(b1-b2)<5)
                        return true;
        }
        catch(Exception exc)
        {
               ErrorLogger.LogException(_className ,"isSameColor",
                    new RendererException("Failed inside isSameColor", exc));
        }
        return false;
    }
    /**
     * Customer requested routine for setting the stroke dash pattern
     * @param width
     * @param style
     * @param cap
     * @param round
     * @return
     */
    public static BasicStroke getLineStroke(float width, int style, int cap, int round)
    {
        // NOTE: We restrict ourselves to using a base dash array of
        // approximately length 8 (scaled by width). CPOF's 3D map usesOpenGL
        // stippling and this works with bit patterns of exactly 16bits.
        // We could increase our base dash array to length 16 if itwere necessary
        // in order to support a new stipple type.

        // Some segments are of length 0.1 because the Java2D rendereradds line caps of
        // width/2 size to both ends of the segment when "round" is oneof BasicStroke.CAP_ROUND
        // or BasicStroke.CAP_SQUARE. This value is small enough not to affect the
        // stipple bit pattern calculation for the 3d map and stilllook good on the
        // 2d map.

        // NOTE: The dash arrays below do not supportBasisStroke.CAP_BUTT line capping,
        // although it would be relatively simple to change them suchthat they would.
        BasicStroke stroke=null;
        try
        {
            switch (style)
            {
                case 0://GraphicProperties.LINE_TYPE_SOLID:
                    stroke = new BasicStroke(width, cap, round);
                    break;
                case 1://GraphicProperties.LINE_TYPE_DASHED:
                    float[] dash = {2*width, 2*width, 2*width, 2*width};
                    stroke = new BasicStroke(width, cap, round, 4f, dash, 0f);
                    break;
                case 2://GraphicProperties.LINE_TYPE_DOTTED:
                    float[] dot = {0.1f*width, 2f*width, 0.1f*width,2f*width, 0.1f*width, 2f*width, 0.1f*width, 2f*width};
                    stroke = new BasicStroke(width, cap, round, 4f, dot, 0f);
                    break;
                case 3://GraphicProperties.LINE_TYPE_DASHDOT:
                    float[] dashdot = {4f*width, 2f*width, 0.1f*width,2f*width};
                    stroke = new BasicStroke(width, cap, round, 4f, dashdot,0f );
                    break;
                case 4://GraphicProperties.LINE_TYPE_DASHDOTDOT:
                    float[] dashdotdot = {2f*width, 2f*width, 0.1f*width,2f*width, 0.1f*width, 2f*width};
                    stroke = new BasicStroke(width, cap, round, 4f,dashdotdot, 0f );
                    break;
                default:
                    stroke = new BasicStroke(width, cap, round);
                    break;
            }
        }
        catch(Exception exc)
        {
               ErrorLogger.LogException(_className ,"getLineStroke",
                    new RendererException("Failed inside getLineStroke", exc));
        }
        return stroke;
    }
    /**
     * GE clients
     * @param width
     * @param style
     * @param cap
     * @param round
     * @return 
     */
    public static BasicStroke getLineStroke2(float width, int style, int cap, int round)
    {
        // NOTE: We restrict ourselves to using a base dash array of
        // approximately length 8 (scaled by width). CPOF's 3D map usesOpenGL
        // stippling and this works with bit patterns of exactly 16bits.
        // We could increase our base dash array to length 16 if itwere necessary
        // in order to support a new stipple type.

        // Some segments are of length 0.1 because the Java2D rendereradds line caps of
        // width/2 size to both ends of the segment when "round" is oneof BasicStroke.CAP_ROUND
        // or BasicStroke.CAP_SQUARE. This value is small enough not to affect the
        // stipple bit pattern calculation for the 3d map and stilllook good on the
        // 2d map.

        // NOTE: The dash arrays below do not supportBasisStroke.CAP_BUTT line capping,
        // although it would be relatively simple to change them suchthat they would.
        BasicStroke stroke=null;
        try
        {
            switch (style)
            {
                case 0://GraphicProperties.LINE_TYPE_SOLID:
                    stroke = new BasicStroke(width, cap, round);
                    break;
                case 1://GraphicProperties.LINE_TYPE_DASHED:
                    //float[] dash = {2*width, 2*width, 2*width, 2*width};
                    float[] dash = {2*width, 2*width};
                    stroke = new BasicStroke(width, cap, round, 4f, dash, 0f);
                    break;
                case 2://GraphicProperties.LINE_TYPE_DOTTED:
                    //float[] dot = {0.1f*width, 2f*width, 0.1f*width,2f*width, 0.1f*width, 2f*width, 0.1f*width, 2f*width};
                    float[] dot = {0.1f*width, 2f*width};
                    stroke = new BasicStroke(width, cap, round, 4f, dot, 0f);
                    break;
                case 3://GraphicProperties.LINE_TYPE_DASHDOT:
                    float[] dashdot = {4f*width, 2f*width, 0.1f*width,2f*width};
                    stroke = new BasicStroke(width, cap, round, 4f, dashdot,0f );
                    break;
                case 4://GraphicProperties.LINE_TYPE_DASHDOTDOT:
                    float[] dashdotdot = {2f*width, 2f*width, 0.1f*width,2f*width, 0.1f*width, 2f*width};
                    stroke = new BasicStroke(width, cap, round, 4f,dashdotdot, 0f );
                    break;
                default:
                    stroke = new BasicStroke(width, cap, round);
                    break;
            }
        }
        catch(Exception exc)
        {
               ErrorLogger.LogException(_className ,"getLineStroke",
                    new RendererException("Failed inside getLineStroke", exc));
        }
        return stroke;
    }
    /**
     * Sets shape properties based on other properties which were set by JavaLineArray
     * @param tg tactical graphic
     * @param shapes the ShapeInfo array
     * @param bi BufferedImage to use for setting shape TexturePaint
     */
    public static void SetShapeProperties(TGLight tg, ArrayList<Shape2> shapes,
            BufferedImage bi) {
        try
        {
            if (shapes == null)
            {
                return;
            }
            
            int j = 0;
            Shape2 shape = null;
            BasicStroke stroke = null;
            //float[] dash = null;
            int lineThickness = tg.get_LineThickness();
            int shapeType = -1;
            int lineType = tg.get_LineType();
            boolean hasFill=LinesWithFill(lineType);
            boolean isChange1Area = clsUtility.IsChange1Area(lineType, null);
            boolean isClosedPolygon = clsUtility.isClosedPolygon(lineType);
            if(tg.get_FillColor()==null)
            {
                switch(tg.get_LineType())
                {
                    case TacticalLines.AC:
                    case TacticalLines.SAAFR:
                    case TacticalLines.MRR:
                    case TacticalLines.MRR_USAS:
                    case TacticalLines.UAV:
                    case TacticalLines.UAV_USAS:
                    case TacticalLines.LLTR:
                        shape=shapes.get(shapes.size()-1);
                        shapes.clear();
                        shapes.add(shape);
                        break;
                    case TacticalLines.CATK:
                    case TacticalLines.AXAD:
                    case TacticalLines.AIRAOA:
                    case TacticalLines.AAAAA:
                    case TacticalLines.SPT:
                    case TacticalLines.AAFNT:		//40
                    case TacticalLines.MAIN:
                    case TacticalLines.CATKBYFIRE:	//80                        
                        ArrayList<Shape2> tempShapes=new ArrayList();
                        for(j=0;j<shapes.size();j++)
                        {
                            shape=shapes.get(j);
                            if(shape.getShapeType() != ShapeInfo.SHAPE_TYPE_FILL)
                                tempShapes.add(shape);
                        }
                        shapes=tempShapes;
                        break;
                    default:
                        break;
                }
            }
            for (j = 0; j < shapes.size(); j++) {
                shape = shapes.get(j);
                if (shape == null || shape.getShape() == null) {
                    continue;
                }

                if (shape.getShapeType() == Shape2.SHAPE_TYPE_FILL) 
                {
                    switch(tg.get_LineType())
                    {
                        case TacticalLines.DEPTH_AREA:
                            break;
                        default:
                            shape.setFillColor(tg.get_FillColor());
                            break;
                    }
                }

                //if(lineType != TacticalLines.LEADING_LINE)
                ResolveModifierShape(tg, shape);
                if(lineType==TacticalLines.AIRFIELD)
                    if(j==1)
                        shape.setFillColor(null);
                //diagnostic
                if(lineType==TacticalLines.BBS_POINT)
                    if(j==0)
                        shape.setLineColor(null);
                //end section
                
                shapeType = shape.getShapeType();

                if (lineType == TacticalLines.LC) {
                    SetLCColor(tg, shape);
                }

                Rectangle2D.Double rect = null;
                Graphics2D grid = null;
                TexturePaint tp = tg.get_TexturePaint();

                if (hasFill || isClosedPolygon || isChange1Area || shapeType == Shape2.SHAPE_TYPE_FILL)
                {                    
                    switch (shape.get_FillStyle()) {
                        case 3://GraphicProperties.FILL_TYPE_RIGHT_SLANTS:
                            rect = new Rectangle2D.Double(0, 0, 8, 8);
                            //rect = new Rectangle2D.Double(0, 0, 100, 100);
                            grid = bi.createGraphics();
                            //grid.setColor(GetOpaqueColor(shape.get_LineColor()));
                            grid.setColor(GetOpaqueColor(tg.get_LineColor()));

                            grid.setStroke(new BasicStroke(2));
                            grid.drawLine(0, 8, 8, 0);
                            //grid.drawLine(0, 0, 100, 100);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(tg.get_FillColor());
                            grid.dispose();
                            //clsUtility.WriteFile("fillstyle = " + Integer.toString(shape.get_FillStyle()));
                            break;
                        case 2://GraphicProperties.FILL_TYPE_LEFT_SLANTS:
                            rect = new Rectangle2D.Double(0, 0, 8, 8);
                            //rect = new Rectangle2D.Double(0, 0, 20, 20);
                            grid = bi.createGraphics();
                            //grid.setColor(GetOpaqueColor(tg.get_LineColor()));
                            grid.setColor(GetOpaqueColor(tg.get_LineColor()));

                            grid.setStroke(new BasicStroke(2));
                            grid.drawLine(0, 0, 8, 8);
                            //grid.drawLine(0, 0, 20, 20);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(tg.get_FillColor());
                            grid.dispose();
                            //clsUtility.WriteFile("set TexturePaint");
                            break;
                        case 6://GraphicProperties.FILL_TYPE_DOTS:
                            rect = new Rectangle2D.Double(3, 3, 8, 8);
                            grid = bi.createGraphics();
                            grid.setColor(tg.get_FillColor());
                            grid.drawLine(3, 3, 5, 3);
                            grid.drawLine(5, 3, 5, 5);
                            grid.drawLine(5, 5, 3, 5);
                            grid.drawLine(3, 5, 5, 3);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(null);
                            grid.dispose();
                            break;
                        case 4://GraphicProperties.FILL_TYPE_VERTICAL_LINES:
                            rect = new Rectangle2D.Double(0, 0, 8, 8);
                            //rect = new Rectangle2D.Double(0, 0, 20, 20);
                            grid = bi.createGraphics();
                            grid.setColor(GetOpaqueColor(tg.get_LineColor()));
                            grid.setStroke(new BasicStroke(2));
                            grid.drawLine(4, 0, 4, 8);
                            //grid.drawLine(10, 0, 10, 20);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(tg.get_FillColor());
                            grid.dispose();
                            break;
                        case 5://GraphicProperties.FILL_TYPE_HORIZONTAL_LINES:
                            rect = new Rectangle2D.Double(0, 0, 8, 8);
                            //rect = new Rectangle2D.Double(0, 0, 20, 20);
                            grid = bi.createGraphics();
                            grid.setColor(GetOpaqueColor(tg.get_LineColor()));
                            grid.setStroke(new BasicStroke(2));
                            grid.drawLine(0, 4, 8, 4);
                            //grid.drawLine(0, 10, 20, 10);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(tg.get_FillColor());
                            grid.dispose();
                            break;
                        case 7://GraphicProperties.FILL_TYPE_HORIZONTAL_PLUSSES:
                            rect = new Rectangle2D.Double(0, 0, 8, 8);
                            //rect = new Rectangle2D.Double(0, 0, 20, 20);
                            grid = bi.createGraphics();
                            grid.setColor(GetOpaqueColor(tg.get_LineColor()));
                            grid.setStroke(new BasicStroke(1));
                            grid.drawLine(4, 2, 4, 6);
                            grid.drawLine(2, 4, 6, 4);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(tg.get_FillColor());
                            grid.dispose();
                            break;
                        case 8://GraphicProperties.FILL_TYPE_XHATCH:
                            rect = new Rectangle2D.Double(0, 0, 10, 10);
                            //rect = new Rectangle2D.Double(0, 0, 20, 20);
                            grid = bi.createGraphics();
                            grid.setColor(GetOpaqueColor(tg.get_LineColor()));
                            grid.setStroke(new BasicStroke(1));
                            grid.drawLine(2, 2, 8, 8);                            
                            grid.drawLine(2, 8, 8, 2);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(tg.get_FillColor());
                            grid.dispose();
                            break;
                        case 1://GraphicProperties.FILL_TYPE_SOLID:
                            //shape.set_FillColor(tg.get_FillColor());
                            break;
                        default:
                            break;
                    }
                }
                
                if(lineThickness==0)
                    lineThickness=1;
                //shape style might have been set by CELineArray, e.g. dotted arrow for AAFNT
                //therefore shape style takes precedent over tg linstyle
                //could have a function call here that sets the linestyles based on priority
//                switch (shape.get_Style())
//                {
//                    case 0://GraphicProperties.LINE_TYPE_SOLID:
//                        dash = new float[1];
//                        dash[0] = 1;
//                        break;
//                    case 1://GraphicProperties.LINE_TYPE_DASHED:
//                        dash = new float[2];
//                        dash[0] = 2.5f*lineThickness;
//                        dash[1] = 2.5f*lineThickness;
//                        break;
//                    case 3://GraphicProperties.LINE_TYPE_DASHDOT:
//                        dash = new float[4];
//                        dash[0] = 2.5f*lineThickness;//dash
//                        dash[1] = 2.5f*lineThickness;//space
//                        dash[2] = 1.5f;//dot
//                        dash[3] = 2.5f*lineThickness;//space
//                        break;
//                    case 4://GraphicProperties.LINE_TYPE_DASHDOTDOT:
//                        dash = new float[6];
//                        dash[0] = 2.5f*lineThickness;//dash
//                        dash[1] = 2.5f*lineThickness;//space
//                        dash[2] = 1.f;//dot
//                        dash[3] = 2.5f*lineThickness;//space
//                        dash[4] = 1.0f*lineThickness;//dot
//                        dash[5] = 2.5f*lineThickness;//space
//                        break;
//                    case 2://GraphicProperties.LINE_TYPE_DOTTED:
//                        dash = new float[2];
//                        dash[0] = 1.5f; //dot
//                        dash[1] = 2f*lineThickness; //space
//                        break;
//                    default:
//                        break;
//                }
                //set the shape with the default properties
                //the switch statement below will override specific properties as needed
                //stroke = new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, dash, 0);
                //replaced above dash pattern with getLineStroke function requested by the client   9/9/11
//                BasicStroke saveStroke=(BasicStroke)shape.getStroke();
                stroke=getLineStroke(lineThickness,shape.get_Style(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
                if(tg.get_Client().equalsIgnoreCase("ge"))
                {
                    if(tg.get_LineType() == TacticalLines.AAFNT && shape.get_Style()==2)
                        shape.set_Style(1);

                    stroke=getLineStroke2(lineThickness,shape.get_Style(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
                }                
                //stroke special cases
//                switch(tg.get_LineType())
//                {
//                    case TacticalLines.SARA:
//                        if(shape.get_Style()!=1)//dashed line
//                            stroke = new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
//                        else
//                            stroke = new BasicStroke(lineThickness,shape.get_Style(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
//                        break;
//                    default:
//                        break;
//                }
                if(shape.getShapeType()==Shape2.SHAPE_TYPE_FILL)
                {
                    stroke = new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
                    //shape.setStroke(new BasicStroke(0));
                }
                shape.setStroke(stroke);
//                switch(lineType)
//                {
//                    case TacticalLines.BBS_LINE:
//                        shape.setStroke(saveStroke);
//                        if(j==shapes.size()-1)//2nd shape is the interior of the thick line
//                            shape.setLineColor(tg.get_FillColor());
//                        break;
//                    default:
//                        break;
//                }
//                if(tg.get_LineType()==TacticalLines.DIRATKGND && j==1)
//                {
//                    if(lineThickness>=2)
//                    {
//                        stroke = new BasicStroke((float)lineThickness/1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, dash, 0);
//                        shape.setStroke(stroke);
//                    }
//                }
            }
        }
        catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsRenderer2.SetShapeProperties");
               ErrorLogger.LogException(_className ,"SetShapeProperties",
                    new RendererException("Failed inside SetShapeProperties", exc));
        }
        return;
    }
    /**
     * Returns a boolean indicating whether the line type is a change 1 area
     * @param lineType the line type
     * @param minPoints Out - object to hold the minimum number of client points required
     * @return true if change 1 area
     */
    public static boolean IsChange1Area(int lineType,
            ref<int[]> minPoints) {
        try {
            if (minPoints != null) {
                minPoints.value = new int[1];
            }
            switch (lineType) {
                case TacticalLines.LAUNCH_AREA:
                    if (minPoints != null) {
                        minPoints.value[0] = 1;
                    }
                    return true;
                case TacticalLines.FEBA:
                    if (minPoints != null) {
                        minPoints.value[0] = 1;
                    }
                    return false;
                case TacticalLines.RECTANGULAR:
                case TacticalLines.PBS_RECTANGLE:
                case TacticalLines.PBS_SQUARE:
                case TacticalLines.CIRCULAR:
                case TacticalLines.BBS_POINT:
                case TacticalLines.FSA_CIRCULAR:
                case TacticalLines.FFA_CIRCULAR:
                case TacticalLines.NFA_CIRCULAR:
                case TacticalLines.RFA_CIRCULAR:
                case TacticalLines.ACA_CIRCULAR:
                case TacticalLines.PAA_CIRCULAR:
                case TacticalLines.ATI_CIRCULAR:
                case TacticalLines.CFFZ_CIRCULAR:
                case TacticalLines.SENSOR_CIRCULAR:
                case TacticalLines.CENSOR_CIRCULAR:
                case TacticalLines.DA_CIRCULAR:
                case TacticalLines.CFZ_CIRCULAR:
                case TacticalLines.ZOR_CIRCULAR:
                case TacticalLines.TBA_CIRCULAR:
                case TacticalLines.TVAR_CIRCULAR:
                case TacticalLines.KILLBOXBLUE_CIRCULAR:
                case TacticalLines.KILLBOXPURPLE_CIRCULAR:
                case TacticalLines.RANGE_FAN:
                case TacticalLines.RANGE_FAN_FILL:
                case TacticalLines.RANGE_FAN_SECTOR:
                    if (minPoints != null) {
                        minPoints.value[0] = 1;
                    }
                    return true;
                case TacticalLines.PAA_RECTANGULAR_REVC:
                case TacticalLines.FSA_RECTANGULAR:
                case TacticalLines.FFA_RECTANGULAR:
                case TacticalLines.RFA_RECTANGULAR:
                case TacticalLines.NFA_RECTANGULAR:
                case TacticalLines.ACA_RECTANGULAR:
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
                    if (minPoints != null) {
                        minPoints.value[0] = 1;
                    }
                    return true;
                default:
                    return false;
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.IsChange1Area");
               ErrorLogger.LogException(_className ,"IsChange1Area",
                    new RendererException("Failed inside IsChange1Area", exc));
        }
        return false;
    }

    public static void WriteFile(String str) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Test.txt"));
            bufferedWriter.write(str);
            //bufferedWriter.newLine();
            //bufferedWriter.write(pointType);
            bufferedWriter.close();
            bufferedWriter = null;
        } 
        catch (Exception exc) {
               ErrorLogger.LogException(_className ,"WriteFile",
                    new RendererException("Failed inside WriteFile", exc));
        }
    }

    /**
     * Calculates point where two lines intersect.
     * First line defined by pt1, m1.
     * Second line defined by pt2, m2.
     * result will be written to ptIntersect.
     * @param pt1 first line point
     * @param m1 slope of first line
     * @param pt2 second line point
     * @param m2 slope of second line
     * @param ptIntersect OUT - intersection point
     */
    protected static void CalcIntersectPt(POINT2 pt1,
            double m1,
            POINT2 pt2,
            double m2,
            POINT2 ptIntersect) {
        try {
            if (m1 == m2) {
                return;
            }

            double x1 = pt1.x;
            double y1 = pt1.y;
            double x2 = pt2.x;
            double y2 = pt2.y;
            //formula for the intersection of two lines
            double dx2 = (double) ((y1 - y2 + m1 * x2 - m1 * x1) / (m2 - m1));
            double x3 = x2 + dx2;
            double y3 = (double) (y2 + m2 * dx2);

            ptIntersect.x = x3;
            ptIntersect.y = y3;
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.CalcIntersectPt");
            ErrorLogger.LogException(_className, "CalcIntersectPt",
                    new RendererException("Failed inside CalcIntersectPt", exc));
        }
        return;
    }

    /**
     * Calculates the channel width in pixels for channel types
     * @param pixels the client points as 2-tuples x,y in pixels
     * @param distanceToChannelPOINT2 OUT - the calculated distance in pixels from the tip of the
     * arrowhead to the back of the arrowhead.
     * @return the channel width in pixels
     */
    protected static int ChannelWidth(double[] pixels,
            ref<double[]> distanceToChannelPOINT2) {
        int width = 0;
        try {
            int numPOINT2s = pixels.length / 2;
            if (numPOINT2s < 3) {
                return 0;
            }

            POINT2 channelWidthPOINT2 = new POINT2(0, 0);
            POINT2 lastSegmentPt1 = new POINT2(0, 0);
            POINT2 lastSegmentPt2 = new POINT2(0, 0);

            lastSegmentPt1.x = (double) pixels[2 * numPOINT2s - 6];
            lastSegmentPt1.y = (double) pixels[2 * numPOINT2s - 5];
            lastSegmentPt2.x = (double) pixels[2 * numPOINT2s - 4];
            lastSegmentPt2.y = (double) pixels[2 * numPOINT2s - 3];
            channelWidthPOINT2.x = (double) pixels[2 * numPOINT2s - 2];
            channelWidthPOINT2.y = (double) pixels[2 * numPOINT2s - 1];

            ref<double[]> m = new ref();
            double m1 = 0;
            //m1.value=new double[1];
            double distance = 0;
            POINT2 ptIntersect = new POINT2(0, 0);
            //boolean bolVertical = TrueSlope(lastSegmentPt1, lastSegmentPt2, ref m);
            boolean bolVertical = lineutility.CalcTrueSlopeDouble2(lastSegmentPt1, lastSegmentPt2, m);
            if (bolVertical == true && m.value[0] != 0) {
                m1 = -1 / m.value[0];
                CalcIntersectPt(channelWidthPOINT2, m1, lastSegmentPt2, m.value[0], ptIntersect);
                distance = lineutility.CalcDistanceDouble(channelWidthPOINT2, ptIntersect);
            }
            if (bolVertical == true && m.value[0] == 0) //horizontal segment
            {
                distance = Math.abs(channelWidthPOINT2.y - lastSegmentPt1.y);
            }
            if (bolVertical == false) //vertical segment
            {
                distance = Math.abs(channelWidthPOINT2.x - lastSegmentPt1.x);
                distanceToChannelPOINT2.value = new double[1];
                distanceToChannelPOINT2.value[0] = distance;
                return (int) distance * 4;
            }

            width = (int) distance * 8;
            if (width < 2) {
                width = 2;
            }

            double hypotenuse = lineutility.CalcDistanceDouble(lastSegmentPt2, channelWidthPOINT2);
            distanceToChannelPOINT2.value = new double[1];
            distanceToChannelPOINT2.value[0] = Math.sqrt(hypotenuse * hypotenuse - distance * distance);

        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.ChannelWidth");
            ErrorLogger.LogException(_className, "ChannelWidth",
                    new RendererException("Failed inside ChannelWidth", exc));
        }
        return width;
    }

    private static boolean InYOrder(POINT2 pt0,
            POINT2 pt1,
            POINT2 pt2) {
        try {
            if (pt0.y <= pt1.y && pt1.y <= pt2.y) {
                return true;
            }

            if (pt2.y <= pt1.y && pt1.y <= pt0.y) {
                return true;
            }

        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.InYOrder");
            ErrorLogger.LogException(_className, "InYOrder",
                    new RendererException("Failed inside InYOrder", exc));
        }
        return false;
    }
    /// <summary>
    /// tests if POINT2s have successively increasing or decreasing x values.
    /// </summary>
    /// <param name="pt0"></param>
    /// <param name="pt1"></param>
    /// <param name="pt2"></param>
    /// <returns>true if POINT2s are in X order</returns>

    private static boolean InXOrder(POINT2 pt0,
            POINT2 pt1,
            POINT2 pt2) {
        try {
            if (pt0.x <= pt1.x && pt1.x <= pt2.x) {
                return true;
            }

            if (pt2.x <= pt1.x && pt1.x <= pt0.x) {
                return true;
            }

        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.InXOrder");
            ErrorLogger.LogException(_className, "InXOrder",
                    new RendererException("Failed inside InXOrder", exc));
        }
        return false;
    }

    /**
     * For each sector calculates left azimuth, right azimuth, min radius, max radius
     * and stuff H2 with the string delimited result. The function is public, called by JavaRendererServer
     * @param tg tactical graphic
     */
    public static void GetSectorRadiiFromPoints(TGLight tg) {
        try {
            if(tg.get_LineType()==TacticalLines.RANGE_FAN_FILL)
                return;
            POINT2 ptCenter = tg.LatLongs.get(0);
            POINT2 ptLeftMin = new POINT2(), ptRightMax = new POINT2();
            int k = 0;
            String strLeft = "", strRight = "", strMin = "", strMax = "", temp = "";
            double nLeft = 0, nRight = 0, nMin = 0, nMax = 0;
            //if tg.PointCollection has more than one point
            //we use the points to calculate left,right,min,max
            //and then stuff tg.H2 with the comma delimited string
            double dist = 0;
            ref<double[]> a12 = new ref(), a21 = new ref();
            int numSectors = 0;
            if (tg.LatLongs.size() > 2) {
                numSectors = (tg.LatLongs.size() - 2) / 2;
                for (k = 0; k < numSectors; k++) {
                    //get the sector points
                    ptLeftMin = tg.LatLongs.get(2 * k + 2);
                    ptRightMax = tg.LatLongs.get(2 * k + 3);

                    dist = mdlGeodesic.geodesic_distance(ptCenter, ptLeftMin, a12, a21);
                    nLeft = a12.value[0];
                    strLeft = Double.toString(nLeft);

                    nMin = dist;
                    strMin = Double.toString(nMin);

                    dist = mdlGeodesic.geodesic_distance(ptCenter, ptRightMax, a12, a21);
                    nRight = a12.value[0];
                    strRight = Double.toString(nRight);

                    nMax = dist;
                    strMax = Double.toString(nMax);

                    if (k == 0) {
                        temp = strLeft + "," + strRight + "," + strMin + "," + strMax;
                    } else {
                        temp += "," + strLeft + "," + strRight + "," + strMin + "," + strMax;
                    }
                }
                if (!temp.equals("")) {
                    tg.set_H2(temp);
                }
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.GetSectorRadiiFromPoints");
            ErrorLogger.LogException(_className, "GetSectorRadiiFromPoints",
                    new RendererException("Failed inside GetSectorRadiiFromPoints", exc));
        }
    }

    /**
     * Note: Mil-Std-2525 Rev C does not use this function.
     * For Rev C the radii are calculated from tg.H2
     * Calculates the radii for circular and sector range fans.
     * @param tg
     * @param lineType
     * @return
     */
    public static double[] GetRadii(TGLight tg, 
            int lineType) {
        double[] radius = null;
        try {
            //if the client passed more than one point we use the
            //additional points to calculate the radii
            //ArrayList<Double> rads=new ArrayList();
            if (lineType == TacticalLines.RANGE_FAN &&
                    tg.LatLongs.size() > 2)
            {

                POINT2 ptCenter = tg.LatLongs.get(0);

                POINT2 pt = new POINT2();
                double dist = 0;
                ref<double[]> a12 = new ref(), a21 = new ref();
                int rad = 0;
                String temp = "";
                //we skip tg.PointCollection.get_Point(1) because
                //that's the orientation point
                for (int j = 2; j < tg.LatLongs.size(); j++)
                {
                    pt = new POINT2(tg.LatLongs.get(j));

                    dist = mdlGeodesic.geodesic_distance(ptCenter, pt, a12, a21);
                    rad = (int) dist;
                    //rads.add(dist);

                    if (rad <= 0) {
                        continue;
                    }
                    if (j == 2) {
                        temp = Double.toString(rad);
                    } else {
                        temp += ",";
                        temp += Double.toString(rad);
                    }
                }
                tg.set_H2(temp);
                //clsTGProperties.set_H2Field(temp);
            }
            String[] strRadius = null;
            String radii = tg.get_H2();
            //clsUtility.WriteFile(radii);

            if (lineType == TacticalLines.RANGE_FAN_SECTOR)
            {
                GetSectorRadiiFromPoints(tg);
                radii = tg.get_H2();
            }
            strRadius = radii.split(",");


            int n = strRadius.length;
            if (n > 0) {
                radius = new double[n];
            } else {
                return null;
            }

            if (lineType == TacticalLines.RANGE_FAN) {
                for (int k = 0; k < n; k++) {
                    radius[k] = Double.parseDouble(strRadius[k]);
                }
            } //there are twice as many strings in H1 as radii for scetor range fans
            else if (lineType == TacticalLines.RANGE_FAN_SECTOR) {
                int numSectors = n / 4;
                double[] radius2 = new double[numSectors * 2];
                int l = 0;
                for (int k = 0; k < numSectors; k++) {
                    radius2[l++] = Double.parseDouble(strRadius[4 * k + 2]);
                    radius2[l++] = Double.parseDouble(strRadius[4 * k + 3]);
                }
                radius = radius2;
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.GetRadii");
            ErrorLogger.LogException(_className, "GetRadii",
                    new RendererException("Failed inside GetRadii", exc));
        }
        return radius;
    }

    /**
     * Reverses the pixels except for the last point. This is used for
     * the axis of advance type routes. The pixels are 2-tuples x,y
     *
     * @param pixels OUT - Array of client points
     */
    protected static void ReorderPixels(double[] pixels) {
        try {
            double[] tempPixels;
            //reverse the pixels
            int j;
            double x;
            double y;
            int counter;
            int numPoints;
            counter = 0;
            numPoints = pixels.length / 2;
            tempPixels = new double[pixels.length];
            for (j = 0; j < numPoints - 1; j++) {
                x = pixels[pixels.length - 2 * j - 4];
                y = pixels[pixels.length - 2 * j - 3];
                tempPixels[counter] = x;
                tempPixels[counter + 1] = y;
                counter += 2;
            }
            //put the last pixel point into the last temppixels point
            int intPixelSize = pixels.length;
            tempPixels[counter] = pixels[intPixelSize - 2];
            tempPixels[counter + 1] = pixels[intPixelSize - 1];
            //stuff the pixels
            for (j = 0; j < pixels.length; j++) {
                pixels[j] = tempPixels[j];
            }
            tempPixels = null;
            return;
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.ReorderPixels");
            ErrorLogger.LogException(_className, "ReorderPixels",
                    new RendererException("Failed inside ReorderPixels", exc));
        }
    }
    /**
     * do not allow vertical segments for these, move the point x value by 1 pixel
     * @param tg tactical graphic
     */
    public static void FilterVerticalSegments(TGLight tg)
    {
        try
        {
            switch(tg.get_LineType())
            {
                case TacticalLines.AAFNT:
                case TacticalLines.MAIN:
                case TacticalLines.CATK:
                case TacticalLines.CATKBYFIRE:
                case TacticalLines.AXAD:
                case TacticalLines.AIRAOA:
                case TacticalLines.AAAAA:
                case TacticalLines.SPT:
                case TacticalLines.LC:
                case TacticalLines.LC2:
                case TacticalLines.UNSP:
                case TacticalLines.DFENCE:
                case TacticalLines.SFENCE:
                case TacticalLines.DOUBLEA:
                case TacticalLines.LWFENCE:
                case TacticalLines.HWFENCE:
                case TacticalLines.BBS_LINE:
                case TacticalLines.SINGLEC:
                case TacticalLines.SINGLEC2:
                case TacticalLines.DOUBLEC:
                case TacticalLines.DOUBLEC2:
                case TacticalLines.TRIPLE:
                case TacticalLines.TRIPLE2:
                case TacticalLines.BELT1:
                case TacticalLines.ALT:
                case TacticalLines.ONEWAY:
                case TacticalLines.TWOWAY:
                case TacticalLines.ATWALL:
                    break;
                default:
                    return;
            }
            POINT2 ptCurrent=null;
            POINT2 ptLast=null;
            for(int j=1;j<tg.Pixels.size();j++)
            {
                ptLast=new POINT2(tg.Pixels.get(j-1));
                ptCurrent=new POINT2(tg.Pixels.get(j));
                double d=Math.abs(ptCurrent.x-ptLast.x);
                if(d<=1)
                {
                    //ptCurrent.x += Math.pow(-1,j);
                    ptCurrent.x = ptLast.x + Math.pow(-1,j);
                    tg.Pixels.set(j, ptCurrent);
                }
            }
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("clsUtility", "FilterVerticalSegments",
                    new RendererException("Failed inside FilterVerticalSegments", exc));

        }
        return;
    }
    /**
     * @deprecated
     * used for BELT to prevent vertical client segments
     * @param pixels
     */
    public static void MovePixels2(ArrayList<POINT2>pixels2)
    {
        try
        {
            double[]pixels=new double[pixels2.size()*2];
            int j=0;
            POINT2 ptTemp=null;
            for(j=0;j<pixels2.size();j++)
            {
                ptTemp=pixels2.get(j);
                pixels[2*j]=ptTemp.x;
                pixels[2*j+1]=ptTemp.y;
            }
            MovePixels(pixels,pixels2.size());
            for(j=0;j<pixels2.size();j++)
            {
                ptTemp=new POINT2(pixels[2*j],pixels[2*j+1]);
                pixels2.set(j, ptTemp);
            }
        }
        catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.ReorderPixels");
            ErrorLogger.LogException(_className, "MovePixels2",
                    new RendererException("Failed inside MovePixels2", exc));
        }
    }
    /**
     * @deprecated
     * Called by clsChannelUtility to prevent pixels from being on top of each other.
     * @param pixels OUT -  client points as 2 tuples x,y
     * @param count the number of client points.
     */
    protected static void MovePixels(double[] pixels, int count) {
        try {
            boolean bolNoRepeats = false;
            int j;
            double x1;
            double y1;
            double x2;
            double y2;
            int count2;
            if (count > 0)
            {
                count2 = count;
            }
            else
            {
                count2 = pixels.length / 2;
            }
            bolNoRepeats = false;
            do
            {
                bolNoRepeats = true;
                for (j = 0; j < count2 - 1; j++)
                {
                    x1 = pixels[2 * j];
                    y1 = pixels[2 * j + 1];
                    x2 = pixels[2 * j + 2];
                    y2 = pixels[2 * j + 3];
                    //if ((int)x1 == (int)x2)
                    if (Math.abs(x2-x1)<1d)
                    {
                        bolNoRepeats = false;
                        pixels[2 * j + 2] = x1 + 1d; //move the point
                        //WriteFile("x repeats");
                        break;
                    }
                    //if ((int)y1 == (int)y2)
                    if (Math.abs(y2-y1)<1d)
                    {
                        bolNoRepeats = false;
                        pixels[2 * j + 3] = y1 + 1d; //move the point
                        break;
                    }
                }
            }
            while (bolNoRepeats == false);
            return;
        } catch (Exception exc) {
            //System.out.println(e.getMessage());
            //clsUtility.WriteFile("Error in clsUtility.MovePixels");
            ErrorLogger.LogException(_className, "MovePixels",
                    new RendererException("Failed inside MovePixels", exc));
        }
    }

    /**
     * Client utility to calculate the channel points for channel types.
     * This code was ported from CJMTK.
     * @param arrLocation the client points
     * @return the channel point
     */
    public static POINT2 ComputeLastPoint(ArrayList<POINT2> arrLocation) {
        POINT2 locD = new POINT2(0, 0);
        try {
            POINT2 locA = arrLocation.get(1);
            //Get the first point (b) in pixels.
            //var locB:Point=new Point(arrLocation[0].x,arrLocation[0].y);
            POINT2 locB = arrLocation.get(0);

            //Compute the distance in pixels from (a) to (b).
            double dblDx = locB.x - locA.x;
            double dblDy = locB.y - locA.y;

            //Compute the dblAngle in radians from (a) to (b).
            double dblTheta = Math.atan2(-dblDy, dblDx);

            //Compute a reasonable intermediate point along the line from (a) to (b).
            POINT2 locC = new POINT2(0, 0);
            locC.x = (int) (locA.x + 0.85 * dblDx);
            locC.y = (int) (locA.y + 0.85 * dblDy);
            //Put the last point on the left side of the line from (a) to (b).
            double dblAngle = dblTheta + Math.PI / 2.0;
            if (dblAngle > Math.PI) {
                dblAngle = dblAngle - 2.0 * Math.PI;
            }
            if (dblAngle < -Math.PI) {
                dblAngle = dblAngle + 2.0 * Math.PI;
            }

            //Set the magnitude of the dblWidth in pixels.  Make sure it is at least 15 pixels.
            double dblWidth = 30;//was 15

            //Compute the last point in pixels.
            locD.x = (locC.x + dblWidth * Math.cos(dblAngle));
            locD.y = (locC.y - dblWidth * Math.sin(dblAngle));
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsUtility.ComputeLatPoint");
            ErrorLogger.LogException(_className, "ComputeLastPoint",
                    new RendererException("Failed inside ComputeLastPoint", exc));
        }
        return locD;
    }

    /**
     * Called by clsChannelUtility. The segments are used for managing double-backed segments
     * for channel types. If the new point is double-backed then the segment at that index will be false.
     *
     * @param pixels the client points as 2-tuples x,y in pixels
     * @param segments OUT - the segments
     * @param factor a steepness factor for calculating whether the segment is double-backed
     */
    protected static void GetSegments(double[] pixels,
            boolean[] segments,
            double factor) {
        try
        {
            int j = 0;
            ref<double[]> m1 = new ref();
            ref<double[]> m2 = new ref();
            long numPoints = 0;
            boolean bolVertical1 = false;
            boolean bolVertical2 = false;

            POINT2 pt0F = new POINT2(0, 0);
            POINT2 pt1F = new POINT2(0, 0);
            POINT2 pt2F = new POINT2(0, 0);

            segments[0] = true;
            
            numPoints = pixels.length / 2;
            for (j = 0; j < numPoints - 2; j++)
            {
                pt0F.x = (double) pixels[2 * j];
                pt0F.y = (double) pixels[2 * j + 1];

                pt1F.x = (double) pixels[2 * j + 2];
                pt1F.y = (double) pixels[2 * j + 3];

                pt2F.x = (double) pixels[2 * j + 4];
                pt2F.y = (double) pixels[2 * j + 5];

                bolVertical1 = lineutility.CalcTrueSlopeDoubleForRoutes(pt0F, pt1F, m1);
                bolVertical2 = lineutility.CalcTrueSlopeDoubleForRoutes(pt1F, pt2F, m2);

                segments[j + 1] = true;
                if (bolVertical1 == true && bolVertical2 == true)
                {
                    if (Math.abs(Math.atan(m1.value[0]) - Math.atan(m2.value[0])) < 1 / factor && InXOrder(pt0F, pt1F, pt2F) == false) //was 0.1
                    {
                        segments[j + 1] = false;
                    }
                }

                if ((bolVertical1 == false || Math.abs(m1.value[0]) > factor) && (bolVertical2 == false || Math.abs(m2.value[0]) > factor) && InYOrder(pt0F, pt1F, pt2F) == false) //was 10
                {
                    segments[j + 1] = false;
                }
            }	//end for
            //int n=segments.length;
        }
        catch (Exception exc)
        {
            //System.out.println(e.getMessage());
            //clsUtility.WriteFile("Error in clsUtility.GetSegments");
            ErrorLogger.LogException(_className, "GetSegments",
                    new RendererException("Failed inside GetSegments", exc));
        }
        return;
    }
    
        /**
     * Called by clsChannelUtility. The segments are used for managing double-backed segments
     * for channel types. If the new point is double-backed then the segment at that index will be false.
     *
     * @param pixels the client points as 2-tuples x,y in pixels
     * @param segments OUT - the segments
     */
    protected static void GetLCSegments(double[] pixels,
                                        boolean[] segments) {
        try
        {
            int j = 0;
            ref<double[]> m1 = new ref();
            ref<double[]> m2 = new ref();
            long numPoints = 0;
            boolean bolVertical1 = false;
            boolean bolVertical2 = false;

            POINT2 pt0F = new POINT2(0, 0);
            POINT2 pt1F = new POINT2(0, 0);
            POINT2 pt2F = new POINT2(0, 0);

            segments[0] = true;
            //used for debugging
            double[] angles = new double[segments.length];
            angles[0] = 0f;

            numPoints = pixels.length / 2;
            for (j = 0; j < numPoints - 2; j++)
            {
                pt0F.x = (double) pixels[2 * j];
                pt0F.y = (double) pixels[2 * j + 1];

                pt1F.x = (double) pixels[2 * j + 2];
                pt1F.y = (double) pixels[2 * j + 3];

                pt2F.x = (double) pixels[2 * j + 4];
                pt2F.y = (double) pixels[2 * j + 5];

                double angle1 = Math.atan2(pt1F.y - pt0F.y,
                        pt1F.x - pt0F.x);
                double angle2 = Math.atan2(pt1F.y - pt2F.y,
                        pt1F.x - pt2F.x);
                double angle = angle1-angle2;// * 180/Math.PI;
                double degrees = angle * 180/Math.PI;

                if(angle < 0)
                {
                    degrees = 360 + degrees;
                }

                if(degrees < 90)
                {
                    segments[j + 1] = false;
                }
                else
                    segments[j + 1] = true;

                angles[j+1] = degrees;

            }	//end for
        }
        catch (Exception exc)
        {
            //System.out.println(e.getMessage());
            //clsUtility.WriteFile("Error in clsUtility.GetSegments");
            ErrorLogger.LogException(_className, "GetLCSegments",
                    new RendererException("Failed inside GetSegments", exc));
        }
    }

    /**
     * Sets the color for the current shape depending on the affiliation
     * @param tg
     * @param shape
     */
    protected static void SetLCColor(TGLight tg, Shape2 shape) {
        try {
            String affiliation = tg.get_Affiliation();
            if (affiliation !=null && affiliation.equals("H")) {
                if (shape.getLineColor() == Color.RED) {
                    shape.setLineColor(tg.get_LineColor());
                } else {
                    shape.setLineColor(Color.RED);
                }
            } else {
                if (shape.getLineColor() != Color.RED) {
                    shape.setLineColor(tg.get_LineColor());
                } else {
                    shape.setLineColor(Color.RED);
                }
            }

        } catch (Exception exc) {
            //WriteFile("Error in clsUtility.SetLCColor");
            ErrorLogger.LogException(_className, "SetLCColor",
                    new RendererException("Failed inside SetLCColor", exc));
        }
    }
    /**
     * USAS requires a left-right orientation for ENY, which negates the upper-lower
     * orientation we used for Mil-Std-2525 ENY compliance. Therefore we must reverse
     * the client points for two of the quadrants
     * @param tg tactical graphic
     */
    public static void ReverseUSASLCPointsByQuadrant(TGLight tg)
    {
        try
        {
            if(tg.Pixels.size()<2)
                return;
            int quadrant=lineutility.GetQuadrantDouble(tg.Pixels.get(0), tg.Pixels.get(1));
            switch(tg.get_LineType())
            {
                case TacticalLines.LC:
                case TacticalLines.LC2:
                    if(tg.get_Affiliation()!=null && tg.get_Affiliation().equals("H"))
                    {
                        switch(quadrant)
                        {
                            case 2:
                            case 3:
                                break;
                            case 1://reverse the points for these two quadrants
                            case 4:
                                int n=tg.Pixels.size();
                                ArrayList<POINT2> pts2=(ArrayList<POINT2>)tg.Pixels.clone();
                                        for(int j=0;j<tg.Pixels.size();j++)
                                            tg.Pixels.set(j, pts2.get(n-j-1));
                                break;
                        }//end switch quadrant
                    }//end if
                    else
                    {
                        switch(quadrant)
                        {
                            case 1:
                            case 4:
                                break;
                            case 2://reverse the points for these two quadrants
                            case 3:
                                int n=tg.Pixels.size();
                                ArrayList<POINT2> pts2=(ArrayList<POINT2>)tg.Pixels.clone();
                                        for(int j=0;j<tg.Pixels.size();j++)
                                            tg.Pixels.set(j, pts2.get(n-j-1));
                                break;
                        }//end switch quadrant
                    }//end else
                default:
                    break;
            }//end switch linetype
        }
        catch (Exception exc) {
            //WriteFile("Error in clsUtility.SetLCColor");
            ErrorLogger.LogException(_className, "ReverseUSASLCPointsByQuadrant",
                    new RendererException("Failed inside ReverseUSASLCPointsByQuadrant", exc));
        }
        return;
    }//end ReverseUSASLCPointsByQuadrant
    /**
     * 
     * @param tg tactical graphic
     * @param bi buffered image to use for creating a graphics object to draw
     * @return ShapeInfo hatch fill
     */
    public static Shape2 getHatchShape(TGLight tg,
            BufferedImage bi)
    {
        Shape2 shape=null;
        try
        {
            if(tg.get_UseHatchFill())
                return null;
            switch(tg.get_LineType())
            {
                case TacticalLines.OBSAREA:
                    break;
                default:
                    return null;
            }
            shape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
            shape.moveTo(tg.Pixels.get(0));
            int j=0;
            for(j=1;j<tg.Pixels.size();j++)
            {
                shape.lineTo(tg.Pixels.get(j));
            }
            //shape.setStroke(new BasicStroke(0));
            shape.setLineColor(new Color(0,0,0,0));
            shape.set_Fillstyle(3);

            Rectangle2D.Double rect = null;
            Graphics2D grid = null;
            TexturePaint tp = tg.get_TexturePaint();

            rect = new Rectangle2D.Double(0, 0, 8, 8);
            grid = bi.createGraphics();
            grid.setColor(clsUtility.GetOpaqueColor(tg.get_LineColor()));
            //Graphics2D g2d=bi.createGraphics();

            grid.setStroke(new BasicStroke(2));
            grid.drawLine(0, 8, 8, 0);
            tp = new TexturePaint(bi, rect);
            shape.setTexturePaint(tp);
            shape.setFillColor(new Color(0,0,0,0));
            grid.dispose();
        }
        catch (Exception exc)
        {
            ErrorLogger.LogException(_className ,"GetHatchShape",
                new RendererException("Failed inside GetHatchShape", exc));
        }
        return shape;
    }
    /**
     * initialize the hash tables
     * @param rev Mil-Standard-2525 revision 
     */
    public static synchronized void initializeLinetypes(int rev)
    {
        try
        {
            if(linetypes != null || metocs != null)
                return;
            
            linetypes=new HashMap<String,Object>();
            
            //basic shapes
            linetypes.put("BS_LINE--------", TacticalLines.BS_LINE);
            linetypes.put("BS_AREA--------", TacticalLines.BS_AREA);
            linetypes.put("BS_CROSS-------", TacticalLines.BS_CROSS);
            linetypes.put("BS_ELLIPSE-----", TacticalLines.BS_ELLIPSE);
            linetypes.put("PBS_ELLIPSE----", TacticalLines.PBS_ELLIPSE);
            linetypes.put("PBS_CIRCLE-----", TacticalLines.PBS_CIRCLE);
            linetypes.put("BS_RECTANGLE---", TacticalLines.BS_RECTANGLE);
            //end basic shapes
            
            //buffered shapes
            linetypes.put("BBS_LINE-------", TacticalLines.BBS_LINE);
            linetypes.put("BBS_AREA-------", TacticalLines.BBS_AREA);
            linetypes.put("BBS_POINT------", TacticalLines.BBS_POINT);
            linetypes.put("BBS_RECTANGLE--", TacticalLines.BBS_RECTANGLE);
            linetypes.put("BS_BBOX--------", TacticalLines.BS_BBOX);
            //end basic shapes
            
            linetypes.put("G*MPOHO---****X", TacticalLines.OVERHEAD_WIRE);
            
            //Tasks
            linetypes.put("G*TPB-----****X", TacticalLines.BLOCK);
            linetypes.put("G*TPH-----****X", TacticalLines.BREACH);
            linetypes.put("G*TPY-----****X", TacticalLines.BYPASS);
            linetypes.put("G*TPC-----****X", TacticalLines.CANALIZE);
            linetypes.put("G*TPX-----****X", TacticalLines.CLEAR);
            linetypes.put("G*TPJ-----****X", TacticalLines.CONTAIN);
            linetypes.put("G*TPK-----****X", TacticalLines.CATK);
            linetypes.put("G*TPKF----****X", TacticalLines.CATKBYFIRE);
            linetypes.put("G*TPL-----****X", TacticalLines.DELAY);
            linetypes.put("G*TPT-----****X", TacticalLines.DISRUPT);
            linetypes.put("G*TPF-----****X", TacticalLines.FIX);
            linetypes.put("G*TPA-----****X", TacticalLines.FOLLA);
            linetypes.put("G*TPAS----****X", TacticalLines.FOLSP);
            linetypes.put("G*TPE-----****X", TacticalLines.ISOLATE);
            linetypes.put("G*TPO-----****X", TacticalLines.OCCUPY);
            linetypes.put("G*TPP-----****X", TacticalLines.PENETRATE);
            linetypes.put("G*TPR-----****X", TacticalLines.RIP);
            linetypes.put("G*TPQ-----****X", TacticalLines.RETAIN);
            linetypes.put("G*TPM-----****X", TacticalLines.RETIRE);
            linetypes.put("G*TPS-----****X", TacticalLines.SECURE);
            linetypes.put("G*TPUS----****X", TacticalLines.SCREEN);
            linetypes.put("G*TPUC----****X", TacticalLines.COVER);
            linetypes.put("G*TPUG----****X", TacticalLines.GUARD);
            linetypes.put("G*TPZ-----****X", TacticalLines.SEIZE);
            linetypes.put("G*TPW-----****X", TacticalLines.WITHDRAW);
            linetypes.put("G*TPWP----****X", TacticalLines.WDRAWUP);
            linetypes.put("G*TPV-----****X", TacticalLines.CORDONSEARCH);
            linetypes.put("G*TP2-----****X", TacticalLines.CORDONKNOCK);
            //end Tasks
            //C2GM
            linetypes.put("G*GPGLB---****X", TacticalLines.BOUNDARY);
            linetypes.put("G*GPGLF---****X", TacticalLines.FLOT);
            linetypes.put("G*GPGLC---****X", TacticalLines.LC);
            linetypes.put("G*GPGLP---****X", TacticalLines.PL);
            linetypes.put("G*GPGLL---****X", TacticalLines.LL);
            linetypes.put("G*GPGAG---****X", TacticalLines.GENERAL);
            linetypes.put("G*GPGAA---****X", TacticalLines.ASSY);
            linetypes.put("G*GPGAE---****X", TacticalLines.EA);
            linetypes.put("G*GPGAF---****X", TacticalLines.FORT);
            linetypes.put("G*GPGAD---****X", TacticalLines.DZ);
            linetypes.put("G*GPGAX---****X", TacticalLines.EZ);
            linetypes.put("G*GPGAL---****X", TacticalLines.LZ);
            linetypes.put("G*GPGAP---****X", TacticalLines.PZ);
            linetypes.put("G*GPGAS---****X", TacticalLines.SARA);
            linetypes.put("G*GPGAY---****X", TacticalLines.LAA);
            linetypes.put("G*GPGAZ---****X", TacticalLines.AIRFIELD);
            linetypes.put("G*GPALC---****X", TacticalLines.AC);
            linetypes.put("G*GPALM---****X", TacticalLines.MRR);
            linetypes.put("G*GPALS---****X", TacticalLines.SAAFR);
            linetypes.put("G*GPALU---****X", TacticalLines.UAV);
            linetypes.put("G*GPALL---****X", TacticalLines.LLTR);
            linetypes.put("G*GPAAR---****X", TacticalLines.ROZ);
            linetypes.put("G*GPAAF---****X", TacticalLines.FAADZ);            
            linetypes.put("G*GPAAH---****X", TacticalLines.HIDACZ);
            linetypes.put("G*GPAAM---****X", TacticalLines.MEZ);
            linetypes.put("G*GPAAML--****X", TacticalLines.LOMEZ);
            linetypes.put("G*GPAAMH--****X", TacticalLines.HIMEZ);
            linetypes.put("G*GPAAW---****X", TacticalLines.WFZ);
            linetypes.put("G*GPPD----****X", TacticalLines.DECEIVE);
            linetypes.put("G*GPPA----****X", TacticalLines.AAFNT);
            linetypes.put("G*GPPF----****X", TacticalLines.DIRATKFNT);
            linetypes.put("G*GPPM----****X", TacticalLines.DMA);
            linetypes.put("G*GPPY----****X", TacticalLines.DMAF);
            linetypes.put("G*GPPC----****X", TacticalLines.DUMMY);
            linetypes.put("G*GPDLF---****X", TacticalLines.FEBA);
            linetypes.put("G*GPDLP---****X", TacticalLines.PDF);
            linetypes.put("G*GPDAB---****X", TacticalLines.BATTLE);
            linetypes.put("G*GPDABP--****X", TacticalLines.PNO);
            linetypes.put("G*GPDAE---****X", TacticalLines.EA1);
            linetypes.put("G*GPOLAV--****X", TacticalLines.AXAD);
            linetypes.put("G*GPOLAA--****X", TacticalLines.AIRAOA);
            linetypes.put("G*GPOLAR--****X", TacticalLines.AAAAA);
            linetypes.put("G*GPOLAGM-****X", TacticalLines.MAIN);
            linetypes.put("G*GPOLAGS-****X", TacticalLines.SPT);
            linetypes.put("G*GPOLKA--****X", TacticalLines.DIRATKAIR);
            linetypes.put("G*GPOLKGM-****X", TacticalLines.DIRATKGND);
            linetypes.put("G*GPOLKGS-****X", TacticalLines.DIRATKSPT);
            linetypes.put("G*GPOLF---****X", TacticalLines.FCL);
            linetypes.put("G*GPOLI---****X", TacticalLines.IL);
            linetypes.put("G*GPOLL---****X", TacticalLines.LOA);
            linetypes.put("G*GPOLT---****X", TacticalLines.LOD);
            linetypes.put("G*GPOLC---****X", TacticalLines.LDLC);
            linetypes.put("G*GPOLP---****X", TacticalLines.PLD);
            linetypes.put("G*GPOAA---****X", TacticalLines.ASSAULT);
            linetypes.put("G*GPOAK---****X", TacticalLines.ATKPOS);
            linetypes.put("G*GPOAF---****X", TacticalLines.ATKBYFIRE);
            linetypes.put("G*GPOAS---****X", TacticalLines.SPTBYFIRE);
            linetypes.put("G*GPOAO---****X", TacticalLines.OBJ);
            linetypes.put("G*GPOAP---****X", TacticalLines.PEN);
            linetypes.put("G*GPSLA---****X", TacticalLines.AMBUSH);
            linetypes.put("G*GPSLH---****X", TacticalLines.HOLD);
            linetypes.put("G*GPSLR---****X", TacticalLines.RELEASE);
            linetypes.put("G*GPSLB---****X", TacticalLines.BRDGHD);
            //linetypes.put("G*GPSLB---****X", TacticalLines.BRDGHD_GE);    //client ge?
            linetypes.put("G*GPSAO---****X", TacticalLines.AO);
            linetypes.put("G*GPSAA---****X", TacticalLines.AIRHEAD);
            linetypes.put("G*GPSAE---****X", TacticalLines.ENCIRCLE);
            linetypes.put("G*GPSAN---****X", TacticalLines.NAI);
            linetypes.put("G*GPSAT---****X", TacticalLines.TAI);
            
            //obstacles
            linetypes.put("G*MPOGB---****X", TacticalLines.BELT);
            //linetypes.put("G*MPOGB---****X", TacticalLines.BELT1);
            linetypes.put("G*MPOGL---****X", TacticalLines.LINE);
            linetypes.put("G*MPOGZ---****X", TacticalLines.ZONE);
            linetypes.put("G*MPOGF---****X", TacticalLines.OBSFAREA);
            linetypes.put("G*MPOGR---****X", TacticalLines.OBSAREA);
            linetypes.put("G*MPOS----****X", TacticalLines.ABATIS);
            linetypes.put("G*MPOADU--****X", TacticalLines.ATDITCH);
            linetypes.put("G*MPOADC--****X", TacticalLines.ATDITCHC);
            linetypes.put("G*MPOAR---****X", TacticalLines.ATDITCHM);
            linetypes.put("G*MPOAW---****X", TacticalLines.ATWALL);
            linetypes.put("G*MPOMC---****X", TacticalLines.CLUSTER);
            linetypes.put("G*MPOFD---****X", TacticalLines.DEPICT);
            linetypes.put("G*MPOFG---****X", TacticalLines.GAP);
            linetypes.put("G*MPOFA---****X", TacticalLines.MINED);
            linetypes.put("G*MPOEB---****X", TacticalLines.MNFLDBLK);
            linetypes.put("G*MPOEF---****X", TacticalLines.MNFLDFIX);
            linetypes.put("G*MPOET---****X", TacticalLines.TURN);
            linetypes.put("G*MPOED---****X", TacticalLines.MNFLDDIS);
            linetypes.put("G*MPOU----****X", TacticalLines.UXO);
            linetypes.put("G*MPORP---****X", TacticalLines.PLANNED);
            linetypes.put("G*MPORS---****X", TacticalLines.ESR1);
            linetypes.put("G*MPORA---****X", TacticalLines.ESR2);
            linetypes.put("G*MPORC---****X", TacticalLines.ROADBLK);
            linetypes.put("G*MPOT----****X", TacticalLines.TRIP);
            linetypes.put("G*MPOWU---****X", TacticalLines.UNSP);
            linetypes.put("G*MPOWS---****X", TacticalLines.SFENCE);
            linetypes.put("G*MPOWD---****X", TacticalLines.DFENCE);
            linetypes.put("G*MPOWA---****X", TacticalLines.DOUBLEA);
            linetypes.put("G*MPOWL---****X", TacticalLines.LWFENCE);
            linetypes.put("G*MPOWH---****X", TacticalLines.HWFENCE);
            linetypes.put("G*MPOWCS--****X", TacticalLines.SINGLEC);
            linetypes.put("G*MPOWCD--****X", TacticalLines.DOUBLEC);
            linetypes.put("G*MPOWCT--****X", TacticalLines.TRIPLE);
            linetypes.put("G*MPBDE---****X", TacticalLines.EASY);
            linetypes.put("G*MPBDD---****X", TacticalLines.BYDIF);
            linetypes.put("G*MPBDI---****X", TacticalLines.BYIMP);
            linetypes.put("G*MPBCA---****X", TacticalLines.ASLTXING);
            linetypes.put("G*MPBCB---****X", TacticalLines.BRIDGE);
            linetypes.put("G*MPBCF---****X", TacticalLines.FERRY);
            linetypes.put("G*MPBCE---****X", TacticalLines.FORDSITE);
            linetypes.put("G*MPBCD---****X", TacticalLines.FORDIF);
            linetypes.put("G*MPBCL---****X", TacticalLines.MFLANE);
            linetypes.put("G*MPBCR---****X", TacticalLines.RAFT);
            linetypes.put("G*MPSL----****X", TacticalLines.FORTL);
            linetypes.put("G*MPSW----****X", TacticalLines.FOXHOLE);
            linetypes.put("G*MPSP----****X", TacticalLines.STRONG);
            linetypes.put("G*MPNM----****X", TacticalLines.MSDZ);
            linetypes.put("G*MPNR----****X", TacticalLines.RAD);
            linetypes.put("G*MPNB----****X", TacticalLines.BIO);
            linetypes.put("G*MPNC----****X", TacticalLines.CHEM);
            linetypes.put("G*MPNL----****X", TacticalLines.DRCL);
            //fire support
            linetypes.put("G*FPLT----****X", TacticalLines.LINTGT);
            linetypes.put("G*FPLTS---****X", TacticalLines.LINTGTS);
            linetypes.put("G*FPLTF---****X", TacticalLines.FPF);
            linetypes.put("G*FPLCF---****X", TacticalLines.FSCL);
            linetypes.put("G*FPLCC---****X", TacticalLines.CFL);
            linetypes.put("G*FPLCN---****X", TacticalLines.NFL);
            linetypes.put("G*FPLCR---****X", TacticalLines.RFL);
            linetypes.put("G*FPAT----****X", TacticalLines.AT);
            linetypes.put("G*FPATR---****X", TacticalLines.RECTANGULAR);
            linetypes.put("PBS_RECTANGLE--", TacticalLines.PBS_RECTANGLE);
            linetypes.put("PBS_SQUARE-----", TacticalLines.PBS_SQUARE);
            linetypes.put("G*FPATC---****X", TacticalLines.CIRCULAR);
            linetypes.put("G*FPATG---****X", TacticalLines.SERIES);
            linetypes.put("G*FPATS---****X", TacticalLines.SMOKE);
            linetypes.put("G*FPATB---****X", TacticalLines.BOMB);
            linetypes.put("G*FPACSI--****X", TacticalLines.FSA);
            linetypes.put("G*FPACSR--****X", TacticalLines.FSA_RECTANGULAR);
            linetypes.put("G*FPACSC--****X", TacticalLines.FSA_CIRCULAR);
            linetypes.put("G*FPACAI--****X", TacticalLines.ACA);
            linetypes.put("G*FPACAR--****X", TacticalLines.ACA_RECTANGULAR);
            linetypes.put("G*FPACAC--****X", TacticalLines.ACA_CIRCULAR);
            linetypes.put("G*FPACFI--****X", TacticalLines.FFA);
            linetypes.put("G*FPACFR--****X", TacticalLines.FFA_RECTANGULAR);
            linetypes.put("G*FPACFC--****X", TacticalLines.FFA_CIRCULAR);
            linetypes.put("G*FPACNI--****X", TacticalLines.NFA);
            linetypes.put("G*FPACNR--****X", TacticalLines.NFA_RECTANGULAR);
            linetypes.put("G*FPACNC--****X", TacticalLines.NFA_CIRCULAR);
            linetypes.put("G*FPACRI--****X", TacticalLines.RFA);
            linetypes.put("G*FPACRR--****X", TacticalLines.RFA_RECTANGULAR);
            linetypes.put("G*FPACRC--****X", TacticalLines.RFA_CIRCULAR);
            linetypes.put("G*FPACPR--****X", TacticalLines.PAA_RECTANGULAR_REVC);
            linetypes.put("G*FPACPC--****X", TacticalLines.PAA_CIRCULAR);
            linetypes.put("G*FPAZII--****X", TacticalLines.ATI);
            linetypes.put("G*FPAZIR--****X", TacticalLines.ATI_RECTANGULAR);
            linetypes.put("G*FPAZIC--****X", TacticalLines.ATI_CIRCULAR);
            linetypes.put("G*FPAZXI--****X", TacticalLines.CFFZ);
            linetypes.put("G*FPAZXR--****X", TacticalLines.CFFZ_RECTANGULAR);
            linetypes.put("G*FPAZXC--****X", TacticalLines.CFFZ_CIRCULAR);
            linetypes.put("G*FPAZSI--****X", TacticalLines.SENSOR);
            linetypes.put("G*FPAZSR--****X", TacticalLines.SENSOR_RECTANGULAR);
            linetypes.put("G*FPAZSC--****X", TacticalLines.SENSOR_CIRCULAR);
            linetypes.put("G*FPAZCI--****X", TacticalLines.CENSOR);
            linetypes.put("G*FPAZCR--****X", TacticalLines.CENSOR_RECTANGULAR);
            linetypes.put("G*FPAZCC--****X", TacticalLines.CENSOR_CIRCULAR);
            linetypes.put("G*FPAZDI--****X", TacticalLines.DA);
            linetypes.put("G*FPAZDR--****X", TacticalLines.DA_RECTANGULAR);
            linetypes.put("G*FPAZDC--****X", TacticalLines.DA_CIRCULAR);
            linetypes.put("G*FPAZFI--****X", TacticalLines.CFZ);
            linetypes.put("G*FPAZFR--****X", TacticalLines.CFZ_RECTANGULAR);
            linetypes.put("G*FPAZFC--****X", TacticalLines.CFZ_CIRCULAR);
            linetypes.put("G*FPAZZI--****X", TacticalLines.ZOR);
            linetypes.put("G*FPAZZR--****X", TacticalLines.ZOR_RECTANGULAR);
            linetypes.put("G*FPAZZC--****X", TacticalLines.ZOR_CIRCULAR);
            linetypes.put("G*FPAZBI--****X", TacticalLines.TBA);
            linetypes.put("G*FPAZBR--****X", TacticalLines.TBA_RECTANGULAR);
            linetypes.put("G*FPAZBC--****X", TacticalLines.TBA_CIRCULAR);
            linetypes.put("G*FPAZVI--****X", TacticalLines.TVAR);
            linetypes.put("G*FPAKBI--****X", TacticalLines.KILLBOXBLUE);
            linetypes.put("G*FPAKBR--****X", TacticalLines.KILLBOXBLUE_RECTANGULAR);
            linetypes.put("G*FPAKBC--****X", TacticalLines.KILLBOXBLUE_CIRCULAR);
            linetypes.put("G*FPAKPI--****X", TacticalLines.KILLBOXPURPLE);
            linetypes.put("G*FPAKPR--****X", TacticalLines.KILLBOXPURPLE_RECTANGULAR);
            linetypes.put("G*FPAKPC--****X", TacticalLines.KILLBOXPURPLE_CIRCULAR);
            
            linetypes.put("G*FPAZVR--****X", TacticalLines.TVAR_RECTANGULAR);
            linetypes.put("G*FPAZVC--****X", TacticalLines.TVAR_CIRCULAR);
            linetypes.put("G*FPAXC---****X", TacticalLines.RANGE_FAN);
            linetypes.put("G*FPAXS---****X", TacticalLines.RANGE_FAN_SECTOR);
            //css
            linetypes.put("G*SPLCM---****X", TacticalLines.CONVOY);
            linetypes.put("G*SPLCH---****X", TacticalLines.HCONVOY);
            linetypes.put("G*SPLRM---****X", TacticalLines.MSR);
            linetypes.put("G*SPLRA---****X", TacticalLines.ASR);
            linetypes.put("G*SPLRO---****X", TacticalLines.ONEWAY);
            linetypes.put("G*SPLRT---****X", TacticalLines.ALT);
            linetypes.put("G*SPLRW---****X", TacticalLines.TWOWAY);
            linetypes.put("G*SPAD----****X", TacticalLines.DHA);
            linetypes.put("G*SPAE----****X", TacticalLines.EPW);
            linetypes.put("G*SPAR----****X", TacticalLines.FARP);
            linetypes.put("G*SPAH----****X", TacticalLines.RHA);
            linetypes.put("G*SPASB---****X", TacticalLines.BSA);
            linetypes.put("G*SPASD---****X", TacticalLines.DSA);
            linetypes.put("G*SPASR---****X", TacticalLines.RSA);
            //other
            linetypes.put("G*OPHN----****X", TacticalLines.NAVIGATION);
            linetypes.put("G*OPB-----****X", TacticalLines.BEARING);
            linetypes.put("G*OPBE----****X", TacticalLines.ELECTRO);
            linetypes.put("G*OPBA----****X", TacticalLines.ACOUSTIC);
            linetypes.put("G*OPBT----****X", TacticalLines.TORPEDO);
            linetypes.put("G*OPBO----****X", TacticalLines.OPTICAL);
            linetypes.put("GENERIC---****X", TacticalLines.GENERIC);
            linetypes.put("G*FPLCM---****X", TacticalLines.MFP);
            if(rev==RendererSettings.Symbology_2525C)
            {
                linetypes.put("G*TPUS----****X", TacticalLines.SCREEN_REVC);
                linetypes.put("G*TPUC----****X", TacticalLines.COVER_REVC);
                linetypes.put("G*TPUG----****X", TacticalLines.GUARD_REVC);
                linetypes.put("G*TPZ-----****X", TacticalLines.SEIZE_REVC);
                //linetypes.put("G*GPAAF---****X", TacticalLines.SHORADZ );            
                
                //modified symbol codes for fire support areas
                linetypes.put("G*FPACEI--****X", TacticalLines.SENSOR);
                linetypes.put("G*FPACEC--****X", TacticalLines.SENSOR_CIRCULAR);
                linetypes.put("G*FPACER--****X", TacticalLines.SENSOR_RECTANGULAR);
                linetypes.put("G*FPACDI--****X", TacticalLines.DA);
                linetypes.put("G*FPACDC--****X", TacticalLines.DA_CIRCULAR);
                linetypes.put("G*FPACDR--****X", TacticalLines.DA_RECTANGULAR);
                linetypes.put("G*FPACZI--****X", TacticalLines.ZOR);
                linetypes.put("G*FPACZC--****X", TacticalLines.ZOR_CIRCULAR);
                linetypes.put("G*FPACZR--****X", TacticalLines.ZOR_RECTANGULAR);
                linetypes.put("G*FPACBI--****X", TacticalLines.TBA);
                linetypes.put("G*FPACBC--****X", TacticalLines.TBA_CIRCULAR);
                linetypes.put("G*FPACBR--****X", TacticalLines.TBA_RECTANGULAR);
                linetypes.put("G*FPACVI--****X", TacticalLines.TVAR);
                linetypes.put("G*FPACVC--****X", TacticalLines.TVAR_CIRCULAR);
                linetypes.put("G*FPACVR--****X", TacticalLines.TVAR_RECTANGULAR);
                
                //MFP added
                linetypes.put("G*FPACT---****X", TacticalLines.TGMF);
            }
                        
            //METOCs
            metocs=new HashMap<String,Object>();
            metocs.put("WA-DPFC----L---",TacticalLines.CF);
            metocs.put("WA-DPFCU---L---",TacticalLines.UCF);
            metocs.put("WA-DPFC-FG-L---",TacticalLines.CFG);
            metocs.put("WA-DPFC-FY-L---",TacticalLines.CFY);
            metocs.put("WA-DPFW----L---",TacticalLines.WF);
            metocs.put("WA-DPFWU---L---",TacticalLines.UWF);
            metocs.put("WA-DPFW-FG-L---",TacticalLines.WFG);
            metocs.put("WA-DPFW-FY-L---",TacticalLines.WFY);
            metocs.put("WA-DPFO----L---",TacticalLines.OCCLUDED);
            metocs.put("WA-DPFOU---L---",TacticalLines.UOF);
            metocs.put("WA-DPFO-FY-L---",TacticalLines.OFY);
            metocs.put("WA-DPFS----L---",TacticalLines.SF);
            metocs.put("WA-DPFSU---L---",TacticalLines.USF);
            metocs.put("WA-DPFS-FG-L---",TacticalLines.SFG);
            metocs.put("WA-DPFS-FY-L---",TacticalLines.SFY);
            metocs.put("WA-DPXT----L---",TacticalLines.TROUGH);
            metocs.put("WA-DPXR----L---",TacticalLines.RIDGE);
            metocs.put("WA-DPXSQ---L---",TacticalLines.SQUALL);
            metocs.put("WA-DPXIL---L---",TacticalLines.INSTABILITY);
            metocs.put("WA-DPXSH---L---",TacticalLines.SHEAR);
            metocs.put("WA-DPXITCZ-L---",TacticalLines.ITC);
            metocs.put("WA-DPXCV---L---",TacticalLines.CONVERGANCE);
            metocs.put("WA-DPXITD--L---",TacticalLines.ITD);
            
            metocs.put("WA-DWJ-----L---",TacticalLines.JET);
            metocs.put("WA-DWS-----L---",TacticalLines.STREAM);
            
            metocs.put("WA-DBAIF----A--",TacticalLines.IFR);
            metocs.put("WA-DBAMV----A--",TacticalLines.MVFR);
            metocs.put("WA-DBATB----A--",TacticalLines.TURBULENCE);
            metocs.put("WA-DBAI-----A--",TacticalLines.ICING);
            metocs.put("WA-DBALPNC--A--",TacticalLines.NON_CONVECTIVE);
            metocs.put("WA-DBALPC---A--",TacticalLines.CONVECTIVE);
            metocs.put("WA-DBAFP----A--",TacticalLines.FROZEN);
            metocs.put("WA-DBAT-----A--",TacticalLines.THUNDERSTORMS);
            metocs.put("WA-DBAFG----A--",TacticalLines.FOG);
            metocs.put("WA-DBAD-----A--",TacticalLines.SAND);
            metocs.put("WA-DBAFF----A--",TacticalLines.FREEFORM);
            
            metocs.put("WA-DIPIB---L---",TacticalLines.ISOBAR);
            metocs.put("WA-DIPCO---L---",TacticalLines.UPPER_AIR);
            metocs.put("WA-DIPIS---L---",TacticalLines.ISOTHERM);
            metocs.put("WA-DIPIT---L---",TacticalLines.ISOTACH);
            metocs.put("WA-DIPID---L---",TacticalLines.ISODROSOTHERM);
            metocs.put("WA-DIPTH---L---",TacticalLines.ISOPLETHS);
            metocs.put("WA-DIPFF---L---",TacticalLines.OPERATOR_FREEFORM);
            metocs.put("WO-DHHDB---L---",TacticalLines.BREAKERS);
            
            metocs.put("WO-DIDID---L---",TacticalLines.ICE_DRIFT);
            metocs.put("WO-DILOV---L---",TacticalLines.LVO);
            metocs.put("WO-DILUC---L---",TacticalLines.UNDERCAST);
            metocs.put("WO-DILOR---L---",TacticalLines.LRO);
            metocs.put("WO-DILIEO--L---",TacticalLines.ICE_EDGE);
            metocs.put("WO-DILIEE--L---",TacticalLines.ESTIMATED_ICE_EDGE);
            metocs.put("WO-DILIER--L---",TacticalLines.ICE_EDGE_RADAR);
            metocs.put("WO-DIOC----L---",TacticalLines.CRACKS);
            metocs.put("WO-DIOCS---L---",TacticalLines.CRACKS_SPECIFIC_LOCATION);
            metocs.put("WO-DIOL----L---",TacticalLines.ICE_OPENINGS_LEAD);
            metocs.put("WO-DIOLF---L---",TacticalLines.ICE_OPENINGS_FROZEN);
            metocs.put("WO-DHDDL---L---",TacticalLines.DEPTH_CURVE);
            metocs.put("WO-DHDDC---L---",TacticalLines.DEPTH_CONTOUR);
            metocs.put("WO-DHDDA----A--",TacticalLines.DEPTH_AREA);
            metocs.put("WO-DHCC----L---",TacticalLines.COASTLINE);
            metocs.put("WO-DHCI-----A--",TacticalLines.ISLAND);
            metocs.put("WO-DHCB-----A--",TacticalLines.BEACH);
            metocs.put("WO-DHCW-----A--",TacticalLines.WATER);
            metocs.put("WO-DHCF----L---",TacticalLines.FORESHORE_LINE);
            metocs.put("WO-DHCF-----A--",TacticalLines.FORESHORE_AREA);
            metocs.put("WO-DHPBA---L---",TacticalLines.ANCHORAGE_LINE);
            metocs.put("WO-DHPBA----A--",TacticalLines.ANCHORAGE_AREA);
            metocs.put("WO-DHPBP---L---",TacticalLines.PIER);
            metocs.put("WOS-HPFF----A--",TacticalLines.WEIRS);
            metocs.put("WO-DHPMD----A--",TacticalLines.DRYDOCK);
            metocs.put("WO-DHPMO---L---",TacticalLines.LOADING_FACILITY_LINE);
            metocs.put("WO-DHPMO----A--",TacticalLines.LOADING_FACILITY_AREA);
            metocs.put("WO-DHPMRA--L---",TacticalLines.RAMP_ABOVE_WATER);
            metocs.put("WO-DHPMRB--L---",TacticalLines.RAMP_BELOW_WATER);
            metocs.put("WO-DHPSPA--L---",TacticalLines.JETTY_ABOVE_WATER);
            metocs.put("WO-DHPSPB--L---",TacticalLines.JETTY_BELOW_WATER);
            metocs.put("WO-DHPSPS--L---",TacticalLines.SEAWALL);
            metocs.put("WO-DHABP----A--",TacticalLines.PERCHES);
            metocs.put("WO-DHALLA--L---",TacticalLines.LEADING_LINE);
            metocs.put("WO-DHHD-----A--",TacticalLines.UNDERWATER_HAZARD);
            metocs.put("WO-DHHDF----A--",TacticalLines.FOUL_GROUND);
            metocs.put("WO-DHHDK----A--",TacticalLines.KELP);
            metocs.put("WOS-HHDR---L---",TacticalLines.REEF);
            metocs.put("WO-DHHDD----A--",TacticalLines.DISCOLORED_WATER);
            metocs.put("WO-DTCCCFE-L---",TacticalLines.EBB_TIDE);
            metocs.put("WO-DTCCCFF-L---",TacticalLines.FLOOD_TIDE);
            
            metocs.put("WO-DOBVA----A--",TacticalLines.VDR_LEVEL_12);
            metocs.put("WO-DOBVB----A--",TacticalLines.VDR_LEVEL_23);
            metocs.put("WO-DOBVC----A--",TacticalLines.VDR_LEVEL_34);
            metocs.put("WO-DOBVD----A--",TacticalLines.VDR_LEVEL_45);
            metocs.put("WO-DOBVE----A--",TacticalLines.VDR_LEVEL_56);
            metocs.put("WO-DOBVF----A--",TacticalLines.VDR_LEVEL_67);
            metocs.put("WO-DOBVG----A--",TacticalLines.VDR_LEVEL_78);
            metocs.put("WO-DOBVH----A--",TacticalLines.VDR_LEVEL_89);
            metocs.put("WO-DOBVI----A--",TacticalLines.VDR_LEVEL_910);
            
            metocs.put("WO-DBSF-----A--",TacticalLines.BEACH_SLOPE_FLAT);
            metocs.put("WO-DBSG-----A--",TacticalLines.BEACH_SLOPE_GENTLE);
            metocs.put("WO-DBSM-----A--",TacticalLines.BEACH_SLOPE_MODERATE);
            metocs.put("WO-DBST-----A--",TacticalLines.BEACH_SLOPE_STEEP);
            
            metocs.put("WO-DGMSR----A--",TacticalLines.SOLID_ROCK);
            metocs.put("WO-DGMSC----A--",TacticalLines.CLAY);
            metocs.put("WO-DGMSSVS--A--",TacticalLines.VERY_COARSE_SAND);
            metocs.put("WO-DGMSSC---A--",TacticalLines.COARSE_SAND);
            metocs.put("WO-DGMSSM---A--",TacticalLines.MEDIUM_SAND);
            metocs.put("WO-DGMSSF---A--",TacticalLines.FINE_SAND);
            metocs.put("WO-DGMSSVF--A--",TacticalLines.VERY_FINE_SAND);
            metocs.put("WO-DGMSIVF--A--",TacticalLines.VERY_FINE_SILT);
            metocs.put("WO-DGMSIF---A--",TacticalLines.FINE_SILT);
            metocs.put("WO-DGMSIM---A--",TacticalLines.MEDIUM_SILT);
            metocs.put("WO-DGMSIC---A--",TacticalLines.COARSE_SILT);
            metocs.put("WO-DGMSB----A--",TacticalLines.BOULDERS);
            metocs.put("WO-DGMS-CO--A--",TacticalLines.OYSTER_SHELLS);
            metocs.put("WO-DGMS-PH--A--",TacticalLines.PEBBLES);
            metocs.put("WO-DGMS-SH--A--",TacticalLines.SAND_AND_SHELLS);
            metocs.put("WO-DGML-----A--",TacticalLines.BOTTOM_SEDIMENTS_LAND);
            metocs.put("WO-DGMN-----A--",TacticalLines.BOTTOM_SEDIMENTS_NO_DATA);
            metocs.put("WO-DGMRS----A--",TacticalLines.BOTTOM_ROUGHNESS_SMOOTH);
            metocs.put("WO-DGMRM----A--",TacticalLines.BOTTOM_ROUGHNESS_MODERATE);
            metocs.put("WO-DGMRR----A--",TacticalLines.BOTTOM_ROUGHNESS_ROUGH);
            
            metocs.put("WO-DGMCL----A--",TacticalLines.CLUTTER_LOW);
            metocs.put("WO-DGMCM----A--",TacticalLines.CLUTTER_MEDIUM);
            metocs.put("WO-DGMCH----A--",TacticalLines.CLUTTER_HIGH);
            
            metocs.put("WO-DGMIBA---A--",TacticalLines.IMPACT_BURIAL_0);
            metocs.put("WO-DGMIBB---A--",TacticalLines.IMPACT_BURIAL_10);
            metocs.put("WO-DGMIBC---A--",TacticalLines.IMPACT_BURIAL_20);
            metocs.put("WO-DGMIBD---A--",TacticalLines.IMPACT_BURIAL_75);
            metocs.put("WO-DGMIBE---A--",TacticalLines.IMPACT_BURIAL_100);
            
            metocs.put("WO-DGMBCA---A--",TacticalLines.BOTTOM_CATEGORY_A);
            metocs.put("WO-DGMBCB---A--",TacticalLines.BOTTOM_CATEGORY_B);
            metocs.put("WO-DGMBCC---A--",TacticalLines.BOTTOM_CATEGORY_C);
            
            metocs.put("WO-DGMBTA---A--",TacticalLines.BOTTOM_TYPE_A1);
            metocs.put("WO-DGMBTB---A--",TacticalLines.BOTTOM_TYPE_A2);
            metocs.put("WO-DGMBTC---A--",TacticalLines.BOTTOM_TYPE_A3);
            metocs.put("WO-DGMBTD---A--",TacticalLines.BOTTOM_TYPE_B1);
            metocs.put("WO-DGMBTE---A--",TacticalLines.BOTTOM_TYPE_B2);
            metocs.put("WO-DGMBTF---A--",TacticalLines.BOTTOM_TYPE_B3);
            metocs.put("WO-DGMBTG---A--",TacticalLines.BOTTOM_TYPE_C1);
            metocs.put("WO-DGMBTH---A--",TacticalLines.BOTTOM_TYPE_C2);
            metocs.put("WO-DGMBTI---A--",TacticalLines.BOTTOM_TYPE_C3);
            
            metocs.put("WO-DL-ML---L---",TacticalLines.MARITIME_LIMIT);
            metocs.put("WO-DL-MA----A--",TacticalLines.MARITIME_AREA);
            metocs.put("WO-DL-RA---L---",TacticalLines.RESTRICTED_AREA);
            metocs.put("WO-DL-SA----A--",TacticalLines.SWEPT_AREA);
            metocs.put("WO-DL-TA----A--",TacticalLines.TRAINING_AREA);
            metocs.put("WO-DL-O-----A--",TacticalLines.OPERATOR_DEFINED);
            metocs.put("WO-DMCA----L---",TacticalLines.CABLE);
            metocs.put("WO-DMCC-----A--",TacticalLines.SUBMERGED_CRIB);
            metocs.put("WO-DMCD----L---",TacticalLines.CANAL);
            metocs.put("WO-DMOA-----A--",TacticalLines.OIL_RIG_FIELD);
            metocs.put("WO-DMPA----L---",TacticalLines.PIPE);
        }   
        catch (Exception exc)
        {
            ErrorLogger.LogException(_className ,"initializeLinetypes",
                new RendererException("Failed inside initializeLinetypes", exc));
        }
        return;
    }
    /**
     * use str if tg is null
     * @param tg tactical graphic
     * @param str Mil=Standard-2525 symbol id
     * @return line type
     */
    public static int GetLinetypeFromString(String str)
    {
        int result=-1;
        try
        {
            String strLine=str;
            String strMask=strLine.substring(0,1)+"*"+strLine.substring(2,3)+"P"+strLine.substring(4, 10)+"****X";            
            if(str.equalsIgnoreCase("GENERIC---****X"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BS_LINE--------"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BBS_LINE-------"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BS_AREA--------"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BBS_AREA-------"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BS_CROSS-------"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BBS_POINT------"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BS_ELLIPSE-----"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("PBS_ELLIPSE----"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("PBS_CIRCLE-----"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BS_RECTANGLE---"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("PBS_RECTANGLE--"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("PBS_SQUARE-----"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BBS_RECTANGLE--"))
            {
                strMask=str;
            }
            else if(str.equalsIgnoreCase("BS_BBOX--------"))
            {
                strMask=str;
            }
            
            Object objResult=null;
            if(linetypes != null && metocs != null)
            {
                objResult=linetypes.get(strMask);
                if(objResult != null)
                    return (Integer)objResult;
                
                objResult=metocs.get(strLine);
                if(objResult != null)
                    return (Integer)objResult;
            }            
        }
        catch (Exception exc) {
            ErrorLogger.LogException(_className ,"CGetLinetypeFromString2",
                    new RendererException("Failed inside CGetLinetypeFromString2", exc));
        }
        return result;
    }
    /**
     * Rev C requires max 50 anchor points for the axis of advance symbols
     * 
     * @param tg tactical graphic
     */
    public static void setRevC(TGLight tg)
    {
        try
        {
            //if(tg.getSymbologyStandard()!=RendererSettings.Symbology_2525C)
                //return;
            
            int rev=tg.getSymbologyStandard();
            if(rev==RendererSettings.Symbology_2525B)
            {
                switch(tg.get_LineType())
                {
                    case TacticalLines.MRR:
                        //if(tg.Pixels.size()>2)                        
                            tg.set_LineType(TacticalLines.MRR_USAS);                        
                        return;
                    case TacticalLines.UAV:
                        //if(tg.Pixels.size()>2)                        
                            tg.set_LineType(TacticalLines.UAV_USAS);                        
                        return;
                    default:
                        return;
                }
            }
            //should be rev C at this point
            if(rev==RendererSettings.Symbology_2525C)
            {
                switch(tg.get_LineType())
                {
                    case TacticalLines.SCREEN:
                        if(tg.Pixels.size()==4)
                            tg.set_LineType(TacticalLines.SCREEN_REVC);
                        break;
                    case TacticalLines.GUARD:
                        if(tg.Pixels.size()==4)
                            tg.set_LineType(TacticalLines.GUARD_REVC);
                        break;
                    case TacticalLines.COVER:
                        if(tg.Pixels.size()==4)
                            tg.set_LineType(TacticalLines.COVER_REVC);
                        break;
                    case TacticalLines.SEIZE:                    
                        if(tg.Pixels.size()==4)
                            tg.set_LineType(TacticalLines.SEIZE_REVC);
                        break;
                    case TacticalLines.PAA_RECTANGULAR:                    
                        tg.set_LineType(TacticalLines.PAA_RECTANGULAR_REVC);
                        break;
                    default:
                        break;
                }
            }
            switch(tg.get_LineType())
            {
                case TacticalLines.SCREEN_REVC:
                    if(tg.Pixels.size()<4)
                        tg.set_LineType(TacticalLines.SCREEN);
                    break;
                case TacticalLines.GUARD_REVC:
                    if(tg.Pixels.size()<4)
                        tg.set_LineType(TacticalLines.GUARD);
                    break;
                case TacticalLines.COVER_REVC:
                    if(tg.Pixels.size()<4)
                        tg.set_LineType(TacticalLines.COVER);
                    break;
                case TacticalLines.SEIZE_REVC:                    
                    if(tg.Pixels.size()<4)
                        tg.set_LineType(TacticalLines.SEIZE);
                    break;
                default:
                    break;
            }//end switch            
        }//end try
        catch (Exception exc) 
        {
            ErrorLogger.LogException(_className ,"setRevC",
                    new RendererException("Failed inside setRevC", exc));
        }
        return;
    }
    /**
     * 
     * @param tg tactical graphic
     * @return true if auto-shape
     */
    public static boolean isAutoshape(TGLight tg)
    {
        try
        {
            int linetype=tg.get_LineType();
            switch(linetype)
            {
                case TacticalLines.BS_ELLIPSE:
                case TacticalLines.PBS_ELLIPSE:
                case TacticalLines.PBS_CIRCLE:
                case TacticalLines.BS_RECTANGLE:
                case TacticalLines.BBS_RECTANGLE:
                case TacticalLines.BBS_POINT:
                case TacticalLines.BS_CROSS:
                //the Tasks
                case TacticalLines.CORDONKNOCK:
                case TacticalLines.CORDONSEARCH:
                case TacticalLines.BLOCK:
                case TacticalLines.BREACH:
                case TacticalLines.BYPASS:
                case TacticalLines.CANALIZE:
                case TacticalLines.CLEAR:
                case TacticalLines.CONTAIN:
                case TacticalLines.DELAY:
                case TacticalLines.DISRUPT:
                case TacticalLines.FIX:
                case TacticalLines.FOLLA:
                case TacticalLines.FOLSP:
                case TacticalLines.ISOLATE:
                case TacticalLines.OCCUPY:
                case TacticalLines.PENETRATE:
                case TacticalLines.RIP:
                case TacticalLines.RETAIN:
                case TacticalLines.RETIRE:
                case TacticalLines.SECURE:
                case TacticalLines.SCREEN:
                case TacticalLines.COVER:
                case TacticalLines.GUARD:
                case TacticalLines.SCREEN_REVC:
                case TacticalLines.COVER_REVC:
                case TacticalLines.GUARD_REVC:
                case TacticalLines.SEIZE:
                case TacticalLines.SEIZE_REVC:
                case TacticalLines.WITHDRAW:
                case TacticalLines.WDRAWUP:
                //autoshapes which are not Tasks
                case TacticalLines.SARA:
                case TacticalLines.AC:
                case TacticalLines.MRR:
                case TacticalLines.SAAFR:
                case TacticalLines.LLTR:
                case TacticalLines.UAV:
                case TacticalLines.DECEIVE:
                case TacticalLines.PDF:
                case TacticalLines.IL:
                case TacticalLines.ATKBYFIRE:
                case TacticalLines.SPTBYFIRE:
                case TacticalLines.AMBUSH:
                case TacticalLines.CLUSTER:
                case TacticalLines.GAP:
                case TacticalLines.MNFLDBLK:
                case TacticalLines.MNFLDFIX:
                case TacticalLines.MNFLDDIS:
                case TacticalLines.TURN:
                case TacticalLines.PLANNED:
                case TacticalLines.ROADBLK:
                case TacticalLines.ESR1:
                case TacticalLines.ESR2:
                case TacticalLines.TRIP:
                case TacticalLines.EASY:
                case TacticalLines.BYDIF:
                case TacticalLines.BYIMP:
                case TacticalLines.ASLTXING:
                case TacticalLines.BRIDGE:
                case TacticalLines.FERRY:
                case TacticalLines.FORDSITE:
                case TacticalLines.FORDIF:
                case TacticalLines.MFLANE:
                case TacticalLines.RAFT:
                case TacticalLines.FOXHOLE:
                case TacticalLines.MSDZ:
                case TacticalLines.CONVOY:
                case TacticalLines.HCONVOY:
                case TacticalLines.LINTGT:
                case TacticalLines.LINTGTS:
                case TacticalLines.FPF:
                case TacticalLines.BEARING:
                case TacticalLines.ACOUSTIC:
                case TacticalLines.ELECTRO:
                case TacticalLines.OPTICAL:
                case TacticalLines.TORPEDO:
                    return true;
            }

        }
        catch (Exception exc)
        {
            ErrorLogger.LogException(_className, "isAutoshape",
                    new RendererException("Failed inside isAutoshape", exc));
        }
        return false;
    }
    /**
     * Client will send the segment colors within a modifier.
     * Format is 0:FFBBBB,4:FFAAAA,...
     * For the time being will assume the modifier being used is the H modifier
     * @param tg
     * @return 
     */
    public static HashMap<Integer,Color> getMSRSegmentColors(TGLight tg)
    {
        HashMap hMap=null;
        try
        {
            int linetype=tg.get_LineType();
            switch(linetype)
            {
                case TacticalLines.MSR:
                case TacticalLines.ASR:
                case TacticalLines.BOUNDARY:
                    if(tg.get_H()==null || tg.get_H().isEmpty())
                        return null;
                    hMap=new HashMap<Integer,Color>();
                    break;
                default:
                    return null;
            }
            String[]colorStrs=tg.get_H().split(",");
            int j=0,numSegs=colorStrs.length;
            String segPlusColor="";
            String[]seg=null;     
            Color color=null;
            int index=-1;
            for(j=0;j<numSegs;j++)
            {
                segPlusColor=colorStrs[j];
                if(!segPlusColor.contains(":"))
                    continue;
                seg=segPlusColor.split(":");
                color=SymbolUtilities.getColorFromHexString(seg[1]);
                index=Integer.parseInt(seg[0]);
                hMap.put(index, color);
            }
        }
        catch (Exception exc)
        {
            ErrorLogger.LogException(_className ,"getMSRSegmentColors",
                    new RendererException("Failed inside getMSRSegmentColors", exc));
        }
        return hMap;
    }
    public static HashMap getMSRSegmentColorStrings(TGLight tg)
    {   
        HashMap hMap=null;
        try
        {
            int linetype = tg.get_LineType();
            switch (linetype) {
                case 25221000:
                case 25222000:
                case 22121000:
                    if (tg.get_H() == null || tg.get_H().isEmpty())
                        return null;
                    hMap = new java.util.HashMap();
                    break;
                default:
                    return null;
            }
            String[] colorStrs = tg.get_H().split(",");
            int j = 0;
            int numSegs = colorStrs.length;
            String segPlusColor = "";
            String[] seg = null;
            //Color color = null;
            int index = -1;
            for (j = 0; j < numSegs; j++) {
                segPlusColor = colorStrs[j];
                if (!segPlusColor.contains(":"))
                    continue;
                seg = segPlusColor.split(":");
                //color = armyc2.c2sd.renderer.utilities.SymbolUtilities.getColorFromHexString(seg[1]);
                index = Integer.parseInt(seg[0]);
                //hMap.put(new Integer(index), color);
                hMap.put(new Integer(index), seg[1]);
            }            
        }
        catch (Exception exc)
        {
            ErrorLogger.LogException(_className ,"getMSRSegmentColorStrings",
                    new RendererException("Failed inside getMSRSegmentColorStrings", exc));
        }
        return hMap;
    }
    /**
     * @deprecated
     * tg.H must be revised for clipped MSR, ASR and Boundary
     * This function is called after the pixels were clipped
     * @param originalPixels the tactical graphic pixels before clipping
     * @param tg 
     */
    public static void reviseHModifier(ArrayList<POINT2>originalPixels, 
            TGLight tg)
    {
        try
        {
            //only revise tg.H if it is not null or empty
            //and the linetype is bounday, MSR, or ASR
            if(tg.get_H()==null || tg.get_H().isEmpty())
                return;
            int linetype=tg.get_LineType();
            switch(linetype)
            {
                case TacticalLines.ASR:
                case TacticalLines.MSR:
                case TacticalLines.BOUNDARY:
                    break;
                default:
                    return;
            }
            int j=0,k=0;
            //Line2D line=new Line2D.Double();
            
            //get the first common point between the original points and tg.Pixels
            //if it is n then n segments will have been dropped at the front end of
            //the clipped array (from the original pixels) so then we would want to
            //set the start index to n for the loop through the original points
            int n=-1; 
            boolean foundPt=false;
            for(j=0;j<originalPixels.size();j++)
            {
                for(k=0;k<tg.Pixels.size();k++)
                {
                    if(originalPixels.get(j).x==tg.Pixels.get(k).x && originalPixels.get(j).y==tg.Pixels.get(k).y)
                    {
                        n=j;
                        foundPt=true;
                        break;
                    }
                }
                if(foundPt)
                    break;
            }
            HashMap<Integer,Color> hmap=getMSRSegmentColors(tg);
            //use a 2nd hashmap to store the revised segment numbers, and exisitng Colors
            HashMap<Integer,Color> hmap2=new HashMap<Integer,Color>();
            POINT2 segPt0=null,segPt1=null; //the original segments
            POINT2 pt0=null,pt1=null;   //the clipped segments
            Color color=null;
            if(n<1)
                n=1;
            for(Integer key : hmap.keySet()) //keys can begin at 0
            {
                if(key<n-1)
                    continue;
                if(key+1>originalPixels.size()-1)
                    break;
                color=hmap.get(key);
                segPt0=originalPixels.get(key);
                segPt1=originalPixels.get(key+1);
                
                for(j=0;j<tg.Pixels.size()-1;j++)
                {
                    pt0=tg.Pixels.get(j);//clipped pixels
                    pt1=tg.Pixels.get(j+1);
                    if(segPt0.x==pt0.x && segPt0.y==pt0.y)
                    {
                        hmap2.put(j, color);
                        break;
                    }
                    else if(segPt1.x==pt1.x && segPt1.y==pt1.y)
                    {
                        hmap2.put(j, color);
                        break;
                    }
                    else
                    {
                        if(pt0.x==segPt1.x && pt0.y==segPt1.y)
                            continue;
                        if(pt1.x==segPt0.x && pt1.y==segPt0.y)
                            continue;
                        else    
                        {       
                            //if the original segment straddles or clips the clipping area
                            //then the original segment will contain the clipped segment
                            double dist0=lineutility.CalcDistanceToLineDouble(segPt0, segPt1, pt0);
                            double dist1=lineutility.CalcDistanceToLineDouble(segPt0, segPt1, pt1);
                            Line2D lineOrigPts=new Line2D.Double(segPt0.x,segPt0.y, segPt1.x,segPt1.y);
                            Rectangle2D rectOrigPts=lineOrigPts.getBounds2D();
                            Line2D lineClipPts=new Line2D.Double(pt0.x,pt0.y, pt1.x, pt1.y);
                            Rectangle2D rectClipPts=lineClipPts.getBounds2D();
                            //test if the lines coincide and the clipped segment is within the original segment
                            if(dist0<1 && dist1<1 && rectOrigPts.contains(rectClipPts))
                            {
                                hmap2.put(j, color);                                
                            }
                        }
                    }
                }
            }        
            if(hmap2.isEmpty())
            {
                tg.set_H(null);
                return;
            }
           
            String h="",temp="";
            for(Integer key : hmap2.keySet()) 
            {
                color=hmap2.get(key);
                temp=Integer.toHexString(color.getRGB());
                h+=key.toString()+":"+temp+",";
            }
            h=h.substring(0, h.length()-1);
            tg.set_H(h);
        }
        catch (Exception exc) {
            ErrorLogger.LogException(_className, "reviseHModifer",
                    new RendererException("Failed inside reviseHModifier", exc));
        }
    }
    /**
     * Interpolate pixels for lines with points too close together.
     * Drops successive points until the next point is at least 10 pixels from the preceding point
     * @param tg 
     */
    public static void InterpolatePixels(TGLight tg)
    {
        try
        {
            if(tg.get_UseLineInterpolation()==false)
                return;
            
            int linetype=tg.get_LineType();
            double glyphSize=10;
            switch(linetype)
            {
                case TacticalLines.ATDITCH:
                case TacticalLines.ATDITCHC:
                    glyphSize=25;
                    break;
                case TacticalLines.ATDITCHM:
                    glyphSize=50;
                    break;
                case TacticalLines.DMAF:
                    glyphSize=20;
                    break;
                case TacticalLines.FLOT:
                case TacticalLines.LC:
                case TacticalLines.FORT:
                case TacticalLines.FORTL:
                case TacticalLines.ENCIRCLE:
                case TacticalLines.BELT:
                case TacticalLines.ZONE:
                case TacticalLines.OBSFAREA:
                case TacticalLines.OBSAREA:
                case TacticalLines.DOUBLEA:
                case TacticalLines.LWFENCE:
                case TacticalLines.HWFENCE:
                case TacticalLines.BBS_LINE:
                case TacticalLines.SINGLEC:
                case TacticalLines.DOUBLEC:
                case TacticalLines.TRIPLE:
//                case TacticalLines.ATDITCH:
//                case TacticalLines.ATDITCHC:
//                case TacticalLines.ATDITCHM:
                case TacticalLines.STRONG:
                    glyphSize=30;
                    break;
//                case TacticalLines.PLD:
//                case TacticalLines.CFL:
//                    glyphSize=10;
//                    break;
                case TacticalLines.UNSP:
                case TacticalLines.LINE:
                case TacticalLines.ATWALL:
                case TacticalLines.SFENCE:
                    glyphSize=40;
                    break;
                case TacticalLines.DFENCE:
                    glyphSize=50;
                    break;
                default:
                    return;
            }
            HashMap hmapPixels=new HashMap<Integer,POINT2>();
            HashMap hmapGeo=new HashMap<Integer,POINT2>();
            int j=0,currentIndex=0;
            double dist=0,dist2=0;
            double direction1=0,direction2=0,delta=0;
            POINT2 pt0=null,pt1=null,pt2=null;
            for(j=0;j<tg.Pixels.size();j++)
            {
                if(j==0)
                {
                    hmapPixels.put(j, tg.Pixels.get(j));
                    hmapGeo.put(j, tg.LatLongs.get(j));
                    currentIndex=0;
                }
                else if(j==tg.Pixels.size()-1)
                {
                    hmapPixels.put(j, tg.Pixels.get(j));
                    hmapGeo.put(j, tg.LatLongs.get(j));                    
                }
                else
                {
                    dist=lineutility.CalcDistanceDouble(tg.Pixels.get(currentIndex), tg.Pixels.get(j));
                    dist2=lineutility.CalcDistanceDouble(tg.Pixels.get(j), tg.Pixels.get(j+1));
                    
                    //change of direction test 2-28-13
                    pt0=tg.Pixels.get(currentIndex);
                    pt1=tg.Pixels.get(j);
                    pt2=tg.Pixels.get(j+1);
                    direction1=(180/Math.PI)*Math.atan((pt0.y-pt1.y)/(pt0.x-pt1.x));
                    direction2=(180/Math.PI)*Math.atan((pt1.y-pt2.y)/(pt1.x-pt2.x));
                    delta=Math.abs(direction1-direction2);
                    if(dist>glyphSize || dist2>glyphSize || delta>20)
                    {
                        hmapPixels.put(j, tg.Pixels.get(j));
                        hmapGeo.put(j, tg.LatLongs.get(j));
                        currentIndex=j;
                    }
                }
            }
            ArrayList<POINT2>pixels=new ArrayList();
            ArrayList<POINT2>geo=new ArrayList();
            for(j=0;j<tg.Pixels.size();j++)
            {
                if(hmapPixels.containsKey(j))
                    pixels.add((POINT2)hmapPixels.get(j));
                if(hmapGeo.containsKey(j))
                    geo.add((POINT2)hmapGeo.get(j));
            }
            switch(linetype)
            {
                case TacticalLines.DMAF:
                case TacticalLines.FORT:
                case TacticalLines.ENCIRCLE:
                case TacticalLines.BELT:
                case TacticalLines.ZONE:
                case TacticalLines.OBSFAREA:
                case TacticalLines.OBSAREA:
                case TacticalLines.STRONG:
                    if(pixels.size()==2)
                    {
                        for(j=0;j<tg.Pixels.size();j++)
                        {
                            if(hmapPixels.containsKey(j)==false && hmapGeo.containsKey(j)==false)
                            {
                                pixels.add(j,tg.Pixels.get(j));
                                geo.add(j,tg.LatLongs.get(j));
                                break;
                            }
                        }                        
                    }
                    break;
                default:
                    break;
            }            
            tg.Pixels=pixels;
            tg.LatLongs=geo;
        }
        catch (Exception exc) {
            ErrorLogger.LogException(_className, "InterpolatePixels",
                    new RendererException("Failed inside InterpolatePixels", exc));
        }
    }
    /**
     * construct a line segment outside the polygon corresponding to some index
     * @param tg
     * @param index
     * @param dist
     * @return 
     */
    protected static Line2D getExtendedLine(TGLight tg,
            int index,
            double dist)
    {
        Line2D line=null;
        try
        {
            Polygon polygon=new Polygon();
            int j=0;
            for(j=0;j<tg.Pixels.size();j++)
            {
                polygon.addPoint((int)tg.Pixels.get(j).x, (int)tg.Pixels.get(j).y);
            }
            POINT2 pt0=null; 
            POINT2 pt1=null; 
            if(tg.Pixels.size()>3)
            {
                pt0=tg.Pixels.get(index);
                pt1=tg.Pixels.get(index+1);
            }
            else
            {
                pt0=tg.Pixels.get(1);
                pt1=tg.Pixels.get(2);                
            }
            
            POINT2 ptExtend=null;
            int extend=-1;
            POINT2 midPt=lineutility.MidPointDouble(pt0, pt1,0);
            double slope=Math.abs(pt1.y-pt0.y)/(pt1.x-pt0.x);
            if(slope<=1)
            {
                ptExtend=lineutility.ExtendDirectedLine(pt0, pt1, midPt, lineutility.extend_above, 2);
                if(polygon.contains(ptExtend.x,ptExtend.y))
                    extend=lineutility.extend_below;
                else
                    extend=lineutility.extend_above;
            }
            else
            {
                ptExtend=lineutility.ExtendDirectedLine(pt0, pt1, midPt, lineutility.extend_left, 2);
                if(polygon.contains(ptExtend.x,ptExtend.y))
                    extend=lineutility.extend_right;
                else
                    extend=lineutility.extend_left;
                
            }
            POINT2 pt3=null;
            POINT2 pt4=null;
            pt3=lineutility.ExtendDirectedLine(pt0, pt1, pt0, extend, dist);
            pt4=lineutility.ExtendDirectedLine(pt0, pt1, pt1, extend, dist);
            line=new Line2D.Double(pt3.x, pt3.y, pt4.x, pt4.y);
        }
        catch (Exception exc) {            
            ErrorLogger.LogException(_className, "getExtendedLine",
                    new RendererException("Failed inside getExtendedLine", exc));
        }
        return line;
    }

}//end clsUtility
