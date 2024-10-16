package AZ;

import java.awt.Color;
import java.util.List;
import java.util.Optional;

public class Rocket extends Ammo {
    private static final double TURNING_CIRCLE = 0.1;
    private static final double WOBBLE = 0.05;
    private static final int SEEK_DELAY = 60; // 1 second at 60 FPS

    private int seekTimer = SEEK_DELAY;
    private Tank target;
    private GameManager gameManager;

    public Rocket(double x, double y, double rot, Field f, GameManager gameManager) {
        super(x, y, 5, 2.5, rot, 600, f);
        setPic("rocket.png");
        name = "Rocket";
        shellCount = 5;
        this.gameManager = gameManager;
    }

    public Rocket(GameManager gameManager) {
        this(0, 0, 0, null, gameManager);
    }

    @Override
    public synchronized void Move(GameManager manager, boolean fal) {
        if (seekTimer > 0) {
            seekTimer--;
        } else {
            if (target == null) {
                target = findNearestPlayer();
            }
            if (target != null) {
                double targetAngle = Math.atan2(target.y - y, target.x - x);
                double angleDifference = targetAngle - rot;
                if (Math.abs(angleDifference) > TURNING_CIRCLE) {
                    rot += Math.signum(angleDifference) * TURNING_CIRCLE;
                } else {
                    rot = targetAngle;
                }
                rot += (Math.random() - 0.5) * WOBBLE;
            }
        }
        super.Move(manager, fal);
    }

    @Override
    public void OnDeath(GameManager manager) {
        // Handle rocket explosion
        manager.PlayMusic(Const.Music.rocketExplode);
    }

    @Override
    public Ammo newInstance(double x, double y, double rot, Field f) {
        return new Rocket(x, y, rot, f, gameManager);
    }

    @Override
    public Ammo newInstance() {
        return new Rocket(gameManager);
    }

    private Tank findNearestPlayer() {
        List<Tank> players = gameManager.getPlayers();
        Optional<Tank> nearestPlayer = players.stream()
//                .filter(player -> player != parent)
                .min((p1, p2) -> Double.compare(distanceTo(p1), distanceTo(p2)));
        return nearestPlayer.orElse(null);
    }

    private double distanceTo(Tank player) {
        return Math.sqrt(Math.pow(player.x - x, 2) + Math.pow(player.y - y, 2));
    }
}
