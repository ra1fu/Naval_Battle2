package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Player;


public class Tournament {
    private int id;
    private String name;
    private Date startDate;
    private List<Player> participants;

    public Tournament() {
        this.participants = new ArrayList<>();
    }

    public Tournament(String name, Date startDate) {
        this.name = name;
        this.startDate = startDate;
        this.participants = new ArrayList<>();
    }

    public Tournament(String name) {
        this.name = name;
        this.startDate = new Date();
        this.participants = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Player> participants) {
        this.participants = participants;
    }

    public void addParticipant(Player player) {
        participants.add(player);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", participants=" + participants +
                '}';
    }
}