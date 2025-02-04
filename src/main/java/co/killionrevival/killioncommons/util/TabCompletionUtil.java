package co.killionrevival.killioncommons.util;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabCompletionUtil {
    public static List<String> getOnlinePlayersStartingWithMinusCurrentPlayer(Player currentPlayer, String input,
            boolean caseInsensitive) {
        if (caseInsensitive) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                    .filter(name -> !name.equalsIgnoreCase(currentPlayer.getName()))
                    .collect(Collectors.toList());
        }
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.startsWith(input))
                .filter(name -> !name.equals(currentPlayer.getName()))
                .collect(Collectors.toList());

    }

    public static List<String> getOnlinePlayersStartingWith(String input, boolean caseInsensitive) {
        if (caseInsensitive) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.startsWith(input))
                .collect(Collectors.toList());

    }

    public static List<String> filterListByPrefix(List<String> list, String prefix, boolean caseInsensitive) {
        if (caseInsensitive) {
            return list.stream().filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return list.stream().filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
