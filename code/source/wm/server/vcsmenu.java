package wm.server;

public class vcsmenu
{
	public static final Object[][][] toArray() 
    {
		return contents;
	}

	private static final Object contents[][][] = 
    {
		{
            { "name",      "About"               },
			{ "url",       "/WmVCS/about.dsp"    },
			{ "tabhelp",   "body"                },
		},
		{
            { "name",      "Settings"            },
			{ "url",       "/WmVCS/settings.dsp" },
			{ "tabhelp",   "body"                },
		},
	};

}

