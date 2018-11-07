<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<HTML>
<HEAD>
<TITLE>Clear Case Configuration</TITLE>
<LINK REL="stylesheet" TYPE="text/css" HREF="../WmRoot/webMethods.css">
<SCRIPT SRC="../WmRoot/webMethods.js"></SCRIPT>
<SCRIPT>

    function check()
    {
    
     if (!verifyRequiredField("configForm","timeout"))
                { 
           		document.configForm.timeout.value=60000;
                }
          
     if(document.configForm.timeout.value==-1)     
          {
          	 	document.configForm.timeout.value=-1;
          }
          
     if (!isNum(document.configForm.timeout.value) && (document.configForm.timeout.value!=-1))
                      {
                                alert("Command Timeout must be a positive number");
                                document.configForm.timeout.value="";
                                document.configForm.timeout.focus();
                               return false;
                      }
    	
     if(!verifyRequiredNonNegNumber("configForm", "timeout") && (document.configForm.timeout.value!=-1))
                		{
                		alert("Command Timeout must be a positive number");
          			document.configForm.timeout.value="";
          	                document.configForm.timeout.focus();
          	                return false;
            		}
    
     if (verifyRequiredField("configForm","cWorkingFolder") &&
     	!(verifyRequiredField("configForm","ccFolder")) || (!verifyRequiredField("configForm","cWorkingFolder") &&
     	(verifyRequiredField("configForm","ccFolder"))))
     	{
     		if(document.configForm.cWorkingFolder.value==null || document.configForm.cWorkingFolder.value=="")
     			{
     				alert("No Working Folder Name was entered");
				document.configForm.cWorkingFolder.focus();
				return false;
			}
		else	
			{
				alert("No ClearCase Folder Name was entered");
				document.configForm.ccFolder.focus();
				return false;
			}
         }        
     else if(!verifyRequiredField("configForm","cWorkingFolder") &&
     	     !(verifyRequiredField("configForm","ccFolder")))
     {
     		return true;
     }
      
     	
     
     }
 </SCRIPT>    
</HEAD>
<BODY onLoad="setNavigation('/WmVCS/solutions-vcs-manageVCSSettings-configureClearCase.dsp', '/WmRoot/doc/OnlineHelp/wwhelp/wwhimpl/js/html/wwhelp.htm#context=is_help&topic=IS_Solutions_VCS_ClearCaseConfigScrn');">
       <DIV class="position">
          <TABLE WIDTH="100%">
             <TR>
                <TD class="menusection-Solutions" colspan=2>VCS &gt; Configuration &gt; Edit VCS Configuration &gt; ClearCase Configuration</TD>
             </TR>
              %ifvar actionPerformed equals('change')%
              %invoke wm.server.vcsservices:clearcaseConfiguration%
	     	
	     	       		    <tr><td colspan="2">&nbsp;</td></tr>
	     	       	           <TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Settings changed successfully</TD></TR>
	     	 %onerror%
		<tr><td colspan="2">&nbsp;</td></tr>
	        <TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Error in loading Settings</TD></TR>
	     	%endinvoke%       	       
 		%endif%
  		<TR>
 		<TD colspan="2">
 		  <UL>
 		    <LI>
			<script>
			  createForm("htmlform_solutions_vcs_manageVCSSettings", "solutions-vcs-manageVCSSettings.dsp", "POST", "BODY");
			</script>
 		   <script>getURL("solutions-vcs-manageVCSSettings.dsp","javascript:document.htmlform_solutions_vcs_manageVCSSettings.submit();","Return to Edit VCS Configuration");</script>
 		    </LI>
 		  </UL>
 		</TD>
	      </TR>
	       <TR>
	      	<TD></TD>
	      	<TD>
	      	  <TABLE class="tableForm" cellpadding=5>
	      	    <FORM NAME="configForm" ACTION="solutions-vcs-manageVCSSettings-configureClearCase.dsp" METHOD="POST">
	      		
	      		%invoke wm.server.vcsservices:readVCS%
	      		
	      		<TR>
	      		   <TD class="heading" colspan=2>
	      		 	ClearCase Configuration
	      		   </TD>
	      		</TR>
	      		<tr>
			  <TD class="oddrow-l">
			    <p align=right><label for="timeout">Command Timeout (msec)</label></p>
			</TD>
			<TD class="oddrow-l"><INPUT name="timeout" id="timeout" VALUE="%value timeout%"></INPUT></td>
	      		</tr>
	      		<tr>
			    <TD class="evenrow-l">
				     <p align=right><label for="ccFolder">ClearCase View Directory</label>
				     </p>
			    </TD>
			    <TD class="evenrow-l">
				    <INPUT name="ccFolder" id="ccFolder" size=70 VALUE="%value clearcaseViewDir%"></INPUT></td>  
			
	      		</tr>
	      		<tr>
				<TD class="oddrow-l">
				     <p align=right><label for="cWorkingFolder">Working Directory</label>
				      </p>
				 </TD>
				 <TD class="oddrow-l">
				 <INPUT name="cWorkingFolder" id="cWorkingFolder" size=70 VALUE="%value clearcaseWorkingFolder%"></INPUT></td>  
				      		    
	      		</tr>
	      		 <TR>
				<TD class="evenrow-l">
					<p align=right><label for="checkOutMode">Checkout Mode</label>
					</p>
				 </TD>
				 <TD class="evenrow-l" VALIGN="Center">
					<SELECT name="checkOutMode" id="checkOutMode">
					%ifvar checkoutMode equals('reserved')%
					<OPTION SELECTED VALUE="reserved">Reserved</OPTION>
					<OPTION VALUE="unreserved">Unreserved</OPTION>
					<OPTION VALUE="both">Both</OPTION>	      
					%endif%
					%ifvar checkoutMode equals('unreserved')%
					<OPTION VALUE="reserved">Reserved</OPTION>
					<OPTION SELECTED VALUE="unreserved">Unreserved</OPTION>
					<OPTION VALUE="both">Both</OPTION>	      
					%endif%
					%ifvar checkoutMode equals('both')%
					<OPTION VALUE="reserved">Reserved</OPTION>
					<OPTION VALUE="unreserved">Unreserved</OPTION>
					<OPTION SELECTED VALUE="both">Both</OPTION>	      
					%endif%
					%ifvar checkoutMode equals('')%
					<OPTION VALUE="reserved">Reserved</OPTION>
					<OPTION VALUE="unreserved">Unreserved</OPTION>
					<OPTION VALUE="both">Both</OPTION>	      
					%endif%
					%ifvar checkoutMode -isnull%
					<OPTION VALUE="reserved">Reserved</OPTION>
					<OPTION VALUE="unreserved">Unreserved</OPTION>
					<OPTION VALUE="both">Both</OPTION>	      
					%endif%
					</SELECT>
				 </TD>
									      
                          </TR>
	      		<tr>
	      		    <TD class="oddrow-l">
				    <p align=right><label for="branchName">ClearCase Branch Name</label>
				    </p>
	      		    </TD>
	      		    <TD class="evenrow-l">
	      		  	     <INPUT name="branchName" id="branchName" size=70 VALUE=%value clearcaseBranchName%></INPUT></td>
	      		</tr>
	      		  		
	      		
	      		<tr>
	      		  <TD class="action" colspan=2>
	      		  		<INPUT TYPE="hidden" NAME="actionPerformed" VALUE="change"></INPUT>
	      		    	     <INPUT TYPE="submit" VALUE="Save Changes" ONCLICK="return check();"></INPUT>
	      		    	     </td>
	      		</tr>
	      		 %onerror%
					   <tr><td colspan="2">&nbsp;</td></tr>
				           <TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Error in loading settings</TD></TR>
			  %endinvoke%
	      	    </FORM>
	      	  </table>
	      	</TD>
	      </TR>
	    </TABLE>
	  </DIV>
	</BODY>
</HTML>

