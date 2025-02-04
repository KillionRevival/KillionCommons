package co.killionrevival.killioncommons.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data @AllArgsConstructor
public class ChunkLocation {
    long x;
    long y;
    String world;

    public Optional<Chunk> toChunk() {
        return Optional.ofNullable(Bukkit.getWorld(world)).map(world -> world.getChunkAt((int) x, (int) y));
    }

    /**
     * Get the set of X, Y of all chunks between two points.
     * Assumes world is the same between points, using corner's world.
     * @param corner The first corner
     * @param otherCorner The second corner
     * @return The set of all chunks between the two points
     */
    public static Set<ChunkLocation> getChunkLocationsBetweenPoints(
            final Point corner,
            final Point otherCorner
    ) {
        final String world = corner.getWorld();
        final HashSet<ChunkLocation> locs = new HashSet<>();
        int xMax; int xMin; int zMax; int zMin;
        // write a function that determines all of the chunks between the two block locations, corner and otherCorner
        if (corner.getX() > otherCorner.getX()) {
            xMax = (int) corner.getX();
            xMin = (int) otherCorner.getX();
        } else {
            xMax = (int) otherCorner.getX();
            xMin = (int) corner.getX();
        }
        if (corner.getZ() > otherCorner.getZ()) {
            zMax = (int) corner.getZ();
            zMin = (int) otherCorner.getZ();
        } else {
            zMax = (int) otherCorner.getZ();
            zMin = (int) corner.getZ();
        }

        for (int x = xMin; x <= xMax; x += 16) {
            for (int z = zMin; z <= zMax; z += 16) {
                locs.add(new ChunkLocation(x >> 4, z >> 4, world));
            }
        }

        return locs;
    }
}
