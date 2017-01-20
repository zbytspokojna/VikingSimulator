package Schemes;

import Armament.Weapon;

public class Weapons {
    //Declaration of weapons   For now values just estimated  R A D P
    public static final Weapon SPEAR = new Weapon(5,3,1,2);
    public static final Weapon SWORD = new Weapon(2,5,2,2);
    public static final Weapon AXE  = new Weapon(2,5,2,1);
    public static final Weapon DOUBLE_AXE = new Weapon(5,3,4,3);
    public static final Weapon BOW = new Weapon(40,7,1,0);

    public static final Weapon[] ARSENAL = {
            SPEAR,
            SWORD,
            AXE,
            DOUBLE_AXE,
            BOW
    };
}
