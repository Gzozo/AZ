package AZ;

import java.awt.Color;
import java.util.List;
import java.util.Optional;

public class Rocket extends Ammo {
    private static final double TURNING_CIRCLE = 0.1;
    private static final double WOBBLE_FACTOR = 0.05;
    private static final int SEEK_DELAY = 60; // 1 second at 60 FPS

    private int seekTimer = SEEK_DELAY;
    private boolean seeking = false;

    public Rocket(double x, double y, double rot, Field f) {
        super(x, y, 5, 2.5, rot, 600, f);
        setPic("rocket.png");
        name = "Rocket";
        shellCount = 5;
    }

    public Rocket() {
        this(0, 0, 0, null);
    }

    @Override
    public void Move(GameManager manager, boolean fal) {
        if (seekTimer > 0) {
            seekTimer--;
        } else if (!seeking) {
            seeking = true;
        }

        if (seeking) {
            Optional<Tank> target = findNearestPlayer();
            target.ifPresent(this::seekTarget);
        }

        super.Move(manager, fal);
    }

    private Optional<Tank> findNearestPlayer() {
        List<Tank> players = f.getPlayers();
        return players.stream()
                .filter(player -> player != parent)
                .min((p1, p2) -> Double.compare(distanceTo(p1), distanceTo(p2)));
    }

    private double distanceTo(Tank player) {
        double dx = player.x - x;
        double dy = player.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void seekTarget(Tank target) {
        double targetAngle = Math.atan2(target.y - y, target.x - x);
        double angleDifference = targetAngle - rot;

        if (Math.abs(angleDifference) > TURNING_CIRCLE) {
            rot += Math.signum(angleDifference) * TURNING_CIRCLE;
        } else {
            rot = targetAngle;
        }

        rot += (Math.random() - 0.5) * WOBBLE_FACTOR;
    }

    @Override
    public void OnDeath(GameManager manager) {
        // Handle rocket explosion
        manager.PlayMusic(Const.Music.rocketExplosion);
        // Add explosion effect or damage logic here
    }

    @Override
    public Ammo newInstance(double x, double y, double rot, Field f) {
        return new Rocket(x, y, rot, f);
    }

    @Override
    public Ammo newInstance() {
        return new Rocket();
    }
}
