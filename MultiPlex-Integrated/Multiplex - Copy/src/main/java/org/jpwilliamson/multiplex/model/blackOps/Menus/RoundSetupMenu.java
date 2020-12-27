package org.jpwilliamson.multiplex.model.blackOps.Menus;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jpwilliamson.multiplex.model.ArenaPlayer;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.jpwilliamson.multiplex.model.blackOps.MonsterRound;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.conversation.SimplePrompt;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.ButtonRemove;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RoundSetupMenu extends MenuPagged<MonsterRound> {

	private final ArenaPlayer cache;

	@Nullable
	private final BlackOpsArena arena;

	private final Button createButton;

	private RoundSetupMenu(final Menu parent, final Player player, final BlackOpsArena arena) {
		super(parent, compileRounds(player, arena));

		this.cache = ArenaPlayer.getCache(player);
		this.arena = arena;

		setTitle("Setup Game Rounds");

		if(isEmpty())
			setSize(9*3);

		this.createButton = new ButtonConversation(new CreateRoundNamePrompt(), ItemCreator.of(CompMaterial.BOW,
				"Create a Round",
				"",
				"Click to add a",
				"new round"));
	}

	private static List<MonsterRound> compileRounds(final Player player, final BlackOpsArena arena) {

		return new ArrayList<>(MonsterRound.getRounds());
	}

	@Override
	public ItemStack getItemAt(int slot) {
		if ((slot == getSize() - 1 && getParent() == null) || (getParent() != null && slot == getSize() - 2))
			return createButton.getItem();

		return super.getItemAt(slot);
	}

	@Override
	protected ItemStack convertToItemStack(final MonsterRound monsterRound) {

		final List<String> lore = new ArrayList<>();
		lore.add("Monster Round");
		return ItemCreator
				.of(monsterRound.getIcon())
				.name(monsterRound.getName() + " Round")
				.lores(lore)
				.build().makeMenuTool();
	}

	@Override
	protected void onPageClick(Player player, MonsterRound monsterRound, ClickType clickType) {
		new RoundMenu(monsterRound);
	}

	public static void openEditMenu(@Nullable final Menu parent, final Player player) {
		new RoundSetupMenu(parent, player, null).displayTo(player);
	}

	@Override
	public Menu newInstance() {
		return new RoundSetupMenu(getParent(), getViewer(), arena);
	}

	private final class CreateRoundNamePrompt extends SimplePrompt {

		private CreateRoundNamePrompt() {
			super(true);
		}

		@Override
		protected String getPrompt(ConversationContext conversationContext) {
			return "&7Enter the Round Name.";
		}

		@Override
		protected boolean isInputValid(ConversationContext context, String input) {
			return !MonsterRound.isRoundLoaded(input);
		}

		@Override
		protected String getFailedValidationText(ConversationContext context, String invalidInput) {
			return "Round named " + invalidInput + " is already loaded.";
		}

		@Override
		protected @org.jetbrains.annotations.Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
			MonsterRound.loadOrCreateRound(s);

			return Prompt.END_OF_CONVERSATION;
		}
	}

	private final class RoundMenu extends Menu {

		private final MonsterRound monsterRound;

		private final Button iconButton;

		private final Button removeButton;

		private final Button nameButton;

		private final Button timeButton;

		private final Button waveButton;

		private final Button monsterButton;

		private final Button spawnTimeButton;

		private final Button positionButton;

		private RoundMenu(final MonsterRound monsterRound) {
			super(RoundSetupMenu.this);

			this.monsterRound = monsterRound;

			setTitle("Round " + monsterRound.getName());
			setSize(9 * 4);

			this.iconButton = new ButtonMenu(new IconMenu(), ItemCreator.of(
					CompMaterial.BEACON,
					"Edit class icon"));

			this.removeButton = new ButtonRemove(this, "round ", monsterRound.getName(), className -> MonsterRound.removeRound(monsterRound));

			this.waveButton = new ButtonConversation(new WavePrompt(), ItemCreator.of(CompMaterial.BONE, "Wave", "", "give number of waves"));

			this.nameButton = new ButtonConversation(new NamePrompt(), ItemCreator.of(CompMaterial.POTION,"Name","Name the round"));

			this.timeButton = new ButtonConversation(new timePrompt(),ItemCreator.of(CompMaterial.COMPASS,"Time","set wait time"));

			this.monsterButton = new ButtonMenu(new MonsterSelectMenu(this,monsterRound),ItemCreator.of(CompMaterial.CREEPER_HEAD,"Monsters","Set Monsters for the round"));

			this.spawnTimeButton = new ButtonConversation(new spawnTimePrompt(), ItemCreator.of(CompMaterial.CLOCK,"Spawn Time","Set the time delay for waves"));

			this.positionButton = new ButtonConversation(new positionPrompt(),ItemCreator.of(CompMaterial.BAMBOO,"Position","Position for this round"));

			displayTo(RoundSetupMenu.this.getViewer());
		}

		@Override
		public ItemStack getItemAt(final int slot) {

			if (slot == 9 + 1)
				return waveButton.getItem();

			if (slot == 9 + 3)
				return iconButton.getItem();

			if (slot == 9 + 5)
				return nameButton.getItem();

			if (slot == 9 + 7)
				return timeButton.getItem();

			if(slot == 9*2 + 2)
				return monsterButton.getItem();

			if(slot == 9*2 + 4)
				return spawnTimeButton.getItem();

			if(slot == 9*2 + 6)
				return positionButton.getItem();

			if (slot == getSize() - 5)
				return removeButton.getItem();

			return null;
		}

		/**
		 * @see org.mineacademy.fo.menu.Menu#newInstance()
		 */
		@Override
		public Menu newInstance() {
			return new RoundMenu(monsterRound);
		}

		/**
		 * @see org.mineacademy.fo.menu.Menu#getInfo()
		 */
		@Override
		protected String[] getInfo() {
			return new String[]{
					"Edit your Rounds",
					"settings here."
			};
		}

		/**
		 * The edit class icon menu
		 */
		private final class IconMenu extends Menu {

			/**
			 * Create a new menu
			 */
			private IconMenu() {
				super(RoundMenu.this);

				setSize(9 * 3);
				setTitle("Select class icon");
			}

			@Override
			public ItemStack getItemAt(final int slot) {

				if (slot == getCenterSlot())
					return monsterRound.getIcon();

				return ItemCreator.of(CompMaterial.GRAY_STAINED_GLASS_PANE).name(" ").build().make();
			}

			@Override
			protected void onMenuClose(final Player player, final Inventory inventory) {
				final ItemStack item = inventory.getItem(getCenterSlot());

				monsterRound.setIcon(item);
				CompSound.SUCCESSFUL_HIT.play(player);
			}

			/**
			 * Enable clicking outside of the menu or in the slot item
			 */
			@Override
			protected boolean isActionAllowed(final MenuClickLocation location, final int slot, final ItemStack clicked, final ItemStack cursor) {
				return location != MenuClickLocation.MENU || (location == MenuClickLocation.MENU && slot == getCenterSlot());
			}

			/**
			 * @see org.mineacademy.fo.menu.Menu#getInfo()
			 */
			@Override
			protected String[] getInfo() {
				return new String[]{
						"Place the class icon item",
						"to the center slot."
				};
			}
		}

		private final class WavePrompt extends SimplePrompt {

			@Override
			protected String getPrompt(ConversationContext conversationContext) {
				return "&6Enter the number of waves for this round";
			}

			@Override
			protected @org.jetbrains.annotations.Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
				monsterRound.setWaves(Integer.parseInt(s));
				return Prompt.END_OF_CONVERSATION;
			}
		}

		private final class NamePrompt extends SimplePrompt {

			@Override
			protected String getPrompt(ConversationContext conversationContext) {
				return "&6Enter the Name for this round";
			}

			@Override
			protected @org.jetbrains.annotations.Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

				monsterRound.setName(s);
				return Prompt.END_OF_CONVERSATION;
			}
		}

		private final class timePrompt extends SimplePrompt {

			@Override
			protected String getPrompt(ConversationContext conversationContext) {
				return "&6Enter the wait time in seconds";
			}

			@Override
			protected @org.jetbrains.annotations.Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

				monsterRound.setTime(s);
				return Prompt.END_OF_CONVERSATION;
			}
		}

		private final class spawnTimePrompt extends SimplePrompt {

			@Override
			protected String getPrompt(ConversationContext conversationContext) {
				return "&6Enter the time delay between waves in seconds";
			}

			@Override
			protected @org.jetbrains.annotations.Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

				monsterRound.setSpawnTime(s);
				return Prompt.END_OF_CONVERSATION;
			}
		}

		private final class positionPrompt extends SimplePrompt {

			@Override
			protected String getPrompt(ConversationContext conversationContext) {
				return "&6Enter the position of this Round";
			}

			@Override
			protected @org.jetbrains.annotations.Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

				monsterRound.setPosition(Integer.parseInt(s));
				return Prompt.END_OF_CONVERSATION;
			}
		}

	}

	private final static class MonsterSelectMenu extends MenuPagged<EntityType> {

		private MonsterRound monsterRound;

		private MonsterSelectMenu(final Menu parent, MonsterRound monsterRound) {
			super(parent, compileValidTypes());

			this.monsterRound = monsterRound;
		}


		private static List<EntityType> compileValidTypes() {
			final List<EntityType> list = new ArrayList<>();

			for (final EntityType type : EntityType.values())
				if (type.isAlive() && type.isSpawnable())
					list.add(type);

			return list;
		}

		@Override
		protected ItemStack convertToItemStack(final EntityType item) {
			return ItemCreator.of(
					CompMaterial.makeMonsterEgg(item),
					ItemUtil.bountifyCapitalized(item))
					.build()
					.makeMenuTool();
		}

		@Override
		protected void onPageClick(final Player player, final EntityType item, final ClickType click) {
			monsterRound.addMonster(item);

			restartMenu("&2Selected " + ItemUtil.bountifyCapitalized(item) + "!");
		}

		/**
		 * @see org.mineacademy.fo.menu.Menu#getInfo()
		 */
		@Override
		protected String[] getInfo() {
			return new String[] {
					"Select what entity",
					"you want for this round."
			};
		}
	}
}
