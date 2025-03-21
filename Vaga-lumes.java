package Vaga-lumes;

import robocode.*;
import java.awt.Color;
import java.util.Random;

public class 20comer70correr extends AdvancedRobot {

    private Random random = new Random();
    private boolean movingForward;
    private int turnDirection = 1;
    private int moveAmount = 50;
    private double enemyBearing;
    private double enemyDistance;
    private double enemyHeading;
    private double enemyVelocity;

    public void run() {
        setColors(Color.BLACK, Color.GRAY, Color.RED);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        movingForward = true;

        while (true) {
            moveZigZag();
            scan();
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        enemyBearing = e.getBearing();
        enemyDistance = e.getDistance();
        enemyHeading = e.getHeading();
        enemyVelocity = e.getVelocity();

        double absoluteBearing = getHeading() + enemyBearing;
        double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

        if (enemyDistance > 200) {
            turnGunRight(bearingFromGun);
            if (Math.abs(bearingFromGun) < 5) {
                fire(calculateFirePower(enemyDistance));
            }
        } else {
            turnGunRight(bearingFromGun);
            if (Math.abs(bearingFromGun) < 10) {
                fire(2);
            }
            turnRight(normalRelativeAngleDegrees(enemyBearing + 90));
            ahead(100);
        }

        turnRadarRight(normalRelativeAngleDegrees(absoluteBearing - getRadarHeading()));
    }

    public void onHitWall(HitWallEvent e) {
        double bestAngle = findBestEscapeAngle();
        turnRight(normalRelativeAngleDegrees(bestAngle - getHeading()));
        ahead(100);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        double bearing = e.getBearing();
        turnRight(normalRelativeAngleDegrees(bearing + 90));
        for (int i = 0; i < 6; i++) {
            ahead(50);
            turnGunRight(60);
            fire(1);
            turnRight(60);
        }
    }

    private void moveZigZag() {
        if (movingForward) {
            ahead(moveAmount);
        } else {
            back(moveAmount);
        }
        turnRight(45 * turnDirection);
        if (getDistanceRemaining() == 0) {
            movingForward = !movingForward;
            turnDirection *= -1;
        }
    }

    private double findBestEscapeAngle() {
        double bestAngle = 0;
        double maxSpace = 0;
        for (int i = 0; i < 36; i++) {
            double angle = i * 10;
            double space = calculateSpace(angle);
            if (space > maxSpace) {
                maxSpace = space;
                bestAngle = angle;
            }
        }
        return bestAngle;
    }

    private double calculateSpace(double angle) {
        double x = getX() + Math.sin(Math.toRadians(angle)) * 100;
        double y = getY() + Math.cos(Math.toRadians(angle)) * 100;
        if (x < 50 || x > getBattleFieldWidth() - 50 || y < 50 || y > getBattleFieldHeight() - 50) {
            return 0;
        }

        return 100;
    }

    private double calculateFirePower(double distance) {
        if (distance > 500) {
            return 3;
        } else if (distance > 300) {
            return 2;
        } else {
            return 1;
        }
    }
}