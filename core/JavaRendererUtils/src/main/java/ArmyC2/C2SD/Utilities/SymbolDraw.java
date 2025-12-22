/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ArmyC2.C2SD.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class that has functions for drawing the symbols and creating some of the
 * needed shapes.
 * @author michael.spinelli
 */
public class SymbolDraw {

    
    /**
     * Does the actual drawing of the Symbol.  MilstdSymbol need to be properly populated
     * via the Render call first.
     * Not for client use.  They should Use IJavaRenderer.Draw or IJavaRenderer.DrawDB
     * @param symbol
     * @param destination surface to draw to
     * @param offsetX usually a negative value.  if your clip.X is 40, offsetX should be -40
     * @param offsetY usually a negative value.  if your clip.Y is 40, offsetY should be -40
     * @throws RendererException
     */
    public static void Draw(MilStdSymbol symbol, Graphics2D destination, int offsetX, int offsetY) throws RendererException
    {
        ArrayList<MilStdSymbol> symbols = new ArrayList<MilStdSymbol>();
        symbols.add(symbol);
        Draw(symbols, destination, offsetX, offsetY);
    }

    /**
     * Does the actual drawing of the Symbol.  MilstdSymbol need to be properly populated
     * via the Render call first.
     * Not for client use.  They should Use IJavaRenderer.Draw or IJavaRenderer.DrawDB
     * @param symbols
     * @param destination surface to draw to
     * @param offsetX usually a negative value.  if your clip.X is 40, offsetX should be -40
     * @param offsetY usually a negative value.  if your clip.Y is 40, offsetY should be -40
     * @throws RendererException
     */
    public static void Draw(ArrayList<MilStdSymbol> symbols, Graphics2D destination, int offsetX, int offsetY) throws RendererException
    {
        Logger loggy = Logger.getLogger(ErrorLogger.LoggerName);
        try
        {


            if(symbols != null && destination != null)
            {

                destination.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Stroke oldStroke = destination.getStroke();
                AffineTransform oldTransform = destination.getTransform();

                //loop through symbols and draw
                MilStdSymbol symbol;

                int unitCount = symbols.size();
                for(int lcv = 0; lcv < unitCount; lcv++)
                {
                    symbol = symbols.get(lcv);

                    //synchronized(destination)
                    //{
                        ArrayList<ShapeInfo> shapes = symbol.getSymbolShapes();
                        if(shapes != null)
                        {
                            ShapeInfo siTemp;
                            for(int i = 0; i < shapes.size(); i++)
                            {
                                siTemp = shapes.get(i);

                                if(siTemp.getAffineTransform() != null)
                                {
                                    AffineTransform atTemp = (AffineTransform)siTemp.getAffineTransform().clone();
                                    atTemp.preConcatenate(AffineTransform.getTranslateInstance(offsetX, offsetY));
                                    destination.setTransform(atTemp);
                                     //destination.setTransform(siTemp.getAffineTransform());
                                }
                                else
                                    destination.translate(offsetX, offsetY);

                                if(siTemp.getStroke() != null)
                                        destination.setStroke(siTemp.getStroke());

                                if(siTemp.getTexturePaint() != null)
                                {
                                    TexturePaint tp=siTemp.getTexturePaint();
                                        destination.setPaint(tp);
                                        destination.fill(siTemp.getShape());
                                }
                                else if(siTemp.getFillColor() != null)
                                {
                                    destination.setColor(siTemp.getFillColor());
                                    if(siTemp.getShape() != null)
                                        destination.fill(siTemp.getShape());
                                }
                                if(siTemp.getLineColor() != null)
                                {
                                    destination.setColor(siTemp.getLineColor());

                                    Point2D point = null;
                                    if(siTemp.getShape() != null)
                                        destination.draw(siTemp.getShape());
                                    else if(siTemp.getGlyphVector() != null)
                                    {
                                        point = siTemp.getGlyphPosition();
                                        destination.drawGlyphVector(siTemp.getGlyphVector(), (float)point.getX(), (float)point.getY());
                                    }
                                    else if(siTemp.getTextLayout() != null)
                                    {
                                        point = siTemp.getGlyphPosition();
                                        siTemp.getTextLayout().draw(destination, (float)point.getX(), (float)point.getY());
                                    }
                                }

                                destination.setTransform(oldTransform);
                                destination.setStroke(oldStroke);
                            }

                            //draw modifiers
                            shapes = symbol.getModifierShapes();

                            if(shapes != null)
                            {
                                int outlineWidth = RendererSettings.getInstance().getTextOutlineWidth()+2;
                                
                                for(int i = 0; i < shapes.size(); i++)
                                {
                                    siTemp = shapes.get(i);
                                    if(siTemp.getShapeType() != ShapeInfo.SHAPE_TYPE_MODIFIER_FILL)
                                    {
                                        if(siTemp.getAffineTransform() != null)
                                        {
                                            AffineTransform atTemp = (AffineTransform)siTemp.getAffineTransform().clone();
                                            atTemp.preConcatenate(AffineTransform.getTranslateInstance(offsetX, offsetY));
                                            destination.setTransform(atTemp);
                                             //destination.setTransform(siTemp.getAffineTransform());
                                        }
                                        else//*/
                                            destination.translate(offsetX, offsetY);

                                        if(siTemp.getStroke() != null)
                                            destination.setStroke(siTemp.getStroke());

                                        if(siTemp.getFillColor() != null)
                                        {
                                            destination.setColor(siTemp.getFillColor());
                                            if(siTemp.getShape() != null)
                                                destination.fill(siTemp.getShape());
                                            else//for deutch.  needs to set line color for text
                                            {
                                                siTemp.setLineColor(siTemp.getFillColor());
                                                siTemp.setFillColor(null);
                                            }

                                        }
                                        if(siTemp.getLineColor() != null)//change to else if when Deutch fixes above
                                        {
                                            destination.setColor(siTemp.getLineColor());

                                            Point2D point = null;
                                            if(siTemp.getShape() != null)
                                                destination.draw(siTemp.getShape());
                                            else if(siTemp.getGlyphVector() != null)
                                            {
                                                point = siTemp.getGlyphPosition();
                                                destination.drawGlyphVector(siTemp.getGlyphVector(), (float)point.getX(), (float)point.getY());
                                            }
                                            else if(siTemp.getTextLayout() != null)
                                            {
                                                point = siTemp.getGlyphPosition();
                                                //siTemp.getTextLayout().draw(destination, (float)point.getX()+offsetX, (float)point.getY()+offsetY);
                                                //Shape outline = siTemp.getTextLayout().getOutline(AffineTransform.getTranslateInstance(point.getX(), point.getY()));
                                                Shape outline = siTemp.getTextLayout().getOutline(AffineTransform.getTranslateInstance(0 , 0));
                                                destination.setStroke(new BasicStroke(outlineWidth));
                                                destination.setColor(SymbolDraw.getIdealTextBackgroundColor(symbol.getLineColor()));
                                                //destination.setColor(Color.BLUE);
                                                destination.draw(outline);
                                                destination.setColor(symbol.getLineColor());
                                                //destination.setColor(Color.RED);
                                                destination.fill(outline);
                                            }
                                        }
                                    }
                                    
                                    destination.setTransform(oldTransform);
                                }
                            }
                        }
                    //}
                }

                destination.setTransform(oldTransform);
                destination.setStroke(oldStroke);

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
                loggy.logp(Level.INFO, "SymbolDraw", "Draw()", "draw failure", re);
                throw re;

            }

        }
        catch(Exception exc)
        {
            RendererException re2 = new RendererException("Draw Operation Failed", exc);
            loggy.logp(Level.INFO, "SymbolDraw", "Draw()", "draw failure", re2);
            throw re2;
        }
    }
    
    /**
     * 
     * @param modifiers
     * @return 
     */
    public static ArrayList<ShapeInfo> ProcessModifierBackgrounds(ArrayList<ShapeInfo> modifiers)
    {
        ArrayList<ShapeInfo> alTemp = modifiers;
        ArrayList<ShapeInfo> modifierShapes = new ArrayList<ShapeInfo>();
        ShapeInfo temp = null;
        ShapeInfo outline = null;
        ShapeInfo background = null;
        //PROCESS shapes and add backgrounds if needed
            if(alTemp != null)
            {
                int tempSize = alTemp.size();
                for(int i = 0; i < tempSize; i++)
                {
                    temp = alTemp.get(i);
                    if(temp != null)
                    {
                        if(RendererSettings.getInstance().getTextBackgroundMethod() == RendererSettings.TextBackgroundMethod_COLORFILL)
                        {
                            background = SymbolDraw.createTextBackgroundFill(temp);
                            if(background != null)
                                modifierShapes.add(background);
                        }
                        else if(RendererSettings.getInstance().getTextBackgroundMethod() == RendererSettings.TextBackgroundMethod_OUTLINE)
                        {
                            outline = SymbolDraw.createTextOutline(temp);
                            if(outline != null)
                                modifierShapes.add(outline);
                        }
                        else if(RendererSettings.getInstance().getTextBackgroundMethod() == RendererSettings.TextBackgroundMethod_OUTLINE_QUICK)
                        {
                            Collection<ShapeInfo> outlines = SymbolDraw.createTextOutlineQuick(temp);
                            if(outlines != null)
                                modifierShapes.addAll(outlines);
                        }
                        modifierShapes.add(temp);
                    }
                }
            }//end if(alTemp != null)

        return modifierShapes;
    }

    /**
     * Creates an outline of a text shape for better readability
     * @param originalText
     * @return
     */
    public static ShapeInfo createTextOutline(ShapeInfo originalText)
    {
        return createTextOutline(originalText, RendererSettings.getInstance().getTextOutlineWidth());
    }

     /**
     * Creates an outline of a text shape for better readability
     * @param originalText 
     * @param outlineSize desired width of the outline. Overrides the value in
      * the RendererSettings object.
     * @return
     */
    public static ShapeInfo createTextOutline(ShapeInfo originalText, int outlineSize)
    {
        Shape outline = null;
            ShapeInfo siOutline = null;

        //int outlineSize = RendererSettings.getInstance().getTextOutlineWidth();

        outlineSize = Math.abs(outlineSize);

        Color textColor = null;

        try
        {
            if(originalText.getShape() != null)
                outline = new GeneralPath(originalText.getShape());
            else if(originalText.getTextLayout() != null)
            {
                outline = originalText.getTextLayout().getOutline(AffineTransform.getTranslateInstance(originalText.getGlyphPosition().getX(), originalText.getGlyphPosition().getY()));
            }
            siOutline = new ShapeInfo(outline);

            if(originalText.getFillColor() != null)
                    textColor = originalText.getFillColor();    //shape
            else if(originalText.getLineColor() != null)        //vs
                textColor = originalText.getLineColor();        //textlayout

//            if(textColor.getRed() == 255 &&
//                    textColor.getGreen() == 255 &&
//                    textColor.getBlue() == 255)
//                siOutline.setLineColor(Color.BLACK);
//            else
//                siOutline.setLineColor(Color.WHITE);

            if(originalText.getAffineTransform() != null)
                siOutline.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));

            if(originalText.getTextBackgroundColor() != null)
            {
                siOutline.setLineColor(originalText.getTextBackgroundColor());
            }
            else
                siOutline.setLineColor(getIdealTextBackgroundColor(textColor));

            //siOutline.setStroke(new BasicStroke(2));
            siOutline.setStroke(new BasicStroke(outlineSize,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3));
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("SymbolDraw", "createTextOuline", exc);
        }
        return siOutline;
    }

    /**
     * Creates an outline of a text shape for better readability. Width is only 1
     * but it is faster because instead of tracing an outline, we duplicate the
     * text 4x and shift each one in a different direction.  Assumes text layout
     * is being used in the shape info.
     * @param originalText
     * @return
     */
    public static ArrayList<ShapeInfo> createTextOutlineQuick(ShapeInfo originalText)
    {
        int thickness = RendererSettings.getInstance().getTextOutlineWidth();
        return createTextOutlineQuick(originalText, thickness);
    }

     /**
     * Creates an outline of a text shape for better readability. It is faster
     * because instead of tracing an outline, we duplicate the
     * text 4x and shift each one in a different direction.
     * Only works when TextRenderMethod is set to NATIVE
     * @param originalText
     * @param thickness - how thick the outline should be
     * @return
     */
    public static ArrayList<ShapeInfo> createTextOutlineQuick(ShapeInfo originalText, int thickness)
    {

        ShapeInfo siOutline1 = null;
        ShapeInfo siOutline2 = null;
        ShapeInfo siOutline3 = null;
        ShapeInfo siOutline4 = null;
        AffineTransform afx1 = null;
        AffineTransform afx2 = null;
        AffineTransform afx3 = null;
        AffineTransform afx4 = null;
        ArrayList<ShapeInfo> outlineShapes = null;
        //int outlineSize = RendererSettings.getInstance().getTextOutlineWidth();

        Color textColor = null;
        Color backgroundColor = null;

        try
        {
            int offset = 0;

            if(originalText.getTextLayout() != null)
            {
                textColor = originalText.getLineColor();        //textlayout
                if(originalText.getTextBackgroundColor() != null)
                {
                    backgroundColor = originalText.getTextBackgroundColor();
                }
                else
                    backgroundColor = getIdealTextBackgroundColor(textColor);
                
                outlineShapes = new ArrayList<ShapeInfo>();

                for(int i = 1; i <= thickness; i++)
                {

                    offset = i;

                    Point2D textPosition = null;
                    if(originalText.getModifierStringPosition()!= null)
                        textPosition = new Point2D.Double(originalText.getModifierStringPosition().getX(),originalText.getModifierStringPosition().getY());
                    else
                        textPosition = new Point2D.Double(0, 0);


                    if(i%2 != 0)
                    {
                        siOutline1 = new ShapeInfo(originalText.getTextLayout(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()-offset));
                        siOutline2 = new ShapeInfo(originalText.getTextLayout(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()-offset));
                        siOutline3 = new ShapeInfo(originalText.getTextLayout(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()+offset));
                        siOutline4 = new ShapeInfo(originalText.getTextLayout(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()+offset));
                    }
                    else
                    {
                        siOutline1 = new ShapeInfo(originalText.getTextLayout(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()));
                        siOutline2 = new ShapeInfo(originalText.getTextLayout(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()));
                        siOutline3 = new ShapeInfo(originalText.getTextLayout(), new Point2D.Double(textPosition.getX(), textPosition.getY()+offset));
                        siOutline4 = new ShapeInfo(originalText.getTextLayout(), new Point2D.Double(textPosition.getX(), textPosition.getY()-offset));
                    }


                    siOutline1.setLineColor(backgroundColor);
                    siOutline2.setLineColor(backgroundColor);
                    siOutline3.setLineColor(backgroundColor);
                    siOutline4.setLineColor(backgroundColor);

                    Stroke tempStroke = new BasicStroke(1,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3);
                    siOutline1.setStroke(tempStroke);
                    siOutline2.setStroke(tempStroke);
                    siOutline3.setStroke(tempStroke);
                    siOutline4.setStroke(tempStroke);

                    if(originalText.getAffineTransform() != null)
                    {
                        siOutline1.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));
                        siOutline2.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));
                        siOutline3.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));
                        siOutline4.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));
                    }

                    outlineShapes.add(siOutline1);
                    outlineShapes.add(siOutline2);
                    outlineShapes.add(siOutline3);
                    outlineShapes.add(siOutline4);

                }//end for
            }//end if
            else if(originalText.getGlyphVector() != null)
            {
                textColor = originalText.getLineColor();        //textlayout
                backgroundColor = getIdealTextBackgroundColor(textColor);
                outlineShapes = new ArrayList<ShapeInfo>();

                for(int j = 1; j <= thickness; j++)
                {

                    offset = j;

                    Point2D textPosition = new Point2D.Double(originalText.getGlyphPosition().getX(),originalText.getGlyphPosition().getY());

                    if(j%2 != 0)
                    {
                        siOutline1 = new ShapeInfo(originalText.getGlyphVector(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()-offset));
                        siOutline2 = new ShapeInfo(originalText.getGlyphVector(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()-offset));
                        siOutline3 = new ShapeInfo(originalText.getGlyphVector(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()+offset));
                        siOutline4 = new ShapeInfo(originalText.getGlyphVector(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()+offset));
                    }
                    else
                    {
                        siOutline1 = new ShapeInfo(originalText.getGlyphVector(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()));
                        siOutline2 = new ShapeInfo(originalText.getGlyphVector(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()));
                        siOutline3 = new ShapeInfo(originalText.getGlyphVector(), new Point2D.Double(textPosition.getX(), textPosition.getY()+offset));
                        siOutline4 = new ShapeInfo(originalText.getGlyphVector(), new Point2D.Double(textPosition.getX(), textPosition.getY()-offset));
                    }


                    siOutline1.setLineColor(backgroundColor);
                    siOutline2.setLineColor(backgroundColor);
                    siOutline3.setLineColor(backgroundColor);
                    siOutline4.setLineColor(backgroundColor);

                    Stroke tempStroke = new BasicStroke(1,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3);
                    siOutline1.setStroke(tempStroke);
                    siOutline2.setStroke(tempStroke);
                    siOutline3.setStroke(tempStroke);
                    siOutline4.setStroke(tempStroke);

                    if(originalText.getAffineTransform() != null)
                    {
                        siOutline1.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));
                        siOutline2.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));
                        siOutline3.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));
                        siOutline4.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));
                    }

                    outlineShapes.add(siOutline1);
                    outlineShapes.add(siOutline2);
                    outlineShapes.add(siOutline3);
                    outlineShapes.add(siOutline4);

                }//end for

            }
            else if(originalText.getShape() != null)
            {
                textColor = originalText.getLineColor();        //textlayout
                backgroundColor = getIdealTextBackgroundColor(textColor);
                outlineShapes = new ArrayList<ShapeInfo>();

                for(int k = 1; k <= thickness; k++)
                {

                    offset = k;


                    siOutline1 = new ShapeInfo(originalText.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline2 = new ShapeInfo(originalText.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline3 = new ShapeInfo(originalText.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline4 = new ShapeInfo(originalText.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);

                    siOutline1.setLineColor(backgroundColor);
                    siOutline2.setLineColor(backgroundColor);
                    siOutline3.setLineColor(backgroundColor);
                    siOutline4.setLineColor(backgroundColor);

                    Stroke tempStroke = new BasicStroke(1,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3);
                    siOutline1.setStroke(tempStroke);
                    siOutline2.setStroke(tempStroke);
                    siOutline3.setStroke(tempStroke);
                    siOutline4.setStroke(tempStroke);

                    
                    
                    if(originalText.getAffineTransform() == null)
                    {
                        afx1 = new AffineTransform();
                        afx2 = new AffineTransform();
                        afx3 = new AffineTransform();
                        afx4 = new AffineTransform();
                    }
                    else
                    {
                        afx1 = new AffineTransform(originalText.getAffineTransform());
                        afx2 = new AffineTransform(originalText.getAffineTransform());
                        afx3 = new AffineTransform(originalText.getAffineTransform());
                        afx4 = new AffineTransform(originalText.getAffineTransform());
                    }
                    
                    if(k%2 != 0)
                    {
                        afx1.translate(-offset, -offset);
                        afx2.translate(+offset, -offset);
                        afx3.translate(-offset, +offset);
                        afx4.translate(+offset, +offset);
                    }
                    else
                    {
                        afx1.translate(-offset, 0);
                        afx2.translate(+offset, 0);
                        afx3.translate(0, +offset);
                        afx4.translate(0, -offset);
                    }
                    
                    siOutline1.setAffineTransform(afx1);
                    siOutline2.setAffineTransform(afx2);
                    siOutline3.setAffineTransform(afx3);
                    siOutline4.setAffineTransform(afx4);
                    
                    outlineShapes.add(siOutline1);
                    outlineShapes.add(siOutline2);
                    outlineShapes.add(siOutline3);
                    outlineShapes.add(siOutline4);

                }//end for

            }
            else
            {
                String message = "ShapeInfo wasn't a TextLayout or a GlyphVector, returning null";
                ErrorLogger.LogMessage("SymbolDraw", "createTextOutlineQuick()", message, Level.FINEST);
                return null;
            }

            return outlineShapes;
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("SymbolDraw", "createTextOuline", exc);
        }
        return null;
    }
    
    /**
     * Creates an outline for single point graphics
     * @param symbolFrame
     * @param thickness
     * @return 
     */
    public static ArrayList<ShapeInfo> createSinglePointOutline(ShapeInfo symbolFrame, int thickness, Color outlineColor)
    {
        ShapeInfo siOutline1 = null;
        ShapeInfo siOutline2 = null;
        ShapeInfo siOutline3 = null;
        ShapeInfo siOutline4 = null;
        ShapeInfo siOutline5 = null;
        ShapeInfo siOutline6 = null;
        ShapeInfo siOutline7 = null;
        ShapeInfo siOutline8 = null;
        AffineTransform afx1 = null;
        AffineTransform afx2 = null;
        AffineTransform afx3 = null;
        AffineTransform afx4 = null;
        AffineTransform afx5 = null;
        AffineTransform afx6 = null;
        AffineTransform afx7 = null;
        AffineTransform afx8 = null;
        ArrayList<ShapeInfo> outlineShapes = null;
        //int outlineSize = RendererSettings.getInstance().getTextOutlineWidth();

        Color lineColor = null;
        Color backgroundColor = null;

        try
        {
            int offset = 0;
          
            if(outlineColor==null)
            {
                backgroundColor = getIdealTextBackgroundColor(lineColor);
            }
            else
            {
                backgroundColor = outlineColor;
            }
            outlineShapes = new ArrayList<ShapeInfo>();

            if(symbolFrame.getTextLayout() != null)
            {
                outlineShapes = new ArrayList<ShapeInfo>();

                for(int i = 1; i <= thickness; i++)
                {

                    offset = i;

                    Point2D textPosition = null;
                    if(symbolFrame.getModifierStringPosition()!= null)
                        textPosition = new Point2D.Double(symbolFrame.getModifierStringPosition().getX(),symbolFrame.getModifierStringPosition().getY());
                    else
                        textPosition = new Point2D.Double(0, 0);

                    
                    siOutline1 = new ShapeInfo(symbolFrame.getTextLayout(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()-offset));
                    siOutline2 = new ShapeInfo(symbolFrame.getTextLayout(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()-offset));
                    siOutline3 = new ShapeInfo(symbolFrame.getTextLayout(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()+offset));
                    siOutline4 = new ShapeInfo(symbolFrame.getTextLayout(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()+offset));
                    siOutline5 = new ShapeInfo(symbolFrame.getTextLayout(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()));
                    siOutline6 = new ShapeInfo(symbolFrame.getTextLayout(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()));
                    siOutline7 = new ShapeInfo(symbolFrame.getTextLayout(), new Point2D.Double(textPosition.getX(), textPosition.getY()+offset));
                    siOutline8 = new ShapeInfo(symbolFrame.getTextLayout(), new Point2D.Double(textPosition.getX(), textPosition.getY()-offset));

                    
                    siOutline1.setLineColor(backgroundColor);
                    siOutline2.setLineColor(backgroundColor);
                    siOutline3.setLineColor(backgroundColor);
                    siOutline4.setLineColor(backgroundColor);
                    siOutline5.setLineColor(backgroundColor);
                    siOutline6.setLineColor(backgroundColor);
                    siOutline7.setLineColor(backgroundColor);
                    siOutline8.setLineColor(backgroundColor);

                    Stroke tempStroke = new BasicStroke(1,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3);
                    siOutline1.setStroke(tempStroke);
                    siOutline2.setStroke(tempStroke);
                    siOutline3.setStroke(tempStroke);
                    siOutline4.setStroke(tempStroke);
                    siOutline5.setStroke(tempStroke);
                    siOutline6.setStroke(tempStroke);
                    siOutline7.setStroke(tempStroke);
                    siOutline8.setStroke(tempStroke);

                    if(symbolFrame.getAffineTransform() != null)
                    {
                        siOutline1.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline2.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline3.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline4.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline5.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline6.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline7.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline8.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                    }

                    outlineShapes.add(siOutline1);
                    outlineShapes.add(siOutline2);
                    outlineShapes.add(siOutline3);
                    outlineShapes.add(siOutline4);
                    outlineShapes.add(siOutline5);
                    outlineShapes.add(siOutline6);
                    outlineShapes.add(siOutline7);
                    outlineShapes.add(siOutline8);

                }//end for
            }//end if
            else if(symbolFrame.getGlyphVector() != null)
            {
                outlineShapes = new ArrayList<ShapeInfo>();

                for(int j = 1; j <= thickness; j++)
                {

                    offset = j;

                    Point2D textPosition = new Point2D.Double(symbolFrame.getGlyphPosition().getX(),symbolFrame.getGlyphPosition().getY());

                  
                    siOutline1 = new ShapeInfo(symbolFrame.getGlyphVector(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()-offset));
                    siOutline2 = new ShapeInfo(symbolFrame.getGlyphVector(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()-offset));
                    siOutline3 = new ShapeInfo(symbolFrame.getGlyphVector(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()+offset));
                    siOutline4 = new ShapeInfo(symbolFrame.getGlyphVector(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()+offset));
                    siOutline5 = new ShapeInfo(symbolFrame.getGlyphVector(), new Point2D.Double(textPosition.getX()-offset, textPosition.getY()));
                    siOutline6 = new ShapeInfo(symbolFrame.getGlyphVector(), new Point2D.Double(textPosition.getX()+offset, textPosition.getY()));
                    siOutline7 = new ShapeInfo(symbolFrame.getGlyphVector(), new Point2D.Double(textPosition.getX(), textPosition.getY()+offset));
                    siOutline8 = new ShapeInfo(symbolFrame.getGlyphVector(), new Point2D.Double(textPosition.getX(), textPosition.getY()-offset));
                    

                    if(symbolFrame.getShapeType()==ShapeInfo.SHAPE_TYPE_TG_SP_FRAME)
                    {
                        siOutline1.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_OUTLINE);
                        siOutline2.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_OUTLINE);
                        siOutline3.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_OUTLINE);
                        siOutline4.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_OUTLINE);
                        siOutline5.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_OUTLINE);
                        siOutline6.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_OUTLINE);
                        siOutline7.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_OUTLINE);
                        siOutline8.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_OUTLINE);
                    }

                    siOutline1.setLineColor(backgroundColor);
                    siOutline2.setLineColor(backgroundColor);
                    siOutline3.setLineColor(backgroundColor);
                    siOutline4.setLineColor(backgroundColor);
                    siOutline5.setLineColor(backgroundColor);
                    siOutline6.setLineColor(backgroundColor);
                    siOutline7.setLineColor(backgroundColor);
                    siOutline8.setLineColor(backgroundColor);

                    Stroke tempStroke = new BasicStroke(1,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3);
                    siOutline1.setStroke(tempStroke);
                    siOutline2.setStroke(tempStroke);
                    siOutline3.setStroke(tempStroke);
                    siOutline4.setStroke(tempStroke);
                    siOutline5.setStroke(tempStroke);
                    siOutline6.setStroke(tempStroke);
                    siOutline7.setStroke(tempStroke);
                    siOutline8.setStroke(tempStroke);

                    if(symbolFrame.getAffineTransform() != null)
                    {
                        siOutline1.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline2.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline3.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline4.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline5.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline6.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline7.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                        siOutline8.setAffineTransform(new AffineTransform(symbolFrame.getAffineTransform()));
                    }

                    outlineShapes.add(siOutline1);
                    outlineShapes.add(siOutline2);
                    outlineShapes.add(siOutline3);
                    outlineShapes.add(siOutline4);
                    outlineShapes.add(siOutline5);
                    outlineShapes.add(siOutline6);
                    outlineShapes.add(siOutline7);
                    outlineShapes.add(siOutline8);

                }//end for

            }
            else if(symbolFrame.getShape() != null)
            {
                outlineShapes = new ArrayList<ShapeInfo>();

                for(int k = 1; k <= thickness; k++)
                {

                    offset = k;


                    siOutline1 = new ShapeInfo(symbolFrame.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline2 = new ShapeInfo(symbolFrame.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline3 = new ShapeInfo(symbolFrame.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline4 = new ShapeInfo(symbolFrame.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline5 = new ShapeInfo(symbolFrame.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline6 = new ShapeInfo(symbolFrame.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline7 = new ShapeInfo(symbolFrame.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);
                    siOutline8 = new ShapeInfo(symbolFrame.getShape(), ShapeInfo.SHAPE_TYPE_SINGLE_POINT_OUTLINE);

                    siOutline1.setLineColor(backgroundColor);
                    siOutline2.setLineColor(backgroundColor);
                    siOutline3.setLineColor(backgroundColor);
                    siOutline4.setLineColor(backgroundColor);
                    siOutline5.setLineColor(backgroundColor);
                    siOutline6.setLineColor(backgroundColor);
                    siOutline7.setLineColor(backgroundColor);
                    siOutline8.setLineColor(backgroundColor);

                    Stroke tempStroke = new BasicStroke(1,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3);
                    siOutline1.setStroke(tempStroke);
                    siOutline2.setStroke(tempStroke);
                    siOutline3.setStroke(tempStroke);
                    siOutline4.setStroke(tempStroke);
                    siOutline5.setStroke(tempStroke);
                    siOutline6.setStroke(tempStroke);
                    siOutline7.setStroke(tempStroke);
                    siOutline8.setStroke(tempStroke);

                    
                    
                    if(symbolFrame.getAffineTransform() == null)
                    {
                        afx1 = new AffineTransform();
                        afx2 = new AffineTransform();
                        afx3 = new AffineTransform();
                        afx4 = new AffineTransform();
                        afx5 = new AffineTransform();
                        afx6 = new AffineTransform();
                        afx7 = new AffineTransform();
                        afx8 = new AffineTransform();
                    }
                    else
                    {
                        afx1 = new AffineTransform(symbolFrame.getAffineTransform());
                        afx2 = new AffineTransform(symbolFrame.getAffineTransform());
                        afx3 = new AffineTransform(symbolFrame.getAffineTransform());
                        afx4 = new AffineTransform(symbolFrame.getAffineTransform());
                        afx5 = new AffineTransform(symbolFrame.getAffineTransform());
                        afx6 = new AffineTransform(symbolFrame.getAffineTransform());
                        afx7 = new AffineTransform(symbolFrame.getAffineTransform());
                        afx8 = new AffineTransform(symbolFrame.getAffineTransform());
                    }
                    
                    afx1.translate(-offset, -offset);
                    afx2.translate(+offset, -offset);
                    afx3.translate(-offset, +offset);
                    afx4.translate(+offset, +offset);   
                    afx5.translate(-offset, 0);
                    afx6.translate(+offset, 0);
                    afx7.translate(0, +offset);
                    afx8.translate(0, -offset);

                    
                    siOutline1.setAffineTransform(afx1);
                    siOutline2.setAffineTransform(afx2);
                    siOutline3.setAffineTransform(afx3);
                    siOutline4.setAffineTransform(afx4);
                    siOutline5.setAffineTransform(afx5);
                    siOutline6.setAffineTransform(afx6);
                    siOutline7.setAffineTransform(afx7);
                    siOutline8.setAffineTransform(afx8);
                    
                    outlineShapes.add(siOutline1);
                    outlineShapes.add(siOutline2);
                    outlineShapes.add(siOutline3);
                    outlineShapes.add(siOutline4);
                    outlineShapes.add(siOutline5);
                    outlineShapes.add(siOutline6);
                    outlineShapes.add(siOutline7);
                    outlineShapes.add(siOutline8);

                }//end for

            }
            else
            {
                String message = "ShapeInfo wasn't a TextLayout or a GlyphVector, returning null";
                ErrorLogger.LogMessage("SymbolDraw", "createTextOutlineQuick()", message, Level.FINEST);
                return null;
            }

            return outlineShapes;
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("SymbolDraw", "createTextOuline", exc);
        }
        return null;
    }

    /**
     * Creates a filled rectangle backdrop for the text
     * @param originalText
     * @return
     */
    public static ShapeInfo createTextBackgroundFill(ShapeInfo originalText)
    {
        Rectangle tempRect = null;
        ShapeInfo background = null;
        ShapeInfo returnVal = null;
        try
        {
            //tempRect = temp.getBounds();
            tempRect = originalText.getTextLayout().getPixelBounds(null, (float)originalText.getGlyphPosition().getX(), (float)originalText.getGlyphPosition().getY());
            //tempRect.setRect(temp.getTextLayout().getBounds());
            background = new ShapeInfo(new Rectangle(tempRect.x - 2, tempRect.y - 2, tempRect.width + 4, tempRect.height + 4));
            if(originalText.getTextBackgroundColor() != null)
            {
                background.setFillColor(originalText.getTextBackgroundColor());
            }
            else if(RendererSettings.getInstance().getLabelBackgroundColor() != null)
            {
                background.setFillColor(RendererSettings.getInstance().getLabelBackgroundColor());
            }
            else
            {
                Color bgColor = null;
                if(originalText.getLineColor() != null)
                    bgColor = getIdealTextBackgroundColor(originalText.getLineColor());
                else if(originalText.getFillColor() != null)
                    bgColor = getIdealTextBackgroundColor(originalText.getFillColor());
                else
                    bgColor = Color.white;

                background.setFillColor(bgColor);
            }

            if(originalText.getAffineTransform() != null)
                background.setAffineTransform(new AffineTransform(originalText.getAffineTransform()));
            returnVal = background;
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("SymbolDraw", "CreateTextBackgroundFill", exc);
        }
        return returnVal;
    }

    /**
     *
     * @param text
     * @param modifierValue
     * @param x
     * @param y
     * @return
     */
    public static ShapeInfo CreateModifierShapeInfo(TextLayout text, String modifierValue, double x, double y)
    {
        return CreateModifierShapeInfo(text, modifierValue, x, y, Color.BLACK, null);
    }
    
    /**
     * 
     * @param text
     * @param modifierValue
     * @param x
     * @param y
     * @param textColor
     * @deprecated 
     * @return 
     */
    public static ShapeInfo CreateModifierShapeInfo(TextLayout text, String modifierValue, double x, double y, Color textColor)
    {
        return CreateModifierShapeInfo(text, modifierValue, x, y, textColor, null);
    }

    /**
     * 
     * @param text
     * @param modifierValue
     * @param x
     * @param y
     * @param textColor Null == Black
     * @param textBackgroundColor null == renderer decides
     * @return 
     */
    public static ShapeInfo CreateModifierShapeInfo(TextLayout text, String modifierValue, double x, double y, Color textColor, Color textBackgroundColor)
    {
        try
        {
            ShapeInfo si = new ShapeInfo(text, new Point((int)x, (int)y));
            //returnVal.setLineColor(textColor);

            if(textColor == null)
                textColor = Color.BLACK;

            int textRenderMethod = RendererSettings.getInstance().getTextRenderMethod();

            AffineTransform at = null;//new AffineTransform();

            if(textRenderMethod == RendererSettings.RenderMethod_SHAPES)
            {
                at = new AffineTransform();
                at.translate(x, y);
                Shape label = text.getOutline(at);
                si = new ShapeInfo(label);
                si.setFillColor(textColor);
            }
            else if(textRenderMethod == RendererSettings.RenderMethod_NATIVE)
            {
                si = new ShapeInfo(text, new Point((int)x, (int)y));
                si.setLineColor(textColor);
            }

            //for World Wind which just takes a string and x,y.
            si.setModifierString(modifierValue);
            si.setModifierStringPosition(new Point((int)x, (int)y));

            si.setStroke(new BasicStroke(0,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 3));
            si.setShapeType(ShapeInfo.SHAPE_TYPE_UNIT_DISPLAY_MODIFIER);
            
            if(textBackgroundColor != null)
                si.setTextBackgroundColor(textBackgroundColor);

            return si;
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("JavaRenderer", "CreateModifierShapeInfo", exc);
            return null;
        }
    }

    /**
     *
     * @param fgColor
     * @return
     */
    public static Color getIdealTextBackgroundColor(Color fgColor)
    {
        //ErrorLogger.LogMessage("SymbolDraw","getIdealtextBGColor", "in function", Level.SEVERE);
        try
        {
            //an array of three elements containing the
            //hue, saturation, and brightness (in that order),
            //of the color with the indicated red, green, and blue components/
            float hsbvals[] = new float[3];

            if(fgColor != null)
            {/*
                Color.RGBtoHSB(fgColor.getRed(), fgColor.getGreen(), fgColor.getBlue(), hsbvals);

                if(hsbvals != null)
                {
                    //ErrorLogger.LogMessage("SymbolDraw","getIdealtextBGColor", "length: " + String.valueOf(hsbvals.length));
                    //ErrorLogger.LogMessage("SymbolDraw","getIdealtextBGColor", "H: " + String.valueOf(hsbvals[0]) + " S: " + String.valueOf(hsbvals[1]) + " B: " + String.valueOf(hsbvals[2]),Level.SEVERE);
                    if(hsbvals[2] > 0.6)
                        return Color.BLACK;
                    else
                        return Color.WHITE;
                }*/

                int nThreshold = RendererSettings.getInstance().getTextBackgroundAutoColorThreshold();//160;
                int bgDelta = (int)((fgColor.getRed() * 0.299) + (fgColor.getGreen() * 0.587) + (fgColor.getBlue() * 0.114));
                //ErrorLogger.LogMessage("bgDelta: " + String.valueOf(255-bgDelta));
                //if less than threshold, black, otherwise white.
                //return (255 - bgDelta < nThreshold) ? Color.BLACK : Color.WHITE;//new Color(0, 0, 0, fgColor.getAlpha())
                return (255 - bgDelta < nThreshold) ? new Color(0, 0, 0, fgColor.getAlpha()) : new Color(255, 255, 255, fgColor.getAlpha());
            }
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("SymbolDraw", "getIdealtextBGColor", exc);
        }
        return Color.WHITE;
    }

    /**
     * used for examining the shapes in a shapeinfo object
     * (currently only designed for those based on Path2D)
     * @param shapes
     * @return
     */
    public static String ShapeInfosToString(ArrayList<ShapeInfo> shapes)
    {
        StringBuilder sb = new StringBuilder();
        Shape tempShape = null;
        try
        {
            for(int i = 0; i < shapes.size(); i++)
            {
                tempShape = shapes.get(i).getShape();
                if(tempShape != null && tempShape instanceof GeneralPath)
                {
                    sb.append(GeneralPathToString((GeneralPath)tempShape));
                }
            }
        }
        catch(Exception exc)
        {

        }

        return sb.toString();
    }

    /**
     * traverse a General path and see how it's made.
     * @param path
     * @return
     */
    public static String GeneralPathToString(GeneralPath path)
    {
        StringBuilder sb = new StringBuilder();
        PathIterator itr = null;
        double[] coords = new double[6];
        int pathSegmentType = 0;
        String strPathSegmentType = "";
        try
        {
            itr = path.getPathIterator(null);
            while(itr.isDone()==false)
            {
                //itr.next();
                pathSegmentType = itr.currentSegment(coords);
                if(pathSegmentType == itr.SEG_MOVETO)
                    sb.append("SEG_MOVETO");
                else if(pathSegmentType == itr.SEG_CUBICTO)
                    sb.append("SEG_CUBICTO");
                else if(pathSegmentType == itr.SEG_LINETO)
                    sb.append("SEG_LINETO");
                else if(pathSegmentType == itr.SEG_QUADTO)
                    sb.append("SEG_QUADTO");
                else if(pathSegmentType == itr.SEG_CLOSE)
                    sb.append("SEG_CLOSE");

                sb.append(": ");

                if(pathSegmentType == itr.SEG_MOVETO)
                    sb.append(String.valueOf(coords[0]) + ", " + String.valueOf(coords[1]));
                else if(pathSegmentType == itr.SEG_CUBICTO)
                    sb.append(String.valueOf(coords[0]) + ", " + String.valueOf(coords[1]));
                else if(pathSegmentType == itr.SEG_LINETO)
                    sb.append(String.valueOf(coords[0]) + ", " + String.valueOf(coords[1]));
                else if(pathSegmentType == itr.SEG_QUADTO)
                    sb.append(String.valueOf(coords[0]) + ", " + String.valueOf(coords[1]));
                else if(pathSegmentType == itr.SEG_CLOSE)
                    sb.append(String.valueOf(coords[0]) + ", " + String.valueOf(coords[1]));

                sb.append('\n');
                
                coords = new double[6];
                itr.next();

            }

        }
        catch(Exception exc)
        {

        }

        return sb.toString();
    }
}
