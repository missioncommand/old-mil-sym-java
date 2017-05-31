/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sec.web.renderer;

import ArmyC2.C2SD.RendererPluginInterface.ISinglePointInfo;
import ArmyC2.C2SD.Rendering.IJavaRenderer;
import ArmyC2.C2SD.Rendering.JavaRenderer;
import ArmyC2.C2SD.Utilities.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import javax.print.DocFlavor.BYTE_ARRAY;
import sec.web.renderer.utilities.JavaRendererUtilities;
import sec.web.renderer.utilities.PNGInfo;
import sec.web.renderer.utilities.SinglePointServerTester;

/**
 *
 * @author michael.spinelli
 */

/**
 * Provides a simpler interface to all the supporting rendering classes. From
 * here the user can start the single point server or use this class to create
 * their own, specify the location of which plugins to load, get an image from a
 * url string or a collections of parameters, or generate kml or json for
 * multipoint symbology.
 */
public class SECRenderer {

	private SinglePointServer sps = null;
        private MultiPointServer mps = null;
	private SinglePointServerTester spst = null;
        private SinglePointRendererService sprs = null;
	private IJavaRenderer jr = null;
	private static SECRenderer renderer = null;

	private SECRenderer() {
		jr = JavaRenderer.getInstance();
                sprs = SinglePointRendererService.getInstance();
	}

	public static synchronized SECRenderer getInstance() {
		if (renderer == null)
			renderer = new SECRenderer();

		return renderer;
	}
        
        // <editor-fold defaultstate="collapsed" desc="Manifest Functions">
        public Manifest getManifest()
        {
            Manifest mf = null;
            Manifest mfTemp = null;

            try
            {
                
                //WORKS but uses full path
                String className = SECRenderer.class.getSimpleName() + ".class";
                String classPath = SECRenderer.class.getResource(className).toString();
                if(!classPath.startsWith("jar"))
                {
                    return null;
                }
                String mfPath = classPath.substring(0,classPath.lastIndexOf("!")+1)+
                        "/META-INF/MANIFEST.MF";
                //System.out.println(mfPath);
                mf = new Manifest(new URL(mfPath).openStream());//*/
                
                //works with relative path but only when the renderer is the applet
                //mf = new Manifest(this.getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));

            }
            catch(Exception exc)
            {
                System.err.println(exc.getMessage());
                exc.printStackTrace();
            }
            
            return mf;
        }
        
        public String getManifestInfoString()
        {
            StringBuilder sb = new StringBuilder();
            Manifest mf = getManifest();
            Attributes attribs = null;
            sb.append("SECRenderer Manifest Info:\n");
            if(mf != null)
            {
                try
                {
                    
                    attribs = mf.getMainAttributes();
                    if(attribs != null)
                    {
                        sb.append("Implementation-Title: ");
                        sb.append(attribs.getValue("Implementation-Title"));
                        sb.append("\n");
                        sb.append("Implementation-Version: ");
                        sb.append(attribs.getValue("Implementation-Version"));
                        sb.append("\n");
                        sb.append("Implementation-Vendor: ");
                        sb.append(attribs.getValue("Implementation-Vendor"));
                        sb.append("\n");
                        sb.append("Build-Number: ");
                        sb.append(attribs.getValue("Build-Number"));
                        sb.append("\n");
                        sb.append("Build-Date: ");
                        sb.append(attribs.getValue("Build-Date"));
                        sb.append("\n");
                        sb.append("Trusted-Library: ");
                        sb.append(attribs.getValue("Trusted-Library"));
                        sb.append("\n");
                        sb.append("\nSymbology Standard set to: " + getSymbologyStandardString());
                        sb.append("\n");
                    }
                }
                catch(Exception exc)
                {

                }
            }
            else
            {
                sb.append("Couldn't locate Manifest.mf");
            }
            return sb.toString();
        }
        
        public void printManifestInfo()
        {
            System.out.println(getManifestInfoString());
        }
        
        private String getSymbologyStandardString()
        {
            String std = "2525B";
            int symstd = RendererSettings.getInstance().getSymbologyStandard();
            switch(symstd)
            {
                case RendererSettings.Symbology_2525B:
                    std = "2525B";
                    break;
                case RendererSettings.Symbology_2525C:
                    std = "2525C";
                    break;
                case 2://RendererSettings.Symbology_2525D:
                    std = "2525D";
                    break;//*/
            }
                    
            return std;
        }
        
        // </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Renderer Settings Functions">

	/**
	 * Sets the rendering settings defaults to match the defaults that are set
	 * in the SECWebRenderer applet.
         * @deprecated 
	 */
	public void matchSECWebRendererAppletDefaultRendererSettings() {
		// Set Renderer
		// Settings/////////////////////////////////////////////////
		//RendererSettings.getInstance().setSinglePointSymbolOutlineWidth(1);
		//RendererSettings.getInstance().setTextRenderMethod(RendererSettings.RenderMethod_NATIVE);
		// RendererSettings.getInstance().setTextBackgroundMethod(RendererSettings.TextBackgroundMethod_COLORFILL);
		// RendererSettings.getInstance().setTextBackgroundMethod(RendererSettings.TextBackgroundMethod_OUTLINE);
		//RendererSettings.getInstance().setTextBackgroundMethod(RendererSettings.TextBackgroundMethod_OUTLINE_QUICK);
		//RendererSettings.getInstance().setTextOutlineWidth(2);
		//RendererSettings.getInstance().setLabelForegroundColor(Color.BLACK);
		//RendererSettings.getInstance().setLabelBackgroundColor(new Color(255, 255, 255, 200));
		//RendererSettings.getInstance().setSymbologyStandard(RendererSettings.Symbology_2525Bch2_USAS_13_14);
                //RendererSettings.getInstance().setLabelFont("arial", Font.BOLD, 12);//, false, 0.05f);
		// RendererSettings.getInstance().setLabelBackgroundColor(Color.WHITE);
		// RendererSettings.getInstance().setLabelFont("arial",
		// Font.BOLD, 12);//default
		// //////////////////////////////////////////////////////////////////////
	}

	/**
	 * Not meant to be changed on the fly. Let's user choose between 2525Bch2
	 * and 2525C. 2525Bch2 = 0, 2525C = 1.
	 * 
	 * @param symStd
	 */
	public void setDefaultSymbologyStandard(int symStd) {
		RendererSettings.getInstance().setSymbologyStandard(symStd);
	}

	/**
	 * \ Set minimum level at which an item can be logged. In descending order:
	 * OFF = Integer.MAX_VALUE Severe = 1000 Warning = 900 Info = 800 Config =
	 * 700 Fine = 500 Finer = 400 Finest = 300 All = Integer.MIN_VALUE Use like
	 * SECRenderer.setLoggingLevel(Level.INFO); or Use like
	 * SECRenderer.setLoggingLevel(800);
	 * 
	 * @param level
	 *            java.util.logging.level
	 */
	public void setLoggingLevel(Level level) {
		try {
			ErrorLogger.setLevel(level, true);
			ErrorLogger.LogMessage("SECRenderer", "setLoggingLevel(Level)", "Logging level set to: "
					+ ErrorLogger.getLevel().getName(), Level.CONFIG);
		} catch (Exception exc) {
			ErrorLogger.LogException("SECRenderer", "setLoggingLevel(Level)", exc, Level.INFO);
		}
	}

	/**
	 * \ Set minimum level at which an item can be logged. In descending order:
	 * OFF = Integer.MAX_VALUE Severe = 1000 Warning = 900 Info = 800 Config =
	 * 700 Fine = 500 Finer = 400 Finest = 300 All = Integer.MIN_VALUE Use like
	 * SECRenderer.setLoggingLevel(Level.INFO); or Use like
	 * SECRenderer.setLoggingLevel(800);
	 * 
	 * @param level
	 *            int
	 */
	public void setLoggingLevel(int level) {
		try {
			if (level > 1000)
				ErrorLogger.setLevel(Level.OFF, true);
			else if (level > 900)
				ErrorLogger.setLevel(Level.SEVERE, true);
			else if (level > 800)
				ErrorLogger.setLevel(Level.WARNING, true);
			else if (level > 700)
				ErrorLogger.setLevel(Level.INFO, true);
			else if (level > 500)
				ErrorLogger.setLevel(Level.CONFIG, true);
			else if (level > 400)
				ErrorLogger.setLevel(Level.FINE, true);
			else if (level > 300)
				ErrorLogger.setLevel(Level.FINER, true);
			else if (level > Integer.MIN_VALUE)
				ErrorLogger.setLevel(Level.FINEST, true);
			else
				ErrorLogger.setLevel(Level.ALL, true);

			ErrorLogger.LogMessage("SECRenderer", "setLoggingLevel(int)", "Logging level set to: "
					+ ErrorLogger.getLevel().getName(), Level.CONFIG);
		} catch (Exception exc) {
			ErrorLogger.LogException("SECRenderer", "setLoggingLevel(int)", exc, Level.INFO);
		}
	}

	/**
	 * Determines size of the symbol assuming no pixel size is specified
	 * 
	 * @param size
	 *            default 50
	 */
	public void setSinglePointUnitsFontSize(int size) {
		jr.setUnitSymbolSize(size);
	}

	/**
	 * Determines size of the symbol assuming no pixel size is specified
	 * 
	 * @param size
	 *            default 60
	 */
	public void setSinglePointTacticalGraphicFontSize(int size) {
		jr.setSinglePointTGSymbolSize(size);
	}
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Single Point Server Functions">

	/*
	 * Starts the single point server. Uses default port # of 6789.
	 */
	public void startSinglePointServer() {
		startSinglePointServer(6789);
	}

	/**
	 * Starts single point server on specified port number
	 * 
	 * @param port
	 */
	public void startSinglePointServer(int port) {
		// 0 for backlog means use system default value.
		startSinglePointServer(port, 0);
	}

	/**
	 * Starts single point server on specified port number
	 * 
	 * @param port
	 * @param backlog
	 *            An integer value of '0' means use the system default
	 */
	public void startSinglePointServer(int port, int backlog) {
		try {
			if (sps == null) {
				// START SINGLE POINT SERVER /////////////////////////////////////////
				sps = new SinglePointServer(port, backlog);
				sps.start();

				spst = new SinglePointServerTester(sps);
				Thread thr1 = new Thread(spst);
				thr1.start();
				// STARTED SINGLE POINT SERVER /////////////////////////////////////////
			} else {
				System.out.println("Single Point Server already started.");
			}
		} catch (Exception exc) {
			ErrorLogger.LogException("SECRenderer", "startSinglePointServer", exc);
		}
	}

	/**
	 * Gets the port # the single point server is currently using.
	 * 
	 * @return
	 */
	public int getSinglePointServerPort() {
		if (sps != null) {
			return sps.getPortNumber();
		} else {
			return -1;
		}
	}

	/**
	 * Checks if the SinglePointServer is running
	 * 
	 * @return
	 */
	public Boolean isSinglePointServerRunning() {
		if (spst != null) {
			return spst.isRunning();
		} else {
			return false;
		}
	}

	/**
	 * Stops the single point server
	 */
	public void stopSinglePointServer() {
		if (sps != null) {
			sps.stop();
		}
		sps = null;
		spst = null;
	}

	// </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Multi Point Server Functions">

	/*
	 * Starts the multi point server. Uses default port # of 6790.
	 */
	public void startMultiPointServer() {
		startSinglePointServer(6790);
	}

	/**
	 * Starts multi point server on specified port number
	 * 
	 * @param port
	 */
	public void startMultiPointServer(int port) {
		// 0 for backlog means use system default value.
		startSinglePointServer(port, 0);
	}

	/**
	 * Starts multi point server on specified port number
	 * 
	 * @param port
	 * @param backlog
	 *            An integer value of '0' means use the system default
	 */
	public void startMultiPointServer(int port, int backlog) {
		try {
			if (mps == null) {
				// START MULTI POINT SERVER /////////////////////////////////////////
				mps = new MultiPointServer(port, backlog);
				mps.start();

				/*spst = new SinglePointServerTester(sps);
				Thread thr1 = new Thread(spst);
				thr1.start();*/
				// STARTED MULTI POINT SERVER /////////////////////////////////////////
			} else {
				System.out.println("Multi Point Server already started.");
			}
		} catch (Exception exc) {
			ErrorLogger.LogException("SECRenderer", "startMultiPointServer", exc);
		}
	}

	/**
	 * Gets the port # the multi point server is currently using.
	 * 
	 * @return
	 */
	public int getMultiPointServerPort() {
		if (mps != null) {
			return mps.getPortNumber();
		} else {
			return -1;
		}
	}

	/**
	 * Checks if the MultiPointServer is running
	 * 
	 * @return
	 */
	public Boolean isMultiPointServerRunning() {
		/*if (spst != null) {
			return spst.isRunning();
		} else {
			return false;
		}//*/
                if(mps != null)
                    return true;
                else
                    return false;
	}

	/**
	 * Stops the multi point server
	 */
	public void stopMultiPointServer() {
		if (mps != null) {
			mps.stop();
		}
		mps = null;
		//spst = null;
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Plugin Functions">

	/**
	 * Will attempt to download and load a plugin given a specific url
	 * Call refreshPlugins() after you've loaded all the plugins you want
         * the service to make available.
	 * @param url
	 */
	public void loadPluginsFromUrl(String url) {
		try {
			SinglePointRendererService.getInstance().AddRenderersToPath(url);
            // SinglePointRendererService.getInstance().LoadSPRendererServices();
		} catch (Exception exc) {
			ErrorLogger.LogException("SECRenderer", "loadDefaultPlugins", exc);
		}
	}

	/**
	 * Attemps to Load a specfic file as a plugin
	 * Call refreshPlugins() after you've loaded all the plugins you want
         * the service to make available.
	 * @param file
	 */
	public void loadPluginsFromFile(File file) {
		try {
			SinglePointRendererService.getInstance().AddRenderersToPathByFile(file);
            // SinglePointRendererService.getInstance().LoadSPRendererServices();
		} catch (Exception exc) {
			ErrorLogger.LogException("SECRenderer", "loadDefaultPlugins", exc);
		}
	}

	/**
	 * Scans a directory and loads any plugins located there.
	 * Call refreshPlugins() after you've loaded all the plugins you want
         * the service to make available.
	 * @param directory
	 */
	public void loadPluginsFromDirectory(File directory) {
		try {
			SinglePointRendererService.getInstance().AddRenderersToPathByDirectory(directory);
                        //SinglePointRendererService.getInstance().LoadSPRendererServices();
		} catch (Exception exc) {
			ErrorLogger.LogException("SECRenderer", "loadDefaultPlugins", exc);
		}
	}
        
        /**
         * After loading plugins, you need to refresh the service so that it's 
         * aware of the plugins that were made available.
         */
        public void refreshPlugins()
        {
            SinglePointRendererService.getInstance().LoadSPRendererServices();
        }

	/**
	 * Gets a list of the loaded plugins
	 * 
	 * @return a list of the currently loaded plugins.
	 */
	public ArrayList<String> getListOfLoadedPlugins() {
		return SinglePointRendererService.getInstance().getSinglePointRendererIDs();
	}
        // </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Single Point Functions">
	/**
	 * Generates an image for a milstd symbol
	 * 
	 * @param url
	 *            assumes url ends like:
	 *            "/SFGP-----------?T=uniquedesignation_1&H=blah&H1=etc"
	 * @return
	 */
	public PNGInfo getMilStdSymbolImageFromURL(String url) {
            MilStdSymbol ms = null;
            try
            {
		String symbolID = (url.startsWith("/") ? url.substring(url.lastIndexOf("/") + 1) : url);
		ms = JavaRendererUtilities.createMilstdSymbol(symbolID);
            }
            catch(Exception exc)
            {
                ErrorLogger.LogException("SECRenderer", "getMilStdSymbolImageFromURL", exc);
            }
	
            if(ms != null)
                return getMilStdSymbolImage(ms);
            else
                return null;
	}
	
	
	/**
	 * Generates an image for a milstd symbol
	 * 
	 * @param {@link STRING} String symbolId
	 * 			contains the MilStd2525 symbolId for the graphic.
	 * @param {@link HashMap} HashMap<String, String> : symbolInfoMap
	 *            A map containing all the correct modifiers for image that will be created.
	 * @return {@link BYTE_ARRAY}
	 */
	public PNGInfo getMilStdSymbolImage(String symbolId, Map<String, String> symbolInfoMap) {
		
		MilStdSymbol ms = JavaRendererUtilities.createMilstdSymbol(symbolId, symbolInfoMap);
		
		return getMilStdSymbolImage(ms);
	}
        
        /**
         * 
         * @param ms
         * @return 
         */
        private PNGInfo getMilStdSymbolImage(MilStdSymbol ms)
        {
            IPointConversion ipc = new PointConversionDummy();
            ImageInfo ii = null;
            PNGInfo pi = null;
            try {
                    if (jr.CanRender(ms)) {
                            jr.Render(ms, ipc, null);
                            ii = ms.toImageInfo();
                    }
            } catch (Exception exc) {
                    ErrorLogger.LogException("SECRenderer", "getMilStdSymbolImage(MilStdSymbol)", exc);
            }
            if (ii != null)
            {
                pi = new PNGInfo(ii);
            }
            else
            {
                //System.out.println("ii is null");
            }

            return pi;
        }
        
        /**
         * Works the same as getMilStdSymbolImageFromURL but if you specify a
         * renderer, the function will tried to get the image from the 
         * specified renderer plugin.
         * @param {@link STRING} String url.
         * @return 
         */
        public PNGInfo getSymbolImageFromURL(String url)
        {
            String symbolID = "";
            Map<String, String> params = null;
            try
            {
                symbolID = (url.startsWith("/") ? url.substring(url.lastIndexOf("/") + 1) : url);
                params = JavaRendererUtilities.createParameterMapFromURL(symbolID);
                
                int questionIndex = symbolID.lastIndexOf('?');
                if(questionIndex != -1)
                {
                     symbolID = java.net.URLDecoder.decode(symbolID.substring(0, questionIndex), "UTF-8");
                }
            }
            catch(Exception exc)
            {
                ErrorLogger.LogException("SECRenderer", "getSymbolImageFromURL", exc);
            }
            return getSymbolImage(symbolID, params);
        }
        
        /**
	 * Works the same as getMilStdSymbolImage but if you specify a
         * renderer, the function will tried to get the image from the 
         * specified renderer plugin.
	 * 
	 * @param {@link STRING} String symbolId
	 * 			contains the symbolId for the graphic.
	 * @param {@link HashMap} HashMap<String, String> : symbolInfoMap
	 *            A map containing all the correct modifiers for image that will be created.
	 * @return {@link BYTE_ARRAY}
	 */
	public PNGInfo getSymbolImage(String symbolId, Map<String, String> symbolInfoMap)
        {

            PNGInfo pi = null;
            ISinglePointInfo spi = null;
            String rendererID = "";
            try 
            {
                if(symbolInfoMap.containsKey("renderer"))
                {
                    rendererID = symbolInfoMap.get("renderer");
                }
                else if(symbolInfoMap.containsKey("RENDERER"))
                {
                    rendererID = symbolInfoMap.get("RENDERER");
                }
                //System.out.println("Requested Renderer ID: " + rendererID);
                
                // check if plugin renderer was requested
                if(rendererID==null || rendererID.equals(""))
                {
                    rendererID=SinglePoint2525Renderer.RENDERER_ID;
                }
                if(sprs.hasRenderer(rendererID)==false)
                {
                    //if renderer id doesn't exist or is no good, set to default plugin.
                    rendererID=SinglePoint2525Renderer.RENDERER_ID;
                }
                
                if(sprs.hasRenderer(rendererID))
                {
                    //System.out.println("Renderer ID: " + rendererID);
                    //System.out.println("Symbol ID: " + symbolId);
                    //ErrorLogger.PrintStringMap(symbolInfoMap);
                    spi = sprs.render(rendererID, symbolId, symbolInfoMap);
                    if(spi!=null)
                    {
                        pi = new PNGInfo(spi);
                    }
                }
                else
                {
                    String message = "Lookup for 2525 renderer plugin failed.";
                    ErrorLogger.LogMessage("SECRenderer", "getSymbolImage", message,Level.WARNING);
                }
                
                
            } catch (Exception exc) {
                    ErrorLogger.LogException("SECRenderer", "getSymbolImage", exc);
            }

            return pi;
        }
        
	/**
	 * Generates an image for a milstd symbol
	 * 
	 * @param symbolID
	 * @param properties
	 *            keyed on values like ModifiersUnits.T_UNIQUE_DESIGNATION_1 OR
	 *            ModifiersTG.Q_DIRECTION_OF_MOVEMENT
	 * @param imageSize
	 * @param keepUnitRatio
	 * @return PNGInfo which has the buffered image as well as offset
	 *         information so the image can be place properly on the map.
         * @deprecated 
	 */
	public PNGInfo getMilStdSymbolImage(String symbolID, Map<String, String> properties, int imageSize, Boolean keepUnitRatio) {
		PNGInfo pi = null;
		ImageInfo ii = jr.RenderSinglePointAsImageInfo(symbolID, properties, imageSize, keepUnitRatio);

		if (ii != null)
			pi = new PNGInfo(ii);

		return pi;

	}
        

	/**
	 * Makes an icon for use in things like nodes on a tree view.
	 * 
	 * @param symbolID
	 * @param iconSize
	 *            height & width in pixels that you want the icon to be.
	 * @param showDisplayModifiers
	 *            things like echelon, mobility, HQ, feint, etc.
	 * @return
	 */
	public BufferedImage getMilStdSymbolasIcon(String symbolID, int iconSize, Boolean showDisplayModifiers) {
		return jr.RenderMilStdSymbolAsIcon(symbolID, iconSize, showDisplayModifiers);
	}
        
        /**
         * Generates kml for single point milstd symbology
         * @param fullURL
         * @param symbolID
         * @param params include "id", "name", "description", "lat", "lon",
         * and "alt" if you'd like to seem them in the resultant KML.  
         * They are not used for the actual rendering.
         * @return 
         */
	public String getMilStdSymbolImageKML(String fullURL, String symbolID, Map<String, String> params) {
		String key = null;
		String value = null;
		String id = "";
		String name = "";
		String description = "";
		String lat = "";
		String lon = "";
		String alt = "";
                String size = "";
                String altMode = "";

                PNGInfo pi = null;
		try {
			
			pi = getMilStdSymbolImage(symbolID, params);
			
			for (Map.Entry<String, String> entry : params.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();

				if (key.equalsIgnoreCase("id")) {
					id = value;
				} else if (key.equalsIgnoreCase("name")) {
					name = value;
				} else if (key.equalsIgnoreCase("description")) {
					description = value;
				} else if (key.equalsIgnoreCase("lat")) {
					lat = value;
				} else if (key.equalsIgnoreCase("lon")) {
					lon = value;
				} else if (key.equalsIgnoreCase("alt")) {
					alt = value;
				} else if (key.equalsIgnoreCase("size")) {
					size = value;
				} else if (key.equalsIgnoreCase("altitudeMode")) {
					altMode = value;
				}
			} 

		} catch (Exception exc) {
			ErrorLogger.LogException("SECRenderer", "getMilStdSymbolKML", exc);
		}
		
		return buildKml(fullURL, id, name, description, lat, lon, alt, size, altMode, pi);
	}
        
                /**
         * Generates kml for single point milstd or plugin symbology
         * @param fullURL
         * @param symbolID
         * @param params include "id", "name", "description", "lat", "lon",
         * and "alt" if you'd like to seem them in the resultant KML.  
         * They are not used for the actual rendering.
         * @return 
         */
	public String getSymbolImageKML(String fullURL, String symbolID, Map<String, String> params) {
		String key = null;
		String value = null;
		String id = "";
		String name = "";
		String description = "";
		String lat = "";
		String lon = "";
		String alt = "";
                String size = "";
                String altMode = "";
                StringBuilder sbParams = null;
                Boolean addToUrl = true;
                PNGInfo pi = null;
		try {
			
			pi = getSymbolImage(symbolID, params);
                        
                        //System.out.println(ErrorLogger.PrintStringMap(params));
			
			for (Map.Entry<String, String> entry : params.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
                                addToUrl = true;

				if (key.equalsIgnoreCase("id")) {
					id = value;
                                        addToUrl = false;
				} else if (key.equalsIgnoreCase("name")) {
					name = value;
                                        addToUrl = false;
				} else if (key.equalsIgnoreCase("description")) {
					description = value;
                                        addToUrl = false;
				} else if (key.equalsIgnoreCase("lat")) {
					lat = value;
                                        addToUrl = false;
				} else if (key.equalsIgnoreCase("lon")) {
					lon = value;
                                        addToUrl = false;
				} else if (key.equalsIgnoreCase("alt")) {
					alt = value;
                                        addToUrl = false;
				} else if (key.equalsIgnoreCase("size")) {
					size = value;
				} else if (key.equalsIgnoreCase("altitudeMode")) {
					altMode = value;
                                        addToUrl = false;
				}
                                
                                if(addToUrl)
                                {
                                    
                                    if(sbParams==null)
                                    {
                                        sbParams = new StringBuilder();
                                        if(fullURL.contains("?"))
                                        {
                                            sbParams.append("&");
                                        }
                                        else
                                        {
                                            sbParams.append("?");
                                        }
                                        sbParams.append(key);
                                        sbParams.append("=");
                                        sbParams.append(value);
                                    }
                                    else
                                    {
                                        sbParams.append("&");
                                        sbParams.append(key);
                                        sbParams.append("=");
                                        sbParams.append(value);
                                    }
                                    
                                }
			} 
                        if(sbParams != null)
                        {
                            fullURL += sbParams.toString();
                        }

		} catch (Exception exc) {
			ErrorLogger.LogException("SECRenderer", "getMilStdSymbolKML", exc);
		}
		
		return buildKml(fullURL, id, name, description, lat, lon, alt, altMode, size, pi);
	}
        
        /**
         * Google likes to resize icons.  Based on patterns I've recognized,
         * I tried to compensate.
         * @param width
         * @param height
         * @return 
         */
        private double getIconScale(double width, double height)
        {
            double scale1 = 28;
            double scale2 = 30;
            double iconScale = 0;
            if (width == height) {
                iconScale = width / scale1;
            } else if (width > height) {
                if (height <= scale2)
                    iconScale = width / 28;
                else
                    iconScale = height / 28;
            } else {
                if (width <= scale2)
                    iconScale = height / 28;
                else
                    iconScale = width / 28;
            }
            return iconScale;
        }

        /**
         * Builds kml to go along with a single point symbol and its url
         * @param fullURL
         * @param id
         * @param name
         * @param description
         * @param lat
         * @param lon
         * @param alt
         * @param altMode
         * @param size
         * @param pi
         * @return 
         */
        private String buildKml(String fullURL, String id, String name, String description, String lat, String lon, String alt, String altMode, String size, PNGInfo pi) {
                
                double width =pi.getImage().getWidth();
                double height =pi.getImage().getHeight();
                double iconScale = 1.0;

                
                if(altMode == null || altMode.equals(""))
                    altMode = "relativeToGround";
                
                iconScale = getIconScale(width, height);

            
                //Build KML
                StringBuilder kml = new StringBuilder();
                kml.append("<kml>");
                    kml.append("<Placemark id=\"" + id + "\">");
                    kml.append("<name><![CDATA[" + name + "]]></name>");
                    kml.append("<description><![CDATA[" + description + "]]></description>");
                    kml.append("<Style>");
                        kml.append("<IconStyle>");
                            kml.append("<scale>" + iconScale + "</scale>");
                            kml.append("<Icon>");
                                kml.append("<href><![CDATA[" + fullURL.replaceAll("kml", "image") + "]]></href>");
                            kml.append("</Icon>");
                        kml.append("</IconStyle>");
                        kml.append("<LabelStyle>");
                            kml.append("<scale>" + "0" + "</scale>");
                        kml.append("</LabelStyle>");
                    kml.append("</Style>");
                    kml.append("<Point>");
                        kml.append("<extrude>1</extrude>");
                        kml.append("<altitudeMode>" + altMode + "</altitudeMode>");
                        kml.append("<coordinates>");
                            if(lon != null && !"".equals(lon) && lat != null && !"".equals(lat)) {
                                    kml.append(lon + ",");
                                    kml.append(lat);
                                    if(alt != null && !"".equals(alt)) {
                                            kml.append("," + alt);
                                    }      
                            }                           
                         kml.append("</coordinates>");
                    kml.append("</Point>");
                    kml.append("</Placemark>");
                kml.append("</kml>");

                return kml.toString();
        }
                
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Multi Point Functions">

	/**
	 * Renders all multi-point symbols, creating KML that can be used to draw it
	 * on a Google map. Multipoint symbols cannot be draw the same at different
	 * scales. For instance, graphics with arrow heads will need to redraw
	 * arrowheads when you zoom in on it. Similarly, graphics like a Forward
	 * Line of Troops drawn with half circles can improve performance if clipped
	 * when the parts of the graphic that aren't on the screen. To help readjust
	 * graphics and increase performance, this function requires the scale and
	 * bounding box to help calculate the new locations.
	 * 
	 * @param id
	 *            A unique identifier used to identify the symbol by Google map.
	 *            The id will be the folder name that contains the graphic.
	 * @param name
	 *            a string used to display to the user as the name of the
	 *            graphic being created.
	 * @param description
	 *            a brief description about the graphic being made and what it
	 *            represents.
	 * @param symbolCode
	 *            A 15 character symbolID corresponding to one of the graphics
	 *            in the MIL-STD-2525C
	 * @param controlPoints
	 *            The vertices of the graphics that make up the graphic. Passed
	 *            in the format of a string, using decimal degrees separating
	 *            lat and lon by a comma, separating coordinates by a space. The
	 *            following format shall be used "x1,y1[,z1] [xn,yn[,zn]]..."
	 * @param altitudeMode
	 *            Indicates whether the symbol should interpret altitudes as
	 *            above sea level or above ground level. Options are
	 *            "clampToGround", "relativeToGround" (from surface of earth),
	 *            "absolute" (sea level), "relativeToSeaFloor" (from the bottom
	 *            of major bodies of water).
	 * @param scale
	 *            A number corresponding to how many meters one meter of our map
	 *            represents. A value "50000" would mean 1:50K which means for
	 *            every meter of our map it represents 50000 meters of real
	 *            world distance.
	 * @param bbox
	 *            The viewable area of the map. Passed in the format of a string
	 *            "lowerLeftX,lowerLeftY,upperRightX,upperRightY." Not required
	 *            but can speed up rendering in some cases. example:
	 *            "-50.4,23.6,-42.2,24.2"
	 * @param modifiers
	 *            A JSON string representing all the possible symbol modifiers
	 *            represented in the MIL-STD-2525C. Format of the string will be
	 *            {"modifiers":
	 *            {"attributeName":"value"[,"attributeNamen":"valuen"]...}} The
	 *            quotes are literal in the above notation. Example:
	 *            {"modifiers":
	 *            {"quantity":"4","speed":"300","azimuth":[100,200]}}
	 * @param format
	 *            An enumeration: 0 for KML, 1 for JSON.
	 * @param symStd
	 *            An enumeration: 0 for 2525Bch2, 1 for 2525C.
	 * @return A JSON string representation of the graphic.
	 */
	public String RenderMultiPointSymbol(String id, String name, String description, String symbolCode,
			String controlPoints, String altitudeMode, double scale, String bbox, String modifiers, int format, int symStd) {
		// System.out.println("RenderSymbol called");
		String output = "";

            try {        
                
                modifiers = JavaRendererUtilities.addAltModeToModifiersString(modifiers, altitudeMode);
                
            

                if (JavaRendererUtilities.is3dSymbol(symbolCode, modifiers))
                {

                    output = SECWebRenderer.RenderMilStd3dSymbol(name, id, symbolCode, description, altitudeMode, controlPoints,
                            modifiers);
    //                System.out.println("old kml without modifiers: ");
    //                System.out.println(output);

                    //get modifiers/////////////////////////////////////////////////
                    String modifierKML = MultiPointHandler.getModififerKML(id, name, description, symbolCode, controlPoints,
                            scale, bbox, modifiers, format,symStd);

                    modifierKML += "</Folder>";

                    output = output.replaceFirst("</Folder>", modifierKML);

    //                System.out.println("new kml with modifiers: ");
    //                System.out.println(output);
                    ////////////////////////////////////////////////////////////////



                    // Check the output of the 3D Symbol Drawing.  If this returned an error
                    // it should either be "" or it should be a JSON string starting with "{".
                    // This really is not a good solution, but was up to 13.0.6 and had to make
                    // this bug fix in quick turnaround.  More consistent error handling should
                    // be done through code.

                    if (output.equals("") || output.startsWith("{")) {
                        output = MultiPointHandler.RenderSymbol(id, name, description, symbolCode, controlPoints,
                            scale, bbox, modifiers, format,symStd);
                    }
                }
                else
                {            
                    output = MultiPointHandler.RenderSymbol(id, name, description, symbolCode, controlPoints,
                            scale, bbox, modifiers, format,symStd);

                    //DEBUGGING
                    if(ErrorLogger.getLevel().intValue() <= Level.FINER.intValue())
                    {
                        System.out.println("");
                        StringBuilder sb = new StringBuilder();
                        sb.append("\nID: " + id + "\n");
                        sb.append("Name: " + name + "\n");
                        sb.append("Description: " + description + "\n");
                        sb.append("SymbolID: " + symbolCode + "\n");
                        sb.append("SymStd: " + String.valueOf(symStd) + "\n");
                        sb.append("Scale: " + String.valueOf(scale) + "\n");
                        sb.append("BBox: " + bbox + "\n");
                        sb.append("Coords: " + controlPoints + "\n");
                        sb.append("Modifiers: " + modifiers + "\n");
                        ErrorLogger.LogMessage("SECWebRenderer", "RenderSymbol", sb.toString(),Level.FINER);
                    }
                    if(ErrorLogger.getLevel().intValue() <= Level.FINEST.intValue())
                    {
                        String briefOutput = output.replaceAll("</Placemark>", "</Placemark>\n");
                        briefOutput = output.replaceAll("(?s)<description[^>]*>.*?</description>", "<description></description>");
                        ErrorLogger.LogMessage("SECWebRenderer", "RenderSymbol", "Output:\n" + briefOutput,Level.FINEST);
                    }
                }


            } catch (Exception ea) {

                output = "{\"type\":'error',error:'There was an error creating the MilStdSymbol - " + ea.toString() + "'}";
                ErrorLogger.LogException("SECWebRenderer", "RenderSymbol", ea, Level.WARNING);
            }
            // System.out.println(output);
                    // System.out.println("RenderSymbol exit");
            return output;
		
		
	}
        
        /**
	 * Renders all multi-point symbols, creating KML that can be used to draw it
	 * on a Google map. Multipoint symbols cannot be draw the same at different
	 * scales. For instance, graphics with arrow heads will need to redraw
	 * arrowheads when you zoom in on it. Similarly, graphics like a Forward
	 * Line of Troops drawn with half circles can improve performance if clipped
	 * when the parts of the graphic that aren't on the screen. To help readjust
	 * graphics and increase performance, this function requires the scale and
	 * bounding box to help calculate the new locations.
	 * 
	 * @param id
	 *            A unique identifier used to identify the symbol by Google map.
	 *            The id will be the folder name that contains the graphic.
	 * @param name
	 *            a string used to display to the user as the name of the
	 *            graphic being created.
	 * @param description
	 *            a brief description about the graphic being made and what it
	 *            represents.
	 * @param symbolCode
	 *            A 15 character symbolID corresponding to one of the graphics
	 *            in the MIL-STD-2525C
	 * @param controlPoints
	 *            The vertices of the graphics that make up the graphic. Passed
	 *            in the format of a string, using decimal degrees separating
	 *            lat and lon by a comma, separating coordinates by a space. The
	 *            following format shall be used "x1,y1[,z1] [xn,yn[,zn]]..."
	 * @param altitudeMode
	 *            Indicates whether the symbol should interpret altitudes as
	 *            above sea level or above ground level. Options are
	 *            "clampToGround", "relativeToGround" (from surface of earth),
	 *            "absolute" (sea level), "relativeToSeaFloor" (from the bottom
	 *            of major bodies of water).
	 * @param scale
	 *            A number corresponding to how many meters one meter of our map
	 *            represents. A value "50000" would mean 1:50K which means for
	 *            every meter of our map it represents 50000 meters of real
	 *            world distance.
	 * @param bbox
	 *            The viewable area of the map. Passed in the format of a string
	 *            "lowerLeftX,lowerLeftY,upperRightX,upperRightY." Not required
	 *            but can speed up rendering in some cases. example:
	 *            "-50.4,23.6,-42.2,24.2"
	 * @param modifiers
	 *            A JSON string representing all the possible symbol modifiers
	 *            represented in the MIL-STD-2525C. Format of the string will be
	 *            {"modifiers":
	 *            {"attributeName":"value"[,"attributeNamen":"valuen"]...}} The
	 *            quotes are literal in the above notation. Example:
	 *            {"modifiers":
	 *            {"quantity":"4","speed":"300","azimuth":[100,200]}}
	 * @param symStd
	 *            An enumeration: 0 for 2525Bch2, 1 for 2525C.
	 * @return A JSON string representation of the graphic.
	 */
	public MilStdSymbol RenderMultiPointAsMilStdSymbol(String id, String name, String description, String symbolCode,
			String controlPoints, String altitudeMode, double scale, String bbox, String modifiers, int symStd) {
		//System.out.println("RenderMultiPointAsMilStdSymbol called");
		MilStdSymbol mSymbol = null;
		try {

			if (JavaRendererUtilities.is3dSymbol(symbolCode, modifiers)==false) 
                        {
                            mSymbol = MultiPointHandler.RenderSymbolAsMilStdSymbol(id, name, description, symbolCode, controlPoints, scale, bbox,
						modifiers, symStd);
			}

		} catch (Exception ea) {
			mSymbol=null;
			ErrorLogger.LogException("SECRenderer", "RenderSymbol", ea, Level.WARNING);
		}
		
		//System.out.println("RenderMultiPointAsMilStdSymbol exit");
		return mSymbol;
	}

	/**
	 * Renders all multi-point symbols, creating KML or JSON for the user to
	 * parse and render as they like. This function requires the bounding box to
	 * help calculate the new locations.
	 * 
	 * @param id
	 *            A unique identifier used to identify the symbol by Google map.
	 *            The id will be the folder name that contains the graphic.
	 * @param name
	 *            a string used to display to the user as the name of the
	 *            graphic being created.
	 * @param description
	 *            a brief description about the graphic being made and what it
	 *            represents.
	 * @param symbolCode
	 *            A 15 character symbolID corresponding to one of the graphics
	 *            in the MIL-STD-2525C
	 * @param controlPoints
	 *            The vertices of the graphics that make up the graphic. Passed
	 *            in the format of a string, using decimal degrees separating
	 *            lat and lon by a comma, separating coordinates by a space. The
	 *            following format shall be used "x1,y1 [xn,yn]..."
	 * @param pixelWidth
	 *            pixel dimensions of the viewable map area
	 * @param pixelHeight
	 *            pixel dimensions of the viewable map area
	 * @param bbox
	 *            The viewable area of the map. Passed in the format of a string
	 *            "lowerLeftX,lowerLeftY,upperRightX,upperRightY." example:
	 *            "-50.4,23.6,-42.2,24.2"
	 * @param symbolModifiers
	 *            A JSON string representing all the possible symbol modifiers
	 *            represented in the MIL-STD-2525C. Format of the string will be
	 *            {"modifiers":
	 *            {"attributeName":"value"[,"attributeNamen":"valuen"]...}} The
	 *            quotes are literal in the above notation. Example:
	 *            {"modifiers":
	 *            {"quantity":"4","speed":"300","azimuth":[100,200]}}
	 * @param format
	 *            An enumeration: 0 for KML, 1 for JSON.
	 * @param symStd
	 *            An enumeration: 0 for 2525Bch2, 1 for 2525C.
	 * @return A JSON (1) or KML (0) string representation of the graphic.
	 */
	public String RenderMultiPointSymbol2D(String id, String name, String description, String symbolCode,
			String controlPoints, int pixelWidth, int pixelHeight, String bbox, String modifiers, int format, int symStd) {
		String output = "";
		try {
			output = MultiPointHandler.RenderSymbol2D(id, name, description, symbolCode, controlPoints, pixelWidth,
					pixelHeight, bbox, modifiers, format, symStd);
		} catch (Exception exc) {
			output = "{\"type\":'error',error:'There was an error creating the MilStdSymbol: " + symbolCode + " - "
					+ exc.toString() + "'}";
		}
		return output;
	}

	/**
	 * 
	 * @param symbolID
	 * @return true if it's advisable that the symbol be clipped
	 */
	public String ShouldClipMultipointSymbol(String symbolID) {
		if (MultiPointHandler.ShouldClipSymbol(symbolID))
			return "true";
		else
			return "false";
	}

	/**
	 * Determines in the symbol is a multipoint symbol. This includes circular &
	 * rectangular symbols that may have only 1 actual point but has AM, AN,
	 * width or height defined that's lets the renderer derive the remaining
	 * point.
	 * 
	 * @param symbolID
	 * @return
	 */
	public Boolean isMultiPointSymbol(String symbolID) {
		int symStd = RendererSettings.getInstance().getSymbologyStandard();
		return isMultiPointSymbol(symbolID, symStd);
	}

	/**
	 * Determines in the symbol is a multipoint symbol. This includes circular &
	 * rectangular symbols that may have only 1 actual point but has AM, AN,
	 * width or height defined that's lets the renderer derive the remaining
	 * point.
	 * 
	 * @param symbolID
	 * @param symStd
	 *            Like RendererSettings.Symbology_2525C
	 * @return
	 */
	public Boolean isMultiPointSymbol(String symbolID, int symStd) {
		
		return SymbolDefTable.getInstance().isMultiPoint(SymbolUtilities.getBasicSymbolID(symbolID), symStd);
		
	}

	// </editor-fold>

}
