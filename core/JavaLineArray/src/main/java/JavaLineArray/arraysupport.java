/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaLineArray;

/**
 * Class to process the pixel arrays
 *
 * @author Michael Deutch
 */
import java.awt.Color;
import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Shape;
//import java.awt.geom.GeneralPath;
import java.awt.geom.Area;
import java.awt.Polygon;
import ArmyC2.C2SD.Utilities.ErrorLogger;
import ArmyC2.C2SD.Utilities.RendererException;
import ArmyC2.C2SD.Utilities.RendererSettings;
import java.awt.geom.Rectangle2D;
import ArmyC2.C2SD.Utilities.IPointConversion;
/*
 * A class to calculate the symbol points for the GeneralPath objects.
 * @author Michael Deutch
 */
public final class arraysupport {

    private static final double maxLength = 100;
    private static final double minLength = 2.5;
    private static final double dACP = 0;
    private static final String _className = "arraysupport";

//    protected static void setMinLength(double value)
//    {
//        minLength=value;
//    }
    private static void FillPoints(POINT2[] pLinePoints,
            int counter,
            ArrayList<POINT2> points) {
        points.clear();
        for (int j = 0; j < counter; j++) {
            points.add(pLinePoints[j]);
        }
    }

    /**
     * This is the interface function to CELineArray from clsRenderer2 for
     * non-channel types
     *
     * @param lineType the line type
     * @param pts the client points
     * @param shapes the symbol ShapeInfo objects
     * @param clipBounds the rectangular clipping bounds
     * @param rev the Mil-Standard-2525 revision
     */
    public static ArrayList<POINT2> GetLineArray2(int lineType,
            ArrayList<POINT2> pts,
            ArrayList<Shape2> shapes,
            Rectangle2D clipBounds,
            int rev,
            IPointConversion converter) {

        ArrayList<POINT2> points = null;
        try {
            POINT2 pt = null;
            POINT2[] pLinePoints2 = null;
            POINT2[] pLinePoints = null;
            int vblSaveCounter = pts.size();
            //get the count from countsupport
            int j = 0;
            if (pLinePoints2 == null || pLinePoints2.length == 0)//did not get set above
            {
                pLinePoints = new POINT2[vblSaveCounter];
                for (j = 0; j < vblSaveCounter; j++) {
                    pt = (POINT2) pts.get(j);
                    pLinePoints[j] = new POINT2(pt.x, pt.y, pt.style);
                }
            }
            //get the number of points the array will require
            int vblCounter = countsupport.GetCountersDouble(lineType, vblSaveCounter, pLinePoints, clipBounds, rev);

            //resize pLinePoints and fill the first vblSaveCounter elements with the original points
            if (vblCounter > 0) {
                pLinePoints = new POINT2[vblCounter];
            } else {
                shapes = null;
                return null;
            }

            lineutility.InitializePOINT2Array(pLinePoints);

            //safeguards added 2-17-11 after CPOF client was allowed to add points to autoshapes
            if (vblSaveCounter > pts.size()) {
                vblSaveCounter = pts.size();
            }
            if (vblSaveCounter > pLinePoints.length) {
                vblSaveCounter = pLinePoints.length;
            }

            for (j = 0; j < vblSaveCounter; j++) {
                pt = (POINT2) pts.get(j);
                pLinePoints[j] = new POINT2(pt.x, pt.y, pt.style);
            }
            //we have to adjust the autoshapes because they are instantiating with fewer points
            points = GetLineArray2Double(lineType, pLinePoints, vblCounter, vblSaveCounter, shapes, clipBounds, rev, converter);

        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetLineArray2",
                    new RendererException("GetLineArray2 " + Integer.toString(lineType), exc));

        }
        return points;
        //the caller can get points
    }

    /**
     * A function to calculate the points for FORTL
     *
     * @param pLinePoints OUT - the points arry also used for the return points
     * @param lineType
     * @param vblSaveCounter the number of client points
     * @return
     */
    private static int GetFORTLPointsDouble(POINT2[] pLinePoints,
            int lineType,
            int vblSaveCounter) {
        int nCounter = 0;
        try {
            //declarations
            int j = 0, k = 0, bolVertical = 0;
            int lCount = 0;
            double dLengthSegment = 0, dIncrement = 20;
            ref<double[]> m = new ref();
            POINT2[] pSpikePoints = null;
            POINT2 pt0 = new POINT2(), pt1 = new POINT2();
            //long[] segments=null;
            //long numpts2=0;
            //end delcarations

            lCount = countsupport.GetFORTLCountDouble(pLinePoints, lineType, vblSaveCounter);
            //numpts2=lineutility.BoundPointsCount(pLinePoints,vblSaveCounter);
            //segments=new long[numpts2];
            //lineutility.BoundPoints(ref pLinePoints,vblSaveCounter,ref segments);
            int numGlyphs = 0;
            double dGlyphSize = 0;
            pSpikePoints = new POINT2[lCount];
            lineutility.InitializePOINT2Array(pSpikePoints);
            switch (lineType) {
                default:
                    dIncrement = 20;
                    break;
            }
            for (j = 0; j < vblSaveCounter - 1; j++) {
                bolVertical = lineutility.CalcTrueSlopeDouble(pLinePoints[j], pLinePoints[j + 1], m);
                dLengthSegment = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                if (dLengthSegment / 20 < 1) {
                    pSpikePoints[nCounter] = new POINT2(pLinePoints[j]);
                    nCounter++;
                    pSpikePoints[nCounter] = new POINT2(pLinePoints[j + 1]);
                    nCounter++;
                    continue;
                }
                numGlyphs = (int) (dLengthSegment / dIncrement);
                //dGlyphSize=dIncrement/2;
                dGlyphSize = 10;
                dIncrement = (dLengthSegment / numGlyphs);
                if (dIncrement > 25) {
                    dIncrement = 25;
                    numGlyphs = (int) (dLengthSegment / dIncrement);
                }

                //for (k = 0; k < dLengthSegment / 20 - 1; k++)
                for (k = 0; k < numGlyphs; k++) {
                    pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement, 0);
                    nCounter++;
                    //pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - 10, 0);
                    pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - dIncrement / 2, 0);
                    nCounter++;
                    pt0 = new POINT2(pSpikePoints[nCounter - 1]);
                    //pt1 = lineutility.ExtendLineDouble(pLinePoints[j], pSpikePoints[nCounter - 1], 10);
                    pt1 = lineutility.ExtendLineDouble(pLinePoints[j], pSpikePoints[nCounter - 1], dIncrement / 2);
                    //the spikes
                    if (pLinePoints[j].x > pLinePoints[j + 1].x) {
                        //pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 3, 10);
                        pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 3, dGlyphSize);
                        nCounter++;
                        //pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt1, 3, 10);
                        pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt1, 3, dGlyphSize);
                        nCounter++;
                    }
                    if (pLinePoints[j].x < pLinePoints[j + 1].x) {
                        //pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 2, 10);
                        pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 2, dGlyphSize);
                        nCounter++;
                        //pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt1, 2, 10);
                        pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt1, 2, dGlyphSize);
                        nCounter++;
                    }
                    if (pLinePoints[j].x == pLinePoints[j + 1].x) {
                        if (pLinePoints[j].y < pLinePoints[j + 1].y) {
                            //pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 1, 10);
                            pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 1, dGlyphSize);
                            nCounter++;
                            //pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt1, 1, 10);
                            pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt1, 1, dGlyphSize);
                            nCounter++;
                        }
                        if (pLinePoints[j].y > pLinePoints[j + 1].y) {
                            //pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 0, 10);
                            pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 0, dGlyphSize);
                            nCounter++;
                            //pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt1, 0, 10);
                            pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt1, 0, dGlyphSize);
                            nCounter++;
                        }
                    }
                    //pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j], pSpikePoints[nCounter - 3], 10, 0);
                    pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j], pSpikePoints[nCounter - 3], dIncrement / 2, 0);
                    nCounter++;
                }//end for k
                pSpikePoints[nCounter] = new POINT2(pLinePoints[j + 1]);
                nCounter++;
            }//end for j
            for (j = 0; j < nCounter; j++) {
                pLinePoints[j] = new POINT2(pSpikePoints[j]);
            }

            //clean up
            pSpikePoints = null;
            return nCounter;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetFORTLPointsDouble",
                    new RendererException("GetFORTLPointsDouble " + Integer.toString(lineType), exc));
        }
        return nCounter;
    }

    private static void CoordFEBADouble(
            POINT2[] pLinePoints,
            int vblCounter) {
        try {
            //declarations
            int j = 0;
            POINT2[] pXLinePoints = new POINT2[4 * vblCounter / 32];
            POINT2[] pNewLinePoints = new POINT2[vblCounter / 32];
            POINT2[] pShortLinePoints = new POINT2[2 * vblCounter / 32];
            POINT2[] pArcLinePoints = new POINT2[26 * vblCounter / 32];
            double dPrinter = 1.0;
            //end declarations
            for (j = vblCounter / 32; j < vblCounter; j++) {
                pLinePoints[j] = new POINT2(pLinePoints[0]);	//initialize the rest of pLinePoints
                pLinePoints[j].style = 0;
            }

            for (j = 0; j < 4 * vblCounter / 32; j++) {
                pXLinePoints[j] = new POINT2(pLinePoints[0]);	//initialization only for pXLinePoints
                pXLinePoints[j].style = 0;
            }

            for (j = 0; j < vblCounter / 32; j++) //initialize pNewLinePoints
            {
                pNewLinePoints[j] = new POINT2(pLinePoints[j]);
                pNewLinePoints[j].style = 0;
            }

            for (j = 0; j < 2 * vblCounter / 32; j++) //initialize pShortLinePoints
            {
                pShortLinePoints[j] = new POINT2(pLinePoints[0]);
                pShortLinePoints[j].style = 0;
            }

            for (j = 0; j < 26 * vblCounter / 32; j++) //initialize pArcLinePoints
            {
                pArcLinePoints[j] = new POINT2(pLinePoints[0]);
                pArcLinePoints[j].style = 0;
            }

            //first get the X's
            lineutility.GetXFEBADouble(pNewLinePoints, 10 * dPrinter, vblCounter / 32,//was 7
                    pXLinePoints);

            for (j = 0; j < 4 * vblCounter / 32; j++) {
                pLinePoints[j] = new POINT2(pXLinePoints[j]);
            }

            pLinePoints[4 * vblCounter / 32 - 1].style = 5;

            for (j = 4 * vblCounter / 32; j < 6 * vblCounter / 32; j++) {
                pLinePoints[j] = new POINT2(pShortLinePoints[j - 4 * vblCounter / 32]);
                pLinePoints[j].style = 5;	//toggle invisible lines between feba's
            }

            pLinePoints[6 * vblCounter / 32 - 1].style = 5;

            //last, get the arcs
            lineutility.GetArcFEBADouble(14.0 * dPrinter, pNewLinePoints,
                    vblCounter / 32,
                    pArcLinePoints);

            for (j = 6 * vblCounter / 32; j < vblCounter; j++) {
                pLinePoints[j] = new POINT2(pArcLinePoints[j - 6 * vblCounter / 32]);
            }

            //clean up
            pXLinePoints = null;
            pNewLinePoints = null;
            pShortLinePoints = null;
            pArcLinePoints = null;
            return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "CoordFEBADouble",
                    new RendererException("CoordFEBADouble", exc));
        }
    }

    private static int GetATWallPointsDouble2(POINT2[] pLinePoints,
            int lineType,
            int vblSaveCounter) {
        int nCounter = 0;
        try {
            //declarations
            int j = 0, k = 0;
            int lCount = 0;
            double dLengthSegment = 0, dIncrement = 0;
            POINT2[] pSpikePoints = null;
            POINT2 pt0;
            double dSpikeSize = 0;
            int limit = 0, numSpikes = 0;
            //POINT2 crossPt1, crossPt2;
            //end delcarations

            lCount = countsupport.GetFORTLCountDouble(pLinePoints, lineType, vblSaveCounter);
            pSpikePoints = new POINT2[lCount];
            lineutility.InitializePOINT2Array(pSpikePoints);
            pSpikePoints[nCounter++] = new POINT2(pLinePoints[0]);
            for (j = 0; j < vblSaveCounter - 1; j++) {
                dLengthSegment = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                dIncrement = 20;
                dSpikeSize = 10;
//  diagnostic
                numSpikes = (int) Math.round((dLengthSegment - 10) / dIncrement);
                dIncrement = dLengthSegment / numSpikes;
                if (dIncrement > 25) {
                    dIncrement = 25;
                }
                //limit = (int) (dLengthSegment / dIncrement) - 1;                
                limit = numSpikes - 1;
//                if (limit < 1) {
//                    pSpikePoints[nCounter] = new POINT2(pLinePoints[j]);
//                    nCounter++;
//                    pSpikePoints[nCounter] = new POINT2(pLinePoints[j + 1]);
//                    nCounter++;
//                    continue;
//                }
//  end section                
                for (k = -1; k < limit; k++)//was k=0 to limit
                {
                    pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - 30, 0);
                    nCounter++;

                    pt0 = lineutility.ExtendLineDouble(pLinePoints[j], pSpikePoints[nCounter - 1], dSpikeSize / 2);

                    //the spikes
                    if (pLinePoints[j].x > pLinePoints[j + 1].x) //extend above the line
                    {
                        pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pSpikePoints[nCounter - 1], pt0, 2, dSpikeSize);
                    }
                    if (pLinePoints[j].x < pLinePoints[j + 1].x) //extend below the line
                    {
                        pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pSpikePoints[nCounter - 1], pt0, 3, dSpikeSize);
                    }
                    if (pLinePoints[j].x == pLinePoints[j + 1].x) {
                        pSpikePoints[nCounter] = new POINT2(pt0);
                        if (pLinePoints[j].y < pLinePoints[j + 1].y) //extend left of line
                        {
                            pSpikePoints[nCounter].x = pt0.x - dSpikeSize;
                        } else //extend right of line
                        {
                            pSpikePoints[nCounter].x = pt0.x + dSpikeSize;
                        }
                    }
                    nCounter++;

                    pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j], pSpikePoints[nCounter - 2], dSpikeSize, 0);
                    nCounter++;
                }
                //use the original line point for the segment end point
                pSpikePoints[nCounter] = new POINT2(pLinePoints[j + 1]);
                pSpikePoints[nCounter].style = 0;
                nCounter++;
            }

            for (j = 0; j < nCounter; j++) {
                pLinePoints[j] = new POINT2(pSpikePoints[j]);
            }

            //clean up
            pSpikePoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetATWallPointsDouble2",
                    new RendererException("GetATWallPointsDouble2", exc));
        }
        return nCounter;
    }

    public static int GetInsideOutsideDouble2(POINT2 pt0,
            POINT2 pt1,
            POINT2[] pLinePoints,
            int vblCounter,
            int index,
            int lineType) {
        int nDirection = 0;
        try {
            //declarations
            ref<double[]> m = new ref();
            ref<double[]> m0 = new ref();

            double b0 = 0;
            double b2 = 0;

            double b = 0;
            double X0 = 0;	//segment midpoint X value
            double Y0 = 0;	//segment midpoint Y value
            double X = 0;	//X value of horiz line from left intercept with current segment
            double Y = 0;	//Y value of vertical line from top intercept with current segment
            int nInOutCounter = 0;
            int j = 0, bolVertical = 0;
            int bolVertical2 = 0;
            int nOrientation = 0; //will use 0 for horiz line from left, 1 for vertical line from top
            int extendLeft = 0;
            int extendRight = 1;
            int extendAbove = 2;
            int extendBelow = 3;

            int oppSegment = vblCounter - index - 3;	//used by BELT1 only
            POINT2 pt2 = new POINT2();
            //end declarations. will use this to determine the direction

            //slope of the segment
            bolVertical = lineutility.CalcTrueSlopeDouble(pt0, pt1, m0);
            if (m0.value == null) {
                return 0;
            }
            //get the midpoint of the segment
            X0 = (pt0.x + pt1.x) / 2;
            Y0 = (pt0.y + pt1.y) / 2;

            if (lineType == TacticalLines.BELT1 && oppSegment >= 0 && oppSegment < vblCounter - 1) {
                //get the midpoint of the opposite segment
                X0 = (pLinePoints[oppSegment].x + pLinePoints[oppSegment + 1].x) / 2;
                Y0 = (pLinePoints[oppSegment].y + pLinePoints[oppSegment + 1].y) / 2;
                //must calculate the corresponding point on the current segment
                //first get the y axis intercept of the perpendicular line for the opposite (short) segment
                //calculate this line at the midpoint of the opposite (short) segment
                b0 = Y0 + 1 / m0.value[0] * X0;
                //the y axis intercept of the index segment
                b2 = pt0.y - m0.value[0] * pt0.x;
                if (m0.value[0] != 0 && bolVertical != 0) {
                    //calculate the intercept at the midpoint of the shorter segment
                    pt2 = lineutility.CalcTrueIntersectDouble2(-1 / m0.value[0], b0, m0.value[0], b2, 1, 1, 0, 0);
                    X0 = pt2.x;
                    Y0 = pt2.y;
                }
                if (m0.value[0] == 0 && bolVertical != 0) {
                    X0 = (pLinePoints[oppSegment].x + pLinePoints[oppSegment + 1].x) / 2;
                    Y0 = (pt0.y + pt1.y) / 2;
                }
                if (bolVertical == 0) {
                    Y0 = (pLinePoints[oppSegment].y + pLinePoints[oppSegment + 1].y) / 2;
                    X0 = (pt0.x + pt1.x) / 2;
                }
            }

            //slope is not too small or is vertical, use left to right
            if (Math.abs(m0.value[0]) >= 1 || bolVertical == 0) {
                nOrientation = 0;	//left to right orientation
                for (j = 0; j < vblCounter - 1; j++) {
                    if (index != j) {
                        //for BELT1 we only want to know if the opposing segment is to the
                        //left of the segment (index), do not check other segments
                        if (lineType == TacticalLines.BELT1 && oppSegment != j) //change 2
                        {
                            continue;
                        }

                        //if ((pLinePoints[j].y <= Y0 && pLinePoints[j + 1].y >= Y0) ||
                        //(pLinePoints[j].y >= Y0 && pLinePoints[j + 1].y <= Y0)) 
                        if ((pLinePoints[j].y < Y0 && pLinePoints[j + 1].y > Y0)
                                || (pLinePoints[j].y > Y0 && pLinePoints[j + 1].y < Y0)
                                || (pLinePoints[j].y < Y0 && pLinePoints[j + 1].y == Y0)
                                || (pLinePoints[j].y == Y0 && pLinePoints[j + 1].y < Y0)) {
                            bolVertical2 = lineutility.CalcTrueSlopeDouble(pLinePoints[j], pLinePoints[j + 1], m);
                            if (bolVertical2 == 1 && m.value[0] == 0) //current segment is horizontal, this should not happen
                            {	//counter unaffected
                                nInOutCounter++;
                                nInOutCounter--;
                            }
                            //current segment is vertical, it's x value must be to the left
                            //of the current segment X0 for the horiz line from the left to cross
                            if (bolVertical2 == 0) {
                                if (pLinePoints[j].x < X0) {
                                    nInOutCounter++;
                                }
                            }

                            //current segment is not horizontal and not vertical
                            if (m.value[0] != 0 && bolVertical2 == 1) {
                                //get the X value of the intersection between the horiz line
                                //from the left and the current segment
                                //b=Y0;
                                b = pLinePoints[j].y - m.value[0] * pLinePoints[j].x;
                                X = (Y0 - b) / m.value[0];
                                if (X < X0) //the horizontal line crosses the segment
                                {
                                    nInOutCounter++;
                                }
                            }

                        }	//end if
                    }

                }	//end for
            } //end if
            else //use top to bottom to get orientation
            {
                nOrientation = 1;	//top down orientation
                for (j = 0; j < vblCounter - 1; j++) {
                    if (index != j) {
                        //for BELT1 we only want to know if the opposing segment is
                        //above the segment (index), do not check other segments
                        if (lineType == TacticalLines.BELT1 && oppSegment != j) {
                            continue;
                        }

                        //if ((pLinePoints[j].x <= X0 && pLinePoints[j + 1].x >= X0) ||
                        //  (pLinePoints[j].x >= X0 && pLinePoints[j + 1].x <= X0)) 
                        if ((pLinePoints[j].x < X0 && pLinePoints[j + 1].x > X0)
                                || (pLinePoints[j].x > X0 && pLinePoints[j + 1].x < X0)
                                || (pLinePoints[j].x < X0 && pLinePoints[j + 1].x == X0)
                                || (pLinePoints[j].x == X0 && pLinePoints[j + 1].x < X0)) {
                            bolVertical2 = lineutility.CalcTrueSlopeDouble(pLinePoints[j], pLinePoints[j + 1], m);
                            if (bolVertical2 == 0) //current segment is vertical, this should not happen
                            {	//counter unaffected
                                nInOutCounter++;
                                nInOutCounter--;
                            }
                            //current segment is horizontal, it's y value must be above
                            //the current segment Y0 for the horiz line from the left to cross
                            if (bolVertical2 == 1 && m.value[0] == 0) {
                                if (pLinePoints[j].y < Y0) {
                                    nInOutCounter++;
                                }
                            }

                            //current segment is not horizontal and not vertical
                            if (m.value[0] != 0 && bolVertical2 == 1) {
                                //get the Y value of the intersection between the vertical line
                                //from the top and the current segment
                                b = pLinePoints[j].y - m.value[0] * pLinePoints[j].x;
                                Y = m.value[0] * X0 + b;
                                if (Y < Y0) //the vertical line crosses the segment
                                {
                                    nInOutCounter++;
                                }
                            }
                        }	//end if
                    }
                }	//end for
            }

            switch (nInOutCounter % 2) {
                case 0:
                    if (nOrientation == 0) {
                        nDirection = extendLeft;
                    } else {
                        nDirection = extendAbove;
                    }
                    break;
                case 1:
                    if (nOrientation == 0) {
                        nDirection = extendRight;
                    } else {
                        nDirection = extendBelow;
                    }
                    break;
                default:
                    break;
            }
            //reverse direction for ICING
            switch (lineType) {
                case TacticalLines.ICING:
                    if (nDirection == extendLeft) {
                        nDirection = extendRight;
                    } else if (nDirection == extendRight) {
                        nDirection = extendLeft;
                    } else if (nDirection == extendAbove) {
                        nDirection = extendBelow;
                    } else if (nDirection == extendBelow) {
                        nDirection = extendAbove;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetInsideOutsideDouble2",
                    new RendererException("GetInsideOutsideDouble2", exc));
        }
        return nDirection;
    }

    /**
     * BELT1 line and others
     *
     * @param pLinePoints
     * @param lineType
     * @param vblSaveCounter
     * @return
     */
    protected static int GetZONEPointsDouble2(POINT2[] pLinePoints,
            int lineType,
            int vblSaveCounter) {
        int nCounter = 0;
        try {
            //declarations
            int j = 0, k = 0, n = 0;
            int lCount = 0;
            double dLengthSegment = 0;
            POINT2 pt0 = new POINT2(pLinePoints[0]), pt1 = null, pt2 = null, pt3 = null;
            POINT2[] pSpikePoints = null;
            int nDirection = 0;
            //end delcarations

            lCount = countsupport.GetFORTLCountDouble(pLinePoints, lineType, vblSaveCounter);
            pSpikePoints = new POINT2[lCount];
            lineutility.InitializePOINT2Array(pSpikePoints);
            double remainder = 0;
            //for(j=0;j<numpts2-1;j++)
            for (j = 0; j < vblSaveCounter - 1; j++) {
                pt1 = new POINT2(pLinePoints[j]);
                pt2 = new POINT2(pLinePoints[j + 1]);
                //get the direction for the spikes
                nDirection = GetInsideOutsideDouble2(pt1, pt2, pLinePoints, vblSaveCounter, (int) j, lineType);
                dLengthSegment = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                //reverse the direction for those lines with inward spikes
                if (!(lineType == TacticalLines.BELT) && !(lineType == TacticalLines.BELT1)) {
                    if (dLengthSegment < 20) {
                        pSpikePoints[nCounter] = new POINT2(pLinePoints[j]);
                        nCounter++;
                        pSpikePoints[nCounter] = new POINT2(pLinePoints[j + 1]);
                        nCounter++;
                        continue;
                    }
                }
                switch (lineType) {
                    case TacticalLines.OBSAREA:
                    case TacticalLines.OBSFAREA:
                        switch (nDirection) {
                            case 0:	//extend left
                                nDirection = 1;	//extend right
                                break;
                            case 1:	//extend right
                                nDirection = 0;	//extend left
                                break;
                            case 2:	//extend above
                                nDirection = 3;	//extend below
                                break;
                            case 3:	//extgend below
                                nDirection = 2;	//extend above
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                n = (int) (dLengthSegment / 20);
                remainder = dLengthSegment - n * 20;
                for (k = 0; k < n; k++) {
                    if (k > 0) {
                        pSpikePoints[nCounter++] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * 20 - remainder / 2, 0);//was +0
                        pSpikePoints[nCounter++] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * 20 - 10 - remainder / 2, 0);//was -10
                    } else {
                        pSpikePoints[nCounter++] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * 20, 0);//was +0
                        pSpikePoints[nCounter++] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * 20 - 10, 0);//was -10
                    }

                    switch (lineType) {
                        case TacticalLines.OBSAREA:
                        case TacticalLines.OBSFAREA:
                        case TacticalLines.ZONE:
                        case TacticalLines.BELT:
                        case TacticalLines.BELT1:
                        case TacticalLines.ENCIRCLE:
                            pt0 = lineutility.ExtendLineDouble(pLinePoints[j], pSpikePoints[nCounter - 1], 5);
                            break;
                        case TacticalLines.STRONG:
                        case TacticalLines.FORT:
                            pt0 = new POINT2(pSpikePoints[nCounter - 1]);
                            break;
                        default:
                            break;
                    }

                    pSpikePoints[nCounter++] = lineutility.ExtendDirectedLine(pt1, pt2, pt0, nDirection, 10);
                    //nCounter++;
                    switch (lineType) {
                        case TacticalLines.OBSAREA:
                        case TacticalLines.OBSFAREA:
                        case TacticalLines.ZONE:
                        case TacticalLines.BELT:
                        case TacticalLines.BELT1:
                        case TacticalLines.ENCIRCLE:
                            pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j], pSpikePoints[nCounter - 2], 10, 0);
                            break;
                        case TacticalLines.STRONG:
                            pSpikePoints[nCounter] = new POINT2(pSpikePoints[nCounter - 2]);
                            break;
                        case TacticalLines.FORT:
                            pt3 = lineutility.ExtendLine2Double(pLinePoints[j], pSpikePoints[nCounter - 2], 10, 0);
                            pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pt1, pt2, pt3, nDirection, 10);
                            nCounter++;
                            pSpikePoints[nCounter] = new POINT2(pt3);
                            break;
                        default:
                            break;
                    }
                    //}
                    nCounter++;
                    //diagnostic
                    if (lineType == TacticalLines.ENCIRCLE) {
                        pSpikePoints[nCounter++] = new POINT2(pSpikePoints[nCounter - 4]);
                    }
                }//end for k
                pSpikePoints[nCounter++] = new POINT2(pLinePoints[j + 1]);
                //nCounter++;
            }//end for j
            for (j = 0; j < nCounter; j++) {
                if (lineType == (long) TacticalLines.OBSAREA) {
                    pSpikePoints[j].style = 11;
                }
            }
            if (lineType == (long) TacticalLines.OBSAREA) {
                pSpikePoints[nCounter - 1].style = 12;
            } else {
                if (nCounter > 0) {
                    pSpikePoints[nCounter - 1].style = 5;
                }
            }

            for (j = 0; j < nCounter; j++) {
                pLinePoints[j] = new POINT2(pSpikePoints[j]);
                if (j == nCounter - 1) {
                    if (lineType != (long) TacticalLines.OBSAREA) {
                        pLinePoints[j].style = 5;
                    }
                }
            }

            //clean up
            pSpikePoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetZONEPointsDouble2",
                    new RendererException("GetZONEPointsDouble2", exc));
        }
        return nCounter;
    }

    private static boolean IsTurnArcReversed(POINT2[] pPoints) {
        try {
            if (pPoints.length < 3) {
                return false;
            }

            POINT2[] ptsSeize = new POINT2[2];
            ptsSeize[0] = new POINT2(pPoints[0]);
            ptsSeize[1] = new POINT2(pPoints[1]);
            lineutility.CalcClockwiseCenterDouble(ptsSeize);
            double d = lineutility.CalcDistanceDouble(ptsSeize[0], pPoints[2]);

            ptsSeize[0] = new POINT2(pPoints[1]);
            ptsSeize[1] = new POINT2(pPoints[0]);
            lineutility.CalcClockwiseCenterDouble(ptsSeize);
            double dArcReversed = lineutility.CalcDistanceDouble(ptsSeize[0], pPoints[2]);

            ptsSeize = null;
            if (dArcReversed > d) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "IsTurnArcReversed",
                    new RendererException("IsTurnArcReversed", exc));
        }
        return false;
    }

    private static void GetIsolatePointsDouble(POINT2[] pLinePoints,
            int lineType,
            IPointConversion converter) {
        try {
            //declarations
            boolean reverseTurn = false;
            POINT2 pt0 = new POINT2(pLinePoints[0]), pt1 = new POINT2(pLinePoints[1]), pt2 = new POINT2(pLinePoints[0]);
            if (pt0.x == pt1.x && pt0.y == pt1.y) {
                pt1.x += 1;
            }

            POINT2 C = new POINT2(), E = new POINT2(), midPt = new POINT2();
            int j = 0, k = 0, l = 0;
            POINT2[] ptsArc = new POINT2[26];
            POINT2[] midPts = new POINT2[7];
            POINT2[] trianglePts = new POINT2[21];
            POINT2[] pArrowPoints = new POINT2[3], reversepArrowPoints = new POINT2[3];
            double dRadius = lineutility.CalcDistanceDouble(pt0, pt1);
            double dLength = Math.abs(dRadius - 20);
            if (dRadius < 40) {
                dLength = dRadius / 1.5;
            }

            double d = lineutility.MBRDistance(pLinePoints, 2);
            POINT2[] ptsSeize = new POINT2[2];
            POINT2[] savepoints = new POINT2[3];
            for (j = 0; j < 2; j++) {
                savepoints[j] = new POINT2(pLinePoints[j]);
            }

            if (pLinePoints.length >= 3) {
                savepoints[2] = new POINT2(pLinePoints[2]);
            }

            lineutility.InitializePOINT2Array(ptsArc);
            lineutility.InitializePOINT2Array(midPts);
            lineutility.InitializePOINT2Array(trianglePts);
            lineutility.InitializePOINT2Array(pArrowPoints);
            lineutility.InitializePOINT2Array(reversepArrowPoints);
            lineutility.InitializePOINT2Array(ptsSeize);

            if (d / 7 > maxLength) {
                d = 7 * maxLength;
            }
            if (d / 7 < minLength) {  //was minLength
                d = 7 * minLength;    //was minLength
            }
            //change due to outsized arrow in 6.0, 11-3-10
            if (d > 140) {
                d = 140;
            }
            //calculation points for the SEIZE arrowhead
            //for SEIZE calculations
            POINT2[] ptsArc2 = new POINT2[26];
            lineutility.InitializePOINT2Array(ptsArc2);
            //end declarations

            E.x = 2 * pt1.x - pt0.x;
            E.y = 2 * pt1.y - pt0.y;
            ptsArc[0] = new POINT2(pLinePoints[1]);
            ptsArc[1] = new POINT2(E);
            if(converter != null)
            {
                ptsArc[0] = new POINT2(pLinePoints[0]);
                ptsArc[1] = new POINT2(pLinePoints[1]);
            }

            lineutility.ArcArrayDouble(ptsArc, 0, dRadius, lineType, converter);
            for (j = 0; j < 26; j++) {
                ptsArc[j].style = 0;
                pLinePoints[j] = new POINT2(ptsArc[j]);
                pLinePoints[j].style = 0;
            }
            if (lineType != TacticalLines.OCCUPY) {
                lineutility.GetArrowHead4Double(ptsArc[24], ptsArc[25], (int) d / 7, (int) d / 7, pArrowPoints, 0);
            } else {
                lineutility.GetArrowHead4Double(ptsArc[24], ptsArc[25], (int) d / 7, (int) (1.75 * d) / 7, pArrowPoints, 0);
            }

            pLinePoints[25].style = 5;

            switch (lineType) {
                case TacticalLines.CORDONKNOCK:
                case TacticalLines.CORDONSEARCH:
                case TacticalLines.ISOLATE:
                    if (dRadius > 100) {
                        dLength = 0.8 * dRadius;
                    }
                    for (j = 1; j <= 23; j++) {
                        if (j % 3 == 0) {
                            midPts[k].x = pt0.x - (long) ((dLength / dRadius) * (pt0.x - ptsArc[j].x));
                            midPts[k].y = pt0.y - (long) ((dLength / dRadius) * (pt0.y - ptsArc[j].y));
                            midPts[k].style = 0;
                            trianglePts[l] = new POINT2(ptsArc[j - 1]);
                            l++;
                            trianglePts[l] = new POINT2(midPts[k]);
                            l++;
                            trianglePts[l] = new POINT2(ptsArc[j + 1]);
                            trianglePts[l].style = 5;
                            l++;
                            k++;
                        }
                    }
                    for (j = 26; j < 47; j++) {
                        pLinePoints[j] = new POINT2(trianglePts[j - 26]);
                    }
                    pLinePoints[46].style = 5;
                    for (j = 47; j < 50; j++) {
                        pLinePoints[j] = new POINT2(pArrowPoints[j - 47]);
                        pLinePoints[j].style = 0;
                    }
                    break;
                case TacticalLines.OCCUPY:
                    midPt.x = (pt1.x + ptsArc[25].x) / 2;
                    midPt.y = (pt1.y + ptsArc[25].y) / 2;
                    //lineutility.GetArrowHead4Double(midPt, ptsArc[25], (int) d / 7, (int) d / 7, reversepArrowPoints, 0);
                    lineutility.GetArrowHead4Double(midPt, ptsArc[25], (int) d / 7, (int) (1.75 * d) / 7, reversepArrowPoints, 0);
                    for (j = 26; j < 29; j++) {
                        pLinePoints[j] = new POINT2(pArrowPoints[j - 26]);
                    }
                    for (j = 29; j < 32; j++) {
                        pLinePoints[j] = new POINT2(reversepArrowPoints[j - 29]);
                        pLinePoints[j].style = 0;
                    }
                    break;
                case TacticalLines.SECURE:
                    for (j = 26; j < 29; j++) {
                        pLinePoints[j] = new POINT2(pArrowPoints[j - 26]);
                        pLinePoints[j].style = 0;
                    }
                    pLinePoints[28].style = 5;
                    break;

                case TacticalLines.TURN:
                    boolean changeArc = IsTurnArcReversed(savepoints); //change 1
                    if (reverseTurn == true || changeArc == true) //swap the points
                    {
                        pt0.x = pt1.x;
                        pt0.y = pt1.y;
                        pt1.x = pt2.x;
                        pt1.y = pt2.y;
                    }

                    ptsSeize[0] = new POINT2(pt0);
                    ptsSeize[1] = new POINT2(pt1);

                    dRadius = lineutility.CalcClockwiseCenterDouble(ptsSeize);

                    C = new POINT2(ptsSeize[0]);
                    E = new POINT2(ptsSeize[1]);
                    ptsArc[0] = new POINT2(pt0);
                    ptsArc[1] = new POINT2(E);
                    lineutility.ArcArrayDouble(ptsArc, 0, dRadius, lineType, converter);
                    for (j = 0; j < 26; j++) {
                        ptsArc[j].style = 0;
                        pLinePoints[j] = new POINT2(ptsArc[j]);
                        pLinePoints[j].style = 0;
                    }

                    if (changeArc == true)//if(changeArc==false)    //change 1
                    {
                        lineutility.GetArrowHead4Double(ptsArc[1], pt0, (int) d / 7, (int) d / 7, pArrowPoints, 5);
                    } else {
                        lineutility.GetArrowHead4Double(ptsArc[24], pt1, (int) d / 7, (int) d / 7, pArrowPoints, 5);
                    }

                    pLinePoints[25].style = 5;

                    for (j = 26; j < 29; j++) {
                        pLinePoints[j] = new POINT2(pArrowPoints[j - 26]);
                        pLinePoints[j].style = 9;
                    }
                    pLinePoints[28].style = 10;

                    break;
                case TacticalLines.RETAIN:
                    for (j = 26; j < 29; j++) {
                        pLinePoints[j] = new POINT2(pArrowPoints[j - 26]);
                        pLinePoints[j].style = 0;
                    }
                    pLinePoints[28].style = 5;
                    //get the extended points for retain
                    k = 29;
                    for (j = 1; j < 24; j++) {
                        pLinePoints[k] = new POINT2(ptsArc[j]);
                        pLinePoints[k].style = 0;
                        k++;
                        pLinePoints[k] = lineutility.ExtendLineDouble(pt0, ptsArc[j], (long) d / 7);
                        pLinePoints[k].style = 5;
                        k++;
                    }

                    break;
                default:
                    break;
            }

            //clean up
            savepoints = null;
            ptsArc = null;
            midPts = null;
            trianglePts = null;
            pArrowPoints = null;
            reversepArrowPoints = null;
            ptsSeize = null;
            ptsArc2 = null;
            //return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetIsolatePointsDouble",
                    new RendererException("GetIsolatePointsDouble " + Integer.toString(lineType), exc));
        }
        return;
    }

    /**
     * @deprecated returns the location for the Dummy Hat
     * @param pLinePoints
     * @return
     */
    private static POINT2 getDummyHat(POINT2[] pLinePoints) {
        POINT2 pt = null;
        try {
            int j = 0;
            double minY = Double.MAX_VALUE;
            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
            int index = -1;
            //get the highest point
            for (j = 0; j < pLinePoints.length - 3; j++) {
                if (pLinePoints[j].y < minY) {
                    minY = pLinePoints[j].y;
                    index = j;
                }
                if (pLinePoints[j].x < minX) {
                    minX = pLinePoints[j].x;
                }
                if (pLinePoints[j].x > maxX) {
                    maxX = pLinePoints[j].x;
                }
            }
            pt = new POINT2(pLinePoints[index]);
            double deltaMaxX = 0;
            double deltaMinX = 0;
            if (pt.x + 25 > maxX) {
                deltaMaxX = pt.x + 25 - maxX;
                pt.x -= deltaMaxX;
            }
            if (pt.x - 25 < minX) {
                deltaMinX = minX - (pt.x - 25);
                pt.x += deltaMinX;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "getDummyHat",
                    new RendererException("getDummyHat", exc));
        }
        return pt;
    }

    private static void AreaWithCenterFeatureDouble(POINT2[] pLinePoints,
            int vblCounter,
            int lineType) {
        try {
            //declarations
            int k = 0;
            POINT2 ptCenter = new POINT2();
            int fLength = 4;
            if (lineType == TacticalLines.AIRFIELD) {
                fLength = 5;
            }
            double d = lineutility.MBRDistance(pLinePoints, vblCounter - fLength);
            //11-18-2010
            if (d > 350) {
                d = 350;
            }

            //end declarations
            for (k = 0; k < vblCounter; k++) {
                pLinePoints[k].style = 0;
            }
            switch (lineType) {
                case TacticalLines.DUMMY:
                    if (d < 20) {
                        d = 20;
                    }
                    if (d > 60) {
                        d = 60;
                    }
                    POINT2 ul = new POINT2();
                    POINT2 lr = new POINT2();
                    lineutility.CalcMBRPoints(pLinePoints, vblCounter - 4, ul, lr);
                    //ul=getDummyHat(pLinePoints);
                    //ul.x-=25;
                    POINT2 ur = new POINT2(lr);
                    ur.y = ul.y;

                    pLinePoints[vblCounter - 3] = lineutility.MidPointDouble(ur, ul, 0);
                    pLinePoints[vblCounter - 3].x -= d / 2;//25;
                    pLinePoints[vblCounter - 3].y -= d / 5;//10;
                    pLinePoints[vblCounter - 2] = lineutility.MidPointDouble(ur, ul, 0);
                    pLinePoints[vblCounter - 2].y -= d * 0.7;//35;
                    pLinePoints[vblCounter - 1] = lineutility.MidPointDouble(ur, ul, 0);
                    pLinePoints[vblCounter - 1].x += d / 2;//25;
                    pLinePoints[vblCounter - 1].y -= d / 5;//10;
                    pLinePoints[vblCounter - 4].style = 5;
                    break;
                case TacticalLines.AIRFIELD:
                    if (d < 100) {
                        d = 100;
                    }
                    pLinePoints[vblCounter - 5] = new POINT2(pLinePoints[0]);
                    pLinePoints[vblCounter - 5].style = 5;
                    pLinePoints[vblCounter - 4] = lineutility.CalcCenterPointDouble(pLinePoints, vblCounter - 6);
                    pLinePoints[vblCounter - 4].x -= d / 10;    //was 20
                    pLinePoints[vblCounter - 4].style = 0;
                    pLinePoints[vblCounter - 3] = new POINT2(pLinePoints[vblCounter - 4]);
                    pLinePoints[vblCounter - 3].x = pLinePoints[vblCounter - 4].x + d / 5;//was 10
                    pLinePoints[vblCounter - 3].style = 5;
                    pLinePoints[vblCounter - 2] = new POINT2(pLinePoints[vblCounter - 4]);
                    pLinePoints[vblCounter - 2].y += d / 20;//was 40
                    pLinePoints[vblCounter - 2].style = 0;
                    pLinePoints[vblCounter - 1] = new POINT2(pLinePoints[vblCounter - 3]);
                    pLinePoints[vblCounter - 1].y -= d / 20;//was 40
                    pLinePoints[vblCounter - 1].style = 0;
                    break;
                case TacticalLines.DMA:
                    if (d < 50) {
                        d = 50;
                    }
                    if (lineType == (long) TacticalLines.DMA) {
                        for (k = 0; k < vblCounter - 4; k++) {
                            pLinePoints[k].style = 14;
                        }
                    }
                    pLinePoints[vblCounter - 4] = new POINT2(pLinePoints[0]);
                    pLinePoints[vblCounter - 4].style = 5;
                    ptCenter = lineutility.CalcCenterPointDouble(pLinePoints, vblCounter - 4);
                    pLinePoints[vblCounter - 3].x = ptCenter.x - d / 10;
                    pLinePoints[vblCounter - 3].y = ptCenter.y;
                    pLinePoints[vblCounter - 3].style = 18;
                    pLinePoints[vblCounter - 2].x = ptCenter.x;
                    pLinePoints[vblCounter - 2].y = ptCenter.y - d / 10;
                    pLinePoints[vblCounter - 2].style = 18;
                    pLinePoints[vblCounter - 1].x = ptCenter.x + d / 10;
                    pLinePoints[vblCounter - 1].y = ptCenter.y;
                    break;
                case TacticalLines.DMAF:
                    if (d < 50) {
                        d = 50;
                    }
                    pLinePoints[vblCounter - 4].style = 5;
                    ptCenter = lineutility.CalcCenterPointDouble(pLinePoints, vblCounter - 4);
                    pLinePoints[vblCounter - 3].x = ptCenter.x - d / 10;
                    pLinePoints[vblCounter - 3].y = ptCenter.y;
                    pLinePoints[vblCounter - 3].style = 18;
                    pLinePoints[vblCounter - 2].x = ptCenter.x;
                    pLinePoints[vblCounter - 2].y = ptCenter.y - d / 10;
                    pLinePoints[vblCounter - 2].style = 18;
                    pLinePoints[vblCounter - 1].x = ptCenter.x + d / 10;
                    pLinePoints[vblCounter - 1].y = ptCenter.y;
                    pLinePoints[vblCounter - 1].style = 5;
                    break;
                default:
                    break;
            }
            return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "AreaWithCenterFeatureDouble",
                    new RendererException("AreaWithCenterFeatureDouble " + Integer.toString(lineType), exc));
        }
    }

    private static int GetATWallPointsDouble(POINT2[] pLinePoints,
            int lineType,
            int vblSaveCounter) {
        int nCounter = 0;
        try {
            //declarations
            int j = 0, k = 0;
            int lCount = 0;
            double dLengthSegment = 0, dIncrement = 0;
            POINT2[] pSpikePoints = null;
            POINT2 pt0;
            double dRemainder = 0, dSpikeSize = 0;
            int limit = 0;
            POINT2 crossPt1, crossPt2;
            //end delcarations

            lCount = countsupport.GetFORTLCountDouble(pLinePoints, lineType, vblSaveCounter);
            pSpikePoints = new POINT2[lCount];
            switch (lineType) {
                case TacticalLines.CFG:
                case TacticalLines.CFY:
                    pSpikePoints[nCounter] = pLinePoints[0];
                    pSpikePoints[nCounter].style = 0;
                    nCounter++;
                    break;
                default:
                    break;
            }
            for (j = 0; j < vblSaveCounter - 1; j++) {
                dLengthSegment = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                switch (lineType) {
                    case TacticalLines.UCF:
                    case TacticalLines.CF:
                    case TacticalLines.CFG:
                    case TacticalLines.CFY:
                        dIncrement = 60;
                        dSpikeSize = 20;
                        dRemainder = dLengthSegment / dIncrement - (double) ((int) (dLengthSegment / dIncrement));
                        if (dRemainder < 0.75) {
                            limit = (int) (dLengthSegment / dIncrement);
                        } else {
                            limit = (int) (dLengthSegment / dIncrement) + 1;
                        }
                        break;
                    default:
                        dIncrement = 20;
                        dSpikeSize = 10;
                        limit = (int) (dLengthSegment / dIncrement) - 1;
                        break;
                }
                if (limit < 1) {
                    pSpikePoints[nCounter] = pLinePoints[j];
                    nCounter++;
                    pSpikePoints[nCounter] = pLinePoints[j + 1];
                    nCounter++;
                    continue;
                }

                for (k = 0; k < limit; k++) {
                    switch (lineType) {
                        case TacticalLines.CFG:	//linebreak for dot
                            if (k > 0) {
                                pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement + 45, 0);
                                nCounter++;
                                pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement + 4, 5);	//+2
                                nCounter++;
                                //dot
                                pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - 1, 20);
                                nCounter++;
                                //remainder of line
                                pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - 7, 0);	//-4
                            } else {
                                pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - 45, 0);
                            }
                            break;
                        case TacticalLines.CFY:	//linebreak for crossed line
                            if (k > 0) {
                                pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement + 45, 0);
                                nCounter++;
                                pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement + 10, 5);	//+2
                                nCounter++;
                                //dot
                                //pSpikePoints[nCounter]=lineutility.ExtendLine2Double(pLinePoints[j+1],pLinePoints[j],-k*dIncrement-1,20);
                                //nCounter++;
                                //replace the dot with crossed line segment
                                pSpikePoints[nCounter] = lineutility.ExtendAlongLineDouble(pSpikePoints[nCounter - 1], pLinePoints[j + 1], 5, 0);
                                nCounter++;
                                pSpikePoints[nCounter] = lineutility.ExtendAlongLineDouble(pSpikePoints[nCounter - 1], pLinePoints[j + 1], 10, 5);
                                nCounter++;
                                crossPt1 = lineutility.ExtendDirectedLine(pSpikePoints[nCounter - 2], pSpikePoints[nCounter - 1], pSpikePoints[nCounter - 1], 2, 5, 0);
                                crossPt2 = lineutility.ExtendDirectedLine(pSpikePoints[nCounter - 1], pSpikePoints[nCounter - 2], pSpikePoints[nCounter - 2], 3, 5, 5);
                                pSpikePoints[nCounter] = crossPt1;
                                nCounter++;
                                pSpikePoints[nCounter] = crossPt2;
                                nCounter++;
                                //remainder of line
                                pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - 13, 0);	//-4
                            } else {
                                pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - 45, 0);
                            }
                            break;
                        default:
                            pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - 30, 0);
                            break;
                    }
                    if (lineType == TacticalLines.CF) {
                        pSpikePoints[nCounter].style = 0;
                    }
                    nCounter++;
                    pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement - dSpikeSize, 0);

                    if (lineType == TacticalLines.CF
                            || lineType == TacticalLines.CFG
                            || lineType == TacticalLines.CFY) {
                        pSpikePoints[nCounter].style = 9;
                    }

                    nCounter++;
                    pt0 = lineutility.ExtendLineDouble(pLinePoints[j], pSpikePoints[nCounter - 1], dSpikeSize / 2);

                    //the spikes
                    if (pLinePoints[j].x > pLinePoints[j + 1].x) //extend above the line
                    {
                        pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pSpikePoints[nCounter - 1], pt0, 2, dSpikeSize);
                    }
                    if (pLinePoints[j].x < pLinePoints[j + 1].x) //extend below the line
                    {
                        pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pSpikePoints[nCounter - 1], pt0, 3, dSpikeSize);
                    }
                    if (pLinePoints[j].x == pLinePoints[j + 1].x) {
                        pSpikePoints[nCounter] = pt0;
                        if (pLinePoints[j].y < pLinePoints[j + 1].y) //extend left of line
                        {
                            pSpikePoints[nCounter].x = pt0.x - dSpikeSize;
                        } else //extend right of line
                        {
                            pSpikePoints[nCounter].x = pt0.x + dSpikeSize;
                        }
                    }
                    nCounter++;

                    if (lineType == TacticalLines.CF
                            || lineType == TacticalLines.CFG
                            || lineType == TacticalLines.CFY) {
                        pSpikePoints[nCounter - 1].style = 9;
                    }

                    pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j], pSpikePoints[nCounter - 2], dSpikeSize, 0);
                    //need an extra point for these
                    switch (lineType) {
                        case TacticalLines.CF:
                            pSpikePoints[nCounter].style = 10;
                            break;
                        case TacticalLines.CFG:
                        case TacticalLines.CFY:
                            pSpikePoints[nCounter].style = 10;
                            nCounter++;
                            pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j], pSpikePoints[nCounter - 3], dSpikeSize, 0);
                            break;
                        default:
                            break;
                    }
                    nCounter++;
                }

                //use the original line point for the segment end point
                pSpikePoints[nCounter] = pLinePoints[j + 1];
                pSpikePoints[nCounter].style = 0;
                nCounter++;
            }

            for (j = 0; j < nCounter; j++) {
                pLinePoints[j] = pSpikePoints[j];
            }
            pLinePoints[nCounter - 1].style = 5;

            //clean up
            pSpikePoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetATWallPointsDouble",
                    new RendererException("GetATWallPointsDouble " + Integer.toString(lineType), exc));
        }
        return nCounter;
    }

    private static int GetRidgePointsDouble(POINT2[] pLinePoints,
            int lineType,
            int vblSaveCounter) {
        int nCounter = 0;
        try {
            //declarations
            int j = 0, k = 0;
            int lCount = 0;
            double dLengthSegment = 0, dIncrement = 20;
            ref<double[]> m = new ref();
            POINT2[] pSpikePoints = null;
            POINT2 pt0;
            double dSpikeSize = 20;
            int limit = 0;
            double d = 0;
            int bolVertical = 0;
            //end delcarations

            m.value = new double[1];
            lCount = countsupport.GetFORTLCountDouble(pLinePoints, lineType, vblSaveCounter);

            pSpikePoints = new POINT2[lCount];
            lineutility.InitializePOINT2Array(pSpikePoints);
            //for(j=0;j<numPts2-1;j++)
            for (j = 0; j < vblSaveCounter - 1; j++) {
                bolVertical = lineutility.CalcTrueSlopeDouble(pLinePoints[j], pLinePoints[j + 1], m);
                dLengthSegment = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                limit = (int) (dLengthSegment / dIncrement);
                if (limit < 1) {
                    pSpikePoints[nCounter] = new POINT2(pLinePoints[j]);
                    nCounter++;
                    pSpikePoints[nCounter] = new POINT2(pLinePoints[j + 1]);
                    nCounter++;
                    continue;
                }
                for (k = 0; k < limit; k++) {
                    pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -k * dIncrement, 0);
                    nCounter++;
                    d = lineutility.CalcDistanceDouble(pLinePoints[j], pSpikePoints[nCounter - 1]);
                    pt0 = lineutility.ExtendLineDouble(pLinePoints[j + 1], pLinePoints[j], -d - dSpikeSize / 2);

                    //the spikes
                    if (bolVertical != 0) //segment is not vertical
                    {
                        if (pLinePoints[j].x < pLinePoints[j + 1].x) //extend above the line
                        {
                            pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 2, dSpikeSize);
                        } else //extend below the line
                        {
                            pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 3, dSpikeSize);
                        }
                    } else //segment is vertical
                    {
                        if (pLinePoints[j + 1].y < pLinePoints[j].y) //extend left of the line
                        {
                            pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 0, dSpikeSize);
                        } else //extend right of the line
                        {
                            pSpikePoints[nCounter] = lineutility.ExtendDirectedLine(pLinePoints[j], pLinePoints[j + 1], pt0, 1, dSpikeSize);
                        }
                    }
                    nCounter++;
                    pSpikePoints[nCounter] = lineutility.ExtendLine2Double(pLinePoints[j + 1], pLinePoints[j], -d - dSpikeSize, 0);
                    nCounter++;
                }
                pSpikePoints[nCounter] = new POINT2(pLinePoints[j + 1]);
                nCounter++;
                //}
            }

            for (j = 0; j < nCounter; j++) {
                pLinePoints[j] = new POINT2(pSpikePoints[j]);
            }
            for (j = nCounter; j < lCount; j++) {
                pLinePoints[j] = new POINT2(pSpikePoints[nCounter - 1]);
            }

            //clean up
            //segments=null;
            pSpikePoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetRidgePointsDouble",
                    new RendererException("GetRidgePointsDouble " + Integer.toString(lineType), exc));
        }
        return nCounter;
    }

    protected static int GetSquallDouble(POINT2[] pLinePoints,
            int amplitude,
            int quantity,
            int length,
            int numPoints) {
        int counter = 0;
        try {
            int j = 0, k = 0;
            POINT2 StartSegPt, EndSegPt;
            POINT2 savePoint1 = new POINT2(pLinePoints[0]);
            POINT2 savePoint2 = new POINT2(pLinePoints[numPoints - 1]);
            ref<int[]> sign = new ref();
            int segQty = 0;
            int totalQty = countsupport.GetSquallQty(pLinePoints, quantity, length, numPoints);
            POINT2[] pSquallPts = new POINT2[totalQty];
            POINT2[] pSquallSegPts = null;
            //end declarations
            lineutility.InitializePOINT2Array(pSquallPts);
            sign.value = new int[1];
            sign.value[0] = -1;
            if (totalQty == 0) {
                return 0;
            }

            for (j = 0; j < numPoints - 1; j++) {
                //if(segments[j]!=0)
                //{
                StartSegPt = new POINT2(pLinePoints[j]);
                EndSegPt = new POINT2(pLinePoints[j + 1]);
                segQty = countsupport.GetSquallSegQty(StartSegPt, EndSegPt, quantity, length);
                if (segQty > 0) {
                    pSquallSegPts = new POINT2[segQty];
                    lineutility.InitializePOINT2Array(pSquallSegPts);
                } else {
                    pSquallPts[counter].x = StartSegPt.x;
                    pSquallPts[counter++].y = StartSegPt.y;
                    pSquallPts[counter].x = EndSegPt.x;
                    pSquallPts[counter++].y = EndSegPt.y;
                    continue;
                }
                sign.value[0] = -1;
                lineutility.GetSquallSegment(StartSegPt, EndSegPt, pSquallSegPts, sign, amplitude, quantity, length);
                for (k = 0; k < segQty; k++) {
                    pSquallPts[counter].x = pSquallSegPts[k].x;
                    pSquallPts[counter].y = pSquallSegPts[k].y;
                    if (k == 0) {
                        pSquallPts[counter] = new POINT2(pLinePoints[j]);
                    }
                    if (k == segQty - 1) {
                        pSquallPts[counter] = new POINT2(pLinePoints[j + 1]);
                    }
                    pSquallPts[counter].style = 0;
                    counter++;
                }
            }
            //load the squall points into the linepoints array
            for (j = 0; j < counter; j++) {
                if (j < totalQty) {
                    pLinePoints[j].x = pSquallPts[j].x;
                    pLinePoints[j].y = pSquallPts[j].y;
                    if (j == 0) {
                        pLinePoints[j] = new POINT2(savePoint1);
                    }
                    if (j == counter - 1) {
                        pLinePoints[j] = new POINT2(savePoint2);
                    }
                    pLinePoints[j].style = pSquallPts[j].style;
                }
            }
            if (counter == 0) {
                for (j = 0; j < pLinePoints.length; j++) {
                    if (j == 0) {
                        pLinePoints[j] = new POINT2(savePoint1);
                    } else {
                        pLinePoints[j] = new POINT2(savePoint2);
                    }
                }
                counter = pLinePoints.length;
            }
            //clean up
            pSquallPts = null;
            pSquallSegPts = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetSquallDouble",
                    new RendererException("GetSquallDouble", exc));
        }
        return counter;
    }

    protected static int GetSevereSquall(POINT2[] pLinePoints,
            int numPoints) {
        int l = 0;
        try {
            int quantity = 5, length = 30, j = 0, k = 0;
            int totalQty = countsupport.GetSquallQty(pLinePoints, quantity, length, numPoints) + 2 * numPoints;
            POINT2[] squallPts = new POINT2[totalQty];
            POINT2 pt0 = new POINT2(), pt1 = new POINT2(), pt2 = new POINT2(),
                    pt3 = new POINT2(), pt4 = new POINT2(), pt5 = new POINT2(), pt6 = new POINT2(),
                    pt7 = new POINT2(), pt8 = new POINT2();
            int segQty = 0;
            double dist = 0;
            //end declarations

            lineutility.InitializePOINT2Array(squallPts);
            //each segment looks like this: --- V
            for (j = 0; j < numPoints - 1; j++) {
                dist = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                segQty = (int) (dist / 30);
                for (k = 0; k < segQty; k++) {
                    pt0 = lineutility.ExtendAlongLineDouble(pLinePoints[j], pLinePoints[j + 1], k * 30);
                    pt1 = lineutility.ExtendAlongLineDouble(pLinePoints[j], pLinePoints[j + 1], k * 30 + 20);
                    //pt0.style = 5;
                    pt5 = lineutility.ExtendAlongLineDouble(pLinePoints[j], pLinePoints[j + 1], k * 30 + 25);
                    pt6 = lineutility.ExtendAlongLineDouble(pLinePoints[j], pLinePoints[j + 1], k * 30 + 30);
                    //pt6.style=5;
                    pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 2, 5, 0);   //extend above line
                    pt3 = lineutility.ExtendDirectedLine(pt0, pt5, pt5, 3, 5, 0);   //extend below line
                    pt4 = lineutility.ExtendDirectedLine(pt0, pt6, pt6, 2, 5, 5);   //extend above line
                    pt4.style = 5;
                    squallPts[l++] = new POINT2(pt2);
                    squallPts[l++] = new POINT2(pt3);
                    squallPts[l++] = new POINT2(pt4);
                    pt7 = lineutility.ExtendAlongLineDouble(pLinePoints[j], pLinePoints[j + 1], k * 30 + 5);
                    pt8 = lineutility.ExtendAlongLineDouble(pLinePoints[j], pLinePoints[j + 1], k * 30 + 10);
                    pt8.style = 5;
                    squallPts[l++] = new POINT2(pt7);
                    squallPts[l++] = new POINT2(pt8);
                }
                //segment remainder
                squallPts[l++] = new POINT2(pLinePoints[j + 1]);
                pt0 = lineutility.ExtendAlongLineDouble(pLinePoints[j + 1], pLinePoints[j], 5);
                pt0.style = 5;
                squallPts[l++] = new POINT2(pt0);
            }
            if (l > pLinePoints.length) {
                l = pLinePoints.length;
            }

            for (j = 0; j < l; j++) {
                if (j < totalQty) {
                    pLinePoints[j] = new POINT2(squallPts[j]);
                } else {
                    break;
                }
            }

        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetSevereSquall",
                    new RendererException("GetSevereSquall", exc));
        }
        return l;
    }

    private static int GetConvergancePointsDouble(POINT2[] pLinePoints, int vblCounter) {
        int counter = vblCounter;
        try {
            int j = 0, k = 0;
            //int counter=vblCounter;
            double d = 0;
            POINT2 pt0 = new POINT2(), pt1 = new POINT2();
            POINT2[] tempPts = new POINT2[vblCounter];
            POINT2 tempPt = new POINT2();
            int numJags = 0;
            //save the original points
            for (j = 0; j < vblCounter; j++) {
                tempPts[j] = new POINT2(pLinePoints[j]);
            }

            //result points begin with the original points,
            //set the last one's linestyle to 5;
            pLinePoints[vblCounter - 1].style = 5;
            for (j = 0; j < vblCounter - 1; j++) {

                pt0 = new POINT2(tempPts[j]);
                pt1 = new POINT2(tempPts[j + 1]);
                d = lineutility.CalcDistanceDouble(pt0, pt1);
                numJags = (int) (d / 10);
                //we don't want too small a remainder
                if (d - numJags * 10 < 5) {
                    numJags -= 1;
                }

                //each 10 pixel section has two spikes: one points above the line
                //the other spike points below the line
                for (k = 0; k < numJags; k++) {
                    //the first spike
                    tempPt = lineutility.ExtendAlongLineDouble(pt0, pt1, k * 10 + 5, 0);
                    pLinePoints[counter++] = new POINT2(tempPt);
                    tempPt = lineutility.ExtendAlongLineDouble(tempPt, pt1, 5);
                    tempPt = lineutility.ExtendDirectedLine(pt0, tempPt, tempPt, 2, 5, 5);
                    pLinePoints[counter++] = new POINT2(tempPt);
                    //the 2nd spike
                    tempPt = lineutility.ExtendAlongLineDouble(pt0, pt1, (k + 1) * 10, 0);
                    pLinePoints[counter++] = new POINT2(tempPt);
                    tempPt = lineutility.ExtendAlongLineDouble(tempPt, pt1, 5);
                    tempPt = lineutility.ExtendDirectedLine(pt0, tempPt, tempPt, 3, 5, 5);
                    pLinePoints[counter++] = new POINT2(tempPt);
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetConvergancePointsDouble",
                    new RendererException("GetConvergancePointsDouble", exc));
        }
        return counter;
    }

    private static int GetITDPointsDouble(POINT2[] pLinePoints, int vblCounter) {
        int counter = 0;
        try {
            int j = 0, k = 0;
            double d = 0;
            POINT2 pt0 = new POINT2(), pt1 = new POINT2();
            POINT2[] tempPts = new POINT2[vblCounter];
            POINT2 tempPt = new POINT2();
            int numJags = 0, lineStyle = 19;
            //save the original points
            for (j = 0; j < vblCounter; j++) {
                tempPts[j] = new POINT2(pLinePoints[j]);
            }

            //result points begin with the original points,
            //set the last one's linestyle to 5;
            //pLinePoints[vblCounter-1].style=5;
            for (j = 0; j < vblCounter - 1; j++) {
                pt0 = new POINT2(tempPts[j]);
                pt1 = new POINT2(tempPts[j + 1]);
                d = lineutility.CalcDistanceDouble(pt0, pt1);
                numJags = (int) (d / 15);
                //we don't want too small a remainder
                if (d - numJags * 10 < 5) {
                    numJags -= 1;
                }
                if (numJags == 0) {
                    pt0.style = 19;
                    pLinePoints[counter++] = new POINT2(pt0);
                    pt1.style = 5;
                    pLinePoints[counter++] = new POINT2(pt1);
                }
                //each 10 pixel section has two spikes: one points above the line
                //the other spike points below the line
                for (k = 0; k < numJags; k++) {
                    tempPt = lineutility.ExtendAlongLineDouble(pt0, pt1, k * 15 + 5, lineStyle);
                    pLinePoints[counter++] = new POINT2(tempPt);

                    if (k < numJags - 1) {
                        tempPt = lineutility.ExtendAlongLineDouble(tempPt, pt1, 10, 5);
                    } else {
                        tempPt = new POINT2(tempPts[j + 1]);
                        tempPt.style = 5;
                    }
                    pLinePoints[counter++] = new POINT2(tempPt);
                    if (lineStyle == 19) {
                        lineStyle = 25;
                    } else {
                        lineStyle = 19;
                    }
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetITDPointsDouble",
                    new RendererException("GetITDPointsDouble", exc));
        }
        return counter;
    }

    private static int GetXPoints(int linetype, POINT2[] pOriginalLinePoints, POINT2[] XPoints, int vblCounter) {
        int xCounter = 0;
        try {
            int j = 0, k = 0;
            double d = 0;
            POINT2 pt0, pt1, pt2, pt3 = new POINT2(), pt4 = new POINT2(), pt5 = new POINT2(), pt6 = new POINT2();
            int numThisSegment = 0;
            double distInterval = 0;
            for (j = 0; j < vblCounter - 1; j++) {
                d = lineutility.CalcDistanceDouble(pOriginalLinePoints[j], pOriginalLinePoints[j + 1]);
                numThisSegment = (int) ((d - 20d) / 20d);
                if (linetype == TacticalLines.LRO) {
                    numThisSegment = (int) ((d - 30d) / 30d);
                }
                //added 4-19-12
                distInterval = d / numThisSegment;
                for (k = 0; k < numThisSegment; k++) {
                    //pt0=lineutility.ExtendAlongLineDouble(pOriginalLinePoints[j],pOriginalLinePoints[j+1], 10+20*k);
                    pt0 = lineutility.ExtendAlongLineDouble2(pOriginalLinePoints[j], pOriginalLinePoints[j + 1], distInterval / 2 + distInterval * k);
                    pt1 = lineutility.ExtendAlongLineDouble2(pt0, pOriginalLinePoints[j + 1], 5);
                    pt2 = lineutility.ExtendAlongLineDouble2(pt0, pOriginalLinePoints[j + 1], -5);

                    pt3 = lineutility.ExtendDirectedLine(pOriginalLinePoints[j], pt1, pt1, 2, 5);
                    pt4 = lineutility.ExtendDirectedLine(pOriginalLinePoints[j], pt1, pt1, 3, 5);
                    pt4.style = 5;
                    pt5 = lineutility.ExtendDirectedLine(pOriginalLinePoints[j], pt2, pt2, 2, 5);
                    pt6 = lineutility.ExtendDirectedLine(pOriginalLinePoints[j], pt2, pt2, 3, 5);
                    pt6.style = 5;
                    XPoints[xCounter++] = new POINT2(pt3);
                    XPoints[xCounter++] = new POINT2(pt6);
                    XPoints[xCounter++] = new POINT2(pt5);
                    XPoints[xCounter++] = new POINT2(pt4);
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetXPointsDouble",
                    new RendererException("GetXPointsDouble", exc));
        }
        return xCounter;
    }

    /**
     * returns a 37 point ellipse
     *
     * @param ptCenter
     * @param ptWidth
     * @param ptHeight
     * @return
     */
    private static POINT2[] getEllipsePoints(POINT2 ptCenter, POINT2 ptWidth, POINT2 ptHeight) {
        POINT2[] pEllipsePoints = null;
        try {
            pEllipsePoints = new POINT2[37];
            int l = 0;
            double dFactor = 0;
            double a = lineutility.CalcDistanceDouble(ptCenter, ptWidth);
            double b = lineutility.CalcDistanceDouble(ptCenter, ptHeight);
            lineutility.InitializePOINT2Array(pEllipsePoints);
            for (l = 1; l < 37; l++) {
                //dFactor = (20.0 * l) * Math.PI / 180.0;
                dFactor = (10.0 * l) * Math.PI / 180.0;
                pEllipsePoints[l - 1].x = ptCenter.x + (int) (a * Math.cos(dFactor));
                pEllipsePoints[l - 1].y = ptCenter.y + (int) (b * Math.sin(dFactor));
                pEllipsePoints[l - 1].style = 0;
            }
            pEllipsePoints[36] = new POINT2(pEllipsePoints[0]);
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetEllipsePoints",
                    new RendererException("GetEllipsePoints", exc));
        }
        return pEllipsePoints;
    }

    /**
     * Calculate an ellipse and rotate about it's center by azimuth in degrees
     *
     * @param ptCenter
     * @param ptWidth
     * @param ptHeight
     * @param azimuth
     * @return
     */
    private static POINT2[] getRotatedEllipsePoints(POINT2 ptCenter, POINT2 ptWidth, POINT2 ptHeight, double azimuth, int lineType) {
        POINT2[] pResultPoints = null;
        try {
            POINT2[] pEllipsePoints = new POINT2[36];
            int l = 0, j = 0;
            double dFactor = 0;
            double a = lineutility.CalcDistanceDouble(ptCenter, ptWidth);
            double b = lineutility.CalcDistanceDouble(ptCenter, ptHeight);
//            if(lineType==TacticalLines.PBS_CIRCLE)
//                b=a;
            lineutility.InitializePOINT2Array(pEllipsePoints);
            for (l = 1; l < 37; l++) {
                dFactor = (10.0 * l) * Math.PI / 180.0;
                //pEllipsePoints[l - 1].x = ptCenter.x + (int) (a * Math.cos(dFactor));
                //pEllipsePoints[l - 1].y = ptCenter.y + (int) (b * Math.sin(dFactor));
                pEllipsePoints[l - 1].x = ptCenter.x + a * Math.cos(dFactor);
                pEllipsePoints[l - 1].y = ptCenter.y + b * Math.sin(dFactor);
                pEllipsePoints[l - 1].style = 0;
            }
            if (lineType != TacticalLines.PBS_CIRCLE) {
                lineutility.RotateGeometryDouble(pEllipsePoints, 36, azimuth - 90);
            }
            pResultPoints = new POINT2[37];
            for (j = 0; j < 36; j++) {
                pResultPoints[j] = pEllipsePoints[j];
            }
            pResultPoints[36] = pEllipsePoints[0];
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetRotatedEllipsePoints",
                    new RendererException("GetRotateEllipsePoints", exc));
        }
        return pResultPoints;
    }

    private static int GetLVOPoints(int linetype, POINT2[] pOriginalLinePoints, POINT2[] pLinePoints, int vblCounter) {
        int lEllipseCounter = 0;
        try {
            //double dAngle = 0, d = 0, a = 13, b = 6, dFactor = 0;
            double dAngle = 0, d = 0, a = 4, b = 8, dFactor = 0;
            int lHowManyThisSegment = 0, j = 0, k = 0, l = 0, t = 0;
            POINT2 ptCenter = new POINT2();
            POINT2[] pEllipsePoints2 = new POINT2[37];

            double distInterval = 0;
            //end declarations
            for (j = 0; j < vblCounter - 1; j++) {
                lineutility.InitializePOINT2Array(pEllipsePoints2);
                d = lineutility.CalcDistanceDouble(pOriginalLinePoints[j], pOriginalLinePoints[j + 1]);
                //lHowManyThisSegment = (int) ((d - 10) / 10);                
                lHowManyThisSegment = (int) ((d - 20) / 20);
                if (linetype == TacticalLines.LRO) {
                    lHowManyThisSegment = (int) ((d - 30) / 30);
                }
                //added 4-19-12
                distInterval = d / lHowManyThisSegment;

                dAngle = lineutility.CalcSegmentAngleDouble(pOriginalLinePoints[j], pOriginalLinePoints[j + 1]);
                dAngle = dAngle + Math.PI / 2;
                for (k = 0; k < lHowManyThisSegment; k++) {
                    //t = k;
                    //ptCenter=lineutility.ExtendAlongLineDouble(pOriginalLinePoints[j], pOriginalLinePoints[j+1], k*20+20);
                    ptCenter = lineutility.ExtendAlongLineDouble2(pOriginalLinePoints[j], pOriginalLinePoints[j + 1], k * distInterval);
                    for (l = 1; l < 37; l++) {
                        //dFactor = (10.0 * l) * Math.PI / 180.0;
                        dFactor = (20.0 * l) * Math.PI / 180.0;
                        pEllipsePoints2[l - 1].x = ptCenter.x + (int) (a * Math.cos(dFactor));
                        pEllipsePoints2[l - 1].y = ptCenter.y + (int) (b * Math.sin(dFactor));
                        pEllipsePoints2[l - 1].style = 0;
                    }
                    lineutility.RotateGeometryDouble(pEllipsePoints2, 36, (int) (dAngle * 180 / Math.PI));
                    pEllipsePoints2[36] = new POINT2(pEllipsePoints2[35]);
                    pEllipsePoints2[36].style = 5;
                    for (l = 0; l < 37; l++) {
                        pLinePoints[lEllipseCounter] = new POINT2(pEllipsePoints2[l]);
                        lEllipseCounter++;
                    }
                }//end k loop
                //extra ellipse on the final segment at the end of the line
                if (j == vblCounter - 2) {
                    ptCenter = pOriginalLinePoints[j + 1];

                    for (l = 1; l < 37; l++) {
                        //dFactor = (10.0 * l) * Math.PI / 180.0;
                        dFactor = (20.0 * l) * Math.PI / 180.0;
                        pEllipsePoints2[l - 1].x = ptCenter.x + (int) (a * Math.cos(dFactor));
                        pEllipsePoints2[l - 1].y = ptCenter.y + (int) (b * Math.sin(dFactor));
                        pEllipsePoints2[l - 1].style = 0;
                    }
                    lineutility.RotateGeometryDouble(pEllipsePoints2, 36, (int) (dAngle * 180 / Math.PI));
                    pEllipsePoints2[36] = new POINT2(pEllipsePoints2[35]);
                    pEllipsePoints2[36].style = 5;
                    for (l = 0; l < 37; l++) {
                        pLinePoints[lEllipseCounter] = new POINT2(pEllipsePoints2[l]);
                        lEllipseCounter++;
                    }
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetLVOPointsDouble",
                    new RendererException("GetLVOPointsDouble", exc));
        }
        return lEllipseCounter;
    }

    private static int GetIcingPointsDouble(POINT2[] pLinePoints, int vblCounter) {
        int counter = 0;
        try {
            int j = 0;
            POINT2[] origPoints = new POINT2[vblCounter];
            int nDirection = -1;
            int k = 0, numSegments = 0;
            POINT2 pt0 = new POINT2(), pt1 = new POINT2(), midPt = new POINT2(), pt2 = new POINT2();
            //save the original points
            for (j = 0; j < vblCounter; j++) {
                origPoints[j] = new POINT2(pLinePoints[j]);
            }
            double distInterval = 0;
            for (j = 0; j < vblCounter - 1; j++) {
                //how many segments for this line segment?
                numSegments = (int) lineutility.CalcDistanceDouble(origPoints[j], origPoints[j + 1]);
                numSegments /= 15;	//segments are 15 pixels long
                //4-19-12
                distInterval = lineutility.CalcDistanceDouble(origPoints[j], origPoints[j + 1]) / numSegments;
                //get the direction and the quadrant
                nDirection = GetInsideOutsideDouble2(origPoints[j], origPoints[j + 1], origPoints, vblCounter, j, TacticalLines.ICING);
                for (k = 0; k < numSegments; k++) {
                    //get the parallel segment
                    if (k == 0) {
                        pt0 = new POINT2(origPoints[j]);
                    } else {
                        //pt0 = lineutility.ExtendAlongLineDouble(origPoints[j], origPoints[j + 1], k * 15, 0);
                        pt0 = lineutility.ExtendAlongLineDouble(origPoints[j], origPoints[j + 1], k * distInterval, 0);
                    }

                    //pt1 = lineutility.ExtendAlongLineDouble(origPoints[j], origPoints[j + 1], k * 15 + 10, 5);
                    pt1 = lineutility.ExtendAlongLineDouble(origPoints[j], origPoints[j + 1], k * distInterval + 10, 5);
                    //midPt = lineutility.ExtendAlongLineDouble(origPoints[j], origPoints[j + 1], k * 15 + 5, 0);
                    midPt = lineutility.ExtendAlongLineDouble(origPoints[j], origPoints[j + 1], k * distInterval + 5, 0);
                    //get the perpendicular segment
                    pt2 = lineutility.ExtendDirectedLine(origPoints[j], origPoints[j + 1], midPt, nDirection, 5, 5);
                    pLinePoints[counter] = new POINT2(pt0);
                    pLinePoints[counter + 1] = new POINT2(pt1);
                    pLinePoints[counter + 2] = new POINT2(midPt);
                    pLinePoints[counter + 3] = new POINT2(pt2);
                    counter += 4;
                }
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetIcingPointsDouble",
                    new RendererException("GetIcingPointsDouble", exc));
        }
        return counter;
    }

    protected static int GetAnchorageDouble(POINT2[] vbPoints2, int numPts) {
        int lFlotCounter = 0;
        try {
            //declarations
            int j = 0, k = 0, l = 0;
            int x1 = 0, y1 = 0;
            int numSegPts = -1;
            int lFlotCount = 0;
            int lNumSegs = 0;
            double dDistance = 0;
            int[] vbPoints = null;
            int[] points = null;
            int[] points2 = null;
            POINT2 pt = new POINT2();
            POINT2 pt1 = new POINT2(), pt2 = new POINT2();
            //end declarations

            lFlotCount = flot.GetAnchorageCountDouble(vbPoints2, numPts);
            vbPoints = new int[2 * numPts];

            for (j = 0; j < numPts; j++) {
                vbPoints[k] = (int) vbPoints2[j].x;
                k++;
                vbPoints[k] = (int) vbPoints2[j].y;
                k++;
            }
            k = 0;

            ref<int[]> bFlip = new ref();
            bFlip.value = new int[1];
            ref<int[]> lDirection = new ref();
            lDirection.value = new int[1];
            ref<int[]> lLastDirection = new ref();
            lLastDirection.value = new int[1];
            for (l = 0; l < numPts - 1; l++) {
                //pt1=new POINT2();
                //pt2=new POINT2();
                pt1.x = vbPoints[2 * l];
                pt1.y = vbPoints[2 * l + 1];
                pt2.x = vbPoints[2 * l + 2];
                pt2.y = vbPoints[2 * l + 3];
                //for all segments after the first segment we shorten
                //the line by 20 so the flots will not abut
                if (l > 0) {
                    pt1 = lineutility.ExtendAlongLineDouble(pt1, pt2, 20);
                }

                dDistance = lineutility.CalcDistanceDouble(pt1, pt2);

                lNumSegs = (int) (dDistance / 20);

                //ref<int[]> bFlip = new ref();
                //bFlip.value = new int[1];
                //ref<int[]> lDirection = new ref();
                //lDirection.value = new int[1];
                //ref<int[]> lLastDirection = new ref();
                //lLastDirection.value = new int[1];
                if (lNumSegs > 0) {
                    points2 = new int[lNumSegs * 32];
                    numSegPts = flot.GetAnchorageFlotSegment(vbPoints, (int) pt1.x, (int) pt1.y, (int) pt2.x, (int) pt2.y, l, points2, bFlip, lDirection, lLastDirection);
                    points = new int[numSegPts];

                    for (j = 0; j < numSegPts; j++) {
                        points[j] = points2[j];
                    }

                    for (j = 0; j < numSegPts / 3; j++) //only using half the flots
                    {
                        x1 = points[k];
                        y1 = points[k + 1];
                        //z = points[k + 2];
                        k += 3;
                        if (j % 10 == 0) {
                            pt.x = x1;
                            pt.y = y1;
                            pt.style = 5;
                        } else if ((j + 1) % 10 == 0) {
                            if (lFlotCounter < lFlotCount) {
                                vbPoints2[lFlotCounter].x = x1;
                                vbPoints2[lFlotCounter++].y = y1;
                                vbPoints2[lFlotCounter++] = new POINT2(pt);
                                continue;
                            } else {
                                break;
                            }
                        }
                        if (lFlotCounter < lFlotCount) {
                            vbPoints2[lFlotCounter].x = x1;
                            vbPoints2[lFlotCounter].y = y1;
                            lFlotCounter++;
                        } else {
                            break;
                        }
                    }
                    k = 0;
                    points = null;
                } else {
                    if (lFlotCounter < lFlotCount) {
                        vbPoints2[lFlotCounter].x = vbPoints[2 * l];
                        vbPoints2[lFlotCounter].y = vbPoints[2 * l + 1];
                        lFlotCounter++;
                    }
                }
            }
            for (j = lFlotCounter - 1; j < lFlotCount; j++) {
                vbPoints2[j].style = 5;
            }

            //clean up
            vbPoints = null;
            points = null;
            points2 = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetAnchorageDouble",
                    new RendererException("GetAnchorageDouble", exc));
        }
        return lFlotCounter;
    }

    private static int GetPipePoints(POINT2[] pLinePoints,
            int vblCounter) {
        int counter = 0;
        try {
            POINT2[] pOriginalPoints = new POINT2[vblCounter];
            POINT2 pt0 = new POINT2();
            POINT2 pt1 = new POINT2();
            POINT2 pt2 = new POINT2();
            POINT2[] xPoints = new POINT2[pLinePoints.length];
            int xCounter = 0;
            int j = 0, k = 0;
            for (j = 0; j < vblCounter; j++) {
                pOriginalPoints[j] = new POINT2(pLinePoints[j]);
            }
            int numSegs = 0;
            double d = 0;

            lineutility.InitializePOINT2Array(xPoints);
            for (j = 0; j < vblCounter - 1; j++) {
                d = lineutility.CalcDistanceDouble(pOriginalPoints[j], pOriginalPoints[j + 1]);
                numSegs = (int) (d / 20);
                for (k = 0; k < numSegs; k++) {
                    pt0 = lineutility.ExtendAlongLineDouble2(pOriginalPoints[j], pOriginalPoints[j + 1], 20 * k);
                    pt0.style = 0;
                    pt1 = lineutility.ExtendAlongLineDouble2(pOriginalPoints[j], pOriginalPoints[j + 1], 20 * k + 10);
                    pt1.style = 5;
                    pt2 = lineutility.ExtendAlongLineDouble2(pOriginalPoints[j], pOriginalPoints[j + 1], 20 * k + 10);
                    pt2.style = 20;	//for filled circle
                    pLinePoints[counter++] = new POINT2(pt0);
                    pLinePoints[counter++] = new POINT2(pt1);
                    xPoints[xCounter++] = new POINT2(pt2);
                }
                if (numSegs == 0) {
                    pLinePoints[counter] = new POINT2(pOriginalPoints[j]);
                    pLinePoints[counter++].style = 0;
                    pLinePoints[counter] = new POINT2(pOriginalPoints[j + 1]);
                    pLinePoints[counter++].style = 5;
                } else {
                    pLinePoints[counter] = new POINT2(pLinePoints[counter - 1]);
                    pLinePoints[counter++].style = 0;
                    pLinePoints[counter] = new POINT2(pOriginalPoints[j + 1]);
                    pLinePoints[counter++].style = 5;
                }
            }
            //load the circle points
            for (k = 0; k < xCounter; k++) {
                pLinePoints[counter++] = new POINT2(xPoints[k]);
            }
            //add one more circle
            pLinePoints[counter++] = new POINT2(pLinePoints[counter]);

            pOriginalPoints = null;
            xPoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetPipePoints",
                    new RendererException("GetPipePoints", exc));
        }
        return counter;
    }

    private static int GetReefPoints(POINT2[] pLinePoints,
            int vblCounter) {
        int counter = 0;
        try {
            POINT2[] pOriginalPoints = new POINT2[vblCounter];
            POINT2 pt0 = new POINT2();
            POINT2 pt1 = new POINT2();
            POINT2 pt2 = new POINT2();
            POINT2 pt3 = new POINT2();
            POINT2 pt4 = new POINT2();
            //POINT2 pt5=new POINT2();
            for (int j = 0; j < vblCounter; j++) {
                pOriginalPoints[j] = new POINT2(pLinePoints[j]);
            }

            int numSegs = 0, direction = 0;
            double d = 0;
            for (int j = 0; j < vblCounter - 1; j++) {
                if (pOriginalPoints[j].x < pOriginalPoints[j + 1].x) {
                    direction = 2;
                } else {
                    direction = 3;
                }

                d = lineutility.CalcDistanceDouble(pOriginalPoints[j], pOriginalPoints[j + 1]);
                numSegs = (int) (d / 40);
                for (int k = 0; k < numSegs; k++) {
                    pt0 = lineutility.ExtendAlongLineDouble2(pOriginalPoints[j], pOriginalPoints[j + 1], 40 * k);

                    pt1 = lineutility.ExtendAlongLineDouble2(pt0, pOriginalPoints[j + 1], 10);
                    pt1 = lineutility.ExtendDirectedLine(pOriginalPoints[j], pOriginalPoints[j + 1], pt1, direction, 15);//was 2

                    pt2 = lineutility.ExtendAlongLineDouble2(pt0, pOriginalPoints[j + 1], 20);
                    pt2 = lineutility.ExtendDirectedLine(pOriginalPoints[j], pOriginalPoints[j + 1], pt2, direction, 5);//was 2

                    pt3 = lineutility.ExtendAlongLineDouble2(pt0, pOriginalPoints[j + 1], 30);
                    pt3 = lineutility.ExtendDirectedLine(pOriginalPoints[j], pOriginalPoints[j + 1], pt3, direction, 20);//was 2

                    pt4 = lineutility.ExtendAlongLineDouble2(pOriginalPoints[j], pOriginalPoints[j + 1], 40 * (k + 1));
                    pLinePoints[counter++] = new POINT2(pt0);
                    pLinePoints[counter++] = new POINT2(pt1);
                    pLinePoints[counter++] = new POINT2(pt2);
                    pLinePoints[counter++] = new POINT2(pt3);
                    pLinePoints[counter++] = new POINT2(pt4);
                }
                if (numSegs == 0) {
                    pLinePoints[counter++] = new POINT2(pOriginalPoints[j]);
                    pLinePoints[counter++] = new POINT2(pOriginalPoints[j + 1]);
                }
            }
            pLinePoints[counter++] = new POINT2(pOriginalPoints[vblCounter - 1]);
            pOriginalPoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetReefPoints",
                    new RendererException("GetReefPoints", exc));
        }
        return counter;
    }

    private static int GetRestrictedAreaPoints(POINT2[] pLinePoints,
            int vblCounter) {
        int counter = 0;
        try {
            POINT2[] pOriginalPoints = new POINT2[vblCounter];
            POINT2 pt0 = new POINT2();
            POINT2 pt1 = new POINT2();
            POINT2 pt2 = new POINT2();
            POINT2 pt3 = new POINT2();
            for (int j = 0; j < vblCounter; j++) {
                pOriginalPoints[j] = new POINT2(pLinePoints[j]);
            }
            int direction = 0;
            int numSegs = 0;
            double d = 0;
            for (int j = 0; j < vblCounter - 1; j++) {
                d = lineutility.CalcDistanceDouble(pOriginalPoints[j], pOriginalPoints[j + 1]);
                numSegs = (int) (d / 15);
                if (pOriginalPoints[j].x < pOriginalPoints[j + 1].x) {
                    direction = 3;
                } else {
                    direction = 2;
                }
                for (int k = 0; k < numSegs; k++) {
                    pt0 = lineutility.ExtendAlongLineDouble2(pOriginalPoints[j], pOriginalPoints[j + 1], 15 * k);
                    pt0.style = 0;
                    pt1 = lineutility.ExtendAlongLineDouble2(pOriginalPoints[j], pOriginalPoints[j + 1], 15 * k + 10);
                    pt1.style = 5;
                    pt2 = lineutility.MidPointDouble(pt0, pt1, 0);
                    //pt3 = lineutility.ExtendDirectedLine(pOriginalPoints[j], pOriginalPoints[j + 1], pt2, 3, 10);
                    pt3 = lineutility.ExtendDirectedLine(pOriginalPoints[j], pOriginalPoints[j + 1], pt2, direction, 10);
                    pt3.style = 5;
                    pLinePoints[counter++] = new POINT2(pt2);
                    pLinePoints[counter++] = new POINT2(pt3);
                    pLinePoints[counter++] = new POINT2(pt0);
                    pLinePoints[counter++] = new POINT2(pt1);
                }
                if (numSegs == 0) {
                    pLinePoints[counter++] = new POINT2(pOriginalPoints[j]);
                    pLinePoints[counter++] = new POINT2(pOriginalPoints[j + 1]);
                }
            }
            pLinePoints[counter - 1].style = 0;
            pLinePoints[counter++] = new POINT2(pOriginalPoints[vblCounter - 1]);
            pOriginalPoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetRestrictedAreaPoints",
                    new RendererException("GetRestrictedAreaPoints", exc));
        }
        return counter;
    }

    //there should be two linetypes depending on scale
    private static int getOverheadWire(POINT2[] pLinePoints, int vblCounter) {
        int counter = 0;
        try {
            int j = 0;
            POINT2 pt = null, pt2 = null;
            double x = 0, y = 0;
            ArrayList<POINT2> pts = new ArrayList();
            for (j = 0; j < vblCounter; j++) {
                pt = new POINT2(pLinePoints[j]);
                x = pt.x;
                y = pt.y;
                //tower
                pt2 = new POINT2(pt);
                pt2.y -= 5;
                pts.add(pt2);
                pt2 = new POINT2(pt);
                pt2.x -= 5;
                pts.add(pt2);
                pt2 = new POINT2(pt);
                pt2.y -= 20;
                pts.add(pt2);
                pt2 = new POINT2(pt);
                pt2.x += 5;
                pts.add(pt2);
                pt2 = new POINT2(pt);
                pt2.y -= 5;
                pt2.style = 5;
                pts.add(pt2);
                //low cross piece
                pt2 = new POINT2(pt);
                pt2.x -= 2;
                pt2.y -= 10;
                pts.add(pt2);
                pt2 = new POINT2(pt);
                pt2.x += 2;
                pt2.y -= 10;
                pt2.style = 5;
                pts.add(pt2);
                //high cross piece
                pt2 = new POINT2(pt);
                pt2.x -= 7;
                pt2.y -= 17;
                pts.add(pt2);
                pt2 = new POINT2(pt);
                pt2.x -= 5;
                pt2.y -= 20;
                pts.add(pt2);
                pt2 = new POINT2(pt);
                pt2.x += 5;
                pt2.y -= 20;
                pts.add(pt2);
                pt2 = new POINT2(pt);
                pt2.x += 7;
                pt2.y -= 17;
                pt2.style = 5;
                pts.add(pt2);
                //angle piece
                pt2 = new POINT2(pt);
                pt2.y -= 20;
                pts.add(pt2);
                pt2 = new POINT2(pt);
                pt2.x += 8;
                pt2.y -= 12;
                pt2.style = 5;
                pts.add(pt2);
            }
            //connect the towers
            for (j = 0; j < vblCounter - 1; j++) {
                pt = new POINT2(pLinePoints[j]);
                pt2 = new POINT2(pLinePoints[j + 1]);
                if (pt.x < pt2.x) {
                    pt.x += 5;
                    pt.y -= 10;
                    pt2.x -= 5;
                    pt2.y -= 10;
                    pt2.style = 5;
                } else {
                    pt.x -= 5;
                    pt.y -= 10;
                    pt2.x += 5;
                    pt2.y -= 10;
                    pt2.style = 5;
                }
                pts.add(pt);
                pts.add(pt2);
            }
            for (j = 0; j < pts.size(); j++) {
                pLinePoints[j] = pts.get(j);
                counter++;
            }
            for (j = counter; j < pLinePoints.length; j++) {
                pLinePoints[j] = new POINT2(pLinePoints[counter - 1]);
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetOverheadWire",
                    new RendererException("GetOverheadWire", exc));
        }
        return counter;
    }
    //private static int linetype=-1; //use for BLOCK, CONTIAN
    /**
     * Calculates the points for the non-channel symbols. The points will be
     * stored in the original POINT2 array in pixels, pLinePoints. The client
     * points occupy the first vblSaveCounter positions in pLinePoints and will
     * be overwritten by the symbol points.
     *
     * @param lineType the line type
     * @param pLinePoints - OUT - an array of POINT2
     * @param vblCounter the number of points allocated
     * @param vblSaveCounter the number of client points
     *
     * @return the symbol point count
     */
    private static ArrayList<POINT2> GetLineArray2Double(int lineType,
            POINT2[] pLinePoints,
            int vblCounter,
            int vblSaveCounter,
            ArrayList<Shape2> shapes,
            Rectangle2D clipBounds,
            int rev,
            IPointConversion converter) {
        ArrayList<POINT2> points = new ArrayList();
        try {
            String client = CELineArray.getClient();
            if (pLinePoints == null || pLinePoints.length < 2) {
                return null;
            }
            //declarations
            int[] segments = null;
            double dMRR = 0;
            int n = 0, bolVertical = 0;
            double dExtendLength = 0;
            double dWidth = 0;
            int nQuadrant = 0;
            int lLinestyle = 0, pointCounter = 0;
            ref<double[]> offsetX = new ref(), offsetY = new ref();
            double b = 0, b1 = 0, dRadius = 0, d1 = 0, d = 0, d2 = 0;
            ref<double[]> m = new ref();
            int direction = 0;
            int nCounter = 0;
            int j = 0, k = 0, middleSegment = -1;
            double dMBR = lineutility.MBRDistance(pLinePoints, vblSaveCounter);
            POINT2 pt0 = new POINT2(pLinePoints[0]), //calculation points for autoshapes
                    pt1 = new POINT2(pLinePoints[1]),
                    pt2 = new POINT2(pLinePoints[1]),
                    pt3 = new POINT2(pLinePoints[0]),
                    pt4 = new POINT2(pLinePoints[0]),
                    pt5 = new POINT2(pLinePoints[0]),
                    pt6 = new POINT2(pLinePoints[0]),
                    pt7 = new POINT2(pLinePoints[0]),
                    pt8 = new POINT2(pLinePoints[0]),
                    ptYIntercept = new POINT2(pLinePoints[0]),
                    ptYIntercept1 = new POINT2(pLinePoints[0]),
                    ptCenter = new POINT2(pLinePoints[0]);
            POINT2[] pArrowPoints = new POINT2[3],
                    arcPts = new POINT2[26],
                    circlePoints = new POINT2[100],
                    pts = null, pts2 = null;
            POINT2 midpt = new POINT2(pLinePoints[0]), midpt1 = new POINT2(pLinePoints[0]);

            POINT2[] pOriginalLinePoints = null;
            POINT2[] pUpperLinePoints = null;
            POINT2[] pLowerLinePoints = null;
            POINT2[] pUpperLowerLinePoints = null;

            POINT2 calcPoint0 = new POINT2(),
                    calcPoint1 = new POINT2(),
                    calcPoint2 = new POINT2(),
                    calcPoint3 = new POINT2(),
                    calcPoint4 = new POINT2();
            POINT2 ptTemp = new POINT2(pLinePoints[0]);
            int acCounter = 0;
            POINT2[] acPoints = new POINT2[6];
            int lFlotCount = 0;
            //end declarations

            //Bearing line and others only have 2 points
            if (vblCounter > 2) {
                pt2 = new POINT2(pLinePoints[2]);
            }
            //strcpy(CurrentFunction,"GetLineArray2");
            pt0.style = 0;
            pt1.style = 0;
            pt2.style = 0;

            //set jaggylength in clsDISMSupport before the points get bounded
            //clsDISMSupport.JaggyLength = Math.Sqrt ( (pLinePoints[1].x-pLinePoints[0].x) * (pLinePoints[1].x-pLinePoints[0].x) +
            //	(pLinePoints[1].y-pLinePoints[0].y) * (pLinePoints[1].y-pLinePoints[0].y) );
            //double saveMaxPixels=CELineArrayGlobals.MaxPixels2;
            //double saveMaxPixels=2000;
            ArrayList xPoints = null;
            pOriginalLinePoints = new POINT2[vblSaveCounter];
            for (j = 0; j < vblSaveCounter; j++) {
                pOriginalLinePoints[j] = new POINT2(pLinePoints[j]);
            }

            //resize the array and get the line array
            //for the specified non-channel line type
            switch (lineType) {
                case TacticalLines.BBS_AREA:
                    lineutility.getExteriorPoints(pLinePoints, vblSaveCounter, lineType, false);
                    acCounter = vblSaveCounter;
                    break;
                case TacticalLines.BS_CROSS:
                    pt0 = new POINT2(pLinePoints[0]);
                    pLinePoints[0] = new POINT2(pt0);
                    pLinePoints[0].x -= 10;
                    pLinePoints[1] = new POINT2(pt0);
                    pLinePoints[1].x += 10;
                    pLinePoints[1].style = 10;
                    pLinePoints[2] = new POINT2(pt0);
                    pLinePoints[2].y += 10;
                    pLinePoints[3] = new POINT2(pt0);
                    pLinePoints[3].y -= 10;
                    acCounter = 4;
                    break;
                case TacticalLines.BS_RECTANGLE:
                    lineutility.CalcMBRPoints(pLinePoints, pLinePoints.length, pt0, pt2);   //pt0=ul, pt1=lr
                    //pt0=new POINT2(pLinePoints[0]);
                    //pt2=new POINT2(pLinePoints[1]);
                    //pt1=new POINT2(pt0);
                    //pt1.y=pt2.y;
                    pt1 = new POINT2(pt0);
                    pt1.x = pt2.x;
                    pt3 = new POINT2(pt0);
                    pt3.y = pt2.y;
                    pLinePoints = new POINT2[5];
                    pLinePoints[0] = new POINT2(pt0);
                    pLinePoints[1] = new POINT2(pt1);
                    pLinePoints[2] = new POINT2(pt2);
                    pLinePoints[3] = new POINT2(pt3);
                    pLinePoints[4] = new POINT2(pt0);
                    acCounter = 5;
                    break;
                case TacticalLines.BBS_RECTANGLE:
                    //double xmax=pLinePoints[0].x,xmin=pLinePoints[1].x,ymax=pLinePoints[0].y,ymin=pLinePoints[1].y;
                    //double xmax=pLinePoints[2].x,xmin=pLinePoints[0].x,ymax=pLinePoints[2].y,ymin=pLinePoints[0].y;
                    double buffer = pLinePoints[0].style;

                    pOriginalLinePoints = new POINT2[5];
                    pOriginalLinePoints[0] = new POINT2(pLinePoints[0]);
                    pOriginalLinePoints[1] = new POINT2(pLinePoints[1]);
                    pOriginalLinePoints[2] = new POINT2(pLinePoints[2]);
                    pOriginalLinePoints[3] = new POINT2(pLinePoints[3]);
                    pOriginalLinePoints[4] = new POINT2(pLinePoints[0]);

                    //clockwise orientation
                    pt0 = pLinePoints[0];
                    pt0.x -= buffer;
                    pt0.y -= buffer;
                    pt1 = pLinePoints[1];
                    pt1.x += buffer;
                    pt1.y -= buffer;
                    pt2 = pLinePoints[2];
                    pt2.x += buffer;
                    pt2.y += buffer;
                    pt3 = pLinePoints[3];
                    pt3.x -= buffer;
                    pt3.y += buffer;
                    pLinePoints = new POINT2[5];
                    pLinePoints[0] = new POINT2(pt0);
                    pLinePoints[1] = new POINT2(pt1);
                    pLinePoints[2] = new POINT2(pt2);
                    pLinePoints[3] = new POINT2(pt3);
                    pLinePoints[4] = new POINT2(pt0);
                    vblSaveCounter = 5;
                    acCounter = 5;
                    break;
                case TacticalLines.BS_ELLIPSE:
                    pt0 = pLinePoints[0];//the center of the ellipse
                    pt1 = pLinePoints[1];//the width of the ellipse
                    pt2 = pLinePoints[2];//the height of the ellipse
                    double azimuth = pLinePoints[3].x;
                    pLinePoints = getRotatedEllipsePoints(pt0, pt1, pt2, azimuth, lineType);
                    acCounter = 37;
                    vblSaveCounter=37;
                    break;
                case TacticalLines.PBS_ELLIPSE:
                case TacticalLines.PBS_CIRCLE:
                    pt0 = pLinePoints[0];//the center of the ellipse
                    pt1 = pLinePoints[1];//the width of the ellipse
                    pt2 = pLinePoints[2];//the height of the ellipse
                    azimuth = pLinePoints[3].x;
                    pOriginalLinePoints = getRotatedEllipsePoints(pt0, pt1, pt2, azimuth, lineType);
                    //use linestyle to get the distance to expand the shape for the buffer shape
                    double dist=pt0.style;
                    pt1.x+=dist;
                    pt2.y-=dist;
                    pLinePoints = getRotatedEllipsePoints(pt0, pt1, pt2, azimuth, lineType);
                    acCounter = 37;
                    vblSaveCounter=37;
                    break;
                case TacticalLines.OVERHEAD_WIRE:
                    acCounter = getOverheadWire(pLinePoints, vblSaveCounter);
                    break;
                case TacticalLines.OVERHEAD_WIRE_LS:
                    for (j = 0; j < vblSaveCounter; j++) {
                        //pLinePoints[j]=new POINT2(pOriginalLinePoints[j]);
                        pLinePoints[j].style = 1;
                    }
                    //pLinePoints[vblSaveCounter-1].style=5;
                    for (j = vblSaveCounter; j < 2 * vblSaveCounter; j++) {
                        pLinePoints[j] = new POINT2(pOriginalLinePoints[j - vblSaveCounter]);
                        pLinePoints[j].style = 20;
                    }
                    //pLinePoints[2*vblSaveCounter-1].style=5;
                    acCounter = pLinePoints.length;
                    break;
                case TacticalLines.BOUNDARY:
                    acCounter = pLinePoints.length;
                    break;
                case TacticalLines.REEF:
                    vblCounter = GetReefPoints(pLinePoints, vblSaveCounter);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.ICE_DRIFT:
                    lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 5], pLinePoints[vblCounter - 4], 10, 10, pArrowPoints, 0);
                    for (j = 0; j < 3; j++) {
                        pLinePoints[vblCounter - 3 + j] = new POINT2(pArrowPoints[j]);
                    }
                    pLinePoints[vblCounter - 4].style = 5;
                    pLinePoints[vblCounter - 1].style = 5;
                    acCounter = vblCounter;
                    break;
                case TacticalLines.RESTRICTED_AREA:
                    vblCounter = GetRestrictedAreaPoints(pLinePoints, vblSaveCounter);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.TRAINING_AREA:
                    //diagnostic
                    dMBR = lineutility.MBRDistance(pLinePoints, vblSaveCounter);
                    d = 20;
                    if (dMBR < 60) {
                        d = dMBR / 4;
                    }
                    if (d < 5) {
                        d = 5;
                    }
                    //end section
                    for (j = 0; j < vblSaveCounter; j++) {
                        pLinePoints[j].style = 1;
                    }
                    pLinePoints[vblSaveCounter - 1].style = 5;
                    pt0 = lineutility.CalcCenterPointDouble(pLinePoints, vblSaveCounter - 1);
                    //lineutility.CalcCircleDouble(pt0, 20, 26, arcPts, 0);
                    lineutility.CalcCircleDouble(pt0, d, 26, arcPts, 0);

                    for (j = vblSaveCounter; j < vblSaveCounter + 26; j++) {
                        pLinePoints[j] = new POINT2(arcPts[j - vblSaveCounter]);
                    }
                    pLinePoints[j - 1].style = 5;

                    //! inside the circle
                    //diagnostic
                    if (dMBR < 50) {
                        //d was used as the circle radius
                        d *= 0.6;
                    } else {
                        d = 12;
                    }
                    //end section

                    pt1 = new POINT2(pt0);
                    pt1.y -= d;//12;
                    pt1.style = 0;
                    pt2 = new POINT2(pt1);
                    pt2.y += d;//12;
                    pt2.style = 5;
                    pt3 = new POINT2(pt2);
                    pt3.y += d / 4;//3;
                    pt3.style = 0;
                    pt4 = new POINT2(pt3);
                    pt4.y += d / 4;//3;
                    pLinePoints[j++] = new POINT2(pt1);
                    pLinePoints[j++] = new POINT2(pt2);
                    pLinePoints[j++] = new POINT2(pt3);
                    pt4.style = 5;
                    pLinePoints[j++] = new POINT2(pt4);
                    vblCounter = j;
                    acCounter = vblCounter;
                    break;
                case TacticalLines.PIPE:
                    vblCounter = GetPipePoints(pLinePoints, vblSaveCounter);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.ANCHORAGE_AREA:
                    //get the direction and quadrant of the first segment
                    n = GetInsideOutsideDouble2(pLinePoints[0], pLinePoints[1], pLinePoints, vblSaveCounter, 0, lineType);
                    nQuadrant = lineutility.GetQuadrantDouble(pLinePoints[0], pLinePoints[1]);
                    //if the direction and quadrant are not compatible with GetFlotDouble then
                    //reverse the points
                    switch (nQuadrant) {
                        case 4:
                            switch (n) {
                                case 1:	//extend left
                                case 2:	//extend below
                                    break;
                                case 0:	//extend right
                                case 3:	//extend above
                                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 1:
                            switch (n) {
                                case 1:	//extend left
                                case 3:	//extend above
                                    break;
                                case 0:	//extend right
                                case 2:	//extend below
                                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 2:
                            switch (n) {
                                case 1:	//extend left
                                case 2:	//extend below
                                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                                    break;
                                case 0:	//extend right
                                case 3:	//extend above
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 3:
                            switch (n) {
                                case 1:	//extend left
                                case 3:	//extend above
                                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                                    break;
                                case 0:	//extend right
                                case 2:	//extend above
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                    lFlotCount = GetAnchorageDouble(pLinePoints, vblSaveCounter);
                    acCounter = lFlotCount;
                    break;
                case TacticalLines.ANCHORAGE_LINE:
                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                    acCounter = GetAnchorageDouble(pLinePoints, vblSaveCounter);
                    break;
                case TacticalLines.LRO:
                    int xCount = countsupport.GetXPointsCount(lineType, pOriginalLinePoints, vblSaveCounter);
                    POINT2[] xPoints2 = new POINT2[xCount];
                    int lvoCount = countsupport.GetLVOCount(lineType, pOriginalLinePoints, vblSaveCounter);
                    POINT2[] lvoPoints = new POINT2[lvoCount];
                    xCount = GetXPoints(lineType, pOriginalLinePoints, xPoints2, vblSaveCounter);
                    lvoCount = GetLVOPoints(lineType, pOriginalLinePoints, lvoPoints, vblSaveCounter);
                    for (k = 0; k < xCount; k++) {
                        pLinePoints[k] = new POINT2(xPoints2[k]);
                    }
                    if (xCount > 0) {
                        pLinePoints[xCount - 1].style = 5;
                    }
                    for (k = 0; k < lvoCount; k++) {
                        pLinePoints[xCount + k] = new POINT2(lvoPoints[k]);
                    }
                    acCounter = xCount + lvoCount;
                    break;
                case TacticalLines.UNDERCAST:
                    if (pLinePoints[0].x < pLinePoints[1].x) {
                        lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                    }

                    lFlotCount = flot.GetFlotDouble(pLinePoints, vblSaveCounter);
                    acCounter = lFlotCount;
                    break;
                case TacticalLines.LVO:
                    acCounter = GetLVOPoints(lineType, pOriginalLinePoints, pLinePoints, vblSaveCounter);
                    break;
                case TacticalLines.ICING:
                    vblCounter = GetIcingPointsDouble(pLinePoints, vblSaveCounter);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.MVFR:
                    //get the direction and quadrant of the first segment
                    n = GetInsideOutsideDouble2(pLinePoints[0], pLinePoints[1], pLinePoints, vblSaveCounter, 0, lineType);
                    nQuadrant = lineutility.GetQuadrantDouble(pLinePoints[0], pLinePoints[1]);
                    //if the direction and quadrant are not compatible with GetFlotDouble then
                    //reverse the points
                    switch (nQuadrant) {
                        case 4:
                            switch (n) {
                                case 0:	//extend left
                                case 3:	//extend below
                                    break;
                                case 1:	//extend right
                                case 2:	//extend above
                                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 1:
                            switch (n) {
                                case 0:	//extend left
                                case 2:	//extend above
                                    break;
                                case 1:	//extend right
                                case 3:	//extend below
                                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 2:
                            switch (n) {
                                case 0:	//extend left
                                case 3:	//extend below
                                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                                    break;
                                case 1:	//extend right
                                case 2:	//extend above
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 3:
                            switch (n) {
                                case 0:	//extend left
                                case 2:	//extend above
                                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);
                                    break;
                                case 1:	//extend right
                                case 3:	//extend above
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                    lFlotCount = flot.GetFlotDouble(pLinePoints, vblSaveCounter);
                    acCounter = lFlotCount;
                    break;
                case TacticalLines.ITD:
                    acCounter = GetITDPointsDouble(pLinePoints, vblSaveCounter);
                    break;
                case TacticalLines.CONVERGANCE:
                    acCounter = GetConvergancePointsDouble(pLinePoints, vblSaveCounter);
                    break;
                case TacticalLines.RIDGE:
                    vblCounter = GetRidgePointsDouble(pLinePoints, lineType, vblSaveCounter);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.TROUGH:
                case TacticalLines.INSTABILITY:
                case TacticalLines.SHEAR:
                    //case TacticalLines.SQUALL:
                    //CELineArrayGlobals.MaxPixels2=saveMaxPixels+100;
                    vblCounter = GetSquallDouble(pLinePoints, 10, 6, 30, vblSaveCounter);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.SQUALL:
                    //vblCounter = GetSquallDouble(pLinePoints, 10, 6, 30, vblSaveCounter);
                    vblCounter = GetSevereSquall(pLinePoints, vblSaveCounter);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.USF:
                case TacticalLines.SFG:
                case TacticalLines.SFY:
                    vblCounter = flot.GetSFPointsDouble(pLinePoints, vblSaveCounter, lineType);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.SF:
                    vblCounter = flot.GetOccludedPointsDouble(pLinePoints, vblSaveCounter, lineType);
                    for (j = 0; j < vblSaveCounter; j++) {
                        pLinePoints[vblCounter + j] = pOriginalLinePoints[j];
                    }

                    vblCounter += vblSaveCounter;
                    acCounter = vblCounter;
                    break;
                case TacticalLines.OFY:
                    vblCounter = flot.GetOFYPointsDouble(pLinePoints, vblSaveCounter, lineType);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.OCCLUDED:
                case TacticalLines.UOF:
                    vblCounter = flot.GetOccludedPointsDouble(pLinePoints, vblSaveCounter, lineType);
                    for (j = 0; j < vblSaveCounter; j++) {
                        pLinePoints[vblCounter + j] = pOriginalLinePoints[j];
                    }
                    vblCounter += vblSaveCounter;
                    acCounter = vblCounter;
                    break;
                case TacticalLines.WF:
                case TacticalLines.UWF:
                    lFlotCount = flot.GetFlot2Double(pLinePoints, vblSaveCounter, lineType);
                    for (j = 0; j < vblSaveCounter; j++) {
                        pLinePoints[vblCounter - vblSaveCounter + j] = pOriginalLinePoints[j];
                    }
                    acCounter = lFlotCount + vblSaveCounter;
                    break;
                case TacticalLines.WFG:
                case TacticalLines.WFY:
                    lFlotCount = flot.GetFlot2Double(pLinePoints, vblSaveCounter, lineType);
                    acCounter = lFlotCount;
                    break;
                case TacticalLines.CFG:
                case TacticalLines.CFY:
                    vblCounter = GetATWallPointsDouble(pLinePoints, lineType, vblSaveCounter);
                    acCounter = vblCounter;
                    break;
                case TacticalLines.CF:
                case TacticalLines.UCF:
                    vblCounter = GetATWallPointsDouble(pLinePoints, lineType, vblSaveCounter);
                    pLinePoints[vblCounter - 1].style = 5;
                    for (j = 0; j < vblSaveCounter; j++) {
                        pLinePoints[vblCounter + j] = pOriginalLinePoints[j];
                    }
                    vblCounter += vblSaveCounter;
                    pLinePoints[vblCounter - 1].style = 5;
                    acCounter = vblCounter;
                    break;
                case TacticalLines.IL:
                case TacticalLines.PLANNED:
                case TacticalLines.ESR1:
                case TacticalLines.ESR2:
                    lineutility.LineRelativeToLine(pLinePoints[0], pLinePoints[1], pLinePoints[2], pt0, pt1);
                    d = lineutility.CalcDistanceDouble(pLinePoints[0], pt0);
                    pt4 = lineutility.ExtendLineDouble(pt0, pLinePoints[0], d);
                    lineutility.LineRelativeToLine(pLinePoints[0], pLinePoints[1], pt4, pt2, pt3);
                    pLinePoints[0] = new POINT2(pt0);
                    pLinePoints[1] = new POINT2(pt1);
                    pLinePoints[2] = new POINT2(pt3);
                    pLinePoints[3] = new POINT2(pt2);
                    switch (lineType) {
                        case TacticalLines.IL:
                        case TacticalLines.ESR2:
                            pLinePoints[0].style = 0;
                            pLinePoints[1].style = 5;
                            pLinePoints[2].style = 0;
                            break;
                        case TacticalLines.PLANNED:
                            pLinePoints[0].style = 1;
                            pLinePoints[1].style = 5;
                            pLinePoints[2].style = 1;
                            break;
                        case TacticalLines.ESR1:
                            pLinePoints[1].style = 5;
                            if (pt0.x <= pt1.x) {
                                if (pLinePoints[1].y <= pLinePoints[2].y) {
                                    pLinePoints[0].style = 0;
                                    pLinePoints[2].style = 1;
                                } else {
                                    pLinePoints[0].style = 1;
                                    pLinePoints[2].style = 0;
                                }
                            } else {
                                if (pLinePoints[1].y >= pLinePoints[2].y) {
                                    pLinePoints[0].style = 0;
                                    pLinePoints[2].style = 1;
                                } else {
                                    pLinePoints[0].style = 1;
                                    pLinePoints[2].style = 0;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    acCounter = 4;
                    break;
                case TacticalLines.FORDSITE:
                    lineutility.LineRelativeToLine(pLinePoints[0], pLinePoints[1], pLinePoints[2], pt0, pt1);
                    pLinePoints[0].style = 1;
                    pLinePoints[1].style = 5;
                    pLinePoints[2] = new POINT2(pt0);
                    pLinePoints[2].style = 1;
                    pLinePoints[3] = new POINT2(pt1);
                    pLinePoints[3].style = 5;
                    acCounter = 4;
                    break;
                case TacticalLines.ROADBLK:
                    pts = new POINT2[4];
                    for (j = 0; j < 4; j++) {
                        pts[j] = new POINT2(pLinePoints[j]);
                    }
                    dRadius = lineutility.CalcDistanceDouble(pLinePoints[0], pLinePoints[1]);
                    d = lineutility.CalcDistanceToLineDouble(pLinePoints[0], pLinePoints[1], pLinePoints[2]);

                    //first two lines
                    pLinePoints[0] = lineutility.ExtendTrueLinePerpDouble(pts[0], pts[1], pts[1], d, 0);
                    pLinePoints[1] = lineutility.ExtendTrueLinePerpDouble(pts[0], pts[1], pts[0], d, 5);
                    pLinePoints[2] = lineutility.ExtendTrueLinePerpDouble(pts[0], pts[1], pts[1], -d, 0);
                    pLinePoints[3] = lineutility.ExtendTrueLinePerpDouble(pts[0], pts[1], pts[0], -d, 5);

                    midpt = lineutility.MidPointDouble(pts[0], pts[1], 0);
                    //move the midpoint
                    midpt = lineutility.ExtendLineDouble(pts[0], midpt, d);

                    //the next line
                    pLinePoints[4] = lineutility.ExtendAngledLine(pts[0], pts[1], midpt, 105, dRadius / 2);
                    pLinePoints[5] = lineutility.ExtendAngledLine(pts[0], pts[1], midpt, -75, dRadius / 2);
                    pLinePoints[5].style = 5;

                    //recompute the original midpt because it was moved
                    midpt = lineutility.MidPointDouble(pts[0], pts[1], 0);
                    //move the midpoint
                    midpt = lineutility.ExtendLineDouble(pts[1], midpt, d);

                    //the last line
                    pLinePoints[6] = lineutility.ExtendAngledLine(pts[0], pts[1], midpt, 105, dRadius / 2);
                    pLinePoints[7] = lineutility.ExtendAngledLine(pts[0], pts[1], midpt, -75, dRadius / 2);
                    pLinePoints[7].style = 5;

                    acCounter = 8;
                    break;
                case TacticalLines.AIRFIELD:
                case TacticalLines.DMA:
                case TacticalLines.DUMMY:
                    AreaWithCenterFeatureDouble(pLinePoints, vblCounter, lineType);
                    acCounter = vblCounter;
                    //FillPoints(pLinePoints,acCounter,points);
                    break;
                case TacticalLines.PNO:
                    for (j = 0; j < vblCounter; j++) {
                        pLinePoints[j].style = 1;
                    }

                    acCounter = vblCounter;
                    break;
                case TacticalLines.DMAF:
                    AreaWithCenterFeatureDouble(pLinePoints, vblCounter, lineType);
                    pLinePoints[vblCounter - 1].style = 5;
                    FillPoints(pLinePoints, vblCounter, points);
                    xPoints = lineutility.LineOfXPoints(pOriginalLinePoints);
                    for (j = 0; j < xPoints.size(); j++) {
                        points.add((POINT2) xPoints.get(j));
                    }

                    acCounter = points.size();
                    break;
                case TacticalLines.FOXHOLE:
                    bolVertical = lineutility.CalcTrueSlopeDouble(pt0, pt1, m);

                    if (bolVertical == 0) //line is vertical
                    {
                        if (pt0.y > pt1.y) {
                            direction = 0;
                        } else {
                            direction = 1;
                        }
                    }
                    if (bolVertical != 0 && m.value[0] <= 1) {
                        if (pt0.x < pt1.x) {
                            direction = 3;
                        } else {
                            direction = 2;
                        }
                    }
                    if (bolVertical != 0 && m.value[0] > 1) {
                        if (pt0.x < pt1.x && pt0.y > pt1.y) {
                            direction = 1;
                        }
                        if (pt0.x < pt1.x && pt0.y < pt1.y) {
                            direction = 0;
                        }

                        if (pt0.x > pt1.x && pt0.y > pt1.y) {
                            direction = 1;
                        }
                        if (pt0.x > pt1.x && pt0.y < pt1.y) {
                            direction = 0;
                        }
                    }

                    //M. Deutch 8-19-04
                    if (dMBR / 20 > maxLength) {
                        dMBR = 20 * maxLength;
                    }
                    if (dMBR / 20 < minLength) {
                        dMBR = 20 * minLength;
                    }
                    if (dMBR < 250) {
                        dMBR = 250;
                    }
                    if (dMBR > 500) {
                        dMBR = 500;
                    }

                    pLinePoints[0] = lineutility.ExtendDirectedLine(pt0, pt1, pt0, direction, dMBR / 20);
                    pLinePoints[1] = new POINT2(pt0);
                    pLinePoints[2] = new POINT2(pt1);
                    pLinePoints[3] = lineutility.ExtendDirectedLine(pt0, pt1, pt1, direction, dMBR / 20);
                    acCounter = 4;
                    break;
                case TacticalLines.ISOLATE:
                    GetIsolatePointsDouble(pLinePoints, lineType, converter);
                    acCounter = 50;
                    break;
                case TacticalLines.CORDONKNOCK:
                case TacticalLines.CORDONSEARCH:
                    GetIsolatePointsDouble(pLinePoints, lineType, converter);
                    acCounter = 50;
                    //FillPoints(pLinePoints,acCounter,points);
                    break;
                case TacticalLines.OCCUPY:
                    GetIsolatePointsDouble(pLinePoints, lineType, converter);
                    acCounter = 32;
                    break;
                case TacticalLines.RETAIN:
                    GetIsolatePointsDouble(pLinePoints, lineType, converter);
                    acCounter = 75;
                    break;
                case TacticalLines.SECURE:
                    GetIsolatePointsDouble(pLinePoints, lineType, converter);
                    acCounter = 29;
                    break;
                case TacticalLines.TURN:
                    
                    if(rev > 0)
                    {
                        //2525C or higher switch first and last point.  Order changed in 2525C
                        POINT2 swapPt = pLinePoints[0];
                        pLinePoints[0] = pLinePoints[1];
                        pLinePoints[1] = swapPt;
                    }//*/
                    
                    GetIsolatePointsDouble(pLinePoints, lineType, null);
                    acCounter = 29;
                    break;
                case TacticalLines.ENCIRCLE:
                    //pOriginalLinePoints=null;
                    //pOriginalLinePoints = new POINT2[vblSaveCounter+2];
                    //for(j = 0;j<vblSaveCounter;j++)
                    //    pOriginalLinePoints[j] = new POINT2(pLinePoints[j]);
                    //pOriginalLinePoints[vblSaveCounter] = new POINT2(pLinePoints[0]);
                    //pOriginalLinePoints[vblSaveCounter+1] = new POINT2(pLinePoints[1]);
                    acCounter = GetZONEPointsDouble2(pLinePoints, lineType, vblSaveCounter);
                    //for(j=0;j<vblSaveCounter+2;j++)
                    //  pLinePoints[pointCounter+j]=new POINT2(pOriginalLinePoints[j]);
                    //pointCounter += vblSaveCounter+1;
                    //acCounter=pointCounter;
                    break;
                case TacticalLines.BELT1:
                    pUpperLinePoints = new POINT2[vblSaveCounter];
                    pLowerLinePoints = new POINT2[vblSaveCounter];
                    pUpperLowerLinePoints = new POINT2[2 * vblCounter];
                    for (j = 0; j < vblSaveCounter; j++) {
                        pLowerLinePoints[j] = new POINT2(pLinePoints[j]);
                    }

                    for (j = 0; j < vblSaveCounter; j++) {
                        pUpperLinePoints[j] = new POINT2(pLinePoints[j]);
                    }

                    pUpperLinePoints = Channels.CoordIL2Double(1, pUpperLinePoints, 1, vblSaveCounter, lineType, 30);
                    pLowerLinePoints = Channels.CoordIL2Double(1, pLowerLinePoints, 0, vblSaveCounter, lineType, 30);
                    for (j = 0; j < vblSaveCounter; j++) {
                        pUpperLowerLinePoints[j] = new POINT2(pUpperLinePoints[j]);
                    }

                    for (j = 0; j < vblSaveCounter; j++) {
                        pUpperLowerLinePoints[j + vblSaveCounter] = new POINT2(pLowerLinePoints[vblSaveCounter - j - 1]);
                    }

                    pUpperLowerLinePoints[2 * vblSaveCounter] = new POINT2(pUpperLowerLinePoints[0]);
                    vblCounter = GetZONEPointsDouble2(pUpperLowerLinePoints, lineType, 2 * vblSaveCounter + 1);
                    for (j = 0; j < vblCounter; j++) {
                        pLinePoints[j] = new POINT2(pUpperLowerLinePoints[j]);
                    }
                    //pLinePoints[j]=pLinePoints[0];
                    //vblCounter++;
                    acCounter = vblCounter;
                    break;
                case TacticalLines.BELT:	//change 2
                case TacticalLines.ZONE:
                case TacticalLines.OBSAREA:
                case TacticalLines.OBSFAREA:
                case TacticalLines.STRONG:
                case TacticalLines.FORT:
                    acCounter = GetZONEPointsDouble2(pLinePoints, lineType, vblSaveCounter);
                    break;
                case TacticalLines.ATWALL:
                case TacticalLines.LINE:  //7-9-07
                    acCounter = GetATWallPointsDouble2(pLinePoints, lineType, vblSaveCounter);
                    break;
//                case TacticalLines.ATWALL3D:
//                    acCounter = GetATWallPointsDouble3D(pLinePoints, lineType, vblSaveCounter);
//                    break;
                case TacticalLines.PLD:
                    for (j = 0; j < vblCounter; j++) {
                        pLinePoints[j].style = 1;
                    }
                    acCounter = vblCounter;
                    break;
                case TacticalLines.FEBA:
                    CoordFEBADouble(pLinePoints, vblCounter);
                    acCounter = pLinePoints.length;
                    break;
                case TacticalLines.UAV:
                case TacticalLines.MRR:
                    if (rev == RendererSettings.Symbology_2525B) {
                        dMRR = pOriginalLinePoints[0].style;
                        if (dMRR <= 0) {
                            dMRR = 1;//was 14
                        }
                        lineutility.GetSAAFRSegment(pLinePoints, lineType, dMRR, rev);
                        acCounter = 6;
                    }
                    if (rev == RendererSettings.Symbology_2525C) {
                        return GetLineArray2Double(TacticalLines.SAAFR, pLinePoints, vblCounter, vblSaveCounter, shapes, clipBounds, rev, converter);
                    }
                    break;
                case TacticalLines.MRR_USAS:
                case TacticalLines.UAV_USAS:
                case TacticalLines.LLTR:	//added 5-4-07
                case TacticalLines.SAAFR:	//these have multiple segments
                case TacticalLines.AC:
                    boolean bolSegmentAC = false;
                    //uncomment the next line if the air corridor is segmented
                    bolSegmentAC = true;
                    dMRR = dACP;
                    lineutility.InitializePOINT2Array(acPoints);
                    lineutility.InitializePOINT2Array(arcPts);
                    acCounter = 0;
                    if (!bolSegmentAC) {
                        for (j = 0; j < vblSaveCounter; j++) {
                            if (pOriginalLinePoints[j].style <= 0) {
                                pOriginalLinePoints[j].style = 1; //was 14
                            }
                        }
                    }
                    //get the SAAFR segments
                    for (j = 0; j < vblSaveCounter - 1; j++) {
                        //diagnostic: use style member for dMBR
                        dMBR = pOriginalLinePoints[j].style;
                        acPoints[0] = new POINT2(pOriginalLinePoints[j]);
                        acPoints[1] = new POINT2(pOriginalLinePoints[j + 1]);
                        lineutility.GetSAAFRSegment(acPoints, lineType, dMBR, rev);//was dMRR
                        for (k = 0; k < 6; k++) {
                            pLinePoints[acCounter] = new POINT2(acPoints[k]);
                            acCounter++;
                        }
                    }
                    //get the circles
                    //int nextCircleSize = 0,
                    int currentCircleSize = 0;
                    if (!bolSegmentAC) {
                        for (j = 0; j < vblSaveCounter - 1; j++) {
                            currentCircleSize = pOriginalLinePoints[j].style;
                        //nextCircleSize = pOriginalLinePoints[j + 1].style;

                            //draw the circle at the segment front end
                            arcPts[0] = new POINT2(pOriginalLinePoints[j]);
                            //diagnostic: use style member for dMBR
                            //dMBR=pOriginalLinePoints[j].style;                        
                            dMBR = currentCircleSize;
                            lineutility.CalcCircleDouble(arcPts[0], dMBR, 26, arcPts, 0);//was dMRR
                            arcPts[25].style = 5;
                            for (k = 0; k < 26; k++) {
                                pLinePoints[acCounter] = new POINT2(arcPts[k]);
                                acCounter++;
                            }

                            //draw the circle at the segment back end
                            arcPts[0] = new POINT2(pOriginalLinePoints[j + 1]);
                            //dMBR=pOriginalLinePoints[j].style;                        
                            dMBR = currentCircleSize;
                            lineutility.CalcCircleDouble(arcPts[0], dMBR, 26, arcPts, 0);//was dMRR
                            arcPts[25].style = 5;
                            for (k = 0; k < 26; k++) {
                                pLinePoints[acCounter] = new POINT2(arcPts[k]);
                                acCounter++;
                            }
                        }
                    } else {
                        int lastCircleSize = 0;
                        POINT2 lastCirclePoint = null;
                        for (j = 0; j < vblSaveCounter; j++) {
                            currentCircleSize = pOriginalLinePoints[j].style;
                            if (j == 0) {
                                lastCircleSize = currentCircleSize;
                                lastCirclePoint = pOriginalLinePoints[j];
                                continue;
                            }
                            if (currentCircleSize < 0) {
                                continue;
                            }
                            //the current circle point
                            arcPts[0] = new POINT2(pOriginalLinePoints[j]);
                            dMBR = lastCircleSize;
                            lineutility.CalcCircleDouble(arcPts[0], dMBR, 26, arcPts, 0);
                            arcPts[25].style = 5;
                            for (k = 0; k < 26; k++) {
                                pLinePoints[acCounter] = new POINT2(arcPts[k]);
                                acCounter++;
                            }
                            //the previous circle point
                            arcPts[0] = new POINT2(lastCirclePoint);
                            lineutility.CalcCircleDouble(arcPts[0], dMBR, 26, arcPts, 0);
                            arcPts[25].style = 5;
                            for (k = 0; k < 26; k++) {
                                pLinePoints[acCounter] = new POINT2(arcPts[k]);
                                acCounter++;
                            }
                            //set the last values
                            lastCircleSize = currentCircleSize;
                            lastCirclePoint = pOriginalLinePoints[j];
                        }
                        //get the middle line for Rev B AC, MRR, UAV, and LLTR
                        if(rev == RendererSettings.Symbology_2525B && lineType != TacticalLines.SAAFR)
                        {
                            pts=lineutility.GetSAAFRMiddleLine(pOriginalLinePoints);
                            for(j=0;j<pts.length;j++)
                            {
                                pLinePoints[acCounter++] = new POINT2(pts[j]);
                            }
                        }
                    }
                    //acPoints = null;
                    break;
                case TacticalLines.MINED:
                case TacticalLines.UXO:
                    acCounter = vblCounter;
                    break;
                case TacticalLines.BEARING:
                case TacticalLines.ACOUSTIC:
                case TacticalLines.ELECTRO:
                case TacticalLines.TORPEDO:
                case TacticalLines.OPTICAL:
                    acCounter = vblCounter;
                    break;
                case TacticalLines.MSDZ:
                    lineutility.InitializePOINT2Array(circlePoints);
                    pt3 = new POINT2(pLinePoints[3]);
                    if(converter==null)
                    {
                        dRadius = lineutility.CalcDistanceDouble(pt0, pt1);
                        lineutility.CalcCircleDouble(pt0, dRadius, 100,
                                circlePoints, 0);
                    }
                    else    //use the converter
                        lineutility.CalcCircleDouble2(pt0, pt1, 100,
                            circlePoints, converter);
                    
                    for (j = 0; j < 100; j++) {
                        pLinePoints[j] = new POINT2(circlePoints[j]);
                    }
                    pLinePoints[99].style = 5;
                    
                    if(converter==null)
                    {
                        dRadius = lineutility.CalcDistanceDouble(pt0, pt2);
                        lineutility.CalcCircleDouble(pt0, dRadius, 100,
                            circlePoints, 0);
                    }
                    else
                        lineutility.CalcCircleDouble2(pt0, pt2, 100,
                            circlePoints, converter);
                    
                    for (j = 0; j < 100; j++) {
                        pLinePoints[100 + j] = new POINT2(circlePoints[j]);
                    }
                    pLinePoints[199].style = 5;
                    
                    if(converter==null)
                    {
                        dRadius = lineutility.CalcDistanceDouble(pt0, pt3);
                        lineutility.CalcCircleDouble(pt0, dRadius, 100,
                            circlePoints, 0);
                    }
                    else
                        lineutility.CalcCircleDouble2(pt0, pt3, 100,
                            circlePoints, converter);
                    
                    for (j = 0; j < 100; j++) {
                        pLinePoints[200 + j] = new POINT2(circlePoints[j]);
                    }
                    acCounter = vblCounter;
                    break;
                case TacticalLines.CONVOY:
                    d = lineutility.CalcDistanceDouble(pt0, pt1);
                    if (d <= 30) {
                        GetLineArray2Double(TacticalLines.DIRATKSPT, pLinePoints, 5, 2, shapes, clipBounds, rev, converter);
                        break;
                    }
                    //reverse the points
                    //lineutility.ReversePointsDouble2(ref pLinePoints, vblSaveCounter);
                    pt0 = new POINT2(pLinePoints[0]);
                    pt1 = new POINT2(pLinePoints[1]);

                    bolVertical = lineutility.CalcTrueSlopeDouble(pt1, pt0, m);
                    pt0 = lineutility.ExtendLine2Double(pt1, pt0, -30, 0);
                    if (m.value[0] < 1) {
                        pLinePoints[0] = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 2, 10);
                        pLinePoints[1] = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 2, 10);
                        pLinePoints[2] = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 3, 10);
                        pLinePoints[3] = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 3, 10);
                    } else {
                        pLinePoints[0] = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 0, 10);
                        pLinePoints[1] = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 0, 10);
                        pLinePoints[2] = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 1, 10);
                        pLinePoints[3] = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 1, 10);
                    }
                    pt2 = lineutility.ExtendLineDouble(pt1, pt0, 30);
                    lineutility.GetArrowHead4Double(pt0, pt2, 30, 30, pArrowPoints, 0);

                    d = lineutility.CalcDistanceDouble(pLinePoints[0], pArrowPoints[0]);
                    d1 = lineutility.CalcDistanceDouble(pLinePoints[3], pArrowPoints[0]);
                    pLinePoints[3].style = 5;
                    if (d < d1) {
                        pLinePoints[4] = new POINT2(pLinePoints[0]);
                        pLinePoints[4].style = 0;
                        pLinePoints[5] = new POINT2(pArrowPoints[0]);
                        pLinePoints[5].style = 0;
                        pLinePoints[6] = new POINT2(pArrowPoints[1]);
                        pLinePoints[6].style = 0;
                        pLinePoints[7] = new POINT2(pArrowPoints[2]);
                        pLinePoints[7].style = 0;
                        pLinePoints[8] = new POINT2(pLinePoints[3]);
                    } else {
                        pLinePoints[4] = pLinePoints[3];
                        pLinePoints[4].style = 0;
                        pLinePoints[5] = pArrowPoints[0];
                        pLinePoints[5].style = 0;
                        pLinePoints[6] = pArrowPoints[1];
                        pLinePoints[6].style = 0;
                        pLinePoints[7] = pArrowPoints[2];
                        pLinePoints[7].style = 0;
                        pLinePoints[8] = pLinePoints[0];
                    }

                    acCounter = 9;
                    //FillPoints(pLinePoints,acCounter,points);
                    break;
                case TacticalLines.HCONVOY:
                    //reverse the points
                    //lineutility.ReversePointsDouble2(ref pLinePoints, vblSaveCounter);
                    pt0 = new POINT2(pLinePoints[0]);
                    pt1 = new POINT2(pLinePoints[1]);

                    pt2.x = (pt0.x + pt1.x) / 2;
                    pt2.y = (pt0.y + pt1.y) / 2;
                    bolVertical = lineutility.CalcTrueSlopeDouble(pt1, pt0, m);
                    if (m.value[0] < 1) {
                        pLinePoints[0] = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 2, 10);
                        pLinePoints[1] = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 2, 10);
                        pLinePoints[2] = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 3, 10);
                        pLinePoints[3] = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 3, 10);
                    } else {
                        pLinePoints[0] = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 0, 10);
                        pLinePoints[1] = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 0, 10);
                        pLinePoints[2] = lineutility.ExtendDirectedLine(pt0, pt1, pt1, 1, 10);
                        pLinePoints[3] = lineutility.ExtendDirectedLine(pt0, pt1, pt0, 1, 10);
                    }

                    pLinePoints[4] = new POINT2(pLinePoints[0]);
                    pLinePoints[5] = new POINT2(pt0);
                    pLinePoints[5].style = 0;
                    pt2 = lineutility.ExtendLineDouble(pt1, pt0, 50);
                    lineutility.GetArrowHead4Double(pt2, pt0, 20, 20, pArrowPoints, 0);

//                    for (j = 0; j < 3; j++)
//                    {
//                        pLinePoints[j + 6] = new POINT2(pArrowPoints[j]);
//                    }
//                    pLinePoints[8].style = 0;
//                    pLinePoints[9] = new POINT2(pArrowPoints[0]);
                    pLinePoints[6] = new POINT2(pArrowPoints[1]);
                    pLinePoints[7] = new POINT2(pArrowPoints[0]);
                    pLinePoints[8] = new POINT2(pArrowPoints[2]);
                    pLinePoints[8].style = 0;
                    pLinePoints[9] = new POINT2(pArrowPoints[1]);

                    acCounter = 10;
                    //FillPoints(pLinePoints,acCounter,points);
                    break;
                case TacticalLines.ONEWAY:
                case TacticalLines.ALT:
                case TacticalLines.TWOWAY:
                    nCounter = (int) vblSaveCounter;
                    pLinePoints[vblSaveCounter - 1].style = 5;
                    for (j = 0; j < vblSaveCounter - 1; j++) {
                        d = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                        if (d < 20) //too short
                        {
                            continue;
                        }
                        pt0 = new POINT2(pLinePoints[j]);
                        pt1 = new POINT2(pLinePoints[j + 1]);
                        bolVertical = lineutility.CalcTrueSlopeDouble(pLinePoints[j], pLinePoints[j + 1], m);
                        d = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                        pt2 = lineutility.ExtendLine2Double(pLinePoints[j], pLinePoints[j + 1], -3 * d / 4, 0);
                        pt3 = lineutility.ExtendLine2Double(pLinePoints[j], pLinePoints[j + 1], -1 * d / 4, 5);
                        if (pLinePoints[j].x < pLinePoints[j + 1].x) {
                            if (m.value[0] < 1) {
                                pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 2, 10);
                                pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 2, 10);
                            }
                            if (m.value[0] >= 1) {
                                pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 1, 10);
                                pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 1, 10);
                            }
                        }
                        if (pLinePoints[j].x > pLinePoints[j + 1].x) {
                            if (m.value[0] < 1) {
                                pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 3, 10);
                                pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 3, 10);
                            }
                            if (m.value[0] >= 1) {
                                pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 0, 10);
                                pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 0, 10);
                            }
                        }
                        if (bolVertical == 0) {
                            if (pLinePoints[j].y > pLinePoints[j + 1].y) {
                                pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 0, 10);
                                pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 0, 10);
                            } else {
                                pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 1, 10);
                                pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 1, 10);
                            }
                        }
                        pLinePoints[nCounter] = new POINT2(pt2);
                        nCounter++;
                        pLinePoints[nCounter] = new POINT2(pt3);
                        nCounter++;

                        d = 10;
                        if (dMBR / 20 < minLength) {
                            d = 5;
                        }

                        lineutility.GetArrowHead4Double(pt2, pt3, (int) d, (int) d,
                                pArrowPoints, 0);

                        for (k = 0; k < 3; k++) {
                            pLinePoints[nCounter] = new POINT2(pArrowPoints[k]);
                            nCounter++;
                        }

                        if (lineType == (long) TacticalLines.ALT) {
                            lineutility.GetArrowHead4Double(pt3, pt2, (int) d, (int) d,
                                    pArrowPoints, 0);

                            for (k = 0; k < 3; k++) {
                                pLinePoints[nCounter] = new POINT2(pArrowPoints[k]);
                                nCounter++;
                            }
                        }
                        if (lineType == (long) TacticalLines.TWOWAY) {
                            if (pLinePoints[j].x < pLinePoints[j + 1].x) {
                                if (m.value[0] < 1) {
                                    pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 2, 15);
                                    pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 2, 15);
                                }
                                if (m.value[0] >= 1) {
                                    pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 1, 15);
                                    pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 1, 15);
                                }
                            }
                            if (pLinePoints[j].x > pLinePoints[j + 1].x) {
                                if (m.value[0] < 1) {
                                    pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 3, 15);
                                    pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 3, 15);
                                }
                                if (m.value[0] >= 1) {
                                    pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 0, 15);
                                    pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 0, 15);
                                }
                            }
                            if (bolVertical == 0) {
                                if (pLinePoints[j].y > pLinePoints[j + 1].y) {
                                    pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 0, 15);
                                    pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 0, 15);
                                } else {
                                    pt2 = lineutility.ExtendDirectedLine(pt0, pt1, pt2, 1, 15);
                                    pt3 = lineutility.ExtendDirectedLine(pt0, pt1, pt3, 1, 15);
                                }
                            }

                            pLinePoints[nCounter] = new POINT2(pt2);
                            nCounter++;
                            pLinePoints[nCounter] = new POINT2(pt3);
                            nCounter++;
                            lineutility.GetArrowHead4Double(pt3, pt2, (int) d, (int) d,
                                    pArrowPoints, 0);

                            for (k = 0; k < 3; k++) {
                                pLinePoints[nCounter] = new POINT2(pArrowPoints[k]);
                                nCounter++;
                            }
                        }
                    }
                    acCounter = nCounter;
                    break;
                case TacticalLines.CFL:
                    for (j = 0; j < vblCounter; j++) //dashed lines
                    {
                        pLinePoints[j].style = 1;
                    }

                    acCounter = vblCounter;
                    break;
                case TacticalLines.DIRATKFNT:	//extra three for arrow plus extra three for feint
                    //diagnostic move the line to make room for the feint
                    d = lineutility.CalcDistanceDouble(pLinePoints[0], pLinePoints[1]);
                    if (d < 20)//was 10
                    {
                        pLinePoints[1] = lineutility.ExtendLineDouble(pLinePoints[0], pLinePoints[1], 21);//was 11
                    }
                    pLinePoints[0] = lineutility.ExtendAlongLineDouble(pLinePoints[0], pLinePoints[1], 20);   //was 10
                    //end section
                    //reverse the points
                    lineutility.ReversePointsDouble2(
                            pLinePoints,
                            vblSaveCounter);

                    d = dMBR;

                    pt0 = lineutility.ExtendLineDouble(pLinePoints[vblCounter - 8], pLinePoints[vblCounter - 7], 20);    //was 10
                    pt1 = new POINT2(pLinePoints[vblCounter - 8]);
                    pt2 = new POINT2(pLinePoints[vblCounter - 7]);

                    if (d / 10 > maxLength) {
                        d = 10 * maxLength;
                    }
                    if (d / 10 < minLength) {
                        d = 10 * minLength;
                    }
                    if (d < 250) {
                        d = 250;
                    }
                    if (d > 500) {
                        d = 250;
                    }

                    lineutility.GetArrowHead4Double(pt1, pt2, (int) d / 10, (int) d / 10,
                            pArrowPoints, 0);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 6 + k] = pArrowPoints[k];
                    }
                    lineutility.GetArrowHead4Double(pt1, pt0, (int) d / 10, (int) d / 10,
                            pArrowPoints, 18);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 3 + k] = pArrowPoints[k];
                    }
                    acCounter = vblCounter;
                    break;
                case TacticalLines.FORDIF:
                    lineutility.LineRelativeToLine(pLinePoints[0], pLinePoints[1], pLinePoints[2], pt4, pt5);   //as pt2,pt3
                    pLinePoints[2] = new POINT2(pt5);//was pt3
                    pLinePoints[3] = new POINT2(pt4);//was pt2

                    for (j = 0; j < vblCounter; j++) {
                        pLinePoints[j].style = 1;
                    }

                    //CommandSight section
                    //comment 2 lines for CS
                    pt0 = lineutility.MidPointDouble(pLinePoints[0], pLinePoints[1], 0);
                    pt1 = lineutility.MidPointDouble(pLinePoints[2], pLinePoints[3], 0);
                    POINT2[] savepoints = new POINT2[2];
                    savepoints[0] = new POINT2(pt0);
                    savepoints[1] = new POINT2(pt1);
                    Boolean drawJaggies = true;
                    if (clipBounds != null) {
                        POINT2 ul = new POINT2(clipBounds.getMinX(), clipBounds.getMinY());
                        POINT2 lr = new POINT2(clipBounds.getMaxX(), clipBounds.getMaxY());
                        savepoints = lineutility.BoundOneSegment(pt0, pt1, ul, lr);
                        if (savepoints != null && savepoints.length > 1) {
                            pt0 = savepoints[0];
                            pt1 = savepoints[1];
                        } else {
                            savepoints = new POINT2[2];
                            savepoints[0] = new POINT2(pt0);
                            savepoints[1] = new POINT2(pt1);
                            drawJaggies = false;
                        }

                        midpt = lineutility.MidPointDouble(pt0, pt1, 0);
                        double dist0 = lineutility.CalcDistanceDouble(midpt, pt0);
                        double dist1 = lineutility.CalcDistanceDouble(midpt, pt1);

                        if (dist0 > dist1) {
                            lineutility.LineRelativeToLine(pLinePoints[0], pLinePoints[1], pt0, pt4, pt5);   //as pt2,pt3
                        } else {
                            lineutility.LineRelativeToLine(pLinePoints[0], pLinePoints[1], pt1, pt4, pt5);   //as pt2,pt3
                        }

                        pLinePoints[2] = new POINT2(pt5);//was pt3
                        pLinePoints[3] = new POINT2(pt4);//was pt2
                        //end section
                    } else {
                        midpt = lineutility.MidPointDouble(pLinePoints[0], pLinePoints[1], 0);
                        double dist0 = lineutility.CalcDistanceDouble(midpt, pt0);
                        double dist1 = lineutility.CalcDistanceDouble(midpt, pt1);

                        if (dist0 > dist1) {
                            lineutility.LineRelativeToLine(pLinePoints[0], pLinePoints[1], pt0, pt4, pt5);   //as pt2,pt3
                        } else {
                            lineutility.LineRelativeToLine(pLinePoints[0], pLinePoints[1], pt1, pt4, pt5);   //as pt2,pt3
                        }
                        pLinePoints[2] = new POINT2(pt5);//was pt3
                        pLinePoints[3] = new POINT2(pt4);//was pt2
                    }

                    //end section
                    //calculate start, end points for upper and lower lines
                    //across the middle
                    pt2 = lineutility.ExtendLine2Double(pLinePoints[0], pt0, -10, 0);
                    pt3 = lineutility.ExtendLine2Double(pLinePoints[3], pt1, -10, 0);
                    pt4 = lineutility.ExtendLine2Double(pLinePoints[0], pt0, 10, 0);
                    pt5 = lineutility.ExtendLine2Double(pLinePoints[3], pt1, 10, 0);

                    dWidth = lineutility.CalcDistanceDouble(pt0, pt1);

                    pointCounter = 4;
                    n = 1;
                    pLinePoints[pointCounter] = new POINT2(pt0);
                    pLinePoints[pointCounter].style = 0;
                    pointCounter++;
                    //while (dExtendLength < dWidth - 20)
                    if (drawJaggies) {
                        while (dExtendLength < dWidth - 10) {
                            //dExtendLength = (double) n * 10;
                            dExtendLength = (double) n * 5;
                            pLinePoints[pointCounter] = lineutility.ExtendLine2Double(pt2, pt3, dExtendLength - dWidth, 0);
                            pointCounter++;
                            n++;
                            //dExtendLength = (double) n * 10;
                            dExtendLength = (double) n * 5;
                            pLinePoints[pointCounter] = lineutility.ExtendLine2Double(pt4, pt5, dExtendLength - dWidth, 0);
                            pointCounter++;
                            if (pointCounter >= pLinePoints.length - 1) {
                                break;
                            }
                            n++;
                        }
                    }
                    pLinePoints[pointCounter] = new POINT2(pt1);
                    pLinePoints[pointCounter].style = 5;
                    pointCounter++;
                    acCounter = pointCounter;
                    break;
                case TacticalLines.ATDITCH:
                    acCounter = lineutility.GetDitchSpikeDouble(pLinePoints, vblSaveCounter,
                            0, lineType);
                    break;
                case (int) TacticalLines.ATDITCHC:	//extra Points were calculated by a function
                    pLinePoints[0].style = 9;
                    acCounter = lineutility.GetDitchSpikeDouble(pLinePoints, vblSaveCounter,
                            0, lineType);
                    //pLinePoints[vblCounter-1].style=10;
                    break;
                case TacticalLines.ATDITCHM:
                    lineutility.ReversePointsDouble2(
                            pLinePoints,
                            vblSaveCounter);
                    pLinePoints[0].style = 9;
                    acCounter = lineutility.GetDitchSpikeDouble(
                            pLinePoints,
                            vblSaveCounter,
                            0, lineType);
                    //pLinePoints[vblCounter-1].style = 20;
                    break;
                case TacticalLines.DIRATKGND:
                    //reverse the points
                    //lineutility.ReversePointsDouble2(
                    //pLinePoints,
                    //vblSaveCounter);

                    //was 20
                    if (dMBR / 30 > maxLength) {
                        dMBR = 30 * maxLength;
                    }
                    if (dMBR / 30 < minLength) {
                        dMBR = 30 * minLength;
                    }
                    //if(dMBR<250)
                    //  dMBR = 250;
                    if (dMBR < 500) {
                        dMBR = 500;
                    }
                    if (dMBR > 750) {
                        dMBR = 500;
                    }

                    //diagnostic move the line to make room for the feint
                    d = lineutility.CalcDistanceDouble(pLinePoints[0], pLinePoints[1]);
                    if (d < dMBR / 40) {
                        pLinePoints[1] = lineutility.ExtendLineDouble(pLinePoints[0], pLinePoints[1], dMBR / 40 + 1);
                    }

                    pLinePoints[0] = lineutility.ExtendAlongLineDouble(pLinePoints[0], pLinePoints[1], dMBR / 40);
                    //end section

                    //reverse the points
                    lineutility.ReversePointsDouble2(
                            pLinePoints,
                            vblSaveCounter);

                    pt0 = new POINT2(pLinePoints[vblCounter - 12]);
                    pt1 = new POINT2(pLinePoints[vblCounter - 11]);
                    pt2 = lineutility.ExtendLineDouble(pt0, pt1, dMBR / 40);
                    lineutility.GetArrowHead4Double(pt0, pt1, (int) dMBR / 20, (int) dMBR / 20,
                            pArrowPoints, 0);

                    for (j = 0; j < 3; j++) {
                        pLinePoints[vblCounter - 10 + j] = new POINT2(pArrowPoints[j]);
                    }
                    lineutility.GetArrowHead4Double(pt0, pt2, (int) (dMBR / 13.33), (int) (dMBR / 13.33),
                            pArrowPoints, 0);

                    for (j = 0; j < 3; j++) {
                        pLinePoints[vblCounter - 7 + j] = new POINT2(pArrowPoints[j]);
                    }

                    pLinePoints[vblCounter - 4] = new POINT2(pLinePoints[vblCounter - 10]);
                    pLinePoints[vblCounter - 4].style = 0;
                    pLinePoints[vblCounter - 3] = new POINT2(pLinePoints[vblCounter - 7]);
                    pLinePoints[vblCounter - 3].style = 5;

                    pLinePoints[vblCounter - 2] = new POINT2(pLinePoints[vblCounter - 8]);
                    pLinePoints[vblCounter - 2].style = 0;
                    pLinePoints[vblCounter - 1] = new POINT2(pLinePoints[vblCounter - 5]);
                    pLinePoints[vblCounter - 1].style = 5;
                    acCounter = vblCounter;
                    break;
                case TacticalLines.MFLANE:
                    pt2 = lineutility.ExtendLineDouble(pLinePoints[vblCounter - 8], pLinePoints[vblCounter - 7], dMBR / 2);
                    pt3 = new POINT2(pLinePoints[vblCounter - 7]);
                    pt1 = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], dMBR / 2);

                    if (dMBR / 10 > maxLength) {
                        dMBR = 10 * maxLength;
                    }
                    if (dMBR / 10 < minLength) {
                        dMBR = 10 * minLength;
                    }
                    if (dMBR > 250) {
                        dMBR = 250;
                    }

                    lineutility.GetArrowHead4Double(pt2, pt3, (int) dMBR / 10, (int) dMBR / 10,
                            pArrowPoints, 0);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 6 + k] = new POINT2(pArrowPoints[k]);
                    }
                    lineutility.GetArrowHead4Double(pt1, pt0, (int) dMBR / 10, (int) dMBR / 10,
                            pArrowPoints, 0);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 3 + k] = new POINT2(pArrowPoints[k]);
                    }
                    //pLinePoints[1].style=5;
                    pLinePoints[vblSaveCounter - 1].style = 5;
                    acCounter = vblCounter;
                    //FillPoints(pLinePoints,acCounter,points);
                    break;
                case TacticalLines.RAFT:	//extra eight Points for hash marks either end
                    pt2 = lineutility.ExtendLineDouble(pLinePoints[vblCounter - 8], pLinePoints[vblCounter - 7], dMBR / 2);
                    pt3 = new POINT2(pLinePoints[vblCounter - 7]);
                    pt1 = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], dMBR / 2);

                    if (dMBR / 10 > maxLength) {
                        dMBR = 10 * maxLength;
                    }
                    if (dMBR / 10 < minLength) {
                        dMBR = 10 * minLength;
                    }
                    if (dMBR > 200) {
                        dMBR = 200;
                    }

                    lineutility.GetArrowHead4Double(pt2, pt3, (int) dMBR / 10, (int) dMBR / 5,
                            pArrowPoints, 0);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 6 + k] = new POINT2(pArrowPoints[k]);
                    }

                    lineutility.GetArrowHead4Double(pt1, pt0, (int) dMBR / 10, (int) dMBR / 5,
                            pArrowPoints, 0);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 3 + k] = new POINT2(pArrowPoints[k]);
                    }
                    //pLinePoints[1].style=5;
                    pLinePoints[vblSaveCounter - 1].style = 5;
                    acCounter = vblCounter;
                    break;
                case TacticalLines.DIRATKAIR:
                    //added section for click-drag mode
                    //reverse the points
                    lineutility.ReversePointsDouble2(
                            pLinePoints,
                            vblSaveCounter);

                    for (k = vblSaveCounter - 1; k > 0; k--) {
                        d += lineutility.CalcDistanceDouble(pLinePoints[k], pLinePoints[k - 1]);
                        if (d > 60) {
                            break;
                        }
                    }
                    if (d > 60) {
                        middleSegment = k;
                        pt2 = pLinePoints[middleSegment];
                        if (middleSegment >= 1) {
                            pt3 = pLinePoints[middleSegment - 1];
                        }
                    } else {
                        if (vblSaveCounter <= 3) {
                            middleSegment = 1;
                        } else {
                            middleSegment = 2;
                        }

                        pt2 = pLinePoints[middleSegment];
                        if (middleSegment >= 1) {
                            pt3 = pLinePoints[middleSegment - 1];
                        }
                    }

                    pt0 = new POINT2(pLinePoints[0]);

                    if (dMBR / 20 > maxLength) {
                        dMBR = 20 * maxLength;
                    }
                    if (dMBR / 20 < minLength) {
                        dMBR = 20 * minLength;
                    }
                    if (dMBR < 150) {
                        dMBR = 150;
                    }

                    if (dMBR > 250) {
                        dMBR = 250;
                    }

                    lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 11], pLinePoints[vblCounter - 10], (int) dMBR / 20, (int) dMBR / 20,
                            pArrowPoints, 0);

                    for (j = 0; j < 3; j++) {
                        pLinePoints[vblCounter - 9 + j] = new POINT2(pArrowPoints[j]);
                    }

                    pLinePoints[vblCounter - 6].x = (pLinePoints[vblCounter - 11].x + pLinePoints[vblCounter - 10].x) / 2;
                    pLinePoints[vblCounter - 6].y = (pLinePoints[vblCounter - 11].y + pLinePoints[vblCounter - 10].y) / 2;
                    pt0 = new POINT2(pLinePoints[vblCounter - 6]);
                    lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 11], pt0, (int) dMBR / 20, (int) dMBR / 20,
                            pArrowPoints, 9);

                    if (middleSegment >= 1) {
                        pt0 = lineutility.MidPointDouble(pt2, pt3, 0);
                        lineutility.GetArrowHead4Double(pt3, pt0, (int) dMBR / 20, (int) dMBR / 20,
                                pArrowPoints, 9);
                    }

                    for (j = 0; j < 3; j++) {
                        pLinePoints[vblCounter - 6 + j] = new POINT2(pArrowPoints[j]);
                    }

                    lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 10], pt0, (int) dMBR / 20, (int) dMBR / 20,
                            pArrowPoints, 9);
                    if (middleSegment >= 1) {
                        pt0 = lineutility.MidPointDouble(pt2, pt3, 0);
                        lineutility.GetArrowHead4Double(pt2, pt0, (int) dMBR / 20, (int) dMBR / 20,
                                pArrowPoints, 9);
                    }
                    for (j = 0; j < 3; j++) {
                        pLinePoints[vblCounter - 3 + j] = new POINT2(pArrowPoints[j]);
                    }

                    //this section was added to remove fill from the bow tie feature
                    ArrayList<POINT2> airPts = new ArrayList();
                    pLinePoints[middleSegment - 1].style = 5;
                    //pLinePoints[middleSegment].style=14;
                    if (vblSaveCounter == 2) {
                        pLinePoints[1].style = 5;
                    }

                    for (j = 0; j < vblCounter; j++) {
                        airPts.add(new POINT2(pLinePoints[j]));
                    }

                    midpt = lineutility.MidPointDouble(pLinePoints[middleSegment - 1], pLinePoints[middleSegment], 0);
                    pt0 = lineutility.ExtendAlongLineDouble(midpt, pLinePoints[middleSegment], dMBR / 20, 0);
                    airPts.add(pt0);
                    pt1 = new POINT2(pLinePoints[middleSegment]);
                    pt1.style = 5;
                    airPts.add(pt1);

                    pt0 = lineutility.ExtendAlongLineDouble(midpt, pLinePoints[middleSegment - 1], dMBR / 20, 0);
                    airPts.add(pt0);
                    pt1 = new POINT2(pLinePoints[middleSegment - 1]);
                    pt1.style = 5;
                    airPts.add(pt1);

                    //re-dimension pLinePoints so that it can hold the
                    //the additional points required by the shortened middle segment
                    //which has the bow tie feature
                    vblCounter = airPts.size();
                    pLinePoints = new POINT2[airPts.size()];
                    for (j = 0; j < airPts.size(); j++) {
                        pLinePoints[j] = new POINT2(airPts.get(j));
                    }
                    //end section

                    acCounter = vblCounter;
                    //FillPoints(pLinePoints,vblCounter,points);
                    break;
                case TacticalLines.PDF:
                    //reverse pt0 and pt1   8-1-08
                    pt0 = new POINT2(pLinePoints[1]);
                    pt1 = new POINT2(pLinePoints[0]);
                    pLinePoints[0] = new POINT2(pt0);
                    pLinePoints[1] = new POINT2(pt1);
                    //end section
                    pts2 = new POINT2[3];
                    pts2[0] = new POINT2(pt0);
                    pts2[1] = new POINT2(pt1);
                    pts2[2] = new POINT2(pt2);
                    lineutility.GetPixelsMin(pts2, 3,
                            offsetX,
                            offsetY);
                    if (offsetX.value[0] < 0) {
                        offsetX.value[0] = offsetX.value[0] - 100;
                    } else {
                        offsetX.value[0] = 0;
                    }

                    pLinePoints[2].style = 5;

                    if (dMBR / 20 > maxLength) {
                        dMBR = 20 * maxLength;
                    }
                    if (dMBR < minLength) {
                        dMBR = 20 * minLength;
                    }
                    if (dMBR > 250) {
                        dMBR = 250;
                    }

                    pt2 = lineutility.ExtendLineDouble(pt0, pt1, -dMBR / 10);
                    bolVertical = lineutility.CalcTrueSlopeDouble(pt0, pt1, m);
                    if (bolVertical != 0 && m.value[0] != 0) {
                        b = pt2.y + (1 / m.value[0]) * pt2.x;
                        b1 = (-1 / m.value[0]) * offsetX.value[0] + b;
                        ptYIntercept.x = offsetX.value[0];
                        ptYIntercept.y = b1;
                        pLinePoints[3] = lineutility.ExtendLineDouble(ptYIntercept, pt2, -2);
                        pLinePoints[3].style = 0;
                        pLinePoints[4] = lineutility.ExtendLineDouble(ptYIntercept, pt2, 2);
                        pLinePoints[4].style = 0;
                    }
                    if (bolVertical != 0 && m.value[0] == 0) {
                        pLinePoints[3] = new POINT2(pt2);
                        pLinePoints[3].y = pt2.y - 2;
                        pLinePoints[3].style = 0;
                        pLinePoints[4] = new POINT2(pt2);
                        pLinePoints[4].y = pt2.y + 2;
                        pLinePoints[4].style = 0;
                    }
                    if (bolVertical == 0) {
                        pLinePoints[3] = new POINT2(pt2);
                        pLinePoints[3].x = pt2.x - 2;
                        pLinePoints[3].style = 0;
                        pLinePoints[4] = new POINT2(pt2);
                        pLinePoints[4].x = pt2.x + 2;
                        pLinePoints[4].style = 0;
                    }

                    pt2 = lineutility.ExtendLineDouble(pt1, pt0, -dMBR / 10);
                    if (bolVertical != 0 && m.value[0] != 0) {
                        b = pt2.y + (1 / m.value[0]) * pt2.x;
                        //get the Y intercept at x=offsetX
                        b1 = (-1 / m.value[0]) * offsetX.value[0] + b;
                        ptYIntercept.x = offsetX.value[0];
                        ptYIntercept.y = b1;
                        pLinePoints[5] = lineutility.ExtendLineDouble(ptYIntercept, pt2, 2);
                        pLinePoints[5].style = 0;
                        pLinePoints[6] = lineutility.ExtendLineDouble(ptYIntercept, pt2, -2);
                    }
                    if (bolVertical != 0 && m.value[0] == 0) {
                        pLinePoints[5] = new POINT2(pt2);
                        pLinePoints[5].y = pt2.y + 2;
                        pLinePoints[5].style = 0;
                        pLinePoints[6] = new POINT2(pt2);
                        pLinePoints[6].y = pt2.y - 2;
                    }
                    if (bolVertical == 0) {
                        pLinePoints[5] = new POINT2(pt2);
                        pLinePoints[5].x = pt2.x + 2;
                        pLinePoints[5].style = 0;
                        pLinePoints[6] = new POINT2(pt2);
                        pLinePoints[6].x = pt2.x - 2;
                    }

                    pLinePoints[6].style = 0;
                    pLinePoints[7] = new POINT2(pLinePoints[3]);
                    pLinePoints[7].style = 5;
                    lineutility.GetArrowHead4Double(pLinePoints[1], pLinePoints[0], (int) dMBR / 20, (int) dMBR / 20, pArrowPoints, 0);
                    for (j = 0; j < 3; j++) {
                        pLinePoints[8 + j] = new POINT2(pArrowPoints[j]);
                    }
                    lineutility.GetArrowHead4Double(pLinePoints[1], pLinePoints[2], (int) dMBR / 20, (int) dMBR / 20, pArrowPoints, 0);
                    for (j = 0; j < 3; j++) {
                        pLinePoints[11 + j] = new POINT2(pArrowPoints[j]);
                        pLinePoints[11 + j].style = 0;
                    }
                    acCounter = 14;
                    break;
                case TacticalLines.DIRATKSPT:
                    //reverse the points
                    lineutility.ReversePointsDouble2(
                            pLinePoints,
                            vblSaveCounter);
                    if (dMBR / 20 > maxLength) {
                        dMBR = 20 * maxLength;
                    }
                    if (dMBR / 20 < minLength) {
                        dMBR = 20 * minLength;
                    }
                    if (client.startsWith("cpof")) {
                        if (dMBR < 250) {
                            dMBR = 250;
                        }
                    } else {
                        if (dMBR < 150) {
                            dMBR = 150;
                        }
                    }
                    if (dMBR > 500) {
                        dMBR = 500;
                    }

                    lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 5], pLinePoints[vblCounter - 4], (int) dMBR / 20, (int) dMBR / 20, pArrowPoints, 0);
                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - k - 1] = new POINT2(pArrowPoints[k]);
                    }
                    acCounter = vblCounter;
                    break;
                case TacticalLines.ABATIS:
                    //must use an x offset for ptYintercept because of extending from it
                    pts2 = new POINT2[2];
                    pts2[0] = new POINT2(pt0);
                    pts2[1] = new POINT2(pt1);
                    lineutility.GetPixelsMin(pts2, 2,
                            offsetX,
                            offsetY);
                    if (offsetX.value[0] <= 0) {
                        offsetX.value[0] = offsetX.value[0] - 100;
                    } else {
                        offsetX.value[0] = 0;
                    }
                    if (dMBR > 300) {
                        dMBR = 300;
                    }

                    pLinePoints[0] = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], -dMBR / 10);
                    bolVertical = lineutility.CalcTrueSlopeDouble(pt0, pt1, m);
                    midpt.x = (pt0.x + pLinePoints[0].x) / 2;
                    midpt.y = (pt0.y + pLinePoints[0].y) / 2;
                    pLinePoints[vblCounter - 3] = new POINT2(pt0);
                    pLinePoints[vblCounter - 4].style = 5;
                    pLinePoints[vblCounter - 3].style = 0;
                    if (bolVertical != 0 && m.value[0] != 0) {
                        b = midpt.y + (1 / m.value[0]) * midpt.x;	//the line equation
                        //get Y intercept at x=offsetX
                        b1 = (-1 / m.value[0]) * offsetX.value[0] + b;
                        ptYIntercept.x = offsetX.value[0];
                        ptYIntercept.y = b1;
                        pLinePoints[vblCounter - 2] = lineutility.ExtendLineDouble(ptYIntercept, midpt, dMBR / 20);
                        if (pLinePoints[vblCounter - 2].y >= midpt.y) {
                            pLinePoints[vblCounter - 2] = lineutility.ExtendLineDouble(ptYIntercept, midpt, -dMBR / 20);
                        }
                    }
                    if (bolVertical != 0 && m.value[0] == 0) //horizontal line
                    {
                        pLinePoints[vblCounter - 2] = new POINT2(midpt);
                        pLinePoints[vblCounter - 2].y = midpt.y - dMBR / 20;
                    }
                    if (bolVertical == 0) {
                        pLinePoints[vblCounter - 2] = new POINT2(midpt);
                        pLinePoints[vblCounter - 2].x = midpt.x - dMBR / 20;
                    }
                    pLinePoints[vblCounter - 2].style = 0;
                    pLinePoints[vblCounter - 1] = new POINT2(pLinePoints[0]);

                    //put the abatis feature on the front
//                    ArrayList<POINT2>abatisPts=new ArrayList();
//                    for(j=3;j<vblCounter;j++)
//                        abatisPts.add(new POINT2(pLinePoints[j-3]));
//
//                    abatisPts.add(0,new POINT2(pLinePoints[vblCounter-3]));
//                    abatisPts.add(1,new POINT2(pLinePoints[vblCounter-2]));
//                    abatisPts.add(2,new POINT2(pLinePoints[vblCounter-1]));
//
//                    for(j=0;j<abatisPts.size();j++)
//                        pLinePoints[j]=new POINT2(abatisPts.get(j));
                    //end section
                    //FillPoints(pLinePoints,vblCounter,points);
                    acCounter = vblCounter;
                    //FillPoints(pLinePoints,acCounter,points);
                    break;
                case TacticalLines.CLUSTER:
                    //must use an x offset for ptYintercept because of extending from it
                    pts2 = new POINT2[2];

                    //for some reason occulus puts the points on top of one another
                    if (Math.abs(pt0.y - pt1.y) < 1) {
                        pt1.y = pt0.y + 1;
                    }

                    pts2[0] = new POINT2(pt0);
                    pts2[1] = new POINT2(pt1);

                    pts = new POINT2[26];
                    dRadius = lineutility.CalcDistanceDouble(pt0, pt1) / 2;
                    midpt.x = (pt1.x + pt0.x) / 2;
                    midpt.y = (pt1.y + pt0.y) / 2;
                    bolVertical = lineutility.CalcTrueSlopeDouble(pt0, pt1, m);
                    if (bolVertical != 0 && m.value[0] != 0) //not vertical or horizontal
                    {
                        b = midpt.y + (1 / m.value[0]) * midpt.x;	//normal y intercept at x=0
                        //we want to get the y intercept at x=offsetX
                        //b1 = (-1 / m.value[0]) * offsetX.value[0] + b;
                        //ptYIntercept.x = offsetX.value[0];
                        ptYIntercept.x = 0;
                        //ptYIntercept.y = b1;
                        ptYIntercept.y = b;
                        pt2 = lineutility.ExtendLineDouble(ptYIntercept, midpt, dRadius);
                        if (pLinePoints[0].x <= pLinePoints[1].x) {
                            if (pt2.y >= midpt.y) {
                                pt2 = lineutility.ExtendLineDouble(ptYIntercept, midpt, -dRadius);
                            }
                        } else {
                            if (pt2.y <= midpt.y) {
                                pt2 = lineutility.ExtendLineDouble(ptYIntercept, midpt, -dRadius);
                            }
                        }

                    }
                    if (bolVertical != 0 && m.value[0] == 0) //horizontal line
                    {
                        pt2 = midpt;
                        if (pLinePoints[0].x <= pLinePoints[1].x) {
                            pt2.y = midpt.y - dRadius;
                        } else {
                            pt2.y = midpt.y + dRadius;
                        }
                    }
                    if (bolVertical == 0) //vertical line
                    {
                        pt2 = midpt;
                        if (pLinePoints[0].y <= pLinePoints[1].y) {
                            pt2.x = midpt.x + dRadius;
                        } else {
                            pt2.x = midpt.x - dRadius;
                        }
                    }

                    pt1 = lineutility.ExtendLineDouble(midpt, pt2, 100);

                    pts[0] = new POINT2(pt2);
                    pts[1] = new POINT2(pt1);

                    lineutility.ArcArrayDouble(
                            pts,
                            0, dRadius,
                            lineType,
                            null);
                    pLinePoints[0].style = 1;
                    pLinePoints[1].style = 5;
                    for (j = 0; j < 26; j++) {
                        pLinePoints[2 + j] = new POINT2(pts[j]);
                        pLinePoints[2 + j].style = 1;
                    }
                    acCounter = 28;
                    break;
                case TacticalLines.TRIP:
                    dRadius = lineutility.CalcDistanceToLineDouble(pt0, pt1, pt2);
                    bolVertical = lineutility.CalcTrueSlopeDouble(pt0, pt1, m);
                    if (bolVertical != 0 && m.value[0] != 0) {
                        b = pt1.y + 1 / m.value[0] * pt1.x;
                        b1 = pt2.y - m.value[0] * pt2.x;
                        calcPoint0 = lineutility.CalcTrueIntersectDouble2(-1 / m.value[0], b, m.value[0], b1, 1, 1, pt0.x, pt0.y);
                        calcPoint1 = lineutility.ExtendLineDouble(pt0, pt1, dRadius / 2);
                        calcPoint2 = lineutility.ExtendLineDouble(pt0, pt1, dRadius);

                        b = calcPoint1.y + 1 / m.value[0] * calcPoint1.x;
                        calcPoint3 = lineutility.CalcTrueIntersectDouble2(-1 / m.value[0], b, m.value[0], b1, 1, 1, pt0.x, pt0.y);
                        b = calcPoint2.y + 1 / m.value[0] * calcPoint2.x;
                        calcPoint4 = lineutility.CalcTrueIntersectDouble2(-1 / m.value[0], b, m.value[0], b1, 1, 1, pt0.x, pt0.y);
                        midpt = lineutility.MidPointDouble(calcPoint1, calcPoint3, 0);
                        midpt1 = lineutility.MidPointDouble(calcPoint2, calcPoint4, 0);

                        b = pt1.y + 1 / m.value[0] * pt1.x;
                        calcPoint0 = lineutility.CalcTrueIntersectDouble2(-1 / m.value[0], b, m.value[0], b1, 1, 1, pt0.x, pt0.y);
                        calcPoint3 = lineutility.ExtendLineDouble(pt0, pt1, dRadius);
                        d = lineutility.CalcDistanceDouble(calcPoint0, calcPoint3);
                        calcPoint1 = lineutility.ExtendLineDouble(calcPoint0, calcPoint3, -(d - dRadius));
                    }
                    if (bolVertical != 0 && m.value[0] == 0) {
                        calcPoint0.x = pt1.x;
                        calcPoint0.y = pt2.y;
                        calcPoint1 = lineutility.ExtendLineDouble(pt0, pt1, dRadius / 2);
                        //calcPoint2 = lineutility.ExtendLineDouble(pt0, pt1, dRadius);
                        calcPoint2 = pt2;

                        calcPoint3.x = calcPoint0.x + dRadius / 2;
                        calcPoint3.y = calcPoint0.y;
                        calcPoint4.x = pt1.x + dRadius;
                        calcPoint4.y = pt2.y;
                        midpt = lineutility.MidPointDouble(calcPoint1, calcPoint3, 0);
                        midpt1 = lineutility.MidPointDouble(calcPoint2, calcPoint4, 0);

                        calcPoint3 = lineutility.ExtendLineDouble(pt0, pt1, dRadius);

                        d = lineutility.CalcDistanceDouble(calcPoint0, calcPoint3);
                        calcPoint1 = lineutility.ExtendLineDouble(calcPoint0, calcPoint3, -(d - dRadius));
                    }
                    if (bolVertical == 0) {

                        calcPoint0.x = pt2.x;
                        calcPoint0.y = pt1.y;
                        calcPoint1 = lineutility.ExtendLineDouble(pt0, pt1, dRadius / 2);
                        //calcPoint2 = lineutility.ExtendLineDouble(pt0, pt1, dRadius);
                        calcPoint2 = pt2;

                        calcPoint3.y = calcPoint0.y + dRadius / 2;
                        calcPoint3.x = calcPoint0.x;
                        calcPoint4.y = pt1.y + dRadius;
                        calcPoint4.x = pt2.x;
                        midpt = lineutility.MidPointDouble(calcPoint1, calcPoint3, 0);
                        midpt1 = lineutility.MidPointDouble(calcPoint2, calcPoint4, 0);

                        calcPoint3 = lineutility.ExtendLineDouble(pt0, pt1, dRadius);

                        d = lineutility.CalcDistanceDouble(calcPoint0, calcPoint3);
                        calcPoint1 = lineutility.ExtendLineDouble(calcPoint0, calcPoint3, -(d - dRadius));
                    }

                    arcPts[0] = new POINT2(calcPoint1);
                    arcPts[1] = new POINT2(calcPoint3);
                    lineutility.ArcArrayDouble(
                            arcPts,
                            0, dRadius,
                            lineType,
                            null);

                    pLinePoints[0].style = 5;
                    pLinePoints[1].style = 5;
                    for (k = 0; k < 26; k++) {
                        pLinePoints[k] = new POINT2(arcPts[k]);
                    }
                    for (k = 25; k < vblCounter; k++) {
                        pLinePoints[k].style = 5;
                    }
                    pLinePoints[26] = new POINT2(pt1);
                    dRadius = lineutility.CalcDistanceDouble(pt1, pt0);

                    midpt = lineutility.ExtendLine2Double(pt1, pt0, -dRadius / 2 - 7, 0);

                    pLinePoints[27] = new POINT2(midpt);
                    pLinePoints[27].style = 0;
                    midpt = lineutility.ExtendLine2Double(pt1, pt0, -dRadius / 2 + 7, 0);
                    pLinePoints[28] = new POINT2(midpt);
                    pLinePoints[29] = new POINT2(pt0);
                    pLinePoints[29].style = 5;
                    lineutility.GetArrowHead4Double(pt1, pt0, 15, 15, pArrowPoints, 0);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[30 + k] = new POINT2(pArrowPoints[k]);
                    }
                    for (k = 0; k < 3; k++) {
                        pLinePoints[30 + k].style = 5;
                    }

                    midpt = lineutility.MidPointDouble(pt0, pt1, 0);
                    d = lineutility.CalcDistanceDouble(pt1, calcPoint0);

                    //diagnostic for CS
                    //pLinePoints[33] = lineutility.ExtendTrueLinePerpDouble(pt0, pt1, midpt, d, 0);
                    //pLinePoints[34] = lineutility.ExtendTrueLinePerpDouble(pt0, pt1, midpt, -d, 5);
                    pLinePoints[33] = pt2;
                    pt3 = lineutility.PointRelativeToLine(pt0, pt1, pt0, pt2);
                    d = lineutility.CalcDistanceDouble(pt3, pt2);
                    pt4 = lineutility.ExtendAlongLineDouble(pt0, pt1, d);
                    d = lineutility.CalcDistanceDouble(pt2, pt4);
                    pLinePoints[34] = lineutility.ExtendLineDouble(pt2, pt4, d);
                    //end section

                    acCounter = 35;
                    break;
                case TacticalLines.FOLLA:
                    d = lineutility.CalcDistanceDouble(pLinePoints[0], pLinePoints[1]);
                    if (client.startsWith("cpof")) {
                        d2 = 20;
                    } else {
                        d2 = 30;
                    }

                    if (d < d2) {
                        lineType = TacticalLines.DIRATKSPT;
                        GetLineArray2Double(TacticalLines.DIRATKSPT, pLinePoints, 5, 2, shapes, clipBounds, rev, converter);
                        break;
                    }

                    //reverse the points
                    lineutility.ReversePointsDouble2(pLinePoints, vblSaveCounter);

                    if (dMBR / 10 > maxLength) {
                        dMBR = 10 * maxLength;
                    }
                    if (dMBR / 10 < minLength) {
                        dMBR = 10 * minLength;
                    }
                    if (dMBR > 150) {
                        dMBR = 150;
                    }

                    pLinePoints[0] = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], -2 * dMBR / 10);

                    for (k = 0; k < vblCounter - 14; k++) {
                        pLinePoints[k].style = 18;
                    }
                    pLinePoints[vblCounter - 15].style = 5;

                    pt0 = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], 5 * dMBR / 10);

                    lineutility.GetArrowHead4Double(pt0, pLinePoints[0], (int) dMBR / 10, (int) dMBR / 10, pArrowPoints, 0);
                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 14 + k] = new POINT2(pArrowPoints[k]);
                    }

                    pt3 = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], dMBR / 10);

                    lineutility.GetArrowHead4Double(pt0, pt3, (int) dMBR / 10, (int) dMBR / 10, pArrowPoints, 0);
                    pLinePoints[vblCounter - 12].style = 0;
                    pLinePoints[vblCounter - 11] = new POINT2(pArrowPoints[2]);
                    pLinePoints[vblCounter - 11].style = 0;
                    pLinePoints[vblCounter - 10] = new POINT2(pArrowPoints[0]);
                    pLinePoints[vblCounter - 10].style = 0;
                    pLinePoints[vblCounter - 9] = new POINT2(pLinePoints[vblCounter - 14]);
                    pLinePoints[vblCounter - 9].style = 5;

                    lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 16], pLinePoints[vblCounter - 15], (int) dMBR / 10, (int) dMBR / 10, pArrowPoints, 0);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 8 + k] = new POINT2(pArrowPoints[k]);
                    }
                    pLinePoints[vblCounter - 6].style = 0;

                    //diagnostic to make first point tip of arrowhead    6-14-12
                    //pt3 = lineutility.ExtendLineDouble(pLinePoints[vblCounter - 16], pLinePoints[vblCounter - 15], 0.75 * dMBR / 10);
                    pt3 = lineutility.ExtendLineDouble(pLinePoints[vblCounter - 16], pLinePoints[vblCounter - 15], -0.75 * dMBR / 10);
                    pLinePoints[1] = pt3;
                    pLinePoints[1].style = 5;
                    //lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 16], pt3, (int) (1.25 * dMBR / 10), (int) (1.25 * dMBR / 10), pArrowPoints, 0);
                    lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 16], pt3, (int) (dMBR / 10), (int) (dMBR / 10), pArrowPoints, 0);
                    //end section

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 5 + k] = new POINT2(pArrowPoints[2 - k]);
                    }
                    pLinePoints[vblCounter - 5].style = 0;

                    pLinePoints[vblCounter - 2] = new POINT2(pLinePoints[vblCounter - 8]);
                    pLinePoints[vblCounter - 2].style = 5;
                    pLinePoints[vblCounter - 1] = new POINT2(pLinePoints[vblCounter - 7]);
                    acCounter = 16;
                    break;
                case TacticalLines.FOLSP:
                    if (client.startsWith("cpof")) {
                        d2 = 25;
                    } else {
                        d2 = 25;
                    }

                    double folspDist = 0;
                    folspDist = lineutility.CalcDistanceDouble(pLinePoints[0], pLinePoints[1]);
                    if (folspDist < d2) //was 10
                    {
                        lineType = TacticalLines.DIRATKSPT;
                        GetLineArray2Double(lineType, pLinePoints, 5, 2, shapes, clipBounds, rev, converter);
                        break;
                    }
//                    else if(folspDist<d2)//was 25
//                    {								
//                        lineType=TacticalLines.FOLLA;
//                        GetLineArray2Double(lineType, pLinePoints,16,2,shapes,clipBounds,rev);
//                        for(k=0;k<pLinePoints.length;k++)
//                                if(pLinePoints[k].style==18)
//                                        pLinePoints[k].style=0;
//
//                        //lineType=TacticalLines.FOLSP;
//                        //acCounter=16;
//                        break;	
//                    }
                    //end section

                    //reverse the points
                    lineutility.ReversePointsDouble2(
                            pLinePoints,
                            vblSaveCounter);

                    if (dMBR / 10 > maxLength) {
                        dMBR = 10 * maxLength;
                    }
                    if (dMBR / 10 < minLength) {
                        dMBR = 10 * minLength;
                    }
                    if (dMBR > 250) {
                        dMBR = 250;
                    }

                    if (client.startsWith("cpof")) {
                        if (folspDist < 25) {
                            dMBR = 125;
                        }
                        if (folspDist < 75) {
                            dMBR = 150;
                        }
                        if (folspDist < 100) {
                            dMBR = 175;
                        }
                        if (folspDist < 125) {
                            dMBR = 200;
                        }
                    } else {
                        dMBR *= 1.5;
                    }
                    //make tail larger 6-10-11 m. Deutch
                    //pLinePoints[0] = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], -dMBR / 10);
                    pLinePoints[0] = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], -dMBR / 8.75);

                    pLinePoints[vblCounter - 15].style = 5;
                    pt0 = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], dMBR / 4);

                    lineutility.GetArrowHead4Double(pt0, pLinePoints[0], (int) dMBR / 20, (int) dMBR / 20,
                            pArrowPoints, 0);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 14 + k] = new POINT2(pArrowPoints[k]);
                    }

                    pLinePoints[vblCounter - 12].style = 0;

                    //make tail larger 6-10-11 m. Deutch
                    //pt3 = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], dMBR / 20);
                    pt3 = lineutility.ExtendLineDouble(pLinePoints[1], pLinePoints[0], dMBR / 15);

                    lineutility.GetArrowHead4Double(pt0, pt3, (int) dMBR / 20, (int) dMBR / 20, pArrowPoints, 0);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 11 + k] = new POINT2(pArrowPoints[2 - k]);
                        pLinePoints[vblCounter - 11 + k].style = 0;
                    }
                    pLinePoints[vblCounter - 8] = new POINT2(pLinePoints[vblCounter - 14]);
                    pLinePoints[vblCounter - 8].style = 5;

                    lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 16], pLinePoints[vblCounter - 15], (int) dMBR / 20, (int) dMBR / 20, pArrowPoints, 9);

                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 7 + k] = new POINT2(pArrowPoints[k]);
                    }
                    for (k = 4; k > 0; k--) {
                        pLinePoints[vblCounter - k].style = 5;
                    }
                    acCounter = 12;
                    break;
                case TacticalLines.FERRY:
                    lLinestyle = 9;
                    if (dMBR / 10 > maxLength) {
                        dMBR = 10 * maxLength;
                    }
                    if (dMBR / 10 < minLength) {
                        dMBR = 10 * minLength;
                    }
                    if (dMBR > 250) {
                        dMBR = 250;
                    }

                    lineutility.GetArrowHead4Double(pLinePoints[vblCounter - 8], pLinePoints[vblCounter - 7], (int) dMBR / 10, (int) dMBR / 10, pArrowPoints, lLinestyle);
                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 6 + k] = new POINT2(pArrowPoints[k]);
                    }
                    lineutility.GetArrowHead4Double(pLinePoints[1], pLinePoints[0], (int) dMBR / 10, (int) dMBR / 10, pArrowPoints, lLinestyle);
                    for (k = 0; k < 3; k++) {
                        pLinePoints[vblCounter - 3 + k] = new POINT2(pArrowPoints[k]);
                    }

                    acCounter = 8;
                    break;
                case TacticalLines.NAVIGATION:
                    pt3 = lineutility.ExtendLine2Double(pt1, pt0, -10, 0);
                    pt4 = lineutility.ExtendLine2Double(pt0, pt1, -10, 0);

                    pt5 = lineutility.ExtendTrueLinePerpDouble(pt0, pt1, pt3, 10, 0);
                    pt6 = lineutility.ExtendTrueLinePerpDouble(pt0, pt1, pt3, -10, 0);
                    pt7 = lineutility.ExtendTrueLinePerpDouble(pt0, pt1, pt4, 10, 0);
                    pt8 = lineutility.ExtendTrueLinePerpDouble(pt0, pt1, pt4, -10, 0);
                    if (pt5.y < pt6.y) {
                        pLinePoints[0] = new POINT2(pt5);
                    } else {
                        pLinePoints[0] = new POINT2(pt6);
                    }
                    if (pt7.y > pt8.y) {
                        pLinePoints[3] = new POINT2(pt7);
                    } else {
                        pLinePoints[3] = new POINT2(pt8);
                    }
                    pLinePoints[1] = new POINT2(pt0);
                    pLinePoints[2] = new POINT2(pt1);
                    acCounter = 4;
                    break;
                case TacticalLines.FORTL:
                    acCounter = GetFORTLPointsDouble(pLinePoints, lineType, vblSaveCounter);
                    break;
//                case TacticalLines.HOLD:
//                case TacticalLines.BRDGHD:
//                    lineutility.ReorderPoints(pLinePoints);
//                    acCounter=pLinePoints.length;
//                    FillPoints(pLinePoints,acCounter,points);
//                    break;
                case TacticalLines.CANALIZE:
                    acCounter = DISMSupport.GetDISMCanalizeDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.BREACH:
                    acCounter = DISMSupport.GetDISMBreachDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.SCREEN:
                case TacticalLines.GUARD:
                case TacticalLines.COVER:
                    acCounter = DISMSupport.GetDISMCoverDouble(pLinePoints, lineType);
                    //acCounter=DISMSupport.GetDISMCoverDoubleRevC(pLinePoints,lineType,vblSaveCounter);
                    break;
                case TacticalLines.SCREEN_REVC: //works for 3 or 4 points
                case TacticalLines.GUARD_REVC:
                case TacticalLines.COVER_REVC:
                    acCounter = DISMSupport.GetDISMCoverDoubleRevC(pLinePoints, lineType, vblSaveCounter);
                    break;
                case TacticalLines.SARA:
                    acCounter = DISMSupport.GetDISMCoverDouble(pLinePoints, lineType);
                    //reorder pLinePoints
                    POINT2[] saraPts = new POINT2[16];
                    for (j = 0; j < 4; j++) {
                        saraPts[j] = pLinePoints[j];  //0-3
                    }
                    for (j = 4; j < 8; j++) {
                        saraPts[j] = pLinePoints[j + 4];    //8-11
                    }
                    for (j = 8; j < 12; j++) {
                        saraPts[j] = pLinePoints[j - 4];    //4-7
                    }
                    for (j = 12; j < 16; j++) {
                        saraPts[j] = pLinePoints[j];  //12-15
                    }
                    pLinePoints = saraPts;
                    //acCounter=14;
                    break;
                case TacticalLines.DISRUPT:
                    acCounter = DISMSupport.GetDISMDisruptDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.CONTAIN:
                    acCounter = DISMSupport.GetDISMContainDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.PENETRATE:
                    DISMSupport.GetDISMPenetrateDouble(pLinePoints, lineType);
                    acCounter = 7;
                    break;
                case TacticalLines.MNFLDBLK:
                    DISMSupport.GetDISMBlockDouble2(
                            pLinePoints,
                            lineType);
                    acCounter = 4;
                    break;
                case TacticalLines.BLOCK:
                    DISMSupport.GetDISMBlockDouble2(
                            pLinePoints,
                            lineType);
                    acCounter = 4;
                    break;
                case TacticalLines.LINTGT:
                case TacticalLines.LINTGTS:
                case TacticalLines.FPF:
                    acCounter = DISMSupport.GetDISMLinearTargetDouble(pLinePoints, lineType, vblCounter);
                    break;
                case TacticalLines.GAP:
                case TacticalLines.ASLTXING:
                case TacticalLines.BRIDGE:    //change 1
                    DISMSupport.GetDISMGapDouble(
                            pLinePoints,
                            lineType);
                    acCounter = 12;
                    break;
                case TacticalLines.MNFLDDIS:
                    acCounter = DISMSupport.GetDISMMinefieldDisruptDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.SPTBYFIRE:
                    acCounter = DISMSupport.GetDISMSupportByFireDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.ATKBYFIRE:
                    acCounter = DISMSupport.GetDISMATKBYFIREDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.BYIMP:
                    acCounter = DISMSupport.GetDISMByImpDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.CLEAR:
                    acCounter = DISMSupport.GetDISMClearDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.BYDIF:
                    acCounter = DISMSupport.GetDISMByDifDouble(pLinePoints, lineType, clipBounds);
                    break;
                case TacticalLines.SEIZE:
                    acCounter = DISMSupport.GetDISMSeizeDouble(pLinePoints, lineType, 0);
                    break;
                case TacticalLines.SEIZE_REVC:  //works for 3 or 4 points
                    double radius = 0;
                    if (rev == RendererSettings.Symbology_2525C) {
                        radius = lineutility.CalcDistanceDouble(pLinePoints[0], pLinePoints[1]);
                        pLinePoints[1] = new POINT2(pLinePoints[3]);
                        pLinePoints[2] = new POINT2(pLinePoints[2]);
                    }
                    acCounter = DISMSupport.GetDISMSeizeDouble(pLinePoints, lineType, radius);
                    break;
                case TacticalLines.FIX:
                case TacticalLines.MNFLDFIX:
                    acCounter = DISMSupport.GetDISMFixDouble(pLinePoints, lineType, clipBounds);
                    break;
                case TacticalLines.RIP:
                    acCounter = DISMSupport.GetDISMRIPDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.DELAY:
                case TacticalLines.WITHDRAW:
                case TacticalLines.WDRAWUP:
                case TacticalLines.RETIRE:
                    //reverse the points
                    //lineutility.ReversePointsDouble2(ref pLinePoints, vblSaveCounter);
                    acCounter = DISMSupport.GetDelayGraphicEtcDouble(pLinePoints);
                    break;
                case TacticalLines.EASY:
                    acCounter = DISMSupport.GetDISMEasyDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.DECEIVE:
                    DISMSupport.GetDISMDeceiveDouble(pLinePoints);
                    acCounter = 4;
                    break;
                case TacticalLines.BYPASS:
                    acCounter = DISMSupport.GetDISMBypassDouble(pLinePoints, lineType);
                    break;
                case TacticalLines.PAA_RECTANGULAR:
                    DISMSupport.GetDISMPAADouble(pLinePoints, lineType);
                    acCounter = 5;
                    break;
                case TacticalLines.AMBUSH:
                    acCounter = DISMSupport.AmbushPointsDouble(pLinePoints);
                    break;
                case TacticalLines.FLOT:
                    acCounter = flot.GetFlotDouble(pLinePoints, vblSaveCounter);
                    break;
                default:
                    acCounter = vblSaveCounter;
                    break;
            }
            //diagnostic
            //lineutility.WriteFile(Double.toString(pLinePoints[0].x)+" to "+Double.toString(pLinePoints[1].x));
            //end diagnostic
            //Fill points
            //and/or return points if shapes is null
            switch (lineType) {
                case TacticalLines.BOUNDARY:
                    FillPoints(pLinePoints, acCounter, points);
                    return points;
                case TacticalLines.CONTAIN:
                case TacticalLines.BLOCK:
                //case TacticalLines.DMAF:  //points are already filled for DMAF
                case TacticalLines.COVER:
                case TacticalLines.SCREEN:  //note: screen, cover, guard are getting their modifiers before the call to getlinearray
                case TacticalLines.GUARD:
                case TacticalLines.COVER_REVC:
                case TacticalLines.SCREEN_REVC:
                case TacticalLines.GUARD_REVC:
                //case TacticalLines.DUMMY: //commented 5-3-10
                case TacticalLines.PAA_RECTANGULAR:
                case TacticalLines.FOLSP:
                case TacticalLines.FOLLA:
                //add these for rev c   3-12-12
                case TacticalLines.BREACH:
                case TacticalLines.BYPASS:
                case TacticalLines.CANALIZE:
                case TacticalLines.CLEAR:
                case TacticalLines.DISRUPT:
                case TacticalLines.FIX:
                case TacticalLines.ISOLATE:
                case TacticalLines.OCCUPY:
                case TacticalLines.PENETRATE:
                case TacticalLines.RETAIN:
                case TacticalLines.SECURE:
                case TacticalLines.SEIZE:
                case TacticalLines.SEIZE_REVC:
                case TacticalLines.BS_RECTANGLE:
                case TacticalLines.BBS_RECTANGLE:
                //add these
                case TacticalLines.AIRFIELD:
                case TacticalLines.DMA:
                case TacticalLines.DUMMY:
                case TacticalLines.CORDONKNOCK:
                case TacticalLines.CORDONSEARCH:
                case TacticalLines.MSDZ:
                case TacticalLines.CONVOY:
                case TacticalLines.HCONVOY:
                case TacticalLines.MFLANE:
                case TacticalLines.DIRATKAIR:
                case TacticalLines.ABATIS:
                    FillPoints(pLinePoints, acCounter, points);
                    break;
                default:
                    //if shapes is null then it is a non-CPOF client, dependent upon pixels
                    //instead of shapes
                    if (shapes == null) {
                        FillPoints(pLinePoints, acCounter, points);
                        return points;
                    }
                    break;
            }

            //the shapes require pLinePoints
            //if the shapes are null then it is a non-CPOF client,
            if (shapes == null) {
                return points;
            }

            Shape2 shape = null;
            Shape gp = null;
            Shape2 redShape = null, blueShape = null, paleBlueShape = null, whiteShape = null;
            Shape2 redFillShape = null, blueFillShape = null, blackShape = null;
            BasicStroke blueStroke, paleBlueStroke;
            Area blueArea = null;
            Area paleBlueArea = null;
            Area whiteArea = null;
            boolean beginLine = true;
            Polygon poly = null;

            //a loop for the outline shapes
            switch (lineType) {
                case TacticalLines.BBS_AREA:
                case TacticalLines.BBS_RECTANGLE:
                case TacticalLines.PBS_ELLIPSE: //add these two
                case TacticalLines.PBS_CIRCLE:
//                    for(j=0;j<vblSaveCounter-1;j++)
//                    {
//                        shape=new Shape2(Shape2.SHAPE_TYPE_FILL);
//                        shape.moveTo(pOriginalLinePoints[j]);
//                        shape.lineTo(pLinePoints[j]);
//                        shape.lineTo(pLinePoints[j+1]);
//                        shape.lineTo(pOriginalLinePoints[j+1]);
//                        shape.lineTo(pOriginalLinePoints[j]);
//                        shapes.add(shape);
//                    }
//                    
//                    //shapes.add(shape);
                    shape = new Shape2(Shape2.SHAPE_TYPE_FILL);
                    shape.moveTo(pLinePoints[0]);
                    for (j = 0; j < vblSaveCounter; j++) {
                        shape.lineTo(pLinePoints[j]);
                    }
                    shapes.add(shape);

                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.moveTo(pOriginalLinePoints[0]);
                    for (j = 1; j < vblSaveCounter; j++) {
                        shape.lineTo(pOriginalLinePoints[j]);
                    }
                    shapes.add(shape);

                    break;
                case TacticalLines.DIRATKGND:
                    //create two shapes. the first shape is for the line
                    //the second shape is for the arrow
                    //renderer will know to use a skinny stroke for the arrow shape

                    //the line shape
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    //shape.moveTo(pLinePoints[0].x,pLinePoints[0].y);
                    shape.moveTo(pLinePoints[0]);
                    for (j = 0; j < acCounter - 10; j++) {
                        //shape.lineTo(pLinePoints[j].x,pLinePoints[j].y);
                        shape.lineTo(pLinePoints[j]);
                    }

                    shapes.add(shape);

                    //the arrow shape
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    //shape.moveTo(pLinePoints[acCounter-10].x,pLinePoints[acCounter-10].y);
                    shape.moveTo(pLinePoints[acCounter - 10]);

                    for (j = 9; j > 0; j--) {
                        if (pLinePoints[acCounter - j - 1].style == 5) {
                            //shape.moveTo(pLinePoints[acCounter-j].x,pLinePoints[acCounter-j].y);
                            shape.moveTo(pLinePoints[acCounter - j]);
                        } else {
                            //shape.lineTo(pLinePoints[acCounter-j].x,pLinePoints[acCounter-j].y);
                            shape.lineTo(pLinePoints[acCounter - j]);
                        }
                    }
                    //shape.lineTo(pLinePoints[acCounter-1].x,pLinePoints[acCounter-1].y);

                    shapes.add(shape);
                    break;
//                case TacticalLines.BBS_LINE:
//                    Shape2 outerShape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);//line color
//                    Shape2 innerShape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);//fill color
//                    BasicStroke wideStroke=new BasicStroke(pLinePoints[0].style);
//                    BasicStroke thinStroke=new BasicStroke(pLinePoints[0].style-1);
//                    for(k=0;k<vblSaveCounter;k++)
//                    {
//                        if(k==0)
//                        {
//                            outerShape.moveTo(pLinePoints[k]);
//                            innerShape.moveTo(pLinePoints[k]);
//                        }
//                        else
//                        {
//                            outerShape.lineTo(pLinePoints[k]);
//                            innerShape.lineTo(pLinePoints[k]);
//                        }
//                    }
//                    outerShape.setStroke(wideStroke);
//                    innerShape.setStroke(thinStroke);
//                    shapes.add(outerShape);
//                    shapes.add(innerShape);
//                    break;
                case TacticalLines.DEPTH_AREA:
                    paleBlueShape = new Shape2(Shape2.SHAPE_TYPE_FILL);//use for symbol
                    paleBlueShape.setFillColor(new Color(153, 204, 255));
                    paleBlueStroke = new BasicStroke(28);
                    blueShape = new Shape2(Shape2.SHAPE_TYPE_FILL);//use for symbol
                    blueShape.setFillColor(new Color(30, 144, 255));
                    blueStroke = new BasicStroke(14);
                    whiteShape = new Shape2(Shape2.SHAPE_TYPE_FILL);//use for symbol
                    whiteShape.setFillColor(Color.WHITE);
                    poly = new Polygon();

                    for (k = 0; k < vblSaveCounter; k++) {
                        poly.addPoint((int) pLinePoints[k].x, (int) pLinePoints[k].y);
                        if (k == 0) {
                            whiteShape.moveTo(pLinePoints[k]);
                        } else {
                            whiteShape.lineTo(pLinePoints[k]);
                        }
                    }

                    whiteArea = new Area(poly);

                    blueArea = new Area(blueStroke.createStrokedShape(poly));
                    blueArea.intersect(whiteArea);
                    blueShape.setShape(lineutility.createStrokedShape(blueArea));

                    paleBlueArea = new Area(paleBlueStroke.createStrokedShape(poly));
                    paleBlueArea.intersect(whiteArea);
                    paleBlueShape.setShape(lineutility.createStrokedShape(paleBlueArea));

                    shapes.add(whiteShape);
                    shapes.add(paleBlueShape);
                    shapes.add(blueShape);
                    break;
                case TacticalLines.TRAINING_AREA:
                    redShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);//use for outline
                    redShape.set_Style(1);
                    blueShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);//use for symbol
                    blueShape.set_Style(0);

                    redShape.moveTo(pLinePoints[0]);
                    for (k = 1; k < vblSaveCounter; k++) {
                        redShape.lineTo(pLinePoints[k]);
                    }

                    //blueShape.moveTo(pLinePoints[vblSaveCounter]);
                    beginLine = true;
                    for (k = vblSaveCounter; k < acCounter; k++) {
                        if (pLinePoints[k].style == 0) {
                            if (beginLine) {
                                blueShape.moveTo(pLinePoints[k]);
                                beginLine = false;
                            } else {
                                blueShape.lineTo(pLinePoints[k]);
                            }
                        }
                        if (pLinePoints[k].style == 5) {
                            blueShape.lineTo(pLinePoints[k]);
                            beginLine = true;
                        }
                    }
                    shapes.add(redShape);
                    shapes.add(blueShape);
                    break;
                case TacticalLines.ITD:
                    redShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    redShape.setLineColor(Color.RED);
                    blueShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    blueShape.setLineColor(Color.GREEN);
                    for (k = 0; k < acCounter - 1; k++) {
                        if (pLinePoints[k].style == 19 && pLinePoints[k + 1].style == 5) {
                            redShape.moveTo(pLinePoints[k]);
                            redShape.lineTo(pLinePoints[k + 1]);
                        } else if (pLinePoints[k].style == 25 && pLinePoints[k + 1].style == 5) {
                            blueShape.moveTo(pLinePoints[k]);
                            blueShape.lineTo(pLinePoints[k + 1]);
                        }
                    }
                    shapes.add(redShape);
                    shapes.add(blueShape);
                    break;
                case TacticalLines.SFY:
                    redShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    redShape.setLineColor(Color.RED);
                    blueShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    blueShape.setLineColor(Color.BLUE);
                    //blueFillShape=new Shape2(Shape2.SHAPE_TYPE_FILL);
                    //blueFillShape.setFillColor(Color.BLUE);
                    //redFillShape=new Shape2(Shape2.SHAPE_TYPE_FILL);
                    //redFillShape.setFillColor(Color.RED);
                    //flots and spikes (triangles)
                    for (k = 0; k < acCounter - 1; k++) {
                        if (pLinePoints[k].style == 23) //red flots
                        {
                            redFillShape = new Shape2(Shape2.SHAPE_TYPE_FILL);    //1-3-12
                            redFillShape.setFillColor(Color.RED);
                            redFillShape.moveTo(pLinePoints[k - 9]);
                            //blackShape.moveTo(pLinePoints[k-9]);
                            for (int l = k - 8; l <= k; l++) {
                                redFillShape.lineTo(pLinePoints[l]);
                                //blackShape.lineTo(pLinePoints[l]);
                            }
                            shapes.add(redFillShape);   //1-3-12
                        }
                        if (pLinePoints[k].style == 24)//blue spikes
                        {
                            blueFillShape = new Shape2(Shape2.SHAPE_TYPE_FILL);   //1-3-12
                            blueFillShape.setFillColor(Color.BLUE);
                            blueFillShape.moveTo(pLinePoints[k - 2]);
                            blueFillShape.lineTo(pLinePoints[k - 1]);
                            blueFillShape.lineTo(pLinePoints[k]);
                            shapes.add(blueFillShape);  //1-3-12

                            redShape.moveTo(pLinePoints[k - 2]);
                            redShape.lineTo(pLinePoints[k - 1]);
                            redShape.lineTo(pLinePoints[k]);
                        }
                    }
                    //the corners
                    for (k = 0; k < vblSaveCounter; k++) {
                        if (k == 0) {
                            d = 50;
                            redShape.moveTo(pOriginalLinePoints[0]);
                            d1 = lineutility.CalcDistanceDouble(pOriginalLinePoints[0], pOriginalLinePoints[1]);
                            if (d1 < d) {
                                d = d1;
                            }

                            pt0 = lineutility.ExtendAlongLineDouble(pOriginalLinePoints[0], pOriginalLinePoints[1], d);
                            redShape.lineTo(pt0);
                        } else if (k > 0 && k < vblSaveCounter - 1) {
                            d = 50;
                            d1 = lineutility.CalcDistanceDouble(pOriginalLinePoints[k], pOriginalLinePoints[k - 1]);
                            if (d1 < d) {
                                d = d1;
                            }

                            pt0 = lineutility.ExtendAlongLineDouble(pOriginalLinePoints[k], pOriginalLinePoints[k - 1], d);
                            pt1 = pOriginalLinePoints[k];

                            d = 50;
                            d1 = lineutility.CalcDistanceDouble(pOriginalLinePoints[k], pOriginalLinePoints[k + 1]);
                            if (d1 < d) {
                                d = d1;
                            }

                            pt2 = lineutility.ExtendAlongLineDouble(pOriginalLinePoints[k], pOriginalLinePoints[k + 1], d);
                            redShape.moveTo(pt0);
                            redShape.lineTo(pt1);
                            redShape.lineTo(pt2);
                        } else //last point
                        {
                            d = 50;
                            d1 = lineutility.CalcDistanceDouble(pOriginalLinePoints[vblSaveCounter - 1], pOriginalLinePoints[vblSaveCounter - 2]);
                            if (d1 < d) {
                                d = d1;
                            }

                            redShape.moveTo(pOriginalLinePoints[vblSaveCounter - 1]);
                            pt0 = lineutility.ExtendAlongLineDouble(pOriginalLinePoints[vblSaveCounter - 1], pOriginalLinePoints[vblSaveCounter - 2], d);
                            redShape.lineTo(pt0);
                        }
                    }
                    //red and blue short segments (between the flots)
                    for (k = 0; k < vblCounter - 1; k++) {
                        if (pLinePoints[k].style == 19 && pLinePoints[k + 1].style == 5) {
                            redShape.moveTo(pLinePoints[k]);
                            redShape.lineTo(pLinePoints[k + 1]);
                        } else if (pLinePoints[k].style == 25 && pLinePoints[k + 1].style == 5) {
                            blueShape.moveTo(pLinePoints[k]);
                            blueShape.lineTo(pLinePoints[k + 1]);
                        }
                    }
                    //add the shapes
                    //shapes.add(redFillShape); //1-3-12
                    //shapes.add(blueFillShape);
                    shapes.add(redShape);
                    shapes.add(blueShape);
                    break;
                case TacticalLines.SFG:
                    redShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    redShape.setLineColor(Color.RED);
                    blueShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    blueShape.setLineColor(Color.BLUE);
                    //blueFillShape=new Shape2(Shape2.SHAPE_TYPE_FILL);
                    //blueFillShape.setFillColor(Color.BLUE);
                    //redFillShape=new Shape2(Shape2.SHAPE_TYPE_FILL);
                    //redFillShape.setFillColor(Color.RED);
                    //color=0;
                    for (k = 0; k < acCounter - 1; k++) {
                        if (pLinePoints[k].style == 23) //red flots
                        {
                            redFillShape = new Shape2(Shape2.SHAPE_TYPE_FILL);    //1-3-12
                            redFillShape.setFillColor(Color.RED);
                            redFillShape.moveTo(pLinePoints[k - 9]);
                            for (int l = k - 8; l <= k; l++) {
                                redFillShape.lineTo(pLinePoints[l]);
                            }
                            shapes.add(redFillShape);   //1-3-12
                        }
                        if (pLinePoints[k].style == 24)//blue spikes red outline
                        {
                            blueFillShape = new Shape2(Shape2.SHAPE_TYPE_FILL);   //1-3-12
                            blueFillShape.setFillColor(Color.BLUE);
                            blueFillShape.moveTo(pLinePoints[k - 2]);
                            blueFillShape.lineTo(pLinePoints[k - 1]);
                            blueFillShape.lineTo(pLinePoints[k]);
                            shapes.add(blueFillShape);   //1-3-12

                            redShape.moveTo(pLinePoints[k - 2]);
                            redShape.lineTo(pLinePoints[k - 1]);
                            redShape.lineTo(pLinePoints[k]);
                        }
                    }
                    //the corners
                    for (k = 0; k < vblSaveCounter; k++) {
                        if (k == 0) {
                            d = 50;
                            redShape.moveTo(pOriginalLinePoints[0]);
                            d1 = lineutility.CalcDistanceDouble(pOriginalLinePoints[0], pOriginalLinePoints[1]);
                            if (d1 < d) {
                                d = d1;
                            }

                            pt0 = lineutility.ExtendAlongLineDouble(pOriginalLinePoints[0], pOriginalLinePoints[1], d);
                            redShape.lineTo(pt0);
                        } else if (k > 0 && k < vblSaveCounter - 1) {
                            d = 50;
                            d1 = lineutility.CalcDistanceDouble(pOriginalLinePoints[k], pOriginalLinePoints[k - 1]);
                            if (d1 < d) {
                                d = d1;
                            }

                            pt0 = lineutility.ExtendAlongLineDouble(pOriginalLinePoints[k], pOriginalLinePoints[k - 1], d);
                            pt1 = pOriginalLinePoints[k];

                            d = 50;
                            d1 = lineutility.CalcDistanceDouble(pOriginalLinePoints[k], pOriginalLinePoints[k + 1]);
                            if (d1 < d) {
                                d = d1;
                            }

                            pt2 = lineutility.ExtendAlongLineDouble(pOriginalLinePoints[k], pOriginalLinePoints[k + 1], d);
                            redShape.moveTo(pt0);
                            redShape.lineTo(pt1);
                            redShape.lineTo(pt2);
                        } else //last point
                        {
                            d = 50;
                            d1 = lineutility.CalcDistanceDouble(pOriginalLinePoints[vblSaveCounter - 1], pOriginalLinePoints[vblSaveCounter - 2]);
                            if (d1 < d) {
                                d = d1;
                            }

                            redShape.moveTo(pOriginalLinePoints[vblSaveCounter - 1]);
                            pt0 = lineutility.ExtendAlongLineDouble(pOriginalLinePoints[vblSaveCounter - 1], pOriginalLinePoints[vblSaveCounter - 2], d);
                            redShape.lineTo(pt0);
                        }
                    }
                    //add the shapes
                    //shapes.add(redFillShape); //1-3-12
                    //shapes.add(blueFillShape);
                    shapes.add(redShape);
                    //the dots
                    for (k = 0; k < acCounter; k++) {
                        if (pLinePoints[k].style == 22) {
                            POINT2[] CirclePoints = new POINT2[8];
                            redShape = lineutility.CalcCircleShape(pLinePoints[k], 3, 8, CirclePoints, 9);
                            redShape.setFillColor(Color.RED);
                            if (redShape != null && redShape.getShape() != null) {
                                shapes.add(redShape);
                            }
                        }
                        if (pLinePoints[k].style == 20) {
                            POINT2[] CirclePoints = new POINT2[8];
                            blueShape = lineutility.CalcCircleShape(pLinePoints[k], 3, 8, CirclePoints, 9);
                            blueShape.setFillColor(Color.BLUE);
                            if (blueShape != null && blueShape.getShape() != null) {
                                shapes.add(blueShape);
                            }
                        }
                    }
                    break;
                case TacticalLines.USF:
                    redShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    redShape.setLineColor(Color.RED);
                    blueShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    blueShape.setLineColor(Color.BLUE);
                    beginLine = true;
                    //int color=0;//red
                    for (k = 0; k < acCounter - 1; k++) {
                        if (pLinePoints[k].style == 19 && pLinePoints[k + 1].style == 5) {
                            redShape.moveTo(pLinePoints[k]);
                            redShape.lineTo(pLinePoints[k + 1]);
                            //color=0;
                        }
                        if (pLinePoints[k].style == 19 && pLinePoints[k + 1].style == 19) {
                            redShape.moveTo(pLinePoints[k]);
                            redShape.lineTo(pLinePoints[k + 1]);
                            //color=0;
                        }
                        if (pLinePoints[k].style == 25 && pLinePoints[k + 1].style == 5) {
                            blueShape.moveTo(pLinePoints[k]);
                            blueShape.lineTo(pLinePoints[k + 1]);
                            //color=1;
                        }
                        if (pLinePoints[k].style == 25 && pLinePoints[k + 1].style == 25) {
                            blueShape.moveTo(pLinePoints[k]);
                            blueShape.lineTo(pLinePoints[k + 1]);
                            //color=1;
                        }
                        if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 5) {
                            redShape.moveTo(pLinePoints[k]);
                            redShape.lineTo(pLinePoints[k + 1]);
                        }

                    }
                    shapes.add(redShape);
                    shapes.add(blueShape);
                    break;
                case TacticalLines.SF:
                    blackShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    blackShape.setLineColor(Color.BLACK);
                    redShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    redShape.setLineColor(Color.RED);
                    blueShape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    blueShape.setLineColor(Color.BLUE);
                    //blueFillShape=new Shape2(Shape2.SHAPE_TYPE_FILL); //12-30-11
                    //blueFillShape.setFillColor(Color.BLUE);
                    //redFillShape=new Shape2(Shape2.SHAPE_TYPE_FILL);
                    //redFillShape.setFillColor(Color.RED);
                    //color=0;
                    for (k = 0; k < acCounter - 1; k++) {
                        if (pLinePoints[k].style == 23) {
                            redFillShape = new Shape2(Shape2.SHAPE_TYPE_FILL);//12-30-11
                            redFillShape.setFillColor(Color.RED);
                            redFillShape.moveTo(pLinePoints[k - 9]);
                            blackShape.moveTo(pLinePoints[k - 9]);
                            for (int l = k - 8; l <= k; l++) {
                                redFillShape.lineTo(pLinePoints[l]);
                                blackShape.lineTo(pLinePoints[l]);
                            }
                            redFillShape.lineTo(pLinePoints[k - 9]);  //12-30-11
                            shapes.add(redFillShape);   //12-30-11
                        }
                        if (pLinePoints[k].style == 24) {
                            blueFillShape = new Shape2(Shape2.SHAPE_TYPE_FILL);   //12-30-11
                            blueFillShape.setFillColor(Color.BLUE);
                            blueFillShape.moveTo(pLinePoints[k - 2]);
                            blueFillShape.lineTo(pLinePoints[k - 1]);
                            blueFillShape.lineTo(pLinePoints[k]);
                            blueFillShape.lineTo(pLinePoints[k - 2]);
                            shapes.add(blueFillShape);   //12-30-11

                            blackShape.moveTo(pLinePoints[k - 2]);
                            blackShape.lineTo(pLinePoints[k - 1]);
                            blackShape.lineTo(pLinePoints[k]);
                        }
                    }
                    //the corners
                    blackShape.moveTo(pOriginalLinePoints[0]);
                    for (k = 1; k < vblSaveCounter; k++) {
                        blackShape.lineTo(pOriginalLinePoints[k]);
                    }

                    //shapes.add(redFillShape); //12-30-11
                    //shapes.add(blueFillShape);
                    shapes.add(redShape);
                    shapes.add(blueShape);
                    shapes.add(blackShape);
                    break;
                case TacticalLines.WFG:
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    for (k = 0; k < acCounter - 1; k++) {
                        if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 5) {
                            shape.moveTo(pLinePoints[k]);
                            shape.lineTo(pLinePoints[k + 1]);
                        }
                    }
                    shapes.add(shape);

                    //the dots
                    for (k = 0; k < acCounter; k++) {
                        if (pLinePoints[k].style == 20) {
                            POINT2[] CirclePoints = new POINT2[8];
                            shape = lineutility.CalcCircleShape(pLinePoints[k], 3, 8, CirclePoints, 9);
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                        }
                    }
                    break;
                case TacticalLines.FOLLA:
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.set_Style(1); //dashed line
                    shape.moveTo(pLinePoints[0]);
                    shape.lineTo(pLinePoints[1]);
                    shapes.add(shape);

                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.set_Style(0); //dashed line
                    //shape.moveTo(pLinePoints[2]);
                    for (j = 2; j < vblCounter; j++) {
                        if (pLinePoints[j - 1].style != 5) {
                            shape.lineTo(pLinePoints[j]);
                        } else {
                            shape.moveTo(pLinePoints[j]);
                        }
                    }
                    shapes.add(shape);
                    break;
                case TacticalLines.CFG:
                    for (k = 0; k < acCounter; k++) {
                        if (pLinePoints[k].style == 20) {
                            POINT2[] CirclePoints = new POINT2[8];
                            shape = lineutility.CalcCircleShape(pLinePoints[k], 3, 8, CirclePoints, 9);
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                            continue;
                        }
                    }
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    //shape.moveTo(pLinePoints[0].x,pLinePoints[0].y);
                    for (k = 0; k < acCounter - 1; k++) {
                        if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 0) {
                            //shape.moveTo(pLinePoints[k].x,pLinePoints[k].y);
                            shape.moveTo(pLinePoints[k]);
                            //shape.lineTo(pLinePoints[k+1].x,pLinePoints[k+1].y);
                            shape.lineTo(pLinePoints[k + 1]);
                        }
                        if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 9) {
                            //shape.moveTo(pLinePoints[k].x,pLinePoints[k].y);
                            shape.moveTo(pLinePoints[k]);
                            //shape.lineTo(pLinePoints[k+1].x,pLinePoints[k+1].y);
                            shape.lineTo(pLinePoints[k + 1]);
                        }

                        if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 5) {
                            d = lineutility.CalcDistanceDouble(pLinePoints[k], pLinePoints[k + 1]);
                            pt0 = lineutility.ExtendAlongLineDouble(pLinePoints[k], pLinePoints[k + 1], d - 5);
                            //shape.moveTo(pLinePoints[k].x,pLinePoints[k].y);
                            shape.moveTo(pLinePoints[k]);
                            //shape.lineTo(pt0.x,pt0.y);
                            shape.lineTo(pt0);
                        }

                        if (pLinePoints[k].style == 0 && k == acCounter - 2) {
                            //shape.moveTo(pLinePoints[k].x,pLinePoints[k].y);
                            shape.moveTo(pLinePoints[k]);
                            //shape.lineTo(pLinePoints[k+1].x,pLinePoints[k+1].y);
                            shape.lineTo(pLinePoints[k + 1]);
                        }
                    }
                    shapes.add(shape);
                    break;
                case TacticalLines.PIPE:
                    for (k = 0; k < acCounter; k++) {
                        if (pLinePoints[k].style == 20) {
                            POINT2[] CirclePoints = new POINT2[8];
                            shape = lineutility.CalcCircleShape(pLinePoints[k], 5, 8, CirclePoints, 9);
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                        }
                    }
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    for (k = 0; k < acCounter - 1; k++) {
                        if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 5) {
                            shape.moveTo(pLinePoints[k]);
                            shape.lineTo(pLinePoints[k + 1]);
                        }
                    }
                    shapes.add(shape);
                    break;
                case TacticalLines.OVERHEAD_WIRE_LS:
                    for (k = 0; k < acCounter; k++) {
                        if (pLinePoints[k].style == 20) {
                            POINT2[] CirclePoints = new POINT2[8];
                            shape = lineutility.CalcCircleShape(pLinePoints[k], 5, 8, CirclePoints, 9);
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                        }
                    }
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    for (k = 0; k < acCounter; k++) {
                        if (pLinePoints[k].style == 1) {
                            if (k == 0) {
                                shape.moveTo(pLinePoints[k]);
                            } else {
                                shape.lineTo(pLinePoints[k]);
                            }
                        }
                    }
                    shapes.add(shape);
                    break;
                case TacticalLines.ATDITCHM:
                    for (k = 0; k < acCounter; k++) {
                        if (pLinePoints[k].style == 20) {
                            POINT2[] CirclePoints = new POINT2[8];
                            shape = lineutility.CalcCircleShape(pLinePoints[k], 4, 8, CirclePoints, 9);//was 3
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                            continue;
                        }
                        if (k < acCounter - 2) {
                            if (pLinePoints[k].style != 0 && pLinePoints[k + 1].style == 0) {
                                shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                                shape.set_Style(pLinePoints[k].style);
                                shape.moveTo(pLinePoints[k]);
                                shape.lineTo(pLinePoints[k]);
                            } else if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 0) {
                                shape.moveTo(pLinePoints[k]);
                                shape.lineTo(pLinePoints[k + 1]);
                            } else if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 10) {
                                shape.moveTo(pLinePoints[k]);
                                shape.lineTo(pLinePoints[k + 1]);
                                shapes.add(shape);
                            }
                        }
                        //use shapes instead of pixels
//                        if(shape==null)
//                            shape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
//
//                        if(beginLine)
//                        {
//                            if(k==0)
//                                shape.set_Style(pLinePoints[k].style);
//
//                            shape.moveTo(pLinePoints[k]);
//                            beginLine=false;
//                        }
//                        else
//                        {
//                            //diagnostic 1-8-13
//                            if(k<acCounter-1)
//                            {
//                                if(pLinePoints[k].style==5)
//                                {
//                                    shape.moveTo(pLinePoints[k]);
//                                    continue;
//                                }                                
//                            }
//                            //end section
//                            shape.lineTo(pLinePoints[k]);
//                            //diagnostic 1-8-13
//                            //if(pLinePoints[k].style==5 || pLinePoints[k].style==10)
//                            if(pLinePoints[k].style==10)
//                            {
//                                beginLine=true;
//                            }
//                        }
//                        if(pLinePoints[k+1].style==20)
//                        {
//                            if(shape !=null && shape.getShape() != null)
//                                shapes.add(shape);
//                        }
                        if (k < acCounter - 2) {
                            if (pLinePoints[k].style == 5 && pLinePoints[k + 1].style == 0) {
                                shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                                shape.set_Style(pLinePoints[k].style);
                                shape.moveTo(pLinePoints[k]);
                                //shape.lineTo(pLinePoints[k]);
                            } else if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 0) {
                                shape.lineTo(pLinePoints[k + 1]);
                            } else if (pLinePoints[k].style == 0 && pLinePoints[k + 1].style == 5) {
                                shape.lineTo(pLinePoints[k + 1]);
                                shapes.add(shape);
                            }
                        }
                    }//end for
                    break;
                case TacticalLines.DIRATKFNT:
                    //the solid lines
                    for (k = 0; k < vblCounter; k++) {
                        if (pLinePoints[k].style == 18) {
                            continue;
                        }

                        if (shape == null) {
                            shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        }

                        if (beginLine) {
                            if (k > 0) //doubled points with linestyle=5
                            {
                                if (pLinePoints[k].style == 5 && pLinePoints[k - 1].style == 5) {
                                    continue;//shape.lineTo(pLinePoints[k]);
                                }
                            }
                            if (k == 0) {
                                shape.set_Style(pLinePoints[k].style);
                            }

                            shape.moveTo(pLinePoints[k]);
                            beginLine = false;
                        } else {
                            shape.lineTo(pLinePoints[k]);
                            if (pLinePoints[k].style == 5) {
                                beginLine = true;
                                //unless there are doubled points with style=5
                            }
                        }
                        if (k == vblCounter - 1) //non-LC should only have one shape
                        {
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                        }
                    }
                    //the dashed lines
                    for (k = 0; k < vblCounter; k++) {
                        if (pLinePoints[k].style == 18 && pLinePoints[k - 1].style == 5) {
                            shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                            //shape.set_Style(pLinePoints[k].style);
                            shape.set_Style(1);
                            shape.moveTo(pLinePoints[k]);
                        } else if (pLinePoints[k].style == 18 && pLinePoints[k - 1].style == 18) {
                            shape.lineTo(pLinePoints[k]);
                        } else if (pLinePoints[k].style == 5 && pLinePoints[k - 1].style == 18) {
                            shape.lineTo(pLinePoints[k]);
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                            break;
                        } else {
                            continue;
                        }
                    }
                    break;
                case TacticalLines.ESR1:
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.set_Style(pLinePoints[0].style);
                    shape.moveTo(pLinePoints[0]);
                    shape.lineTo(pLinePoints[1]);
                    //if(shape !=null && shape.get_Shape() != null)
                    shapes.add(shape);
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.set_Style(pLinePoints[2].style);
                    shape.moveTo(pLinePoints[2]);
                    shape.lineTo(pLinePoints[3]);
                    //if(shape !=null && shape.get_Shape() != null)
                    shapes.add(shape);
                    break;
                case TacticalLines.DUMMY: //commented 5-3-10
                    //first shape is the original points
//                    shape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
//                    shape.set_Style(1);
//                    shape.moveTo(pLinePoints[acCounter-1]);
//                    shape.lineTo(pLinePoints[acCounter-2]);
//                    shape.lineTo(pLinePoints[acCounter-3]);
//                    if(shape !=null && shape.getShape() != null)
//                        shapes.add(shape);
                    //first shape is the original points
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    beginLine = true;
                    for (k = 0; k < acCounter - 3; k++) {
                        //use shapes instead of pixels
                        if (shape == null) {
                            shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        }

                        if (beginLine) {
                            if (k == 0) {
                                shape.set_Style(pLinePoints[k].style);
                            }

                            //if(k>0) //doubled points with linestyle=5
                            //  if(pLinePoints[k].style==5 && pLinePoints[k-1].style==5)
                            //    shape.lineTo(pLinePoints[k]);
                            shape.moveTo(pLinePoints[k]);
                            beginLine = false;
                        } else {
                            shape.lineTo(pLinePoints[k]);
                            if (pLinePoints[k].style == 5 || pLinePoints[k].style == 10) {
                                beginLine = true;
                                //unless there are doubled points with style=5
                            }
                        }
                        if (k == acCounter - 4) //non-LC should only have one shape
                        {
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                        }
                    }//end for
                    //last shape are the xpoints
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.set_Style(1);
                    shape.moveTo(pLinePoints[acCounter - 1]);
                    shape.lineTo(pLinePoints[acCounter - 2]);
                    shape.lineTo(pLinePoints[acCounter - 3]);
                    if (shape != null && shape.getShape() != null) {
                        shapes.add(shape);
                    }
                    break;
                case TacticalLines.DMA:
                    //first shape is the original points
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.set_Style(pLinePoints[0].style);
                    shape.moveTo(pLinePoints[0]);
                    for (k = 1; k < vblCounter - 3; k++) {
                        shape.lineTo(pLinePoints[k]);
                    }
                    if (shape != null && shape.getShape() != null) {
                        shapes.add(shape);
                    }
                    //next shape is the center feature
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.moveTo(pLinePoints[vblCounter - 3]);
                    shape.set_Style(1);
                    shape.lineTo(pLinePoints[vblCounter - 2]);
                    shape.lineTo(pLinePoints[vblCounter - 1]);
                    if (shape != null && shape.getShape() != null) {
                        shapes.add(shape);
                    }

                    //add a shape to outline the center feature in case fill is opaque
                    //and the same color so that the fill obscures the feature
//                    gp=shape.getShape();
//                    shape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
//                    shape.setShape(gp);
//                    shapes.add(1,shape);
                    break;
                case TacticalLines.FORDIF:
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.set_Style(pLinePoints[0].style);
                    shape.moveTo(pLinePoints[0]);
                    shape.lineTo(pLinePoints[1]);
                    shape.moveTo(pLinePoints[2]);
                    shape.lineTo(pLinePoints[3]);
                    shapes.add(shape);
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.set_Style(pLinePoints[4].style);
                    shape.moveTo(pLinePoints[4]);
                    for (k = 5; k < acCounter; k++) {
                        if (pLinePoints[k - 1].style != 5) {
                            shape.lineTo(pLinePoints[k]);
                        }
                    }

                    if (shape != null && shape.getShape() != null) {
                        shapes.add(shape);
                    }
                    break;
                case TacticalLines.DMAF:
                    //first shape is the original points
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.set_Style(points.get(0).style);
                    shape.moveTo(points.get(0));
                    for (k = 1; k < vblCounter - 3; k++) {
                        shape.lineTo(points.get(k));
                    }
                    if (shape != null && shape.getShape() != null) {
                        shapes.add(shape);
                    }
                    //next shape is the center feature
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.moveTo(points.get(vblCounter - 3));
                    shape.set_Style(1);
                    shape.lineTo(points.get(vblCounter - 2));
                    shape.lineTo(points.get(vblCounter - 1));
                    if (shape != null && shape.getShape() != null) {
                        shapes.add(shape);
                    }

                    //add a shape to outline the center feature in case fill is opaque
                    //and the same color so that the fill obscures the feature
//                    gp=shape.getShape();
//                    shape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
//                    shape.setShape(gp);
//                    shapes.add(1,shape);
                    //last shape are the xpoints
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    beginLine = true;
                    for (k = vblCounter; k < points.size(); k++) {
                        if (beginLine) {
                            if (k == 0) {
                                shape.set_Style(points.get(k).style);
                            }

                            if (k > 0) //doubled points with linestyle=5
                            {
                                if (points.get(k).style == 5 && points.get(k - 1).style == 5) {
                                    shape.lineTo(points.get(k));
                                }
                            }

                            shape.moveTo(points.get(k));
                            beginLine = false;
                        } else {
                            shape.lineTo(points.get(k));
                            if (points.get(k).style == 5 || points.get(k).style == 10) {
                                beginLine = true;
                                //unless there are doubled points with style=5
                            }
                        }
                        if (k == points.size() - 1) //non-LC should only have one shape
                        {
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                        }
                    }
                    break;
                case TacticalLines.AIRFIELD:
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.moveTo(pLinePoints[0]);
                    for (k = 1; k < acCounter - 5; k++) {
                        shape.lineTo(pLinePoints[k]);
                    }

                    shapes.add(shape);

                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.moveTo(pLinePoints[acCounter - 4]);
                    shape.lineTo(pLinePoints[acCounter - 3]);
                    shape.moveTo(pLinePoints[acCounter - 2]);
                    shape.lineTo(pLinePoints[acCounter - 1]);
                    shapes.add(shape);

                    //we need an extra shape to outline the center feature
                    //in case there is opaque fill that obliterates it
//                    gp=shape.getShape();
//                    shape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
//                    shape.setShape(gp);
//                    shapes.add(1,shape);
                    break;
                case TacticalLines.MIN_POINTS:
                    shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                    shape.moveTo(pLinePoints[0]);
                    for (k = 1; k < pLinePoints.length; k++) {
                        shape.lineTo(pLinePoints[k]);
                    }

                    shapes.add(shape);
                    break;
                default:
                    for (k = 0; k < acCounter; k++) {
                        //use shapes instead of pixels
                        if (shape == null) {
                            shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                        }

                        if (beginLine) {
                            //if(pLinePoints[k].style==5)
                            //  continue;

                            if (k == 0) {
                                shape.set_Style(pLinePoints[k].style);
                            }

                            if (k > 0) //doubled points with linestyle=5
                            {
                                if (pLinePoints[k].style == 5 && pLinePoints[k - 1].style == 5 && k < acCounter - 1) {
                                    continue;
                                } else if (pLinePoints[k].style == 5 && pLinePoints[k - 1].style == 10) //CF
                                {
                                    continue;
                                }
                            }

                            if (k == 0 && pLinePoints.length > 1) {
                                if (pLinePoints[k].style == 5 && pLinePoints[k + 1].style == 5) {
                                    continue;
                                }
                            }

                            shape.moveTo(pLinePoints[k]);
                            beginLine = false;
                        } else {
                            shape.lineTo(pLinePoints[k]);
                            if (pLinePoints[k].style == 5 || pLinePoints[k].style == 10) {
                                beginLine = true;
                                //unless there are doubled points with style=5
                            }
                        }
                        if (k == acCounter - 1) //non-LC should only have one shape
                        {
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(shape);
                            }
                        }
                    }//end for
                    break;
            }//end switch
            //a loop for arrowheads with fill
            //these require a separate shape for fill
            switch (lineType) {
                case TacticalLines.AC:
                case TacticalLines.SAAFR:
                case TacticalLines.MRR:
                case TacticalLines.MRR_USAS:
                case TacticalLines.UAV:
                case TacticalLines.UAV_USAS:
                case TacticalLines.LLTR:
                    for (j = 0; j < vblSaveCounter - 1; j++) {
                        dMBR = pOriginalLinePoints[j].style;
                        acPoints[0] = new POINT2(pOriginalLinePoints[j]);
                        acPoints[1] = new POINT2(pOriginalLinePoints[j + 1]);
                        lineutility.GetSAAFRFillSegment(acPoints, dMBR);//was dMRR
                        shape = new Shape2(Shape2.SHAPE_TYPE_FILL);
                        shape.moveTo(acPoints[0]);
                        shape.lineTo(acPoints[1]);
                        shape.lineTo(acPoints[2]);
                        shape.lineTo(acPoints[3]);
                        shapes.add(0, shape);
                    }
                    break;
                case TacticalLines.BELT1://requires non-decorated fill shape
                    shape = new Shape2(Shape2.SHAPE_TYPE_FILL);
                    shape.moveTo(pUpperLinePoints[0]);
                    for (j = 1; j < pUpperLinePoints.length; j++) {
                        shape.lineTo(pUpperLinePoints[j]);
                        //lineutility.SegmentLineShape(pUpperLinePoints[j], pUpperLinePoints[j+1], shape);
                    }
                    shape.lineTo(pLowerLinePoints[pLowerLinePoints.length - 1]);
                    for (j = pLowerLinePoints.length - 1; j >= 0; j--) {
                        shape.lineTo(pLowerLinePoints[j]);
                        //lineutility.SegmentLineShape(pLowerLinePoints[j], pLowerLinePoints[j-1], shape);
                    }
                    shape.lineTo(pUpperLinePoints[0]);
                    shapes.add(0, shape);
                    break;
                case TacticalLines.DIRATKAIR:
                    //added this section to not fill the bow tie and instead
                    //add a shape to close what had been the bow tie fill areas with
                    //a line segment for each one
                    int outLineCounter = 0;
                    POINT2[] ptOutline = new POINT2[4];
                    for (k = 0; k < acCounter; k++) {
                        if (pLinePoints[k].style == 10) {
                            //shape=new Shape2(Shape2.SHAPE_TYPE_FILL);
                            shape = new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
                            shape.moveTo(pLinePoints[k - 2]);
                            shape.lineTo(pLinePoints[k]);
                            if (shape != null && shape.getShape() != null) {
                                shapes.add(1, shape);
                            }

                            //collect these four points
                            ptOutline[outLineCounter++] = pLinePoints[k - 2];
                            ptOutline[outLineCounter++] = pLinePoints[k];
                        }
                    }//end for
                    //build a shape from the to use as outline for the feature
                    //if fill alpha is high
//                    shape=new Shape2(Shape2.SHAPE_TYPE_POLYLINE);
//                    shape.moveTo(ptOutline[0]);
//                    shape.lineTo(ptOutline[1]);
//                    shape.lineTo(ptOutline[3]);
//                    shape.lineTo(ptOutline[2]);
//                    shape.lineTo(ptOutline[0]);
//                    if(shape !=null && shape.getShape() != null)
//                        shapes.add(0,shape);
                    break;
                case TacticalLines.OFY:
                case TacticalLines.OCCLUDED:
                case TacticalLines.WF:
                case TacticalLines.WFG:
                case TacticalLines.WFY:
                case TacticalLines.CF:
                case TacticalLines.CFY:
                case TacticalLines.CFG:
                //case TacticalLines.SF:
                //case TacticalLines.SFY:
                //case TacticalLines.SFG:
                case TacticalLines.SARA:
                case TacticalLines.FERRY:
                case TacticalLines.EASY:
                case TacticalLines.BYDIF:
                case TacticalLines.BYIMP:
                case TacticalLines.FOLSP:
                case TacticalLines.ATDITCHC:
                case TacticalLines.ATDITCHM:
                case TacticalLines.MNFLDFIX:
                case TacticalLines.TURN:
                case TacticalLines.MNFLDDIS:
                    //POINT2 initialFillPt=null;
                    for (k = 0; k < acCounter; k++) {
                        if (k == 0) {
                            if (pLinePoints[k].style == 9) {
                                shape = new Shape2(Shape2.SHAPE_TYPE_FILL);
                                shape.set_Style(pLinePoints[k].style);
                                shape.moveTo(pLinePoints[k]);
                                //need to capture the initial fill point
                                //initialFillPt=new POINT2(pLinePoints[k]);
                            }
                        } else //k>0
                        {
                            if (pLinePoints[k].style == 9 && pLinePoints[k - 1].style != 9) {
                                shape = new Shape2(Shape2.SHAPE_TYPE_FILL);
                                shape.set_Style(pLinePoints[k].style);
                                shape.moveTo(pLinePoints[k]);
                                //need to capture the initial fill point
                                //initialFillPt=new POINT2(pLinePoints[k]);
                            }
                            if (pLinePoints[k].style == 9 && pLinePoints[k - 1].style == 9) //9,9,...,9,10
                            {
                                shape.lineTo(pLinePoints[k]);
                            }
                        }
                        if (pLinePoints[k].style == 10) {
                            shape.lineTo(pLinePoints[k]);
                            //if(lineType==TacticalLines.SARA)
                            //shape.lineTo(pLinePoints[k-2]);
                            if (shape != null && shape.getShape() != null) {
                                //must line to the initial fill point to close the shape
                                //this is a requirement for the java linear ring (map3D java client)
                                //if(initialFillPt != null)
                                //shape.lineTo(initialFillPt);

                                shapes.add(0, shape);
                                //initialFillPt=null;
                            }
                        }
                    }//end for
                    break;
                default:
                    break;
            }

            //CELineArray.add_Shapes(shapes);
            //clean up
            pArrowPoints = null;
            arcPts = null;
            circlePoints = null;
            pOriginalLinePoints = null;
            pts2 = null;
            pts = null;
            segments = null;
            pUpperLinePoints = null;
            pLowerLinePoints = null;
            pUpperLowerLinePoints = null;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className, "GetLineArray2Double",
                    new RendererException("GetLineArray2Dboule " + Integer.toString(lineType), exc));
        }
        return points;
    }
}
