PATH									= .
SRC 									= $(PATH)/src
TEST 									= $(SRC)/test
MAIN									= $(SRC)/main

PROTO									= $(MAIN)/proto

PROTO_DOTA 						= $(PROTO)/dota
PROTO_DOTA_PKG				= com/dota/proto
PROTO_DOTA_FILES 			= steammessages.proto matchmaker_common.proto \
												gcsdk_gcmessages.proto dota_gcmessages_common.proto \
												econ_gcmessages.proto network_connection.proto \
												dota_gcmessages_client.proto dota_gcmessages_server.proto \
												gcsystemmsgs.proto
PROTO_DOTA_SOURCES		= $(addprefix $(PROTO_DOTA)/, $(PROTO_DOTA_FILES))

PROTO_STEAMC					= $(PROTO)/steamclient
PROTO_STEAMC_PKG			= com/steam/proto
PROTO_STEAMC_FILES		=	encrypted_app_ticket.proto steammessages_base.proto \
												steammessages_clientserver.proto steammessages_clientserver_2.proto
PROTO_STEAMC_SOURCES	= $(addprefix $(PROTO_STEAMC)/, $(PROTO_STEAMC_FILES))

PROTOC								= protoc
PROTOC_OUT						= $(MAIN)/java
PROTOC_CMD_BASE				= $(PROTOC) --java_out=$(PROTOC_OUT)

all: $(PROTOC_OUT)/$(PROTO_DOTA_PKG) $(PROTOC_OUT)/$(PROTO_STEAMC_PKG)

$(PROTOC_OUT)/$(PROTO_DOTA_PKG): $(PROTO_DOTA_SOURCES)
	$(PROTOC_CMD_BASE) --proto_path=$(PROTO_DOTA) --proto_path=$(PROTO) $(PROTO_DOTA_SOURCES)

$(PROTOC_OUT)/$(PROTO_STEAMC_PKG): $(PROTO_STEAMC_SOURCES)
	$(PROTOC_CMD_BASE) --proto_path=$(PROTO_STEAMC) --proto_path=$(PROTO) $(PROTO_STEAMC_SOURCES)

clean:
	rm -r $(PROTOC_OUT)/$(PROTO_DOTA_PKG)
	rm -r $(PROTOC_OUT)/$(PROTO_STEAMC_PKG)
