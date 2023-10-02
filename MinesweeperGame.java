package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private static final int SIDE = 20;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countClosedTiles = SIDE * SIDE;
    private int countMinesOnField;
    private int countFlags;
    private int score;
    private boolean isGameStopped;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
//        isGameStopped = false;

        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y, "");
                boolean isMine = getRandomNumber(10) == 0 || getRandomNumber(10) == 9;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                if (!gameField[x][y].getIsMine()) {
                    for (GameObject element : getNeighbors(gameField[x][y])) {
                        if (element.getIsMine()) {
                            gameField[x][y].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (!gameField[y][x].isOpen && !gameField[y][x].isFlag && !isGameStopped) {
            if (gameField[y][x].getIsMine()) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameField[y][x].isOpen = true;
                gameOver();
            } else {
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                gameField[y][x].isOpen = true;
                countClosedTiles--;
                score += 5;
                setScore(score);
                setCellColor(x, y, Color.GREEN);

                if (countClosedTiles == countMinesOnField) {
                    win();
                }

                if (!gameField[y][x].isMine && gameField[y][x].countMineNeighbors == 0) {
                    setCellValue(x, y, "");
                    for (GameObject element : getNeighbors(gameField[y][x])) {
                        if (!element.isOpen) {
                            openTile(element.getX(), element.getY());
                        }
                    }
                } else if (!gameField[y][x].isMine && gameField[y][x].countMineNeighbors != 0) {
                    setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                }
            }
        }
    }

    private void markTile(int x, int y) {
        if (!isGameStopped && !gameField[y][x].isOpen && countFlags != 0 && !gameField[y][x].isFlag) {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        } else if (!isGameStopped && gameField[y][x].isFlag) {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.ORANGE);
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
        } else {
            openTile(x, y);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "YOU DIED", Color.RED, 50);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "NOT BAD, NOT BAD", Color.VIOLET, 50);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }
}