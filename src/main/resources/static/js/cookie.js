$(function () {
  $("#cookie-bar").on("click", "#close-cookie-bar", function () {
    setConsentCookie();
    $('#cookie-bar').hide();
  });

  getCookieConsent();

  //TODO Move this somewhere else.
  $("#mail-bar").delay(5000).fadeOut("fast");
});

function getCookieConsent() {
  var consent = hasConsentCookie();

  if (!consent) {
    // show bar
    $('#cookie-bar').show();
  }

}

function setConsentCookie() {
  var expr = new Date();
  expr.setFullYear(expr.getFullYear() + 1);
  expr = expr.toUTCString();
  document.cookie = "cookie-consent=true;" + "path=/; expires=" + expr;
}

function hasConsentCookie() {
  var documentCookies = document.cookie.split(";");
  for (var i = 0; i < documentCookies.length; i++) {
    var eachCookieName = documentCookies[i]
      .substr(0, documentCookies[i].indexOf("="));

    if (eachCookieName.indexOf("cookie-consent") != -1) {
      return true;
    }
  }

  return false;
}

$("#cookie-bar").on("click", "#close-cookie-bar", function () {
  setConsentCookie();
  $('#cookie-bar').hide('slow');
});