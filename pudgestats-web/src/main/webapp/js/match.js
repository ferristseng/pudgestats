$(document).on("click", "#parser-log-toggle", function(e) {
  $("#parser-log").toggle();

  return false;
});

$(document).on("change", "#timeline-selector", function(e) {
  var evtId = $(this).val()

  $("#timeline .highlighted").toggleClass("highlighted");

  if (evtId != "0") {
    $("#timeline ." + evtId).toggleClass("highlighted");
  }
});
