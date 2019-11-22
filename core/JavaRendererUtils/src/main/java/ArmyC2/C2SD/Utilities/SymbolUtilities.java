/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ArmyC2.C2SD.Utilities;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.regex.Pattern;


/**
 *
 * @author michael.spinelli
 */
public class SymbolUtilities {



    private static SimpleDateFormat dateFormatFront = new SimpleDateFormat("ddHHmmss",Locale.US);
    private static SimpleDateFormat dateFormatBack = new SimpleDateFormat("MMMyy",Locale.US);
    private static SimpleDateFormat dateFormatFull = new SimpleDateFormat("ddHHmmssZMMMyy",Locale.US);
    private static SimpleDateFormat dateFormatZulu = new SimpleDateFormat("Z",Locale.US);
    
  //this regex is from: https://docs.oracle.com/javase/7/docs/api/java/lang/Double.html
  private static final String Digits     = "(\\p{Digit}+)";
  private static final String HexDigits  = "(\\p{XDigit}+)";
  // an exponent is 'e' or 'E' followed by an optionally
  // signed decimal integer.
  private static final String Exp        = "[eE][+-]?"+Digits;
  private static final String fpRegex    =
      ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
       "[+-]?(" + // Optional sign character
       "NaN|" +           // "NaN" string
       "Infinity|" +      // "Infinity" string

       // A decimal floating-point string representing a finite positive
       // number without a leading sign has at most five basic pieces:
       // Digits . Digits ExponentPart FloatTypeSuffix
       //
       // Since this method allows integer-only strings as input
       // in addition to strings of floating-point literals, the
       // two sub-patterns below are simplifications of the grammar
       // productions from section 3.10.2 of
       // The Java™ Language Specification.

       // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
       "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

       // . Digits ExponentPart_opt FloatTypeSuffix_opt
       "(\\.("+Digits+")("+Exp+")?)|"+

       // Hexadecimal strings
       "((" +
        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "(\\.)?)|" +

        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

        ")[pP][+-]?" + Digits + "))" +
       "[fFdD]?))" +
       "[\\x00-\\x20]*");// Optional trailing "whitespace"

  private static final Pattern pIsNumber = Pattern.compile(fpRegex);
  
  private static RendererSettings rendererSettings = RendererSettings.getInstance();
  
  /*if (Pattern.matches(fpRegex, myString))
      Double.valueOf(myString); // Will not throw NumberFormatException
  else {
      // Perform suitable alternative action
       }*/

  /**
   * @name getBasicSymbolID
   *
   * @desc Returns a formatted string that has only the necessary static
   * characters needed to draw a symbol. For instance
   * GetBasicSymbolID("GFTPGLB----K---") returns "G*T*GLB---****X"
   *
   * @param strSymbolID - IN - A 15 character MilStd code
   * @return A properly formated basic symbol ID
   */
  public static String getBasicSymbolID(String strSymbolID)
  {
    try
    {
        String strRetSymbolID = null;
      if((strSymbolID != null) && (strSymbolID != null) && (strSymbolID.length() == 15))
      {
        // Check to make sure it is a tacitcal graphic symbol.
        if((isWeather(strSymbolID)) || (isBasicShape(strSymbolID)) || (isBridge(strSymbolID)))
        {
          return strSymbolID;
        }
        else if(isTacticalGraphic(strSymbolID)==true)
        {
          strRetSymbolID = strSymbolID.substring(0, 1)
            + "*"
            + strSymbolID.substring(2, 3)
            + "*"
            + strSymbolID.substring(4, 10)
            + "****"
            + "X";
          
          if(isEMSNaturalEvent(strSymbolID)==true)
          {
              strRetSymbolID = strRetSymbolID.substring(0, 14) + "*";
          }
          
          return strRetSymbolID;
        }
        else if(isWarfighting(strSymbolID))
        {
          strRetSymbolID = strSymbolID.substring(0, 1)
            + "*"
            + strSymbolID.substring(2, 3)
            + "*"
            //+ strSymbolID.substring(4, 15);
            + strSymbolID.substring(4, 10);
          if(isSIGINT(strSymbolID))
            strRetSymbolID = strRetSymbolID + "--***";
          else if(isInstallation(strSymbolID))
              strRetSymbolID = strRetSymbolID + "H****";
          else
          {
              strRetSymbolID = strRetSymbolID + "*****";
              UnitDefTable udt = UnitDefTable.getInstance();
                String temp = strRetSymbolID;
                for(int i = 0; i < 2; i++)
                {
                    if(udt.HasUnitDef(temp,i)==true)
                    {
                        return temp;
                    }
                    else
                    {
                        temp = temp.substring(0,10) + "H****";
                        if(udt.HasUnitDef(temp,i)==true)
                        {
                            return temp;
                        }
                        else
                        {
                            temp = temp.substring(0,10) + "MO***";
                            if(udt.HasUnitDef(temp,i)==true)
                            {
                                return temp;
                            }
                        }
                    }
                    temp = temp.substring(0,10) + "*****";
                }
          }
            
          return strRetSymbolID;
        }
        else if(isEngineeringOverlayObstacle(strSymbolID))
        {
            strRetSymbolID = strSymbolID.substring(0, 1)
            + "*"
            + strSymbolID.substring(2, 3)
            + "*"
            + strSymbolID.substring(4, 10)
            + "****"
            + "X";
          return strRetSymbolID;

        }
        else
        // Don't do anything for bridge symbols
        {
          return strSymbolID;
        }
      }
      else
      {
        return strSymbolID;
      }
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return "";
  }
  
    public static String getBasicSymbolIDStrict(String strSymbolID)
    {
        StringBuilder sb = new StringBuilder();
        if(strSymbolID != null && strSymbolID.length() == 15)
        {
            char scheme = strSymbolID.charAt(0);
            if(scheme == 'G')
            {
                sb.append(strSymbolID.charAt(0));
                sb.append("*");
                sb.append(strSymbolID.charAt(2));
                sb.append("*");
                sb.append(strSymbolID.substring(4, 10));
                sb.append("****X");
            }
            else if(scheme != 'W' && scheme != 'B'  && scheme != 'P')
            {
                sb.append(strSymbolID.charAt(0));
                sb.append("*");
                sb.append(strSymbolID.charAt(2));
                sb.append("*");
                sb.append(strSymbolID.substring(4, 10));
                sb.append("*****");
            }
            else
            {
                return strSymbolID;
            }
            return sb.toString();
        }
        return strSymbolID;
    }



       public static String reconcileSymbolID(String symbolID)
       {
          return reconcileSymbolID(symbolID, false);
       }
  
       public static String reconcileSymbolID(String symbolID, boolean isMultiPoint)
       {
           StringBuilder sb = new StringBuilder("");
            char codingScheme = symbolID.charAt(0);
            
            if(symbolID.startsWith("BS_") || symbolID.startsWith("BBS_") || symbolID.startsWith("PBS_"))
            {
                return symbolID;
            }
            
            if(symbolID.length() < 15)
            {
                while (symbolID.length() < 15)
                {
                    symbolID += "-";
                }
            }
            if(symbolID.length() > 15)
            {
                symbolID = symbolID.substring(0, 15);
            }

            if(symbolID != null && symbolID.length()==15)
            {
                if(codingScheme=='S' || //warfighting
                        codingScheme=='I' ||//sigint
                        codingScheme=='O' ||//stability operation
                        codingScheme=='E')//emergency management
                {
                    sb.append(codingScheme);

                    if(SymbolUtilities.hasValidAffiliation(symbolID)==false)
                        sb.append('U');
                    else
                        sb.append(symbolID.charAt(1));

                    if(SymbolUtilities.hasValidBattleDimension(symbolID)==false)
                    {
                        sb.append('Z');
                        sb.replace(0, 1, "S");
                    }
                    else
                        sb.append(symbolID.charAt(2));

                    if(SymbolUtilities.hasValidStatus(symbolID)==false)
                        sb.append('P');
                    else
                        sb.append(symbolID.charAt(3));

                    sb.append("------");
                    sb.append(symbolID.substring(10, 15));

                }
                else if(codingScheme=='G')//tactical
                {
                    sb.append(codingScheme);

                    if(SymbolUtilities.hasValidAffiliation(symbolID)==false)
                        sb.append('U');
                    else
                        sb.append(symbolID.charAt(1));

                    //if(SymbolUtilities.hasValidBattleDimension(SymbolID)==false)
                        sb.append('G');
                    //else
                    //    sb.append(SymbolID.charAt(2));

                    if(SymbolUtilities.hasValidStatus(symbolID)==false)
                        sb.append('P');
                    else
                        sb.append(symbolID.charAt(3));

                    if(isMultiPoint)
                        sb.append("GAG---");//return a boundary
                    else
                        sb.append("GPP---");//return an action point
                    
                    sb.append(symbolID.substring(10, 15));

                }
                else if(codingScheme=='W')//weather
                {//no default weather graphic
                    return "SUZP-----------";//unknown
                }
                else//bad codingScheme
                {
                    sb.append('S');
                    if(SymbolUtilities.hasValidAffiliation(symbolID)==false)
                        sb.append('U');
                    else
                        sb.append(symbolID.charAt(1));

                    if(SymbolUtilities.hasValidBattleDimension(symbolID)==false)
                    {
                        sb.append('Z');
                        //sb.replace(0, 1, "S");
                    }
                    else
                        sb.append(symbolID.charAt(2));

                    if(SymbolUtilities.hasValidStatus(symbolID)==false)
                        sb.append('P');
                    else
                        sb.append(symbolID.charAt(3));

                    sb.append("------");
                    sb.append(symbolID.substring(10, 15));
                }
            }
            else
            {
                return "SUZP-----------";//unknown
            }
            return sb.toString();
       }
  
      
       /**
        * Returns true if the SymbolID has a valid Status (4th character)
        * @param SymbolID
        * @return
        */
       public static Boolean hasValidStatus(String SymbolID)
       {
           if(SymbolID != null && SymbolID.length()>=10)
           {
               char status = SymbolID.charAt(3);
               
               char codingScheme = SymbolID.charAt(0);
               
               if(codingScheme=='S' || //warfighting
                    codingScheme=='I' ||//sigint
                    codingScheme=='O' ||//stability operation
                    codingScheme=='E')//emergency management
               {
                   if(status == 'A' ||
                       status == 'P' ||
                       status == 'C' ||
                       status == 'D' ||
                       status == 'X' ||
                       status == 'F')
                   {
                       return true;
                   }
                   else
                       return false;
               }
               else if(codingScheme=='G')
               {
                   if(status == 'A' ||
                       status == 'S' ||
                       status == 'P' ||
                       status == 'K')
                   {
                       return true;
                   }
                   else
                       return false;
               }
               else if(codingScheme=='W')
               {
                   return true;//doesn't apply
               }
               
               return false;
           }
           else
               return false;
       }

       /**
        * Returns true if the SymbolID has a valid Affiliation (2nd character)
        * @param SymbolID
        * @return
        */
       public static Boolean hasValidAffiliation(String SymbolID)
       {
           if(SymbolID != null && SymbolID.length()>=10)
           {
               char affiliation = SymbolID.charAt(1);
               if(affiliation == 'P' ||
                       affiliation == 'U' ||
                       affiliation == 'A' ||
                       affiliation == 'F' ||
                       affiliation == 'N' ||
                       affiliation == 'S' ||
                       affiliation == 'H' ||
                       affiliation == 'G' ||
                       affiliation == 'W' ||
                       affiliation == 'M' ||
                       affiliation == 'D' ||
                       affiliation == 'L' ||
                       affiliation == 'J' ||
                       affiliation == 'K')
                   return true;
               else
                   return false;
           }
           else
               return false;
       }

       public static Boolean hasValidCodingScheme(String symbolID)
       {
           if(symbolID != null && symbolID.length()>0)
           {
               char codingScheme = symbolID.charAt(0);
               if(codingScheme=='S'||
                       codingScheme=='G'||
                       codingScheme=='W'||
                       codingScheme=='I'||
                       codingScheme=='O'||
                       codingScheme=='E')
               {
                   return true;
               }
               else
               {
                   return false;
               }
           }
           else
           {
               return false;
           }
       }
       
       /**
        * Returns true if the SymbolID has a valid BattleDimension (3rd character)
        * "Category" for tactical graphics
        * @param SymbolID 15 character String
        * @return
        */
       public static Boolean hasValidBattleDimension(String SymbolID)
       {
           if(SymbolID != null && SymbolID.length()>=10)
           {
               char codingScheme = SymbolID.charAt(0);
               char bd = SymbolID.charAt(2);

               if(codingScheme=='S')//warfighting
               {
                   if(bd == 'P' ||
                       bd == 'A' ||
                       bd == 'G' ||
                       bd == 'S' ||
                       bd == 'U' ||
                       bd == 'F' ||
                       //status == 'X' ||//doesn't seem to be a valid use for this one
                       bd == 'Z')
                       return true;
                   else
                       return false;
               }
               else if(codingScheme=='O')//stability operation
               {
                   if(bd == 'V' ||
                       bd == 'L' ||
                       bd == 'O' ||
                       bd == 'I' ||
                       bd == 'P' ||
                       bd == 'G' ||
                       bd == 'R')
                       return true;
                   else
                       return false;
               }
               else if(codingScheme=='E')//emergency management
               {
                   if(bd == 'I' ||
                       bd == 'N' ||
                       bd == 'O' ||
                       bd == 'F')
                       return true;
                   else
                       return false;
               }
               else if(codingScheme=='G')//tactical grahpic
               {
                   if(bd == 'T' ||
                       bd == 'G' ||
                       bd == 'M' ||
                       bd == 'F' ||
                       bd == 'S' ||
                       bd == 'O')
                   {
                       return true;
                   }
                   else
                       return false;
               }
               else if(codingScheme=='W')//weather
               {
                   return true;//doesn't apply
               }
               else if(codingScheme=='I')//sigint
               {
                   if(bd == 'P' ||
                       bd == 'A' ||
                       bd == 'G' ||
                       bd == 'S' ||
                       bd == 'U' ||
                       //status == 'X' ||//doesn't seem to be a valid use for this one
                       bd == 'Z')
                       return true;
                   else
                       return false;
               }
               else//bad codingScheme, can't confirm battle dimension
                    return false;
           }
           else
               return false;
       }


       public static Boolean hasValidCountryCode(String symbolID)
       {
           if(Character.isLetter(symbolID.charAt(12)) && 
                   Character.isLetter(symbolID.charAt(13)))
               return true;
           else
               return false;
       }
       /**
        * converts a Java Date object into a properly formated String for
        * W or W1
        * @param time
        * @return
        */
       public static String getDateLabel(Date time)
       {

            String modifierString = null;
            
            String zulu = "";
            zulu = dateFormatZulu.format(time);
            
            if(zulu != null && zulu.length()==5)
            {

                if(zulu.startsWith("+"))//Integer.valueOf doesn't like '+'
                    zulu = zulu.substring(1,3);
                else
                    zulu = zulu.substring(0,3);
                
                int intZulu = Integer.valueOf(zulu);
                
                zulu = getZuluCharFromTimeZoneOffset(intZulu);
            }
            else
                zulu = getZuluCharFromTimeZoneOffset(time);
            
            
            modifierString = dateFormatFront.format(time) + zulu + dateFormatBack.format(time);

            return modifierString.toUpperCase();
       }
       
       /**
        * Given date, return character String representing
        * which NATO time zone you're in.
        * @param hour
        * @return 
        */
       private static String getZuluCharFromTimeZoneOffset(Date time)
        {
                TimeZone tz = TimeZone.getDefault();
                Date offset = new Date(tz.getOffset(time.getTime()));
                long lOffset = offset.getTime() / 3600000;//3600000 = (1000(ms)*60(s)*60(m))

                int hour = (int)lOffset;

                return getZuluCharFromTimeZoneOffset(hour);
        }
       
       /**
        * Given hour offset from Zulu return character String representing
        * which NATO time zone you're in.
        * @param hour
        * @return 
        */
       private static String getZuluCharFromTimeZoneOffset(int hour)
       {
                if(hour==0)
                        return "Z";
                else if(hour==-1)
                        return "N";
                else if(hour==-2)
                        return "O";
                else if(hour==-3)
                        return "P";
                else if(hour==-4)
                        return "Q";
                else if(hour==-5)
                        return "R";
                else if(hour==-6)
                        return "S";
                else if(hour==-7)
                        return "T";
                else if(hour==-8)
                        return "U";
                else if(hour==-9)
                        return "V";
                else if(hour==-10)
                        return "W";
                else if(hour==-11)
                        return "X";
                else if(hour==-12)
                        return "Y";
                else if(hour==1)
                        return "A";
                else if(hour==2)
                        return "B";
                else if(hour==3)
                        return "C";
                else if(hour==4)
                        return "D";
                else if(hour==5)
                        return "E";
                else if(hour==6)
                        return "F";
                else if(hour==7)
                        return "G";
                else if(hour==8)
                        return "H";
                else if(hour==9)
                        return "I";
                else if(hour==10)
                        return "K";
                else if(hour==11)
                        return "L";
                else if(hour==12)
                        return "M";
                else
                    return "-";
       }
       

       /**
        * 
        * @param symbolID
        * @param unitModifier
        * @return 
        */
       public static boolean canUnitHaveModifier(String symbolID, String unitModifier)
       {
           boolean returnVal = false;
           try
           {
               if(unitModifier.equals(ModifiersUnits.B_ECHELON))
               {
                   return (SymbolUtilities.isUnit(symbolID) || SymbolUtilities.isSTBOPS(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.C_QUANTITY))
               {
                   return (SymbolUtilities.isEquipment(symbolID) ||
                           SymbolUtilities.isEMSEquipment(symbolID) ||
                           SymbolUtilities.isEMSIncident(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.D_TASK_FORCE_INDICATOR))
               {
                   return (SymbolUtilities.isUnit(symbolID) ||
                           SymbolUtilities.isSTBOPS(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.F_REINFORCED_REDUCED))
               {
                   return (SymbolUtilities.isUnit(symbolID) ||
                           SymbolUtilities.isSTBOPS(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.G_STAFF_COMMENTS))
               {
                   return (SymbolUtilities.isEMS(symbolID) == false);
               }
               else if(unitModifier.equals(ModifiersUnits.H_ADDITIONAL_INFO_1))
               {
                   return true;
               }
               else if(unitModifier.equals(ModifiersUnits.J_EVALUATION_RATING))
               {
                   return true;
               }
               else if(unitModifier.equals(ModifiersUnits.K_COMBAT_EFFECTIVENESS))
               {
                   return (SymbolUtilities.isUnit(symbolID) ||
                           SymbolUtilities.isSTBOPS(symbolID) ||
                           (SymbolUtilities.hasInstallationModifier(symbolID) && SymbolUtilities.isEMS(symbolID)==false));
               }
               else if(unitModifier.equals(ModifiersUnits.L_SIGNATURE_EQUIP))
               {
                   return (SymbolUtilities.isEquipment(symbolID) ||
                           SymbolUtilities.isSIGINT(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.M_HIGHER_FORMATION))
               {
                   return (SymbolUtilities.isUnit(symbolID) ||
                           SymbolUtilities.isSIGINT(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.N_HOSTILE))
               {
                   return (SymbolUtilities.isEquipment(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.P_IFF_SIF))
               {
                   return (symbolID.charAt(0) == 'S' ||
                           SymbolUtilities.isSTBOPS(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.Q_DIRECTION_OF_MOVEMENT))
               {
                   return ((SymbolUtilities.hasInstallationModifier(symbolID) == false) &&
                           (SymbolUtilities.isSIGINT(symbolID) == false));
               }
               else if(unitModifier.equals(ModifiersUnits.R_MOBILITY_INDICATOR))
               {
                   return (SymbolUtilities.isEquipment(symbolID) ||
                           SymbolUtilities.isEMSEquipment(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.R2_SIGNIT_MOBILITY_INDICATOR))
               {
                   return (SymbolUtilities.isSIGINT(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.S_HQ_STAFF_OR_OFFSET_INDICATOR))
               {
                   return (SymbolUtilities.isSIGINT(symbolID)==false);
               }
               else if(unitModifier.equals(ModifiersUnits.T_UNIQUE_DESIGNATION_1))
               {
                   return true;
               }
               else if(unitModifier.equals(ModifiersUnits.V_EQUIP_TYPE))
               {
                   return (SymbolUtilities.isEquipment(symbolID) ||
                           SymbolUtilities.isSIGINT(symbolID) ||
                           SymbolUtilities.isEMSEquipment(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.W_DTG_1))
               {
                   return true;
               }
               else if(unitModifier.equals(ModifiersUnits.X_ALTITUDE_DEPTH))
               {
                   return (SymbolUtilities.isSIGINT(symbolID)==false);
               }
               else if(unitModifier.equals(ModifiersUnits.Y_LOCATION))
               {
                   return true;
               }
               else if(unitModifier.equals(ModifiersUnits.Z_SPEED))
               {
                   return ((SymbolUtilities.hasInstallationModifier(symbolID)==false) &&
                           (SymbolUtilities.isSIGINT(symbolID)==false));
               }
               else if(unitModifier.equals(ModifiersUnits.AA_SPECIAL_C2_HQ))
               {
                   return (SymbolUtilities.isUnit(symbolID) ||
                           SymbolUtilities.isSTBOPS(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.AB_FEINT_DUMMY_INDICATOR))
               {
                   return ((SymbolUtilities.isSIGINT(symbolID)==false) &&
                           (SymbolUtilities.isEMS(symbolID)==false));
               }
               else if(unitModifier.equals(ModifiersUnits.AC_INSTALLATION))
               {
                   return (SymbolUtilities.isSIGINT(symbolID)==false);
               }
               else if(unitModifier.equals(ModifiersUnits.AD_PLATFORM_TYPE))
               {
                   return (SymbolUtilities.isSIGINT(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.AE_EQUIPMENT_TEARDOWN_TIME))
               {
                   return (SymbolUtilities.isSIGINT(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.AF_COMMON_IDENTIFIER))
               {
                   return (SymbolUtilities.isSIGINT(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.AG_AUX_EQUIP_INDICATOR))
               {
                   return (SymbolUtilities.isEquipment(symbolID));
               }
               else if(unitModifier.equals(ModifiersUnits.AH_AREA_OF_UNCERTAINTY) ||
                       unitModifier.equals(ModifiersUnits.AI_DEAD_RECKONING_TRAILER) ||
                       unitModifier.equals(ModifiersUnits.AJ_SPEED_LEADER))
               {
                   return ((SymbolUtilities.isSIGINT(symbolID)==false) &&
                           (SymbolUtilities.hasInstallationModifier(symbolID)==false));
               }
               else if(unitModifier.equals(ModifiersUnits.AK_PAIRING_LINE))
               {
                   return ((SymbolUtilities.isSIGINT(symbolID)==false) &&
                           (SymbolUtilities.isEMS(symbolID)==false) &&
                           (SymbolUtilities.hasInstallationModifier(symbolID)==false));
               }
               else if(unitModifier.equals(ModifiersUnits.AL_OPERATIONAL_CONDITION))
               {
                   return (SymbolUtilities.isUnit(symbolID)==false);
               }
               else if(unitModifier.equals(ModifiersUnits.AO_ENGAGEMENT_BAR))
               {
                   return ((SymbolUtilities.isEquipment(symbolID) ||
                           SymbolUtilities.isUnit(symbolID) ||
                           SymbolUtilities.hasInstallationModifier(symbolID)) &&
                           SymbolUtilities.isEMS(symbolID)==false);
               }
               //out of order because used less often
               else if(unitModifier.equals(ModifiersUnits.A_SYMBOL_ICON))
               {
                   return true;
               }
               else if(unitModifier.equals(ModifiersUnits.E_FRAME_SHAPE_MODIFIER))
               {
                   //return (SymbolUtilities.isSIGINT(symbolID)==false);
                   //not sure why milstd say sigint don't have it.
                   //they clearly do.
                   return true;
               }
               else if(unitModifier.equals(ModifiersUnits.SCC_SONAR_CLASSIFICATION_CONFIDENCE))
               {
                   if(SymbolUtilities.isSubSurface(symbolID))
                   {
                       //these symbols only exist in 2525C
                       String temp = symbolID.substring(4, 10);
                       if(temp.equals("WMGC--") ||
                               temp.equals("WMMC--") ||
                               temp.equals("WMFC--") ||
                               temp.equals("WMC---"))
                       {
                           return true;
                       }
                   }
                   
                   return false;
               }
               else
                   return false;
               
                
           }
           catch(Exception exc)
           {
               ErrorLogger.LogException("SymbolUtilties", "canHaveModifier", exc);
           }
           return returnVal;
       }
       
       public static Boolean hasModifier(String symbolID, String modifier)
       {
           return hasModifier(symbolID, modifier, RendererSettings.getInstance().getSymbologyStandard());
       }
       
       /**
        * 
        * @param symbolID
        * @param modifier - from the constants ModifiersUnits or ModifiersTG
        * @param symStd - 0=2525B, 1=2525C.  Constants available in RendererSettings.
        * @return
        */
        public static Boolean hasModifier(String symbolID, String modifier, int symStd){
           Boolean returnVal = false;
           
           if(isTacticalGraphic(symbolID) == true)
           {
               returnVal = canSymbolHaveModifier(symbolID, modifier, symStd);
           }
           else
           {
               returnVal = canUnitHaveModifier(symbolID, modifier);
           }
           return returnVal;
       };

       /**
        * Checks if a tactical graphic has the passed modifier.
        * @param symbolID - symbolID of Tactical Graphic
        * @param tgModifier - ModifiersTG.AN_AZIMUTH
        * @return 
        */
       public static boolean canSymbolHaveModifier(String symbolID, String tgModifier)
       {
           return canSymbolHaveModifier(symbolID, tgModifier, RendererSettings.getInstance().getSymbologyStandard());
       }
       
        /**
        * Checks if a tactical graphic has the passed modifier.
        * @param symbolID - symbolID of Tactical Graphic
        * @param tgModifier - ModifiersTG.AN_AZIMUTH
        * @param symStd - like RendererSettings.Symbology_2525C
        * @return 
        */
       public static boolean canSymbolHaveModifier(String symbolID, String tgModifier, int symStd)
       {
           String basic = null;
           SymbolDef sd = null;
           boolean returnVal = false;
           
            try
            {
                 
                basic = SymbolUtilities.getBasicSymbolID(symbolID);
                sd = SymbolDefTable.getInstance().getSymbolDef(basic, symStd);
                if(sd != null)
                {
                    int dc = sd.getDrawCategory();
                    if(tgModifier.equals(ModifiersTG.AM_DISTANCE))
                    {
                        switch(dc)
                        {
                            case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE:
                            case SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                            case SymbolDef.DRAW_CATEGORY_TWO_POINT_RECT_PARAMETERED_AUTOSHAPE: 
                                returnVal = true;
                                break;
                            case SymbolDef.DRAW_CATEGORY_CIRCULAR_PARAMETERED_AUTOSHAPE:
                            case SymbolDef.DRAW_CATEGORY_CIRCULAR_RANGEFAN_AUTOSHAPE:
                                returnVal = true;
                                break;
                            case SymbolDef.DRAW_CATEGORY_LINE:
                                if(sd.getModifiers().indexOf(tgModifier + ".")>-1)
                                    returnVal = true;
                                break;
                            default:
                                returnVal = false;
                        }
                    }
                    else if(tgModifier.equals(ModifiersTG.AN_AZIMUTH))
                    {
                        switch(dc)
                        {
                            case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE:
                            case SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                                returnVal = true;
                                break;
                            default:
                                returnVal = false;
                        }
                    }
                    else
                    {
                        if(sd.getModifiers().indexOf(tgModifier + ".")>-1)
                            returnVal = true;
                    }
                }

                return returnVal;
                

            }
            catch(Exception exc)
            {
                ErrorLogger.LogException("SymbolUtilties", "canSymbolHaveModifier", exc);
            }
            return returnVal;
       }
       
        /**
         * Gets line color used if no line color has been set. The color is specified based on the affiliation of
         * the symbol and whether it is a unit or not.
         * @param symbolID
         * @return
         */
         public static Color getLineColorOfAffiliation(String symbolID)
        {
            Color retColor = null;
            String basicSymbolID = getBasicSymbolID(symbolID);
            try
            {
                // We can't get the fill color if there is no symbol id, since that also means there is no affiliation
                if((symbolID == null) || (symbolID.equals("")))
                {
                        return retColor;
                }

                if (SymbolUtilities.isTacticalGraphic(symbolID))// && !SymbolUtilities.isTGWithFill(symbolID))
                {
                    if((symbolID.substring(0, 4).equals("ESRI")) || SymbolUtilities.isJWARN(symbolID))
                    {
                        //retColor = Color.BLACK;//0x000000;	// Black
                        retColor =  rendererSettings.getFriendlyGraphicLineColor();
                    }
                    else if (SymbolUtilities.isWeather(symbolID))
                    {
                        retColor = getLineColorOfWeather(symbolID);
                    }
                    else if(SymbolUtilities.isEMSNaturalEvent(symbolID))
                    {
                        //retColor = Color.black;
                        retColor =  rendererSettings.getFriendlyGraphicLineColor();
                    }
                    else if(SymbolUtilities.isObstacle(symbolID))
                    {
                        //retColor = Color.GREEN;	// Green
                        retColor =  rendererSettings.getNeutralGraphicLineColor();
                    }
                    else if ((SymbolUtilities.isNBC(symbolID)) && 
                            (basicSymbolID.equals("G*M*NR----****X")==true || //Radioactive Area
                            basicSymbolID.equals("G*M*NC----****X")==true || //Chemically Contaminated Area
                            basicSymbolID.equals("G*M*NB----****X")==true)) //Biologically Contaminated Area
                    {
                        //retColor = Color.BLACK;//0xffff00;
                        retColor =  rendererSettings.getFriendlyGraphicLineColor();//
                    }
                    else
                    {
                        String switchChar = symbolID.substring(1, 2);
                            if(switchChar.equals("F") ||
                                switchChar.equals("A") ||
                                switchChar.equals("D") ||
                                switchChar.equals("M"))
                            {
                                 retColor =  rendererSettings.getFriendlyGraphicLineColor();
                            }
                            else if(switchChar.equals("H") ||
                                switchChar.equals("S") ||
                                switchChar.equals("J") ||
                                switchChar.equals("K"))
                            {

                                if (SymbolUtilities.getBasicSymbolID(symbolID).equals("G*G*GLC---****X"))	// Line of Contact
                                {
                                    //retColor = Color.BLACK;//0x000000;	// Black
                                    retColor = rendererSettings.getFriendlyGraphicLineColor();
                                }
                                else
                                {
                                    //retColor = Color.RED;//0xff0000;	// Red
                                    retColor = rendererSettings.getHostileGraphicLineColor();
                                }

                            }
                            else if(switchChar.equals("N") ||
                                    switchChar.equals("L"))	// Neutral:
                            {
                                    //retColor = Color.GREEN;//0x00ff00;	// Green
                                    retColor = rendererSettings.getNeutralGraphicLineColor();

                            }
                            else if(switchChar.equals("U") ||
                                switchChar.equals("P") ||
                                switchChar.equals("O") ||
                                switchChar.equals("G") ||
                                switchChar.equals("W"))
                            {
                                if (symbolID.substring(0, 8).equals("WOS-HDS-"))
                                {
                                    retColor = Color.GRAY;//0x808080;	// Gray
                                }
                                else
                                {
                                    //retColor = Color.YELLOW;//0xffff00;	// Yellow
                                    retColor =  rendererSettings.getUnknownGraphicLineColor();
                                }

                            }
                            else
                            {
                                    //retColor = Color.black;//null;//0;//Color.Empty;
                                    //retColor = rendererSettings.getFriendlyGraphicLineColor();
                                    retColor = rendererSettings.getFriendlyUnitLineColor();

                            }	// End default

                    }	// End else
                }// End if (SymbolUtilities.IsTacticalGraphic(this.SymbolID))
                else
                {
                    //stopped doing check because all warfighting
                    //should have black for line color.
                    //retColor = Color.BLACK;
                    retColor = rendererSettings.getFriendlyUnitLineColor();

                }	// End else
            }	// End try
            catch(Exception e)
            {
                    // Log Error
                    ErrorLogger.LogException("SymbolUtilties", "getLineColorOfAffiliation", e);
                    //throw e;
            }	// End catch
            return retColor;
        }	// End get LineColorOfAffiliation



            /**
                * Is the fill color used if no fill color has been set. The color is specified based on the affiliation
                * of the symbol and whether it is a unit or not.
                * @param symbolID
                * @return
                */
            public static Color getFillColorOfAffiliation(String symbolID)
            {
                    Color retColor = null;
                    String basicSymbolID = getBasicSymbolID(symbolID);

                    try
                    {
                        String switchChar;
                            // We can't get the fill color if there is no symbol id, since that also means there is no affiliation
                            if((symbolID == null) || (symbolID.equals("")))
                            {
                                    return retColor;
                            }

                            if(basicSymbolID.equals("G*M*NZ----****X") ||//ground zero
                                //basicSymbolID.equals("G*M*NF----****X") || //fallout producing
                                basicSymbolID.equals("G*M*NEB---****X") ||//biological
                                basicSymbolID.equals("G*M*NEC---****X"))//chemical
                            {
                                    //retColor = AffiliationColors.UnknownUnitFillColor;//  Color.yellow;
                                    retColor = rendererSettings.getUnknownUnitFillColor();
                            }
                            else if(SymbolUtilities.isTacticalGraphic(symbolID) && !SymbolUtilities.isTGSPWithFill(symbolID))
                            {
                                    if(basicSymbolID.equals("G*M*NZ----****X") ||//ground zero
                                    //basicSymbolID.equals("G*M*NF----****X") || //fallout producing
                                    basicSymbolID.equals("G*M*NEB---****X") ||//biological
                                    basicSymbolID.equals("G*M*NEC---****X"))//chemical
                                    {
                                        //retColor = Color.yellow;
                                        retColor = rendererSettings.getUnknownUnitFillColor();
                                        
                                    }
                                    else
                                    {
                                        switchChar = symbolID.substring(1, 2);
                                        if(switchChar.equals("F") ||
                                            switchChar.equals("A") ||
                                            switchChar.equals("D") ||
                                            switchChar.equals("M"))
                                        {
                                                //retColor = AffiliationColors.FriendlyGraphicFillColor;//0x00ffff;	// Cyan
                                                retColor = rendererSettings.getFriendlyGraphicFillColor();

                                        }
                                        else if(switchChar.equals("H") ||
                                            switchChar.equals("S") ||
                                            switchChar.equals("J") ||
                                            switchChar.equals("K"))
                                        {
                                                //retColor = AffiliationColors.HostileGraphicFillColor;//0xfa8072;	// Salmon
                                                retColor = rendererSettings.getHostileGraphicFillColor();

                                        }
                                        else if(switchChar.equals("N") ||
                                                switchChar.equals("L"))
                                        {
                                                //retColor = AffiliationColors.NeutralGraphicFillColor;//0x7fff00;	// Light Green
                                                retColor = rendererSettings.getNeutralGraphicFillColor();

                                        }
                                        else if(switchChar.equals("U") ||
                                            switchChar.equals("P") ||
                                            switchChar.equals("O") ||
                                            switchChar.equals("G") ||
                                            switchChar.equals("W"))
                                        {
                                                retColor = new Color(255,250, 205); //0xfffacd;	// LemonChiffon 255 250 205
                                        }
                                        else
                                        {
                                                //retColor = AffiliationColors.UnknownGraphicFillColor;
                                                retColor = rendererSettings.getUnknownGraphicFillColor();
                                        }
                                    }
                            }	// End if(SymbolUtilities.IsTacticalGraphic(this._strSymbolID))
                            else
                            {
                                    switchChar = symbolID.substring(1, 2);
                                    if(switchChar.equals("F") ||
                                        switchChar.equals("A") ||
                                        switchChar.equals("D") ||
                                        switchChar.equals("M"))
                                    {
                                            //retColor = AffiliationColors.FriendlyUnitFillColor;//0x00ffff;	// Cyan
                                            retColor = rendererSettings.getFriendlyUnitFillColor();

                                    }
                                    else if(switchChar.equals("H") ||
                                        switchChar.equals("S") ||
                                        switchChar.equals("J") ||
                                        switchChar.equals("K"))
                                    {
                                            //retColor = AffiliationColors.HostileUnitFillColor;//0xfa8072;	// Salmon
                                            retColor = rendererSettings.getHostileUnitFillColor();

                                    }
                                    else if(switchChar.equals("N") ||
                                            switchChar.equals("L"))
                                    {
                                            //retColor = AffiliationColors.NeutralUnitFillColor;//0x7fff00;	// Light Green
                                            retColor = rendererSettings.getNeutralUnitFillColor();

                                    }
                                    else if(switchChar.equals("U") ||
                                        switchChar.equals("P") ||
                                        switchChar.equals("O") ||
                                        switchChar.equals("G") ||
                                        switchChar.equals("W"))
                                    {
                                            //retColor = AffiliationColors.UnknownUnitFillColor;//new Color(255,250, 205); //0xfffacd;	// LemonChiffon 255 250 205
                                            retColor = rendererSettings.getUnknownUnitFillColor();
                                    }
                                    else

                                    {
                                            //retColor = AffiliationColors.UnknownUnitFillColor;//null;
                                            retColor = rendererSettings.getUnknownUnitFillColor();
                                    }


                            }	// End else
                    }	// End try
                    catch(Exception e)
                    {
                            // Log Error
                            ErrorLogger.LogException("SymbolUtilties", "getFillColorOfAffiliation", e);
                            //throw e;
                    }	// End catch

                    return retColor;
            }	// End FillColorOfAffiliation
            
            public static Color getLineColorOfWeather(String symbolID)
            {
                Color retColor = Color.BLACK;
                // Get the basic id
                //String symbolID = SymbolUtilities.getBasicSymbolID(symbolID);

                //if(symbolID.equals(get))
                if(symbolID.equals("WAS-WSGRL-P----") || // Hail - Light not Associated With Thunder
                    symbolID.equals("WAS-WSGRMHP----") || // Hail - Moderate/Heavy not Associated with Thunder
                    symbolID.equals("WAS-PL----P----") || // Low Pressure Center - Pressure Systems
                    symbolID.equals("WAS-PC----P----") || // Cyclone Center - Pressure Systems
                    symbolID.equals("WAS-WSIC--P----") || // Ice Crystals (Diamond Dust)
                    symbolID.equals("WAS-WSPLL-P----") || // Ice Pellets - Light
                    symbolID.equals("WAS-WSPLM-P----") || // Ice Pellets - Moderate
                    symbolID.equals("WAS-WSPLH-P----") || // Ice Pellets - Heavy
                    symbolID.equals("WAS-WST-NPP----") || // Thunderstorm - No Precipication
                    symbolID.equals("WAS-WSTMR-P----") || // Thunderstorm Light to Moderate with Rain/Snow - No Hail
                    symbolID.equals("WAS-WSTHR-P----") || // Thunderstorm Heavy with Rain/Snow - No Hail
                    symbolID.equals("WAS-WSTMH-P----") || // Thunderstorm Light to Moderate - With Hail
                    symbolID.equals("WAS-WSTHH-P----") || // Thunderstorm Heavy - With Hail
                    symbolID.equals("WAS-WST-FCP----") || // Funnel Cloud (Tornado/Waterspout)
                    symbolID.equals("WAS-WST-SQP----") || // Squall
                    symbolID.equals("WAS-WST-LGP----") || // Lightning
                    symbolID.equals("WAS-WSFGFVP----") || // Fog - Freezing, Sky Visible
                    symbolID.equals("WAS-WSFGFOP----") || // Fog - Freezing, Sky not Visible
                    symbolID.equals("WAS-WSTSD-P----") || // Tropical Depression
                    symbolID.equals("WAS-WSTSS-P----") || // Tropical Storm
                    symbolID.equals("WAS-WSTSH-P----") || // Hurricane/Typhoon
                    symbolID.equals("WAS-WSRFL-P----") || // Freezing Rain - Light
                    symbolID.equals("WAS-WSRFMHP----") || // Freezing Rain - Moderate/Heavy
                    symbolID.equals("WAS-WSDFL-P----") || // Freezing Drizzle - Light
                    symbolID.equals("WAS-WSDFMHP----") || // Freezing Drizzle - Moderate/Heavy
                    symbolID.equals("WOS-HHDMDBP----") || //mine-naval (doubtful)
                    symbolID.equals("WOS-HHDMDFP----") || // mine-naval (definited)
                    symbolID.substring(0,7).equals("WA-DPFW") || //warm front
                    //symbolID.substring(0,7).equals("WA-DPFS")//stationary front (actually, it's red & blue)
                    symbolID.equals("WA-DBAIF----A--") || // INSTRUMENT FLIGHT RULE (IFR)
                    symbolID.equals("WA-DBAFP----A--") || // 
                    symbolID.equals("WA-DBAT-----A--") || // 
                    symbolID.equals("WA-DIPIS---L---") || // 
                    symbolID.equals("WA-DIPTH---L---") || // 
                    symbolID.equals("WA-DWJ-----L---") || // Jet Stream  
                    symbolID.equals("WO-DGMSB----A--") || //
                    symbolID.equals("WO-DGMRR----A--") ||
                    symbolID.equals("WO-DGMCH----A--") ||
                    symbolID.equals("WO-DGMIBE---A--") ||
                    symbolID.equals("WO-DGMBCC---A--") ||
                    symbolID.equals("WO-DOBVI----A--"))

                {
                    retColor = Color.RED;//0xff0000;	// Red
                }
                else if(symbolID.equals("WAS-PH----P----") || // High Pressure Center - Pressure Systems
                        symbolID.equals("WAS-PA----P----")  || // Anticyclone Center - Pressure Systems
                        symbolID.equals("WA-DBAMV----A--")  || // MARGINAL VISUAL FLIGHT RULE (MVFR)
                        symbolID.equals("WA-DBATB----A--")  || // BOUNDED AREAS OF WEATHER / TURBULENCE
                        symbolID.substring(0,5).equals("WAS-T")  || // Turbulence
                        symbolID.substring(0,7).equals("WA-DPFC") || //cold front
                        symbolID.equals("WO-DGMIBA---A--"))
                {
                    retColor = Color.BLUE;
                }
                else if(
                symbolID.equals("WAS-WSFGPSP----") || // Fog - Shallow Patches
                symbolID.equals("WAS-WSFGCSP----") || // Fog - Shallow Continuous
                symbolID.equals("WAS-WSFGP-P----") || // Fog - Patchy
                symbolID.equals("WAS-WSFGSVP----") || // Fog - Sky Visible
                symbolID.equals("WAS-WSFGSOP----") || // Fog - Sky Obscured
                symbolID.equals("WA-DBAFG----A--") || // Fog
                symbolID.equals("WO-DGMRM----A--") ||
                symbolID.equals("WO-DGMCM----A--") ||
                symbolID.equals("WO-DGMIBC---A--") ||
                symbolID.equals("WO-DGMBCB---A--") ||
                symbolID.equals("WO-DGMBTE---A--") ||
                symbolID.equals("WAS-WSBR--P----")) // Mist
                {
                    retColor = Color.YELLOW;//0xffff00;	// Yellow
                }
                else if(
                symbolID.equals("WAS-WSFU--P----") || // Smoke
                symbolID.equals("WAS-WSHZ--P----") || // Haze
                symbolID.equals("WAS-WSDSLMP----") || // Dust/Sand Storm - Light to Moderate
                symbolID.equals("WAS-WSDSS-P----") || // Dust/Sand Storm - Severe
                symbolID.equals("WAS-WSDD--P----") || // Dust Devil
                symbolID.equals("WA-DBAD-----A--") || // Dust or Sand
                symbolID.equals("WAS-WSBD--P----")) // Blowing Dust or Sand
                {
                    retColor = new Color(165,42,42);  //165 42 42 //0xa52a2a;	// Brown
                }
                else if(
                symbolID.equals("WA-DBALPNC--A--") || // 
                symbolID.equals("WA-DBALPC---A--") || // 
                symbolID.equals("WA-DIPID---L---") || // 
                symbolID.equals("WO-DGMSIM---A--") || //
                symbolID.equals("WO-DGMRS----A--") ||
                symbolID.equals("WO-DGMCL----A--") ||
                symbolID.equals("WO-DGMIBB---A--") ||
                symbolID.equals("WO-DGMBCA---A--") ||
                symbolID.equals("WAS-WSR-LIP----") || // Rain - Intermittent Light
                symbolID.equals("WAS-WSR-LCP----") || // Rain - Continuous Light
                symbolID.equals("WAS-WSR-MIP----") || // Rain - Intermittent Moderate
                symbolID.equals("WAS-WSR-MCP----") || // Rain - Continuous Moderate
                symbolID.equals("WAS-WSR-HIP----") || // Rain - Intermittent Heavy
                symbolID.equals("WAS-WSR-HCP----") || // Rain - Continuous Heavy
                symbolID.equals("WAS-WSRSL-P----") || // Rain Showers - Light
                symbolID.equals("WAS-WSRSMHP----") || // Rain Showers - Moderate/Heavy
                symbolID.equals("WAS-WSRST-P----") || // Rain Showers - Torrential
                symbolID.equals("WAS-WSD-LIP----") || // Drizzle - Intermittent Light
                symbolID.equals("WAS-WSD-LCP----") || // Drizzle - Continuous Light
                symbolID.equals("WAS-WSD-MIP----") || // Drizzle - Intermittent Moderate
                symbolID.equals("WAS-WSD-MCP----") || // Drizzle - Continuous Moderate
                symbolID.equals("WAS-WSD-HIP----") || // Drizzle - Intermittent Heavy
                symbolID.equals("WAS-WSD-HCP----") || // Drizzle - Continuous Heavy
                symbolID.equals("WAS-WSM-L-P----") || // Rain or Drizzle and Snow - Light
                symbolID.equals("WAS-WSM-MHP----") || // Rain or Drizzle and Snow - Moderate/Heavy
                symbolID.equals("WAS-WSMSL-P----") || // Rain and Snow Showers - Light
                symbolID.equals("WAS-WSMSMHP----") || // Rain and Snow Showers - Moderate/Heavy
                symbolID.equals("WAS-WSUKP-P----") || // Precipitation of unknown type & intensity
                symbolID.equals("WAS-WSS-LIP----") || // Snow - Intermittent Light
                symbolID.equals("WAS-WSS-LCP----") || // Snow - Continuous Light
                symbolID.equals("WAS-WSS-MIP----") || // Snow - Intermittent Moderate
                symbolID.equals("WAS-WSS-MCP----") || // Snow - Continuous Moderate
                symbolID.equals("WAS-WSS-HIP----") || // Snow - Intermittent Heavy
                symbolID.equals("WAS-WSS-HCP----") || // Snow - Continuous Heavy
                symbolID.equals("WAS-WSSBLMP----") || // Blowing Snow - Light/Moderate
                symbolID.equals("WAS-WSSBH-P----") || // Blowing Snow - Heavy
                symbolID.equals("WAS-WSSG--P----") || // Snow Grains
                symbolID.equals("WAS-WSSSL-P----") || // Snow Showers - Light
                symbolID.equals("WAS-WSSSMHP----")) // Snow Showers - Moderate/Heavy
                {
                    retColor = Color.GREEN;// 0x00ff00;	// Green
                }
                else if(symbolID.equals("WO-DHCF----L---") || // 
                    symbolID.equals("WO-DHCF-----A--")) 
                {
                    retColor = new Color(173,255,47);// 0xADFF2F;// GreenYellow
                }
                else if(symbolID.equals("WOS-HDS---P----")|| // Soundings
                    symbolID.equals("WOS-HHDF--P----")||//foul ground
                    symbolID.equals("WO-DHHDF----A--")||//foul ground
                    symbolID.equals("WOS-HPFS--P----")||//fish stakes/traps/weirs
                    symbolID.equals("WOS-HPFS---L---")||//fish stakes
                    symbolID.equals("WOS-HPFF----A--")||//fish stakes/traps/weirs
                    symbolID.equals("WO-DHDDL---L---")||//depth curve
                    symbolID.equals("WO-DHDDC---L---")||//depth contour
                    symbolID.equals("WO-DHCC----L---")||//coastline
                    symbolID.equals("WO-DHPBP---L---")||//ports
                    symbolID.equals("WO-DHPMO---L---")||//offshore loading
                    symbolID.equals("WO-DHPSPA--L---")||//sp above water
                    symbolID.equals("WO-DHPSPB--L---")||//sp below water
                    symbolID.equals("WO-DHPSPS--L---")||//sp sea wall
                    symbolID.equals("WO-DHHDK--P----")||//kelp seaweed
                    symbolID.equals("WO-DHHDK----A--")||//kelp seaweed
                    symbolID.equals("WO-DHHDB---L---")||//breakers
                    symbolID.equals("WO-DTCCCFE-L---")||//current flow - ebb
                    symbolID.equals("WO-DTCCCFF-L---")||//current flow - flood
                    symbolID.equals("WOS-TCCTD-P----")||//tide data point    
                    symbolID.equals("WO-DHCW-----A--")||
                    symbolID.equals("WO-DMOA-----A--") ||
                    symbolID.equals("WO-DMPA----L---"))//water
                    retColor = Color.GRAY;//0x808080;	// Gray
                else if(
                    symbolID.equals("WO-DBSM-----A--") ||
                    symbolID.equals("WO-DBSF-----A--") ||
                    symbolID.equals("WO-DGMN-----A--")) // 
                {
                        retColor = new Color(230,230,230);//230,230,230;	// light gray
                }
                else if(
                    symbolID.equals("WO-DBSG-----A--") ||
                        symbolID.equals("WO-DBST-----A--")) // 
                {
                        retColor = new Color(169,169,169);//169,169,169;	// dark gray
                }
                else if(
                symbolID.equals("WAS-WSVE--P----") || // Volcanic Eruption
                symbolID.equals("WAS-WSVA--P----") || // Volcanic Ash
                symbolID.equals("WAS-WST-LVP----") || // Tropopause Level
                symbolID.equals("WAS-WSF-LVP----")) // Freezing Level
                {
                        retColor = Color.BLACK;//0x000000;	// Black
                }
                else if(
                symbolID.equals("WOS-HPBA--P----") || // anchorage
                symbolID.equals("WOS-HPBA---L---") || // anchorage
                symbolID.equals("WOS-HPBA----A--") || // anchorage
                symbolID.equals("WOS-HPCP--P----") || // call in point
                symbolID.equals("WOS-HPFH--P----") || // fishing harbor
                symbolID.equals("WOS-HPM-FC-L---") || //ferry crossing
                symbolID.equals("WOS-HABM--P----") || //marker
                symbolID.equals("WOS-HAL---P----") || //light
                symbolID.equals("WA-DIPIT---L---") || //ISOTACH
                symbolID.equals("WOS-TCCTG-P----") || // Tide gauge
                symbolID.equals("WO-DL-ML---L---") ||
                symbolID.equals("WOS-HPM-FC-L---") ||
                symbolID.equals("WO-DL-RA---L---") ||
                symbolID.equals("WO-DHPBA---L---") ||
                symbolID.equals("WO-DMCA----L---") ||
                symbolID.equals("WO-DHPBA----A--") ||
                symbolID.equals("WO-DL-MA----A--") ||
                symbolID.equals("WO-DL-SA----A--") ||
                symbolID.equals("WO-DL-TA----A--") ||
                symbolID.equals("WO-DGMSR----A--")) 
                {
                    retColor = new Color(255,0,255);//magenta
                }
                else if(symbolID.substring(0,7).equals("WA-DPFO")//occluded front
                )
                {
                    retColor = new Color(226,159,255);//light purple
                }
                else if(
                symbolID.equals("WA-DPXITCZ-L---") || // inter-tropical convergance zone oragne?
                symbolID.equals("WO-DL-O-----A--") ||
                symbolID.equals("WA-DPXCV---L---")) // 
                {
                    retColor = new Color(255,165,0);//orange
                }
                else if(
                symbolID.equals("WA-DBAI-----A--") || //BOUNDED AREAS OF WEATHER / ICING
                symbolID.startsWith("WAS-IC") || // clear icing
                symbolID.startsWith("WAS-IR") || // rime icing
                symbolID.startsWith("WAS-IM")) // mixed icing
                {
                    retColor = new Color(128,96,16);//mud?
                }
                else if(
                symbolID.equals("WO-DHCI-----A--") || //Island
                symbolID.equals("WO-DHCB-----A--") || //Beach
                symbolID.equals("WO-DHPMO----A--")||//offshore loading
                symbolID.equals("WO-DHCI-----A--")) // mixed icing
                {
                    retColor = new Color(210,176,106);//light/soft brown
                }
                else if(symbolID.equals("WO-DOBVA----A--")
                )
                {
                    retColor = new Color(26,153,77);//dark green
                }
                else if(symbolID.equals("WO-DGMBTI---A--")
                )
                {
                    retColor = new Color(255,48,0);//orange red
                }
                else if(symbolID.equals("WO-DGMBTH---A--")
                )
                {
                    retColor = new Color(255,80,0);//dark orange
                }
                //255,127,0
                //WO-DGMBTG---A--
                else if (symbolID.equals("WO-DGMBTG---A--")) {
                    retColor = new Color(255, 127, 0);
                }
                //255,207,0
                //WO-DGMBTF---A--
                else if (symbolID.equals("WO-DGMBTF---A--")) {
                    retColor = new Color(255, 207, 0);
                }
                //048,255,0
                //WO-DGMBTA---A--
                else if (symbolID.equals("WO-DGMBTA---A--")) {
                    retColor = new Color(48, 255, 0);
                }
                //220,220,220
                //WO-DGML-----A--
                else if (symbolID.equals("WO-DGML-----A--")) {
                    retColor = new Color(220, 220, 220);
                }
                //255,220,220
                //WO-DGMS-SH--A--
                else if (symbolID.equals("WO-DGMS-SH--A--")) {
                    retColor = new Color(255, 220, 220);
                }
                //255,190,190
                //WO-DGMS-PH--A--
                else if (symbolID.equals("WO-DGMS-PH--A--")) {
                    retColor = new Color(255, 190, 190);
                }
                //lime green 128,255,51
                //WO-DOBVC----A--
                else if (symbolID.equals("WO-DOBVC----A--")) {
                    retColor = new Color(128, 255, 51);
                }
                //255,255,0
                //WO-DOBVE----A--
                else if (symbolID.equals("WO-DOBVE----A--")) {
                    retColor = new Color(255, 255, 0);
                }
                //255,150,150
                //WO-DGMS-CO--A--
                else if (symbolID.equals("WO-DGMS-CO--A--")) {
                    retColor = new Color(255, 150, 150);
                }
                //175,255,0
                //WO-DGMBTC---A--
                else if (symbolID.equals("WO-DGMBTC---A--")) {
                    retColor = new Color(175, 255, 0);
                }
                //207,255,0
                //WO-DGMBTD---A--
                else if (symbolID.equals("WO-DGMBTD---A--")) {
                    retColor = new Color(207, 255, 0);
                }
                //127,255,0
                //WO-DGMBTB---A--
                else if (symbolID.equals("WO-DGMBTB---A--")) {
                    retColor = new Color(127, 255, 0);
                }
                //255,127,0
                //WO-DGMIBD---A--
                else if (symbolID.equals("WO-DGMIBD---A--")) {
                    retColor = new Color(255, 127, 0);
                }
                else if (symbolID.equals("WO-DGMSIF---A--")) {
                    retColor = new Color(25, 255, 230);
                }
                //0,215,255
                //WO-DGMSIVF--A--
                else if (symbolID.equals("WO-DGMSIVF--A--")) {
                    retColor = new Color(0, 215, 255);
                }
                //255,255,220
                //WO-DGMSSVF--A--
                else if (symbolID.equals("WO-DGMSSVF--A--")) {
                    retColor = new Color(255, 255, 220);
                }
                //255,255,140
                //WO-DGMSSF---A--
                else if (symbolID.equals("WO-DGMSSF---A--")) {
                    retColor = new Color(255, 255, 140);
                }
                //255,235,0
                //WO-DGMSSM---A--
                else if (symbolID.equals("WO-DGMSSM---A--")) {
                    retColor = new Color(255, 235, 0);
                }
                //255,215,0
                //WO-DGMSSC---A--
                else if (symbolID.equals("WO-DGMSSC---A--")) {
                    retColor = new Color(255, 215, 0);
                }
                //255,180,0
                //WO-DGMSSVS--A--
                else if (symbolID.equals("WO-DGMSSVS--A--")) {
                    retColor = new Color(255, 180, 0);
                }
                //200,255,105
                //WO-DGMSIC---A--
                else if (symbolID.equals("WO-DGMSIC---A--")) {
                    retColor = new Color(200, 255, 105);
                }
                //100,130,255
                //WO-DGMSC----A--
                else if (symbolID.equals("WO-DGMSC----A--")) {
                    retColor = new Color(100, 130, 255);
                }
                //255,77,0
                //WO-DOBVH----A--
                else if (symbolID.equals("WO-DOBVH----A--")) {
                    retColor = new Color(255, 77, 0);
                }
                //255,128,0
                //WO-DOBVG----A--
                else if (symbolID.equals("WO-DOBVG----A--")) {
                    retColor = new Color(255, 128, 0);
                }
                //255,204,0
                //WO-DOBVF----A--
                else if (symbolID.equals("WO-DOBVF----A--")) {
                    retColor = new Color(255, 204, 0);
                }
                //204,255,26
                //WO-DOBVD----A--
                else if (symbolID.equals("WO-DOBVD----A--")) {
                    retColor = new Color(204, 255, 26);
                }
                else
                {
                    retColor = Color.BLACK;//0x000000;	// Black
                }
                
                return retColor;
            }
            
            /**
             * Only for single points at the moment
             * @param symbolID
             * @return 
             */
            public static Color getFillColorOfWeather(String symbolID)
            {
                if(symbolID.equals("WOS-HPM-R-P----"))//landing ring - brown 148,48,0
                    return new Color(148,48,0);
                else if(symbolID.equals("WOS-HPD---P----"))//dolphin facilities - brown
                    return new Color(148,48,0);
                else if(symbolID.equals("WO-DHCB-----A--"))//
                    return new Color(249,243,241);
                else if(symbolID.equals("WOS-HABB--P----"))//buoy default - 255,0,255
                    return new Color(255,0,255);//magenta
                else if(symbolID.equals("WOS-HHRS--P----"))//rock submerged - 0,204,255
                    return new Color(0,204,255);//a type of blue
                else if(symbolID.equals("WOS-HHDS--P----"))//snags/stumps - 0,204,255
                    return new Color(0,204,255);
                else if(symbolID.equals("WOS-HHDWB-P----"))//wreck - 0,204,255
                    return new Color(0,204,255);
                else if(symbolID.equals("WOS-TCCTG-P----"))//tide gauge - 210, 176, 106
                    return new Color(210,176,106);
                else if(symbolID.equals("WO-DHCW-----A--"))//water
                    return new Color(255,255,255);
                else if (symbolID.equals("WO-DHABP----A--") ||
                    symbolID.equals("WO-DMCC-----A--")) 
                {
                    return new Color(0,0,255);
                }
                else if(symbolID.equals("WO-DHHD-----A--") ||
                    symbolID.equals("WO-DHHDD----A--")  ||//discolored water (DeepSkyBlue)
                    symbolID.equals("WO-DHDDA----A--"))//Depth Area
                {
                    return new Color(0,191,255);
                }
                else if(symbolID.equals("WO-DHPMD----A--"))//drydock
                    return new Color(188,153,58);
                else if(symbolID.equals("WO-DOBVA----A--"))//BIOLUMINESCENCE/VDR LEVEL 1-2
                    return new Color(26,153,77);
                else if(symbolID.equals("WO-DOBVB----A--"))//BIOLUMINESCENCE/VDR LEVEL 2-3
                    return new Color(26,204,77);
                else if(symbolID.equals("WO-DOBVC----A--"))//BIOLUMINESCENCE/VDR LEVEL 3-4
                    return new Color(128,255,51);
                else if(symbolID.equals("WO-DOBVD----A--"))//BIOLUMINESCENCE/VDR LEVEL 4-5
                    return new Color(204,255,26);
                else if(symbolID.equals("WO-DOBVE----A--"))//BIOLUMINESCENCE/VDR LEVEL 5-6
                    return new Color(255,255,0);
                else if(symbolID.equals("WO-DOBVF----A--"))//BIOLUMINESCENCE/VDR LEVEL 6-7
                    return new Color(255,204,0);
                else if(symbolID.equals("WO-DOBVG----A--"))//BIOLUMINESCENCE/VDR LEVEL 7-8
                    return new Color(255,128,0);
                else if(symbolID.equals("WO-DOBVH----A--"))//BIOLUMINESCENCE/VDR LEVEL 8-9
                    return new Color(255,77,0);
                else if(symbolID.equals("WO-DOBVI----A--"))//BIOLUMINESCENCE/VDR LEVEL 9-10
                    return new Color(255,0,0);
                else if(symbolID.equals("WO-DHCF----L---") || // 
                    symbolID.equals("WO-DHCF-----A--")) 
                {
                    return new Color(173,255,47);// 0xADFF2F;// GreenYellow
                }
                
                else return null;
            }

  /**
   * @name hierarchyToSymbolID
   *
   * @desc Takes in the hierarchy passed in and, returns a formatted string that
   * has only the necessary static characters needed to draw a symbol.
   *
   * @param strHierarchy - IN - A MilStd hierarchy
   * @return A properly formated basic symbol ID
   * @deprecated - Not all symbols have a hierarchy anymore
   */
  public static String hierarchyToSymbolID(String strHierarchy)
  {/*
    try
    {
      String strBasicID = "";
      SymbolDefTable symDefTable = SymbolDefTable.getInstance();
      UnitDefTable unitDefTable = UnitDefTable.getInstance();
      // var arrUD:ArrayCollection;
      // var arrSD:ArrayCollection;
      String[] arrUD;
      String[] arrSD;
      if(strHierarchy.substring(0, 1).equals("1")
        || strHierarchy.substring(0, 1).equals("4")
        || strHierarchy.substring(0, 1).equals("5"))
      {
        arrUD = unitDefTable.searchByHierarchy(strHierarchy);
        if((arrUD != null) && (arrUD.length > 0) && (arrUD[0] != null))
        {
          // strBasicID = UnitDef(arrUD[0]).basicSymbolId;
        }
      }
      else if(strHierarchy.substring(0, 1).equals("2") || strHierarchy.substring(0, 1).equals("3"))
      {
        // arrSD = symDefTable.searchByHierarchy(strHierarchy);
        arrSD = new String[] {};
        if((arrSD != null) && (arrSD.length > 0) && (arrSD[0] != null))
        {
          // strBasicID = SymbolDef(arrSD[0]).basicSymbolId;
        }
      }
      else
      {
        arrUD = unitDefTable.searchByHierarchy(strHierarchy);
        // If symbol is in UnitDefTable
        if((arrUD != null) && (arrUD.length > 0) && (arrUD[0] != null))
        {
          // strBasicID = UnitDef(arrUD[0]).basicSymbolId;
        }
        else
        {
          // Symbol is not in UnitDefTable, see if it is in SymbolDefTable
          // arrSD = symDefTable.searchByHierarchy(strHierarchy);
          arrSD = new String[] {};
          if((arrSD != null) && (arrSD.length > 0) && (arrSD[0] != null))
          {
            // strBasicID = SymbolDef(arrSD[0]).basicSymbolId;
          }
        }
      }
      return strBasicID;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }*/
    return "";
  }


  /**
   * @name copyModifiers
   *
   * @desc Copies the symbol modifier data from one symbol code to another. This
   * data includes the echelon, country, affiliation, etc.
   *
   * NOTE: This operation will return null if there is problem copying the data
   * or one of the items passed in is null. You must check for null.
   *
   * @param strFromCode - IN - Code containing data to copy
   * @param strToCode - IN - Code to copy data to
   * @return Symbol modifier data.
   * @deprecated
   */
  public static String copyModifiers(String strFromCode, String strToCode)
  {
    String sb = null;
    if((strFromCode != null) && (strToCode != null))
    {
      try
      {
        sb = strToCode;
        sb = sb.substring(0, 1)
          + strFromCode.substring(1, 2)
          + sb.substring(2, 3)
          + strFromCode.substring(3, 4)
          + sb.substring(4, 10)
          + strFromCode.substring(10, 14)
          + sb.substring(14, 15);
        /*
         * sb[1] = strFromCode.charAt(1); sb[3] = strFromCode.charAt(3); sb[10] =
         * strFromCode.charAt(10); sb[11] = strFromCode.charAt(11); sb[12] =
         * strFromCode.charAt(12); sb[13] = strFromCode.charAt(13);
         */
      }
      catch(Throwable t)
      {
        System.out.println(t);
        return null;
      }
    }
    return sb.toString();
  } // End CopyModifiers

  /**
   * Determines if the symbol is a tactical graphic
   * @param strSymbolID
   * @return true if symbol starts with "G", or is a weather graphic, or a bridge graphic
   */
  public static boolean isTacticalGraphic(String strSymbolID)
  {
    try
    {
      if(strSymbolID == null) // Error handling
      {
        return false;
      }
      if((strSymbolID.substring(0, 1).equals("G")) || (isWeather(strSymbolID)) 
              || (isBridge(strSymbolID)) || isEMSNaturalEvent(strSymbolID) ||
              isBasicShape(strSymbolID))
      {
        return true;
      }
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  }
  
  public static boolean isBasicShape(String symbolID)
  {
      if(symbolID != null && symbolID.length() >= 2)
      {
          char scheme = symbolID.charAt(0);
          if(scheme == 'B' || scheme == 'P')
              return true;
          else
              return false;
      }
      else
      {
          return false;
      }
  }

  /**
   * Determines if symbols is a warfighting symbol.
   * @param strSymbolID
   * @return True if code starts with "O", "S", or "I". (or "E" in 2525C)
   */
  public static boolean isWarfighting(String strSymbolID)
  {
    try
    {
      if(strSymbolID == null) // Error handling
      {
        return false;
      }
      if((strSymbolID.substring(0, 1).equals("O")) || (strSymbolID.substring(0, 1).equals("S")) ||
              (strSymbolID.substring(0, 1).equals("I")) || (strSymbolID.substring(0, 1).equals("E") && strSymbolID.substring(2, 3).equals("N")==false))
      {
        return true;
      }
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  }

  /**
   * Determines if the symbol is a weather graphic
   * @param strSymbolID
   * @return true if symbolID starts with a "W"
   */
  public static boolean isWeather(String strSymbolID)
  {
    try
    {
      boolean blRetVal = strSymbolID.substring(0, 1).equals("W");
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  }
  
  public static boolean isMETOCWithIconFill(String symbolID)
  {
      if(symbolID.equals("WOS-HPFF----A--") ||
              symbolID.equals("WO-DHHDF----A--") ||
              symbolID.equals("WO-DHHDK----A--") ||
              symbolID.equals("WO-DBSM-----A--") ||
              symbolID.equals("WO-DBST-----A--") ||
              symbolID.equals("WO-DL-SA----A--") ||
              symbolID.equals("WO-DMOA-----A--"))
      {
          return true;
      }
      else
      {
          return false;
      }
  }
  
   /**
   * Determines if a String represents a valid number
   * @param text
   * @return "1.56" == true, "1ab" == false
   */
  public static boolean isNumber(String text)
  {
      return pIsNumber.matcher(text).matches();
  }
  
  private static String convert(int integer)
  {
        String hexAlphabet = "0123456789ABCDEF";
        String foo = "gfds" + "dhs";
        char char1 =  hexAlphabet.charAt((integer - integer % 16)/16);
        char char2 = hexAlphabet.charAt(integer % 16);
        String returnVal = String.valueOf(char1) + String.valueOf(char2);
        return returnVal;
  }
  
  public static String colorToHexString(Color color, Boolean withAlpha)
  {
      String hex = "";
      if(withAlpha == false)
        {
            hex = "#" + convert(color.getRed()) + 
                            convert(color.getGreen()) + 
                            convert(color.getBlue());
        }
        else
        {
            hex = "#" + convert(color.getAlpha()) + 
                            convert(color.getRed()) + 
                            convert(color.getGreen()) + 
                            convert(color.getBlue());
        }   
      return hex;
  }
  /**
   *
   * @param hexValue - String representing hex value
   * (formatted "0xRRGGBB" i.e. "0xFFFFFF")
   * OR
   * formatted "0xAARRGGBB" i.e. "0x00FFFFFF" for a color with an alpha value
   * I will also put up with "RRGGBB" and "AARRGGBB" without the starting "0x"
   * @return
   */
  public static Color getColorFromHexString(String hexValue)
  {
      try
      {
          String hexOriginal = hexValue;

        String hexAlphabet = "0123456789ABCDEF";

        if(hexValue.charAt(0)=='#')
            hexValue = hexValue.substring(1);
        if(hexValue.substring(0,2).equals("0x") || hexValue.substring(0,2).equals("0X"))
            hexValue = hexValue.substring(2);

        hexValue = hexValue.toUpperCase();

        int count = hexValue.length();
        int[] value = null;
        int k = 0;
        int int1 = 0;
        int int2 = 0;

        if(count == 8 || count == 6)
        {
            value = new int[(count / 2)];
            for(int i=0; i<count;i+=2)
            {
                    int1 = hexAlphabet.indexOf(hexValue.charAt(i));
                    int2 = hexAlphabet.indexOf(hexValue.charAt(i+1));
                    value[k]=(int1 * 16) + int2;
                    k++;
            }

            if(count == 8)
            {
                    return new Color(value[1],value[2],value[3],value[0]);
            }
            else if(count == 6)
            {
                    return new Color(value[0],value[1],value[2]);
            }
        }
        else
        {       
                ErrorLogger.LogMessage("SymbolUtilties", "getColorFromHexString", "Bad hex value: " + hexOriginal, Level.WARNING);
        }
        return null;

          /*//Old Approach
          Color returnVal = null;
          
          if(hexValue.startsWith("0x"))//0xRRGGBB or 0xAARRGGBB
          {
              if(hexValue.length()==8)
              {
                  returnVal = Color.decode(hexValue);
              }
              else if(hexValue.length()==10)
              {
                  String color = "0x"+hexValue.substring(4);
                  String alpha = "0x"+hexValue.substring(2,4);
                  returnVal = Color.decode(color);
                  returnVal = new Color(returnVal.getRed(), returnVal.getGreen(), returnVal.getBlue(), Integer.decode(alpha));
              }
          }
          else if(hexValue.startsWith("#"))//#RRGGBB or #AARRGGBB
          {
              if(hexValue.length()==7)
              {
                  returnVal = Color.decode("0x"+hexValue.substring(1, 7));
              }
              else if(hexValue.length()==9)
              {
                  String color = "0x"+hexValue.substring(3);
                  String alpha = "0x"+hexValue.substring(1,3);
                  returnVal = Color.decode(color);
                  returnVal = new Color(returnVal.getRed(), returnVal.getGreen(), returnVal.getBlue(), Integer.decode(alpha));
              }
          }
          else//just RRGGBB or AARRGGBB without the starting 0x
          {
              if(hexValue.length()==6)
              {
                  returnVal = Color.decode("0x"+hexValue);
              }
              else if(hexValue.length()==8)
              {
                  String color = "0x"+hexValue.substring(2);
                  String alpha = "0x"+hexValue.substring(0,2);
                  returnVal = Color.decode(color);
                  returnVal = new Color(returnVal.getRed(), returnVal.getGreen(), returnVal.getBlue(), Integer.decode(alpha));
              }
          }
          
          return returnVal;*/
      }
      catch(Exception exc)
      {
          ErrorLogger.LogException("SymbolUtilities", "getColorFromHexString", exc);
          return null;
      }
  }

  /**
   * Determines if symbols is a Bridge symbol
   * @param strSymbolID
   * @return true if symbolID starts with "ESRI"
   */
  public static boolean isBridge(String strSymbolID)
  {
    try
    {
      boolean blRetVal = strSymbolID.substring(0, 4).equals("ESRI");
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  }

  /**
   * Engineering overlay graphics including bridges and overlays at 2.X.7
   * Non-MilStd
   * @param strSymbolID
   * @return
   */
  public static boolean isEngineeringOverlayGraphic(String strSymbolID)
  {
    try
    {
      boolean blRetVal = (isBridge(strSymbolID) || isEngineeringOverlayObstacle(strSymbolID));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  }

  /**
   * Determines if symbol is an Engineering Overlay Obstacle
   * @param strSymbolID
   * @return
   */
  public static boolean isEngineeringOverlayObstacle(String strSymbolID)
  {
      try
    {
      boolean blRetVal = (strSymbolID.substring(0, 1).equals("E") &&
                            strSymbolID.substring(2, 3).equals("G"));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  }

  /**
   * Symbols that don't exist outside of MCS
   * @param sd
   * @return
   */
  public static boolean isMCSSpecificTacticalGraphic(SymbolDef sd)
  {
         if(sd.getHierarchy().startsWith("2.X.7") || //Engineering Overlay graphics (ESRI----)
            sd.getHierarchy().startsWith("2.X.5.2.3") || //Route Critical Points
            sd.getBasicSymbolId().startsWith("G*R*") || //Route Critical Points
            sd.getHierarchy().startsWith("21.X") || //JCID (21.X)
            sd.getBasicSymbolId().startsWith("G*E*"))//MCS Eng (20.X)
         {
             return true;
         }
         else
         {
             return false;
         }
  }
  
  /**
   * Symbols that don't exist outside of MCS
   * or units that are no longer supported like
   * those from the SASO Proposal.
   * @param ud
   * @return 
   */
  public static boolean isMCSSpecificForceElement(UnitDef ud)
  {
      if(isSASO(ud))//SASO
        {
                return true;
        }
        else
        {
                return false;
        }
  }


  /**
   * Just checks the symbolID if it could be rendered in 3D.  Does not check
   * for needed modifiers.
   * @param symbolID
   * @return
   */
  public static Boolean is3dGraphic(String symbolID)
  {
        String symbolId = symbolID.substring(4, 10);

        if (symbolId.equals("ACAI--") || // Airspace Coordination Area Irregular
            symbolId.equals("ACAR--") || // Airspace Coordination Area Rectangular
            symbolId.equals("ACAC--") || // Airspace Coordination Area Circular
            symbolId.equals("AKPC--") || // Kill box circular
            symbolId.equals("AKPR--") || // Kill box rectangular
            symbolId.equals("AKPI--") || // Kill box irregular
            symbolId.equals("ALC---") || // Air corridor
            symbolId.equals("ALM---") || // 
            symbolId.equals("ALS---") || // SAAFR
            symbolId.equals("ALU---") || // UAV
            symbolId.equals("ALL---") || // Low level transit route
            symbolId.equals("AAR---") ||
            symbolId.equals("AAF---") ||
            symbolId.equals("AAH---") ||
            symbolId.equals("AAM---") || // MEZ
            symbolId.equals("AAML--") || // LOMEZ
            symbolId.equals("AAMH--"))
        {
            return true;
        }
        else
        {
           return false;
        }

  }


  /**
   * Symbols from the SASO Proposal.
   * Most were replaced by the USAS 13-14 update or 2525C.
   * @param sd
   * @return
   */
  public static boolean isSASO(UnitDef sd)
    {
         if(
                sd.getHierarchy().startsWith("5.X.10") || //SASOP Individuals
                sd.getHierarchy().startsWith("5.X.11") || //SASOP Organization/groups
                sd.getHierarchy().startsWith("5.X.12") ||//SASOP //replaced by USAS 13-14 update
                sd.getHierarchy().startsWith("5.X.13") || //SASOP Structures
                sd.getHierarchy().startsWith("5.X.14")) //SASOP Equipment/Weapons
         {
             return true;
         }
         else
             return false;
    }

  public static boolean isCheckPoint(String strSymbolID)
  {
    try
    {
      String strBasicSymbolID = getBasicSymbolID(strSymbolID);
      boolean blRetVal = false;
      if(strBasicSymbolID.equals("G*G*GPPE--****X")//release point
        || strBasicSymbolID.equals("G*G*GPPK--****X")//check point
        || strBasicSymbolID.equals("G*G*GPPS--****X"))//start point
      {
        blRetVal = true;
      }
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsCheckPoint

  /**
   * @name IsCriticalPoint
   *
   * @desc Returns true if the symbolID is a critical point.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
   * critical point
   * @return True if the graphic is a critical point, false otherwise.
   */
  public static boolean isCriticalPoint(String strSymbolID)
  {
    try
    {
      String strBasicSymbolID = getBasicSymbolID(strSymbolID);
      boolean blRetVal = false;
      if(isTacticalGraphic(strBasicSymbolID))
      {
        String[] arr = new String[] { "G*M*BDD---****X",
                                     "G*M*BDE---****X",
                                     "G*M*BDI---****X",
                                     "G*R*CN----****X",
                                     "G*R*CP----****X",
                                     "G*R*FD----****X",
                                     "G*R*FR----****X",
                                     "G*R*PCC---****X",
                                     "G*R*PCO---****X",
                                     "G*R*PDC---****X",
                                     "G*R*PHP---****X",
                                     "G*R*PMC---****X",
                                     "G*R*PO----****X",
                                     "G*R*PPO---****X",
                                     "G*R*PTO---****X",
                                     "G*R*RLGC--****X",
                                     "G*R*SG----****X",
                                     "G*R*SSC---****X",
                                     "G*R*SC----****X",
                                     "G*R*TN----****X",
                                     "G*R*UP----****X" };
        int arrLength = arr.length;
        for(int i = 0; i < arrLength; i++)
        {
          if(arr[i].equals(strBasicSymbolID))
          {
            blRetVal = true;
            break;
          }
        }
      }
      else
      {
        if(strBasicSymbolID.equals("O*E*AL---------")
          || strBasicSymbolID.equals("O*E*AM---------")
          || strBasicSymbolID.equals("S*G*IMNB-------"))
        {
          blRetVal = true;
        }
      }
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  }

  /**
   * @name IsRoute
   *
   * @desc Returns true if the symbolID is a route.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a route
   * @return True if the graphic is a route, false otherwise.
   */
  public static boolean isRoute(String strSymbolID)
  {
    try
    {
      String strBasicSymbolID = getBasicSymbolID(strSymbolID);
      boolean blRetVal = false;
      if(strBasicSymbolID.equals("G*S*LRA---****X") || strBasicSymbolID.equals("G*S*LRM---****X"))
      {
        blRetVal = true;
      }
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsRoute

  /**
   * @name IsRoad
   *
   * @desc Returns true if the symbolID is a road.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a road
   * @return True if the graphic is a road, false otherwise.
   */
  public static boolean isRoad(String strSymbolID)
  {
    try
    {
      String strBasicSymbolID = getBasicSymbolID(strSymbolID);
      boolean blRetVal = false;
      if(strBasicSymbolID.equals("ROAD------****X"))
      {
        blRetVal = true;
      }
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsRoad

  /**
   * @name IsJWARN
   *
   * @desc Returns true if the symbolID is a JWARN symbol.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a JWARN
   * graphic
   * @return True if the graphic is a JWARN symbol, false otherwise.
   */
  public static boolean isJWARN(String strSymbolID)
  {
    try
    {
      if(strSymbolID.substring(0, 5).equals("JWARN"))
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsJWARN

  /**
   * @name IsMOOTW
   *
   * @desc Returns true if the symbolID is a MOOTW symbol.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a MOOTW
   * graphic
   * @return True if the graphic is a MOOTW symbol in the MIL-STD 2525B or
   * STBOPS in 2525C, false otherwise.
   */
  public static boolean isMOOTW(String strSymbolID)
  {
    try
    {
      if(strSymbolID.substring(0, 1).equals("O"))
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsMOOTW
  
  /**
   * @name isSTBOPS
   *
   * @desc Returns true if the symbolID is a Stability Operations (STBOPS) symbol.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a 
   * isStabilityOperations graphic
   * @return True if the graphic is a MOOTW symbol in the MIL-STD 2525B or
   * STBOPS in 2525C, false otherwise.
   */
    public static boolean isSTBOPS(String strSymbolID)
  {
    try
    {
      if(strSymbolID.substring(0, 1).equals("O"))
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isStabilityOperations

    /**
   * @name IsMOOTW
   *
   * @desc Returns true if the symbolID is an event symbol.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a MOOTW
   * graphic
   * @return True if the graphic is a MOOTW symbol in the MIL-STD 2525B, false
   * otherwise.
   */
  public static boolean isEvent(String strSymbolID)
  {
    try
    {
        String[] arr = null;
        char category = strSymbolID.charAt(2);
        String strBasicSymbolID = getBasicSymbolID(strSymbolID);
        if(isMOOTW(strSymbolID) || 
                (isEMS(strSymbolID) && 
                      (category == 'I' || category == 'N' || category == 'O')))
            return true;
        else
        {

            arr = new String[] {"S*G*EXI---*****",
                                     "S*G*EXI---MO***"};
            int arrLength = arr.length;
            for(int i = 0; i < arrLength; i++)
            {
              if(arr[i].equals(strBasicSymbolID))
              {
                return true;
              }
            }
        }
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsMOOTW

  /**
   * @name isHQ
   *
   * @desc Determines if the symbol id passed in contains a flag for one of the
   * various HQ options Pos 11 of the symbol code
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a HQ
   * @return True if the graphic is a HQ symbol in the MIL-STD 2525B, false
   * otherwise.
   */
  public static boolean isHQ(String strSymbolID)
  {
    boolean blRetVal = false;
    char hq = strSymbolID.charAt(10);
    try
    {
        if(hq != '-' && hq != '*')
        {
            blRetVal = ((strSymbolID.substring(10, 11).equals("A"))
                || (strSymbolID.substring(10, 11).equals("B"))
                || (strSymbolID.substring(10, 11).equals("C")) 
                || (strSymbolID.substring(10, 11).equals("D")));
        }
        else
        {
            blRetVal = (strSymbolID.charAt(0) == 'S' && strSymbolID.substring(4,6).equals("UH"));
        }
      
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isHQ

  /**
   * @name isTaskForce
   *
   * @desc Returns whether or not the given symbol id contains task force.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it contains
   * task force
   * @return Returns true if the symbol id contains task force, false otherwise.
   */
  public static boolean isTaskForce(String strSymbolID)
  {
    try
    {
      // Return whether or not task force is included in the symbol id.
      boolean blRetVal = ((strSymbolID.substring(10, 11).equals("B"))
        || (strSymbolID.substring(10, 11).equals("D"))
        || (strSymbolID.substring(10, 11).equals("E")) || (strSymbolID.substring(10, 11).equals("G")));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsTaskForce

  /**
   * @name isFeintDummy
   *
   * @desc Returns whether or not the given symbol id contains FeintDummy.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it contains
   * feint dummy
   * @return Returns true if the symbol id contains FeintDummy, false otherwise.
   */
  public static boolean isFeintDummy(String strSymbolID)
  {
    try
    {
      // Return whether or not feintdummy is included in the symbol id.
      boolean blRetVal = ((strSymbolID.substring(10, 11).equals("C"))
        || (strSymbolID.substring(10, 11).equals("D"))
        || (strSymbolID.substring(10, 11).equals("F")) || (strSymbolID.substring(10, 11).equals("G")));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsFeintDummy

  /**
   * @name isMobilityWheeled
   *
   * @desc Determines if the symbol id passed in contains a flag for the various
   * Wheeled Mobility options Pos 11 and 12 of the symbol code
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
   * Wheeled Mobility
   * @return True if the graphic (equipment only) is a Wheeled Mobility in the
   * MIL-STD 2525B, false otherwise.
   */
  public static boolean isMobilityWheeled(String strSymbolID)
  {
    boolean mobilityWheeledIsOn = false;
    try
    {
      // See if the mobility wheeled modifier is on.
      mobilityWheeledIsOn = (isEquipment(strSymbolID) &&
                                        strSymbolID.substring(10, 11).equals("M") &&
                                        strSymbolID.substring(11,12).equals("O"));
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    // Return whether or not the mobility wheeled modifier is on.
    return mobilityWheeledIsOn;
  }

  /**
   * Symbol has a mobility modifier
   * @param strSymbolID
   * @return
   */
  public static boolean isMobility(String strSymbolID)
  {
    boolean mobilityIsOn = false;
    try
    {

      //if(isEquipment(strSymbolID))
      //{
          if(strSymbolID.substring(10, 12).equals("MO") ||
                  strSymbolID.substring(10, 12).equals("MP") ||
                  strSymbolID.substring(10, 12).equals("MQ") ||
                  strSymbolID.substring(10, 12).equals("MR") ||
                  strSymbolID.substring(10, 12).equals("MS") ||
                  strSymbolID.substring(10, 12).equals("MT") ||
                  strSymbolID.substring(10, 12).equals("MU") ||
                  strSymbolID.substring(10, 12).equals("MV") ||
                  strSymbolID.substring(10, 12).equals("MW") ||
                  strSymbolID.substring(10, 12).equals("MX") ||
                  strSymbolID.substring(10, 12).equals("MY") ||
                  strSymbolID.substring(10, 12).equals("NS") ||
                  strSymbolID.substring(10, 12).equals("NL"))
          {
              mobilityIsOn = true;
          }
      //}
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    // Return whether or not the mobility wheeled modifier is on.
    return mobilityIsOn;
  }
  
    /**
   * Returns true if Symbol is a Target
   * @param strSymbolID
   * @return 
   */
    public static Boolean isTarget(String strSymbolID)
    {
            String basicID = SymbolUtilities.getBasicSymbolID(strSymbolID);
            if(basicID.substring(0,6).equals("G*F*PT") ||//fire support/point/point target
                    basicID.substring(0,6).equals("G*F*LT") ||//fire support/lines/linear target
                    basicID.substring(0,6).equals("G*F*AT"))//fire support/area/area target
            {
                    return true;
            }
            else
            {
                    return false;
            }
    }

    /**
     * Returns true if Symbol is an Air Track
     * @param strSymbolID
     * @return 
     */
    public static Boolean isAirTrack(String strSymbolID)
    {
            if(strSymbolID.charAt(0)=='S' &&
                    strSymbolID.charAt(2)=='A')
            {
                    return true;
            }
            else
            {
                    return false;
            }
    }

  /**
   * @name isObstacle
   *
   * @desc Returns true if the symbol id passed in is an Obstacle symbol code.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is an
   * Obstacle
   * @return True if the graphic is an Obstacle in the MIL-STD 2525B, false
   * otherwise.
   */
  public static boolean isObstacle(String strSymbolID)
  {
    try
    {
      // An Obstacle is denoted by the symbol code "G*M*O"
      // So see if it is a tactical graphic then check to see
      // if we have the M and then the O in the correct position.
      boolean blRetVal = ((isTacticalGraphic(strSymbolID)) && ((strSymbolID.substring(2, 3).equals("M")) && (strSymbolID.substring(4, 5).equals("O"))));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isObstacle

  /**
   * @name isDeconPoint
   *
   * @desc Returns true if the symbol id is a DECON (NBC graphic) point symbol.
   *
   * @param strSymbolID - IN - Symbol Id we are checking to see if it is a Decon
   * Point
   * @return True if the graphic is a Decon Point in the MIL-STD 2525B, false
   * otherwise.
   */
  public static boolean isDeconPoint(String strSymbolID)
  {
    try
    {
      boolean blRetVal = ((isNBC(strSymbolID)) && (strSymbolID.substring(4, 6).equals("ND")));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isDeconPoint

  /**
   * @name isEchelonGraphic
   *
   * @desc Returns true if the graphic is to have an echelon associated with it.
   *
   *
   * Here's the rules: Single point tactical graphics do not have an echelon
   * (see ms2525b ch2 5.5.2.2) Weather graphics do not have an echelon - don't
   * set one Neither do Decon Point graphics Neither do Bridge graphics Pretty
   * much only Obstacles, Units, and SOF get echelons
   *
   * @param strSymbolID - IN - A basic MilStd2525B symbolID
   * @param symStd RendererSettings.Symbology_2525C
   * @return True if the graphic displays an echelon, false if it ignores the
   * echelon field
   */
  public static boolean isEchelonGraphic(String strSymbolID, int symStd)
  {
    try
    {
      // Here's the rules:
      // Single point tactical graphics do not have an echelon (see ms2525b ch2
      // 5.5.2.2)
      // Weather graphics do not have an echelon - don't set one
      // Neither do Decon Point graphics
      // Neither do Bridge graphics
      // Pretty much only Obstacles, Units, and SOF get echelons
      boolean blIsSinglePointTG = false;
      String basicID = getBasicSymbolID(strSymbolID);
      if(isTacticalGraphic(strSymbolID))
      {
        SymbolDefTable symDefTable = SymbolDefTable.getInstance();
        SymbolDef sd = symDefTable.getSymbolDef(basicID,symStd);
        if(sd.getGeometry().equals("point"))
        {
          blIsSinglePointTG = true;
        }
      }
      boolean blRetVal = (((isUnit(strSymbolID))/* || (isMOOTW(strSymbolID))*/
        || (isSOF(strSymbolID))
        || (isMOOTW(strSymbolID))
        || ((SymbolUtilities.isObstacle(strSymbolID)) && (!blIsSinglePointTG))
        || (basicID.equals("G*G*GLB---****X"))
        || (basicID.equals("G*G*DAB---****X"))
        || (basicID.equals("G*G*DABP--****X")) 
        || (basicID.equals("G*M*SP----****X"))));
        
        
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isEchelonGraphic


    /**
   * Reads the Symbol ID string and returns the text that represents the echelon
     * code.
   * @param echelon
   * @return
   */
    public static String getEchelonText(String echelon)
    {
        char[] dots = new char[3];
        dots[0] = (char)8226;
        dots[1] = (char)8226;
        dots[2] = (char)8226;
        String dot = new String(dots);
        String text = "";
        if(echelon.equals("A"))
        {
            text = "0";
        }
        else if(echelon.equals("B"))
        {
            text = dot.substring(0, 1);
        }
        else if(echelon.equals("C"))
        {
            text = dot.substring(0, 2);
        }
        else if(echelon.equals("D"))
        {
            text = dot;
        }
        else if(echelon.equals("E"))
        {
            text = "|";
        }
        else if(echelon.equals("F"))
        {
            text = "||";
        }
        else if(echelon.equals("G"))
        {
            text = "|||";
        }
        else if(echelon.equals("H"))
        {
            text = "X";
        }
        else if(echelon.equals("I"))
        {
            text = "XX";
        }
        else if(echelon.equals("J"))
        {
            text = "XXX";
        }
        else if(echelon.equals("K"))
        {
            text = "XXXX";
        }
        else if(echelon.equals("L"))
        {
            text = "XXXXX";
        }
        else if(echelon.equals("M"))
        {
            text = "XXXXXX";
        }
        else if(echelon.equals("N"))
        {
            text = "++";
        }
        return text;
    }

  /**
   * @name isUnit
   *
   * @desc Returns true if the symbolID is a unit.
   *
   * @param strSymbolID - IN - SymbolID we are checking on
   * @return True if the graphic is a unit in the MIL-STD 2525B or is a special
   * operation forces unit, false otherwise.
   */
  public static boolean isUnit(String strSymbolID)
  {
    try
    {
      boolean isGroundUnit = ((strSymbolID.substring(0, 1).equals("S")) && 
                            (strSymbolID.substring(2, 3).equals("G")) &&
                            (strSymbolID.substring(4,5).equals("U")));
      return isGroundUnit || isSOF(strSymbolID);
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isUnit

  /**
   * @name isNBC
   *
   * @desc Returns true if the symbol id passed in is a NBC symbol code.
   *
   * @param strSymbolID - IN - SymbolID we are checking on
   * @return True if the graphic is a NBC in the MIL-STD 2525B, false otherwise.
   */
  public static boolean isNBC(String strSymbolID)
  {
    try
    {
        String temp = getBasicSymbolID(strSymbolID);
      boolean blRetVal = ((isTacticalGraphic(strSymbolID)) && (temp.substring(0, 5).equals("G*M*N")));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isNBC

  /**
   * returns true if the symbol code
   * represents a symbol that has control points
   * @param symStd RendererSettings.Symbology_2525C
   * @param strSymbolID
   * @return 
   */
  public static boolean isTGWithControlPoints(String strSymbolID, int symStd)
  {
      String temp = getBasicSymbolID(strSymbolID);
      SymbolDef sd = SymbolDefTable.getInstance().getSymbolDef(temp,symStd);
      
      if (sd != null && sd.getDrawCategory() == SymbolDef.DRAW_CATEGORY_ROUTE)
          return true;
      else
        return false;//blRetVal;
  }

  /**
   * There's a handful of single point tactical graphics with unique
   * modifier positions.
   * @param strSymbolID
   * @return
   */
  public static boolean isTGSPWithSpecialModifierLayout(String strSymbolID)
  {
      String temp = getBasicSymbolID(strSymbolID);
      
      boolean blRetVal = (temp.equals("G*G*GPH---****X"))//Harbor(General) - center
        || (temp.equals("G*G*GPPC--****X")) //Contact Point - center
        || (temp.equals("G*G*GPPD--****X"))//Decisions Point - center
        || (temp.equals("G*G*GPPW--****X")) //Waypoint - right of center
        || (temp.equals("G*G*APP---****X"))//ACP - circle, just below center
        || (temp.equals("G*G*APC---****X"))//CCP - circle, just below center
        || (temp.equals("G*G*DPT---****X")) //Target Reference - target special
        || (temp.equals("G*F*PTS---****X"))//Point/Single Target - target special
        || (temp.equals("G*F*PTN---****X"))//Nuclear Target - target special
        || (temp.equals("G*F*PCF---****X")) //Fire Support Station - right of center
        || (temp.equals("G*M*NZ----****X")) //NUCLEAR DETINATIONS GROUND ZERO
        || (temp.equals("G*M*NEB---****X"))//BIOLOGICAL
        || (temp.equals("G*M*NEC---****X"))//CHEMICAL
        || (temp.equals("G*G*GPRI--****X"))//Point of Interest
        || (temp.equals("G*M*OFS---****X"))//Minefield
        || (temp.equals("WAS-WSF-LVP----"))//Freezing Level
        || (temp.equals("WAS-PLT---P----"))//Tropopause Low
        || (temp.equals("WAS-PHT---P----"))//Tropopause High
        || (temp.equals("WAS-WST-LVP----"));//Tropopause Level
      return blRetVal;//blRetVal;
  }

  /**
   * Is a single point tactical graphic that has integral text (like the NBC
   * single points)
   * @param strSymbolID
   * @return
   */
  public static boolean isTGSPWithIntegralText(String strSymbolID)
  {
      String temp = getBasicSymbolID(strSymbolID);

     // ErrorLogger.LogMessage("SU", "integraltext?", temp);

      boolean blRetVal = (temp.equals("G*G*GPRD--****X"))//DLRP (D)
        || (temp.equals("G*G*APU---****X")) //pull-up point (PUP)
        || (temp.equals("G*M*NZ----****X")) //Nuclear Detonation Ground Zero (N)
        || (temp.equals("G*M*NF----****X"))//Fallout Producing (N)
        || (temp.equals("G*M*NEB---****X"))//Release Events Chemical (BIO, B)
        || (temp.equals("G*M*NEC---****X"));//Release Events Chemical (CML, C)

      //if(temp.equals("G*G*GPRD--****X"))
      //    ErrorLogger.LogMessage("DLRP");

      return blRetVal;//blRetVal;
  }


  /**
   * Is tactical graphic with fill
   * @param strSymbolID
   * @return
   */
  public static boolean isTGSPWithFill(String strSymbolID)
  {
      String temp = getBasicSymbolID(strSymbolID);
      boolean blRetVal = isDeconPoint(temp)//Decon Points
          || temp.startsWith("G*S*P")//TG/combat service support/points
          ||(temp.equals("G*G*GPP---****X"))//Action points (general)
          || (temp.equals("G*G*GPPK--****X"))//Check Point
          || (temp.equals("G*G*GPPL--****X"))//Linkup Point
          || (temp.equals("G*G*GPPP--****X"))//Passage Point
          || (temp.equals("G*G*GPPR--****X"))//Rally Point
          || (temp.equals("G*G*GPPE--****X"))//Release Point
          || (temp.equals("G*G*GPPS--****X"))//Start Point
          || (temp.equals("G*G*GPPA--****X"))//Amnesty Point
          || (temp.equals("G*G*GPPN--****X"))//Entry Control Point
          || (temp.equals("G*G*APD---****X"))//Down Aircrew Pickup Point
          || (temp.equals("G*G*OPP---****X"))//Point of Departure
          || (temp.equals("G*F*PCS---****X"))//Survey Control Point
          || (temp.equals("G*F*PCB---****X"))//Firing Point
          || (temp.equals("G*F*PCR---****X"))//Reload Point
          || (temp.equals("G*F*PCH---****X"))//Hide Point
          || (temp.equals("G*F*PCL---****X"))//Launch Point
          || (temp.equals("G*M*BCP---****X"))//Engineer Regulating Point
          || (temp.equals("G*O*ES----****X"))//Emergency Distress Call
              
          //star
          || (temp.startsWith("G*G*GPPD-"))//Decision Point    

          //circle
          || (temp.equals("G*G*GPPO--****X"))//Coordination Point
          || (temp.equals("G*G*APP---****X"))//ACP
          || (temp.equals("G*G*APC---****X"))//CCP
          || (temp.equals("G*G*APU---****X"))//PUP

          //circle with squiggly
          || (temp.startsWith("G*G*GPUY"))//SONOBUOY and those that fall under it
              
          //reference point
          || ((temp.startsWith("G*G*GPR") && temp.charAt(7) != 'I'))

          //NBC
          || (temp.equals("G*M*NEB---****X"))//BIO
          || (temp.equals("G*M*NEC---****X")) //CHEM
          || (temp.equals("G*M*NF----****X")) //fallout producing
          || (temp.equals("G*M*NZ----****X"));//NUC

      return blRetVal;
  }
  
  public static boolean hasDefaultFill(String strSymbolID)
  {
        if(SymbolUtilities.isTacticalGraphic(strSymbolID))
        {
                String temp = SymbolUtilities.getBasicSymbolID(strSymbolID);
                //SymbolDef sd = SymbolDefTable.getInstance().getSymbolDef(temp);
                if((temp.equals("G*M*NEB---****X"))//BIO
                    || (temp.equals("G*M*NEC---****X")) //CHEM
                   // || (temp.equals("G*M*NF----****X")) //fallout producing
                    || (temp.equals("G*M*NZ----****X")))//NUC)
                {
                    return true;
                }
                else
                    return false;
        }
        else
            return true;
  }

  /**
   *
   * @param strSymbolID
   * @return
   */
  public static String getTGFillSymbolCode(String strSymbolID)
  {
      String temp = getBasicSymbolID(strSymbolID);
      if(temp.equals("G*M*NEB---****X"))
          return "NBCBIOFILL****X";
      if(temp.equals("G*M*NEC---****X"))
          return "NBCCMLFILL****X";
      if(temp.equals("G*M*NZ----****X") || temp.equals("G*M*NF----****X"))
          return "NBCNUCFILL****X";
      if(temp.startsWith("G*G*GPUY"))
          return "SONOBYFILL****X";
      if((temp.equals("G*G*GPPO--****X"))//Coordination Point
          || (temp.equals("G*G*APP---****X"))//ACP
          || (temp.equals("G*G*APC---****X"))//CCP
          || (temp.equals("G*G*APU---****X")))//PUP)
      {
          return "CPOINTFILL****X";
      }
      if(isDeconPoint(temp)//Decon Points
          || temp.startsWith("G*S*P")//TG/combat service support/points
          || (temp.equals("G*G*GPP---****X"))//Action points (general)
          || (temp.equals("G*G*GPPK--****X"))//Check Point
          || (temp.equals("G*G*GPPL--****X"))//Linkup Point
          || (temp.equals("G*G*GPPP--****X"))//Passage Point
          || (temp.equals("G*G*GPPR--****X"))//Rally Point
          || (temp.equals("G*G*GPPE--****X"))//Release Point
          || (temp.equals("G*G*GPPS--****X"))//Start Point
          || (temp.equals("G*G*GPPA--****X"))//Amnesty Point
          || (temp.equals("G*G*APD---****X"))//Down Aircrew Pickup Point
          || (temp.equals("G*G*OPP---****X"))//Point of Departure
          || (temp.equals("G*F*PCS---****X"))//Survey Control Point
          || (temp.equals("G*F*PCB---****X"))//Firing Point
          || (temp.equals("G*F*PCR---****X"))//Reload Point
          || (temp.equals("G*F*PCH---****X"))//Hide Point
          || (temp.equals("G*F*PCL---****X"))//Launch Point
          || (temp.equals("G*G*GPPN--****X"))//Entry Control Point
          || (temp.equals("G*O*ES----****X"))//Emergency Distress Call
          || (temp.equals("G*M*BCP---****X")))//Engineer Regulating Point
      {
          return "CHKPNTFILL****X";
      }
      if(temp.startsWith("G*G*GPR") && temp.charAt(7) != 'I')
      {
          return "REFPNTFILL****X";
      }
      if(temp.startsWith("G*G*GPPD"))
      {
          return "DECPNTFILL****X";
      }

      
      return null;
  }
  
  public static boolean isWeatherSPWithFill(String symbolID)
  {
      if(symbolID.equals("WOS-HPM-R-P----") ||//landing ring - brown 148,48,0
         symbolID.equals("WOS-HPD---P----")||//dolphin facilities - brown
         symbolID.equals("WOS-HABB--P----")||//buoy default - 255,0,255
         symbolID.equals("WOS-HHRS--P----")||//rock submerged - 0,204,255
         symbolID.equals("WOS-HHDS--P----")||//snags/stumps - 0,204,255
         symbolID.equals("WOS-HHDWB-P----")||//wreck - 0,204,255
         symbolID.equals("WOS-TCCTG-P----"))//tide gauge - 210, 176, 106
          return true;
      else
          return false;
  }

  /**
   * @name isSOF
   *
   * @desc Returns true if the symbolID is an SOF (special operations forces)
   * graphic
   *
   * @param strSymbolID - IN - SymbolID we are checking on
   * @return True if the graphic is a SOF in the MIL-STD 2525B, false otherwise.
   */
  public static boolean isSOF(String strSymbolID)
  {
    try
    {
      boolean blRetVal = ((strSymbolID.substring(0, 1).equals("S")) && (strSymbolID.substring(2, 3).equals("F")));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isSOF
  
/**
 * @desc Returns true if the symbol id is a Sonobuoy point symbol. 
  * 
  * @param strSymbolID - IN - Symbol Id we are checking to see if it is a Sonobuoy Point
  * @return True if the graphic is a Decon Point in the MIL-STD 2525B, false otherwise.
 */
  public static boolean isSonobuoy(String strSymbolID)
  {
    try
    {
        String basic = getBasicSymbolID(strSymbolID);
      boolean blRetVal = (basic.substring(0, 8)=="G*G*GPUY");
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isSOF

   /**
   * @name isSeaSurface
   *
   * @desc Returns true if the symbolID is an warfighting/seasurface graphic
   *
   * @param strSymbolID - IN - SymbolID we are checking on
   * @return True if the graphic is a seasurface in the MIL-STD 2525B, false otherwise.
   */
  public static boolean isSeaSurface(String strSymbolID)
  {
    try
    {
      boolean blRetVal = ((strSymbolID.substring(0, 1).equals("S")) && (strSymbolID.substring(2, 3).equals("S")));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isSOF
    
    
    /**
   * @name isSubSurface
   *
   * @desc Returns true if the symbolID is an warfighting/subsurface graphic
   *
   * @param strSymbolID - IN - SymbolID we are checking on
   * @return True if the graphic is a subsurface in the MIL-STD 2525B, false otherwise.
   */
  public static boolean isSubSurface(String strSymbolID)
  {
    try
    {
      boolean blRetVal = ((strSymbolID.substring(0, 1).equals("S")) && (strSymbolID.substring(2, 3).equals("U")));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isSOF

  /**
   * @name isChangeOne
   *
   * @desc Returns true if the graphic is a change one graphic
   *
   * @param strSymbolID - IN - A basic MilStd2525B symbolID
   * @return True if symbol is change 1, false otherwise.
   */
  public static boolean isChangeOne(String strSymbolID)
  {
    try
    {
      String strBasicSymbolID = getBasicSymbolID(strSymbolID);
      boolean blRetVal = (strBasicSymbolID.equals("G*F*ACFZ--****X")
        || strBasicSymbolID.equals("G*F*ACFFZ-****X")
        || strBasicSymbolID.equals("G*F*AP----****X")
        || strBasicSymbolID.equals("G*F*AXC---****X")
        || strBasicSymbolID.equals("G*F*AXS---****X")
        || isChangeOneCircular(strBasicSymbolID) || isChangeOneRectangular(strBasicSymbolID));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isChangeOne

  /**
   * @name isChangeOneRectangular
   *
   * @desc Returns true if the graphic is a Rectangular change one graphic
   *
   * @param strSymbolID - IN - A basic MilStd2525B symbolID
   * @return True if symbol is change 1 rectangular, false otherwise.
   */
  public static boolean isChangeOneRectangular(String strSymbolID)
  {
    try
    {
      String strBasicSymbolID = getBasicSymbolID(strSymbolID);
      String[] arr = new String[] { "G*F*ATR---****X",
                                   "G*F*ACSR--****X",
                                   "G*F*ACAR--****X",
                                   "G*F*ACFR--****X",
                                   "G*F*ACNR--****X",
                                   "G*F*ACPR--****X",
                                   "G*F*ACRR--****X",
                                   "G*F*AZIR--****X",
                                   "G*F*AZXR--****X",
                                   "G*F*AZSR--****X",
                                   "G*F*AZCR--****X",
                                   "G*F*AZDR--****X",
                                   "G*F*AZFR--****X",
                                   "G*F*AZZR--****X",
                                   "G*F*AZBR--****X",
                                   "G*F*AZVR--****X" };
      int arrLength = arr.length;
      for(int i = 0; i < arrLength; i++)
      {
        if(arr[i].equals(strBasicSymbolID))
        {
          return true;
        }
      }
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isChangeOneRectangular

  /**
   * @name isChangeOneCircular
   *
   * @desc Returns true if the graphic is a Circular change one graphic
   *
   * @param strSymbolID - IN - A basic MilStd2525B symbolID
   * @return True if symbol is change 1 Circular, false otherwise.
   */
  public static boolean isChangeOneCircular(String strSymbolID)
  {
    try
    {
      String strBasicSymbolID = getBasicSymbolID(strSymbolID);
      String[] arr = new String[] { "G*F*ATC---****X",
                                   "G*F*ACSC--****X",
                                   "G*F*ACAC--****X",
                                   "G*F*ACFC--****X",
                                   "G*F*ACNC--****X",
                                   "G*F*ACRC--****X",
                                   "G*F*AZIC--****X",
                                   "G*F*AZXC--****X",
                                   "G*F*AZSC--****X",
                                   "G*F*AZCC--****X",
                                   "G*F*AZDC--****X",
                                   "G*F*AZFC--****X",
                                   "G*F*AZZC--****X",
                                   "G*F*AZBC--****X",
                                   "G*F*AZVC--****X",
                                   "G*F*ACPC--****X" };
      int arrLength = arr.length;
      for(int i = 0; i < arrLength; i++)
      {
        if(arr[i].equals(strBasicSymbolID))
        {
          return true;
        }
      }
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End isChangeOneCircular }}

  /**
   * @name isEquipment
   *
   * @desc Returns true if the symbol id is an Equipment Id (S*G*E).
   *
   * @param strSymbolID - IN - A MilStd2525B symbolID
   * @return True if symbol is Equipment, false otherwise.
   */
  public static boolean isEquipment(String strSymbolID)
  {
    try
    {
      boolean blRetVal = ((strSymbolID.substring(0, 1).equals("S")) && 
              (strSymbolID.substring(2, 3).equals("G")) &&
              (strSymbolID.substring(4,5).equals("E")));
            // || isEMSEquipment(strSymbolID); //uncomment when supporting 2525C
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsEquipment

  /**
   * determines if an EMS symbol (a symbol code that starts with 'E')
   * Is an equipment type.  There is no logical pattern to EMS equipment
   * symbol codes so all we can do is check against a list of codes.
   * @param strSymbolID
   * @return
   */
    public static boolean isEMSEquipment(String strSymbolID)
    {
        String basicCode = getBasicSymbolIDStrict(strSymbolID);
        boolean blRetVal = false;
        try
        {
          if(strSymbolID.startsWith("E"))
          {
              if(basicCode.equals("E*O*AB----*****") || //equipment
                      basicCode.equals("E*O*AE----*****") ||//ambulance
                      basicCode.equals("E*O*AF----*****") ||//medivac helicopter
                      basicCode.equals("E*O*BB----*****") ||//emergency operation equipment
                      basicCode.equals("E*O*CB----*****") ||//fire fighting operation equipment
                      basicCode.equals("E*O*CC----*****") ||//fire hydrant
                      basicCode.equals("E*O*DB----*****") ||//law enforcement operation equipment
                      //equipment for different service departments
                      (basicCode.startsWith("E*O*D") && basicCode.endsWith("B---*****")) || 
                      //different sensor types
                      (basicCode.startsWith("E*O*E") && basicCode.endsWith("----*****")) ||
                      basicCode.equals("E*F*BA----*****") ||//ATM
                      basicCode.equals("E*F*LF----*****") ||//Heli Landing site
                      basicCode.equals("E*F*MA----*****") ||//control valve
                      basicCode.equals("E*F*MC----*****"))// ||//discharge outfall
              {
                  blRetVal = true;
              }
          }
        }
        catch(Throwable t)
        {
          System.out.println(t);
        }
        return blRetVal;
    } // End IsEquipment

   /**
   * determines if an symbol code represents an EMS (Emergency Management
   * Symbol).  Returns true only for those that start with 'E'
   * @return
   */
    public static boolean isEMS(String strSymbolID)
    {
        //String basicCode = getBasicSymbolID(strSymbolID);
        boolean blRetVal = false;
        try
        {
          if(strSymbolID.startsWith("E"))
          {
              blRetVal = true;
          }
        }
        catch(Throwable t)
        {
          System.out.println(t);
        }
        return blRetVal;
    }
    
    /**
     * Determines if a symbol is an EMS Natural Event
     * @param strSymbolID
     * @return 
     */
    public static boolean isEMSNaturalEvent(String strSymbolID)
    {
        boolean blRetVal = false;
        try
        {
          if(strSymbolID.charAt(0)=='E' && strSymbolID.charAt(2)=='N')
          {
              blRetVal = true;
          }
        }
        catch(Throwable t)
        {
          System.out.println(t);
        }
        return blRetVal;
    }
    
        /**
     * Determines if a symbol is an EMS Installation
     * @param strSymbolID
     * @return 
     */
    public static boolean isEMSInstallation(String strSymbolID)
    {
        boolean blRetVal = false;
        try
        {
          if(strSymbolID.charAt(0)=='E')
          {
            if(strSymbolID.charAt(2)=='O' &&
                    strSymbolID.charAt(4)=='D' && (strSymbolID.charAt(6)=='C' || strSymbolID.charAt(5)=='K'))
            {
                blRetVal = true;
            }
            else if(strSymbolID.charAt(2)=='F' &&
                    strSymbolID.substring(4, 6).equals("BA")==false)
            {
                blRetVal = true;
            }
            else if(strSymbolID.charAt(2)=='O')
            {
                if(strSymbolID.charAt(4)=='A')
                {
                    switch(strSymbolID.charAt(5))
                    {
                        case 'C':
                        case 'D':
                        case 'G':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                            blRetVal = true;
                            break;
                        default:
                            break;
                    }
                }
                else if(strSymbolID.charAt(4)=='B')
                {
                    switch(strSymbolID.charAt(5))
                    {
                        case 'C':
                        case 'E':
                        case 'F':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'K':
                        case 'L':
                            blRetVal = true;
                            break;
                        default:
                            break;
                    }
                }
                else if(strSymbolID.charAt(4)=='C')
                {
                    switch(strSymbolID.charAt(5))
                    {
                        case 'D':
                        case 'E':
                            blRetVal = true;
                            break;
                        default:
                            break;
                    }
                }
            }
          }
        }
        catch(Throwable t)
        {
          System.out.println(t);
        }
        return blRetVal;
    }
    
    /**
     * Determines if a symbol is an EMS Incident
     * @param strSymbolID
     * @return 
     */
    public static boolean isEMSIncident(String strSymbolID)
    {
        boolean blRetVal = false;
        try
        {
          if(strSymbolID.charAt(0)=='E' && strSymbolID.charAt(2)=='I')
          {
              blRetVal = true;
          }
        }
        catch(Throwable t)
        {
          System.out.println(t);
        }
        return blRetVal;
    }

  /**
   * @name isInstallation
   * Warfighting ground installations.  They are always installations.
   * @desc Returns true if the symbol id is an installation (S*G*I).
   *
   * @param strSymbolID - IN - A MilStd2525B symbolID
   * @return True if symbol is an Installation, false otherwise.
   */
  public static boolean isInstallation(String strSymbolID)
  {
    try
    {
        boolean blRetVal = false;
        if(strSymbolID.charAt(0)=='S')
            blRetVal = ((strSymbolID.charAt(2) == 'G') && (strSymbolID.charAt(4) == 'I'));
        else if((strSymbolID.charAt(0)=='E'))
            blRetVal = isEMSInstallation(strSymbolID);
        return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsInstallation
  

    /**
   * @name isSIGINT
   *
   * @desc Returns true if the symbol id is Signals Intelligence (SIGINT) (starts with 'I').
   *
   * @param strSymbolID - IN - A MilStd2525B symbolID
   * @return True if symbol is a Signals Intelligence, false otherwise.
   */
  public static boolean isSIGINT(String strSymbolID)
  {
    try
    {
      boolean blRetVal = (strSymbolID.substring(0, 1).equals("I"));
      return blRetVal;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false;
  } // End IsInstallation

  /**
   * @name isFeintDummyInstallation
   *
   * @desc Returns true if the symbol id has a feint dummy installation modifier
   *
   * @param strSymbolID - IN - A MilStd2525B symbolID
   * @return True if symbol has a feint dummy installation modifier, false
   * otherwise.
   */
  public static boolean isFeintDummyInstallation(String strSymbolID)
  {
    boolean feintDummyInstallationIsOn = false;
    try
    {
      // See if the feint dummy installation is on.
      feintDummyInstallationIsOn = (strSymbolID.substring(10, 11).equals("H") && strSymbolID.substring(11, 12).equals("B"));
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    // Return whether or not the feint dummy installation is on.
    return feintDummyInstallationIsOn;
  }
  
  /**
   * 
   * @param symbolID
   * @return 
   * @deprecated Incomplete and unnecessary since we have XML files for B & C
   */
  public static boolean is2525CSpecific(String symbolID)
  {
      String ID = getBasicSymbolID(symbolID);
    try
    {
      // See if the feint dummy installation is on.
      if(ID.startsWith("S"))
      {
          if(ID.equals("S*A*MV----*****") ||//VIP
                  ID.equals("S*A*ME----*****"))//ESCORT
          {
              return true;
          }
          else
              return false;
      }
      else if(ID.startsWith("O"))
      {
          return false;
      }
      else if(ID.startsWith("E"))
      {
          return false;
      }
      else
          return false;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return false; 
  }

  /**
   * has an 'H' in the 11th position
   * Any symbol can have this character added to make it an installation.
   * @param strSymbolID
   * @return
   */
  public static boolean hasInstallationModifier(String strSymbolID)
  {
    boolean hasInstallationModifier = false;
    try
    {
      // See if the feint dummy installation is on.
      hasInstallationModifier = (strSymbolID.charAt(10)==('H'));
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    // Return whether or not the feint dummy installation is on.
    return hasInstallationModifier;
  }

  /**
   * @name getAffiliation
   *
   * @desc This operation will return the affiliation enumeration for the given
   * symbol id. If the symbol has an unknown or offbeat affiliation, the
   * affiliation of "U" will be returned.
   *
   * @param strSymbolID - IN - Symbol Id we want the affiliation of
   * @return The affiliation of the Symbol Id that was passed in.
   */
  public static String getAffiliation(String strSymbolID)
  {
    try
    {
      String strAffiliation = strSymbolID.substring(1, 2);
      return strAffiliation;
    } // End try
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return "U";
  } // End GetAffiliation

  /**
   * @name getStatus
   *
   * @desc Returns the status (present / planned) for the symbol id provided. If
   * the symbol contains some other status than planned or present, present is
   * returned by default (no unknown available).
   *
   * @param strSymbolID - IN - 15 char symbol code.
   * @return The status of the Symbol Id that was passed in.
   */
  public static String getStatus(String strSymbolID)
  {
    try
    {
      String strStatus = strSymbolID.substring(3, 4);
      return strStatus;
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return "P";
  } // End getStatus

  /**
   * @name getEchelon
   *
   * @desc Returns the echelon enumeration for the symbol id provided. Note;
   * this works only with the sub-set of echelon codes tracked in the SymbolID
   * class. 2525 contains more codes than are tracked here. The 11th char of the
   * symbol id is used to determine the echelon. If we are unable to determine
   * the echelon, we return "NULL".
   *
   * @param strSymbolID - IN - 15 char symbol code.
   * @return The echelon of the Symbol Id that was passed in.
   */
  public static String getEchelon(String strSymbolID)
  {
    try
    {
      String strSubEch = strSymbolID.substring(11, 12);
      return strSubEch;
    } // End try
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return "-";
  } // End getEchelon

  /**
   * @name getSymbolModifier
   *
   * @desc Returns the enumeration belonging to the type of symbol modifier used
   * for this symbol ID.
   *
   * @param strSymbolID - IN - A valid 15 char MilStd2525B code.
   * @return The enumeration indicating which symbol modifier is turned on.
   * @deprecated 9/6/2013
   */
  public static String getSymbolModifier(String strSymbolID)
  {
    try
    {
      String strModifiers = strSymbolID.substring(10, 11);
      String subModifier;
      if(strModifiers.equals("A"))
      {
        return "A";
      }
      else if(strModifiers.equals("B"))
      {
        return "B";
      }
      else if(strModifiers.equals("C"))
      {
        return "C";
      }
      else if(strModifiers.equals("D"))
      {
        return "D";
      }
      else if(strModifiers.equals("E"))
      {
        return "E";
      }
      else if(strModifiers.equals("F"))
      {
        return "F";
      }
      else if(strModifiers.equals("G"))
      {
        return "G";
      }
      else if(strModifiers.equals("H"))
      {
        subModifier = strSymbolID.substring(11, 12);
        if(subModifier.equals("B"))
        {
          return "HB";
        }
        else
        {
          return "H";
        }
      }
      else if(strModifiers.equals("M"))
      {
        subModifier = strSymbolID.substring(11, 12);
        if(subModifier.equals("O"))
        {
          return "MO";
        }
        else
        {
          return "M";
        }
      }
    } // End try
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return "-";
  } // End GetSymbolModifier
  
  public static String getUnitAffiliationModifier(String symbolID, int symStd)
  {
      String textChar = null;
      char affiliation;
      
      try
      {
        affiliation = symbolID.charAt(1);
      
        if(affiliation==('F') ||
            affiliation==('H') ||
            affiliation==('U') ||
            affiliation==('N') ||
            affiliation==('P'))
        textChar=null;
        else if(affiliation==('A') ||
                affiliation==('S'))
        {
            if(symStd==RendererSettings.Symbology_2525B)
                textChar = "?";
            else
                textChar=null;
        }
        else if(affiliation==('J'))
            textChar = "J";
        else if(affiliation==('K'))
            textChar = "K";
        else if(affiliation==('D') ||
                affiliation==('L') ||
                affiliation==('G') ||
                affiliation==('W'))
            textChar = "X";
        else if(affiliation==('M'))
        {
            if(symStd==RendererSettings.Symbology_2525B)
                textChar = "X?";
            else
                textChar = "X";
        }

        //check sea mine symbols
        if(symStd==RendererSettings.Symbology_2525C) 
        {
            if(symbolID.charAt(0)=='S' && symbolID.indexOf("WM")==4)
            {//variuos sea mine exercises
                if(symbolID.indexOf("GX")==6 ||
                        symbolID.indexOf("MX")==6 ||
                        symbolID.indexOf("FX")==6 ||
                        symbolID.indexOf("X")==6 ||
                        symbolID.indexOf("SX")==6)
                    textChar = "X";
                else
                    textChar=null;
            }
        }
      }
      catch(Exception exc)
      {
          ErrorLogger.LogException("SymbolUtilities", 
                  "getUnitAffiliationModifier", exc, Level.WARNING);
          return null;
      }
      return textChar;
  }

  /**
   * checks symbol code to see if graphic has a DOM (Q) modifier
   * @param symbolID
   * @param symStd RendererSettings.Symbology_2525C
   * @return
   */
  public static boolean hasDirectionOfMovement(String symbolID, int symStd)
  {
      SymbolDef temp = null;
      if(isNBC(symbolID))//just 3 NBCs have DOM
      {
          temp = SymbolDefTable.getInstance().getSymbolDef(getBasicSymbolID(symbolID),symStd);
          if(temp != null)
          {
              if(temp.getModifiers().indexOf("Q.") != -1)
                  return true;
              else
                  return false;
                  
          }
          else
              return false;
      }
      else if(isWarfighting(symbolID))//all warfighting has DOM
      {
          if(SymbolUtilities.isSIGINT(symbolID) == false)
            return true;
          else
            return false;
      }
      else
          return false;
  }

  public static Boolean hasAMmodifierWidth(String symbolID)
  {
      return hasAMmodifierWidth(symbolID, RendererSettings.getInstance().getSymbologyStandard());
  }
  
  public static Boolean hasAMmodifierWidth(String symbolID, int symStd)
  {
        SymbolDef sd = null;
        Boolean returnVal = false;
        String basic = SymbolUtilities.getBasicSymbolID(symbolID);
            
        sd = SymbolDefTable.getInstance().getSymbolDef(basic, symStd);
        if(sd != null)
        {
            int dc = sd.getDrawCategory();
        
            switch(dc)
            {
                case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE://width
                case SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                case SymbolDef.DRAW_CATEGORY_TWO_POINT_RECT_PARAMETERED_AUTOSHAPE: 
                    returnVal = true;
                    break;
                case SymbolDef.DRAW_CATEGORY_LINE:
                    if(sd.getModifiers().indexOf(ModifiersTG.AM_DISTANCE + ".")>-1)
                        returnVal = true;
                    break;
                default:
                    returnVal = false;
            }
        }
        
        return returnVal;
  }
  
  public static Boolean hasANmodifier(String symbolID)
  {
      return hasANmodifier(symbolID, RendererSettings.getInstance().getSymbologyStandard());
  }
  
  public static Boolean hasANmodifier(String symbolID, int symStd)
  {
        SymbolDef sd = null;
        Boolean returnVal = false;
        String basic = SymbolUtilities.getBasicSymbolID(symbolID);
            
        sd = SymbolDefTable.getInstance().getSymbolDef(basic, symStd);
        if(sd != null)
        {
            int dc = sd.getDrawCategory();
        
            switch(dc)
            {
                case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE://width
                case SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                    returnVal = true;
                    break;
                default:
                    returnVal = false;
            }
        }
        
        return returnVal;
  }
  
  public static Boolean hasAMmodifierRadius(String symbolID)
  {
      return hasAMmodifierRadius(symbolID, RendererSettings.getInstance().getSymbologyStandard());
  }

  public static Boolean hasAMmodifierRadius(String symbolID, int symStd)
  {
        SymbolDef sd = null;
        Boolean returnVal = false;
        String basic = SymbolUtilities.getBasicSymbolID(symbolID);
            
        sd = SymbolDefTable.getInstance().getSymbolDef(basic, symStd);
        if(sd != null)
        {
            int dc = sd.getDrawCategory();
        
            switch(dc)
            {
                case SymbolDef.DRAW_CATEGORY_CIRCULAR_PARAMETERED_AUTOSHAPE://radius
                case SymbolDef.DRAW_CATEGORY_CIRCULAR_RANGEFAN_AUTOSHAPE:
                    returnVal = true;
                    break;
                default:
                    returnVal = false;
            }
        }
        
        return returnVal;
  }

  /**
   * @name setAffiliation
   *
   * @desc Sets the affiliation for a Mil-Std 2525B symbol ID.
   *
   * @param strSymbolID - IN - A 15 character symbol ID
   * @param strSymbolID - IN - The affiliation we want to change the ID to.
   * @return A string with the affiliation changed to affiliationID
   */
  public static String setAffiliation(String strSymbolID, String strAffiliationID)
  {
    try
    {
      if(strSymbolID != null && strSymbolID.length() == 15 &&
          isWeather(strSymbolID)==false &&
          strAffiliationID != null && strAffiliationID.length() == 1)
      {
        String strChangedID = strSymbolID.substring(0, 1) + strAffiliationID.toUpperCase() + strSymbolID.substring(2, 15);
        if(hasValidAffiliation(strChangedID))
            return strChangedID;
        else
            return strSymbolID;
      }
      else
      {
        return strSymbolID;
      }
    } // End try
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return "";
  } // End SetAffiliation }

  /**
   * @name setEchelon
   *
   * @desc Sets the echelon for a Mil-Std 2525B symbol ID.
   *
   * @param strSymbolID - IN - A 15 character symbol ID
   * @param strSymbolID - IN - A string representing the echelon we want to
   * change the ID to. The case of the string does not matter, it can be upper
   * or lower. The string is the name of the echelon and can be of the following
   * choices:
   *
   * Null, //- Team, Crew, //A Squad, //B Section, //C Platoon, Detachment //D
   * Company, Battery, Troop //E Battalion, Squadron, //F Regiment, Group, //G
   * Brigade, //H Division, //I Corps, Mef, //J Army, //K Army Group, Front, //L
   * Region //M
   * @return A symbol ID with the echelon changed to echelonID
   */
  public static String setEchelon(String strSymbolID, String strEchelon)
  {
    String strChangedID = strSymbolID;
    try
    {
      if(strSymbolID.length() == 15)
      {
        String strUppercaseEchelon = strEchelon.toUpperCase();
        strChangedID = strSymbolID.substring(0, 11) + strUppercaseEchelon + strSymbolID.substring(12, 15);
      } // End if (strSymbolID.Length == 15 &&
      // !SymbolUtilities.IsDrawingPrimitive(strSymbolID))
    } // End try
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return strChangedID;
  } // End SetEchelon }}}}

  /**
   * @name setStatus
   *
   * @desc Sets the status for a Mil-Std 2525B symbol ID.
   *
   * @param strSymbolID - IN - A 15 character symbol ID
   * @param strStatusID - IN - The status we want to change the ID to.
   * "present", "planned", "anticipated", "plannedanticipated"
   * @return A string with the status changed to statusID
   * @deprecated 
   */
  public static String setStatus(String strSymbolID, String strStatusID)
  {
    // PlannedAnticipated, //A
    // Present //P
    String changedID = strSymbolID;
    try
    {
      if((strSymbolID.length() == 15)
        && (!isBridge(strSymbolID))
        && (!isWeather(strSymbolID))
        && (!isJWARN(strSymbolID)))
      {
        if(strStatusID.toLowerCase().equals("present"))
        {
          changedID = strSymbolID.substring(0, 3) + "P" + strSymbolID.substring(4,15);
        }
        else if(strStatusID.equalsIgnoreCase("planned")
          || strStatusID.equalsIgnoreCase("anticipated")
          || strStatusID.equalsIgnoreCase("plannedanticipated"))
        {
          changedID = strSymbolID.substring(0, 3) + "A" + strSymbolID.substring(4,15);
        }
      } // End if((strSymbolID.Length == 15) && (!
      // SymbolUtilities.IsDrawingPrimitive(strSymbolID)))
    }
    catch(Throwable t)
    {
      System.out.println(t);
    }
    return changedID;
  } // End SetStatus

  /**
   * @name setSymbolModifier
   *
   * @desc Sets the symbol modifier for a Mil-Std 2525B symbol ID.
   *
   * @param strSymbolID - IN - A 15 character symbol ID
   * @param symbolModifierID - IN - The symbolModifier we want to change the ID to.
   * @return A symbol ID with the symbolModifier changed to symbolModifierID
   * @deprecated 
   */
  public static String setSymbolModifier(String strSymbolID, String symbolModifierID)
  {
    String strChangedID = strSymbolID;
    if(strSymbolID.length() == 15)
    {
      if(symbolModifierID.equals("-"))
      {
        if(isMobilityWheeled(strSymbolID) || isFeintDummyInstallation(strSymbolID))
        {
          strChangedID = strSymbolID.substring(0, 10) + "--" + strSymbolID.substring(12, 15);
        }
        else
        {
          strChangedID = strSymbolID.substring(0, 10) + "-" + strSymbolID.substring(11, 15);
        }
      }
      else if(symbolModifierID.equals("A"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "A" + strSymbolID.substring(11, 15);
      }
      else if(symbolModifierID.equals("B"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "B" + strSymbolID.substring(11, 15);
      }
      else if(symbolModifierID.equals("C"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "C" + strSymbolID.substring(11, 15);
      }
      else if(symbolModifierID.equals("D"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "D" + strSymbolID.substring(11, 15);
      }
      else if(symbolModifierID.equals("E"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "E" + strSymbolID.substring(11, 15);
      }
      else if(symbolModifierID.equals("F"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "F" + strSymbolID.substring(11, 15);
      }
      else if(symbolModifierID.equals("G"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "G" + strSymbolID.substring(11, 15);
      }
      else if(symbolModifierID.equals("HB"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "HB" + strSymbolID.substring(12, 15);
      }
      else if(symbolModifierID.equals("HH"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "H" + strSymbolID.substring(11, 15);
      }
      else if(symbolModifierID.equals("M"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "M" + strSymbolID.substring(11, 15);
      }
      else if(symbolModifierID.equals("MO"))
      {
        strChangedID = strSymbolID.substring(0, 10) + "MO" + strSymbolID.substring(12, 15);
      }
    } // End if
    return strChangedID;
  } // End SetSymbolModifier

  
}
