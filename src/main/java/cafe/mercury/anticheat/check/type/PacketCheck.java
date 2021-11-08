package cafe.mercury.anticheat.check.type;

import cafe.mercury.anticheat.check.Check;
import cafe.mercury.anticheat.data.PlayerData;
import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.violation.handler.ViolationHandler;

public abstract class PacketCheck extends Check<WrappedPacket> {

    public PacketCheck(PlayerData playerData, ViolationHandler violationHandler) {
        super(playerData, violationHandler);
    }

    @Override
    public abstract void handle(WrappedPacket paramPacket);

}
