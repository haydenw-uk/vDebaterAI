import java.lang.reflect.Array;
import java.util.ArrayList;

public class Chamber {
    private ArrayList<Agent> presentEntities;
    private String houseMotion;
    private boolean debateRunning;
    private int ayesVoteCount;
    private int noesVoteCount;
    public Chamber(String sHouseMotion)
    {
        this.houseMotion = sHouseMotion;
        System.out.println("[INFO] MOTION HAS BEEN SET");

        this.ayesVoteCount = 0;
        this.noesVoteCount = 0;

        presentEntities = new ArrayList<>();

    }

    public void AddEntityToChamber(Agent entityToAdd)
    {
        presentEntities.add(entityToAdd);
        System.out.println("[INFO] " + entityToAdd.getAgentName().toUpperCase() + " (" + entityToAdd.getDebatePosition().toString().toUpperCase() + ")"  + " HAS ENTERED THE CHAMBER.");
    }

    public ArrayList<Agent> getAllEntitiesPresent()
    {
        return presentEntities;
    }

    public int getAyesVoteCount() {
        return ayesVoteCount;
    }

    public void setAyesVoteCount(int ayesVoteCount) {
        this.ayesVoteCount = ayesVoteCount;
    }

    public int getNoesVoteCount() {
        return noesVoteCount;
    }

    public void setNoesVoteCount(int noesVoteCount) {
        this.noesVoteCount = noesVoteCount;
    }

    public String getHouseMotion() {
        return houseMotion;
    }

    public void setHouseMotion(String houseMotion) {
        this.houseMotion = houseMotion;
    }

    public boolean isDebateRunning() {
        return debateRunning;
    }

    public void setDebateRunning(boolean debateRunning) {
        this.debateRunning = debateRunning;
    }
}
