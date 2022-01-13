package cafe.mercury.anticheat.util.collision;

import cafe.mercury.anticheat.util.location.CustomLocation;
import cafe.mercury.anticheat.util.mcp.AxisAlignedBB;
import cafe.mercury.anticheat.util.mcp.MathHelper;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;
import java.util.stream.Collectors;

public class CollisionUtil {

    /**
     * We use this field as a divisor for the player's position on the Y axis.
     * This will tell us if the player is mathematically on the ground.
     */
    private static final double DIVISOR = 1D / 64D;

    private static final int MATERIAL_MAX = Material.values().length; //every material in minecraft
    private static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[MATERIAL_MAX]; //the corresponding bounding box sizes
    private static final List<Integer> CLIMBABLE = Arrays.asList(65, 106); //vines and ladders
    private static final List<Integer> WATER = Arrays.asList(8, 9); //water IDs
    private static final List<Integer> LAVA = Arrays.asList(10, 11); //lava IDs
    private static final Map<Integer, Float> ABNORMAL_FRICTION = ImmutableMap.of( //all blocks that have some form of irregular friction
            Material.ICE.getId(), 0.98F,
            Material.PACKED_ICE.getId(), 0.98F,
            Material.SLIME_BLOCK.getId(), 0.8F
    );

    //TODO: Find an automated, NMS-independent way of doing this
    static {
        AxisAlignedBB fence = AxisAlignedBB.fromBounds(0, 0, 0, 1, 1.5, 1);
        AxisAlignedBB defaultBb = AxisAlignedBB.fromBounds(0, 0, 0, 1, 1, 1);

        for(int i = 0; i < MATERIAL_MAX; i++) {
            Material material = Material.values()[i];

            if (material.name().contains("FENCE")) {
                BOUNDING_BOXES[i] = fence;
            } else {
                BOUNDING_BOXES[i] = defaultBb;
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

        result.setOnGround(CollisionUtil.isCollidedBelow(collisionBoxes, MathHelper.floor_double(location.getY())));
        result.setFrictionFactor(result.isOnGround() ? CollisionUtil.getFrictionFactor(world, location) : .91F);
        result.setClimbing(collides(collisions, CLIMBABLE));
        result.setCollidedHorizontally(collisions.size() > 0);
        result.setUnderBlock(isCollidedAbove(collisionBoxes, bb.maxY));
        result.setInCobweb(collides(collisions, Material.WEB.getId()));
        result.setCollidedVertically(result.isOnGround() || result.isUnderBlock());
        result.setInLava(collides(collisions, LAVA));
        result.setInWater(collides(collisions, WATER));
        result.setMathematicallyOnGround(location.getY() % DIVISOR == 0);

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

                for (int y = minY; y < maxY; y++) {
                    Block block = world.getBlockAt(new Location(world, x, y, z));

                    int id = block.getType().getId();
                    if (id == Material.AIR.getId()) {
                        continue;
                    }
                    
                    AxisAlignedBB bb = BOUNDING_BOXES[id];

                    bb = bb.addCoord(x, y, z);

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
                .anyMatch(bb -> bb.minY <= targetY);
    }

    /**
     * Checks if {@param from} is collided with {@param to}
     * @param from The from bounding box
     * @param to The to bounding box
     * @return If 2 bbs collide
     */
    public static boolean isCollided(AxisAlignedBB from, AxisAlignedBB to) {
        return from.minX <= to.maxX && from.maxX >= to.minX &&
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
