package models;

import java.util.List;

public class Ship {
    private int id;
    private ShipCategory category;
    private List<String> coordinates;
    private boolean isSunk;

    public Ship() {
    }


    public Ship(ShipCategory category, List<String> coordinates) {
        this.category = category;
        this.coordinates = coordinates;
        this.isSunk = false;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ShipCategory getCategory() {
        return category;
    }

    public void setCategory(ShipCategory category) {
        this.category = category;
    }

    public List<String> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isSunk() {
        return isSunk;
    }

    public void setSunk(boolean sunk) {
        isSunk = sunk;
    }

    public void hit(String coordinate) {

        if (coordinates.remove(coordinate) && coordinates.isEmpty()) {
            isSunk = true;
        }
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", category=" + category +
                ", coordinates=" + coordinates +
                ", isSunk=" + isSunk +
                '}';
    }
}