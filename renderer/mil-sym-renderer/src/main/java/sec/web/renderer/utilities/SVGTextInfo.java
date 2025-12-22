/*
 * Copyright 2025 Michael.P.Spinelli.
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
package sec.web.renderer.utilities;

import ArmyC2.C2SD.Utilities.RectUtilities;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public class SVGTextInfo {
    
    protected String _text;
    protected String _font;
    protected String _fontName;
    protected int _fontSize = 0;
    protected String _fontStyle;
    protected Point2D _location;
    protected Rectangle2D _bounds;
    private String justification = "start";
    private double angle = 0;
    private String alignmentBaseline = "middle";
    
        public SVGTextInfo(String text, Point2D position, Font font, String justification, double angle) {
        BufferedImage bi = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            FontRenderContext frc = g2d.getFontRenderContext();

        this._font = font.toString();
        this._fontName = font.getName();
        this._fontSize = font.getSize();
        if (font.isBold()) {
            this._fontStyle = font.isItalic() ? "bolditalic" : "bold";
        } else {
            this._fontStyle = font.isItalic() ? "italic" : "plain";
        }
        this._text = text;

        this._location = new Point2D.Double(position.getX(), position.getY());

        this.justification = justification;
        this.angle = angle;

        this._bounds = font.getStringBounds(text, frc);

        if (this.justification.equals("middle"))
            this._bounds.setRect(position.getX() - this._bounds.getWidth() / 2, position.getY() - this._bounds.getHeight() / 2, this._bounds.getWidth(), this._bounds.getHeight());
        else if (this.justification.equals("end"))
            this._bounds.setRect(position.getX() - this._bounds.getWidth(), position.getY() - this._bounds.getHeight() / 2, this._bounds.getWidth(), this._bounds.getHeight());
        else
            this._bounds.setRect(position.getX(), position.getY() - this._bounds.getHeight() / 2, this._bounds.getWidth(), this._bounds.getHeight());

        RectUtilities.grow(this._bounds, 1);

        if (this.angle != 0)
            this._bounds = SVGTextInfo.getRotatedRectangleBounds(this._bounds, position, this.angle, this.justification);
    }


    public Rectangle2D getTextBounds() {
        return this._bounds;
    }

    public String toSVGElement(String textColor, String outlineColor, double outlineWidth) {
        String fill = textColor;
        String stroke = outlineColor;
        double strokeWidth = outlineWidth;

        double x = this._location.getX();
        double y = this._location.getY();

        String se = "<text";
        if (this.angle == 0)
            se += " x=\"" + x + "\" y=\"" + y + "\"";
        else
            se += " transform=\"translate(" + x + ',' + y + ") rotate(" + this.angle + ")\"";
        se += " font-family=\"" + this._fontName + '"';
        se += " font-size=\"" + this._fontSize + "px\"";
        se += " font-weight=\"" + this._fontStyle + '"';
        se += " alignment-baseline=\"" + this.alignmentBaseline + "\"";
        se += " stroke-miterlimit=\"3\"";
        se += " text-anchor=\"" + justification + '"';

        /*if(this._angle)
        {
            se += ' transform="rotate(' + this._angle + ' ' + this._location.getX() + ' ' + this._location.getY() + ')"';
        }//*/

        String seStroke = null,
                seFill = null;

        String text = this._text;

        if (stroke != null && strokeWidth > 0) {

            seStroke = se + " stroke=\"" + stroke + '"';
            /*else
                seStroke = se + ' stroke="' + stroke.replace(/#/g,"&#35;") + '"';*/

            if (strokeWidth != 0)
                seStroke += " stroke-width=\"" + (strokeWidth + 2) + '"';
            seStroke += " fill=\"none\"";
            seStroke += ">";
            seStroke += text;
            seStroke += "</text>";
        }

        if (fill != null) {

            seFill = se + " fill=\"" + fill + '"';
            /*else
                seFill = se + ' fill="' + fill.replace(/#/g,"%23") + '"';*/
            seFill += ">";
            seFill += text;
            seFill += "</text>";
        }

        if (stroke != null && !stroke.isEmpty() && fill != null && !fill.isEmpty())
            se = seStroke + seFill;
        else if (fill != null && !fill.isEmpty())
            se = seFill;
        else
            se = "";
        return se;
    }

    public static Rectangle2D getRotatedRectangleBounds(Rectangle2D rectangle, Point2D pivotPt, double angle, String justification) {
        double textWidth = rectangle.getWidth();

        if (justification.equals("start"))
            rectangle.setRect(rectangle.getX() - textWidth / 2, rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        else if (justification.equals("end"))
            rectangle.setRect(rectangle.getX() + textWidth / 2, rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());

        Point2D ptTL = new Point2D.Double(rectangle.getMinX(), rectangle.getMinY());
        Point2D ptTR = new Point2D.Double(rectangle.getMaxX(), rectangle.getMinY());
        Point2D ptBL = new Point2D.Double(rectangle.getMinX(), rectangle.getMaxY());
        Point2D ptBR = new Point2D.Double(rectangle.getMaxX(), rectangle.getMaxY());

        SVGTextInfo.rotatePoint(ptTL, pivotPt, angle);
        SVGTextInfo.rotatePoint(ptTR, pivotPt, angle);
        SVGTextInfo.rotatePoint(ptBL, pivotPt, angle);
        SVGTextInfo.rotatePoint(ptBR, pivotPt, angle);

        rectangle = new Rectangle2D.Double(ptTL.getX(), ptTL.getY(), 0, 0);
        rectangle.add(ptTR.getX(), ptTR.getY());
        rectangle.add(ptBL.getX(), ptBL.getY());
        rectangle.add(ptBR.getX(), ptBR.getY());

        if (justification == "start") {
            double s = Math.sin(angle * 2 * Math.PI / 360);
            double c = Math.cos(angle * 2 * Math.PI / 360);
            rectangle.setRect(rectangle.getX() + textWidth / 2 * c, rectangle.getY() + textWidth / 2 * s, rectangle.getWidth(), rectangle.getHeight());
        } else if (justification == "end") {
            double s = Math.sin(angle * 2 * Math.PI / 360);
            double c = Math.cos(angle * 2 * Math.PI / 360);
            rectangle.setRect(rectangle.getX() - textWidth / 2 * c, rectangle.getY() - textWidth / 2 * s, rectangle.getWidth(), rectangle.getHeight());

        }

        return rectangle;
    }

    static void rotatePoint(Point2D pt, Point2D pivotPt, double angle) {
        final double s = Math.sin(-angle * 2 * Math.PI / 360);
        final double c = Math.cos(-angle * 2 * Math.PI / 360);

        pt.setLocation(pt.getX() - pivotPt.getX(), pt.getY() - pivotPt.getY());

        double xnew = pt.getX() * c - pt.getY() * s;
        double ynew = pt.getX() * s + pt.getY() * c;

        pt.setLocation(xnew + pivotPt.getX(), ynew + pivotPt.getY());

    }
    
}
