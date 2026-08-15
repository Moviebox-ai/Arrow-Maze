package com.example.arrowmaze3d.game;

import com.example.arrowmaze3d.utilities.Vector3i;
import com.example.arrowmaze3d.world.Direction;
import java.util.Stack;

public class CommandHistory {

    public static class MoveRecord {
        public final Vector3i playerPos;
        public final Direction playerFacing;
        public final int stepCount;

        public MoveRecord(Vector3i playerPos, Direction playerFacing, int stepCount) {
            this.playerPos = playerPos.cpy();
            this.playerFacing = playerFacing;
            this.stepCount = stepCount;
        }
    }

    private final Stack<MoveRecord> history = new Stack<>();

    public void push(Vector3i playerPos, Direction playerFacing, int stepCount) {
        history.push(new MoveRecord(playerPos, playerFacing, stepCount));
    }

    public MoveRecord pop() {
        if (!history.isEmpty()) {
            return history.pop();
        }
        return null;
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public void clear() {
        history.clear();
    }
}
