<HTML>
<HEAD>
<TITLE>Create new user mapping</TITLE>
<LINK REL="stylesheet" TYPE="text/css" HREF="../WmRoot/webMethods.css">
<SCRIPT SRC="../WmRoot/webMethods.js"></SCRIPT>
<SCRIPT SRC="../WmRoot/users.js"></SCRIPT>
<SCRIPT>

   function trim(str)
 	{
 		var retval = "";
 		var start = str.length;
 		var end = 0;
 		for(i = 0; i < str.length; i++)
 		{
 			if(start > i)
 			{
 				if(str.charAt(i) != " ")
 				{
 					start = i;
 				}
 			}
 
 			if(str.charAt(i) != " ")
 			{
 				end = i;
 			}
 		}
 
 		if(start <= end)
 		{
 			retval = str.substr(start, (end-start+1));
 		}
 
 		return retval;
 	}
 
   function checkLegalUserName(username)
   {
     var illegalChars = "\"\\\n";
 
     for (var i=0; i<illegalChars.length; i++)
     {
       if (username.indexOf(illegalChars.charAt(i)) >= 0)
         return illegalChars.charAt(i);
     }
     return "";
 
   }
 
   function check()
   {
     var covered = true;
     var passwdProvided = false;
     var userList = document.configForm.isUsername.value;
     var vcsUser = document.configForm.vcsUsername.value;
     var passField1 = document.configForm.pwdVCS.value;
     var passField2 = document.configForm.rePwdVCS.value;
     var starStart = /^\*+/;
     var userPass = userList.split("\n");
 
     if(userList == "")
     {
       covered = false;
       alert("No users were entered into the list");
     }
     
     else if( (passField1 == "") && (passField2 != "") )
     {
       covered = false;
       alert("Password fields do not match");
     }
     else if( (passField1 != "") && (passField2 == "") )
     {
       covered = false;
       alert("Password fields do not match");
     }
     else
     {
       if( (passField1 != "") && (passField2 != "") )
       {
         if(passField1 != passField2)
         {
           covered = false;
           alert("Password fields do not match");
           return false;
         }
         else if( passField1.search(starStart) >= 0 )
         {
           covered = false;
           alert("Passwords must not start with *");
           return false;
         }
         else
         {
           passwdProvided = true;
         }
         
       }
       
 
       
      for (var i = 0; i < userPass.length; i++)
       {
         
        var pair = userPass[i].split(";");
        if(pair.length < 3)
         {
           if(pair.length ==1)
         	{
         	  if(vcsUser=="")
         	  {	
         	  	alert("VCS User Name is required");	
         	  	covered = false;
         	  	passwdProvided = false;
         	  	break;
         	  }
         	  else
         	  {
         	  	passwdProvided = true;
         	  }
         	  
         	 }
           if(pair.length ==2)
           	   {
           	    if(pair[1]=="" && vcsUser=="")
 		     {
 		       	covered = false;
 		       	passwdProvided = false;
 		     	alert("VCS User Name is required");
 		     	break;
 		     }
 		     else
         	 	 {
         	  	passwdProvided = true;
         		 }		        	      
           	   }
        }
         pair[0] = trim(pair[0]);
         pair[1] = trim(pair[1]);
         
         var rc = checkLegalUserName (pair[0]);
         
         if (rc != "")
         {
           alert ("Username: " + pair[0] + " contains illegal character \"" + rc + "\"");
           return false;
         }
                 
         var rc2 = checkLegalUserName (pair[1]);
	          
	          if (rc2 != "")
	          {
	            alert ("VCSUsername: " + pair[1] + " contains illegal character \"" + rc2 + "\"");
	            return false;
	          }
	          
	 if(pair.length >= 3)
         {
           pair[1] = trim(pair[1]);
           pair[2]=trim(pair[2]);
 
           if(pair[0] == "" || pair[1] == "")
           {
             covered = false;
           }
           
          }
 
         if(!passwdProvided && !covered )
         {
           alert("One or more users do not have a password specified");
           break;
         }
       }
                 
              
     }
       return covered || passwdProvided;
   }
 

 </SCRIPT>   
 </HEAD>
 
<BODY onLoad="setNavigation('/WmVCS/solutions-vcs-manageUserMapping-createNewUserMapping.dsp', '/WmRoot/doc/OnlineHelp/wwhelp/wwhimpl/js/html/wwhelp.htm#context=is_help&topic=IS_Solutions_VCS_CreateUserMapScrn');">
       <DIV class="position">
          <TABLE WIDTH="100%">
             <TR>
                <TD class="menusection-Solutions" colspan=2>VCS &gt; User Mapping &gt; Create New User Mapping</TD>
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
 		  </UL>
 		</TD>
	      </TR>
	       <TR>
	      	<TD></TD>
	      	<TD>
	      	  <TABLE class="tableForm">
	      	    <FORM NAME="configForm" ACTION="solutions-vcs-manageUserMapping.dsp" METHOD="POST">
	 		<TR>
	      		 <TD class="heading" colspan=2>Create New User Mapping</TD>
	      		</TR>
	      		<tr>
	      			<TD class="oddrow-l"><P ALIGN=right><label for="isUsername">Integration Server User Name</label></P></TD>
				<TD class="oddrow-l">One "IS user name" or<br>
				      		    "IS user name;VCS user name;VCS password" per line.<br>
				      		  <textarea wrap="off" rows="5" name="isUsername" id="isUsername" cols="35"></textarea>
				      		  
			<tr>
				<TD class="evenrow-l" colspan=2>The password below will apply to all new users that do not have a password specified above.</td>
	      		</tr>
	      		<tr>
	      		  <TD class="oddrow-l"><P ALIGN=right><label for="vcsUsername">VCS User Name</label></P></TD>
	      		  <TD class="oddrow-l"><INPUT name="vcsUsername" id="vcsUsername"></INPUT></td>
	      		</tr>
	      		    		  
	      		<tr><TD class="evenrow-l"><P ALIGN=right><label for="pwdVCS">VCS Password</label></P</TD>
	      		    <TD class="evenrow-l"><INPUT TYPE="Password" name="pwdVCS" id="pwdVCS" autocomplete="off"></INPUT></td>  
	      		  </td>
	      		</tr>
	      		<tr><TD class="oddrow-l"><P ALIGN=right><label for="rePwdVCS">Confirm VCS Password</label></P></TD>
			    <TD class="oddrow-l"><INPUT TYPE="Password" name="rePwdVCS" id="rePwdVCS" autocomplete="off"></INPUT></td>  
			  </td>
	      		</tr>
	      		<tr>
	      		  <TD class="action" colspan=2>
	      		  	<INPUT TYPE="hidden" NAME="action" VALUE="addMultipleUsers"></INPUT>
	      		    <INPUT TYPE="submit" VALUE="Save Changes" ONCLICK="return check();"></INPUT>
	      		  </td>
	      		</tr>
	      	    </FORM>
	      	  </table>
	      	</TD>
	      </TR>
	    </TABLE>
	</DIV>    
  </BODY>
</HTML>