import java.awt.*;

public class Particle {
    double x;
    double y;
    double velocityX;
    double velocityY;
    int[] alpha;
    double lifetime;
    double lifetimeStart;
    public int currentAlpha;
    ParticleType type;
    private final int[] red;
    private final int[] green;
    private final int[] blue;
    public Color currentColor;
    public double speed;
    public double direction;
    public boolean hasSprite = false;
    public Image sprite;
    public int spriteWidth;
    public int spriteHeight;
    private double scale;
    public double rotation;
    public double size;
    public ParticleType onDeath;
    public boolean createDeath;
    Particle(double originX, double originY, ParticleType particleType) {
        x = originX;
        y = originY;
        type = particleType;
        lifetime = type.lifetime;
        lifetimeStart = lifetime;
        alpha = type.alpha;
        currentAlpha = alpha[0];
        red = type.colors[0];
        green = type.colors[1];
        blue = type.colors[2];
        speed = randomRange(type.speedMin,type.speedMax);
        direction = randomRange(type.directionMin,type.directionMax);
        rotation = randomRange(type.rotationMin,type.rotationMax);
        currentColor = new Color(red[0], green[0], blue[0], currentAlpha);
        if (randomRange(0,type.deathChance)>randomRange(0,100))
        {
            createDeath = true;
            onDeath = type.onDeath;
        }
        if (type.sprite != null) {
            hasSprite = true;
            scale = randomRange(type.scaleMin,type.scaleMax);
        } else {
            size = randomRange(type.sizeMin, type.sizeMax);
        }
    }
    void update() {
        double t = 1 - lifetime / (lifetimeStart + 1);
        if (alpha.length > 1) {
            currentAlpha = interpolate(t * (alpha.length - 1), alpha);
        }
        if (hasSprite) {
            scale += type.scaleIncrease;
            scale += type.scaleWiggle * randomRange(-1,1);
            spriteWidth = (int) Math.max(type.sprite.getWidth()*scale,1);
            spriteHeight = (int) Math.max(type.sprite.getHeight()*scale,1);
            sprite = type.sprite.getScaledInstance(spriteWidth,spriteHeight,Image.SCALE_FAST);
        } else {
            if (red.length > 1 && green.length > 1 && blue.length > 1) {
                int currentRed = interpolate(t * (red.length - 1), red);
                int currentGreen = interpolate(t * (green.length - 1), green);
                int currentBlue = interpolate(t * (blue.length - 1), blue);
                currentColor = new Color(currentRed, currentGreen, currentBlue, currentAlpha);
            } else {
                currentColor = new Color(red[0],green[0],blue[0],currentAlpha);
            }
            size += type.sizeIncrease;
            size += type.sizeWiggle * randomRange(-1,1);
        }
        speed += type.speedIncrease;
        speed += type.speedWiggle * randomRange(-1,1);
        rotation += type.rotationIncrease;
        rotation += type.rotationWiggle * randomRange(-1,1);
        direction += type.directionIncrease;
        direction += type.directionWiggle * randomRange(-1,1);
        double aliveFor = (lifetimeStart-lifetime);
        velocityX = type.accelerationX*aliveFor + lengthDirX(speed,direction);
        velocityY = type.accelerationY*aliveFor + lengthDirY(speed,direction);
        x += velocityX;
        y += velocityY;
        lifetime--;
    }
    int interpolate(double t, int[] array) {
        double currentProgress = t%1;
        int index = (int) Math.min(t-currentProgress,array.length-2);
        return (int) lerp(array[index],array[index+1],currentProgress);
    }

    double lerp(double a, double b, double v) {
        return a + (b - a) * v;
    }

    public boolean isDead() {
        return lifetime <= 0;
    }
    double randomRange(double min, double max) {
        return Math.random() * (max - min) + min;
    }
    double lengthDirX(double len, double dir) {
        double rad = Math.toRadians(dir);
        return Math.cos(rad)*len;
    }
    double lengthDirY(double len, double dir) {
        double rad = Math.toRadians(dir);
        return Math.sin(rad)*len;
    }
}
