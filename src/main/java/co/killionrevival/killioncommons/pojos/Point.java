package co.killionrevival.killioncommons.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Data @AllArgsConstructor
public class Point {
    long x;
    long y;
    long z;
    String world;

    public ChunkLocation toChunkLocation() {
        return new ChunkLocation(x >> 4,  z >> 4, world);
    }
    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
    public Vector toVector() {
        return new Vector(x, y, z);
    }
}
