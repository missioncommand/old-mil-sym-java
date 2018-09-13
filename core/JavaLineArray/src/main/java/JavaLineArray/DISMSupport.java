/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package JavaLineArray;
import ArmyC2.C2SD.Utilities.ErrorLogger;
import ArmyC2.C2SD.Utilities.RendererException;
import java.awt.geom.Rectangle2D;
/**
 * A class which imported many of the C++ functions from Trident
 * Systems Dismounted Intelligence Situational Awareness System (DISM) for
 * rendering Mil-Standard-2525 tactical lines. This class does not get instantiated
 * Much of the code is still the original DISM code.
 * @author Michael Deutch
 */
public final class DISMSupport
{
    private static final int LEFT_SIDE=0;
    private static final int RIGHT_SIDE=1;
    private static final int COLINEAR=2;

    private static final double CONST_PI = Math.PI;
    private static final double maxLength=100;
    private static final double minLength=2.5;    
    private static final String _className="DISMSupport";
    
//    protected static void setMinLength(double mLength)
//    {
//        minLength=mLength;
//    }
    private static double GetTGFontSize(double iLength)
    {
        double result=-1;
        try
        {
            if (iLength < 20)
                result = 0;
            else if (iLength < 50)
                result = 1;
            else if (iLength > 250)
                result = 3;
            else
                result = 2;
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className ,"GetTGFontSize",
                    new RendererException("Failed inside GetTGFontSize", exc));
        }
        return result;
    }
    private static void ArcApproximationDouble(double left, double top, double right, double bottom,
            double startx, double starty, double endx, double endy, POINT2[] lpoints)
    {

        try
        {
            double dstartx = startx;
            double dstarty = starty;
            double dendx = endx;
            double dendy = endy;
            double a = 0;
            double b = 0;
            double ctrX = 0;
            double ctrY = 0;
            double x1, y1, x2, y2;
            double startAngle, endAngle;
            double angleIncrement = 0;
            double t=0;

            int i = 0;
            if (left > right)
            {
                double temp = left;
                left = right;
                right = temp;
            }
            if (top > bottom)
            {
                double temp = top;
                top = bottom;
                bottom = temp;
            }

            a = (right - left) / 2.0;
            b = (bottom - top) / 2.0;
            ctrX = left + a;
            ctrY = top + b;

            x1 = dstartx - ctrX;
            x2 = dendx - ctrX;
            y1 = ctrY - dstarty;
            y2 = ctrY - dendy;

            if (y1 == 0)
            {
                if (x1 > 0) startAngle = 0;
                else startAngle = CONST_PI;
            }
            else if (x1 == 0)
            {
                if (y1 > 0) startAngle = CONST_PI * 0.5;
                else startAngle = CONST_PI * -0.5;
            }
            else startAngle = Math.atan2(y1, x1);

            if (y2 == 0)
            {
                if (x2 > 0) endAngle = 0;
                else endAngle = CONST_PI;
            }
            else if (x2 == 0)
            {
                if (y2 > 0) endAngle = CONST_PI * 0.5;
                else endAngle = CONST_PI * -0.5;
            }
            else endAngle = Math.atan2(y2, x2);

            if (endAngle <= startAngle) endAngle += 2 * CONST_PI;
            angleIncrement = (endAngle - startAngle) / 16.0;

            for (t = startAngle; i < 17; t += angleIncrement, i++)
            {
                lpoints[i].x = ctrX + a * Math.cos(t);
                lpoints[i].y = ctrY - b * Math.sin(t);
            }
            return;
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className ,"ArcApproximationDouble",
                    new RendererException("Failed inside ArcApproximationDouble", exc));
        }
    }
    private static void DrawOpenRectangleDouble(POINT2[] points, POINT2[] pointsCorner, POINT2[] resultpts) {
        try {
            // draw open-ended rectangle
            POINT2 point_mid = new POINT2();
            int j = 0;
            //	POINT1 pts[4];
            point_mid.x = (points[0].x + points[1].x) / 2;
            point_mid.y = (points[0].y + points[1].y) / 2;
            pointsCorner[0].x = points[0].x - point_mid.x + points[2].x;
            pointsCorner[0].y = points[0].y - point_mid.y + points[2].y;
            pointsCorner[1].x = points[1].x - point_mid.x + points[2].x;
            pointsCorner[1].y = points[1].y - point_mid.y + points[2].y;
            resultpts[0] = new POINT2(points[1]);
            resultpts[1] = new POINT2(pointsCorner[1]);
            resultpts[2] = new POINT2(pointsCorner[0]);
            resultpts[3] = new POINT2(points[0]);
            for (j = 0; j < 4; j++) {
                resultpts[j].style = 0;
            }
            resultpts[3].style = 5;
            
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"DrawOpenRectangleDouble",
                    new RendererException("Failed inside DrawOpenRectangleDouble", exc));
        }
        return;
    }
    private static int DetermineDirectionDouble(POINT2[] points) {
        int result=0;
        try {
            double dP0P1M = 0;
            double iP0P1B = 0;
            if (points[0].x == points[1].x) {
                //return (points[2].x < points[0].x);
                if (points[2].x < points[0].x) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                // dP0P1M = slope of line between Point0 and Point1
                dP0P1M = (double) (points[0].y - points[1].y) / (double) (points[0].x - points[1].x);
                // iP0P1B = b component of y=mx+b equation of line
                iP0P1B = (points[0].y - dP0P1M * points[0].x);
                if (((points[2].y - iP0P1B) / dP0P1M) > points[2].x) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"DetermineDirectionDouble",
                    new RendererException("Failed inside DetermineDirectionDouble", exc));
        }
        return result;
    }
    private static void CalcEndpieceDeltasDouble(POINT2[] points, ref
        <double[]> piDeltaX, ref <double[]> piDeltaY,
        double dAngleDelta
               )
    {
        try {
            // find midpoint between point0 and point1
            POINT2 pntMid = new POINT2();
            double iDiagEOL_length = 0;
            double dAngle1 = 0;

            pntMid.x = (points[0].x + points[1].x) / 2;
            pntMid.y = (points[0].y + points[1].y) / 2;
            // iDiagEOL_length = length of the diagonal on end of line from line out to endpoint
            iDiagEOL_length =  ((Math.sqrt // height of graphic
                    (
                    (points[1].x - points[0].x) * (points[1].x - points[0].x) +
                    (points[1].y - points[0].y) * (points[1].y - points[0].y)) +
                    Math.sqrt // length of graphic
                    (
                    (points[2].x - pntMid.x) * (points[2].x - pntMid.x) +
                    (points[2].y - pntMid.y) * (points[2].y - pntMid.y))) / 20);

            if ((double) iDiagEOL_length > maxLength/5) {
                iDiagEOL_length = maxLength/5;
            }
            if ((double) iDiagEOL_length < minLength) {   
                iDiagEOL_length = minLength; 
            }

            // dAngle1 = angle used to calculate the end-piece deltas
            dAngle1 = Math.atan2(points[2].y - pntMid.y, points[2].x - pntMid.x) + dAngleDelta;
            //	dAngle1 = atan2(points[2].y - pntMid.y, points[2].x - pntMid.x) + dAngleDelta;
            piDeltaX.value=new double[1];
            piDeltaY.value=new double[1];
            piDeltaX.value[0] =  (iDiagEOL_length * Math.cos(dAngle1));
            piDeltaY.value[0] = (iDiagEOL_length * Math.sin(dAngle1));
            return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"CalcEndpieceDeltasDouble",
                    new RendererException("Failed inside CalcEndpieceDeltasDouble", exc));
        }
    }
    /**
     * Calculates the points for DELAY, WITHDRAW, WDRAWUP, RETIRE
     *
     * @param points OUT - the client points, also used for the returned points.
     */
    protected static int GetDelayGraphicEtcDouble(POINT2[] points) {
        int counter=0;
        try {
            POINT2[] pts = new POINT2[2];
            POINT2[] savepoints = new POINT2[3];
            double iLength = 0;
            double iRadius = 0;
            double iDiagEOL_length = 0;
            double dAngle1 = 0;
            double iDeltaX1 = 0;
            double iDeltaY1 = 0;
            double iDeltaX2 = 0;
            double iDeltaY2 = 0;
            POINT2 ptArcCenter = new POINT2();
            POINT2[] arcpoints = new POINT2[17];
            POINT2[] deltapoints = new POINT2[4];
            int j = 0;

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(arcpoints);
            lineutility.InitializePOINT2Array(deltapoints);

            points[counter] = new POINT2(savepoints[0]);
            points[counter].style = 14;
            counter++;
            points[counter] = new POINT2(savepoints[1]);
            points[counter].style = 5;
            counter++;

            iLength =  Math.sqrt((savepoints[1].x - savepoints[0].x) * (savepoints[1].x - savepoints[0].x) +
                    (savepoints[1].y - savepoints[0].y) * (savepoints[1].y - savepoints[0].y));
            iRadius =  Math.sqrt((savepoints[2].x - savepoints[1].x) * (savepoints[2].x - savepoints[1].x) +
                    (savepoints[2].y - savepoints[1].y) * (savepoints[2].y - savepoints[1].y)) / 2;
            iDiagEOL_length = (iLength + iRadius * 2) / 20;           

            if ((double) iDiagEOL_length > maxLength) {
                iDiagEOL_length = maxLength;
            }
            if ((double) iDiagEOL_length < minLength) {   //was minLength
                iDiagEOL_length = minLength;
            }
                        
            dAngle1 = Math.atan2(points[1].y - points[0].y, points[1].x - points[0].x);

            iDeltaX1 = (iDiagEOL_length * Math.cos(dAngle1 - CONST_PI / 4));
            iDeltaY1 = (iDiagEOL_length * Math.sin(dAngle1 - CONST_PI / 4));
            iDeltaX2 = (iDiagEOL_length * Math.cos(dAngle1 + CONST_PI / 4));
            iDeltaY2 = (iDiagEOL_length * Math.sin(dAngle1 + CONST_PI / 4));
            DrawEndpieceDeltasDouble(savepoints[0],
                    iDeltaX1, iDeltaY1, iDeltaX2, iDeltaY2, deltapoints);


            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints[j]);
                counter++;
            }
            // draw the semicircle
            ptArcCenter.x = (savepoints[1].x + savepoints[2].x) / 2;
            ptArcCenter.y = (savepoints[1].y + savepoints[2].y) / 2;


            boolean reverseArc = ReverseDelayArc(savepoints);
            if (reverseArc == false) {
                ArcApproximationDouble( (ptArcCenter.x - iRadius), (ptArcCenter.y - iRadius),
                        (ptArcCenter.x + iRadius), (ptArcCenter.y + iRadius),
                        savepoints[1].x, savepoints[1].y, savepoints[2].x, savepoints[2].y, arcpoints);
            } else {
                ArcApproximationDouble((ptArcCenter.x - iRadius), (ptArcCenter.y - iRadius),
                        (ptArcCenter.x + iRadius), (ptArcCenter.y + iRadius),
                        savepoints[2].x, savepoints[2].y, savepoints[1].x, savepoints[1].y, arcpoints);
            }

            for (j = 0; j < 17; j++) {
                points[counter] = new POINT2(arcpoints[j]);
                points[counter].style = 0;
                counter++;
            }

            //clean up
            pts = null;
            savepoints = null;
            arcpoints = null;
            deltapoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDelayGraphicEtcDouble",
                    new RendererException("Failed inside GetDelayGraphicEtcDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for SCREEN, COVER, GUARD, SARA.
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMCoverDouble(POINT2[] points,
                                            int linetype) {
            int counter = 0;
        try {
            // switch points[1] and points[2] if they are backwards
            double dAngle0, dAngle1, dDeltaX0, dDeltaY0, dDeltaX1, dDeltaY1;
            double iLengthPt0Pt1 = 0;
            double iLengthPt0Pt2 = 0;
            double iDelta = 0;
            int j=0;
            int t=1;
            double iFontSize = 0;
            double iLetterOffset = 0;
            POINT2[] savepoints = new POINT2[3];
            POINT2[] pts = new POINT2[2];
            POINT2[] ptsJaggyLine = new POINT2[4];
            int sign=1;
            
            //added section for jaggy line orientation M. Deutch 6-24-11
            POINT2 pt0=new POINT2(points[0]);
            POINT2 pt1=new POINT2(points[1]);
            POINT2 pt2=new POINT2(points[2]);
            POINT2 pt3=new POINT2();
            POINT2 pt4=new POINT2();                        
            
            lineutility.LineRelativeToLine(pt1, pt2, pt0, pt3, pt4);
            //now we have the pt3-pt4 line which pt0 is on
            //get the corresponding point back on the original line
            lineutility.LineRelativeToLine(pt3, pt0, pt1, pt2, pt4);
            int quadrant=lineutility.GetQuadrantDouble(pt0, pt4);

            pt1=new POINT2(points[1]);
            pt2=new POINT2(points[2]);
            if(pt1.x<pt2.x && quadrant==1)
                sign=-1;
            else if(pt1.x > pt2.x && quadrant == 2)
                sign=-1;
            else if(pt1.x > pt2.x && quadrant == 3)
                sign=-1;
            else if(pt1.x < pt2.x && quadrant == 4)
                sign=-1;
            //end section

            if(linetype==TacticalLines.SARA)
                t=0;

            //save the points in the correct order
            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
                savepoints[j].style = 0;
            }

            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(ptsJaggyLine);

            iLengthPt0Pt1 = Math.sqrt((savepoints[1].x - savepoints[0].x) * (savepoints[1].x - savepoints[0].x) +
                    (savepoints[1].y - savepoints[0].y) * (savepoints[1].y - savepoints[0].y));
            iLengthPt0Pt2 = Math.sqrt((savepoints[2].x - savepoints[0].x) * (savepoints[2].x - savepoints[0].x) +
                    (savepoints[2].y - savepoints[0].y) * (savepoints[2].y - savepoints[0].y));

            if (iLengthPt0Pt1 > iLengthPt0Pt2) {
                iLengthPt0Pt1 = iLengthPt0Pt2;
            }
            iFontSize = GetTGFontSize(iLengthPt0Pt1);
            if (iFontSize > 0) {
                iDelta = iLengthPt0Pt1 / 15;//was 15

                //M. Deutch 8-18-04
                if (iDelta > maxLength) {
                    iDelta = maxLength;
                }
                if (iDelta < minLength) {
                    iDelta = minLength;
                }

                dAngle0 = Math.atan2(savepoints[0].y - savepoints[1].y, savepoints[0].x - savepoints[1].x);
                dAngle1 = Math.atan2(savepoints[0].y - savepoints[2].y, savepoints[0].x - savepoints[2].x);

                // left side: draw letter in from the jaggy line

                savepoints[0].x -= 30 * Math.cos(dAngle0);  //was 20
                savepoints[0].y -= 30 * Math.sin(dAngle0);

                iLetterOffset = 0;
                ptsJaggyLine[0].x = savepoints[0].x - iLetterOffset * 2;//was -
                ptsJaggyLine[0].y = savepoints[0].y;
                ptsJaggyLine[0].x -= iLetterOffset;

                pts[0].x = (ptsJaggyLine[0].x + savepoints[1].x) / 2;
                pts[0].y = (ptsJaggyLine[0].y + savepoints[1].y) / 2;
                dDeltaX0 = Math.cos(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                dDeltaY0 = Math.sin(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                ptsJaggyLine[1].x = pts[0].x - dDeltaX0;    //was -
                ptsJaggyLine[1].y = pts[0].y - dDeltaY0;    //was -
                ptsJaggyLine[2].x = pts[0].x + dDeltaX0;    //was +
                ptsJaggyLine[2].y = pts[0].y + dDeltaY0;    //was +
                ptsJaggyLine[3] = new POINT2(savepoints[1]);
                for (j = 0; j < 4; j++) {
                    points[counter] = new POINT2(ptsJaggyLine[j]);
                    counter++;
                }
                points[counter - 1].style = 5;
                // draw arrow at end of line
                dDeltaX1 = Math.cos(dAngle0 - sign*CONST_PI / 4) * iDelta;   //was -
                dDeltaY1 = Math.sin(dAngle0 - sign*CONST_PI / 4) * iDelta;   //was -
                ptsJaggyLine[0].x = savepoints[1].x + dDeltaX0; //was +
                ptsJaggyLine[0].y = savepoints[1].y + dDeltaY0; //was +
                ptsJaggyLine[1] = new POINT2(savepoints[1]);
                ptsJaggyLine[2].x = savepoints[1].x + dDeltaX1; //was +
                ptsJaggyLine[2].y = savepoints[1].y + dDeltaY1; //was +
                for (j = 0; j < 3; j++) {
                    points[counter] = new POINT2(ptsJaggyLine[j]);
                    points[counter].style = 0;
                    if(linetype==TacticalLines.SARA)
                    {
                        points[counter].style = 9;
                    }

                    counter++;
                }

                points[counter - 1].style = 5;
                if(linetype==TacticalLines.SARA)
                {
                    points[counter-1].style = 9;
                    points[counter]=new POINT2(points[counter-3]);
                    points[counter].style=10;
                    counter++;
                }

                // right side: draw letter and jaggy line

                savepoints[0].x += 30 * (Math.cos(dAngle0) - Math.cos(dAngle1));  //was 20
                savepoints[0].y += 30 * (Math.sin(dAngle0) - Math.sin(dAngle1));

                ptsJaggyLine[0].x = savepoints[0].x + iLetterOffset * 2;
                ptsJaggyLine[0].y = savepoints[0].y;
                ptsJaggyLine[0].x += iLetterOffset;

                pts[0].x = (ptsJaggyLine[0].x + savepoints[2].x) / 2;
                pts[0].y = (ptsJaggyLine[0].y + savepoints[2].y) / 2;
                dDeltaX0 = Math.cos(dAngle1 - sign*CONST_PI / 4) * iDelta;   //was -
                dDeltaY0 = Math.sin(dAngle1 - sign*CONST_PI / 4) * iDelta;   //was -
                ptsJaggyLine[1].x = pts[0].x - dDeltaX0;    //was -
                ptsJaggyLine[1].y = pts[0].y - dDeltaY0;    //was -
                ptsJaggyLine[2].x = pts[0].x + dDeltaX0;    //was +
                ptsJaggyLine[2].y = pts[0].y + dDeltaY0;    //was +
                ptsJaggyLine[3] = new POINT2(savepoints[2]);
                for (j = 0; j < 4; j++) {
                    points[counter] = new POINT2(ptsJaggyLine[j]);
                    counter++;
                }
                points[counter - 1].style = 5;
                // draw arrow at end of line
                dDeltaX1 = Math.cos(dAngle1 + sign*CONST_PI / 4) * iDelta;   //was +
                dDeltaY1 = Math.sin(dAngle1 + sign*CONST_PI / 4) * iDelta;   //was +
                ptsJaggyLine[0].x = savepoints[2].x + dDeltaX0;
                ptsJaggyLine[0].y = savepoints[2].y + dDeltaY0;
                ptsJaggyLine[1] = savepoints[2];
                ptsJaggyLine[2].x = savepoints[2].x + dDeltaX1;
                ptsJaggyLine[2].y = savepoints[2].y + dDeltaY1;
                //		DrawLine(destination, mask, color, ptsJaggyLine, 3, iLineThickness);
                for (j = 0; j < 3; j++) {
                    points[counter] = new POINT2(ptsJaggyLine[j]);
                    points[counter].style = 0;
                    if(linetype==TacticalLines.SARA)
                        points[counter].style = 9;

                    counter++;
                }
                points[counter - 1].style = 5;
                if(linetype==TacticalLines.SARA)
                {
                    points[counter-1].style = 9;
                    points[counter]=new POINT2(points[counter-3]);
                    points[counter].style=10;
                    counter++;
                }
            }
            else {
                points[0] = new POINT2(savepoints[0]);
                points[0].style = 0;
                points[1] = new POINT2(savepoints[1]);
                points[1].style = 5;
                points[2] = new POINT2(savepoints[0]);
                points[2].style = 0;
                points[3] = new POINT2(savepoints[2]);
                //for (j = 3; j < vblCounter; j++) {
                  //  points[j].style = 5;
                //}
                return 4;
            }

            savepoints = null;
            pts = null;
            ptsJaggyLine = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMcoverDouble",
                    new RendererException("Failed inside GetDISMCoverDouble", exc));
        }
        return counter;
    }
    /**
     * rev C uses 4 
     * @param points
     * @param linetype
     * @return 
     */
    protected static int GetDISMCoverDoubleRevC(POINT2[] points,
                                            int linetype,
                                            int vblSaveCounter) 
    {
            int counter = 0;
        try 
        {                        
            // switch points[1] and points[2] if they are backwards
            double dAngle0=0, dDeltaX0=0, dDeltaY0=0, dDeltaX1=0, dDeltaY1=0;
            double iLengthPt0Pt1 = 0;
            double iLengthPt0Pt2 = 0;
            double iDelta = 0;
            int j=0;
            int t=1;
            double iFontSize = 0;
            double iLetterOffset = 0;
            POINT2[] savepoints = new POINT2[3];
            POINT2[] pts = new POINT2[2];
            POINT2[] ptsJaggyLine = new POINT2[4];
            //float scale = 1;
            boolean goLeftThenRight=false;
            int sign=1;
            
            //rev C with 4 points
            POINT2[]origPoints=null;
            if(vblSaveCounter==4)
            {
                origPoints=new POINT2[4];
                for(j=0;j<vblSaveCounter;j++)
                    origPoints[j]=new POINT2(points[j]);
                
                //reorder points
                points[1]=origPoints[0];
                points[2]=origPoints[3];
                points[0].x=(origPoints[1].x+origPoints[2].x)/2;
                points[0].y=(origPoints[1].y+origPoints[2].y)/2;
            }
                        
            //added section for jaggy line orientation M. Deutch 6-24-11
            POINT2 pt0=new POINT2(points[0]);
            POINT2 pt1=new POINT2(points[1]);
            POINT2 pt2=new POINT2(points[2]);
            POINT2 pt3=new POINT2();
            POINT2 pt4=new POINT2();                        
            
            lineutility.LineRelativeToLine(pt1, pt2, pt0, pt3, pt4);
            //now we have the pt3-pt4 line which pt0 is on
            //get the corresponding point back on the original line
            lineutility.LineRelativeToLine(pt3, pt0, pt1, pt2, pt4);
            int quadrant=lineutility.GetQuadrantDouble(pt0, pt4);

            pt1=new POINT2(points[1]);
            pt2=new POINT2(points[2]);
            if(pt1.x<pt2.x && quadrant==1)
                sign=-1;
            else if(pt1.x > pt2.x && quadrant == 2)
                sign=-1;
            else if(pt1.x > pt2.x && quadrant == 3)
                sign=-1;
            else if(pt1.x < pt2.x && quadrant == 4)
                sign=-1;
            //end section
            
            //System.out.print(Integer.toString(quadrant));
            //System.out.print("\n");
            if(linetype==TacticalLines.SARA)
                t=0;
            
            if(points[1].x<=points[2].x)
                goLeftThenRight=true;

            //save the points in the correct order
            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
                savepoints[j].style = 0;
            }

            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(ptsJaggyLine);

            iLengthPt0Pt1 = Math.sqrt((savepoints[1].x - savepoints[0].x) * (savepoints[1].x - savepoints[0].x) +
                    (savepoints[1].y - savepoints[0].y) * (savepoints[1].y - savepoints[0].y));
            iLengthPt0Pt2 = Math.sqrt((savepoints[2].x - savepoints[0].x) * (savepoints[2].x - savepoints[0].x) +
                    (savepoints[2].y - savepoints[0].y) * (savepoints[2].y - savepoints[0].y));

            if (iLengthPt0Pt1 > iLengthPt0Pt2) {
                iLengthPt0Pt1 = iLengthPt0Pt2;
            }
            iFontSize = GetTGFontSize(iLengthPt0Pt1);
            if (iFontSize > 0) 
            {
                iDelta = iLengthPt0Pt1 / 15;//was 15

                //M. Deutch 8-18-04
                if (iDelta > maxLength) {
                    iDelta = maxLength;
                }
                if (iDelta < minLength) {
                    iDelta = minLength;
                }
                
                // left side: draw letter in from the jaggy line
                if(vblSaveCounter<4)//rev b
                {
                    if(goLeftThenRight)
                        savepoints[0].x-=30*t;  //was 20
                    else
                        savepoints[0].x+=30*t;  //was 20
                    
                    iLetterOffset = 0;
                    ptsJaggyLine[0].x = savepoints[0].x - iLetterOffset * 2;//was -
                    ptsJaggyLine[0].y = savepoints[0].y;                
                    ptsJaggyLine[0].x -= iLetterOffset;                
                    dAngle0 = Math.atan2(ptsJaggyLine[0].y - savepoints[1].y, ptsJaggyLine[0].x - savepoints[1].x);
                    pts[0].x = (ptsJaggyLine[0].x + savepoints[1].x) / 2;
                    pts[0].y = (ptsJaggyLine[0].y + savepoints[1].y) / 2;
                    dDeltaX0 = Math.cos(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                    dDeltaY0 = Math.sin(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                    ptsJaggyLine[1].x = pts[0].x - dDeltaX0;    //was -
                    ptsJaggyLine[1].y = pts[0].y - dDeltaY0;    //was -
                    ptsJaggyLine[2].x = pts[0].x + dDeltaX0;    //was +
                    ptsJaggyLine[2].y = pts[0].y + dDeltaY0;    //was +
                    ptsJaggyLine[3] = new POINT2(savepoints[1]);
                    for (j = 0; j < 4; j++) 
                    {
                        points[counter] = new POINT2(ptsJaggyLine[j]);
                        counter++;
                    }
                    points[counter - 1].style = 5;
                }//end rev b
                else    //rev c
                {
                    //iLetterOffset = 0;
                    //ptsJaggyLine[0].x = savepoints[0].x - iLetterOffset * 2;//was -
                    //ptsJaggyLine[0].y = savepoints[0].y;                
                    //ptsJaggyLine[0].x -= iLetterOffset;
                    ptsJaggyLine[0]=new POINT2(origPoints[1]);
                    //dAngle0 = Math.atan2(ptsJaggyLine[0].y - savepoints[1].y, ptsJaggyLine[0].x - savepoints[1].x);
                    dAngle0 = Math.atan2(ptsJaggyLine[0].y - origPoints[0].y, ptsJaggyLine[0].x - origPoints[0].x);
                    //pts[0].x = (ptsJaggyLine[0].x + savepoints[1].x) / 2;
                    //pts[0].y = (ptsJaggyLine[0].y + savepoints[1].y) / 2;
                    pts[0].x = (ptsJaggyLine[0].x + origPoints[0].x) / 2;
                    pts[0].y = (ptsJaggyLine[0].y + origPoints[0].y) / 2;
                    dDeltaX0 = Math.cos(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                    dDeltaY0 = Math.sin(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                    ptsJaggyLine[1].x = pts[0].x - dDeltaX0;    //was -
                    ptsJaggyLine[1].y = pts[0].y - dDeltaY0;    //was -
                    ptsJaggyLine[2].x = pts[0].x + dDeltaX0;    //was +
                    ptsJaggyLine[2].y = pts[0].y + dDeltaY0;    //was +
                    //ptsJaggyLine[3] = new POINT2(savepoints[1]);
                    ptsJaggyLine[3] = new POINT2(origPoints[0]);
                    for (j = 0; j < 4; j++) 
                    {
                        points[counter] = new POINT2(ptsJaggyLine[j]);
                        counter++;
                    }
                    points[counter - 1].style = 5;                    
                }//end rev c
                
                // draw arrow at end of line
                dDeltaX1 = Math.cos(dAngle0 - sign*CONST_PI / 4) * iDelta;   //was -
                dDeltaY1 = Math.sin(dAngle0 - sign*CONST_PI / 4) * iDelta;   //was -
                //ptsJaggyLine[0].x = savepoints[1].x + dDeltaX0; //was +
                //ptsJaggyLine[0].y = savepoints[1].y + dDeltaY0; //was +
                if(vblSaveCounter<4)
                {
                    ptsJaggyLine[0].x = savepoints[1].x + dDeltaX0; //was +
                    ptsJaggyLine[0].y = savepoints[1].y + dDeltaY0; //was +
                }
                else
                {
                    ptsJaggyLine[0].x = origPoints[0].x + dDeltaX0; //was +
                    ptsJaggyLine[0].y = origPoints[0].y + dDeltaY0; //was +                    
                }
                //ptsJaggyLine[1] = new POINT2(savepoints[1]);
                if(vblSaveCounter<4)
                    ptsJaggyLine[1] = new POINT2(savepoints[1]);
                else
                    ptsJaggyLine[1] = new POINT2(origPoints[0]);
                    
                //ptsJaggyLine[2].x = savepoints[1].x + dDeltaX1; //was +
                //ptsJaggyLine[2].y = savepoints[1].y + dDeltaY1; //was +
                if(vblSaveCounter<4)
                {
                    ptsJaggyLine[2].x = savepoints[1].x + dDeltaX1; //was +
                    ptsJaggyLine[2].y = savepoints[1].y + dDeltaY1; //was +
                }
                else
                {
                    ptsJaggyLine[2].x = origPoints[0].x + dDeltaX1; //was +
                    ptsJaggyLine[2].y = origPoints[0].y + dDeltaY1; //was +                    
                }
                for (j = 0; j < 3; j++) {
                    points[counter] = new POINT2(ptsJaggyLine[j]);
                    points[counter].style = 0;
                    if(linetype==TacticalLines.SARA)
                    {
                        points[counter].style = 9;
                    }

                    counter++;
                }

                points[counter - 1].style = 5;
                if(linetype==TacticalLines.SARA)
                {
                    points[counter-1].style = 9;
                    points[counter]=new POINT2(points[counter-3]);
                    points[counter].style=10;
                    counter++;
                }

                // right side: draw letter and jaggy line
                if(vblSaveCounter<4)    //rev b
                {
                    if(goLeftThenRight)
                        savepoints[0].x+=60*t;  //was 40
                    else
                        savepoints[0].x-=60*t;  //wass 40

                    ptsJaggyLine[0].x = savepoints[0].x + iLetterOffset * 2;
                    ptsJaggyLine[0].y = savepoints[0].y;
                    ptsJaggyLine[0].x += iLetterOffset;                
                    dAngle0 = Math.atan2(ptsJaggyLine[0].y - savepoints[2].y, ptsJaggyLine[0].x - savepoints[2].x);
                    pts[0].x = (ptsJaggyLine[0].x + savepoints[2].x) / 2;
                    pts[0].y = (ptsJaggyLine[0].y + savepoints[2].y) / 2;
                    dDeltaX0 = Math.cos(dAngle0 - sign*CONST_PI / 4) * iDelta;   //was -
                    dDeltaY0 = Math.sin(dAngle0 - sign*CONST_PI / 4) * iDelta;   //was -
                    ptsJaggyLine[1].x = pts[0].x - dDeltaX0;    //was -
                    ptsJaggyLine[1].y = pts[0].y - dDeltaY0;    //was -
                    ptsJaggyLine[2].x = pts[0].x + dDeltaX0;    //was +
                    ptsJaggyLine[2].y = pts[0].y + dDeltaY0;    //was +
                    ptsJaggyLine[3] = new POINT2(savepoints[2]);
                    for (j = 0; j < 4; j++) {
                        points[counter] = new POINT2(ptsJaggyLine[j]);
                        counter++;
                    }
                    points[counter - 1].style = 5;
                    // draw arrow at end of line
                    dDeltaX1 = Math.cos(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                    dDeltaY1 = Math.sin(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                    ptsJaggyLine[0].x = savepoints[2].x + dDeltaX0;
                    ptsJaggyLine[0].y = savepoints[2].y + dDeltaY0;
                    ptsJaggyLine[1] = savepoints[2];
                    ptsJaggyLine[2].x = savepoints[2].x + dDeltaX1;
                    ptsJaggyLine[2].y = savepoints[2].y + dDeltaY1;
                }//end rev b
                else    //rev c
                {
                    //if(goLeftThenRight)
                    //    savepoints[0].x+=60*t;  //was 40
                    //else
                    //    savepoints[0].x-=60*t;  //wass 40

                    //ptsJaggyLine[0].x = savepoints[0].x + iLetterOffset * 2;
                    //ptsJaggyLine[0].y = savepoints[0].y;
                    //ptsJaggyLine[0].x += iLetterOffset;                
                    ptsJaggyLine[0]=new POINT2(origPoints[2]);
                    //dAngle0 = Math.atan2(ptsJaggyLine[0].y - savepoints[2].y, ptsJaggyLine[0].x - savepoints[2].x);
                    dAngle0 = Math.atan2(ptsJaggyLine[0].y - origPoints[3].y, ptsJaggyLine[0].x - origPoints[3].x);
                    //pts[0].x = (ptsJaggyLine[0].x + savepoints[2].x) / 2;
                    //pts[0].y = (ptsJaggyLine[0].y + savepoints[2].y) / 2;
                    pts[0].x = (ptsJaggyLine[0].x + origPoints[3].x) / 2;
                    pts[0].y = (ptsJaggyLine[0].y + origPoints[3].y) / 2;
                    dDeltaX0 = Math.cos(dAngle0 - sign*CONST_PI / 4) * iDelta;   //was -
                    dDeltaY0 = Math.sin(dAngle0 - sign*CONST_PI / 4) * iDelta;   //was -
                    ptsJaggyLine[1].x = pts[0].x - dDeltaX0;    //was -
                    ptsJaggyLine[1].y = pts[0].y - dDeltaY0;    //was -
                    ptsJaggyLine[2].x = pts[0].x + dDeltaX0;    //was +
                    ptsJaggyLine[2].y = pts[0].y + dDeltaY0;    //was +
                    //ptsJaggyLine[3] = new POINT2(savepoints[2]);
                    ptsJaggyLine[3] = new POINT2(origPoints[3]);
                    for (j = 0; j < 4; j++) {
                        points[counter] = new POINT2(ptsJaggyLine[j]);
                        counter++;
                    }
                    points[counter - 1].style = 5;
                    // draw arrow at end of line
                    dDeltaX1 = Math.cos(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                    dDeltaY1 = Math.sin(dAngle0 + sign*CONST_PI / 4) * iDelta;   //was +
                    //ptsJaggyLine[0].x = savepoints[2].x + dDeltaX0;
                    //ptsJaggyLine[0].y = savepoints[2].y + dDeltaY0;
                    //ptsJaggyLine[1] = savepoints[2];
                    //ptsJaggyLine[2].x = savepoints[2].x + dDeltaX1;
                    //ptsJaggyLine[2].y = savepoints[2].y + dDeltaY1;                    
                    ptsJaggyLine[0].x = origPoints[3].x + dDeltaX0;
                    ptsJaggyLine[0].y = origPoints[3].y + dDeltaY0;
                    ptsJaggyLine[1] = new POINT2(origPoints[3]);
                    ptsJaggyLine[2].x = origPoints[3].x + dDeltaX1;
                    ptsJaggyLine[2].y = origPoints[3].y + dDeltaY1;                    
                }//end rev c
                
                for (j = 0; j < 3; j++) 
                {
                    points[counter] = new POINT2(ptsJaggyLine[j]);
                    points[counter].style = 0;
                    if(linetype==TacticalLines.SARA)
                        points[counter].style = 9;

                    counter++;
                }
                points[counter - 1].style = 5;
                if(linetype==TacticalLines.SARA)
                {
                    points[counter-1].style = 9;
                    points[counter]=new POINT2(points[counter-3]);
                    points[counter].style=10;
                    counter++;
                }
            }
            else 
            {
                points[0] = new POINT2(savepoints[0]);
                points[0].style = 0;
                points[1] = new POINT2(savepoints[1]);
                points[1].style = 5;
                points[2] = new POINT2(savepoints[0]);
                points[2].style = 0;
                points[3] = new POINT2(savepoints[2]);
                return 4;
            }

            savepoints = null;
            pts = null;
            ptsJaggyLine = null;
        } 
        catch (Exception exc) 
        {
            ErrorLogger.LogException(_className ,"GetDISMcoverDoubleRevC",
                    new RendererException("Failed inside GetDISMCoverDoubleRevc", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for BYPASS
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMBypassDouble(POINT2[] points,
                                            int linetype) {
        int counter = 0;
        try {
            int j = 0;
            POINT2[] pointsCorner = new POINT2[2];
            POINT2[] rectpts = new POINT2[4];
            POINT2[] savepoints = new POINT2[3];
            POINT2[] deltapoints1 = new POINT2[4];
            POINT2[] deltapoints2 = new POINT2[4];
            ref<double[]> iDeltaX = new ref(), iDeltaY = new ref();
            int bPointsRight = 0;

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pointsCorner);
            lineutility.InitializePOINT2Array(rectpts);
            lineutility.InitializePOINT2Array(deltapoints1);
            lineutility.InitializePOINT2Array(deltapoints2);

            DrawOpenRectangleDouble(savepoints, pointsCorner, rectpts);
            for (j = 0; j < 4; j++) {
                points[counter] = rectpts[j];
                counter++;
            }

            bPointsRight = DetermineDirectionDouble(savepoints);

            CalcEndpieceDeltasDouble(savepoints, iDeltaX, iDeltaY, CONST_PI / 4);

            if ((savepoints[0].y - savepoints[1].y) < 0) {// Point0 is higher than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints2);
                }
            } else {// Point0 is lower than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints2);
                }
            }

            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints1[j]);
                counter++;
            }
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints2[j]);
                counter++;
            }
            //clean up
            pointsCorner = null;
            rectpts = null;
            savepoints = null;
            deltapoints1 = null;
            deltapoints2 = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMBypassDouble",
                    new RendererException("Failed inside GetDISMBypassDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for BREACH.
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMBreachDouble(POINT2[] points, int linetype) {
        int counter = 0;
        try {
            POINT2[] pointsCorner = new POINT2[2];
            POINT2[] rectpts = new POINT2[4];
            POINT2[] deltapoints1 = new POINT2[4];
            POINT2[] deltapoints2 = new POINT2[4];
            ref<double[]> iDeltaX = new ref(), iDeltaY = new ref();
            int bPointsRight = 0, j = 0;
            POINT2[] savepoints = new POINT2[3];

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pointsCorner);
            lineutility.InitializePOINT2Array(rectpts);
            lineutility.InitializePOINT2Array(deltapoints1);
            lineutility.InitializePOINT2Array(deltapoints2);

            DrawOpenRectangleDouble(savepoints, pointsCorner, rectpts);
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(rectpts[j]);
                counter++;
            }

            bPointsRight = DetermineDirectionDouble(savepoints);

            CalcEndpieceDeltasDouble(savepoints, iDeltaX, iDeltaY, CONST_PI / 4);

            if ((savepoints[0].y - savepoints[1].y) < 0) {// Point0 is higher than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], iDeltaX.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], deltapoints2);
                }
            } else {// Point0 is lower than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], iDeltaX.value[0], deltapoints2);
                }
            }
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints1[j]);
                counter++;
            }
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints2[j]);
                counter++;
            }
            //clean up
            pointsCorner = null;
            rectpts = null;
            savepoints = null;
            deltapoints1 = null;
            deltapoints2 = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMBreachDouble",
                    new RendererException("Failed inside GetDISMBreachDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for CANALIZE
     *
     * @param points - OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMCanalizeDouble(POINT2[] points, int linetype) {
        int counter = 0;
        try {
            POINT2[] pointsCorner = new POINT2[2];
            POINT2[] rectpts = new POINT2[4];
            POINT2[] deltapoints1 = new POINT2[4];
            POINT2[] deltapoints2 = new POINT2[4];
            int j = 0;
            ref<double[]> iDeltaX = new ref(), iDeltaY = new ref();
            int bPointsRight = 0;
            POINT2[] savepoints = new POINT2[3];

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pointsCorner);
            lineutility.InitializePOINT2Array(rectpts);
            lineutility.InitializePOINT2Array(deltapoints1);
            lineutility.InitializePOINT2Array(deltapoints2);

            DrawOpenRectangleDouble(savepoints, pointsCorner, rectpts);

            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(rectpts[j]);
                counter++;
            }

            bPointsRight = DetermineDirectionDouble(savepoints);

            CalcEndpieceDeltasDouble(savepoints, iDeltaX, iDeltaY, CONST_PI / 4);

            if ((savepoints[0].y - savepoints[1].y) < 0) {// Point0 is higher than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], iDeltaX.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], deltapoints2);
                }
            } else {// Point0 is lower than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], -iDeltaY.value[0], iDeltaX.value[0], deltapoints2);
                }
            }

            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints1[j]);
                counter++;
            }
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints2[j]);
                counter++;
            }
            //clean up
            pointsCorner = null;
            rectpts = null;
            savepoints = null;
            deltapoints1 = null;
            deltapoints2 = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMCanalizeDouble",
                    new RendererException("Failed inside GetDISMCanalizeDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for DECEIVE.
     *
     * @param points - OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     * @param vblCounter the number of symbol points.
     */
    protected static void GetDISMDeceiveDouble(POINT2[] points) {
        try {
            POINT2[] savepoints = new POINT2[3];// POINT2[3];
            int j = 0;

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            points[0] = new POINT2(savepoints[0]);
            points[0].style = 1;
            points[1] = new POINT2(savepoints[1]);
            points[1].style = 5;
            points[2] = new POINT2(savepoints[2]);
            points[2].style = 1;
            points[3] = new POINT2(savepoints[0]);
            points[3].style = 5;

            savepoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMDeceiveDouble",
                    new RendererException("Failed inside GetDISMDeceiveDouble", exc));
        }
        return;
    }
    /**
     * Calculates the points for DISRUPT
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMDisruptDouble(POINT2[] points, int linetype) {
        int counter = 0;
        try {
            POINT2[] pts = new POINT2[2];
            POINT2[] ptsArrow = new POINT2[3];
            POINT2 ptCenter = new POINT2();
            int j = 0;
            POINT2[] savepoints = new POINT2[3];
            double dAngle1 = 0;
            POINT2[] deltapoints1 = new POINT2[4];
            POINT2[] deltapoints2 = new POINT2[4];
            POINT2[] deltapoints3 = new POINT2[4];
            double iDiagEOL_length = 0;
            double iDeltaX1 = 0;
            double iDeltaY1 = 0;
            double iDeltaX2 = 0;
            double iDeltaY2 = 0;

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(ptsArrow);
            lineutility.InitializePOINT2Array(deltapoints1);
            lineutility.InitializePOINT2Array(deltapoints2);
            lineutility.InitializePOINT2Array(deltapoints3);

            //	DrawLine(destination, mask, color, points, 2, 2);
            points[counter] = new POINT2(savepoints[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(savepoints[1]);
            points[counter].style = 5;
            counter++;
            //	pts[0] = points[1];
            //	pts[1] = points[2];
            //	DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(savepoints[1]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(savepoints[2]);
            points[counter].style = 5;
            counter++;

            ptCenter.x = (savepoints[0].x + savepoints[1].x) / 2;
            ptCenter.y = (savepoints[0].y + savepoints[1].y) / 2;
            ptsArrow[0] = new POINT2(savepoints[2]);
            ptsArrow[1].x = ptCenter.x + (savepoints[2].x - savepoints[1].x) * 4 / 5;
            ptsArrow[1].y = ptCenter.y + (savepoints[2].y - savepoints[1].y) * 4 / 5;
            ptsArrow[2].x = savepoints[0].x + (savepoints[2].x - savepoints[1].x) * 3 / 5;
            ptsArrow[2].y = savepoints[0].y + (savepoints[2].y - savepoints[1].y) * 3 / 5;

            pts[0].x = ptCenter.x - (savepoints[2].x - savepoints[1].x) / 5;
            pts[0].y = ptCenter.y - (savepoints[2].y - savepoints[1].y) / 5;
            pts[1] = new POINT2(ptsArrow[1]);
            //	DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0] = new POINT2(savepoints[0]);
            pts[1] = new POINT2(ptsArrow[2]);
            //	DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            // the following code is very similar to CalcEndpieceDeltas
            iDiagEOL_length =  ((Math.sqrt // height of graphic
                    (
                    (savepoints[1].x - savepoints[0].x) * (savepoints[1].x - savepoints[0].x) +
                    (savepoints[1].y - savepoints[0].y) * (savepoints[1].y - savepoints[0].y)) +
                    Math.sqrt // length of graphic
                    (
                    (savepoints[2].x - savepoints[1].x) * (savepoints[2].x - savepoints[1].x) +
                    (savepoints[2].y - savepoints[1].y) * (savepoints[2].y - savepoints[1].y))) / 15);

            //M. Deutch 8-18-04
            if (iDiagEOL_length >  maxLength) {
                iDiagEOL_length =  maxLength;
            }
            if (iDiagEOL_length <  minLength) {//was minLength
                iDiagEOL_length =  minLength;   //was minLength
            }

            // dAngle1 = angle used to calculate the end-piece deltas
            dAngle1 = Math.atan2(savepoints[1].y - savepoints[2].y, savepoints[1].x - savepoints[2].x);
            //	dAngle1 = atan2(savepoints[1].y - savepoints[2].y, savepoints[1].x - savepoints[2].x);
            iDeltaX1 =  (iDiagEOL_length * Math.cos(dAngle1 - CONST_PI / 6));
            iDeltaY1 =  (iDiagEOL_length * Math.sin(dAngle1 - CONST_PI / 6));
            iDeltaX2 =  (iDiagEOL_length * Math.cos(dAngle1 + CONST_PI / 6));
            iDeltaY2 =  (iDiagEOL_length * Math.sin(dAngle1 + CONST_PI / 6));

            DrawEndpieceDeltasDouble(ptsArrow[0],
                    iDeltaX1, iDeltaY1, iDeltaX2, iDeltaY2, deltapoints1);
            DrawEndpieceDeltasDouble(ptsArrow[1],
                    iDeltaX1, iDeltaY1, iDeltaX2, iDeltaY2, deltapoints2);
            DrawEndpieceDeltasDouble(ptsArrow[2],
                    iDeltaX1, iDeltaY1, iDeltaX2, iDeltaY2, deltapoints3);
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints1[j]);
                counter++;
            }
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints2[j]);
                counter++;
            }
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints3[j]);
                counter++;
            }
            //clean up
            pts = null;
            ptsArrow = null;
            savepoints = null;
            deltapoints1 = null;
            deltapoints2 = null;
            deltapoints3 = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMDisruptDouble",
                    new RendererException("Failed inside GetDISMDisruptDouble", exc));
        }
        return counter;
    }
//    protected int GetDISMSARADouble(POINT2[] points, int linetype, int vblCounter) {
//        int counter = 0;
//        try {
//            int i = 0, j = 0;
//            float scale = (float) 0.9;
//            float iSymbolSize = scale * (float) 10; // size left for symbol centered within this graphic
//            POINT2[] pts = new POINT2[2];
//            POINT2[] savepoints = new POINT2[3];
//            // locate point near center where jaggy lines should begin
//            POINT2[] ptCenter = new POINT2[2];
//            // four possible center points
//            POINT2[] ptsBegin = new POINT2[4];
//            int iLengthTemp = 0;
//            int iLengthCntPt1 = 0;
//            int iLengthCntPt2 = 0;
//            int iDelta = 0;
//            double dAngle0 = 0, dDeltaX0 = 0, dDeltaY0 = 0, dDeltaX1 = 0, dDeltaY1 = 0;
//            POINT2[] ptsJaggyLine = new POINT2[4];
//
//            for (j = 0; j < 3; j++) {
//                savepoints[j] = new POINT2(points[j]);
//            }
//
//            lineutility.InitializePOINT2Array(ptsBegin);
//            lineutility.InitializePOINT2Array(ptCenter);
//            lineutility.InitializePOINT2Array(pts);
//            lineutility.InitializePOINT2Array(ptsJaggyLine);
//
//            ptsBegin[0].x = savepoints[0].x - iSymbolSize;
//            ptsBegin[0].y = savepoints[0].y - iSymbolSize; // top left
//            ptsBegin[1].x = savepoints[0].x + iSymbolSize;
//            ptsBegin[1].y = savepoints[0].y - iSymbolSize; // top right
//            ptsBegin[2].x = savepoints[0].x + iSymbolSize;
//            ptsBegin[2].y = savepoints[0].y + iSymbolSize; // bottom right
//            ptsBegin[3].x = savepoints[0].x - iSymbolSize;
//            ptsBegin[3].y = savepoints[0].y + iSymbolSize; // bottom left
//            ptCenter[0] = ptCenter[1] = ptsBegin[0];
//            // locate begin point for Point1
//            iLengthCntPt1 = (int) Math.sqrt(
//                    (savepoints[1].x - ptsBegin[0].x) * (savepoints[1].x - ptsBegin[0].x) +
//                    (savepoints[1].y - ptsBegin[0].y) * (savepoints[1].y - ptsBegin[0].y));
//            for (i = 1; i < 4; i++) {
//                iLengthTemp = (int) Math.sqrt(
//                        (savepoints[1].x - ptsBegin[i].x) * (savepoints[1].x - ptsBegin[i].x) +
//                        (savepoints[1].y - ptsBegin[i].y) * (savepoints[1].y - ptsBegin[i].y));
//            }
//            // locate begin point for Point2
//            iLengthCntPt2 = (int) Math.sqrt(
//                    (savepoints[2].x - ptsBegin[0].x) * (savepoints[2].x - ptsBegin[0].x) +
//                    (savepoints[2].y - ptsBegin[0].y) * (savepoints[2].y - ptsBegin[0].y));
//            for (i = 1; i < 4; i++) {
//                iLengthTemp = (int) Math.sqrt(
//                        (savepoints[2].x - ptsBegin[i].x) * (savepoints[2].x - ptsBegin[i].x) +
//                        (savepoints[2].y - ptsBegin[i].y) * (savepoints[2].y - ptsBegin[i].y));
//            }
//            // calculate length of jaggy within the line
//            if (iLengthCntPt1 < iLengthCntPt2) {
//                iDelta = iLengthCntPt1 / 12;
//            } else {
//                iDelta = iLengthCntPt2 / 12;
//            }
//
//            //M. Deutch 8-18-04
//            if (iDelta > (int) maxLength) {
//                iDelta = (int) maxLength;
//            }
//            if (iDelta < (int) minLength) {
//                iDelta = (int) minLength;
//            }
//
//            // draw line from center to Point1
//            ptsJaggyLine[0] = ptCenter[0];
//            dAngle0 = Math.atan2(ptsJaggyLine[0].y - savepoints[1].y, ptsJaggyLine[0].x - savepoints[1].x);
//            pts[0].x = (ptsJaggyLine[0].x + savepoints[1].x) / 2;
//            pts[0].y = (ptsJaggyLine[0].y + savepoints[1].y) / 2;
//            dDeltaX0 = Math.cos(dAngle0 + CONST_PI / 4) * iDelta;
//            dDeltaY0 = Math.sin(dAngle0 + CONST_PI / 4) * iDelta;
//            ptsJaggyLine[1].x = pts[0].x - dDeltaX0;
//            ptsJaggyLine[1].y = pts[0].y - dDeltaY0;
//            ptsJaggyLine[2].x = pts[0].x + dDeltaX0;
//            ptsJaggyLine[2].y = pts[0].y + dDeltaY0;
//            ptsJaggyLine[3] = new POINT2(savepoints[1]);
//            //	DrawLine(destination, mask, color, ptsJaggyLine, 4, iLineThickness);
//            for (j = 0; j < 4; j++) {
//                points[counter] = new POINT2(ptsJaggyLine[j]);
//                //points[counter].style=0;
//                counter++;
//            }
//            points[counter - 1].style = 5;
//
//            // draw arrow at end of line
//            dDeltaX1 = Math.cos(dAngle0 - CONST_PI / 4) * iDelta;
//            dDeltaY1 = Math.sin(dAngle0 - CONST_PI / 4) * iDelta;
//            ptsJaggyLine[0].x = savepoints[1].x + dDeltaX0;
//            ptsJaggyLine[0].y = savepoints[1].y + dDeltaY0;
//            ptsJaggyLine[1] = new POINT2(savepoints[1]);
//            ptsJaggyLine[2].x = savepoints[1].x + dDeltaX1;
//            ptsJaggyLine[2].y = savepoints[1].y + dDeltaY1;
//            //	DrawFilledArrowhead(destination, mask, ptsJaggyLine, color);
//            for (j = 0; j < 3; j++) {
//                points[counter] = new POINT2(ptsJaggyLine[j]);
//                points[counter].style = 9;
//                counter++;
//            }
//            points[counter - 1].style = 10;
//
//            // draw line from center to Point2
//            ptsJaggyLine[0] = new POINT2(ptCenter[1]);
//            dAngle0 = Math.atan2(ptsJaggyLine[0].y - savepoints[2].y, ptsJaggyLine[0].x - savepoints[2].x);
//            //	dAngle0 = atan2(ptsJaggyLine[0].y - savepoints[2].y, ptsJaggyLine[0].x - savepoints[2].x);
//            pts[0].x = (ptsJaggyLine[0].x + savepoints[2].x) / 2;
//            pts[0].y = (ptsJaggyLine[0].y + savepoints[2].y) / 2;
//            dDeltaX0 = Math.cos(dAngle0 + CONST_PI / 4) * iDelta;
//            dDeltaY0 = Math.sin(dAngle0 + CONST_PI / 4) * iDelta;
//            ptsJaggyLine[1].x = pts[0].x - dDeltaX0;
//            ptsJaggyLine[1].y = pts[0].y - dDeltaY0;
//            ptsJaggyLine[2].x = pts[0].x + dDeltaX0;
//            ptsJaggyLine[2].y = pts[0].y + dDeltaY0;
//            ptsJaggyLine[3] = new POINT2(savepoints[2]);
//            //	DrawLine(destination, mask, color, ptsJaggyLine, 4, iLineThickness);
//            for (j = 0; j < 4; j++) {
//                points[counter] = new POINT2(ptsJaggyLine[j]);
//                //points[counter].style=0;
//                counter++;
//            }
//            points[counter - 1].style = 5;
//
//            // draw arrow at end of line
//            dDeltaX1 = Math.cos(dAngle0 - CONST_PI / 4) * iDelta;
//            dDeltaY1 = Math.sin(dAngle0 - CONST_PI / 4) * iDelta;
//            ptsJaggyLine[0].x = savepoints[2].x + dDeltaX0;
//            ptsJaggyLine[0].y = savepoints[2].y + dDeltaY0;
//            ptsJaggyLine[1] = new POINT2(savepoints[2]);
//            ptsJaggyLine[2].x = savepoints[2].x + dDeltaX1;
//            ptsJaggyLine[2].y = savepoints[2].y + dDeltaY1;
//            //	DrawFilledArrowhead(destination, mask, ptsJaggyLine, color);
//            for (j = 0; j < 3; j++) {
//                points[counter] = new POINT2(ptsJaggyLine[j]);
//                points[counter].style = 9;
//                counter++;
//            }
//            points[counter - 1].style = 10;
//            //clean up
//            pts = null;
//            savepoints = null;
//            ptCenter = null;
//            ptsBegin = null;
//            ptsJaggyLine = null;
//            //return;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return counter;
//    }

    /**
     * Calculates the points for CONTAIN
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMContainDouble(POINT2[] points, int linetype) {
        int counter = 0;
        try {
            POINT2[] pts = new POINT2[3];
            POINT2 ptCenter = new POINT2();
            POINT2 ptPerp = new POINT2(); // point used to draw perpendicular line
            double iPerpLength = 0;
            int j = 0;
            double dAngle1 = 0, d = 0;
            double dCosAngle1 = 0;
            double dSinAngle1 = 0;
            double iRadius = 0;
            double iDiagEOL_length = 0;
            double dAngle2 = 0;
            double dDeltaX1, dDeltaY1, dDeltaX2, dDeltaY2;
            POINT2[] savepoints = new POINT2[3];
            POINT2[] arcpoints = new POINT2[17];

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(arcpoints);

            ptCenter.x = (savepoints[0].x + savepoints[1].x) / 2;
            ptCenter.y = (savepoints[0].y + savepoints[1].y) / 2;

            //added section M. Deutch   8-10-06
            //reverse points 0 and 1 if necessary to ensure arc
            //has correct orientation
            ref<double[]> m = new ref();
            POINT2 ptRelative = lineutility.PointRelativeToLine(savepoints[0], savepoints[1], savepoints[2]);

            lineutility.CalcTrueSlopeDouble2(savepoints[0], savepoints[1], m);
            if (m.value[0] != 0) {
                if (savepoints[0].y > savepoints[1].y) {
                    if (ptRelative.x > ptCenter.x) {
                        lineutility.Reverse2Points(savepoints[0], savepoints[1]);
                    }
                }
                if (savepoints[0].y < savepoints[1].y) {
                    if (ptRelative.x < ptCenter.x) {
                        lineutility.Reverse2Points(savepoints[0], savepoints[1]);
                    }
                }
            } else {
                if (savepoints[0].x < savepoints[1].x) {
                    if (ptRelative.y > ptCenter.y) {
                        lineutility.Reverse2Points(savepoints[0], savepoints[1]);
                    }
                }
                if (savepoints[0].x > savepoints[1].x) {
                    if (ptRelative.y < ptCenter.y) {
                        lineutility.Reverse2Points(savepoints[0], savepoints[1]);
                    }
                }
            }
            //end section

            iPerpLength =  Math.sqrt((ptCenter.x - savepoints[2].x) * (ptCenter.x - savepoints[2].x) + (ptCenter.y - savepoints[2].y) * (ptCenter.y - savepoints[2].y));
            if (iPerpLength < 1) {
                iPerpLength = 1;
            }

            dAngle1 = Math.atan2(savepoints[0].y - savepoints[1].y, savepoints[0].x - savepoints[1].x);
            dCosAngle1 = Math.cos(dAngle1 + CONST_PI / 2);
            dSinAngle1 = Math.sin(dAngle1 + CONST_PI / 2);
            
            ptPerp.x = ptCenter.x + dCosAngle1 * iPerpLength;
            ptPerp.y = ptCenter.y + dSinAngle1 * iPerpLength;

            pts[0] = new POINT2(ptCenter);

            //diagnostic for CommandSight
            //pts[1] = new POINT2(ptPerp);
            pts[1] = new POINT2(savepoints[2]);

            //	DrawLineWithText(destination, mask, color, pts, 2, "ENY", scale, eraseBackground);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 14;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            // draw arrowhead
            iRadius =  Math.sqrt((ptCenter.x - savepoints[0].x) * (ptCenter.x - savepoints[0].x) + (ptCenter.y - savepoints[0].y) * (ptCenter.y - savepoints[0].y));
            iDiagEOL_length = (iPerpLength + iRadius) / 20;

            if (iDiagEOL_length >  maxLength) {
                iDiagEOL_length =  maxLength;
            }
            if (iDiagEOL_length <  minLength) { 
                iDiagEOL_length =  minLength;   
            }

            dAngle2 = Math.atan2(ptPerp.y - ptCenter.y, ptPerp.x - ptCenter.x);
            dDeltaX1 = Math.cos(dAngle2 + CONST_PI / 4);
            dDeltaY1 = Math.sin(dAngle2 + CONST_PI / 4);
            dDeltaX2 = Math.cos(dAngle2 - CONST_PI / 4);
            dDeltaY2 = Math.sin(dAngle2 - CONST_PI / 4);
            pts[0].x = ptCenter.x + dDeltaX1 * iDiagEOL_length;
            pts[0].y = ptCenter.y + dDeltaY1 * iDiagEOL_length;
            pts[1] = new POINT2(ptCenter);
            pts[2].x = ptCenter.x + dDeltaX2 * iDiagEOL_length;
            pts[2].y = ptCenter.y + dDeltaY2 * iDiagEOL_length;
            //end section
            for (j = 0; j < 3; j++) {
                points[counter] = new POINT2(pts[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;

            // draw arc
            ArcApproximationDouble(ptCenter.x - iRadius, ptCenter.y - iRadius,
                    ptCenter.x + iRadius, ptCenter.y + iRadius,
                    savepoints[0].x, savepoints[0].y, savepoints[1].x, savepoints[1].y, arcpoints);

            for (j = 0; j < 17; j++) {
                points[counter] = new POINT2(arcpoints[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;

            // draw spokes inside arc
            pts[0] = new POINT2(savepoints[0]);
            pts[1].x = (pts[0].x + ptCenter.x) / 2;
            pts[1].y = (pts[0].y + ptCenter.y) / 2;
            d = lineutility.CalcDistanceDouble(pts[0], pts[1]);
            if (d > maxLength) //shorten the spoke
            {
                pts[1] = lineutility.ExtendLineDouble(pts[1], pts[0], -maxLength);
            }

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0] = new POINT2(savepoints[1]);
            pts[1].x = (pts[0].x + ptCenter.x) / 2;
            pts[1].y = (pts[0].y + ptCenter.y) / 2;
            d = lineutility.CalcDistanceDouble(pts[0], pts[1]);
            if (d > maxLength) //shorten the spoke
            {
                pts[1] = lineutility.ExtendLineDouble(pts[1], pts[0], -maxLength);
            }
            //	DrawLine(destination, mask, color, pts, 2, 2);

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = ptCenter.x - (ptPerp.x - ptCenter.x) * iRadius / iPerpLength;
            pts[0].y = ptCenter.y - (ptPerp.y - ptCenter.y) * iRadius / iPerpLength;
            pts[1].x = (ptCenter.x + pts[0].x) / 2;
            pts[1].y = (ptCenter.y + pts[0].y) / 2;
            d = lineutility.CalcDistanceDouble(pts[0], pts[1]);
            if (d > maxLength) //shorten the spoke
            {
                pts[1] = lineutility.ExtendLineDouble(pts[1], pts[0], -maxLength);
            }

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = ptCenter.x - dDeltaX1 * iRadius;
            pts[0].y = ptCenter.y - dDeltaY1 * iRadius;
            pts[1].x = (ptCenter.x + pts[0].x) / 2;
            pts[1].y = (ptCenter.y + pts[0].y) / 2;
            d = lineutility.CalcDistanceDouble(pts[0], pts[1]);
            if (d > maxLength) //shorten the spoke
            {
                pts[1] = lineutility.ExtendLineDouble(pts[1], pts[0], -maxLength);
            }

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = ptCenter.x - dDeltaX2 * iRadius;
            pts[0].y = ptCenter.y - dDeltaY2 * iRadius;
            pts[1].x = (ptCenter.x + pts[0].x) / 2;
            pts[1].y = (ptCenter.y + pts[0].y) / 2;
            d = lineutility.CalcDistanceDouble(pts[0], pts[1]);
            if (d > maxLength) //shorten the spoke
            {
                pts[1] = lineutility.ExtendLineDouble(pts[1], pts[0], -maxLength);
            }

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            dDeltaX1 = Math.cos(dAngle2 + CONST_PI / 8);
            dDeltaY1 = Math.sin(dAngle2 + CONST_PI / 8);
            dDeltaX2 = Math.cos(dAngle2 - CONST_PI / 8);
            dDeltaY2 = Math.sin(dAngle2 - CONST_PI / 8);
            pts[0].x = ptCenter.x - dDeltaX1 * iRadius;
            pts[0].y = ptCenter.y - dDeltaY1 * iRadius;
            pts[1].x = (ptCenter.x + pts[0].x) / 2;
            pts[1].y = (ptCenter.y + pts[0].y) / 2;
            d = lineutility.CalcDistanceDouble(pts[0], pts[1]);
            if (d > maxLength) //shorten the spoke
            {
                pts[1] = lineutility.ExtendLineDouble(pts[1], pts[0], -maxLength);
            }

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = ptCenter.x - dDeltaX2 * iRadius;
            pts[0].y = ptCenter.y - dDeltaY2 * iRadius;
            pts[1].x = (ptCenter.x + pts[0].x) / 2;
            pts[1].y = (ptCenter.y + pts[0].y) / 2;
            d = lineutility.CalcDistanceDouble(pts[0], pts[1]);
            if (d > maxLength) //shorten the spoke
            {
                pts[1] = lineutility.ExtendLineDouble(pts[1], pts[0], -maxLength);
            }

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            dDeltaX1 = Math.cos(dAngle2 + 3 * CONST_PI / 8);
            dDeltaY1 = Math.sin(dAngle2 + 3 * CONST_PI / 8);
            dDeltaX2 = Math.cos(dAngle2 - 3 * CONST_PI / 8);
            dDeltaY2 = Math.sin(dAngle2 - 3 * CONST_PI / 8);
            pts[0].x = ptCenter.x - dDeltaX1 * iRadius;
            pts[0].y = ptCenter.y - dDeltaY1 * iRadius;
            pts[1].x = (ptCenter.x + pts[0].x) / 2;
            pts[1].y = (ptCenter.y + pts[0].y) / 2;
            d = lineutility.CalcDistanceDouble(pts[0], pts[1]);
            if (d > maxLength) //shorten the spoke
            {
                pts[1] = lineutility.ExtendLineDouble(pts[1], pts[0], -maxLength);
            }

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = ptCenter.x - dDeltaX2 * iRadius;
            pts[0].y = ptCenter.y - dDeltaY2 * iRadius;
            pts[1].x = (ptCenter.x + pts[0].x) / 2;
            pts[1].y = (ptCenter.y + pts[0].y) / 2;
            d = lineutility.CalcDistanceDouble(pts[0], pts[1]);
            if (d > maxLength) //shorten the spoke
            {
                pts[1] = lineutility.ExtendLineDouble(pts[1], pts[0], -maxLength);
            }

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            //clean up
            pts = null;
            savepoints = null;
            arcpoints = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMContainDouble",
                    new RendererException("Failed inside GetDISMContainDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for FIX, MNFLDFIX
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMFixDouble(POINT2[] points, int linetype,Rectangle2D clipBounds) {
        int counter = 0;
        try {
            POINT2[] pts = new POINT2[3];
            POINT2[] savepoints = new POINT2[2];
            double dAngle1 = 0;
            double dLength = 0;
            double dJaggyHalfAmp = 0;
            double dJaggyHalfPeriod = 0;
            double dDeltaXOut = 0;
            double dDeltaYOut = 0;
            double dDeltaXAlong = 0;
            double dDeltaYAlong = 0;
            int iNumJaggies = 0;
            int i = 0, j = 0;

            for (j = 0; j < 2; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            Boolean drawJaggies=true;
            if(clipBounds != null)
            {
                POINT2 ul=new POINT2(clipBounds.getMinX(),clipBounds.getMinY());
                POINT2 lr=new POINT2(clipBounds.getMaxX(),clipBounds.getMaxY());
                savepoints=lineutility.BoundOneSegment(savepoints[0], savepoints[1], ul, lr);
            }
            if(savepoints==null)
            {
                savepoints=new POINT2[2];
                for (j = 0; j < 2; j++) {
                    savepoints[j] = new POINT2(points[j]);
                }
                drawJaggies=false;
            }

            lineutility.InitializePOINT2Array(pts);
            //reverse the points

            dAngle1 = Math.atan2(savepoints[0].y - savepoints[1].y, savepoints[0].x - savepoints[1].x);
            dLength = Math.sqrt((savepoints[1].x - savepoints[0].x) * (savepoints[1].x - savepoints[0].x) +
                    (savepoints[1].y - savepoints[0].y) * (savepoints[1].y - savepoints[0].y));
            //arraysupport tries to set jaggylength before the points get bounded
            dJaggyHalfAmp = dLength / 15; // half the amplitude of the "jaggy function"

            if (dJaggyHalfAmp > maxLength) {
                dJaggyHalfAmp = maxLength;
            }
            if (dJaggyHalfAmp < minLength) {  
                dJaggyHalfAmp = minLength;    
            }

            dJaggyHalfPeriod = dJaggyHalfAmp / 1.5; // half the period of the "jaggy function"
            dDeltaXOut = Math.cos(dAngle1 + CONST_PI / 2) * dJaggyHalfAmp; // X-delta out from the center line
            dDeltaYOut = Math.sin(dAngle1 + CONST_PI / 2) * dJaggyHalfAmp; // Y-delta out from the center line
            dDeltaXAlong = Math.cos(dAngle1) * dJaggyHalfPeriod; // X-delta along the center line
            dDeltaYAlong = Math.sin(dAngle1) * dJaggyHalfPeriod; // Y-delta along the center line
            iNumJaggies = (int) (dLength / dJaggyHalfPeriod) - 3;
            i = 2;
            pts[0] = new POINT2(savepoints[1]);
            pts[1].x = savepoints[1].x + dDeltaXAlong * 1.5;
            pts[1].y = savepoints[1].y + dDeltaYAlong * 1.5;
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = savepoints[1].x + dDeltaXOut + dDeltaXAlong * i;
            pts[0].y = savepoints[1].y + dDeltaYOut + dDeltaYAlong * i;
            i++;
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            if(drawJaggies)
            while (i <= iNumJaggies) {
                pts[1].x = savepoints[1].x - dDeltaXOut + dDeltaXAlong * i;
                pts[1].y = savepoints[1].y - dDeltaYOut + dDeltaYAlong * i;
                i++;
                pts[2].x = savepoints[1].x + dDeltaXOut + dDeltaXAlong * i;
                pts[2].y = savepoints[1].y + dDeltaYOut + dDeltaYAlong * i;
                i++;
                //DrawLine(destination, mask, color, pts, 3, 2);
                for (j = 0; j < 3; j++) {
                    points[counter] = new POINT2(pts[j]);
                    points[counter].style = 0;
                    counter++;
                }
                points[counter - 1].style = 5;
                pts[0] = new POINT2(pts[2]);
            }

            pts[1] = new POINT2(pts[0]);
            pts[0].x = savepoints[1].x + dDeltaXAlong * i;
            pts[0].y = savepoints[1].y + dDeltaYAlong * i;
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[1] = new POINT2(savepoints[0]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            // draw arrowhead
            pts[0].x = savepoints[0].x + dDeltaXOut / 1.5 - dDeltaXAlong;
            pts[0].y = savepoints[0].y + dDeltaYOut / 1.5 - dDeltaYAlong;
            pts[2].x = savepoints[0].x - dDeltaXOut / 1.5 - dDeltaXAlong;
            pts[2].y = savepoints[0].y - dDeltaYOut / 1.5 - dDeltaYAlong;
            for (j = 0; j < 3; j++) {
                points[counter] = new POINT2(pts[j]);
                if (linetype == (long) TacticalLines.MNFLDFIX) {
                    points[counter].style = 9;
                } else {
                    points[counter].style = 0;
                }
                counter++;
            }
            if (linetype == (long) TacticalLines.MNFLDFIX) {
                points[counter - 1].style = 10;
            } else {
                points[counter - 1].style = 5;
            }

            //clean up
            pts = null;
            savepoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMFixDouble",
                    new RendererException("Failed inside GetDISMFixDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for CLEAR.
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMClearDouble(POINT2[] points, int linetype) {
        int counter = 0;
        try {
            POINT2[] savepoints = new POINT2[3];
            int j = 0;
            POINT2[] pts = new POINT2[2];
            POINT2[] ptsArrow = new POINT2[3];
            double ctrX =  ((points[0].x + points[1].x) / 2);
            double ctrY =  ((points[0].y + points[1].y) / 2);
            ref<double[]> iDeltaX1 = new ref(), iDeltaY1 = new ref(), iDeltaX2 = new ref(), iDeltaY2 = new ref();
            POINT2[] deltapoints1 = new POINT2[4];
            POINT2[] deltapoints2 = new POINT2[4];
            POINT2[] deltapoints3 = new POINT2[4];

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(ptsArrow);
            lineutility.InitializePOINT2Array(deltapoints1);
            lineutility.InitializePOINT2Array(deltapoints2);
            lineutility.InitializePOINT2Array(deltapoints3);

            //DrawLine(destination, mask, color, points, 2, 2);
            points[counter] = new POINT2(savepoints[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(savepoints[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = ctrX;
            pts[0].y = ctrY;
            pts[1] = new POINT2(savepoints[2]);
            ptsArrow[0] = new POINT2(pts[0]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = (savepoints[0].x + ctrX) / 2;
            pts[0].y = (savepoints[0].y + ctrY) / 2;
            pts[1].x = savepoints[2].x + savepoints[0].x - pts[0].x;
            pts[1].y = savepoints[2].y + savepoints[0].y - pts[0].y;
            ptsArrow[1] = new POINT2(pts[0]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = (savepoints[1].x + ctrX) / 2;
            pts[0].y = (savepoints[1].y + ctrY) / 2;
            pts[1].x = savepoints[2].x + savepoints[1].x - pts[0].x;
            pts[1].y = savepoints[2].y + savepoints[1].y - pts[0].y;
            ptsArrow[2] = new POINT2(pts[0]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            CalcEndpieceDeltasDouble(savepoints, iDeltaX1, iDeltaY1, CONST_PI / 6);
            CalcEndpieceDeltasDouble(savepoints, iDeltaX2, iDeltaY2, -CONST_PI / 6);
            DrawEndpieceDeltasDouble(ptsArrow[0],
                    iDeltaX1.value[0], iDeltaY1.value[0], iDeltaX2.value[0], iDeltaY2.value[0], deltapoints1);
            DrawEndpieceDeltasDouble(ptsArrow[1],
                    iDeltaX1.value[0], iDeltaY1.value[0], iDeltaX2.value[0], iDeltaY2.value[0], deltapoints2);
            DrawEndpieceDeltasDouble(ptsArrow[2],
                    iDeltaX1.value[0], iDeltaY1.value[0], iDeltaX2.value[0], iDeltaY2.value[0], deltapoints3);
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints1[j]);
                counter++;
            }
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints2[j]);
                counter++;
            }
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints3[j]);
                counter++;
            }
            //clean up
            pts = null;
            savepoints = null;
            ptsArrow = null;
            deltapoints1 = null;
            deltapoints2 = null;
            deltapoints3 = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMClearDouble",
                    new RendererException("Failed inside GetDISMClearDouble", exc));
        }
        return counter;
    }
    private static boolean IsSeizeArcReversed(POINT2[] pPoints){
        try {
            double dAngle1 = Math.atan2(pPoints[0].y - pPoints[1].y, pPoints[0].x - pPoints[1].x);
            double dDeltaX1 = Math.cos(dAngle1 + CONST_PI / 4);
            double dDeltaY1 = Math.sin(dAngle1 + CONST_PI / 4);
            double dDeltaX2 = Math.cos(dAngle1 - CONST_PI / 4);
            double dDeltaY2 = Math.sin(dAngle1 - CONST_PI / 4);

            double dChordLength = Math.sqrt((pPoints[1].x - pPoints[0].x) * (pPoints[1].x - pPoints[0].x) +
                    (pPoints[1].y - pPoints[0].y) * (pPoints[1].y - pPoints[0].y));
            double dArcRadius = dChordLength / 1.414213562373; // sqrt(2) == 1.414213562373
            POINT2 ptArcCenter = new POINT2();

            //get the default center
            ptArcCenter.x = pPoints[0].x - dDeltaX1 * dArcRadius;
            ptArcCenter.y = pPoints[0].y - dDeltaY1 * dArcRadius;
            double d = lineutility.CalcDistanceDouble(ptArcCenter, pPoints[2]);

            //get the alternate center if the arc is reversed
            POINT2 ptArcCenterReversed = new POINT2();
            ptArcCenterReversed.x = pPoints[0].x - dDeltaX2 * dArcRadius;
            ptArcCenterReversed.y = pPoints[0].y - dDeltaY2 * dArcRadius;
            double dReversed = lineutility.CalcDistanceDouble(ptArcCenterReversed, pPoints[2]);

            if (dReversed > d) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"IsSeizeArcReversed",
                    new RendererException("Failed inside IsSeizeArcReversed", exc));
        }
        return false;
    }
    /**
     * Calculates the points for SEIZE
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMSeizeDouble(POINT2[] points,
            int linetype,
            double radius) 
    {
        int counter = 0;
        try {
            POINT2 ptArcCenter = new POINT2();
            POINT2 ptArcStart = new POINT2();
            POINT2[] savepoints = new POINT2[3];
            float scale = (float) 0.9;
            double iCircleRadius =  (25 * scale);
            POINT2[] arcpoints = new POINT2[17];
            POINT2[] pts = new POINT2[3];
            double dAngle1 = 0;
            double dDeltaX1 = 0;
            double dDeltaY1 = 0;
            double dDeltaX2 = 0;
            double dDeltaY2 = 0;
            double dChordLength = 0;
            double dArcRadius = 0;
            int j = 0;
            double dDeltaX3 = 0;
            double dDeltaY3 = 0;
            double iDiagEOL_length = 0;
            double factor = 1;

            if(radius>0)
                iCircleRadius=radius;
            
            
            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
                //if(points.Length>2)
                //	savepoints[2]=points[j];
            }
            
            //if radius is 0 then it is rev B
            String client=CELineArray.getClient();
            if(!client.startsWith("cpof") && radius==0)
            {
                dArcRadius=lineutility.CalcDistanceDouble(savepoints[0], savepoints[1]);
                if(iCircleRadius>dArcRadius/2)
                    iCircleRadius=dArcRadius/2;
            }

            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(arcpoints);
            // draw circle
            ArcApproximationDouble(savepoints[0].x - iCircleRadius, savepoints[0].y - iCircleRadius,
                    savepoints[0].x + iCircleRadius, savepoints[0].y + iCircleRadius,
                    savepoints[0].x, savepoints[0].y, savepoints[0].x, savepoints[0].y, arcpoints);
            for (j = 0; j < 17; j++) {
                points[counter] = new POINT2(arcpoints[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;

            // draw arc
            dAngle1 = Math.atan2(savepoints[0].y - savepoints[1].y, savepoints[0].x - savepoints[1].x);
            dDeltaX1 = Math.cos(dAngle1 + CONST_PI / 4);
            dDeltaY1 = Math.sin(dAngle1 + CONST_PI / 4);
            dDeltaX2 = Math.cos(dAngle1 - CONST_PI / 4);
            dDeltaY2 = Math.sin(dAngle1 - CONST_PI / 4);

            boolean isArcReversed = IsSeizeArcReversed(savepoints);
            //bool isArcReversed = false;

            if (isArcReversed == false) {
                ptArcStart.x = savepoints[0].x - dDeltaX2 * iCircleRadius;
                ptArcStart.y = savepoints[0].y - dDeltaY2 * iCircleRadius;
                dChordLength = Math.sqrt((savepoints[1].x - savepoints[0].x) * (savepoints[1].x - savepoints[0].x) +
                        (savepoints[1].y - savepoints[0].y) * (savepoints[1].y - savepoints[0].y));
                dArcRadius = dChordLength / 1.414213562373; // sqrt(2) == 1.414213562373
                ptArcCenter.x = savepoints[0].x - dDeltaX1 * dArcRadius;
                ptArcCenter.y = savepoints[0].y - dDeltaY1 * dArcRadius;

                ArcApproximationDouble((ptArcCenter.x - dArcRadius), (ptArcCenter.y - dArcRadius),
                        (ptArcCenter.x + dArcRadius), (ptArcCenter.y + dArcRadius),
                        savepoints[1].x, savepoints[1].y, ptArcStart.x, ptArcStart.y, arcpoints);
            } else //arc is reversed
            {
                ptArcStart.x = savepoints[0].x - dDeltaX1 * iCircleRadius;
                ptArcStart.y = savepoints[0].y - dDeltaY1 * iCircleRadius;
                dChordLength = Math.sqrt((savepoints[1].x - savepoints[0].x) * (savepoints[1].x - savepoints[0].x) +
                        (savepoints[1].y - savepoints[0].y) * (savepoints[1].y - savepoints[0].y));
                dArcRadius = dChordLength / 1.414213562373; // sqrt(2) == 1.414213562373
                ptArcCenter.x = savepoints[0].x - dDeltaX2 * dArcRadius;
                ptArcCenter.y = savepoints[0].y - dDeltaY2 * dArcRadius;
                ArcApproximationDouble((ptArcCenter.x - dArcRadius), (ptArcCenter.y - dArcRadius),
                        (ptArcCenter.x + dArcRadius), (ptArcCenter.y + dArcRadius),
                        ptArcStart.x, ptArcStart.y, savepoints[1].x, savepoints[1].y, arcpoints);
            }
            //end diagnostic

            for (j = 0; j < 17; j++) {
                points[counter] = new POINT2(arcpoints[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;

            // draw arrow
            if (dChordLength / 8 > maxLength) {
                factor = dChordLength / (8 * maxLength);
            }
            if (factor == 0) {
                factor = 1;
            }


            if (isArcReversed == false) {
                pts[0].x = savepoints[1].x - (savepoints[1].x - savepoints[0].x) / (8 * factor);
                pts[0].y = savepoints[1].y - (savepoints[1].y - savepoints[0].y) / (8 * factor);
                pts[1] = new POINT2(savepoints[1]);
                dDeltaX3 = Math.cos(dAngle1 + CONST_PI / 2);
                dDeltaY3 = Math.sin(dAngle1 + CONST_PI / 2);
                iDiagEOL_length =  (dChordLength / 8);
                pts[2].x = savepoints[1].x + dDeltaX3 * iDiagEOL_length / factor;
                pts[2].y = savepoints[1].y + dDeltaY3 * iDiagEOL_length / factor;
            } //DrawLine(destination, mask, color, pts, 3, 2);
            //diagnostic arc reversed
            else {
                pts[0].x = savepoints[1].x - (savepoints[1].x - savepoints[0].x) / (8 * factor);
                pts[0].y = savepoints[1].y - (savepoints[1].y - savepoints[0].y) / (8 * factor);
                pts[1] = new POINT2(savepoints[1]);
                dDeltaX3 = Math.cos(dAngle1 - CONST_PI / 2);
                dDeltaY3 = Math.sin(dAngle1 - CONST_PI / 2);
                iDiagEOL_length =  (dChordLength / 8);
                pts[2].x = savepoints[1].x + dDeltaX3 * iDiagEOL_length / factor;
                pts[2].y = savepoints[1].y + dDeltaY3 * iDiagEOL_length / factor;
            }
            //end diagnostic


            //diagnostic
            //end diagnostic

            for (j = 0; j < 3; j++) {
                points[counter] = new POINT2(pts[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;
            //clean up
            savepoints = null;
            arcpoints = null;
            pts = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMSeizeDouble",
                    new RendererException("Failed inside GetDISMSeizeDouble", exc));
        }
        return counter;
    }
    /**
     * Used twice for RIP to determine if the points are clockwise.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param px
     * @param py
     * @return RIGHT_SIDE if 3 points are clockwise
     */
    private static int side(double x1, double y1, double x2, double y2, double px, double py) {
        double dx1, dx2, dy1, dy2;
        try {
            double o;

            dx1 = x2 - x1;
            dy1 = y2 - y1;
            dx2 = px - x1;
            dy2 = py - y1;
            o = (dx1 * dy2) - (dy1 * dx2);
            if (o > 0.0) {
                return (LEFT_SIDE);
            }
            if (o < 0.0) {
                return (RIGHT_SIDE);
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "side",
                    new RendererException("Failed inside side", exc));
        }
        return (COLINEAR);
    }

    /**
     * Calculates the points for RIP
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type
     */
    protected static int GetDISMRIPDouble(POINT2[] points, int linetype) {
        int counter = 0;
        try {
            // draw the straight lines
            POINT2[] pts = new POINT2[2];
            POINT2[] savepoints = new POINT2[4];
            int j = 0;
            double iLengthPt0Pt1 = 0;
            double iDiagEOL_length = 0;
            double dAngle1 = 0;
            double iDeltaX1 = 0;
            double iDeltaY1 = 0;
            double iDeltaX2 = 0;
            double iDeltaY2 = 0;
            double iLengthPt2Pt3 = 0;
            double iRadius = 0;
            POINT2[] deltapoints = new POINT2[4];
            POINT2[] arcpoints = new POINT2[17];
            POINT2 ptArcCenter = new POINT2();

            boolean clockwise=false;
            int side01=side(points[0].x,points[0].y,points[1].x,points[1].y,points[2].x,points[2].y);
            int side12=side(points[1].x,points[1].y,points[2].x,points[2].y,points[3].x,points[3].y);
            if(side01==RIGHT_SIDE && side12==RIGHT_SIDE)
                clockwise=true;
            else if(side01==RIGHT_SIDE && side12==COLINEAR)
                clockwise=true;
            else if(side01==COLINEAR && side12==RIGHT_SIDE)
                clockwise=true;

            for (j = 0; j < 4; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(deltapoints);
            lineutility.InitializePOINT2Array(arcpoints);

            //DrawLine(destination, mask, color, points, 2, 2);
            points[counter] = new POINT2(savepoints[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(savepoints[1]);
            points[counter].style = 5;
            counter++;

            pts[0] = new POINT2(savepoints[2]);
            pts[1] = new POINT2(savepoints[3]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            // draw the arrowhead on line between savepoints 0 and 1
            pts[0] = new POINT2(savepoints[0]);
            pts[1] = new POINT2(savepoints[1]);
            iLengthPt0Pt1 =  Math.sqrt((pts[1].x - pts[0].x) * (pts[1].x - pts[0].x) +
                    (pts[1].y - pts[0].y) * (pts[1].y - pts[0].y));
            iDiagEOL_length = iLengthPt0Pt1 / 8;

            //M. Deutch 8-19-04
            if (iDiagEOL_length >  maxLength) {
                iDiagEOL_length =  maxLength;
            }
            if (iDiagEOL_length <  minLength) {
                iDiagEOL_length =  minLength;
            }

            dAngle1 = Math.atan2(pts[1].y - pts[0].y, pts[1].x - pts[0].x);
            iDeltaX1 =  (iDiagEOL_length * Math.cos(dAngle1 - CONST_PI / 4));
            iDeltaY1 =  (iDiagEOL_length * Math.sin(dAngle1 - CONST_PI / 4));
            iDeltaX2 =  (iDiagEOL_length * Math.cos(dAngle1 + CONST_PI / 4));
            iDeltaY2 =  (iDiagEOL_length * Math.sin(dAngle1 + CONST_PI / 4));
            DrawEndpieceDeltasDouble(pts[0],
                    iDeltaX1, iDeltaY1, iDeltaX2, iDeltaY2, deltapoints);
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 3].style = 5;
            points[counter - 1].style = 5;
            // draw the arrowhead on line between savepoints 2 and 3
            pts[0] = new POINT2(savepoints[2]);
            pts[1] = new POINT2(savepoints[3]);
            iLengthPt2Pt3 =  Math.sqrt((pts[1].x - pts[0].x) * (pts[1].x - pts[0].x) +
                    (pts[1].y - pts[0].y) * (pts[1].y - pts[0].y));
            iDiagEOL_length = iLengthPt2Pt3 / 8;

            //M. Deutch 8-19-04
            if (iDiagEOL_length >  maxLength) {
                iDiagEOL_length =  maxLength;
            }
            if (iDiagEOL_length <  minLength) {
                iDiagEOL_length =  minLength;
            }

            dAngle1 = Math.atan2(pts[1].y - pts[0].y, pts[1].x - pts[0].x);
            iDeltaX1 =  (iDiagEOL_length * Math.cos(dAngle1 - CONST_PI / 4));
            iDeltaY1 =  (iDiagEOL_length * Math.sin(dAngle1 - CONST_PI / 4));
            iDeltaX2 =  (iDiagEOL_length * Math.cos(dAngle1 + CONST_PI / 4));
            iDeltaY2 =  (iDiagEOL_length * Math.sin(dAngle1 + CONST_PI / 4));
            DrawEndpieceDeltasDouble(pts[0],
                    iDeltaX1, iDeltaY1, iDeltaX2, iDeltaY2, deltapoints);
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(deltapoints[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 3].style = 5;
            points[counter - 1].style = 5;

            // draw the semicircle
            iRadius =  (Math.sqrt((savepoints[2].x - savepoints[1].x) * (savepoints[2].x - savepoints[1].x) +
                    (savepoints[2].y - savepoints[1].y) * (savepoints[2].y - savepoints[1].y)) / 2);
            ptArcCenter.x = (savepoints[1].x + savepoints[2].x) / 2;
            ptArcCenter.y = (savepoints[1].y + savepoints[2].y) / 2;

            //diagnostic
            if(clockwise==false)
            {
                ArcApproximationDouble((ptArcCenter.x - iRadius), (ptArcCenter.y - iRadius),
                    (ptArcCenter.x + iRadius), (ptArcCenter.y + iRadius),
                    savepoints[2].x, savepoints[2].y, savepoints[1].x, savepoints[1].y, arcpoints);
            }
            else
            {
                ArcApproximationDouble((ptArcCenter.x - iRadius), (ptArcCenter.y - iRadius),
                    (ptArcCenter.x + iRadius), (ptArcCenter.y + iRadius),
                    savepoints[1].x, savepoints[1].y, savepoints[2].x, savepoints[2].y, arcpoints);
            }
            for (j = 0; j < 17; j++) {
                points[counter] = new POINT2(arcpoints[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;
            //clean up
            pts = null;
            savepoints = null;
            deltapoints = null;
            arcpoints = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMRIPDouble",
                    new RendererException("Failed inside GetDISMRIPDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for BYDIF
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMByDifDouble(POINT2[] points,
            int linetype,
            Rectangle2D clipBounds) {
        int counter = 0;
        try {
            POINT2[] pointsCorner = new POINT2[2];
            POINT2[] rectpts = new POINT2[4];
            POINT2[] savepoints = new POINT2[3], savepoints2 = new POINT2[2];
            POINT2[] deltapoints1 = new POINT2[4];
            POINT2[] deltapoints2 = new POINT2[4];
            POINT2[] pts = new POINT2[3];
            //POINT2 pt0 = new POINT2();
            //POINT2 pt1 = new POINT2();
            ref<double[]> iDeltaX = new ref(), iDeltaY = new ref();
            int bPointsRight = 0;
            double dAngle1 = 0;
            double dLength = 0;
            double dJaggyHalfAmp = 0;
            double dJaggyHalfPeriod = 0;
            double dDeltaXOut = 0;
            double dDeltaYOut = 0;
            double dDeltaXAlong = 0;
            double dDeltaYAlong = 0;
            int iNumJaggies = 0;
            int i = 0, j = 0;
            //int pointcounter = 0;
            //int[] segments = null;
            //end declarations
            //lineutility.WriteFile("made it this far");
            //ok to here

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pointsCorner);
            lineutility.InitializePOINT2Array(rectpts);
            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(deltapoints1);
            lineutility.InitializePOINT2Array(deltapoints2);

            DrawOpenRectangleDouble(savepoints, pointsCorner, rectpts);
            //save the back side for use by the jagged line
            savepoints2[0] = new POINT2(rectpts[1]);
            savepoints2[1] = new POINT2(rectpts[2]);

            //diagnostic these hard coded because JavalineArray does not know the bounds
            if(clipBounds != null)
            {
                POINT2 ul=new POINT2(clipBounds.getMinX(),clipBounds.getMinY());
                POINT2 lr=new POINT2(clipBounds.getMaxX(),clipBounds.getMaxY());
                savepoints2=lineutility.BoundOneSegment(savepoints2[0], savepoints2[1], ul, lr);
            }
            Boolean drawJaggies=true;
            if(savepoints2==null)
            {
                savepoints2 = new POINT2[2];
                savepoints2[0] = new POINT2(rectpts[1]);
                savepoints2[1] = new POINT2(rectpts[2]);
                drawJaggies=false;
            }
            //lineutility.WriteFile("made it this far");
            //ok to here

            //M. Deutch 10-27-10
            //pointcounter = lineutility.BoundPointsCount(savepoints2, 2);
            //pointcounter=countsupport.GetDISMFixCountDouble(savepoints2[0],savepoints2[1]);
            //removed section 10-27-10
//            segments = new int[pointcounter];
//            savepoints2 = new POINT2[pointcounter];
//            //reload savepoints2
//            savepoints2[0] = rectpts[1];
//            savepoints2[1] = rectpts[2];
//            pt0 = rectpts[1];
//            pt1 = rectpts[2];
//            boolean bolFound = false;
//            lineutility.BoundPoints(savepoints2, 2, segments);
//            for (j = 0; j < pointcounter - 1; j++) {
//                if (segments[j] == 1) {
//                    pt0 = new POINT2(savepoints2[j]);
//                    pt1 = new POINT2(savepoints2[j + 1]);
//                    bolFound = true;
//                    break;
//                }
//            }
//            savepoints2 = null;
//            savepoints2 = new POINT2[2];
//            savepoints2[0] = new POINT2(pt0);
//            savepoints2[1] = new POINT2(pt1);
//            //end section
//
//            if (bolFound == false) {
//                counter = GetDISMEasyDouble(points, linetype);
//                for (j = 12; j < points.length; j++) {
//                    points[j].style = 5;
//                }
//
//                //cleanup
//                pointsCorner = null;
//                rectpts = null;
//                savepoints = null;
//                savepoints2 = null;
//                deltapoints1 = null;
//                deltapoints2 = null;
//                pts = null;
//                segments = null;
//                return counter;
//            }

            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(rectpts[j]);
                points[counter].style = 0;
                counter++;
            }
            points[1].style = 5;
            points[counter - 1].style = 5;

            dAngle1 = Math.atan2(savepoints2[0].y - savepoints2[1].y, savepoints2[0].x - savepoints2[1].x);
            dLength = Math.sqrt((savepoints2[1].x - savepoints2[0].x) * (savepoints2[1].x - savepoints2[0].x) +
                    (savepoints2[1].y - savepoints2[0].y) * (savepoints2[1].y - savepoints2[0].y));
            dJaggyHalfAmp = dLength / 15; // half the amplitude of the "jaggy function"

            //M. Deutch 8-18-04
            if (dJaggyHalfAmp > maxLength) {
                dJaggyHalfAmp = maxLength;
            }
            if (dJaggyHalfAmp < minLength) {
                dJaggyHalfAmp = minLength;
            }

            dJaggyHalfPeriod = dJaggyHalfAmp / 1.5; // half the period of the "jaggy function"
            dDeltaXOut = Math.cos(dAngle1 + CONST_PI / 2) * dJaggyHalfAmp; // X-delta out from the center line
            dDeltaYOut = Math.sin(dAngle1 + CONST_PI / 2) * dJaggyHalfAmp; // Y-delta out from the center line
            dDeltaXAlong = Math.cos(dAngle1) * dJaggyHalfPeriod; // X-delta along the center line
            dDeltaYAlong = Math.sin(dAngle1) * dJaggyHalfPeriod; // Y-delta along the center line

            iNumJaggies = (int) (dLength / dJaggyHalfPeriod) - 3;
            i = 2;
            pts[0] = new POINT2(savepoints2[1]);
            pts[1].x = savepoints2[1].x + dDeltaXAlong * 1.5;
            pts[1].y = savepoints2[1].y + dDeltaYAlong * 1.5;
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = savepoints2[1].x + dDeltaXOut + dDeltaXAlong * i;
            pts[0].y = savepoints2[1].y + dDeltaYOut + dDeltaYAlong * i;
            i++;
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            //lineutility.WriteFile("made it this far");
            //ok to here
            //lineutility.WriteFile("length = " + Double.toString(dLength) + "numJaggies =  " +
                    //Integer.toString(iNumJaggies) + "points size = " + Integer.toString(points.length));
            
            if(drawJaggies)//diagnostic
            while (i <= iNumJaggies) {
                pts[1].x = savepoints2[1].x - dDeltaXOut + dDeltaXAlong * i;
                pts[1].y = savepoints2[1].y - dDeltaYOut + dDeltaYAlong * i;
                i++;
                pts[2].x = savepoints2[1].x + dDeltaXOut + dDeltaXAlong * i;
                pts[2].y = savepoints2[1].y + dDeltaYOut + dDeltaYAlong * i;
                i++;
                for (j = 0; j < 3; j++) {
                    points[counter] = new POINT2(pts[j]);
                    points[counter].style = 0;
                    counter++;
                }
                points[counter - 1].style = 5;
                pts[0] = new POINT2(pts[2]);
            }

            //lineutility.WriteFile("made it this far " + Double.toString(dLength));

            pts[1] = new POINT2(pts[0]);
            pts[0].x = savepoints2[1].x + dDeltaXAlong * i;
            pts[0].y = savepoints2[1].y + dDeltaYAlong * i;
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[1] = new POINT2(savepoints2[0]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            //lineutility.WriteFile("made it this far");
            //did not get this far

            bPointsRight = DetermineDirectionDouble(savepoints);

            CalcEndpieceDeltasDouble(savepoints, iDeltaX, iDeltaY, CONST_PI / 4);

            if ((savepoints[0].y - savepoints[1].y) < 0) {// Point0 is higher than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints2);
                }
            } else {// Point0 is lower than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints2);
                }
            }
            //lineutility.WriteFile("made it this far");
            points[counter] = new POINT2(deltapoints1[1]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[0]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[3]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[3]);
            points[counter].style = 10;
            counter++;

            points[counter] = new POINT2(deltapoints2[1]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[0]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[3]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[3]);
            points[counter].style = 10;
            counter++;

            //clean up
            pointsCorner = null;
            rectpts = null;
            savepoints = null;
            savepoints2 = null;
            deltapoints1 = null;
            deltapoints2 = null;
            pts = null;
            //segments = null;
            //return;
        } catch (Exception exc) {
            //lineutility.WriteFile(exc.getMessage());
            ErrorLogger.LogException(_className ,"GetDISMByDifDouble",
                    new RendererException("Failed inside GetDISMByDifDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for PENETRATE
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static void GetDISMPenetrateDouble(POINT2[] points, int linetype) {
        try {
            POINT2[] arrowpts = new POINT2[3];
            POINT2 midpt = new POINT2();
            POINT2[] savepoints = new POINT2[3];
            //int nQuadrant=0,j=0;
            int j = 0;
            double d = 0;
            //end declarations

            for (j = 0; j < 3; j++) {
                //savepoints[j].x=points[j].x;
                //savepoints[j].y=points[j].y;
                savepoints[j] = new POINT2(points[j]);
            }
            lineutility.InitializePOINT2Array(arrowpts);

            points[0].x = savepoints[0].x;
            points[0].y = savepoints[0].y;
            points[0].style = 0;
            points[1].x = savepoints[1].x;
            points[1].y = savepoints[1].y;
            points[1].style = 5;

            midpt = lineutility.MidPointDouble(savepoints[0], savepoints[1], 0);

            //comment one line for CommandSight
            //points[2] = lineutility.PointRelativeToLine(savepoints[0], savepoints[1], savepoints[2]);
            //uncomment one line for CommandSight
            points[2] = new POINT2(savepoints[2]);

            points[3] = new POINT2(midpt);
            points[3].style = 5;
            d = lineutility.MBRDistance(savepoints, 3);

            //M. Deutch 8-19-04
            if (d / 5 > maxLength) {
                d = 5 * maxLength;
            }
            if (d / 5 < minLength) {  
                d = 5 * minLength;    
            }
//            if(d<400)
//                d=400;
            String client=CELineArray.getClient();
            if(client.matches("cpof3d") || client.matches("cpof2d"))
            {
                if(d<400)
                    d=400;                
            }
            else
            {
                if(d<150)
                    d=150;                
            }
            if(d>600)
                d=600;

            lineutility.GetArrowHead4Double(points[2], points[3], (int)d / 20,(int)d / 20, arrowpts, 0);
            for (j = 0; j < 3; j++) {
                points[4 + j] = new POINT2(arrowpts[j]);
            }

            //clean up
            arrowpts = null;
            savepoints = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMPenetrateDouble",
                    new RendererException("Failed inside GetDISMPenetrateDouble", exc));
        }
        return;
    }
    /**
     * Calculates the points for BYIMP
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMByImpDouble(POINT2[] points,
            int linetype) {
        int counter = 0;
        try {
            int j = 0;
            POINT2[] pointsCorner = new POINT2[2];
            POINT2[] rectpts = new POINT2[4];
            POINT2[] savepoints = new POINT2[3];
            POINT2[] deltapoints1 = new POINT2[4];
            POINT2[] deltapoints2 = new POINT2[4];
            POINT2 midpt = new POINT2();
            POINT2[] pts = new POINT2[6];
            POINT2 ptRelative = new POINT2();
            ref<double[]> iDeltaX = new ref(), iDeltaY = new ref();
            int bPointsRight = 0;
            double dMBR = lineutility.MBRDistance(points, 3);
            //end declarations

            //M. Deutch 8-18-04
            if (dMBR > 40 * maxLength) {
                dMBR = 40 * maxLength;
            }
            if (dMBR < 5 * minLength) {
                dMBR = 5 * minLength;
            }
            if(dMBR>250)
                dMBR=250;

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(rectpts);
            lineutility.InitializePOINT2Array(deltapoints1);
            lineutility.InitializePOINT2Array(deltapoints2);
            lineutility.InitializePOINT2Array(pts);
            lineutility.InitializePOINT2Array(pointsCorner);

            DrawOpenRectangleDouble(savepoints, pointsCorner, rectpts);

            points[counter] = new POINT2(rectpts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(rectpts[1]);
            points[counter].style = 0;
            counter++;
            midpt = lineutility.MidPointDouble(rectpts[1], rectpts[2], 0);
            pts[0] = lineutility.ExtendLine2Double(rectpts[1], midpt, -5, 5);
            pts[1] = lineutility.ExtendLine2Double(rectpts[1], midpt, 5, 5);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 5;
            counter++;

            ptRelative = lineutility.PointRelativeToLine(rectpts[0], rectpts[1], pts[0]);
            pts[2] = lineutility.ExtendLineDouble(ptRelative, pts[0], -dMBR / 40);

            pts[3] = lineutility.ExtendLineDouble(ptRelative, pts[0], dMBR / 40);

            points[counter] = new POINT2(pts[2]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[3]);
            points[counter].style = 5;
            counter++;
            ptRelative = lineutility.PointRelativeToLine(rectpts[2], rectpts[3], pts[1]);
            pts[4] = lineutility.ExtendLineDouble(ptRelative, pts[1], -dMBR / 40);

            pts[5] = lineutility.ExtendLineDouble(ptRelative, pts[1], dMBR / 40);
            points[counter] = new POINT2(pts[4]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[5]);
            points[counter].style = 5;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(rectpts[2]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(rectpts[3]);
            points[counter].style = 5;
            counter++;

            bPointsRight = DetermineDirectionDouble(savepoints);

            CalcEndpieceDeltasDouble(savepoints, iDeltaX, iDeltaY, CONST_PI / 4);

            if ((savepoints[0].y - savepoints[1].y) < 0) {// Point0 is higher than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints2);
                }
            } else {// Point0 is lower than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints2);
                }
            }

            points[counter] = new POINT2(deltapoints1[1]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[0]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[3]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[3]);
            points[counter].style = 10;
            counter++;

            points[counter] = new POINT2(deltapoints2[1]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[0]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[3]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[3]);
            points[counter].style = 10;
            counter++;

            //clean up
            pointsCorner = null;
            rectpts = null;
            savepoints = null;
            deltapoints1 = null;
            deltapoints2 = null;
            pts = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMByImpDouble",
                    new RendererException("Failed inside GetDISMByImpDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for SPTBYFIRE
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMSupportByFireDouble(POINT2[] points,
            int linetype) {
        int counter = 0;
        try {
            POINT2[] pts = new POINT2[3];
            POINT2[] savepoints = new POINT2[4];
            int j = 0;
            double iDiagEOL_length = 0;
            double dAngle1 = 0;
            double iDeltaX1 = 0;
            double iDeltaY1 = 0;
            double iDeltaX2 = 0;
            double iDeltaY2 = 0;

            for (j = 0; j < 4; j++) {
                savepoints[j] = new POINT2(points[j]);
            }
            ReorderSptByFirePoints(savepoints);

            lineutility.InitializePOINT2Array(pts);
            // draw line connecting points 1 & 2
            //DrawLine(destination, mask, color, savepoints, 2, 2);
            points[counter] = new POINT2(savepoints[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(savepoints[1]);
            points[counter].style = 5;
            counter++;

            // draw line connecting savepoints 1 & 3
            pts[0] = new POINT2(savepoints[0]);
            pts[1] = new POINT2(savepoints[2]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            // draw arrow at end of line
            iDiagEOL_length =  (Math.sqrt(
                    (savepoints[0].x - savepoints[1].x) * (savepoints[0].x - savepoints[1].x) +
                    (savepoints[0].y - savepoints[1].y) * (savepoints[0].y - savepoints[1].y)) / 10);

            //M. Deutch 8-19-04
            if (iDiagEOL_length >  maxLength) {
                iDiagEOL_length =  maxLength;
            }
            if (iDiagEOL_length <  minLength) {
                iDiagEOL_length =  minLength;
            }

            dAngle1 = Math.atan2(savepoints[0].y - savepoints[2].y, savepoints[0].x - savepoints[2].x);
            iDeltaX1 =  (Math.cos(dAngle1 + CONST_PI / 6) * iDiagEOL_length);
            iDeltaY1 =  (Math.sin(dAngle1 + CONST_PI / 6) * iDiagEOL_length);
            iDeltaX2 =  (Math.cos(dAngle1 - CONST_PI / 6) * iDiagEOL_length);
            iDeltaY2 =  (Math.sin(dAngle1 - CONST_PI / 6) * iDiagEOL_length);
            pts[0].x = savepoints[2].x + iDeltaX1;
            pts[0].y = savepoints[2].y + iDeltaY1;
            pts[1] = new POINT2(savepoints[2]);
            pts[2].x = savepoints[2].x + iDeltaX2;
            pts[2].y = savepoints[2].y + iDeltaY2;
            //DrawLine(destination, mask, color, pts, 3, 2);
            for (j = 0; j < 3; j++) {
                points[counter] = new POINT2(pts[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;

            // draw line connecting savepoints 2 & 4
            pts[0] = new POINT2(savepoints[1]);
            pts[1] = new POINT2(savepoints[3]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            // draw arrow at end of line
            dAngle1 = Math.atan2(savepoints[1].y - savepoints[3].y, savepoints[1].x - savepoints[3].x);
            iDeltaX1 =  (Math.cos(dAngle1 + CONST_PI / 6) * iDiagEOL_length);
            iDeltaY1 =  (Math.sin(dAngle1 + CONST_PI / 6) * iDiagEOL_length);
            iDeltaX2 =  (Math.cos(dAngle1 - CONST_PI / 6) * iDiagEOL_length);
            iDeltaY2 =  (Math.sin(dAngle1 - CONST_PI / 6) * iDiagEOL_length);
            pts[0].x = savepoints[3].x + iDeltaX1;
            pts[0].y = savepoints[3].y + iDeltaY1;
            pts[1] = new POINT2(savepoints[3]);
            pts[2].x = savepoints[3].x + iDeltaX2;
            pts[2].y = savepoints[3].y + iDeltaY2;
            //DrawLine(destination, mask, color, pts, 3, 2);
            for (j = 0; j < 3; j++) {
                points[counter] = new POINT2(pts[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;

            // draw lines on the back of the graphic
            dAngle1 = Math.atan2(savepoints[1].y - savepoints[0].y, savepoints[1].x - savepoints[0].x);
            iDiagEOL_length *= 2;
            iDeltaX1 =  (Math.cos(dAngle1 - CONST_PI / 4) * iDiagEOL_length);
            iDeltaY1 =  (Math.sin(dAngle1 - CONST_PI / 4) * iDiagEOL_length);
            iDeltaX2 =  (Math.cos(dAngle1 + CONST_PI / 4) * iDiagEOL_length);
            iDeltaY2 =  (Math.sin(dAngle1 + CONST_PI / 4) * iDiagEOL_length);
            pts[0].x = savepoints[0].x - iDeltaX1;
            pts[0].y = savepoints[0].y - iDeltaY1;
            pts[1] = new POINT2(savepoints[0]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = savepoints[1].x + iDeltaX2;
            pts[0].y = savepoints[1].y + iDeltaY2;
            pts[1] = new POINT2(savepoints[1]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            //clean up
            pts = null;
            savepoints = null;
            //return;
        }
        catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMSupportbyFireDouble",
                    new RendererException("Failed inside GetDISMSupportByFireDouble", exc));
        }
        return counter;
    }

    private static void ReorderAtkByFirePoints(POINT2[] points) {
        try {
            //assume the points were ordered correctly. then pt0 is above the line from pt1 to pt2
            POINT2[] savepoints = new POINT2[3];
            POINT2 ptAboveLine = new POINT2(), ptBelowLine = new POINT2(), ptLeftOfLine = new POINT2(), ptRightOfLine = new POINT2();
            double distToLine = 0, distanceToPointAboveLine = 0, distanceToPointBelowLine = 0;
            double distanceToPointLeftOfLine = 0, distanceToPointRightOfLine = 0;
            for (int j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            if (Math.abs(savepoints[1].x - savepoints[2].x) > 2) {
                distToLine = lineutility.CalcDistanceToLineDouble(savepoints[1], savepoints[2], savepoints[0]);
                ptAboveLine = lineutility.ExtendDirectedLine(savepoints[1], savepoints[2], savepoints[2], 2, distToLine);
                ptBelowLine = lineutility.ExtendDirectedLine(savepoints[1], savepoints[2], savepoints[2], 3, distToLine);
                distanceToPointAboveLine = lineutility.CalcDistanceDouble(savepoints[0], ptAboveLine);
                distanceToPointBelowLine = lineutility.CalcDistanceDouble(savepoints[0], ptBelowLine);
                if (distanceToPointAboveLine < distanceToPointBelowLine) {
                    //then pt2 - pt3 should be left to right
                    if (savepoints[2].x < savepoints[1].x) {
                        lineutility.Reverse2Points(savepoints[1], savepoints[2]);
                        //if(linetype==TacticalLines.SPTBYFIRE)
                        //  lineutility.Reverse2Points(points[0],points[1]);
                    }


                } else {
                    if (savepoints[2].x > savepoints[1].x) {
                        lineutility.Reverse2Points(savepoints[1], savepoints[2]);
                        //if(linetype==TacticalLines.SPTBYFIRE)
                        //  lineutility.Reverse2Points(points[0],points[1]);
                    }

                }
            } else //the last 2 points form a vertical line
            {
                distToLine = lineutility.CalcDistanceToLineDouble(savepoints[1], savepoints[2], savepoints[0]);
                ptLeftOfLine = lineutility.ExtendDirectedLine(savepoints[1], savepoints[2], savepoints[2], 0, distToLine);
                ptRightOfLine = lineutility.ExtendDirectedLine(savepoints[1], savepoints[2], savepoints[2], 1, distToLine);
                distanceToPointLeftOfLine = lineutility.CalcDistanceDouble(savepoints[0], ptLeftOfLine);
                distanceToPointRightOfLine = lineutility.CalcDistanceDouble(savepoints[0], ptRightOfLine);
                if (distanceToPointRightOfLine < distanceToPointLeftOfLine) {
                    if (savepoints[2].y < savepoints[1].y) {
                        lineutility.Reverse2Points(savepoints[1], savepoints[2]);
                    }
                } else {
                    if (savepoints[2].y > savepoints[1].y) {
                        lineutility.Reverse2Points(savepoints[1], savepoints[2]);
                    }
                }
            }
            points[1].x = savepoints[1].x;
            points[1].y = savepoints[1].y;
            points[2].x = savepoints[2].x;
            points[2].y = savepoints[2].y;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "ReorderAtkByFirePoints",
                    new RendererException("Failed inside GetDISMSupportByFireDouble", exc));
        }
    }
    private static void ReorderSptByFirePoints(POINT2[] points) {
        try {
            //assume the points were ordered correctly. then pt0 is above the line from pt1 to pt2
            POINT2 ptAboveLine = new POINT2(), ptBelowLine = new POINT2(), ptLeftOfLine = new POINT2(), ptRightOfLine = new POINT2();
            double distToLine = 0, distanceToPointAboveLine = 0, distanceToPointBelowLine = 0;
            double distanceToPointLeftOfLine = 0, distanceToPointRightOfLine = 0;

            POINT2 midpt = lineutility.MidPointDouble(points[0], points[1], 0);
            if (Math.abs(points[2].x - points[3].x) > 2) {
                distToLine = lineutility.CalcDistanceToLineDouble(points[1], points[2], midpt);
                ptAboveLine = lineutility.ExtendDirectedLine(points[1], points[2], points[2], 2, distToLine);
                ptBelowLine = lineutility.ExtendDirectedLine(points[1], points[2], points[2], 3, distToLine);
                distanceToPointAboveLine = lineutility.CalcDistanceDouble(points[0], ptAboveLine);
                distanceToPointBelowLine = lineutility.CalcDistanceDouble(points[0], ptBelowLine);
                if (distanceToPointAboveLine < distanceToPointBelowLine) {
                    //then pt2 - pt3 should be left to right
                    if (points[2].x < points[1].x) {
                        lineutility.Reverse2Points(points[0], points[1]);
                        lineutility.Reverse2Points(points[2], points[3]);
                    }
                } else {
                    if (points[2].x > points[1].x) {
                        lineutility.Reverse2Points(points[0], points[1]);
                        lineutility.Reverse2Points(points[2], points[3]);
                    }
                }
            } else //the last 2 points form a vertical line
            {
                distToLine = lineutility.CalcDistanceToLineDouble(points[1], points[2], midpt);
                ptLeftOfLine = lineutility.ExtendDirectedLine(points[1], points[2], points[2], 0, distToLine);
                ptRightOfLine = lineutility.ExtendDirectedLine(points[1], points[2], points[2], 1, distToLine);
                distanceToPointLeftOfLine = lineutility.CalcDistanceDouble(points[0], ptLeftOfLine);
                distanceToPointRightOfLine = lineutility.CalcDistanceDouble(points[0], ptRightOfLine);
                if (distanceToPointLeftOfLine < distanceToPointRightOfLine) {
                    //then pt2 - pt3 should be left to right
                    if (points[2].y > points[1].y) {
                        lineutility.Reverse2Points(points[0], points[1]);
                        lineutility.Reverse2Points(points[2], points[3]);
                    }
                } else {
                    if (points[2].y < points[1].y) {
                        lineutility.Reverse2Points(points[0], points[1]);
                        lineutility.Reverse2Points(points[2], points[3]);
                    }
                }

            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "ReorderSptByFire",
                    new RendererException("Failed inside ReorderSptByFirePoints", exc));
        }
    }
    /**
     * Calculates the points for ATKBYFIRE
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMATKBYFIREDouble(POINT2[] points, int linetype) {
        int counter = 0;
        try {
            POINT2[] pts = new POINT2[3];
            POINT2 ptMid = new POINT2();
            POINT2[] savepoints = new POINT2[3];
            int j = 0;
            double iDiagEOL_length = 0;
            double dAngle1 = 0;
            double iDeltaX1 = 0;
            double iDeltaY1 = 0;
            double iDeltaX2 = 0;
            double iDeltaY2 = 0;

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            ReorderAtkByFirePoints(savepoints);

            lineutility.InitializePOINT2Array(pts);
            // draw line across back
            pts[0] = new POINT2(savepoints[1]);
            pts[1] = new POINT2(savepoints[2]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            // draw perpendicular line
            ptMid.x = (savepoints[1].x + savepoints[2].x) / 2;
            ptMid.y = (savepoints[1].y + savepoints[2].y) / 2;
            pts[0] = new POINT2(ptMid);
            pts[1] = new POINT2(savepoints[0]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            // draw arrowhead
            iDiagEOL_length =  ((Math.sqrt // height of graphic
                    (
                    (savepoints[1].x - savepoints[2].x) * (savepoints[1].x - savepoints[2].x) +
                    (savepoints[1].y - savepoints[2].y) * (savepoints[1].y - savepoints[2].y)) +
                    Math.sqrt // length of graphic
                    (
                    (savepoints[0].x - ptMid.x) * (savepoints[0].x - ptMid.x) +
                    (savepoints[0].y - ptMid.y) * (savepoints[0].y - ptMid.y))) / 20);
            //if(iDiagEOL_length<10)
            //	iDiagEOL_length=10;
            if ((double) iDiagEOL_length > maxLength/5) {
                iDiagEOL_length =  maxLength/5;
            }
            if ((double) iDiagEOL_length < minLength) {
                iDiagEOL_length =  minLength;
            }

            dAngle1 = Math.atan2(ptMid.y - savepoints[0].y, ptMid.x - savepoints[0].x);
            iDeltaX1 =  (Math.cos(dAngle1 + CONST_PI / 6) * iDiagEOL_length);
            iDeltaY1 =  (Math.sin(dAngle1 + CONST_PI / 6) * iDiagEOL_length);
            iDeltaX2 =  (Math.cos(dAngle1 - CONST_PI / 6) * iDiagEOL_length);
            iDeltaY2 =  (Math.sin(dAngle1 - CONST_PI / 6) * iDiagEOL_length);
            pts[0].x = savepoints[0].x + iDeltaX1;
            pts[0].y = savepoints[0].y + iDeltaY1;
            pts[1] = new POINT2(savepoints[0]);
            pts[2].x = savepoints[0].x + iDeltaX2;
            pts[2].y = savepoints[0].y + iDeltaY2;
            //DrawLine(destination, mask, color, pts, 3, 2);
            for (j = 0; j < 3; j++) {
                points[counter] = new POINT2(pts[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;

            // draw lines on the back of the graphic
            dAngle1 = Math.atan2(savepoints[1].y - savepoints[2].y, savepoints[1].x - savepoints[2].x);
            iDeltaX1 =  (Math.cos(dAngle1 - CONST_PI / 4) * iDiagEOL_length * 2);
            iDeltaY1 =  (Math.sin(dAngle1 - CONST_PI / 4) * iDiagEOL_length * 2);
            iDeltaX2 =  (Math.cos(dAngle1 + CONST_PI / 4) * iDiagEOL_length * 2);
            iDeltaY2 =  (Math.sin(dAngle1 + CONST_PI / 4) * iDiagEOL_length * 2);

            pts[0].x = savepoints[1].x + iDeltaX1;
            pts[0].y = savepoints[1].y + iDeltaY1;
            pts[1] = new POINT2(savepoints[1]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            pts[0].x = savepoints[2].x - iDeltaX2;
            pts[0].y = savepoints[2].y - iDeltaY2;
            pts[1] = new POINT2(savepoints[2]);
            //DrawLine(destination, mask, color, pts, 2, 2);
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;
            //clean up
            pts = null;
            savepoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMAtkByFireDouble",
                    new RendererException("Failed inside GetDISMAtkByFireDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for GAP
     *
     * @param points OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMGapDouble(POINT2[] points, int linetype) {
        try {
            POINT2[] savepoints = new POINT2[4];
            POINT2[] pts = new POINT2[2];
            int j = 0;
            double dMBR = lineutility.MBRDistance(points, 4);
            //end declarations

            for (j = 0; j < 4; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(pts);
            //M. Deutch 8-19-04
            if (dMBR / 10 > maxLength) {
                dMBR = 10 * maxLength;
            }
            if (dMBR / 10 < minLength) {
                dMBR = 10 * minLength;
            }

            points[0] = new POINT2(savepoints[0]);
            points[0].style = 0;
            points[1] = new POINT2(savepoints[1]);
            points[1].style = 5;
            points[2] = new POINT2(savepoints[2]);
            points[2].style = 0;
            points[3] = new POINT2(savepoints[3]);
            points[3].style = 5;

            double dist = dMBR / 10;
            if(dist>20)
                dist=20;
            double dist2 = dist;
            switch (linetype) //change 1
            {
                case TacticalLines.BRIDGE:
                    dist = 1.5 * dist;
                    dist2 = dist / 2;
                    break;
                default:
                    dist2 = dist;
                    break;
            }

            //get the extension point
            pts[0] = lineutility.ExtendLineDouble(savepoints[1], savepoints[0], dist);
            pts[1] = lineutility.ExtendLineDouble(savepoints[2], savepoints[0], dist2);
            points[4] = new POINT2(points[0]);
            points[4].style = 0;
            points[5] = lineutility.MidPointDouble(pts[0], pts[1], 5);
            //get the extension point
            pts[0] = lineutility.ExtendLineDouble(savepoints[0], savepoints[1], dist);
            pts[1] = lineutility.ExtendLineDouble(savepoints[3], savepoints[1], dist2);
            points[6] = new POINT2(points[1]);
            points[6].style = 0;
            points[7] = lineutility.MidPointDouble(pts[0], pts[1], 5);
            //get the extension point
            pts[0] = lineutility.ExtendLineDouble(savepoints[0], savepoints[2], dist2);
            pts[1] = lineutility.ExtendLineDouble(savepoints[3], savepoints[2], dist);
            points[8] = new POINT2(points[2]);
            points[8].style = 0;
            points[9] = lineutility.MidPointDouble(pts[0], pts[1], 5);
            //get the extension point
            pts[0] = lineutility.ExtendLineDouble(savepoints[1], savepoints[3], dist2);
            pts[1] = lineutility.ExtendLineDouble(savepoints[2], savepoints[3], dist);
            points[10] = new POINT2(points[3]);
            points[10].style = 0;
            points[11] = lineutility.MidPointDouble(pts[0], pts[1], 5);

            //clean up
            pts = null;
            savepoints = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMGapDouble",
                    new RendererException("Failed inside GetDISMGapDouble", exc));
        }
        return 12;
    }
    /**
     * Calculates the points for DECEIVE.
     *
     * @param points - OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
//    protected static void GetDISMDeceiveDouble(POINT2[] points,
//            int linetype) {
//        try {
//            POINT2[] savepoints = new POINT2[3];
//            int j = 0;
//
//            for (j = 0; j < 3; j++) {
//                savepoints[j] = new POINT2(points[j]);
//            }
//
//            points[0] = new POINT2(savepoints[0]);
//            points[0].style = 17;
//            points[1] = new POINT2(savepoints[1]);
//            points[1].style = 5;
//            points[2] = new POINT2(savepoints[2]);
//            points[2].style = 17;
//            points[3] = new POINT2(savepoints[0]);
//            points[3].style = 5;
//
//            savepoints = null;
//            //return;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return;
//    }
    /**
     * Calculates the points for MNFLDDIS
     *
     * @param points - OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMMinefieldDisruptDouble(POINT2[] points, int linetype) {
        int counter=0;
        try {
            POINT2[] pts = new POINT2[2];
            POINT2[] ptsArrow = new POINT2[3];
            POINT2 ptCenter = new POINT2();
            int j = 0;
            POINT2[] savepoints = new POINT2[3];
            double dAngle1 = 0, d = 0, dist = 0;
            POINT2[] deltapoints1 = new POINT2[4];
            POINT2[] deltapoints2 = new POINT2[4];
            POINT2[] deltapoints3 = new POINT2[4];
            double iDiagEOL_length = 0;
            double iDeltaX1 = 0;
            double iDeltaY1 = 0;
            double iDeltaX2 = 0;
            double iDeltaY2 = 0;
            POINT2 ptTail = new POINT2();
            //end declarations

            for (j = 0; j < 3; j++) {
                savepoints[j] = new POINT2(points[j]);
            }

            lineutility.InitializePOINT2Array(ptsArrow);
            lineutility.InitializePOINT2Array(deltapoints1);
            lineutility.InitializePOINT2Array(deltapoints2);
            lineutility.InitializePOINT2Array(deltapoints3);
            lineutility.InitializePOINT2Array(pts);
            
            points[counter] = new POINT2(savepoints[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(savepoints[1]);
            points[counter].style = 5;
            counter++;

            ptCenter.x = (savepoints[0].x + savepoints[1].x) / 2;
            ptCenter.y = (savepoints[0].y + savepoints[1].y) / 2;

            ptsArrow[0] = new POINT2(savepoints[2]);
            ptsArrow[1].x = ptCenter.x + (savepoints[2].x - savepoints[0].x) * 4 / 5;
            ptsArrow[1].y = ptCenter.y + (savepoints[2].y - savepoints[0].y) * 4 / 5;
            ptsArrow[2].x = savepoints[1].x + (savepoints[2].x - savepoints[0].x) * 3 / 5;
            ptsArrow[2].y = savepoints[1].y + (savepoints[2].y - savepoints[0].y) * 3 / 5;

            points[counter] = new POINT2(savepoints[1]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(ptsArrow[2]);
            points[counter].style = 5;
            counter++;

            pts[1] = new POINT2(ptsArrow[1]);

            //draw middle line
            points[counter] = new POINT2(ptCenter);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            //draw tail
            //M. Deutch 8-19-04
            dist = lineutility.CalcDistanceDouble(savepoints[2], savepoints[0]);
            d = dist;
            if (d > 5 * maxLength) {
                d = 5 * maxLength;
            }
            if (d < 5 * minLength) {
                d = 5 * minLength;
            }
            ptTail.x = (savepoints[0].x + ptCenter.x) / 2;
            ptTail.y = (savepoints[0].y + ptCenter.y) / 2;
            pts[0].x = ptTail.x - (savepoints[2].x - savepoints[0].x) / 5;
            pts[0].y = ptTail.y - (savepoints[2].y - savepoints[0].y) / 5;
            pts[0] = lineutility.ExtendLineDouble(pts[0], ptTail, -d / 5);
            points[counter] = new POINT2(ptTail);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[0]);
            points[counter].style = 5;
            counter++;

            pts[0] = new POINT2(savepoints[0]);
            pts[1] = new POINT2(ptsArrow[0]);

            points[counter] = new POINT2(pts[0]);
            points[counter].style = 0;
            counter++;
            points[counter] = new POINT2(pts[1]);
            points[counter].style = 5;
            counter++;

            // the following code is very similar to CalcEndpieceDeltas
            iDiagEOL_length =  ((Math.sqrt // height of graphic
                    (
                    (savepoints[1].x - savepoints[0].x) * (savepoints[1].x - savepoints[0].x) +
                    (savepoints[1].y - savepoints[0].y) * (savepoints[1].y - savepoints[0].y)) +
                    Math.sqrt // length of graphic
                    (
                    (savepoints[2].x - savepoints[1].x) * (savepoints[2].x - savepoints[1].x) +
                    (savepoints[2].y - savepoints[1].y) * (savepoints[2].y - savepoints[1].y))) / 15);
            // dAngle1 = angle used to calculate the end-piece deltas

            //M. Deutch 8-19-04
            if (iDiagEOL_length >  maxLength) {
                iDiagEOL_length =  maxLength;
            }
            if (iDiagEOL_length <  minLength) {
                iDiagEOL_length =  minLength;
            }

            dAngle1 = Math.atan2(savepoints[0].y - savepoints[2].y, savepoints[0].x - savepoints[2].x);
            iDeltaX1 =  (iDiagEOL_length * Math.cos(dAngle1 - CONST_PI / 6));
            iDeltaY1 =  (iDiagEOL_length * Math.sin(dAngle1 - CONST_PI / 6));
            iDeltaX2 =  (iDiagEOL_length * Math.cos(dAngle1 + CONST_PI / 6));
            iDeltaY2 =  (iDiagEOL_length * Math.sin(dAngle1 + CONST_PI / 6));

            DrawEndpieceDeltasDouble(ptsArrow[0],
                    iDeltaX1, iDeltaY1, iDeltaX2, iDeltaY2, deltapoints1);
            DrawEndpieceDeltasDouble(ptsArrow[1],
                    iDeltaX1, iDeltaY1, iDeltaX2, iDeltaY2, deltapoints2);
            DrawEndpieceDeltasDouble(ptsArrow[2],
                    iDeltaX1, iDeltaY1, iDeltaX2, iDeltaY2, deltapoints3);
            points[counter] = new POINT2(deltapoints1[1]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[0]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[3]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[3]);
            points[counter].style = 10;
            counter++;

            points[counter] = new POINT2(deltapoints2[1]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[0]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[3]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[3]);
            points[counter].style = 10;
            counter++;

            points[counter] = new POINT2(deltapoints3[1]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints3[0]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints3[3]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints3[3]);
            points[counter].style = 10;
            counter++;

            //clean up
            pts = null;
            ptsArrow = null;
            savepoints = null;
            deltapoints1 = null;
            deltapoints2 = null;
            deltapoints3 = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMMinefieldDisruptDouble",
                    new RendererException("Failed inside GetDISMMinefieldDisruptDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for LINTGT
     *
     * @param points - OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     * @param vblCounter the number of points required to display the symbol
     */
    protected static int GetDISMLinearTargetDouble(POINT2[] points, int linetype, int vblCounter) {
        int counter = 0;
        try {
            int j = 0;
            double dMBR = lineutility.MBRDistance(points, vblCounter-4);
            //end declarations

            //M. Deutch 8-19-04
            if (dMBR / 20 > maxLength) {
                dMBR = 20 * maxLength;
            }
            if (dMBR / 20 < minLength) {
                dMBR = 20 * minLength;
            }
            if (dMBR < 150) {
                dMBR = 150;
            }
            if(dMBR>250)
                dMBR=250;
            
            for (j = 0; j < vblCounter - 4; j++) {
                points[counter].style = 0;
                counter++;
            }
            //for(j=vblCounter-4;j<vblCounter;j++)
              //  points[j]=new POINT2();

            points[counter - 1].style = 5;

            points[counter] = lineutility.ExtendTrueLinePerpDouble(points[0], points[1], points[0], dMBR / 20, 0);
            counter++;
            points[counter] = lineutility.ExtendTrueLinePerpDouble(points[0], points[1], points[0], -dMBR / 20, 5);
            counter++;
            points[counter] = lineutility.ExtendTrueLinePerpDouble(points[vblCounter - 5], points[vblCounter - 6], points[vblCounter - 5], dMBR / 20, 0);
            counter++;
            points[counter] = lineutility.ExtendTrueLinePerpDouble(points[vblCounter - 5], points[vblCounter - 6], points[vblCounter - 5], -dMBR / 20, 5);
            counter++;
            if (linetype == (long) TacticalLines.FPF) {
                points[0].style = 6;
            }
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMLinearTargetDouble",
                    new RendererException("Failed inside GetDISMLinearTargetDouble", exc));
        }
        return counter;
    }
    /**
     * Calculates the points for BLOCK, MNFLDBLK
     *
     * @param points - OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static void GetDISMBlockDouble2(POINT2[] points,
            int linetype) {
        try {
            //diagnostic for CommandSight
            //POINT2 ptRelative = lineutility.PointRelativeToLine(points[0], points[1], points[2]);            
            POINT2 ptRelative = new POINT2(points[2]);
            
            POINT2 midpt = lineutility.MidPointDouble(points[0], points[1], 0);
            int j = 0;
            points[0].style = 0;
            points[1].style = 5;
            points[2] = new POINT2(midpt);
            points[3] = new POINT2(ptRelative);
            if (linetype == (long) TacticalLines.BLOCK) {
                points[2].style = 14;
            }
            if (linetype == (long) TacticalLines.FPF) {
                points[2].style = 6;
            }
            //for (j = 3; j < vblCounter; j++) {
              //  points[j].style = 5;
            //}
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMBlockDouble2",
                    new RendererException("Failed inside GetDISMBlockDouble2", exc));
        }
        return;
    }
    /**
     * Calculates the points for PAA_RECTANGULAR.
     *
     * @param points - OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static void GetDISMPAADouble(POINT2[] points, int linetype) {
        try {
            POINT2 pt0 = new POINT2(points[0]);
            POINT2 pt1 = new POINT2(points[1]);
            POINT2 pt2 = new POINT2();
            POINT2 pt3 = new POINT2();
            POINT2 midpt = new POINT2();
            double d = lineutility.CalcDistanceDouble(pt0, pt1);
            //end declarations

            midpt = lineutility.MidPointDouble(pt0, pt1, 0);
            pt2 = lineutility.ExtendTrueLinePerpDouble(pt0, pt1, midpt, d / 2, 0);
            pt3 = lineutility.ExtendTrueLinePerpDouble(pt0, pt1, midpt, -d / 2, 0);
            d = lineutility.CalcDistanceDouble(pt0, pt2);
            points[0] = new POINT2(pt0);
            points[0].style = 14;
            points[1] = new POINT2(pt2);
            points[1].style = 14;
            points[2] = new POINT2(pt1);
            points[2].style = 14;
            points[3] = new POINT2(pt3);
            points[3].style = 14;
            points[4] = new POINT2(pt0);
            points[4].style = 5;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMPAADouble",
                    new RendererException("Failed inside GetDISMPAADouble", exc));
        }
        return;
    }

    private static boolean ReverseDelayArc(POINT2[] points) {
        try {
           POINT2 pt1 = points[0];
        POINT2 pt2 = points[1];
        POINT2 pt3 = points[2];

        float lineAngle = getAngleBetweenPoints(pt1.x, pt1.y, pt2.x, pt2.y);
        float curveAngle = getAngleBetweenPoints(pt2.x, pt2.y, pt3.x, pt3.y);

        float upperBound = curveAngle + 180;
        return !isInRange(curveAngle, upperBound, lineAngle);
        
        } 
        catch (Exception exc) {
            ErrorLogger.LogException(_className ,"ReverseDelayArc",
                    new RendererException("Failed inside GetDelayArc", exc));
        }
        return false;
    }
    
        private static boolean isInRange(float min, float max, float targetAngle) {
        targetAngle = normalizeAngle(targetAngle);
        min = normalizeAngle(min);
        max = normalizeAngle(max);

        if (min < max) {
            return min <= targetAngle && targetAngle <= max;
        }
        return min <= targetAngle || targetAngle <= max;

    }

    private static float getAngleBetweenPoints(double x1, double y1, double x2, double y2) {
        return (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
    }

    /**
     * Returns an angle from 0 to 360
     *
     * @param angle the angle to normalize
     * @return an angle in range from 0 to 360
     */
    public static float normalizeAngle(float angle) {
        return (3600000 + angle) % 360;
    }

    private static void DrawEndpieceDeltasDouble(POINT2 point,
            double iDelta1,
            double iDelta2,
            double iDelta3,
            double iDelta4,
            POINT2[] deltapoints)
		{
        try
        {
            deltapoints[0] = new POINT2(point);
            deltapoints[0].style = 0;
            deltapoints[1].x = point.x + iDelta1;
            deltapoints[1].y = point.y + iDelta2;
            deltapoints[1].style = 5;
            deltapoints[2] = new POINT2(point);
            deltapoints[2].style = 0;
            deltapoints[3].x = point.x + iDelta3;
            deltapoints[3].y = point.y + iDelta4;
            deltapoints[3].style = 5;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"DrawEndpieceDeltasDouble",
                    new RendererException("Failed inside DrawEndpieceDeltasDouble", exc));
        }
    }
    /**
     * Calculates the points for EASY
     *
     * @param points - OUT - the client points, also used for the returned points.
     * @param linetype the line type.
     */
    protected static int GetDISMEasyDouble(POINT2[] points,
            int linetype) {
        int counter = 0;
        try {
            int j = 0;
            POINT2[] pointsCorner = new POINT2[2];
            POINT2[] rectpts = new POINT2[4];
            POINT2[] savepoints = new POINT2[3];
            POINT2[] deltapoints1 = new POINT2[4];
            POINT2[] deltapoints2 = new POINT2[4];
            ref<double[]> iDeltaX = new ref(), iDeltaY = new ref();
            int bPointsRight = 0;
            //end declarations

            for (j = 0; j < 3; j++) {
                savepoints[j] = points[j];
            }
            lineutility.InitializePOINT2Array(pointsCorner);
            lineutility.InitializePOINT2Array(rectpts);
            lineutility.InitializePOINT2Array(deltapoints1);
            lineutility.InitializePOINT2Array(deltapoints2);

            DrawOpenRectangleDouble(savepoints, pointsCorner, rectpts);
            for (j = 0; j < 4; j++) {
                points[counter] = new POINT2(rectpts[j]);
                points[counter].style = 0;
                counter++;
            }
            points[counter - 1].style = 5;

            bPointsRight = DetermineDirectionDouble(savepoints);

            CalcEndpieceDeltasDouble(savepoints, iDeltaX, iDeltaY, CONST_PI / 4);

            if ((savepoints[0].y - savepoints[1].y) < 0) {// Point0 is higher than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints2);
                }
            } else {// Point0 is lower than Point1
                if (bPointsRight != 0) {// figure opens to the right
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaY.value[0], -iDeltaX.value[0], iDeltaX.value[0], iDeltaY.value[0], deltapoints2);
                } else {// figure opens to the left
                    DrawEndpieceDeltasDouble(savepoints[0],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints1);
                    DrawEndpieceDeltasDouble(savepoints[1],
                            iDeltaX.value[0], iDeltaY.value[0], iDeltaY.value[0], -iDeltaX.value[0], deltapoints2);
                }
            }

            points[counter] = new POINT2(deltapoints1[1]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[0]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[3]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints1[3]);
            points[counter].style = 10;
            counter++;

            points[counter] = new POINT2(deltapoints2[1]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[0]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[3]);
            points[counter].style = 9;
            counter++;
            points[counter] = new POINT2(deltapoints2[3]);
            points[counter].style = 10;
            counter++;

            //clean up
            pointsCorner = null;
            rectpts = null;
            savepoints = null;
            deltapoints1 = null;
            deltapoints2 = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMEasyDouble",
                    new RendererException("Failed inside GetDISMEasyDouble", exc));
        }
        return counter;
    }
    
    /**
     * Calculates the points for AMBUSH
     *
     * @param pLinePoints - OUT - the client points, also used for the returned points.
     */
    protected static int AmbushPointsDouble(POINT2[] pLinePoints)
    {
        int counter=0;
        try
        {
            POINT2[] pts=new POINT2[3];
            POINT2[] savepoints=new POINT2[3];
            // calculate midpoint
            POINT2 ptMid=new POINT2();
            double dRadius=0,d=0;
            double dAngle1=0;
            double dAngle1c=0;
            double dAngle2c=0;
            double dAngle12c=0;
            double dAngle0=0;
            POINT2[] arcpoints=new POINT2[17];
            double dAngleTic = 0;
            double dDeltaX1=0, dDeltaY1=0;
            double dDeltaX2 = 0;
            double dDeltaY2 = 0;
            POINT2 ptCenter=new POINT2();
            int j=0,i=0;
            double iArrowLength=0;

            for(j=0;j<3;j++)
            {
                savepoints[j]=new POINT2(pLinePoints[j]);
            }

            //initialize the pOINT2 arrays
            lineutility.InitializePOINT2Array(arcpoints);
            lineutility.InitializePOINT2Array(pts);

            ptMid.x = (savepoints[1].x + savepoints[2].x) / 2;
            ptMid.y = (savepoints[1].y + savepoints[2].y) / 2;

            // calculate arc center
            dRadius = Math.sqrt( (ptMid.x-savepoints[2].x) * (ptMid.x-savepoints[2].x) +
                (ptMid.y-savepoints[2].y) * (ptMid.y-savepoints[2].y) );

            // add section M. Deutch 8-25-05
            //consider the other possiblity for a center
            double dRadius2 = Math.sqrt( (ptMid.x-savepoints[1].x) * (ptMid.x-savepoints[1].x) +
                (ptMid.y-savepoints[1].y) * (ptMid.y-savepoints[1].y) );

            dAngle1 = Math.atan2(savepoints[1].y - savepoints[2].y, savepoints[1].x - savepoints[2].x);
            ptCenter.x = ptMid.x + Math.cos(dAngle1 - CONST_PI / 2) * dRadius;
            ptCenter.y = ptMid.y + Math.sin(dAngle1 - CONST_PI / 2) * dRadius;

            //added section M. Deutch 8-25-05
            //consider the other possibility for a center if the points were reversed
            double dAngle2 = Math.atan2(savepoints[2].y - savepoints[1].y, savepoints[2].x - savepoints[1].x);
            POINT2 ptCenter2=new POINT2();
            ptCenter2.x = ptMid.x + Math.cos(dAngle2 - CONST_PI / 2) * dRadius;
            ptCenter2.y = ptMid.y + Math.sin(dAngle2 - CONST_PI / 2) * dRadius;
            double dist=lineutility.CalcDistanceDouble(savepoints[0],ptCenter);
            double dist2=lineutility.CalcDistanceDouble(savepoints[0],ptCenter2);
            //if the distance to the new center is closer
            //then reverse the arc endpoints
            if(dist2>dist)
            {
                //POINT2 ptTemp=new POINT2();
                POINT2 ptTemp=new POINT2(savepoints[1]);
                savepoints[1]=new POINT2(savepoints[2]);
                savepoints[2]=new POINT2(ptTemp);
                ptCenter=new POINT2(ptCenter2);
                dAngle1=dAngle2;
            }
            //end section

            dRadius = Math.sqrt ( (savepoints[1].x-ptCenter.x) * (savepoints[1].x-ptCenter.x) +
                (savepoints[1].y-ptCenter.y) * (savepoints[1].y-ptCenter.y) );

            // draw arc
            ArcApproximationDouble ((ptCenter.x - dRadius),(ptCenter.y - dRadius),
                (ptCenter.x + dRadius),( ptCenter.y + dRadius),
                savepoints[2].x, savepoints[2].y, savepoints[1].x, savepoints[1].y, arcpoints);

            for(j=0;j<17;j++)
            {
                pLinePoints[counter]=new POINT2(arcpoints[j]);
                pLinePoints[counter].style=0;
                counter++;
            }
            pLinePoints[counter-1].style=5;

            // draw line out from arc to point 1
            pts[0] = new POINT2(savepoints[0]);
            dAngle1c = Math.atan2(ptCenter.y - savepoints[1].y, ptCenter.x - savepoints[1].x);
            dAngle2c = Math.atan2(ptCenter.y - savepoints[2].y, ptCenter.x - savepoints[2].x);
            //	dAngle1c = atan2(ptCenter.y - savepoints[1].y, ptCenter.x - savepoints[1].x);
            //	dAngle2c = atan2(ptCenter.y - savepoints[2].y, ptCenter.x - savepoints[2].x);
            dAngle12c = (dAngle1c + dAngle2c) / 2;
            if ( (dAngle1c > 0) && (dAngle2c < 0) )
            {
                pts[1].x = ptCenter.x + Math.cos(dAngle12c) * dRadius;
                pts[1].y = ptCenter.y + Math.sin(dAngle12c) * dRadius;
            }
            else
            {
                pts[1].x = ptCenter.x - Math.cos(dAngle12c) * dRadius;
                pts[1].y = ptCenter.y - Math.sin(dAngle12c) * dRadius;
            }
            //DrawLine(destination, mask, color, pts, 2, 2);
            pLinePoints[counter]=new POINT2(pts[0]);
            pLinePoints[counter].style=0;counter++;
            pLinePoints[counter]=new POINT2(pts[1]);
            pLinePoints[counter].style=5;counter++;


            // draw arrowhead on end of line
            dAngle0 = Math.atan2(pts[1].y - savepoints[0].y, pts[1].x - savepoints[0].x);
            //	dAngle0 = atan2(pts[1].y - savepoints[0].y, pts[1].x - savepoints[0].x);
            iArrowLength =(
                (
                Math.sqrt // height of graphic
                (
                (savepoints[1].x-savepoints[2].x) * (savepoints[1].x-savepoints[2].x) +
                (savepoints[1].y-savepoints[2].y) * (savepoints[1].y-savepoints[2].y)
                ) +
                Math.sqrt // length of graphic
                (
                (savepoints[0].x-ptMid.x) * (savepoints[0].x-ptMid.x) +
                (savepoints[0].y-ptMid.y) * (savepoints[0].y-ptMid.y)
                )
                ) / 20);

            if((double)iArrowLength>maxLength)
                iArrowLength=(int)maxLength;
            if((double)iArrowLength<minLength)
                iArrowLength=(int)minLength;

            pts[0].x = savepoints[0].x + Math.cos(dAngle0 + CONST_PI / 6) * iArrowLength;
            pts[0].y = savepoints[0].y + Math.sin(dAngle0 + CONST_PI / 6) * iArrowLength;
            pts[1] = savepoints[0];
            pts[2].x = savepoints[0].x + Math.cos(dAngle0 - CONST_PI / 6) * iArrowLength;
            pts[2].y = savepoints[0].y + Math.sin(dAngle0 - CONST_PI / 6) * iArrowLength;
            for(j=0;j<3;j++)
            {
                pLinePoints[counter]=new POINT2(pts[j]);
                pLinePoints[counter].style=0;
                counter++;
            }
            pLinePoints[counter-1].style=5;
            //	DrawLine(destination, mask, color, pts, 3, 2);

            // draw lines out from arc toward back of graphic
            //M. Deutch 8-19-04
            d=dRadius/3;
            if(d>maxLength)
                    d=maxLength;
            if(d<minLength)
                    d=minLength;

            dAngleTic = CONST_PI / 18; // angle in radians between tic-marks
            //dDeltaX2 = Math.Cos(dAngle1 + CONST_PI / 2) * dRadius / 3;
            //dDeltaY2 = Math.Sin(dAngle1 + CONST_PI / 2) * dRadius / 3;
            dDeltaX2 = Math.cos(dAngle1 + CONST_PI / 2) * d;
            dDeltaY2 = Math.sin(dAngle1 + CONST_PI / 2) * d;
            for (i=0; i<8; i++)
            {
                dAngle1c += dAngleTic;
                dDeltaX1 = Math.cos(dAngle1c) * dRadius;
                dDeltaY1 = Math.sin(dAngle1c) * dRadius;
                pts[0].x = ptCenter.x - dDeltaX1;
                pts[0].y = ptCenter.y - dDeltaY1;
                pLinePoints[counter]=new POINT2(pts[0]);
                pLinePoints[counter].style=0;
                counter++;
                pts[1].x = pts[0].x - dDeltaX2;
                pts[1].y = pts[0].y - dDeltaY2;
                pLinePoints[counter]=new POINT2(pts[1]);
                pLinePoints[counter].style=5;
                counter++;
                //DrawLine(destination, mask, color, pts, 2, 2);
            }
            //FillPoints(pLinePoints,counter);
            //clean up
            pts=null;
            savepoints=null;
            arcpoints=null;
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className ,"AmbushPointsDouble",
                    new RendererException("Failed inside AmbushPointsDouble", exc));
        }
        return counter;
    }

}
