var small_hidden = false
var hasntchanged_hidden = false

window.onload = function() {
  $("#small-hide").click(function() {
      if (small_hidden) {
        $("small").fadeIn();
        small_hidden = false;
      } else {
        $("small").fadeOut();
        small_hidden = true;
      }
  });

  $("#hasntchanged_hide").click(function() {
      if (hasntchanged_hidden) {
        $("tr td.same").fadeIn();
        hasntchanged_hidden = false;
      } else {
        $("tr td.same").fadeOut();
        hasntchanged_hidden = true;
      }
  });
}
