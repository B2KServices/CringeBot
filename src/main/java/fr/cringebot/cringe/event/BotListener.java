/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BotListener.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 11:45:58 by gchatain          #+#    #+#             */
/*   Updated: 2022/07/05 20:26:02 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */


package fr.cringebot.cringe.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.Polls.PollListener;
import fr.cringebot.cringe.Polls.PollMessage;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.cki.cki;
import fr.cringebot.cringe.cki.ckiListener;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.lol.Champion;
import fr.cringebot.cringe.objects.DetectorAttachment;
import fr.cringebot.cringe.objects.Emotes;
import fr.cringebot.cringe.objects.activity;
import fr.cringebot.cringe.pokemon.objects.Attacks;
import fr.cringebot.cringe.pokemon.objects.Pokemon;
import fr.cringebot.cringe.reactionsrole.MessageReact;
import fr.cringebot.cringe.reactionsrole.RoleReaction;
import fr.cringebot.cringe.siterequest.Request;
import fr.cringebot.cringe.waifus.Waifu;
import fr.cringebot.cringe.waifus.WaifuCommand;
import fr.cringebot.cringe.xp.TextuelXp;
import fr.cringebot.cringe.xp.XP;
import fr.cringebot.music.MusicCommand;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static fr.cringebot.BotDiscord.isMaintenance;
import static fr.cringebot.cringe.event.MembersQuotes.MemberReact;
import static fr.cringebot.cringe.event.MenuInteract.onSelectMenu;
import static fr.cringebot.cringe.event.memesEvent.postmeme;

/**
 * capture tout les evenements du bot
 */
public class BotListener implements EventListener {

	private final CommandMap commandMap;
	private final BotDiscord bot;

	public BotListener(CommandMap cmd, BotDiscord bot) {
		this.commandMap = cmd;
		this.bot = bot;
	}

	public static class ArrayNumber<E extends Number> {
		public ArrayList<E> li = new ArrayList<>();

		public ArrayNumber() {
		}

		public ArrayNumber(E element) {
			li.add(element);
		}

		@Override
		public String toString() {
			return li.toString();
		}
	}



	public static Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ArrayNumber.class,
			(JsonDeserializer<ArrayNumber<Integer>>) (jsonElement, type, jsonDeserializationContext) -> {
				if (!jsonElement.isJsonArray())
					return new ArrayNumber<>(jsonElement.getAsNumber().intValue());
				ArrayNumber<Integer> n = new ArrayNumber<>();
				JsonArray ar = jsonElement.getAsJsonArray();
				for (int i = 0; i < ar.size(); i++)
					n.li.add(ar.get(i).getAsNumber().intValue());
				return n;
			}).create();

	@Override
	public void onEvent(GenericEvent event) {
		System.out.println(event.getClass().getSimpleName());
		try {
			if (event instanceof ReadyEvent) onEnable((ReadyEvent) event);
			else if (event instanceof MessageReceivedEvent) onMessage((MessageReceivedEvent) event);
			else if (event instanceof GuildMemberJoinEvent) onGuildMemberJoin((GuildMemberJoinEvent) event);
			else if (event instanceof GuildMemberRemoveEvent) onGuildMemberLeave((GuildMemberRemoveEvent) event);
			else if (event instanceof MessageReactionAddEvent) onAddReact((MessageReactionAddEvent) event);
			else if (event instanceof SelectMenuInteractionEvent) onSelectMenu((SelectMenuInteractionEvent) event);
			else if (event instanceof GatewayPingEvent) onPing((GatewayPingEvent) event);
			else if (event instanceof RoleCreateEvent) onCreateRole((RoleCreateEvent) event);
			else if (event instanceof RoleDeleteEvent) onDeleteRole((RoleDeleteEvent) event);
			else if (event instanceof MessageReactionRemoveEvent) onRemoveReact((MessageReactionRemoveEvent) event);
			else if (event instanceof GuildUpdateNameEvent) onChangeServerName((GuildUpdateNameEvent) event);
			else if (event instanceof GuildVoiceJoinEvent) onConnect((GuildVoiceJoinEvent) event);
			else if (event instanceof GuildVoiceLeaveEvent) onDisconnect((GuildVoiceLeaveEvent) event);
			else if (event instanceof MessageEmbedEvent) onEmbed((MessageEmbedEvent) event);
			else if (event instanceof SlashCommandInteraction) onSlashCommand((SlashCommandInteraction) event);
			else if (event instanceof ButtonInteractionEvent) onButton((ButtonInteractionEvent) event);
			else if (event instanceof GuildVoiceMoveEvent) onMove((GuildVoiceMoveEvent) event);
		} catch (IOException | InterruptedException | IllegalAccessException | NoSuchFieldException | ExecutionException e) {
			e.printStackTrace();
			event.getJDA().getGuilds().get(0).getMemberById("315431392789921793").getUser().openPrivateChannel().complete().sendMessage("erreur sur " + event.getClass().getSimpleName()).queue();
		}
	}

	private void onMove(GuildVoiceMoveEvent event) {
		if (XP.isVoiceXp(event.getChannelLeft()))
			for (Member mb : event.getChannelJoined().getMembers())
				XP.end(mb);
		if (XP.isVoiceXp(event.getChannelJoined()))
			for (Member mb : event.getChannelJoined().getMembers())
				XP.start(mb);
			else XP.end(event.getMember());
	}

	private void onButton(ButtonInteractionEvent event) {
		return;
	}

	private void onSlashCommand(SlashCommandInteraction event) {
		event.reply("en attente d'update de JDA").setEphemeral(true).queue();
	}

	private void onDisconnect(GuildVoiceLeaveEvent event) {
		if (event.getMember().getUser().equals(event.getJDA().getSelfUser()))
			MusicCommand.stop(event.getGuild());
		XP.end(event.getMember());
		if (!XP.isVoiceXp(event.getChannelLeft()))
			for (Member mb : event.getChannelLeft().getMembers())
				XP.end(mb);
		System.out.println(event.getMember().getUser().getName() + " s'est déconnecté");
	}

	private void onConnect(GuildVoiceJoinEvent event) {
		System.out.println(event.getMember().getUser().getName() + " s'est connecté");
		if (XP.isVoiceXp(event.getChannelJoined()))
			for (Member mb : event.getChannelJoined().getMembers())
				XP.start(mb);
		XP.start(event.getMember());
	}

	private void onChangeServerName(GuildUpdateNameEvent event) {
		if (event.getGuild().getId().equals("382938797442334720"))
			Request.sendRequest(Request.Type.SETSEASON, event.getNewName());
	}

	private void onRemoveReact(MessageReactionRemoveEvent event) {
		if (event.getChannel().getId().equals("853210283480055809")) {
			for (MessageReact mr : MessageReact.message) {
				if (event.getMessageId().equals(mr.getId())) {
					for (RoleReaction rr : mr.list) {
						if (event.getEmoji().getAsReactionCode().equals(rr.getEmote())) {
							event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(rr.getId())).queue();
							return;
						}
					}
				}
			}
		}
	}

	private void onDeleteRole(RoleDeleteEvent event) {
		for (MessageReact mr : MessageReact.message)
			for (RoleReaction rr : mr.list)
				if (rr.getId().equals(event.getRole().getId())) {
					mr.list.remove(rr);
					mr.refresh(event.getGuild());
				}
	}

	private void onCreateRole(RoleCreateEvent event) {
		new Thread(() -> {
			for (MessageReact mr : MessageReact.message)
				mr.refresh(event.getJDA().getGuildById("382938797442334720"));
		}).start();
		if (!event.getRole().getName().startsWith("©◊ß"))
			event.getRole().delete().queue();
		else
			event.getRole().getManager().setName(event.getRole().getName().replace("©◊ß", "")).queue();
	}

	/**
	 * evenement quand un Embed est posté
	 *
	 * @param event
	 */
	private void onEmbed(MessageEmbedEvent event) {
		if (event.getChannel().getId().equals("461606547064356864")) {
			Message ret;
			Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
			if (DetectorAttachment.isReddit(msg.getContentRaw()))
				ret = memesEvent.repostReddit(msg, null, msg.getTextChannel());
			else if (DetectorAttachment.isTwitter(msg.getContentRaw()))
				ret = memesEvent.repostTwitter(msg, null, msg.getTextChannel());
			else
				return;
			ret.addReaction(ret.getGuild().getEmojiById(Emotes.rirederoite))
					.and(ret.addReaction(msg.getGuild().getEmojiById(Emotes.anto)))
					.and(ret.addReaction(msg.getGuild().getEmojiById(Emotes.porte))).queue();
			msg.delete().queue();
		}
	}

	/**
	 * evenement quand discord cherche si le bot existe encore
	 * @param event
	 */
	private void onPing(GatewayPingEvent event) {
		PollListener.verifTimePoll(event.getJDA());
		TextuelXp.verif();
	}

	/**
	 * au lancement
	 *
	 * @param event
	 */
	private void onEnable(ReadyEvent event) {
		Activity act;
		act = new activity("Analyse en cours", null, Activity.ActivityType.WATCHING);
		bot.getJda().getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, act);
		if (new File("save").mkdir())
			System.out.println("création du directoryCentral");
		if (new File("save/gift").mkdir())
			System.out.println("CADO");
		if (new File("save/waifu").mkdir())
			System.out.println("création du directory Waifu");
		Request.sendRequest(Request.Type.SETSEASON, event.getJDA().getGuildById("382938797442334720").getName());
		Squads.load();
		MessageReact.load();
		PollMessage.load();
		cki.load();
		Waifu.load();
		MessageReact.load();




		for (MessageReact mr : MessageReact.message) {
				mr.refresh(event.getJDA().getGuildById("382938797442334720"));
			}
		new Thread(() -> {
			for (Member mem : event.getJDA().getGuildById("382938797442334720").getMembers()) {
				if (mem.getRoles().get(0).getPosition() < mem.getGuild().getRoleById("734011696242360331").getPosition())
					event.getJDA().getGuildById("382938797442334720").addRoleToMember(mem, event.getJDA().getGuildById("382938797442334720").getRoleById("734011696242360331")).and(event.getJDA().getGuildById("382938797442334720").addRoleToMember(mem, event.getJDA().getGuildById("382938797442334720").getRoleById("634839000644845619"))).and(event.getJDA().getGuildById("382938797442334720").addRoleToMember(mem, event.getJDA().getGuildById("382938797442334720").getRoleById("734012661494317077"))).queue();
			}
		}).start();
		new Thread(() -> recupMeme(event.getJDA().getGuildById("382938797442334720"))).start();
		Pokemon.pok = gson.fromJson(new BufferedReader(new InputStreamReader(BotListener.class.getClassLoader().getResourceAsStream("pokemons.json"))), new TypeToken<Collection<Pokemon>>() {
		}.getType());

		Champion.champions = gson.fromJson(new BufferedReader(new InputStreamReader(BotListener.class.getClassLoader().getResourceAsStream("Champions.json"))), new TypeToken<HashMap<String, Champion>>() {
		}.getType());

		Attacks.capa = gson.fromJson(new BufferedReader(new InputStreamReader(BotListener.class.getClassLoader().getResourceAsStream("attacks.json"))), new TypeToken<Collection<Attacks>>() {
		}.getType());

		if (isMaintenance) {
			act = new activity(", je suis en maintenance", null, Activity.ActivityType.LISTENING);
			bot.getJda().getPresence().setPresence(OnlineStatus.ONLINE, act);

		}
		else {
			act = new activity(", si tu lis ça tu es cringe", null, Activity.ActivityType.LISTENING);
			bot.getJda().getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, act);
		}

	}

	/**
	 * sert à faire une vérification du salon meme
	 * @param g
	 */
	private void recupMeme(Guild g) {
		TextChannel tc = g.getTextChannelById("461606547064356864");
		List<Message> msgs = tc.getHistory().retrievePast(100).complete();
		int i = 0;
		while(i < 100)
		{
			if (!msgs.get(i).getAuthor().isBot() && DetectorAttachment.isAnyLink(msgs.get(i)))
			{
				try {
					memesEvent.postmeme(msgs.get(i));
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if (!msgs.get(i).getAuthor().isBot())
				msgs.get(i).delete().queue();
			i++;
		}
	}

	/**
	 * si une nouvelle personne a rejoint
	 *
	 * @param event
	 */
	private void onGuildMemberJoin(GuildMemberJoinEvent event) {
		event.getGuild().getTextChannelById("947564791759777792").sendMessage(event.getUser().getAsMention() + " a rejoint la Guild.").queue();
		event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("734011696242360331")).and(event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("634839000644845619"))).and(event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("734012661494317077"))).queue();
	}

	/**
	 * si une personne nous as quitté
	 *
	 */
	private void onGuildMemberLeave(GuildMemberRemoveEvent event) {
		event.getGuild().getTextChannelById("947564791759777792").sendMessage(event.getUser().getAsMention() + " a quitté la Guild.").queue();
	}

	/**
	 * si une personne ajoute une réaction
	 *
	 */
	private void onAddReact(MessageReactionAddEvent event) throws NoSuchFieldException, IllegalAccessException, IOException {
		if (event.getJDA().getSelfUser().equals(event.getMember().getUser()))
			return;

		if (event.getReaction().getEmoji().getAsReactionCode().equals("◀️")) {
			Message msg = event.retrieveMessage().complete();
			if (!msg.getEmbeds().isEmpty() && msg.getEmbeds().get(0).getTitle().startsWith("Listes des waifus")){
				WaifuCommand.listwaifu(msg, Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText())-1);
				event.getReaction().removeReaction(event.getUser()).queue();
				return;
			} else if (!msg.getEmbeds().isEmpty() && msg.getEmbeds().get(0).getTitle().startsWith("Waifu de")){
				WaifuCommand.haremEmbed(msg, Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText())-1);
				event.getReaction().removeReaction(event.getUser()).queue();
				return;
			}
		}
		else if (event.getReaction().getEmoji().getAsReactionCode().equals("▶️")) {
			Message msg = event.retrieveMessage().complete();
			if (!msg.getEmbeds().isEmpty() && msg.getEmbeds().get(0).getTitle().startsWith("Listes des waifus")){
				WaifuCommand.listwaifu(msg, Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText())+1);
				event.getReaction().removeReaction(event.getUser()).queue();
				return;
			} else if (!msg.getEmbeds().isEmpty() && msg.getEmbeds().get(0).getTitle().startsWith("Waifu de")){
				WaifuCommand.haremEmbed(msg, Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText())+1);
				event.getReaction().removeReaction(event.getUser()).queue();
				return;
			}
		}
		else if (event.getReaction().getEmoji().getAsReactionCode().equals("⬆️") ) {
			Message msg = event.retrieveMessage().complete();
			int u = 0;
			for (MessageReaction reaction : msg.getReactions()) {
				if (reaction.getEmoji().getAsReactionCode().equals("⬆️"))
					u = reaction.getCount() - 1;
			}
			if (msg.getAuthor().equals(event.getJDA().getSelfUser()))
			if (msg.getContentRaw().equalsIgnoreCase("ratio") && u >= 2)
				msg.editMessage("Turbo ratio").queue();
			else if (msg.getContentRaw().equalsIgnoreCase("Turbo ratio") && u >= 4)
					msg.editMessage("Super ratio").queue();
			else if (msg.getContentRaw().equalsIgnoreCase("Super ratio") && u >= 6)
					msg.editMessage("Hyper ratio").queue();
			else if (msg.getContentRaw().equalsIgnoreCase("Hyper ratio") && u >= 8)
					msg.editMessage("Méga ratio").queue();
			else if (msg.getContentRaw().equalsIgnoreCase("Méga ratio") && u >= 11)
					msg.editMessage("Giga ratio").queue();
			else if (msg.getContentRaw().equalsIgnoreCase("Giga ratio") && u >= 14)
					msg.editMessage("RATIO ÉPIQUE").queue();
			else if (msg.getContentRaw().equalsIgnoreCase("RATIO ÉPIQUE") && u >= 17)
					msg.editMessage("RATIO MYTHIQUE").queue();
			else if (msg.getContentRaw().equalsIgnoreCase("RATIO MYTHIQUE") && u >= 20)
					msg.editMessage("**RATIO LÉGENDAIRE**").queue();
			return;
		}
		if (event.getChannel().getId().equals("461606547064356864")) {
			memesEvent.addReaction(event.getChannel().retrieveMessageById(event.getMessageId()).complete(), event.getReaction());
			return;
		}
		if (event.getChannel().getId().equals("853210283480055809")) {
			for (MessageReact mr : MessageReact.message)
				if (event.getMessageId().equals(mr.getId())) {
					for (RoleReaction rr : mr.list) {
						if (event.getEmoji().getAsReactionCode().equals(rr.getEmote())) {
							event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(rr.getId())).queue();
							return;
						}
					}
				}
			return;
		}

		MusicCommand.MusicAddReact(event);
	}



	/**
	 * à la recption d'un message
	 *
	 */
	private void onMessage(MessageReceivedEvent event) throws IOException, InterruptedException {
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		if (!event.getGuild().getId().equals("382938797442334720")) return;
		Message msg = event.getMessage();
		if (msg.getChannel().getId().equals("912092369292263534"))
			msg.createThreadChannel("requete n°" + msg.getId()).complete().addThreadMember(event.getGuild().getMemberById("315431392789921793"))
					.and(msg.addReaction(Emoji.fromFormatted("⬆️")))
					.and(msg.addReaction(Emoji.fromFormatted("⬇️")))
					.queue();
		if (msg.getMentions().getMembers().contains(msg.getGuild().getMemberById(event.getJDA().getSelfUser().getId())) && msg.getReferencedMessage() == null)
			msg.getChannel().sendMessage("Hé oh t'es qui a me ping, tu veux te battre ?\nfais un ping everyone pendant que t'y est").queue();
		if (msg.getContentRaw().startsWith(CommandMap.getTag())) {
			commandMap.commandUser(msg.getContentRaw().replaceFirst(CommandMap.getTag(), ""), event.getMessage());
			return;
		}
		TextuelXp.addmsg(event.getMember());
		if (cki.wtpThreads.containsKey(msg.getChannel().getId()))
			new ckiListener(msg, cki.wtpThreads.get(msg.getChannel().getId()));

		if (msg.getChannel().getId().equals("461606547064356864"))
			new Thread(() -> postmeme(msg)).start();

		String[] args = msg.getContentRaw().split(" ");

		if (MemberReact(msg))
			return;

		ReactionEvent.reactionevent(msg, msg.getJDA());
		//tous les events mis sans le prefix les reactions en gros


		if (msg.getContentRaw().equalsIgnoreCase("meme") && msg.getReferencedMessage() != null) {
			memesEvent.postmeme(msg.getReferencedMessage());
			msg.delete().queue();
		}


	}
}
