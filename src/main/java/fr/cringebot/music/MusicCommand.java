package fr.cringebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerOptions;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.lang.reflect.Field;

import static fr.cringebot.music.EmbedGenerator.getQueueEmbed;
import static fr.cringebot.music.EmbedGenerator.getVolumeEmbed;

public class MusicCommand {

    public static final MusicCommand INSTANCE;
    private static final MusicManager manager = new MusicManager();

    static {
        INSTANCE = new MusicCommand();
    }

    // Ceci stoppe les "guigui" qui font plein d'instances
    private MusicCommand() {
    }


    @Command(name = "volume", type = ExecutorType.USER, description = "changer le volume")
    private void volume(Guild guild, TextChannel textChannel, String[] args) {
        if (args.length == 0)
            volume(guild, textChannel, -1);
        else if (args[0].equalsIgnoreCase("reset"))
            volume(guild, textChannel, 50);
        else
            volume(guild, textChannel, Integer.parseInt(args[0]));
    }

    private static int volume(Guild guild, TextChannel textChannel, Integer vol) {
        MusicPlayer player = manager.getPlayer(guild);
        if (player.getListener().getCurrent() == null && textChannel != null) {
            textChannel.sendMessageEmbeds(new EmbedBuilder().setDescription("not playing").setColor(Color.red).build()).queue();
            return -1;
        }
        Field f = null;
        try {
            f = DefaultAudioPlayer.class.getDeclaredField("options");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if ((vol >= 0)&&(vol <= 300))
        {
            try {
                    vol = (vol/5);
                    ((AudioPlayerOptions) f.get(manager.getPlayer(guild).getAudioPlayer())).volumeLevel.set(vol);
            } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                textChannel.sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : volume 500").queue();
            } catch (IllegalAccessException e) {
                e.printStackTrace(); //ca peut arriver en cas de mise a jour de d??pendances, mais ca devrait ??tre bon sinon
            }
        } else {
            try {
                vol = ((AudioPlayerOptions) f.get(manager.getPlayer(guild).getAudioPlayer())).volumeLevel.get();
            } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                textChannel.sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : volume 500").queue();
            } catch (IllegalAccessException e) {
                e.printStackTrace(); //ca peut arriver en cas de mise a jour de d??pendances, mais ca devrait ??tre bon sinon
            }
        }
        if (textChannel != null)
        {
            EmbedBuilder eb = getVolumeEmbed(vol*5);
            Message ret = textChannel.sendMessageEmbeds(eb.build()).complete();
            ret.addReaction(Emoji.fromFormatted("\uD83D\uDD3C")).and(ret.addReaction(Emoji.fromFormatted("\uD83D\uDD3D"))).queue();
        }
        System.out.println(vol*5);
        return vol*5;
    }

    @Command(name="joue",type=ExecutorType.USER, description = "ajoute une musique a la playlist")
    private void joue(Guild guild, TextChannel textChannel, User user, Message msg)
    {
        play(guild, textChannel, user, msg, false);
    }

    @Command(name="play",type=ExecutorType.USER, description = "ajoute une musique a la playlist")
    private void play(Guild guild, TextChannel textChannel, User user, Message msg)
    {
        play(guild, textChannel, user, msg, false);
    }

    @Command(name = "playmerde", type = ExecutorType.USER, description = "ajoute une musique a la playlist")
    private void playmerde(Guild guild, TextChannel textChannel, User user, Message msg) {
        play(guild, textChannel, user,"https://www.youtube.com/playlist?list=PLXNtHjjUh7HwLdiCIwSrGiiTAv95GTBjy" , true);
    }

    @Command(name = "jouemerde", type = ExecutorType.USER, description = "ajoute une musique a la playlist")
    private void jouemerde(Guild guild, TextChannel textChannel, User user, Message msg) {
        play(guild, textChannel, user,"https://www.youtube.com/playlist?list=PLXNtHjjUh7HwLdiCIwSrGiiTAv95GTBjy" , true);
    }

    private void play(Guild guild, TextChannel textChannel, User user, Message msg, boolean random) {
        play(guild, textChannel, user, msg.getContentRaw().replaceFirst(CommandMap.getTag(),"").replaceFirst("play ", "").replaceFirst("random ", ""), random);
    }

    private void play(Guild guild, TextChannel textChannel, User user, String str, boolean random) {
        if (guild == null) return;
        if (!guild.getAudioManager().isConnected()) {
            AudioChannel voiceChannel = guild.getMember(user).getVoiceState().getChannel();
            if (voiceChannel == null) {
                textChannel.sendMessage("Vous devez ??tre connect?? ?? un salon vocal.").queue();
                return;
            }
            guild.getAudioManager().openAudioConnection(voiceChannel);
            volume(guild, null, 50);
        }
        manager.loadTrack(textChannel, str, random);
    }


    @Command(name="skip",type=ExecutorType.USER, description = "musique suivante")
    private void skip(Guild guild, TextChannel textChannel){
        if(!guild.getAudioManager().isConnected() && !guild.getAudioManager().isConnected()){
            textChannel.sendMessage("je dois d'abord etre connect??").queue();
            return;
        }
        manager.getPlayer(guild).skipTrack(textChannel);
    }

    @Command(name="rewind", type = ExecutorType.USER, description = "retourne au d??but")
    private void rewind(TextChannel tc)
    {
        if (manager.getPlayer(tc.getGuild()).getListener().getCurrent() == null)
        {
            tc.sendMessage("bah non tu peux pas rewind le vide").queue();
            return;
        }
        manager.getPlayer(tc.getGuild()).getListener().getCurrent().setPosition(0);
        tc.sendMessage("han han it's rewind time").queue();
    }

    @Command(name="queue",type = ExecutorType.USER,description = "montre la liste en cours")
    private void queue(TextChannel tc){
        MusicPlayer player = manager.getPlayer(tc.getGuild());
        EmbedBuilder eb = getQueueEmbed(player);
        if (player.getListener().getCurrent() != null)
            tc.sendMessage("actuellement : " + player.getListener().getCurrent().getInfo().title).setEmbeds(eb.build()).queue();
        else
            tc.sendMessageEmbeds(eb.build()).queue();
    }


    @Command(name="musicclear",type=ExecutorType.USER)
    private void clear(TextChannel textChannel){
        MusicPlayer player = manager.getPlayer(textChannel.getGuild());

        if(player.getListener().getTracks().isEmpty()){
            textChannel.sendMessage("Il n'y a pas de piste dans la liste d'attente.").queue();
            return;
        }

        player.getListener().clear();
        textChannel.sendMessage("La liste d'attente ?? ??t?? vid??.").queue();
    }
    @Command(name = "random", type = ExecutorType.USER, description = "m??lange les futurs musique")
    private void random(TextChannel textChannel, Message msg, Guild guild){
        if (msg.getContentRaw().split(" ").length > 1) {
            play(guild, textChannel, msg.getMember().getUser(), msg, true);
        }
        else if (manager.getPlayer(textChannel.getGuild()).getListener().getTracks().size() > 1) {
            manager.getPlayer(textChannel.getGuild()).getListener().randomise();
            textChannel.sendMessage("playlist m??lang??").queue();
        }
        else
            textChannel.sendMessage("pas assez de morceau pour m??langer").queue();
    }

    public static void stop(Guild guild)
    {
        manager.getPlayer(guild).getListener().stop();
    }
    @Command(name = "stop",type = ExecutorType.USER, description = "arrete la musique")
    private void stop(Message msg, Guild guild){
        manager.getPlayer(guild).getListener().stop();
    }
    @Command(name = "np", type = ExecutorType.USER, description = "passe une musique en priorit?? et met l'autre apr??s")
    private void np(Message msg, Guild guild)
    {
        nowplaying(msg,guild);
    }
    @Command(name = "nowplaying", type = ExecutorType.USER, description = "passe une musique en priorit?? et met l'autre apr??s")
    private void nowplaying(Message msg, Guild guild){
        manager.getPlayer(guild).getListener().nowplaying(msg.getTextChannel(), msg.getContentRaw().replaceFirst(CommandMap.getTag(),"").replaceFirst("nowplaying ", ""),manager);
    }

    @Command(name = "loop", type = ExecutorType.USER,description = "met en boucle la premi??re musique")
    private void loop(Message msg, Guild guild){
        manager.getPlayer(guild).getListener().nowLoop(msg.getTextChannel());
    }

    public static void MusicAddReact(MessageReactionAddEvent event) {
        if (event.getMember().getUser().isBot())
            return;
        Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        if (!msg.getEmbeds().isEmpty() && msg.getEmbeds().get(0).getFooter() != null && msg.getEmbeds().get(0).getFooter().getText().equals("MusicManager")) {
            if (!event.getGuild().getAudioManager().isConnected()) {
                msg.editMessageEmbeds(new EmbedBuilder().setDescription("not playing").setColor(Color.red).build()).complete().clearReactions().queue();
            }
            else if (msg.getEmbeds().get(0).getTitle().equals("Volume")) {
                if (event.getReaction().getEmoji().getAsReactionCode().equals("U+1f53c")) {
                    int vol = Integer.parseInt(msg.getEmbeds().get(0).getDescription().replace("%", "").split("\n")[0]);
                    vol = volume(msg.getGuild(), null, vol + 10);
                    msg.editMessageEmbeds(getVolumeEmbed(vol).build()).queue();
                } else if (event.getReaction().getEmoji().getAsReactionCode().equals("U+1f53d")) {
                    int vol = Integer.parseInt(msg.getEmbeds().get(0).getDescription().replace("%", "").split("\n")[0]);
                    vol = volume(msg.getGuild(), null, vol - 10);
                    msg.editMessageEmbeds(getVolumeEmbed(vol).build()).queue();
                }
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
