package fr.cringebot.cringe.cki;

import fr.cringebot.cringe.objects.StringExtenders;
import fr.cringebot.cringe.pokemon.objects.Pokemon;
import fr.cringebot.cringe.pokemon.objects.Type;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.IOException;

public class ckiListener {

    public ckiListener(Message msg, cki object)
    {
        if (msg.getContentRaw().equalsIgnoreCase("indice"))
            IndiceListener(msg, object);
        else if (StringExtenders.BetterIgnoreCase(object.getName(), msg.getContentRaw()))
            found(msg, object);
        else if (msg.getContentRaw().equalsIgnoreCase("abandon"))
        {
            if (object.getAction() < 5)
            {
                msg.getChannel().sendMessage("c'est trop tôt pour abandonner, continue !").queue();
                return;
            }
            EmbedBuilder eb = new EmbedBuilder().setTitle("Partie abandonné").setImage("https://assets.pokemon.com/assets/cms2/img/pokedex/detail/" + String.format("%03d", Pokemon.getByRealName(cki.wtpThreads.get(msg.getChannel().getId()).getName()).getId()) + ".png");
            eb.setFooter("Abandonné par "+ msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl());
            eb.setColor(new Color(255, 92, 243));
            sendresponse(msg, eb);
            cki.save();
        }
        else
        {
            msg.getChannel().sendMessage("raté").reference(msg).queue();
            object.addAction();
        }
    }

    private void found(Message msg, cki object) {
        EmbedBuilder eb = new EmbedBuilder();
        if (object.getType().equals(cki.Type.POKEMON))
            eb.setTitle("Le pokémon à été trouvé !").setImage("https://assets.pokemon.com/assets/cms2/img/pokedex/detail/" + String.format("%03d", Pokemon.getByRealName(object.getName()).getId()) + ".png");
        else if (object.getType().equals(cki.Type.LOL))
            eb.setTitle("Le champion de League of Legends à été trouvé").setImage("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/"+ object.getName().replace(' ','\0')+"_0.jpg");
        eb.setFooter("Trouvé par " + msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl());
        eb.setColor(Color.green);
        sendresponse(msg, eb);
        cki.save();
    }

    private void IndiceListener(Message msg, cki object){
        if (object.getType().equals(cki.Type.POKEMON))
            wtpIndice(msg, object);
        object.addIndice();
    }

    private void wtpIndice(Message msg, cki object)
    {
        if (object.getIndice().equals(0))
        {
            StringBuilder sb = new StringBuilder();
            sb.append(object.getName().charAt(0));
            int	i = 1;
            while (i++ < object.getName().length() - 1) {
                if (object.getName().charAt(i) == ' ')
                    sb.append("   ");
                else
                    sb.append(" _ ");
            }
            msg.getGuildChannel().getManager().setName(sb.toString()).queue();
        }
        else if (object.getIndice() == 1)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Type du pokémon : \n");
            for (Type type : Pokemon.getByRealName(object.getName()).getType())
                sb.append(type.getNamefr()).append(" ");
            msg.getChannel().sendMessage(sb.toString()).queue();
        }
        else if (object.getIndice() == 2)
        {
            Document doc;
            String region;
            try {
                doc = Jsoup.connect("https://pokemondb.net/pokedex/"+ Pokemon.getByRealName(object.getName()).getName()).get();
                region = doc.selectXpath("/html[1]/body[1]/main[1]/div[1]/div[1]/p[1]/abbr[1]").text();
            } catch (IOException e) {
                msg.getChannel().sendMessage("Je n'ai pas trouvé d'indice, va falloir que tu cherches par toi meme").queue();
                return;
            }
            msg.getChannel().sendMessage(region).queue();
        }
    }

    private void sendresponse(Message msg, EmbedBuilder eb) {
        Message ed = msg.getGuild().getTextChannelById(cki.wtpThreads.get(msg.getChannel().getId()).getChannel()).retrieveMessageById(cki.wtpThreads.get(msg.getChannel().getId()).getMessage()).complete();
        eb.setDescription(ed.getEmbeds().get(0).getDescription().replace("<ce pokémon>", cki.wtpThreads.get(msg.getChannel().getId()).getName()).replace("<ce champion>", cki.wtpThreads.get(msg.getChannel().getId()).getName()) + "\n\nle " + cki.wtpThreads.get(msg.getChannel().getId()).getType().getName() + " était : " + cki.wtpThreads.get(msg.getChannel().getId()).getName() + "\n\nNombre d'échec : " + cki.wtpThreads.get(msg.getChannel().getId()).getAction() + "\nNombre d'indice utilisé : " + cki.wtpThreads.get(msg.getChannel().getId()).getIndice());
        cki.wtpThreads.remove(msg.getChannel().getId());
        msg.getChannel().delete().queue();
        ed.editMessageEmbeds(eb.build()).queue();
    }

}

