package tideengine;

import coreutilities.sql.SQLUtil;

import java.io.File;

import java.sql.Connection;

import java.util.ArrayList;

import java.util.TreeMap;

import oracle.xml.parser.v2.XMLDocument;

/**
 * Access method agnostic front end.
 * Calls the right methods, depending on the chosen option (XML or SQL)
 */
public class BackEndTideComputer
{
  private static String dbLocation = System.getProperty("db.location", ".." + File.separator + "all-db");
  
  public static final int XML_OPTION = 0;
  public static final int SQL_OPTION = 1;
  
  private static int CHOSEN_OPTION = XML_OPTION;

  private static String constituentFileLocation = "xml.data" + File.separator + "constituents.xml";
  private static String stationFileLocation     = "xml.data" + File.separator + "stations.xml";
  
  private static XMLDocument constituents = null;  
  private static Connection conn = null;
  
  public static void connect(int option) throws Exception
  {
    CHOSEN_OPTION = option;
    switch (CHOSEN_OPTION)
    {
      case SQL_OPTION:
        if (dbLocation.startsWith("//"))
          conn = SQLUtil.getServerConnection(dbLocation, "tides", "tides"); // like //localhost:1234/tides
        else
          conn = SQLUtil.getConnection(dbLocation, "TIDES", "tides", "tides");
        break;
      case XML_OPTION:
        constituents = BackEndXMLTideComputer.loadDOM(BackEndXMLTideComputer.CONSTITUENT_FILE);
        break;
    }
  }
  
  public static void disconnect() throws Exception
  {
    if (CHOSEN_OPTION == SQL_OPTION)
      conn.close();
  }
  
  public static ArrayList<Coefficient> buildSiteConstSpeed() throws Exception
  {
    ArrayList<Coefficient> constSpeed = null;
    switch (CHOSEN_OPTION)
    {
      case XML_OPTION:
        constSpeed = BackEndXMLTideComputer.buildSiteConstSpeed(constituents);
        break;
      case SQL_OPTION:
        constSpeed = BackEndSQLTideComputer.buildSiteConstSpeed(conn);
        break;
    }
    return constSpeed;
  }
  
  public static double getAmplitudeFix(int year, String name) throws Exception
  {
    double d = 0;
    switch (CHOSEN_OPTION)
    {
      case XML_OPTION:
        d = BackEndXMLTideComputer.getAmplitudeFix(constituents, year, name);
        break;
      case SQL_OPTION:
        d = BackEndSQLTideComputer.getAmplitudeFix(conn, year, name);
        break;
    }
    return d;
  }
  
  public static double getEpochFix(int year, String name) throws Exception
  {
    double d = 0;
    switch (CHOSEN_OPTION)
    {
      case XML_OPTION:
        d = BackEndXMLTideComputer.getEpochFix(constituents, year, name);
        break;
      case SQL_OPTION:
        d = BackEndSQLTideComputer.getEpochFix(conn, year, name);
        break;
    }
    return d;
  }
  
  public static TideStation findTideStation(String stationName, int year) throws Exception
  {
    TideStation ts = null;
    switch (CHOSEN_OPTION)
    {
      case XML_OPTION:
        ts = BackEndXMLTideComputer.findTideStation(stationName, year, constituents);
        break;
      case SQL_OPTION:
        ts = BackEndSQLTideComputer.findTideStation(stationName, year, conn);
        break;
    }
    return ts;
  }
  
  public static ArrayList<TideStation> getStationData() throws Exception
  {
    ArrayList<TideStation> alts = null;
    switch (CHOSEN_OPTION)
    {
      case XML_OPTION:
        alts = BackEndXMLTideComputer.getStationData();
        break;
      case SQL_OPTION:
        alts = BackEndSQLTideComputer.getStationData(conn);
        break;
    }
    return alts;
  }
  
  public static TreeMap<String, TideUtilities.StationTreeNode> buildStationTree()
  {
    TreeMap<String, TideUtilities.StationTreeNode> st = null;
  
    switch (CHOSEN_OPTION)
    {
      case XML_OPTION:
        st = TideUtilities.buildStationTree(stationFileLocation);
        break;
      case SQL_OPTION:
        st = TideUtilities.buildStationTree(conn);
        break;
    }      
    return st;
  }
  
  public static void setVerbose(boolean v)
  {
    switch (CHOSEN_OPTION)
    {
      case SQL_OPTION:
        BackEndSQLTideComputer.setVerbose(v);
        break;
      case XML_OPTION:
        BackEndXMLTideComputer.setVerbose(v);
        break;
    }
  }

  public static void setDbLocation(String dbLocation)
  {
    BackEndTideComputer.dbLocation = dbLocation;
  }

  public static void setConstituentFileLocation(String constituentFileLocation)
  {
    BackEndTideComputer.constituentFileLocation = constituentFileLocation;
  }

  public static void setStationFileLocation(String stationFileLocation)
  {
    BackEndTideComputer.stationFileLocation = stationFileLocation;
  }

  public static String getStationFileLocation()
  {
    return stationFileLocation;
  }

  public static String getConstituentFileLocation()
  {
    return constituentFileLocation;
  }
}
