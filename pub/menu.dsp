<html>
  <head>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Expires" content="-1">
    <link rel="stylesheet" type="text/css" href="../WmRoot/webMethods.css">
    <style>
      body { border-top: 1px solid #97A6CB; }
    </style>
    <script src="../WmRoot/common-menu.js"></script>
    <script src="../WmRoot/csrf-guard.js"></script>
    <script type="text/javascript">
var selected = null;
var menuInit = false;

function menuSelect(object, id) {
  selected = menuext.select(object, id, selected);
}

function menuMouseOver(object, id) {
  menuext.mouseOver(object, id, selected);
}

function menuMouseOut(object, id) {
  menuext.mouseOut(object, id, selected);
}

function initMenu(firstImage) {
    menuInit = true;
    return true;
}
</script>

  </head>

  <body class="menu" onload="initMenu('')">
    <form name="urlsaver">
      <input type="hidden" name="helpURL" value="doc/OnlineHelp/WmRoot.htm#CS_Server_Statistics.htm">
    </form>

    <table class="menuTable" width="100%" cellspacing="0" cellpadding="0" border="0">
      %scope param(expanded='true') param(text='VCS')%
        %include ../../WmRoot/pub/menu/section-top.dsp%
          VCS
        %include ../../WmRoot/pub/menu/section-bottom.dsp%
      %endscope%

      %scope param(section='VCS') param(text='Configuration') param(url='solutions-vcs.dsp')%
        %include ../../WmRoot/pub/menu-item-start.dsp%
          %scope%
          %rename text inString -copy%
          %invoke wm.server.csrfguard:replaceSpecialCharacters%
            <script>
              if (is_csrf_guard_enabled && needToInsertToken) {
                createFormWithTargetAndSetProperties("htmlform_menu_subelement_%value replacedString%", "%value encode(javascript) url%", "POST", "BODY", "%ifvar target%%value $host%%value target%%else%body%endif%");
                setFormProperty("htmlform_menu_subelement_%value replacedString%", _csrfTokenNm_, _csrfTokenVal_);
                document.write('<a id="a%value encode(htmlattr) url%" href="javascript:htmlform_menu_subelement_%value replacedString%.submit()"> Configuration %ifvar target% ... %endif% </a>');
              } else {
                document.write('<a id="a%value encode(htmlattr) url%" target="%ifvar target%%value $host%%value target%%else%body%endif%" href="%value encode(javascript) url%"> Configuration %ifvar target% ... %endif% </a>');
              }
            </script>
          %endinvoke%
          %endscope%
          </span>
        </td>
      </tr>
      %endscope%

      %scope param(section='VCS') param(text='UserMapping') param(url='solutions-vcs-manageUserMapping.dsp')%
        %include ../../WmRoot/pub/menu-item-start.dsp%
          %scope%
          %rename text inString -copy%
          %invoke wm.server.csrfguard:replaceSpecialCharacters%
            <script>
              if (is_csrf_guard_enabled && needToInsertToken) {
                createFormWithTargetAndSetProperties("htmlform_menu_subelement_%value replacedString%", "%value encode(javascript) url%", "POST", "BODY", "%ifvar target%%value $host%%value target%%else%body%endif%");
                setFormProperty("htmlform_menu_subelement_%value replacedString%", _csrfTokenNm_, _csrfTokenVal_);
                document.write('<a id="a%value encode(htmlattr) url%" href="javascript:htmlform_menu_subelement_%value replacedString%.submit()"> User Mapping %ifvar target% ... %endif% </a>');
              } else {
                document.write('<a id="a%value encode(htmlattr) url%" target="%ifvar target%%value $host%%value target%%else%body%endif%" href="%value encode(javascript) url%"> User Mapping %ifvar target% ... %endif% </a>');
              }
            </script>
          %endinvoke%
          %endscope%
          </span>
        </td>
      </tr>
      %endscope%
    </table>
  </body>
</html>
