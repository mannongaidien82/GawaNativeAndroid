function onPushMeClicked() {
  Native.showToast('Java PushMe clicked!');
  return false;
}

function addTextNode(text) {
  document.body.appendChild(document.createTextNode(text));
}

addTextNode('Hello, from JS');
