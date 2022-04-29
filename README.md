# XPiggy Bank

Welcome! 

## Using the mod

This is a simple mod for dealing with XP in bulk. It adds a single block, the XPiggy Bank, which is where you can:

- Store your XP as Liquid Experience or any of the configured compatible fluids
- Retrieve XP
- Collect XP in a radius
- Pipe in compatible fluids and auto-convert them
- Pipe out a compatible fluid

You can store and retrieve your own XP by right-clicking on the XPiggy Bank and then using the buttons to transfer 10, 100, 1000 or all XP. 

You can change the XPiggy Bank's fluid type by using the arrows above the tank image on the right. This does not stop you storing or retrieving 
XP manually, and any supported fluid can still be piped in and automatically converted. This does, however, change the type of fluid that can 
be piped out of the XPiggy Bank.

## Configuring the mod

Mod configuration options include:

- Compatible fluids and their conversion rates. This allows things like:
	- only using the single Liquid Experience added by this mod
	- specifying different rates of conversion for "Experience Fluids" added by other mods. For example, CoFH's Experience might be a 1:1 conversion, but Industrial Foregoing's Essence could be configured at 100:1 since it's easier to obtain.
- Set the default fluid in a tank
- The radius of the XP collection (the default covers a 9x9 area)
- Change the number ticks between collections (defaults to every 10 ticks, or twice a second)
- Set the internal tank size. The default is a whopping 20 million mB (20 thousand buckets of Liquid Experience - or about 2125 levels!)

The config is found in `config/cuboiddroid/xpiggybank/xpiggybank.toml`. The settings are all explained in that file.