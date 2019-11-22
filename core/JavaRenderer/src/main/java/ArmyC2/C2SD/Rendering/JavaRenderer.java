/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ArmyC2.C2SD.Rendering;


import ArmyC2.C2SD.Utilities.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author michael.spinelli
 */
public class JavaRenderer implements IJavaRenderer {



    private static JavaRenderer _instance = null;
    private static String _className = "";
    private static SinglePointRenderer _SPR = null;
    private static IMultiPointRenderer _MPR = null;
    private static TacticalGraphicIconRenderer _TGIR = null;


    private SymbolDefTable _SymbolDefTable = null;


    PointConversion _PointConverter = null;
    

    //Unit 2525C sizes
    public static final int UnitSizeMedium = 40;
    public static final int UnitSizeSmall = 30;
    public static final int UnitSizeLarge = 50;
    public static final int UnitSizeXL = 60;

    //TG & unit 2525B sizes
    public static final int SymbolSizeMedium = 80;
    public static final int SymbolSizeSmall = 60;
    public static final int SymbolSizeLarge = 100;
    public static final int SymbolSizeXL = 120;


    /**
     *
     */
    private JavaRenderer()
    {

        try
        {
//            ErrorLogger.LogMessage("TEST");
//            ErrorLogger.LogException("JavaRenderer", "constructor", new Exception("exception!"), Level.WARNING);
//            ErrorLogger.setLevel(Level.ALL);
//            ErrorLogger.Entering(_className, "nit");
            //System.out.println("v0.0.5b-2:36 PM 11/15/2012");
           /*
            ErrorLogger.LogMessage("TEST");
            ErrorLogger.LogMessage("JR " ,"JR()","TEST",false);
            ErrorLogger.LogException(this.getClass().getName() ,"JR()",new RendererException("YAR!", null));
            */

            _SPR = SinglePointRenderer.getInstance();

            _MPR = MultiPointRenderer.getInstance();
            
            _TGIR = TacticalGraphicIconRenderer.getInstance();

            _className = this.getClass().getName();


            if(_SPR == null)
            {
                //ErrorLogger.LogException(this.getClass().getName() ,"JavaRenderer()",
                //    new RendererException("JavaRenderer failed to initialize - SinglePointRenderer didn't load.", null));
                System.err.println("JavaRenderer failed to initialize - SinglePointRenderer didn't load.");
            }
            if(_TGIR == null)
            {
                //ErrorLogger.LogException(this.getClass().getName() ,"JavaRenderer()",
                //    new RendererException("JavaRenderer failed to initialize - TacticalGraphicIconRenderer didn't load.", null));
                System.err.println("JavaRenderer failed to initialize - TacticalGraphicIconRenderer didn't load.");
            }

        }
        catch(Exception exc)
        {
            //ErrorLogger.LogException(_className ,"JavaRenderer()",
            //        new RendererException("JavaRenderer failed to initialize", exc));
            System.err.println("JavaRenderer failed to initialize");
            System.err.println(exc.getMessage());
        }
    }

    /**
     * Instance of the JavaRenderer
     * @return the instance
     */
    public static synchronized JavaRenderer getInstance()
    {
        if(_instance == null)
            _instance = new JavaRenderer();

        return _instance;
    }

    /**
     * Takes a symbol and determines if it is renderable
     * @param symbol
     * @return true if symbol can be rendered based on provided information
     */
    public Boolean CanRender(MilStdSymbol symbol)
    {
       //String basicSymbolID =  symbol.getSymbolID();
       return CanRender(symbol.getSymbolID(), symbol.getCoordinates(),symbol.getSymbologyStandard());
    }
    
    public Boolean CanRender(String symbolCode, 
            ArrayList<Point2D.Double> coords)
    {
        return CanRender(symbolCode, 
                coords,
                RendererSettings.getInstance().getSymbologyStandard());
    }

    /**
     * takes symbol properties and determines if they can be rendered as a symbol
     * @param symbolCode
     * @param coords
     * @param symbologyStandard like RendererSettings.Symbology_2525C
     * @return true if symbol can be rendered based on provided information
     */
    public Boolean CanRender(String symbolCode, 
            ArrayList<Point2D.Double> coords,
            int symStd)
    {
       String message = null;
       String basicSymbolID =  symbolCode;
       basicSymbolID = SymbolUtilities.getBasicSymbolIDStrict(basicSymbolID);
        //ErrorLogger.LogMessage("TEST");
       try
       {
            // message = "Cannot draw: " + symbolCode + " (" + basicSymbolID + ")";
            if(SymbolUtilities.isTacticalGraphic(basicSymbolID))
            {
                if(_SymbolDefTable == null)
                 _SymbolDefTable = SymbolDefTable.getInstance();

                SymbolDef sd =  _SymbolDefTable.getSymbolDef(basicSymbolID,symStd);
                if(sd != null)
                {
                    int pointCount = 0;
                    if(coords != null)
                    {
                        pointCount = coords.size();
                    }

                    int dc = sd.getDrawCategory();
                    if (dc == SymbolDef.DRAW_CATEGORY_POINT)//make sure we can find the character in the font.
                    {
                        int index = -1;
                        index = SinglePointLookup.getInstance().getCharCodeFromSymbol(symbolCode,symStd);
                        if(index > 0)
                            return true;
                        else
                            message = "Bad font lookup for: " + symbolCode + " (" + basicSymbolID + ")";
                    }
                    else if(dc > 0 && dc < 99)
                    {
                        if(sd.getMinPoints() == sd.getMaxPoints())
                        {    //complex graphic like ambush

                            if(pointCount == sd.getMinPoints())
                                return true;
                            else
                                message = "Specific point count not met for: " + symbolCode + " (" + basicSymbolID + ") - Had: " + String.valueOf(pointCount) + " Needed: " + String.valueOf(sd.getMinPoints());
                        }
                        else if(pointCount >= sd.getMinPoints())
                        {
                             return true;
                        }
                        else if(sd.getDrawCategory() == SymbolDef.DRAW_CATEGORY_POLYGON
                                && pointCount == 2 && sd.getMinPoints() == 3 && sd.getMaxPoints() > 100)
                        {//areas with 2 points are allowable.
                            return true;
                        }
                        else if(pointCount < sd.getMinPoints())
                        {
                             message = symbolCode + " had less than the required number of points. Had: " + String.valueOf(coords.size()) + " Needed: " + String.valueOf(sd.getMinPoints());
                        }
                    }
                    else
                    {
                        message = "Cannot draw: " + symbolCode + " (" + basicSymbolID + ")";
                    }

                }
                else
                {
                    message = "Cannot draw symbolID: " + symbolCode + " (" + basicSymbolID + ")";
                }
            }
            else
            {
                //UnitDef ud =  UnitDefTable.getInstance().getUnitDef(basicSymbolID,symStd);
                UnitFontLookupInfo ufli = UnitFontLookup.getInstance().getLookupInfo(basicSymbolID,symStd);
                if(ufli != null)
                {
                    return true;
                }
                else
                {
                    message = "JavaRenderer.CanRender() - Cannot draw symbolID: " + symbolCode + " (" + basicSymbolID + ")";
                }
            }

            if(message != null && !message.equals(""))
            {
                ErrorLogger.LogMessage(this.getClass().getName(), "CanRender()", message,Level.FINE);
                //System.err.println(message);
                //System.out.println("");
                //System.out.println("INFO: CanRender - " + message);
                //Exception foo = new Exception("Stack?");
                //foo.printStackTrace();
            }
       }
       catch(Exception exc)
       {
           System.err.println(String.valueOf(message));
           System.err.println(exc.getMessage());
       }
       return false;
    }

    /**
     * Populates the Symbol & Modifier Shape collection of the milstdsymbol
     * @param symbol
     * @param converter does point conversion between pixels & lat/lon coordinates.
     * @param clipBounds dimensions of drawing surface.  needed to do clipping.
     * @return drawable symbol populated with shape data
     * @throws RendererException
     */
    public MilStdSymbol Render(MilStdSymbol symbol, IPointConversion converter, Rectangle2D clipBounds)  throws RendererException
    {
        ProcessSymbolGeometry(symbol, converter, clipBounds);
        return symbol;
    }


     /**
     * Populates the Symbol & Modifier Shape collection of the milstdsymbol
     * @param symbols
     * @param converter does point conversion between pixels & lat/lon coordinates.
     * @param clipBounds dimensions of drawing surface.  needed to do clipping.
     * @return drawable symbols populated with shape data
     * @throws TBCRendererException
     */
    public ArrayList<MilStdSymbol> Render(ArrayList<MilStdSymbol> symbols, IPointConversion converter, Rectangle2D clipBounds)  throws RendererException
    {
        ProcessSymbolGeometryBulk(symbols, converter, clipBounds);
        return symbols;
    }



    /**
     * Populates the Symbol & Modifier Shape collection of the milstdsymbol
     * @param symbolCode
     * @param UUID
     * @param coords
     * @param Modifiers
     * @param converter does point conversion between pixels & lat/lon coordinates.
     * @param clipBounds dimensions of drawing surface.  needed to do clipping.
     * @return drawable symbol populated with shape data
     * @throws TBCRendererException
     */
    public MilStdSymbol Render(String symbolCode, String UUID, ArrayList<Point2D.Double> coords, Map<String, String> Modifiers, IPointConversion converter, Rectangle2D clipBounds) throws RendererException
    {
        MilStdSymbol symbol = null;
        //try
        //{
            symbol = new MilStdSymbol(symbolCode, UUID, coords, Modifiers);
            ProcessSymbolGeometry(symbol, converter, clipBounds);
        //}
        //catch(RendererException re)
        //{
        //    throw re;
        //}
        
        return symbol;
    }

    public ImageInfo RenderSinglePointAsImageInfo(String symbolCode, Map<String, String> Modifiers, int unitSize, boolean keepUnitRatio)
    {
        return RenderSinglePointAsImageInfo(symbolCode, Modifiers, unitSize, keepUnitRatio, RendererSettings.getInstance().getSymbologyStandard());
    }
    
    /**
     * @param symbolCode
     * @param Modifiers
     * @param unitSize 35 would make a an image where the core symbol is 35x35.
     * label modifiers and display modifiers may fall outside of that area and
     * final image may be bigger than 35x35.
     * use getSinglePointTGSymbolSize or getUnitSymbolSize. 
     * @param keepUnitRatio Recommend setting to true when drawing on a map.
     * Only applies to force elements (units).  If KeepUnitRatio is set,
     * Symbols will be drawn with respect to each other.  Unknown unit
     * is the all around biggest, neutral unit is the smallest. if size is
     * 35, neutral would be (35/1.5)*1.1=25.7
     * @return ImageInfo, which has the image and all the information needed to 
     * position it properly.
     */
    public ImageInfo RenderSinglePointAsImageInfo(String symbolCode, Map<String, String> Modifiers, int unitSize, boolean keepUnitRatio, int symStd)
    {
        ImageInfo returnVal = null;
        try
        {
            ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
            //fake point.  cpof knows where they want to render so they don't 
            //give us a valid point.
            points.add(new Point2D.Double(0, 0));
            
            IPointConversion ipc = new PointConversionDummy();
            MilStdSymbol symbol = null;
            symbol = new MilStdSymbol(symbolCode, null, points, Modifiers);
            symbol.setUnitSize(unitSize);
            symbol.setKeepUnitRatio(keepUnitRatio);
            symbol.setSymbologyStandard(symStd);
            
            if(Modifiers.containsKey(MilStdAttributes.LineColor))
                symbol.setLineColor(SymbolUtilities.getColorFromHexString(Modifiers.get(MilStdAttributes.LineColor)));
            if(Modifiers.containsKey(MilStdAttributes.FillColor))
                symbol.setFillColor(SymbolUtilities.getColorFromHexString(Modifiers.get(MilStdAttributes.FillColor)));
            if(Modifiers.containsKey(MilStdAttributes.IconColor))
                symbol.setIconColor(SymbolUtilities.getColorFromHexString(Modifiers.get(MilStdAttributes.IconColor)));
            
            ProcessSymbolGeometry(symbol, ipc, null);
            returnVal = symbol.toImageInfo();
        }
        catch(Exception exc)
        {
           // ErrorLogger.LogException("JavaRenderer", "RenderSinglePointAsImageInfo(MilStdSymbol)", exc);
           System.err.println(exc.getMessage());
        }
        return returnVal;
    }
    
    /**
     * Given parameters, generates an ImageInfo object.  Works for all symbols
     * @param symbolCode
     * @param UUID
     * @param coords
     * @param Modifiers
     * @param converter
     * @param clipBounds
     * @return 
     */
    public ImageInfo RenderMilStdSymbolAsImageInfo(String symbolCode, String UUID, ArrayList<Point2D.Double> coords, Map<String, String> Modifiers, IPointConversion converter, Rectangle2D clipBounds)
    {
        ImageInfo returnVal = null;
        try
        {
            MilStdSymbol symbol = new MilStdSymbol(symbolCode, UUID, coords, Modifiers);
            ProcessSymbolGeometry(symbol, converter, clipBounds);
            returnVal = symbol.toImageInfo();
        }
        catch(Exception exc)
        {
            //ErrorLogger.LogException("JavaRenderer", "RenderMilStdSymbolAsImageInfo", exc);
            System.err.println(exc.getMessage());
        }
        return returnVal;
    }
    
    /**
     * Generates an imageInfo object 
     * @param symbol
     * @param converter
     * @param clipBounds
     * @return 
     */
    public ImageInfo RenderMilStdSymbolAsImageInfo(MilStdSymbol symbol, IPointConversion converter, Rectangle2D clipBounds)
    {
        ImageInfo returnVal = null;
        try
        {
            ProcessSymbolGeometry(symbol, converter, clipBounds);
            returnVal = symbol.toImageInfo();
        }
        catch(Exception exc)
        {
            //ErrorLogger.LogException("JavaRenderer", "RenderSinglePointAsImageInfo", exc);
            System.err.println(exc.getMessage());
        }
        return returnVal;
    }

    /**
     * Doesn't support multipoints yet.
     * Renders using the default symbology Standard specified here:
     * RendererSettings.getInstance().getSymbologyStandard());
     * @param symbolID
     * @param iconSize
     * @param showDisplayModifiers
     * @return 
     */
    public BufferedImage RenderMilStdSymbolAsIcon(String symbolID, int iconSize, 
            Boolean showDisplayModifiers)
    {
        return RenderMilStdSymbolAsIcon(symbolID, iconSize, showDisplayModifiers, 
                RendererSettings.getInstance().getSymbologyStandard());
    }
    
    /**
     * 
     * @param symbolID
     * @param iconSize
     * @param showDisplayModifiers
     * @param symStd
     * @return 
     */
    public BufferedImage RenderMilStdSymbolAsIcon(String symbolID, int iconSize, 
            Boolean showDisplayModifiers, int symStd)
    {
        BufferedImage returnVal = null;
        try
        {
            ImageInfo ii = null;
            SymbolDef sd = null;
            Map<String, String> Modifiers = new HashMap<String, String>();
            
            ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
            points.add(new Point2D.Double(0, 0));

            MilStdSymbol symbol = null;
            symbol = new MilStdSymbol(symbolID, null, points, Modifiers);
            //symbol.setUnitSize(iconSize);
            //symbol.setKeepUnitRatio(false);
            if(showDisplayModifiers)
                symbol.setDrawAffiliationModifierAsLabel(false);
            
            symbol.setModifier("showdisplaymodifiers", showDisplayModifiers.toString());
            
            if(SymbolUtilities.isTacticalGraphic(symbolID))
            {
                sd = SymbolDefTable.getInstance().getSymbolDef(SymbolUtilities.getBasicSymbolID(symbolID),symStd);
                if(sd!=null && (sd.getDrawCategory() != SymbolDef.DRAW_CATEGORY_POINT))
                {
                    //call TG icon renderer for multipoints
                    ii = _TGIR.getIcon(symbolID,iconSize,null,symbol.getSymbologyStandard());
                }
                else
                {
                    ii = RenderSinglePointAsImageInfo(symbolID, Modifiers,iconSize,false);
                }
            }
            else
            {
                ii = RenderSinglePointAsImageInfo(symbolID, Modifiers,iconSize,false);
            }
            
            if(showDisplayModifiers==true)
                returnVal = ImageInfo.getScaledInstance(ii.getImage(), iconSize, iconSize, RenderingHints.VALUE_INTERPOLATION_BILINEAR, false,true);
            else
                returnVal = ii.getImage();
            
            //redraw to fit size.  at 35x35, action point may turn out like
            //15x35.  So redraw in the middle of a blank 35x35 image.
            int type = (returnVal.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
           
            int w = iconSize;
            int h = iconSize;
            int x = 0;
            int y = 0;
            
            BufferedImage tmp = new BufferedImage(w, h, type);
            
            int ow = returnVal.getWidth();//original width
            int oh = returnVal.getHeight();//original height
            if(ow < w)
            {
                x=(w-ow)/2;
                w = w-x;
            }
            if(oh < h)
            {
                y=(h-oh)/2;
                h = h-y;
            }
            
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(returnVal, x, y, w, h, null);
            g2.dispose();

            returnVal = tmp;
        }
        catch(Exception exc)
        {
            //ErrorLogger.LogException("JavaRenderer", "RenderMilStdSymbolAsIcon", exc);
            System.err.println(exc.getMessage());
        }
        return returnVal;
    }   
    

    /**
     * Does the actual drawing of the Symbol.  MilstdSymbol need to be properly populated
     * via the Render call first. Draws to an offscreen image that blits the result
     * to the desination Graphics2D object.
     * @param symbol
     * @param destination surface to draw to
     * @param clip Cannot be null. This function does not apply it to the destination object.
     * Clip dimesions are used to determine the size of the back buffer.  Also useful for making
     * sure only an specfic area is being redrawn.  It shouldn't be bigger than the drawing area,
     * but it can be a section of the drawing area. like if the draw area is 400x400 and the clip is
     * x200,y200,w200,h200; the bottom right quadrant is the only part that will be drawn and the back buffer
     * will only be 200x200.  Or you can simply have the dimensions of the clip match the dimensions
     * of the draw area.
     * @throws RendererException
     */
    public void DrawDB(MilStdSymbol symbol, Graphics2D destination, Rectangle clip) throws RendererException
    {
        ArrayList<MilStdSymbol> symbols = new ArrayList<MilStdSymbol>();
        symbols.add(symbol);
        DrawDB(symbols, destination, clip);
    }

    /**
     * Does the actual drawing of the Symbol.  MilstdSymbol need to be properly populated
     * via the Render call first. Draws to an offscreen image that blits the result
     * to the destination Graphics2D object.
     * @param symbols
     * @param destination surface to draw to
     * @param clip Cannot be null. This function does not apply it to the destination object.
     * Clip dimensions are used to determine the size of the back buffer.  Also useful for making
     * sure only a specific area is being redrawn.  It shouldn't be bigger than the drawing area,
     * but it can be a section of the drawing area. like if the draw area is 400x400 and the clip is
     * x200,y200,w200,h200; the bottom right quadrant is the only part that will be drawn and the back buffer
     * will only be 200x200.  Or you can simply have the dimensions of the clip match the dimensions
     * of the draw area.
     * @throws RendererException
     */
    public void DrawDB(ArrayList<MilStdSymbol> symbols, Graphics2D destination, Rectangle clip) throws RendererException
    {
        try
        {

            if(symbols != null && destination != null && clip != null && clip.width > 0 && clip.height > 0)
            {
                //make buffer image
                BufferedImage buffer = new BufferedImage(clip.width, clip.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = (Graphics2D)buffer.createGraphics();
                //graphics.setClip(0, 0, clip.width, clip.height);
                Draw(symbols, graphics,-clip.x,-clip.y);

                //draw offscreen image to the screen
                //synchronized(destination)
                //{
                    destination.drawImage(buffer, clip.x, clip.y, null);
                //}

                graphics.dispose();
                graphics = null;
                buffer.flush();
                buffer = null;

            }
            else
            {
                //parameters are bad, throw exception
                String badValues = "Bad parameters passed: ";
                if(symbols == null)
                    badValues += " symbols";
                if(destination == null)
                    badValues += " destination";
                if(clip == null)
                {
                    badValues += " clip";
                }
                else
                {
                    if(clip.width < 1)
                        badValues += " clip.width";
                    if(clip.height < 1)
                        badValues += " clip.height";
                }

                RendererException re = new RendererException(badValues, null);
                ErrorLogger.LogException(this.getClass().getName() ,"DrawDB()",re);
                throw re;
            }


        }
        catch(Exception exc)
        {
            RendererException re2 = new RendererException("Draw Operation Failed", exc);
            //ErrorLogger.LogException(this.getClass().getName() ,"DrawDB()",re2);
            System.err.println(exc.getMessage());
            throw re2;
        }
    }


    /**
     * Does the actual drawing of the Symbol.  MilstdSymbol need to be properly populated
     * via the Render call first.
     * @param symbol
     * @param destination surface to draw to
     * @throws RendererException
     */
    public void Draw(MilStdSymbol symbol, Graphics2D destination) throws RendererException
    {
        ArrayList<MilStdSymbol> symbols = new ArrayList<MilStdSymbol>();
        symbols.add(symbol);
        Draw(symbols, destination, 0, 0);
    }

    /**
     * Does the actual drawing of the Symbol.  MilstdSymbol need to be properly populated
     * via the Render call first.
     * @param symbols
     * @param destination surface to draw to
     * @throws RendererException
     */
    public void Draw(ArrayList<MilStdSymbol> symbols, Graphics2D destination) throws RendererException
    {
        Draw(symbols, destination, 0, 0);
    }

    /**
     * Does the actual drawing of the Symbol.  MilstdSymbol need to be properly populated
     * via the Render call first.
     * @param units
     * @param destination surface to draw to
     * @param offsetX usually a negative value.  if your clip.X is 40, offsetX should be -40
     * @param offsetY usually a negative value.  if your clip.Y is 40, offsetY should be -40
     */
    private void Draw(ArrayList<MilStdSymbol> symbols, Graphics2D destination, int offsetX, int offsetY) throws RendererException
    {
        try
        {

            if(symbols != null && destination != null)
            {

                SymbolDraw.Draw(symbols, destination, offsetX, offsetY);

            }
            else
            {
                //parameters are bad, throw exception
                String badValues = "Bad parameters passed: ";
                if(symbols == null)
                    badValues += " symbols";
                if(destination == null)
                    badValues += " destination";

                RendererException re = new RendererException(badValues, null);
                //ErrorLogger.LogException(this.getClass().getName() ,"Draw()",re);
                System.err.println(re.getMessage());
                throw re;

            }

        }
        catch(Exception exc)
        {
            RendererException re2 = new RendererException("Draw Operation Failed", exc);
            ErrorLogger.LogException(this.getClass().getName() ,"Draw()",re2);
            throw re2;
        }
    }
    
    /**
     * Get a Map of the supported Unit or Force Element symbols
     * @return a Map of UnitDefs keyed by symbol code.
     */
    public Map<String, UnitDef> getSupportedFETypes(int symStd)
    {
        return UnitDefTable.getInstance().GetAllUnitDefs(symStd);
    }

    /**
     * Get a Map of the supported Tactical Graphic symbols.
     * @return a Map of SymbolDefs keyed by symbol code.
     */
    public Map<String, SymbolDef> getSupportedTGTypes(int symStd)
    {

        Map<String, SymbolDef> types;

        Map<String,SymbolDef> defs = SymbolDefTable.getInstance().GetAllSymbolDefs(symStd);

        types = new HashMap<String, SymbolDef>();


        Collection<SymbolDef> symbols = defs.values();
        Iterator<SymbolDef> itr = symbols.iterator();
        SymbolDef item;

        while(itr.hasNext())
        {
            item = itr.next();
            if(!SymbolUtilities.isMCSSpecificTacticalGraphic(item))
                types.put(item.getBasicSymbolId(),item);
        }
        return types;
    }


    /**
     *
     * @param arg0
     * @deprecated
     */
    public void initialize(Map<String, String> arg0) {
            // TODO Auto-generated method stub
            //JOptionPane.showMessageDialog(null, "Oh yeah!!!", "Initializing", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Set minimum level at which an item can be logged.
     * In descending order:
     * Severe
     * Warning
     * Info
     * Config
     * Fine
     * Finer
     * Finest
     * @param newLevel
     */
    public static void setLoggingLevel(Level newLevel)
    {
        ErrorLogger.setLevel(newLevel);
    }

    public int getSinglePointTGSymbolSize()
    {
        return _SPR.getSinglePointTGSymbolSize();
    }

    public int getUnitSymbolSize()
    {
       return _SPR.getUnitSymbolSize();
    }

    public void setSinglePointTGSymbolSize(int size) 
    {
        _SPR.setSinglePointTGSymbolSize(size);
    }

    public void setUnitSymbolSize(int size) 
    {
        _SPR.setUnitSymbolSize(size);
    }

    /**
     * Set the label font to be used in the renderer
     * Default tracking to TextAttribute.TRACKING_LOOSE
     * and kerning to off.
     * @param name like "arial"
     * @param type like Font.BOLD
     * @param size like 12
     */
    public void setModifierFont(String name, int type, int size) 
    {
        RendererSettings.getInstance().setLabelFont(name, type, size);
        _SPR.RefreshModifierFont();
    }
    
    /**
     * Set the label font to be used in the renderer
     * @param name like "arial"
     * @param type like Font.BOLD
     * @param size like 12
     * @param tracking like TextAttribute.TRACKING_LOOSE (0.04f)
     * @param kerning default false.
     */
    public void setModifierFont(String name, int type, int size, float tracking, Boolean kerning) 
    {
        RendererSettings.getInstance().setLabelFont(name, type, size, kerning, tracking);
        _SPR.RefreshModifierFont();
    }


    /**
     * Populates the Symbol with the shapes necessary to render.
     * @param symbol
     * @param converter does point conversion between pixels & lat/lon coordinates.
     * @param clipBounds dimensions of drawing surface.  needed to do clipping.
     */
    private void ProcessSymbolGeometry(MilStdSymbol symbol, IPointConversion converter, Rectangle2D clipBounds) throws RendererException
    {
        ArrayList<MilStdSymbol> symbols = new ArrayList<MilStdSymbol>();
        symbols.add(symbol);
        ProcessSymbolGeometryBulk(symbols, converter, clipBounds);

    }

    /**
     * Processes multiple MilStdSymbols.
     * Sets the Modifier Shapes and Symbol Shapes ArrayLists on the MilStdSymbol.
     * @param symbols ArrayList of type MilStdSymbol - symbols to get their shapes
     * @param converter does point conversion between pixels & lat/lon coordinates.
     * @param clipBounds dimensions of drawing surface.  needed to do clipping.
     * No clipping will be done if value is NULL
     */
    private void ProcessSymbolGeometryBulk(ArrayList<MilStdSymbol> symbols, IPointConversion converter, Rectangle2D clipBounds) throws RendererException
    {
        try
        {

            String basicSymbolID = "";
            int count = symbols.size();
            String message = null;
            String symbolID = null;
            
            SymbolDef symbolDef = null;
            if(_SymbolDefTable == null)
                _SymbolDefTable = SymbolDefTable.getInstance();


            MilStdSymbol symbol;
            for(int lcv = 0; lcv < count; lcv++)
            {
                symbol = symbols.get(lcv);
                int pointCount = 0;
                if(symbol.getCoordinates()!=null)
                {
                    pointCount=symbol.getCoordinates().size();
                }
                
                symbolID = symbol.getSymbolID();
                if(symbol.getSymbologyStandard()==2 && symbol.getSymbolID().startsWith("10") && symbol.getSymbolID().substring(4, 6).equals("25") && pointCount > 1)
                {//2525D multipoint
                    //send to multipointRendering
                    _MPR.render(symbol, converter, clipBounds);
                    //ProcessTGSymbol(symbol, converter,clipBounds);
                }
                else if(SymbolUtilities.isTacticalGraphic(symbolID)==true || pointCount>1)
                {
                     basicSymbolID = SymbolUtilities.getBasicSymbolID(symbol.getSymbolID());
                     symbolDef = _SymbolDefTable.getSymbolDef(basicSymbolID,symbol.getSymbologyStandard());
                     
                     if(symbolDef == null)
                     {//if bad symbol code, replace with action point or boundary
                         if(symbol.getCoordinates().size() <= 1)
                         {
                            if(symbol.getModifier(ModifiersTG.H_ADDITIONAL_INFO_1)!=null)
                                symbol.setModifier(ModifiersTG.H1_ADDITIONAL_INFO_2,symbol.getModifier(ModifiersTG.H_ADDITIONAL_INFO_1));
                            symbol.setModifier(ModifiersTG.H_ADDITIONAL_INFO_1,symbolID.substring(0, 10));

                            symbol.setSymbolID("G" + SymbolUtilities.getAffiliation(symbolID) + 
                                                    "G" + SymbolUtilities.getStatus(symbolID) + "GPP---****X");
                            symbol.setLineColor(SymbolUtilities.getLineColorOfAffiliation(symbolID));
                            symbol.setFillColor(SymbolUtilities.getFillColorOfAffiliation(symbolID));
                         }
                         else
                         {
                             symbol.setSymbolID("G" + SymbolUtilities.getAffiliation(symbolID) + 
                                                    "G" + SymbolUtilities.getStatus(symbolID) + "GLB---****X");
                             symbol.setLineColor(SymbolUtilities.getLineColorOfAffiliation(symbolID));
                             symbol.setFillColor(null);
                         }
                         basicSymbolID = SymbolUtilities.getBasicSymbolID(symbol.getSymbolID());
                         symbolDef = _SymbolDefTable.getSymbolDef(basicSymbolID,symbol.getSymbologyStandard());
                     }
                     
                     if(symbolDef != null)
                     {
                         if(symbolDef.getDrawCategory() == SymbolDef.DRAW_CATEGORY_POINT)
                         {
                            _SPR.ProcessSPSymbol(symbol, converter);
                         }
                         else
                         {
                             //send to multipointRendering
                             _MPR.render(symbol, converter, clipBounds);
                             //ProcessTGSymbol(symbol, converter,clipBounds);
                         }
                     }
                     else
                     {
                         message = "Cannot draw: " + symbolID + " (" + basicSymbolID + "), lookup failed.";
                         throw new RendererException(message);
                     }
                }
                else// if(SymbolUtilities.isWarfighting(symbol.getSymbolID()))
                {
                    //Pass to Unit rendering
                    _SPR.ProcessUnitSymbol(symbol,converter);
                }
            }
        }
        catch(Exception exc)
        {
            throw new RendererException(exc.getMessage(), exc);
        }
    }


         /**
     * given a MilStdSymbol, creates the shapes for the symbol
     * and the shapes for the modifiers (not yet on the modifiers);
     * @param symbol
     * @deprecated
     */
    private void ProcessTGSymbol(MilStdSymbol symbol, IPointConversion converter, Rectangle2D clipBounds)
    {
        try
        {

            //RenderMultipoints.clsRenderer.render(symbol, converter);
            //TGLight tgl = new TGLight();
            
            //sector range fan, make sure there is a minimum distance value.
            if(SymbolUtilities.getBasicSymbolID(symbol.getSymbolID()).equals("G*F*AXS---****X"))
            {
                if(symbol.getModifiers_AM_AN_X(ModifiersTG.AN_AZIMUTH)!=null &&
                symbol.getModifiers_AM_AN_X(ModifiersTG.AM_DISTANCE)!=null)
                {
                    int anCount = symbol.getModifiers_AM_AN_X(ModifiersTG.AN_AZIMUTH).size();
                    int amCount = symbol.getModifiers_AM_AN_X(ModifiersTG.AM_DISTANCE).size();
                    ArrayList<Double> am = null;
                    if(amCount < ((anCount/2) + 1))
                    {
                        am = symbol.getModifiers_AM_AN_X(ModifiersTG.AM_DISTANCE);
                        if(am.get(0)!=0.0)
                        {
                            am.add(0, 0.0);
                        }
                    }
                }
            }

            //call that supports clipping
            

            ArrayList<ShapeInfo> shapes = new ArrayList<ShapeInfo>();
            ArrayList<ShapeInfo> modifiers = new ArrayList<ShapeInfo>();
            RenderMultipoints.clsRenderer.render(symbol, converter, shapes, modifiers, clipBounds);

            if(RendererSettings.getInstance().getTextBackgroundMethod()
                    != RendererSettings.TextBackgroundMethod_NONE)
            {
                modifiers = SymbolDraw.ProcessModifierBackgrounds(modifiers);
                symbol.setModifierShapes(modifiers);
            }

        }
        catch(Exception exc)
        {
            String message = "Failed to build multipoint TG";
            if(symbol != null)
                message = message + ": " + symbol.getSymbolID();
            //ErrorLogger.LogException(this.getClass().getName() ,"ProcessTGSymbol()",
            //        new RendererException(message, exc));
            System.err.println(exc.getMessage());
        }
        catch(Throwable t)
        {
            String message2 = "Failed to build multipoint TG";
            if(symbol != null)
                message2 = message2 + ": " + symbol.getSymbolID();
            //ErrorLogger.LogException(this.getClass().getName() ,"ProcessTGSymbol()",
            //        new RendererException(message2, t));
            System.err.println(t.getMessage());
        }
    }
    
}
