(function () {
  var STORAGE_KEY = "its-arc-unlocked";
  // SHA-256 of the shared ARC password (not stored in plaintext).
  var PASSWORD_HASH =
    "2b52c58437e4afb74160a76360f8ed72cbc54e18bed36f420a6ddc2ac0a1435d";

  function isGatePage() {
    var path = window.location.pathname;
    return /\/(?:index\.html)?$/.test(path) || path.endsWith("/into-the-singularity-book-preview/");
  }

  function isUnlocked() {
    try {
      return sessionStorage.getItem(STORAGE_KEY) === "1";
    } catch (e) {
      return false;
    }
  }

  function setUnlocked() {
    try {
      sessionStorage.setItem(STORAGE_KEY, "1");
    } catch (e) {
      /* ignore */
    }
  }

  function sha256Hex(text) {
    var data = new TextEncoder().encode(text);
    return crypto.subtle.digest("SHA-256", data).then(function (buf) {
      return Array.from(new Uint8Array(buf))
        .map(function (b) {
          return b.toString(16).padStart(2, "0");
        })
        .join("");
    });
  }

  function redirectToGate() {
    window.location.replace("index.html");
  }

  window.ItsArcGate = {
    tryUnlock: function (password) {
      return sha256Hex(password).then(function (hash) {
        if (hash === PASSWORD_HASH) {
          setUnlocked();
          return true;
        }
        return false;
      });
    },
    isUnlocked: isUnlocked,
  };

  if (!isGatePage() && !isUnlocked()) {
    redirectToGate();
  }
})();
