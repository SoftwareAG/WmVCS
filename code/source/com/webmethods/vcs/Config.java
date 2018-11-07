package com.webmethods.vcs;

import com.wm.app.b2b.server.Resources;
import com.wm.app.b2b.server.Server;
import java.io.*;
import java.util.*;


public class Config extends Properties
{
    // all VCS property keys should be here.

	private static final String TEMPLATE_FILE_EXTN = ".template"; 
    private static Config instance = null;
    
    public static Config getInstance()
    {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    /**
     * Loads a configuration file, returning whether it existed and could be
     * read.
     */
    public boolean readFile(File file)
    {
		try {
			if (file.exists()) {
                VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_LOADING_CONFIG_FILE, file);
				FileInputStream fis = new FileInputStream(file);
                // with our own load method, we could do some validation.
				load(fis);
				fis.close();
                
                return true;
			}
            else {
                VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_NO_CONFIG_FILE);
            }
		}
		catch (IOException io) {
            VCSLog.log(VCSLog.WARN, VCSLog.VCS_PKG_ERROR_LOADING_CONFIG_FILE, io.getMessage());
		}
        return false;
    }

    /**
     * 
     */
    public void writeProperties(String packageName, String file, Map properties)
    {
    	File cnf = new File(Server.getResources().getPackageConfigDir(packageName), file);
    	writeProperties(cnf,properties);
    }
    
    public Map readProperties(String packageName, String file){
    	
    	Resources resources = Server.getResources();
    	boolean prevIsCreate = resources.isCreate();
    	
    	//This is to make sure the package\dir is not created
    	// if it does not exist
    	resources.setCreate(false);
    	File cnf = new File(resources.getPackageConfigDir(packageName), file);
    	resources.setCreate(prevIsCreate);
    			
    	return readProperties(cnf);
    }
    
    public Object put(Object key, Object value)
    {
        String val = (String)get(key);
        if (val != null) {
            VCSLog.log(VCSLog.INFO, VCSLog.VCS_PKG_OVERWRITING_EXISTING_PROPERTY, key, val);
        }
        return super.put(key, value);
    }

    private Config()
    {
        File cnf = new File(Server.getResources().getPackageConfigDir("WmVCS"), "vcs.cnf");
        readFile(cnf);
    }
    
    private Map readProperties(File file){
		try {
			if (file.exists()) {
                VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_LOADING_CONFIG_FILE, file);
				FileInputStream fis = new FileInputStream(file);
                // with our own load method, we could do some validation.
				Properties properties = new Properties();
				properties.load(fis);
				fis.close();
				return properties;
			}
            else if (file.getParentFile().exists()){
            	return new Properties();
            }else
            {
                VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_NO_CONFIG_FILE);
            }
		}
		catch (IOException io) {
            VCSLog.log(VCSLog.WARN, VCSLog.VCS_PKG_ERROR_LOADING_CONFIG_FILE, io.getMessage());
		}
        return null;
    }

    private void writeProperties(File file,Map properties){
    	Map newProperties = new HashMap();
    	newProperties.putAll(properties);
    	
    	File outFile = file;//outfile can be different, if input file is template
    	
    	if (!file.exists())
    		file = getTemplateFile(file);

		try {
			if (file.exists()) {
                VCSLog.log(VCSLog.DEBUG8, VCSLog.VCS_PKG_LOADING_CONFIG_FILE, file);
				
                FileInputStream fis = new FileInputStream(file);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis, "8859_1"));
				List data = new ArrayList();
				
		        synchronized (this)  {
			        while(true){
			        	String line = in.readLine();
			        	if (line == null)
			        		break;
			        	String updateLine = null;
			        	int length = line.length();
			        	int startPos = 0;
			        	for (int i=0;i<length;i++){
			        		if (line.charAt(i) != ' ')
			        			break;
			        		startPos++;
			        	}
			        	if (startPos < length)
			        		if (line.indexOf("watt.")==startPos){
			        			int keyEnd = line.indexOf('=',startPos);
			        			if (keyEnd > startPos){
			        				String key=line.substring(startPos,keyEnd);
			        				if (properties.containsKey(key)){
			        					String value=(String)properties.get(key);
				        				if (value != null) {
				        					updateLine = key+"="+value.replaceAll("\\\\","\\\\\\\\");
				        				}
			        					newProperties.remove(key);
			        				}
			        			}
			        		}
			        			
			        	if (updateLine != null)	
			                data.add(updateLine);
			        	else 
			        		data.add(line);
			        }
			        
			        fis.close();

			        Iterator it = newProperties.entrySet().iterator();
			        while (it.hasNext()) {
			            Map.Entry pairs = (Map.Entry)it.next();
			            if (pairs.getValue()!=null) {
			            	data.add(pairs.getKey()+"=" +pairs.getValue());
			            }
			        }
			        
					OutputStream fos = new FileOutputStream(outFile);
					BufferedWriter awriter = new BufferedWriter(new OutputStreamWriter(fos, "8859_1"));
					for(int i=0;i<data.size();i++){
						awriter.write((String)data.get(i));
						awriter.write("\n");
					}
			        awriter.flush();
			        fos.close();
		        }
			}
		}
		catch (IOException io) {
            VCSLog.log(VCSLog.WARN, VCSLog.VCS_PKG_ERROR_LOADING_CONFIG_FILE, io.getMessage());
		}
	}	
    
    private File getTemplateFile(File file){
    	
    	try {
    		String templateFileName = file.getCanonicalPath()+TEMPLATE_FILE_EXTN;
    		File templateFile = new File(templateFileName);
    		if (templateFile.exists()){
    			return templateFile; 
    		}    		
    	}catch(Exception e){
    		//do nothing
    	}
    	return file;
    }
}
