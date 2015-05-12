import re
import datetime

"""
generate.py

Simple script to generate Scala enumeration files, name helper for 
Dota 2 entities -- heroes, abilities, and units.
"""

"""
--------------------------------------
Scala Template for Enumeration File
======================================
"""

SCALA_HEADER = """/** File generated with `generate.py` on %s */""" % datetime.datetime.now()

SCALA_TEMPLATE_F = """%s

package com.dota.data

object {class_name} {{
  private val innermap = Map(
{mappings}
  )

  def fromString(s: String): Option[DotaEntity] = {{ 
{from_string}
  }}
}}
""" % SCALA_HEADER

"""
--------------------------------------
Regexes to extract data
======================================
"""

RE_MODES = re.VERBOSE | re.MULTILINE | re.DOTALL

HEROES_SECTION_RE_RAW = r"""
\s*\/\/\s*Hero\s+Names                                              # Header
[^\/]+                                                              # Content
\/\/                                                                # Begin new section
"""

HEROES_SINGLE_RE_RAW = r"""
\s*"(?P<npc_hero_string>npc_dota_hero_[a-z_]+)"                     # Hero NPC String
\s+                                                                 # Whitespace
"(?P<localized_hero_string>[^"]+)"                                  # Localized Name
"""

UNITS_SECTION_RE_RAW = r"""
\s*\/\/\s*Other\s+Names                                             # Header
[^\/]+                                                              # Content
\/\/                                                                # Begin new section
"""

UNITS_SINGLE_RE_RAW = r"""
\s*"(?P<npc_unit_string>[a-z_]+dota_[a-z_0-9]+)"                    # Unit NPC String
\s+                                                                 # Whitespace
"(?P<localized_unit_string>[^"]+)"                                  # Localized Name
"""

BLDGS_SECTION_RE_RAW = r"""
\s*\/\/\s*Towns                                                     # Header
[^\/]+                                                              # Content
\/\/                                                                # Begin new section
"""

BLDGS_SINGLE_RE_RAW = r"""
\s*"(?P<npc_bldg_string>[a-z_\d]*dota_[a-z_\d]+)"                   # Building NPC String
\s+                                                                 # Whitespace
"(?P<localized_bldg_string>[^"]+)"                                  # Localized Name
"""

NEUTRALS_SECTION_RE_RAW = r"""
\s*\/\/\s*Neutral\s+Creeps                                          # Header
[^\/]+                                                              # Content
\/\/                                                                # Begin new section
"""

NEUTRALS_SINGLE_RE_RAW = r"""
\s*"(?P<npc_neutral_string>npc_dota_neutral_[a-z_]+)"               # Neutral NPC String
\s+                                                                 # Whitespace
"(?P<localized_neutral_string>[^"]+)"                               # Localized Name
"""

ABILITIES_SINGLE_RE_RAW = r"""
\s*"DOTA_Tooltip_ability_(?P<npc_ability_string>[a-z_]+)"           # Ability NPC String
\s+                                                                 # Whitespace
"(?P<localized_ability_string>[A-Z][a-z][^"\.]+)"                   # Localized Name (non-sentences)
"""

ITEMS_SINGLE_RE_RAW = r"""
\s*"DOTA_Tooltip_Ability_(?P<npc_item_string>item_[a-z\d_L]+)"      # Item NPC String
\s+                                                                 # Whitespace
"(?P<localized_item_string>[^"]+)"                                  # Localized Name
"""

HEROES_SECTION_RE   = re.compile(HEROES_SECTION_RE_RAW, RE_MODES)
UNITS_SECTION_RE    = re.compile(UNITS_SECTION_RE_RAW, RE_MODES)
BLDGS_SECTION_RE    = re.compile(BLDGS_SECTION_RE_RAW, RE_MODES)
NEUTRALS_SECTION_RE = re.compile(NEUTRALS_SECTION_RE_RAW, RE_MODES)

HEROES_SINGLE_RE    = re.compile(HEROES_SINGLE_RE_RAW, RE_MODES)
UNITS_SINGLE_RE     = re.compile(UNITS_SINGLE_RE_RAW, RE_MODES)
BLDGS_SINGLE_RE     = re.compile(BLDGS_SINGLE_RE_RAW, RE_MODES)
NEUTRALS_SINGLE_RE  = re.compile(NEUTRALS_SINGLE_RE_RAW, RE_MODES)
ABILITIES_SINGLE_RE = re.compile(ABILITIES_SINGLE_RE_RAW, RE_MODES)
ITEMS_SINGLE_RE     = re.compile(ITEMS_SINGLE_RE_RAW, RE_MODES)

"""
--------------------------------------
Text Files -- heroes, abilities, units
======================================
"""

ENGLISH_LOCALE_TXT  = open("data/dota_english.txt", encoding='utf-16le')

"""
--------------------------------------
Text Files Content
======================================
"""

def get_content(regex, container):
    return regex.search(container).group()

ENGLISH_LOCALE      = ENGLISH_LOCALE_TXT.read()

HEROES_CONTENT      = get_content(HEROES_SECTION_RE, ENGLISH_LOCALE)
UNITS_CONTENT       = get_content(UNITS_SECTION_RE, ENGLISH_LOCALE)
BLDGS_CONTENT       = get_content(BLDGS_SECTION_RE, ENGLISH_LOCALE)
NEUTRALS_CONTENT    = get_content(NEUTRALS_SECTION_RE, ENGLISH_LOCALE)

"""
--------------------------------------
Find Data
======================================
"""

def get_matches(regex, content):
    return [e for e in regex.finditer(content)]

HEROES_MATCHES      = get_matches(HEROES_SINGLE_RE, HEROES_CONTENT)
UNITS_MATCHES       = get_matches(UNITS_SINGLE_RE, UNITS_CONTENT)
BLDGS_MATCHES       = get_matches(BLDGS_SINGLE_RE, BLDGS_CONTENT)
NEUTRALS_MATCHES    = get_matches(NEUTRALS_SINGLE_RE, NEUTRALS_CONTENT)
ABILITIES_MATCHES   = get_matches(ABILITIES_SINGLE_RE, ENGLISH_LOCALE)
ITEMS_MATCHES       = get_matches(ITEMS_SINGLE_RE, ENGLISH_LOCALE)

print("Heroes found: %s..." % len(HEROES_MATCHES))
print("Units found: %s..." % len(UNITS_MATCHES))
print("Buildings found: %s..." % len(BLDGS_MATCHES))
print("Neutrals found: %s..." % len(NEUTRALS_MATCHES))
print("Abilities found: %s..." % len(ABILITIES_MATCHES))
print("Items found: %s..." % len(ITEMS_MATCHES))

HEROES = \
    [(h.group("npc_hero_string"), h.group("localized_hero_string")) for h in HEROES_MATCHES]

UNITS = \
    [(u.group("npc_unit_string"), u.group("localized_unit_string")) for u in UNITS_MATCHES]

BLDGS = \
    [(b.group("npc_bldg_string"), b.group("localized_bldg_string")) for b in BLDGS_MATCHES]

NEUTRALS = \
    [(n.group("npc_neutral_string"), n.group("localized_neutral_string")) for n in NEUTRALS_MATCHES]

ABILITIES = \
    [(a.group("npc_ability_string"), a.group("localized_ability_string")) for a in ABILITIES_MATCHES]

ITEMS = \
    [(i.group("npc_item_string"), i.group("localized_item_string")) for i in ITEMS_MATCHES]

"""
--------------------------------------
From String Def
======================================
"""

INDENT_LEVEL = "    "

DEFAULT_FROM_STR = "%sthis.innermap.get(s)" % INDENT_LEVEL

ITEM_FROM_STR = \
"""
{tab}val isRecipe = s.contains("recipe")

{tab}if (isRecipe) {{
{tab}  this.innermap.get(s.patch(5, "", 7)) match {{
{tab}    case Some(item) => Some(new DotaEntity("Recipe: " + item.name, item.npcString)) 
{tab}    case None => None
{tab}  }}
{tab}}} else {{
{tab}  this.innermap.get(s)
{tab}}}
""".format(tab=INDENT_LEVEL).strip('\n')

"""
--------------------------------------
Toolchain
======================================
"""

CLASSES = [
    (HEROES, "Hero", DEFAULT_FROM_STR),
    (UNITS, "Creep", DEFAULT_FROM_STR),
    (BLDGS, "Building", DEFAULT_FROM_STR),
    (NEUTRALS, "Neutral", DEFAULT_FROM_STR),
    (ABILITIES, "Ability", DEFAULT_FROM_STR),
    (ITEMS, "Item", ITEM_FROM_STR)
]

OUTPUT_F = "src/main/scala/com/dota/data/%s.scala"

"""
--------------------------------------
Toolchain Transformations
======================================
"""

def build_map(tuple_list, class_name):
    return ",\n".join(
        ["%s\"%s\" -> new DotaEntity(\"%s\", \"%s\")" % 
            (INDENT_LEVEL, t[0], t[1], t[0]) 
        for t in tuple_list])

"""
--------------------------------------
Write New Content
======================================
"""


for c in CLASSES:
    tuple_list = c[0] 
    class_name = c[1]

    rmap = build_map(tuple_list, class_name)

    content = SCALA_TEMPLATE_F.format(
        class_name=class_name, 
        mappings=rmap,
        from_string=c[2])

    open(OUTPUT_F % class_name, "w+").write(content)
