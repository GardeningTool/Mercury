package cafe.mercury.anticheat.packet;

import cafe.mercury.anticheat.packet.wrapper.WrappedPacket;
import cafe.mercury.anticheat.packet.wrapper.client.*;
import cafe.mercury.anticheat.packet.wrapper.server.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ClassWrapper {

    private static final Map<PacketType, Constructor<? extends WrappedPacket>> PACKET_MAP = new HashMap<>();

    static {
        PACKET_MAP.put(PacketType.Play.Client.ABILITIES, getConstructor(WrappedPacketPlayInAbilities.class));
        PACKET_MAP.put(PacketType.Play.Client.ARM_ANIMATION, getConstructor(WrappedPacketPlayInArmAnimation.class));
        PACKET_MAP.put(PacketType.Play.Client.BLOCK_DIG, getConstructor(WrappedPacketPlayInBlockDig.class));
        PACKET_MAP.put(PacketType.Play.Client.BLOCK_PLACE, getConstructor(WrappedPacketPlayInBlockPlace.class));
        PACKET_MAP.put(PacketType.Play.Client.CHAT, getConstructor(WrappedPacketPlayInChat.class));
        PACKET_MAP.put(PacketType.Play.Client.CLIENT_COMMAND, getConstructor(WrappedPacketPlayInClientCommand.class));
        PACKET_MAP.put(PacketType.Play.Client.CLOSE_WINDOW, getConstructor(WrappedPacketPlayInCloseWindow.class));
        PACKET_MAP.put(PacketType.Play.Client.CUSTOM_PAYLOAD, getConstructor(WrappedPacketPlayInCustomPayload.class));
        PACKET_MAP.put(PacketType.Play.Client.ENCHANT_ITEM, getConstructor(WrappedPacketPlayInEnchantItem.class));
        PACKET_MAP.put(PacketType.Play.Client.ENTITY_ACTION, getConstructor(WrappedPacketPlayInEntityAction.class));
        PACKET_MAP.put(PacketType.Play.Client.FLYING, getConstructor(WrappedPacketPlayInFlying.class));
        PACKET_MAP.put(PacketType.Play.Client.LOOK, getConstructor(WrappedPacketPlayInLook.class));
        PACKET_MAP.put(PacketType.Play.Client.POSITION, getConstructor(WrappedPacketPlayInPosition.class));
        PACKET_MAP.put(PacketType.Play.Client.POSITION_LOOK, getConstructor(WrappedPacketPlayInPositionLook.class));
        PACKET_MAP.put(PacketType.Play.Client.RESOURCE_PACK_STATUS, getConstructor(WrappedPacketPlayInResourcePackStatus.class));
        PACKET_MAP.put(PacketType.Play.Client.SET_CREATIVE_SLOT, getConstructor(WrappedPacketPlayInSetCreativeSlot.class));
        PACKET_MAP.put(PacketType.Play.Client.SETTINGS, getConstructor(WrappedPacketPlayInSettings.class));
        PACKET_MAP.put(PacketType.Play.Client.STEER_VEHICLE, getConstructor(WrappedPacketPlayInSteerVehicle.class));
        PACKET_MAP.put(PacketType.Play.Client.TAB_COMPLETE, getConstructor(WrappedPacketPlayInTabComplete.class));
        PACKET_MAP.put(PacketType.Play.Client.TRANSACTION, getConstructor(WrappedPacketPlayInTransaction.class));
        PACKET_MAP.put(PacketType.Play.Client.UPDATE_SIGN, getConstructor(WrappedPacketPlayInUpdateSign.class));
        PACKET_MAP.put(PacketType.Play.Client.USE_ENTITY, getConstructor(WrappedPacketPlayInUseEntity.class));
        PACKET_MAP.put(PacketType.Play.Client.WINDOW_CLICK, getConstructor(WrappedPacketPlayInWindowClick.class));

        PACKET_MAP.put(PacketType.Play.Server.ENTITY_DESTROY, getConstructor(WrappedPacketPlayOutDestroyEntities.class));
        PACKET_MAP.put(PacketType.Play.Server.ENTITY, getConstructor(WrappedPacketPlayOutEntity.class));
        PACKET_MAP.put(PacketType.Play.Server.ENTITY_EFFECT, getConstructor(WrappedPacketPlayOutEntityEffect.class));
        PACKET_MAP.put(PacketType.Play.Server.ENTITY_LOOK, getConstructor(WrappedPacketPlayOutEntityLook.class));
        PACKET_MAP.put(PacketType.Play.Server.ENTITY_MOVE_LOOK, getConstructor(WrappedPacketPlayOutEntityLookMove.class));
        PACKET_MAP.put(PacketType.Play.Server.ENTITY_TELEPORT, getConstructor(WrappedPacketPlayOutEntityTeleport.class));
        PACKET_MAP.put(PacketType.Play.Server.ENTITY_VELOCITY, getConstructor(WrappedPacketPlayOutEntityVelocity.class));
        PACKET_MAP.put(PacketType.Play.Server.KEEP_ALIVE, getConstructor(WrappedPacketPlayOutKeepAlive.class));
        PACKET_MAP.put(PacketType.Play.Server.NAMED_ENTITY_SPAWN, getConstructor(WrappedPacketPlayOutNamedEntitySpawn.class));
        PACKET_MAP.put(PacketType.Play.Server.POSITION, getConstructor(WrappedPacketPlayOutPosition.class));
        PACKET_MAP.put(PacketType.Play.Server.REL_ENTITY_MOVE, getConstructor(WrappedPacketPlayOutRelMove.class));
        PACKET_MAP.put(PacketType.Play.Server.REMOVE_ENTITY_EFFECT, getConstructor(WrappedPacketPlayOutRemoveEntityEffect.class));
        PACKET_MAP.put(PacketType.Play.Server.TRANSACTION, getConstructor(WrappedPacketPlayOutTransaction.class));
    }

    @SneakyThrows
    private static <T extends WrappedPacket> Constructor<T> getConstructor(Class<T> clazz) {
        return clazz.getDeclaredConstructor(PacketContainer.class);
    }

    public static PacketType[] getProcessedPackets() {
        return PACKET_MAP.keySet().toArray(new PacketType[0]);
    }

    @SneakyThrows
    public static WrappedPacket wrapPacket(PacketType packetType, PacketContainer packetContainer) {
        return PACKET_MAP.get(packetType).newInstance(packetContainer);
    }
}
