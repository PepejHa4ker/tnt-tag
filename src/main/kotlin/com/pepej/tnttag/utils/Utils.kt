package com.pepej.tnttag.utils

import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.menu.Item
import com.pepej.papi.text.Text.colorize
import com.pepej.tnttag.TNTTag.Companion.instance
import com.pepej.tnttag.models.User
import com.pepej.tnttag.service.UserService
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun Player.user() = UserService.getUser(this)

operator fun String.unaryPlus(): String = colorize(this)

inline fun ItemStack.meta(block: ItemMeta.() -> Unit): ItemStack = apply { itemMeta = itemMeta.apply(block) }

inline fun item(material: Material, amount: Int = 1, meta: ItemMeta.() -> Unit = {}): ItemStack =
    ItemStack(material, amount).meta(meta)


typealias MenuCallback = User.() -> Unit

inline fun ItemStack.handle(crossinline block: MenuCallback): Item = ItemStackBuilder.of(this).buildConsumer {
    val clicked = it.whoClicked as Player
    block(UserService.getUser(clicked))

}
