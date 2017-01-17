package Simulation;

import Army.SquadVikings;
import Army.SquadVillagers;
import Army.Viking;
import Army.Villager;
import Map.Building;
import Map.Village;
import Schemes.Colors;

import java.awt.*;

/**
 * Created by anka on 16.01.17.
 */
public class Stats {
    private Generator generator;

    private double vikingsHealth;
    private int vikingsAlive;
    private int vikingsRetreated;
    private int vikingsDead;
    private int lootOnVikings;
    private String vikingsState;

    private double villagersHealth;
    private int villagersAlive;
    private int villagersRetreated;
    private int villagersDead;
    private int lootInVillage;
    private String villagersState;

    public Stats(Generator generator) {
        this.generator = generator;
        vikingsHealth = 0;
        vikingsAlive = 0;
        vikingsDead = 0;
        vikingsRetreated = 0;
        lootOnVikings = 0;
        for (SquadVikings i : generator.getVikings().getSquads()){
            for (Viking j : i.getVikings()){
                vikingsAlive ++;
                vikingsHealth += j.getHealth();
            }
        }
        switch (generator.getVikings().getState()){
            case 0 : vikingsState = "Loss";
                break;
            case 1 : vikingsState = "Win";
                break;
            case 2 : vikingsState = "Fighting";
                break;
        }

        villagersHealth = 0;
        villagersAlive = 0;
        villagersDead = 0;
        villagersRetreated = 0;
        lootInVillage = 0;
        for (SquadVillagers i : generator.getVillagers().getSquads()){
            for (Villager j : i.getVillagers() ){
                villagersAlive ++;
                villagersHealth += j.getHealth();
            }
        }

        for (Building i : generator.getVillage().getBuildings()){
            lootInVillage += i.getLoot();
        }
        switch (generator.getVillagers().getState()){
            case 0 : villagersState = "Loss";
                break;
            case 1 : villagersState = "Win";
                break;
            case 2 : villagersState = "Fighting";
                break;
        }
    }

    public void estimate(){
        vikingsHealth = 0;
        vikingsAlive = 0;
        vikingsDead = 0;
        vikingsRetreated = 0;
        lootOnVikings = 0;
        for (SquadVikings i : generator.getVikings().getSquads()){
            for (Viking j : i.getVikings()){
                vikingsHealth += j.getHealth();
                lootOnVikings += j.getLoot();
                if (j.getHealth() == 0) vikingsDead ++;
                else vikingsAlive ++;
                if (j.getState() == 2) vikingsRetreated ++;
            }
        }
        switch (generator.getVikings().getState()){
            case 0 : vikingsState = "Loss";
                break;
            case 1 : vikingsState = "Win";
                break;
            case 2 : vikingsState = "Fighting";
                break;
        }

        villagersHealth = 0;
        villagersAlive = 0;
        villagersDead = 0;
        villagersRetreated = 0;
        lootInVillage = 0;
        for (SquadVillagers i : generator.getVillagers().getSquads()){
            for (Villager j : i.getVillagers() ){
                villagersHealth += j.getHealth();
                if (j.getHealth() == 0) villagersDead ++;
                else villagersAlive ++;
                if (j.getState() == 2) villagersRetreated ++;
            }
        }

        for (Building i : generator.getVillage().getBuildings()){
            lootInVillage += i.getLoot();
        }
        switch (generator.getVillagers().getState()){
            case 0 : villagersState = "Loss";
                break;
            case 1 : villagersState = "Win";
                break;
            case 2 : villagersState = "Fighting";
                break;
        }
    }

    public void draw(Graphics g){
        g.setFont(new Font("TimesRoman", Font.BOLD, 25));
        g.setColor(Colors.VIKING);
        g.drawString("VIKINGS", 1010 , 250);
        g.setColor(Colors.VILLAGER);
        g.drawString("VILLAGERS", 1010, 600);

        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.setColor(Colors.VIKING);
        g.drawString("Health " + String.valueOf(this.vikingsHealth),1010, 290);
        g.drawString("Alive " + String.valueOf(this.vikingsAlive),1010, 330);
        g.drawString("Dead " + String.valueOf(this.vikingsDead),1010, 370);
        g.drawString("Retreated " + String.valueOf(this.vikingsRetreated),1010, 410);
        g.drawString("Loot " + String.valueOf(this.lootOnVikings),1010, 450);
        g.setFont(new Font("TimesRoman", Font.BOLD, 25));
        g.drawString(vikingsState, 1010, 500);

        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.setColor(Colors.VILLAGER);
        g.drawString("Health " + String.valueOf(this.villagersHealth),1010, 640);
        g.drawString("Alive " + String.valueOf(this.villagersAlive),1010, 680);
        g.drawString("Dead " + String.valueOf(this.villagersDead),1010, 720);
        g.drawString("Retreated " + String.valueOf(this.villagersRetreated),1010, 760);
        g.drawString("Loot " + String.valueOf(this.lootInVillage),1010, 800);
        g.setFont(new Font("TimesRoman", Font.BOLD, 25));
        g.drawString(villagersState, 1010, 850);
    }
}
