package net.sf.l2j.gameserver.model.actor.instance;

import java.util.List;

import net.sf.l2j.gameserver.data.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.data.xml.SkillTreeData;
import net.sf.l2j.gameserver.enums.skills.AcquireSkillType;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.holder.skillnode.EnchantSkillNode;
import net.sf.l2j.gameserver.model.holder.skillnode.GeneralSkillNode;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.AcquireSkillList;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExEnchantSkillList;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.effects.EffectBuff;
import net.sf.l2j.gameserver.skills.effects.EffectDebuff;

public class Folk extends Npc
{
	public Folk(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setIsMortal(false);
	}
	
	@Override
	public void addEffect(L2Effect newEffect)
	{
		if (newEffect instanceof EffectDebuff || newEffect instanceof EffectBuff)
			super.addEffect(newEffect);
		else if (newEffect != null)
			newEffect.stopEffectTask();
	}
	
	/**
	 * This method displays SkillList to the player.
	 * @param player The player who requested the method.
	 */
	public void showSkillList(Player player)
	{
		if (!getTemplate().canTeach(player.getClassId()))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/trainer/" + getTemplate().getNpcId() + "-noskills.htm");
			player.sendPacket(html);
			return;
		}
		
		final List<GeneralSkillNode> skills = player.getAvailableSkills();
		if (skills.isEmpty())
		{
			final int minlevel = player.getRequiredLevelForNextSkill();
			if (minlevel > 0)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(minlevel));
			else
				player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(new AcquireSkillList(AcquireSkillType.USUAL, skills));
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * This method displays EnchantSkillList to the player.
	 * @param player The player who requested the method.
	 */
	public void showEnchantSkillList(Player player)
	{
		if (!getTemplate().canTeach(player.getClassId()))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/trainer/" + getTemplate().getNpcId() + "-noskills.htm");
			player.sendPacket(html);
			return;
		}
		
		if (player.getClassId().level() < 3)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml("<html><body> You must have 3rd class change quest completed.</body></html>");
			player.sendPacket(html);
			return;
		}
		
		final List<EnchantSkillNode> skills = SkillTreeData.getInstance().getEnchantSkillsFor(player);
		if (skills.isEmpty())
		{
			player.sendPacket(SystemMessageId.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT);
			
			if (player.getLevel() < 74)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(74));
			else
				player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(new ExEnchantSkillList(skills));
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void giveBlessingSupport(Player player)
	{
		if (player == null)
			return;
		
		// Select the player
		setTarget(player);
		
		// If the player is too high level, display a message and return
		if (player.getLevel() > 39 || player.getClassId().level() >= 2)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml("<html><body>Newbie Guide:<br>I'm sorry, but you are not eligible to receive the protection blessing.<br1>It can only be bestowed on <font color=\"LEVEL\">characters below level 39 who have not made a seccond transfer.</font></body></html>");
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			return;
		}
		doCast(FrequentSkill.BLESSING_OF_PROTECTION.getSkill());
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("SkillList"))
			showSkillList(player);
		else if (command.startsWith("EnchantSkillList"))
			showEnchantSkillList(player);
		else if (command.startsWith("GiveBlessing"))
			giveBlessingSupport(player);
		else
			super.onBypassFeedback(player, command);
	}
}