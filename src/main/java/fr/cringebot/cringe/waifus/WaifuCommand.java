package fr.cringebot.cringe.waifus;

import fr.cringebot.cringe.objects.SelectOptionImpl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class WaifuCommand {
	public static void addwaifu(Message msg) throws ExecutionException, InterruptedException {
		String[] args = msg.getContentRaw().split("\n");
		if (!msg.getAttachments().isEmpty() && msg.getAttachments().size() == 1)
		{
			ArrayList<SelectOption> options = new ArrayList<>();
			for (waifu.Type tpe: waifu.Type.values())
				options.add(new SelectOptionImpl("Catégorie : "+tpe.name(), tpe.name()));
			options.add(new SelectOptionImpl("Annuler", "stop"));
			SelectMenuImpl selectionMenu = new SelectMenuImpl( "waifu", "selectionnez un choix", 1, 1, false, options);
			msg.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle(args[0].substring(">addwaifu ".length())).setDescription(msg.getContentRaw().substring(args[0].length() + 1)).build()).addFile(msg.getAttachments().get(0).downloadToFile().get()).setActionRow(selectionMenu).queue();
		}
		else
		{
			msg.getChannel().sendMessage("t'es une merde").queue();
		}
	}
	public static void infowaifu(Message msg)
	{
		ArrayList<waifu> w = waifu.getWaifubyName(msg.getContentRaw().substring(">infowaifu ".length()));
		if (w != null)
			msg.getChannel().sendMessageEmbeds(w.get(0).EmbedWaifu(msg.getGuild()).build()).addFile(w.get(0).getProfile()).queue();
		else
			msg.getChannel().sendMessage("je ne connais aucune waifu a ce nom").queue();
	}
}
