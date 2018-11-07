<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<HTML>
<HEAD>
<LINK REL="stylesheet" TYPE="text/css" HREF="../WmRoot/webMethods.css">
<TITLE>VCS</TITLE>
<SCRIPT src="../WmRoot/webMethods.js"></SCRIPT>
   </HEAD>
    <BODY onLoad="setNavigation('/WmVCS/solutions-vcs.dsp', '/WmRoot/doc/OnlineHelp/wwhelp/wwhimpl/js/html/wwhelp.htm#context=is_help&topic=IS_Solutions_VCS_ConfigScrn');">
      <DIV class="position">
         <TABLE WIDTH="100%">
            <TR>
               <TD class="menusection-Solutions" colspan=2>VCS &gt; Configuration</TD>
            </TR>

  	   <TR>
               <TD colspan=2>
                    <UL>
                        <LI>
						<script>
					       createForm("htmlform_solutions_vcs_manageVCSSettings", "solutions-vcs-manageVCSSettings.dsp", "POST", "BODY");
						</script>
						<script>getURL("solutions-vcs-manageVCSSettings.dsp","javascript:document.htmlform_solutions_vcs_manageVCSSettings.submit();","Edit Configuration");</script>
                        </LI>
                    </UL>
               </TD>
  	    </TR>
         </TABLE>
         <TABLE WIDTH="100%" class="tableView">
  	     <TR>
		   	<TD colspan=3 class="heading">General</TD>
	     	  </TR>
	     	  %invoke wm.server.vcsservices:readVCS%
	     	  <TR>
	     		<TD  class="oddrow" width="40%"><p ALIGN="left">Version Control System</p></TD>
	     		%ifvar vcsType equals('Microsoft Visual SourceSafe')%
				%ifvar vssList -notempty%
					<TD class="oddrow-l" colspan=2>%value vcsType%</TD>
				%else%
					<TD class="oddrow-l" colspan=2>----None----</TD>
				%endif%	
			%endif%
			%ifvar vcsType equals('ClearCase')%
				%ifvar clearcaseList -notempty%
					<TD class="oddrow-l" colspan=2>%value vcsType%</TD>
				%else%
					<TD class="oddrow-l" colspan=2>----None----</TD>
				%endif%	
			%endif%
                        %ifvar vcsType equals('Subversion')%
				%ifvar svnList -notempty%
					<TD class="oddrow-l" colspan=2>%value vcsType%</TD>
				%else%
					<TD class="oddrow-l" colspan=2>----None----</TD>
				%endif%	
			%endif%
			%ifvar vcsType equals('---None---')%
				<TD class="oddrow-l" colspan=2>%value vcsType%</TD>
			%endif%
	     		
		  </TR>
		  %onerror%
		   <tr><td colspan="2">&nbsp;</td></tr>
	           <TR><TD class="message" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Error in loading settings</TD></TR>
		  %endinvoke%
		  
</TABLE>
</DIV>
</BODY>
</HTML>
