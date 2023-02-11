execute as @e[name=b] at @s run execute if entity @a[nbt={SelectedItem:{tag:{Custom:112}}},limit=1,sort=nearest] run kill @e[name=b]
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] run execute at @a[scores={playerHit=1..}] run execute at @e[nbt={HurtTime:10s}] run tag @e[name=!kindred408,nbt=!{SelectedItem:{tag:{Custom:112}}},distance=..5,type=!item,type=!minecraft:wolf,type=!minecraft:armor_stand,type=!arrow,type=!experience_orb] add kill
execute as @e[tag=kill] at @s run effect give @s minecraft:wither 1 100 true
execute as @e[tag=kill] at @s run playsound minecraft:entity.drowned.shoot voice @a ~ ~ ~ 2 1
execute as @e[tag=kill] at @s run particle minecraft:sweep_attack ~ ~1 ~ 0.5 0.5 0.5 1 1 normal @a
execute as @e[tag=kill,type=!#minecraft:skeletons] at @s run particle minecraft:item redstone ~ ~1.5 ~ 0.2 0.3 0.2 0.05 10 force @a
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s at @e[type=#minecraft:impact_projectiles,distance=..3] run playsound minecraft:block.anvil.land voice @a ~ ~ ~ 0.5 2
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s at @e[type=#minecraft:impact_projectiles,distance=..3] run particle minecraft:sweep_attack ~ ~ ~ 0 0 0 1 1 normal @a
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run kill @e[type=#minecraft:impact_projectiles,distance=..3]
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] run execute at @a[scores={playerHit=1..}] run effect give @a[scores={playerHit=1..}] minecraft:mining_fatigue 1 5 true
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] run execute at @a[scores={playerHit=1..}] run effect give @a[scores={playerHit=1..}] minecraft:saturation 5 0 true
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] run execute at @a[scores={playerHit=1..}] run effect give @a[scores={playerHit=1..}] minecraft:regeneration 2 2 true
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] run execute at @a[scores={playerHit=1..}] run tag @e[type=ender_dragon] remove kill
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] at @s run particle minecraft:soul ^ ^1 ^ 0.7 0.5 0.7 0.01 1 normal @a
execute as @e[name="k",limit=1] run particle minecraft:enchant ^ ^1.5 ^ 0.7 0.5 0.7 0.5 1 normal @a
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] at @s run effect give @s speed 1 2 true
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] at @s run effect give @s hunger 3 45 true
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] at @s run effect give @s strength 3 1 true
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run scoreboard players add @s En 1
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run kill @e[type=item,nbt={Item:{id:"minecraft:slime_ball"}},distance=..3]
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s[scores={En=1}] run summon villager ^ ^5 ^ {PersistenceRequired:1,CustomName:"\"sw10\"",Silent:1,NoAI:1}
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run effect give @s speed 1 1 true
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run kill @e[name="k",distance=..10]
effect give @e[name="sw10"] invisibility 100 100 true
scoreboard players add @e[name="sw10"] bye 1
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s[scores={En=1..}] run tp @e[name="sw10"] ^ ^ ^2 facing entity @s
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run scoreboard players reset @e[name="sw10"] bye
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run fill ^-5 ^ ^-5 ^5 ^5 ^5 air replace lava
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run fill ^-5 ^ ^-5 ^5 ^5 ^5 air replace fire
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run particle minecraft:witch ^ ^1 ^ 0.5 0.7 0.5 0.2 1 normal @a
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}}] at @s run scoreboard players reset @e[name="sw10"] re3
execute as @e[name="sw10",scores={re3=20..}] at @s run kill @s
execute as @a[nbt=!{SelectedItem:{tag:{Custom:113}}}] at @s run scoreboard players reset @s En
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}},scores={playerHit=1..}] at @s run summon villager ^ ^5 ^ {PersistenceRequired:1,CustomName:"\"sw10\"",Silent:1,NoAI:1}
execute as @a[nbt={SelectedItem:{tag:{Custom:113}}},scores={playerHit=1..}] at @s run summon armor_stand ^ ^1.7 ^-4 {PersistenceRequired:1,CustomName:"\"sw8\"",Silent:1,NoAI:1,Invisible:1b,NoBasePlate:1b,NoGravity:1b}
execute as @e[name="sw8"] at @s[scores={re=1..4}] run tp @s ^ ^ ^ facing entity @p eyes
execute as @e[name="sw8"] at @s run effect give @e[type=phantom,distance=..3] instant_health 1 1 true
execute as @e[name="sw8"] at @s run effect give @e[type=#skeletons,distance=..3] instant_health 1 1 true
execute as @e[name="sw8"] at @s run effect give @e[type=zombie,distance=..3] instant_health 1 1 true
execute as @e[name="sw8"] at @s run effect give @e[type=zombie_villager,distance=..3] instant_health 1 1 true
execute as @e[name="sw8"] at @s run effect give @e[type=zombified_piglin,distance=..3] instant_health 1 1 true
execute as @e[name="sw8"] at @s run effect give @e[type=drowned,distance=..3] instant_health 1 1 true
execute as @e[name="sw8"] at @s run effect give @e[type=husk,distance=..3] instant_health 1 1 true
execute as @e[name="sw8"] at @s run effect give @e[type=zoglin,distance=..3] instant_health 1 1 true
execute as @e[name="sw8"] at @s run effect give @e[type=!phantom,type=!#skeletons,type=!zombie,type=!zombie_villager,type=!zombified_piglin,type=!drowned,type=!husk,type=!zoglin,distance=..3,nbt=!{SelectedItem:{tag:{Custom:113}}}] instant_damage 1 1 true
execute as @a[scores={En=0}] at @s run kill @e[name="sw10",distance=..3]
scoreboard players add @e[name="sw8"] re 1
execute as @e[name="sw8"] at @s[scores={re=30}] run kill @s
execute as @e[name="sw8"] at @s[scores={re=1..}] run playsound entity.drowned.shoot voice @a ^ ^ ^ 2 1
execute as @e[name="sw8",scores={re=5..}] at @s run tp ^ ^ ^5
execute as @e[name="sw8"] at @s run particle sweep_attack ^ ^ ^ 10 0 0 5 100 normal @a
execute as @e[name="sw8"] at @s run particle witch ^ ^ ^ 10 1 0 10 100 normal @a
execute as @e[name="sw8"] at @s run fill ^-30 ^ ^5 ^30 ^3 ^5 air
execute as @e[name="sw8"] at @s run kill @e[distance=..20,type=!player,type=!armor_stand,type=!villager,type=!item]
scoreboard players add @e[tag=kill] oi 1
execute as @a[nbt={SelectedItem:{tag:{Custom:112}}}] run execute at @a[scores={playerHit=1..}] run execute at @e[nbt={HurtTime:10s}] run scoreboard players reset @e[nbt={HurtTime:10s}] oi
execute as @e[tag=kill,scores={oi=20}] at @s run effect clear @s wither
execute as @e[tag=kill,scores={oi=20}] at @s run tag @s remove kill
scoreboard players reset @a[scores={playerHit=1..}] playerHit
bossbar set 5 color red
bossbar add 5 "hero1"
bossbar set 5 max 160
bossbar set minecraft:5 name {"text":"Blade Master","bold":true,"color":"dark_red"}
execute store result bossbar minecraft:5 value run data get entity @e[name="Blade Master",limit=1] Health
bossbar set 4 color red
bossbar add 4 "hero"
bossbar set 4 max 160
bossbar set minecraft:4 name {"text":"Fallen Hero","bold":true,"color":"dark_red"}
execute store result bossbar minecraft:4 value run data get entity @e[name="Fallen Hero",limit=1] Health
execute as @e[name="Fallen Hero"] at @s run attribute @s generic.max_health base set 160
execute as @e[name="Blade Master"] at @s run attribute @s generic.max_health base set 160
execute as @a[limit=1,scores={di=1..}] as @e[name="Fallen Hero"] at @s run particle minecraft:soul ^ ^1.5 ^ 1 1 1 0.1 3000 normal @a
execute as @a[limit=1,scores={di=1..}] as @e[name="Blade Master"] at @s run particle minecraft:poof ^ ^1.5 ^ 1 1 1 0.1 3000 normal @a
execute as @a[limit=1,scores={di=1..}] as @e[name="Blade Master"] at @s run tp @s ~ ~-10 ~
execute as @a[limit=1,scores={di=1..}] as @e[name="Fallen Hero"] at @s run tp @s ~ ~-10 ~
execute as @a[limit=1,scores={di=1..}] run kill @e[type=wither_skeleton]
execute as @a[limit=1,scores={di=1..}] run kill @e[type=skeleton]
execute as @a[limit=1,scores={di=1..}] run bossbar remove 5
execute as @a[limit=1,scores={di=1..}] run kill @e[type=item]
execute as @a[limit=1,scores={di=1..}] run bossbar remove 4
execute as @a[limit=1,scores={di=1..}] run stopsound @a
execute as @a[limit=1,scores={di=1..}] run scoreboard players reset @a di
execute as @e[name="Blade Master"] at @a[scores={playerHit=1..}] run playsound block.anvil.place voice @a ^ ^ ^ 2 1.6
execute as @e[name="sw10",scores={bye=7..}] at @s run tp @s ^ ^-10 ^
execute as @e[name="sw10",scores={bye=10..}] at @s run kill @s

execute as @e[type=minecraft:item,nbt={Item:{id:"minecraft:iron_sword",tag:{Custom:113}}}] at @s at @p run summon armor_stand ^ ^ ^3 {NoGravity:1b,Invisible:1b,ShowArms:1b,CustomName:'{"text":"k"}',Pose:{RightArm:[265f,100f,177f]}}
execute as @e[type=minecraft:item,nbt={Item:{id:"minecraft:iron_sword",tag:{Custom:113}}}] at @s at @p run replaceitem entity @e[name="k",limit=1,sort=nearest] weapon.mainhand minecraft:iron_sword{Custom:1,display:{Name:'{"text":"Enma","color":"black","italic":false}'},Enchantments:[{id:sharpness,lvl:1}],HideFlags:5,Unbreakable:1,Custom:113}
execute as @e[type=minecraft:item,nbt={Item:{id:"minecraft:iron_sword",tag:{Custom:113}}}] at @s run kill @s
