/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArmyC2.C2SD.Rendering;

import ArmyC2.C2SD.Utilities.ErrorLogger;
import ArmyC2.C2SD.Utilities.IPointConversion;
import ArmyC2.C2SD.Utilities.MilStdAttributes;
import ArmyC2.C2SD.Utilities.MilStdSymbol;
import ArmyC2.C2SD.Utilities.ModifiersTG;
import ArmyC2.C2SD.Utilities.ModifiersUnits;
import ArmyC2.C2SD.Utilities.PointConversion;
import ArmyC2.C2SD.Utilities.RendererException;
import ArmyC2.C2SD.Utilities.RendererSettings;
import ArmyC2.C2SD.Utilities.ShapeInfo;
import ArmyC2.C2SD.Utilities.SinglePointFont;
import ArmyC2.C2SD.Utilities.SinglePointLookup;
import ArmyC2.C2SD.Utilities.SymbolDef;
import ArmyC2.C2SD.Utilities.SymbolDefTable;
import ArmyC2.C2SD.Utilities.SymbolDraw;
import ArmyC2.C2SD.Utilities.SymbolUtilities;
import ArmyC2.C2SD.Utilities.UnitFontLookup;
import ArmyC2.C2SD.Utilities.UnitFontLookupInfo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author michael.spinelli
 */
public class SinglePointRenderer {
    
    private static SinglePointRenderer _instance = null;
    
    private static String _className = "SinglePointRenderer";

    private RendererSettings _RendererSettings = null;


    //private SymbolDefTable _SymbolDefTable = null;
    private BufferedImage _buffer = null;
    private FontRenderContext _fontRenderContext = null;


    PointConversion _PointConverter = null;
    //private static ArrayList<String> _ModifierNamesUnit = null;
    private static ArrayList<String> _ModifierNamesTG = null;

    private static Font _SinglePointFont = null;//SinglePointFont.getInstance().getSPFont(100);
    private static Font _UnitFont = null;//SinglePointFont.getInstance().getUnitFont(100);
    private static Font _ModifierFont = null;

    private final Object _SinglePointFontMutex = new Object();
    private final Object _UnitFontMutex = new Object();
    private final Object _ModifierFontMutex = new Object();


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
    
    
    private SinglePointRenderer()
    {
        try
        {
            _RendererSettings = RendererSettings.getInstance();
            _SinglePointFont = SinglePointFont.getInstance().getSPFont(SymbolSizeSmall);
            _UnitFont = SinglePointFont.getInstance().getUnitFont(UnitSizeMedium);
            _ModifierFont = RendererSettings.getInstance().getLabelFont();
            

            _ModifierNamesTG = ModifiersTG.GetModifierList();
            //_ModifierNamesUnit = ModifiersUnits.GetModifierList();
            _className = this.getClass().getName();

            if(_SinglePointFont == null)
            {
                ErrorLogger.LogException(this.getClass().getName() ,"SinglePointRenderer()",
                    new RendererException("SinglePointRenderer failed to initialize - _SinglePointFont didn't load.", null));
            }
            if(_UnitFont == null)
            {
                ErrorLogger.LogException(this.getClass().getName() ,"SinglePointRenderer()",
                    new RendererException("SinglePointRenderer failed to initialize - _UnitFont didn't load.", null));
            }


            //trying to use just 1 image all the time
            //and one FontRenderContext
            if(_buffer == null)
            {
                _buffer = new BufferedImage(8,8,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = (Graphics2D)_buffer.createGraphics();
                _fontRenderContext = g2d.getFontRenderContext();
            }
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className, "SinglePointRenderer", exc);
        }
    }
    
    /**
     * Instance of the JavaRenderer
     * @return the instance
     */
    public static synchronized SinglePointRenderer getInstance()
    {
        if(_instance == null)
            _instance = new SinglePointRenderer();

        return _instance;
    }
    
    
    /**
     * given a MilStdSymbol, creates the shapes for the symbol
     * and the shapes for the modifiers (not yet on the modifiers);
     * @param symbol - MilStdSymbol
     * @param converter
     */
    public void ProcessUnitSymbol(MilStdSymbol symbol, IPointConversion converter)
    {

        try
        {
            FontRenderContext fontRenderContext = _fontRenderContext;
      
            String symbolID = symbol.getSymbolID();

            int renderMethod = RendererSettings.getInstance().getUnitRenderMethod();

            double pixelSize = symbol.getUnitSize();
            //get basic symbol id
            //String strBasicSymbolID = SymbolUtilities.getBasicSymbolID(symbol.getSymbolID());
            
            int symStd = symbol.getSymbologyStandard();

            //2525C/USAS
            //get fill character
            int charFillIndex = UnitFontLookup.getFillCode(symbolID, symStd);
            //get frame character
            int charFrameIndex = UnitFontLookup.getFrameCode(symbolID, charFillIndex,symStd);
            
            int charFrameAssumedIndex = -1;
            if(symStd == RendererSettings.Symbology_2525C)
            {
                char affiliation = symbol.getSymbolID().charAt(1);
                switch(affiliation)
                {
                    case 'P':
                    case 'A':
                    case 'S':
                    case 'G':
                    case 'M':
                        if(symbol.getSymbolID().charAt(2) == 'U' && 
                                (symbolID.substring(4, 6).equals("WM") ||
                                symbolID.substring(4, 7).equals("WDM")))
                        {
                            if(symbol.getSymbolID().charAt(3) != 'A')
                            {
                                charFillIndex++;
                            }
                            charFrameAssumedIndex = charFillIndex - 1;
                            charFrameIndex = -1;
                            
                        }
                        else
                        {
                            charFrameIndex = charFillIndex + 2;
                            charFrameAssumedIndex = charFillIndex + 1;
                        }
                        
                        break;
                }
            }
            int charSymbol1Index = -1;
            int charSymbol2Index = -1;

            //get symbol info
            UnitFontLookupInfo lookup = UnitFontLookup.getInstance().getLookupInfo(symbolID,symStd);
            //if lookup fails, fix code/use unknown symbol code.
            if(lookup==null)
            {
                //if symbolID bad, do best to find a workable code
                lookup = ResolveUnitFontLookupInfo(symbolID,symStd);
                symbol.setFillColor(SymbolUtilities.getFillColorOfAffiliation(symbolID));
                symbol.setLineColor(SymbolUtilities.getLineColorOfAffiliation(symbolID));
            }


            if(lookup != null)
            {
                //get Symbol1 character
                charSymbol1Index = lookup.getMapping1(symbolID);
                //get Symbol2 character
                charSymbol2Index = lookup.getMapping2();
            }
            //set Font & Font size
            //myGraphics.setFont(_UnitFont);
            //get FontRencerContext
            FontRenderContext frc = fontRenderContext;


            //create unit shape
            char[] frameSymbol = {(char)charFrameIndex};
            char[] frameAssumedSymbol = {(char)charFrameAssumedIndex};
            char[] fillSymbol = {(char)charFillIndex};
            char[] symbol1 = new char[1];
            char[] symbol2 = new char[1];
            if(charSymbol1Index > 0)
            {
                symbol1[0] = (char)charSymbol1Index;
            }
            if(charSymbol2Index > 0)
            {
                symbol2[0] = (char)charSymbol2Index;
            }

            //create glyhps
            GlyphVector gvFrame = null;
            GlyphVector gvFrameAssumed = null;
            GlyphVector gvFill = null;
            GlyphVector gvSymbol1 = null;
            GlyphVector gvSymbol2 = null;

            Rectangle symbolBounds = null;

            //gvFrame = CreateUnitGlyphVector(frc, frameSymbol);//_UnitFont.createGlyphVector(frc, frameSymbol);
            //gvFill = CreateUnitGlyphVector(frc, fillSymbol);//_UnitFont.createGlyphVector(frc, fillSymbol);

            double ratio = 0;
            synchronized(_UnitFontMutex)
            {

                if(symbol.getKeepUnitRatio() && pixelSize > 0)
                {//symbols can range frmo 1.0 to 1.5
                    //neutral unit is 1.1 x 1.1
                    //unknown unit is 1.44x1.44
                    //friendly unit is 1.5x1.0
                    double heightRatio = 1.5;
                    double widthRatio = 1.5;

                    heightRatio = UnitFontLookup.getUnitRatioHeight(charFillIndex);
                    widthRatio = UnitFontLookup.getUnitRatioWidth(charFillIndex);
                    
                    if(heightRatio > widthRatio)
                    {
                        pixelSize = (pixelSize / 1.5) * heightRatio;
                    }
                    else
                    {
                        pixelSize = (pixelSize / 1.5) * widthRatio;
                    }
                }

                if(charFrameAssumedIndex > 0)
                    gvFrameAssumed = _UnitFont.createGlyphVector(frc, frameAssumedSymbol);
                
                if(charFrameIndex > 0)
                    gvFrame = _UnitFont.createGlyphVector(frc, frameSymbol);

                if(charFillIndex > 0)
                    gvFill = _UnitFont.createGlyphVector(frc, fillSymbol);

                if(charSymbol1Index > 0)
                    gvSymbol1 = _UnitFont.createGlyphVector(frc, symbol1);
                if(charSymbol2Index > 0)
                    gvSymbol2 = _UnitFont.createGlyphVector(frc, symbol2);


                //If we're not drawing the frame or fill and keepUnitRatio is false, we want the core symbol to take all the space.
                if(symbol.getKeepUnitRatio()==false && gvSymbol1 != null && symbol.getIconColor() != null && symbol.getLineColor().getAlpha() == 0 && symbol.getFillColor().getAlpha() == 0)
                {
                    gvFill = null;
                    gvFrameAssumed = null;
                    gvFrame = null;
                }

                
                //check size ratio
                if(pixelSize > 0 && (charFrameIndex > 0 || charFillIndex > 0))
                {
                    Rectangle foo = null;
                    if(gvFrame != null)
                        foo = gvFrame.getPixelBounds(frc, 0, 0);
                    else if(gvFill != null)
                        foo = gvFill.getPixelBounds(frc, 0, 0);
                    else if(gvSymbol1 != null)
                        foo = gvSymbol1.getPixelBounds(frc, 0, 0);
                    else if(gvSymbol2 != null)
                        foo = gvSymbol2.getPixelBounds(frc, 0, 0);

                    //adjust size
                    //foo = gvTemp.getPixelBounds(frc, 0, 0);
                    ratio = Math.min((pixelSize / foo.getHeight()), (pixelSize / foo.getWidth()));

                }
            }
            
            //resize to pixels
            if(ratio > 0)
            {
                if(gvFill != null)
                    gvFill.setGlyphTransform(0, AffineTransform.getScaleInstance(ratio, ratio));
                if(gvFrameAssumed != null)
                    gvFrameAssumed.setGlyphTransform(0, AffineTransform.getScaleInstance(ratio, ratio));
                if(gvFrame != null)
                    gvFrame.setGlyphTransform(0, AffineTransform.getScaleInstance(ratio, ratio));
                if(gvSymbol1 != null)
                    gvSymbol1.setGlyphTransform(0, AffineTransform.getScaleInstance(ratio, ratio));
                if(gvSymbol2 != null)
                    gvSymbol2.setGlyphTransform(0, AffineTransform.getScaleInstance(ratio, ratio));
            }


            //convert coords to pixel
            ArrayList<Point2D.Double> coords = symbol.getCoordinates();

            Point2D.Double location = coords.get(0);

            //Point pixel = converter.GeoToPixels(location);
            Point2D pixel = converter.GeoToPixels(location);


            Rectangle2D symbolBounds2D = null;

            if(gvFrame != null)
                symbolBounds2D = gvFrame.getPixelBounds(frc, (int)pixel.getX(), (int)pixel.getY());
            else if(gvFill != null)
                symbolBounds2D = gvFill.getPixelBounds(frc, (int)pixel.getX(), (int)pixel.getY());
            else if(gvSymbol1 != null)
                symbolBounds2D = gvSymbol1.getPixelBounds(frc, (int)pixel.getX(), (int)pixel.getY());
            else if(gvSymbol2 != null)
                symbolBounds2D = gvSymbol2.getPixelBounds(frc, (int)pixel.getX(), (int)pixel.getY());
            else
            {
                if(pixelSize > 0)
                {   //ideally doesn't get to this point
                    symbolBounds2D = new Rectangle2D.Double(pixel.getX() - (pixelSize/2), pixel.getY() - (pixelSize/2), pixelSize, pixelSize);
                }
            }
            
            //Get symbol dimensions
//            if(gvFrame != null)
//            {
//                System.out.println("Pixel Bounds: " + gvFrame.getPixelBounds(frc, pixel.x, pixel.y).toString());
//                System.out.println("Visual Bounds: " + gvFrame.getVisualBounds().toString());
//            }

            //if symbol is HQ, we must move symbol and draw the staff
            if(SymbolUtilities.isHQ(symbol.getSymbolID()) && 
                    RendererSettings.getInstance().getCenterOnHQStaff()==true)
            {
                String affiliation = symbol.getSymbolID().substring(1, 2);
                if(affiliation.equals("F") ||
                        affiliation.equals("A") ||
                        affiliation.equals("D") ||
                        affiliation.equals("M") ||
                        affiliation.equals("J") ||
                        affiliation.equals("K") ||
                        affiliation.equals("N") ||
                        affiliation.equals("L"))
                {
                    double x = pixel.getX() + (int)(symbolBounds2D.getWidth() / 2.0);
                    double y = pixel.getY() - (int)(symbolBounds2D.getHeight() * 1.5);
                    pixel.setLocation(x, y);
                }
                else
                {
                    double x = pixel.getX() + (int)(symbolBounds2D.getWidth() / 2.0);
                    double y = pixel.getY() - (int)(symbolBounds2D.getHeight());
                    pixel.setLocation(x, y);
                }

            }

            
            //get shapes based on coordinates
            Shape shapeFrameAssumed = null;
            Shape shapeFrame = null;
            Shape shapeFill = null;
            Shape shapeSymbol1 = null;
            Shape shapeSymbol2 = null;
            if(renderMethod == RendererSettings.RenderMethod_SHAPES)
            {
                if(gvFrameAssumed != null)
                    shapeFrameAssumed = gvFrameAssumed.getGlyphOutline(0, (int)pixel.getX(), (int)pixel.getY());
                if(gvFrame != null)
                    shapeFrame = gvFrame.getGlyphOutline(0, (int)pixel.getX(), (int)pixel.getY());
                if(gvFill != null)
                shapeFill = gvFill.getGlyphOutline(0, (int)pixel.getX(), (int)pixel.getY());
                if(gvSymbol1 != null)
                    shapeSymbol1 = gvSymbol1.getGlyphOutline(0, (int)pixel.getX(), (int)pixel.getY());
                if(gvSymbol2 != null)
                    shapeSymbol2 = gvSymbol2.getGlyphOutline(0, (int)pixel.getX()  , (int)pixel.getY());
            }


            //Build ShapeInfo Objects
            ShapeInfo siFrameAssumed = null;
            ShapeInfo siFrame = null;
            ShapeInfo siFill = null;
            ShapeInfo siSymbol1 = null;
            ShapeInfo siSymbol2 = null;
            if(renderMethod == RendererSettings.RenderMethod_SHAPES)
            {
                if(shapeFrameAssumed != null)
                    siFrameAssumed = new ShapeInfo(shapeFrameAssumed);
                if(shapeFrame != null)
                    siFrame = new ShapeInfo(shapeFrame);
                if(shapeFill != null)
                    siFill = new ShapeInfo(shapeFill);
                if(shapeSymbol1 != null)
                    siSymbol1 = new ShapeInfo(shapeSymbol1);
                if(shapeSymbol2 != null)
                    siSymbol2 = new ShapeInfo(shapeSymbol2);

                //just so we know where the symbol centers if getImage() called
                siFrame.setGlyphPosition(pixel);

                if(siFrameAssumed != null)
                    siFrameAssumed.setFillColor(Color.WHITE);
                if(siFrame != null)
                    siFrame.setFillColor(symbol.getLineColor());//AffiliationColors.FriendlyUnitLineColor);
                if(siFill != null)
                    siFill.setFillColor(symbol.getFillColor());//AffiliationColors.FriendlyUnitFillColor);
                if(siSymbol1 != null)
                {
                    Color c1 = null;
                    
                    if(symbol.getIconColor() != null)
                        c1 = symbol.getIconColor();
                    else
                        c1 = lookup.getColor1();
                    
                    siSymbol1.setFillColor(new Color(c1.getRed(), c1.getGreen(),
                            c1.getBlue(), symbol.getLineColor().getAlpha()));
                }
                if(siSymbol2 != null)
                {
                    Color c2 = lookup.getColor2();
                    siSymbol2.setFillColor(new Color(c2.getRed(), c2.getGreen(),
                            c2.getBlue(), symbol.getLineColor().getAlpha()));
                }
            }
            else if(renderMethod == RendererSettings.RenderMethod_NATIVE)
            {
                if(gvFrameAssumed != null)
                    siFrameAssumed = new ShapeInfo(gvFrameAssumed, pixel);
                if(gvFrame != null)
                    siFrame = new ShapeInfo(gvFrame, pixel);
                if(gvFill != null)
                    siFill = new ShapeInfo(gvFill, pixel);
                if(gvSymbol1 != null)
                    siSymbol1 = new ShapeInfo(gvSymbol1, pixel);
                if(gvSymbol2 != null)
                    siSymbol2 = new ShapeInfo(gvSymbol2, pixel);
                
                

                if(siFrameAssumed != null)
                    siFrameAssumed.setLineColor(Color.WHITE);
                if(siFrame != null)
                    siFrame.setLineColor(symbol.getLineColor());//AffiliationColors.FriendlyUnitLineColor);
                if(siFill != null)
                    siFill.setLineColor(symbol.getFillColor());//AffiliationColors.FriendlyUnitFillColor);


                //Color c1 = lookup.getColor1();
                Color c1 = null;                
                if(siSymbol1 != null)
                {
                    
                    if(symbol.getIconColor() != null)
                    {
                        c1 = symbol.getIconColor();
                        siSymbol1.setLineColor(new Color(c1.getRed(), c1.getGreen(), 
                            c1.getBlue(), c1.getAlpha()));
                    }
                    else
                    {
                        c1 = lookup.getColor1();
                        siSymbol1.setLineColor(new Color(c1.getRed(), c1.getGreen(), 
                            c1.getBlue(), symbol.getLineColor().getAlpha()));
                    }
                    
                    
                }
                if(siSymbol2 != null)
                {
                    Color c2 = lookup.getColor2();
                    if(c1 == null)
                    {
                        siSymbol2.setLineColor(new Color(c2.getRed(), c2.getGreen(), 
                            c2.getBlue(), symbol.getLineColor().getAlpha()));
                    }
                    else
                    {
                        siSymbol2.setLineColor(new Color(c2.getRed(), c2.getGreen(), 
                            c2.getBlue(), c1.getAlpha()));
                    }
                }
                
                //Just for sea mines
                if(symbol.getSymbolID().charAt(2) == 'U' &&
                                symbol.getSymbolID().substring(4, 6).equals("WM"))
                {
                    if(symStd == RendererSettings.Symbology_2525B)
                    {
                        siFill.setLineColor(symbol.getFillColor());
                        siSymbol1.setLineColor(symbol.getLineColor());
                    }
                    else if(symStd == RendererSettings.Symbology_2525C)
                    {
                        //siFill.setLineColor(symbol.getLineColor());
                    }
                    
                }
                else if(symbol.getSymbolID().charAt(2) == 'S' &&
                    symbol.getSymbolID().charAt(4) == 'O')//own track, //SUSPO
                {
                    siFill.setLineColor(symbol.getFillColor());
                }
            }

            if(siFill != null)
                siFill.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_FILL);
            if(siFrameAssumed != null)
                siFrameAssumed.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_FRAME);
            if(siFrame != null)
                siFrame.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_FRAME);
            if(siSymbol1 != null)
                siSymbol1.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_SYMBOL1);
            if(siSymbol2 != null)
                siSymbol2.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_SYMBOL2);



            ArrayList<ShapeInfo> shapes = new ArrayList<ShapeInfo>();

            //add shapes to the collection
            if(siFrameAssumed != null && charFrameIndex == -1)
                shapes.add(siFrameAssumed);
            if(siFill != null)
                shapes.add(siFill);
            if(siFrameAssumed != null && charFrameIndex > 0)
                shapes.add(siFrameAssumed);
            if(siFrame != null)
                shapes.add(siFrame);
            //color part needs to be drawn first if exists so we swap order.
            if(siSymbol2 != null)
                shapes.add(siSymbol2);
            if(siSymbol1 != null)
                shapes.add(siSymbol1);
            
            if(symbol.getOutlineEnabled() && siFrame != null)
            {
                ArrayList<ShapeInfo> symbolOutlines = 
                SymbolDraw.createSinglePointOutline(siFrame, symbol.getOutlineWidth(), symbol.getOutlineColor());
                
                shapes.addAll(0, symbolOutlines);
            }

            //set the symbol shapes
            symbol.setSymbolShapes(shapes);

            //get current symbol bounds
            if(gvFrame != null)
                symbolBounds = gvFrame.getPixelBounds(frc, (int)pixel.getX(), (int)pixel.getY());
            else if(gvFill != null)
                symbolBounds = gvFill.getPixelBounds(frc, (int)pixel.getX(), (int)pixel.getY());
            else if(gvSymbol1 != null)
                symbolBounds = gvSymbol1.getPixelBounds(frc, (int)pixel.getX(), (int)pixel.getY());
            else if(gvSymbol2 != null)
                symbolBounds = gvSymbol2.getPixelBounds(frc, (int)pixel.getX(), (int)pixel.getY());

            //check if we're drawing below items////////////////////////////////
            String drawModifiers = symbol.getModifier("showdisplaymodifiers");
            if(drawModifiers != null && drawModifiers.equalsIgnoreCase("false"))
                return;
            
            //grow symbol bounds for outline if present.////////
            if(symbol.getOutlineEnabled())
                symbolBounds.grow(symbol.getOutlineWidth(), symbol.getOutlineWidth());

            //Process display modifiers///////////////////////
            ProcessUnitDisplayModifiers(symbol.getSymbolID(), shapes, symbolBounds, pixelSize,symStd, symbol.getTextColor(), symbol.getTextBackgroundColor());

            //Process unit affiliation modifier
            if(symbol.getDrawAffiliationModifierAsLabel()==false)
            {
                ProcessUnitAffiliationModifiers(symbol.getSymbolID(), shapes, symbolBounds,symStd, symbol.getTextColor(), symbol.getTextBackgroundColor());
            }
            else
            {
                String affiliationModifier = SymbolUtilities.getUnitAffiliationModifier(symbolID, symStd);
                if(affiliationModifier != null)
                {   //Set affiliation modifier
                    symbol.setModifier(ModifiersUnits.E_FRAME_SHAPE_MODIFIER, affiliationModifier);
                }
            }
            //Process label modifiers///////////////////////////////////////////    
            Map modifiers = symbol.getModifierMap();
            Boolean hasCC = SymbolUtilities.hasValidCountryCode(symbolID);
            if(hasCC || (modifiers != null && modifiers.size() > 0))
            {
                //draw direction of movement arrow if angle exists
                Object odom = modifiers.get(ModifiersUnits.Q_DIRECTION_OF_MOVEMENT);
                if(odom != null)
                {
                    String dom = String.valueOf(odom);
                    if(dom != null && !dom.equals(""))
                    {
                        if(SymbolUtilities.isNumber(dom))
                        {
                            if(SymbolUtilities.hasDirectionOfMovement(symbol.getSymbolID(),symStd))
                            {
                                double angle = Double.valueOf(dom);

                                ArrayList<ShapeInfo> arrow = CreateDOMArrow(symbol.getSymbolID(), symbolBounds, angle);
                                if(arrow != null)
                                    shapes.addAll(arrow);
                            }

                        }
                    }
                }
                //process modifiers
                //get current symbol bounds
//                symbolBounds.setRect(symbol.getFullSymbolExtent().getX(), symbolBounds.getY(),
//                        symbol.getFullSymbolExtent().getWidth(), symbolBounds.getHeight());
                
                //if there's an echelon, make sure unit affiliation modifier doesn't
                //overlap it.
                Rectangle echelonBounds = null;
                int shapeSize = shapes.size();
                for(int i = 0; i < shapeSize; i++)
                {
                    if(shapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_ECHELON)
                    {
                        echelonBounds = shapes.get(i).getBounds();
                        int textOutlineWidth = RendererSettings.getInstance().getTextOutlineWidth();
                        echelonBounds.grow(textOutlineWidth, i);
                        i = shapeSize;
                    }
                }

                
                Rectangle affiliationBounds = null;
                for(int j = 0; j < shapeSize; j++)
                {
                    if(shapes.get(j).getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_AFFILIATION_MODIFIER)
                    {
                        affiliationBounds = shapes.get(j).getBounds();
                        j = shapeSize;
                    }
                }


                Rectangle bounds = new Rectangle(symbolBounds);

                
                Color textColor = Color.BLACK;
                Color textBackgroundColor = null;
                if(symbol.getTextColor() != null)
                {
                    textColor = symbol.getTextColor();
                }
                if(symbol.getTextBackgroundColor() != null)
                {
                    textBackgroundColor = symbol.getTextBackgroundColor();
                }
                    

                if(RendererSettings.getInstance().getLabelForegroundColor() != null)
                {
                    textColor = RendererSettings.getInstance().getLabelForegroundColor();
                    //textColor = symbol.getLineColor();
                }
                
                ArrayList<ShapeInfo> msTemp = GetUnitModifierShape(symbol.getSymbolID(), symbol.getModifierMap(), frc, _ModifierFont, bounds, echelonBounds, affiliationBounds, textColor, textBackgroundColor);
                msTemp = SymbolDraw.ProcessModifierBackgrounds(msTemp);
                symbol.setModifierShapes(msTemp);

            }
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className, "ProcessUnitSymbol("+ symbol.getSymbolID()+")" , exc);
        }
    }


    /**
     * Tries to get a valid UnitFontLookupInfo object when the symbolID is
     * poorly formed or there's no match in the lookup.
     * Use this if you get a null return value from:
     * "UnitFontLookupC.getInstance().getLookupInfo(symbolID)"
     * or "CanRender" returns false.
     * @param symbolID
     * @return
     */
    private static UnitFontLookupInfo ResolveUnitFontLookupInfo(String symbolID,int symStd)
    {
        String id = symbolID;
        UnitFontLookupInfo lookup = null;
        String affiliation = "";
        String status = "";
        if(id != null && id.length()>=10)//if lookup fails, fix code/use unknown symbol code.
        {
            StringBuilder sb = new StringBuilder("");
            sb.append(id.charAt(0));

            if(SymbolUtilities.hasValidAffiliation(id)==false)
            {
                sb.append('U');
                affiliation = "U";
            }
            else
            {
                sb.append(id.charAt(1));
                affiliation = id.substring(1, 2);
            }

            if(SymbolUtilities.hasValidBattleDimension(id)==false)
            {
                sb.append('Z');
                sb.replace(0, 1, "S");
            }
            else
                sb.append(id.charAt(2));

            if(SymbolUtilities.hasValidStatus(id)==false)
            {
                sb.append('P');
                status = "P";
            }
            else
            {
                sb.append(id.charAt(3));
                status = id.substring(3, 4);
            }

            sb.append("------");
            if(id.length()>=15)
            {
                sb.append(id.substring(10, 15));
            }
            else
            {
                sb.append("*****");
            }
            id = sb.toString();

            lookup = UnitFontLookup.getInstance().getLookupInfo(id,symStd);
        }
        else if(symbolID==null || symbolID.equals(""))
        {
            lookup = UnitFontLookup.getInstance().getLookupInfo("SUZP------*****",symStd);
        }
        return lookup;
    }



    /**
     * Draw the affiliation modifier if appropriate.
     * Call AFTER ProcessUnitDisplayModifers as they could change where the
     * affiliation modifier goes.
     * @param SymbolID
     * @param shapes
     * @param bounds
     */
    private void ProcessUnitAffiliationModifiers(String SymbolID, ArrayList<ShapeInfo> shapes, Rectangle bounds,int symStd, Color textColor, Color textBackgroundColor)
    {
        int x = 0;
        int y = 0;
        char affiliation = '0';
        String textChar = null;
        ShapeInfo siAffiliation = null;
        Rectangle echelonBounds = null;
        try
        {
            int fontSize = 12;
            int offset = 1;

            double ratio = 3;//chose 3 because I want 12pt font at 35x35 pixels

            fontSize =  (int)Math.round(bounds.getWidth() / ratio);

            //if there's an echelon, make sure unit affiliation modifier doesn't
            //overlap it.
            int shapeSize = shapes.size();
            for(int i = 0; i < shapeSize; i++)
            {
                if(shapes.get(i).getShapeType() == ShapeInfo.SHAPE_TYPE_UNIT_ECHELON)
                {
                    echelonBounds = shapes.get(i).getBounds();
                    i = shapeSize;
                }
            }

            affiliation = SymbolID.charAt(1);
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
                if(SymbolID.charAt(0)=='S' && SymbolID.indexOf("WM")==4)
                {//variuos sea mine exercises
                    if(SymbolID.indexOf("GX")==6 ||
                            SymbolID.indexOf("MX")==6 ||
                            SymbolID.indexOf("FX")==6 ||
                            SymbolID.indexOf("X")==6 ||
                            SymbolID.indexOf("SX")==6)
                        textChar = "X";
                    else
                        textChar=null;
                }
            }

            
            if(textChar==null)
                return;

            TextLayout text = new TextLayout(textChar, new Font("Arial", Font.BOLD, fontSize), _fontRenderContext);
            //Float descent = text.getDescent();
            //Rectangle labelBounds = text.getPixelBounds(null, 0, 0);

            if(echelonBounds != null &&
                    (echelonBounds.width + echelonBounds.getX() > bounds.getX() + bounds.getWidth()))
            {// if echelon, position affiliation modifier so it doesn't overlap.
                x = echelonBounds.x + echelonBounds.width + offset;
                y = bounds.y - offset;
            }
            else
            {
                x = bounds.x + bounds.width + offset;
                y = bounds.y - offset;
            }
            
           
            siAffiliation = SymbolDraw.CreateModifierShapeInfo(text, textChar, x, y, textColor, textBackgroundColor);

            siAffiliation.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_AFFILIATION_MODIFIER);

            if(RendererSettings.getInstance().getTextBackgroundMethod() 
                    != RendererSettings.TextBackgroundMethod_NONE)
            {
                ArrayList<ShapeInfo> affiliationParts = new ArrayList<ShapeInfo>();
                affiliationParts.add(siAffiliation);
                affiliationParts = SymbolDraw.ProcessModifierBackgrounds(affiliationParts);
                shapes.addAll(affiliationParts);            
            }
            else
            {
                shapes.add(siAffiliation);
            }

            

        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("JavaRenderer", "ProcessUnitAffiliationModifiers", exc, Boolean.FALSE);
        }
    }

    /**
     * 
     * @param SymbolID
     * @param shapes
     * @param bounds
     * @param pixelSize
     * @param symStd 
     */
    private void ProcessUnitDisplayModifiers(String SymbolID, ArrayList<ShapeInfo> shapes, Rectangle bounds, double pixelSize,int symStd, Color textColor, Color textBackgroundColor)
    {
       // Path2D displayModifiers = new Path2D.Double();
        try
        {
            ShapeInfo siTemp = null;
            ArrayList<ShapeInfo> siTempArray = null;

            Rectangle2D echelonBounds = null;

            siTempArray = CreateOperationalConditionIndicator(SymbolID, bounds, pixelSize);
            if(siTempArray != null)
                shapes.addAll(siTempArray);

            
            if(!(SymbolID.substring(10, 11).equals("M") || SymbolID.substring(10, 11).equals("N")))
            {
                //Draw Echelon
                if(SymbolUtilities.canUnitHaveModifier(SymbolID, ModifiersUnits.B_ECHELON) && !SymbolUtilities.hasInstallationModifier(SymbolID))
                {
                    String echelon = SymbolID.substring(11, 12);
                    if(!echelon.equals("-") && !echelon.equals("*"))
                    {
                        siTemp = CreateEchelonShapeInfo(SymbolID.substring(11, 12), _fontRenderContext, bounds, textColor, textBackgroundColor);
                        if(siTemp != null)
                        {
                            //
                            ArrayList<ShapeInfo> echelonParts = new ArrayList<ShapeInfo>();
                            echelonParts.add(siTemp);
                            echelonParts = SymbolDraw.ProcessModifierBackgrounds(echelonParts);
                            shapes.addAll(echelonParts);
                            echelonBounds = echelonParts.get(echelonParts.size()-1).getBounds();
//                            //grow for antialiasing
//                            echelonBounds.setRect(echelonBounds.getX()-1, echelonBounds.getY()-1,
//                                    echelonBounds.getWidth()+2, echelonBounds.getHeight()+2);
                            //
                            //shapes.add(siTemp);
                            //echelonBounds = siTemp.getBounds();
                        }
                    }
                }



                if(SymbolUtilities.isTaskForce(SymbolID))
                {
                    //create task force indicator (box)
                    siTemp = CreateTaskForceIndicator(SymbolID, bounds, echelonBounds);
                    if(siTemp != null)
                            shapes.add(siTemp);

                    //task force surround echelon so bounds should be set to
                    //task force bounds so feint dummy doesn't overlap.
                    echelonBounds = siTemp.getBounds();
                }

                if(SymbolUtilities.isFeintDummy(SymbolID) ||
                        SymbolUtilities.isFeintDummyInstallation(SymbolID))
                {
                    //create feint indicator /\
                    siTemp = CreateFeintDummyIndicator(SymbolID, bounds, echelonBounds);
                    if(siTemp != null)
                            shapes.add(siTemp);
                }

                if(SymbolUtilities.hasInstallationModifier(SymbolID))
                {//the actual installation symbols have the modifier
                    //built in.  everything else, we have to draw it.
                    siTemp = CreateInstallationIndicator(SymbolID, bounds);
                    if(siTemp != null)
                    {
                        String affiliation = SymbolID.substring(1, 2);
                        if(affiliation.equals("F") ||
                                affiliation.equals("A") ||
                                affiliation.equals("D") ||
                                affiliation.equals("M") ||
                                affiliation.equals("J") ||
                                affiliation.equals("K") ||
                                affiliation.equals("N") ||
                                affiliation.equals("L"))
                        {
                            shapes.add(siTemp);
                        }
                        else
                        {
                            /*OLD
                            //non-rectangle/square.  Cut the installation modifier
                            //so that it sits on top of the symbol.
                            Area aInstallation = new Area(siTemp.getShape());
                            //subtract fill from modifier so it lines up with the symbol.
                            Area subtract = new Area(shapes.get(0).getShape());
                            aInstallation.subtract(subtract);
                            siTemp.setShape(aInstallation);
                            shapes.add(siTemp);*/

                            //Add to beginning so it is partially obscured by
                            //symbol rather than trying to cut the shape
                            shapes.add(0, siTemp);

                        }
                        
                    }

                }

                //Draw Staff
                if(SymbolUtilities.isHQ(SymbolID))
                {
                    Point2D pt1 = null;
                    Point2D pt2 = null;

                    String affiliation = SymbolID.substring(1, 2);
                    if(affiliation.equals("F") ||
                            affiliation.equals("A") ||
                            affiliation.equals("D") ||
                            affiliation.equals("M") ||
                            affiliation.equals("J") ||
                            affiliation.equals("K") ||
                            affiliation.equals("N") ||
                            affiliation.equals("L"))
                    {
                        pt1 = new Point2D.Double(bounds.getX()+1,
                            bounds.getY() + bounds.getHeight());
                        pt2 = new Point2D.Double(pt1.getX(), pt1.getY() + bounds.getHeight());
                    }
                    else
                    {
                        pt1 = new Point2D.Double(bounds.getX()+1,
                            bounds.getY() + (bounds.getHeight()/2));
                        pt2 = new Point2D.Double(pt1.getX(), pt1.getY() + bounds.getHeight());
                    }
                    
                    
                    Line2D staff = new Line2D.Double(pt1,pt2);
                    siTemp = new ShapeInfo(staff);
                    siTemp.setLineColor(Color.BLACK);
                    siTemp.setStroke(new BasicStroke(2));
                    siTemp.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);
                    shapes.add(siTemp);

                }

            }
            else
            {
                //Draw Mobility
                if(SymbolID.substring(10, 11).equals("M"))
                {
                    //ArrayList<ShapeInfo> alsi =  CreateMobilityShapeInfo(SymbolID,bounds);
                    //shapes.addAll(alsi);
                    siTemp = CreateMobilityShapeInfo(SymbolID,bounds);
                    if(siTemp != null)
                        shapes.add(siTemp);
                }

                //Draw Towed Array Sonar
                if(SymbolID.substring(10, 12).equals("NS") ||
                        SymbolID.substring(10, 12).equals("NL"))
                {
                    ArrayList<ShapeInfo> tas = CreateTowedSonarArrayShapeInfo(SymbolID, bounds);
                    if(tas.isEmpty() == false)
                        shapes.addAll(tas);
                }
            }
            
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className, "ProcessUnitDisplayModifiers()", exc);
        }
        
    }

    private ShapeInfo CreateFeintDummyIndicator(String symbolID, Rectangle2D symbolBounds, Rectangle2D echelonBounds )
    {
        Path2D sFeint = new Path2D.Double();
        ShapeInfo siFeint = null;
        //float dash[] = {5.0f};
        float dash[] = {6.0f, 4.0f};
        //Shape sFeint = null;
        //dashed stroke
        float width = 2.0f;

        try
        {
            if(symbolBounds != null && symbolBounds.getWidth()<20)
            {
                width = 1.0f;
                dash[0] = 5.0f;
                dash[1] = 3.0f;
            }
            
            Stroke stroke = new BasicStroke(width,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);

            Point2D left = new Point2D.Double(symbolBounds.getX(), symbolBounds.getY());

            Point2D top = null;//new Point2D.Double(symbolBounds.getCenterX(), symbolBounds.getY() - (symbolBounds.getHeight() * .75));

            Point2D right = new Point2D.Double(symbolBounds.getX() + symbolBounds.getWidth(), symbolBounds.getY());

            String affiliation = symbolID.substring(1, 2);
                        if(affiliation.equals("F") ||
                                affiliation.equals("A") ||
                                affiliation.equals("D") ||
                                affiliation.equals("M") ||
                                affiliation.equals("J") ||
                                affiliation.equals("K"))
                        {
                            top = new Point2D.Double(symbolBounds.getCenterX(), symbolBounds.getY() - (symbolBounds.getHeight() * .75));
                        }
                        else
                        {
                            top = new Point2D.Double(symbolBounds.getCenterX(), symbolBounds.getY() - (symbolBounds.getHeight() * .54));

                        }

            Line2D leftLine = new Line2D.Double(left, top);
            Line2D rightLine = new Line2D.Double(top, right);
            sFeint.append(leftLine, false);
            sFeint.append(rightLine, false);

            if(echelonBounds != null && sFeint.intersects(echelonBounds))
            {
                //move it above echelon and add some breathing room
                sFeint.transform(AffineTransform.getTranslateInstance(0, -echelonBounds.getHeight()-2));
            }

            siFeint = new ShapeInfo(sFeint);
            siFeint.setLineColor(Color.BLACK);
            siFeint.setStroke(stroke);
            siFeint.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className, "CreateFeintDummyIndicator()", exc);
        }
        return siFeint;
    }

    private ShapeInfo CreateTaskForceIndicator(String symbolID, Rectangle2D symbolBounds, Rectangle2D echelonBounds )
    {
        ShapeInfo siTaskForce = null;
        Stroke stroke = new BasicStroke(2);
        Rectangle2D sTaskForce = null;
        int sideBuffer = 3;

        try
        {
            if(symbolBounds != null && symbolBounds.getWidth() < 20)
                stroke = new BasicStroke(1);

            if(echelonBounds != null && echelonBounds.isEmpty() == false)
            {
                //make box around echelon
                sTaskForce = new Rectangle();
                sTaskForce.setFrame(echelonBounds.getX() - sideBuffer,
                        echelonBounds.getY() - 2,
                        echelonBounds.getWidth() + (sideBuffer*2),
                        symbolBounds.getY() - (echelonBounds.getY() - 2));
                
            }
            else
            {
                double height = 10;
                double width = 30;
                //4th height, 3rd width
                height = symbolBounds.getHeight() / 4;
                width = symbolBounds.getWidth() / 3;

                //make default box
                sTaskForce = new Rectangle();
                sTaskForce.setFrame(symbolBounds.getX() + width,
                        symbolBounds.getY() - height,
                        width,
                        height);
            }

            siTaskForce = new ShapeInfo(sTaskForce);
            siTaskForce.setLineColor(Color.BLACK);
            siTaskForce.setStroke(stroke);
            siTaskForce.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className, "CreateTaskForceIndicator()", exc);
        }
        return siTaskForce;
    }

    /**
     * 
     * @param symbolID
     * @param symbolBounds
     * @return 
     */
    private ShapeInfo CreateInstallationIndicator(String symbolID, Rectangle2D symbolBounds)
    {
        ShapeInfo siInstallation = null;
        Stroke stroke = new BasicStroke(1);
        Rectangle2D sInstallation = null;

        try
        {

            double height = 10;
            double width = 30;
            

            //make default box
            sInstallation = new Rectangle();

            //String affiliation = symbolID.substring(1, 2);
            char affiliation = symbolID.charAt(1);//F,H,N,U,etc...

            //get indicator dimensions
            if(affiliation == 'F' ||
                          affiliation == 'A' ||
                          affiliation == 'D' ||
                          affiliation == 'M' ||
                          affiliation == 'J' ||
                          affiliation == 'K')
            {
                //4th height, 3rd width
                height = symbolBounds.getHeight() / 4;
                width = symbolBounds.getWidth() / 3;
            }
            else if(affiliation == 'H' || affiliation == 'S')//hostile,suspect
            {
                //6th height, 3rd width
                height = symbolBounds.getHeight() / 6;
                width = symbolBounds.getWidth() / 3;  
            }
            else if(affiliation == 'N' || affiliation == 'L')//neutral,exercise neutral
            {
                //6th height, 3rd width
                height = symbolBounds.getHeight() / 6;
                width = symbolBounds.getWidth() / 3;
            }
            else if(affiliation == 'P' ||
                     affiliation == 'U' ||
                     affiliation == 'G' ||
                     affiliation == 'W')
            {
                //6th height, 3rd width
                height = symbolBounds.getHeight() / 6;
                width = symbolBounds.getWidth() / 3;
            }
            else
            {
                //6th height, 3rd width
                height = symbolBounds.getHeight() / 6;
                width = symbolBounds.getWidth() / 3;               
            }

            //set position of indicator
            if(affiliation == 'F' ||
                          affiliation == 'A' ||
                          affiliation == 'D' ||
                          affiliation == 'M' ||
                          affiliation == 'J' ||
                          affiliation == 'K' ||
                          affiliation == 'N' ||
                          affiliation == 'L')
            {
                sInstallation.setFrame(symbolBounds.getX() + width,
                    symbolBounds.getY() - height,
                    width,
                    height);
            }
            else if(affiliation == 'H' || affiliation == 'S')//hostile,suspect
            {
                sInstallation.setFrame(symbolBounds.getX() + width,
                    symbolBounds.getY() - (height * 0.2),
                    width,
                    height);

            }
            else if(affiliation == 'P' ||
                     affiliation == 'U' ||
                     affiliation == 'G' ||
                     affiliation == 'W')
            {
                sInstallation.setFrame(symbolBounds.getX() + width,
                    symbolBounds.getY() - (height * 0.25),
                    width,
                    height);

            }
            else
            {
               sInstallation.setFrame(symbolBounds.getX() + width,
                    symbolBounds.getY() - (height * 0.25),
                    width,
                    height);
            }

            siInstallation = new ShapeInfo(sInstallation);
            siInstallation.setFillColor(Color.BLACK);
            siInstallation.setStroke(stroke);
            siInstallation.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className, "CreateInstallationIndicator()", exc);
        }
        return siInstallation;
    }


    /**
     *
     * @param SymbolID
     * @param symbolBounds
     * @param pixelSize
     * @return
     */
    private ArrayList<ShapeInfo> CreateOperationalConditionIndicator(String SymbolID, Rectangle2D symbolBounds, double pixelSize)
    {
        ArrayList<ShapeInfo> bars = null;
        char status = ' ';
        Color statusColor = Color.GREEN;
        ShapeInfo siOCIF = null;
        ShapeInfo siOCIB = null;
        Stroke stroke = new BasicStroke(1);
        Rectangle2D sOCIForeground = null;
        Rectangle2D sOCIBackground = null;

        //size of black bar outline
        int outlineSize = 1;
        //shrink width bar by this much around
        int widthAdjust = 1;

        try
        {
            status = SymbolID.charAt(3);
            if(RendererSettings.getInstance().getOperationalConditionModifierType() == RendererSettings.OperationalConditionModifierType_BAR)
            {
                //set color
                if(symbolBounds != null && SymbolID.length() > 4)
                {
                    bars = new ArrayList<ShapeInfo>();
                    if(status == 'C')//Fully Capable
                        statusColor = Color.GREEN;
                    else if(status == 'D')//Damage
                        statusColor = Color.YELLOW;
                    else if(status == 'X')
                        statusColor = Color.RED;
                    else if(status == 'F')//full to capacity(hospital)
                        statusColor = Color.BLUE;
                    else
                        return null; //no valid color, return null
                }
                else
                    return null;

                bars = new ArrayList<ShapeInfo>();
                double barSize = 0;
                if(pixelSize > 0)
                    barSize = pixelSize/5;

                if(barSize < 2)
                    barSize = 2;

                sOCIForeground = new Rectangle2D.Double(symbolBounds.getX()+widthAdjust, symbolBounds.getY() + symbolBounds.getHeight() + 2, symbolBounds.getWidth()-(widthAdjust*2), barSize);
                sOCIBackground = new Rectangle2D.Double(sOCIForeground.getX()-outlineSize, sOCIForeground.getY() - outlineSize, sOCIForeground.getWidth()+(outlineSize*2), barSize+(outlineSize*2));


                siOCIF = new ShapeInfo(sOCIForeground, ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);
                siOCIF.setFillColor(statusColor);
                siOCIF.setStroke(stroke);
                siOCIB = new ShapeInfo(sOCIBackground, ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);
                siOCIB.setFillColor(Color.BLACK);
                siOCIB.setStroke(stroke);

                bars.add(siOCIB);
                bars.add(siOCIF);
            }
            else //slash
            {
                //create Operational Condition Indicator 
                GeneralPath path = null;
                if (status == 'D' || status == 'X')
                {
                    bars = new ArrayList<ShapeInfo>();
                    path = new GeneralPath();
                    int fillCode = UnitFontLookup.getFillCode(SymbolID,RendererSettings.Symbology_2525C);
                    double widthRatio = UnitFontLookup.getUnitRatioWidth(fillCode);
                    double heightRatio = UnitFontLookup.getUnitRatioHeight(fillCode);

                    double slashHeight = (symbolBounds.getHeight() / heightRatio * 1.47);
                    double slashWidth = (symbolBounds.getWidth()/ widthRatio * 0.85);
                    double centerX = symbolBounds.getCenterX();
                    double centerY = symbolBounds.getCenterY();

                    if(status == 'D')//Damaged /
                    {
                        path.moveTo(centerX - (slashWidth/2),centerY+(slashHeight/2));
                        path.lineTo(centerX + (slashWidth/2),centerY-(slashHeight/2));
                    }
                    else if(status == 'X')//Destroyed X
                    {
                        path.moveTo(centerX - (slashWidth/2),centerY+(slashHeight/2));
                        path.lineTo(centerX + (slashWidth/2),centerY-(slashHeight/2));
                        path.moveTo(centerX - (slashWidth/2),centerY-(slashHeight/2));
                        path.lineTo(centerX + (slashWidth/2),centerY+(slashHeight/2));
                    }
                    ShapeInfo siOCMS = new ShapeInfo(path,ShapeInfo.SHAPE_TYPE_UNIT_OPERATIONAL_CONDITION);
                    siOCMS.setLineColor(Color.BLACK);
                    double size = symbolBounds.getWidth();
                    float ociStrokeWidth = 3f;
                    ociStrokeWidth = (float)size/20f;
                    if(ociStrokeWidth < 1f)
                        ociStrokeWidth = 1f;
                    stroke = new BasicStroke(ociStrokeWidth);
                    siOCMS.setStroke(stroke);
                    if(siOCMS != null)
                        bars.add(siOCMS);
                }
            }
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className, "CreateInstallationIndicator()", exc);
        }
        return bars;
    }

    private ShapeInfo CreateEchelonShapeInfo(String echelon, FontRenderContext frc, Rectangle bounds, Color textColor, Color textBackgroundColor)
    {
        ShapeInfo siEchelon = null;

        int x = 0;
        int y = 0;
        String echelonText;

        int bufferY = 1;

        double ratio = 3.5;//chose 3.5 because I want 10pt font at 35x35 pixels
        //double ratio = 3;//chose 3 because I want 12pt font at 35x35 pixels

        int fontSize = 12;
        
        boolean scaleEchelon = RendererSettings.getInstance().getScaleEchelon();
        
        fontSize = RendererSettings.getInstance().getLabelFontSize();
        
        if(scaleEchelon)
            fontSize = (int)Math.round(bounds.getWidth() / ratio);

        

        try
        {
            if(echelon != null && !echelon.equals(""))
            {

                echelonText = GetEchelonText(echelon);
                if(!echelonText.equals(""))
                {
                    TextLayout text = new TextLayout(echelonText, new Font("Arial", Font.BOLD, fontSize), frc);
                    Float descent = text.getDescent();
                    Rectangle labelBounds = text.getPixelBounds(null, 0, 0);

                    x = bounds.x + (bounds.width/2) - (labelBounds.width / 2);
                    y = bounds.y - descent.intValue();// - bufferY;

                    if(RendererSettings.getInstance().getTextBackgroundMethod() == RendererSettings.TextBackgroundMethod_OUTLINE)
                    {
                        y = y - (RendererSettings.getInstance().getTextOutlineWidth()/2);
                    }
                    else if(RendererSettings.getInstance().getTextBackgroundMethod() == RendererSettings.TextBackgroundMethod_OUTLINE_QUICK)
                    {
                        y = y - RendererSettings.getInstance().getTextOutlineWidth();
                    }
                    else if(RendererSettings.getInstance().getTextBackgroundMethod() == RendererSettings.TextBackgroundMethod_COLORFILL)
                    {
                        y = y - 1;
                    }

                    siEchelon = SymbolDraw.CreateModifierShapeInfo(text, echelonText, x, y, textColor, textBackgroundColor);
                    siEchelon.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_ECHELON);

                }
            }

        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className, "CreateEchelonShapeInfo()", exc);
        }

        return siEchelon;
    }

    private String GetEchelonText(String echelon)
    {
        char[] dots = new char[3];
        dots[0] = (char)8226;
        dots[1] = (char)8226;
        dots[2] = (char)8226;
        String dot = new String(dots);
        String text = "";
        if(echelon.equals("A"))
        {
            text = String.valueOf((char)216);
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
            text = "I ";
        }
        else if(echelon.equals("F"))
        {
            text = "I I  ";
        }
        else if(echelon.equals("G"))
        {
            text = "I I I ";//"| | | ";
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
     * Given mobility type, create the appropriate ShapeInfo object to be added 
     * to the MilStdSymbol's SymbolShapes collection.
     * @param mobility like SymbolID.substring(10, 12) where SymbolID is a
     * 15 character String.
     * @param bounds pixel bounds of the unit
     */
    private ShapeInfo CreateMobilityShapeInfo(String symbolID, Rectangle bounds)
    {
        Path2D shapeMobility = new Path2D.Double();
        ShapeInfo siMobility = null;
        Stroke stroke = new BasicStroke(2);
        String mobility = "";

        double x = 0;
        double y = 0;
        double centerX = 0;
        double bottomY = 0;
        double height = 0;
        double width = 0;
        double middleY = 0;
        double wheelOffset = 1;
        double wheelSize = 5;
        double rrHeight = 5;
        double rrArcWidth = 8;
        if(symbolID != null && !symbolID.equals("") && bounds != null && bounds.isEmpty() == false)
        {
            mobility = symbolID.substring(10, 12);
            x = bounds.getX();
            y = bounds.getY();
            height = bounds.getHeight();
            width = bounds.getWidth()-1;
            bottomY = y+height+1;

            wheelSize = width / 7;
            rrHeight = width / 7;
            //rrArcWidth = width / 7;

            if(mobility.equals("MO"))//mobility wheeled (limited cross country)
            {
                //Line2D line = new Line2D.Double(x,y + height,x + width, y + height);
                //Shape circle = new Ellipse2D.Double(x, y + height + wheelOffset, wheelSize, wheelSize);
                //line
                shapeMobility.append(new Line2D.Double(x,bottomY,x + width, bottomY), false);
                //left circle
                shapeMobility.append(new Ellipse2D.Double(x, bottomY + wheelOffset, wheelSize, wheelSize), false);
                //right circle
                shapeMobility.append(new Ellipse2D.Double(x + width - wheelSize, bottomY + wheelOffset, wheelSize, wheelSize), false);
                
            }
            else if(mobility.equals("MP"))//mobility wheeled (cross country)
            {
                //line
                shapeMobility.append(new Line2D.Double(x,bottomY,x + width, bottomY), false);
                //left circle
                shapeMobility.append(new Ellipse2D.Double(x, bottomY + wheelOffset, wheelSize, wheelSize), false);
                //right circle
                shapeMobility.append(new Ellipse2D.Double(x + width - wheelSize, bottomY + wheelOffset, wheelSize, wheelSize), false);
                //center wheel
                shapeMobility.append(new Ellipse2D.Double(x + (width/2)-(wheelSize/2), bottomY + wheelOffset, wheelSize, wheelSize), false);

            }
            else if(mobility.equals("MQ"))//mobility tracked
            {
                //round rectangle
                shapeMobility.append(new RoundRectangle2D.Double(x, bottomY, width, rrHeight, rrArcWidth, rrHeight),false);
            }
            else if(mobility.equals("MR"))//mobility wheeled and tracked combination
            {
                //round rectangle
                shapeMobility.append(new RoundRectangle2D.Double(x, bottomY, width, rrHeight, rrArcWidth, rrHeight),false);
                //left circle
                shapeMobility.append(new Ellipse2D.Double(x - wheelSize - wheelSize, bottomY, wheelSize, wheelSize), false);

            }
            else if(mobility.equals("MS"))//mobility towed
            {
                //line
                shapeMobility.append(new Line2D.Double(x + wheelSize,bottomY + (wheelSize/2),x + width - wheelSize, bottomY + (wheelSize/2)), false);
                //left circle
                shapeMobility.append(new Ellipse2D.Double(x, bottomY, wheelSize, wheelSize), false);
                //right circle
                shapeMobility.append(new Ellipse2D.Double(x + width - wheelSize, bottomY, wheelSize, wheelSize), false);
            }
            else if(mobility.equals("MT"))//mobility rail
            {
                //line
                shapeMobility.append(new Line2D.Double(x,bottomY,x + width, bottomY), false);
                //left circle
                shapeMobility.append(new Ellipse2D.Double(x + wheelSize, bottomY + wheelOffset, wheelSize, wheelSize), false);
                //left circle2
                shapeMobility.append(new Ellipse2D.Double(x, bottomY + wheelOffset, wheelSize, wheelSize), false);
                //right circle
                shapeMobility.append(new Ellipse2D.Double(x + width - wheelSize, bottomY + wheelOffset, wheelSize, wheelSize), false);
                //right circle2
                shapeMobility.append(new Ellipse2D.Double(x + width - wheelSize - wheelSize, bottomY + wheelOffset, wheelSize, wheelSize), false);
            }
            else if(mobility.equals("MU"))//mobility over the snow
            {
                shapeMobility.moveTo(x, bottomY);
                shapeMobility.lineTo(x + 5, bottomY + 5);
                shapeMobility.lineTo(x + width, bottomY + 5);
            }
            else if(mobility.equals("MV"))//mobility sled
            {
                //Arc2D foo = new Arc2D.Double(x, y + height, 5, 5, 0, 180, Arc2D.OPEN);//x, y + height, 5, 5, 0, 180, ​Arc2D.OPEN);
                //left half circle
                //shapeMobility.append(new Arc2D.Double(x + rrArcWidth, y + height, 5, 5, 90, 180, Arc2D.OPEN),false);
                //right half circle
                //shapeMobility.append(new Arc2D.Double(x + width - rrArcWidth, y + height, 5, 5, 90, -180, Arc2D.OPEN),false);
                //shapeMobility.moveTo(x, y + height);
                //shapeMobility.curveTo(x, y, x, y, x, y);
                //line
                //shapeMobility.append(new Line2D.Double(x + rrArcWidth,y + height + rrHeight,x + width - rrArcWidth, y + height + rrHeight), false);

                shapeMobility.moveTo(x, bottomY);
                shapeMobility.curveTo(x, bottomY, x-rrArcWidth, bottomY+3, x, bottomY+rrHeight);
                shapeMobility.lineTo(x + width, bottomY + rrHeight);
                shapeMobility.curveTo(x + width, bottomY + rrHeight, x+ width + rrArcWidth, bottomY+3, x + width, bottomY);

            }
            else if(mobility.equals("MW"))//mobility pack animals
            {
                centerX = bounds.getCenterX();
                shapeMobility.moveTo(centerX, bottomY + rrHeight+2);
                shapeMobility.lineTo(centerX - 3, bottomY);
                shapeMobility.lineTo(centerX - 6, bottomY + rrHeight+2);
                shapeMobility.moveTo(centerX, bottomY + rrHeight+2);
                shapeMobility.lineTo(centerX + 3, bottomY);
                shapeMobility.lineTo(centerX + 6, bottomY + rrHeight+2);

            }
            else if(mobility.equals("MX"))//mobility barge
            {
                centerX = bounds.getCenterX();
                double quarterX = (centerX - x)/2;
                double quarterY = (((bottomY + rrHeight) - bottomY)/2);
                shapeMobility.moveTo(x+width, bottomY);
                shapeMobility.lineTo(x, bottomY);
                shapeMobility.curveTo(x+quarterX, bottomY+rrHeight, centerX + quarterX, bottomY + rrHeight, x + width, bottomY);
            }
            else if(mobility.equals("MY"))//mobility amphibious
            {
                double incrementX = width / 7;
                middleY = (((bottomY + rrHeight) - bottomY)/2);

                shapeMobility.append(new Arc2D.Double(x, bottomY + middleY, incrementX, rrHeight, 0, 180, Arc2D.OPEN),false);
                shapeMobility.append(new Arc2D.Double(x + incrementX, bottomY + middleY, incrementX, rrHeight, 0, -180, Arc2D.OPEN),false);
                shapeMobility.append(new Arc2D.Double(x + incrementX*2, bottomY + middleY, incrementX, rrHeight, 0, 180, Arc2D.OPEN),false);
                shapeMobility.append(new Arc2D.Double(x + incrementX*3, bottomY + middleY, incrementX, rrHeight, 0, -180, Arc2D.OPEN),false);
                shapeMobility.append(new Arc2D.Double(x + incrementX*4, bottomY + middleY, incrementX, rrHeight, 0, 180, Arc2D.OPEN),false);
                shapeMobility.append(new Arc2D.Double(x + incrementX*5, bottomY + middleY, incrementX, rrHeight, 0, -180, Arc2D.OPEN),false);
                shapeMobility.append(new Arc2D.Double(x + incrementX*6, bottomY + middleY, incrementX, rrHeight, 0, 180, Arc2D.OPEN),false);
                //if not null, reuse
                //AffineTransform.getTranslateInstance(x, y);
            }

            siMobility = new ShapeInfo(shapeMobility);
            siMobility.setLineColor(Color.BLACK);
            siMobility.setStroke(stroke);
            siMobility.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);
        }
        return siMobility;
    }


    private ArrayList<ShapeInfo> CreateTowedSonarArrayShapeInfo(String symbolID, Rectangle bounds)
    {

        ArrayList<ShapeInfo> returnVal = new ArrayList<ShapeInfo>();
        Path2D shapeLines = new Path2D.Double();
        Path2D shapeSquares = new Path2D.Double();
        ShapeInfo siLine = null;
        ShapeInfo siSquares = null;
        Stroke stroke = new BasicStroke(1);
        String mobility = "";

        double x = 0;
        double y = 0;
        double centerX = 0;
        double bottomY = 0;
        double height = 0;
        double width = 0;
        double middleY = 0;
        double wheelOffset = 1;
        double wheelSize = 5;
        double rrHeight = 5;
        double rrArcWidth = 8;
        if(symbolID != null && !symbolID.equals("") && bounds != null && bounds.isEmpty() == false)
        {
            mobility = symbolID.substring(10, 12);
            x = bounds.getX();
            y = bounds.getY();
            height = bounds.getHeight();
            width = bounds.getWidth();
            bottomY = y+height+1;
            if(mobility.equals("NS"))//towed array (short)
            {
                centerX = bounds.getCenterX();
                double squareOffset = wheelSize/2;
                middleY = (((bottomY + rrHeight) - bottomY)/2)+bottomY;
                //line
                shapeLines.append(new Line2D.Double(centerX,bottomY - 2,centerX, bottomY + rrHeight + 1), false);
                //line
                shapeLines.append(new Line2D.Double(x,middleY,x + width, middleY), false);
                //square
                shapeSquares.append(new Rectangle2D.Double(x-squareOffset, bottomY, 5, 5), false);
                //square
                shapeSquares.append(new Rectangle2D.Double(centerX-squareOffset, bottomY, 5, 5), false);
                //square
                shapeSquares.append(new Rectangle2D.Double(x + width - squareOffset, bottomY, 5, 5), false);

            }
            if(mobility.equals("NL"))//towed array (long)
            {
                centerX = bounds.getCenterX();
                double squareOffset = wheelSize/2;
                middleY = (((bottomY + rrHeight) - bottomY)/2)+bottomY;
                double leftX = x+(centerX - x)/2;
                double rightX = centerX + (x + width - centerX)/2;
                //line vertical left
                shapeLines.append(new Line2D.Double(leftX,bottomY - 2,leftX, bottomY + rrHeight + 1), false);
                //line vertical right
                shapeLines.append(new Line2D.Double(rightX,bottomY - 2,rightX, bottomY + rrHeight + 1), false);
                //line horizontal
                shapeLines.append(new Line2D.Double(x,middleY,x + width, middleY), false);
                //square left
                shapeSquares.append(new Rectangle2D.Double(x-squareOffset, bottomY, 5, 5), false);
                //square middle
                shapeSquares.append(new Rectangle2D.Double(centerX-squareOffset, bottomY, 5, 5), false);
                //square right
                shapeSquares.append(new Rectangle2D.Double(x + width - squareOffset, bottomY, 5, 5), false);
                //square middle left
                shapeSquares.append(new Rectangle2D.Double(leftX - squareOffset, bottomY, 5, 5), false);
                //square middle right
                shapeSquares.append(new Rectangle2D.Double(rightX - squareOffset, bottomY, 5, 5), false);
            }
            siLine = new ShapeInfo(shapeLines);
            siLine.setLineColor(Color.BLACK);
            siLine.setStroke(stroke);
            siLine.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);

            siSquares = new ShapeInfo(shapeSquares);
            siSquares.setFillColor(Color.BLACK);
            siSquares.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);

            returnVal.add(siLine);
            returnVal.add(siSquares);

        }
        return returnVal;
    }


     /**
     * given a MilStdSymbol, creates the shapes for the symbol
     * and the shapes for the modifiers (not yet on the modifiers);
     * @param symbol
     * @param converter
     */
    public void ProcessSPSymbol(MilStdSymbol symbol, IPointConversion converter)
    {
        try
        {
            FontRenderContext fontRenderContext = _fontRenderContext;
                    
            int tgRenderMethod = RendererSettings.getInstance().getSymbolRenderMethod();
            
            int symStd = symbol.getSymbologyStandard();

            double pixelSize = symbol.getUnitSize();
            double scale = symbol.getScale();

            //get basic symbol id
            //String strBasicSymbolID = SymbolUtilities.getBasicSymbolID(symbol.getSymbolID());
            //get char index for unit we are drawing
            int charIndex = SinglePointLookup.getInstance().getCharCodeFromSymbol(
                                                                symbol.getSymbolID(),symStd);
            

            int fillIndex = -1;
            String fillID = null;
            if(SymbolUtilities.isTGSPWithFill(symbol.getSymbolID()))
            {
                fillID = SymbolUtilities.getTGFillSymbolCode(symbol.getSymbolID());
                if(fillID != null)
                    fillIndex = SinglePointLookup.getInstance().getCharCodeFromSymbol(fillID,symStd);
            }
            else if(SymbolUtilities.isWeatherSPWithFill(symbol.getSymbolID()))
            {
                fillIndex = charIndex + 1;
                symbol.setFillColor(SymbolUtilities.getFillColorOfWeather(symbol.getSymbolID()));
                
            }//*/
            //if(SymbolUtilities.isNBCWithFill(symbol.getSymbolID()))

            //set Font & Font size
            //myGraphics.setFont(_UnitFont);
            //get FontRencerContext
            FontRenderContext frc = fontRenderContext;


            //create unit shape
            char[] frameSymbol = {(char)charIndex};
            char[] fillSymbol = null;

            //create glyhps
            GlyphVector gvFrame = null;
            GlyphVector gvFill = null;
            //gvFrame = CreateTGSPGlyphVector(frc, frameSymbol);//_SinglePointFont.createGlyphVector(frc, frameSymbol);

            double ratio = 0;
            synchronized(_SinglePointFontMutex)
            {
               double spScale;
               if(pixelSize > 0)
               {
                   Rectangle foo = null;
                   gvFrame = _SinglePointFont.createGlyphVector(frc, frameSymbol);
                   foo = gvFrame.getPixelBounds(frc, 0, 0);
                   if(symbol.getKeepUnitRatio()==true)
                   {
                       //scale it somehow for consistency with units.
                       
                       //when SymbolSizeMedium = 80;
                       //a pixel size of 35 = scale value of 1.0
                       if(scale <=0)
                       {
                           if(_SinglePointFont.getSize2D()==80.0f)//medium
                           {
                            scale = pixelSize / 35.0;
                           }//TODO: need to adjust multiplier for other scales
                           else if(_SinglePointFont.getSize2D()==60.0f)//small
                           {
                            scale = pixelSize / 35.0;
                           }
                           else if(_SinglePointFont.getSize2D()==100.0f)//large
                           {
                            scale = pixelSize / 35.0;
                           }
                           else if(_SinglePointFont.getSize2D()==120.0f)//XL
                           {
                            scale = pixelSize / 35.0;
                           }
                           else
                           {
                               scale = pixelSize / 35.0;
                           }
                       }
                   }
                   
                   //adjust size
                   ratio = Math.min((pixelSize / foo.getHeight()), (pixelSize / foo.getWidth()));
               }
               
               if(gvFrame == null)
                   gvFrame = _SinglePointFont.createGlyphVector(frc, frameSymbol);

               if(fillIndex > 0)
               {
                   fillSymbol = new char[1];
                   fillSymbol[0] = (char)fillIndex;
                   gvFill = _SinglePointFont.createGlyphVector(frc, fillSymbol);
               }
               
               
               
               //scale overrides pixel size.
               if(scale > 0)
               {
                   ratio = scale;
               }
            }

            if(ratio > 0)
            {
                if(gvFill != null)
                    gvFill.setGlyphTransform(0, AffineTransform.getScaleInstance(ratio, ratio));
                if(gvFrame != null)
                    gvFrame.setGlyphTransform(0, AffineTransform.getScaleInstance(ratio, ratio));
            }

            //convert coords to pixel
            ArrayList<Point2D.Double> coords = symbol.getCoordinates();

            Point2D.Double location = coords.get(0);

            //Point pixel = converter.GeoToPixels(location);
            Point2D pixel = converter.GeoToPixels(location);

            //get shapes based on coordinates
            Shape shapeFill = null;
            Shape shapeFrame = null;
            if(tgRenderMethod == RendererSettings.RenderMethod_SHAPES)
            {
                if(gvFill != null)
                    shapeFill = gvFill.getGlyphOutline(0, (int)pixel.getX(), (int)pixel.getY());
                
                shapeFrame = gvFrame.getGlyphOutline(0, (int)pixel.getX(), (int)pixel.getY());
            }
            //ErrorLogger.LogMessage("X: " + String.valueOf(pixel.x) + "Y: " + String.valueOf(pixel.y), Boolean.TRUE);

            ShapeInfo siFill = null;
            ShapeInfo siFrame = null;

            float strokeWidth = 0;

            //WW needs a stroke > 0 to draw eventhough these shapes are just filled.
            //Can potentially delete later when fill supported.
            strokeWidth = 0.5f;

            if(tgRenderMethod == RendererSettings.RenderMethod_SHAPES)
            {
                if(shapeFill != null)// && symbol.getFillColor() != null)
                {
                    siFill = new ShapeInfo(shapeFill);
                    siFill.setFillColor(symbol.getFillColor());
                    siFill.setStroke(new BasicStroke(strokeWidth,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3));
                    siFill.setGlyphPosition(pixel);//need as anchor point for cpof
                }

                siFrame = new ShapeInfo(shapeFrame);

                siFrame.setFillColor(symbol.getLineColor());//AffiliationColors.FriendlyUnitLineColor);
                siFrame.setStroke(new BasicStroke(strokeWidth,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3));
                siFrame.setGlyphPosition(pixel);//need as anchor point for cpof
            }
            else if(tgRenderMethod == RendererSettings.RenderMethod_NATIVE)
            {
                if(gvFill != null && symbol.getFillColor() != null)
                {
                    siFill = new ShapeInfo(gvFill, pixel);
                    siFill.setLineColor(symbol.getFillColor());
                    siFill.setStroke(new BasicStroke(strokeWidth,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3));
                    //test for cpof rendering
                    siFill.setModifierString(String.valueOf(fillSymbol));
                    siFill.setModifierStringPosition(pixel);
                }

                siFrame = new ShapeInfo(gvFrame, pixel);

                siFrame.setLineColor(symbol.getLineColor());//AffiliationColors.FriendlyUnitLineColor);
                siFrame.setStroke(new BasicStroke(strokeWidth,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3));

                //test for cpof rendering
                siFrame.setModifierString(String.valueOf(frameSymbol));
                siFrame.setModifierStringPosition(pixel);
            }

            //deprecated call
            //siFrame.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT);

            ArrayList<ShapeInfo> shapes = new ArrayList<ShapeInfo>();

            if(siFill != null)
            {    siFill.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_FILL);
                shapes.add(siFill);
            }
            siFrame.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_FRAME);
            shapes.add(siFrame);

            if(symbol.getOutlineEnabled())
            {
                ArrayList<ShapeInfo> symbolOutlines = 
                SymbolDraw.createSinglePointOutline(siFrame, symbol.getOutlineWidth(), symbol.getOutlineColor());
                
                if(siFill != null)
                    shapes.addAll(1, symbolOutlines);
                else
                    shapes.addAll(0, symbolOutlines);
                    
            }

            symbol.setSymbolShapes(shapes);


            Map modifiers = symbol.getModifierMap();

            //ErrorLogger.LogMessage(PrintList(modifiers));
            boolean drawAsIcon = false;
            if(modifiers != null && modifiers.containsKey(MilStdAttributes.DrawAsIcon))
            {
                String val = String.valueOf(modifiers.get(MilStdAttributes.DrawAsIcon));
                
                if(val != null & val.equalsIgnoreCase("true"))
                {
                    drawAsIcon = true;
                }
            }
            //TODO:  separate itegral text from modifier process
            if(drawAsIcon==false && ((modifiers != null && modifiers.size() > 0) || SymbolUtilities.isTGSPWithIntegralText(symbol.getSymbolID())))// || SymbolUtilities.isTGSPWithIntegralText(symbol.getSymbolID()))
            {
                //process modifiers
                ArrayList<ShapeInfo> modifiersShapes = ProcessSinglePointModifiers(symbol);
                symbol.setModifierShapes(modifiersShapes);
            }

            //Rotate Symbol
            if(symbol.getRotation() != 0.0)
            {
                RotateShapeInfo(symbol.getSymbolShapes(), symbol.getRotation(), pixel.getX(), pixel.getY());
                RotateShapeInfo(symbol.getModifierShapes(), symbol.getRotation(), pixel.getX(), pixel.getY());
            }

            //draw direction of movement arrow if angle exists
            Object odom = modifiers.get(ModifiersTG.Q_DIRECTION_OF_MOVEMENT);
            if(odom != null)
            {
                String dom = String.valueOf(odom);
                if(dom != null && !dom.equals(""))
                {
                    if(SymbolUtilities.isNumber(dom))
                    {
                        if(SymbolUtilities.hasDirectionOfMovement(symbol.getSymbolID(),symStd))
                        {
                            Rectangle bounds = new Rectangle(symbol.getSymbolExtent());

                            if(modifiers.containsKey(ModifiersTG.Y_LOCATION))
                            {//if Y exists, we have to move DOM arrow down below it.
                                Rectangle adjustedBounds = new Rectangle(symbol.getSymbolExtentFull());
                                bounds.setRect(bounds.getX(), adjustedBounds.getY(), bounds.getWidth(), adjustedBounds.getHeight()+2);
                            }

                            double angle = Double.valueOf(dom);

                            ArrayList<ShapeInfo> arrow = CreateDOMArrow(symbol.getSymbolID(), bounds, angle);
                            if(arrow != null && arrow.size() > 0)
                            {
                                shapes.addAll(arrow);
                            }
                        }
                    }
                }
            }//end draw direction of movement arrow
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className, "ProcessSPSymbol("+ symbol.getSymbolID()+")", exc);
        }
    }






    /**
     * Returns an arraylist representing the ShapeInfo objects used to draw
     * all of the modifiers.
     * @param symbol
     * @return
     */
    private ArrayList<ShapeInfo> ProcessSinglePointModifiers(
            MilStdSymbol symbol)
    {

        Color textColor = null;
        Color textBackgroundColor = null;
        String validModifiers = "";
        SymbolDef sDef = null;
        ArrayList<ShapeInfo> modifierShapes = null;
        
        int symStd = symbol.getSymbologyStandard();
        
        sDef = SymbolDefTable.getInstance().getSymbolDef(SymbolUtilities.getBasicSymbolID(symbol.getSymbolID()),symStd);
        if(sDef != null)
        {
            validModifiers = sDef.getModifiers();
        }

        if(symbol.getTextColor() != null)
        {
            textColor = symbol.getTextColor();
        }
        else if(_RendererSettings.getLabelForegroundColor() != null)
        {
            textColor = _RendererSettings.getLabelForegroundColor();
        }
        else if(SymbolUtilities.isTacticalGraphic(symbol.getSymbolID()) && symbol.getLineColor() != null)
        {
            textColor = symbol.getLineColor();
        }
        else
            textColor = Color.BLACK;
        
        if(symbol.getTextBackgroundColor() != null)
        {
            textBackgroundColor = symbol.getTextBackgroundColor();
        }


        Rectangle symbolBounds = symbol.getSymbolExtent();
        
        //grow symbol bounds for outline if present.
        if(symbol.getOutlineEnabled())
            symbolBounds.grow(symbol.getOutlineWidth(), symbol.getOutlineWidth());
        
        modifierShapes = ProcessSPTGModifiers(symbol.getSymbolID(), validModifiers,
                                     symbol.getModifierMap(), symbolBounds,_fontRenderContext, _ModifierFont,textColor,textBackgroundColor,symStd);
        return modifierShapes;

    }





    /**
     *
     * @param milStdCode
     * @param validModifiers  null or "" will result in no modifiers being processed
     * @param modifiers
     * @param bounds
     * @param frc
     * @param labelFont
     * @param textColor
     * @return
     */
    private static ArrayList<ShapeInfo> ProcessSPTGModifiers(String milStdCode,
            String validModifiers, Map<String, String> modifiers, Rectangle bounds,
                                    FontRenderContext frc, Font labelFont, Color textColor, Color textBackgroundColor, int symStd)
    {
        ArrayList<ShapeInfo> modifierShapes = null;

        Object modifierValue = null;
        String modifierString = null;
        ArrayList<ShapeInfo> alTemp = null;
        ArrayList<ShapeInfo> alTemp2 = null;


        try
        {
            //PrintStringMap(modifiers);
            //ErrorLogger.LogMessage(milStdCode);
            //INTEGRAL TEXT
            alTemp = new ArrayList<ShapeInfo>();
            modifierShapes = new ArrayList<ShapeInfo>();
            
            if(SymbolUtilities.isTGSPWithIntegralText(milStdCode))
            {
                alTemp.addAll(CreateTGSPIntegralText(milStdCode, bounds, frc, labelFont, textColor, textBackgroundColor, symStd));
            }

            //if symbol can have modifiers and has pass modifier values, create modifier text
            if(validModifiers != null && (validModifiers.equals("") == false) &&
                    modifiers != null && modifiers.size() > 0)
            {
                //ErrorLogger.LogMessage(milStdCode + " - " + String.valueOf(validModifiers) + " it: " + String.valueOf(alTemp.size()));

                if(_ModifierNamesTG == null)
                {
                     _ModifierNamesTG = ModifiersTG.GetModifierList();
                }


                //JOptionPane.showMessageDialog(null, PrintList(modifiers), "supported types", JOptionPane.PLAIN_MESSAGE);
                //JOptionPane.showMessageDialog(null, String.valueOf(modifiers.size()), "modifier count", JOptionPane.PLAIN_MESSAGE);

                //MODIFIERS
                String modifierName = "";
                int modifierCount = _ModifierNamesTG.size();
                boolean specialLayout = SymbolUtilities.isTGSPWithSpecialModifierLayout(milStdCode);
                for(int lcv = 0; lcv < modifierCount; lcv++ )//for(String modifierName : _ModifierNamesTG)
                {
                    modifierName = _ModifierNamesTG.get(lcv);
                    //code = ModifierNameToCode(modifierName);
                    if(modifierName != null && validModifiers.indexOf(modifierName + ".") != -1)
                    {
                        modifierValue = modifiers.get(modifierName);

                        if(modifierValue != null)
                        {

                            if(modifierValue instanceof String)
                                modifierString = modifierValue.toString();


                            if(modifierString != null && !(modifierString.equals("")))
                            {

                                if(specialLayout)
                                {
                                    alTemp2 = GetSPTGSpecialModifierShape(milStdCode, modifierString, modifierName, frc, labelFont, bounds, textColor, textBackgroundColor);
                                }
                                else
                                {
                                    //ErrorLogger.LogMessage(modifierString);
                                    alTemp2 = GetSPTGModifierShape(milStdCode, modifierString, modifierName, frc, labelFont, bounds, textColor, textBackgroundColor);
                                }

                                if(alTemp2 != null && alTemp2.size() > 0)
                                            alTemp.addAll(alTemp2);

                                //temp = GetSPTGModifierShape(milStdCode, modifierString, modifierName, frc, labelFont, bounds);

                            }
                        }
                    }
                }
            }

            modifierShapes = SymbolDraw.ProcessModifierBackgrounds(alTemp);

        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className ,"ProcessSPTGModifiers()",
                    new RendererException("Failed to build single point TG for " + milStdCode, exc));
           
        }
        return modifierShapes;
    }

    


    /**
     * Creates and positions a shape for the modifier.
     * (Label Modifiers, not Graphic modifiers)
     * @param symbolID
     * @param modifierValue
     * @param modifierName example: MilStdSymbol.ADDITIONAL_INFO_1
     * @param frc
     * @param labelFont
     * @param bounds
     * @param byLabelHeight position by label height (true) or position accurately (false).
     * Typically set to true when label font is too big to position modifiers accurately.
     * @return
     */
    private static ArrayList<ShapeInfo> GetSPTGModifierShape(String symbolID, String modifierValue, String modifierName, FontRenderContext frc, Font labelFont, Rectangle bounds, Color TextColor, Color textBackgroundColor)
    {
        //Shout(modifierName, modifierValue);

        double bufferXL = 6;
        double bufferXR = 4;
        double bufferY = 2;
        double bufferText = 2;
        double x = 0;
        double y = 0;//best y
        ArrayList<ShapeInfo> alTemp = null;
        int labelHeight = 0;
        int labelWidth = 0;
        Boolean byLabelHeight = false;

        try
        {
            if(modifierValue != null && modifierValue.equals("")==false)
            {

                TextLayout text = new TextLayout(modifierValue, labelFont, frc);
                TextLayout testText = new TextLayout("TQgj", labelFont, frc);
                Float descent = text.getDescent();
                //Float totalHeight = (text.getAscent() + text.getDescent());// + text.getLeading());
                //labelHeight = Math.round(totalHeight);

                //GlyphVector gv = labelFont.createGlyphVector(frc, modifierValue);
                Rectangle labelBounds = testText.getPixelBounds(null, 0, 0);

                labelHeight = labelBounds.height;
                labelBounds = text.getPixelBounds(null, 0, 0);
                labelWidth = labelBounds.width;

                //check if text is too tall:
                double maxHeight = ((bounds.height / 3) * 2);
                if((labelHeight * 3) > maxHeight)
                    byLabelHeight = true;

                String basicID = SymbolUtilities.getBasicSymbolID(symbolID);
                //Primarily labels for checkpoint graphics

                if(modifierName.equals(ModifiersTG.N_HOSTILE))
                {
                    x = bounds.x + bounds.width + bufferXR;

                    if(!byLabelHeight)
                    {
                        y = labelHeight + ((bounds.height / 3) * 2);//checkpoint, get box above the point
                        y = bounds.y + y;
                    }
                    else
                    {
                        y = ((labelHeight + bufferText) * 3);
                        y = bounds.y + y;
                    }

                }
                else if(modifierName.equals(ModifiersTG.H_ADDITIONAL_INFO_1))//H
                {

                    x = bounds.x + (bounds.width * 0.5);
                    x = x - (labelWidth * 0.5);
                    y = bounds.y - descent;

                }
                else if(modifierName.equals(ModifiersTG.H1_ADDITIONAL_INFO_2) &&
                        basicID.equals("G*G*GPP---****X"))//action point general
                {
                    //ErrorLogger.LogMessage("action point gets H1");
                    //pretty much just for Action Point
                    x = bounds.x + (bounds.width * 0.5);
                    x = x - (labelWidth * 0.5);
                    y = bounds.y + labelHeight + (bounds.height*0.2);
                }
                else if(modifierName.equals(ModifiersTG.W_DTG_1))//W
                {
                    x = bounds.x - labelWidth - bufferXL;
                    y = bounds.y + labelHeight;
                }
                else if(modifierName.equals(ModifiersTG.W1_DTG_2))//W1
                {

                    x = bounds.x - labelWidth - bufferXL;
                    if(!byLabelHeight)
                    {
                        y = ((bounds.height / 3) * 2);//checkpoint, get box above the point
                        y = ((y * 0.5) + (labelHeight * 0.5));

                        y = bounds.y + y;
                    }
                    else
                    {
                        y = ((labelHeight + bufferText) * 2);
                        y = bounds.y + y;
                    }
                }
                else if(modifierName.equals(ModifiersTG.T_UNIQUE_DESIGNATION_1))//T
                {

                    x = bounds.x + bounds.width + bufferXR;
                    y = bounds.y + labelHeight;

                }
                else if(modifierName.equals(ModifiersTG.T1_UNIQUE_DESIGNATION_2) &&
                        (basicID.equals("G*O*ES----****X") || //emergency distress call
                        basicID.equals("G*S*PP----****X") || //medevac pick-up point
                        basicID.equals("G*S*PX----****X")))//ambulance exchange point
                {
                    //points
                    x = bounds.x + (bounds.width * 0.5);
                    x = x - (labelWidth * 0.5);
                    //y = bounds.y + (bounds.height * 0.5);
                    
                    y = ((bounds.height * 0.64));//633333333
                    y = bounds.y + y;

                }
                else
                    return null;//not a valid modifier

                //have the position, now create the shape
                ShapeInfo si = null;
                si = SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, textBackgroundColor);

                alTemp = new ArrayList<ShapeInfo>(1);
                alTemp.add(si);
                return alTemp;
            }
            else
                return null;
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("JavaRenderer", "GetSPTGModifierShape()", exc);
            return null;
        }

        
    }

    /**
     * Creates and positions a shape for the modifier.
     * (Label Modifiers, not Graphic modifiers)
     * Specific to symbols that fall under SymbolUtilities.isTGSPWithSpecialModifierLayout()
     * @param symbolID
     * @param modifierValue
     * @param modifierName example: MilStdSymbol.ADDITIONAL_INFO_1
     * @param frc
     * @param labelFont
     * @param bounds
     * @return
     */
    private static ArrayList<ShapeInfo> GetSPTGSpecialModifierShape(String symbolID, String modifierValue, String modifierName, FontRenderContext frc, Font labelFont, Rectangle bounds, Color textColor, Color textBackgroundColor)
    {
        //Shout(modifierName, modifierValue);

        double bufferXL = 6;
        double bufferXR = 4;
        double bufferY = 2;
        double bufferText = 2;
        double x = 0;
        double y = 0;//best y
        boolean duplicate = false;
        double x2 = 0;
        double y2 = 0;
        int labelHeight = 0;
        int labelWidth = 0;
        Boolean byLabelHeight = false;

        try
        {
            if(modifierValue != null && modifierValue.equals("")==false)
            {
                String basicSymbolID = SymbolUtilities.getBasicSymbolID(symbolID);
                TextLayout text = new TextLayout(modifierValue, labelFont, frc);
                TextLayout testText = new TextLayout("TQgj", labelFont, frc);
                Float descent = text.getDescent();
                //Float totalHeight = (text.getAscent() + text.getDescent());// + text.getLeading());
                //labelHeight = Math.round(totalHeight);

                //GlyphVector gv = labelFont.createGlyphVector(frc, modifierValue);
                Rectangle labelBounds = testText.getPixelBounds(null, 0, 0);

                labelHeight = labelBounds.height;
                labelBounds = text.getPixelBounds(frc, 0, 0);
                labelWidth = labelBounds.width;
                //labelBounds.height = labelHeight;
                //Shout(modifierValue, modifierName);

                //check label height:
                if(basicSymbolID.equals("G*M*NZ----****X") ||//ground zero
                        basicSymbolID.equals("G*M*NEB---****X") ||//biological
                        basicSymbolID.equals("G*M*NEC---****X"))//chemical
                {
                    if((labelHeight * 3) > bounds.getHeight())
                            byLabelHeight = true;
                }

                if(basicSymbolID.equals("G*G*GPH---****X") ||
                        basicSymbolID.equals("G*G*GPPC--****X") ||
                        basicSymbolID.equals("G*G*GPPD--****X"))
                {
                    //One modifier symbols and modifier goes in center
                    x = bounds.x + (bounds.width * 0.5);
                    x = x - (labelWidth * 0.5);
                    y = bounds.y + (bounds.height * 0.5);
                    y = y + (labelHeight * 0.5);

                }
                else if(basicSymbolID.equals("G*G*GPRI--****X"))
                {
                    //One modifier symbols, top third & center
                    x = bounds.x + (bounds.width * 0.5);
                    x = x - (labelWidth * 0.5);
                    y = bounds.y + (bounds.height * 0.25);
                    y = y + (labelHeight * 0.5);

                }
                else if(basicSymbolID.equals("G*G*GPPW--****X") ||
                        basicSymbolID.equals("G*F*PCF---****X"))
                {
                    //One modifier symbols and modifier goes right of center
                    x = bounds.x + (bounds.width * 0.75);
                    y = bounds.y + (bounds.height * 0.5);
                    y = y + (labelHeight * 0.5);

                }
                else if(basicSymbolID.equals("G*G*APP---****X") ||
                        basicSymbolID.equals("G*G*APC---****X"))
                {
                    //One modifier symbols and modifier goes just below of center
                    x = bounds.x + (bounds.width * 0.5);
                    x = x - (labelWidth * 0.5);
                    y = bounds.y + (bounds.height * 0.5);
                    y = y + (labelHeight * 1.25);

                }
                else if(basicSymbolID.equals("G*G*DPT---****X") || //T (target reference point)
                        basicSymbolID.equals("G*F*PTS---****X") || //t,h,h1 (Point/Single Target)
                        basicSymbolID.equals("G*F*PTN---****X")) //T (nuclear target)
                { //Targets with special modifier positions
                    if(modifierName.equals(ModifiersTG.H_ADDITIONAL_INFO_1) &&
                            basicSymbolID.equals("G*F*PTS---****X"))//H
                    {

                        x = bounds.x + (bounds.width * 0.75);
                        y = bounds.y + (bounds.height * 0.75);
                        y = y + (labelHeight * 0.5);

                    }
                    else if(modifierName.equals(ModifiersTG.H1_ADDITIONAL_INFO_2) &&
                            basicSymbolID.equals("G*F*PTS---****X"))//H1
                    {

                        x = bounds.x + (bounds.width * 0.25);
                        x = x - (labelWidth);
                        y = bounds.y + (bounds.height * 0.75);
                        y = y + (labelHeight * 0.5);

                    }
                    else if(modifierName.equals(ModifiersTG.T_UNIQUE_DESIGNATION_1))//T
                    {

                        x = bounds.x + (bounds.width * 0.75);
    //                    x = x - (labelBounds.width * 0.5);
                        y = bounds.y + (bounds.height * 0.25);
                        y = y + (labelHeight * 0.5);

                    }
                    else
                        return null;//not a valid modifier
                }
                else if(basicSymbolID.equals("G*M*NZ----****X") ||//ground zero
                        basicSymbolID.equals("G*M*NEB---****X") ||//biological
                        basicSymbolID.equals("G*M*NEC---****X"))//chemical
                {//NBC
                    if(modifierName.equals(ModifiersTG.N_HOSTILE))
                    {
                        x = bounds.x + bounds.width + bufferXR;

                        if(!byLabelHeight)
                        {
                            y = bounds.y + bounds.height;
                        }
                        else
                        {
                            y = ((labelHeight + bufferText) * 3);
                            y = bounds.y + y;//3rd spot
                        }

                    }
                    else if(modifierName.equals(ModifiersTG.H_ADDITIONAL_INFO_1))//H
                    {
                        x = bounds.x + bounds.width + bufferXR;
                        y = bounds.y + labelHeight - descent;
                    }
                    else if(modifierName.equals(ModifiersTG.W_DTG_1))//W
                    {
                        x = bounds.x - labelWidth - bufferXL;
                        y = bounds.y + labelHeight - descent;
                    }
                    else if(modifierName.equals(ModifiersTG.V_EQUIP_TYPE))//V
                    {
                        //subset of nbc, just nuclear
                        x = bounds.x - labelWidth - bufferXL;
                        if(!byLabelHeight)
                        {
                            y = bounds.y + ((bounds.height * 0.5) + (labelHeight * 0.5));//((bounds.height / 2) - (labelHeight/2));
                        }
                        else
                        {
                            y = bounds.y + ((labelHeight + bufferText) * 2);//2nd spot
                        }
                    }
                    else if(modifierName.equals(ModifiersTG.T_UNIQUE_DESIGNATION_1))//T
                    {
                        x = bounds.x - labelWidth - bufferXL;
                        if(!byLabelHeight)
                            y = bounds.y + bounds.height;
                        else
                        {
                            y = ((labelHeight + bufferText) * 3);
                            y = bounds.y + y;
                        }
                    }
                    else if(modifierName.equals(ModifiersTG.Y_LOCATION))//Y
                    {
                        //just NBC
                        //x = bounds.getX() + (bounds.getWidth() * 0.5);
                        //x = x - (labelWidth * 0.5);
                        x = bounds.x + (bounds.width * 0.5);
                        x = x - (labelWidth * 0.5);

                        if(!byLabelHeight)
                        {
                            y = bounds.y + bounds.height + labelHeight + bufferY;
                        }
                        else
                        {
                            y = ((labelHeight + bufferText) * 4);
                            y = bounds.y + y;
                        }

                    }
                    else if(modifierName.equals(ModifiersTG.C_QUANTITY))//C
                    {
                        //subset of NBC, just nuclear
                        x = bounds.x + (bounds.width * 0.5);
                        x = x - (labelWidth * 0.5);
                        y = bounds.y - descent;

                    }
                    else
                        return null;//not a valid modifier
                }
                else if(basicSymbolID.equals("G*M*OFS---****X"))
                {
                    if(modifierName.equals(ModifiersTG.H_ADDITIONAL_INFO_1))//H
                    {

                        x = bounds.x + (bounds.width * 0.5);
                        x = x - (labelWidth * 0.5);
                        y = bounds.y - descent;// + (bounds.height * 0.5);
                        //y = y + (labelHeight * 0.5);

                    }
                    else if(modifierName.equals(ModifiersTG.W_DTG_1))//W
                    {
                        x = bounds.x + (bounds.width * 0.5);
                        x = x - (labelWidth * 0.5);
                        y = bounds.y + (bounds.height);
                        y = y + (labelHeight) + descent;
                    }
                    else if(modifierName.equals(ModifiersTG.N_HOSTILE))
                    {
                        x = bounds.x + (bounds.width) + bufferXR;//right
                        //x = x + labelWidth;//- (labelBounds.width * 0.75);

                        duplicate = true;

                        x2 = bounds.x;//left
                        x2 = x2 - labelWidth - bufferXL;// - (labelBounds.width * 0.25);

                        y = bounds.y + (bounds.height * 0.5);//center
                        y = y + (labelHeight * 0.5);

                        y2 = y;
                    }

                }
                else if(basicSymbolID.charAt(0) == 'W' && modifierName.equals(ModifiersTG.X_ALTITUDE_DEPTH))
                {
                    String strText = modifierValue;
                    
                    if(basicSymbolID.equals("WAS-WSF-LVP----"))//Freezing Level
                    {
                        strText = "0" + (char)(176) + ":" + modifierValue;

                        text = new TextLayout(strText, labelFont, frc);
                        labelBounds = text.getPixelBounds(frc, 0, 0);
                        labelWidth = labelBounds.width;
                        
                        //One modifier symbols and modifier goes in center
                        x = bounds.x + (bounds.width * 0.5);
                        x = x - (labelWidth * 0.5);
                        y = bounds.y + (bounds.height * 0.5);
                        y = y + (labelHeight * 0.5);

                    }
                    else if(basicSymbolID.equals("WAS-WST-LVP----"))//tropopause Level
                    {
                        //One modifier symbols and modifier goes in center
                        x = bounds.x + (bounds.width * 0.5);
                        x = x - (labelWidth * 0.5);
                        y = bounds.y + (bounds.height * 0.5);
                        y = y + (labelHeight * 0.5);
                    }
                    else if(basicSymbolID.equals("WAS-PLT---P----"))//tropopause Low
                    {
                        //One modifier symbols and modifier goes just above center
                        x = bounds.x + (bounds.width * 0.5);
                        x = x - (labelWidth * 0.5);
                        y = bounds.y + (bounds.height * 0.5);
                        y = y - descent;

                    }
                    else if(basicSymbolID.equals("WAS-PHT---P----"))//tropopause High
                    {
                        //One modifier symbols and modifier goes just below of center
                        x = bounds.x + (bounds.width * 0.5);
                        x = x - (labelWidth * 0.5);
                        y = bounds.y + (bounds.height * 0.5);
                        y = y + (labelHeight);
                    }
                }
                else
                    return null;


                //have the position, now create the shape
                //Shape label = gv.getGlyphOutline(y, x, y);

                ShapeInfo si = null;
                si = SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, textColor, textBackgroundColor);

                ArrayList<ShapeInfo> siList = new ArrayList<ShapeInfo>(1);
                siList.add(si);

                if(duplicate)
                {

                    ShapeInfo si2 = null;
                    si2 = SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x2, y2, textColor, textBackgroundColor);

                    siList.add(si2);
                }
                return siList;
            }
            else
                return null;
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("JavaRenderer", "GetSPTGModifierShape()", exc);
            return null;
        }


    }

    /**
     * Rotates the shape in a ShapeInfo Object
     * @param shapes
     * @param angle IN DEGREES
     * @param anchorX Pixel location to be rotated about
     * @param anchorY Pixel location to be rotated about
     */
    private void RotateShapeInfo(ArrayList<ShapeInfo> shapes, double angle, double anchorX, double anchorY)
    {
        try
        {
            double radians = Math.toRadians(angle);
            AffineTransform at = AffineTransform.getRotateInstance(radians, anchorX, anchorY);

            ShapeInfo siTemp = null;
            if(shapes != null)
            {
                int shapeSize = shapes.size();
                for(int i = 0; i < shapeSize; i++)
                {

                    siTemp = shapes.get(i);
                    if(siTemp.getAffineTransform() != null)
                    {
                        siTemp.getAffineTransform().rotate(radians, anchorX, anchorY);
                    }
                    else
                    {
                        siTemp.setAffineTransform(at);
                    }

                }
            }

        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("JavaRenderer", "RotateShapeInfo()", exc);
            //return null;
        }
    }

    private ArrayList<ShapeInfo> CreateDOMArrow(String symbolCode, Rectangle bounds, double angle)
    {
        //x2 = x1 + (length * Math.cos(radians)));
        //y2 = y1 + (length * Math.sin(radians)));

        ArrayList<ShapeInfo> returnVal = new ArrayList<ShapeInfo>();
        Path2D.Double line = new Path2D.Double();
        Polygon arrowHead = null;
        double length = 40;

        if(SymbolUtilities.isNBC(symbolCode))
            length = bounds.height / 2;
        else
            length = bounds.height;
        //Boolean drawStaff = false;

        //get endpoint
        double dx2, dy2;
        double x1, y1;
        double x2, y2;

        x1 = bounds.getCenterX();
        y1 = bounds.getCenterY();

        //ErrorLogger.LogMessage("X: " + String.valueOf(bounds.getX()) + " Y: " + String.valueOf(bounds.getY()) + " CX: " + String.valueOf(x1) + " CY: " + String.valueOf(y1), Boolean.TRUE);
        

        line.moveTo(x1, y1);
        char scheme = symbolCode.charAt(0);
        if(SymbolUtilities.isNBC(symbolCode) ||
                ((scheme == 'S') && symbolCode.substring(2, 3).equals("G")) ||
                    scheme == 'O' || scheme == 'E')
        {
            //drawStaff = true;
            y1 = bounds.getY() + bounds.getHeight();
            line.moveTo(x1, y1);
            y1 = y1 + length;
            line.lineTo(x1, y1);

        }

        //get endpoint given start point and an angle
        //x2 = x1 + (length * Math.cos(radians)));
        //y2 = y1 + (length * Math.sin(radians)));
        angle = angle - 90;//in java, east is zero, we want north to be zero
        double radians = 0;
        radians = Math.toRadians(angle);

        dx2 = x1 + (length * Math.cos(radians));

        dy2 = (y1 + (length * Math.sin(radians)));
        x2 = (int)dx2;
        y2 = (int)dy2;

        /////////////////////////////////////////////////////////
        //draw arrow
        float arrowWidth = 8.0f;//6.5f;//7.0f;//6.5f;//10.0f//default
        float theta = 0.423f;//higher value == shorter arrow head
        theta = 0.7f;
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        float[] vecLine = new float[2];
        float[] vecLeft = new float[2];
        float fLength;
        float th;
        float ta;
        float baseX, baseY;

        xPoints[0] = (int)x2;
        yPoints[0] = (int)y2;

        //build the line vector
        vecLine[0] = (float)(xPoints[0] - x1);
        vecLine[1] = (float)(yPoints[0] - y1);

        //build the arrow base vector - normal to the line
        vecLeft[0] = -vecLine[1];
        vecLeft[1] = vecLine[0];

        //setup length parameters
        fLength = (float)Math.sqrt(vecLine[0] * vecLine[0] + vecLine[1] * vecLine[1]);
        th = arrowWidth / (2.0f * fLength);
        ta = arrowWidth / (2.0f * ((float)Math.tan(theta)/2.0f)*fLength);

        //find base of the arrow
        baseX = ((float)xPoints[0] - ta * vecLine[0]);
        baseY = ((float)yPoints[0] - ta * vecLine[1]);

        //build the points on the sides of the arrow
        xPoints[1] = (int)(baseX + th * vecLeft[0]);
        yPoints[1] = (int)(baseY + th * vecLeft[1]);
        xPoints[2] = (int)(baseX - th * vecLeft[0]);
        yPoints[2] = (int)(baseY - th * vecLeft[1]);

        //g2d.drawLine(x1, y1, (int)baseX, (int)baseY);
        line.lineTo((int)baseX, (int)baseY);
        //g2d.fillPolygon(xPoints, yPoints, 3);
        arrowHead = new Polygon(xPoints, yPoints, 3);

        ShapeInfo siLine = new ShapeInfo(line);
        siLine.setLineColor(Color.BLACK);
        siLine.setStroke(new BasicStroke(2));
        ShapeInfo siArrowHead = new ShapeInfo(arrowHead);
        siArrowHead.setFillColor(Color.BLACK);
        
        if(SymbolUtilities.isTacticalGraphic(symbolCode))
        {
            siLine.setShapeType(ShapeInfo.SHAPE_TYPE_TG_Q_MODIFIER);
            siArrowHead.setShapeType(ShapeInfo.SHAPE_TYPE_TG_Q_MODIFIER);
        }

        returnVal.add(siLine);
        returnVal.add(siArrowHead);


        return returnVal;
    }

    /**
     * Creates and positions a shape for the modifier.
     * (Label Modifiers, not Graphic modifiers)
     * @param symbolID
     * @param modifiers map of modifiers
     * @param frc
     * @param labelFont
     * @param bounds
     * @param echelonBounds optional - echelon width may indicate modifiers
     * need to be further out from the symbol (Region)
     * @param affiliationBounds optional - Like in the case of Joker, some modifiers
     * may need to be repositioned.
     * @return
     */
    private static ArrayList<ShapeInfo> GetUnitModifierShape(String symbolID, Map<String,String> modifiers, FontRenderContext frc, Font labelFont, Rectangle bounds, Rectangle echelonBounds, Rectangle affiliationBounds, Color TextColor, Color TextBackgroundColor)
    {
        double bufferXL = 5;
        double bufferXR = 5;
        double bufferY = 2;
        double bufferText = 2;
        double x = 0;
        double y = 0;//best y
        double cpofNameX = 0;
        ArrayList<ShapeInfo> alTemp = new ArrayList<ShapeInfo>();
        int labelHeight = 0;
        int labelWidth = 0;
        Boolean byLabelHeight = false;


        TextLayout text = null;

        try
        {
            if(echelonBounds != null && echelonBounds.getWidth() > bounds.getWidth())
            {
                bounds.setRect(echelonBounds.getX(), bounds.getY(),
                echelonBounds.getWidth(), bounds.getHeight());
            }

            TextLayout testText = new TextLayout("TQgj", labelFont, frc);
            Float descent = testText.getDescent();
            //Float totalHeight = (text.getAscent() + text.getDescent());// + text.getLeading());
            //labelHeight = Math.round(totalHeight);

            //GlyphVector gv = labelFont.createGlyphVector(frc, modifierValue);
            Rectangle labelBounds = testText.getPixelBounds(null, 0, 0);

            labelHeight = labelBounds.height;

            labelWidth = labelBounds.width;

            //check if text is too tall:
            double maxHeight = (bounds.height);
            if((labelHeight * 3) > maxHeight)
                byLabelHeight = true;


//            int y0 = 0;//W    E/F
//            int y1 = 0;//X/Y  G
//            int y2 = 0;//V/AD/AE    H/AF
//            int y3 = 0;//T    M CC
//            int y4 = 0;//Z    J/K/L/N/P
//
//            y0 = bounds.y - 0;
//            y1 = bounds.y - labelHeight;
//            y2 = bounds.y - (labelHeight + (int)bufferText) * 2;
//            y3 = bounds.y - (labelHeight + (int)bufferText) * 3;
//            y4 = bounds.y - (labelHeight + (int)bufferText) * 4;


            //int textRenderMethod = RendererSettings.getInstance().getTextRenderMethod();

            alTemp = new ArrayList<ShapeInfo>();

            Iterator itr = modifiers.keySet().iterator();
            String modifierName = "";
            String modifierValue = "";
            
            if(SymbolUtilities.hasValidCountryCode(symbolID))
            {
                modifiers.put(ModifiersUnits.CC_COUNTRY_CODE, symbolID.substring(12, 14));
            }

            //ArrayList<String> modifierNames = ModifiersUnits.GetModifierList();
            
            cpofNameX = bounds.x + bounds.width + bufferXR;

            if(modifiers.containsKey(ModifiersUnits.X_ALTITUDE_DEPTH) 
                    || modifiers.containsKey(ModifiersUnits.Y_LOCATION))
            {
                String xm = modifiers.get(ModifiersUnits.X_ALTITUDE_DEPTH);
                String ym = modifiers.get(ModifiersUnits.Y_LOCATION);

                if(xm == null && ym != null)
                    modifierValue = ym;
                else if(xm != null && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.X_ALTITUDE_DEPTH))
                {
                    if(ym == null)
                        modifierValue = xm;
                    else
                        modifierValue = xm + " " + ym;    
                }
                

                text = new TextLayout(modifierValue, labelFont, frc);
                labelBounds = text.getPixelBounds(null, 0, 0);
                //labelWidth = labelBounds.width;
                if(!byLabelHeight)
                {
                    x = bounds.x - labelBounds.width - bufferXL;
                    y = bounds.y + labelHeight;
                }
                else
                {
                    x = bounds.x - labelBounds.width - bufferXL;
                    
                    y = (bounds.height );
                    y = ((y * 0.5) + (labelHeight * 0.5));
                    
                    y = y - ((labelHeight + bufferText));
                    y = bounds.y + y;
                }

                alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));
            }
            if(modifiers.containsKey(ModifiersUnits.G_STAFF_COMMENTS) && 
                    SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.G_STAFF_COMMENTS))
            {

                modifierValue = modifiers.get(ModifiersUnits.G_STAFF_COMMENTS);
                text = new TextLayout(modifierValue, labelFont, frc);
                //labelBounds = text.getPixelBounds(null, 0, 0);//not needed for right side labels
                //labelWidth = labelBounds.width;//not needed for right side labels
                if(!byLabelHeight)
                {
                    x = bounds.x + bounds.width + bufferXR;
                    y = bounds.y + labelHeight;
                }
                else
                {
                    x = bounds.x + bounds.width + bufferXR;
                    
                    y = (bounds.height );
                    y = ((y * 0.5) + (labelHeight * 0.5));
                    
                    y = y - ((labelHeight + bufferText));
                    y = bounds.y + y;
                }
                
               
                alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));

                //Concession for cpof name label
                if((x + text.getBounds().getWidth() + 3) > cpofNameX)
                        cpofNameX = x + text.getBounds().getWidth() + 3;


            }
            if(modifiers.containsKey(ModifiersUnits.V_EQUIP_TYPE) ||
                    modifiers.containsKey(ModifiersUnits.AD_PLATFORM_TYPE) ||
                    modifiers.containsKey(ModifiersUnits.AE_EQUIPMENT_TEARDOWN_TIME))
            {
                
                String vm = null,
                    adm = null,
                    aem = null;

                if (modifiers.containsKey(ModifiersUnits.V_EQUIP_TYPE) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.V_EQUIP_TYPE))
                {
                    vm = modifiers.get(ModifiersUnits.V_EQUIP_TYPE);
                }
                if (modifiers.containsKey(ModifiersUnits.AD_PLATFORM_TYPE) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.AD_PLATFORM_TYPE))
                {
                    adm = modifiers.get(ModifiersUnits.AD_PLATFORM_TYPE);
                }
                if (modifiers.containsKey(ModifiersUnits.AE_EQUIPMENT_TEARDOWN_TIME) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.AE_EQUIPMENT_TEARDOWN_TIME))
                {
                    aem = modifiers.get(ModifiersUnits.AE_EQUIPMENT_TEARDOWN_TIME);
                }
                
                modifierValue = "";
                if(vm != null && vm.equals("") == false)
                    modifierValue = vm;
                if(adm != null && adm.equals("") == false)
                    modifierValue += " " + adm;
                if(aem != null && aem.equals("") == false)
                    modifierValue += " " + aem;
                
                modifierValue = modifierValue.trim();
                if(modifierValue.equals("") == false)
                {
                    text = new TextLayout(modifierValue, labelFont, frc);
                    labelBounds = text.getPixelBounds(null, 0, 0);
                    //labelWidth = labelBounds.width;

                    x = bounds.x - labelBounds.width - bufferXL;

                    y = (bounds.height );//checkpoint, get box above the point
                    y = ((y * 0.5) + (labelHeight * 0.5));
                    y = bounds.y + y;

                    alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));        
                }
            }
            if(modifiers.containsKey(ModifiersUnits.H_ADDITIONAL_INFO_1) ||
                    modifiers.containsKey(ModifiersUnits.AF_COMMON_IDENTIFIER))
            {
                modifierValue = "";
                String hm = "",
                        afm = "";
                hm = modifiers.get(ModifiersUnits.H_ADDITIONAL_INFO_1);
                if (modifiers.containsKey(ModifiersUnits.H_ADDITIONAL_INFO_1))
                {
                    hm = modifiers.get(ModifiersUnits.H_ADDITIONAL_INFO_1);
                }
                if (modifiers.containsKey(ModifiersUnits.AF_COMMON_IDENTIFIER) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.AF_COMMON_IDENTIFIER))
                {
                    afm = modifiers.get(ModifiersUnits.AF_COMMON_IDENTIFIER);
                }
                
                modifierValue = hm + " " + afm;
                modifierValue = modifierValue.trim();
                
                if(modifierValue.equals("") == false)
                {
                    text = new TextLayout(modifierValue, labelFont, frc);
                    //labelBounds = text.getPixelBounds(null, 0, 0);
                    //labelWidth = labelBounds.width;

                    x = bounds.x + bounds.width + bufferXR;

                    y = (bounds.height );//checkpoint, get box above the point
                    y = ((y * 0.5) + (labelHeight * 0.5));
                    y = bounds.y + y;

                    alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));

                    //Concession for cpof name label
                    if((x + text.getBounds().getWidth() + 3) > cpofNameX)
                            cpofNameX = x + text.getBounds().getWidth() + 3;
                }
            }
            if(modifiers.containsKey(ModifiersUnits.T_UNIQUE_DESIGNATION_1))
            {
                modifierValue = modifiers.get(ModifiersUnits.T_UNIQUE_DESIGNATION_1);
                text = new TextLayout(modifierValue, labelFont, frc);
                labelBounds = text.getPixelBounds(null, 0, 0);
                labelWidth = labelBounds.width;

                x = bounds.x - labelWidth - bufferXL;
                if(!byLabelHeight)
                    y = bounds.y + bounds.height;
                else
                {
                    y = (bounds.height );
                    y = ((y * 0.5) + (labelHeight * 0.5));
                    
                    y =  y + ((labelHeight + bufferText));
                    y = bounds.y + y;
                }
                alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));
            }
            if(modifiers.containsKey(ModifiersUnits.M_HIGHER_FORMATION) ||
                       modifiers.containsKey(ModifiersUnits.CC_COUNTRY_CODE))
            {
                modifierValue = "";
                String mValue = "";
                if(SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.M_HIGHER_FORMATION))
                    mValue = modifiers.get(ModifiersUnits.M_HIGHER_FORMATION);
                String ccValue = modifiers.get(ModifiersUnits.CC_COUNTRY_CODE);

                if(mValue != null)
                    modifierValue += mValue;
                if(ccValue != null)
                {
                    if(mValue != null && mValue.equals("") == false)
                        modifierValue += " ";
                    modifierValue += ccValue;
                }
                
                if(modifierValue.equals("")==false)
                {
                    text = new TextLayout(modifierValue, labelFont, frc);

                    x = bounds.x + bounds.width + bufferXR;
                    if(!byLabelHeight)
                        y = bounds.y + bounds.height;
                    else
                    {
                        y = (bounds.height );
                        y = ((y * 0.5) + (labelHeight * 0.5));

                        y =  y + ((labelHeight + bufferText));
                        y = bounds.y + y;
                    }
                    alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));

                    //Concession for cpof name label
                    if((x + text.getBounds().getWidth() + 3) > cpofNameX)
                            cpofNameX = x + text.getBounds().getWidth() + 3;
                }
            }
            if(modifiers.containsKey(ModifiersUnits.Z_SPEED) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.Z_SPEED))
            {
                modifierValue = modifiers.get(ModifiersUnits.Z_SPEED);
                text = new TextLayout(modifierValue, labelFont, frc);
                labelBounds = text.getPixelBounds(null, 0, 0);
                labelWidth = labelBounds.width;

                x = bounds.x - labelWidth - bufferXL;
                if(!byLabelHeight)
                    y = bounds.y + bounds.height + labelHeight + bufferText;
                else
                {
                    y = (bounds.height );
                    y = ((y * 0.5) + (labelHeight * 0.5));
                    
                    y = y + ((labelHeight + bufferText)*2);
                    y = bounds.y + y;
                }
                alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));
            }
            if(modifiers.containsKey(ModifiersUnits.J_EVALUATION_RATING) ||
                    modifiers.containsKey(ModifiersUnits.K_COMBAT_EFFECTIVENESS) ||
                    modifiers.containsKey(ModifiersUnits.L_SIGNATURE_EQUIP) ||
                    modifiers.containsKey(ModifiersUnits.N_HOSTILE) ||
                    modifiers.containsKey(ModifiersUnits.P_IFF_SIF))
            {
                String jm = null,
                    km = null,
                    lm = null,
                    nm = null,
                    pm = null;

                if (modifiers.containsKey(ModifiersUnits.J_EVALUATION_RATING))
                {
                    jm = modifiers.get(ModifiersUnits.J_EVALUATION_RATING);
                }
                if (modifiers.containsKey(ModifiersUnits.K_COMBAT_EFFECTIVENESS) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.K_COMBAT_EFFECTIVENESS))
                {
                    km = modifiers.get(ModifiersUnits.K_COMBAT_EFFECTIVENESS);
                }
                if (modifiers.containsKey(ModifiersUnits.L_SIGNATURE_EQUIP) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.L_SIGNATURE_EQUIP))
                {
                    lm = modifiers.get(ModifiersUnits.L_SIGNATURE_EQUIP);
                }
                if (modifiers.containsKey(ModifiersUnits.N_HOSTILE) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.N_HOSTILE))
                {
                    nm = modifiers.get(ModifiersUnits.N_HOSTILE);
                }
                if (modifiers.containsKey(ModifiersUnits.P_IFF_SIF) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.P_IFF_SIF))
                {
                    pm = modifiers.get(ModifiersUnits.P_IFF_SIF);
                }

                modifierValue = "";
                if (jm != null && jm.equals("") == false)
                {
                    modifierValue = modifierValue + jm;
                }
                if (km != null && km.equals("") == false)
                {
                    modifierValue = modifierValue + " " + km;
                }
                if (lm != null && lm.equals("") == false)
                {
                    modifierValue = modifierValue + " " + lm;
                }
                if (nm != null && nm.equals("") == false)
                {
                    modifierValue = modifierValue + " " + nm;
                }
                if (pm != null && pm.equals("") == false)
                {
                    modifierValue = modifierValue + " " + pm;
                }

                if (modifierValue.length() > 2 && modifierValue.charAt(0) == ' ')
                {
                    modifierValue = modifierValue.substring(1);
                }
                modifierValue = modifierValue.trim();

                if(modifierValue.equals("")==false)
                {
                    text = new TextLayout(modifierValue, labelFont, frc);
                    labelBounds = text.getPixelBounds(null, 0, 0);
                    labelWidth = labelBounds.width;

                    x = bounds.x + bounds.width + bufferXR;
                    if(!byLabelHeight)
                        y = bounds.y + bounds.height + labelHeight + bufferText;
                    else
                    {
                        y = (bounds.height );
                        y = ((y * 0.5) + (labelHeight * 0.5));

                        y = y + ((labelHeight + bufferText)*2);
                        y = bounds.y + y;
                    }
                    alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));

                    //Concession for cpof name label
                    if((x + text.getBounds().getWidth() + 3) > cpofNameX)
                            cpofNameX = x + text.getBounds().getWidth() + 3;
                }
            }
            if(modifiers.containsKey(ModifiersUnits.W_DTG_1))
            {
                modifierValue = modifiers.get(ModifiersUnits.W_DTG_1);
                text = new TextLayout(modifierValue, labelFont, frc);
                labelBounds = text.getPixelBounds(null, 0, 0);//not needed for right side labels
                labelWidth = labelBounds.width;//not needed for right side labels
                if(!byLabelHeight)
                {
                    x = bounds.x - labelWidth - bufferXL;
                    y = bounds.y - bufferY - descent;
                }
                else
                {
                    x = bounds.x - labelWidth - bufferXL;
                    
                    y = (bounds.height );
                    y = ((y * 0.5) + (labelHeight * 0.5));
                    
                    y = y - ((labelHeight + bufferText)*2);
                    y = bounds.y + y;
                }
                alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));
            }
            if(modifiers.containsKey(ModifiersUnits.F_REINFORCED_REDUCED) ||
                    modifiers.containsKey(ModifiersUnits.E_FRAME_SHAPE_MODIFIER))
            {
                modifierValue = null;
                String E = modifiers.get(ModifiersUnits.E_FRAME_SHAPE_MODIFIER);
                String F = "";
                
                if(E != null && E.equals("")==false)
                    modifierValue = E;
                
                if(SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.F_REINFORCED_REDUCED))
                    F = modifiers.get(ModifiersUnits.F_REINFORCED_REDUCED);
                if(F!= null && F.equals("")==false)
                {
                    if(F.toUpperCase().equals("R"))
                        F = "(+)";
                    else if(F.toUpperCase().equals("D"))
                        F = "(-)";
                    else if(F.toUpperCase().equals("RD"))
                        F = "(" + (char)177 + ")";
                    else
                        F = null;
                }

                if(F != null && F.equals("")==false)
                {
                    if(modifierValue != null && modifierValue.equals("")==false)
                        modifierValue = modifierValue + " " + F;
                    else
                        modifierValue = F;
                }
                
                
                if(modifierValue != null && modifierValue.equals("")==false)
                {
                    text = new TextLayout(modifierValue, labelFont, frc);
                    //labelBounds = text.getPixelBounds(null, 0, 0);//not needed for right side labels
                    //labelWidth = labelBounds.width;//not needed for right side labels
                    if(!byLabelHeight)
                    {
                        if(affiliationBounds != null)
                        {
                            x = affiliationBounds.x + affiliationBounds.width + bufferXR;
                        }
                        else
                        {
                            x = bounds.x + bounds.width + bufferXR;
                        }
                        y = bounds.y - bufferY - descent;
                    }
                    else
                    {
                        if(affiliationBounds != null)
                        {
                            x = affiliationBounds.x + affiliationBounds.width + bufferXR;
                        }
                        else
                        {
                            x = bounds.x + bounds.width + bufferXR;
                        }
                        
                        y = (bounds.height );
                        y = ((y * 0.5) + (labelHeight * 0.5));

                        y = y - ((labelHeight + bufferText)*2);
                        y = bounds.y + y;
                    }
                    alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));

                    //Concession for cpof name label
                    if((x + text.getBounds().getWidth() + 3) > cpofNameX)
                            cpofNameX = x + text.getBounds().getWidth() + 3;
                }
            }
            if(modifiers.containsKey(ModifiersUnits.C_QUANTITY) &&
                    SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.C_QUANTITY))
            {
                modifierValue = modifiers.get(ModifiersUnits.C_QUANTITY);
                text = new TextLayout(modifierValue, labelFont, frc);
                labelBounds = text.getPixelBounds(null, 0, 0);//not needed for right side labels
                labelWidth = labelBounds.width;//not needed for right side labels
                x = (bounds.x + (bounds.width * 0.5)) - (labelWidth * 0.5);
                y = bounds.y - bufferY - descent;
                alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));
            }
            if(modifiers.containsKey(ModifiersUnits.AA_SPECIAL_C2_HQ) &&
                    SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.AA_SPECIAL_C2_HQ))
            {
                modifierValue = modifiers.get(ModifiersUnits.AA_SPECIAL_C2_HQ);
                text = new TextLayout(modifierValue, labelFont, frc);
                labelBounds = text.getPixelBounds(null, 0, 0);
                labelWidth = labelBounds.width;

                x = (bounds.x + (bounds.width * 0.5)) - (labelWidth * 0.5);
                
                y = (bounds.height );//checkpoint, get box above the point
                y = ((y * 0.5) + (labelHeight * 0.5));
                y = bounds.y + y;
                
                
                alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));
            }
            if(modifiers.containsKey(ModifiersUnits.CN_CPOF_NAME_LABEL))
            {
                modifierValue = modifiers.get(ModifiersUnits.CN_CPOF_NAME_LABEL);

                text = new TextLayout(modifierValue, labelFont, frc);
                //labelBounds = text.getPixelBounds(null, 0, 0);
                //labelWidth = labelBounds.width;

                x = cpofNameX;
                
                y = (bounds.height );//checkpoint, get box above the point
                y = ((y * 0.5) + (labelHeight * 0.5));
                y = bounds.y + y;
                
                
                alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));
            }
            if(modifiers.containsKey(ModifiersUnits.SCC_SONAR_CLASSIFICATION_CONFIDENCE) && SymbolUtilities.canUnitHaveModifier(symbolID, ModifiersUnits.SCC_SONAR_CLASSIFICATION_CONFIDENCE))
            {
                modifierValue = modifiers.get(ModifiersUnits.SCC_SONAR_CLASSIFICATION_CONFIDENCE);
                
                int scc = 0;
                if(SymbolUtilities.isNumber(modifierValue) && SymbolUtilities.hasModifier(symbolID, ModifiersUnits.SCC_SONAR_CLASSIFICATION_CONFIDENCE))
                {
                    scc = Integer.valueOf(modifierValue);
                    if(scc > 0 && scc < 6)
                    {
                        double yPosition = getYPositionForSCC(symbolID);
                        
                        text = new TextLayout(modifierValue, labelFont, frc);
                        //labelBounds = text.getPixelBounds(null, 0, 0);
                        //labelWidth = labelBounds.width;

                        labelBounds = text.getPixelBounds(null, 0, 0);
                        labelWidth = labelBounds.width;

                        x = (bounds.x + (bounds.width * 0.5)) - (labelWidth * 0.5);

                        y = (bounds.height );//checkpoint, get box above the point
                        y = ((y * yPosition) + ((labelHeight-descent) * 0.5));
                        y = bounds.y + y;


                        alTemp.add(SymbolDraw.CreateModifierShapeInfo(text, modifierValue, x, y, TextColor, TextBackgroundColor));
                    }
                }

            }
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("JavaRenderer", "GetUnitModifierShape", exc);
            //return null;
        }
 
        return alTemp;
    }

    private static double getYPositionForSCC(String symbolID)
    {
        double yPosition = 0.32;
        String temp = symbolID.substring(4, 10);
        char affiliation = symbolID.charAt(1);

        if(temp.equals("WMGC--"))//GROUND (BOTTOM) MILCO
        {
            if(affiliation == 'H' || 
                    affiliation == 'S')//suspect
                yPosition = 0.29;
            else if(affiliation == 'N' ||
                    affiliation == 'L')//exercise neutral
                yPosition = 0.32;
            else if(affiliation == 'F' ||
                    affiliation == 'A' ||//assumed friend
                    affiliation == 'D' ||//exercise friend
                    affiliation == 'M' ||//exercise assumed friend
                    affiliation == 'K' ||//faker
                    affiliation == 'J')//joker
                yPosition = 0.32;
            else
                yPosition = 0.34;
        }
        else if(temp.equals("WMMC--"))//MOORED MILCO
        {
            if(affiliation == 'H' || 
                    affiliation == 'S')//suspect
                yPosition = 0.29;
            else if(affiliation == 'N' ||
                    affiliation == 'L')//exercise neutral
                yPosition = 0.32;
            else if(affiliation == 'F' ||
                    affiliation == 'A' ||//assumed friend
                    affiliation == 'D' ||//exercise friend
                    affiliation == 'M' ||//exercise assumed friend
                    affiliation == 'K' ||//faker
                    affiliation == 'J')//joker
                yPosition = 0.32;
            else
                yPosition = 0.34;
        }
        else if(temp.equals("WMFC--"))//FLOATING MILCO
        {
            if(affiliation == 'H' || 
                    affiliation == 'S')//suspect
                yPosition = 0.29;
            else if(affiliation == 'N' ||
                    affiliation == 'L')//exercise neutral
                yPosition = 0.32;
            else if(affiliation == 'F' ||
                    affiliation == 'A' ||//assumed friend
                    affiliation == 'D' ||//exercise friend
                    affiliation == 'M' ||//exercise assumed friend
                    affiliation == 'K' ||//faker
                    affiliation == 'J')//joker
                yPosition = 0.32;
            else
                yPosition = 0.34;
        }
        else if(temp.equals("WMC---"))//GENERAL MILCO
        {
            if(affiliation == 'H' || 
                    affiliation == 'S')//suspect
                yPosition = 0.35;
            else if(affiliation == 'N' ||
                    affiliation == 'L')//exercise neutral
                yPosition = 0.39;
            else if(affiliation == 'F' ||
                    affiliation == 'A' ||//assumed friend
                    affiliation == 'D' ||//exercise friend
                    affiliation == 'M' ||//exercise assumed friend
                    affiliation == 'K' ||//faker
                    affiliation == 'J')//joker
                yPosition = 0.39;
            else
                yPosition = 0.39;
        }
        
        return yPosition;
    }
    /**
     * 
     * @param SymbolID
     * @param symbolBounds
     * @return
     */
    private static ArrayList<ShapeInfo> CreateTGSPIntegralText(String SymbolID, Rectangle2D symbolBounds, FontRenderContext frc, Font labelFont, Color textColor, Color textBackgroundColor, int symStd)
    {

        double bufferXL = 6;
        double bufferXR = 4;
        double bufferY = 2;
        double bufferText = 2;
        double centerOffset = 1; //getCenterX/Y function seems to go over by a pixel
        double x = 0;
        double y = 0;
        double x2 = 0;
        double y2 = 0;

        int labelHeight = 0;
        int labelWidth = 0;

        String basicID = SymbolUtilities.getBasicSymbolID(SymbolID);

        TextLayout text1 = null;//new TextLayout(modifierValue, labelFont, frc);
        TextLayout text2 = null;
        TextLayout testText = null;
        GlyphVector gvText1 = null;
        GlyphVector gvText2 = null;
        Float descent = 0f;//text.getDescent();
        //Float totalHeight = (text.getAscent() + text.getDescent());// + text.getLeading());
        //labelHeight = Math.round(totalHeight);

        //GlyphVector gv = labelFont.createGlyphVector(frc, modifierValue);
        Rectangle labelBounds1 = null;//text.getPixelBounds(null, 0, 0);
        Rectangle labelBounds2 = null;


        Boolean byLabelHeight = false;

        double ratio = 0;
        double pixelSize = 0;
        
        ArrayList<ShapeInfo> returnVal = null;

        String strText1 = "";
        String strText2 = "";

        try
        {

            returnVal = new ArrayList<ShapeInfo>();
            if(basicID.equals("G*G*GPRD--****X"))//DLRP (D)
            {

                strText1 = "D";
                text1 = new TextLayout(strText1, labelFont, frc);
                //gvText1 = labelFont.createGlyphVector(frc, "D");
                descent = text1.getDescent();
                labelBounds1 = text1.getPixelBounds(null, 0, 0);
                if(symStd == RendererSettings.Symbology_2525B)
                {
                    y = symbolBounds.getY() + symbolBounds.getHeight();
                    x = symbolBounds.getX() - labelBounds1.getWidth() - bufferXL;
                }
                else//2525C built in
                {
                    text1=null;
                    //y = symbolBounds.getY() + symbolBounds.getHeight() - bufferY;
                    //x = symbolBounds.getX() + symbolBounds.getWidth()/2 - labelBounds1.getWidth()/2;
                }

                //ErrorLogger.LogMessage("D: " + String.valueOf(x)+ ", " + String.valueOf(y));
            }
            else if (basicID.equals("G*G*APU---****X")) //pull-up point (PUP)
            {
                strText1 = "PUP";
                text1 = new TextLayout(strText1, labelFont, frc);
                //gvText1 = labelFont.createGlyphVector(frc, "D");
                descent = text1.getDescent();
                labelBounds1 = text1.getPixelBounds(null, 0, 0);
                y = symbolBounds.getY() + (symbolBounds.getHeight()/2) + (text1.getAscent()/2);
                x = symbolBounds.getX() + symbolBounds.getWidth() + bufferXR;
            }
            else if(basicID.equals("G*M*NZ----****X")) //Nuclear Detonation Ground Zero (N)
            {
//                strText1 = "N";
//                text1 = new TextLayout(strText1, labelFont, frc);
//                descent = text1.getDescent();
//                labelBounds1 = text1.getPixelBounds(null, 0, 0);
//                y = symbolBounds.getY() + (symbolBounds.getHeight() * 0.8) - centerOffset;
//                x = symbolBounds.getCenterX() - centerOffset - (labelBounds1.getWidth()/2);
            }
            else if(basicID.equals("G*M*NF----****X"))//Fallout Producing (N)
            {
//                strText1 = "N";
//                text1 = new TextLayout(strText1, labelFont, frc);
//                descent = text1.getDescent();
//                labelBounds1 = text1.getPixelBounds(null, 0, 0);
//                y = symbolBounds.getY() + (symbolBounds.getHeight() * 0.8) - centerOffset;
//                x = symbolBounds.getCenterX() - centerOffset - (labelBounds1.getWidth()/2);
            }
            else if(basicID.equals("G*M*NEB---****X"))//Release Events Biological (BIO, B)
            {
                testText = new TextLayout("TQgj", labelFont, frc);
                labelHeight = (int)(testText.getBounds().getHeight());
                if(labelHeight * 3 > symbolBounds.getHeight())
                        byLabelHeight = true;

                //strText1 = "B";
                //text1 = new TextLayout(strText1, labelFont, frc);
                strText2 = "BIO";
                text2 = new TextLayout(strText2, labelFont, frc);
                //descent = text1.getDescent();
                //labelBounds1 = text1.getPixelBounds(null, 0, 0);
                labelBounds2 = text2.getPixelBounds(null, 0, 0);
                //y = symbolBounds.getY() + (symbolBounds.getHeight() * 0.9);
                //x = symbolBounds.getCenterX() - centerOffset - (labelBounds1.getWidth()/2);
                if(!byLabelHeight)
                    y2 = symbolBounds.getCenterY() + (labelBounds2.getHeight()/2);
                else
                    y2 = symbolBounds.getY() + ((labelHeight + bufferText) * 2);
                x2 = symbolBounds.getX() - labelBounds2.getWidth() - bufferXL;

                //ErrorLogger.LogMessage("BIO: " + String.valueOf(x2)+ ", " + String.valueOf(y2));
            }
            else if(basicID.equals("G*M*NEC---****X"))//Release Events Chemical (CML, C)
            {
                testText = new TextLayout("TQgj", labelFont, frc);
                labelHeight = (int)(testText.getBounds().getHeight());
                if(labelHeight * 3 > symbolBounds.getHeight())
                        byLabelHeight = true;

                //strText1 = "C";
                //text1 = new TextLayout(strText1, labelFont, frc);
                strText2 = "CML";
                text2 = new TextLayout(strText2, labelFont, frc);
                //descent = text1.getDescent();
                //labelBounds1 = text1.getPixelBounds(null, 0, 0);
                labelBounds2 = text2.getPixelBounds(null, 0, 0);
                //y = symbolBounds.getY() + (symbolBounds.getHeight() * 0.9);
                //x = symbolBounds.getCenterX() - centerOffset - (labelBounds1.getWidth()/2);
                if(!byLabelHeight)
                    y2 = symbolBounds.getCenterY() + (labelBounds2.getHeight()/2);
                else
                    y2 = symbolBounds.getY() + ((labelHeight + bufferText) * 2);
                x2 = symbolBounds.getX() - labelBounds2.getWidth() - bufferXL;
            }

            //have the position, now create the shape
            returnVal = new ArrayList<ShapeInfo>();

            //create shapeInfo objects
            if(text1 != null)
            {
                
                ShapeInfo si1 = null;
                si1 = SymbolDraw.CreateModifierShapeInfo(text1, strText1, x, y, textColor, textBackgroundColor);

                returnVal.add(si1);
            }
            if(text2 != null)
            {
                
                ShapeInfo si2 = null;
                si2 = SymbolDraw.CreateModifierShapeInfo(text2, strText2, x2, y2, textColor, textBackgroundColor);

                returnVal.add(si2);
            }
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("JavaRenderer", "CreateTGSPIntegralText()", exc);
        }


        return returnVal;

    }
    
        public int getSinglePointTGSymbolSize()
    {
            return _SinglePointFont.getSize();
    }

    public int getUnitSymbolSize()
    {
       return  _UnitFont.getSize();
    }

    public void setSinglePointTGSymbolSize(int size) {
        synchronized(_SinglePointFontMutex)
        {
            _SinglePointFont = SinglePointFont.getInstance().getSPFont(size);
        }

    }

    public void setUnitSymbolSize(int size) {
        synchronized(_UnitFontMutex)
        {
            _UnitFont = SinglePointFont.getInstance().getUnitFont(size);
        }
    }
    
    /**
     * reloads modifier font based on the current RendererSettings
     */
    public void RefreshModifierFont()
    {
        synchronized(_ModifierFontMutex)
        {
            _ModifierFont = RendererSettings.getInstance().getLabelFont();
        }
    }
    
    /**
     * 
     * @param name
     * @param type
     * @param size 
     * @deprecated
     */
    public void setModifierFont(String name, int type, int size) 
    {
        synchronized(_ModifierFontMutex)
        {
            _ModifierFont = RendererSettings.getInstance().getLabelFont();
        }
    }
    /**
     * 
     * @param font 
     * @deprecated
     */
    public void setModifierFont(Font font) 
    {
        synchronized(_ModifierFontMutex)
        {
            _ModifierFont = RendererSettings.getInstance().getLabelFont();
        }
    }

    private static String PrintList(ArrayList list)
    {
        String message = "";
        for(Object item : list)
        {

            message += item.toString() + "\n";
        }
        return message;
    }
    
    private static String PrintObjectMap(Map<String, Object> map)
    {
        Iterator<Object> itr = map.values().iterator();
        String message = "";
        String temp = null;
        while(itr.hasNext())
        {
            temp = String.valueOf(itr.next());
            if(temp != null)
                message += temp + "\n";
        }
        //ErrorLogger.LogMessage(message);
        return message;
    }
    
    private static void PrintStringMap(Map<String, String> map)
    {
        Iterator<String> itr = map.values().iterator();
        String message = "";
        String temp = null;
        while(itr.hasNext())
        {
            temp = String.valueOf(itr.next());
            if(temp != null)
                message += temp + "\n";
        }
        ErrorLogger.LogMessage(message);
    }
    
}
