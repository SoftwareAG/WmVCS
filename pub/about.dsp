<?xml version='1.0'?>
<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'
                      '/local/share/xml/XHTML/dtds/xhtml1-transitional.dtd'>

%invoke wm.vcs.admin:getUiProperties%%endinvoke%

<html>
    <head>
        <title>%value uiProperties/productName%</title>
        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'></meta>
        <meta http-equiv="Pragma" content="no-cache"></meta>
        <meta http-equiv="Expires" content="-1"></meta>
        <link rel="stylesheet" type="text/css" href="../WmRoot/webMethods.css"></link>
        <link rel="stylesheet" type="text/css" href="vcs.css"></link>
		<SCRIPT SRC="webMethods.js"></SCRIPT>
    </head>

    <body>
        <p>Geez, it's difficult writing DSPs.</p>

        <p>uiProperties/productName: %value uiProperties/productName%</p>
    
        %invoke wm.vcs.admin:getStartupErrors%
            %scope startupErrors%
                <!-- Show startup errors, if any -->
                <table width=100%>
                    <tr>
                        <td id="message">
                            <b>Errors occured during package startup.</b>
                        </td>
                    </tr>
                    %loop -struct%
                        %scope #$key%
                            <tr class="message">
                                <td id="message">
                                    <b>
									<script>
					                createForm("htmlform_showErrorDetail", "error/showErrorDetail.dsp", "POST", "BODY");
									setFormProperty("htmlform_showErrorDetail", "errorMessage", "%value -urlencode $key%>%value message%");
									</script>
									
									<script>
		        if(is_csrf_guard_enabled && needToInsertToken) {
		     	document.write('<a href="javascript:document.htmlform_showErrorDetail.submit();"</a>');
		       } else {
			document.write('<a href=error/showErrorDetail.dsp?errorMessage=%value -urlencode $key%>%value message%</a>');
		     }
           </script>
									</b>
                                    <a href=/invoke/wm.vcs.error/getErrorDetail?errorMessage=%value -urlencode $key%>[alternate view]</a>
                                    <br>
                                    %ifvar reason%
                                        (caused by %value exceptionClass%: %value reason%)
                                    %endif%
                                </td>
                            </tr>
                        %endscope%
                    %endloop%
                </table>
                <!-- End startup errors -->
            %endscope%
        %endinvoke%
        
        %invoke wm.vcs.admin:getConfiguration%
            %invoke wm.vcs.admin.statistic:getStatistics%
                <table width=100%>
                
                    <tr>
                        <td class="menusection-Adapters" colspan=3>
                            %value uiProperties/button.name% &gt; %value uiProperties/tabs.1.name%
                        </td>
                    </tr>
            
                    <tr>
                        <td valign="top" width=50%>
                            <table class="table2" width=100%>
                                <tr>
                                    <td class="heading" colspan=3>
                                        General
                                    </td>
                                </tr>
                                
                                <tr>
                                    <td class="oddcol" nowrap="nowrap">
                                        %value uiProperties/button.name% Start Date
                                    </td>
                                    <td class="oddrowdata" colspan=2>
                                        %value statistics/packageLoadDate%
                                    </td>
                                </tr>
                                
                                <tr>
                                    <td class="heading" colspan=3>
                                        Logs
                                    </td>
                                </tr>

                                <tr>
                                    <td class="oddcol" nowrap="nowrap">
                                        %value uiProperties/button.name% Log
                                    </td>
                                    <td class="oddrowdata" colspan=2>
                                        <!-- this doesn't seem to work on all browsers -->
                                        <a href=/invoke/wm.vcs.log/readLogFile?logId=WmVCS>view</a>
                                    </td>
                                </tr>
                                
                                <tr>
                                    <td class="oddcol" nowrap="nowrap">
                                        %value uiProperties/button.name% Log Level
                                    </td>
                                    <td class="oddrowdata" colspan=1>
                                        %ifvar configuration/logLevel%
                                            %value configuration/logLevel%
                                        %else%
                                            Use server log level
                                        %endif%
                                    </td>
                                    <td class="oddrowdata">
									<script>
					                createForm("htmlform_editLogLevel", "editLogLevel.dsp", "POST", "BODY");
									setFormProperty("htmlform_editLogLevel", "logId", "WmVCS");
									</script>
                                        <script>getURL("editLogLevel.dsp?logId=WmVCS","javascript:document.htmlform_editLogLevel.submit();","change");</script>
                                    </td>
                                </tr>
                                
                                <tr>
                                    <td class="oddcol" nowrap="nowrap">
                                        %value uiProperties/button.name% Errors
                                    </td>
                                    <td class="oddrowdata" colspan=2>
									<script>
					                createForm("htmlform_showErrors", "error/showErrors.dsp", "POST", "BODY");
									</script>
                                        <script>getURL("error/showErrors.dsp","javascript:document.htmlform_showErrors.submit();","view");</script>
                                    </td>
                                </tr>
                                
                                <tr>
                                    <td class="oddcol" nowrap="nowrap">
                                        %value uiProperties/button.name% Envelope Log
                                    </td>
                                    <td class="oddrowdata" colspan=2>
									<script>
					                createForm("htmlform_envelopeLogging", "envelopeLogging.dsp", "POST", "BODY");
									</script>
									<script>getURL("envelopeLogging.dsp","javascript:document.htmlform_envelopeLogging.submit();","view");</script>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    
                        <td valign="top" width=50%>
                            <table width=100%>
                                <tr>
                                    <td class="heading" colspan=2>
                                        Test
                                    </td>
                                </tr>
                                <tr>
                                    <td class="oddcol" nowrap="nowrap" colspan=2>
									<script>
					                createForm("htmlform_chooseTest", "../test/chooseTest.dsp", "POST", "BODY");
									</script>
                                        <script>getURL("../test/chooseTest.dsp","javascript:document.htmlform_chooseTest.submit();","Test Connection");</script>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="oddcol" nowrap="nowrap" colspan=2>
									<script>
					                createForm("htmlform_submitEnvelopeToService", "../test/submitEnvelopeToService.dsp", "POST", "BODY");
									</script>
                                        <script>getURL("../test/submitEnvelopeToService.dsp","javascript:document.htmlform_submitEnvelopeToService.submit();","Test Send/Receive");</script>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="oddcol" nowrap="nowrap" colspan=2>
									<script>
					                createForm("htmlform_parseStringTest", "../test/parseStringTest.dsp", "POST", "BODY");
									</script>
                                        <script>getURL("../test/parseStringTest.dsp","javascript:document.htmlform_parseStringTest.submit();","Validate Document");</script>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
                <!-- end of top table -->

                %include envelopeStatisticsTable.dsp%

            %onerror%
                <!--
                %loop -struct%
                    %value $key%: %value%
                %endloop%
                -->
                %include ../../../WmVCS/pub/error/onerrorTable.dsp%
            %endinvoke%
        
        %onerror%
            <!--
            %loop -struct%
                %value $key%: %value%
            %endloop%
            -->
            %include ../../../WmVCS/pub/error/onerrorTable.dsp%
        %endinvoke%
    </body>
</html>
