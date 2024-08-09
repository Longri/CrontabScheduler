package com.botiss.prtg.backup.crontab;


import de.longri.utils.SystemType;

public class EXCLUDE {
    public static boolean ex() {
//         if(true) return false;
        return SystemType.isMac()||SystemType.isWindows();
    }

}
