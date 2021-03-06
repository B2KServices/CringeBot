package fr.cringebot.cringe.escouades;

import fr.cringebot.cringe.waifus.InvWaifu;
import fr.cringebot.cringe.waifus.Waifu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.*;
import java.util.stream.Collectors;

public class SquadMember {

	private Long points;
	private final String id;
	private HashMap<String, Integer> collections;
	private HashMap<String, Integer> inventory;
	private HashMap<Integer, InvWaifu> waifus;
	private Long coins;
	private Integer bank;

	public SquadMember(String id) {
		this.id = id;
		this.points = 0L;
		this.collections = new HashMap<>();
		this.waifus = new HashMap<>();
		this.coins = 0L;
		this.bank = 0;
	}

	//---coins---//
	public void addBank(Integer points)
	{
		int lim = 1000;
		if (this.bank == null)
			bank = 0;
		this.bank = this.bank + points;
		if (Squads.getSortedSquads().get(0).equals(Squads.getSquadByMember(this.id)))
			lim = 833;
		if (this.bank > lim) {
			addCoins(1L);
			this.bank = this.bank - lim;
		}
	}

	public void addItem(String item){
		if (inventory == null)
			inventory = new HashMap<>();
		inventory.putIfAbsent(item, 0);
		inventory.put(item, inventory.getOrDefault(item, 0) + 1);
		Squads.save();
	}

	public Integer getAmountItem(String item)
	{
		if (inventory == null)
			inventory = new HashMap<>();
		inventory.putIfAbsent(item, 0);
		return inventory.getOrDefault(item, 0);
	}

	public Boolean removeItem(String item)
	{
		if (inventory == null)
			inventory = new HashMap<>();
		inventory.putIfAbsent(item, 0);
		if (inventory.getOrDefault(item, 0) > 0) {
			inventory.put(item, inventory.get(item) - 1);
			Squads.save();
			return true;
		}
		return false;
	}
	public void addCoins(Long coins) {
		if (this.coins == null)
			this.coins = 0L;
		this.coins = coins + this.coins;
		Squads.save();
	}

	public boolean removeCoins(Long coins) {
		if (this.coins == null)
			this.coins = 0L;
		if (this.coins - coins < 0)
			return false;
		this.coins = this.coins - coins;
		Squads.save();
		return true;
	}

	public Long getCoins() {
		if (coins == null)
			coins = 0L;
		return coins;
	}

	//---waifu---//

	public EmbedBuilder newWaifu(Integer id, String memId, Guild g) throws InterruptedException {
		EmbedBuilder eb = InvWaifu.catchWaifu(id, memId, g);
		if (Waifu.getWaifuById(id).isLegendary()) {
			Waifu.getWaifuById(id).setIstaken(true);
			Waifu.save();
		}
		this.addPoint(50L);
		this.waifus.put(id, new InvWaifu(id));
		Squads.save();
		return eb;
	}

	public void addInvWaifu(InvWaifu ivW)
	{
		this.waifus.put(ivW.getId(), ivW);
		Squads.save();
	}

	public InvWaifu popInvWaifu (Integer id)
	{
		InvWaifu ivW = this.waifus.get(id);
		if (ivW != null)
			this.waifus.remove(id);
		Squads.save();
		return ivW;
	}

	public void initCollections() {
		this.collections = new HashMap<>();
	}

	public void initwaifus() {
		this.waifus = new HashMap<>();
	}

	public boolean removeWaifu(Integer i)
	{
		if (this.waifus.get(i) != null) {
			this.waifus.remove(i);
			return true;
		}
		return false;
	}

	public ArrayList<Waifu> getWaifubyOrigin(String origin)
	{
		ArrayList<Waifu> ret = new ArrayList<>();
		for (InvWaifu iw : this.getWaifus().values())
			if (iw.getWaifu().getOrigin().equalsIgnoreCase(origin))
				ret.add(iw.getWaifu());
			return ret;
	}

	public EmbedBuilder getWaifu(String str, String memId, Guild g) throws InterruptedException {
		ArrayList<Waifu> waifus;
		if (str == null)
			waifus = Waifu.getAllWaifu();
		else
			waifus = Waifu.getWaifusByOrigin(str);
		waifus.removeIf(waifu -> this.waifus.containsKey(waifu.getId()) || (waifu.isLegendary() && !waifu.isIstaken()));
		return newWaifu(waifus.get(new Random().nextInt(waifus.size() - 1)).getId(), memId, g);
	}

	public void addCollection(String str, Message msg) throws InterruptedException {
		if (this.collections.getOrDefault(str, 0) == 2) {
			this.collections.put(str, 0);
			msg.getChannel().sendMessageEmbeds(this.getWaifu(str, msg.getMember().getId(), msg.getGuild()).build()).queue();
		}
		else
			this.collections.put(str, this.collections.getOrDefault(str, 0) + 1);
		Squads.save();
	}

	public Integer getCollection(String str)
	{
		return this.collections.getOrDefault(str, 0);
	}
	public HashMap<String, Integer> getCollection()
	{
		return this.collections;
	}

	public HashMap<Integer, InvWaifu> getWaifus() {
		return waifus;
	}

	public void setWaifus(HashMap<Integer, InvWaifu> iv) {
		waifus = iv;
	}


	//---squads---//
	public void addPoint(Long nb) {
		addBank(nb.intValue());
		this.points = this.points + nb;
	}

	public void resetPoint()
	{
		this.points = 0L;
	}
	public void removepoint(Long nb) {
		this.points = this.points - nb;
	}

	public Long getPoints() {
		return points;
	}

	public String getId() {
		return id;
	}
}
