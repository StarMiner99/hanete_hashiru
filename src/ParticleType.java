import java.awt.image.BufferedImage;

public class ParticleType {
    public double accelerationX;
    public double accelerationY;
    public int lifetime = 100;
    public int[] alpha = {255};
    public int[][] colors = {{255},{255},{255}};
    public ParticleType onDeath;
    public int deathChance = 0;
    public double speedMin;
    public double speedMax;
    public double speedIncrease;
    public double speedWiggle;
    public double directionMin;
    public double directionMax;
    public double directionIncrease;
    public double directionWiggle;
    public double scaleMax;
    public double scaleIncrease;
    public double scaleWiggle;
    public double scaleMin;
    public double rotationMin;
    public double rotationMax;
    public double rotationIncrease;
    public double rotationWiggle;
    public double sizeMin;
    public double sizeMax;
    public double sizeIncrease;
    public double sizeWiggle;
    public BufferedImage sprite;
    public ParticleSystem.shapes shape;

    public void setAcceleration(double accelerationX, double accelerationY) {
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }
    public void setSpeed(double min,double max,double incr, double wiggle) {
        this.speedMin = min;
        this.speedMax = max;
        this.speedIncrease = incr;
        this.speedWiggle = wiggle;
    }
    public void setRotation(double min,double max,double incr, double wiggle) {
        this.rotationMin = min;
        this.rotationMax = max;
        this.rotationIncrease = Math.toRadians(incr);
        this.rotationWiggle = Math.toRadians(wiggle);
    }
    public void setScale(double min,double max,double incr, double wiggle) {
        this.scaleMin = min;
        this.scaleMax = max;
        this.scaleIncrease = incr;
        this.scaleWiggle = wiggle;
    }
    public void setSprite(BufferedImage sprite)
    {
        this.sprite = sprite;
    }
    public void setDirection(double min,double max,double incr, double wiggle) {
        this.directionMin = min;
        this.directionMax = max;
        this.directionIncrease = incr;
        this.directionWiggle = wiggle;
    }
    public void setAlpha(int[] alphaValues) {
        this.alpha = alphaValues;
    }
    public void setColors(int[][] colors) {
        this.colors = colors;
    }
    public void setLifeTime(int lifetime) {
        this.lifetime = lifetime;
    }

    public void setSize(double min, double max, double increase, double wiggle) {
        this.sizeMin = min;
        this.sizeMax = max;
        this.sizeIncrease = increase;
        this.sizeWiggle = wiggle;
    }

    public void setShape(ParticleSystem.shapes shape) {
        this.shape = shape;
    }

    public void onDeath(ParticleType type, int chance) {
        this.onDeath = type;
        this.deathChance = chance;
    }
}
