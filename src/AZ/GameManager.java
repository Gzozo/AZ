package AZ;

public interface GameManager
{
    /**
     * Eltávolítja a GameEntity-t
     *
     * @param entity A GameEntity
     */
    void RemoveEntity(GameEntity entity);
    
    /**
     * Ütközést vizsgál az össze tankkal
     *
     * @param ammo A lövedék ami ütközik
     * @return ütközött e?
     */
    boolean CheckTank(Ammo ammo);
    
    /**
     * Hozzáad egy GameEntity-t
     *
     * @param entity A GameEntity
     */
    void AddEntity(GameEntity entity);
    
    /**
     * Halott kliens értesítése, hogy meghalt
     *
     * @param t A kliens tankja
     */
    void deadTank(Tank t);
    
    /**
     * Zene lejátszása
     *
     * @param f Zene neve
     */
    void PlayMusic(String f);
    
    
    long ellapsedTime();
    
    
}
