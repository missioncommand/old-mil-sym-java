/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ArmyC2.C2SD.Utilities;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author michael.spinelli
 */
public class UnitFontLookup {

     private static UnitFontLookup _instance;
  private boolean _ready = false;
  private static HashMap<String, UnitFontLookupInfo> hashMapB = new HashMap<String, UnitFontLookupInfo>();
  private static HashMap<String, UnitFontLookupInfo> hashMapC = new HashMap<String, UnitFontLookupInfo>();

  //UNKNOWN FILL Indexes
  private static final int FillIndexUZ = 800;//Unknown
  private static final int FillIndexUP = 849;//Space
  private static final int FillIndexUA = 825;//Air
  private static final int FillIndexUG = 800;//Ground
  private static final int FillIndexUGE = 800;//Ground Equipment
  private static final int FillIndexUS = 800;//Sea Surface
  private static final int FillIndexUU = 837;//Subsurface
  private static final int FillIndexUF = 800;//SOF
   //FRIENDLY FILL Indexes
  private static final int FillIndexFZ = 812;
  private static final int FillIndexFP = 843;
  private static final int FillIndexFA = 819;
  private static final int FillIndexFG = 803;
  private static final int FillIndexFGE = 812;
  private static final int FillIndexFS = 812;
  private static final int FillIndexFU = 831;
  private static final int FillIndexFF = 803;
   //NEUTRAL FILL Indexes
  private static final int FillIndexNZ = 809;
  private static final int FillIndexNP = 846;
  private static final int FillIndexNA = 822;
  private static final int FillIndexNG = 809;
  private static final int FillIndexNGE = 809;
  private static final int FillIndexNS = 809;
  private static final int FillIndexNU = 834;
  private static final int FillIndexNF = 809;
   //HOSTILE FILL Indexes
  private static final int FillIndexHZ = 806;
  private static final int FillIndexHP = 840;
  private static final int FillIndexHA = 816;
  private static final int FillIndexHG = 806;
  private static final int FillIndexHGE = 806;
  private static final int FillIndexHS = 806;
  private static final int FillIndexHU = 828;
  private static final int FillIndexHF = 806;

  //Font positions for new layout
  //770-799: small fills, inside the frame
  //800-900: regular fills with solid & dashed frame
  //1000 - 3000?: Warfighting - A -
  //3000 - 3200?: SigInt - D - 80 only need fill/frame/symbol
  //3200? - 3400?: Stability Operations Symbology - E - 60 w/ 6 that need 4 symbols & 6 with secondary symbol / fill
  //4000+: Emergency Management Symbols - G -

  //
  public boolean getReady()
  {
    return this._ready;
  }

  private UnitFontLookup()
  {
    init();
  }

  public static synchronized UnitFontLookup getInstance()
  {
    if(_instance == null)
    {
      _instance = new UnitFontLookup();
    }
    return _instance;
  }

  private void init()
  {
    xmlLoaded();
  }

  private void xmlLoaded()
  {
      String xmlPathB = "XML/UnitFontMappingsB.xml";
      String xmlPathC = "XML/UnitFontMappingsC.xml";
        
      
    InputStream xmlStreamB = this.getClass().getClassLoader().getResourceAsStream(xmlPathB);
    InputStream xmlStreamC = this.getClass().getClassLoader().getResourceAsStream(xmlPathC);

    String lookupXmlB = FileHandler.InputStreamToString(xmlStreamB);
    String lookupXmlC = FileHandler.InputStreamToString(xmlStreamC);

    populateLookup(lookupXmlB,RendererSettings.Symbology_2525B);
    populateLookup(lookupXmlC,RendererSettings.Symbology_2525C);
  }

  private void populateLookup(String xml, int SymbologyStandard)
  {

    ArrayList<String> al = XMLUtil.getItemList(xml, "<SYMBOL>", "</SYMBOL>");
    for(int i = 0; i < al.size(); i++)
    {
      String data = (String)al.get(i);
      String ID = XMLUtil.parseTagValue(data, "<SYMBOLID>", "</SYMBOLID>");
      String description = XMLUtil.parseTagValue(data, "<DESCRIPTION>", "</DESCRIPTION>");
      String m1u = XMLUtil.parseTagValue(data, "<MAPPING1U>", "</MAPPING1U>");
      String m1f = XMLUtil.parseTagValue(data, "<MAPPING1F>", "</MAPPING1F>");
      String m1n = XMLUtil.parseTagValue(data, "<MAPPING1N>", "</MAPPING1N>");
      String m1h = XMLUtil.parseTagValue(data, "<MAPPING1H>", "</MAPPING1H>");
      String m2 = XMLUtil.parseTagValue(data, "<MAPPING2>", "</MAPPING2>");
      String c1 = XMLUtil.parseTagValue(data, "<MAPPING1COLOR>", "</MAPPING1COLOR>");
      String c2 = XMLUtil.parseTagValue(data, "<MAPPING2COLOR>", "</MAPPING2COLOR>");

      UnitFontLookupInfo uflTemp = null;
      
      //Check for bad font locations and remap
      m1u = checkMappingIndex(m1u);
      m1f = checkMappingIndex(m1f);
      m1n = checkMappingIndex(m1n);
      m1h = checkMappingIndex(m1h);
      m2 = checkMappingIndex(m2);
      ////////////////////////////////////////

      uflTemp =  new UnitFontLookupInfo(ID, description, m1u, m1f, m1n, m1h, c1, m2, c2);

        if(uflTemp != null)
        {
            if(SymbologyStandard == RendererSettings.Symbology_2525B)
                hashMapB.put(ID, uflTemp);
            else if(SymbologyStandard == RendererSettings.Symbology_2525C)
                hashMapC.put(ID, uflTemp);
        }
    }

  }
  
    /**
   * Until XML files are updated, we need to shift the index
   * @param index
   * @return 
   */  
  private static String checkMappingIndex(String index)
  {
      int i = -1;
      if(SymbolUtilities.isNumber(index))
      {
          i = Integer.valueOf(index);
          
          if(i < 9000)
        	  return String.valueOf(i + 57000);
          else
        	  return String.valueOf(i + 54000);
      }          
      return index;//*/
 
  }

  /**
   * we only have font lookups for F,H,N,U.  But the shapes match one of these
   * four for the remaining affiliations.  So we convert the string to a base
   * affiliation before we do the lookup.
   * @param symbolID
   * @return
   */
  private String resolveAffiliation(String symbolID)
  {
      String code = symbolID.substring(0);
      String affiliation = symbolID.substring(1,2);

      if(affiliation.equals("F") ||//friendly
              affiliation.equals("H") ||//hostile
              affiliation.equals("U") ||//unknown
              affiliation.equals("N") )//neutral
          return code;
      else if(affiliation.equals("S"))//suspect
          code = code.substring(0, 1) + "H" + code.substring(2, 15);
      else if(affiliation.equals("L"))//exercise neutral
          code = code.substring(0, 1) + "N" + code.substring(2, 15);
      else if(affiliation.equals("A") ||//assumed friend
              affiliation.equals("D") ||//exercise friend
              affiliation.equals("M") ||//exercise assumed friend
              affiliation.equals("K") ||//faker
              affiliation.equals("J"))//joker
          code = code.substring(0, 1) + "F" + code.substring(2, 15);
      else if(affiliation.equals("P") ||//pending
              affiliation.equals("G") ||//exercise pending
              affiliation.equals("O") ||//? brought it over from mitch's code
              affiliation.equals("W"))//exercise unknown
          code = code.substring(0, 1) + "U" + code.substring(2, 15);
      else
          code = code.substring(0, 1) + "U" + code.substring(2, 15);

      return code;
  }

 /**
  * @deprecated 
  * @param original
  * @return 
  */
  public static UnitFontLookupInfo adjustSubSurfaceIfNot2525C(UnitFontLookupInfo original)
  {
      UnitFontLookupInfo result = null;
      
      return result;
  }

  /**
   * 2525C
   * returns the character index for the fill frame based on the symbol code.
   * @param SymbolID 15 character symbol ID
   * @param symStd 0 for 2525B, 1 for 2525C
   * @return fill character index
   */
  public static int getFillCode(String SymbolID, int symStd)
  {
      int returnVal = -1;

      char scheme = 0;
      char battleDimension = 0;
      char status = 0;
      char affiliation = 0;
      char grdtrkSubset = 0;
      //char foo = 'a';


      try
      {
          //to upper
          if(SymbolID != null && SymbolID.length() >= 10)
          {
              scheme = SymbolID.charAt(0);//S,O,E,I,etc...
              affiliation = SymbolID.charAt(1);//F,H,N,U,etc...
              battleDimension = SymbolID.charAt(2);//P,A,G,S,U,F,X,Z
              status = SymbolID.charAt(3);//A,P,C,D,X,F
              grdtrkSubset = SymbolID.charAt(4);

              if(scheme == 'S')//Warfighting symbols
              {
                  if(affiliation == 'F' ||
                          affiliation == 'A' ||
                          affiliation == 'D' ||
                          affiliation == 'M' ||
                          affiliation == 'J' ||
                          affiliation == 'K')
                  {
                      
                      if(battleDimension=='F' || battleDimension=='G')//ground & SOF
                      {
                          if(battleDimension=='F' ||
                                  (battleDimension=='G' &&
                                    (grdtrkSubset=='U' || grdtrkSubset=='I' || grdtrkSubset=='0'|| grdtrkSubset=='-')))
                          {
                              returnVal = 803;
                          }
                          else if(battleDimension=='G' && grdtrkSubset=='E')
                          {
                              returnVal = 812;
                          }
                          else
                              returnVal = 803;
                      }
                      else if(battleDimension=='A')//Air
                      {
                          returnVal = 819;
                      }
                      else if(battleDimension=='S')//SeaSurface
                      {
                          returnVal = getSeaSurfaceFill(SymbolID);
                      }
                      else if(battleDimension=='U')//Subsurface
                      {
                          returnVal = getSubSurfaceFill(SymbolID, symStd);
                      }
                      else if(battleDimension=='P')//space
                      {
                        if(symStd == 0)
                            returnVal = 819;
                        else
                            returnVal = 843;
                      }
                      else//if(battleDimension=='Z')//unknown
                      {
                          returnVal = 812;//index in font file
                      }
                  }
                  else if(affiliation == 'H' || affiliation == 'S')//hostile,suspect
                  {
                      if(battleDimension=='F' || battleDimension=='G')//ground & SOF
                      {
                          returnVal = 806;
                      }
                      else if(battleDimension=='A')//Air
                      {
                          returnVal = 816;
                      }
                      else if(battleDimension=='S')//SeaSurface
                      {
                          returnVal = getSeaSurfaceFill(SymbolID);
                      }
                      else if(battleDimension=='U')//Subsurface
                      {
                          returnVal = getSubSurfaceFill(SymbolID, symStd);
                      }
                      else if(battleDimension=='P')//space
                      {
                          if(symStd == 0)
                            returnVal = 816;
                        else
                            returnVal = 840;
                      }
                      else//if(battleDimension=='Z')//unknown
                      {
                          returnVal = 806;//index in font file
                      }
                  }
                  else if(affiliation == 'N' || affiliation == 'L')//neutral,exercise neutral
                  {
                      if(battleDimension=='F' || battleDimension=='G')//ground & SOF
                      {
                          returnVal = 809;
                      }
                      else if(battleDimension=='A')//Air
                      {
                          returnVal = 822;
                      }
                      else if(battleDimension=='S')//SeaSurface
                      {
                          returnVal = getSeaSurfaceFill(SymbolID);
                      }
                      else if(battleDimension=='U')//Subsurface
                      {
                          returnVal = getSubSurfaceFill(SymbolID, symStd);
                      }
                      else if(battleDimension=='P')//space
                      {
                          if(symStd == 0)
                            returnVal = 822;
                        else
                            returnVal = 846;
                      }
                      else//if(battleDimension=='Z')//unknown
                      {
                          returnVal = 809;//index in font file
                      }
                  }
                  else /*if(affiliation == 'P' ||
                     affiliation == 'U' ||
                     affiliation == 'G' ||
                     affiliation == 'W')//*/ //these or bad affiliations
                  {

                      if(battleDimension=='Z' ||//unknown
                            battleDimension=='G' ||//ground
                            battleDimension=='F')//SOF
                      {
                          returnVal = 800;//index in font file
                      }
                      else if(battleDimension=='A')//Air
                      {
                          returnVal = 825;
                      }
                      else if(battleDimension=='S')//SeaSurface
                      {
                          returnVal = getSeaSurfaceFill(SymbolID);
                      }
                      else if(battleDimension=='U')//Subsurface
                      {
                          returnVal = getSubSurfaceFill(SymbolID, symStd);
                      }
                      else if(battleDimension=='P')//space
                      {
                          if(symStd == 0)
                            returnVal = 825;
                        else
                            returnVal = 849;
                      }
                      else
                          returnVal = 800;
                  }
              }//end if scheme == 's'
              else if(scheme == 'E')//Emergency Management Symbols
              {
                  if(battleDimension != 'N')//if not EMS natural event
                  {
                      if(affiliation == 'F' ||
                              affiliation == 'A' ||
                              affiliation == 'D' ||
                              affiliation == 'M' ||
                              affiliation == 'J' ||
                              affiliation == 'K')
                      {

                          //EMS symbols break some rules about symbol codes
                          if(SymbolUtilities.isEMSEquipment(SymbolID))
                              returnVal = 812;
                          else
                            returnVal = 803;
                      }
                      else if(affiliation == 'H' || affiliation == 'S')//hostile,suspect
                      {
                          returnVal = 806;//index in font file

                      }
                      else if(affiliation == 'N' || affiliation == 'L')//neutral,exercise neutral
                      {
                          returnVal = 809;
                      }
                      else /*if(affiliation == 'P' ||
                         affiliation == 'U' ||
                         affiliation == 'G' ||
                         affiliation == 'W')*/
                      {
                          returnVal = 800;//index in font file
                      }
                  }
                  else //natural events do not have a fill/frame
                  {
                      returnVal = -1;
                  }
              }//end if scheme == 'E'
              else if(scheme == 'I')//Also default behavior
              {
                  if(affiliation == 'F' ||
                          affiliation == 'A' ||
                          affiliation == 'D' ||
                          affiliation == 'M' ||
                          affiliation == 'J' ||
                          affiliation == 'K')
                  {
                      if(battleDimension=='Z')//unknown
                      {
                          returnVal = 812;//index in font file
                      }
                      else if(battleDimension=='F' || battleDimension=='G' || battleDimension=='S')//ground & SOF & sea surface
                      {
                          if(scheme=='I')
                            returnVal = 812;
                          else
                            returnVal = 803;
                      }
                      else if(battleDimension=='A')//Air
                      {
                          returnVal = 819;
                      }
                      else if(battleDimension=='U')//Subsurface
                      {
                          returnVal = 831;
                      }
                      else if(battleDimension=='P')//space
                      {
                          if(symStd == 0)
                            returnVal = 819;
                        else
                            returnVal = 843;
                      }
                      else
                      {
                          if(scheme=='I')
                            returnVal = 812;
                          else
                            returnVal = 803;
                      }
                  }
                  else if(affiliation == 'H' || affiliation == 'S')//hostile,suspect
                  {
                      if(battleDimension=='Z')//unknown
                      {
                          returnVal = 806;//index in font file
                      }
                      else if(battleDimension=='F' || battleDimension=='G' || battleDimension=='S')//ground & SOF & sea surface
                      {
                          returnVal = 806;
                      }
                      else if(battleDimension=='A')//Air
                      {
                          returnVal = 816;
                      }
                      else if(battleDimension=='U')//Subsurface
                      {
                          returnVal = 828;
                      }
                      else if(battleDimension=='P')//space
                      {
                          if(symStd == 0)
                            returnVal = 816;
                        else
                            returnVal = 840;
                      }
                      else
                      {
                          returnVal = 806;
                      }
                  }
                  else if(affiliation == 'N' || affiliation == 'L')//neutral,exercise neutral
                  {
                      if(battleDimension=='Z')//unknown
                      {
                          returnVal = 809;//index in font file
                      }
                      else if(battleDimension=='F' || battleDimension=='G' || battleDimension=='S')//ground & SOF & sea surface
                      {
                          returnVal = 809;
                      }
                      else if(battleDimension=='A')//Air
                      {
                          returnVal = 822;
                      }
                      else if(battleDimension=='U')//Subsurface
                      {
                          returnVal = 834;
                      }
                      else if(battleDimension=='P')//space
                      {
                          if(symStd == 0)
                            returnVal = 822;
                        else
                            returnVal = 846;
                      }
                      else
                      {
                          returnVal = 809;
                      }
                  }
                  else /*if(affiliation == 'P' ||
                     affiliation == 'U' ||
                     affiliation == 'G' ||
                     affiliation == 'W')//*/
                  {

                      if(battleDimension=='Z' ||//unknown
                            battleDimension=='G' ||//ground
                            battleDimension=='S' ||//sea surface
                            battleDimension=='F')//SOF
                      {
                          returnVal = 800;//index in font file
                      }
                      else if(battleDimension=='A')//Air
                      {
                          returnVal = 825;
                      }
                      else if(battleDimension=='U')//Subsurface
                      {
                          returnVal = 837;
                      }
                      else if(battleDimension=='P')//Space
                      {
                          if(symStd == 0)
                            returnVal = 825;
                        else
                            returnVal = 849;
                      }
                      else
                      {
                          returnVal = 800;
                      }
                  }
              }//end if scheme == 'I'
              else//scheme = 'O' and anything else
              {
                  if(affiliation == 'F' ||
                          affiliation == 'A' ||
                          affiliation == 'D' ||
                          affiliation == 'M' ||
                          affiliation == 'J' ||
                          affiliation == 'K')
                  {
                      if(SymbolID.startsWith("OFI") && SymbolID.substring(4,10).equals("T-----"))
                      {
                          //friendly tent is the ONE STBOPS that draws like equipment.
                          returnVal = FillIndexFGE;
                      }
                      else
                      {
                          returnVal = 803;
                      }
                  }
                  else if(affiliation == 'H' || affiliation == 'S')//hostile,suspect
                  {
                      returnVal = 806;//index in font file
                  }
                  else if(affiliation == 'N' || affiliation == 'L')//neutral,exercise neutral
                  {
                      returnVal = 809;
                  }
                  else /*if(affiliation == 'P' ||
                     affiliation == 'U' ||
                     affiliation == 'G' ||
                     affiliation == 'W')*/
                  {
                      returnVal = 800;//index in font file
                  }
              }//end default

          }
          else
          {
              returnVal = 800;
          }

      }
      catch(Exception exc)
      {
          ErrorLogger.LogException("UnitFontLookup", "getFillCode", exc, Level.SEVERE);
      }

      return returnVal + 57000;
  }

  public static int getFrameCode(String SymbolID, int FillCode)
  {
      return getFrameCode(SymbolID, FillCode,RendererSettings.getInstance().getSymbologyStandard());
  }
  
  public static int getFrameCode(String SymbolID, int FillCode, int symStd)
  {
      int returnVal = 0;
      char status = SymbolID.charAt(3);

      
      if(status == 'A')
          returnVal = FillCode + 2;
      else//P, C, D, X, F
          returnVal = FillCode + 1;
      
      if(symStd > RendererSettings.Symbology_2525B && status != 'A')
      {
          char affiliation = SymbolID.charAt(1);
          switch(affiliation)
          {
              case 'P':
              case 'A':
              case 'S':
              case 'G':
              case 'M':
                  returnVal++;
                  break;
          }
          
      }

      if(SymbolUtilities.isSeaSurface(SymbolID))
      {
          returnVal = getSeaSurfaceFrame(SymbolID, FillCode);
      }
      if(SymbolUtilities.isSubSurface(SymbolID))
      {
          returnVal = getSubSurfaceFrame(SymbolID, FillCode, symStd);
      }

      return returnVal;

  }

  private static int getSeaSurfaceFill(String SymbolID)
  {
      char affiliation = 0;
      char status = 0;
      int returnVal = 0;
      
      try
      {
        affiliation = SymbolID.charAt(1);//F,H,N,U,etc...
        status = SymbolID.charAt(3);//A,P,C,D,X,F

        if(SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*S*O-----*****"))
        {
          return 2016;  
        }
        else
        {
            if(affiliation == 'F' ||
                        affiliation == 'A' ||
                        affiliation == 'D' ||
                        affiliation == 'M' ||
                        affiliation == 'J' ||
                        affiliation == 'K')
            {
                return FillIndexFS;
            }
            else if(affiliation == 'H' || affiliation == 'S')//hostile,suspect
            {
                return FillIndexHS;
            }
            else if(affiliation == 'N' || affiliation == 'L')//neutral,exercise neutral
            {
                return FillIndexNS;
            }
            else if(affiliation == 'P' ||
                     affiliation == 'U' ||
                     affiliation == 'G' ||
                     affiliation == 'W')
            {
                return FillIndexUS;
            }
        }
      }
      catch(Exception exc)
      {
          ErrorLogger.LogException("UnitFontLookupC", "getSeaSurfaceFill", exc);
          return FillIndexUG;
      }
      return FillIndexUG;
  }
  
  private static int getSubSurfaceFill(String SymbolID, int symStd)
  {
      char affiliation = 0;
      char status = 0;
      int returnVal = 0;

      returnVal = 831;

      try
      {
          affiliation = SymbolID.charAt(1);//F,H,N,U,etc...
          status = SymbolID.charAt(3);//A,P,C,D,X,F

          if(affiliation == 'F' ||
                  affiliation == 'A' ||
                  affiliation == 'D' ||
                  affiliation == 'M' ||
                  affiliation == 'J' ||
                  affiliation == 'K')
          {
                returnVal = 831;//
          }
         else if(affiliation == 'H' || affiliation == 'S')//hostile,suspect
          {
              returnVal = 828;//index in font file

          }
         else if(affiliation == 'N' || affiliation == 'L')//neutral,exercise neutral
          {
              returnVal = 834;
          }
          else if(affiliation == 'P' ||
             affiliation == 'U' ||
             affiliation == 'G' ||
             affiliation == 'W')
          {
              returnVal = 837;//index in font file
          }
          
          //appears in USAS so we check in both standards
          if(SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*X-----*****"))
          {
              if(status=='A')
                  returnVal = returnVal+2;
              else
                  returnVal++;
          }

          //Special check for sea mine graphics
          //2525C///////////////////////////////////////////////////////////////
          if(symStd == RendererSettings.Symbology_2525C)
          {
              if(SymbolID.indexOf("WM")==4 || //Sea Mine
                      SymbolID.indexOf("WDM")==4 ||//Sea Mine Decoy
                      SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*E-----*****") ||
                      SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*V-----*****"))
              {
                  returnVal++;

                  if(status == 'A')
                      returnVal++;

              }
              else if(SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*ND----*****"))
              {
                  returnVal = 2121;
              }
          }
          else//2525Bch2////////////////////////////////////////////////////////
          {
              if(SymbolID.indexOf("WM")==4)//Sea Mine
              {
                  if(SymbolID.indexOf("----", 6)==6 || SymbolID.indexOf("D---", 6)==6)
                      returnVal = 2059;//
                  else if(SymbolID.indexOf("G---", 6)==6)
                      returnVal = 2062;
                  else if(SymbolID.indexOf("GD--", 6)==6)
                      returnVal = 2064;
                  else if(SymbolID.indexOf("M---", 6)==6)
                      returnVal = 2073;
                  else if(SymbolID.indexOf("MD--", 6)==6)
                      returnVal = 2075;
                  else if(SymbolID.indexOf("F---", 6)==6)
                      returnVal = 2084;
                  else if(SymbolID.indexOf("FD--", 6)==6)
                      returnVal = 2086;
                  else if(SymbolID.indexOf("O---", 6)==6 ||
                          SymbolID.indexOf("OD--", 6)==6)
                      returnVal = 2094;

              }
              else if(SymbolID.indexOf("WDM")==4)//Sea Mine Decoy
              {
                    returnVal = 2115;
              }
              else if(SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*ND----*****"))
              {
                    returnVal = 2121;
              }//
          }
      }
      catch(Exception exc)
      {
          ErrorLogger.LogException("UnitFontLookupC", "getSubSurfaceFill", exc);
          return FillIndexUU;
      }

      return returnVal;
  }

  private static int getSeaSurfaceFrame(String SymbolID, int fillCode)
  {
      int returnVal = fillCode+1;

      if(SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*S*O-----*****"))
          returnVal = -1;
      else
      {
          if(SymbolID.charAt(3)=='A' || SymbolID.charAt(3)=='a')
                      return fillCode + 2;
                  else
                      return fillCode + 1;
      }
      
      return returnVal;
  }
  
  private static int getSubSurfaceFrame(String SymbolID, int fillCode, int symStd)
  {
      int returnVal = 0;

      returnVal = 831;

      try
      {
          //Special check for sea mine graphics
          //2525C///////////////////////////////////////////////////////////////
          if(RendererSettings.getInstance().getSymbologyStandard() == 
                  RendererSettings.Symbology_2525C)
          {
              if(SymbolID.indexOf("WM")==4 || //Sea Mine
                      SymbolID.indexOf("WDM")==4 ||//Sea Mine Decoy
                      SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*E-----*****") ||
                      SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*V-----*****") ||
                      SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*X-----*****"))
              {
                  returnVal = -1;
              }
              else if(SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*ND----*****"))
              {
                  returnVal = -1;
              }
              else
              {
                  if(SymbolID.charAt(3)=='A' || SymbolID.charAt(3)=='a')
                      return fillCode + 2;
                  else
                      return fillCode + 1;
              }//
          }
          else//2525Bch2////////////////////////////////////////////////////////
          {
              if(SymbolID.indexOf("WM")==4)//Sea Mine
              {
                  returnVal = -1;

              }
              else if(SymbolID.indexOf("WDM")==4)//Sea Mine Decoy
              {
                    returnVal = -1;
              }
              else if(SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*ND----*****"))
              {
                    returnVal = -1;
              }//
              else if(SymbolUtilities.getBasicSymbolIDStrict(SymbolID).equalsIgnoreCase("S*U*X-----*****"))
              {
                  returnVal = -1;
              }
              else
              {
                  if(SymbolID.charAt(3)=='A' || SymbolID.charAt(3)=='a')
                      return fillCode + 2;
                  else
                      return fillCode + 1;
              }
          }
      }
      catch(Exception exc)
      {
          ErrorLogger.LogException("UnitFontLookupC", "getSubSurfaceFrame", exc);
          return fillCode;
      }

      return returnVal;
  }

  /**
   * 
   * @param SymbolID
   * @return 
   * @deprecated 
   */
  public UnitFontLookupInfo getLookupInfo(String SymbolID)
  {
      return getLookupInfo(SymbolID, RendererSettings.getInstance().getSymbologyStandard());
  }
  
  /**
   * 
   * @param SymbolID
   * @param symbologyStandard
   * @return 
   */
  public UnitFontLookupInfo getLookupInfo(String SymbolID, int symbologyStandard)
  {
    try
    {
        String code = SymbolUtilities.getBasicSymbolIDStrict(SymbolID);

        UnitFontLookupInfo data = null;
        if(symbologyStandard == RendererSettings.Symbology_2525B)
        {
            data = hashMapB.get(code);
        }
        else
        {
            data = hashMapC.get(code);
        }
        return data;
    }
    catch(Exception exc)
    {
        ErrorLogger.LogException("UnitFontLookup", "getLookupInfo(" + String.valueOf(SymbolID) +")", exc, Level.WARNING);
        return null;
    }
  }

  /**
   *
   * @param charIndex - Fill Character Index
   * @return
   */
  public static double getUnitRatioHeight(int charIndex)
  {
      int characterIndex = charIndex - 57000;
      if(characterIndex == FillIndexHP ||
              characterIndex == FillIndexHA ||
              characterIndex == FillIndexHU ||
              characterIndex == (FillIndexHU+1) ||
              characterIndex == (FillIndexHU+2) ||
              characterIndex == FillIndexUP ||
              characterIndex == FillIndexUA ||
              characterIndex == FillIndexUU ||
              characterIndex == (FillIndexUU+1) ||
              characterIndex == (FillIndexUU+2))
      {
          return 1.3;
      }
      else if(characterIndex == FillIndexHZ ||
              characterIndex == FillIndexHG ||
              characterIndex == FillIndexHGE ||
              characterIndex == FillIndexHS ||
              characterIndex == FillIndexHF ||
              characterIndex == FillIndexUZ ||
              characterIndex == FillIndexUG ||
              characterIndex == FillIndexUGE ||
              characterIndex == FillIndexUS ||
              characterIndex == FillIndexUF)
      {
          return 1.44;
      }
      else if(characterIndex == FillIndexFGE ||
              characterIndex == FillIndexFP ||
              characterIndex == FillIndexFA ||
              characterIndex == FillIndexFU ||
              characterIndex == (FillIndexFU+1) ||
              characterIndex == (FillIndexFU+2) ||
              characterIndex == FillIndexFZ ||
              characterIndex == FillIndexFS ||
              characterIndex == FillIndexNP ||
              characterIndex == FillIndexNA ||
              characterIndex == FillIndexNU ||
              characterIndex == (FillIndexNU+1) ||
              characterIndex == (FillIndexNU+2))
      {
          return 1.2;
      }
      else if(characterIndex == FillIndexNZ ||
              characterIndex == FillIndexNG ||
              characterIndex == FillIndexNGE ||
              characterIndex == FillIndexNS ||
              characterIndex == FillIndexNF)
      {
          return 1.1;
      }
      else if(characterIndex == FillIndexFG ||
              characterIndex == FillIndexFGE)
      {
          return 1.0;
      }
      else
      {
          return 1.2;
      }
  }

  /**
   *
   * @param charIndex - Fill Character Index
   * @return
   */
  public static double getUnitRatioWidth(int charIndex)
  {
      int characterIndex = charIndex - 57000;
      if(characterIndex == FillIndexUP ||
              characterIndex == FillIndexUA ||
              characterIndex == FillIndexUU ||
              characterIndex == FillIndexUU+1 ||
              characterIndex == FillIndexUU+2 ||
              characterIndex == FillIndexFG ||
              characterIndex == FillIndexFF)
      {
          return 1.5;
      }
      else if(characterIndex == FillIndexHZ ||
              characterIndex == FillIndexHG ||
              characterIndex == FillIndexHGE ||
              characterIndex == FillIndexHS ||
              characterIndex == FillIndexHF ||
              characterIndex == FillIndexUZ ||
              characterIndex == FillIndexUG ||
              characterIndex == FillIndexUGE ||
              characterIndex == FillIndexUS ||
              characterIndex == FillIndexUF)
      {
          return 1.44;
      }
      else if(characterIndex == FillIndexFZ ||
              characterIndex == FillIndexFGE ||
              characterIndex == FillIndexFS)
      {
          return 1.2;
      }
      else
      {
          return 1.1;
      }
  }

  /*
  public static void main(String args[])
  {
    int mapping = UnitFontLookup.instance().getCharCodeFromSymbol("G*FPPTN---****X");
    String junk = "";
  }*/
}
