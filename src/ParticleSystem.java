import java.util.ArrayList;

public class ParticleSystem {
    public ArrayList<Particle> particles;
    public enum shapes {
        circle,
        rectangle
    }
    ParticleSystem() {
        particles = new ArrayList<>();
    }
    void addParticle(double x, double y, ParticleType type)
    {
        particles.add(new Particle(x,y,type));
    }
}