<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<HTML>
<HEAD>
<LINK REL="stylesheet" TYPE="text/css" HREF="../WmRoot/webMethods.css">
<SCRIPT SRC="../WmRoot/webMethods.js"></SCRIPT>
<SCRIPT>
   	
   	    function check()
   	    {
		
	   		if(document.configForm.reconnectingRepository.checked){
   	       		document.configForm.reconnectingRepository.value="true";
   	    	}
   	    
   	    	if(!document.configForm.reconnectingRepository.checked){;
   	       		document.configForm.reconnectingRepository.value="";
   	  		}
			if (!verifyRequiredField("configForm","svnRepositoryLocation"))
   	        {
   	           alert("Subversion Repository Location is not entered.");
   	           document.configForm.svnRepositoryLocation.focus();
   	           return false;
   	      	} 
   	    	return true;
   	     }

		function check_configForm1()
   	    {
		
		if(document.configForm1.deleteSVNFiles.checked){
				
   	       		document.configForm1.deleteSVNFiles.value="true";
   	    	}
   	    
   	    	if(!document.configForm1.deleteSVNFiles.checked){;
   	       		document.configForm1.deleteSVNFiles.value="";
   	  	}
			
   	    	return true;
   	     }
	 </SCRIPT>  
<TITLE>Blank</TITLE>
 </HEAD>
<BODY onLoad="setNavigation('/WmVCS/solutions-vcs-manageVCSSettings-configureSVN.dsp', '/WmRoot/doc/OnlineHelp/wwhelp/wwhimpl/js/html/wwhelp.htm#context=is_help&topic=IS_Solutions_VCS_SubversionConfigScrn');">
     <DIV class="position">
         <TABLE WIDTH="100%">
            <TR>
               <TD class="menusection-Solutions" colspan=2>VCS</TD>
            </TR>
	    <TR>
  	       <TD colspan=2>
  	       <UL>
               <LI>
				<script>
			    createForm("htmlform_solutions_vcs_manageVCSSettings", "solutions-vcs-manageVCSSettings.dsp", "POST", "BODY");
			    </script>
					  <script>getURL("solutions-vcs-manageVCSSettings.dsp","javascript:document.htmlform_solutions_vcs_manageVCSSettings.submit();","Back to Manage VCS Settings");</script>
			   </LI>	  
               </UL>
               </TD>
  	    </TR>
  	    <TR>
	      %invoke wm.server.vcsservices:readVCS%
  	      %ifvar actionPerformed equals('change')%
	        %invoke wm.server.svn.svnservices:configureSVN%
	      	     <tr><td colspan="2">&nbsp;</td></tr>
	      	     <TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Subversion configuration done successfully.</TD></TR>
	      	  %onerror%
	      	    <tr><td colspan="2">&nbsp;</td></tr>
	      	    <TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Error in subversion configuration</TD></TR>
	          %endinvoke%
	          %invoke wm.server.vcsservices:readVCS%
		  %onerror%
		  	<tr><td colspan="2">&nbsp;</td></tr>
		  	<TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Error in subversion configuration</TD></TR>
	          %endinvoke%
	      %endif%
	      %ifvar actionPerformed equals('disconnect')%
	      	  %invoke wm.server.svn.svnservices:disconnect%
	      	  	<tr><td colspan="2">&nbsp;</td></tr>
	      	  	<TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Subversion disconnected successfully.</TD></TR>
	      	  %onerror%
	      	      	<tr><td colspan="2">&nbsp;</td></tr>
	      	  	<TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Error in subversion configuration</TD></TR>
	      	  %endinvoke%
	      	  %invoke wm.server.vcsservices:readVCS%
	      	  %onerror%
	      		<tr><td colspan="2">&nbsp;</td></tr>
	      		<TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Error in subversion configuration</TD></TR>
	      	  %endinvoke%

	      %endif%
	      	  		    
	      %ifvar svnConfigured equals('true')%
		<FORM NAME="configForm1" ACTION="solutions-vcs-manageVCSSettings-configureSVN.dsp" METHOD="POST">		
  	      <TABLE class="tableForm" cellpadding=5>
	      	<TR>
			<TD class="heading" colspan=2>Subversion Configuration</TD>
		</TR>
		<tr>
			<TD class="oddrow-l">
				<p align=right>Subversion Repository Location:</p>
			</TD>
			<TD class="oddrow-l">%value svnRepositoryLocation%</td>
		</tr>
		<tr>
				<TD class="oddrow-l">
					<p align=right>Delete Subversion Metadata Files:</p>
				</TD>
				<TD class="oddrow-l"><INPUT TYPE="CHECKBOX" NAME="deleteSVNFiles"  %ifvar deleteSVNFiles equals('true')% CHECKED %endif%></INPUT></td>
			</tr>
		<tr>
			<TD class="action" colspan=2>
			<TABLE>
				<tr>
				<td>

			
				<INPUT TYPE="hidden" NAME="actionPerformed" VALUE="disconnect"></INPUT>
				<INPUT TYPE="hidden" NAME="svnConfigured" size=70 VALUE=""></INPUT>
				<INPUT TYPE="submit" VALUE="Disconnect" ONCLICK="return check_configForm1();"></INPUT>
				
				</td>
				</tr>
			</TABLE>	
			</td>
		</tr>
		    
	     </table>
</FORM>
	%endif%	
 %ifvar svnConfigured equals('false')%
      	<TABLE class="tableForm" cellpadding=5>
  	      
	      	    <FORM NAME="configForm" ACTION="solutions-vcs-manageVCSSettings-configureSVN.dsp" METHOD="POST">
       	      		<TR>
    	      			 <TD class="heading" colspan=2>Subversion Configuration</TD>
    	      		</TR>
    	      		<tr>
    	      		 	 <TD class="oddrow-l">
    	      		 	 	<p align=right><label for="svnRepositoryLocation">Subversion Repository Location:</label></p>
    	      		 	</TD>
    	      		  	<TD class="oddrow-l"><INPUT NAME="svnRepositoryLocation" id="svnRepositoryLocation" size=70 VALUE="%value svnRepositoryLocation%"></INPUT></td>
    	      		</tr>
<tr>
				<TD class="oddrow-l">
					<p align=right><label for="reconnectingRepository">Reconnect to Subversion:</label></p>
				</TD>
				<TD class="oddrow-l"><INPUT TYPE="CHECKBOX" id="reconnectingRepository" NAME="reconnectingRepository"  %ifvar reconnectingRepository equals('true')% CHECKED %endif%></INPUT></td>
			</tr>

    	      		<tr>
    	      			<TD class="action" colspan=2>
					<INPUT TYPE="hidden" NAME="actionPerformed" VALUE="change"></INPUT>
					<INPUT TYPE="submit" VALUE="Save Changes"  ONCLICK="return check();"></INPUT>
				</td>
		    	</tr>
		    </FORM>
	</table>
	%endif%		      	  	
	    </TR>	
</TABLE>
</DIV>
</BODY>
</HTML>
