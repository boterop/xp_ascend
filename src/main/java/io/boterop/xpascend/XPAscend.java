package io.boterop.xpascend;

import io.boterop.xpascend.events.XPEventHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(XPAscend.MODID)
public class XPAscend {
    public static final String MODID = "xpascend";

    public XPAscend(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(XPEventHandler.class);
    }
}
