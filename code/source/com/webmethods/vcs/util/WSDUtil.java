/*
 * Created on Jun 13, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.webmethods.vcs.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageStore;
import com.wm.lang.ns.NSNode;

/**
 * @author sanjuk
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WSDUtil {

	public static List getWSDFiles(Package pkg,NSNode node){
		List files = new ArrayList();
		File file = pkg.getStore().getNodePath(node.getNSName());
		String fileName = null;
		try {
			fileName = file.getCanonicalPath();
		}catch(IOException e){
			return files;
		}
		addDocTypeFiles(files, fileName);
		addServicesNode(files, fileName);
		
		return files;
	}
	
	private static void addDocTypeFiles(List files,String folderName){
		File docTypeFolder = new File(folderName+"_"+File.separator+"docTypes");
		if (docTypeFolder.exists())
			addAllFiles(files,docTypeFolder);
		//<wsd name>_\docTypes
	}
		
	private static void addServicesNode(List files,String folderName){
		File servicesNode = new File(folderName+"_"+File.separator+"services",PackageStore.IDF_FILE);
		if (servicesNode.exists()){
			try {
				files.add(servicesNode.getCanonicalPath());
			}catch(Exception e){
				//do nothing
			}
		}
		////<wsd name>_\services\node.idf
	}
	
	private static void addAllFiles(List list, File directory){
		File[] files = directory.listFiles();
		for (int i=0;i<files.length;i++){
			if (files[i].isDirectory())
				addAllFiles(list,files[i]);
			else {
				try {
					list.add(files[i].getCanonicalPath());
				}catch(IOException e){
					//do nothing
				}
			}
		}
	}
}
