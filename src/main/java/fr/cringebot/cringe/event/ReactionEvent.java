/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   ReactionEvent.java                                 :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:00:42 by gchatain          #+#    #+#             */
/*   Updated: 2022/07/08 10:52:14 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.event;

import fr.cringebot.cringe.objects.DetectorAttachment;
import fr.cringebot.cringe.objects.MessageConsumer;
import fr.cringebot.cringe.objects.StringExtenders;
import fr.cringebot.cringe.objects.imgExtenders;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static fr.cringebot.cringe.objects.StringExtenders.*;
import static fr.cringebot.cringe.objects.imgExtenders.getImage;
import static fr.cringebot.cringe.objects.imgExtenders.resize;

public class ReactionEvent {

    public static void reactionevent(Message msg, JDA jda) throws IOException {
        if (msg.getChannel().getId().equals("461606547064356864"))
            return;

        if (msg.getContentRaw().split(" ")[0].equalsIgnoreCase("f")) {
            pressf(msg);
        }

        if (StringExtenders.containsWord(msg.getContentRaw(), "mdr"))
        {
            String[] rep = {
                    "Hilarant...",
                    "je suis actuellement hilare",
                    "#jpp",
                    "\uD83D\uDE02"
            };
            int r = new Random().nextInt(rep.length);
            msg.getChannel().sendMessage(rep[r]).queue();
        }

        if (StringExtenders.containsWord(msg.getContentRaw(),"lyon"))
        {
            msg.getChannel().sendMessage("les portes s'ouvriront ?? droite dans le sens de la circulation").queue();
        }
        if (containsIgnoreCase(msg.getContentRaw(), "je suis"))
        {
            int i = firstsearch(msg.getContentRaw().toLowerCase(Locale.ROOT), "je suis");
            String str = msg.getContentRaw().substring(i + 2);
            if (str.split(" ").length == 1) {
                str = str.replace("@everyone", "tout le monde");
                if (new Random().nextInt(2) == 0 && msg.getGuild().getMember(jda.getSelfUser()) != null)
                    msg.getChannel().sendMessage("Bonjour " + str + ". Moi c'est " + msg.getGuild().getMember(jda.getSelfUser()).getEffectiveName()).queue();
                else
                    msg.getChannel().sendMessage("Bonjour " + str + ". Moi c'est " + jda.getSelfUser().getName()).queue();
            }
        }

        if (msg.getContentRaw().equalsIgnoreCase("ratio") )
        {
            if (msg.getReferencedMessage() != null) {
                msg.getChannel().sendMessage("Ratio").reference(msg.getReferencedMessage()).complete().addReaction(Emoji.fromFormatted("??????")).queue();
                msg.delete().queue();
            }
        }

        if (StringExtenders.containsWord(msg.getContentRaw(), "sus"))
            sus(msg);

        if (msg.getContentRaw().equalsIgnoreCase("ping"))
        {
            long time = System.currentTimeMillis();
            msg.getChannel().sendMessage("pong").complete().editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
        }

        String s = msg.getContentRaw().replace("?","").replace(".","").replace(" ","");
        if ( s.substring(Math.max(0, s.length() - 4)).equalsIgnoreCase("quoi")){
            feur(msg);
        }

        if (containsIgnoreCase(msg.getContentRaw().replace('??', 'e'), "societer"))
            msg.getChannel().sendFile(imgExtenders.getFile("societer.png")).queue();
        if (containsIgnoreCase(msg.getContentRaw(), "putain")) {
            putain(msg);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "hmm")){
            for (String split : msg.getContentRaw().split(" "))
                if (StringExtenders.startWithIgnoreCase(split,"hmm"))
                    hmm(msg, split);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "je lag"))
        {
            File f = imgExtenders.getFile("internet.png");
            msg.getChannel().sendFile(f).queue();
            f.delete();
        }

        if (containsIgnoreCase(msg.getContentRaw(), "cringe")) {
            if (!DetectorAttachment.isAnyLink(msg))
                msg.getTextChannel().sendMessage("https://tenor.com/view/oh-no-cringe-cringe-oh-no-kimo-kimmo-gif-23168319").queue();
        }

        if (containsIgnoreCase(msg.getContentRaw(), "stonks")) {
            if (new Random().nextInt(100) >= 95)
            {
                BufferedImage bi = imgExtenders.getImage("not stonks.png");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "gif", baos);
                msg.getChannel().sendFile(baos.toByteArray(), "not stonks.png").queue();
                return;
            }
            File f = imgExtenders.getFile("stonks.gif");
            msg.getChannel().sendFile(f).queue();
        }

        if (startWithIgnoreCase(msg.getContentRaw(), "daronned") || startWithIgnoreCase(msg.getContentRaw(), "daronn??"))
            daronned(msg);

        if (msg.getContentRaw().equalsIgnoreCase("saint pierre"))
        {
            int r = new Random().nextInt(2);
            if (r == 1)
                msg.getChannel().sendMessage("cret-en-belledonne, nuance.\nsi ??a se trouve les habitants sont les cr??tins").queue();
            if (r == 0)
                msg.getChannel().sendMessage("village pr??f??r?? de mon conseill?? principal, il a souvent des id??es de merde et des blagues beauf\nmais bon on l'aime quand meme").queue();
        }
        if (msg.getContentRaw().equalsIgnoreCase("allevard"))
        {
            int r = new Random().nextInt(2);
            if (r == 1)
                msg.getChannel().sendMessage("village de dealers").queue();
            if (r == 0)
                msg.getChannel().sendMessage("village natal de mon cr??ateur, il est un peu tete en l'air mais ??a va").queue();
        }
        if (containsIgnoreCase(msg.getContentRaw(), "michel"))
            msg.getChannel().sendMessage( msg.getGuild().getMemberById("282859044593598464").getAsMention()+ ", eh oh je crois qu'on parle de toi").queue();
        if (containsIgnoreCase(msg.getContentRaw(), "shadow hunter"))
            msg.getChannel().sendMessage("*shadow **in**ter*").reference(msg).queue();
        if (msg.getContentRaw().equalsIgnoreCase("creeper"))
            msg.getChannel().sendMessage("Aww man").queue();
        if (msg.getContentRaw().equalsIgnoreCase("i am a dwarf"))
            msg.getChannel().sendMessage("and I'm digging a hole").queue();
        if (msg.getContentRaw().equalsIgnoreCase("je suis un nain"))
            msg.getChannel().sendMessage("et je creuse un gros trou").queue();
        if (msg.getContentRaw().equalsIgnoreCase("mov?? salon"))
        {
            if (msg.getMember().getId().equals("280959408119349248"))
                msg.getChannel().sendMessage("Franchement si j'??tais vous **je me tairais**").queue();
            else
                msg.getChannel().sendMessage("eh oh tu t'es pris pour Logan ou quoi ? \n**MDR**").queue();
        }

        if (containsIgnoreCase(msg.getContentRaw(), "je possede des thunes") || containsIgnoreCase(msg.getContentRaw(), "je poss??de des thunes"))
            msg.getChannel().sendMessage(msg.getMember().getAsMention() + " est ?? l'aise financi??rement").queue();
    }


    public static void sus(Message msg) {
        if (new Random().nextInt(100) < 95)
        {
            msg.getChannel().sendMessage(". ?????????*   ????????????????????? ??????????????? ??????. ??  ,  *?????????.?????????* ??????.???????????????????????? ???\n" +
                    "\n" +
                    "?????????.?????????* ??????.???????????????????????? ??????. ???.  ??  ,  *   ????????????????????? ??????????????? ??????. \n" +
                    "\n" +
                    ".?????? ?????????????????? ??? ??? . ?????? ??? ??????   ???????????   ??  .?????????.?????????* ??????.???????????????????????? ??? \n" +
                    "\n" +
                    "??????????????? " + msg.getMember().getAsMention() + " was not An Impostor.??? ??????.?????????* ??????.???????????????????????? ???\n" +
                    "\n" +
                    "??????'????????? 1 Impostor remains ??? ?????????   . ??  , ?????????.?????????* ??????.???????????????????????? ???\n" +
                    "\n" +
                    "??????????????????.?????????. ,????????????.??? .   *  ??  . ??  ?????????.?????????* ??????.???????????????????????? ???").queue();
        }
        else
            msg.getChannel().sendMessage(". ?????????*   ????????????????????? ??????????????? ??????. ??  ,  *?????????.?????????* ??????.???????????????????????? ???\n" +
                    "\n" +
                    "?????????.?????????* ??????.???????????????????????? ??????. ???.  ??  ,  *   ????????????????????? ??????????????? ??????. \n" +
                    "\n" +
                    ".?????? ?????????????????? ??? ??? . ?????? ??? ??????   ???????????   ??  .?????????.?????????* ??????.???????????????????????? ??? \n" +
                    "\n" +
                    "??????????????? " + msg.getMember().getAsMention() + " was the Impostor.??? ??????.?????????* ??????.???????????????????????? ???\n" +
                    "\n" +
                    "??????'????????? ?????????* ??????.???????????????????????? ??? ??? ?????????   . ??  , ?????????.?????????* ??????.???????????????????????? ???\n" +
                    "\n" +
                    "??????????????????.?????????. ,????????????.??? .   *  ??  . ??  ?????????.?????????* ??????.???????????????????????? ???").queue();
    }

    /**
     * quelqu'un veut rendre respect ? press f to respect
     *
     * @param msg
     */
    public static void pressf(Message msg) {
        String[] args = msg.getContentRaw().split(" ");
        if (msg.getContentRaw().length() < 60) {
            if (args.length != 1) {
                if ((msg.getContentRaw().contains("@here") || msg.getContentRaw().contains("@everyone"))) {
                    msg.getChannel().sendMessage("tu as rendu respect a tout le monde").queue();
                    return;
                }
                List<Member> mbrs = msg.getGuild().getMembers();
                for (Member m : mbrs) {
                    if (args[1].equalsIgnoreCase(m.getUser().getName())) {
                        msg.getChannel().sendMessage("tu as rendu respect a " + m.getAsMention()).queue();
                        return;
                    }
                }
                if (msg.getMentions().getMembers().size() >= 1) {
                    msg.getChannel().sendMessage("tu as rendu respect ?? " + msg.getMentions().getMembers().get(0).getAsMention()).queue();
                } else {
                    msg.getChannel().sendMessage("tu as rendu respect ?? " + msg.getContentDisplay().substring(2)).queue();
                }
            } else {
                msg.getChannel().sendMessage("tu lui as rendu respect").queue();
            }
        } else {
            msg.getChannel().sendMessage("trop long, donc je te respecterai pas").queue();
        }
    }

    /**
     * on vous demande d'??viter de crier
     * @param msg
     */
    public static void rage(Message msg) {
        if (msg.getAuthor().getId().equals("358659144330248193")) {
            msg.getTextChannel().sendMessage("aaaaah on le reconnait bien").reference(msg).queue();
        } else {
            msg.getTextChannel().sendMessage("oula on va se calmer tu vas d??troner le ROI DU SEL").reference(msg).queue();
        }
    }

    /**
     * nice
     * r??cup??re les donn??es sur un cite
     * @param msg
     * @throws IOException
     */


    /**
     * on ??vite de casser une table ?? chaque putain bordel
     * @param msg
     */
    public static void putain(Message msg){
        msg.getChannel().sendMessage("????????????( ?? _ ?????)").queue(new MessageConsumer("(??-??)\\ ?????????", 100, new MessageConsumer("(??????????)???    ]", 100, new MessageConsumer("(??????????)???  ???  ?????????", 100, null))));
    }

    /**
     * va te coiffer, tu me fais peur
     * @param msg
     */
    public static void feur(Message msg){
        msg.getTextChannel().sendMessage("feur").queue();
    }

    /**
     * hmmm d??pend du nombre de m
     * @param msg
     * @param rec
     */
    public static void hmm(Message msg,String rec){
        int i = 1;
        while ( i < rec.length() && rec.charAt(i) == 'm')
            i++;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage im = getImage("hmmm.png");
            if (DetectorAttachment.percent(2)) {
                msg.getChannel().sendMessage("https://cdn.discordapp.com/emojis/439511275437948938.gif?size=4096").queue();
                return;
            }
            int w = im.getWidth() + i*2;
            int h = im.getHeight() + i*2;
            im = resize(im, w, h, 0, 0, true);
            im = resize(im, w, h, 0, 0, false);
            ImageIO.write(im, "png", baos);
            msg.getTextChannel().sendFile(baos.toByteArray(), "thunk.png").queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void daronned(Message msg)
    {
        String[] args = msg.getContentRaw().split(" ");
        if (args.length != 1) {
            if ((msg.getContentRaw().contains("@here") || msg.getContentRaw().contains("@everyone"))) {
                msg.getChannel().sendMessage("tout le monde se fait daronn??").queue();
                return;
            }
            List<Member> mbrs = msg.getGuild().getMembers();
            for (Member m : mbrs) {
                if (args[1].equalsIgnoreCase(m.getUser().getName())) {
                    msg.getChannel().sendMessage(m.getAsMention() + "s'est fait darron??").queue();
                    return;
                }
            }
            if (msg.getMentions().getMembers().size() >= 1)
                msg.getChannel().sendMessage(msg.getMentions().getMembers().get(0).getAsMention() + "s'est fait daronn??").queue();
    }
    else
        msg.getChannel().sendMessage("il s'est fait daronn??").queue();
}
}


