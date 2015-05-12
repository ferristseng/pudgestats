GENERATE_PY 		= bin/generate.py

OUTPUT_DIR			= src/main/scala/com/dota/data
OUTPUT_FILES		= Hero.scala Ability.scala Item.scala Creep.scala \
									Building.scala Neutral.scala
OUTPUT_PATHS		= $(addprefix $(OUTPUT_DIR)/, $(OUTPUT_FILES))

DATA_DIR				= data
DATA_FILES			= dota_english.txt 
DATA_PATHS			= $(addprefix $(DATA_DIR)/, $(DATA_FILES))

$(OUTPUT_PATHS): $(DATA_PATHS) $(GENERATE_PY)
	python $(GENERATE_PY)

clean:
	rm -r $(OUTPUT_PATHS)
