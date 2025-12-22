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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 */
public class Path {
    static class ActionTypes {
        public static final int ACTION_MOVE_TO = 0;
        public static final int ACTION_LINE_TO = 1;
        public static final int ACTION_DASHED_LINE_TO = 6;
    }
    
    static class Action {
        int actionType;
        double x, y;

        public Action(int actionType, double x, double y) {
            this.actionType = actionType;
            this.x = x;
            this.y = y;
        }
    }

    private ArrayList<Action> _actions = new ArrayList<>();
    private String _dashArray = null;
    private Rectangle2D _rectangle = new Rectangle2D.Double();


    public void setLineDash(float[] dashArray) {
        this._dashArray = dashArray.toString()//.toString(dashArray)
                .replace(",", "")
                .replace("[", "")
                .replace("]", "")
                .trim();
    }


    public Rectangle2D getBounds() {
        return this._rectangle;
    }


    /**
     * Adds a point to the path by moving to the specified coordinates specified
     *
     * @param x
     * @param y
     */
    public void moveTo(double x, double y) {

        if (this._actions.size() == 0) {
            this._rectangle = new Rectangle2D.Double(x, y, 1, 1);
        }
        this._rectangle.add(x, y);
        this._actions.add(new Action(ActionTypes.ACTION_MOVE_TO, x, y));
    }

    /**
     * Adds a point to the path by drawing a straight line from the current
     * coordinates to the new specified coordinates specified
     *
     * @param x
     * @param y
     */
    public void lineTo(double x, double y) {

        if (this._actions.size() == 0) {
            this.moveTo(0, 0);
        }
        this._actions.add(new Action(ActionTypes.ACTION_LINE_TO, x, y));
        this._rectangle.add(x, y);
    }

    /**
     * Adds a point to the path by drawing a straight line from the current
     * coordinates to the new specified coordinates specified
     *
     * @param x
     * @param y
     */
    public void dashedLineTo(double x, double y) {
        if (this._actions.size() == 0) {
            this.moveTo(0, 0);
        }
        this._actions.add(new Action(ActionTypes.ACTION_DASHED_LINE_TO, x, y));
        this._rectangle.add(x, y);
    }


    public String toSVGElement(String stroke, int strokeWidth, String fill, double strokeOpacity, double fillOpacity) {
        //context.beginPath();
        int size = this._actions.size();
        Action temp = null;
        String path = "";

        for (int i = 0; i < size; i++) {
            temp = this._actions.get(i);

            if (temp.actionType == ActionTypes.ACTION_LINE_TO) {
                path += "L" + temp.x + " " + temp.y;
            } else if (temp.actionType == ActionTypes.ACTION_MOVE_TO) {
                path += "M" + temp.x + " " + temp.y;
            } else if (temp.actionType == ActionTypes.ACTION_DASHED_LINE_TO) {
                path += "L" + temp.x + " " + temp.y;

            }

        }

        String line = "<path d=\"" + path + '"';

        if (stroke != null && !stroke.isEmpty()) {

            line += " stroke=\"" + stroke + '"';


            if (strokeWidth != 0)
                line += " stroke-width=\"" + strokeWidth + '"';
            else
                line += " stroke-width=\"2\"";

            if (strokeOpacity != 1.0) {
                //stroke-opacity="0.4"
                line += " stroke-opacity=\"" + strokeOpacity + '"';
            }

            //line += ' stroke-linejoin="round"';
            line += " stroke-linecap=\"round\"";
            //line += ' stroke-linecap="square"';
        }

        if (this._dashArray != null) line += " stroke-dasharray=\"" + this._dashArray + '"';

        if (fill != null && !fill.isEmpty()) {
            if (fill.indexOf("url") == 0) {
                line += " fill=\"url(#fillPattern)\"";
                //line += ' fill="url(&#35;fillPattern)"';
            } else {
                line += " fill=\"" + fill + '"';//text = text.replace(/\</g,"&gt;");
                if (fillOpacity != 1.0) {
                    //fill-opacity="0.4"
                    line += " fill-opacity=\"" + fillOpacity + '"';
                }
            }

        } else
            line += " fill=\"none\"";

        line += " />";
        return line;

    }
}