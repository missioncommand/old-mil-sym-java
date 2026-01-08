/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaTacticalRenderer;

import JavaLineArray.TacticalLines;
import JavaLineArray.arraysupport;
import JavaLineArray.lineutility;
import JavaLineArray.CELineArray;
import JavaLineArray.POINT2;
import java.util.ArrayList;
import JavaLineArray.Shape2;
import java.awt.geom.GeneralPath;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.InputStream;
import ArmyC2.C2SD.Utilities.ErrorLogger;
import ArmyC2.C2SD.Utilities.RendererException;
import ArmyC2.C2SD.Utilities.ShapeInfo;

/**
 * Class to calculate the points for the Weather symbols
 * @author Michael Deutch
 */
public final class clsMETOC {
    private static final String _className = "clsMETOC";    
    /**
     * @param symbolID Mil-Standard 2525 15 character code
     * @return the line type as an integer if it is a weather symbol, else return -1
     */    
    public static int IsWeather(String symbolID) {
        //the MeTOCs
        try
        {            
            //added section for revD
            if(symbolID != null && symbolID.length()>15)
            {
                //test for hold,brdghd
                //String setA=Modifier2.getSetA(symbolID);
                String setA=symbolID.substring(0,10);
                //String setB=Modifier2.getSetB(symbolID);
                String setB=symbolID.substring(10);
                //String entityCode=Modifier2.getCode(setB);
                String entityCode=setB.substring(0,6);
                int nEntityCode=Integer.parseInt(entityCode);
                //String symbolSet=Modifier2.getSymbolSet(setA);
                String symbolSet=setA.substring(4,6);
                int nSymbolSet=Integer.parseInt(symbolSet);
                switch(nSymbolSet)
                {
                    case 25:    //look for holding line, bridgehead
                        if(nEntityCode==141400)
                            return TacticalLines.BRDGHD;
                        else if(nEntityCode==141500)
                            return TacticalLines.HOLD;
                        break;
                    case 45:
                    case 46:
                        return getWeatherLinetype(symbolSet,entityCode);
                }
            }
            //end section
            if(symbolID==null)
                return -1;
            if(symbolID.equalsIgnoreCase("HOLD"))
                return TacticalLines.HOLD;
            if(symbolID.equalsIgnoreCase("BRDGHD"))
                return TacticalLines.BRDGHD;

            if (symbolID.equalsIgnoreCase("CF")) {
                return TacticalLines.CF;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_TYPE_C3")) {
                return TacticalLines.BOTTOM_TYPE_C3;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_TYPE_C2")) {
                return TacticalLines.BOTTOM_TYPE_C2;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_TYPE_C1")) {
                return TacticalLines.BOTTOM_TYPE_C1;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_TYPE_B3")) {
                return TacticalLines.BOTTOM_TYPE_B3;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_TYPE_B2")) {
                return TacticalLines.BOTTOM_TYPE_B2;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_TYPE_B1")) {
                return TacticalLines.BOTTOM_TYPE_B1;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_TYPE_A3")) {
                return TacticalLines.BOTTOM_TYPE_A3;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_TYPE_A2")) {
                return TacticalLines.BOTTOM_TYPE_A2;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_TYPE_A1")) {
                return TacticalLines.BOTTOM_TYPE_A1;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_CATEGORY_C")) {
                return TacticalLines.BOTTOM_CATEGORY_C;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_CATEGORY_B")) {
                return TacticalLines.BOTTOM_CATEGORY_B;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_CATEGORY_A")) {
                return TacticalLines.BOTTOM_CATEGORY_A;
            } else if (symbolID.equalsIgnoreCase("IMPACT_BURIAL_100")) {
                return TacticalLines.IMPACT_BURIAL_100;
            } else if (symbolID.equalsIgnoreCase("IMPACT_BURIAL_75")) {
                return TacticalLines.IMPACT_BURIAL_75;
            } else if (symbolID.equalsIgnoreCase("IMPACT_BURIAL_20")) {
                return TacticalLines.IMPACT_BURIAL_20;
            } else if (symbolID.equalsIgnoreCase("IMPACT_BURIAL_10")) {
                return TacticalLines.IMPACT_BURIAL_10;
            } else if (symbolID.equalsIgnoreCase("IMPACT_BURIAL_0")) {
                return TacticalLines.IMPACT_BURIAL_0;
            } else if (symbolID.equalsIgnoreCase("CLUTTER_HIGH")) {
                return TacticalLines.CLUTTER_HIGH;
            } else if (symbolID.equalsIgnoreCase("CLUTTER_MEDIUM")) {
                return TacticalLines.CLUTTER_MEDIUM;
            } else if (symbolID.equalsIgnoreCase("CLUTTER_LOW")) {
                return TacticalLines.CLUTTER_LOW;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_ROUGHNESS_ROUGH")) {
                return TacticalLines.BOTTOM_ROUGHNESS_ROUGH;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_ROUGHNESS_MODERATE")) {
                return TacticalLines.BOTTOM_ROUGHNESS_MODERATE;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_ROUGHNESS_SMOOTH")) {
                return TacticalLines.BOTTOM_ROUGHNESS_SMOOTH;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_SEDIMENTS_LAND")) {
                return TacticalLines.BOTTOM_SEDIMENTS_LAND;
            } else if (symbolID.equalsIgnoreCase("BOTTOM_SEDIMENTS_NO_DATA")) {
                return TacticalLines.BOTTOM_SEDIMENTS_NO_DATA;
            } else if (symbolID.equalsIgnoreCase("SAND_AND_SHELLS")) {
                return TacticalLines.SAND_AND_SHELLS;
            } else if (symbolID.equalsIgnoreCase("PEBBLES")) {
                return TacticalLines.PEBBLES;
            } else if (symbolID.equalsIgnoreCase("OYSTER_SHELLS")) {
                return TacticalLines.OYSTER_SHELLS;
            } else if (symbolID.equalsIgnoreCase("BOULDERS")) {
                return TacticalLines.BOULDERS;
            } else if (symbolID.equalsIgnoreCase("COARSE_SILT")) {
                return TacticalLines.COARSE_SILT;
            } else if (symbolID.equalsIgnoreCase("MEDIUM_SILT")) {
                return TacticalLines.MEDIUM_SILT;
            } else if (symbolID.equalsIgnoreCase("FINE_SILT")) {
                return TacticalLines.FINE_SILT;
            } else if (symbolID.equalsIgnoreCase("VERY_FINE_SILT")) {
                return TacticalLines.VERY_FINE_SILT;
            } else if (symbolID.equalsIgnoreCase("VERY_FINE_SAND")) {
                return TacticalLines.VERY_FINE_SAND;
            } else if (symbolID.equalsIgnoreCase("FINE_SAND")) {
                return TacticalLines.FINE_SAND;
            } else if (symbolID.equalsIgnoreCase("MEDIUM_SAND")) {
                return TacticalLines.MEDIUM_SAND;
            } else if (symbolID.equalsIgnoreCase("COARSE_SAND")) {
                return TacticalLines.COARSE_SAND;
            } else if (symbolID.equalsIgnoreCase("VERY_COARSE_SAND")) {
                return TacticalLines.VERY_COARSE_SAND;
            } else if (symbolID.equalsIgnoreCase("CLAY")) {
                return TacticalLines.CLAY;
            } else if (symbolID.equalsIgnoreCase("SOLID_ROCK")) {
                return TacticalLines.SOLID_ROCK;
            } else if (symbolID.equalsIgnoreCase("VDR_LEVEL_12")) {
                return TacticalLines.VDR_LEVEL_12;
            } else if (symbolID.equalsIgnoreCase("VDR_LEVEL_23")) {
                return TacticalLines.VDR_LEVEL_23;
            } else if (symbolID.equalsIgnoreCase("VDR_LEVEL_34")) {
                return TacticalLines.VDR_LEVEL_34;
            } else if (symbolID.equalsIgnoreCase("VDR_LEVEL_45")) {
                return TacticalLines.VDR_LEVEL_45;
            } else if (symbolID.equalsIgnoreCase("VDR_LEVEL_56")) {
                return TacticalLines.VDR_LEVEL_56;
            } else if (symbolID.equalsIgnoreCase("VDR_LEVEL_67")) {
                return TacticalLines.VDR_LEVEL_67;
            } else if (symbolID.equalsIgnoreCase("VDR_LEVEL_78")) {
                return TacticalLines.VDR_LEVEL_78;
            } else if (symbolID.equalsIgnoreCase("VDR_LEVEL_89")) {
                return TacticalLines.VDR_LEVEL_89;
            } else if (symbolID.equalsIgnoreCase("VDR_LEVEL_910")) {
                return TacticalLines.VDR_LEVEL_910;
            } else if (symbolID.equalsIgnoreCase("CANAL")) {
                return TacticalLines.CANAL;
            } else if (symbolID.equalsIgnoreCase("SUBMERGED_CRIB")) {
                return TacticalLines.SUBMERGED_CRIB;
            } else if (symbolID.equalsIgnoreCase("OPERATOR_DEFINED")) {
                return TacticalLines.OPERATOR_DEFINED;
            } else if (symbolID.equalsIgnoreCase("MARITIME_AREA")) {
                return TacticalLines.MARITIME_AREA;
            } else if (symbolID.equalsIgnoreCase("MARITIME_LIMIT")) {
                return TacticalLines.MARITIME_LIMIT;
            } else if (symbolID.equalsIgnoreCase("BEACH_SLOPE_GENTLE")) {
                return TacticalLines.BEACH_SLOPE_GENTLE;
            } else if (symbolID.equalsIgnoreCase("BEACH_SLOPE_FLAT")) {
                return TacticalLines.BEACH_SLOPE_FLAT;
            } else if (symbolID.equalsIgnoreCase("DISCOLORED_WATER")) {
                return TacticalLines.DISCOLORED_WATER;
            } else if (symbolID.equalsIgnoreCase("BREAKERS")) {
                return TacticalLines.BREAKERS;
            } else if (symbolID.equalsIgnoreCase("UNDERWATER_HAZARD")) {
                return TacticalLines.UNDERWATER_HAZARD;
            } else if (symbolID.equalsIgnoreCase("PERCHES")) {
                return TacticalLines.PERCHES;
            } else if (symbolID.equalsIgnoreCase("LOADING_FACILITY_LINE")) {
                return TacticalLines.LOADING_FACILITY_LINE;
            } else if (symbolID.equalsIgnoreCase("LOADING_FACILITY_AREA")) {
                return TacticalLines.LOADING_FACILITY_AREA;
            } else if (symbolID.equalsIgnoreCase("DRYDOCK")) {
                return TacticalLines.DRYDOCK;
            } else if (symbolID.equalsIgnoreCase("FORESHORE_LINE")) {
                return TacticalLines.FORESHORE_LINE;
            } else if (symbolID.equalsIgnoreCase("FORESHORE_AREA")) {
                return TacticalLines.FORESHORE_AREA;
            } else if (symbolID.equalsIgnoreCase("ICE_DRIFT")) {
                return TacticalLines.ICE_DRIFT;
            } else if (symbolID.equalsIgnoreCase("REEF")) {
                return TacticalLines.REEF;
            } else if (symbolID.equalsIgnoreCase("RESTRICTED_AREA")) {
                return TacticalLines.RESTRICTED_AREA;
            } else if (symbolID.equalsIgnoreCase("TRAINING_AREA")) {
                return TacticalLines.TRAINING_AREA;
            } else if (symbolID.equalsIgnoreCase("PIPE")) {
                return TacticalLines.PIPE;
            } else if (symbolID.equalsIgnoreCase("ANCHORAGE_LINE")) {
                return TacticalLines.ANCHORAGE_LINE;
            } else if (symbolID.equalsIgnoreCase("ANCHORAGE_AREA")) {
                return TacticalLines.ANCHORAGE_AREA;
            } else if (symbolID.equalsIgnoreCase("BEACH_SLOPE_STEEP")) {
                return TacticalLines.BEACH_SLOPE_STEEP;
            } else if (symbolID.equalsIgnoreCase("BEACH_SLOPE_MODERATE")) {
                return TacticalLines.BEACH_SLOPE_MODERATE;
            } else if (symbolID.equalsIgnoreCase("KELP")) {
                return TacticalLines.KELP;
            } else if (symbolID.equalsIgnoreCase("FOUL_GROUND")) {
                return TacticalLines.FOUL_GROUND;
            } else if (symbolID.equalsIgnoreCase("OIL_RIG_FIELD")) {
                return TacticalLines.OIL_RIG_FIELD;
            } else if (symbolID.equalsIgnoreCase("SWEPT_AREA")) {
                return TacticalLines.SWEPT_AREA;
            } else if (symbolID.equalsIgnoreCase("WEIRS")) {
                return TacticalLines.WEIRS;
            } else if (symbolID.equalsIgnoreCase("BEACH")) {
                return TacticalLines.BEACH;
            } else if (symbolID.equalsIgnoreCase("WATER")) {
                return TacticalLines.WATER;
            } else if (symbolID.equalsIgnoreCase("ISLAND")) {
                return TacticalLines.ISLAND;
            } else if (symbolID.equalsIgnoreCase("DEPTH_AREA")) {
                return TacticalLines.DEPTH_AREA;
            } else if (symbolID.equalsIgnoreCase("LRO")) {
                return TacticalLines.LRO;
            } else if (symbolID.equalsIgnoreCase("LVO")) {
                return TacticalLines.LVO;
            } else if (symbolID.equalsIgnoreCase("UNDERCAST")) {
                return TacticalLines.UNDERCAST;
            } else if (symbolID.equalsIgnoreCase("SAND")) {
                return TacticalLines.SAND;
            } else if (symbolID.equalsIgnoreCase("OPERATOR_FREEFORM")) {
                return TacticalLines.OPERATOR_FREEFORM;
            } else if (symbolID.equalsIgnoreCase("FREEFORM")) {
                return TacticalLines.FREEFORM;
            } else if (symbolID.equalsIgnoreCase("FOG")) {
                return TacticalLines.FOG;
            } else if (symbolID.equalsIgnoreCase("THUNDERSTORMS")) {
                return TacticalLines.THUNDERSTORMS;
            } else if (symbolID.equalsIgnoreCase("FROZEN")) {
                return TacticalLines.FROZEN;
            } else if (symbolID.equalsIgnoreCase("ICING")) {
                return TacticalLines.ICING;
            } else if (symbolID.equalsIgnoreCase("NON_CONVECTIVE")) {
                return TacticalLines.NON_CONVECTIVE;
            } else if (symbolID.equalsIgnoreCase("CONVECTIVE")) {
                return TacticalLines.CONVECTIVE;
            } else if (symbolID.equalsIgnoreCase("MVFR")) {
                return TacticalLines.MVFR;
            } else if (symbolID.equalsIgnoreCase("TURBULENCE")) {
                return TacticalLines.TURBULENCE;
            } else if (symbolID.equalsIgnoreCase("IFR")) {
                return TacticalLines.IFR;
            } else if (symbolID.equalsIgnoreCase("ITD")) {
                return TacticalLines.ITD;
            } else if (symbolID.equalsIgnoreCase("CONVERGANCE")) {
                return TacticalLines.CONVERGANCE;
            } else if (symbolID.equalsIgnoreCase("ITC")) {
                return TacticalLines.ITC;
            } else if (symbolID.equalsIgnoreCase("SEVERE")) {
                return TacticalLines.SQUALL;
            } else if (symbolID.equalsIgnoreCase("SQUALL")) {
                return TacticalLines.SQUALL;
            } else if (symbolID.equalsIgnoreCase("RIDGE")) {
                return TacticalLines.RIDGE;
            } else if (symbolID.equalsIgnoreCase("TROUGH")) {
                return TacticalLines.TROUGH;                
            } 
            else if (symbolID.equalsIgnoreCase("TROUGH_A")) {
                return TacticalLines.TROUGH;                
            } 
            else if (symbolID.equalsIgnoreCase("INSTABILITY")) {
                return TacticalLines.INSTABILITY;
            } else if (symbolID.equalsIgnoreCase("SHEAR")) {
                return TacticalLines.SHEAR;
            } else if (symbolID.equalsIgnoreCase("UCF")) {
                return TacticalLines.UCF;
            } else if (symbolID.equalsIgnoreCase("CFG")) {
                return TacticalLines.CFG;
            } else if (symbolID.equalsIgnoreCase("CFY")) {
                return TacticalLines.CFY;
            } else if (symbolID.equalsIgnoreCase("UWF")) {
                return TacticalLines.UWF;
            } else if (symbolID.equalsIgnoreCase("WF")) {
                return TacticalLines.WF;
            } else if (symbolID.equalsIgnoreCase("WFG")) {
                return TacticalLines.WFG;
            } else if (symbolID.equalsIgnoreCase("WFY")) {
                return TacticalLines.WFY;
            } else if (symbolID.equalsIgnoreCase("OCCLUDED")) {
                return TacticalLines.OCCLUDED;
            } else if (symbolID.equalsIgnoreCase("UOF")) {
                return TacticalLines.UOF;
            } else if (symbolID.equalsIgnoreCase("OFY")) {
                return TacticalLines.OFY;
            } else if (symbolID.equalsIgnoreCase("SF")) {
                return TacticalLines.SF;
            } else if (symbolID.equalsIgnoreCase("USF")) {
                return TacticalLines.USF;
            } else if (symbolID.equalsIgnoreCase("SFG")) {
                return TacticalLines.SFG;
            } else if (symbolID.equalsIgnoreCase("SFY")) {
                return TacticalLines.SFY;
            } else if (symbolID.equalsIgnoreCase("ISOBAR")) {
                return TacticalLines.ISOBAR;
            } else if (symbolID.equalsIgnoreCase("UPPER_AIR")) {
                return TacticalLines.UPPER_AIR;
            } else if (symbolID.equalsIgnoreCase("LEADING_LINE")) {
                return TacticalLines.LEADING_LINE;
            } else if (symbolID.equalsIgnoreCase("ISODROSOTHERM")) {
                return TacticalLines.ISODROSOTHERM;
            } else if (symbolID.equalsIgnoreCase("CRACKS")) {
                return TacticalLines.CRACKS;
            } else if (symbolID.equalsIgnoreCase("ICE_EDGE")) {
                return TacticalLines.ICE_EDGE;
            } else if (symbolID.equalsIgnoreCase("DEPTH_CURVE")) {
                return TacticalLines.DEPTH_CURVE;
            } else if (symbolID.equalsIgnoreCase("DEPTH_CONTOUR")) {
                return TacticalLines.DEPTH_CONTOUR;
            } else if (symbolID.equalsIgnoreCase("COASTLINE")) {
                return TacticalLines.COASTLINE;
            } else if (symbolID.equalsIgnoreCase("PIER")) {
                return TacticalLines.PIER;
            } else if (symbolID.equalsIgnoreCase("RAMP_ABOVE_WATER")) {
                return TacticalLines.RAMP_ABOVE_WATER;
            } else if (symbolID.equalsIgnoreCase("JETTY_ABOVE_WATER")) {
                return TacticalLines.JETTY_ABOVE_WATER;
            } else if (symbolID.equalsIgnoreCase("SEAWALL")) {
                return TacticalLines.SEAWALL;
            } else if (symbolID.equalsIgnoreCase("CABLE")) {
                return TacticalLines.CABLE;
            } else if (symbolID.equalsIgnoreCase("ICE_OPENINGS_LEAD")) {
                return TacticalLines.ICE_OPENINGS_LEAD;
            } else if (symbolID.equalsIgnoreCase("ISOTACH")) {
                return TacticalLines.ISOTACH;
            } else if (symbolID.equalsIgnoreCase("ISOTHERM")) {
                return TacticalLines.ISOTHERM;
            } else if (symbolID.equalsIgnoreCase("ISOPLETHS")) {
                return TacticalLines.ISOPLETHS;
            } else if (symbolID.equalsIgnoreCase("ESTIMATED_ICE_EDGE")) {
                return TacticalLines.ESTIMATED_ICE_EDGE;
            } else if (symbolID.equalsIgnoreCase("RAMP_BELOW_WATER")) {
                return TacticalLines.RAMP_BELOW_WATER;
            } else if (symbolID.equalsIgnoreCase("JETTY_BELOW_WATER")) {
                return TacticalLines.JETTY_BELOW_WATER;
            } else if (symbolID.equalsIgnoreCase("ICE_OPENINGS_FROZEN")) {
                return TacticalLines.ICE_OPENINGS_FROZEN;
            } else if (symbolID.equalsIgnoreCase("ICE_EDGE_RADAR")) {
                return TacticalLines.ICE_EDGE_RADAR;
            } else if (symbolID.equalsIgnoreCase("CRACKS_SPECIFIC_LOCATION")) {
                return TacticalLines.CRACKS_SPECIFIC_LOCATION;
            } else if (symbolID.equalsIgnoreCase("EBB_TIDE")) {
                return TacticalLines.EBB_TIDE;
            } else if (symbolID.equalsIgnoreCase("FLOOD_TIDE")) {
                return TacticalLines.FLOOD_TIDE;
            } else if (symbolID.equalsIgnoreCase("JET")) {
                return TacticalLines.JET;
            } else if (symbolID.equalsIgnoreCase("STREAM")) {
                return TacticalLines.STREAM;
            }

            if (symbolID.length() != 15) {
                return -1;
            }

            String strLine = symbolID;
            String str1, str2, str3, str4, str5, str6, str7, str10;
            String c0 = strLine.substring(0, 1);
            String c1 = strLine.substring(1, 2);
            String c2 = strLine.substring(2, 3);
            //int bolUseEllipticArc=0;

            str1 = strLine.substring(4, 5);//was(4,1)
            str2 = strLine.substring(4, 6);//was(4,2)
            str3 = strLine.substring(4, 7);//was(4,3)
            str4 = strLine.substring(4, 8);//was(4,4)
            str5 = strLine.substring(4, 9);//was(4,5)
            str6 = strLine.substring(3, 9);//was(3,6)
            str7 = strLine.substring(3, 10);//was(3,7)
            str10 = strLine.substring(3, 13);//was(3,10)

            //METOC
            //HOLD  handled as METOC
            if (str3.equals("SLH") && c0.equals("G") && c2.equals("G"))
                return TacticalLines.HOLD;
            //BRDGHD    handled as METOC
            if (str3.equals("SLB") && c0.equals("G") && c2.equals("G"))
                return TacticalLines.BRDGHD;
            
            if(c0.equalsIgnoreCase("W")==false)
                return -1;

            if (c0.equals("W") && c1.equals("A")) {
                if (str7.equals("DPXSQ--")) {
                    return TacticalLines.SQUALL;
                }
                if (str7.equals("DPFC---")) {
                    return TacticalLines.CF;
                }
                if (str7.equals("DPFC-FG")) {
                    return TacticalLines.CFG;
                }
                if (str7.equals("DPFC-FY")) {
                    return TacticalLines.CFY;
                }
                if (str7.equals("DPFW-FG")) {
                    return TacticalLines.WFG;
                }
                if (str7.equals("DPFW-FY")) {
                    return TacticalLines.WFY;
                }
                if (str7.equals("DPFOU--")) {
                    return TacticalLines.UOF;
                }
                if (str7.equals("DPFO-FY")) {
                    return TacticalLines.OFY;
                }
                if (str7.equals("DPFSU--")) {
                    return TacticalLines.USF;
                }
                if (str7.equals("DPFS-FG")) {
                    return TacticalLines.SFG;
                }
                if (str7.equals("DPFS-FY")) {
                    return TacticalLines.SFY;
                }
                if (str7.equals("DPXIL--")) {
                    return TacticalLines.INSTABILITY;
                }
                if (str7.equals("DPXSH--")) {
                    return TacticalLines.SHEAR;
                }
                if (str7.equals("DPXITCZ")) {
                    return TacticalLines.ITC;
                }
                if (str7.equals("DPXCV--")) {
                    return TacticalLines.CONVERGANCE;
                }
                if (str7.equals("DPXITD-")) {
                    return TacticalLines.ITD;
                }
                if (str7.equals("DWJ----")) {
                    return TacticalLines.JET;
                }
                if (str7.equals("DWS----")) {
                    return TacticalLines.STREAM;
                }
                if (str7.equals("DBAIF--")) {
                    return TacticalLines.IFR;
                }
                if (str7.equals("DBAMV--")) {
                    return TacticalLines.MVFR;
                }
                if (str7.equals("DBATB--")) {
                    return TacticalLines.TURBULENCE;
                }
                if (str7.equals("DBAI---")) {
                    return TacticalLines.ICING;
                }
                if (str7.equals("DBALPNC")) {
                    return TacticalLines.NON_CONVECTIVE;
                }
                if (str7.equals("DBALPC-")) {
                    return TacticalLines.CONVECTIVE;
                }
                if (str7.equals("DBAFP--")) {
                    return TacticalLines.FROZEN;
                }
                if (str7.equals("DBAT---")) {
                    return TacticalLines.THUNDERSTORMS;
                }
                if (str7.equals("DBAFG--")) {
                    return TacticalLines.FOG;
                }
                if (str7.equals("DBAD---")) {
                    return TacticalLines.SAND;
                }
                if (str7.equals("DBAFF--")) {
                    return TacticalLines.FREEFORM;
                }
                if (str7.equals("DIPIB--")) {
                    return TacticalLines.ISOBAR;
                }
                if (str7.equals("DIPCO--")) {
                    return TacticalLines.UPPER_AIR;
                }
                if (str7.equals("DIPIS--")) {
                    return TacticalLines.ISOTHERM;
                }
                if (str7.equals("DIPIT--")) {
                    return TacticalLines.ISOTACH;
                }
                if (str7.equals("DIPID--")) {
                    return TacticalLines.ISODROSOTHERM;
                }
                if (str7.equals("DIPTH--")) {
                    return TacticalLines.ISOPLETHS;
                }
                if (str7.equals("DIPFF--")) {
                    return TacticalLines.OPERATOR_FREEFORM;
                }


                //if (strncmp(str,"PXR",3).equals(0)
                if (str3.equals("PXR")) {
                    return TacticalLines.RIDGE;
                }
                //if (strncmp(str,"PXS",3).equals(0)
                //if(str3.equals("PXS")
                //	return TacticalLines.SQUALL;
                //if (strncmp(str,"PXT",3).equals(0)
                if (str3.equals("PXT")) {
                    return TacticalLines.TROUGH;
                }
                //if (strncmp(str,"PFCU",4).equals(0)
                if (str4.equals("PFCU")) {
                    return TacticalLines.UCF;
                }
                //if (strncmp(str,"PFO",3).equals(0)
                if (str3.equals("PFO")) {
                    return TacticalLines.OCCLUDED;
                }
                //if (strncmp(str,"PFS",3).equals(0)
                if (str3.equals("PFS")) {
                    return TacticalLines.SF;
                }
                //if (strncmp(str,"PFWU",4).equals(0)
                if (str4.equals("PFWU")) {
                    return TacticalLines.UWF;
                }
                //if (strncmp(str,"PFW",3).equals(0)
                if (str3.equals("PFW")) {
                    return TacticalLines.WF;
                }
                //if (strncmp(str,"PFC",3).equals(0)
                if (str3.equals("PFC")) {
                    return TacticalLines.CF;
                }
            }
            if (c0.equals("W") && c1.equals("O")) {
                if (str10.equals("DHCF----L-")) {
                    return TacticalLines.FORESHORE_LINE;
                }
                if (str10.equals("DHCF-----A")) {
                    return TacticalLines.FORESHORE_AREA;
                }
                if (str10.equals("DHPBA---L-")) {
                    return TacticalLines.ANCHORAGE_LINE;
                }
                if (str10.equals("DHPBA----A")) {
                    return TacticalLines.ANCHORAGE_AREA;
                }
                if (str10.equals("DHPMO---L-")) {
                    return TacticalLines.LOADING_FACILITY_LINE;
                }
                if (str10.equals("DHPMO----A")) {
                    return TacticalLines.LOADING_FACILITY_AREA;
                }
            }
            if (c0.equals("W") && c1.equals("O")) {
                if (str7.equals("DIDID--")) {
                    return TacticalLines.ICE_DRIFT;
                }
                if (str7.equals("DILOV--")) {
                    return TacticalLines.LVO;
                }
                if (str7.equals("DILUC--")) {
                    return TacticalLines.UNDERCAST;
                }
                if (str7.equals("DILOR--")) {
                    return TacticalLines.LRO;
                }
                if (str7.equals("DILIEO-")) {
                    return TacticalLines.ICE_EDGE;
                }
                if (str7.equals("DILIEE-")) {
                    return TacticalLines.ESTIMATED_ICE_EDGE;
                }
                if (str7.equals("DILIER-")) {
                    return TacticalLines.ICE_EDGE_RADAR;
                }
                if (str7.equals("DIOC---")) {
                    return TacticalLines.CRACKS;
                }
                if (str7.equals("DIOCS--")) {
                    return TacticalLines.CRACKS_SPECIFIC_LOCATION;
                }
                if (str7.equals("DIOL---")) {
                    return TacticalLines.ICE_OPENINGS_LEAD;
                }
                if (str7.equals("DIOLF--")) {
                    return TacticalLines.ICE_OPENINGS_FROZEN;
                }
                if (str7.equals("DHDDL--")) {
                    return TacticalLines.DEPTH_CURVE;
                }
                if (str7.equals("DHDDC--")) {
                    return TacticalLines.DEPTH_CONTOUR;
                }
                if (str7.equals("DHDDA--")) {
                    return TacticalLines.DEPTH_AREA;
                }
                if (str7.equals("DHCC---")) {
                    return TacticalLines.COASTLINE;
                }
                if (str7.equals("DHCI---")) {
                    return TacticalLines.ISLAND;
                }
                if (str7.equals("DHCB---")) {
                    return TacticalLines.BEACH;
                }
                if (str7.equals("DHCW---")) {
                    return TacticalLines.WATER;
                }
                if (str7.equals("DHPBP--")) {
                    return TacticalLines.PIER;
                }
                if (str7.equals("-HPFF--")) {
                    return TacticalLines.WEIRS;
                }
                if (str7.equals("-HHDR--")) {
                    return TacticalLines.REEF;
                }
                if (str7.equals("DHPMD--")) {
                    return TacticalLines.DRYDOCK;
                }
                if (str7.equals("DHPMRA-")) {
                    return TacticalLines.RAMP_ABOVE_WATER;
                }
                if (str7.equals("DHPMRB-")) {
                    return TacticalLines.RAMP_BELOW_WATER;
                }
                if (str7.equals("DHPSPA-")) {
                    return TacticalLines.JETTY_ABOVE_WATER;
                }
                if (str7.equals("DHPSPB-")) {
                    return TacticalLines.JETTY_BELOW_WATER;
                }
                if (str7.equals("DHPSPS-")) {
                    return TacticalLines.SEAWALL;
                }
                if (str7.equals("DHABP--")) {
                    return TacticalLines.PERCHES;
                }
                if (str7.equals("DHALLA-")) {
                    return TacticalLines.LEADING_LINE;
                }
                if (str7.equals("DHHD---")) {
                    return TacticalLines.UNDERWATER_HAZARD;
                }
                if (str7.equals("DHHDF--")) {
                    return TacticalLines.FOUL_GROUND;
                }
                if (str7.equals("DHHDK--")) {
                    return TacticalLines.KELP;
                }
                if (str7.equals("DHHDB--")) {
                    return TacticalLines.BREAKERS;
                }
                if (str7.equals("DHHDD--")) {
                    return TacticalLines.DISCOLORED_WATER;
                }
                if (str7.equals("DTCCCFE")) {
                    return TacticalLines.EBB_TIDE;
                }
                if (str7.equals("DTCCCFF")) {
                    return TacticalLines.FLOOD_TIDE;
                }
                if (str7.equals("DL-RA--")) {
                    return TacticalLines.RESTRICTED_AREA;
                }
                if (str7.equals("DMPA---")) {
                    return TacticalLines.PIPE;
                }
                if (str7.equals("DL-TA--")) {
                    return TacticalLines.TRAINING_AREA;
                }
                if (str7.equals("DOBVA--")) {
                    return TacticalLines.VDR_LEVEL_12;
                }
                if (str7.equals("DOBVB--")) {
                    return TacticalLines.VDR_LEVEL_23;
                }
                if (str7.equals("DOBVC--")) {
                    return TacticalLines.VDR_LEVEL_34;
                }
                if (str7.equals("DOBVD--")) {
                    return TacticalLines.VDR_LEVEL_45;
                }
                if (str7.equals("DOBVE--")) {
                    return TacticalLines.VDR_LEVEL_56;
                }
                if (str7.equals("DOBVF--")) {
                    return TacticalLines.VDR_LEVEL_67;
                }
                if (str7.equals("DOBVG--")) {
                    return TacticalLines.VDR_LEVEL_78;
                }
                if (str7.equals("DOBVH--")) {
                    return TacticalLines.VDR_LEVEL_89;
                }
                if (str7.equals("DOBVI--")) {
                    return TacticalLines.VDR_LEVEL_910;
                }
                if (str7.equals("DBSF---")) {
                    return TacticalLines.BEACH_SLOPE_FLAT;
                }
                if (str7.equals("DBSG---")) {
                    return TacticalLines.BEACH_SLOPE_GENTLE;
                }
                if (str7.equals("DBSM---")) {
                    return TacticalLines.BEACH_SLOPE_MODERATE;
                }
                if (str7.equals("DBST---")) {
                    return TacticalLines.BEACH_SLOPE_STEEP;
                }
                if (str7.equals("DGMSR--")) {
                    return TacticalLines.SOLID_ROCK;
                }
                if (str7.equals("DGMSC--")) {
                    return TacticalLines.CLAY;
                }
                if (str7.equals("DGMSSVS")) {
                    return TacticalLines.VERY_COARSE_SAND;
                }
                if (str7.equals("DGMSSC-")) {
                    return TacticalLines.COARSE_SAND;
                }
                if (str7.equals("DGMSSM-")) {
                    return TacticalLines.MEDIUM_SAND;
                }
                if (str7.equals("DGMSSF-")) {
                    return TacticalLines.FINE_SAND;
                }
                if (str7.equals("DGMSSVF")) {
                    return TacticalLines.VERY_FINE_SAND;
                }
                if (str7.equals("DGMSIVF")) {
                    return TacticalLines.VERY_FINE_SILT;
                }
                if (str7.equals("DGMSIF-")) {
                    return TacticalLines.FINE_SILT;
                }
                if (str7.equals("DGMSIM-")) {
                    return TacticalLines.MEDIUM_SILT;
                }
                if (str7.equals("DGMSIC-")) {
                    return TacticalLines.COARSE_SILT;
                }
                if (str7.equals("DGMSB--")) {
                    return TacticalLines.BOULDERS;
                }
                if (str7.equals("DGMS-CO")) {
                    return TacticalLines.OYSTER_SHELLS;
                }
                if (str7.equals("DGMS-PH")) {
                    return TacticalLines.PEBBLES;
                }
                if (str7.equals("DGMS-SH")) {
                    return TacticalLines.SAND_AND_SHELLS;
                }
                if (str7.equals("DGML---")) {
                    return TacticalLines.BOTTOM_SEDIMENTS_LAND;
                }
                if (str7.equals("DGMN---")) {
                    return TacticalLines.BOTTOM_SEDIMENTS_NO_DATA;
                }
                if (str7.equals("DGMRS--")) {
                    return TacticalLines.BOTTOM_ROUGHNESS_SMOOTH;
                }
                if (str7.equals("DGMRM--")) {
                    return TacticalLines.BOTTOM_ROUGHNESS_MODERATE;
                }
                if (str7.equals("DGMRR--")) {
                    return TacticalLines.BOTTOM_ROUGHNESS_ROUGH;
                }
                if (str7.equals("DGMCL--")) {
                    return TacticalLines.CLUTTER_LOW;
                }
                if (str7.equals("DGMCM--")) {
                    return TacticalLines.CLUTTER_MEDIUM;
                }
                if (str7.equals("DGMCH--")) {
                    return TacticalLines.CLUTTER_HIGH;
                }
                if (str7.equals("DGMIBA-")) {
                    return TacticalLines.IMPACT_BURIAL_0;
                }
                if (str7.equals("DGMIBB-")) {
                    return TacticalLines.IMPACT_BURIAL_10;
                }
                if (str7.equals("DGMIBC-")) {
                    return TacticalLines.IMPACT_BURIAL_20;
                }
                if (str7.equals("DGMIBD-")) {
                    return TacticalLines.IMPACT_BURIAL_75;
                }
                if (str7.equals("DGMIBE-")) {
                    return TacticalLines.IMPACT_BURIAL_100;
                }
                if (str7.equals("DGMBCA-")) {
                    return TacticalLines.BOTTOM_CATEGORY_A;
                }
                if (str7.equals("DGMBCB-")) {
                    return TacticalLines.BOTTOM_CATEGORY_B;
                }
                if (str7.equals("DGMBCC-")) {
                    return TacticalLines.BOTTOM_CATEGORY_C;
                }
                if (str7.equals("DGMBTA-")) {
                    return TacticalLines.BOTTOM_TYPE_A1;
                }
                if (str7.equals("DGMBTB-")) {
                    return TacticalLines.BOTTOM_TYPE_A2;
                }
                if (str7.equals("DGMBTC-")) {
                    return TacticalLines.BOTTOM_TYPE_A3;
                }
                if (str7.equals("DGMBTD-")) {
                    return TacticalLines.BOTTOM_TYPE_B1;
                }
                if (str7.equals("DGMBTE-")) {
                    return TacticalLines.BOTTOM_TYPE_B2;
                }
                if (str7.equals("DGMBTF-")) {
                    return TacticalLines.BOTTOM_TYPE_B3;
                }
                if (str7.equals("DGMBTG-")) {
                    return TacticalLines.BOTTOM_TYPE_C1;
                }
                if (str7.equals("DGMBTH-")) {
                    return TacticalLines.BOTTOM_TYPE_C2;
                }
                if (str7.equals("DGMBTI-")) {
                    return TacticalLines.BOTTOM_TYPE_C3;
                }
                if (str7.equals("DL-SA--")) {
                    return TacticalLines.SWEPT_AREA;
                }
                if (str7.equals("DMOA---")) {
                    return TacticalLines.OIL_RIG_FIELD;
                }
                if (str7.equals("DMCC---")) {
                    return TacticalLines.SUBMERGED_CRIB;
                }
                if (str7.equals("DMCA---")) {
                    return TacticalLines.CABLE;
                }
                if (str7.equals("DL-ML--")) {
                    return TacticalLines.MARITIME_LIMIT;
                }
                if (str7.equals("DL-MA--")) {
                    return TacticalLines.MARITIME_AREA;
                }
                if (str7.equals("DMCD---")) {
                    return TacticalLines.CANAL;
                }
                if (str7.equals("DL-O---")) {
                    return TacticalLines.OPERATOR_DEFINED;
                }
            }
        } 
        catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsMETOC.IsWeather");
               ErrorLogger.LogException(_className ,"isWeather",
                    new RendererException("Failed inside isWeather", exc));
        }
        //end METOC section
        return -1;
    }
    //the following functions are for the rev D symbols
    /**
     * Rev D METOC symbols
     * @param SymbolSet
     * @param entityCode
     * @return 
     */
    public static int getWeatherLinetype(String SymbolSet, String entityCode)
    {
        int symbolSet=Integer.parseInt(SymbolSet);
        if(symbolSet != 45 && symbolSet != 46)
            return -1;
        int nCode=Integer.parseInt(entityCode);
        switch(nCode)
        {
            case 110301:
                return TacticalLines.CF;
            case 110302:
                return TacticalLines.UCF;
            case 110303:
                return TacticalLines.CFG;
            case 110304:
                return TacticalLines.CFY;
            case 110305:
                return TacticalLines.WF;
            case 110306:
                return TacticalLines.UWF;
            case 110307:
                return TacticalLines.WFG;
            case 110308:
                return TacticalLines.WFY;
            case 110309:
                return TacticalLines.OCCLUDED;
            case 110310:
                return TacticalLines.UOF;
            case 110311:
                return TacticalLines.OFY;
            case 110312:
                return TacticalLines.SF;
            case 110313:
                return TacticalLines.USF;
            case 110314:
                return TacticalLines.SFG;
            case 110315:
                return TacticalLines.SFY;
            case 110401:    //trough with dashed lines new symbol
            case 110402:    //now called upper trough
                return TacticalLines.TROUGH;
            case 110403:
                return TacticalLines.RIDGE;
            case 110404:
                return TacticalLines.SQUALL;
            case 110405:
                return TacticalLines.INSTABILITY;
            case 110406:
                return TacticalLines.SHEAR;
            case 110407:
                return TacticalLines.ITC;
            case 110408:
                return TacticalLines.CONVERGANCE;
            case 110409:
                return TacticalLines.ITD;
            case 140300:
                return TacticalLines.JET;
            case 140400:
                return TacticalLines.STREAM;
            case 162004:            //tropical storm wind
                break;
            case 170100:
                return TacticalLines.IFR;
            case 170200:
                return TacticalLines.MVFR;
            case 170300:
                return TacticalLines.TURBULENCE;
            case 170400:
                return TacticalLines.ICING;
            case 170500:
                return TacticalLines.NON_CONVECTIVE;
            case 170501:
                return TacticalLines.CONVECTIVE;
            case 170600:
                return TacticalLines.FROZEN;
            case 170700:
                return TacticalLines.THUNDERSTORMS;
            case 170800:
                return TacticalLines.FOG;                
            case 170900:
                return TacticalLines.SAND;
            case 171000:
                return TacticalLines.FREEFORM;
            case 180100:
                return TacticalLines.ISOBAR;
            case 180200:
                return TacticalLines.UPPER_AIR;
            case 180300:
                return TacticalLines.ISOTHERM;
            case 180400:
                return TacticalLines.ISOTACH;
            case 180500:
                return TacticalLines.ISODROSOTHERM;
            case 180600:
                return TacticalLines.ISOPLETHS;
            case 180700:
                return TacticalLines.OPERATOR_FREEFORM;
            case 110501:
                return TacticalLines.LVO;
            case 110502:
                return TacticalLines.UNDERCAST;
            case 110503:
                return TacticalLines.LRO;
            case 110504:
                return TacticalLines.ICE_EDGE;
            case 110505:
                return TacticalLines.ESTIMATED_ICE_EDGE;
            case 110506:
                return TacticalLines.ICE_EDGE_RADAR;
            case 110601:
                return TacticalLines.CRACKS;
            case 110602:
                return TacticalLines.CRACKS_SPECIFIC_LOCATION;
            case 110603:
                return TacticalLines.ICE_OPENINGS_LEAD;
            case 110604:
                return TacticalLines.ICE_OPENINGS_FROZEN;
            case 120102:
                return TacticalLines.DEPTH_CURVE;
            case 120103:
                return TacticalLines.DEPTH_CONTOUR;
            case 120104:
                return TacticalLines.DEPTH_AREA;
            case 120201:
                return TacticalLines.COASTLINE;
            case 120202:
                return TacticalLines.ISLAND;
            case 120203:
                return TacticalLines.BEACH;
            case 120204:
                return TacticalLines.WATER;
            case 120205:
                return TacticalLines.FORESHORE_LINE;
            case 120206:
                return TacticalLines.FORESHORE_AREA;
            case 120305:
                return TacticalLines.ANCHORAGE_LINE;
            case 120306:
                return TacticalLines.ANCHORAGE_AREA;
                
            case 120308:
                return TacticalLines.PIER;
            case 120312:
                return TacticalLines.WEIRS;
            case 120313:
                return TacticalLines.DRYDOCK;
            case 120317:
                return TacticalLines.LOADING_FACILITY_LINE;
            case 120318:
                return TacticalLines.LOADING_FACILITY_AREA;
                
            case 120319:
                return TacticalLines.RAMP_ABOVE_WATER;
            case 120320:
                return TacticalLines.RAMP_BELOW_WATER;
                
            case 120326:
                return TacticalLines.JETTY_ABOVE_WATER;
            case 120327:
                return TacticalLines.JETTY_BELOW_WATER;
            case 120328:
                return TacticalLines.SEAWALL;
            case 120405:
                return TacticalLines.PERCHES;
            case 120407:
                return TacticalLines.LEADING_LINE;
            case 120503:
                return TacticalLines.UNDERWATER_HAZARD;
            case 120505:
                return TacticalLines.FOUL_GROUND;
            case 120507:
                return TacticalLines.KELP;
            case 120511:
                return TacticalLines.BREAKERS;
            case 120512:
                return TacticalLines.REEF;
            case 120514:
                return TacticalLines.DISCOLORED_WATER;
            case 120702:
                return TacticalLines.EBB_TIDE;
            case 120703:
                return TacticalLines.FLOOD_TIDE;
                
            case 130101:
                return TacticalLines.VDR_LEVEL_12;
            case 130102:
                return TacticalLines.VDR_LEVEL_23;
            case 130103:
                return TacticalLines.VDR_LEVEL_34;
            case 130104:
                return TacticalLines.VDR_LEVEL_45;
            case 130105:
                return TacticalLines.VDR_LEVEL_56;
            case 130106:
                return TacticalLines.VDR_LEVEL_67;
            case 130107:
                return TacticalLines.VDR_LEVEL_78;
            case 130108:
                return TacticalLines.VDR_LEVEL_89;
            case 130109:
                return TacticalLines.VDR_LEVEL_910;
            case 130201:
                return TacticalLines.BEACH_SLOPE_FLAT;
            case 130202:
                return TacticalLines.BEACH_SLOPE_GENTLE;
            case 130203:
                return TacticalLines.BEACH_SLOPE_MODERATE;
            case 130204:
                return TacticalLines.BEACH_SLOPE_STEEP;
            case 140101:
                return TacticalLines.SOLID_ROCK;
            case 140102:
                return TacticalLines.CLAY;
            case 140103:
                return TacticalLines.VERY_COARSE_SAND;
            case 140104:
                return TacticalLines.COARSE_SAND;
            case 140105:
                return TacticalLines.MEDIUM_SAND;
            case 140106:
                return TacticalLines.FINE_SAND;
            case 140107:
                return TacticalLines.VERY_FINE_SAND;
            case 140108:
                return TacticalLines.VERY_FINE_SILT;
            case 140109:
                return TacticalLines.FINE_SILT;
            case 140110:
                return TacticalLines.MEDIUM_SILT;
            case 140111:
                return TacticalLines.COARSE_SILT;
            case 140112:
                return TacticalLines.BOULDERS;
            case 140113:
                return TacticalLines.OYSTER_SHELLS;
            case 140114:
                return TacticalLines.PEBBLES;
            case 140115:
                return TacticalLines.SAND_AND_SHELLS;
            case 140116:
                return TacticalLines.BOTTOM_SEDIMENTS_LAND;
            case 140117:
                return TacticalLines.BOTTOM_SEDIMENTS_NO_DATA;
            case 140118:
                return TacticalLines.BOTTOM_ROUGHNESS_SMOOTH;
            case 140119:
                return TacticalLines.BOTTOM_ROUGHNESS_MODERATE;
            case 140120:
                return TacticalLines.BOTTOM_ROUGHNESS_ROUGH;
            case 140121:
                return TacticalLines.CLUTTER_LOW;
            case 140122:
                return TacticalLines.CLUTTER_MEDIUM;
            case 140123:
                return TacticalLines.CLUTTER_HIGH;
            case 140124:
                return TacticalLines.IMPACT_BURIAL_0;
            case 140125:
                return TacticalLines.IMPACT_BURIAL_10;
            case 140126:
                return TacticalLines.IMPACT_BURIAL_20;
            case 140127:
                return TacticalLines.IMPACT_BURIAL_75;
            case 140128:
                return TacticalLines.IMPACT_BURIAL_100;
            case 140129:
                return TacticalLines.BOTTOM_CATEGORY_A;
            case 140130:
                return TacticalLines.BOTTOM_CATEGORY_B;
            case 140131:
                return TacticalLines.BOTTOM_CATEGORY_C;
            case 140132:
                return TacticalLines.BOTTOM_TYPE_A1;
            case 140133:
                return TacticalLines.BOTTOM_TYPE_A2;
            case 140134:
                return TacticalLines.BOTTOM_TYPE_A3;
            case 140135:
                return TacticalLines.BOTTOM_TYPE_B1;
            case 140136:
                return TacticalLines.BOTTOM_TYPE_B2;
            case 140137:
                return TacticalLines.BOTTOM_TYPE_B3;
            case 140138:
                return TacticalLines.BOTTOM_TYPE_C1;
            case 140139:
                return TacticalLines.BOTTOM_TYPE_C2;
            case 140140:
                return TacticalLines.BOTTOM_TYPE_C3;
            
            case 150100:
                return TacticalLines.MARITIME_LIMIT;
            case 150200:
                return TacticalLines.MARITIME_AREA;
            case 150300:
                return TacticalLines.RESTRICTED_AREA;
            case 150400:
                return TacticalLines.SWEPT_AREA;
            case 150500:
                return TacticalLines.TRAINING_AREA;
            case 150600:
                return TacticalLines.OPERATOR_DEFINED;
            case 160100:
                return TacticalLines.CABLE;
            case 160200:
                return TacticalLines.SUBMERGED_CRIB;
            case 160300:
                return TacticalLines.CANAL;
            case 160700:
                return TacticalLines.OIL_RIG_FIELD;
            case 160800:
                return TacticalLines.PIPE;
                
            default:
                return -1;
        }
        return -1;
    }
    
/**
 * Sets tactical graphic properties based on Mil-Std-2525 Appendix C.
 * @param tg
 */
    private static void SetMeTOCProperties(TGLight tg) {
        try
        {
            //METOC's have no user defined fills
            //any fills per Mil-Std-2525 will be set below
            //tg.set_FillColor(null);
            String symbolId=tg.get_SymbolId();
            switch (tg.get_LineType()) {   //255:150:150
                case TacticalLines.TROUGH:
                    if(symbolId.length()>=20)
                    {
                       String setB=symbolId.substring(10);
                       String entityCode=setB.substring(0,6);
                       if(entityCode.equalsIgnoreCase("110401"))
                       {
                           tg.set_LineStyle(2);
                       }
                    }
                    tg.set_LineColor(Color.BLACK);
                    break;
                case TacticalLines.BOTTOM_TYPE_A2:
                    tg.set_LineColor(new Color(127, 255, 0));   //light green
                    tg.set_FillColor(new Color(127, 255, 0));
                    break;
                case TacticalLines.BOTTOM_TYPE_C2:
                    tg.set_LineColor(new Color(255, 80, 0));   //dark orange
                    tg.set_FillColor(new Color(255, 80, 0));
                    break;
                case TacticalLines.BOTTOM_TYPE_C3:
                    tg.set_LineColor(new Color(255, 48, 0));   //orange red
                    tg.set_FillColor(new Color(255, 48, 0));
                    break;
                case TacticalLines.IMPACT_BURIAL_0:
                    tg.set_LineColor(new Color(0, 0, 255));   //blue
                    tg.set_FillColor(new Color(0, 0, 255));
                    break;
                case TacticalLines.BOTTOM_TYPE_C1:
                case TacticalLines.IMPACT_BURIAL_75:
                    tg.set_LineColor(new Color(255, 127, 0));   //orange
                    tg.set_FillColor(new Color(255, 127, 0));
                    break;
                case TacticalLines.BOTTOM_CATEGORY_C:
                case TacticalLines.IMPACT_BURIAL_100:
                case TacticalLines.CLUTTER_HIGH:
                case TacticalLines.BOTTOM_ROUGHNESS_ROUGH:
                    tg.set_LineColor(new Color(255, 0, 0));   //red
                    tg.set_FillColor(new Color(255, 0, 0));
                    break;
                case TacticalLines.BOTTOM_TYPE_B2:
                case TacticalLines.BOTTOM_CATEGORY_B:
                case TacticalLines.IMPACT_BURIAL_20:
                case TacticalLines.CLUTTER_MEDIUM:
                case TacticalLines.BOTTOM_ROUGHNESS_MODERATE:
                    tg.set_LineColor(new Color(255, 255, 0));   //yellow
                    tg.set_FillColor(new Color(255, 255, 0));
                    break;
                case TacticalLines.BOTTOM_TYPE_A1:
                case TacticalLines.BOTTOM_CATEGORY_A:
                case TacticalLines.IMPACT_BURIAL_10:
                case TacticalLines.CLUTTER_LOW:
                case TacticalLines.BOTTOM_ROUGHNESS_SMOOTH:
                    tg.set_LineColor(new Color(0, 255, 0));   //green
                    tg.set_FillColor(new Color(0, 255, 0));
                    break;
                case TacticalLines.BOTTOM_SEDIMENTS_NO_DATA:
                    tg.set_LineColor(new Color(230, 230, 230));   //light gray
                    tg.set_FillColor(new Color(230, 230, 230));
                    break;
                case TacticalLines.BOTTOM_SEDIMENTS_LAND:
                    tg.set_LineColor(new Color(220, 220, 220));   //gray
                    tg.set_FillColor(new Color(220, 220, 220));
                    break;
                case TacticalLines.SAND_AND_SHELLS:
                    tg.set_LineColor(new Color(255, 220, 220));   //light peach
                    tg.set_FillColor(new Color(255, 220, 220));
                    break;
                case TacticalLines.PEBBLES:
                    tg.set_LineColor(new Color(255, 190, 190));   //peach
                    tg.set_FillColor(new Color(255, 190, 190));
                    break;
                case TacticalLines.OYSTER_SHELLS:
                    tg.set_LineColor(new Color(255, 150, 150));   //dark peach
                    tg.set_FillColor(new Color(255, 150, 150));
                    break;
                case TacticalLines.BOULDERS:
                    tg.set_LineColor(new Color(255, 0, 0));
                    tg.set_FillColor(new Color(255, 0, 0));
                    break;
                case TacticalLines.COARSE_SILT:
                    tg.set_LineColor(new Color(200, 255, 105));
                    tg.set_FillColor(new Color(200, 255, 105));
                    break;
                case TacticalLines.MEDIUM_SILT:
                    tg.set_LineColor(new Color(0, 255, 0));     //green
                    tg.set_FillColor(new Color(0, 255, 0));
                    break;
                case TacticalLines.FINE_SILT:
                    tg.set_LineColor(new Color(25, 255, 230));     //turquoise
                    tg.set_FillColor(new Color(25, 255, 230));
                    break;
                case TacticalLines.VERY_FINE_SILT:
                    tg.set_LineColor(new Color(0, 215, 255));     //turquoise
                    tg.set_FillColor(new Color(0, 215, 255));
                    break;
                case TacticalLines.VERY_FINE_SAND:
                    tg.set_LineColor(new Color(255, 255, 220));     //pale yellow
                    tg.set_FillColor(new Color(255, 255, 220));
                    break;
                case TacticalLines.FINE_SAND:
                    tg.set_LineColor(new Color(255, 255, 140));     //light yellow
                    tg.set_FillColor(new Color(255, 255, 140));
                    break;
                case TacticalLines.MEDIUM_SAND:
                    tg.set_LineColor(new Color(255, 235, 0));     //yellow
                    tg.set_FillColor(new Color(255, 235, 0));
                    break;
                case TacticalLines.COARSE_SAND:
                    tg.set_LineColor(new Color(255, 215, 0));     //light gold
                    tg.set_FillColor(new Color(255, 215, 0));
                    break;
                case TacticalLines.BOTTOM_TYPE_B3:
                    tg.set_LineColor(new Color(255, 207, 0));     //gold
                    tg.set_FillColor(new Color(255, 207, 0));
                    break;
                case TacticalLines.VERY_COARSE_SAND:
                    tg.set_LineColor(new Color(255, 180, 0));     //gold
                    tg.set_FillColor(new Color(255, 180, 0));
                    break;
                case TacticalLines.CLAY:
                    tg.set_LineColor(new Color(100, 130, 255));     //periwinkle
                    tg.set_FillColor(new Color(100, 130, 255));
                    break;
                case TacticalLines.SOLID_ROCK:
                    tg.set_LineColor(new Color(255, 0, 255));     //magent
                    tg.set_FillColor(new Color(255, 0, 255));
                    break;
                case TacticalLines.VDR_LEVEL_12:
                    tg.set_LineColor(new Color(26, 153, 55));     //dark green
                    tg.set_FillColor(new Color(26, 153, 55));
                    break;
                case TacticalLines.VDR_LEVEL_23:
                    tg.set_LineColor(new Color(26, 204, 77));     //light green
                    tg.set_FillColor(new Color(26, 204, 77));
                    break;
                case TacticalLines.BOTTOM_TYPE_A3:
                case TacticalLines.VDR_LEVEL_34:
                    tg.set_LineColor(new Color(128, 255, 51));    //lime green
                    tg.set_FillColor(new Color(128, 255, 51));
                    break;
                case TacticalLines.BOTTOM_TYPE_B1:
                case TacticalLines.VDR_LEVEL_45:
                    tg.set_LineColor(new Color(204, 255, 26));    //yellow green
                    tg.set_FillColor(new Color(204, 255, 26));
                    break;
                case TacticalLines.VDR_LEVEL_56:
                    tg.set_LineColor(new Color(255, 255, 0));     //yellow
                    tg.set_FillColor(new Color(255, 255, 0));
                    break;
                case TacticalLines.VDR_LEVEL_67:
                    tg.set_LineColor(new Color(255, 204, 0));     //gold
                    tg.set_FillColor(new Color(255, 204, 0));
                    break;
                case TacticalLines.VDR_LEVEL_78:
                    tg.set_LineColor(new Color(255, 128, 0));     //light orange
                    tg.set_FillColor(new Color(255, 128, 0));
                    break;
                case TacticalLines.VDR_LEVEL_89:
                    tg.set_LineColor(new Color(255, 77, 0));      //dark orange
                    tg.set_FillColor(new Color(255, 77, 0));
                    break;
                case TacticalLines.VDR_LEVEL_910:
                    tg.set_LineColor(Color.RED);
                    tg.set_FillColor(Color.RED);
                    break;
                case TacticalLines.CANAL:
                    tg.set_LineColor(Color.BLACK);
                    tg.set_LineThickness(4);
                    break;
                case TacticalLines.OPERATOR_DEFINED:
                    tg.set_LineColor(Color.ORANGE);
                    break;
                case TacticalLines.MARITIME_LIMIT:
                    tg.set_LineColor(Color.MAGENTA);
                    tg.set_LineStyle(1);
                    tg.set_LineThickness(1);
                    break;
                case TacticalLines.MARITIME_AREA:
                    tg.set_LineColor(Color.MAGENTA);
                    tg.set_LineStyle(1);
                    break;
                case TacticalLines.PERCHES:
                case TacticalLines.SUBMERGED_CRIB:
                    tg.set_LineColor(Color.BLACK);
                    tg.set_LineStyle(2);
                    tg.set_FillColor(Color.BLUE);
                    break;
                case TacticalLines.DISCOLORED_WATER:
                case TacticalLines.UNDERWATER_HAZARD:
                    tg.set_LineColor(Color.BLACK);
                    tg.set_LineStyle(2);
                    tg.set_FillColor(new Color(0, 191, 255)); //deep sky blue
                    break;
                case TacticalLines.LOADING_FACILITY_AREA:
                    tg.set_LineColor(new Color(210, 180, 140));
                    tg.set_FillColor(new Color(210, 180, 140));
                    break;
                case TacticalLines.LOADING_FACILITY_LINE:
                    tg.set_LineColor(Color.GRAY);
                    tg.set_LineThickness(4);
                    break;
                case TacticalLines.DRYDOCK:
                    tg.set_LineColor(Color.BLACK);
                    //tg.set_FillColor(new Color(165, 42, 42)); //brown
                    tg.set_FillColor(new Color(205, 133, 63)); //brown
                    tg.set_LineStyle(1);
                    break;
                case TacticalLines.FORESHORE_AREA:
                    //tg.set_LineColor(new Color(154, 205, 50));
                    //tg.set_FillColor(new Color(154, 205, 50));
                    tg.set_LineColor(new Color(173, 255, 47));
                    tg.set_FillColor(new Color(173, 255, 47));
                    break;
                case TacticalLines.FORESHORE_LINE:
                    //tg.set_LineColor(new Color(154, 205, 50));
                    tg.set_LineColor(new Color(173, 255, 47));
                    break;
                case TacticalLines.RESTRICTED_AREA:
                case TacticalLines.TRAINING_AREA:
                case TacticalLines.ANCHORAGE_LINE:
                case TacticalLines.ANCHORAGE_AREA:
                    tg.set_LineColor(Color.MAGENTA);
                    //tg.set_LineStyle(1);    //dashed
                    break;
                case TacticalLines.PIPE:
                    tg.set_LineColor(Color.GRAY);   //beige
                    tg.set_FillColor(Color.GRAY);
                    break;
                case TacticalLines.WATER:
                    tg.set_LineColor(Color.GRAY);   //beige
                    tg.set_FillColor(Color.WHITE);
                    tg.set_LineStyle(1);
                    break;
                case TacticalLines.WEIRS:
                    tg.set_LineColor(new Color(245, 245, 220));   //beige
                    tg.set_LineStyle(1);
                    tg.set_LineThickness(3);
                    break;
                case TacticalLines.SWEPT_AREA:
                case TacticalLines.OIL_RIG_FIELD:
                case TacticalLines.FOUL_GROUND:
                case TacticalLines.KELP:
                case TacticalLines.BEACH_SLOPE_MODERATE:
                case TacticalLines.BEACH_SLOPE_STEEP:
                case TacticalLines.BEACH:
                    tg.set_LineColor(new Color(245, 245, 220));   //beige
                    break;
                case TacticalLines.DEPTH_AREA:
                    tg.set_LineColor(Color.BLUE);
                    tg.set_FillColor(Color.WHITE);
                    break;
                case TacticalLines.CONVERGANCE:
                case TacticalLines.ITC:
                    tg.set_LineColor(Color.ORANGE);
                    break;
                case TacticalLines.OFY:
                case TacticalLines.OCCLUDED:
                    tg.set_LineColor(new Color(160, 32, 240));
                    tg.set_FillColor(new Color(160, 32, 240));
                    break;
                case TacticalLines.UOF:
                    tg.set_LineColor(new Color(160, 32, 240));
                    break;
                case TacticalLines.WFY:
                case TacticalLines.WFG:
                case TacticalLines.WF:
                    tg.set_FillColor(Color.RED);
                    tg.set_LineColor(Color.RED);
                    break;
                case TacticalLines.UWF:
                case TacticalLines.IFR:
                    tg.set_LineColor(Color.RED);
                    break;
                case TacticalLines.CFG:
                case TacticalLines.CFY:
                case TacticalLines.CF:
                    tg.set_LineColor(Color.BLUE);
                    tg.set_FillColor(Color.BLUE);
                    break;
                case TacticalLines.UCF:
                case TacticalLines.MVFR:
                    tg.set_LineColor(Color.BLUE);
                    break;
                case TacticalLines.TURBULENCE:
                    tg.set_LineColor(Color.BLUE);
                    tg.set_LineStyle(2);
                    break;
                case TacticalLines.CABLE:
                case TacticalLines.CABLE_GE:
                    tg.set_LineColor(Color.MAGENTA);
                    break;
                case TacticalLines.ISLAND:
                    //tg.set_LineColor(new Color(165, 42, 42)); //brown
                    //tg.set_FillColor(new Color(165, 42, 42)); //brown
                    tg.set_LineColor(new Color(210, 180, 140)); //tan
                    tg.set_FillColor(new Color(210, 180, 140)); //tam
                    break;
                case TacticalLines.SEAWALL:
                case TacticalLines.SEAWALL_GE:
                case TacticalLines.FLOOD_TIDE:
                case TacticalLines.FLOOD_TIDE_GE:
                case TacticalLines.EBB_TIDE:
                case TacticalLines.EBB_TIDE_GE:
                case TacticalLines.JETTY_ABOVE_WATER:
                case TacticalLines.JETTY_ABOVE_WATER_GE:
                    tg.set_LineColor(Color.GRAY);
                    break;
                case TacticalLines.BEACH_SLOPE_FLAT:
                    tg.set_LineColor(new Color(211, 211, 211));
                    tg.set_FillColor(null);
                    break;
                case TacticalLines.BEACH_SLOPE_GENTLE:
                    tg.set_LineColor(new Color(111, 111, 111));
                    tg.set_FillColor(null);
                    break;
                case TacticalLines.BREAKERS:
                    tg.set_LineStyle(1);
                    tg.set_LineColor(Color.GRAY);
                    tg.set_LineThickness(1);
                    break;
                case TacticalLines.JETTY_BELOW_WATER:
                case TacticalLines.JETTY_BELOW_WATER_GE:
                    tg.set_LineStyle(1);
                    tg.set_LineColor(Color.GRAY);
                    break;
                case TacticalLines.DEPTH_CURVE:
                case TacticalLines.DEPTH_CURVE_GE:
                case TacticalLines.DEPTH_CONTOUR:
                case TacticalLines.DEPTH_CONTOUR_GE:
                case TacticalLines.COASTLINE:
                case TacticalLines.COASTLINE_GE:
                case TacticalLines.PIER:
                case TacticalLines.PIER_GE:
                    //tg.set_LineStyle(1);
                    tg.set_LineThickness(1);
                    tg.set_LineColor(Color.GRAY);
                    break;
                case TacticalLines.FROZEN:
                case TacticalLines.JET:
                case TacticalLines.JET_GE:
                    tg.set_LineColor(Color.RED);
                    break;
                case TacticalLines.THUNDERSTORMS:
                    tg.set_LineColor(Color.RED);
                    tg.set_LineStyle(3);
                    break;
                case TacticalLines.RAMP_BELOW_WATER:
                case TacticalLines.RAMP_BELOW_WATER_GE:
                case TacticalLines.ESTIMATED_ICE_EDGE:
                case TacticalLines.ESTIMATED_ICE_EDGE_GE:
                    tg.set_LineStyle(1);
                    tg.set_LineColor(Color.BLACK);
                    break;
                case TacticalLines.ISODROSOTHERM:
                case TacticalLines.ISODROSOTHERM_GE:
                    tg.set_LineColor(Color.GREEN);
                    break;
                case TacticalLines.LRO:
                case TacticalLines.UNDERCAST:
                case TacticalLines.LVO:
                case TacticalLines.SQUALL:
                case TacticalLines.RIDGE:
                //case TacticalLines.TROUGH:
                case TacticalLines.ICE_OPENINGS_LEAD:
                case TacticalLines.ICE_OPENINGS_LEAD_GE:
                case TacticalLines.ICE_OPENINGS_FROZEN:
                case TacticalLines.ICE_OPENINGS_FROZEN_GE:
                case TacticalLines.LEADING_LINE:
                case TacticalLines.STREAM:
                case TacticalLines.STREAM_GE:
                case TacticalLines.CRACKS:
                case TacticalLines.CRACKS_GE:
                case TacticalLines.CRACKS_SPECIFIC_LOCATION:
                case TacticalLines.CRACKS_SPECIFIC_LOCATION_GE:
                case TacticalLines.ISOBAR:
                case TacticalLines.ISOBAR_GE:
                case TacticalLines.UPPER_AIR:
                case TacticalLines.UPPER_AIR_GE:
                case TacticalLines.ICE_EDGE:
                case TacticalLines.ICE_EDGE_GE:
                case TacticalLines.ICE_EDGE_RADAR:
                case TacticalLines.ICE_EDGE_RADAR_GE:
                case TacticalLines.REEF:
                case TacticalLines.ICE_DRIFT:
                    tg.set_LineColor(Color.BLACK);
                    break;
                case TacticalLines.INSTABILITY:
                    tg.set_LineStyle(4);
                    tg.set_LineColor(Color.BLACK);
                    break;
                case TacticalLines.SHEAR:
                    tg.set_LineStyle(3);
                    tg.set_LineColor(Color.BLACK);
                    break;
                case TacticalLines.ISOPLETHS:
                case TacticalLines.ISOPLETHS_GE:
                case TacticalLines.ISOTHERM:
                case TacticalLines.ISOTHERM_GE:
                    tg.set_LineStyle(1);
                    tg.set_LineColor(Color.RED);
                    break;
                case TacticalLines.ISOTACH:
                case TacticalLines.ISOTACH_GE:
                    tg.set_LineStyle(1);
                    tg.set_LineColor(new Color(160, 32, 240));
                    break;
                case TacticalLines.SAND:
                case TacticalLines.ICING:
                    tg.set_LineColor(new Color(165, 42, 42)); //brown
                    break;
                case TacticalLines.NON_CONVECTIVE:
                    tg.set_LineColor(Color.GREEN);
                    break;
                case TacticalLines.CONVECTIVE:
                    tg.set_LineColor(Color.GREEN);
                    tg.set_LineStyle(3);
                    break;
                case TacticalLines.FOG:
                    tg.set_LineColor(Color.YELLOW);
                    break;
                case TacticalLines.RAMP_ABOVE_WATER:
                case TacticalLines.RAMP_ABOVE_WATER_GE:
                    tg.set_LineColor(Color.BLACK);
                    break;
                default:
                    break;
            }
        }
        catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsMETOC.SetMeTOCProperties");
               ErrorLogger.LogException(_className ,"SetMeTOCProperties",
                    new RendererException("Failed inside SetMeTOCProperties", exc));
        }
    }

    /**
     *
     * Finds next closest point with same x position on the splinePoints curve as pt
     * walks up the curve and if it does not find a range that straddles x it return null.
     * We ultimately will draw a line from pt to the extrapolated point on the splinePoints spline.
     * used for ICE_OPENINGS_FROZEN_LEAD
     *
     * @param splinePoints - the points on the opposite spline
     * @param pt - the point in the original curve from which the line will start
     *
     * @return The extrapolated point on the opposite spline to which the line will be drawn
     */
    private static POINT2 ExtrapolatePointFromCurve(ArrayList<POINT2> splinePoints,
            POINT2 pt) {

        POINT2 pt2 = null;
        try {
            double dx = 0;	//delta x between two points on the curve
            double dy = 0;	//delta y between two points on the curve
            double m = 1;		//the slope dy/dx
            double y = 0;		//the y component of the calculated interpolation point
            double x = pt.x;
            int j = 0;
            //walk up the index to find points which straddle x.
            //if we find a pair which straddle x then extrapolate the y avlue from the curve and
            //return the point
            for (j = 0; j < splinePoints.size() - 1; j++) {
                //increment the index to find a greater value
                if (splinePoints.get(j).x <= x && splinePoints.get(j + 1).x >= x) {	//extrapolate the point y value from the curve and return the point as Point(x,y)
                    dx = splinePoints.get(j + 1).x - splinePoints.get(j).x;
                    dy = splinePoints.get(j + 1).y - splinePoints.get(j).y;
                    m = dy / dx;
                    y = splinePoints.get(j).y + (x - splinePoints.get(j).x) * m;
                    pt2 = new POINT2(x, y);
                    return pt2;
                }
                if (splinePoints.get(j).x >= x && splinePoints.get(j + 1).x <= x) {	//extrapolate the point y value from the curve and return the point as Point(x,y)
                    dx = splinePoints.get(j + 1).x - splinePoints.get(j).x;
                    dy = splinePoints.get(j + 1).y - splinePoints.get(j).y;
                    m = dy / dx;
                    y = splinePoints.get(j).y + (x - splinePoints.get(j).x) * m;
                    pt2 = new POINT2(x, y);
                    return pt2;
                }
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("Error in clsMETOC.ExtrapolatePointFromCurve");
               ErrorLogger.LogException(_className ,"ExtrapolatePointFromCurve",
                    new RendererException("Failed inside ExtrapolatePointfromCurve", exc));
        }
        return pt2;
    }
    /**
     * The public interface, main function to return METOC shapes
     * @param tg the tactical graphic
     * @param shapes the ShapeInfo array
     * @param rev the Mil-Standard-2525 revision
     */
    public static void GetMeTOCShape(TGLight tg, 
            ArrayList<Shape2> shapes,
            int rev) {
        try
        {
            if(shapes==null)
                return;
            GeneralPath lineObject = null;
            GeneralPath lineObject2 = null;
            ArrayList<POINT2> splinePoints = new ArrayList();
            ArrayList<POINT2> splinePoints2 = new ArrayList();
            double d = 0;
            int j = 0, k=0, l=0;
            Shape2 shape=null;
            POINT2 ptLast = tg.Pixels.get(tg.Pixels.size() - 1);
            ArrayList<POINT2> twoSplines = null;
            ArrayList<POINT2> upperSpline = null;
            ArrayList<POINT2> lowerSpline = null;
            ArrayList<POINT2> originalPixels = null;
            
            ArrayList<POINT2>pixels=null;
            originalPixels=null;
            ArrayList<P1>partitions=null;
            SetMeTOCProperties(tg);
            switch (tg.get_LineType()) {
                case TacticalLines.SF:
                case TacticalLines.USF:
                case TacticalLines.SFG:
                case TacticalLines.SFY:
                case TacticalLines.WFY:
                case TacticalLines.WFG:
                case TacticalLines.WF:
                case TacticalLines.UWF:
                case TacticalLines.UCF:
                case TacticalLines.CF:
                case TacticalLines.CFG:
                case TacticalLines.CFY:
                case TacticalLines.OCCLUDED:
                case TacticalLines.UOF:
                case TacticalLines.OFY:
                case TacticalLines.TROUGH:
                case TacticalLines.INSTABILITY:
                case TacticalLines.SHEAR:
                case TacticalLines.RIDGE:
                case TacticalLines.SQUALL:
                case TacticalLines.ITC:
                case TacticalLines.CONVERGANCE:
                case TacticalLines.ITD:
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
                case TacticalLines.OPERATOR_FREEFORM:
                case TacticalLines.LVO:
                case TacticalLines.UNDERCAST:
                case TacticalLines.LRO:
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
                case TacticalLines.ANCHORAGE_LINE:
                case TacticalLines.PIPE:
                case TacticalLines.TRAINING_AREA:
                case TacticalLines.RESTRICTED_AREA:
                case TacticalLines.REEF:
                case TacticalLines.ICE_DRIFT:
                case TacticalLines.FORESHORE_AREA:
                case TacticalLines.FORESHORE_LINE:
                case TacticalLines.DRYDOCK:
                case TacticalLines.LOADING_FACILITY_LINE:
                case TacticalLines.LOADING_FACILITY_AREA:
                case TacticalLines.PERCHES:
                case TacticalLines.UNDERWATER_HAZARD:
                case TacticalLines.BREAKERS:
                case TacticalLines.DISCOLORED_WATER:
                case TacticalLines.BEACH_SLOPE_FLAT:
                case TacticalLines.BEACH_SLOPE_GENTLE:
                case TacticalLines.MARITIME_LIMIT:
                case TacticalLines.MARITIME_AREA:
                case TacticalLines.OPERATOR_DEFINED:
                case TacticalLines.SUBMERGED_CRIB:
                case TacticalLines.CANAL:
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
                    //int rev=tg.getSymbologyStandard();
                    arraysupport.GetLineArray2(tg.get_LineType(), tg.Pixels, shapes,null,rev,null);
                    break;
                case TacticalLines.ISOBAR:
                case TacticalLines.ISOBAR_GE:
                case TacticalLines.UPPER_AIR:
                case TacticalLines.UPPER_AIR_GE:
                case TacticalLines.ISOTHERM:
                case TacticalLines.ISOTHERM_GE:
                case TacticalLines.ISOTACH:
                case TacticalLines.ISOTACH_GE:
                case TacticalLines.ISODROSOTHERM:
                case TacticalLines.ISODROSOTHERM_GE:
                case TacticalLines.ISOPLETHS:
                case TacticalLines.ISOPLETHS_GE:
                case TacticalLines.ICE_EDGE:
                case TacticalLines.ICE_EDGE_GE:
                case TacticalLines.ESTIMATED_ICE_EDGE:
                case TacticalLines.ESTIMATED_ICE_EDGE_GE:
                case TacticalLines.CRACKS:
                case TacticalLines.CRACKS_GE:
                case TacticalLines.DEPTH_CURVE:
                case TacticalLines.DEPTH_CURVE_GE:
                case TacticalLines.DEPTH_CONTOUR:
                case TacticalLines.DEPTH_CONTOUR_GE:
                case TacticalLines.COASTLINE:
                case TacticalLines.COASTLINE_GE:
                case TacticalLines.PIER:
                case TacticalLines.PIER_GE:
                case TacticalLines.RAMP_ABOVE_WATER:
                case TacticalLines.RAMP_ABOVE_WATER_GE:
                case TacticalLines.RAMP_BELOW_WATER:
                case TacticalLines.RAMP_BELOW_WATER_GE:
                case TacticalLines.JETTY_ABOVE_WATER:
                case TacticalLines.JETTY_ABOVE_WATER_GE:
                case TacticalLines.JETTY_BELOW_WATER:
                case TacticalLines.JETTY_BELOW_WATER_GE:
                case TacticalLines.SEAWALL:
                case TacticalLines.SEAWALL_GE:
                case TacticalLines.EBB_TIDE:
                case TacticalLines.FLOOD_TIDE:
                case TacticalLines.EBB_TIDE_GE:
                case TacticalLines.FLOOD_TIDE_GE:
                case TacticalLines.CABLE:
                case TacticalLines.CABLE_GE:
                case TacticalLines.JET:
                case TacticalLines.STREAM:
                case TacticalLines.JET_GE:
                case TacticalLines.STREAM_GE:
                    lineObject2 = DrawSplines(tg, splinePoints);
                    lineObject2.lineTo(ptLast.x, ptLast.y);
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.setShape(lineObject2);
                    shapes.add(shape);
                    break;
                case TacticalLines.HOLD_GE:
                case TacticalLines.BRDGHD_GE:
                    if(tg.get_FillColor()!=null && tg.get_FillColor().getAlpha()>1)
                    {
                        lineObject2 = DrawSplines(tg, splinePoints);
                        lineObject2.lineTo(ptLast.x, ptLast.y);
                        shape = new Shape2(Shape2.SHAPE_TYPE_FILL);
                        shape.setShape(lineObject2);
                        //shape.setLineColor(null);
                        //shape.setFillColor(tg.get_FillColor());
                        //shape.setStroke(new BasicStroke(0));
                        shapes.add(shape);
                        splinePoints.clear();
                    }
                    
                    lineObject2 = DrawSplines(tg, splinePoints);
                    lineObject2.lineTo(ptLast.x, ptLast.y);
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.setShape(lineObject2);
                    //shape.setLineColor(tg.get_LineColor());
                    //shape.setStroke(new BasicStroke(tg.get_LineThickness()));
                    //shape.setFillColor(null);
                    shapes.add(shape);
                    SetShapeProperties(tg,shapes,null);
                    return;
                case TacticalLines.HOLD:
                case TacticalLines.BRDGHD:
                    lineObject2 = DrawSplines(tg, splinePoints);
                    //lineObject2.lineTo(ptLast.x, ptLast.y);
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.setShape(lineObject2);
                    shapes.add(shape);

                    //diagnostic add section 1-3-11
                    //this section is to add a fill shape using the spline points
                    //Use tg.Pixels if splinepoints is empty
                    //fill did not conform very well to the outline
                    shape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    //shape=new Shape2(Shape2.SHAPE_TYPE_FILL);
                    shape.setFillColor(tg.get_FillColor());
                    if(tg.get_FillColor() != null && tg.get_FillColor().getAlpha()>1)
                    {
                        if(splinePoints != null && splinePoints.size()>0)
                        {
                            shape.moveTo(splinePoints.get(0));
                            for(j=1;j<splinePoints.size();j++)
                                shape.lineTo(splinePoints.get(j));

                            shape.lineTo(tg.Pixels.get(tg.Pixels.size()-1));
                            shapes.add(0,shape);
                        }
                        else
                        {
                            shape.moveTo(tg.Pixels.get(0));
                            for(j=1;j<tg.Pixels.size();j++)
                                shape.lineTo(tg.Pixels.get(j));

                            shape.lineTo(tg.Pixels.get(tg.Pixels.size()-1));
                            shapes.add(0,shape);
                        }
                    }
                    //end section
                    break;
                case TacticalLines.CRACKS_SPECIFIC_LOCATION:
                case TacticalLines.CRACKS_SPECIFIC_LOCATION_GE:
                case TacticalLines.ICE_EDGE_RADAR:
                case TacticalLines.ICE_EDGE_RADAR_GE:
                    lineObject2 = DrawSplines(tg, splinePoints);
                    //lineObject2.lineTo(ptLast.x, ptLast.y);
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.setShape(lineObject2);
                    shapes.add(shape);
                    break;
                case TacticalLines.ICE_OPENINGS_LEAD:
                    //pixels=null;
                    originalPixels=tg.Pixels;
                    partitions=clsChannelUtility.GetPartitions2(tg);
                    //int s=0,e=0;
                    for(l=0;l<partitions.size();l++)
                    {
                        tg.Pixels=originalPixels;
                        pixels=new ArrayList();
                        for(k=partitions.get(l).start;k<=partitions.get(l).end_Renamed+1;k++)
                            pixels.add(tg.Pixels.get(k));
                        
                        if(pixels==null || pixels.isEmpty())
                            continue;

                        twoSplines = new ArrayList();
                        //twoSplines = ParallelLines(tg);
                        twoSplines = ParallelLines2(pixels,rev);

                        upperSpline = new ArrayList();
                        lowerSpline = new ArrayList();

                        for (j = 0; j < twoSplines.size() / 2; j++) {
                            upperSpline.add(twoSplines.get(j));
                        }

                        for (j = twoSplines.size() / 2; j < twoSplines.size(); j++) {
                            lowerSpline.add(twoSplines.get(j));
                        }

                        tg.Pixels = lowerSpline;
                        lineObject2 = DrawSplines(tg, splinePoints);
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject2);
                        shapes.add(shape);

                        tg.Pixels = upperSpline;
                        lineObject2 = DrawSplines(tg, splinePoints);
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject2);
                        shapes.add(shape);
                    }
                    break;
                case TacticalLines.ICE_OPENINGS_LEAD_GE:
                    //pixels=null;
                    originalPixels=tg.Pixels;
                    partitions=clsChannelUtility.GetPartitions2(tg);
                    //int s=0,e=0;
                    for(l=0;l<partitions.size();l++)
                    {
                        tg.Pixels=originalPixels;
                        pixels=new ArrayList();
                        for(k=partitions.get(l).start;k<=partitions.get(l).end_Renamed+1;k++)
                            pixels.add(tg.Pixels.get(k));

                        if(pixels==null || pixels.isEmpty())
                            continue;

                        twoSplines = new ArrayList();
                        //twoSplines = ParallelLines(tg);
                        twoSplines = ParallelLines2(pixels,rev);

                        upperSpline = new ArrayList();
                        lowerSpline = new ArrayList();

                        for (j = 0; j < twoSplines.size() / 2; j++) {
                            upperSpline.add(twoSplines.get(j));
                        }

                        for (j = twoSplines.size() / 2; j < twoSplines.size(); j++) {
                            lowerSpline.add(twoSplines.get(j));
                        }

                        tg.Pixels = lowerSpline;
                        lineObject2 = DrawSplines(tg, splinePoints);
                        ptLast=tg.Pixels.get(tg.Pixels.size()-1);
                        lineObject2.lineTo(ptLast.x, ptLast.y);
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject2);
                        shapes.add(shape);

                        tg.Pixels = upperSpline;
                        splinePoints=new ArrayList();
                        lineObject2 = DrawSplines(tg, splinePoints);
                        ptLast=tg.Pixels.get(tg.Pixels.size()-1);
                        lineObject2.lineTo(ptLast.x, ptLast.y);
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject2);
                        shapes.add(shape);
                    }
                    break;
                case TacticalLines.ICE_OPENINGS_FROZEN:
                    originalPixels=tg.Pixels;
                    partitions=clsChannelUtility.GetPartitions2(tg);
                    for(l=0;l<partitions.size();l++)
                    {
                        tg.Pixels=originalPixels;
                        pixels=new ArrayList();
                        for(k=partitions.get(l).start;k<=partitions.get(l).end_Renamed+1;k++)
                            pixels.add(tg.Pixels.get(k));

                        if(pixels==null || pixels.isEmpty())
                            continue;

                        twoSplines = new ArrayList();
                        //twoSplines = ParallelLines(tg);
                        twoSplines = ParallelLines2(pixels,rev);
                        upperSpline = new ArrayList();
                        lowerSpline = new ArrayList();

                        for (j = 0; j < twoSplines.size() / 2; j++) {
                            upperSpline.add(twoSplines.get(j));
                        }

                        for (j = twoSplines.size() / 2; j < twoSplines.size(); j++) {
                            lowerSpline.add(twoSplines.get(j));
                        }

                        tg.Pixels = lowerSpline;
                        lineObject2 = DrawSplines(tg, splinePoints);
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject2);
                        shapes.add(shape);

                        tg.Pixels = upperSpline;
                        lineObject2 = DrawSplines(tg, splinePoints2);
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject2);
                        shapes.add(shape);

                        //parse upper and lower arrys to find the corresponding splines
                        ArrayList splinePointsArrays = new ArrayList();
                        ArrayList splinePoints2Arrays = new ArrayList();
                        ArrayList<POINT2> ptsArray = new ArrayList();
                        for (j = 0; j < splinePoints.size(); j++) {
                            if (splinePoints.get(j).style != 47)
                            {
                                ptsArray.add(splinePoints.get(j));
                            } 
                            else
                            {
                                splinePointsArrays.add(ptsArray);
                                ptsArray = new ArrayList();
                            }
                        }

                        for (j = 0; j < splinePoints2.size(); j++) {
                            if (splinePoints2.get(j).style != 47)
                            {
                                ptsArray.add(splinePoints2.get(j));
                            } 
                            else
                            {
                                splinePoints2Arrays.add(ptsArray);
                                ptsArray = new ArrayList();
                            }
                        }

                        //int k = 0;
                        ArrayList<POINT2> array = null;
                        ArrayList<POINT2> array2 = null;
                        POINT2 pt,
                         pt2;
                        lineObject = new GeneralPath();
                        //the lines to connect the extrapolated points
                        for (j = 0; j < splinePointsArrays.size(); j++) {
                            array = (ArrayList<POINT2>) splinePointsArrays.get(j);

                            if(splinePoints2Arrays.size()>j)
                                array2 = (ArrayList<POINT2>) splinePoints2Arrays.get(j);
                            else
                                break;
                            //extrapolate against points in the shortest array
                            if (splinePointsArrays.size() >= splinePoints2Arrays.size()) //array is shorter
                            {
                                for (k = 0; k < array.size(); k++) {
                                    if(array.size()>k)
                                        pt = array.get(k);
                                    else
                                        break;

                                    pt2 = ExtrapolatePointFromCurve(array2, pt);
                                    //if we got a valid extrapolation point then draw the line
                                    if (pt2 != null) {
                                        //sprite2.graphics.moveTo(pt.x,pt.y);
                                        //sprite2.graphics.lineTo(pt2.x,pt2.y);
                                        lineObject.moveTo(pt.x, pt.y);
                                        lineObject.lineTo(pt2.x, pt2.y);
                                    }
                                }
                            } else //array2 is shorter
                            {
                                for (k = 0; k < array2.size(); k++) {
                                    pt = array2.get(k);
                                    pt2 = ExtrapolatePointFromCurve(array, pt);
                                    //if we got a valid extrapolation point then draw the line
                                    if (pt2 != null) {
                                        //sprite2.graphics.moveTo(pt.x,pt.y);
                                        //sprite2.graphics.lineTo(pt2.x,pt2.y);
                                        lineObject.moveTo(pt.x, pt.y);
                                        lineObject.lineTo(pt2.x, pt2.y);
                                    }
                                }
                            }
                        }
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject);
                        shapes.add(shape);
                    }
                    //the lines connecting the extrapolated points
                    break;
                case TacticalLines.ICE_OPENINGS_FROZEN_GE:
                    originalPixels=tg.Pixels;
                    partitions=clsChannelUtility.GetPartitions2(tg);
                    for(l=0;l<partitions.size();l++)
                    {
                        tg.Pixels=originalPixels;
                        pixels=new ArrayList();
                        for(k=partitions.get(l).start;k<=partitions.get(l).end_Renamed+1;k++)
                            pixels.add(tg.Pixels.get(k));

                        if(pixels==null || pixels.isEmpty())
                            continue;

                        twoSplines = new ArrayList();
                        //twoSplines = ParallelLines(tg);
                        twoSplines = ParallelLines2(pixels,rev);
                        upperSpline = new ArrayList();
                        lowerSpline = new ArrayList();

                        for (j = 0; j < twoSplines.size() / 2; j++) {
                            upperSpline.add(twoSplines.get(j));
                        }

                        for (j = twoSplines.size() / 2; j < twoSplines.size(); j++) {
                            lowerSpline.add(twoSplines.get(j));
                        }

                        tg.Pixels = lowerSpline;
                        ArrayList<POINT2>splinePoints3=new ArrayList();
                        lineObject2 = DrawSplines(tg, splinePoints3);
                        splinePoints.addAll(splinePoints3);
                        ptLast=tg.Pixels.get(tg.Pixels.size()-1);
                        lineObject2.lineTo(ptLast.x, ptLast.y);
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject2);
                        shapes.add(shape);

                        tg.Pixels = upperSpline;
                        ArrayList<POINT2>splinePoints4=new ArrayList();
                        lineObject2 = DrawSplines(tg, splinePoints4);
                        splinePoints2.addAll(splinePoints4);
                        ptLast=tg.Pixels.get(tg.Pixels.size()-1);
                        lineObject2.lineTo(ptLast.x, ptLast.y);
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject2);
                        shapes.add(shape);

                        //parse upper and lower arrys to find the corresponding splines
                        ArrayList splinePointsArrays = new ArrayList();
                        ArrayList splinePoints2Arrays = new ArrayList();
                        ArrayList<POINT2> ptsArray = new ArrayList();
                        for (j = 0; j < splinePoints.size(); j++) {
                            if (splinePoints.get(j).style != 47)
                            {
                                ptsArray.add(splinePoints.get(j));
                            }
                            else
                            {
                                splinePointsArrays.add(ptsArray);
                                ptsArray = new ArrayList();
                            }
                        }

                        for (j = 0; j < splinePoints2.size(); j++) {
                            if (splinePoints2.get(j).style != 47)
                            {
                                ptsArray.add(splinePoints2.get(j));
                            }
                            else
                            {
                                splinePoints2Arrays.add(ptsArray);
                                ptsArray = new ArrayList();
                            }
                        }

                        //int k = 0;
                        ArrayList<POINT2> array = null;
                        ArrayList<POINT2> array2 = null;
                        POINT2 pt,
                         pt2;
                        lineObject = new GeneralPath();
                        //the lines to connect the extrapolated points
                        for (j = 0; j < splinePointsArrays.size(); j++) {
                            array = (ArrayList<POINT2>) splinePointsArrays.get(j);

                            if(splinePoints2Arrays.size()>j)
                                array2 = (ArrayList<POINT2>) splinePoints2Arrays.get(j);
                            else
                                break;
                            //extrapolate against points in the shortest array
                            if (splinePointsArrays.size() >= splinePoints2Arrays.size()) //array is shorter
                            {
                                for (k = 0; k < array.size(); k++) {
                                    if(array.size()>k)
                                        pt = array.get(k);
                                    else
                                        break;

                                    pt2 = ExtrapolatePointFromCurve(array2, pt);
                                    //if we got a valid extrapolation point then draw the line
                                    if (pt2 != null) {
                                        //sprite2.graphics.moveTo(pt.x,pt.y);
                                        //sprite2.graphics.lineTo(pt2.x,pt2.y);
                                        lineObject.moveTo(pt.x, pt.y);
                                        lineObject.lineTo(pt2.x, pt2.y);
                                    }
                                }
                            } else //array2 is shorter
                            {
                                for (k = 0; k < array2.size(); k++) {
                                    pt = array2.get(k);
                                    pt2 = ExtrapolatePointFromCurve(array, pt);
                                    //if we got a valid extrapolation point then draw the line
                                    if (pt2 != null) {
                                        //sprite2.graphics.moveTo(pt.x,pt.y);
                                        //sprite2.graphics.lineTo(pt2.x,pt2.y);
                                        lineObject.moveTo(pt.x, pt.y);
                                        lineObject.lineTo(pt2.x, pt2.y);
                                    }
                                }
                            }
                        }
                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject);
                        shapes.add(shape);
                    }
                    //the lines connecting the extrapolated points
                    break;
                case TacticalLines.LEADING_LINE:
                    //the solid line
                    lineObject = DrawSplines(tg, splinePoints);
                    lineObject2 = new GeneralPath();
                    if(splinePoints.size()>0)
                        lineObject2.moveTo(splinePoints.get(0).x, splinePoints.get(0).y);
                    else
                    {
                        lineObject2.moveTo(tg.Pixels.get(0).x,tg.Pixels.get(0).y);
                        for(j=0;j<tg.Pixels.size();j++)
                            lineObject2.lineTo(tg.Pixels.get(j).x,tg.Pixels.get(j).y);

                        shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        shape.setShape(lineObject2);
                        shape.set_Style(1);
                        shapes.add(shape);
                        return;
                    }

                    int n = splinePoints.size() / 2;
                    for (j = 1; j <= n; j++) {
                        if(splinePoints.size()>=j-1)
                            lineObject2.lineTo(splinePoints.get(j).x, splinePoints.get(j).y);
                    }
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.setShape(lineObject2);
                    shapes.add(shape);

                    //the dashed line
                    lineObject2 = new GeneralPath();
                    lineObject2.moveTo(splinePoints.get(n).x, splinePoints.get(n).y);
                    for (j = n + 1; j < splinePoints.size(); j++) {
                        if(splinePoints.size()>=j-1)
                            lineObject2.lineTo(splinePoints.get(j).x, splinePoints.get(j).y);
                    }
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.setShape(lineObject2);
                    shape.set_Style(1);
                    shapes.add(shape);

                    //set the sahpe properties based on the tg properties
                    //override the above line, set the 2nd shape to dashed line
                    //shapes.get(1).set_Style(1);
                    break;
                default:
                    break;
            }
            //add the last point
            if (tg.get_LineType() != TacticalLines.ICE_OPENINGS_LEAD &&
                    tg.get_LineType() != TacticalLines.ICE_OPENINGS_LEAD_GE &&
                    tg.get_LineType() != TacticalLines.ICE_OPENINGS_FROZEN &&
                    tg.get_LineType() != TacticalLines.ICE_OPENINGS_FROZEN_GE &&
                    //tg.get_LineType() != TacticalLines.ICE_EDGE_RADAR_GE &&
                    tg.get_LineType() != TacticalLines.ICE_EDGE_RADAR)
            {
                if (splinePoints != null && splinePoints.size() > 0)
                {
                    lineObject2 = new GeneralPath();
                    lineObject2.moveTo(splinePoints.get(splinePoints.size() - 1).x, splinePoints.get(splinePoints.size() - 1).y);
                    lineObject2.lineTo(ptLast.x, ptLast.y);
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.setShape(lineObject2);
                    shape.set_Style(0);
                    shapes.add(shape);
                }
            }
            SetShapeProperties(tg, shapes, null);
        } 
        catch (Exception exc) {
            //clsUtility.WriteFile("error in clsMETOC.GetMeTOCShape");
               ErrorLogger.LogException(_className ,"GetMeTOCShape",
                    new RendererException("Failed inside GetMeTOCShape", exc));
        }
    }
/**
 * Returns the image file name based on the linetype for the METOC symbols with pattern ill
 * @param tg
 * @return
 */
    private static String GetImageFile(TGLight tg) {
        String fileName = "";
        try
        {
        switch (tg.get_LineType()) {
            case TacticalLines.WEIRS:
                fileName = "visualAssets/Weirs.png";
                break;
            case TacticalLines.SWEPT_AREA:
                fileName = "visualAssets/SweptArea.png";
                break;
            case TacticalLines.OIL_RIG_FIELD:
                fileName = "visualAssets/OilRigField.png";
                break;
            case TacticalLines.FOUL_GROUND:
                fileName = "visualAssets/FoulGround.png";
                break;
            case TacticalLines.KELP:
                fileName = "visualAssets/Kelp.png";
                break;
            case TacticalLines.BEACH_SLOPE_STEEP:
                fileName = "visualAssets/BeachSlopeSteep.png";
                break;
            case TacticalLines.BEACH:
                fileName = "visualAssets/BeigeStipple.png";
                break;
            case TacticalLines.BEACH_SLOPE_MODERATE:
                fileName = "visualAssets/BeachSlopeModerate.png";
                break;
            default:
                break;
        }
        }
        catch(Exception exc)
        {
            //clsUtility.WriteFile("Error in clsMETOC.GetImageFile");
               ErrorLogger.LogException(_className ,"GetImageFile",
                    new RendererException("Failed inside GetImageFile", exc));
        }

        return fileName;
    }
/**
 * Sets the shape properties based on the tacttical graphic properties and also based on shape
 * styles which may have been set by JavaLineArray
 * @param tg
 * @param shapes shapes array to set properties
 * @param bi BufferedImage used for hatch fills
 */
    protected static void SetShapeProperties(TGLight tg, ArrayList<Shape2> shapes, BufferedImage bi) {
        try
        {
            if (shapes == null)
            {
                return;
            }
            switch(tg.get_LineType())
            {
                case TacticalLines.DEPTH_AREA:
                    return;
                default:
                    break;
            }

            int j = 0;
            Shape2 shape = null;
            BasicStroke stroke = null;
            InputStream inFile = null;
            BufferedImage bi2 = null;
            float[] dash = null;
            int lineThickness = tg.get_LineThickness();
            Rectangle2D.Double rect = null;
            Graphics2D grid = null;
            TexturePaint tp = tg.get_TexturePaint();
            String fileName = GetImageFile(tg);
            switch (tg.get_LineType()) {
                case TacticalLines.WEIRS:
                case TacticalLines.SWEPT_AREA:
                case TacticalLines.OIL_RIG_FIELD:
                case TacticalLines.FOUL_GROUND:
                case TacticalLines.KELP:
                case TacticalLines.BEACH_SLOPE_MODERATE:
                case TacticalLines.BEACH_SLOPE_STEEP:
                case TacticalLines.BEACH:
//                    if(tg.get_UsePatternFill())
//                        break;
                    shape = shapes.get(0);
                    shape.setLineColor(tg.get_LineColor());
                    inFile = clsMETOC.class.getClassLoader().getResourceAsStream(fileName);
                    if(inFile!=null)
                    {
                        bi2 = ImageIO.read(inFile);
                        rect = new Rectangle2D.Double(0, 0, bi2.getWidth(), bi2.getHeight());
                        tp = new TexturePaint(bi2, rect);
                        shape.setTexturePaint(tp);
                        inFile.close();
                    }
                    break;
                case TacticalLines.SF:
                case TacticalLines.USF:
                case TacticalLines.SFG:
                case TacticalLines.SFY:
                    for (j = 0; j < shapes.size(); j++) {
                        shape = shapes.get(j);
                        if (shape == null || shape.getShape() == null) {
                            continue;
                        }

                        shape.set_Style(tg.get_LineStyle());
                        stroke = new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, dash, 0);
                        shape.setStroke(stroke);
                    }
                    return;
                default:
                    break;
            }

            int shapeType = -1;
            int lineType = tg.get_LineType();
            boolean isChange1Area = clsUtility.IsChange1Area(lineType, null);
            boolean isClosedPolygon = clsUtility.isClosedPolygon(lineType);
            for (j = 0; j < shapes.size(); j++) {
                shape = shapes.get(j);
                if (shape == null || shape.getShape() == null) {
                    continue;
                }

                if (shape.getShapeType() == Shape2.SHAPE_TYPE_FILL) {
                    shape.setFillColor(tg.get_FillColor());
                }

                //clsUtility.ResolveModifierShape(tg,shape);

                shapeType = shape.getShapeType();
                switch (tg.get_LineType()) {
                    case TacticalLines.SF:
                    case TacticalLines.USF:
                    case TacticalLines.SFG:
                    case TacticalLines.SFY:
                    case TacticalLines.ITD:
                        break;
                    case TacticalLines.LEADING_LINE:
                    case TacticalLines.TRAINING_AREA:
                        shape.setLineColor(tg.get_LineColor());
                        break;
                    case TacticalLines.BRDGHD_GE:
                    case TacticalLines.HOLD_GE:
                        if(shape.getShapeType()==ShapeInfo.SHAPE_TYPE_FILL)
                            shape.setLineColor(null);
                        else
                        {
                            shape.setLineColor(tg.get_LineColor());
                            shape.set_Style(tg.get_LineStyle());                            
                        }
                        break;
                    default:
                        shape.setLineColor(tg.get_LineColor());
                        shape.set_Style(tg.get_LineStyle());
                        break;
                }

                if (isClosedPolygon || shapeType == Shape2.SHAPE_TYPE_FILL)
                {
                    switch(tg.get_LineType())//these have fill instead of TexturePaint
                    {
                        case TacticalLines.FORESHORE_AREA:
                        case TacticalLines.WATER:
                        case TacticalLines.ISLAND:
                        case TacticalLines.DRYDOCK:
                        case TacticalLines.LOADING_FACILITY_AREA:
                        case TacticalLines.PERCHES:
                        case TacticalLines.UNDERWATER_HAZARD:
                        case TacticalLines.DISCOLORED_WATER:
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
                        case TacticalLines.FINE_SAND:
                        case TacticalLines.MEDIUM_SAND:
                        case TacticalLines.COARSE_SAND:
                        case TacticalLines.VERY_COARSE_SAND:
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
                        case TacticalLines.BOTTOM_ROUGHNESS_MODERATE:
                        case TacticalLines.BOTTOM_ROUGHNESS_ROUGH:
                        case TacticalLines.BOTTOM_ROUGHNESS_SMOOTH:
                        case TacticalLines.CLUTTER_HIGH:
                        case TacticalLines.CLUTTER_MEDIUM:
                        case TacticalLines.CLUTTER_LOW:
                        case TacticalLines.IMPACT_BURIAL_0:
                        case TacticalLines.IMPACT_BURIAL_10:
                        case TacticalLines.IMPACT_BURIAL_100:
                        case TacticalLines.IMPACT_BURIAL_20:
                        case TacticalLines.IMPACT_BURIAL_75:
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
                        case TacticalLines.SUBMERGED_CRIB:
                        case TacticalLines.FREEFORM:
                            shape.setFillColor(tg.get_FillColor());
                            break;
                        default:
                            break;
                    }
                    switch (shape.get_FillStyle()) {
                        case 3://GraphicProperties.FILL_TYPE_RIGHT_SLANTS:
                            rect = new Rectangle2D.Double(0, 0, 8, 8);
                            grid = bi.createGraphics();
                            grid.setColor(shape.getFillColor());
                            grid.drawLine(0, 8, 8, 0);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(null);
                            grid.dispose();
                            break;
                        case 2://GraphicProperties.FILL_TYPE_LEFT_SLANTS:
                            rect = new Rectangle2D.Double(0, 0, 8, 8);
                            grid = bi.createGraphics();
                            grid.setColor(tg.get_FillColor());
                            grid.drawLine(0, 0, 8, 8);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(null);
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
                            grid = bi.createGraphics();
                            grid.setColor(tg.get_FillColor());
                            grid.drawLine(4, 0, 4, 8);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(null);
                            grid.dispose();
                            break;
                        case 5://GraphicProperties.FILL_TYPE_HORIZONTAL_LINES:
                            rect = new Rectangle2D.Double(0, 0, 8, 8);
                            grid = bi.createGraphics();
                            grid.setColor(tg.get_FillColor());
                            grid.drawLine(0, 4, 8, 4);
                            tp = new TexturePaint(bi, rect);
                            shape.setTexturePaint(tp);
                            shape.setFillColor(null);
                            grid.dispose();
                            break;
                        case 1://GraphicProperties.FILL_TYPE_SOLID:
                            //shape.set_FillColor(tg.get_FillColor());
                            break;
                        default:
                            break;
                    }
                }
                switch (shape.get_Style()) {
                    case 0://GraphicProperties.LINE_TYPE_SOLID:
                        //dash=new float[1];
                        //dash[0]=1;
                        dash = null;
                        break;
                    case 1://GraphicProperties.LINE_TYPE_DASHED:
                        dash = new float[2];
                        dash[0] = 5f;
                        dash[1] = 5f;
                        //clsUtility.WriteFile("dashed");
                        break;
                    case 2://GraphicProperties.LINE_TYPE_DOTTED:
                        dash = new float[2];
                        dash[0] = 3f;
                        dash[1] = 3f;
                        break;
                    case 3://GraphicProperties.LINE_TYPE_DASHDOT:
                        dash = new float[4];
                        dash[0] = 8;//dash
                        dash[1] = 4;//space
                        dash[2] = 4;//dot
                        dash[3] = 4;//space
                        break;
                    case 4://GraphicProperties.LINE_TYPE_DASHDOTDOT:
                        dash = new float[6];
                        dash[0] = 8;//dash
                        dash[1] = 4;//space
                        dash[2] = 4;//dot
                        dash[3] = 4;//space
                        dash[4] = 4;//dot
                        dash[5] = 4;//space
                        break;
                    default:
                        break;
                }
                //set the shape with the default properties
                //the switch statement below will override specific properties as needed
                stroke = new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, dash, 0);
                shape.setStroke(stroke);
            }
        } catch (Exception exc) {
            //clsUtility.WriteFile("error in clsMETOC.SetShapeProperties");
               ErrorLogger.LogException(_className ,"SetShapeProperties",
                    new RendererException("Failed inside SetShapeProperties", exc));
        }
    }

    /**
     * Draws an arrow to the GeneralPath object from pt1 to pt2.
     *
     * @param pt1 arrow tip
     * @param pt2 - arrow base
     * @param size - arrow size in pixels
     * @param lineObject - general path to draw the arrow
     *
     * @return arrow sprite
     */
    private static void DrawArrow(POINT2 pt1,
            POINT2 pt2,
            int size,
            GeneralPath lineObject) {
        try
        {
            POINT2 ptBase = new POINT2();
            POINT2 ptTemp = new POINT2();
            ArrayList<POINT2> pts = new ArrayList();
            ptBase = lineutility.ExtendAlongLineDouble(pt2, pt1, size);
            ptTemp = lineutility.ExtendDirectedLine(pt1, ptBase, ptBase, 2, size);

            pts.add(ptTemp);
            pts.add(pt2);
            ptTemp = lineutility.ExtendDirectedLine(pt1, ptBase, ptBase, 3, size);
            pts.add(ptTemp);
            lineObject.moveTo(pts.get(0).x, pts.get(0).y);
            lineObject.lineTo(pts.get(1).x, pts.get(1).y);
            lineObject.lineTo(pts.get(2).x, pts.get(2).y);
            pts.clear();
            pts = null;
        } catch (Exception exc) {
            //clsUtility.WriteFile("error in clsMETOC.DrawArrow");
               ErrorLogger.LogException(_className ,"DrawArrow",
                    new RendererException("Failed inside DrawArrow", exc));
        }
    }
    /**
     * Returns a GeneralPath for symbols which require splines. Also returns the calculated
     * spline points for those symbols with additional features based on them.
     * @param tg
     * @param splinePoints2 spline points in pixels
     * @return
     */
    private static GeneralPath DrawSplines(TGLight tg,
            ArrayList<POINT2> splinePoints2) {
        GeneralPath lineObject = new GeneralPath();
        try {
            int i = 0, j = 0;
            ArrayList<POINT2> splinePoints;
            ArrayList<POINT2> array = tg.get_Pixels();
            POINT2 pt0 = new POINT2(), pt1 = new POINT2(), pt2 = new POINT2(), pt3 = new POINT2(),
                    pt4 = new POINT2(), pt5 = new POINT2(), pt6 = new POINT2();
            POINT2 pt, pt_before, pt_after, Di, p2, p3, pt_after2;
            double tension = 0.33;
            double control_scale = (tension / 0.5 * 0.175);
            ArrayList<POINT2> tmpArray = null;
            for (i = 0; i < array.size() - 1; i++) //was length-1
            {
                pt = array.get(i);
                if (i == 0) {
                    lineObject.moveTo(pt.x, pt.y);
                    pt_before = pt;
                } else {
                    pt_before = array.get(i - 1);
                }

                if (i == array.size() - 1) {
                    pt2 = array.get(i);
                } else {
                    pt2 = array.get(i + 1);
                }

                if (i < array.size() - 2) {
                    pt_after = array.get(i + 1);
                } else {
                    pt_after = array.get(array.size() - 1);
                }

                if (i < array.size() - 2) {
                    pt_after2 = array.get(i + 2);
                } else {
                    pt_after2 = array.get(array.size() - 1);
                }


                Di = new POINT2();
                p2 = new POINT2();

                Di.x = pt_after.x - pt_before.x;
                Di.y = pt_after.y - pt_before.y;
                p2.x = pt.x + control_scale * Di.x;
                p2.y = pt.y + control_scale * Di.y;

                p3 = new POINT2();
                POINT2 DiPlus1 = new POINT2();

                DiPlus1.x = pt_after2.x - pt.x;
                DiPlus1.y = pt_after2.y - pt.y;
                p3.x = pt_after.x - control_scale * DiPlus1.x;
                p3.y = pt_after.y - control_scale * DiPlus1.y;
                //p3.x = pt_after.x - control_scale * DiPlus1.x;
                //p3.y = pt_after.y - control_scale * DiPlus1.y;

                tmpArray = drawCubicBezier2(tg, lineObject, pt, p2, p3, pt2);

                //ICE_OPENINGS_FROZEN needs to know which segment corresponds to each spline point
                if (tg.get_LineType() == TacticalLines.ICE_OPENINGS_FROZEN ||
                        tg.get_LineType() == TacticalLines.ICE_OPENINGS_FROZEN_GE)
                {
                    if(tmpArray.size()>0)
                        tmpArray.get(tmpArray.size() - 1).style = 47;   //use this to differentiate the arrays
                }
                splinePoints2.addAll(tmpArray);

                splinePoints = tmpArray;

                switch (tg.get_LineType()) {
                    case TacticalLines.EBB_TIDE:
                        if (i == array.size() - 2)
                        {
                            if(splinePoints.size()>=2)
                                DrawArrow(splinePoints.get(splinePoints.size() - 2), tg.Pixels.get(tg.Pixels.size() - 1), 10, lineObject);
                        }
                        break;
                    case TacticalLines.FLOOD_TIDE:
//                        if (i == array.size() - 2)
//                        {
//                            if(splinePoints.size()>=2)
//                                DrawArrow(splinePoints.get(splinePoints.size() - 2), tg.Pixels.get(tg.Pixels.size() - 1), 10, lineObject);
//                        }
                        if (i == 0 && splinePoints.size() > 1) {
                            //finally get the feather points
                            //must allocate for the feather points, requires 4 additional points
                            pt0 = splinePoints.get(0);
                            pt1 = splinePoints.get(1);
                            //pt0=array.get(0);
                            //pt1=array.get(1);
                            pt2 = lineutility.ExtendLineDouble(pt0, pt1, 10);
                            pt3 = lineutility.ExtendLineDouble(pt0, pt1, 20);
                            pt4 = lineutility.ExtendLineDouble(pt0, pt1, 30);
                            //lineutility.ExtendDirectedLine(pt3,pt2,pt5,10,3);
                            //lineutility.ExtendDirectedLine(pt4,pt3,pt6,10,3);
                            pt5 = lineutility.ExtendDirectedLine(pt3, pt2, pt2, 3, 10);
                            pt6 = lineutility.ExtendDirectedLine(pt4, pt3, pt3, 3, 10);

                            //first feather line
                            lineObject.moveTo(pt3.x, pt3.y);
                            lineObject.lineTo(pt5.x, pt5.y);
                            //second feather line
                            lineObject.moveTo(pt4.x, pt4.y);
                            lineObject.lineTo(pt6.x, pt6.y);
                        }
                        if (i == array.size() - 2)
                        {
                            if(splinePoints.size()>=2)
                                DrawArrow(splinePoints.get(splinePoints.size() - 2), tg.Pixels.get(tg.Pixels.size() - 1), 10, lineObject);
                        }
                        break;
                    case TacticalLines.STREAM:
                    case TacticalLines.JET:
                        //if (i == 0)
                        if (i == 0 && splinePoints.size()>1)
                        {
                            DrawArrow(splinePoints.get(1), splinePoints.get(0), 10, lineObject);
                        }
                        break;
                    case TacticalLines.FLOOD_TIDE_GE:
//                        if(i==array.size()-2)//the last point in the array
//                        {
//                            lineObject.moveTo((int)splinePoints2.get(0).x,(int)splinePoints2.get(0).y);
//                            for(j=1;j<splinePoints2.size();j++)
//                                lineObject.lineTo((int)splinePoints2.get(j).x,(int)splinePoints2.get(j).y);
//
//                            if(splinePoints.size()>=2)
//                                DrawArrow(splinePoints.get(splinePoints.size() - 2), tg.Pixels.get(tg.Pixels.size() - 1), 10, lineObject);
//                        }
                        if (i == 0 && splinePoints.size() > 1) {
                            //finally get the feather points
                            //must allocate for the feather points, requires 4 additional points
                            pt0 = splinePoints.get(0);
                            pt1 = splinePoints.get(1);
                            //pt0=array.get(0);
                            //pt1=array.get(1);
                            pt2 = lineutility.ExtendLineDouble(pt0, pt1, 10);
                            pt3 = lineutility.ExtendLineDouble(pt0, pt1, 20);
                            pt4 = lineutility.ExtendLineDouble(pt0, pt1, 30);
                            //lineutility.ExtendDirectedLine(pt3,pt2,pt5,10,3);
                            //lineutility.ExtendDirectedLine(pt4,pt3,pt6,10,3);
                            pt5 = lineutility.ExtendDirectedLine(pt3, pt2, pt2, 3, 10);
                            pt6 = lineutility.ExtendDirectedLine(pt4, pt3, pt3, 3, 10);

                            //first feather line
                            lineObject.moveTo(pt3.x, pt3.y);
                            lineObject.lineTo(pt5.x, pt5.y);
                            //second feather line
                            lineObject.moveTo(pt4.x, pt4.y);
                            lineObject.lineTo(pt6.x, pt6.y);
                        }
                        if(i==array.size()-2)//the last point in the array
                        {
                            lineObject.moveTo((int)splinePoints2.get(0).x,(int)splinePoints2.get(0).y);
                            for(j=1;j<splinePoints2.size();j++)
                                lineObject.lineTo((int)splinePoints2.get(j).x,(int)splinePoints2.get(j).y);

                            if(splinePoints.size()>=2)
                                DrawArrow(splinePoints.get(splinePoints.size() - 2), tg.Pixels.get(tg.Pixels.size() - 1), 10, lineObject);
                        }
                        break;
                    case TacticalLines.EBB_TIDE_GE:
                        if(i==array.size()-2)//the last point in the array
                        {
                            lineObject=new GeneralPath();
                            lineObject.moveTo((int)splinePoints2.get(0).x,(int)splinePoints2.get(0).y);
                            for(j=1;j<splinePoints2.size();j++)
                                lineObject.lineTo((int)splinePoints2.get(j).x,(int)splinePoints2.get(j).y);

                            if(splinePoints.size()>=2)
                                DrawArrow(splinePoints.get(splinePoints.size() - 2), tg.Pixels.get(tg.Pixels.size() - 1), 10, lineObject);
                        }
                        break;
                    case TacticalLines.JET_GE:
                    case TacticalLines.STREAM_GE:
                        if (i == 0 && splinePoints.size()>1)
                        {
                            DrawArrow(splinePoints.get(1), splinePoints.get(0), 10, lineObject);
                        }
                        if(i==array.size()-2)//the last point in the array
                        {
                            //lineObject=new GeneralPath();
                            lineObject.moveTo((int)splinePoints2.get(0).x,(int)splinePoints2.get(0).y);
                            for(j=1;j<splinePoints2.size();j++)
                                lineObject.lineTo((int)splinePoints2.get(j).x,(int)splinePoints2.get(j).y);
                        }
                        break;
                    case TacticalLines.ICE_OPENINGS_FROZEN_GE:
                    case TacticalLines.ICE_OPENINGS_LEAD_GE:
                    case TacticalLines.CABLE_GE:
                    case TacticalLines.SEAWALL_GE:
                    case TacticalLines.JETTY_BELOW_WATER_GE:
                    case TacticalLines.JETTY_ABOVE_WATER_GE:
                    case TacticalLines.RAMP_ABOVE_WATER_GE:
                    case TacticalLines.RAMP_BELOW_WATER_GE:
                    case TacticalLines.PIER_GE:
                    case TacticalLines.COASTLINE_GE:
                    case TacticalLines.DEPTH_CONTOUR_GE:
                    case TacticalLines.DEPTH_CURVE_GE:
                    case TacticalLines.CRACKS_GE:
                    case TacticalLines.ESTIMATED_ICE_EDGE_GE:
                    case TacticalLines.ICE_EDGE_GE:
                    case TacticalLines.ISOPLETHS_GE:
                    case TacticalLines.ISODROSOTHERM_GE:
                    case TacticalLines.ISOTACH_GE:
                    case TacticalLines.ISOTHERM_GE:
                    case TacticalLines.UPPER_AIR_GE:
                    case TacticalLines.ISOBAR_GE:
                    case TacticalLines.HOLD_GE:
                    case TacticalLines.BRDGHD_GE:
                        if(splinePoints2!=null && !splinePoints2.isEmpty())
                        {
                            lineObject=new GeneralPath();
                            if(i==array.size()-2)//the last point in the array
                            {
                                lineObject.moveTo((int)splinePoints2.get(0).x,(int)splinePoints2.get(0).y);
                                for(j=1;j<splinePoints2.size();j++)
                                    lineObject.lineTo((int)splinePoints2.get(j).x,(int)splinePoints2.get(j).y);
                            }
                        }
                        break;
                    case TacticalLines.ICE_EDGE_RADAR:
                        for (j = 0; j < splinePoints.size() - 1; j++) {
                            pt0 = new POINT2(splinePoints.get(j).x, splinePoints.get(j).y);
                            pt2 = lineutility.ExtendAngledLine(splinePoints.get(j), splinePoints.get(j + 1), pt0, 45, 5);
                            pt1 = new POINT2(splinePoints.get(j).x, splinePoints.get(j).y);
                            pt3 = lineutility.ExtendAngledLine(splinePoints.get(j), splinePoints.get(j + 1), pt1, -45, 5);
                            lineObject.moveTo(splinePoints.get(j).x, splinePoints.get(j).y);
                            lineObject.lineTo(pt2.x, pt2.y);
                            lineObject.moveTo(splinePoints.get(j).x, splinePoints.get(j).y);
                            lineObject.lineTo(pt3.x, pt3.y);

                            pt0 = new POINT2(splinePoints.get(j).x, splinePoints.get(j).y);
                            pt2 = lineutility.ExtendAngledLine(splinePoints.get(j), splinePoints.get(j + 1), pt0, 135, 5);
                            pt1 = new POINT2(splinePoints.get(j).x, splinePoints.get(j).y);
                            pt3 = lineutility.ExtendAngledLine(splinePoints.get(j), splinePoints.get(j + 1), pt1, -135, 5);
                            lineObject.moveTo(splinePoints.get(j).x, splinePoints.get(j).y);
                            lineObject.lineTo(pt2.x, pt2.y);
                            lineObject.moveTo(splinePoints.get(j).x, splinePoints.get(j).y);
                            lineObject.lineTo(pt3.x, pt3.y);
                        }
                        break;
                    case TacticalLines.ICE_EDGE_RADAR_GE:
                        for (j = 0; j < splinePoints.size() - 1; j++)
                        {
                            pt0 = new POINT2(splinePoints.get(j).x, splinePoints.get(j).y);
                            pt2 = lineutility.ExtendAngledLine(splinePoints.get(j), splinePoints.get(j + 1), pt0, 45, 5);
                            pt1 = new POINT2(splinePoints.get(j).x, splinePoints.get(j).y);
                            pt3 = lineutility.ExtendAngledLine(splinePoints.get(j), splinePoints.get(j + 1), pt1, -45, 5);
                            lineObject.moveTo(splinePoints.get(j).x, splinePoints.get(j).y);
                            lineObject.lineTo(pt2.x, pt2.y);
                            lineObject.moveTo(splinePoints.get(j).x, splinePoints.get(j).y);
                            lineObject.lineTo(pt3.x, pt3.y);

                            pt0 = new POINT2(splinePoints.get(j).x, splinePoints.get(j).y);
                            pt2 = lineutility.ExtendAngledLine(splinePoints.get(j), splinePoints.get(j + 1), pt0, 135, 5);
                            pt1 = new POINT2(splinePoints.get(j).x, splinePoints.get(j).y);
                            pt3 = lineutility.ExtendAngledLine(splinePoints.get(j), splinePoints.get(j + 1), pt1, -135, 5);
                            lineObject.moveTo(splinePoints.get(j).x, splinePoints.get(j).y);
                            lineObject.lineTo(pt2.x, pt2.y);
                            lineObject.moveTo(splinePoints.get(j).x, splinePoints.get(j).y);
                            lineObject.lineTo(pt3.x, pt3.y);
                        }
                        if(i==array.size()-2)//the last point in the array
                        {
                            //lineObject=new GeneralPath();
                            lineObject.moveTo((int)splinePoints2.get(0).x,(int)splinePoints2.get(0).y);
                            for(j=1;j<splinePoints2.size();j++)
                                lineObject.lineTo((int)splinePoints2.get(j).x,(int)splinePoints2.get(j).y);
                        }
                        break;
                    case TacticalLines.CRACKS_SPECIFIC_LOCATION:
                        for (j = 0; j < splinePoints.size() - 1; j++)
                        {
                            //get perpendicular points (point pair)
                            pt0 = splinePoints.get(j + 1);
                            pt1 = lineutility.ExtendDirectedLine(splinePoints.get(j), splinePoints.get(j + 1), pt0, 2, 5);
                            lineObject.moveTo(pt1.x, pt1.y);
                            pt1 = lineutility.ExtendDirectedLine(splinePoints.get(j), splinePoints.get(j + 1), pt0, 3, 5);
                            lineObject.lineTo(pt1.x, pt1.y);
                        }
                        break;
                    case TacticalLines.CRACKS_SPECIFIC_LOCATION_GE:
                        for (j = 0; j < splinePoints.size() - 1; j++)
                        {
                            //get perpendicular points (point pair)
                            pt0 = splinePoints.get(j + 1);
                            pt1 = lineutility.ExtendDirectedLine(splinePoints.get(j), splinePoints.get(j + 1), pt0, 2, 5);
                            lineObject.moveTo(pt1.x, pt1.y);
                            pt1 = lineutility.ExtendDirectedLine(splinePoints.get(j), splinePoints.get(j + 1), pt0, 3, 5);
                            lineObject.lineTo(pt1.x, pt1.y);
                        }
                        if(i==array.size()-2)//the last point in the array
                        {
                            //lineObject=new GeneralPath();
                            lineObject.moveTo((int)splinePoints2.get(0).x,(int)splinePoints2.get(0).y);
                            for(j=1;j<splinePoints2.size();j++)
                                lineObject.lineTo((int)splinePoints2.get(j).x,(int)splinePoints2.get(j).y);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        catch (Exception exc) {
            //clsUtility.WriteFile("error in clsMETOC.DrawSplines");
               ErrorLogger.LogException(_className ,"DrawSplines",
                    new RendererException("Failed inside DrawSplines", exc));
        }
        return lineObject;
    }

    /**
     * Calculates a point on a segment using a ratio of the segment length.
     * This function is used for calculating control points on Bezier curves.
     *
     * @param P0 the 1st point on the segment.
     * @param P1 the last point on the segment
     * @param ratio the fraction of the segment length
     *
     * @return calculated point on the P0-P1 segment.
     */
    private static POINT2 getPointOnSegment(POINT2 P0, POINT2 P1, double ratio) {
        //return {x: (P0.x + ((P1.x - P0.x) * ratio)), y: (P0.y + ((P1.y - P0.y) * ratio))};
        //var pt:Point=new Point();
        POINT2 pt = new POINT2();
        try
        {
            pt.x = P0.x + (P1.x - P0.x) * ratio;
            pt.y = P0.y + (P1.y - P0.y) * ratio;
        }
        catch (Exception exc)
        {
            //clsUtility.WriteFile("error in clsMETOC.getPointOnSegment");
               ErrorLogger.LogException(_className ,"getPointOnSegment",
                    new RendererException("Failed inside getPointOnSegment", exc));
        }
        return pt;
    }

    /**
     * This function will trace a cubic approximation of the cubic Bezier
     * It will calculate a series of (control point/Destination point] which
     * will be used to draw quadratic Bezier starting from P0
     *
     * @param lineObject - the sprite to use for drawing
     * @param P0 - 1st client point
     * @param P1 - 1st control point for a cubic Bezier
     * @param P2 - 2nd control point
     * @param P3 - 2nd client point
     *
     * @return an array of points along the spline at linetype specific intervals
     */
    private static ArrayList drawCubicBezier2(
            TGLight tg,
            GeneralPath lineObject,
            POINT2 P0,
            POINT2 P1,
            POINT2 P2,
            POINT2 P3)
    {
        ArrayList<POINT2> array = new ArrayList();
        try {
            // this stuff may be unnecessary
            // calculates the useful base points
            POINT2 PA = getPointOnSegment(P0, P1, 0.75);
            POINT2 PB = getPointOnSegment(P3, P2, 0.75);

            // get 1/16 of the [P3, P0] segment
            double dx = (P3.x - P0.x) / 16d;
            double dy = (P3.y - P0.y) / 16d;

            // calculates control point 1
            POINT2 Pc_1 = getPointOnSegment(P0, P1, 0.375);

            // calculates control point 2
            POINT2 Pc_2 = getPointOnSegment(PA, PB, 0.375);
            Pc_2.x -= dx;
            Pc_2.y -= dy;

            // calculates control point 3
            POINT2 Pc_3 = getPointOnSegment(PB, PA, 0.375);
            Pc_3.x += dx;
            Pc_3.y += dy;

            // calculates control point 4
            POINT2 Pc_4 = getPointOnSegment(P3, P2, 0.375);

            // calculates the 3 anchor points
            POINT2 Pa_1 = lineutility.MidPointDouble(Pc_1, Pc_2, 0);
            POINT2 Pa_2 = lineutility.MidPointDouble(PA, PB, 0);
            POINT2 Pa_3 = lineutility.MidPointDouble(Pc_3, Pc_4, 0);
            switch (tg.get_LineType()) {   //draw the solid curve for these
                case TacticalLines.ISOBAR:
                case TacticalLines.UPPER_AIR:
                case TacticalLines.ISODROSOTHERM:
                case TacticalLines.ICE_EDGE:
                case TacticalLines.CRACKS:
                case TacticalLines.DEPTH_CURVE:
                case TacticalLines.DEPTH_CONTOUR:
                case TacticalLines.COASTLINE:
                case TacticalLines.PIER:
                case TacticalLines.RAMP_ABOVE_WATER:
                case TacticalLines.JETTY_ABOVE_WATER:
                case TacticalLines.SEAWALL:
                case TacticalLines.CABLE:
                case TacticalLines.ICE_OPENINGS_LEAD:
                case TacticalLines.ISOTACH:
                case TacticalLines.ISOTHERM:
                case TacticalLines.ISOPLETHS:
                case TacticalLines.ESTIMATED_ICE_EDGE:
                case TacticalLines.RAMP_BELOW_WATER:
                case TacticalLines.JETTY_BELOW_WATER:
                    lineObject.moveTo(P0.x, P0.y);
                    lineObject.curveTo(P1.x, P1.y, P2.x, P2.y, P3.x, P3.y);
                    return array;
                case TacticalLines.HOLD://added 1-3-11
                case TacticalLines.BRDGHD://added 1-3-11
                    lineObject.moveTo(P0.x, P0.y);
                    lineObject.curveTo(P1.x, P1.y, P2.x, P2.y, P3.x, P3.y);
                    //return if no fill, these points are for fill
                    if(tg.get_FillColor()==null || tg.get_FillColor().getAlpha()<2)
                        return array;
                    break;
                case TacticalLines.ICE_OPENINGS_LEAD_GE:
                case TacticalLines.CABLE_GE:
                case TacticalLines.SEAWALL_GE:
                case TacticalLines.JETTY_BELOW_WATER_GE:
                case TacticalLines.JETTY_ABOVE_WATER_GE:
                case TacticalLines.RAMP_ABOVE_WATER_GE:
                case TacticalLines.RAMP_BELOW_WATER_GE:
                case TacticalLines.PIER_GE:
                case TacticalLines.COASTLINE_GE:
                case TacticalLines.DEPTH_CONTOUR_GE:
                case TacticalLines.DEPTH_CURVE_GE:
                case TacticalLines.CRACKS_GE:
                case TacticalLines.ESTIMATED_ICE_EDGE_GE:
                case TacticalLines.ICE_EDGE_GE:
                case TacticalLines.ISOPLETHS_GE:
                case TacticalLines.ISOTACH_GE:
                case TacticalLines.ISOTHERM_GE:
                case TacticalLines.ISOBAR_GE:
                case TacticalLines.UPPER_AIR_GE:
                case TacticalLines.ISODROSOTHERM_GE:
                case TacticalLines.ICE_OPENINGS_FROZEN:
                case TacticalLines.ICE_OPENINGS_FROZEN_GE:
                case TacticalLines.ICE_EDGE_RADAR:
                case TacticalLines.ICE_EDGE_RADAR_GE:
                case TacticalLines.CRACKS_SPECIFIC_LOCATION:
                case TacticalLines.CRACKS_SPECIFIC_LOCATION_GE:
                case TacticalLines.EBB_TIDE:
                case TacticalLines.FLOOD_TIDE:
                case TacticalLines.EBB_TIDE_GE:
                case TacticalLines.FLOOD_TIDE_GE:
                case TacticalLines.JET:
                case TacticalLines.STREAM:
                case TacticalLines.JET_GE:
                case TacticalLines.STREAM_GE:
                case TacticalLines.HOLD_GE:
                case TacticalLines.BRDGHD_GE:
                    lineObject.moveTo(P0.x, P0.y);
                    lineObject.curveTo(P1.x, P1.y, P2.x, P2.y, P3.x, P3.y);
                    //do not return, we still need the spline points
                    //to claculate other features
                    break;
                default:
                    //the rest of them must use the calculated curve points
                    break;
            }
            //var sprite:Sprite;
            int j = 0;
            double distance;
            int n = 0;
            double x = 0, y = 0, increment = 0;
            POINT2 pt0, pt1, pt2;
            double t;
            POINT2 pt;
            array.clear();
            //distance=clsUtility.Distance2(P0,Pa_1);
            //add the curve points to tg.Pixels
            switch (tg.get_LineType()) {
                case TacticalLines.HOLD://added these 1-3-11. need small increment because these
                case TacticalLines.BRDGHD://points are used for fill which needs to confrom to the outline
                case TacticalLines.HOLD_GE://added these 1-3-11. need small increment because these
                case TacticalLines.BRDGHD_GE://points are used for fill which needs to confrom to the outline
                    increment = 3.0;
                    break;
                case TacticalLines.ICE_EDGE_RADAR:
                    increment = 10.0;
                    break;
                case TacticalLines.ICE_OPENINGS_FROZEN:
                case TacticalLines.ICE_OPENINGS_FROZEN_GE:
                case TacticalLines.CRACKS_SPECIFIC_LOCATION:
                case TacticalLines.CRACKS_SPECIFIC_LOCATION_GE:
                    //increment = 12.0;
                    increment = 7.0;
                    break;
                default:
                    increment = 10.0;
                    break;
            }

            distance = lineutility.CalcDistanceDouble(P0, Pa_1);
            if(distance<increment)
                distance=increment;
            n = (int) (distance / increment);
            
            pt0 = P0;
            pt1 = Pc_1;
            pt2 = Pa_1;
            for (j = 0; j < n; j++) {
                t = (double) j * (increment / distance);
                x = (1d - t) * (1d - t) * pt0.x + 2 * (1d - t) * t * pt1.x + t * t * pt2.x;
                y = (1d - t) * (1d - t) * pt0.y + 2 * (1d - t) * t * pt1.y + t * t * pt2.y;
                pt = new POINT2(x, y);
                //array.push(pt);
                array.add(pt);
            }
            //distance=clsUtility.Distance2(Pa_1,Pa_2);
            distance = lineutility.CalcDistanceDouble(Pa_1, Pa_2);

            //add the curve points to tg.Pixels
            n = (int) (distance / increment);
            pt0 = Pa_1;
            pt1 = Pc_2;
            pt2 = Pa_2;
            for (j = 0; j < n; j++) {
                t = (double) j * (increment / distance);
                x = (1d - t) * (1d - t) * pt0.x + 2 * (1d - t) * t * pt1.x + t * t * pt2.x;
                y = (1d - t) * (1d - t) * pt0.y + 2 * (1d - t) * t * pt1.y + t * t * pt2.y;
                pt = new POINT2(x, y);
                array.add(pt);
            }

            //distance=clsUtility.Distance2(Pa_2,Pa_3);
            distance = lineutility.CalcDistanceDouble(Pa_2, Pa_3);
            //add the curve points to tg.Pixels
            n = (int) (distance / increment);
            pt0 = Pa_2;
            pt1 = Pc_3;
            pt2 = Pa_3;
            for (j = 0; j < n; j++) {
                t = (double) j * (increment / distance);
                x = (1d - t) * (1d - t) * pt0.x + 2 * (1d - t) * t * pt1.x + t * t * pt2.x;
                y = (1d - t) * (1d - t) * pt0.y + 2 * (1d - t) * t * pt1.y + t * t * pt2.y;
                pt = new POINT2(x, y);
                array.add(pt);
            }
            //distance=clsUtility.Distance2(Pa_3,P3);
            distance = lineutility.CalcDistanceDouble(Pa_3, P3);
            //add the curve points to tg.Pixels
            n = (int) (distance / increment);
            pt0 = Pa_3;
            pt1 = Pc_4;
            pt2 = P3;
            for (j = 0; j < n; j++) {
                t = (double) j * (increment / distance);
                x = (1d - t) * (1d - t) * pt0.x + 2 * (1d - t) * t * pt1.x + t * t * pt2.x;
                y = (1d - t) * (1d - t) * pt0.y + 2 * (1d - t) * t * pt1.y + t * t * pt2.y;
                pt = new POINT2(x, y);
                array.add(pt);
            }
        }
        catch (Exception exc) {
            //System.out.println(e.getMessage());
            //clsUtility.WriteFile("error in clsMETOC.drawCubicBezier2");
               ErrorLogger.LogException(_className ,"drawCubicBezier2",
                    new RendererException("Failed inside drawCubicBezier2", exc));
        }
        return array;
    }

    /**
     *
     * Called by Splines2TG to get straight channel lines for splines.
     *
     * @param tg - TGlight
     * @deprecated
     *
     * @return An ArrayList to use for building the parallel splines
     */
    private static ArrayList ParallelLines(TGLight tg,int rev) {
        ArrayList<POINT2> channelPoints2 = new ArrayList();
        try {
            double[] pLinePoints = new double[tg.Pixels.size() * 2];
            double[] channelPoints = new double[6 * tg.Pixels.size()];
            int j = 0;
            for (j = 0; j < tg.Pixels.size(); j++) {
                pLinePoints[2 * j] = tg.Pixels.get(j).x;
                pLinePoints[2 * j + 1] = tg.Pixels.get(j).y;
            }
            //POINT2 pt=new POINT2(null);
            int numPoints = tg.Pixels.size();
            int channelWidth = 20;
            int usePtr = 0;
            ArrayList<Shape2> shapes = null;
            //double distanceToChannelPoint=20;
            //Channels.GetChannel1Double(pLinePoints, numPoints, numPoints, TacticalLines.CHANNEL, channelWidth, distanceToChannelPoint );

            try {
                CELineArray.CGetChannel2Double(pLinePoints, pLinePoints, channelPoints, numPoints, numPoints, (int) TacticalLines.CHANNEL, channelWidth, usePtr, shapes,rev);
            } catch (Exception e) {
                //clsUtility.WriteFile("error in clsMETOC.ParallelLines call to CGetChanne2lDouble");
                ErrorLogger.LogException(_className, "ParallelLines",
                    new RendererException("Failed inside ParallelLines", e));
            }

            POINT2 pt2 = null;
            int style = 0;
            for (j = 0; j < channelPoints.length / 3; j++) {
                pt2 = new POINT2(channelPoints[3 * j], channelPoints[3 * j + 1], style);
                channelPoints2.add(pt2);
            }
        } catch (Exception exc) {
            //System.out.println(e.getMessage());
            //clsUtility.WriteFile("error in clsMETOC.ParallelLines");
            ErrorLogger.LogException(_className, "ParallelLines",
                    new RendererException("Failed inside ParallelLines", exc));
        }
        return channelPoints2;
    }
    /**
     * Call this function with segment
     * @param Pixels a segment of tg.Pixels
     * @return
     */
    private static ArrayList ParallelLines2(ArrayList<POINT2> Pixels,int rev) {
        ArrayList<POINT2> channelPoints2 = new ArrayList();
        try {
            double[] pLinePoints = new double[Pixels.size() * 2];
            double[] channelPoints = new double[6 * Pixels.size()];
            int j = 0;
            for (j = 0; j < Pixels.size(); j++) {
                pLinePoints[2 * j] = Pixels.get(j).x;
                pLinePoints[2 * j + 1] = Pixels.get(j).y;
            }
            //POINT2 pt=new POINT2(null);
            int numPoints = Pixels.size();
            int channelWidth = 20;
            int usePtr = 0;
            ArrayList<Shape2> shapes = null;
            //double distanceToChannelPoint=20;
            //Channels.GetChannel1Double(pLinePoints, numPoints, numPoints, TacticalLines.CHANNEL, channelWidth, distanceToChannelPoint );

            try {
                CELineArray.CGetChannel2Double(pLinePoints, pLinePoints, channelPoints, numPoints, numPoints, (int) TacticalLines.CHANNEL, channelWidth, usePtr, shapes,rev);
            } catch (Exception e) {
                //clsUtility.WriteFile("error in clsMETOC.ParallelLines call to CGetChanne2lDouble");
                ErrorLogger.LogException(_className, "ParallelLines2",
                    new RendererException("Failed inside ParallelLines2", e));
            }

            POINT2 pt2 = null;
            int style = 0;
            for (j = 0; j < channelPoints.length / 3; j++) {
                pt2 = new POINT2(channelPoints[3 * j], channelPoints[3 * j + 1], style);
                channelPoints2.add(pt2);
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "ParallelLines2",
                    new RendererException("Failed inside ParallelLines2", exc));
        }
        return channelPoints2;
    }
}
