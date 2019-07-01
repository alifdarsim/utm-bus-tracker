package com.codecraft.busutm.Model;

import android.graphics.drawable.Drawable;

import com.codecraft.busutm.MainActivity;
import com.codecraft.busutm.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BusHelper {

    public String getDriverName(String busPlate) {
        switch(busPlate) {
            case "JRC7100": return "AHMAD SHAH BIN ALIMIN";
            case "JKU7100": return "WAN MUHAMMAD FADZLI BIN WAN ABU BAKAR";
            case "JLD7100": return "AZHARI BIN ABDUL HAMID";
            case "JQS7100": return "BAHRON BIN DARKON";
            case "JQF7100": return "KHAIRUL NIZAM BIN MAT NOR";
            case "JQJ7100": return "JAMIL BIN PALIL";
            case "JQR7100": return "ROHAIZAT BIN ZAINEE";
            case "JRA7100": return "KAMALUDDIN BIN ADB RAHMAN";
            case "JPP7100": return "SHAHRIZAT BIN SARMON";
            case "JKC7100": return "MOHD PADLI BIN AHAMD@ISMAIL";
            case "JMC7100": return "AHMAD AZMI BIN PUNGUT";
            case "JMX7100": return "NORHAIZAN BIN SUKARMIN@KARNI";
            case "JPN7100": return "IRWAN HAFIZ BIN BAHAROM";
            case "JMU7100": return "MOHAMAD NOR BIN MAT DAUD";
            case "JKJ7100": return "JAMAL BIN PALIL";
        }
        return null;
    }

    public String getRoute(String busPlate) {
        switch(busPlate) {
            case "JRC7100": return "K9/K10-LINGKARAN ILMU";
            case "JKU7100": return "KTC-K9/K10-LINGKARAN ILMU";
            case "JLD7100": return "FAB-CLUSTER";
            case "JQS7100": return "FAB-FBME(TAMAN U)";
            case "JQF7100": return "KDSE-KDOJ-LINKARAN ILMU";
            case "JQJ7100": return "KDSE-KDOJ-LINKARAN ILMU";
            case "JQR7100": return "KP-LINGKARAN ILMU";
            case "JRA7100": return "KP-LINGKARAN ILMU";
            case "JPP7100": return "KTC-K9/K10-KP-CLUSTER";
            case "JKC7100": return "KTC-K9/K10-KP-CLUSTER";
            case "JMC7100": return "KTR-KTHO-KTDI-LINGKARAN ILMU";
            case "JMX7100": return "KTR-KTHO-KTDI-LINGKARAN ILMU";
            case "JPN7100": return "KTR-KTHO-KTDI-LINGKARAN ILMU";
            case "JMU7100": return "KTC-KTHO-KTDI-FKT";
            case "JKJ7100": return "KTC-KTHO-KTDI-FKT";
        }
        return null;
    }

    public int getBusColor(String busPlate) {
        switch(busPlate) {
            case "JRC7100": return R.drawable.ic_bus_top_cyan;
            case "JKU7100": return R.drawable.ic_bus_top_cyan;
            case "JLD7100": return R.drawable.ic_bus_top_lime;
            case "JQS7100": return R.drawable.ic_bus_top_teal;
            case "JQF7100": return R.drawable.ic_bus_top_amber;
            case "JQJ7100": return R.drawable.ic_bus_top_amber;
            case "JQR7100": return R.drawable.ic_bus_top_red;
            case "JRA7100": return R.drawable.ic_bus_top_red;
            case "JPP7100": return R.drawable.ic_bus_top_purple;
            case "JKC7100": return R.drawable.ic_bus_top_purple;
            case "JMC7100": return R.drawable.ic_bus_top_blue;
            case "JMX7100": return R.drawable.ic_bus_top_blue;
            case "JPN7100": return R.drawable.ic_bus_top_blue;
            case "JMU7100": return R.drawable.ic_bus_top_orange;
            case "JKJ7100": return R.drawable.ic_bus_top_orange;
            case "ADMIN": return R.drawable.ic_bus_top_orange;
        }
        return 0;
    }

    public int getBusColorItem(String busPlate) {
        switch(busPlate) {
            case "JRC7100": return R.drawable.bus_cyan;
            case "JKU7100": return R.drawable.bus_cyan;
            case "JLD7100": return R.drawable.bus_lime;
            case "JQS7100": return R.drawable.bus_teal;
            case "JQF7100": return R.drawable.bus_amber;
            case "JQJ7100": return R.drawable.bus_amber;
            case "JQR7100": return R.drawable.bus_red;
            case "JRA7100": return R.drawable.bus_red;
            case "JPP7100": return R.drawable.bus_purple;
            case "JKC7100": return R.drawable.bus_purple;
            case "JMC7100": return R.drawable.bus_blue;
            case "JMX7100": return R.drawable.bus_blue;
            case "JPN7100": return R.drawable.bus_blue;
            case "JMU7100": return R.drawable.bus_orange;
            case "JKJ7100": return R.drawable.bus_orange;
            case "ADMIN": return R.drawable.bus_orange;
        }
        return 0;
    }



}
