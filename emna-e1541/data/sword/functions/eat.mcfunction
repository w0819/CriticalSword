execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:ender_eye"}},distance=..1.5] run summon wither_skeleton ~ ~1 ~ {PersistenceRequired:1,HandItems:[{Count:1,id:iron_sword}],ArmorItems:[{Count:1,id:netherite_boots},{Count:1,id:netherite_leggings},{Count:1,id:netherite_chestplate},{}],CustomName:"\"Fallen Hero\""}
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:nether_star"}},distance=..1.5] run summon skeleton ~ ~1 ~ {PersistenceRequired:1,HandItems:[{Count:1,id:iron_sword}],ArmorItems:[{Count:1,id:iron_boots},{Count:1,id:iron_leggings},{Count:1,id:iron_chestplate},{}],CustomName:"\"Blade Master\"",Silent:1}
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:nether_star"}},distance=..1.5] run summon armor_stand ^2 ^1.7 ^-4 {PersistenceRequired:1,CustomName:"\"sw2\"",Silent:1,NoAI:1,Invisible:1b,NoBasePlate:1b,NoGravity:1b}
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:nether_star"}},distance=..1.5] run effect give @e[name="Blade Master"] instant_damage 10 100 true
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:nether_star"}},distance=..1.5] run bossbar set 5 players @a
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:nether_star"}},distance=..1.5] run playsound minecraft:music_disc.pigstep voice @a ~ ~ ~ 0.2 0.7
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:ender_eye"}},distance=..1.5] run particle minecraft:poof ~ ~1 ~ 0 0 0 0.2 3000 normal @a
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:nether_star"}},distance=..1.5] run summon lightning_bolt ~ ~ ~
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:ender_eye"}},distance=..1.5] run particle minecraft:soul ~ ~1 ~ 0 0 0 0.2 3000 normal @a
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:ender_eye"}},distance=..1.5] run effect give @e[name="Fallen Hero"] instant_damage 10 100 true
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:ender_eye"}},distance=..1.5] run effect give @e[name="Fallen Hero"] slowness 99999 0 true
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:ender_eye"}},distance=..1.5] run bossbar set 4 players @a
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:ender_eye"}},distance=..1.5] run playsound minecraft:music_disc.pigstep voice @a ~ ~ ~ 0.2 0.7
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:ender_eye"}},distance=..1.5] run summon lightning_bolt ~ ~ ~
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:dragon_egg"}},distance=..1.5] run summon armor_stand ^ ^-0.3 ^ {PersistenceRequired:1,CustomName:"\"sw2\"",Silent:1,NoAI:1,Invisible:1b,NoBasePlate:1b,NoGravity:1b}
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:dragon_egg"}},distance=..1.5] run scoreboard players add @p sk 1
execute at @e[type=item,nbt={Item:{id:"minecraft:iron_sword"}}] run execute if entity @e[type=item,nbt={Item:{id:"minecraft:dragon_egg"}},distance=..1.5] run summon lightning_bolt ~ ~ ~-1
execute at @e[name="sw2"] as @a[scores={sk=1..}] at @e[name="sw2"] run particle minecraft:witch ^ ^0.5 ^1 0.2 0.2 0.2 0.2 3000 normal @a
execute at @e[name="sw2"] as @a[scores={sk=1..}] at @e[name="sw2"] run particle minecraft:portal ^ ^0.5 ^1 0 0 0 0.2 3000 normal @a
execute at @e[name="sw2"] as @a[scores={sk=1..}] at @e[name="sw2"] run playsound minecraft:block.beacon.deactivate voice @a ~ ~ ~ 100 0.7
execute at @e[name="sw2"] as @a[scores={sk=1..}] at @e[name="sw2"] run bossbar remove minecraft:5
execute at @e[name="sw2"] as @a[scores={sk=1..}] at @e[name="sw2"] run stopsound @a * music_disc.pigstep
execute at @e[name="sw2"] as @a[scores={sk=1..}] at @e[name="sw2"] run summon lightning_bolt ~ ~ ~
execute at @e[name="sw2"] as @a[scores={sk=1..}] at @e[name="sw2"] run summon armor_stand ^ ^-0.5 ^2 {NoGravity:1b,Invisible:1b,ShowArms:1b,CustomName:'{"text":"k"}',Pose:{RightArm:[265f,100f,177f]}}
execute at @e[name="sw2"] as @a[scores={sk=1..}] at @e[name="sw2"] run replaceitem entity @e[name="k",limit=1,sort=nearest] weapon.mainhand minecraft:iron_sword{Custom:1,display:{Name:'{"text":"Enma","color":"black","italic":false}'},Enchantments:[{id:sharpness,lvl:1}],HideFlags:5,Unbreakable:1,Custom:113}
execute at @e[name="sw2"] as @a[scores={sk=1..}] at @e[name="sw2"] run kill @e[name="sw2"]
execute as @a[scores={wither=1..}] at @s run summon armor_stand ^ ^-0.5 ^2 {NoGravity:1b,Invisible:1b,ShowArms:1b,CustomName:'{"text":"b"}',Pose:{RightArm:[265f,100f,177f]}}
execute as @a[scores={wither=1..}] at @e[name="b"] run playsound minecraft:block.beacon.deactivate voice @a ~ ~ ~ 100 0.7
execute as @a[scores={wither=1..}] at @e[name="b"] run playsound minecraft:block.beacon.deactivate voice @a ~ ~ ~ 100 0.7
execute as @a[scores={wither=1..}] at @e[name="b"] run playsound minecraft:block.beacon.deactivate voice @a ~ ~ ~ 100 0.7
execute as @a[scores={wither=1..}] at @e[name="b"] run playsound minecraft:block.beacon.deactivate voice @a ~ ~ ~ 100 0.7
execute as @a[scores={wither=1..}] at @e[name="b"] run particle minecraft:soul ~ ~0.5 ~ 0 0 0 0.2 3000 normal @a
execute as @a[scores={wither=1..}] at @e[name="b"] run summon lightning_bolt ~ ~ ~
execute as @a[scores={wither=1..}] run replaceitem entity @e[name="b",limit=1,sort=nearest] weapon.mainhand minecraft:iron_sword{Custom:1,display:{Name:'{"text":"Blade of The Fallen Hero","color":"dark_red","italic":false}'},Enchantments:[{id:sharpness,lvl:1}],HideFlags:5,Unbreakable:1,Custom:112}
execute as @a[scores={wither=1..}] run bossbar remove minecraft:4
execute as @a[scores={wither=1..}] run stopsound @a * music_disc.pigstep
execute at @e[name="b"] run particle minecraft:soul ^0.3 ^0.6 ^-0.2 0.2 0.5 0.2 0.01 1 normal @a
execute at @e[name="k"] run particle minecraft:enchant ^0.3 ^0.6 ^-0.2 0.2 0.5 0.2 0.01 1 normal @a
execute at @e[name="k"] run particle minecraft:witch ^0.3 ^0.6 ^-0.2 0.2 0.5 0.2 0.001 1 normal @a
execute as @a[scores={wither=1..}] at @s run scoreboard players reset @a wither
execute as @a[scores={sk=1..}] at @s run scoreboard players reset @a sk