package com.company.map;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.company.Main.map;

//փ

public class MapFactory
{
    private static final int roomMaxHeight = 11; // >= 7
    private static final int roomMaxWidth = 22;  // >= 7
    private static int mapHeight;
    private static int mapWidth;
    private static final int verticalDoorPosition = roomMaxHeight / 2;
    private static final int horizontalDoorPosition = roomMaxWidth / 2;
    private static final Point heroBaseTopLeft = new Point(roomMaxHeight / 4 - 1, roomMaxWidth / 4 - 1);
    private static final Point heroBaseBottomRight = new Point(3 * roomMaxHeight / 4 - 1, 3 * roomMaxWidth / 4 - 1);
    private static final HashSet<Integer> safeRoomNumbers = new HashSet<>();
    public static int freeSpaceCounter;

    public static int getNumberOfRooms() { return mapHeight * mapWidth; }

    public static int getMapWidthInCells() { return mapWidth * roomMaxWidth; }

    public static int getMapHeightInCells() { return mapHeight * roomMaxHeight; }

    public static Point getHeroStartPosition() {
        return new Point(heroBaseBottomRight.x - 1, horizontalDoorPosition);
    }

    public static Point getHeroBaseTopLeft() {
        return heroBaseTopLeft;
    }

    public static Point getHeroBaseBottomRight() {
        return heroBaseBottomRight;
    }

    public static Point getHeroChestPosition() { return new Point(heroBaseTopLeft.x + 1, heroBaseBottomRight.y - 1); }

    public static CellTypes[][] createMap() {
        mapHeight = ThreadLocalRandom.current().nextInt(5) + 2;
        mapWidth = ThreadLocalRandom.current().nextInt(5) + 3;

        generateMapTemplate();

        safeRoomNumbers.clear();
        safeRoomNumbers.add(0);

        for(int i = ThreadLocalRandom.current().nextInt((int)Math.sqrt(mapHeight * mapWidth)); i > 0; i--) {
            safeRoomNumbers.add(ThreadLocalRandom.current().nextInt(mapHeight * mapWidth));
        }

        CellTypes[][] map = new CellTypes[roomMaxHeight * mapHeight][roomMaxWidth * mapWidth];

        for (int roomNumber = 0; roomNumber <= mapHeight * mapWidth - 1; roomNumber++) {

            CellTypes[][] room;
            if (roomNumber == 0) room = createRoom(0, heroBaseTopLeft, heroBaseBottomRight);
            else room = createRoom(
                    roomNumber,
                    new Point(
                        ThreadLocalRandom.current().nextInt(roomMaxHeight / 4 + 1),
                        ThreadLocalRandom.current().nextInt(roomMaxWidth / 4 + 1)),
                    new Point(
                        ThreadLocalRandom.current().nextInt(roomMaxHeight / 4 + 1) + 3 * roomMaxHeight / 4 - 1,
                        ThreadLocalRandom.current().nextInt(roomMaxWidth / 4 + 1) +  3 * roomMaxWidth / 4 - 1)
            );

            int di = roomMaxHeight * (roomNumber / mapWidth);
            int dj = roomMaxWidth * (roomNumber % mapWidth);
            for (int i = 0; i < roomMaxHeight; i++)
                if (roomMaxWidth >= 0) System.arraycopy(room[i], 0, map[i + di], dj, roomMaxWidth);
        }

        Random random = new Random();
        while (true)
        {
            int x = random.nextInt(map.length / 2) + map.length / 2, y = random.nextInt(map[x].length / 2) + map[x].length / 2;
            if(List.of(CellTypes.EMPTY, CellTypes.SAFE_AREA).contains(map[x][y])) {
                map[x][y] = CellTypes.EXIT;
                break;
            }
        }

        return map;
    }

    private static CellTypes[][] createRoom(int roomNumber, Point topLeft, Point bottomRight) {
        CellTypes[][] room = new CellTypes[roomMaxHeight][roomMaxWidth];
        for (int i = 0; i < roomMaxHeight; i++)
            for (int j = 0; j < roomMaxWidth; j++)
                room[i][j] = CellTypes.LAVA;

        for (int i = topLeft.y + 1; i < bottomRight.y; i++) {
            room[topLeft.x][i] = CellTypes.HORIZONTAL_WALL;
            room[bottomRight.x][i] = CellTypes.HORIZONTAL_WALL;
        }

        for (int i = topLeft.x + 1; i < bottomRight.x; i++) {
            room[i][topLeft.y] = CellTypes.VERTICAL_WALL;
            room[i][bottomRight.y] = CellTypes.VERTICAL_WALL;
        }

        CellTypes fillingCell = safeRoomNumbers.contains(roomNumber) ? CellTypes.SAFE_AREA : CellTypes.EMPTY;

        for (int i = topLeft.x + 1; i < bottomRight.x; i++)
            for (int j = topLeft.y + 1; j < bottomRight.y; j++)
                room[i][j] = fillingCell;

        fillingCell = safeRoomNumbers.contains(roomNumber) ? CellTypes.DOOR : CellTypes.EMPTY;

        room[topLeft.x][topLeft.y] = CellTypes.TOP_LEFT_CORNER_WALL;
        room[topLeft.x][bottomRight.y] = CellTypes.TOP_RIGHT_CORNER_WALL;
        room[bottomRight.x][bottomRight.y] = CellTypes.BOTTOM_RIGHT_CORNER_WALL;
        room[bottomRight.x][topLeft.y] = CellTypes.BOTTOM_LEFT_CORNER_WALL;

        ArrayList<CorridorDirections> corridorDirections = roomMap[roomNumber / mapWidth][roomNumber % mapWidth].corridorDirections;

        /*if(roomNumber / mapWidth != 0) corridorDirections.add(CorridorDirections.UP);
        if(roomNumber % mapWidth != mapWidth - 1 && roomNumber != 0) corridorDirections.add(CorridorDirections.RIGHT);
        if(roomNumber / mapWidth != mapHeight - 1) corridorDirections.add(CorridorDirections.DOWN);
        if((roomNumber != 1 || mapHeight < 2) && roomNumber % mapWidth != 0) corridorDirections.add(CorridorDirections.LEFT);

        for(int i = ThreadLocalRandom.current().nextInt(corridorDirections.size()); i > 0; i--)
        {
            corridorDirections.remove(ThreadLocalRandom.current().nextInt(corridorDirections.size()));
        }*/

        if (corridorDirections.contains(CorridorDirections.UP)) // Up
        {
            for (int i = topLeft.x - 1; i >= 0; i--)
            {
                room[i][horizontalDoorPosition - 1] = CellTypes.VERTICAL_WALL;
                room[i][horizontalDoorPosition] = CellTypes.EMPTY;
                room[i][horizontalDoorPosition + 1] = CellTypes.VERTICAL_WALL;
            }
            room[topLeft.x][horizontalDoorPosition - 1] = topLeft.y == horizontalDoorPosition - 1 ? CellTypes.VERTICAL_WALL : CellTypes.BOTTOM_RIGHT_CORNER_WALL;
            room[topLeft.x][horizontalDoorPosition] = fillingCell;
            room[topLeft.x][horizontalDoorPosition + 1] = bottomRight.y == horizontalDoorPosition + 1 ? CellTypes.VERTICAL_WALL : CellTypes.BOTTOM_LEFT_CORNER_WALL;
        }

        if(corridorDirections.contains(CorridorDirections.RIGHT)) // Right
        {
            for (int i = bottomRight.y + 1; i < roomMaxWidth; i++)
            {
                room[verticalDoorPosition - 1][i] = CellTypes.HORIZONTAL_WALL;
                room[verticalDoorPosition][i] = CellTypes.EMPTY;
                room[verticalDoorPosition + 1][i] = CellTypes.HORIZONTAL_WALL;
            }
            room[verticalDoorPosition - 1][bottomRight.y] = topLeft.x == verticalDoorPosition - 1 ? CellTypes.HORIZONTAL_WALL : CellTypes.BOTTOM_LEFT_CORNER_WALL;
            room[verticalDoorPosition][bottomRight.y] = fillingCell;
            room[verticalDoorPosition + 1][bottomRight.y] = bottomRight.x == verticalDoorPosition + 1 ? CellTypes.HORIZONTAL_WALL : CellTypes.TOP_LEFT_CORNER_WALL;
        }

        if(corridorDirections.contains(CorridorDirections.DOWN)) // Down
        {
            for (int i = bottomRight.x + 1; i < roomMaxHeight; i++) {
                room[i][horizontalDoorPosition - 1] = CellTypes.VERTICAL_WALL;
                room[i][horizontalDoorPosition] = CellTypes.EMPTY;
                room[i][horizontalDoorPosition + 1] = CellTypes.VERTICAL_WALL;
            }
            room[bottomRight.x][horizontalDoorPosition - 1] = topLeft.y == horizontalDoorPosition - 1 ? CellTypes.VERTICAL_WALL : CellTypes.TOP_RIGHT_CORNER_WALL;
            room[bottomRight.x][horizontalDoorPosition] = fillingCell;
            room[bottomRight.x][horizontalDoorPosition + 1] = bottomRight.y == horizontalDoorPosition + 1 ? CellTypes.VERTICAL_WALL : CellTypes.TOP_LEFT_CORNER_WALL;
        }

        if(corridorDirections.contains(CorridorDirections.LEFT)) // Left
        {
            for (int i = topLeft.y - 1; i >= 0; i--) {
                room[verticalDoorPosition - 1][i] = CellTypes.HORIZONTAL_WALL;
                room[verticalDoorPosition][i] = CellTypes.EMPTY;
                room[verticalDoorPosition + 1][i] = CellTypes.HORIZONTAL_WALL;
            }
            room[verticalDoorPosition - 1][topLeft.y] = topLeft.x == verticalDoorPosition - 1 ? CellTypes.HORIZONTAL_WALL : CellTypes.BOTTOM_RIGHT_CORNER_WALL;
            room[verticalDoorPosition][topLeft.y] = fillingCell;
            room[verticalDoorPosition + 1][topLeft.y] = bottomRight.x == verticalDoorPosition + 1 ? CellTypes.HORIZONTAL_WALL : CellTypes.TOP_RIGHT_CORNER_WALL;
        }

        return room;
    }

    private enum CorridorDirections {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }

    private static class Room {
        ArrayList<CorridorDirections> corridorDirections = new ArrayList<>();

        boolean isVisited = false;
    }

    private static Optional<Integer> getNextRoomIndex(int currentRoomIndex) {
        ArrayList<Integer> indexes = new ArrayList<>();
        if(currentRoomIndex / mapWidth > 0 && !roomMap[(currentRoomIndex - mapWidth) / mapWidth][(currentRoomIndex - mapWidth) % mapWidth].isVisited) indexes.add(currentRoomIndex - mapWidth);
        if(currentRoomIndex / mapWidth < mapHeight - 1 && !roomMap[(currentRoomIndex + mapWidth) / mapWidth][(currentRoomIndex + mapWidth) % mapWidth].isVisited) indexes.add(currentRoomIndex + mapWidth);
        if(currentRoomIndex % mapWidth > 0 && !roomMap[(currentRoomIndex - 1) / mapWidth][(currentRoomIndex - 1) % mapWidth].isVisited) indexes.add(currentRoomIndex - 1);
        if(currentRoomIndex % mapWidth < mapWidth - 1 && !roomMap[(currentRoomIndex + 1) / mapWidth][(currentRoomIndex + 1) % mapWidth].isVisited) indexes.add(currentRoomIndex + 1);

        if (indexes.isEmpty()) return Optional.empty();
        return Optional.of(indexes.get(ThreadLocalRandom.current().nextInt(indexes.size())));
    }

    private static void removeWalls(int currentRoomIndex, int nextRoomIndex) {

        if (nextRoomIndex - currentRoomIndex == -mapWidth) {
            roomMap[currentRoomIndex / mapWidth][currentRoomIndex % mapWidth].corridorDirections.add(CorridorDirections.UP);
            roomMap[nextRoomIndex / mapWidth][nextRoomIndex % mapWidth].corridorDirections.add(CorridorDirections.DOWN);
        }   // UP
        if (nextRoomIndex - currentRoomIndex == 1) {
            roomMap[currentRoomIndex / mapWidth][currentRoomIndex % mapWidth].corridorDirections.add(CorridorDirections.RIGHT);
            roomMap[nextRoomIndex / mapWidth][nextRoomIndex % mapWidth].corridorDirections.add(CorridorDirections.LEFT);
        }   // RIGHT
        if (nextRoomIndex - currentRoomIndex == mapWidth) {
            roomMap[currentRoomIndex / mapWidth][currentRoomIndex % mapWidth].corridorDirections.add(CorridorDirections.DOWN);
            roomMap[nextRoomIndex / mapWidth][nextRoomIndex % mapWidth].corridorDirections.add(CorridorDirections.UP);
        }    // DOWN
        if (nextRoomIndex - currentRoomIndex == -1) {
            roomMap[currentRoomIndex / mapWidth][currentRoomIndex % mapWidth].corridorDirections.add(CorridorDirections.LEFT);
            roomMap[nextRoomIndex / mapWidth][nextRoomIndex % mapWidth].corridorDirections.add(CorridorDirections.RIGHT);
        }   // LEFT
    }

    private static void generateMapTemplate() {
        roomMap = new Room[mapHeight][mapWidth];

        for(int i = 0; i < mapHeight; i++) {
            roomMap[i] = new Room[mapWidth];
            for (int j = 0; j < mapWidth; j++)
                roomMap[i][j] = new Room();
        }

        ArrayList<Integer> roomIndexes = new ArrayList<>();
        int currentRoomIndex = 0;
        Optional<Integer> nextRoomIndex;
        while (Arrays.stream(roomMap).anyMatch((rooms) -> Arrays.stream(rooms).filter(Objects::nonNull).anyMatch((room) -> !room.isVisited))) {
            nextRoomIndex = getNextRoomIndex(currentRoomIndex);
            if(nextRoomIndex.isPresent()) {
                roomMap[nextRoomIndex.get() / mapWidth][nextRoomIndex.get() % mapWidth].isVisited = true;
                roomIndexes.add(currentRoomIndex);
                removeWalls(currentRoomIndex, nextRoomIndex.get());
                currentRoomIndex = nextRoomIndex.get();
            }
            else if(!roomIndexes.isEmpty()) {
                currentRoomIndex = roomIndexes.get(roomIndexes.size() - 1);
                roomIndexes.remove(roomIndexes.size() - 1);
            }
        }
    }

    static Room[][] roomMap;
}