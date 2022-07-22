package fr.cringebot.cringe.slashInteraction;

import fr.cringebot.cringe.CommandBuilder.Gift;
import fr.cringebot.cringe.CommandBuilder.Info;
import fr.cringebot.cringe.CommandBuilder.Shop;
import fr.cringebot.cringe.CommandBuilder.Top;
import fr.cringebot.cringe.command.CommandListener;
import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;

public class SlashListener {
    public static void onSlashCommand(SlashCommandInteraction event) throws IOException, InterruptedException {
        if (event.getName().equals("gift"))
        {
            Gift ret = Gift.sendGift(event.getOption("code").getAsString(), event.getMember());
            EmbedBuilder eb = ret.getEmbedBuilder();
            if (ret.getId() == null)
                event.replyEmbeds(eb.build()).queue();
            else {
                ButtonImpl bttn = new ButtonImpl("gift_" + ret.getId(), "ouvrir", ButtonStyle.SUCCESS, false, null);
                event.replyEmbeds(eb.build()).addActionRow(bttn).queue();
            }
        }
        else if (event.getName().equals("info"))
        {
            if (event.getOption("nom") == null)
                event.replyEmbeds(Info.info(event.getMember()).build()).queue();
            else
                event.replyEmbeds(Info.info(event.getOption("nom").getAsMember()).build()).queue();
        }
        else if (event.getName().equals("top"))
        {
            if (event.getOption("nom") == null)
                event.replyEmbeds(Top.top(null, event.getGuild()).build()).queue();
            else
                event.replyEmbeds(Top.top(event.getOption("nom").getAsString(), event.getGuild()).build()).queue();
        }
        else if (event.getName().equals("shop"))
            event.replyEmbeds(Shop.ShopDisplay(event.getMember()).build()).addActionRow(Shop.PrincipalMenu()).queue();
        else
            event.reply("patience ça arrive").setEphemeral(true).queue();
    }

    public static void autoComplete(CommandAutoCompleteInteraction event) {
        if (event.getOptions().get(0).getName().equals("squadname")) {
            ArrayList<String> ret = new ArrayList<>();
            for (Squads sq : Squads.getAllSquads())
                ret.add(sq.getName());
            event.replyChoiceStrings(ret).queue();
        }
    }
}
