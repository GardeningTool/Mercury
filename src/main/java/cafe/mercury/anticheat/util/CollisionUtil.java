package cafe.mercury.anticheat.util;

import cafe.mercury.anticheat.util.collision.CollisionResult;
import cafe.mercury.anticheat.util.location.CustomLocation;
import cafe.mercury.anticheat.util.mcp.AxisAlignedBB;
import cafe.mercury.anticheat.util.mcp.MathHelper;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;
import java.util.stream.Collectors;

public class CollisionUtil {

    private static final int MATERIAL_MAX = Material.values().length;
    private static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[MATERIAL_MAX];
    private static final List<Integer> CLIMBABLE = Arrays.asList(65, 106);
    private static final List<Integer> WATER = Arrays.asList(8, 9);
    private static final List<Integer> LAVA = Arrays.asList(10, 11);
    private static final Map<Integer, Float> ABNORMAL_FRICTION = ImmutableMap.of(
            Material.ICE.getId(), 0.98F,
            Material.PACKED_ICE.getId(), 0.98F,
            Material.SLIME_BLOCK.getId(), 0.8F
    );

    static {
        AxisAlignedBB fence = AxisAlignedBB.fromBounds(0, 0, 0, 1, 1.5, 1);

        for(int i = 0; i < MATERIAL_MAX; i++) {
            Material material = Material.values()[i];

            if (material.name().contains("FENCE")) {
                BOUNDING_BOXES[i] = fence;
            }
        }

        BOUNDING_BOXES[Material.COBBLE_WALL.getId()] = fence;
        BOUNDING_BOXES[Material.CARPET.getId()] = AxisAlignedBB.fromBounds(0, 0, 0, 1, 0.0625, 1);
        BOUNDING_BOXES[Material.SNOW.getId()] = AxisAlignedBB.fromBounds(0, 0, 0, 1, 1, 1);
        BOUNDING_BOXES[Material.BREWING_STAND.getId()] = AxisAlignedBB.fromBounds(0, 0, 0, 1, 0.875, 1);
        BOUNDING_BOXES[Material.TRIPWIRE.getId()] = AxisAlignedBB.fromBounds(0, 0, 0, 1.0, 0.15625, 1);
        BOUNDING_BOXES[Material.SKULL.getId()] = AxisAlignedBB.fromBounds(0.25, 0, 0.25, 0.75, 0.5, 0.75);
        BOUNDING_BOXES[Material.WATER_LILY.getId()] = AxisAlignedBB.fromBounds(0, 0., 0, 1, 0.015625, 1);
    }

    public static CollisionResult calculateCollisions(World world, CustomLocation location, AxisAlignedBB bb) {
        Set<CollisionUtil.Collision> collisions = CollisionUtil.getCollidingBoundingBoxes(world, bb);
        Set<AxisAlignedBB> collisionBoxes = collisions.stream()
                .map(CollisionUtil.Collision::getCollidingBox)
                .filter(box -> isCollided(box, bb))
                .collect(Collectors.toSet());

        CollisionResult result = new CollisionResult();

        result.setOnGround(CollisionUtil.isCollidedBelow(collisionBoxes, location.getY()));
        result.setFrictionFactor(result.isOnGround() ? CollisionUtil.getFrictionFactor(world, location) : .91F);
        result.setClimbing(collides(collisions, CLIMBABLE));
        result.setCollidedHorizontally(collisions.size() > 0);
        result.setUnderBlock(isCollidedAbove(collisionBoxes, bb.maxY));
        result.setInCobweb(collides(collisions, Material.WEB.getId()));
        result.setCollidedVertically(result.isOnGround() || result.isUnderBlock());
        result.setInLava(collides(collisions, LAVA));
        result.setInWater(collides(collisions, WATER));

        return result;
    }

    public static Set<Collision> getCollidingBoundingBoxes(World world, AxisAlignedBB boundingBox) {
        Set<Collision> bbs = new HashSet<>();

        int minX = MathHelper.floor_double(boundingBox.minX);
        int maxX = MathHelper.floor_double(boundingBox.maxX);
        int minY = MathHelper.floor_double(boundingBox.minY);
        int maxY = MathHelper.floor_double(boundingBox.maxY);
        int minZ = MathHelper.floor_double(boundingBox.minZ);
        int maxZ = MathHelper.floor_double(boundingBox.maxZ);

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                    continue;
                }

                for (int y = minY - 1; y < maxY; y++) {
                    Block block = world.getBlockAt(new Location(world, x, y, z));

                    int id = block.getType().getId();
                    AxisAlignedBB bb = BOUNDING_BOXES[id];

                    if (bb != null) {
                        bb = bb.addCoord(x, y, z);
                    } else {
                        /*
                         * We're just going to assume the block's bounding box
                         * is this exact shape and size. Abnormal bounding boxes
                         * are accounted for above
                         */
                        bb = new AxisAlignedBB(x - 0.5, y, z - 0.5, x + 0.5, y + 1, z + 0.5);
                    }

                    if (boundingBox.intersectsWith(bb)) {
                        bbs.add(new Collision(bb, id));
                    }
                }
            }
        }

        return bbs;
    }

    private static boolean collides(Set<Collision> collisions, List<Integer> types) {
        return collisions.stream()
                .anyMatch(collision -> types.contains(collision.getCollidingType()));
    }

    private static boolean collides(Set<Collision> collisions, int type) {
        return collides(collisions, Collections.singletonList(type));
    }

    /**
     * Checks if any bounding boxes collides above with {@param targetY}
     * @param bbs The bounding boxes to check
     * @param targetY The y to check
     * @return If above below
     */
    public static boolean isCollidedAbove(Set<AxisAlignedBB> bbs, double targetY) {
        return bbs.stream()
                .anyMatch(b -> b.maxY >= targetY);
    }

    /**
     * Checks if any bounding boxes collides below with {@param targetY}
     * @param bbs The bounding boxes to check
     * @param targetY The y to check
     * @return If collided below
     */
    public static boolean isCollidedBelow(Set<AxisAlignedBB> bbs, double targetY) {
        return bbs.stream()
                /*
                 * Since our bounding box system isn't perfect we have to check the minY and see if it's less than
                 * our target y
                 */
                .anyMatch(b -> b.minY < targetY);
    }

    /**
     * Checks if {@param from} is collided with {@param to}
     * @param from The from bounding box
     * @param to The to bounding box
     * @return If 2 bbs collide
     */
    public static boolean isCollided(AxisAlignedBB from, AxisAlignedBB to) {
        return from.minX <= to.maxX && from.maxX >= to.minY &&
                from.minY <= to.maxY && from.maxY >= to.minY &&
                from.minZ <= to.maxZ && from.maxZ >= to.minZ;
    }

    public static float getFrictionFactor(World world, CustomLocation location) {
        Location bukkitLocation = new Location(world, Math.floor(location.getX()),
                Math.floor(location.getY() - 1), Math.floor(location.getZ()));
        int id = bukkitLocation.getBlock().getType().getId();

        return ABNORMAL_FRICTION.getOrDefault(id, 0.6F) * 0.91F;
    }

    @Getter
    @AllArgsConstructor
    public static class Collision {

        private final AxisAlignedBB collidingBox;
        private final int collidingType;
    }
}
