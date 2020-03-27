package net.sf.l2j.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.data.sql.AuctionTable;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.AuctionItem;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class AuctionManager
extends Npc {
    public AuctionManager(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.startsWith("auction")) {
            try {
                String[] data = command.substring(8).split(" - ");
                int page = Integer.parseInt(data[0]);
                String search = data[1];
                this.showAuction(player, page, search);
            }
            catch (Exception e) {
                this.showChatWindow(player);
                player.sendMessage("Invalid input. Please try again.");
                return;
            }
        } else if (command.startsWith("buy")) {
            int auctionId = Integer.parseInt(command.substring(4));
            AuctionItem item = AuctionTable.getInstance().getItem(auctionId);
            if (item == null) {
                this.showChatWindow(player);
                player.sendMessage("Invalid choice. Please try again.");
                return;
            }
            if (player.getInventory().getItemByItemId(item.getCostId()) == null || player.getInventory().getItemByItemId(item.getCostId()).getCount() < item.getCostCount()) {
                this.showChatWindow(player);
                player.sendMessage("Incorrect item count.");
                return;
            }
            player.destroyItemByItemId("auction", item.getCostId(), item.getCostCount(), this, true);
            Player owner = World.getInstance().getPlayer(item.getOwnerId());
            if (owner != null && owner.isOnline()) {
                owner.addItem("auction", item.getCostId(), item.getCostCount(), null, true);
                owner.sendMessage("You have sold an item in the Auction Shop.");
            } else {
                AuctionManager.addItemToOffline(item.getOwnerId(), item.getCostId(), item.getCostCount());
            }
            ItemInstance i = player.addItem("auction", item.getItemId(), item.getCount(), this, true);
            i.setEnchantLevel(item.getEnchant());
            player.sendPacket(new InventoryUpdate());
            player.sendMessage("You have purchased an item from the Auction Shop.");
            AuctionTable.getInstance().deleteItem(item);
            this.showChatWindow(player);
        } else if (command.startsWith("addpanel")) {
            int page = Integer.parseInt(command.substring(9));
            this.showAddPanel(player, page);
        } else if (command.startsWith("additem")) {
            int itemId = Integer.parseInt(command.substring(8));
            if (player.getInventory().getItemByObjectId(itemId) == null) {
                this.showChatWindow(player);
                player.sendMessage("Invalid item. Please try again.");
                return;
            }
            this.showAddPanel2(player, itemId);
        } else if (command.startsWith("addit2")) {
            try {
                String[] data = command.substring(7).split(" ");
                int itemId = Integer.parseInt(data[0]);
                String costitemtype = data[1];
                int costCount = Integer.parseInt(data[2]);
                int itemAmount = Integer.parseInt(data[3]);
                if (player.getInventory().getItemByObjectId(itemId) == null) {
                    this.showChatWindow(player);
                    player.sendMessage("Invalid item. Please try again.");
                    return;
                }
                if (player.getInventory().getItemByObjectId(itemId).getCount() < itemAmount) {
                    this.showChatWindow(player);
                    player.sendMessage("Invalid item. Please try again.");
                    return;
                }
                if (!player.getInventory().getItemByObjectId(itemId).isTradable()) {
                    this.showChatWindow(player);
                    player.sendMessage("Invalid item. Please try again.");
                    return;
                }
                int costId = 0;
                if (costitemtype.equals("GoldFarm")) {
                    costId = 9302;
                }
                if (costitemtype.equals("TicktsShop")) {
                    costId = 9303;
                }
                if (costitemtype.equals("RaidBloody")) {
                    costId = 9304;
                }
                if (costitemtype.equals("Adena")) {
                    costId = 57;
                }
                AuctionTable.getInstance().addItem(new AuctionItem(AuctionTable.getInstance().getNextAuctionId(), player.getObjectId(), player.getInventory().getItemByObjectId(itemId).getItemId(), itemAmount, player.getInventory().getItemByObjectId(itemId).getEnchantLevel(), costId, costCount));
                player.destroyItem("auction", itemId, itemAmount, this, true);
                player.sendPacket(new InventoryUpdate());
                player.sendMessage("You have added an item for sale in the Auction Shop.");
                this.showChatWindow(player);
            }
            catch (Exception e) {
                this.showChatWindow(player);
                player.sendMessage("Invalid input. Please try again.");
                return;
            }
        } else if (command.startsWith("myitems")) {
            int page = Integer.parseInt(command.substring(8));
            this.showMyItems(player, page);
        } else if (command.startsWith("remove")) {
            int auctionId = Integer.parseInt(command.substring(7));
            AuctionItem item = AuctionTable.getInstance().getItem(auctionId);
            if (item == null) {
                this.showChatWindow(player);
                player.sendMessage("Invalid choice. Please try again.");
                return;
            }
            AuctionTable.getInstance().deleteItem(item);
            ItemInstance i = player.addItem("auction", item.getItemId(), item.getCount(), this, true);
            i.setEnchantLevel(item.getEnchant());
            player.sendPacket(new InventoryUpdate());
            player.sendMessage("You have removed an item from the Auction Shop.");
            this.showChatWindow(player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private void showMyItems(Player player, int page) {
    	Map<Integer, List<AuctionItem>> items = new HashMap<>();
        int curr = 1;
        int counter = 0;
        ArrayList<AuctionItem> temp = new ArrayList<>();
        for (AuctionItem item : AuctionTable.getInstance().getItems()) {
            if (item.getOwnerId() != player.getObjectId()) continue;
            temp.add(item);
            if (++counter != 10) continue;
            items.put(curr, temp);
            temp = new ArrayList<>();
            ++curr;
            counter = 0;
        }
        items.put(curr, temp);
        if (!items.containsKey(page)) {
            this.showChatWindow(player);
            player.sendMessage("Invalid page. Please try again.");
            return;
        }
        String html = "";
        html = html + "<html><title>Auction Shop</title><body><center><br1>";
        html = html + "<table width=255 bgcolor=000000 border=1>";
        html = html + "<tr><td>Item</td><td>Cost</td><td></td></tr>";
        for (AuctionItem item : items.get(page))

        {
            html = html + "<tr>";
            ItemData.getInstance();
            html = html + "<td><img src=\"" + ItemData.getInstance().getTemplate(item.getItemId()).getIcon() + "\" width=32 height=32 align=center></td>";
            html = html + "<td>Item: " + (item.getEnchant() > 0 ? "+" + item.getEnchant() + " " + ItemData.getInstance().getTemplate(item.getItemId()).getName() + " - " + item.getCount() : ItemData.getInstance().getTemplate(item.getItemId()).getName() + " - " + item.getCount());
            html = html + "<br1>Cost: " + item.getCostCount() + " " + ItemData.getInstance().getTemplate(item.getCostId()).getName();
            html = html + "</td>";
            html = html + "<td fixwidth=71><button value=\"Remove\" action=\"bypass -h npc_" + this.getObjectId() + "_remove " + item.getAuctionId() + "\" width=70 height=21 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\">";
            html = html + "</td></tr>";
        }
        html = html + "</table><br><br>";
        html = html + "Page: " + page;
        html = html + "<br1>";
        if (items.keySet().size() > 1) {
            if (page > 1) {
                html = html + "<a action=\"bypass -h npc_" + this.getObjectId() + "_myitems " + (page - 1) + "\"><- Prev</a>";
            }
            if (items.keySet().size() > page) {
                html = html + "<a action=\"bypass -h npc_" + this.getObjectId() + "_myitems " + (page + 1) + "\">Next -></a>";
            }
        }
        html = html + "</center></body></html>";
        NpcHtmlMessage htm = new NpcHtmlMessage(this.getObjectId());
        htm.setHtml(html);
        player.sendPacket(htm);
    }

    private void showAddPanel2(Player player, int itemId) {
        ItemInstance item = player.getInventory().getItemByObjectId(itemId);
        String html = "";
        html = html + "<html><title>Auction Shop</title><body><center><br1>";
        ItemData.getInstance();
        html = html + "<td><img src=\"" + ItemData.getInstance().getTemplate(item.getItemId()).getIcon() + "\" width=32 height=32 align=center></td>";
        html = html + "Item: " + (item.getEnchantLevel() > 0 ? "+" + item.getEnchantLevel() + " " + item.getName() : item.getName());
        if (item.isStackable()) {
            html = html + "<br>Set amount of items to sell:";
            html = html + "<edit var=amm type=number width=120 height=17>";
        }
        html = html + "<br>Select price:";
        html = html + "<br><combobox width=120 height=17 var=ebox list=TicktsShop;RaidBloody;GoldFarm;Adena;>";
        html = html + "<br><edit var=count type=number width=120 height=17>";
        html = html + "<br><button value=\"Add item\" action=\"bypass -h npc_" + this.getObjectId() + "_addit2 " + itemId + " $ebox $count " + (item.isStackable() ? "$amm" : "1") + "\" width=70 height=21 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\">";
        html = html + "</center></body></html>";
        NpcHtmlMessage htm = new NpcHtmlMessage(this.getObjectId());
        htm.setHtml(html);
        player.sendPacket(htm);
    }

    private void showAddPanel(Player player, int page) {
    	Map<Integer, List<ItemInstance>> items = new HashMap<>();
        int curr = 1;
        int counter = 0;
        ArrayList<ItemInstance> temp = new ArrayList<>();
        for (ItemInstance item : player.getInventory().getItems()) {
            if (item.getItemId() == 57 || !item.isTradable()) continue;
            temp.add(item);
            if (++counter != 10) continue;
            items.put(curr, temp);
            temp = new ArrayList<>();
            ++curr;
            counter = 0;
        }
        items.put(curr, temp);
        if (!items.containsKey(page)) {
            this.showChatWindow(player);
            player.sendMessage("Invalid page. Please try again.");
            return;
        }
        String html = "";
        html = html + "<html><title>Auction Shop</title><body><center><br1>";
        html = html + "Select item:";
        html = html + "<br><table width=255 bgcolor=000000 border=1>";
        for (ItemInstance item : items.get(page)) {
            html = html + "<tr>";
            html = html + "<td>";
            ItemData.getInstance();
            html = html + "<td><img src=\"" + ItemData.getInstance().getTemplate(item.getItemId()).getIcon() + "\" width=32 height=32 align=center></td>";
            html = html + "<td>" + (item.getEnchantLevel() > 0 ? "+" + item.getEnchantLevel() + " " + item.getName() : item.getName());
            html = html + "</td>";
            html = html + "<td><button value=\"Select\" action=\"bypass -h npc_" + this.getObjectId() + "_additem " + item.getObjectId() + "\" width=70 height=21 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\">";
            html = html + "</td>";
            html = html + "</tr>";
        }
        html = html + "</table><br><br>";
        html = html + "Page: " + page;
        html = html + "<br1>";
        if (items.keySet().size() > 1) {
            if (page > 1) {
                html = html + "<a action=\"bypass -h npc_" + this.getObjectId() + "_addpanel " + (page - 1) + "\"><- Prev</a>";
            }
            if (items.keySet().size() > page) {
                html = html + "<a action=\"bypass -h npc_" + this.getObjectId() + "_addpanel " + (page + 1) + "\">Next -></a>";
            }
        }
        html = html + "</center></body></html>";
        NpcHtmlMessage htm = new NpcHtmlMessage(this.getObjectId());
        htm.setHtml(html);
        player.sendPacket(htm);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SuppressWarnings("resource")
	private static void addItemToOffline(int playerId, int itemId, int count) {
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement("SELECT count FROM items WHERE owner_id=? AND item_id=?");
            stm.setInt(1, playerId);
            stm.setInt(2, itemId);
            ResultSet rset = stm.executeQuery();
            if (rset.next()) {
                stm = con.prepareStatement("UPDATE items SET count=? WHERE owner_id=? AND item_id=?");
                stm.setInt(1, rset.getInt("count") + count);
                stm.setInt(2, playerId);
                stm.setInt(3, itemId);
                stm.execute();
            } else {
                stm = con.prepareStatement("INSERT INTO items VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
                stm.setInt(1, playerId);
                stm.setInt(2, IdFactory.getInstance().getNextId());
                stm.setInt(3, itemId);
                stm.setInt(4, count);
                stm.setInt(5, 0);
                stm.setString(6, "INVENTORY");
                stm.setInt(7, 0);
                stm.setInt(8, 0);
                stm.setInt(9, 0);
                stm.setInt(10, 0);
                stm.setInt(11, -1);
                stm.setInt(12, 0);
                stm.execute();
            }
            rset.close();
            stm.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAuction(Player player, int page, String search) {
        boolean src = !search.equals("*null*");
        HashMap<Integer, ArrayList<AuctionItem>> items = new HashMap<>();
        int curr = 1;
        int counter = 0;
        ArrayList<AuctionItem> temp = new ArrayList<>();
        for (AuctionItem item : AuctionTable.getInstance().getItems()) {
            if (item.getOwnerId() == player.getObjectId() || src && (!src || !ItemData.getInstance().getTemplate(item.getItemId()).getName().contains(search))) continue;
            temp.add(item);
            if (++counter != 10) continue;
            items.put(curr, temp);
            temp = new ArrayList<>();
            ++curr;
            counter = 0;
        }
        items.put(curr, temp);
        if (!items.containsKey(page)) {
            this.showChatWindow(player);
            player.sendMessage("Invalid page. Please try again.");
            return;
        }
        String html = "<html><title>Auction Shop</title><body><center><br1>";
        html = html + "<multiedit var=srch width=150 height=20><br1>";
        html = html + "<button value=\"Search\" action=\"bypass -h npc_" + this.getObjectId() + "_auction 1 - $srch\" width=70 height=21 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\">";
        html = html + "<br><table width=255 bgcolor=000000 border=1>";
        html = html + "<tr><td>Item</td><td>Cost</td><td></td></tr>";
        for (AuctionItem item : items.get(page)) {
            html = html + "<tr>";
            ItemData.getInstance();
            html = html + "<td><img src=\"" + ItemData.getInstance().getTemplate(item.getItemId()).getIcon() + "\" width=32 height=32 align=center></td>";
            html = html + "<td>Item: " + (item.getEnchant() > 0 ? "+" + item.getEnchant() + " " + ItemData.getInstance().getTemplate(item.getItemId()).getName() + " - " + item.getCount() : ItemData.getInstance().getTemplate(item.getItemId()).getName() + " - " + item.getCount());
            html = html + "<br1>Cost: " + item.getCostCount() + " " + ItemData.getInstance().getTemplate(item.getCostId()).getName();
            html = html + "</td>";
            html = html + "<td fixwidth=71><button value=\"Buy\" action=\"bypass -h npc_" + this.getObjectId() + "_buy " + item.getAuctionId() + "\" width=70 height=21 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\">";
            html = html + "</td></tr>";
        }
        html = html + "</table><br><br>";
        html = html + "Page: " + page;
        html = html + "<br1>";
        if (items.keySet().size() > 1) {
            if (page > 1) {
                html = html + "<a action=\"bypass -h npc_" + this.getObjectId() + "_auction " + (page - 1) + " - " + search + "\"><- Prev</a>";
            }
            if (items.keySet().size() > page) {
                html = html + "<a action=\"bypass -h npc_" + this.getObjectId() + "_auction " + (page + 1) + " - " + search + "\">Next -></a>";
            }
        }
        html = html + "</center></body></html>";
        NpcHtmlMessage htm = new NpcHtmlMessage(this.getObjectId());
        htm.setHtml(html);
        player.sendPacket(htm);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String pom = "";
        pom = val == 0 ? "" + npcId : npcId + "-" + val;
        return "data/html/mods/shop/" + pom + ".htm";
    }
}

