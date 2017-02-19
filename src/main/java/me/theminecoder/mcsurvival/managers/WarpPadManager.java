package me.theminecoder.mcsurvival.managers;

import com.google.common.collect.Lists;
import me.theminecoder.mcsurvival.objects.WarpPad;

import java.util.List;

/**
 * @author theminecoder
 * @version 1.0
 */
public final class WarpPadManager {

    private static List<WarpPad> warpPads = Lists.newArrayList();

    private WarpPadManager(){
    }

    public static List<WarpPad> getWarpPads() {
        return warpPads;
    }

}
