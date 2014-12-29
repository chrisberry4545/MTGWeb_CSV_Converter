/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package csvconverter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.BufferedWriter;
/**
 *
 * @author Chris
 */
public class CSV_Converter {

    
    public static final String FILE_LOCATION = "/Users/Chris/XMLConverter/config.xml";
    private static String fileName;
    private static String fileLocation;
    private static String seperator;
    private static String destination;
    private static String varName;
    
    private static String javaScript = "";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        readConfig();
        initialiseJavaScript();
        processXML();
        javaScript += "];";
        javaScript = javaScript.replaceAll("\\\\\\\\", ",");
        System.out.println(javaScript);
        writeToFile();
    }
    
    public static void initialiseJavaScript() {
        javaScript += "var " + varName + " = [\n";
    }
    
    public static void readConfig() {
        try {
 
	File fXmlFile = new File(FILE_LOCATION);
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
	doc.getDocumentElement().normalize();
        
	NodeList nList = doc.getElementsByTagName("fileInfo");
	for (int temp = 0; temp < nList.getLength(); temp++) {
 
		Node nNode = nList.item(temp);
 
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
			Element eElement = (Element) nNode;
                        fileLocation = eElement.getElementsByTagName("fileLocation").item(0).getTextContent();
                        fileName = eElement.getElementsByTagName("fileName").item(0).getTextContent();
                        seperator = eElement.getElementsByTagName("seperator").item(0).getTextContent();
                        destination = eElement.getElementsByTagName("destination").item(0).getTextContent();
                        varName = eElement.getElementsByTagName("variableName").item(0).getTextContent();
                        
			System.out.println("fileLocation: " + fileLocation);
			System.out.println("fileName: " + fileName);
                        System.out.println("seperator: " + seperator);
                        System.out.println("varName: " + varName);
		}
	}
        
    } catch (Exception e) {
	e.printStackTrace();
    }
    }
    
    public static void processXML() {
        String path = fileLocation + fileName;
        File file = new File(path);
        System.out.println(file.getName());
        
        BufferedReader br;
    try {
        br = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String firstLine = br.readLine();
        
        String[] columns = firstLine.split(",");
        
        String line = br.readLine();
        while (line != null) {
            String[] row = line.split(",");
            addToJavaScript(columns,row);
            line = br.readLine();
        }
        br.close();
    } catch(java.io.FileNotFoundException e) {
        System.err.println("File Not Found");
    } catch (java.io.IOException i) {
        System.err.println("File Not Found");
    } finally {
    }
    }
     
    public static void addToJavaScript(String[] cols, String[] row) {
        String jsString = "{";
        for (int i = 0; i < cols.length; i++)
        {
            jsString += cols[i];
            jsString += ":";
            if (cols[i].equals("Color") || cols[i].equals("Type"))
            {
                jsString += "[";
                for (char c : row[i].toCharArray())
                {
                    jsString += "\"";
                    jsString += c;
                    jsString += "\"";
                    jsString += ",";
                }
                if (row[i].toCharArray().length > 0)
                {
                    jsString = jsString.substring(0, jsString.toCharArray().length - 1);
                }
                jsString += "], ";
            }
            else
            {
                jsString += "\"";
                jsString += row[i];
                jsString += "\"";
                if (i != cols.length - 1)
                {
                    jsString += ", ";
                }
            }
        }
        jsString += "},\n";
        javaScript += jsString;
    }
    
    public static void writeToFile() {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new java.io.FileOutputStream(destination), "utf-8"));
            writer.write(javaScript);
        } catch (java.io.IOException ex) {
          // report
        } finally {
           try {writer.close();} catch (Exception ex) {}
        }
    }
    
}
