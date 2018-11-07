<HTML>
  <HEAD>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
    <META HTTP-EQUIV="Expires" CONTENT="-1">
    <LINK REL="stylesheet" TYPE="text/css" HREF="../WmRoot/webMethods.css">
    <LINK REL="stylesheet" TYPE="text/css" HREF="../WmRoot/top.css">
  <script>
    function launchHelp() {
    var url="";
            if (parent.menu != null){
			    var helpURLFromPage = parent.menu.document.forms["urlsaver"].helpURL.value;
				var helptopic = helpURLFromPage.lastIndexOf("=");				
				var topic = helpURLFromPage.substring(helptopic+1);
				
				%invoke wm.server.admin:getHelpTopicUrlMappings%			
					%ifvar result%
					%loop result%
						var id = "%value id%";
							if(id==topic)
							{
								url="%value url%";
							}
					%endloop%		
					%endif%
				%endinvoke%
				window.open(url, 'help', "directories=no,location=yes,menubar=yes,scrollbars=yes,status=yes,toolbar=yes,resizable=yes", true);
            }
    }

    function loadPage(url)
    {
    window.location.replace(url);
    }

    %ifvar message%
    %ifvar norefresh%%else%
    setTimeout("loadPage('top.dsp')", 30000);
    %endif%
    %endif%
  </script>
  </HEAD>

  <body class="topbar" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0">
    <table border=0 cellspacing=0 cellpadding=0 height=70 width=100%>
      <tr>
        <td>
          <table height=14 width=100% cellspacing=0 cellpadding=0 border=0>
		  
            <tr>
              %invoke wm.server.query:getServerInstanceName%

              <td class="saglogo">
                %ifvar productname equals('Integration Agent')%
                  <img src="../WmRoot/images/ia_logo.png" /><br/>
                %else%
					%ifvar productname equals('Microservices Runtime')%
					  <img src="../WmRoot/images/msc_logo.png" /><br/>
					%else%
					  <img src="../WmRoot/images/is_logo.png" /><br/>
					%endif%
                %endif%
              </td>
			  
              <TD nowrap class="toptitle" width="100%">
                   %ifvar instancename%
                     %value instancename encode(html)% ::
                   %endif%
				   %value $host%
				   %invoke wm.server.query:getCurrentUser%
				   %ifvar username%
				    :: %value  username%
				   %endif%
				   %endinvoke%
              </TD>

              <TD nowrap class="topmenu">
          	<A href="#" onclick="launchHelp();return false;" target="_blank">Help</A>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      
      <tr height=100%>
      </tr>
  </table>
  </body>
</html>
