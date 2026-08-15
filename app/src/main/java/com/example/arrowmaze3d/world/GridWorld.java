package com.example.arrowmaze3d.world;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.example.arrowmaze3d.objects.*;
import com.example.arrowmaze3d.utilities.Vector3i;
import java.util.HashMap;
import java.util.Map;

public class GridWorld {
    private int sizeX;
    private int sizeY;
    private int sizeZ;

    private final Map<Vector3i, GridTile> tiles = new HashMap<>();
    private final Map<Vector3i, ArrowObject> arrows = new HashMap<>();
    private final Map<Vector3i, SwitchObject> switches = new HashMap<>();
    private final Map<Vector3i, GateObject> gates = new HashMap<>();
    private final Map<Vector3i, TeleportObject> teleporters = new HashMap<>();
    private final Map<Vector3i, SpikeObject> spikes = new HashMap<>();
    private final Map<Vector3i, KeyObject> keys = new HashMap<>();
    private final Map<Vector3i, LockGateObject> lockGates = new HashMap<>();
    private final Map<Vector3i, StarObject> stars = new HashMap<>();
    private GoalObject goalObject;

    private final Array<ModelInstance> renderInstances = new Array<>();

    public GridWorld(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public void addTile(GridTile tile) {
        tiles.put(tile.getGridPosition(), tile);
    }

    public GridTile getTile(Vector3i pos) {
        return tiles.get(pos);
    }

    public Map<Vector3i, GridTile> getAllTiles() {
        return tiles;
    }

    public void addArrow(ArrowObject arrow) {
        arrows.put(arrow.getGridPosition(), arrow);
        if (arrow.getModelInstance() != null) {
            renderInstances.add(arrow.getModelInstance());
        }
    }

    public ArrowObject getArrow(Vector3i pos) {
        return arrows.get(pos);
    }

    public Map<Vector3i, ArrowObject> getAllArrows() {
        return arrows;
    }

    public void addSwitch(SwitchObject switchObj) {
        switches.put(switchObj.getGridPosition(), switchObj);
        if (switchObj.getModelInstance() != null) {
            renderInstances.add(switchObj.getModelInstance());
        }
    }

    public SwitchObject getSwitch(Vector3i pos) {
        return switches.get(pos);
    }

    public void addGate(GateObject gate) {
        gates.put(gate.getGridPosition(), gate);
        if (gate.getModelInstance() != null) {
            renderInstances.add(gate.getModelInstance());
        }
    }

    public GateObject getGate(Vector3i pos) {
        return gates.get(pos);
    }

    public void addTeleporter(TeleportObject tp) {
        teleporters.put(tp.getGridPosition(), tp);
        if (tp.getModelInstance() != null) {
            renderInstances.add(tp.getModelInstance());
        }
    }

    public TeleportObject getTeleporter(Vector3i pos) {
        return teleporters.get(pos);
    }

    public void addSpike(SpikeObject spike) {
        spikes.put(spike.getGridPosition(), spike);
        if (spike.getModelInstance() != null) {
            renderInstances.add(spike.getModelInstance());
        }
    }

    public SpikeObject getSpike(Vector3i pos) {
        return spikes.get(pos);
    }

    public Map<Vector3i, SpikeObject> getAllSpikes() {
        return spikes;
    }

    public void addKey(KeyObject key) {
        keys.put(key.getGridPosition(), key);
        if (key.getModelInstance() != null) {
            renderInstances.add(key.getModelInstance());
        }
    }

    public KeyObject getKey(Vector3i pos) {
        return keys.get(pos);
    }

    public Map<Vector3i, KeyObject> getAllKeys() {
        return keys;
    }

    public void removeKey(Vector3i pos) {
        KeyObject k = keys.remove(pos);
        if (k != null && k.getModelInstance() != null) {
            renderInstances.removeValue(k.getModelInstance(), true);
        }
    }

    public void addLockGate(LockGateObject lockGate) {
        lockGates.put(lockGate.getGridPosition(), lockGate);
        if (lockGate.getModelInstance() != null) {
            renderInstances.add(lockGate.getModelInstance());
        }
    }

    public LockGateObject getLockGate(Vector3i pos) {
        return lockGates.get(pos);
    }

    public Map<Vector3i, LockGateObject> getAllLockGates() {
        return lockGates;
    }

    public void addStar(StarObject star) {
        stars.put(star.getGridPosition(), star);
        if (star.getModelInstance() != null) {
            renderInstances.add(star.getModelInstance());
        }
    }

    public StarObject getStar(Vector3i pos) {
        return stars.get(pos);
    }

    public Map<Vector3i, StarObject> getAllStars() {
        return stars;
    }

    public void removeStar(Vector3i pos) {
        StarObject s = stars.remove(pos);
        if (s != null && s.getModelInstance() != null) {
            renderInstances.removeValue(s.getModelInstance(), true);
        }
    }

    public Map<Vector3i, SwitchObject> getAllSwitches() {
        return switches;
    }

    public Map<Vector3i, GateObject> getAllGates() {
        return gates;
    }

    public void setGoal(GoalObject goal) {
        this.goalObject = goal;
        if (goal != null && goal.getModelInstance() != null) {
            renderInstances.add(goal.getModelInstance());
        }
    }

    public GoalObject getGoal() {
        return goalObject;
    }

    public void addRenderInstance(ModelInstance instance) {
        if (instance != null) {
            renderInstances.add(instance);
        }
    }

    public Array<ModelInstance> getRenderInstances() {
        return renderInstances;
    }

    public boolean isValidStep(Vector3i pos, Direction moveDir) {
        GridTile tile = tiles.get(pos);
        if (tile == null || !tile.isWalkable()) {
            return false;
        }

        // Check if blocked by locked gate
        LockGateObject lock = lockGates.get(pos);
        if (lock != null && !lock.isUnlocked()) {
            return false;
        }

        // Check if blocked by closed switch gate
        GateObject gate = gates.get(pos);
        if (gate != null && !gate.isOpen()) {
            if (gate.getPassageDirection() != null && gate.getPassageDirection() != moveDir) {
                return false;
            }
        }

        return true;
    }

    public void clear() {
        tiles.clear();
        arrows.clear();
        switches.clear();
        gates.clear();
        teleporters.clear();
        spikes.clear();
        keys.clear();
        lockGates.clear();
        stars.clear();
        goalObject = null;
        renderInstances.clear();
    }

    public int getSizeX() { return sizeX; }
    public int getSizeY() { return sizeY; }
    public int getSizeZ() { return sizeZ; }
}
