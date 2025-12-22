/*
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sec.web.renderer;

import ArmyC2.C2SD.Utilities.ErrorLogger;
import ArmyC2.C2SD.Utilities.IPointConversion;
import ArmyC2.C2SD.Utilities.RendererSettings;
import ArmyC2.C2SD.Utilities.ShapeInfo;
import ArmyC2.C2SD.Utilities.SymbolDraw;
import ArmyC2.C2SD.Utilities.SymbolUtilities;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import sec.web.renderer.utilities.Path;
import sec.web.renderer.utilities.SVGTextInfo;


/**
 *
 */
public class MultiPointHandlerSVG {
    public static String GeoSVGize(String id, String name, String description, String symbolID, ArrayList<ShapeInfo> shapes, ArrayList<ShapeInfo> modifiers, IPointConversion ipc, boolean normalize, String textColor, String textBackgroundColor, boolean wasClipped) {
        return GeoSVGize(id, name, description, symbolID, shapes, modifiers, ipc, normalize, textColor, textBackgroundColor, wasClipped, null);
    }

    /**
     * Generates an SVG which can be draped on a map.
     * Better with RenderSymbol2D
     *
     * @param id
     * @param name
     * @param description
     * @param symbolID
     * @param shapes              {@link ShapeInfo[]}
     * @param modifiers           {@link ShapeInfo[]}
     * @param ipc                 {@link IPointConversion}
     * @param normalize
     * @param textColor
     * @param textBackgroundColor
     * @param wasClipped
     * @return
     */
    public static String GeoSVGize(String id, String name, String description, String symbolID, ArrayList<ShapeInfo> shapes, ArrayList<ShapeInfo> modifiers, IPointConversion ipc, boolean normalize, String textColor, String textBackgroundColor, boolean wasClipped, Rectangle bbox) {

        int height = 10;

        Rectangle tempBounds = null;
        ArrayList<String> paths = new ArrayList<>();
        Rectangle pathBounds = null;
        ArrayList<SVGTextInfo> labels = new ArrayList<>();
        Rectangle labelBounds = null;
        Rectangle unionBounds = null;
        float lineWidth;
        String fillTexture = null;
        Point2D geoCoordTL = null;
        Point2D geoCoordTR = null;
        Point2D geoCoordBL = null;
        Point2D geoCoordBR = null;
        Point2D west = null;
        Point2D north = null;
        Point2D south = null;
        Point2D east = null;
        int len = shapes.size();

        try {
            Font fontInfo = RendererSettings.getInstance().getMPLabelFont();
            height = fontInfo.getSize();


            for (int i = 0; i < len; i++) {
                tempBounds = new Rectangle();
                String svg = MultiPointHandlerSVG.ShapesToGeoSVG(symbolID, shapes.get(i), tempBounds, ipc, normalize);
                if (svg != null) {
                    BasicStroke bsTemp = (BasicStroke)shapes.get(i).getStroke();
                    lineWidth = bsTemp.getLineWidth();
                    tempBounds.grow(Math.round(lineWidth / 2), Math.round(lineWidth / 2));//adjust for line width so nothing gets clipped.
                    if (pathBounds == null)
                        pathBounds = (Rectangle) tempBounds.clone();
                    else
                        pathBounds = pathBounds.union(tempBounds);
                    paths.add(svg);

                    
                    if (shapes.get(i).getPatternFillImage() != null && fillTexture == null)
                        fillTexture = getFillPattern(shapes.get(i));
                }
            }

            ShapeInfo tempModifier;
            int len2 = modifiers.size();
            SVGTextInfo tiTemp = null;
            for (int j = 0; j < len2; j++) {
                tempModifier = modifiers.get(j);

                if (tempModifier.getModifierString() != null && !tempModifier.getModifierString().isEmpty()) {
                    Point2D tempLocation = tempModifier.getModifierStringPosition();

                    int justify = tempModifier.getTextJustify();
                    String strJustify = "start";
                    if (justify == ShapeInfo.justify_left)
                        strJustify = "start";
                    else if (justify == ShapeInfo.justify_center)
                        strJustify = "middle";
                    else if (justify == ShapeInfo.justify_right)
                        strJustify = "end";

                    double degrees = tempModifier.getModifierStringAngle();
                    tiTemp = new SVGTextInfo(tempModifier.getModifierString(), tempLocation, fontInfo, strJustify, degrees);

                    Rectangle bounds = tiTemp.getTextBounds().getBounds();

                    //make sure labels are in the bbox, otherwise they can
                    //make the canvas grow out of control.
                    //if (tiTemp && bbox.containsRectangle(bounds))
                    //if(bbox !== null)
                    if (tiTemp != null) {
                        if ((bbox != null && bbox.intersects(bounds)) || bbox == null) {
                            labels.add(tiTemp);
                            if (bounds != null) {
                                if (labelBounds != null)
                                    labelBounds = labelBounds.union(bounds);
                                else
                                    labelBounds = bounds;
                            }
                        }
                    }
                } 
                /* //Not implemented in 2525C renderer
                else if (tempModifier.getModifierImage() != null) 
                {
                    BufferedImage imgModifier = tempModifier.getModifierImage();
                    Rectangle bounds = new Rectangle(0, 0, imgModifier.getWidth(), imgModifier.getHeight());

                    Point2D tempLocation = tempModifier.getModifierStringPosition();
                    tempLocation.setLocation(tempLocation.getX() - bounds.getWidth() / 2, tempLocation.getY() - bounds.getHeight() / 2);
                    int x = (int) tempLocation.getX();
                    int y = (int) tempLocation.getY();
                    bounds.setLocation(x, y);

                    double angle = tempModifier.getModifierStringAngle();
                    paths.add("<image transform=\"translate(" + x + ',' + y + ") rotate(" + angle + ")\" href=\"" + RendererUtilities.imgToBase64String(tempModifier.getModifierImage()) + "\" />");
                    if (angle != 0) {
                        Rectangle2D bounds2D = SVGTextInfo.getRotatedRectangleBounds(bounds, tempLocation, -angle, "middle");
                        bounds = bounds2D.getBounds();
                    }
                    if (bounds != null) {
                        if ((bbox != null && bbox.intersects(bounds)) || bbox == null) {
                            if (pathBounds != null)
                                pathBounds = pathBounds.union(bounds);
                            else
                                pathBounds = bounds;
                        }
                    }
                }//*/
            }
            if (pathBounds != null) {
                unionBounds = (Rectangle) pathBounds.clone();
            }
            if (labelBounds != null) {
                if (unionBounds != null) {
                    unionBounds = unionBounds.union(labelBounds);
                } else {
                    unionBounds = labelBounds;
                }
            }

            //get geo bounds for canvas

            if (unionBounds != null) {
                Point2D coordTL = new Point2D.Double();
                coordTL.setLocation(unionBounds.getX(), unionBounds.getY());
                Point2D coordBR = new Point2D.Double();
                coordBR.setLocation(unionBounds.getX() + unionBounds.getWidth(), unionBounds.getY() + unionBounds.getHeight());

                Point2D coordTR = new Point2D.Double();
                coordTR.setLocation(unionBounds.getX() + unionBounds.getWidth(), unionBounds.getY());
                Point2D coordBL = new Point2D.Double();
                coordBL.setLocation(unionBounds.getX(), unionBounds.getY() + unionBounds.getHeight());

                south = new Point2D.Double(unionBounds.getX() + unionBounds.getWidth() / 2, unionBounds.getY() + unionBounds.getHeight());
                north = new Point2D.Double(unionBounds.getX() + unionBounds.getWidth() / 2, unionBounds.getY());
                east = new Point2D.Double(unionBounds.getX() + unionBounds.getWidth(), unionBounds.getY() + unionBounds.getHeight() / 2);
                west = new Point2D.Double(unionBounds.getX(), unionBounds.getY() + unionBounds.getHeight() / 2);


                geoCoordTL = ipc.PixelsToGeo(coordTL);
                geoCoordBR = ipc.PixelsToGeo(coordBR);
                geoCoordTR = ipc.PixelsToGeo(coordTR);
                geoCoordBL = ipc.PixelsToGeo(coordBL);

                north = ipc.PixelsToGeo(north);
                south = ipc.PixelsToGeo(south);
                east = ipc.PixelsToGeo(east);
                west = ipc.PixelsToGeo(west);


                if (normalize) {
                    geoCoordTL = MultiPointHandler.NormalizeCoordToGECoord(geoCoordTL);
                    geoCoordBR = MultiPointHandler.NormalizeCoordToGECoord(geoCoordBR);
                    geoCoordTR = MultiPointHandler.NormalizeCoordToGECoord(geoCoordTR);
                    geoCoordBL = MultiPointHandler.NormalizeCoordToGECoord(geoCoordBL);

                    north = MultiPointHandler.NormalizeCoordToGECoord(north);
                    south = MultiPointHandler.NormalizeCoordToGECoord(south);
                    east = MultiPointHandler.NormalizeCoordToGECoord(east);
                    west = MultiPointHandler.NormalizeCoordToGECoord(west);
                }
            } else//nothing to draw
            {
                geoCoordTL = new Point2D.Double(0, 0);
                geoCoordBR = new Point2D.Double(0, 0);
                geoCoordTR = new Point2D.Double(0, 0);
                geoCoordBL = new Point2D.Double(0, 0);

                north = new Point2D.Double(0, 0);
                south = new Point2D.Double(0, 0);
                east = new Point2D.Double(0, 0);
                west = new Point2D.Double(0, 0);
            }
        } catch (Exception err) {
            ErrorLogger.LogException("MultiPointHandler", "GeoSVGize", err);
        }

        if (paths != null && len > 0 && unionBounds != null) {
            //create group with offset translation
            //ctx.translate(bounds.getX() * -1, bounds.getY() * -1);
            String group = "<g transform=\"translate(" + (unionBounds.getX() * -1) + ',' + (unionBounds.getY() * -1) + ")\">";

            //loop through paths and labels and build SVG.
            for (int i = 0; i < paths.size(); i++) {
                group += paths.get(i);
            }

            ArrayList<String> labelStrs = renderTextElement(labels, textColor, textBackgroundColor);
            for (int j = 0; j < labelStrs.size(); j++) {
                group += labelStrs.get(j);
            }
            //close
            group += "</g>";

            //wrap in SVG
            String geoSVG = "<svg width=\"" + Math.ceil(unionBounds.getWidth()) + "px\" height=\"" + Math.ceil(unionBounds.getHeight()) + "px\" preserveAspectRatio=\"none\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";

            geoSVG += ("<metadata>\n");
            geoSVG += ("<id>") + id + ("</id>\n");
            geoSVG += ("<name>") + name + ("</name>\n");
            geoSVG += ("<description>") + description + ("</description>\n");
            geoSVG += ("<symbolID>") + symbolID + ("</symbolID>\n");
            geoSVG += ("<geoTL>") + geoCoordTL.getX() + " " + geoCoordTL.getY() + ("</geoTL>\n");
            geoSVG += ("<geoBR>") + geoCoordBR.getX() + " " + geoCoordBR.getY() + ("</geoBR>\n");
            geoSVG += ("<geoTR>") + geoCoordTR.getX() + " " + geoCoordTR.getY() + ("</geoTR>\n");
            geoSVG += ("<geoBL>") + geoCoordBL.getX() + " " + geoCoordBL.getY() + ("</geoBL>\n");
            geoSVG += ("<north>") + north.getY() + ("</north>\n");
            geoSVG += ("<south>") + south.getY() + ("</south>\n");
            geoSVG += ("<east>") + east.getX() + ("</east>\n");
            geoSVG += ("<west>") + west.getX() + ("</west>\n");
            geoSVG += ("<wasClipped>") + wasClipped + ("</wasClipped>\n");
            geoSVG += ("<width>") + unionBounds.getWidth() + ("</width>\n");
            geoSVG += ("<height>") + unionBounds.getHeight() + ("</height>\n");
            geoSVG += ("</metadata>\n");


            /*//Scale the image, commented out as I decided to alter scale in getReasonableScale rather than adjust after the fact.
            var tempWidth = Math.ceil(unionBounds.getWidth());
            var tempHeight = Math.ceil(unionBounds.getHeight());
            var quality = 1.0;
            var bigger = Math.max(tempWidth, tempHeight);
            var max = 1000;
            if(!converter)
            {
                if(bigger < max)
                {
                    if(bigger * 2 < max)
                    {
                        quality = 2;
                    }
                    else
                    {
                        quality = max / bigger;
                    }
                }
                else
                {
                    quality = 1;
                }
            }
            var geoSVG = '<svg viewBox="0 0 ' + tempWidth + ' ' + tempHeight + '"' + ' width="' + (tempWidth * quality) + 'px" height="' + (tempHeight * quality) + 'px" preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg" version="1.1">';//*/
            if (fillTexture != null)
                geoSVG += fillTexture;
            geoSVG += group;
            geoSVG += "</svg>";//*/

            return geoSVG;

        } else {
            //return blank 2x2 SVG
            return "<svg width=\"2px\" height=\"2px\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"></svg>";
        }
    }

    /**
     * @param tiArray      {@link SVGTextInfo[]}
     * @param color        a hex string "#000000"
     * @param outlineColor a hex string "#000000"
     */
    static ArrayList<String> renderTextElement(ArrayList<SVGTextInfo> tiArray, String color, String outlineColor) {
        //ctx.lineCap = "butt";
        //ctx.lineJoin = "miter";
        //ctx.miterLimit = 3;
        /*ctx.lineCap = "round";
        ctx.lineJoin = "round";
        ctx.miterLimit = 3;*/
        ArrayList<String> svgElements = new ArrayList<>();

        int size = tiArray.size();
        SVGTextInfo tempShape = null;
        String textColor = "#000000";
        int tbm = RendererSettings.getInstance().getTextBackgroundMethod();
        int outlineWidth = RendererSettings.getInstance().getTextOutlineWidth();

        if (color != null) {
            textColor = color;
        }


        if (outlineColor == null || outlineColor.isEmpty()) {
            outlineColor = SymbolUtilities.colorToHexString(SymbolDraw.getIdealTextBackgroundColor(SymbolUtilities.getColorFromHexString(textColor)), false);
        }


        if (tbm == RendererSettings.TextBackgroundMethod_OUTLINE
                || tbm == RendererSettings.TextBackgroundMethod_OUTLINE_QUICK
                || tbm == RendererSettings.TextBackgroundMethod_COLORFILL) {
            for (int i = 0; i < size; i++) {
                tempShape = tiArray.get(i);
                svgElements.add(tempShape.toSVGElement(textColor, outlineColor, outlineWidth));
            }
        } else //if(tbm == RendererSettings.TextBackgroundMethod_NONE)
        {
            for (int j = 0; j < size; j++) {
                tempShape = tiArray.get(j);
                svgElements.add(tempShape.toSVGElement(textColor, null, 0));
            }
        }

        return svgElements;
    }

    /**
     * 
     * @param shapeInfo
     * @return 
     */
    static String getFillPattern(ShapeInfo shapeInfo) 
    {
        if (shapeInfo.getPatternFillImage() != null) {
            int width = shapeInfo.getPatternFillImage().getWidth();
            int height = shapeInfo.getPatternFillImage().getHeight();
            return "<defs><pattern id=\"fillPattern\" patternUnits=\"userSpaceOnUse\" width=\"" + width + "\" height=\"" + height + "\"><image href=\"" + imgToBase64String(shapeInfo.getPatternFillImage()) + "\" /></pattern></defs>";
        } else {
            return null;
        }

    }

    /**
     * @param symbolID
     * @param shapeInfo  {@link ShapeInfo}
     * @param pathBounds {@link Rectangle}
     * @param ipc        {@link IPointConversion}
     * @param normalize
     */
    static String ShapesToGeoSVG(String symbolID, ShapeInfo shapeInfo, Rectangle pathBounds, IPointConversion ipc, boolean normalize) {
        Path path = null;
        String fillColor = null;
        String lineColor = null;
        int lineWidth = 0;
        double lineAlpha = 1.0;
        double fillAlpha = 1.0;
        float[] dashArray = null;
        String fillPattern = null;

        if (shapeInfo.getLineColor() != null) {
            Color lineColorTemp = shapeInfo.getLineColor();
            lineAlpha = lineColorTemp.getAlpha() / 255.0;
            lineColor = SymbolUtilities.colorToHexString(lineColorTemp, false);
        }
        if (shapeInfo.getFillColor() != null) {
            Color fillColorTemp = shapeInfo.getFillColor();
            fillAlpha = fillColorTemp.getAlpha() / 255.0;
            fillColor = SymbolUtilities.colorToHexString(fillColorTemp, false);
        }

        BasicStroke stroke = (BasicStroke)shapeInfo.getStroke();
        if (stroke != null) {
            lineWidth = Math.round(stroke.getLineWidth());
            dashArray = stroke.getDashArray();
        }

        ArrayList<ArrayList<Point2D>> shapesArray = shapeInfo.getPolylines();
        path = new Path();
        if (dashArray != null && dashArray.length > 0)
            path.setLineDash(dashArray);
        for (int i = 0; i < shapesArray.size(); i++) {
            ArrayList<Point2D> shape = shapesArray.get(i);

            for (int j = 0; j < shape.size(); j++) {
                Point2D coord = shape.get(j);
                if (j == 0) {
                    path.moveTo(coord.getX(), coord.getY());
                } else if (dashArray != null) {
                    path.dashedLineTo(coord.getX(), coord.getY());
                } else {
                    path.lineTo(coord.getX(), coord.getY());
                }
            }
        }
        
        if (shapeInfo.getPatternFillImage() != null)
            fillColor = "url(#fillPattern)";
        
        String svgElement = path.toSVGElement(lineColor, lineWidth, fillColor, lineAlpha, fillAlpha);
        pathBounds.setBounds(path.getBounds().getBounds());
        return svgElement;
    }
    
    public static String imgToBase64String(BufferedImage img)
    {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try
        {
            // Base64 index table
            final char[] BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
            byte data[] = null;
            if(img != null)
            {
                ImageIO.write(img, "png", os);
                data = os.toByteArray();
            }
            if(data != null)
            {
                StringBuilder encoded = new StringBuilder((data.length * 4 + 2) / 3);

                int i = 0;
                while (i < data.length) {
                    // Take 3 bytes (24 bits) at a time
                    int b1 = data[i++] & 0xFF;
                    int b2 = (i < data.length) ? data[i++] & 0xFF : 0;
                    int b3 = (i < data.length) ? data[i++] & 0xFF : 0;

                    // Break into four 6-bit chunks
                    int chunk1 = b1 >>> 2;
                    int chunk2 = ((b1 & 0x03) << 4) | (b2 >>> 4);
                    int chunk3 = ((b2 & 0x0F) << 2) | (b3 >>> 6);
                    int chunk4 = b3 & 0x3F;

                    // Append Base64 characters
                    encoded.append(BASE64_CHARS[chunk1]);
                    encoded.append(BASE64_CHARS[chunk2]);
                    encoded.append(i - 1 < data.length ? BASE64_CHARS[chunk3] : '=');
                    encoded.append(i < data.length ? BASE64_CHARS[chunk4] : '=');
                }
                return "data:image/png;base64," + encoded.toString();
            }
            return "data:image/png;base64,";
            
        }
        catch(IOException ioe)
        {
            ErrorLogger.LogException("MultiPointHandlerSVG", "imgToBase64String", ioe);
            return "data:image/png;base64,";
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("MultiPointHandlerSVG", "imgToBase64String", exc);
            return "data:image/png;base64,";
        }
    }
}
