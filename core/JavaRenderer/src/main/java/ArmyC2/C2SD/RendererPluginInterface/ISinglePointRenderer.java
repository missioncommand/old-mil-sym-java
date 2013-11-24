package ArmyC2.C2SD.RendererPluginInterface;

import java.util.Map;

public interface ISinglePointRenderer {

	String getRendererID();

	Boolean canRender(String symbolID, Map<String, String> params);

	ISinglePointInfo render(String symbolID, Map<String, String> params);

}
