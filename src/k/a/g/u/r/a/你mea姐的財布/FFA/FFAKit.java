package k.a.g.u.r.a.Äãmea½ãµÄØ”²¼.FFA;

import c.aqua.neeeeee.utils.Config.ConfigCursor;
import c.aqua.neeeeee.utils.InventoryUtil;
import k.a.g.u.r.a.Tekoki;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Setter
@Getter
public class FFAKit {

    @Getter private static Map<FFAKitType, FFAKit> ffaKits = new HashMap<>();

    private String kitName;

    private ItemStack[] armor;
    private ItemStack[] inventory;
    private Collection<PotionEffect> effects;

    private FFAKitType type;

    public FFAKit(String name, FFAKitType kitType) {
        this.kitName = name;
        this.type = kitType;
        this.armor = new ItemStack[4];
        this.inventory = new ItemStack[36];
        this.effects = new ArrayList<>();
    }

    public void applyKit(Player player) {
        player.getInventory().setContents(this.inventory);
        player.getInventory().setArmorContents(this.armor);
        this.effects.forEach(effect ->{
            player.addPotionEffect(effect);
        });
        player.updateInventory();
    }

    public void save() {
        ConfigCursor cursor = new ConfigCursor(Tekoki.tekoki().getFFaConfig(), "kits." + this.kitName);
        cursor.set("armor", InventoryUtil.serializeInventory(armor));
        cursor.set("inv", InventoryUtil.serializeInventory(inventory));
        if (!this.effects.isEmpty()) {
            cursor.set("effects", InventoryUtil.serizlizeEffect(effects));
        }
        cursor.save();
    }
}
