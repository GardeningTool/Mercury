package cafe.mercury.anticheat.tracker.impl;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.*;
import cafe.mercury.anticheat.tracker.Tracker;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;

@Getter
public class ActionTracker extends Tracker {

    private boolean digging;
    private boolean placing;
    private boolean attacking;
    private boolean sneaking;
    private boolean sprinting;

    public ActionTracker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void handle(WrappedPacket paramPacket) {
        if (paramPacket instanceof WrappedPacketPlayInBlockDig) {
            WrappedPacketPlayInBlockDig packet = (WrappedPacketPlayInBlockDig) paramPacket;
            switch (packet.getPlayerDigType()) {
                case START_DESTROY_BLOCK:
                    digging = true;
                    break;
                case ABORT_DESTROY_BLOCK:
                case STOP_DESTROY_BLOCK:
                    digging = false;
                    break;
            }
        } else if (paramPacket instanceof WrappedPacketPlayInEntityAction) {
            WrappedPacketPlayInEntityAction packet = (WrappedPacketPlayInEntityAction) paramPacket;

            switch (packet.getAction()) {
                case START_SNEAKING: {
                    sneaking = true;
                    break;
                }
                case STOP_SNEAKING: {
                    sneaking = false;
                    break;
                }
                case START_SPRINTING: {
                    sprinting = true;
                    break;
                }
                case STOP_SPRINTING: {
                    sprinting = false;
                    break;
                }
            }
        } else if (paramPacket instanceof WrappedPacketPlayInUseEntity) {
            WrappedPacketPlayInUseEntity packet = (WrappedPacketPlayInUseEntity) paramPacket;

            if (packet.getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                attacking = true;
            }
        } else if (paramPacket instanceof WrappedPacketPlayInBlockPlace) {
            placing = true;
        } else if (paramPacket instanceof WrappedPacketPlayInFlying) {
            attacking = placing = false;
        }
    }
}
