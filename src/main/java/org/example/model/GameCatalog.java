package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameCatalog {
    private List<Game> games = new ArrayList<>();

    public void addGame(Game game) {
        games.add(game);
    }

    public void removeGame(Game game) {
        games.remove(game);
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games.clear();
        this.games.addAll(games);
    }

    public List<Game> search(String keyword) {
        String lowerKeyword = keyword.toLowerCase();

        return games.stream().filter(game ->
                (game.getTitle() != null && game.getTitle().toLowerCase().contains(lowerKeyword)) ||
                        (game.getDeveloper() != null && game.getDeveloper().toLowerCase().contains(lowerKeyword)) ||
                        (game.getPublisher() != null && game.getPublisher().toLowerCase().contains(lowerKeyword)) ||
                        (game.getSteamId() != null && game.getSteamId().toLowerCase().contains(lowerKeyword)) ||
                        (String.valueOf(game.getReleaseYear()).contains(lowerKeyword)) ||
                        (game.getGenre() != null && game.getGenre().stream().anyMatch(g -> g.toLowerCase().contains(lowerKeyword))) ||
                        (game.getPlatforms() != null && game.getPlatforms().stream().anyMatch(p -> p.toLowerCase().contains(lowerKeyword))) ||
                        (game.getLanguage() != null && game.getLanguage().toLowerCase().contains(lowerKeyword)) ||
                        (game.getTags() != null && game.getTags().stream().anyMatch(t -> t.toLowerCase().contains(lowerKeyword)))
        ).collect(Collectors.toList());
    }
}
