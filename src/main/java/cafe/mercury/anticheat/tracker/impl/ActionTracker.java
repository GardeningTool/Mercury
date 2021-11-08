package cafe.mercury.anticheat.tracker.impl;

import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockDig;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import cafe.mercury.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;
import cafe.mercury.anticheat.tracker.Tracker;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;

@Getter
public class ActionTracker extends Tracker {

    private boolean digging;
    private boolean placing;
    private boolean attacking;

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
