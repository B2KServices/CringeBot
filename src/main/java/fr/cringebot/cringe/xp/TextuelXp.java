package fr.cringebot.cringe.xp;

import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;

public class TextuelXp {
    private Integer nbmsg;
    private Long starttime;
    private String id;

    private static HashMap<String, TextuelXp> timer = new HashMap<>();

    public TextuelXp(String id)
    {
        this.nbmsg = 0;
        this.starttime = System.currentTimeMillis();
        timer.put(id, this);
    }

    private Long calcul() {
        return Math.round(-100 *(Math.exp(((this.nbmsg-20)/2)) + 100));
    }

    public static void addmsg(Member m){
        if (!timer.containsKey(m.getId())){
            new TextuelXp(m.getId()) ;
        timer.get(m.getId()).addmsg();
        }
    }

    private void addmsg(){
        this.nbmsg = this.nbmsg + 1;
    }

    public static void verif() {
        long currenttime = System.currentTimeMillis();
        for (TextuelXp txp : timer.values()){
            if (txp.starttime + 3600000 < currenttime)
            {
                Squads.addPoints(txp.id, txp.calcul());
                timer.remove(txp.id);
            }
        }
    }
}