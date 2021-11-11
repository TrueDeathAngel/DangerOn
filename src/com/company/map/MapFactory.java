package com.company.map;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MapFactory {
    static int roomMaxHeight = 9;
    static int mapHeight = 4;
    static int roomMaxWidth = 18;
    static int mapWidth = 9;
    static int horizontalDoorPosition = 11;
    static int verticalDoorPosition = 4;
    static Point heroBaseTopLeft = new Point(1, 1);
    static Point heroBaseBottomRight = new Point(7, 15);

    //փ

    public static CellTypes[][] createMap() {
        CellTypes[][] map = new CellTypes[roomMaxHeight * mapHeight][roomMaxWidth * mapWidth];

        for (int i = 0; i < roomMaxHeight; i++)
            for (int j = 0; j < roomMaxWidth; j++)
                map[i][j] = CellTypes.LAVA;

        for (int i = heroBaseTopLeft.y + 1; i < heroBaseBottomRight.y; i++) {
            map[heroBaseTopLeft.x][i] = CellTypes.HORIZONTAL_WALL;
            map[heroBaseBottomRight.x][i] = CellTypes.HORIZONTAL_WALL;
        }

        for (int i = heroBaseTopLeft.x + 1; i < heroBaseBottomRight.x; i++) {
            map[i][heroBaseTopLeft.y] = CellTypes.VERTICAL_WALL;
            map[i][heroBaseBottomRight.y] = CellTypes.VERTICAL_WALL;
        }

        for (int i = heroBaseTopLeft.x + 1; i < heroBaseBottomRight.x; i++)
            for (int j = heroBaseTopLeft.y + 1; j < heroBaseBottomRight.y; j++)
                map[i][j] = CellTypes.SAFE_AREA;

        for (int i = heroBaseBottomRight.x + 1; i < roomMaxHeight; i++) {
            map[i][horizontalDoorPosition - 1] = CellTypes.VERTICAL_WALL;
            map[i][horizontalDoorPosition] = CellTypes.EMPTY;
            map[i][horizontalDoorPosition + 1] = CellTypes.VERTICAL_WALL;
        }
        map[heroBaseBottomRight.x][horizontalDoorPosition - 1] = CellTypes.TOP_RIGHT_CORNER_WALL;
        map[heroBaseBottomRight.x][horizontalDoorPosition] = CellTypes.DOOR;
        map[heroBaseBottomRight.x][horizontalDoorPosition + 1] = CellTypes.TOP_LEFT_CORNER_WALL;

        map[heroBaseTopLeft.x][heroBaseTopLeft.y] = CellTypes.TOP_LEFT_CORNER_WALL;
        map[heroBaseTopLeft.x][heroBaseBottomRight.y] = CellTypes.TOP_RIGHT_CORNER_WALL;
        map[heroBaseBottomRight.x][heroBaseBottomRight.y] = CellTypes.BOTTOM_RIGHT_CORNER_WALL;
        map[heroBaseBottomRight.x][heroBaseTopLeft.y] = CellTypes.BOTTOM_LEFT_CORNER_WALL;

        for (int roomNumber = 1; roomNumber <= 35; roomNumber++) {
            CellTypes[][] room = createRoom(roomNumber);
            int di = roomMaxHeight * (roomNumber / 9);
            int dj = roomMaxWidth * (roomNumber % 9);
            for (int i = 0; i < roomMaxHeight; i++)
                if (roomMaxWidth >= 0) System.arraycopy(room[i], 0, map[i + di], dj, roomMaxWidth);
        }

        return map;
    }

    private static CellTypes[][] createRoom(int roomNumber) {
        CellTypes[][] room = new CellTypes[roomMaxHeight][roomMaxWidth];
        for (int i = 0; i < roomMaxHeight; i++)
            for (int j = 0; j < roomMaxWidth; j++)
                room[i][j] = CellTypes.LAVA;

        Point topLeft = new Point(ThreadLocalRandom.current().nextInt(3), ThreadLocalRandom.current().nextInt(5));
        Point bottomRight = new Point(ThreadLocalRandom.current().nextInt(3) + 6, ThreadLocalRandom.current().nextInt(5) + 13);

        for (int i = topLeft.y + 1; i < bottomRight.y; i++) {
            room[topLeft.x][i] = CellTypes.HORIZONTAL_WALL;
            room[bottomRight.x][i] = CellTypes.HORIZONTAL_WALL;
        }

        for (int i = topLeft.x + 1; i < bottomRight.x; i++) {
            room[i][topLeft.y] = CellTypes.VERTICAL_WALL;
            room[i][bottomRight.y] = CellTypes.VERTICAL_WALL;
        }

        for (int i = topLeft.x + 1; i < bottomRight.x; i++)
            for (int j = topLeft.y + 1; j < bottomRight.y; j++)
                room[i][j] = CellTypes.EMPTY;

        room[topLeft.x][topLeft.y] = CellTypes.TOP_LEFT_CORNER_WALL;
        room[topLeft.x][bottomRight.y] = CellTypes.TOP_RIGHT_CORNER_WALL;
        room[bottomRight.x][bottomRight.y] = CellTypes.BOTTOM_RIGHT_CORNER_WALL;
        room[bottomRight.x][topLeft.y] = CellTypes.BOTTOM_LEFT_CORNER_WALL;

        ArrayList<CorridorDirections> corridorDirections = new ArrayList<>();

        if(roomNumber / mapWidth != 0) corridorDirections.add(CorridorDirections.UP);
        if(roomNumber % mapWidth != mapWidth - 1) corridorDirections.add(CorridorDirections.RIGHT);
        if(roomNumber / mapWidth != mapHeight - 1) corridorDirections.add(CorridorDirections.DOWN);
        if((roomNumber != 1 || mapHeight < 2) && roomNumber % mapWidth != 0) corridorDirections.add(CorridorDirections.LEFT);

        for(int i = ThreadLocalRandom.current().nextInt(corridorDirections.size()); i > 0; i--)
        {
            corridorDirections.remove(ThreadLocalRandom.current().nextInt(corridorDirections.size()));
        }

        if (corridorDirections.contains(CorridorDirections.UP)) // Up
        {
            for (int i = topLeft.x - 1; i >= 0; i--)
            {
                room[i][horizontalDoorPosition - 1] = CellTypes.VERTICAL_WALL;
                room[i][horizontalDoorPosition] = CellTypes.EMPTY;
                room[i][horizontalDoorPosition + 1] = CellTypes.VERTICAL_WALL;
            }
            room[topLeft.x][horizontalDoorPosition - 1] = CellTypes.BOTTOM_RIGHT_CORNER_WALL;
            room[topLeft.x][horizontalDoorPosition] = CellTypes.EMPTY;
            room[topLeft.x][horizontalDoorPosition + 1] = CellTypes.BOTTOM_LEFT_CORNER_WALL;
        }

        if(corridorDirections.contains(CorridorDirections.RIGHT)) // Right
        {
            for (int i = bottomRight.y + 1; i < roomMaxWidth; i++)
            {
                room[verticalDoorPosition - 1][i] = CellTypes.HORIZONTAL_WALL;
                room[verticalDoorPosition][i] = CellTypes.EMPTY;
                room[verticalDoorPosition + 1][i] = CellTypes.HORIZONTAL_WALL;
            }
            room[verticalDoorPosition - 1][bottomRight.y] = CellTypes.BOTTOM_LEFT_CORNER_WALL;
            room[verticalDoorPosition][bottomRight.y] = CellTypes.EMPTY;
            room[verticalDoorPosition + 1][bottomRight.y] = CellTypes.TOP_LEFT_CORNER_WALL;
        }

        if(corridorDirections.contains(CorridorDirections.DOWN)) // Down
        {
            for (int i = bottomRight.x + 1; i < roomMaxHeight; i++) {
                room[i][horizontalDoorPosition - 1] = CellTypes.VERTICAL_WALL;
                room[i][horizontalDoorPosition] = CellTypes.EMPTY;
                room[i][horizontalDoorPosition + 1] = CellTypes.VERTICAL_WALL;
            }
            room[bottomRight.x][horizontalDoorPosition - 1] = CellTypes.TOP_RIGHT_CORNER_WALL;
            room[bottomRight.x][horizontalDoorPosition] = CellTypes.EMPTY;
            room[bottomRight.x][horizontalDoorPosition + 1] = CellTypes.TOP_LEFT_CORNER_WALL;
        }

        if(corridorDirections.contains(CorridorDirections.LEFT)) // Left
        {
            for (int i = topLeft.y - 1; i >= 0; i--) {
                room[verticalDoorPosition - 1][i] = CellTypes.HORIZONTAL_WALL;
                room[verticalDoorPosition][i] = CellTypes.EMPTY;
                room[verticalDoorPosition + 1][i] = CellTypes.HORIZONTAL_WALL;
            }
            room[verticalDoorPosition - 1][topLeft.y] = CellTypes.BOTTOM_RIGHT_CORNER_WALL;
            room[verticalDoorPosition][topLeft.y] = CellTypes.EMPTY;
            room[verticalDoorPosition + 1][topLeft.y] = CellTypes.TOP_RIGHT_CORNER_WALL;
        }

        return room;
    }

    private enum CorridorDirections
    {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
}