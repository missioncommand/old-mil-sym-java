package ArmyC2.C2SD.RendererPluginInterface;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public interface ISinglePointInfo {

	BufferedImage getImage();

	Point2D getSymbolCenterPoint();

	Rectangle2D getSymbolBounds();

}
