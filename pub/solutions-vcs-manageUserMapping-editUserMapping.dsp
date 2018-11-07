<HTML>
<HEAD>
<TITLE>Edit User Mapping</TITLE>
<LINK REL="stylesheet" TYPE="text/css" HREF="../WmRoot/webMethods.css">
<SCRIPT SRC="../WmRoot/webMethods.js"></SCRIPT>
<SCRIPT SRC="../WmRoot/users.js"></SCRIPT>
<SCRIPT>

    function save()
    {

	if (!verifyRequiredField("editForm","vcsUsername"))
		               {
		               	 
		                  alert("VCS User Name is required");
		                  return false;
	         }
	        
	         
	      
	      else if (document.editForm.pwdVCS.value!=document.editForm.rePwdVCS.value)
	               {
	               	 
	                  alert("Password fields do not match");
	                  document.editForm.pwdVCS.value="";
	                  document.editForm.rePwdVCS.value="";
	                  document.editForm.pwdVCS.focus();
	                  return false;
	         } 
	         else
	         {
	              
	         	return true;
        	 }
	
}
  </SCRIPT>  
</HEAD>


<BODY onLoad="setNavigation('/WmVCS/solutions-vcs-manageUserMapping.dsp', '/WmRoot/doc/OnlineHelp/wwhelp/wwhimpl/js/html/wwhelp.htm#context=is_help&topic=IS_Solutions_VCS_EditUserMapScrn');">
      <DIV class="position">
         <TABLE WIDTH="100%">
            <TR>
               <TD class="menusection-Solutions" colspan=2>VCS &gt; User Mapping &gt; Edit User Mapping</TD>
            </TR>
		<TR>
		<TD colspan="2">
		  <UL>
		    <LI>
			
			<script>
			  createForm("htmlform_solutions_vcs_manageUserMapping", "solutions-vcs-manageUserMapping.dsp", "POST", "BODY");
			</script>
		    	  <script>getURL("solutions-vcs-manageUserMapping.dsp","javascript:document.htmlform_solutions_vcs_manageUserMapping.submit();","Return to Manage User Mapping");</script>
		    </LI>
		    <LI>
			<script>
			  createForm("htmlform_solutions_vcs_manageUserMapping_createNewUserMapping", "solutions-vcs-manageUserMapping-createNewUserMapping.dsp", "POST", "BODY");
			</script>
		          <script>getURL("solutions-vcs-manageUserMapping-createNewUserMapping.dsp","javascript:document.htmlform_solutions_vcs_manageUserMapping_createNewUserMapping.submit();","Create New User Mapping");</script>
		    </LI>
		  </UL>
		</TD>
	      </TR>
	      	<TR>
	       <TD></TD>
	      	<TD>
	      	  <TABLE class="tableForm" cellpadding=5>
	      	    <FORM NAME="editForm" METHOD="POST" ACTION="solutions-vcs-manageUserMapping.dsp">
	      		<TR>
	      		  <TD class="heading" colspan=4>Edit User Mapping</TD>
	      		</TR>
	      		<tr>
	      		  <TD class="oddrow" ><p align=right><label for="isUsername">Integration Server User Name</label></p></TD>
	      		   <TD class="oddrow" ><INPUT TYPE="TEXT" size=50 NAME="isUsername" ID="isUsername"  VALUE="%value isUsername%" READONLY></INPUT></TD>
	      		</TR>  
	      		<TR> 
	      		 <TD class="evenrow"><p align=right><label for="vcsUsername">VCS User Name</label></p></TD>
	      		 <TD class="evenrow" ><INPUT TYPE="TEXT" size=50 NAME="vcsUsername" ID="vcsUsername"  VALUE="%value vcsUsername%"></INPUT></TD>
	      		</TR>
	      		<tr>
			  <TD class="oddrow" ><p align=right><label for="pwdVCS">VCS Password</label></p></TD>
			  <TD class="oddrow" ><INPUT TYPE="PASSWORD" autocomplete="off" size=50 NAME="pwdVCS" ID="pwdVCS" ></INPUT></TD>
	      		</TR>  
	      		<tr>
			  <TD class="evenrow" ><p align=right><label for="rePwdVCS">Confirm VCS Password</label></p></TD>
			  <TD class="evenrow" ><INPUT TYPE="PASSWORD" autocomplete="off" size=50 NAME="rePwdVCS" ID="rePwdVCS" ></INPUT></TD>
	      		</TR>  
	      		<tr>
			 <TD  class="action" colspan=2 >
			 	<INPUT TYPE="hidden" NAME="action" VALUE="edit"></INPUT>
			 	<INPUT TYPE="submit" VALUE="Update Changes" ONCLICK="return save();"</INPUT>
			 	</INPUT></TD>
	      		</TR> 
	      	
	      	
	      	</FORM>
	    </table>
	  </TD>
	  </TR>
	</TABLE>
	</DIV>
</BODY>
</HTML>
	
